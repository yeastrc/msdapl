/**
 * SQTSpectrumData.java
 * @author Vagisha Sharma
 * Jul 5, 2008
 * @version 1.0
 */
package org.yeastrc.ms.domain.search.sqtfile.impl;

import java.math.BigDecimal;

import org.yeastrc.ms.domain.search.sqtfile.SQTSearchScan;

/**
 * 
 */
public class SQTSearchScanBean implements SQTSearchScan {

    private int scanId;
    private int charge;
    private int runSearchId;
    private int processTime;
    private String serverName;
    private Double totalIntensity;
    private BigDecimal observedMass;
    private BigDecimal lowestSp;
    private int sequenceMatches = -1;
    
    /**
     * @return the scanId
     */
    public int getScanId() {
        return scanId;
    }
    /**
     * @param scanId the scanId to set
     */
    public void setScanId(int scanId) {
        this.scanId = scanId;
    }
    
    /**
     * @return the charge
     */
    public int getCharge() {
        return charge;
    }
    /**
     * @param charge the charge to set
     */
    public void setCharge(int charge) {
        this.charge = charge;
    }
    /**
     * @return the runSearchId
     */
    public int getRunSearchId() {
        return runSearchId;
    }
    /**
     * @param runSearchId the searchId to set
     */
    public void setRunSearchId(int runSearchId) {
        this.runSearchId = runSearchId;
    }
    /**
     * @return the processTime
     */
    public int getProcessTime() {
        return processTime;
    }
    /**
     * @param processTime the processTime to set
     */
    public void setProcessTime(int processTime) {
        this.processTime = processTime;
    }
    /**
     * @return the serverName
     */
    public String getServerName() {
        return serverName;
    }
    /**
     * @param serverName the serverName to set
     */
    public void setServerName(String serverName) {
        this.serverName = serverName;
    }
    /**
     * @return the totalIntensity
     */
    public Double getTotalIntensity() {
        return totalIntensity;
    }
    /**
     * @param totalIntensity the totalIntensity to set
     */
    public void setTotalIntensity(Double totalIntensity) {
        this.totalIntensity = totalIntensity;
    }
    
    @Override
    public BigDecimal getObservedMass() {
        return observedMass;
    }
    
    public void setObservedMass(BigDecimal mass) {
        this.observedMass = mass;
    }
    
    /**
     * @return the lowestSp
     */
    public BigDecimal getLowestSp() {
        return lowestSp;
    }
    /**
     * @param lowestSp the lowestSp to set
     */
    public void setLowestSp(BigDecimal lowestSp) {
        this.lowestSp = lowestSp;
    }
    
    @Override
    public int getSequenceMatches() {
        return sequenceMatches;
    }
    
    public void setSequenceMatches(int sequenceMatches) {
        this.sequenceMatches = sequenceMatches;
    }
}
