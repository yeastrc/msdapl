/**
 * 
 */
package org.uwpr.www.costcenter;

import java.util.Calendar;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.yeastrc.project.Project;
import org.yeastrc.project.ProjectsSearcher;
import org.yeastrc.www.user.Groups;
import org.yeastrc.www.user.User;
import org.yeastrc.www.user.UserUtils;

/**
 * ViewCostCenterHomeAction.java
 * @author Vagisha Sharma
 * Jun 20, 2011
 * 
 */
public class ViewCostCenterHomeAction extends Action {

	//private static final Logger log = Logger.getLogger(ViewCostCenterHomeAction.class);
	
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
            return mapping.findForward("standardHome");
        }
        
        ExportBillingInformationForm exportForm = (ExportBillingInformationForm) form;
        
        // set the start date to be the 1st of the current month
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        exportForm.setStartDate(calendar.getTime());
        // set the end date to be the last date of the current month
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        exportForm.setEndDate(calendar.getTime());
        
        exportForm.setSummarize(true);
        
        // get a list of projects
        ProjectsSearcher searcher = new ProjectsSearcher();
        List<Project> projects = searcher.search();
        request.getSession().setAttribute("billedProjects", projects);
        
        return mapping.findForward("Success");
	}
}
