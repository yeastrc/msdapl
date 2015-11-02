/*
 * YatesSQT.java
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
public class SQT {

	// Our constructor
	public SQT() {
		this.cycleID = 0;
		this.sqtData = null;
	}

	/**
	 * Set the cycle ID to which this data belongs.
	 * @param id The cycle ID to which this data belongs.
	 */
	public void setCycleID(int id) { this.cycleID = id; }

	
	/**
	 * Set the contents of the supplied file as the SQT Data
	 * @param file
	 * @throws Exception
	 */
	public void setSQTFile(File file) throws Exception {
		this.sqtData = Compresser.getInstance().compressFile(file);
	}
	
	/**
	 * Set the SQT data
	 * @param arg
	 */
	public void setSQTData(String arg) throws Exception {
		try {
			this.sqtData = Compresser.getInstance().compressString(arg);
		} catch (Exception e) {
			throw new Exception ("Error compressing SQT Data: " + e.getMessage() );
		} finally {
			arg = "";
			System.gc();
		}
	}

	/**
	 * Sets the SQT data
	 * @param arg A StringBuffer containing the uncompressed SQT data
	 * @throws Exception If there is a problem compression the data
	 */
	public void setSQTData(StringBuffer arg) throws Exception {
		try {
			this.sqtData = Compresser.getInstance().compressStringBuffer(arg);
		} catch (Exception e) {
			throw new Exception ("Error compressing SQT Data: " + e.getMessage() );
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
	
	// The SQT Data as a compressed byte array (as it is in the database)
	private byte[] sqtData;

	/**
	 * @return the sqtData
	 */
	public byte[] getSqtData() {
		return sqtData;
	}



}