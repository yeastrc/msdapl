/**
 * MsRunLocationWrap.java
 * @author Vagisha Sharma
 * Sep 7, 2008
 * @version 1.0
 */
package org.yeastrc.ms.dao.run.ibatis;

import org.yeastrc.ms.domain.run.MsRunLocationIn;

/**
 * This class is used only by ibatis
 */
public class MsRunLocationWrap {

    private int runId;
    private String serverDirectory;
    
    public MsRunLocationWrap (MsRunLocationIn runLoc, int runId) {
        this.serverDirectory = runLoc.getServerDirectory();
        this.runId = runId;
    }
    
    public MsRunLocationWrap(String serverDirectory, int runId) {
        this.serverDirectory = serverDirectory;
        this.runId = runId;
    }
    
    public int getRunId() {
        return runId;
    }

    public String getServerDirectory() {
        return serverDirectory;
    }
}
