/**
 * PercolatorBinnedSpectraResult.java
 * @author Vagisha Sharma
 * Oct 3, 2010
 */
package org.yeastrc.ms.domain.analysis.percolator.impl;

/**
 * 
 */
public class PercolatorBinnedSpectraResult {

	private int percSpectraResultId;
	private double binStart;
	private double binEnd;
	private int total;
	private int filtered;
	
	public int getPercolatorFilteredSpectraId() {
		return percSpectraResultId;
	}
	public void setPercolatorFilteredSpectraId(int percSpectraResultId) {
		this.percSpectraResultId = percSpectraResultId;
	}
	public double getBinStart() {
		return binStart;
	}
	public void setBinStart(double binStart) {
		this.binStart = binStart;
	}
	public double getBinEnd() {
		return binEnd;
	}
	public void setBinEnd(double binEnd) {
		this.binEnd = binEnd;
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
}
