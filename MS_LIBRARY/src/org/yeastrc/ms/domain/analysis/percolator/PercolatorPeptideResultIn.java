/**
 * PercolatorPeptideResultIn.java
 * @author Vagisha Sharma
 * Sep 16, 2010
 */
package org.yeastrc.ms.domain.analysis.percolator;

import java.util.List;

import org.yeastrc.ms.domain.search.MsSearchResultPeptide;
import org.yeastrc.ms.domain.search.MsSearchResultProteinIn;
import org.yeastrc.ms.parser.percolator.PercolatorXmlPsmId;

/**
 * 
 */
public interface PercolatorPeptideResultIn {

	/**
	 * 
	 * @return the peptide
	 */
	public abstract MsSearchResultPeptide getResultPeptide();
	
	/**
     * @return the qvalue
     */
    public abstract double getQvalue();
    
    /**
     * @return the p-value
     */
    public abstract double getPvalue();
    
    /**
     * @return the posterior error probability or -1.0 if there was no posterior probability 
     * for this result
     */
    public abstract double getPosteriorErrorProbability();
    
    /**
     * @return the percolator discriminant score or null if there was no discriminant score. 
     */
    public abstract Double getDiscriminantScore();
    
    /**
     * @return the proteinMatchList
     */
    public abstract List<MsSearchResultProteinIn> getProteinMatchList();
    
    
    /**
     * 
     * @return List of IDs of PSM's for this peptide
     */
    public abstract List<PercolatorXmlPsmId> getPsmIds();
    
	
}
