package edu.uwpr.protinfer.infer;

public interface SpectrumMatch {

    // database ID of the result (e.g. Percolator result id)
    public abstract int getResultId();
    
    // database ID of the underlying search result that this result corresponds to. 
    // If this result and the underlying search result are the same, getResultId()
    // and getSearchResultId() will return the same ID. 
    public abstract int getSearchResultId();
    
    public abstract int getSourceId();
    
    public abstract int getScanId();
    
    public abstract int getCharge();
    
    /**
     * Returns the sequence with modifications
     * @return
     */
    public abstract String getModifiedSequence();
    
    public abstract int getRank();
    
    public abstract void setRank(int rank);
}
