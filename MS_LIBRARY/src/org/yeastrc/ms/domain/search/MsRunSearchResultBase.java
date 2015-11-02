package org.yeastrc.ms.domain.search;

import java.math.BigDecimal;

public interface MsRunSearchResultBase {
    
    /**
     * @return the peptideResult
     */
    public abstract MsSearchResultPeptide getResultPeptide();
    
    public abstract void setResultPeptide(MsSearchResultPeptide resultPeptide);
    
    /**
     * @return the charge
     */
    public abstract int getCharge();
    
    public abstract void setCharge(int charge);
    
    /**
     * @return the observedMass
     */
    public abstract BigDecimal getObservedMass();
    
    public abstract void setObservedMass(BigDecimal mass);

    /**
     * @return the validationStatus
     */
    public abstract ValidationStatus getValidationStatus();
    
}
