/*
 * YatesRun.java
 *
 * Created November 11, 2003
 *
 * Michael Riffle <mriffle@u.washington.edu>
 *
 */

package org.yeastrc.ms2.data;

import java.io.File;

import org.yeastrc.ms2.utils.*;

/**
 * 
 */
public class DTARun {

	/**
	 * @param selectTXT The File containing the DTASelect data to set
	 */
	public void setDTASelectTXTFile(File file) throws Exception {
		this.DTASelectTXTData = Compresser.getInstance().compressFile(file);
		this.hasDTASelectTXT = true;
	}
	
	public boolean hasDTASelectTXT() {
		return this.hasDTASelectTXT;
	}
	
	public byte[] getDTASelectTXTData() {
		return this.DTASelectTXTData;
	}
	
	// Constructor
	public DTARun () {
		this.id = 0;
		this.projectID = 0;
		this.baitProtein = 0;
		this.baitDesc = null;
		this.runDate = null;
		this.directoryName = null;

		this.DTASelectHTML = null;
		this.DTASelectFilterTXT = null;
		this.DTASelectParams = null;
		this.comments = null;
		this.targetSpecies = 0;
		this.uploadDate = null;
		
		this.hasDTASelectTXT = false;

	}

	
	// Instance variables.
	private int id;
	private int projectID;
	private String baitDesc;
	private int baitProtein;
	private int targetSpecies;
	private java.util.Date runDate;
	private java.util.Date uploadDate;
	private String directoryName;

	private byte[] DTASelectTXTData;
	private boolean hasDTASelectTXT;
	private String DTASelectHTML;
	private String DTASelectFilterTXT;
	private String DTASelectParams;
	private String comments;
	private String databaseName;
	
	/**
	 * Get the target species for this MS Run
	 * @return The target species (the species from which we're expecting identified protein), null if not set
	 */
	public int getTargetSpecies() {
		return this.targetSpecies;
	}
	
	/**
	 * Set the target species (the species from which we're expecting identified proteins)
	 * @param target The target species
	 */
	public void setTargetSpecies(int target) {
		this.targetSpecies = target;
	}

	/**
	 * @return Returns the comments.
	 */
	public String getComments() {
		return comments;
	}
	/**
	 * @param comments The comments to set.
	 */
	public void setComments(String comments) {
		this.comments = comments;
	}
	/**
	 * @return Returns the directoryName.
	 */
	public String getDirectoryName() {
		return directoryName;
	}
	/**
	 * @param directoryName The directoryName to set.
	 */
	public void setDirectoryName(String directoryName) {
		this.directoryName = directoryName;
	}
	/**
	 * @return Returns the dTASelectFilterTXT.
	 */
	public String getDTASelectFilterTXT() throws Exception {
		return this.DTASelectFilterTXT;
	}
	/**
	 * @param selectFilterTXT The DTASelectFilterTXT to set.
	 */
	public void setDTASelectFilterTXT(String selectFilterTXT) {
		DTASelectFilterTXT = selectFilterTXT;
	}
	/**
	 * @return Returns the DTASelectHTML.
	 */
	public String getDTASelectHTML() throws Exception {
		return this.DTASelectHTML;
	}

	/**
	 * @param selectHTML The dTASelectHTML to set.
	 */
	public void setDTASelectHTML(String selectHTML) {
		DTASelectHTML = selectHTML;
	}
	/**
	 * @return Returns the dTASelectParams.
	 */
	public String getDTASelectParams() throws Exception {
		return this.DTASelectParams;
	}
	/**
	 * @param selectParams The dTASelectParams to set.
	 */
	public void setDTASelectParams(String selectParams) {
		DTASelectParams = selectParams;
	}


	/**
	 * @return Returns the id.
	 */
	public int getId() {
		return id;
	}
	/**
	 * @param id The id to set.
	 */
	public void setId(int id) {
		this.id = id;
	}
	/**
	 * @return Returns the projectID.
	 */
	public int getProjectID() {
		return projectID;
	}
	/**
	 * @param projectID The projectID to set.
	 */
	public void setProjectID(int projectID) {
		this.projectID = projectID;
	}
	/**
	 * @return Returns the runDate.
	 */
	public java.util.Date getRunDate() {
		return runDate;
	}
	/**
	 * @param runDate The runDate to set.
	 */
	public void setRunDate(java.util.Date runDate) {
		this.runDate = runDate;
	}
	/**
	 * @return Returns the uploadDate.
	 */
	public java.util.Date getUploadDate() {
		return uploadDate;
	}
	/**
	 * @return Returns the baitProtein.
	 */
	public int getBaitProtein() {
		return baitProtein;
	}
	/**
	 * @param baitProtein The baitProtein to set.
	 */
	public void setBaitProtein(int baitProtein) {
		this.baitProtein = baitProtein;
	}

	/**
	 * @return Returns the baitDesc.
	 */
	public String getBaitDesc() {
		return baitDesc;
	}
	/**
	 * @param baitDesc The baitDesc to set.
	 */
	public void setBaitDesc(String baitDesc) {
		this.baitDesc = baitDesc;
	}
	
	/**
	 * @return Returns the databaseName.
	 */
	public String getDatabaseName() {
		return databaseName;
	}
	/**
	 * @param databaseName The databaseName to set.
	 */
	public void setDatabaseName(String databaseName) {
		this.databaseName = databaseName;
	}
}