/**
 * MS2ScanDb.java
 * @author Vagisha Sharma
 * Jul 11, 2008
 * @version 1.0
 */
package org.yeastrc.ms.domain.run.ms2file;

import java.util.List;

import org.yeastrc.ms.domain.run.MsScan;

/**
 * 
 */
public interface MS2Scan extends MsScan {

    /**
     * @return the scanChargeList
     */
    public abstract List<MS2ScanCharge> getScanChargeList();

    /**
     * @return list of charge independent analysis for the scan.
     */
    public abstract List<MS2NameValuePair> getChargeIndependentAnalysisList();
    
    /**
     * Returns the precursor ion area calculated by Bullseye. 
     * Returns -1.0 if no calculated area was found
     * @return
     */
    public abstract double getBullsEyeArea();
}
