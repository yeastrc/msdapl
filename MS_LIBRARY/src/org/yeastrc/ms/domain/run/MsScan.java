/**
 * MsScanDb.java
 * @author Vagisha Sharma
 * Jul 11, 2008
 * @version 1.0
 */
package org.yeastrc.ms.domain.run;

import java.io.IOException;
import java.util.List;



/**
 * 
 */
public interface MsScan extends MsScanBase {

    /**
     * @return database id of the run this scan belongs to
     */
    public abstract int getRunId();
    
    /**
     * @return database if of the scan
     */
    public abstract int getId();
    
    /**
     * @return database id of the precursor scan if it exists, 0 otherwise.
     * Return value of this method for MS1 scans will always be 0.
     */
    public abstract int getPrecursorScanId();
    

    public abstract List<String[]> getPeaksString() throws IOException;
    
    public abstract List<Peak> getPeaks() throws IOException;
    
    public abstract PeakStorageType getPeakStorageType();
    
}
