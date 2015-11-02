package org.yeastrc.ms.domain.protinfer.idpicker;

import org.yeastrc.ms.domain.protinfer.GenericProteinferIon;
import org.yeastrc.ms.domain.protinfer.GenericProteinferPeptide;
import org.yeastrc.ms.domain.protinfer.ProteinferSpectrumMatch;

public abstract class GenericIdPickerPeptide <S extends ProteinferSpectrumMatch, T extends GenericProteinferIon<S>> 
                            extends GenericProteinferPeptide<S, T> {

    private int peptideGroupLabel = -1;
    
    public GenericIdPickerPeptide () {}
    
    public GenericIdPickerPeptide (int peptideGroupLabel) {
        this.peptideGroupLabel = peptideGroupLabel;
    }
    
    public int getPeptideGroupLabel() {
        return peptideGroupLabel;
    }

    public void setPeptideGroupLabel(int peptideGroupLabel) {
        this.peptideGroupLabel = peptideGroupLabel;
    }
}
