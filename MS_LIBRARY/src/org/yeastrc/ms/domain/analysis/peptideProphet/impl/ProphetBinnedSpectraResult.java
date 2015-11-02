/**
 * ProphetBinnedSpectraResult.java
 * @author Vagisha Sharma
 * Aug 7, 2011
 */
package org.yeastrc.ms.domain.analysis.peptideProphet.impl;

/**
 * 
 */
public class ProphetBinnedSpectraResult {

	private int prophetSpectraResultId;
	private double binStart;
	private double binEnd;
	private int total;
	private int filtered;
	
	public int getProphetFilteredSpectraId() {
		return prophetSpectraResultId;
	}
	public void setProphetFilteredSpectraId(int prophetSpectraResultId) {
		this.prophetSpectraResultId = prophetSpectraResultId;
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
