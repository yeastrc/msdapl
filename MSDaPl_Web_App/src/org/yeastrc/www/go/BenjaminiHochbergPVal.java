/**
 * BenjaminiHochbergPValCorrector.java
 * @author Vagisha Sharma
 * Dec 28, 2010
 */
package org.yeastrc.www.go;

import java.util.Collections;
import java.util.List;

import org.apache.log4j.Logger;

/**
 * 
 */
public class BenjaminiHochbergPVal {

	private static final Logger log = Logger.getLogger(BenjaminiHochbergPVal.class);
	
	/**
     * Applies the Benjamini and Hochberg FDR correction
     * 
     * GO terms are sorted in ascending order of p-value. 
     * Rank of term with smallest p-value = 1
     * pvalue_adjusted = pvalue * (n/(rank_in_sorted_list)))
     */
	public static void adjust(List<EnrichedGOTerm> enrichedTerms) {
		
		// sort the terms by hypergeometric p-value (ascending)
    	Collections.sort(enrichedTerms);
    	
    	int n = enrichedTerms.size();
    	
    	double minPVal = 1.0;
    	
    	for (int i = n; i > 0; i--) {
    		
    		EnrichedGOTerm term = enrichedTerms.get(i-1);
    		
    		double corrected = (term.getPValue() * n)/(double)(i); // pvalue_adjusted = pvalue * (n/(rank_in_list)))
    		
    		minPVal = Math.min(minPVal, corrected); // To preserve monotonocity
    		
    		//log.info("rank: "+(i)+" "+term.getGoNode().getAccession()+"; original: "+term.getPValue()+" corrected is: "+minPVal);
    		term.setCorrectedPvalue(minPVal);
    	}
	}
}
