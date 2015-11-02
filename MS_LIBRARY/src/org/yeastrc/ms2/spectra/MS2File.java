/**
 * MS2File.java
 * Created by: Michael Riffle <mriffle@u.washington.edu>
 * Created on: May 10, 2007 at 2:22:29 PM
 */

package org.yeastrc.ms2.spectra;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

/**
 * @author Michael Riffle <mriffle@u.washington.edu>
 * @version May 10, 2007
 *
 * Class definition goes here
 */
public class MS2File extends MS2 {

	// private constructor
	private MS2File() {
		super();
	}

	/**
	 * Return a ScanReader for reading scans from this MS2file
	 * @return An appropriate ScanReader for this MS2 object
	 */
	public MS2ScanReader getScanReader() {
		return MS2FileScanReader.getInstance( this.getFilename() );
	}
	
	/**
	 * Get an instance of an MS2 object, from the supplied filename
	 * Will read MS2 data from a file on disk
	 * @param filename
	 * @return
	 * @throws Exception
	 */
	public static MS2 getInstance( String filename ) throws Exception {
		
		File ms2File = new File( filename );
		if (!ms2File.exists())
			throw new Exception( "The MS2 file: " + filename + " does not exist." );
		if (!ms2File.canRead())
			throw new Exception( "Can not read the MS2 file: " +filename );
		
		MS2File ms2Object = new MS2File();
		BufferedReader br = new BufferedReader( new FileReader( ms2File ) );
		String line = br.readLine();
		
		while (line.startsWith( "H" )) {
			String[] vals = line.split( "\\s" );
			if (vals.length < 2) continue;
			
			if (vals.length >= 3 && vals[1].equals( "CreationDate" ) )
				ms2Object.setCreationDate( vals[1] );
			else if (vals.length >= 3 && vals[1].equals( "Extractor" ) )
				ms2Object.setExtractor( vals[1] );
			else if (vals.length >= 3 && vals[1].equals( "ExtractorVersion" ) )
				ms2Object.setExtractorVersion( vals[1] );
			else if (vals.length >= 3 && vals[1].equals( "ExtractorOptions" ) )
				ms2Object.setExtractorOptions( vals[1] );
			else if (vals.length >= 3 && vals[1].equals( "IAnalyzer" ) )
				ms2Object.setIAnalyzer( vals[1] );
			else if (vals.length >= 3 && vals[1].equals( "IAnalyzerVersion" ) )
				ms2Object.setIAnalyzerVersion( vals[1] );
			else if (vals.length >= 3 && vals[1].equals( "IAnalyzerOptions" ) )
				ms2Object.setIAnalyzerOptions( vals[1] );
			else if (vals.length >= 3 && vals[1].equals( "DAnalyzer" ) )
				ms2Object.setDAnalyzer( vals[1] );
			else if (vals.length >= 3 && vals[1].equals( "DAnalyzerVersion" ) )
				ms2Object.setDAnalyzerVersion( vals[1] );
			else if (vals.length >= 3 && vals[1].equals( "DAnalyzerOptions" ) )
				ms2Object.setDAnalyzerOptions( vals[1] );
		}

		try {
			br.close();
			br = null;
		} catch (Exception e) { ; }
		
		return ms2Object;
	}

	

	private String filename;

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

}
