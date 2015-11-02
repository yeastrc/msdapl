/**
 * SearchFile.java
 * @author Vagisha Sharma
 * Apr 2, 2009
 * @version 1.0
 */
package org.yeastrc.experiment;

import java.sql.Date;

import org.yeastrc.ms.domain.search.MsRunSearch;
import org.yeastrc.ms.domain.search.SearchFileFormat;

/**
 * 
 */
public class SearchFile implements File{

    private final String filename;
    private final MsRunSearch runSearch;
    private int numResults;
    
    public int getNumResults() {
        return numResults;
    }

    public void setNumResults(int numResults) {
        this.numResults = numResults;
    }

    public SearchFile(MsRunSearch runSearch, String filename) {
        this.runSearch = runSearch;
        this.filename = filename;
    }
    
    public String getFileName() {
        return filename;
    }
    
    public int getId() {
        return runSearch.getId();
    }

    public int getRunId() {
        return runSearch.getId();
    }

    public int getSearchId() {
        return runSearch.getSearchId();
    }

    public Date getUploadDate() {
        return runSearch.getUploadDate();
    }

    public SearchFileFormat getSearchFileFormat() {
        return runSearch.getSearchFileFormat();
    }
}
