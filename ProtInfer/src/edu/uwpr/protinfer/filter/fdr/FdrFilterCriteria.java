package edu.uwpr.protinfer.filter.fdr;

import edu.uwpr.protinfer.filter.FilterCriteria;
import edu.uwpr.protinfer.filter.FilterException;

public class FdrFilterCriteria implements FilterCriteria <FdrFilterable> {

    private double thresholdFdr;
    
    public FdrFilterCriteria(double thresholdFdr) throws FilterException {
        if (thresholdFdr < 0.0 || thresholdFdr > 1.0)
            throw new FilterException("Invalid threshold FDR. FDR should be <= 0.0 and >=1.0");
        this.thresholdFdr = thresholdFdr;
    }
    
    @Override
    public boolean filter(FdrFilterable filterable) {
        if (filterable.getFdr() <= thresholdFdr) {
            filterable.setAccepted(true);
            return true;
        } 
        filterable.setAccepted(false);
        return false;
    }
}
