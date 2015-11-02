/*
 * YatesCycle.java
 *
 * Created November 11, 2003
 *
 * Michael Riffle <mriffle@u.washington.edu>
 *
 */

package org.yeastrc.ms2.data;

import java.io.File;
import org.yeastrc.ms2.utils.Compresser;

/**
 * 
 */
public class MS2 {

	// Our constructor
	public MS2() {
		this.cycleID = 0;
		this.ms2Data = null;
	}
	/**
	 * Set the cycle ID to which this data belongs.
	 * @param id The cycle ID to which this data belongs.
	 */
	public void setCycleID(int id) { this.cycleID = id; }

	/**
	 * Set the contents of the supplied file as the MS2 Data
	 * @param file
	 * @throws Exception
	 */
	public void setMS2File(File file) throws Exception {
		this.ms2Data = Compresser.getInstance().compressFile(file);
	}
	
	/**
	 * Set the MS2 data
	 * @param arg
	 */
	public void setMS2Data(String arg) throws Exception {
		try {
			this.ms2Data = Compresser.getInstance().compressString(arg);
		} catch (Exception e) {
			throw new Exception ("Error compressing MS2 Data: " + e.getMessage() );
		} finally {
			arg = "";
			System.gc();
		}
	}

	/**
	 * Sets the MS2 data
	 * @param arg A StringBuffer containing the uncompressed MS2 data
	 * @throws Exception If there is a problem compression the data
	 */
	public void setMS2Data(StringBuffer arg) throws Exception {
		try {
			this.ms2Data = Compresser.getInstance().compressStringBuffer(arg);
		} catch (Exception e) {
			throw new Exception ("Error compressing MS2 Data: " + e.getMessage() );
		} finally {
			System.gc();
		}
	}
	

	/**
	 * Get the cycle ID.
	 * @return the cycle ID
	 */
	public int getCycleID() { return this.cycleID; }


	// The cycle ID
	private int cycleID;
	
	// The MS2 Data as a compressed byte array (as it is stored in the database)
	private byte[] ms2Data;

	/**
	 * @return the ms2Data
	 */
	public byte[] getMs2Data() {
		return ms2Data;
	}

}