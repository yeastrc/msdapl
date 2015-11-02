/**
 * PeptideProphetResultDAO.java
 * @author Vagisha Sharma
 * Jul 28, 2009
 * @version 1.0
 */
package org.yeastrc.ms.dao.analysis.peptideProphet;

import java.util.List;

import org.yeastrc.ms.domain.analysis.peptideProphet.PeptideProphetResult;
import org.yeastrc.ms.domain.analysis.peptideProphet.PeptideProphetResultDataWId;
import org.yeastrc.ms.domain.analysis.peptideProphet.PeptideProphetResultFilterCriteria;
import org.yeastrc.ms.domain.search.ResultSortCriteria;

/**
 * 
 */
public interface PeptideProphetResultDAO {

    public abstract PeptideProphetResult loadForProphetResultId(int peptideProphetResultId);
    
    public abstract PeptideProphetResult loadForRunSearchAnalysis(int searchResultId, int runSearchAnalysisId);
    
    public abstract PeptideProphetResult loadForSearchAnalysis(int searchResultId, int searchAnalysisId);
    
    
    // ids for a runSearchAnalysis
    public abstract List<Integer> loadIdsForRunSearchAnalysis(int runSearchAnalysisId);
    
    public abstract List<Integer> loadIdsForRunSearchAnalysis(int runSearchAnalysisId, int limit, int offset);
    
    public abstract List<Integer> loadIdsForRunSearchAnalysis(int runSearchAnalysisId, 
                            PeptideProphetResultFilterCriteria filterCriteria, 
                            ResultSortCriteria sortCriteria);
    public abstract int numRunAnalysisResults(int runSearchAnalysisId);
    
    
    
    // ids for a searchAnalysis
    public abstract List<Integer> loadIdsForSearchAnalysis(int searchAnalysisId, 
            PeptideProphetResultFilterCriteria filterCriteria, 
            ResultSortCriteria sortCriteria);
    
    public abstract List<Integer> loadIdsForSearchAnalysisUniqPeptide(int searchAnalysisId, 
            PeptideProphetResultFilterCriteria filterCriteria, 
            ResultSortCriteria sortCriteria);
    
    public abstract List<Integer> loadIdsForRunSearchAnalysisScan(int runSearchAnalysisId, int scanId);
    
    public abstract List<Integer> loadIdsForAnalysis(int analysisId);
    
    public abstract List<Integer> loadIdsForAnalysis(int analysisId, int limit, int offset);
    
    public abstract int numAnalysisResults(int searchAnalysisId);
    
    
    
    public abstract void save(PeptideProphetResultDataWId data);
    
    public abstract void saveAllPeptideProphetResultData(List<PeptideProphetResultDataWId> dataList);
    
    
    public abstract void deleteResultsForRunSearchAnalysis(int id);
    
}
