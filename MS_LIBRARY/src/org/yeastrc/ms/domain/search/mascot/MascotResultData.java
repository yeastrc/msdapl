/**
 * MascotResultData.java
 * @author Vagisha Sharma
 * Oct 12, 2009
 * @version 1.0
 */
package org.yeastrc.ms.domain.search.mascot;

import java.math.BigDecimal;

/**
 * 
 */
public interface MascotResultData {

    public abstract int getRank();
    
    public abstract void setRank(int rank);
    
    public abstract BigDecimal getIonScore();
    
    public abstract void setIonScore(BigDecimal ionScore);

    public abstract BigDecimal getIdentityScore();
    
    public abstract void setIdentityScore(BigDecimal identityScore);

    public abstract int getStar();
    
    public abstract void setStar(int star);
    
    public abstract BigDecimal getHomologyScore();
    
    public abstract void setHomologyScore(BigDecimal homologyScore);

    public abstract BigDecimal getExpect();
    
    public abstract void setExpect(BigDecimal expect);
    
    public abstract BigDecimal getCalculatedMass();
    
    public abstract void setCalculatedMass(BigDecimal mass);

    public abstract int getMatchingIons();
    
    public abstract void setMatchingIons(int matchingIons);

    public abstract int getPredictedIons();
    
    public abstract void setPredictedIons(int predictedIons);
}
