/**
 * SpectrumMatchImpl.java
 * @author Vagisha Sharma
 * Jan 2, 2009
 * @version 1.0
 */
package edu.uwpr.protinfer.idpicker;

import edu.uwpr.protinfer.infer.SpectrumMatch;

/**
 * 
 */
public class SpectrumMatchNoFDRImpl implements SpectrumMatch {

    private int charge;
    private int resultId;
    private int searchResultId;
    private int sourceId;
    private int scanId;
    private String peptideModifiedSequence;
    private int rank;
    
    public void setCharge(int charge) {
        this.charge = charge;
    }
    
    public int getCharge() {
        return charge;
    }

    public void setResultId(int resultId) {
        this.resultId = resultId;
    }
    
    public int getResultId() {
        return this.resultId;
    }
    
    public void setSearchResultId(int searchResultId) {
        this.searchResultId = searchResultId;
    }
    
    public int getSearchResultId() {
        return this.searchResultId;
    }

    
    public int getSourceId() {
        return sourceId;
    }
    
    public void setSourceId(int sourceId) {
        this.sourceId = sourceId;
    }
    
    public void setScanId(int scanId) {
        this.scanId = scanId;
    }
    
    @Override
    public int getScanId() {
        return this.scanId;
    }

    @Override
    public int getRank() {
        return rank;
    }
    
    public void setRank(int rank) {
        this.rank = rank;
    }
    
    @Override
    public String getModifiedSequence() {
        return peptideModifiedSequence;
    }
    
    public void setModifiedSequence(String sequence) {
        this.peptideModifiedSequence = sequence;
    }
}
