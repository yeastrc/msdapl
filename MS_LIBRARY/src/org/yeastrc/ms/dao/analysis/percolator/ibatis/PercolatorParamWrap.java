package org.yeastrc.ms.dao.analysis.percolator.ibatis;

import org.yeastrc.ms.domain.analysis.percolator.PercolatorParam;

public class PercolatorParamWrap {

    private int analysisId;
    private PercolatorParam param;
    
    public PercolatorParamWrap(PercolatorParam param, int analysisId) {
        this.param = param;
        this.analysisId = analysisId;
    }
   
    public String getParamName() {
        return param.getParamName();
    }
    
    public String getParamValue() {
        return param.getParamValue();
    }
    
    public int getSearchAnalysisId() {
        return analysisId;
    }
}
