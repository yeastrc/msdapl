/**
 * MS2Scan.java
 * @author Vagisha Sharma
 * Jul 11, 2008
 * @version 1.0
 */
package org.yeastrc.ms.domain.run.ms2file;

import java.util.List;

import org.yeastrc.ms.domain.run.MsScanIn;

public interface MS2ScanIn extends MsScanIn {

    
    /**
     * @return the scanChargeList
     */
    public abstract List<MS2ScanCharge> getScanChargeList();

    /**
     * @return list of charge independent analysis for the scan.
     */
    public abstract List<MS2NameValuePair> getChargeIndependentAnalysisList();
}