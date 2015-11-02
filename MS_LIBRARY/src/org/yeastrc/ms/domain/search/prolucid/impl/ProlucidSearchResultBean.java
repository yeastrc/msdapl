/**
 * ProlucidSearchResultDbImpl.java
 * @author Vagisha Sharma
 * Aug 26, 2008
 * @version 1.0
 */
package org.yeastrc.ms.domain.search.prolucid.impl;

import java.math.BigDecimal;

import org.yeastrc.ms.domain.search.impl.SearchResultBean;
import org.yeastrc.ms.domain.search.prolucid.ProlucidResultData;
import org.yeastrc.ms.domain.search.prolucid.ProlucidSearchResult;

/**
 * 
 */
public class ProlucidSearchResultBean extends SearchResultBean implements ProlucidSearchResult {

    private ProlucidResultDataBean prolucidData;
    
    public ProlucidSearchResultBean() {
        prolucidData = new ProlucidResultDataBean();
    }
    
    /**
     * @param primaryRank the primaryRank to set
     */
    public void setPrimaryScoreRank(int primaryRank) {
        prolucidData.setPrimaryScoreRank(primaryRank);
    }
   
    /**
     * @param secondaryRank the secondaryRank to set
     */
    public void setSecondaryScoreRank(int secondaryRank) {
        prolucidData.setSecondaryScoreRank(secondaryRank);
    }
    
    /**
     * @param deltaCN the deltaCN to set
     */
    public void setDeltaCN(BigDecimal deltaCN) {
        prolucidData.setDeltaCN(deltaCN);
    }
    
    /**
     * @param primaryScore the primaryScore to set
     */
    public void setPrimaryScore(Double primaryScore) {
        prolucidData.setPrimaryScore(primaryScore);
    }
   
    public void setSecondaryScore(Double secondaryScore) {
        prolucidData.setSecondaryScore(secondaryScore);
    }
    
    public void setCalculatedMass(BigDecimal calculatedMass) {
        prolucidData.setCalculatedMass(calculatedMass);
    }
   
    public void setMatchingIons(int matchingIons) {
        prolucidData.setMatchingIons(matchingIons);
    }
   
    public void setPredictedIons(int predictedIons) {
        prolucidData.setPredictedIons(predictedIons);
    }

    @Override
    public ProlucidResultData getProlucidResultData() {
        return prolucidData;
    }
}
