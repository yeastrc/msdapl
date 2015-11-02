/**
 * PeptideProphetResultData.java
 * @author Vagisha Sharma
 * Oct 7, 2009
 * @version 1.0
 */
package org.yeastrc.ms.domain.analysis.peptideProphet;

/**
 * 
 */
public interface PeptideProphetResultDataIn {

    public abstract double getProbability();
    
    public abstract double getfVal();
    
    public abstract int getNumEnzymaticTermini();
    
    public abstract int getNumMissedCleavages();
    
    public abstract double getMassDifference();
    
    // TODO Not sure how to handle this PeptideProphet result
//    public abstract String getAllNttProb();
    
    public double getProbabilityNet_0();
    public double getProbabilityNet_1();
    public double getProbabilityNet_2();
}
