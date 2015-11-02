/**
 * MsRunSearchImpl.java
 * @author Vagisha Sharma
 * Sep 7, 2008
 * @version 1.0
 */
package org.yeastrc.ms.domain.search.impl;

import java.sql.Date;

import org.yeastrc.ms.domain.search.MsRunSearch;
import org.yeastrc.ms.domain.search.MsRunSearchIn;
import org.yeastrc.ms.domain.search.SearchFileFormat;
import org.yeastrc.ms.domain.search.Program;

/**
 * 
 */
public class MsRunSearchWrap implements MsRunSearch {

    private int searchId;
    private int runId;
    private MsRunSearchIn runSearch;
    
    public MsRunSearchWrap(MsRunSearchIn runSearch, int searchId, int runId) {
        this.searchId = searchId;
        this.runId = runId;
        this.runSearch = runSearch;
    }
    @Override
    public int getId() {
        throw new UnsupportedOperationException();
    }

    @Override
    public int getRunId() {
        return runId;
    }

    @Override
    public int getSearchId() {
        return searchId;
    }

    @Override
    public Date getUploadDate() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Date getSearchDate() {
        return runSearch.getSearchDate();
    }

    @Override
    public int getSearchDuration() {
        return runSearch.getSearchDuration();
    }

    @Override
    public SearchFileFormat getSearchFileFormat() {
        return runSearch.getSearchFileFormat();
    }

    @Override
    public Program getSearchProgram() {
        return runSearch.getSearchProgram();
    }
}
