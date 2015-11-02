/**
 * MsRunSearchAnalysis.java
 * @author Vagisha Sharma
 * Dec 29, 2008
 * @version 1.0
 */
package org.yeastrc.ms.domain.analysis;

import org.yeastrc.ms.domain.search.SearchFileFormat;

/**
 * 
 */
public interface MsRunSearchAnalysis {

    /**
     * @return the database id
     */
    public abstract int getId();
    
    /**
     * database id of the search analysis
     * @return
     */
    public abstract int getAnalysisId();
    
    /**
     * @return the database id of the run search
     */
    public abstract int getRunSearchId();
    
    
    /**
     * @return the analysisProgramName
     */
    public abstract SearchFileFormat getAnalysisFileFormat();

}
