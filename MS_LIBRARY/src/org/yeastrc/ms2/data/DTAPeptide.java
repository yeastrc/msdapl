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
public class DTAPeptide {

	// Instance variables
	private int id;
	private int resultID;
	private int searchId;
	private int scanId;
	private boolean unique;
	private String filename;
	private double XCorr;
	private double deltaCN;
	private double MH;
	private double calcMH;
	private double totalIntensity;
	private int spRank;
	private double spScore;
	private double ionProportion;
	private int redundancy;
	private String sequence;
	private double pI;
	private String peptide;
	private double confPercent;
	private double ZScore;
	private String ppm;
	
	/**
	 * @return the ppm
	 */
	public String getPpm() {
		return ppm;
	}


	/**
	 * @param ppm the ppm to set
	 */
	public void setPpm(String ppm) {
		this.ppm = ppm;
	}


	/**
	 * Get the Peptide Sequence object that corresponds to the actual sequence found.
	 * @return
	 * @throws Exception
	 */
	public String getPeptide() throws Exception {
		return this.peptide;
	}

	
	/**
	 * @return Returns the calcMH.
	 */
	public double getCalcMH() {
		return calcMH;
	}
	/**
	 * @param calcMH The calcMH to set.
	 */
	public void setCalcMH(double calcMH) {
		this.calcMH = calcMH;
	}
	/**
	 * @return Returns the deltaCN.
	 */
	public double getDeltaCN() {
		return deltaCN;
	}
	/**
	 * @param deltaCN The deltaCN to set.
	 */
	public void setDeltaCN(double deltaCN) {
		this.deltaCN = deltaCN;
	}
	/**
	 * @return Returns the filename.
	 */
	public String getFilename() {
		return filename;
	}
	/**
	 * @param filename The filename to set.
	 */
	public void setFilename(String filename) {
		this.filename = filename;
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
     * @return Returns the scanID.
     */
    public int getScanID() {
        return scanId;
    }
    /**
     * @param scanId The scanID to set.
     */
    public void setScanID(int scanId) {
        this.scanId = scanId;
    }
    
    /**
     * @return Returns the searchId.
     */
    public int getSearchID() {
        return searchId;
    }
    /**
     * @param searchId The searchId to set.
     */
    public void setSearchID(int searchId) {
        this.searchId = searchId;
    }
    
	/**
	 * @return Returns the ionProportion.
	 */
	public double getIonProportion() {
		return ionProportion;
	}
	/**
	 * @param ionProportion The ionProportion to set.
	 */
	public void setIonProportion(double ionProportion) {
		this.ionProportion = ionProportion;
	}
	/**
	 * @return Returns the mH.
	 */
	public double getMH() {
		return MH;
	}
	/**
	 * @param mh The mH to set.
	 */
	public void setMH(double mh) {
		MH = mh;
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
	 * @return Returns the redundancy.
	 */
	public int getRedundancy() {
		return redundancy;
	}
	/**
	 * @param redundancy The redundancy to set.
	 */
	public void setRedundancy(int redundancy) {
		this.redundancy = redundancy;
	}
	/**
	 * @return Returns the resultID.
	 */
	public int getResultID() {
		return resultID;
	}
	/**
	 * @param resultID The resultID to set.
	 */
	public void setResultID(int resultID) {
		this.resultID = resultID;
	}
	/**
	 * @return Returns the sequence.
	 */
	public String getSequence() {
		return sequence;
	}
	/**
	 * @param sequence The sequence to set.
	 */
	public void setSequence(String sequence) {
		this.sequence = sequence;
	}
	/**
	 * @return Returns the spRank.
	 */
	public int getSpRank() {
		return spRank;
	}
	/**
	 * @param spRank The spRank to set.
	 */
	public void setSpRank(int spRank) {
		this.spRank = spRank;
	}
	/**
	 * @return Returns the spScore.
	 */
	public double getSpScore() {
		return spScore;
	}
	/**
	 * @param spScore The spScore to set.
	 */
	public void setSpScore(double spScore) {
		this.spScore = spScore;
	}
	/**
	 * @return Returns the totalIntensity.
	 */
	public double getTotalIntensity() {
		return totalIntensity;
	}
	/**
	 * @param totalIntensity The totalIntensity to set.
	 */
	public void setTotalIntensity(double totalIntensity) {
		this.totalIntensity = totalIntensity;
	}
	/**
	 * @return Returns the unique.
	 */
	public boolean isUnique() {
		return unique;
	}
	/**
	 * @param unique The unique to set.
	 */
	public void setUnique(boolean unique) {
		this.unique = unique;
	}
	/**
	 * @return Returns the xCorr.
	 */
	public double getXCorr() {
		return XCorr;
	}
	/**
	 * @param corr The xCorr to set.
	 */
	public void setXCorr(double corr) {
		XCorr = corr;
	}
	
	/**
	 * @return Returns the confPercent.
	 */
	public double getConfPercent() {
		return confPercent;
	}
	/**
	 * @param confPercent The confPercent to set.
	 */
	public void setConfPercent(double confPercent) {
		this.confPercent = confPercent;
	}

	/**
	 * @return Returns the zScore.
	 */
	public double getZScore() {
		return ZScore;
	}
	/**
	 * @param score The zScore to set.
	 */
	public void setZScore(double score) {
		ZScore = score;
	}
}
