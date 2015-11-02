/**
 * SequestResultData.java
 * @author Vagisha Sharma
 * Aug 18, 2008
 * @version 1.0
 */
package org.yeastrc.ms.domain.search.sequest.impl;

import java.math.BigDecimal;

import org.yeastrc.ms.domain.search.sequest.SequestResultData;

/**
 * 
 */
public class SequestResultDataBean implements SequestResultData {

    private BigDecimal calculatedMass;  // Calculated M+H+ value for this sequence
    private BigDecimal deltaCN;
    private BigDecimal deltaCNstar;
    private int matchingIons = -1;
    private int predictedIons = -1;
    private BigDecimal sp;
    private int spRank = -1;
    private Double evalue;
    private BigDecimal xcorr;
    private int xcorrRank = -1;
    
    
    @Override
    public BigDecimal getCalculatedMass() {
        return calculatedMass;
    }

    @Override
    public BigDecimal getDeltaCN() {
        return deltaCN;
    }

    @Override
    public BigDecimal getDeltaCNstar() {
        return deltaCNstar;
    }
    
    @Override
    public int getMatchingIons() {
        return matchingIons;
    }

    @Override
    public int getPredictedIons() {
        return predictedIons;
    }

    @Override
    public BigDecimal getSp() {
        return sp;
    }

    @Override
    public int getSpRank() {
        return spRank;
    }

    @Override
    public Double getEvalue() {
        return evalue;
    }

    @Override
    public BigDecimal getxCorr() {
        return xcorr;
    }

    @Override
    public int getxCorrRank() {
        return xcorrRank;
    }

    public void setCalculatedMass(BigDecimal calculatedMass) {
        this.calculatedMass = calculatedMass;
    }

    public void setDeltaCN(BigDecimal deltaCN) {
        this.deltaCN = deltaCN;
    }
    
    public void setDeltaCNstar(BigDecimal deltaCNstar) {
        this.deltaCNstar = deltaCNstar;
    }

    public void setMatchingIons(int matchingIons) {
        this.matchingIons = matchingIons;
    }

    public void setPredictedIons(int predictedIons) {
        this.predictedIons = predictedIons;
    }

    public void setSp(BigDecimal sp) {
        this.sp = sp;
    }

    public void setSpRank(int spRank) {
        this.spRank = spRank;
    }

    public void setEvalue(Double evalue) {
        this.evalue = evalue;
    }

    public void setxCorr(BigDecimal xcorr) {
        this.xcorr = xcorr;
    }

    public void setxCorrRank(int xcorrRank) {
        this.xcorrRank = xcorrRank;
    }
}
