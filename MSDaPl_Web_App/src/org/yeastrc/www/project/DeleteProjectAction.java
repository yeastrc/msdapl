/* DeleteProjectAction.java
 * Created on Mar 30, 2004
 */
package org.yeastrc.www.project;

import java.util.List;

import javax.servlet.http.*;
import org.apache.struts.action.*;

import org.yeastrc.data.InvalidIDException;
import org.yeastrc.experiment.ProjectExperimentDAO;
import org.yeastrc.project.*;
import org.yeastrc.www.project.experiment.ExperimentDeleter;
import org.yeastrc.www.user.*;

/**
 * Implements the logic to delete a project
 */
public class DeleteProjectAction extends Action {

	public ActionForward execute( ActionMapping mapping,
								  ActionForm form,
								  HttpServletRequest request,
								  HttpServletResponse response )
	throws Exception {
		// Get the projectID they're after
		int projectID;
		
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


		try {
			String strID = request.getParameter("ID");

			if (strID == null || strID.equals("")) {
				ActionErrors errors = new ActionErrors();
				errors.add("username", new ActionMessage("error.project.noprojectid"));
				saveErrors( request, errors );
				return mapping.findForward("standardHome");
			}

			projectID = Integer.parseInt(strID);

		} catch (NumberFormatException nfe) {
			ActionErrors errors = new ActionErrors();
			errors.add("username", new ActionMessage("error.project.invalidprojectid"));
			saveErrors( request, errors );
			return mapping.findForward("standardHome");
		}

		
		// get all the experiment IDs for the project and mark them for deletion
		List<Integer> experimentIds = ProjectExperimentDAO.instance().getExperimentIdsForProject(projectID);
		ExperimentDeleter deleter = ExperimentDeleter.getInstance();
		for(int exptId: experimentIds)
            deleter.addExperimentId(exptId, projectID, true);
		
		try {
			ProjectDAO.instance().delete(projectID);
		} catch (InvalidIDException e) {
			
			ActionErrors errors = new ActionErrors();
			errors.add("username", new ActionMessage("error.project.projectnotfound"));
			saveErrors( request, errors );
			return mapping.findForward("standardHome");	
		}

		return mapping.findForward("Success");

	}
	
}