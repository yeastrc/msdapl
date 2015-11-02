/**
 * AnalysisDataUploadService.java
 * @author Vagisha Sharma
 * Mar 25, 2009
 * @version 1.0
 */
package org.yeastrc.ms.service;

import java.util.List;

import org.yeastrc.ms.domain.search.Program;
import org.yeastrc.ms.domain.search.SearchFileFormat;

/**
 * 
 */
public interface AnalysisDataUploadService extends UploadService {

    public void setSearchId(int searchId);
    
    public SearchFileFormat getAnalysisFileFormat();
    
    public void setSearchProgram(Program searchProgram);
    
    public void setSearchDataFileNames(List<String> searchDataFileNames);
    
    public void setComments(String comments);
    
    public List<Integer> getUploadedAnalysisIds();
    
    public void deleteAnalysis(int analysisId);
    
}
