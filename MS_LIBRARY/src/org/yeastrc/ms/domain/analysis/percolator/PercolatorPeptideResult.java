/**
 * PercolatorPeptideResult.java
 * @author Vagisha Sharma
 * Sep 17, 2010
 */
package org.yeastrc.ms.domain.analysis.percolator;

import java.util.List;

import org.yeastrc.ms.domain.search.MsSearchResultPeptide;
import org.yeastrc.ms.domain.search.MsSearchResultProtein;


/**
 *
 */
public interface PercolatorPeptideResult {

	public abstract int getId();

    public abstract int getSearchAnalysisId();


    /**
     * @return the peptideResult
     */
    public abstract MsSearchResultPeptide getResultPeptide();

    /**
     * @return the qvalue
     */
    public abstract double getQvalue();

    public abstract double getQvalueRounded();

    public abstract String getQvalueRounded3SignificantDigits();

    /**
     * @return the posterior error probability or -1.0 if there was no posterior probability
     * for this result
     */
    public abstract double getPosteriorErrorProbability();

    public abstract double getPosteriorErrorProbabilityRounded();

    public abstract String getPosteriorErrorProbabilityRounded3SignificantDigits();

    /**
     * @return the percolator discriminant score or null if there was no discriminant score
     */
    public abstract Double getDiscriminantScore();

    public abstract Double getDiscriminantScoreRounded();

    public abstract String getDiscriminantScoreRounded3SignificantDigits();

    /**
     * @return the pvalue
     */
    public abstract double getPvalue();

    public abstract double getPvalueRounded();

    public abstract String getPvalueRounded3SignificantDigits();

    public abstract List<PercolatorResult> getPsmList();

    public abstract List<Integer> getPsmIdList();

    /**
     * @return the proteinMatchList
     */
    public abstract List<MsSearchResultProtein> getProteinMatchList();
}
