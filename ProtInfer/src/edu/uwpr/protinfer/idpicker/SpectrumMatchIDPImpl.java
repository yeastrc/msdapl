package edu.uwpr.protinfer.idpicker;


public class SpectrumMatchIDPImpl implements SpectrumMatchIDP {

    private int charge;
    private int resultId;
    private int searchResultId;
    private int runSearchId;
    private int scanId;
    private double fdr = 1.0;
    private boolean accepted;
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
        return runSearchId;
    }
    
    public void setSourceId(int sourceId) {
        this.runSearchId = sourceId;
    }
    
    public void setScanId(int scanId) {
        this.scanId = scanId;
    }
    
    @Override
    public int getScanId() {
        return this.scanId;
    }

    @Override
    public double getFdr() {
        return fdr;
    }

    public void setFdr(double fdr) {
        this.fdr = fdr;
    }

    @Override
    public boolean isAccepted() {
        return this.accepted;
    }

    @Override
    public void setAccepted(boolean accepted) {
        this.accepted = accepted;
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
