/**
 * EditPaymentMethodAction.java
 * @author Vagisha Sharma
 * May 20, 2011
 */
package org.yeastrc.www.project.payment;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.uwpr.instrumentlog.InstrumentUsagePaymentDAO;
import org.yeastrc.project.Affiliation;
import org.yeastrc.project.Project;
import org.yeastrc.project.ProjectFactory;
import org.yeastrc.project.payment.PaymentMethod;
import org.yeastrc.project.payment.PaymentMethodDAO;
import org.yeastrc.utils.CountriesBean;
import org.yeastrc.utils.StatesBean;
import org.yeastrc.www.user.User;
import org.yeastrc.www.user.UserUtils;

/**
 * 
 */
public class EditPaymentMethodAction extends Action {

	//private static final Logger log = Logger.getLogger(EditPaymentMethodAction.class);
	
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
		
        
		// we need a projectID so that we can restrict access to researchers on a project
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
        
        
        // user should be an admin OR a researcher on the project
        Project project = ProjectFactory.getProject(projectId);
        if(!project.checkAccess(user.getResearcher())) {
        	ActionErrors errors = new ActionErrors();
			errors.add("payment", new ActionMessage("error.payment.invalidaccess","User does not have access to view details of the payment method."));
			saveErrors( request, errors );
			ActionForward fwd = mapping.findForward("Failure");
			ActionForward newFwd = new ActionForward(fwd.getPath()+"?ID="+projectId, fwd.getRedirect());
        	return newFwd;
        }
        
        // we need a paymentMethodId
        int paymentMethodId = 0;
        try {
        	paymentMethodId = Integer.parseInt(request.getParameter("paymentMethodId"));
        }
        catch(NumberFormatException e) {
        	paymentMethodId = 0;
        }
        if(paymentMethodId == 0) {
        	ActionErrors errors = new ActionErrors();
            errors.add("payment", new ActionMessage("error.payment.invalidid", "Invalid paymentMethodID in request"));
            saveErrors( request, errors );
            ActionForward fwd = mapping.findForward("Failure");
			ActionForward newFwd = new ActionForward(fwd.getPath()+"?ID="+projectId, fwd.getRedirect());
        	return newFwd;
        }
        
        // load the payment method
        PaymentMethod paymentMethod = null;
        try {
        	paymentMethod = PaymentMethodDAO.getInstance().getPaymentMethod(paymentMethodId);
        }
        catch(Exception e) {
        	ActionErrors errors = new ActionErrors();
			errors.add("payment", new ActionMessage("error.payment.load",
					"Error loading payment method for ID: "+paymentMethodId+". "+e.getMessage()));
			saveErrors( request, errors );
			ActionForward fwd = mapping.findForward("Failure");
			ActionForward newFwd = new ActionForward(fwd.getPath()+"?ID="+projectId, fwd.getRedirect());
        	return newFwd;
        }
        if(paymentMethod == null) {
        	ActionErrors errors = new ActionErrors();
			errors.add("payment", new ActionMessage("error.payment.load","No payment method found for ID: "+paymentMethodId));
			saveErrors( request, errors );
			ActionForward fwd = mapping.findForward("Failure");
			ActionForward newFwd = new ActionForward(fwd.getPath()+"?ID="+projectId, fwd.getRedirect());
        	return newFwd;
        }
        
        // determine if this payment is editable
        boolean isEditable = false;
        // If there are any usage blocks already associated with this payment method 
        // then the only field the user will be able to change is the status of the 
        // payment method (current or not).
        if(InstrumentUsagePaymentDAO.getInstance().hasInstrumentUsageForPayment(paymentMethod.getId())) {
        	isEditable = false;
        }
        else
        	isEditable = true;
        
        
        
        
        // set the projectID in the form
        PaymentMethodForm paymentMethodForm = (PaymentMethodForm) form;
        paymentMethodForm.setId(paymentMethod.getId());
        paymentMethodForm.setProjectId(projectId);
        paymentMethodForm.setUwBudgetNumber(paymentMethod.getUwbudgetNumber());
        paymentMethodForm.setPoNumber(paymentMethod.getPonumber());
        paymentMethodForm.setPaymentMethodName(paymentMethod.getPaymentMethodName());
        paymentMethodForm.setContactFirstName(paymentMethod.getContactFirstName());
        paymentMethodForm.setContactLastName(paymentMethod.getContactLastName());
        paymentMethodForm.setContactEmail(paymentMethod.getContactEmail());
        paymentMethodForm.setContactPhone(paymentMethod.getContactPhone());
        paymentMethodForm.setOrganization(paymentMethod.getOrganization());
        paymentMethodForm.setAddressLine1(paymentMethod.getAddressLine1());
        paymentMethodForm.setAddressLine2(paymentMethod.getAddressLine2());
        paymentMethodForm.setCity(paymentMethod.getCity());
        paymentMethodForm.setState(paymentMethod.getState());
        paymentMethodForm.setZip(paymentMethod.getZip());
        paymentMethodForm.setCountry(paymentMethod.getCountry());
        paymentMethodForm.setCurrent(paymentMethod.isCurrent());
        paymentMethodForm.setEditable(isEditable); // is the form editable
        paymentMethodForm.setFederalFunding(paymentMethod.isFederalFunding());
        
        // Only non-UW affiliated projects are allowed a PO number
        paymentMethodForm.setPonumberAllowed(!(project.getAffiliation() == Affiliation.internal));
        // Only UW affiliated projects are allowed a UW Budget number.
        paymentMethodForm.setUwbudgetAllowed(project.getAffiliation() == Affiliation.internal);
        
        // Save our states bean
		StatesBean sb = StatesBean.getInstance();
		request.getSession().setAttribute("states", sb.getStates());

		// Save our countries bean
		CountriesBean cb = CountriesBean.getInstance();
		request.getSession().setAttribute("countries", cb.getCountries());
        
        
        return mapping.findForward("Success");
	}
}
