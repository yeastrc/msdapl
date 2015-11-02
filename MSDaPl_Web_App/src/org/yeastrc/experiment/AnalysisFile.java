/**
 * AnalysisFile.java
 * @author Vagisha Sharma
 * Apr 2, 2009
 * @version 1.0
 */
package org.yeastrc.experiment;

import org.yeastrc.ms.domain.analysis.MsRunSearchAnalysis;
import org.yeastrc.ms.domain.search.SearchFileFormat;

/**
 * 
 */
public class AnalysisFile implements MsRunSearchAnalysis, File {

    private final String filename;
    private final MsRunSearchAnalysis runSearchAnalysis;
    private int numResults;
    
    public int getNumResults() {
        return numResults;
    }

    public void setNumResults(int numResults) {
        this.numResults = numResults;
    }

    public AnalysisFile(MsRunSearchAnalysis runSearchAnalysis, String filename) {
        this.runSearchAnalysis = runSearchAnalysis;
        this.filename = filename;
    }
    
    public String getFileName() {
        return filename;
    }
    
    public int getId() {
        return runSearchAnalysis.getId();
    }

    public int getRunId() {
        return runSearchAnalysis.getId();
    }

    @Override
    public SearchFileFormat getAnalysisFileFormat() {
        return runSearchAnalysis.getAnalysisFileFormat();
    }

    @Override
    public int getAnalysisId() {
        return runSearchAnalysis.getAnalysisId();
    }

    @Override
    public int getRunSearchId() {
        return runSearchAnalysis.getRunSearchId();
    }
}
