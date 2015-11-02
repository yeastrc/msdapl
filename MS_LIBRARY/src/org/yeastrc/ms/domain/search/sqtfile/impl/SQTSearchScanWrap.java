/**
 * SQTSearchScanImpl.java
 * @author Vagisha Sharma
 * Sep 7, 2008
 * @version 1.0
 */
package org.yeastrc.ms.domain.search.sqtfile.impl;

import java.math.BigDecimal;

import org.yeastrc.ms.domain.search.sqtfile.SQTSearchScan;
import org.yeastrc.ms.domain.search.sqtfile.SQTSearchScanIn;

/**
 * 
 */
public class SQTSearchScanWrap implements SQTSearchScan {

    private int scanId;
    private int runSearchId;
    private SQTSearchScanIn scan;
    
    public SQTSearchScanWrap(SQTSearchScanIn scan, int runSearchId, int scanId) {
        this.scan = scan;
        this.runSearchId = runSearchId;
        this.scanId = scanId;
    }
    
    @Override
    public int getRunSearchId() {
        return runSearchId;
    }

    @Override
    public int getScanId() {
        return scanId;
    }

    @Override
    public int getCharge() {
        return scan.getCharge();
    }

    @Override
    public BigDecimal getLowestSp() {
        return scan.getLowestSp();
    }

    @Override
    public BigDecimal getObservedMass() {
        return scan.getObservedMass();
    }

    @Override
    public int getProcessTime() {
        return scan.getProcessTime();
    }

    @Override
    public int getSequenceMatches() {
        return scan.getSequenceMatches();
    }

    @Override
    public String getServerName() {
        return scan.getServerName();
    }

    @Override
    public Double getTotalIntensity() {
        return scan.getTotalIntensity();
    }
}
