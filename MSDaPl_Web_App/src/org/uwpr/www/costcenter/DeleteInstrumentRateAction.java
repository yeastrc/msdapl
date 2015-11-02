/**
 * ViewTimeBlocksAction.java
 * @author Vagisha Sharma
 * May 1, 2011
 */
package org.uwpr.www.costcenter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.uwpr.costcenter.InstrumentRateDAO;
import org.uwpr.instrumentlog.InstrumentUsageDAO;
import org.yeastrc.www.user.Groups;
import org.yeastrc.www.user.User;
import org.yeastrc.www.user.UserUtils;

/**
 * 
 */
public class DeleteInstrumentRateAction extends Action {

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
		
		// Restrict access to administrators
        Groups groupMan = Groups.getInstance();
        if (!groupMan.isMember(user.getResearcher().getID(), "administrators")) {
            ActionErrors errors = new ActionErrors();
            errors.add("access", new ActionMessage("error.access.invalidgroup"));
            saveErrors( request, errors );
            return mapping.findForward("Failure");
        }
        
        int instrumentRateId = 0;
        try {
        	instrumentRateId = Integer.parseInt(request.getParameter("instrumentRateId"));
        }
        catch(NumberFormatException e) {
        	ActionErrors errors = new ActionErrors();
        	errors.add("costcenter", new ActionMessage("error.costcenter.delete", " Invalid instrument rate ID: "+instrumentRateId));
            saveErrors( request, errors );
            return mapping.findForward("Failure");
        }
        
        
        // If the instrument rate has been assigned to a usage block it cannot be deleted.
        if(InstrumentUsageDAO.getInstance().hasInstrumentUsageForInstrumentRate(instrumentRateId)) {
        	
        	ActionErrors errors = new ActionErrors();
        	errors.add("costcenter", new ActionMessage("error.costcenter.delete", "Cannot delete instrument rate ID: "+instrumentRateId+
        			". It is associated with usage block(s)"));
            saveErrors( request, errors );
            return mapping.findForward("Failure");
        }
        
        try {
        	InstrumentRateDAO.getInstance().deleteInstrumentRate(instrumentRateId);
        }
        catch(Exception e) {
        	ActionErrors errors = new ActionErrors();
            errors.add("costcenter", new ActionMessage("error.costcenter.delete", e.getMessage()));
            saveErrors( request, errors );
            return mapping.findForward("Failure");
        }
        
        return mapping.findForward("Success");
        
	}
}
