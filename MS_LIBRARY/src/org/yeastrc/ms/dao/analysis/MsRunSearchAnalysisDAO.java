/**
 * MsRunSearchAnalysisDAO.java
 * @author Vagisha Sharma
 * Dec 29, 2008
 * @version 1.0
 */
package org.yeastrc.ms.dao.analysis;

import java.util.List;

import org.yeastrc.ms.domain.analysis.MsRunSearchAnalysis;

/**
 * 
 */
public interface MsRunSearchAnalysisDAO {

    public abstract int save(MsRunSearchAnalysis runSearchAnalysis);
    
    public abstract MsRunSearchAnalysis load(int runSearchAnalysisId);
    
    public abstract MsRunSearchAnalysis load(int analysisId, int runSearchId);
    
    public abstract List<Integer> getRunSearchAnalysisIdsForAnalysis(int analysisId);
    
    /**
     * Returns the base name of the original file for this analysis
     * @param runSearchAnalysisId
     * @return
     */
    public abstract String loadFilenameForRunSearchAnalysis(int runSearchAnalysisId);
}
