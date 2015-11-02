/**
 * MsSearchWrap.java
 * @author Vagisha Sharma
 * Sep 18, 2008
 * @version 1.0
 */
package org.yeastrc.ms.dao.search.ibatis;

import java.sql.Date;

import org.yeastrc.ms.domain.search.MsSearchIn;
import org.yeastrc.ms.domain.search.Program;

/**
 * NOTE: This class is used internally by MsSearchDAOImpl.
 */
public class MsSearchWrap {

    private MsSearchIn search;
    private int experimentId;
    
    public MsSearchWrap(MsSearchIn search, int experimentId) {
        this.search = search;
        this.experimentId = experimentId;
    }
    
    public int getExperimentId() {
        return experimentId;
    }

    public Date getSearchDate() {
        return search.getSearchDate();
    }

    public Program getSearchProgram() {
        return search.getSearchProgram();
    }

    public String getSearchProgramVersion() {
        return search.getSearchProgramVersion();
    }

    public String getServerDirectory() {
        return search.getServerDirectory();
    }
}
