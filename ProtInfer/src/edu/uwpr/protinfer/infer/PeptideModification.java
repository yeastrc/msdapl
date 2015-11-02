package edu.uwpr.protinfer.infer;

import java.math.BigDecimal;

public class PeptideModification {

    private int modifiedIndex;
    private BigDecimal massShift;
    
    public PeptideModification(int modifiedIndex, BigDecimal massShift) {
        this.modifiedIndex = modifiedIndex;
        this.massShift = massShift;
    }

    public int getModifiedIndex() {
        return modifiedIndex;
    }

    public BigDecimal getMassShift() {
        return massShift;
    }
}
