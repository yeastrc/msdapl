package org.yeastrc.ms.domain.search.mascot.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.yeastrc.ms.domain.search.MsSearchResultPeptide;
import org.yeastrc.ms.domain.search.MsSearchResultProtein;
import org.yeastrc.ms.domain.search.MsSearchResultProteinIn;
import org.yeastrc.ms.domain.search.impl.SearchResultBean;
import org.yeastrc.ms.domain.search.impl.SearchResultPeptideBean;
import org.yeastrc.ms.domain.search.impl.SearchResultProteinBean;
import org.yeastrc.ms.domain.search.mascot.MascotResultData;
import org.yeastrc.ms.domain.search.mascot.MascotSearchResult;
import org.yeastrc.ms.domain.search.mascot.MascotSearchResultIn;

public class MascotSearchResultBean extends SearchResultBean implements MascotSearchResult {

    private MascotResultDataBean mascotData;
    
    public MascotSearchResultBean() {
        mascotData = new MascotResultDataBean();
    }
    
    public static MascotSearchResultBean create(MascotSearchResultIn result, int runSearchId, int scanId) {
        
        MascotSearchResultBean bean = new MascotSearchResultBean();
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
        
        MascotResultData mascotInfo = result.getMascotResultData();
        bean.setIonScore(mascotInfo.getIonScore());
        bean.setIdentityScore(mascotInfo.getIdentityScore());
        bean.setHomologyScore(mascotInfo.getHomologyScore());
        bean.setExpect(mascotInfo.getExpect());
        bean.setStar(mascotInfo.getStar());
        bean.setCalculatedMass(mascotInfo.getCalculatedMass());
        bean.setMatchingIons(mascotInfo.getMatchingIons());
        bean.setPredictedIons(mascotInfo.getPredictedIons());
        
        
        bean.setValidationStatus(result.getValidationStatus());
        return bean;
    }
    
    
    public void setIdentityScore(BigDecimal identityScore) {
        mascotData.setIdentityScore(identityScore);
    }
    
    public void setRank(int rank) {
        mascotData.setRank(rank);
    }
    
    public void setIonScore(BigDecimal ionScore) {
        mascotData.setIonScore(ionScore);
    }
    
    public void setHomologyScore(BigDecimal homologyScore) {
        mascotData.setHomologyScore(homologyScore);
    }
    
    public void setExpect(BigDecimal expect) {
        mascotData.setExpect(expect);
    }
    
    public void setStar(int star) {
        mascotData.setStar(star);
    }
    
    public void setCalculatedMass(BigDecimal calculatedMass) {
        mascotData.setCalculatedMass(calculatedMass);
    }
    
    public void setMatchingIons(int matchingIons) {
        mascotData.setMatchingIons(matchingIons);
    }
    
    public void setPredictedIons(int predictedIons) {
        mascotData.setPredictedIons(predictedIons);
    }
    
    @Override
    public MascotResultData getMascotResultData() {
        return mascotData;
    }
}
