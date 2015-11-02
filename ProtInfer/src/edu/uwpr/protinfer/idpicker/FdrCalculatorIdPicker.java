package edu.uwpr.protinfer.idpicker;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.log4j.Logger;

import edu.uwpr.protinfer.filter.fdr.FdrCalculator;
import edu.uwpr.protinfer.filter.fdr.FdrCalculatorException;

public class FdrCalculatorIdPicker <T extends FdrCandidateHasCharge> extends FdrCalculator<T> {

    private double decoyRatio = 1.0;
    private boolean separateChargeStates = false;
    private boolean useIdpickerFdr = true;
    
    private static final Logger log = Logger.getLogger(FdrCalculatorIdPicker.class);

    public void setDecoyRatio(double decoyRatio) throws FdrCalculatorException {
        if (decoyRatio <= 0.0 || decoyRatio > 1.0)
            throw new FdrCalculatorException("Invalid threshold FDR. Decoy ratio should be < 0.0 and >=1.0");
        this.decoyRatio = decoyRatio;
    }
    
    public void separateChargeStates(boolean separate) {
        separateChargeStates = separate;
    }
    
    public void setUseIdPickerFdr(boolean useIdPickerFdr) {
        this.useIdpickerFdr = useIdPickerFdr;
    }

    @Override
    protected double calculateFdr(int targetCount, int decoyCount) {
//        double fdr = (double)(2 * decoyCount) / (double)(decoyCount + targetCount);
        if(useIdpickerFdr)
            return Math.min(1.0, (double)(decoyCount*(1+decoyRatio)) / (double)(decoyCount + targetCount));
        else 
            return Math.min(1.0, (double)(decoyCount) / (double)(targetCount));
    }

    @Override
    protected boolean considerCandidate(T candidate) {
        return !(candidate.isDecoyMatch() && candidate.isTargetMatch());
    }
    
    public void calculateFdr(List<T> candidates, Comparator<T> comparator) {
        if (!separateChargeStates)
            super.calculateFdr(candidates, comparator);
        else {
            
            // sort by charge
            Collections.sort(candidates, new Comparator<FdrCandidateHasCharge>() {
                @Override
                public int compare(FdrCandidateHasCharge o1, FdrCandidateHasCharge o2) {
                    return Integer.valueOf(o1.getCharge()).compareTo(o2.getCharge());
                }});
            
            List<T> candidatesWithCharge = new ArrayList<T>();
            int currentChg = 1;
            for (T candidate: candidates) {
                if (candidate.getCharge() != currentChg) {
                    if (candidatesWithCharge.size() > 0) {
                        log.info("Calculating FDR for candidates with charge: +"+currentChg);
                        super.calculateFdr(candidatesWithCharge, comparator);
                    }
                    
                    candidatesWithCharge.clear();
                    currentChg = candidate.getCharge();
                }
                candidatesWithCharge.add(candidate);
            }
            if (candidatesWithCharge.size() > 0) {
                log.info("Calculating FDR for candidates with charge: +"+currentChg);
                super.calculateFdr(candidatesWithCharge, comparator);
            }
        }
    }
}
