/**
 * PhiliusResultsAction.java
 * @author Vagisha Sharma
 * Feb 2, 2010
 * @version 1.0
 */
package org.yeastrc.www.philiusws;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.yeastrc.bio.philius.PhiliusUtils;
import org.yeastrc.philius.domain.PhiliusResult;
import org.yeastrc.philius.domain.PhiliusSegment;
import org.yeastrc.philius.domain.PhiliusSegmentType;
import org.yeastrc.philius.domain.PhiliusSpSegment;
import org.yeastrc.www.proteinfer.ProteinSequenceHtmlBuilder;
import org.yeastrc.www.user.User;
import org.yeastrc.www.user.UserUtils;

/**
 * 
 */
public class PhiliusResultsAjaxAction extends Action {

    public ActionForward execute( ActionMapping mapping,
            ActionForm form,
            HttpServletRequest request,
            HttpServletResponse response )
    throws Exception {
        
        
        // User making this request
        User user = UserUtils.getUser(request);
        if (user == null) {
        	response.getWriter().write("You are not logged in!");
            response.setStatus(HttpServletResponse.SC_SEE_OTHER); // Status code (303) indicating that the response to the request can be found under a different URI.
            return null;
        }

        // get the protein inference protein ID
        int nrseqProteinId = 0;
        try {nrseqProteinId = Integer.parseInt((String)request.getParameter("nrseqProteinId"));}
        catch(NumberFormatException e){};
        // if we  do not have a valid nrseq protein ID
        // return an error.
        if(nrseqProteinId <= 0) {
        	response.setContentType("text/html");
            response.getWriter().write("FAILED: Invalid protein ID: "+nrseqProteinId);
            return null;
        }
        
        // the token for the Philius job
        int philiusToken = 0;
        try {philiusToken = Integer.parseInt(request.getParameter("philiusToken"));}
        catch(NumberFormatException e){};
        if(philiusToken <= 0) {
        	response.setContentType("text/html");
            response.getWriter().write("FAILED: Invalid Philius job token in request: "+philiusToken);
            return null;
        }
        
        // check if the job is done; otherwise tell the requester to wait
        if(!jobDone(philiusToken)) {
        	response.setContentType("text/html");
            response.getWriter().write("WAIT");
            return null;
        }
        else {
        	
        	// Get the covered peptide sequences for this protein, if available
            Set<String>peptideSequences = (Set<String>) request.getAttribute("peptides");
            if(peptideSequences == null) {
            	peptideSequences = new HashSet<String>(0);
            }
            
            
            PhiliusResultPlus result = getResult(philiusToken);
            result.setCoveredSequences(peptideSequences);

            request.setAttribute("philiusAnnotation", result);
            String html = "";
            if(!(result.getResult().getSegments() == null || result.getResult().getSegments().size() == 0)) {
            	html = PhiliusSequenceHtmlFormatter.getInstance().format(result.getResult(), result.getSequence(), peptideSequences);
            	request.setAttribute("sequenceHtml", html);
            }
            else {
            	html = ProteinSequenceHtmlBuilder.getInstance().build(result.getSequence(), peptideSequences);
            }
            request.setAttribute("sequenceHtml", html);
        	
        	request.setAttribute( "philiusmap", PhiliusImageMapMaker.getInstance().getImageMap(result.getResult(), result.getSequence()));
        	
    		// set the result in the session for future use.  Will be needed for building the 
    		// Philius graphic
    		request.getSession().setAttribute( "philiusResult", result);
    		
    		// hack to prevent caching of philius image
    		request.setAttribute("philiusToken", philiusToken);
            return mapping.findForward("Success");
        }
        
        
    }
    
    private boolean jobDone(int philiusToken) throws PhiliusWSException_Exception {
    	PhiliusPredictorService service = new PhiliusPredictorService();
    	PhiliusPredictorDelegate port = service.getPhiliusPredictorPort();
    	return port.isJobDone(philiusToken);
    }
    
    private PhiliusResultPlus getResult(int philiusToken) throws PhiliusWSException_Exception {
        
        PhiliusSequenceAnnotationWS psa = null;
        PhiliusPredictorService service = new PhiliusPredictorService();
        PhiliusPredictorDelegate port = service.getPhiliusPredictorPort();
        psa = port.getResults(philiusToken);

        PhiliusResult result = new PhiliusResult();
        result.setTransMembrane(psa.isHasTm());
        result.setSignalPeptide(psa.isHasSp());
        result.setSpProbabilitySum(psa.getSpProbabilitySum());
        result.setTmProbabilitySum(psa.getTmProbabilitySum());
        result.setTopologyConfidenceScore(psa.getTopologyConfidence());
        result.setTypeScore(psa.getTypeScore());
        result.setAnnotation(psa.getTypeString());
        
        List<PhiliusSegment> segments = new ArrayList<PhiliusSegment>();
        for(PhiliusSegmentWS wsSegment: psa.getSegments()) {
        	PhiliusSegment segment = new PhiliusSegment();
        	segment.setStart(wsSegment.getStart());
        	segment.setEnd(wsSegment.getEnd());
        	PhiliusSegmentType type = PhiliusSegmentType.forLongName(PhiliusUtils.proteinTypeStrings[wsSegment.getType()]);
        	segment.setType(type);
        	segment.setConfidence(wsSegment.getTypeConfidence());
        	
        	if(wsSegment.getSpSegments() != null) {
        		
        		List<PhiliusSpSegment> spSegments = new ArrayList<PhiliusSpSegment>();
        		
        		for(PhiliusSPSegmentWS wsSpSegment: wsSegment.getSpSegments()) {
        			
        			PhiliusSpSegment spSegment = new PhiliusSpSegment();
        			spSegment.setStart(wsSpSegment.getStart());
        			spSegment.setEnd(wsSpSegment.getEnd());
        			PhiliusSegmentType segmentType = PhiliusSegmentType.forLongName(PhiliusUtils.proteinTypeStrings[wsSpSegment.getSpSegmentType()]);
        			spSegment.setSegmentType(segmentType);
        			spSegments.add(spSegment);
        			
        		}
        		segment.setSpSegments(spSegments);
        	}
        	segments.add(segment);
        }
        
        
        PhiliusResultPlus resPlus = new PhiliusResultPlus();
        resPlus.setResult(result);
        resPlus.setSequence(psa.getSequence());
        return resPlus;
    }
}
