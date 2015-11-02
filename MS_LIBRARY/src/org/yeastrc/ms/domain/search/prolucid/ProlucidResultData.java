/**
 * ProlucidResultData.java
 * @author Vagisha Sharma
 * Aug 26, 2008
 * @version 1.0
 */
package org.yeastrc.ms.domain.search.prolucid;

import java.math.BigDecimal;

/**
 * 
 */
public interface ProlucidResultData {

    // RANKS
    /**
     * @return the primaryScoreRank
     */
    public abstract int getPrimaryScoreRank();
    
    /**
     * @return the secondaryScoreRank
     */
    public abstract int getSecondaryScoreRank();
    
    
    // SCORES
    /**
     * @return the primaryScore
     */
    public abstract Double getPrimaryScore();
    
    /**
     * @return the secondaryScore
     */
    public abstract Double getSecondaryScore();
    
    
    /**
     * @return the deltaCN
     */
    public abstract BigDecimal getDeltaCN();
    
    
    public abstract BigDecimal getCalculatedMass();

    public abstract int getMatchingIons();

    public abstract int getPredictedIons();
    
}
