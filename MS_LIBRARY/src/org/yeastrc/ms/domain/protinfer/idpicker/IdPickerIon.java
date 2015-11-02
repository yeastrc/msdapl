package org.yeastrc.ms.domain.protinfer.idpicker;

import org.yeastrc.ms.domain.protinfer.GenericProteinferIon;

public class IdPickerIon extends GenericProteinferIon<IdPickerSpectrumMatch> {

    public double getBestFdr() {
        double best = this.getBestSpectrumMatch().getFdr();
        return (Math.round(best*100.0) / 100.0);
    }
}
