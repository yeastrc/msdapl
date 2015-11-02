/**
 * PercolatorResultBean.java
 * @author Vagisha Sharma
 * Dec 10, 2008
 * @version 1.0
 */
package org.yeastrc.ms.domain.analysis.percolator.impl;

import java.math.BigDecimal;

import org.yeastrc.ms.domain.analysis.percolator.PercolatorResult;
import org.yeastrc.ms.domain.search.impl.SearchResultBean;
import org.yeastrc.utils.RoundingUtilsMSLIBRARY;

/**
 *
 */
public class PercolatorResultBean extends SearchResultBean implements PercolatorResult{

    private PercolatorResultDataBean data = new PercolatorResultDataBean();
    private int percolatorResultId;

    @Override
    public Double getDiscriminantScore() {
        return data.getDiscriminantScore();
    }

    public void setDiscriminantScore(Double discriminantScore) {
        data.setDiscriminantScore(discriminantScore);
    }

    @Override
    public int getRunSearchAnalysisId() {
        return data.getRunSearchAnalysisId();
    }

    public void setRunSearchAnalysisId(int runSearchAnalysisId) {
        this.data.setRunSearchAnalysisId(runSearchAnalysisId);
    }

    @Override
	public int getPeptideResultId() {
		return data.getPeptideResultId();
	}

    public void setPeptideResultId(int peptideResultId) {
    	this.data.setPeptideResultId(peptideResultId);
    }

    @Override
    public double getPosteriorErrorProbability() {
        return data.getPosteriorErrorProbability();
    }

    @Override
    public double getPosteriorErrorProbabilityRounded() {
        return Math.round(data.getPosteriorErrorProbability() * 1000.0) / 1000.0;
    }

	@Override
	public String getPosteriorErrorProbabilityRounded3SignificantDigits() {

		String rounded = RoundingUtilsMSLIBRARY.getInstance().roundThreeSignificantDigits( data.getPosteriorErrorProbability() );
		return rounded;
	}

    public void setPosteriorErrorProbability(double pep) {
        data.setPosteriorErrorProbability(pep);
    }



    @Override
    public double getQvalue() {
        return data.getQvalue();
    }

    @Override
    public double getQvalueRounded() {
        return Math.round(data.getQvalue() * 1000.0) / 1000.0;
    }


	@Override
	public String getQvalueRounded3SignificantDigits() {

		String rounded = RoundingUtilsMSLIBRARY.getInstance().roundThreeSignificantDigits( data.getQvalue() );
		return rounded;
	}


    public void setQvalue(double qvalue) {
        data.setQvalue(qvalue);
    }

    @Override
	public Double getPvalue() {
		return data.getPvalue();
	}

	@Override
	public Double getPvalueRounded() {
		return Math.round(data.getPvalue() * 1000.0) / 1000.0;
	}

	@Override
	public String getPvalueRounded3SignificantDigits() {

		String rounded = RoundingUtilsMSLIBRARY.getInstance().roundThreeSignificantDigits( data.getPvalue() );
		return rounded;
	}

	public void setPvalue(double pvalue) {
		data.setPvalue(pvalue);
	}

    @Override
    public Double getDiscriminantScoreRounded() {
        Double discriminantScore = data.getDiscriminantScore();
        if(discriminantScore == null)   return null;
        return Math.round(discriminantScore * 1000.0) / 1000.0;
    }


	@Override
	public String getDiscriminantScoreRounded3SignificantDigits() {

        Double discriminantScore = data.getDiscriminantScore();
        if(discriminantScore == null)   return null;

        String rounded = RoundingUtilsMSLIBRARY.getInstance().roundThreeSignificantDigits( discriminantScore );
		return rounded;
	}


    @Override
    public BigDecimal getPredictedRetentionTime() {
        return data.getPredictedRetentionTime();
    }

    public void setPredictedRetentionTime(BigDecimal predictedRT) {
        data.setPredictedRetentionTime(predictedRT);
    }

    @Override
    public int getPercolatorResultId() {
        return this.percolatorResultId;
    }

    public void setPercolatorResultId(int percolatorResultId) {
        this.percolatorResultId = percolatorResultId;
    }
}
