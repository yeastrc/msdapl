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
import org.uwpr.costcenter.TimeBlockDAO;
import org.yeastrc.www.user.Groups;
import org.yeastrc.www.user.User;
import org.yeastrc.www.user.UserUtils;

/**
 * 
 */
public class DeleteTimeBlockAction extends Action {

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
        
        int timeBlockId = 0;
        try {
        	timeBlockId = Integer.parseInt(request.getParameter("timeBlockId"));
        }
        catch(NumberFormatException e) {
        	ActionErrors errors = new ActionErrors();
        	errors.add("costcenter", new ActionMessage("error.costcenter.delete", " Invalid time block ID: "+timeBlockId));
            saveErrors( request, errors );
            return mapping.findForward("Failure");
        }
        
        // A time block can be deleted only if there are no instrument rates associated with it
        if(InstrumentRateDAO.getInstance().hasRatesForTimeBlock(timeBlockId)) {
        	ActionErrors errors = new ActionErrors();
        	errors.add("costcenter", new ActionMessage("error.costcenter.delete", "Cannot delete time block ID: "+timeBlockId+
        			". It is associated with instrument rates"));
            saveErrors( request, errors );
            return mapping.findForward("Failure");
        }
        
        try {
        	TimeBlockDAO.getInstance().deleteTimeBlock(timeBlockId);
        }
        catch(Exception e) {
        	ActionErrors errors = new ActionErrors();
            errors.add("costcenter", new ActionMessage("error.costcenter.delete", e.getMessage()));
            saveErrors( request, errors );
            return mapping.findForward("Failure");
        }
        
        ActionForward fwd = mapping.findForward("Success");
        return new ActionForward(fwd.getPath()+"?showall=true", fwd.getRedirect());
        
	}
}
