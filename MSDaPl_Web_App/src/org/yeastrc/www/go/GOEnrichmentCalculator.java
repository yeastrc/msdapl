/**
 * GOEnrichmentCalculator.java
 * @author Vagisha Sharma
 * May 26, 2009
 * @version 1.0
 */
package org.yeastrc.www.go;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.yeastrc.bio.go.GONode;
import org.yeastrc.bio.go.GOUtils;
import org.yeastrc.stats.StatUtils;

/**
 * 
 */
public class GOEnrichmentCalculator {

    private static final Logger log = Logger.getLogger(GOEnrichmentCalculator.class.getName());
    
    private GOEnrichmentCalculator() {}
    
    public static GOEnrichmentOutput calculate(GOEnrichmentInput input) throws Exception {
        
        log.info("Number of input proteins: "+input.getProteinIds().size());
        
        GOEnrichmentOutput output = new GOEnrichmentOutput(input);
        output.setNumInputProteins(input.getProteinIds().size());
        if(input.getGoAspect() == GOUtils.BIOLOGICAL_PROCESS)
        	output.setGoDomainName("Biological Process");
        else if(input.getGoAspect() == GOUtils.CELLULAR_COMPONENT)
        	output.setGoDomainName("Cellular Component");
        else if(input.getGoAspect() == GOUtils.MOLECULAR_FUNCTION)
        	output.setGoDomainName("Molecular Function");
        
        getEnrichedTerms(output, input.getProteinIds(), input.getGoAspect());
        return output;
    }
    
    private static void getEnrichedTerms(GOEnrichmentOutput output, List<Integer> proteinIds, int goAspect) throws Exception {
        
        int numSpeciesAnnotatedProteins = totalAnnotatedProteinCount(output.getSpeciesId());
        output.setNumAllAnnotatedSpeciesProteins(numSpeciesAnnotatedProteins);
        log.info("Total number of annotated species proteins: "+numSpeciesAnnotatedProteins);
        
        int totalAnnotatedInputProteins = totalAnnotatedInputProteinCount(proteinIds);
        log.info("Total number of annotated input proteins: "+totalAnnotatedInputProteins);
        output.setNumInputAnnotatedProteins(totalAnnotatedInputProteins);
        
        
        double pValCutoff = output.getpValCutoff();
        int speciesId = output.getSpeciesId();
        
        // Get all the GO terms for our protein set
        Map<String, EnrichedGOTerm> goTerms = getAllGOAspectTerms(speciesId, goAspect, output.isExactAnnotations(), proteinIds);
        
        List<EnrichedGOTerm> enrichedTerms = new ArrayList<EnrichedGOTerm>(goTerms.values());
        
        Iterator<EnrichedGOTerm> iter = enrichedTerms.iterator();
        // get the root nodes
        GONode aspectRoot = GOUtils.getAspectRootNode(goAspect);
        GONode rootNode = GOUtils.getRootNode();
        
        // remove the root nodes
        while(iter.hasNext()) {
            
            EnrichedGOTerm term = iter.next();
            
            if(term.getGoNode().equals(aspectRoot))
                iter.remove();
            
            if(term.getGoNode().equals(rootNode))
                iter.remove();
        }

        calculateEnrichment(enrichedTerms, output.getNumInputAnnotatedProteins(), output.getNumAllAnnotatedSpeciesProteins());
        
        if(output.isApplyMultiTestCorrection()) {
        	log.info("Applying Benjamini Hochberg correction");
        	// Apply Benjamini-Hochberg correction to control FDR rate
        	applyBenjaminiHochbergCorrection(enrichedTerms);
        }
        
        // returns a list of GO terms enriched above the given cutoff
        iter = enrichedTerms.iterator();
        while(iter.hasNext()) {
            
            EnrichedGOTerm term = iter.next();
            if(term.getCorrectedPvalue() > pValCutoff) {
                iter.remove();
                continue;
            }
            //log.info("original: "+term.getPValue()+"; corrected: "+term.getCorrectedPvalue());
        }
        
        // sort by p-value
        Collections.sort(enrichedTerms);
        
        output.setEnrichedTerms(enrichedTerms);
    }
    
    private static void applyBenjaminiHochbergCorrection(
			List<EnrichedGOTerm> enrichedTerms) {
		
    	BenjaminiHochbergPVal.adjust(enrichedTerms);
	}

	private static int totalAnnotatedInputProteinCount(List<Integer> proteinIds) throws SQLException {
		
    	return ProteinGOAnnotationChecker.getAnnotatedProteins(proteinIds).size();
	}

	private static void calculateEnrichment(List<EnrichedGOTerm> enrichedTerms, int numProteinsInSet, int totalAnnotatedProteins) throws Exception {
        
        for(EnrichedGOTerm term: enrichedTerms) {
        	if(term.getNumAnnotatedProteins() > term.getTotalAnnotatedProteins()) {
        		term.setPValue(-1.0);
        		log.error(term.getGoNode().getAccession()+", Num annotated: "+term.getNumAnnotatedProteins()+
        				", Total annotated: "+term.getTotalAnnotatedProteins());
        	}
        	else {
        		term.setPValue(StatUtils.PScore(term.getNumAnnotatedProteins(), term.getTotalAnnotatedProteins(), numProteinsInSet, totalAnnotatedProteins));
        		term.setCorrectedPvalue(term.getPValue());
        	}
        }
    }
    
    private static int totalAnnotatedProteinCount(int speciesId) throws Exception {
    	return GOProteinCounter.getInstance().countProteins(GOUtils.getRootNode(), speciesId);
    }

    
    private static Map<String, EnrichedGOTerm> getAllGOAspectTerms(int speciesId, int goAspect, boolean exact, List<Integer> nrseqIds)
    throws Exception {

    	Map<String, EnrichedGOTerm> goTerms = new HashMap<String, EnrichedGOTerm>(); // unique GO terms

    	for(Integer nrseqId: nrseqIds) {

    		// Get a list of GO term annotations for this protein
    		Set<GONode> nodes = null;
    		try {
    			// setting the second argument (exact) to false should get us all terms for this protein
    			// This should include all ancestors of terms directly assigned to this protein.
    			nodes  = ProteinGOAnnotationSearcher.getTermsForProtein(nrseqId, goAspect, exact);
    		}
    		catch (Exception e) {
    			log.error("Could not get GO annotations for proteinID: "+nrseqId, e);
    		}

    		if(nodes != null) {
    			// Add the GO term to our map. 
    			// If the map already contains this term, add the proteinId to the term.
    			for(GONode node: nodes) {
    				EnrichedGOTerm term = goTerms.get(node.getAccession());
    				if(term == null) {
    					term = initEnrichedGOTerm(node, speciesId, exact);
    					goTerms.put(node.getAccession(), term);
    				}
    				term.addProtein(nrseqId);
    			}
    		}
    	}
    	
    	return goTerms;
    }
    
    private static EnrichedGOTerm initEnrichedGOTerm(GONode node, int speciesId, boolean exact) throws Exception {
        int totalProteins = 0; // total proteins in the universe with this GO term
        totalProteins = GOProteinCounter.getInstance().countProteins(node, speciesId, exact);
        return new EnrichedGOTerm(node, totalProteins);
    }
    
}
