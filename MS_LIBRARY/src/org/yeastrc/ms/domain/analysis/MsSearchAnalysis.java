/**
 * PercolatorSearch.java
 * @author Vagisha Sharma
 * Dec 10, 2008
 * @version 1.0
 */
package org.yeastrc.ms.domain.analysis;

import java.sql.Date;

import org.yeastrc.ms.domain.search.Program;

/**
 * 
 */
public interface MsSearchAnalysis {

    /**
     * @return the database id for this Percolator Analysis
     */
    public abstract int getId();
    
    public abstract void setId(int analysisId);
    
    /**
     * database id of the search on which Percolator was run.
     * @return
     */
//    public abstract int getSearchId();
    
    /**
     * @return the date this search was uploaded
     */
    public abstract Date getUploadDate();
    
    
    /**
     * @return the analysisProgramName
     */
    public abstract Program getAnalysisProgram();

    /**
     * @return the analysisProgramVersion
     */
    public abstract String getAnalysisProgramVersion();
    
    public abstract String getComments();
    
    public abstract void setComments(String comments);
    
    public abstract String getFilename();
    
}
