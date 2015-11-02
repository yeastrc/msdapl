/**
 * MsSearchDatabaseDbImpl.java
 * @author Vagisha Sharma
 * Jul 3, 2008
 * @version 1.0
 */
package org.yeastrc.ms.domain.search.impl;

import java.io.File;

import org.yeastrc.ms.domain.search.MsSearchDatabase;
import org.yeastrc.ms.domain.search.MsSearchDatabaseIn;

/**
 * 
 */
public class MsSearchDatabaseWrap implements MsSearchDatabase {

    private int sequenceDatabaseId;
    private MsSearchDatabaseIn database;
    
    public MsSearchDatabaseWrap(MsSearchDatabaseIn database, int sequenceDatabaseId) {
        this.database = database;
        this.sequenceDatabaseId = sequenceDatabaseId;
    }
    
    public int getId() {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public String getDatabaseFileName() {
        String serverPath = database.getServerPath();
        if (serverPath != null)
            return new File(serverPath).getName();
        return null;
    }
    @Override
    public int getSequenceDatabaseId() {
        return sequenceDatabaseId;
    }
    
    @Override
    public String getServerAddress() {
        return database.getServerAddress();
    }
    
    @Override
    public String getServerPath() {
        return database.getServerPath();
    }
    
}
