package org.yeastrc.ms.domain.search.sqtfile;

import java.math.BigDecimal;
import java.util.List;

import org.yeastrc.ms.domain.search.MsSearchResultIn;

public interface SQTSearchScanIn <T extends MsSearchResultIn> extends SQTSearchScanBase {

    public abstract int getScanNumber();
    
    public abstract List<T> getScanResults();
}

interface SQTSearchScanBase {
    /**
     * @return the charge
     */
    public abstract int getCharge();
    
    /**
     * @return the observed M+H
     */
    public abstract BigDecimal getObservedMass();

    /**
     * @return the processTime
     */
    public abstract int getProcessTime();

    /**
     * @return the serverName
     */
    public abstract String getServerName();

    /**
     * @return the totalIntensity
     */
    public abstract Double getTotalIntensity();

    /**
     * @return the lowestSp
     */
    public abstract BigDecimal getLowestSp();
    
    /**
     * Returns the number of sequence matching the precursor ion.
     * @return
     */
    public abstract int getSequenceMatches();
}