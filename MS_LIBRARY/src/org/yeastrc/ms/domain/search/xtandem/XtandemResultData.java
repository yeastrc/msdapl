/**
 * XtandemResultData.java
 * @author Vagisha Sharma
 * Oct 20, 2009
 * @version 1.0
 */
package org.yeastrc.ms.domain.search.xtandem;

import java.math.BigDecimal;

/**
 * 
 */
public interface XtandemResultData {

    public abstract int getRank();
    
    public abstract void setRank(int rank);
    
    public abstract BigDecimal getHyperScore();
    
    public abstract void setHyperScore(BigDecimal hyperScore);

    public abstract BigDecimal getNextScore();
    
    public abstract void setNextScore(BigDecimal nextScore);

    public abstract BigDecimal getBscore();
    
    public abstract void setBscore(BigDecimal bscore);
    
    public abstract BigDecimal getYscore();
    
    public abstract void setYscore(BigDecimal yscore);

    public abstract BigDecimal getExpect();
    
    public abstract void setExpect(BigDecimal expect);
    
    public abstract BigDecimal getCalculatedMass();
    
    public abstract void setCalculatedMass(BigDecimal mass);

    public abstract int getMatchingIons();
    
    public abstract void setMatchingIons(int matchingIons);

    public abstract int getPredictedIons();
    
    public abstract void setPredictedIons(int predictedIons);
}
