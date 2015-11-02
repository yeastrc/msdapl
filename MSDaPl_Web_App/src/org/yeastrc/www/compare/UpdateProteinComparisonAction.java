/**
 * UpdateProteinComparisonAction.java
 * @author Vagisha Sharma
 * Nov 13, 2009
 * @version 1.0
 */
package org.yeastrc.www.compare;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.yeastrc.www.taglib.HistoryTag;
import org.yeastrc.www.user.User;
import org.yeastrc.www.user.UserUtils;

/**
 * 
 */
public class UpdateProteinComparisonAction extends Action {

    private static final Logger log = Logger.getLogger(UpdateProteinComparisonAction.class.getName());

    public ActionForward execute( ActionMapping mapping,
            ActionForm form,
            HttpServletRequest request,
            HttpServletResponse response )
    throws Exception {
        
        // User making this request
        User user = UserUtils.getUser(request);
        if (user == null) {
            ActionErrors errors = new ActionErrors();
            errors.add("username", new ActionMessage("error.login.notloggedin"));
            saveErrors( request, errors );
            return mapping.findForward("authenticate");
        }
        
        // Form we will use
        ProteinSetComparisonForm myForm = (ProteinSetComparisonForm) form;
        if(myForm == null) {
            ActionErrors errors = new ActionErrors();
            errors.add(ActionErrors.GLOBAL_ERROR, new ActionMessage("error.general.errorMessage", "No comparison form in request."));
            saveErrors( request, errors );
            return mapping.findForward("Failure");
        }
        
        request.setAttribute(HistoryTag.NO_HISTORY_ATTRIB, true); // Don't want this to be saved to history.
        
        if(myForm.isCluster())
        	return mapping.findForward("ClusterGateway");
        else {
        	return mapping.findForward("DoComparison");
        }
    }
}
