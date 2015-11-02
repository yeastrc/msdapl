package org.yeastrc.ms.domain.search.sequest.impl;

import java.math.BigDecimal;

import org.yeastrc.ms.domain.search.MsSearchResultProteinIn;
import org.yeastrc.ms.domain.search.impl.SearchResult;
import org.yeastrc.ms.domain.search.sequest.SequestResultData;
import org.yeastrc.ms.domain.search.sequest.SequestSearchResultIn;


public class SequestResult extends SearchResult implements SequestSearchResultIn {

  
    private SequestResultDataBean scores;
    
    public SequestResult() {
        super();
        scores = new SequestResultDataBean();
    }
    
    /**
     * @param numMatchingIons the numMatchingIons to set
     */
    public void setMatchingIons(int numMatchingIons) {
        scores.setMatchingIons(numMatchingIons);
    }

    /**
     * @param numPredictedIons the numPredictedIons to set
     */
    public void setPredictedIons(int numPredictedIons) {
        scores.setPredictedIons(numPredictedIons);
    }
    
    /**
     * @param xcorrRank the xcorrRank to set
     */
    public void setxCorrRank(int xcorrRank) {
        scores.setxCorrRank(xcorrRank);
    }

    /**
     * @param spRank the spRank to set
     */
    public void setSpRank(int spRank) {
        scores.setSpRank(spRank);
    }

    /**
     * @param mass the mass to set
     */
    public void setCalculatedMass(BigDecimal mass) {
        scores.setCalculatedMass(mass);
    }

    /**
     * @param deltaCN the deltaCN to set
     */
    public void setDeltaCN(BigDecimal deltaCN) {
        scores.setDeltaCN(deltaCN);
    }
    
    /**
     * @param deltaCNstar the deltaCNstar to set
     */
    public void setDeltaCNstar(BigDecimal deltaCNstar) {
        scores.setDeltaCNstar(deltaCNstar);
    }

    /**
     * @param xcorr the xcorr to set
     */
    public void setxCorr(BigDecimal xcorr) {
        scores.setxCorr(xcorr);
    }

    /**
     * @param sp the sp to set
     */
    public void setSp(BigDecimal sp) {
        scores.setSp(sp);
    }

    /**
     * @param evalue
     */
    public void setEvalue(Double evalue) {
        scores.setEvalue(evalue);
    }
    
    public String toString() {
        StringBuilder buf = new StringBuilder();
        buf.append("M\t");
        buf.append(scores.getxCorrRank());
        buf.append("\t");
        buf.append(scores.getSpRank());
        buf.append("\t");
        buf.append(scores.getCalculatedMass());
        buf.append("\t");
        buf.append(scores.getDeltaCN().stripTrailingZeros());
        buf.append("\t");
        buf.append(scores.getDeltaCNstar().stripTrailingZeros());
        buf.append("\t");
        buf.append(scores.getxCorr().stripTrailingZeros());
        buf.append("\t");
        buf.append(scores.getSp().stripTrailingZeros());
        buf.append("\t");
        buf.append(scores.getMatchingIons());
        buf.append("\t");
        buf.append(scores.getPredictedIons());
        buf.append("\t");
        buf.append(getOriginalPeptideSequence());
        buf.append("\t");
        buf.append(getValidationStatus());
    
        buf.append("\n");
    
        for (MsSearchResultProteinIn locus: getProteinMatchList()) {
            buf.append(locus.toString());
            buf.append("\n");
        }
        buf.deleteCharAt(buf.length() -1); // delete last new line
        return buf.toString();
    }
    
    @Override
    public SequestResultData getSequestResultData() {
        return scores;
    }
}
