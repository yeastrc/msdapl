/**
 * PeptideProphetResultBean.java
 * @author Vagisha Sharma
 * Jun 24, 2009
 * @version 1.0
 */
package org.yeastrc.ms.domain.analysis.peptideProphet.impl;

import org.yeastrc.ms.domain.analysis.peptideProphet.PeptideProphetResult;
import org.yeastrc.ms.domain.search.impl.SearchResultBean;

/**
 * 
 */
public class PeptideProphetResultBean extends SearchResultBean implements PeptideProphetResult {

    private PeptideProphetResultData data = new PeptideProphetResultData();
    private int peptideProphetResultId;
    private int runSearchAnalysisId;
    
    public boolean hasAllNttProb() {
        return data.hasAllNttProb();
    }
    
    public void setAllNttProb(String allNttProb) {
        this.data.setAllNttProb(allNttProb);
    }

    @Override
    public double getMassDifference() {
        return data.getMassDifference();
    }
    
    public void setMassDifference(double massDiff) {
        data.setMassDifference(massDiff);
    }

    @Override
    public double getMassDifferenceRounded() {
        return Math.round(data.getMassDifference() * 1000.0) / 1000.0;
    }

    @Override
    public int getNumMissedCleavages() {
        return data.getNumMissedCleavages();
    }
    
    public void setNumMissedCleavages(int nmc) {
        this.data.setNumMissedCleavages(nmc);
    }

    @Override
    public int getNumEnzymaticTermini() {
        return data.getNumEnzymaticTermini();
    }
    
    public void setNumEnzymaticTermini(int ntt) {
        this.data.setNumEnzymaticTermini(ntt);
    }

    @Override
    public double getProbability() {
        return data.getProbability();
    }
    
    public void setProbability(double probability) {
        this.data.setProbability(probability);
    }

    @Override
    public double getProbabilityRounded() {
        return Math.round(data.getProbability() * 1000.0) / 1000.0;
    }

    @Override
    public int getRunSearchAnalysisId() {
        return this.runSearchAnalysisId;
    }
    
    public void setRunSearchAnalysisId(int runSearchAnalysisId) {
        this.runSearchAnalysisId = runSearchAnalysisId;
    }
    
    @Override
    public double getfVal() {
        return data.getfVal();
    }
    
    public void setfVal(double fVal) {
        this.data.setfVal(fVal);
    }

    @Override
    public double getfValRounded() {
        return Math.round(data.getfVal() * 1000.0) / 1000.0;
    }

    @Override
    public double getProbabilityNet_0() {
        return data.getProbabilityNet_0();
    }

    @Override
    public double getProbabilityNet_1() {
        return data.getProbabilityNet_1();
    }

    @Override
    public double getProbabilityNet_2() {
        return data.getProbabilityNet_2();
    }
    
    public void setProbabilityNet_0(double probabilityNet_0) {
        data.setProbabilityNet_0(probabilityNet_0);
    }

    public void setProbabilityNet_1(double probabilityNet_1) {
        data.setProbabilityNet_1(probabilityNet_1);
    }

    public void setProbabilityNet_2(double probabilityNet_2) {
        data.setProbabilityNet_2(probabilityNet_2);
    }
    
    @Override
    public int getPeptideProphetResultId() {
        return this.peptideProphetResultId;
    }
    
    public void setPeptideProphetResultId(int peptideProphetResultId) {
        this.peptideProphetResultId = peptideProphetResultId;
    }
}
