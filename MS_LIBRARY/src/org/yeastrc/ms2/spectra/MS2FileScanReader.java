/**
 * MS2FileScanReader.java
 * Created by: Michael Riffle <mriffle@u.washington.edu>
 * Created on: May 10, 2007 at 2:53:18 PM
 */

package org.yeastrc.ms2.spectra;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Vector;

/**
 * @author Michael Riffle <mriffle@u.washington.edu>
 * @version May 10, 2007
 *
 * Reads scans out of an MS2 file on disk
 */
public class MS2FileScanReader implements MS2ScanReader {

	// private constructor
	private MS2FileScanReader() { }
	
	/**
	 * Get an instance of this class
	 * @param filename
	 * @return
	 */
	public static MS2ScanReader getInstance( String filename ) {
		MS2FileScanReader reader = new MS2FileScanReader();
		reader.setFilename( filename );
		
		return reader;
	}
	
	
	/**
	 * Read the next scan
	 * @return The next read MS2Scan
	 * @throws Exception if there is a problem
	 */
	public MS2Scan readNext() throws Exception {
		
		// did we already read it all?
		if (this.isDone())
			throw new Exception( "Read past ends of scans." );
		
		// initialize this reader
		if (br == null)
			trimHeaders();
		
		// make sure we're getting what we think we're getting
		if (!this.lastLineRead.startsWith( "S" ) )
			throw new Exception( "Error parsing " + filename + ".  Expected S line, but got:\n" + this.lastLineRead + "\n" );

		// set up the scan object we're returning
		MS2Scan scan = new MS2Scan();
		scan.setRun( this.getMS2() );
		
		String[] scanData = this.lastLineRead.split( "\\t" );
		if (scanData.length != 4)
			throw new Exception ( "S line did not have 4 fields, it had " + scanData.length +
					"\nLine in question: " + this.lastLineRead + "\n" );

		if (!scanData[0].equals( "S" ) )
			throw new Exception ("S line did not properly begin with S[tab].\nLine in question: " + this.lastLineRead + "\n" );
		
		
		// Set the scan meta data
		scan.setStart( Integer.parseInt( scanData[1] ) );
		scan.setEnd( Integer.parseInt( scanData[2] ) );
		scan.setPreMZ( Float.parseFloat( scanData[3] ) );
		scanData = null;
		
		this.lastLineRead = br.readLine();		// read next line
		
		// read the I (charge-independent analysis) lines for this scan
		Vector<MS2ChargeIndependentAnalysis> iAnalysisVector = null;
		while (this.lastLineRead.startsWith( "I" ) ) {
			scanData = this.lastLineRead.split( "\\t" );
			if (scanData.length != 3)
				throw new Exception( "Error reading I line.  Expected 3 fields, got " + scanData.length + "fields.\nLine in question: " + this.lastLineRead + "\n" );

			// get the analysis data
			MS2ChargeIndependentAnalysis analysis = new MS2ChargeIndependentAnalysis();
			analysis.setHeader( scanData[ 1 ] );
			analysis.setValue( scanData[ 2 ] );
			
			if (iAnalysisVector == null) iAnalysisVector = new Vector<MS2ChargeIndependentAnalysis>();
			iAnalysisVector.add( analysis );
			
			scanData = null;
			
			this.lastLineRead = br.readLine();		// read next line
		}
		if (iAnalysisVector != null) {
			iAnalysisVector.trimToSize();
			scan.setIAnalysis( iAnalysisVector );
		}
		iAnalysisVector = null;

		
		/* ---------- begin charge state section -------------- */

		// this is where we're storing the charge states (Z lines)
		Vector<MS2ScanCharge> chargeStates = new Vector<MS2ScanCharge>( 3 );
		
		// read the Z lines (charge states) for this scan
		while (this.lastLineRead.startsWith( "Z" ) ) {
			scanData = this.lastLineRead.split( "\\t" );
			if (scanData.length != 3)
				throw new Exception( "Error reading Z line.  Expected 3 fields, got " + scanData.length + "fields.\nLine in question: " + this.lastLineRead + "\n" );
			
			MS2ScanCharge charge = new MS2ScanCharge();
			charge.setCharge( Integer.parseInt( scanData[ 1 ] ) );
			charge.setMass( Float.parseFloat( scanData[ 2 ] ) );
			charge.setScan( scan );
			
			scanData = null;
			this.lastLineRead = br.readLine();		// read in next line
			
			// this is where we're storing the analysis of this charge state (D lines)
			Vector<MS2ChargeDependentAnalysis> analysisVector = null;
			
			// read the D lines for this Z line
			while (this.lastLineRead.startsWith( "D" ) ) {
				scanData = this.lastLineRead.split( "\\t" );
				if (scanData.length != 3)
					throw new Exception( "Error reading D line.  Expected 3 fields, got " + scanData.length + "fields.\nLine in question: " + this.lastLineRead + "\n" );

				// get the analysis data
				MS2ChargeDependentAnalysis analysis = new MS2ChargeDependentAnalysis();
				analysis.setHeader( scanData[ 1 ] );
				analysis.setValue( scanData[ 2 ] );
				
				if (analysisVector == null) analysisVector = new Vector<MS2ChargeDependentAnalysis>();
				analysisVector.add( analysis );
				
				scanData = null;
				
				this.lastLineRead = br.readLine();		// read next line
			}

			// add the analysis of this charge state to the charge state
			if (analysisVector != null) {
				analysisVector.trimToSize();
				charge.setAnalysis( analysisVector );
			}
			
			chargeStates.add( charge );
		}
		
		chargeStates.trimToSize();
		scan.setCharges( chargeStates );

		/* -------- end charge state section -------------- */
		
		
		
		
		
		/* -------- read in the mass intensity pairs for this scan --------------- */
		Vector<Vector<Float>> miPairs = new Vector<Vector<Float>>();
		while ( this.lastLineRead != null && !this.lastLineRead.startsWith( "S" ) ) {
			scanData = lastLineRead.split( " " );
			if ( scanData.length != 2 )
				throw new Exception( "Exepcted mass intensity pair but got: " + this.lastLineRead );
			
			Vector<Float> pair = new Vector<Float>(2);
			pair.add( Float.parseFloat( scanData[0] ) );
			pair.add( Float.parseFloat( scanData[1] ) );
			
			miPairs.add( pair );
			
			this.lastLineRead = br.readLine();
		}
		miPairs.trimToSize();
		scan.setData( miPairs );
		/* -------- end reading in mass intensity pairs ------------- */
		
		
		
		// we've read the last line in the file
		if (this.lastLineRead == null) {
			this.setDone( true );
			try {
				this.br.close();
				this.br = null;
			} catch (Exception e) { ; }
		}
		
		
		return scan;
	}

