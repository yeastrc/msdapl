/**
 * ProteinferIon.java
 * @author Vagisha Sharma
 * Dec 21, 2008
 * @version 1.0
 */
package org.yeastrc.ms.domain.protinfer;

import java.util.List;

/**
 * 
 */
public class GenericProteinferIon <T extends ProteinferSpectrumMatch>{

    private int id;
    private int pinferPeptideId;
    private int charge;
    private int modificationStateId;
    private String modifiedSequence;
    
    private T bestSpectrumMatch;
    private int spectrumCount;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getProteinferPeptideId() {
        return pinferPeptideId;
    }
    
    public void setProteinferPeptideId(int pinferPeptideId) {
        this.pinferPeptideId = pinferPeptideId;
    }
    
    public String getModifiedSequence() {
        return modifiedSequence;
    }

    public void setModifiedSequence(String sequence) {
        this.modifiedSequence = sequence;
    }
    
    public int getCharge() {
        return charge;
    }

    public void setCharge(int charge) {
        this.charge = charge;
    }

    public int getModificationStateId() {
        return modificationStateId;
    }

    public void setModificationStateId(int modificationStateId) {
        this.modificationStateId = modificationStateId;
    }

    public T getBestSpectrumMatch() {
        return bestSpectrumMatch;
    }

    /**
     * This method will keep the keep the first match in the list and ignore the rest.
     * @param bestSpectrumMatches
     */
    public void setBestSpectrumMatchList(List<T> bestSpectrumMatches) {
        if(bestSpectrumMatches != null && bestSpectrumMatches.size() > 0)
            this.bestSpectrumMatch = bestSpectrumMatches.get(0);
    }
    
    public int getSpectrumCount() {
        return spectrumCount;
    }

    public void setSpectrumCount(int spectrumCount) {
        this.spectrumCount = spectrumCount;
    }
    
    public String toString() {
        StringBuilder buf = new StringBuilder();
        buf.append("ID: "+id);
        buf.append("\t");
        buf.append("pinferPeptideId: "+pinferPeptideId);
        buf.append("\t");
        buf.append("charge: "+charge);
        buf.append("\t");
        buf.append("mod_seq: "+modifiedSequence);
        return buf.toString();
    }
}
