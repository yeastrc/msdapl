/*
 * YatesPeptide.java
 *
 * Created November 11, 2003
 *
 * Michael Riffle <mriffle@u.washington.edu>
 *
 */

package org.yeastrc.ms2.data;


/**
 * 
 */
public class DTACycle {

	// Our constructor
	public DTACycle() {
		this.cycleID = 0;
		this.runID = 0;
		this.cycleFileName = null;
	}

	// Document these setter methods soon, though they're self explanitory
	public void setRunID(int arg) { this.runID = arg; }
	public void setFileName(String arg) { this.cycleFileName = arg; }
	public void setID(int id) { this.cycleID = id; }

	// Document these getter methods soon, though they're self explanitory
	public int getID() { return this.cycleID; }
	public int getRunID() { return this.runID; }
	public String getFileName() { return this.cycleFileName; }

	// Our instance variables
	private int cycleID;
	private int runID;
	private String cycleFileName;

}