	/**
	 * Essentially initializes the MS2FileScanReader
	 * Opens the file and trims the headers
	 * 
	 * @throws Exception If there is an IO problem
	 */
	private void trimHeaders() throws Exception {
		
		if (this.br != null)
			throw new Exception( "Called trimHeaders, but br isn't null.  This should not happen." );
		
		File file = new File( this.filename );
		if (!file.exists())
			throw new Exception( this.filename + " does not exist." );
		if (!file.canRead())
			throw new Exception( "Insufficient privledges to read: " + this.filename );
		
		try {
			this.br = new BufferedReader( new FileReader( file ) );
			this.lastLineRead = br.readLine();
			while ( this.lastLineRead.startsWith( "H" ) )
				this.lastLineRead = br.readLine();
		} catch (Exception e) {
			this.lastLineRead = null;
			this.close();
		} finally {
			file = null;
		}
		
	}
	
	
	/**
	 * Whether or not we have more scans to read
	 */
	public boolean hasNext() {
		return !isDone();
	}
	
	/**
	 * Close
	 */
	public void close() {
		try {
			this.br.close();
			this.br = null;
		} catch (Exception e) { ; }
	}
	
	
	private String filename;
	private BufferedReader br;
	private String lastLineRead;
	private boolean done;
	private MS2 MS2;
	
	
	
	/**
	 * @return the mS2
	 */
	public MS2 getMS2() {
		return MS2;
	}

	/**
	 * @param ms2 the mS2 to set
	 */
	public void setMS2(MS2 ms2) {
		MS2 = ms2;
	}
	/**
	 * @return the done
	 */
	public boolean isDone() {
		return done;
	}
	/**
	 * @param done the done to set
	 */
	private void setDone(boolean done) {
		this.done = done;
	}
	/**
	 * @return the filename
	 */
	public String getFilename() {
		return filename;
	}
	/**
	 * @param filename the filename to set
	 */
	public void setFilename(String filename) {
		this.filename = filename;
	}
	/**
	 * @return the lastLineRead
	 */
	public String getLastLineRead() {
		return lastLineRead;
	}
	/**
	 * @param lastLineRead the lastLineRead to set
	 */
	public void setLastLineRead(String lastLineRead) {
		this.lastLineRead = lastLineRead;
	}
}
