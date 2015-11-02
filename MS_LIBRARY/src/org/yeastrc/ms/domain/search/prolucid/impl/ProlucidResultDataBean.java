/**
 * ProlucidResultDataImpl.java
 * @author Vagisha Sharma
 * Aug 26, 2008
 * @version 1.0
 */
package org.yeastrc.ms.domain.search.prolucid.impl;

import java.math.BigDecimal;

import org.yeastrc.ms.domain.search.prolucid.ProlucidResultData;

/**
 * 
 */
public class ProlucidResultDataBean implements ProlucidResultData {

    private BigDecimal calculatedMass;
    
    private int matchingIons = -1;
    private int predictedIons = -1;
    
    private Double primaryScore;
    private Double secondaryScore;
    private BigDecimal deltaCN;
    
    private int secondaryRank = -1;
    private int primaryRank = -1;
    
    
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

    
    // RANKS  
    @Override
    public int getPrimaryScoreRank() {
        return primaryRank;
    }
    
    @Override
    public int getSecondaryScoreRank() {
        return secondaryRank;
    }
    
    // SCORES
    @Override
    public Double getPrimaryScore() {
        return primaryScore;
    }

    @Override
    public Double getSecondaryScore() {
        return secondaryScore;
    }
    
    @Override
    public BigDecimal getDeltaCN() {
        return deltaCN;
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

    public void setPrimaryScore(Double primaryScore) {
        this.primaryScore = primaryScore;
    }

    public void setSecondaryScore(Double secondaryScore) {
        this.secondaryScore = secondaryScore;
    }

    public void setDeltaCN(BigDecimal deltaCN) {
        this.deltaCN = deltaCN;
    }

    public void setSecondaryScoreRank(int secondaryRank) {
        this.secondaryRank = secondaryRank;
    }

    public void setPrimaryScoreRank(int primaryRank) {
        this.primaryRank = primaryRank;
    }
}
