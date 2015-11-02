/**
 * SequestResultDataDbImpl.java
 * @author Vagisha Sharma
 * Aug 18, 2008
 * @version 1.0
 */
package org.yeastrc.ms.domain.search.sequest.impl;

import java.math.BigDecimal;

import org.yeastrc.ms.domain.search.sequest.SequestResultData;
import org.yeastrc.ms.domain.search.sequest.SequestResultDataWId;


/**
 * 
 */
public class SequestResultDataWrap implements SequestResultDataWId {

    private int resultId;
    private SequestResultData data;
    
    public SequestResultDataWrap(SequestResultData data, int resultId) {
        this.data = data;
        this.resultId = resultId;
    }
    
    public int getResultId() {
        return resultId;
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
    public Double getEvalue() {
        return data.getEvalue();
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
    public BigDecimal getSp() {
        return data.getSp();
    }

    @Override
    public int getSpRank() {
        return data.getSpRank();
    }

    @Override
    public BigDecimal getxCorr() {
        return data.getxCorr();
    }

    @Override
    public int getxCorrRank() {
        return data.getxCorrRank();
    }

    @Override
    public BigDecimal getDeltaCNstar() {
        return data.getDeltaCNstar();
    }

    @Override
    public void setCalculatedMass(BigDecimal mass) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setDeltaCN(BigDecimal deltaCn) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setDeltaCNstar(BigDecimal dcnStar) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setEvalue(Double evalue) {
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
    public void setSp(BigDecimal sp) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setSpRank(int rank) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setxCorr(BigDecimal xcorr) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setxCorrRank(int rank) {
        throw new UnsupportedOperationException();
    }
}
