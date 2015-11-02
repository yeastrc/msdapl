/**
 * MascotResultDataWrap.java
 * @author Vagisha Sharma
 * Oct 12, 2009
 * @version 1.0
 */
package org.yeastrc.ms.domain.search.mascot.impl;

import java.math.BigDecimal;

import org.yeastrc.ms.domain.search.mascot.MascotResultData;
import org.yeastrc.ms.domain.search.mascot.MascotResultDataWId;


/**
 * 
 */
public class MascotResultDataWrap implements MascotResultDataWId {

    private int resultId;
    private MascotResultData data;
    
    public MascotResultDataWrap(MascotResultData data, int resultId) {
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
    public BigDecimal getIonScore() {
        return data.getIonScore();
    }

    @Override
    public BigDecimal getIdentityScore() {
        return data.getIdentityScore();
    }

    @Override
    public BigDecimal getHomologyScore() {
        return data.getHomologyScore();
    }

    @Override
    public int getStar() {
        return data.getStar();
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
    public void setHomologyScore(BigDecimal homologyScore) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setIdentityScore(BigDecimal identityScore) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setIonScore(BigDecimal ionScore) {
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

    @Override
    public void setStar(int star) {
        throw new UnsupportedOperationException();
    }
}
