/**
 * PeptideProphetResultIn.java
 * @author Vagisha Sharma
 * Jun 24, 2009
 * @version 1.0
 */
package org.yeastrc.ms.domain.analysis.peptideProphet;

import org.yeastrc.ms.domain.search.MsSearchResultIn;

/**
 * 
 */
public interface PeptideProphetResultIn extends MsSearchResultIn {

    public abstract double getProbability();
    public abstract double getProbabilityRounded();
    
    public abstract double getfVal();
    public abstract double getfValRounded();
    
    public abstract int getNumEnzymaticTermini();
    
    public abstract int getNumMissedCleavages();
    
    public abstract double getMassDifference();
    public abstract double getMassDifferenceRounded();
    
    // TODO Not sure how to handle this PeptideProphet result
//    public abstract String getAllNttProb();
    
    public double getProbabilityNet_0();
    public double getProbabilityNet_1();
    public double getProbabilityNet_2();
}
