package org.yeastrc.ms.domain.protinfer.idpicker;

import org.yeastrc.ms.domain.protinfer.GenericProteinferPeptide;
import org.yeastrc.ms.domain.protinfer.ProteinferIon;
import org.yeastrc.ms.domain.protinfer.ProteinferSpectrumMatch;

public class IdPickerPeptideBase extends GenericIdPickerPeptide<ProteinferSpectrumMatch, ProteinferIon> {

    @Override
    protected GenericProteinferPeptide<ProteinferSpectrumMatch, ProteinferIon> newPeptide() {
        return new IdPickerPeptideBase();
    }

}
