/**
 * XtandemResultDataBean.java
 * @author Vagisha Sharma
 * Oct 20, 2009
 * @version 1.0
 */
package org.yeastrc.ms.domain.search.xtandem.impl;

import java.math.BigDecimal;

import org.yeastrc.ms.domain.search.xtandem.XtandemResultData;

/**
 * 
 */
public class XtandemResultDataBean implements XtandemResultData {

    private int rank = -1;
    private BigDecimal hyperScore;
    private BigDecimal nextScore;
    private BigDecimal bscore;
    private BigDecimal yscore;
    private BigDecimal expect;
    private int matchingIons = -1;
    private int predictedIons = -1;
    private BigDecimal calculatedMass;
    
    @Override
    public int getRank() {
        return rank;
    }

    @Override
    public void setRank(int rank) {
        this.rank = rank;
    }
    
    @Override
    public BigDecimal getHyperScore() {
        return hyperScore;
    }

    @Override
    public BigDecimal getNextScore() {
        return nextScore;
    }

    @Override
    public BigDecimal getBscore() {
        return bscore;
    }
    
    @Override
    public BigDecimal getYscore() {
        return yscore;
    }
    
    @Override
    public BigDecimal getExpect() {
        return expect;
    }

    @Override
    public BigDecimal getCalculatedMass() {
        return calculatedMass;
    }
    
    @Override
    public int getMatchingIons() {
        return matchingIons;
    }

    @Override
    public int getPredictedIons() {
        return predictedIons;
    }
    
    public void setHyperScore(BigDecimal hyperScore) {
        this.hyperScore = hyperScore;
    }

    public void setNextScore(BigDecimal nextScore) {
        this.nextScore = nextScore;
    }

    public void setBscore(BigDecimal bscore) {
        this.bscore = bscore;
    }
    
    public void setYscore(BigDecimal yscore) {
        this.yscore = yscore;
    }

    public void setExpect(BigDecimal expect) {
        this.expect = expect;
    }

    public void setCalculatedMass(BigDecimal calculatedMass) {
        this.calculatedMass = calculatedMass;
    }
    
    public void setMatchingIons(int matchingIons) {
        this.matchingIons = matchingIons;
    }

    public void setPredictedIons(int predictedIons) {
        this.predictedIons = predictedIons;
    }

}
