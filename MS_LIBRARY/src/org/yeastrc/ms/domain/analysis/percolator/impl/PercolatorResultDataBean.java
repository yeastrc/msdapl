package org.yeastrc.ms.domain.analysis.percolator.impl;

import java.math.BigDecimal;

import org.yeastrc.ms.domain.analysis.percolator.PercolatorResultDataWId;

public class PercolatorResultDataBean implements PercolatorResultDataWId {

    private int searchResultId;
    private int runSearchAnalysisId;
    private double qvalue = -1.0;
    private Double discriminantScore = null;
    private double pep = -1.0;
    private double pvalue = -1.0;
    private int peptideResultId;
    
    private BigDecimal predictedRT = null;
    
    @Override
    public Double getDiscriminantScore() {
        return discriminantScore;
    }

    @Override
    public int getRunSearchAnalysisId() {
        return runSearchAnalysisId;
    }

    @Override
	public int getPeptideResultId() {
		return peptideResultId;
	}
    
    @Override
    public double getPosteriorErrorProbability() {
        return pep;
    }

    @Override
    public int getSearchResultId() {
        return searchResultId;
    }

    @Override
    public double getQvalue() {
        return qvalue;
    }

    @Override
    public double getPvalue() {
    	return pvalue;
    }
    
    public void setDiscriminantScore(Double score) {
        this.discriminantScore = score;
    }
    
    public void setPosteriorErrorProbability(double pep) {
        this.pep = pep;
    }
    
    public void setQvalue(double qvalue) {
        this.qvalue = qvalue;
    }
    
    public void setPvalue(double pvalue) {
    	this.pvalue = pvalue;
    }
    
    public void setSearchResultId(int searchResultId) {
        this.searchResultId = searchResultId;
    }
    
    public void setRunSearchAnalysisId(int analysisId) {
        this.runSearchAnalysisId = analysisId;
    }
    
    public void setPeptideResultId(int peptideResultId) {
    	this.peptideResultId = peptideResultId;
    }
    
    @Override
    public BigDecimal getPredictedRetentionTime() {
        return predictedRT;
    }
    
    public void setPredictedRetentionTime(BigDecimal predictedRT) {
        this.predictedRT = predictedRT;
    }
	
}
