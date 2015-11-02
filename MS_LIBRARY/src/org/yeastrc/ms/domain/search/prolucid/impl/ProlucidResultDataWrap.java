/**
 * ProlucidResultDataDbImpl.java
 * @author Vagisha Sharma
 * Aug 26, 2008
 * @version 1.0
 */
package org.yeastrc.ms.domain.search.prolucid.impl;

import java.math.BigDecimal;

import org.yeastrc.ms.domain.search.prolucid.ProlucidResultData;
import org.yeastrc.ms.domain.search.prolucid.ProlucidResultDataWId;

/**
 * 
 */
public class ProlucidResultDataWrap implements ProlucidResultDataWId {

    private int resultId;
    private ProlucidResultData data;
    
    public ProlucidResultDataWrap(ProlucidResultData data, int resultId) {
        this.data = data;
        this.resultId = resultId;
    }
    
    public int getResultId() {
        return resultId;
    }

    @Override
    public Double getPrimaryScore() {
        return data.getPrimaryScore();
    }

    @Override
    public BigDecimal getCalculatedMass() {
        return data.getCalculatedMass();
    }

    @Override
    public BigDecimal getDeltaCN() {
        return data.getDeltaCN();
    }

    @Override
    public int getMatchingIons() {
        return data.getMatchingIons();
    }

    @Override
    public int getPredictedIons() {
        return data.getPredictedIons();
    }

    @Override
    public int getSecondaryScoreRank() {
        return data.getSecondaryScoreRank();
    }

    @Override
    public Double getSecondaryScore() {
        return data.getSecondaryScore();
    }

    @Override
    public int getPrimaryScoreRank() {
        return data.getPrimaryScoreRank();
    }
}
