/*
 * YatesResult.java
 *
 * Created November 11, 2003
 *
 * Michael Riffle <mriffle@u.washington.edu>
 *
 */

package org.yeastrc.ms2.data;

import java.util.List;

/**
 * 
 */
public class DTAResult {

	/**
	 * Get the MS Run for which this is a result
	 */
	public DTARun getRun() throws Exception {
		return this.run;
	}

	/**
	 * Set the peptides that belong to this Result.  This will only be called when creating new
	 * Yates Runs via the web upload form.
	 * @param peptides  The peptides to associate with this Result
	 */
	public void setPeptides(List<DTAPeptide> peptides) {
		this.peptides = peptides;
	}
	
	/**
	 * Get the Set of peptides associated with this mass spec result (mass spec hit)
	 */
	public List<DTAPeptide> getPeptides() throws Exception {
		return this.peptides;	
	}
	
	// Constructor
	public DTAResult() {
		this.id = 0;
		this.runID = 0;
		this.hitProtein = 0;
		this.sequenceCount = 0;
		this.spectrumCount = 0;
		this.sequenceCoverage = 0.0;
		this.length = 0;
		this.molecularWeight = 0;
		this.pI = 0;
		this.validationStatus = null;
		this.description = null;
		this.run = null;
	}

	// Instance variables
	private int id;
	private int runID;
	private int hitProtein;
	private int sequenceCount;
	private int spectrumCount;
	private double sequenceCoverage;
	private int length;
	private int molecularWeight;
	private double pI;
	private String validationStatus;
	private String description;
	private DTARun run;
	private List<DTAPeptide> peptides;

	/**
	 * @return Returns the description.
	 */
	public String getDescription() {
		return description;
	}
	/**
	 * @param description The description to set.
	 */
	public void setDescription(String description) {
		this.description = description;
	}
	/**
	 * @return Returns the hitORF.
	 */
	public int getHitProtein() {
		return hitProtein;
	}
	/**
	 * @param hitORF The hitORF to set.
	 */
	public void setHitProtein(int hitProtein) {
		this.hitProtein = hitProtein;
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
	 * @return Returns the length.
	 */
	public int getLength() {
		return length;
	}
	/**
	 * @param length The length to set.
	 */
	public void setLength(int length) {
		this.length = length;
	}
	/**
	 * @return Returns the molecularWeight.
	 */
	public int getMolecularWeight() {
		return molecularWeight;
	}
	/**
	 * @param molecularWeight The molecularWeight to set.
	 */
	public void setMolecularWeight(int molecularWeight) {
		this.molecularWeight = molecularWeight;
	}
	/**
	 * @return Returns the pI.
	 */
	public double getPI() {
		return pI;
	}
	/**
	 * @param pi The pI to set.
	 */
	public void setPI(double pi) {
		pI = pi;
	}
	/**
	 * @return Returns the runID.
	 */
	public int getRunID() {
		return runID;
	}
	/**
	 * @param runID The runID to set.
	 */
	public void setRunID(int runID) {
		this.runID = runID;
	}
	/**
	 * @return Returns the sequenceCount.
	 */
	public int getSequenceCount() {
		return sequenceCount;
	}
	/**
	 * @param sequenceCount The sequenceCount to set.
	 */
	public void setSequenceCount(int sequenceCount) {
		this.sequenceCount = sequenceCount;
	}
	/**
	 * @return Returns the sequenceCoverage.
	 */
	public double getSequenceCoverage() {
		return sequenceCoverage;
	}
	/**
	 * @param sequenceCoverage The sequenceCoverage to set.
	 */
	public void setSequenceCoverage(double sequenceCoverage) {
		this.sequenceCoverage = sequenceCoverage;
	}
	/**
	 * @return Returns the spectrumCount.
	 */
	public int getSpectrumCount() {
		return spectrumCount;
	}
	/**
	 * @param spectrumCount The spectrumCount to set.
	 */
	public void setSpectrumCount(int spectrumCount) {
		this.spectrumCount = spectrumCount;
	}
	/**
	 * @return Returns the validationStatus.
	 */
	public String getValidationStatus() {
		return validationStatus;
	}
	/**
	 * @param validationStatus The validationStatus to set.
	 */
	public void setValidationStatus(String validationStatus) {
		this.validationStatus = validationStatus;
	}
}