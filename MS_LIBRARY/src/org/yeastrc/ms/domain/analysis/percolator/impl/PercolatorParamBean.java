package org.yeastrc.ms.domain.analysis.percolator.impl;

import org.yeastrc.ms.domain.analysis.percolator.PercolatorParam;

public class PercolatorParamBean implements PercolatorParam {

    private String paramName;
    private String paramValue;
    
    
    @Override
    public String getParamName() {
        return paramName;
    }

    @Override
    public String getParamValue() {
        return paramValue;
    }

    public void setParamName(String paramName) {
        this.paramName = paramName;
    }

    public void setParamValue(String paramValue) {
        this.paramValue = paramValue;
    }

}
