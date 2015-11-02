/**
 * MsSearchBase.java
 * @author Vagisha Sharma
 * Dec 10, 2008
 * @version 1.0
 */
package org.yeastrc.ms.domain.search;

import java.sql.Date;

/**
 * 
 */
public interface MsSearchBase {

    /**
     * @return the serverDirectory
     */
    public abstract String getServerDirectory();

    /**
     * @return the searchDate
     */
    public abstract Date getSearchDate();
    
    /**
     * @return the analysisProgramName
     */
    public abstract Program getSearchProgram();

    /**
     * @return the analysisProgramVersion
     */
    public abstract String getSearchProgramVersion();
    
}
