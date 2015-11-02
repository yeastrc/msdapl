/**
 * XtandemResultDataWrap.java
 * @author Vagisha Sharma
 * Oct 20, 2009
 * @version 1.0
 */
package org.yeastrc.ms.domain.search.xtandem.impl;

import java.math.BigDecimal;

import org.yeastrc.ms.domain.search.xtandem.XtandemResultData;
import org.yeastrc.ms.domain.search.xtandem.XtandemResultDataWId;


/**
 * 
 */
public class XtandemResultDataWrap implements XtandemResultDataWId {

    private int resultId;
    private XtandemResultData data;
    
    public XtandemResultDataWrap(XtandemResultData data, int resultId) {
        this.data = data;
        this.resultId = resultId;
    }
    
    public int getResultId() {
        return resultId;
    }
    
    @Override
    public int getRank() {
        return data.getRank();
    }

    @Override
    public BigDecimal getHyperScore() {
        return data.getHyperScore();
    }

    @Override
    public BigDecimal getNextScore() {
        return data.getNextScore();
    }

    @Override
    public BigDecimal getBscore() {
        return data.getBscore();
    }
    
    @Override
    public BigDecimal getYscore() {
        return data.getYscore();
    }

    @Override
    public BigDecimal getExpect() {
        return data.getExpect();
    }

    @Override
    public BigDecimal getCalculatedMass() {
        return data.getCalculatedMass();
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
    public void setCalculatedMass(BigDecimal mass) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setExpect(BigDecimal expect) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setHyperScore(BigDecimal hyperScore) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setNextScore(BigDecimal nextScore) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setBscore(BigDecimal bscore) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public void setYscore(BigDecimal yscore) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setMatchingIons(int matchingIons) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setPredictedIons(int predictedIons) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setRank(int rank) {
        throw new UnsupportedOperationException();
    }
}
