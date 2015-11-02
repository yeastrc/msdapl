/**
 * WairForClusteringAction.java
 * @author Vagisha Sharma
 * Apr 20, 2010
 * @version 1.0
 */
package org.yeastrc.www.compare.clustering;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.yeastrc.www.compare.ComparisonCommand;
import org.yeastrc.www.compare.ProteinSetComparisonForm;
import org.yeastrc.www.taglib.HistoryTag;

/**
 * 
 */
public class WaitForClusteringAction extends Action {

	private static final Logger log = Logger.getLogger(WaitForClusteringAction.class.getName());
    
    @Override
    public ActionForward execute(ActionMapping mapping, ActionForm form,
            HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        
        log.info("Got request for clustering spectrum counts. Forwarding to wait page...");
        
        ProteinSetComparisonForm myForm = (ProteinSetComparisonForm) form;
        if(myForm == null) {
            ActionErrors errors = new ActionErrors();
            errors.add(ActionErrors.GLOBAL_ERROR, new ActionMessage("error.general.errorMessage", "Comparison form not found in request"));
            saveErrors( request, errors );
            return mapping.findForward("Failure");
        }
        
        String token;
		// Create a new token and put it in the form
		token = createToken();
		myForm.setClusteringToken(token);
		myForm.setNewToken(true); // this is new token
		
		List<ComparisonCommand> comparisonCommands = new ArrayList<ComparisonCommand>(1);
		comparisonCommands.add(ComparisonCommand.CLUSTER);
		request.setAttribute("comparisonCommands", comparisonCommands);
        request.setAttribute(HistoryTag.NO_HISTORY_ATTRIB, true); // We don't want this added to history.
        return mapping.findForward("Success");
    }
    
    private String createToken() {
		return String.valueOf(System.currentTimeMillis());
	}
}
