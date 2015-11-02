package org.yeastrc.ms.domain.search.xtandem.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.yeastrc.ms.domain.search.MsSearchResultPeptide;
import org.yeastrc.ms.domain.search.MsSearchResultProtein;
import org.yeastrc.ms.domain.search.MsSearchResultProteinIn;
import org.yeastrc.ms.domain.search.impl.SearchResultBean;
import org.yeastrc.ms.domain.search.impl.SearchResultPeptideBean;
import org.yeastrc.ms.domain.search.impl.SearchResultProteinBean;
import org.yeastrc.ms.domain.search.xtandem.XtandemResultData;
import org.yeastrc.ms.domain.search.xtandem.XtandemSearchResult;
import org.yeastrc.ms.domain.search.xtandem.XtandemSearchResultIn;

public class XtandemSearchResultBean extends SearchResultBean implements XtandemSearchResult {

    private XtandemResultDataBean xtandemData;
    
    public XtandemSearchResultBean() {
        xtandemData = new XtandemResultDataBean();
    }
    
    public static XtandemSearchResultBean create(XtandemSearchResultIn result, int runSearchId, int scanId) {
        
        XtandemSearchResultBean bean = new XtandemSearchResultBean();
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
        
        XtandemResultData xtandemInfo = result.getXtandemResultData();
        bean.setHyperScore(xtandemInfo.getHyperScore());
        bean.setNextScore(xtandemInfo.getNextScore());
        bean.setBscore(xtandemInfo.getBscore());
        bean.setYscore(xtandemInfo.getYscore());
        bean.setExpect(xtandemInfo.getExpect());
        bean.setCalculatedMass(xtandemInfo.getCalculatedMass());
        bean.setMatchingIons(xtandemInfo.getMatchingIons());
        bean.setPredictedIons(xtandemInfo.getPredictedIons());
        
        
        bean.setValidationStatus(result.getValidationStatus());
        return bean;
    }
    
    
    public void setHyperScore(BigDecimal hyperScore) {
        xtandemData.setHyperScore(hyperScore);
    }
    
    public void setRank(int rank) {
        xtandemData.setRank(rank);
    }
    
    public void setNextScore(BigDecimal nextScore) {
        xtandemData.setNextScore(nextScore);
    }
    
    public void setBscore(BigDecimal bscore) {
        xtandemData.setBscore(bscore);
    }
    
    public void setYscore(BigDecimal yscore) {
        xtandemData.setYscore(yscore);
    }
    
    public void setExpect(BigDecimal expect) {
        xtandemData.setExpect(expect);
    }
    
    public void setCalculatedMass(BigDecimal calculatedMass) {
        xtandemData.setCalculatedMass(calculatedMass);
    }
    
    public void setMatchingIons(int matchingIons) {
        xtandemData.setMatchingIons(matchingIons);
    }
    
    public void setPredictedIons(int predictedIons) {
        xtandemData.setPredictedIons(predictedIons);
    }
    
    @Override
    public XtandemResultData getXtandemResultData() {
        return xtandemData;
    }
}
