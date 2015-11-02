package org.yeastrc.ms.domain.analysis.percolator;

import java.math.BigDecimal;

public interface PercolatorResultDataWId {

    public abstract int getRunSearchAnalysisId();
    
    /**
     * This will return the ID of the base search results which the Percolator result is based on
     * (id column in the msRunSearchResult table).
     * @return
     */
    public abstract int getSearchResultId();
    
    /**
     * Returns the ID of the peptide-level result, if present. Returns 0 otherwise.
     * @return
     */
    public abstract int getPeptideResultId();
    
    /**
     * @return the qvalue
     */
    public abstract double getQvalue();
    
    /**
     * @return the p-value
     */
    public abstract double getPvalue();
    
    /**
     * @return the posterior error probability or -1.0 if there was no posterior probability 
     * for this result
     */
    public abstract double getPosteriorErrorProbability();
    
    /**
     * @return the percolator discriminant score or -1.0 if there was no discriminant score. 
     */
    public abstract Double getDiscriminantScore();
    
    /**
     * @return the Predicted Retention Time
     */
    public abstract BigDecimal getPredictedRetentionTime();
}
