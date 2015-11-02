/**
 * MsFile.java
 * @author Vagisha Sharma
 * Apr 4, 2009
 * @version 1.0
 */
package org.yeastrc.experiment;

import org.yeastrc.ms.domain.run.MsRun;
import org.yeastrc.ms.domain.run.RunFileFormat;

/**
 * 
 */
public class MsFile implements File {
    
    private final String filename;
    private final int id;
    private final RunFileFormat format;
    private final int scanCount;
    
    public MsFile(MsRun run, int scanCount) {
        
        this.id = run.getId();
        this.filename = run.getFileName();
        this.format = run.getRunFileFormat();
        this.scanCount = scanCount;
    }

    public int getId() {
        return id;
    }

    public String getFileName() {
        return filename;
    }

    public RunFileFormat getRunFileFormat() {
        return format;
    }

    public int getScanCount() {
        return scanCount;
    }
}
