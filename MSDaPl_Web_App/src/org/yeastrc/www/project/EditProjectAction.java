/*
 *
 * Created on February 5, 2004
 *
 * Created by Michael Riffle <mriffle@u.washington.edu>
 *
 */

package org.yeastrc.www.project;

import java.util.Collection;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.yeastrc.project.Affiliation;
import org.yeastrc.project.Project;
import org.yeastrc.project.ProjectFactory;
import org.yeastrc.project.Projects;
import org.yeastrc.project.Researcher;
import org.yeastrc.www.user.User;
import org.yeastrc.www.user.UserUtils;

/**
 * Controller class for editing a project.
 */
public class EditProjectAction extends Action {

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
		} 
		catch (Exception e) {
			
			// Couldn't load the project.
			ActionErrors errors = new ActionErrors();
			errors.add("project", new ActionMessage("error.project.projectnotfound"));
			saveErrors( request, errors );
			return mapping.findForward("Failure");	
		}


		request.setAttribute("project", project);

		EditProjectForm newForm = new EditProjectForm();			
		request.setAttribute("editProjectForm", newForm);

		newForm.setGrantList(project.getGrants());

		// Set the parameters available to all project types
		newForm.setTitle(project.getTitle());
		newForm.setAbstract(project.getAbstract());
		newForm.setProgress(project.getProgress());
		newForm.setComments(project.getComments());
		newForm.setPublications(project.getPublications());
		

		newForm.setID(project.getID());
		newForm.setSubmitDate(project.getSubmitDate());
		
		newForm.setAffiliation(project.getAffiliation());
		
		// Set the Researchers
		Researcher res = project.getPI();
		if (res != null) newForm.setPI(res.getID());
		
		newForm.setResearcherList(project.getResearchers());

		// Set up a Collection of all the Researchers to use in the form as a pull-down menu for researchers
		Collection<Researcher> researchers = Projects.getAllResearchers();
		request.getSession().setAttribute("researchers", researchers);


		List<Affiliation> affiliationTypes = Affiliation.getList();
        request.getSession().setAttribute("affiliationTypes", affiliationTypes);
        
		// Go!
		return mapping.findForward("Success");

	}
	
}