package edu.uwpr.protinfer.idpicker;

import edu.uwpr.protinfer.filter.fdr.FdrCandidate;

public interface FdrCandidateHasCharge extends FdrCandidate {

    public abstract int getCharge();
}
