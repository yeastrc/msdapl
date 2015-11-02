package org.yeastrc.ms.domain.protinfer.idpicker;

import org.yeastrc.ms.domain.protinfer.ProteinferSpectrumMatch;

public class IdPickerSpectrumMatch extends ProteinferSpectrumMatch {

    private double fdr = -1.0;
    
    public IdPickerSpectrumMatch() {
        super();
    }
    
    public IdPickerSpectrumMatch(int pinferIonId, int resultId, double fdr) {
        super(pinferIonId, resultId);
        this.fdr = fdr;
    }
    
    public double getFdr() {
        return fdr;
    }
    
    public double getFdrRounded() {
        return Math.round(fdr * 1000.0) / 1000.0;
    }
    
    public void setFdr(double fdr) {
        this.fdr = fdr;
    }
}
