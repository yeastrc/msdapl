/**
 * MsSearchDatabaseDb.java
 * @author Vagisha Sharma
 * Jul 11, 2008
 * @version 1.0
 */
package org.yeastrc.ms.domain.search;


/**
 * 
 */
public interface MsSearchDatabase extends MsSearchDatabaseIn {

    /**
     * @return database id of the search database
     */
    public abstract int getId();
    
    /**
     * @return id of the nrseq protein database
     */
    public abstract int getSequenceDatabaseId();
    
}
