/**
 * PercolatorPeptideResultDAO.java
 * @author Vagisha Sharma
 * Sep 17, 2010
 */
package org.yeastrc.ms.dao.analysis.percolator;

import java.util.List;

import org.yeastrc.ms.domain.analysis.percolator.PercolatorPeptideResult;
import org.yeastrc.ms.domain.analysis.percolator.PercolatorResult;
import org.yeastrc.ms.domain.analysis.percolator.PercolatorResultFilterCriteria;
import org.yeastrc.ms.domain.search.ResultSortCriteria;

/**
 * 
 */
public interface PercolatorPeptideResultDAO {

	public abstract PercolatorPeptideResult load(int id);
	
	public abstract PercolatorPeptideResult loadForPercolatorResult(int percolatorResultId);
    
    public abstract List<Integer> loadIdsForAnalysis(int searchAnalysisId);
    
    public abstract List<Integer> loadIdsForAnalysis(int searchAnalyisId, int limit, int offset);
    
    public abstract List<PercolatorResult> loadPercolatorPsms(int runSearchAnalysisId, 
            Double qvalue, Double pep, Double discriminantScore);
    
    public abstract List<PercolatorResult> loadPercolatorPsms(int runSearchAnalysisId, 
            Double peptideQvalue, Double peptidePep, Double peptideDiscriminantScore,
            Double psmQvalue, Double psmPep, Double psmDiscriminantScore);
    
    public abstract void saveAllPercolatorPeptideResults(List<PercolatorPeptideResult> peptideResultList);
    
    public abstract int peptideCountForAnalysis(int searchAnalysisId);
    
    public abstract List<Integer> loadIdsForSearchAnalysis(int searchAnalysisId, 
            PercolatorResultFilterCriteria filterCriteria, 
            ResultSortCriteria sortCriteria);
}
