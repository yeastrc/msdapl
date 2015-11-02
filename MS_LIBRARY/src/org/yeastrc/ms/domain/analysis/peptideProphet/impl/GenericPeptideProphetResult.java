package org.yeastrc.ms.domain.analysis.peptideProphet.impl;

import org.yeastrc.ms.domain.analysis.peptideProphet.GenericPeptideProphetResultIn;
import org.yeastrc.ms.domain.analysis.peptideProphet.PeptideProphetResultDataIn;
import org.yeastrc.ms.domain.search.MsSearchResultIn;

public class GenericPeptideProphetResult <T extends MsSearchResultIn> 
    implements GenericPeptideProphetResultIn<T> {

    public PeptideProphetResultDataIn ppRes;
    public T searchResult;

    public GenericPeptideProphetResult() {
        super();
    }

    public void setSearchResult(T searchResult) {
        this.searchResult = searchResult;
    }
    
    public T getSearchResult() {
        return this.searchResult;
    }
    
    public void setPeptideProphetResult(PeptideProphetResultDataIn ppRes) {
        this.ppRes = ppRes;
    }

    @Override
    public double getProbabilityNet_0() {
        return ppRes.getProbabilityNet_0();
    }

    @Override
    public double getProbabilityNet_1() {
        return ppRes.getProbabilityNet_1();
    }

    @Override
    public double getProbabilityNet_2() {
        return ppRes.getProbabilityNet_2();
    }

    @Override
    public double getMassDifference() {
        return ppRes.getMassDifference();
    }

    @Override
    public int getNumMissedCleavages() {
        return ppRes.getNumMissedCleavages();
    }

    @Override
    public int getNumEnzymaticTermini() {
        return ppRes.getNumEnzymaticTermini();
    }

    @Override
    public double getProbability() {
        return ppRes.getProbability();
    }

    @Override
    public double getfVal() {
        return ppRes.getfVal();
    }


}