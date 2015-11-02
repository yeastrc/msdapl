/**
 * PercolatorResultIn.java
 * @author Vagisha Sharma
 * Dec 10, 2008
 * @version 1.0
 */
package org.yeastrc.ms.domain.analysis.percolator;

import java.math.BigDecimal;

import org.yeastrc.ms.domain.search.MsSearchResultIn;

/**
 * 
 */
public interface PercolatorResultIn extends MsSearchResultIn {

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
     * @return the percolator discriminant score or null if there was no discriminant score. 
     */
    public abstract Double getDiscriminantScore();
    
    
    /**
     * @return the predicted retention time or null if there is not predicted RT.
     */
    public abstract BigDecimal getPredictedRetentionTime();
    
}
