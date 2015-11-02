/**
 * SequestRunSearchResultBase.java
 * @author Vagisha Sharma
 * Aug 18, 2008
 * @version 1.0
 */
package org.yeastrc.ms.domain.search.sequest;

import java.math.BigDecimal;

/**
 * 
 */
public interface SequestResultData {

    /**
     * @return the xCorrRank
     */
    public abstract int getxCorrRank();
    
    public abstract void setxCorrRank(int rank);

    /**
     * @return the spRank
     */
    public abstract int getSpRank();
    
    public abstract void setSpRank(int rank);

    /**
     * @return the deltaCN
     */
    public abstract BigDecimal getDeltaCN();
    
    public abstract void setDeltaCN(BigDecimal deltaCn);
    
    /**
     * @return the deltaCNstar
     */
    public abstract BigDecimal getDeltaCNstar();
    
    public abstract void setDeltaCNstar(BigDecimal dcnStar);

    /**
     * @return the xCorr
     */
    public abstract BigDecimal getxCorr();
    
    public abstract void setxCorr(BigDecimal xcorr);

    /**
     * @return the sp
     */
    public abstract BigDecimal getSp();
    
    public abstract void setSp(BigDecimal sp);

    /**
     * @return the e-value
     */
    public abstract Double getEvalue();
    
    public abstract void setEvalue(Double evalue);
    

    public abstract BigDecimal getCalculatedMass();
    
    public abstract void setCalculatedMass(BigDecimal mass);
    

    public abstract int getMatchingIons();
    
    public abstract void setMatchingIons(int matchingIons);
    

    public abstract int getPredictedIons();
    
    public abstract void setPredictedIons(int predictedIons);
}
