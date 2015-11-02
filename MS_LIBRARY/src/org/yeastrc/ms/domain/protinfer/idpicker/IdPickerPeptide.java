package org.yeastrc.ms.domain.protinfer.idpicker;

import org.yeastrc.ms.domain.protinfer.GenericProteinferPeptide;


public class IdPickerPeptide extends GenericIdPickerPeptide<IdPickerSpectrumMatch, IdPickerIon> {

    
    public IdPickerPeptide() {
        super();
    }
    
    public IdPickerPeptide(int peptideGroupId) {
        super(peptideGroupId);
    }
    
    public double getBestFdr() {
        double best = Float.MAX_VALUE;
        for(IdPickerIon ion: this.getIonList()) {
            best = ion.getBestFdr();
        }
        return (Math.round(best*100.0) / 100.0);
    }
    
    public IdPickerSpectrumMatch getBestSpectrumMatch() {
        IdPickerSpectrumMatch bestPsm = null;
        for(IdPickerIon ion: this.getIonList()) {
            if(bestPsm == null) {
                bestPsm = ion.getBestSpectrumMatch();
            }
            else {
                bestPsm = bestPsm.getFdr() < ion.getBestFdr() ? bestPsm : ion.getBestSpectrumMatch();
            }
        }
        return bestPsm;
    }

    @Override
    protected GenericProteinferPeptide<IdPickerSpectrumMatch, IdPickerIon> newPeptide() {
        return new IdPickerPeptide();
    }
}
