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

import javax.servlet.http.*;
import org.apache.struts.action.*;

import org.yeastrc.project.*;
import org.yeastrc.www.user.*;
import org.yeastrc.data.*;
import org.yeastrc.grant.Grant;
import org.yeastrc.grant.ProjectGrantDAO;

/**
 * Controller class for saving a project.
 */
public class SaveProjectAction extends Action {

	public ActionForward execute( ActionMapping mapping,
								  ActionForm form,
								  HttpServletRequest request,
								  HttpServletResponse response )
	throws Exception {
		int projectID;			// Get the projectID they're after
		
		// The form elements we're after
		String title = null;
		int pi = 0;
		List<Researcher> researchers;
		List<Grant> grants;
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


		try {
			String strID = request.getParameter("ID");

			if (strID == null || strID.equals("")) {
				ActionErrors errors = new ActionErrors();
				errors.add("username", new ActionMessage("error.project.noprojectid"));
				saveErrors( request, errors );
				return mapping.findForward("Failure");
			}

			projectID = Integer.parseInt(strID);

		} catch (NumberFormatException nfe) {
			ActionErrors errors = new ActionErrors();
			errors.add("username", new ActionMessage("error.project.invalidprojectid"));
			saveErrors( request, errors );
			return mapping.findForward("Failure");
		}

		// Load our project
		Project project;
		
		try {
			project = ProjectFactory.getProject(projectID);
			if (!project.checkAccess(user.getResearcher())) {
				
				// This user doesn't have access to this project.
				ActionErrors errors = new ActionErrors();
				errors.add("username", new ActionMessage("error.project.noaccess"));
				saveErrors( request, errors );
				return mapping.findForward("Failure");
			}
		} catch (Exception e) {
			
			// Couldn't load the project.
			ActionErrors errors = new ActionErrors();
			errors.add("username", new ActionMessage("error.project.projectnotfound"));
			saveErrors( request, errors );
			return mapping.findForward("Failure");	
		}

		// Set this project in the request, as a bean to be displayed on the view
		//request.setAttribute("project", project);

		// We're saving!
		title = ((EditProjectForm)(form)).getTitle();
		pi = ((EditProjectForm)(form)).getPI();
		researchers = ((EditProjectForm)(form)).getResearcherList();
		grants = ((EditProjectForm)(form)).getGrantList();
		projectAbstract = ((EditProjectForm)(form)).getAbstract();
		progress = ((EditProjectForm)(form)).getProgress();
		publications = ((EditProjectForm)(form)).getPublications();
		comments = ((EditProjectForm)(form)).getComments();
		affiliation = ((EditProjectForm)(form)).getAffiliation();
		
		// Set blank items to null
		if (title.equals("")) title = null;
		if (projectAbstract.equals("")) projectAbstract = null;
		if (progress.equals("")) progress = null;
		if (publications.equals("")) publications = null;
		if (comments.equals("")) comments = null;
		if(researchers == null) {
		    researchers = new ArrayList<Researcher>(0);
		}
		if(grants == null) {
		    grants = new ArrayList<Grant>(0);
		}
		
		// Set up our researchers
		try {
			if (pi != 0) {
				Researcher piR = new Researcher();
				piR.load(pi);
				project.setPI(piR);
			}			
			for(Researcher r: researchers) {
			    if(r == null || r.getID() <= 0)
			        continue;
			    r.load(r.getID());
			}
			project.setResearchers(researchers);
		} catch (InvalidIDException iie) {

			// Couldn't load the researcher.
			ActionErrors errors = new ActionErrors();
			errors.add("project", new ActionMessage("error.project.invalidresearcher"));
			saveErrors( request, errors );
			return mapping.findForward("Failure");
		}
		
		

//		// Set up the groups
//		project.clearGroups();
//		if (groups != null) {
//			if (groups.length > 0) {
//				for (int i = 0; i < groups.length; i++) {
//					try { project.setGroup(groups[i]); }
//					catch (InvalidIDException iie) {
//					
//						// Somehow got an invalid group...
//						ActionErrors errors = new ActionErrors();
//						errors.add("project", new ActionMessage("error.project.invalidgroup"));
//						saveErrors( request, errors );
//						return mapping.findForward("Failure");					
//					}
//				}
//			}
//		}

		// Set all of the new values in the project
		project.setTitle(title);
		project.setAbstract(projectAbstract);
		project.setProgress(progress);
		project.setPublications(publications);
		project.setComments(comments);
		project.setGrants(grants);
		project.setAffiliation(affiliation);
		
		// Save the project
		ProjectDAO.instance().save(project);

		// Go!
		return mapping.findForward("viewProject");

	}
	
}