/**
 * MsPeptideSearch.java
 * @author Vagisha Sharma
 * July 2, 2008
 * @version 1.0
 */
package org.yeastrc.ms.domain.search.impl;

import java.sql.Date;

import org.yeastrc.ms.domain.search.MsRunSearch;
import org.yeastrc.ms.domain.search.SearchFileFormat;
import org.yeastrc.ms.domain.search.Program;

public class RunSearchBean implements MsRunSearch {

    private int id; // unique id (database) for this search result
    private int searchId; // id of the search group this search belongs to
    private int runId; // MS run on which the search was performed
    
    private SearchFileFormat originalFileType;
    private Date searchDate;
    private int searchDuration = -1; // number of minutes for the search
    private Date uploadDate;
    
    
    /**
     * @return the id
     */
    public int getId() {
        return id;
    }
    /**
     * @param id the id to set
     */
    public void setId(int id) {
        this.id = id;
    }
    
    /**
     * @return the runId
     */
    public int getRunId() {
        return runId;
    }

    /**
     * @param runId the runId to set
     */
    public void setRunId(int runId) {
        this.runId = runId;
    }
    
    /**
     * @return the searchId
     */
    public int getSearchId() {
        return searchId;
    }
    
    /**
     * @param searchId the searchId to set
     */
    public void setSearchId(int searchId) {
        this.searchId = searchId;
    }
    
    @Override
    public Program getSearchProgram() {
        return Program.programForFileFormat(this.originalFileType);
    }
    
    public SearchFileFormat getSearchFileFormat() {
        return originalFileType;
    }
    /**
     * @param originalFileType the originalFileType to set
     */
    public void setSearchFileFormat(SearchFileFormat format) {
        this.originalFileType = format;
    }
    
    public Date getSearchDate() {
        return searchDate;
    }
    /**
     * @param searchDate the searchDate to set
     */
    public void setSearchDate(Date searchDate) {
        this.searchDate = searchDate;
    }
    
    public int getSearchDuration() {
        return searchDuration;
    }
    /**
     * @param searchDuration the searchDuration to set
     */
    public void setSearchDuration(int searchDuration) {
        this.searchDuration = searchDuration;
    }
    
    public Date getUploadDate() {
        return this.uploadDate;
    }
    
    public void setUploadDate(Date date) {
        this.uploadDate = date;
    }
}
