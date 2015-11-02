/*
 *
 * Created on February 5, 2004
 *
 * Created by Michael Riffle <mriffle@u.washington.edu>
 *
 */

package org.yeastrc.www.project;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.yeastrc.data.InvalidIDException;
import org.yeastrc.grant.Grant;
import org.yeastrc.group.Group;
import org.yeastrc.group.GroupDAO;
import org.yeastrc.project.Affiliation;
import org.yeastrc.project.Project;
import org.yeastrc.project.ProjectDAO;
import org.yeastrc.project.Researcher;
import org.yeastrc.www.user.Groups;
import org.yeastrc.www.user.User;
import org.yeastrc.www.user.UserUtils;

/**
 * Controller class for saving a new collaboration or technology development project.
 */
public class SaveNewProjectAction extends Action {

	public ActionForward execute( ActionMapping mapping,
								  ActionForm form,
								  HttpServletRequest request,
								  HttpServletResponse response )
	throws Exception {
		int projectID;			// Get the projectID they're after
		
		// The form elements we're after
		String title = null;
		int pi = 0;
//		String[] groups = null;
		String projectAbstract = null;
		String progress = null;
		String publications = null;
		String comments;
		Affiliation affiliation = null;

		
		// User making this request
		User user = UserUtils.getUser(request);
		if (user == null) {
			ActionErrors errors = new ActionErrors();
			errors.add("username", new ActionMessage("error.login.notloggedin"));
			saveErrors( request, errors );
			return mapping.findForward("authenticate");
		}


		// We're saving!
		title = ((EditProjectForm)(form)).getTitle();
		pi = ((EditProjectForm)(form)).getPI();
		projectAbstract = ((EditProjectForm)(form)).getAbstract();
		progress = ((EditProjectForm)(form)).getProgress();
		publications = ((EditProjectForm)(form)).getPublications();
		comments = ((EditProjectForm)(form)).getComments();
		affiliation = ((EditProjectForm)form).getAffiliation();
		
		// Set blank items to null
		if (title.equals("")) title = null;
		if (projectAbstract.equals("")) projectAbstract = null;
		if (progress.equals("")) progress = null;
		if (publications.equals("")) publications = null;
		if (comments.equals("")) comments = null;

		// Load our project
		Project project = new Project();

		// Set up our researchers
		Researcher oPI = null;
		try {
			if (pi != 0) {
				oPI = new Researcher();
				oPI.load(pi);
			}
			List<Researcher> rList = ((EditProjectForm)(form)).getResearcherList();
			List<Researcher> projResearchers = new ArrayList<Researcher>();
			for(Researcher r: rList) {
			    if(r != null && r.getID() > 0) {
			        r.load(r.getID());
			        projResearchers.add(r);
			    }
			}
			project.setResearchers(projResearchers);
		} catch (InvalidIDException iie) {
			// Couldn't load the researcher.
			ActionErrors errors = new ActionErrors();
			errors.add("project", new ActionMessage("error.project.invalidresearcher"));
			saveErrors( request, errors );
			return mapping.findForward("Failure");
		}


		// Set all of the new values in the project
		project.setTitle(title);
		project.setPI(oPI);
		project.setAbstract(projectAbstract);
		project.setProgress(progress);
		project.setPublications(publications);
		project.setComments(comments);
		project.setAffiliation(affiliation);
		
		// Get a list of this user's groups
		List<String> userGroupNames = Groups.getInstance().getUserGroups(user.getResearcher().getID());
		List<Group> groups = new ArrayList<Group>(userGroupNames.size());
		for(String groupName: userGroupNames) {
			if(groupName.equalsIgnoreCase("administrators"))
				continue;
			Group grp = GroupDAO.instance().load(groupName);
			if(grp != null)
				groups.add(grp);
		}
		project.setGroups(groups);
		
		// project grants
		List<Grant> grants = ((EditProjectForm)(form)).getGrantList();
		project.setGrants(grants);
		
		// Save the project
		ProjectDAO.instance().save(project);
		
		// Go!
		ActionForward success = mapping.findForward("Success") ;
		success = new ActionForward( success.getPath() + "?ID=" + project.getID(), success.getRedirect() ) ;
		return success ;
	}
	
}