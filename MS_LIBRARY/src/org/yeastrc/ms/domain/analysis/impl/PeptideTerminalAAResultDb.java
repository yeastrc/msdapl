/**
 * PeptideTerminiResult.java
 * @author Vagisha Sharma
 * Mar 4, 2011
 */
package org.yeastrc.ms.domain.analysis.impl;

/**
 * 
 */
public class PeptideTerminalAAResultDb {

	private int analysisId;
	
	private double scoreCutoff;
	private String scoreType;
	
	private int totalResultCount;
	private int numResultsWithEnzTerm_0;
	private int numResultsWithEnzTerm_1;
	private int numResultsWithEnzTerm_2;
	
	private String ntermMinusOneAminoAcidCount;
	private String ntermAminoAcidCount;
	private String ctermAminoAcidCount;
	private String ctermPlusOneAminoAcidCount;
	
	private String enzyme;
	private int enzymeId;
	
	
	public int getAnalysisId() {
		return analysisId;
	}
	public void setAnalysisId(int analysisId) {
		this.analysisId = analysisId;
	}
	public double getScoreCutoff() {
		return scoreCutoff;
	}
	public void setScoreCutoff(double scoreCutoff) {
		this.scoreCutoff = scoreCutoff;
	}
	public String getScoreType() {
		return scoreType;
	}
	public void setScoreType(String scoreType) {
		this.scoreType = scoreType;
	}
	public int getTotalResultCount() {
		return totalResultCount;
	}
	public void setTotalResultCount(int resultCount) {
		this.totalResultCount = resultCount;
	}
	
	public int getNumResultsWithEnzTerm_0() {
		return numResultsWithEnzTerm_0;
	}
	public void setNumResultsWithEnzTerm_0(int numResultsWithEnzTerm_0) {
		this.numResultsWithEnzTerm_0 = numResultsWithEnzTerm_0;
	}
	public int getNumResultsWithEnzTerm_1() {
		return numResultsWithEnzTerm_1;
	}
	public void setNumResultsWithEnzTerm_1(int numResultsWithEnzTerm_1) {
		this.numResultsWithEnzTerm_1 = numResultsWithEnzTerm_1;
	}
	public int getNumResultsWithEnzTerm_2() {
		return numResultsWithEnzTerm_2;
	}
	public void setNumResultsWithEnzTerm_2(int numResultsWithEnzTerm_2) {
		this.numResultsWithEnzTerm_2 = numResultsWithEnzTerm_2;
	}
	public String getNtermMinusOneAminoAcidCount() {
		return ntermMinusOneAminoAcidCount;
	}
	public void setNtermMinusOneAminoAcidCount(String ntermMinusOneAminoAcidCount) {
		this.ntermMinusOneAminoAcidCount = ntermMinusOneAminoAcidCount;
	}
	public String getNtermAminoAcidCount() {
		return ntermAminoAcidCount;
	}
	public void setNtermAminoAcidCount(String ntermAminoAcidCount) {
		this.ntermAminoAcidCount = ntermAminoAcidCount;
	}
	public String getCtermAminoAcidCount() {
		return ctermAminoAcidCount;
	}
	public void setCtermAminoAcidCount(String ctermAminoAcidCount) {
		this.ctermAminoAcidCount = ctermAminoAcidCount;
	}
	public String getCtermPlusOneAminoAcidCount() {
		return ctermPlusOneAminoAcidCount;
	}
	public void setCtermPlusOneAminoAcidCount(String ctermPlusOneAminoAcidCount) {
		this.ctermPlusOneAminoAcidCount = ctermPlusOneAminoAcidCount;
	}
	public String getEnzyme() {
		return enzyme;
	}
	public void setEnzyme(String enzyme) {
		this.enzyme = enzyme;
	}
	public int getEnzymeId() {
		return enzymeId;
	}
	public void setEnzymeId(int enzymeId) {
		this.enzymeId = enzymeId;
	}
}
