/**
 * 
 */
package org.yeastrc.ms.domain.protinfer.proteinProphet;

/**
 * ProteinProphetRunSummary.java
 * @author Vagisha Sharma
 * Jul 14, 2010
 * 
 */
public final class ProteinProphetRunSummary {

	private int piRunId;
	private int prophetGroupCount;
	private int indistGroupCount;
	private int proteinCount;
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
	public int getProphetGroupCount() {
		return prophetGroupCount;
	}
	public void setProphetGroupCount(int prophetGroupCount) {
		this.prophetGroupCount = prophetGroupCount;
	}
	/**
	 * Returns the number of indistinguishable groups that are NOT subsumed.
	 * @param indistGroupCount
	 */
	public int getIndistGroupCount() {
		return indistGroupCount;
	}
	public void setIndistGroupCount(int indistGroupCount) {
		this.indistGroupCount = indistGroupCount;
	}
	/**
	 * Returns the number of proteins that are NOT subsumed.
	 * @param indistGroupCount
	 */
	public int getProteinCount() {
		return proteinCount;
	}
	public void setProteinCount(int proteinCount) {
		this.proteinCount = proteinCount;
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
