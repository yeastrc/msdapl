/**
 * MascotResultDataBean.java
 * @author Vagisha Sharma
 * Oct 12, 2009
 * @version 1.0
 */
package org.yeastrc.ms.domain.search.mascot.impl;

import java.math.BigDecimal;

import org.yeastrc.ms.domain.search.mascot.MascotResultData;

/**
 * 
 */
public class MascotResultDataBean implements MascotResultData {

    private int rank = -1;
    private BigDecimal ionScore;
    private BigDecimal identityScore;
    private BigDecimal homologyScore;
    private BigDecimal expect;
    private int star = -1;
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
    public BigDecimal getIonScore() {
        return ionScore;
    }

    @Override
    public BigDecimal getIdentityScore() {
        return identityScore;
    }

    @Override
    public BigDecimal getHomologyScore() {
        return homologyScore;
    }
    
    @Override
    public int getStar() {
        return star;
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
    
    public void setIonScore(BigDecimal ionScore) {
        this.ionScore = ionScore;
    }

    public void setIdentityScore(BigDecimal identityScore) {
        this.identityScore = identityScore;
    }

    public void setHomologyScore(BigDecimal homologyScore) {
        this.homologyScore = homologyScore;
    }

    public void setExpect(BigDecimal expect) {
        this.expect = expect;
    }

    public void setStar(int star) {
        this.star = star;
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
