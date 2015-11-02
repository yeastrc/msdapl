/**
 * PepXmlSearchScanIn.java
 * @author Vagisha Sharma
 * Jul 14, 2009
 * @version 1.0
 */
package org.yeastrc.ms.domain.search.pepxml;

import java.math.BigDecimal;
import java.util.List;

import org.yeastrc.ms.domain.analysis.peptideProphet.GenericPeptideProphetResultIn;
import org.yeastrc.ms.domain.search.MsSearchResultIn;


/**
 * 
 */
public interface PepXmlSearchScanIn <T extends GenericPeptideProphetResultIn<S>, S extends MsSearchResultIn> 
        extends PepXmlSearchScanBase {

    public abstract int getScanNumber();
    
    public abstract void setScanNumber(int scanNumber);
    
    public abstract void addSearchResult(T result);
    
    public abstract List<T> getScanResults();
}

interface PepXmlSearchScanBase {
    
    public abstract BigDecimal getObservedMass();
    
    public abstract void setObservedMass(BigDecimal mass);
    
    public abstract int getCharge();
    
    public abstract void setCharge(int charge);
    
    public abstract BigDecimal getRetentionTime();
    
    public abstract void setRetentionTime(BigDecimal rt);
}
