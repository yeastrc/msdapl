/**
 * 
 */
package org.yeastrc.www.philiusws;

import java.util.HashSet;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.yeastrc.ms.dao.ProteinferDAOFactory;
import org.yeastrc.ms.domain.protinfer.ProteinferPeptide;
import org.yeastrc.ms.domain.protinfer.ProteinferProtein;
import org.yeastrc.www.user.User;
import org.yeastrc.www.user.UserUtils;

/**
 * PhiliusResultForPinferProteinAction.java
 * @author Vagisha Sharma
 * May 24, 2010
 * 
 */
public class PhiliusResultForPinferProteinAction extends Action {

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
        int pinferProteinId = 0;
        try {pinferProteinId = Integer.parseInt(request.getParameter("pinferProteinId"));}
        catch(NumberFormatException e){};
        // if we  do not have a valid protein inference protein ID
        // return an error.
        if(pinferProteinId <= 0) {
        	response.setContentType("text/html");
            response.getWriter().write("FAILED: Invalid protein inference protein ID: "+pinferProteinId);
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
        	
        	ProteinferProtein protein = ProteinferDAOFactory.instance().getProteinferProteinDao().loadProtein(pinferProteinId);
        	if(protein == null) {
        		response.setContentType("text/html");
                response.getWriter().write("FAILED: No database entry found for protein inference protein ID: "+pinferProteinId);
                return null;
        	}
        	
        	// Get the unique peptide sequences for this protein (for building the protein sequence HTML)
            Set<String>peptideSequences = new HashSet<String>(protein.getPeptideCount());
            for(ProteinferPeptide peptide: protein.getPeptides()) {
                peptideSequences.add(peptide.getSequence());
            }
            
            request.setAttribute("peptides", peptideSequences);
            ActionForward fwd = mapping.findForward("MakeResult");
            ActionForward newFwd = new ActionForward(fwd.getPath()+
					"?nrseqProteinId="+protein.getNrseqProteinId()+
					"&philiusToken="+philiusToken,
					fwd.getRedirect());
            return newFwd;
        }
    }
	
	private boolean jobDone(int philiusToken) throws PhiliusWSException_Exception {
    	PhiliusPredictorService service = new PhiliusPredictorService();
    	PhiliusPredictorDelegate port = service.getPhiliusPredictorPort();
    	return port.isJobDone(philiusToken);
    }
}
