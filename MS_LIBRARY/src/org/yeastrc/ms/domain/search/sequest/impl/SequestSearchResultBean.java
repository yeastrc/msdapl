package org.yeastrc.ms.domain.search.sequest.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.yeastrc.ms.domain.search.MsSearchResultPeptide;
import org.yeastrc.ms.domain.search.MsSearchResultProtein;
import org.yeastrc.ms.domain.search.MsSearchResultProteinIn;
import org.yeastrc.ms.domain.search.impl.SearchResultBean;
import org.yeastrc.ms.domain.search.impl.SearchResultPeptideBean;
import org.yeastrc.ms.domain.search.impl.SearchResultProteinBean;
import org.yeastrc.ms.domain.search.sequest.SequestResultData;
import org.yeastrc.ms.domain.search.sequest.SequestSearchResult;
import org.yeastrc.ms.domain.search.sequest.SequestSearchResultIn;

public class SequestSearchResultBean extends SearchResultBean implements SequestSearchResult {

    private SequestResultDataBean sequestData;
    
    public SequestSearchResultBean() {
        sequestData = new SequestResultDataBean();
    }
    
    public static SequestSearchResultBean create(SequestSearchResultIn result, int runSearchId, int scanId) {
        
        SequestSearchResultBean bean = new SequestSearchResultBean();
        bean.setRunSearchId(runSearchId);
        bean.setScanId(scanId);
        bean.setCharge(result.getCharge());
        bean.setObservedMass(result.getObservedMass());
        
        MsSearchResultPeptide resPeptide = result.getResultPeptide();
        SearchResultPeptideBean peptide = new SearchResultPeptideBean();
        peptide.setPeptideSequence(resPeptide.getPeptideSequence());
        peptide.setPreResidue(resPeptide.getPreResidue());
        peptide.setPostResidue(resPeptide.getPostResidue());
        peptide.setDynamicResidueModifications(resPeptide.getResultDynamicResidueModifications());
        peptide.setDynamicTerminalModifications(resPeptide.getResultDynamicTerminalModifications());
        bean.setResultPeptide(peptide);
        
        List<MsSearchResultProteinIn> proteins = result.getProteinMatchList();
        List<MsSearchResultProtein> prots = new ArrayList<MsSearchResultProtein>(proteins.size());
        for(MsSearchResultProteinIn p: proteins) {
            SearchResultProteinBean pb = new SearchResultProteinBean();
            pb.setAccession(p.getAccession());
            prots.add(pb);
        }
        bean.setProteinMatchList(prots);
        
        SequestResultData seqInfo = result.getSequestResultData();
        bean.setxCorrRank(seqInfo.getxCorrRank());
        bean.setSpRank(seqInfo.getSpRank());
        bean.setDeltaCN(seqInfo.getDeltaCN());
        bean.setDeltaCNstar(seqInfo.getDeltaCNstar());
        bean.setxCorr(seqInfo.getxCorr());
        bean.setEvalue(seqInfo.getEvalue());
        bean.setSp(seqInfo.getSp());
        bean.setCalculatedMass(seqInfo.getCalculatedMass());
        bean.setMatchingIons(seqInfo.getMatchingIons());
        bean.setPredictedIons(seqInfo.getPredictedIons());
        
        
        bean.setValidationStatus(result.getValidationStatus());
        return bean;
    }
    /**
     * @param xcorrRank the xCorrRank to set
     */
    public void setxCorrRank(int xcorrRank) {
        sequestData.setxCorrRank(xcorrRank);
    }
    
    /**
     * @param spRank the spRank to set
     */
    public void setSpRank(int spRank) {
        sequestData.setSpRank(spRank);
    }
    
    /**
     * @param deltaCN the deltaCN to set
     */
    public void setDeltaCN(BigDecimal deltaCN) {
        sequestData.setDeltaCN(deltaCN);
    }
    
    /**
     * @param deltaCNstar the deltaCNstar to set
     */
    public void setDeltaCNstar(BigDecimal deltaCNstar) {
        sequestData.setDeltaCNstar(deltaCNstar);
    }
    
    /**
     * @param xcorr the xCorr to set
     */
    public void setxCorr(BigDecimal xcorr) {
        sequestData.setxCorr(xcorr);
    }
    
    /**
     * @param sp the sp to set
     */
    public void setSp(BigDecimal sp) {
        sequestData.setSp(sp);
    }
    
    public void setEvalue(Double evalue) {
        sequestData.setEvalue(evalue);
    }

    public void setCalculatedMass(BigDecimal calculatedMass) {
        sequestData.setCalculatedMass(calculatedMass);
    }
    
    public void setMatchingIons(int matchingIons) {
        sequestData.setMatchingIons(matchingIons);
    }
    
    public void setPredictedIons(int predictedIons) {
        sequestData.setPredictedIons(predictedIons);
    }

    @Override
    public SequestResultData getSequestResultData() {
        return sequestData;
    }
}
