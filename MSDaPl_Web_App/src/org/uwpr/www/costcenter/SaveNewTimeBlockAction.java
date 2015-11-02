/**
 * ViewTimeBlocksAction.java
 * @author Vagisha Sharma
 * May 1, 2011
 */
package org.uwpr.www.costcenter;

import java.sql.SQLException;
import java.sql.Time;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.uwpr.costcenter.TimeBlock;
import org.uwpr.costcenter.TimeBlockDAO;
import org.yeastrc.www.user.Groups;
import org.yeastrc.www.user.User;
import org.yeastrc.www.user.UserUtils;

/**
 * 
 */
public class SaveNewTimeBlockAction extends Action {

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
        
        TimeBlockForm timeBlockForm = (TimeBlockForm) form;
        
        Time startTime = timeBlockForm.getStartTime();
        int numHours = timeBlockForm.getNumHours();
        
        TimeBlock block = new TimeBlock();
        block.setNumHours(numHours);
        block.setName(timeBlockForm.getName());
        if(startTime != null) {
        	block.setStartTime(startTime);
        }
        
        try {
        	TimeBlockDAO.getInstance().saveTimeBlock(block);
        }
        catch(SQLException e) {
        	ActionErrors errors = new ActionErrors();
            errors.add("costcenter", new ActionMessage("error.costcenter.save", e.getMessage()));
            saveErrors( request, errors );
            return mapping.findForward("Failure");
        }
        
        ActionForward fwd = mapping.findForward("Success");
        return new ActionForward(fwd.getPath()+"?showall=true", fwd.getRedirect());
	}
}
