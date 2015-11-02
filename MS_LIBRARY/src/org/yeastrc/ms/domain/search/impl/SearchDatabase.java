/**
 * SearchDatabase.java
 * @author Vagisha Sharma
 * Sep 6, 2008
 * @version 1.0
 */
package org.yeastrc.ms.domain.search.impl;

import java.io.File;

import org.yeastrc.ms.domain.search.MsSearchDatabaseIn;

/**
 * 
 */
public class SearchDatabase implements MsSearchDatabaseIn {

    private String serverAddress;
    private String serverPath;
   
    public String getServerAddress() {
        return serverAddress;
    }
    /**
     * @param serverAddress the serverAddress to set
     */
    public void setServerAddress(String serverAddress) {
        this.serverAddress = serverAddress;
    }
    
    public String getServerPath() {
        return serverPath;
    }
    /**
     * @param serverPath the serverPath to set
     */
    public void setServerPath(String serverPath) {
        this.serverPath = serverPath;
    }
    
    @Override
    public String getDatabaseFileName() {
        if (serverPath != null)
            return new File(serverPath).getName();
        return null;
    }
}
