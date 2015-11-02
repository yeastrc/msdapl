/**
 * ProlucidResult.java
 * @author Vagisha Sharma
 * Aug 26, 2008
 * @version 1.0
 */
package org.yeastrc.ms.domain.search.prolucid.impl;

import java.math.BigDecimal;

import org.yeastrc.ms.domain.search.impl.SearchResult;
import org.yeastrc.ms.domain.search.prolucid.ProlucidResultData;
import org.yeastrc.ms.domain.search.prolucid.ProlucidSearchResultIn;

/**
 * 
 */
public class ProlucidResult extends SearchResult implements ProlucidSearchResultIn{

    
    private ProlucidResultDataBean resultData;
    
    
    public ProlucidResult() {
        super();
        resultData = new ProlucidResultDataBean();
    }
    
    public void setNumMatchingIons(int numMatchingIons) {
       resultData.setMatchingIons(numMatchingIons);
    }

    public void setNumPredictedIons(int numPredictedIons) {
        resultData.setPredictedIons(numPredictedIons);
    }
    
    public void setPrimaryScoreRank(int primaryScoreRank) {
        resultData.setPrimaryScoreRank(primaryScoreRank);
    }

    public void setSecondaryScoreRank(int secondaryScoreRank) {
        resultData.setSecondaryScoreRank(secondaryScoreRank);
    }

    public void setMass(BigDecimal mass) {
        resultData.setCalculatedMass(mass);
    }

    public void setDeltaCN(BigDecimal deltaCN) {
        resultData.setDeltaCN(deltaCN);
    }
    
    public void setPrimaryScore(Double primaryScore) {
        resultData.setPrimaryScore(primaryScore);
    }
    
    public void setSecondaryScore(Double secondaryScore) {
        resultData.setSecondaryScore(secondaryScore);
    }
    
    @Override
    public ProlucidResultData getProlucidResultData() {
        return resultData;
    }
}
