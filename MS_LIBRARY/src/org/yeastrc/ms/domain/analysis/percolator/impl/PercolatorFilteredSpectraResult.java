/**
 * PercolatorFilteredSpectraResult.java
 * @author Vagisha Sharma
 * Oct 3, 2010
 */
package org.yeastrc.ms.domain.analysis.percolator.impl;

import java.util.List;

/**
 * 
 */
public class PercolatorFilteredSpectraResult {

	private int id;
	private int runSearchAnalysisId;
	private double qvalue;
	private int total;
	private int filtered;
	private List<PercolatorBinnedSpectraResult> binnedResults;
	
	
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
	public double getQvalue() {
		return qvalue;
	}
	public void setQvalue(double qvalue) {
		this.qvalue = qvalue;
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
	public List<PercolatorBinnedSpectraResult> getBinnedResults() {
		return binnedResults;
	}
	public void setBinnedResults(List<PercolatorBinnedSpectraResult> binnedResults) {
		this.binnedResults = binnedResults;
	}
}
