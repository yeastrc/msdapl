/**
 * ProphetFilteredPsmResult.java
 * @author Vagisha Sharma
 * Sep 29, 2010
 */
package org.yeastrc.ms.domain.analysis.peptideProphet.impl;

import java.util.List;

/**
 * 
 */
public class ProphetFilteredPsmResult {

	private int id;
	private int runSearchAnalysisId;
	private double probability;
	private int total;
	private int filtered;
	private List<ProphetBinnedPsmResult> binnedResults;
	
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getRunSearchAnalysisId() {
		return runSearchAnalysisId;
	}
	public void setRunSearchAnalysisId(int runSearchAnalysisId) {
		this.runSearchAnalysisId = runSearchAnalysisId;
	}
	public double getProbability() {
		return probability;
	}
	public void setProbability(double probability) {
		this.probability = probability;
	}
	public int getTotal() {
		return total;
	}
	public void setTotal(int total) {
		this.total = total;
	}
	public int getFiltered() {
		return filtered;
	}
	public void setFiltered(int filtered) {
		this.filtered = filtered;
	}
	public List<ProphetBinnedPsmResult> getBinnedResults() {
		return binnedResults;
	}
	public void setBinnedResults(List<ProphetBinnedPsmResult> binnedResults) {
		this.binnedResults = binnedResults;
	}
}
