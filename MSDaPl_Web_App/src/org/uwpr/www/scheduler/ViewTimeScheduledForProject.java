/**
 * 
 */
package org.uwpr.www.scheduler;

import java.util.Collections;
import java.util.Comparator;
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
import org.uwpr.instrumentlog.ProjectInstrumentUsageDAO;
import org.uwpr.instrumentlog.UsageBlock;
import org.yeastrc.project.Project;
import org.yeastrc.project.ProjectFactory;
import org.yeastrc.www.user.User;
import org.yeastrc.www.user.UserUtils;

/**
 * ViewTimeScheduledForProject.java
 * @author Vagisha Sharma
 * May 31, 2011
 * 
 */
public class ViewTimeScheduledForProject extends Action {

private static final Logger log = Logger.getLogger(ViewTimeScheduledForProject.class);
	
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
            errors.add("scheduler", new ActionMessage("error.scheduler.invalidid", "Invalid projectID in request"));
            saveErrors( request, errors );
            return mapping.findForward("standardHome");
        }
        
        // Make sure the user has access to the project
        Project project = null;
        try {
        	project = ProjectFactory.getProject(projectId);
        	if(!project.checkAccess(user.getResearcher())) {
        		ActionErrors errors = new ActionErrors();
        		errors.add("scheduler", new ActionMessage("error.scheduler.invalidaccess","User does not have access to schedule instrument time for project."));
        		saveErrors( request, errors );
        		ActionForward fwd = mapping.findForward("Failure");
    			ActionForward newFwd = new ActionForward(fwd.getPath()+"?ID="+projectId, fwd.getRedirect());
            	return newFwd;

        	}
        }
        catch(Exception e) {
        	ActionErrors errors = new ActionErrors();
			errors.add("scheduler", new ActionMessage("error.scheduler.load","Error loading project to check access."));
			saveErrors( request, errors );
			log.error("Error checking access to project ID: "+projectId, e);
			ActionForward fwd = mapping.findForward("Failure");
			ActionForward newFwd = new ActionForward(fwd.getPath()+"?ID="+projectId, fwd.getRedirect());
        	return newFwd;

        }
        
        List<UsageBlock> usageBlocks = ProjectInstrumentUsageDAO.getInstance().getUsageBlocksForProject(projectId);
        // sort the blocks by instrument and then by start date, descending
        Collections.sort(usageBlocks, new Comparator<UsageBlock>() {
			@Override
			public int compare(UsageBlock o1, UsageBlock o2) {
				int val = Integer.valueOf(o1.getInstrumentID()).compareTo(o2.getInstrumentID());
				if(val == 0)
					return o1.getStartDate().compareTo(o2.getStartDate());
				else
					return val;
			}
		});
        
        request.setAttribute("project", project);
        request.setAttribute("usageBlocks", usageBlocks);
        
        
        return mapping.findForward("Success");
	}
}
