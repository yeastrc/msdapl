/**
 * DeletePaymentMethodAction.java
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
import org.uwpr.instrumentlog.InstrumentUsagePaymentDAO;
import org.yeastrc.project.Project;
import org.yeastrc.project.ProjectFactory;
import org.yeastrc.project.payment.PaymentMethod;
import org.yeastrc.project.payment.PaymentMethodDAO;
import org.yeastrc.project.payment.ProjectPaymentMethodDAO;
import org.yeastrc.www.user.Groups;
import org.yeastrc.www.user.User;
import org.yeastrc.www.user.UserUtils;

/**
 * 
 */
public class DeletePaymentMethodAction extends Action {

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
        
        
        // the user requesting this action should be the one who created this payment method
        // OR the user should be an admin
        Groups groupMan = Groups.getInstance();
        if (!groupMan.isMember(user.getResearcher().getID(), "administrators")) {
        	
        	if(paymentMethod.getCreatorId() != user.getID()) {
        		
        		ActionErrors errors = new ActionErrors();
    			errors.add("costcenter", new ActionMessage("error.costcenter.invalidaccess","A payment method can only be deleted by the user that created it."));
    			saveErrors( request, errors );
            	
    			ActionForward fwd = mapping.findForward("Failure");
    			ActionForward newFwd = new ActionForward(fwd.getPath()+"?ID="+projectId, fwd.getRedirect());
            	return newFwd;
        	}
           
        }
        
        // If there are any usage blocks already associated with this payment method
        // then it cannot be deleted.
        if(InstrumentUsagePaymentDAO.getInstance().hasInstrumentUsageForPayment(paymentMethod.getId())) {
        	ActionErrors errors = new ActionErrors();
			errors.add("costcenter", new ActionMessage("error.costcenter.delete", 
					"Payment method could not be deleted as it is already in use."));
			saveErrors( request, errors );
			
			ActionForward fwd = mapping.findForward("Failure");
			ActionForward newFwd = new ActionForward(fwd.getPath()+"?ID="+projectId, fwd.getRedirect());
        	return newFwd;
        }
        
        // delete the payment method
        try {
        	ProjectPaymentMethodDAO ppmDao = ProjectPaymentMethodDAO.getInstance();
        	ppmDao.deletePaymentMethod(paymentMethodId);
        }
        catch(Exception e) {
        	ActionErrors errors = new ActionErrors();
			errors.add("costcenter", new ActionMessage("error.costcenter.delete", "Error deleting payment method."+e.getMessage()));
			saveErrors( request, errors );
			log.error("Error deleting payment method", e);
			
			ActionForward fwd = mapping.findForward("Failure");
			ActionForward newFwd = new ActionForward(fwd.getPath()+"?ID="+projectId, fwd.getRedirect());
        	return newFwd;
        }
        
        ActionForward oldFwd = mapping.findForward("Success");
        ActionForward fwd = new ActionForward(oldFwd.getPath()+"?ID="+projectId, oldFwd.getRedirect());
        return fwd;
	}
}
