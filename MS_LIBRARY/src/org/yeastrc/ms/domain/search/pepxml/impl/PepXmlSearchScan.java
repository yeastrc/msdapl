/**
 * PepXmlSearchScan.java
 * @author Vagisha Sharma
 * Oct 5, 2009
 * @version 1.0
 */
package org.yeastrc.ms.domain.search.pepxml.impl;

import java.math.BigDecimal;

import org.yeastrc.ms.domain.analysis.peptideProphet.GenericPeptideProphetResultIn;
import org.yeastrc.ms.domain.search.MsSearchResultIn;
import org.yeastrc.ms.domain.search.pepxml.PepXmlSearchScanIn;

/**
 * 
 */
public abstract class PepXmlSearchScan <T extends GenericPeptideProphetResultIn<S>, S extends MsSearchResultIn> 
        implements PepXmlSearchScanIn<T, S> {

    private int scanNumber;
    private int charge;
    private BigDecimal precursorMass;
    private BigDecimal retentionTime;
    
    @Override
    public int getScanNumber() {
        return scanNumber;
    }

    @Override
    public int getCharge() {
        return charge;
    }

    @Override
    public BigDecimal getObservedMass() {
        return precursorMass;
    }

    @Override
    public BigDecimal getRetentionTime() {
        return retentionTime;
    }

    public void setScanNumber(int scanNumber) {
        this.scanNumber = scanNumber;
    }

    public void setCharge(int charge) {
        this.charge = charge;
    }

    public void setObservedMass(BigDecimal precursorMass) {
        this.precursorMass = precursorMass;
    }

    public void setRetentionTime(BigDecimal retentionTime) {
        this.retentionTime = retentionTime;
    }
}
