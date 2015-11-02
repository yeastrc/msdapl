/**
 * NewPaymentMethodAction.java
 * @author Vagisha Sharma
 * May 19, 2011
 */
package org.yeastrc.www.project.payment;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.yeastrc.project.Affiliation;
import org.yeastrc.project.Project;
import org.yeastrc.project.ProjectFactory;
import org.yeastrc.utils.CountriesBean;
import org.yeastrc.utils.StatesBean;
import org.yeastrc.www.user.User;
import org.yeastrc.www.user.UserUtils;

/**
 * 
 */
public class NewPaymentMethodAction extends Action {

	private static final Logger log = Logger.getLogger(NewPaymentMethodAction.class);
	
	public ActionForward execute(ActionMapping mapping, ActionForm form, 
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		
		// User making this request
		User user = UserUtils.getUser(request);
		if (user == null) {
			ActionErrors errors = new ActionErrors();
			errors.add("username", new ActionMessage("error.login.notloggedin"));
			saveErrors( request, errors );
			return mapping.findForward("authenticate");
		}
		
        
        // we need a projectID
        int projectId = 0;
        try {
        	projectId = Integer.parseInt(request.getParameter("projectId"));
        }
        catch(NumberFormatException e) {
        	projectId = 0;
        }
        if(projectId == 0) {
        	ActionErrors errors = new ActionErrors();
            errors.add("payment", new ActionMessage("error.payment.invalidid", "Invalid projectID in request"));
            saveErrors( request, errors );
            return mapping.findForward("standardHome");
        }
        
        // Make sure the user has access to the project
        Project project = null;
        try {
        	project = ProjectFactory.getProject(projectId);
        	if(!project.checkAccess(user.getResearcher())) {
        		ActionErrors errors = new ActionErrors();
        		errors.add("payment", new ActionMessage("error.payment.invalidaccess","User does not have access to save payment method for project."));
        		saveErrors( request, errors );
        		ActionForward fwd = mapping.findForward("Failure");
    			ActionForward newFwd = new ActionForward(fwd.getPath()+"?ID="+projectId, fwd.getRedirect());
            	return newFwd;

        	}
        }
        catch(Exception e) {
        	ActionErrors errors = new ActionErrors();
			errors.add("payment", new ActionMessage("error.payment.load","Error loading project to check access."));
			saveErrors( request, errors );
			log.error("Error checking access to project ID: "+projectId, e);
			ActionForward fwd = mapping.findForward("Failure");
			ActionForward newFwd = new ActionForward(fwd.getPath()+"?ID="+projectId, fwd.getRedirect());
        	return newFwd;

        }
        
        // set the projectID in the form
        PaymentMethodForm paymentMethodForm = (PaymentMethodForm) form;
        paymentMethodForm.setUwBudgetNumber(""); // Rest Budget number and PO number fields, otherwise cached values get sent in the request.
        paymentMethodForm.setPoNumber("");
        paymentMethodForm.setProjectId(projectId);
        
        // Only non-UW affiliated projects are allowed a PO number
        paymentMethodForm.setPonumberAllowed(!((project).getAffiliation() == Affiliation.internal));
        // Only UW affiliated projects are allowed a UW Budget number.
        paymentMethodForm.setUwbudgetAllowed((project).getAffiliation() == Affiliation.internal);
        
        // Save our states bean
		StatesBean sb = StatesBean.getInstance();
		request.getSession().setAttribute("states", sb.getStates());

		// Save our countries bean
		CountriesBean cb = CountriesBean.getInstance();
		request.getSession().setAttribute("countries", cb.getCountries());
        
        
        return mapping.findForward("Success");
	}
}
