/**
 * ProteinProphetResultGatewayAction.java
 * @author Vagisha Sharma
 * Mar 25, 2010
 * @version 1.0
 */
package org.yeastrc.www.proteinfer.proteinProphet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.yeastrc.www.user.User;
import org.yeastrc.www.user.UserUtils;

/**
 * 
 */
public class ProteinProphetResultGatewayAction extends Action {

	private static final Logger log = Logger.getLogger(ProteinProphetResultGatewayAction.class);

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

        // form for filtering and display options
        ProteinProphetFilterForm filterForm = (ProteinProphetFilterForm)form;
        
        if(filterForm.isDoDownload()) {
        	return mapping.findForward("Download");
        }
        if (filterForm.isDoGoSlimAnalysis()) {
        	return mapping.findForward("GOSlimAnalysis");
        }
        else if (filterForm.isDoGoEnrichAnalysis()) {
        	return mapping.findForward("GOEnrichAnalysis");
        }
        else {
        	if(request.getAttribute("newRequest") == null) {
        		return mapping.findForward("Update");
        	}
        	else
        		return mapping.findForward("ViewResults");
        }
        
	}
}
