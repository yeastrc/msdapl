/**
 * MascotResult.java
 * @author Vagisha Sharma
 * Oct 5, 2009
 * @version 1.0
 */
package org.yeastrc.ms.domain.search.mascot.impl;

import java.math.BigDecimal;

import org.yeastrc.ms.domain.search.impl.SearchResult;
import org.yeastrc.ms.domain.search.mascot.MascotResultData;
import org.yeastrc.ms.domain.search.mascot.MascotSearchResultIn;

/**
 * 
 */
public class MascotResult extends SearchResult implements MascotSearchResultIn{

    
    private MascotResultDataBean resultData;
    
    
    public MascotResult() {
        super();
        resultData = new MascotResultDataBean();
    }
    
    public void setNumMatchingIons(int numMatchingIons) {
       resultData.setMatchingIons(numMatchingIons);
    }

    public void setNumPredictedIons(int numPredictedIons) {
        resultData.setPredictedIons(numPredictedIons);
    }
    
    public void setCalculatedMass(BigDecimal mass) {
        resultData.setCalculatedMass(mass);
    }
    
    public void setStar(int star) {
        resultData.setStar(star);
    }

    public void setIonScore(BigDecimal ionScore) {
        resultData.setIonScore(ionScore);
    }
    
    public void setIdentityScore(BigDecimal identityScore) {
        resultData.setIdentityScore(identityScore);
    }
    
    public void setHomologyScore(BigDecimal homologyScore) {
        resultData.setHomologyScore(homologyScore);
    }
    
    public void setExpect(BigDecimal expect) {
        resultData.setExpect(expect);
    }
    
    @Override
    public MascotResultData getMascotResultData() {
        return resultData;
    }
}
