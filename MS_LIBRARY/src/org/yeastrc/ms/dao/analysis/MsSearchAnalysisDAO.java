package org.yeastrc.ms.dao.analysis;

import java.util.List;

import org.yeastrc.ms.domain.analysis.MsSearchAnalysis;
import org.yeastrc.ms.domain.search.Program;

public interface MsSearchAnalysisDAO {

    public abstract int save(MsSearchAnalysis analysis);
    
    public abstract MsSearchAnalysis load(int analysisId);
    
    public abstract List<Integer> getAnalysisIdsForSearch(int searchId);
    
    public abstract List<Integer> getSearchIdsForAnalysis(int analysisId);
    
    public MsSearchAnalysis loadAnalysisForFileName(String fileName, int searchId);
    
    public abstract int updateAnalysisProgramVersion(int analysisId, String versionStr);
    
    public abstract int updateAnalysisProgram(int analysisId, Program program);
    
    public abstract void updateComments(int experimentId, String comments);
    
    public void delete(int analysisId);
    
}
