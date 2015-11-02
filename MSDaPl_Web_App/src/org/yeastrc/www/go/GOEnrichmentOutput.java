/**
 * GOEnrichmentOutput.java
 * @author Vagisha Sharma
 * May 26, 2009
 * @version 1.0
 */
package org.yeastrc.www.go;

import java.sql.SQLException;
import java.util.List;

import org.yeastrc.bio.go.GOUtils;
import org.yeastrc.bio.taxonomy.Species;

/**
 * 
 */
public class GOEnrichmentOutput {

    private final int speciesId; // NRSEQ species id
    private String speciesName;
    private String goDomainName;
    
    private int numInputProteins; 			// number of proteins given for analysis
    private int numInputAnnotatedProteins; 	// number of proteins in the input that have at least one GO annotation
    
    private int numAllAnnotatedSpeciesProteins; // number of ALL proteins in our database that come from the given species AND
    									        // have at least one GO annotation.
    
    private final double pValCutoff;
    private boolean applyMultiTestCorrection = true;
    private boolean exactAnnotations = false;
    
    private List<EnrichedGOTerm> enrichedTerms;  // enriched terms for Cellular Component
    
    public GOEnrichmentOutput(GOEnrichmentInput input) {
        this.speciesId = input.getSpeciesId();
        Species species = new Species();
        try {
			species.setId(speciesId);
			this.speciesName = species.getName();
		} catch (SQLException e) {
			this.speciesName = "ERROR";
		}
        if(input.getGoAspect() == GOUtils.BIOLOGICAL_PROCESS)
        	goDomainName = "Biological Process";
        else if(input.getGoAspect() == GOUtils.CELLULAR_COMPONENT)
        	goDomainName = "Cellular Component";
        else if(input.getGoAspect() == GOUtils.MOLECULAR_FUNCTION) 
        	goDomainName = "MolecularFunction";
        
        this.pValCutoff = input.getPValCutoff();
        this.applyMultiTestCorrection = input.isApplyMultiTestCorrection();
        this.exactAnnotations = input.isExactAnnotations();
    }
    
    public int getSpeciesId() {
        return speciesId;
    }
    public String getSpeciesName() {
    	return this.speciesName;
    }
    public String getGoDomainName() {
    	return goDomainName;
    }
    
    public void setGoDomainName(String goDomainName) {
    	this.goDomainName = goDomainName;
    }
    
    public double getpValCutoff() {
        return pValCutoff;
    }

    public boolean isApplyMultiTestCorrection() {
		return applyMultiTestCorrection;
	}

	public boolean isExactAnnotations() {
		return exactAnnotations;
	}
    
    public List<EnrichedGOTerm> getEnrichedTerms() {
        return enrichedTerms;
    }
    
    public void setEnrichedTerms(List<EnrichedGOTerm> terms) {
    	this.enrichedTerms = terms;
    }
    
    public int getEnrichedTermCount() {
    	return enrichedTerms.size();
    }

	public int getNumInputProteins() {
		return numInputProteins;
	}

	public void setNumInputProteins(int numInputProteins) {
		this.numInputProteins = numInputProteins;
	}

	public int getNumInputAnnotatedProteins() {
		return numInputAnnotatedProteins;
	}

	public void setNumInputAnnotatedProteins(int numInputAnnotatedProteins) {
		this.numInputAnnotatedProteins = numInputAnnotatedProteins;
	}

	public int getNumAllAnnotatedSpeciesProteins() {
		return numAllAnnotatedSpeciesProteins;
	}

	public void setNumAllAnnotatedSpeciesProteins(int numAllAnnotatedSpeciesProteins) {
		this.numAllAnnotatedSpeciesProteins = numAllAnnotatedSpeciesProteins;
	}
}
