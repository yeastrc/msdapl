/**
 * PercolatorPeptideResult.java
 * @author Vagisha Sharma
 * Sep 17, 2010
 */
package org.yeastrc.ms.domain.analysis.percolator.impl;

import java.util.ArrayList;
import java.util.List;

import org.yeastrc.ms.domain.analysis.percolator.PercolatorPeptideResult;
import org.yeastrc.ms.domain.analysis.percolator.PercolatorResult;
import org.yeastrc.ms.domain.search.MsSearchResultPeptide;
import org.yeastrc.ms.domain.search.MsSearchResultProtein;
import org.yeastrc.utils.RoundingUtilsMSLIBRARY;


/**
 *
 */
public class PercolatorPeptideResultBean implements PercolatorPeptideResult {

	private int id;
    private int searchAnalysisId;
    private MsSearchResultPeptide resultPeptide;
    private double qvalue = -1.0;
    private Double discriminantScore = null;
    private double pep = -1.0;
    private double pvalue = -1.0;

    private List<Integer> psmIdList;
    private List<PercolatorResult> psmList;

    private List<MsSearchResultProtein> proteinMatchList;

    public int getId() {
    	return id;
    }

    public void setId(int id) {
    	this.id = id;
    }

    @Override
    public int getSearchAnalysisId() {
        return searchAnalysisId;
    }

    public void setSearchAnalysisId(int searchAnalysisId) {
    	this.searchAnalysisId = searchAnalysisId;
    }

    @Override
    public double getQvalue() {
        return qvalue;
    }

    @Override
    public double getQvalueRounded() {
        return Math.round(qvalue * 1000.0) / 1000.0;
    }

	@Override
	public String getQvalueRounded3SignificantDigits() {

		String rounded = RoundingUtilsMSLIBRARY.getInstance().roundThreeSignificantDigits( qvalue );
		return rounded;
	}

    public void setQvalue(double qvalue) {
        this.qvalue = qvalue;
    }

    @Override
    public double getPosteriorErrorProbability() {
        return pep;
    }

    @Override
    public double getPosteriorErrorProbabilityRounded() {
        return Math.round(pep * 1000.0) / 1000.0;
    }

	@Override
	public String getPosteriorErrorProbabilityRounded3SignificantDigits() {

		String rounded = RoundingUtilsMSLIBRARY.getInstance().roundThreeSignificantDigits( pep );
		return rounded;
	}

    public void setPosteriorErrorProbability(double pep) {
        this.pep = pep;
    }

    @Override
    public Double getDiscriminantScore() {
        return discriminantScore;
    }

    @Override
	public Double getDiscriminantScoreRounded() {
    	if(discriminantScore != null)
    		return Math.round(discriminantScore * 1000.0) / 1000.0;
    	else
    		return null;
	}


	@Override
	public String getDiscriminantScoreRounded3SignificantDigits() {

        if(discriminantScore == null) {
        	return null;
        }

        String rounded = RoundingUtilsMSLIBRARY.getInstance().roundThreeSignificantDigits( discriminantScore );
		return rounded;
	}


    public void setDiscriminantScore(Double score) {
        this.discriminantScore = score;
    }


    @Override
    public double getPvalue() {
    	return pvalue;
    }

    @Override
	public double getPvalueRounded() {
    	return Math.round(pvalue * 1000.0) / 1000.0;
	}

	@Override
	public String getPvalueRounded3SignificantDigits() {

		String rounded = RoundingUtilsMSLIBRARY.getInstance().roundThreeSignificantDigits( pvalue );
		return rounded;
	}
	
    public void setPvalue(double pvalue) {
    	this.pvalue = pvalue;
    }



	@Override
	public List<Integer> getPsmIdList() {
		if(this.psmIdList == null)
			return new ArrayList<Integer>(0);
		else
			return this.psmIdList;
	}

	public void setPsmIdList(List<Integer> psmIds) {
		this.psmIdList = psmIds;
	}

	@Override
	public List<PercolatorResult> getPsmList() {
		if(this.psmList == null)
			return new ArrayList<PercolatorResult>(0);
		else
			return this.psmList;
	}

	public void setPsmList(List<PercolatorResult> psms) {
		this.psmList = psms;
	}

	@Override
	public MsSearchResultPeptide getResultPeptide() {
        return resultPeptide;
    }

    public void setResultPeptide(MsSearchResultPeptide peptide) {
        this.resultPeptide = peptide;
    }

    /**
     * @param proteinMatchList the proteinMatchList to set
     */
    public void setProteinMatchList(List<MsSearchResultProtein> proteinMatchList) {
        this.proteinMatchList = proteinMatchList;
    }

    public List<MsSearchResultProtein> getProteinMatchList() {
        return proteinMatchList;
    }

}

