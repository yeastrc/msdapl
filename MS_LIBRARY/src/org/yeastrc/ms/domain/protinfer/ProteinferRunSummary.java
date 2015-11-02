/**
 * 
 */
package org.yeastrc.ms.domain.protinfer;

/**
 * ProteinferRunSummary.java
 * @author Vagisha Sharma
 * Jul 14, 2010
 * 
 */
public final class ProteinferRunSummary {

	private int piRunId;
	private int parsimIndistGroupCount;
	private int parsimProteinCount;
	private int uniqPeptSeqCount;
	private int uniqIonCount;
	private int spectrumCount;
	private int minSpectrumCount;
	private int maxSpectrumCount;
	
	public int getPiRunId() {
		return piRunId;
	}
	public void setPiRunId(int piRunId) {
		this.piRunId = piRunId;
	}
	public int getParsimIndistGroupCount() {
		return parsimIndistGroupCount;
	}
	public void setParsimIndistGroupCount(int parsimIndistGroupCount) {
		this.parsimIndistGroupCount = parsimIndistGroupCount;
	}
	public int getParsimProteinCount() {
		return parsimProteinCount;
	}
	public void setParsimProteinCount(int parsimProteinCount) {
		this.parsimProteinCount = parsimProteinCount;
	}
	public int getUniqPeptSeqCount() {
		return uniqPeptSeqCount;
	}
	public void setUniqPeptSeqCount(int uniqPeptSeqCount) {
		this.uniqPeptSeqCount = uniqPeptSeqCount;
	}
	public int getUniqIonCount() {
		return uniqIonCount;
	}
	public void setUniqIonCount(int uniqIonCount) {
		this.uniqIonCount = uniqIonCount;
	}
	public int getSpectrumCount() {
		return spectrumCount;
	}
	public void setSpectrumCount(int spectrumCount) {
		this.spectrumCount = spectrumCount;
	}
	public int getMinSpectrumCount() {
		return minSpectrumCount;
	}
	public void setMinSpectrumCount(int minSpectrumCount) {
		this.minSpectrumCount = minSpectrumCount;
	}
	public int getMaxSpectrumCount() {
		return maxSpectrumCount;
	}
	public void setMaxSpectrumCount(int maxSpectrumCount) {
		this.maxSpectrumCount = maxSpectrumCount;
	}
}
