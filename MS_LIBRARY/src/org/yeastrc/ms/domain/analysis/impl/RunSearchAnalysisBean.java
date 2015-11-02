package org.yeastrc.ms.domain.analysis.impl;

import org.yeastrc.ms.domain.analysis.MsRunSearchAnalysis;
import org.yeastrc.ms.domain.search.SearchFileFormat;

public class RunSearchAnalysisBean implements MsRunSearchAnalysis{

    private int id;
    private int analysisId;
    private int runSearchId;
    private SearchFileFormat analysisFileFormat;
    
    @Override
    public SearchFileFormat getAnalysisFileFormat() {
        return analysisFileFormat;
    }

    @Override
    public int getAnalysisId() {
        return analysisId;
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public int getRunSearchId() {
        return runSearchId;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setAnalysisId(int analysisId) {
        this.analysisId = analysisId;
    }

    public void setRunSearchId(int runSearchId) {
        this.runSearchId = runSearchId;
    }

    public void setAnalysisFileFormat(SearchFileFormat analysisFileFormat) {
        this.analysisFileFormat = analysisFileFormat;
    }
}
