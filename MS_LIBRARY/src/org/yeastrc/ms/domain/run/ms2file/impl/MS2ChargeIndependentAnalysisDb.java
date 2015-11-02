/**
 * Ms2FileChargeIndependentAnalysisDbImpl.java
 * @author Vagisha Sharma
 * Jun 16, 2008
 * @version 1.0
 */
package org.yeastrc.ms.domain.run.ms2file.impl;

import org.yeastrc.ms.domain.run.ms2file.MS2ChargeIndependentAnalysis;
import org.yeastrc.ms.domain.run.ms2file.MS2NameValuePair;


/**
 * Represents an "I" line in the MS2 file.  Charge independent analysis for a particular scan.
 * There can be multiple "I" lines in the MS2 file for a single scan.
 */
public class MS2ChargeIndependentAnalysisDb implements MS2ChargeIndependentAnalysis {

    private int scanId;         // id (database) of the scan that was analyzed
    private MS2NameValuePair header;
    
    public MS2ChargeIndependentAnalysisDb(MS2NameValuePair header, int scanId) {
        this.scanId = scanId;
        this.header = header;
    }
    
    public int getScanId() {
        return scanId;
    }
    
    @Override
    public String getName() {
        return header.getName();
    }

    @Override
    public String getValue() {
        return header.getValue();
    }
}
