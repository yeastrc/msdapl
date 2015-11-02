package org.yeastrc.ms.domain.search;

import java.sql.Date;

public interface MsRunSearchIn {
    
    /**
     * @return the originalFileType
     */
    public abstract SearchFileFormat getSearchFileFormat();

    /**
     * @return searchProgram
     */
    public abstract Program getSearchProgram();
    /**
     * @return the searchDate
     */
    public abstract Date getSearchDate();

    /**
     * @return the time taken for the search (in minutes)
     */
    public abstract int getSearchDuration();
    
}
