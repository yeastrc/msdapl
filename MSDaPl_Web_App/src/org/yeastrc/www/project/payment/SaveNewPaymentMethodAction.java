/**
 * SaveNewPaymentMethod.java
 * @author Vagisha Sharma
 * May 20, 2011
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
import org.yeastrc.project.Project;
import org.yeastrc.project.ProjectFactory;
import org.yeastrc.project.payment.PaymentMethod;
import org.yeastrc.project.payment.ProjectPaymentMethodDAO;
import org.yeastrc.www.user.User;
import org.yeastrc.www.user.UserUtils;

/**
 * 
 */
public class SaveNewPaymentMethodAction extends Action {

	private static final Logger log = Logger.getLogger(SaveNewPaymentMethodAction.class);
	
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
		
        
        PaymentMethodForm pmForm = (PaymentMethodForm) form;
        
        // make sure we have a project ID
        int projectId = pmForm.getProjectId();
        if(projectId == 0) {
        	ActionErrors errors = new ActionErrors();
			errors.add("payment", new ActionMessage("error.payment.invalidid","No projectID found in form."));
			saveErrors( request, errors );
        	return mapping.findForward("standardHome");
        }
        
        // Make sure the user has access to the project
        try {
        	Project project = ProjectFactory.getProject(projectId);
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
        
        
        
        PaymentMethod paymentMethod = new PaymentMethod();
        paymentMethod.setUwbudgetNumber(pmForm.getUwBudgetNumber());
        paymentMethod.setPonumber(pmForm.getPoNumber());
        paymentMethod.setContactFirstName(pmForm.getContactFirstName());
        paymentMethod.setContactLastName(pmForm.getContactLastName());
        paymentMethod.setContactEmail(pmForm.getContactEmail());
        paymentMethod.setContactPhone(pmForm.getContactPhone());
        paymentMethod.setOrganization(pmForm.getOrganization());
        paymentMethod.setAddressLine1(pmForm.getAddressLine1());
        paymentMethod.setAddressLine2(pmForm.getAddressLine2());
        paymentMethod.setCity(pmForm.getCity());
        paymentMethod.setState(pmForm.getState());
        paymentMethod.setZip(pmForm.getZip());
        paymentMethod.setCountry(pmForm.getCountry());
        paymentMethod.setCreatorId(user.getID());
        paymentMethod.setCurrent(true);
        paymentMethod.setFederalFunding(pmForm.isFederalFunding());
        
        // save the payment method
        try {
        	ProjectPaymentMethodDAO ppmDao = ProjectPaymentMethodDAO.getInstance();
        	ppmDao.savePaymentMethod(pmForm.getProjectId(), paymentMethod);
        }
        catch(Exception e) {
        	ActionErrors errors = new ActionErrors();
			errors.add("payment", new ActionMessage("error.payment.save", e.getMessage()));
			saveErrors( request, errors );
			log.error("Error saving payment method", e);
			ActionForward fwd = mapping.findForward("Failure");
			ActionForward newFwd = new ActionForward(fwd.getPath()+"?ID="+projectId, fwd.getRedirect());
        	return newFwd;
        }
        

        ActionForward fwd = mapping.findForward("Success");
		ActionForward newFwd = new ActionForward(fwd.getPath()+"?ID="+projectId, fwd.getRedirect());
    	return newFwd;
	}
}
