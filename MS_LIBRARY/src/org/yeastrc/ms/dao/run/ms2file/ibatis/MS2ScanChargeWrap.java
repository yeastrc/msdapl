/**
 * Ms2FileScanCharge.java
 * @author Vagisha Sharma
 * Jun 16, 2008
 * @version 1.0
 */
package org.yeastrc.ms.dao.run.ms2file.ibatis;

import java.math.BigDecimal;

import org.yeastrc.ms.domain.run.ms2file.MS2ScanCharge;


/**
 * This class is used only by ibatis
 */
public class MS2ScanChargeWrap {

    
    private int scanId;     // the database id of the scan to which this charge corresponds
    private MS2ScanCharge scanCharge;
    
    public MS2ScanChargeWrap(MS2ScanCharge scanCharge, int scanId) {
        this.scanCharge = scanCharge;
        this.scanId = scanId;
    }
    
    /**
     * @return the scanId
     */
    public int getScanId() {
        return scanId;
    }
   
    /**
     * @return the charge
     */
    public int getCharge() {
        return scanCharge.getCharge();
    }
    
    /**
     * @return the mass
     */
    public BigDecimal getMass() {
        return scanCharge.getMass();
    }
}
