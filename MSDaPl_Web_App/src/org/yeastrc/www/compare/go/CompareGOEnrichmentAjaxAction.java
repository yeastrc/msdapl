/**
 * CompareGOEnrichmentAjaxAction.java
 * @author Vagisha Sharma
 * Jun 14, 2009
 * @version 1.0
 */
package org.yeastrc.www.compare.go;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.yeastrc.ms.util.TimeUtils;
import org.yeastrc.www.compare.ComparisonProtein;
import org.yeastrc.www.compare.ProteinComparisonDataset;
import org.yeastrc.www.compare.ProteinSetComparisonForm;
import org.yeastrc.www.go.GOEnrichmentCalculator;
import org.yeastrc.www.go.GOEnrichmentChartUrlCreator;
import org.yeastrc.www.go.GOEnrichmentInput;
import org.yeastrc.www.go.GOEnrichmentOutput;

/**
 * 
 */
public class CompareGOEnrichmentAjaxAction extends Action {

    private static final Logger log = Logger.getLogger(CompareGOEnrichmentAjaxAction.class.getName());
    
    public ActionForward execute( ActionMapping mapping,
            ActionForm form,
            HttpServletRequest request,
            HttpServletResponse response )
    throws Exception {
        
        
        long s = System.currentTimeMillis();
        
        ProteinComparisonDataset comparison = (ProteinComparisonDataset) request.getAttribute("comparisonDataset");
        if(comparison == null) {
            request.setAttribute("errorMessage", "Comparison dataset not found in request");
            return mapping.findForward("FailureMessage");
        }
        
        ProteinSetComparisonForm myForm = (ProteinSetComparisonForm) request.getAttribute("comparisonForm");
        if(myForm == null) {
        	request.setAttribute("errorMessage", "Comparison form not found in request");
            return mapping.findForward("FailureMessage");
        }
        
        
        int goAspect = myForm.getGoAspect();
        int speciesId = myForm.getSpeciesId();
        
        List<Integer> nrseqIds = new ArrayList<Integer>(comparison.getProteins().size());
        log.info(nrseqIds.size()+" proteins for GO enrichment analysis");
        for(ComparisonProtein protein: comparison.getProteins()) {
            nrseqIds.add(protein.getNrseqId());
        }
        
        GOEnrichmentOutput enrichment = doGoEnrichmentAnalysis(nrseqIds, speciesId, goAspect, myForm.getGoEnrichmentPValDouble());
        
        long e = System.currentTimeMillis();
        log.info("CompareGOEnrichmentAjaxAction results in: "+TimeUtils.timeElapsedMinutes(s,e)+" minutes");
        
//        if(myForm.isGoEnrichmentGraph()) { // TODO need to figure out how to get this to work.
//            request.setAttribute("enrichedTerms", enrichment.getEnrichedTerms());
//            return mapping.findForward("CreateGraph");
//        }
//        else {
        	request.setAttribute("goEnrichment", enrichment);
        	
        	if(enrichment.getEnrichedTermCount() > 0) {
            	String pieChartUrl = GOEnrichmentChartUrlCreator.getPieChartUrl(enrichment, 15);
            	request.setAttribute("pieChartUrl", pieChartUrl);

            	String barChartUrl = GOEnrichmentChartUrlCreator.getBarChartUrl(enrichment, 15);
            	request.setAttribute("barChartUrl", barChartUrl);
            }
        	
        	return mapping.findForward("Success");
//        }
    }
    
    private GOEnrichmentOutput doGoEnrichmentAnalysis(List<Integer> nrseqIds, int speciesId, int goAspect, double pVal) throws Exception {
        
        log.info(nrseqIds.size()+" proteins for GO enrichment analysis");
        
        GOEnrichmentInput input = new GOEnrichmentInput(speciesId);
        input.setProteinIds(nrseqIds);
        input.setPValCutoff(pVal);
        input.setGoAspect(goAspect);
        
        GOEnrichmentOutput enrichment = GOEnrichmentCalculator.calculate(input);
        return enrichment;
        
    }
}
