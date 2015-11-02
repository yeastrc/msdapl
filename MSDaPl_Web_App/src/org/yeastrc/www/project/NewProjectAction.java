/*
 * Created by Michael Riffle <mriffle@u.washington.edu>
 *
 */

package org.yeastrc.www.project;

import java.util.*;
import javax.servlet.http.*;
import org.apache.struts.action.*;

import org.yeastrc.project.*;
import org.yeastrc.www.user.*;

/**
 * Controller class for creating a new Collaboration.
 */
public class NewProjectAction extends Action {

	public ActionForward execute( ActionMapping mapping,
								  ActionForm form,
								  HttpServletRequest request,
								  HttpServletResponse response )
	throws Exception {
		HttpSession session = request.getSession();
		
		// User making this request
		User user = UserUtils.getUser(request);
		if (user == null) {
			ActionErrors errors = new ActionErrors();
			errors.add("username", new ActionMessage("error.login.notloggedin"));
			saveErrors( request, errors );
			return mapping.findForward("authenticate");
		}

		
		// Make sure this user is in a group
		Groups groupMan = Groups.getInstance();
		if (!groupMan.isInAGroup(user.getResearcher().getID())) {
            ActionErrors errors = new ActionErrors();
            errors.add("access", new ActionMessage("error.access.invalidgroup"));
            saveErrors( request, errors );
            return mapping.findForward("Failure");
        }
		
		// The Researcher
		Researcher researcher = user.getResearcher();

		
		// Create our ActionForm
		EditProjectForm newForm = new EditProjectForm();
		request.setAttribute("editProjectForm", newForm);
		
		// Set the default PI.
		List<String> userGroups = Groups.getInstance().getUserGroups(user.getResearcher().getID());
		int piId = LabDirector.get(userGroups);
		if(piId != 0)
			newForm.setPI(piId);
		
		List<Researcher> researcherIds = new ArrayList<Researcher>();
		Researcher tempR = new Researcher();
		tempR = new Researcher();
		tempR.load(researcher.getID());
		researcherIds.add(tempR);
		newForm.setResearcherList(researcherIds);
		

		// Set up a Collection of all the Researchers to use in the form as a pull-down menu for researchers
		Collection researchers = Projects.getAllResearchers();
		session.setAttribute("researchers", researchers);

		List<Affiliation> affiliationTypes = Affiliation.getList();
        request.getSession().setAttribute("affiliationTypes", affiliationTypes);
        
        
		// remove any project, if it exists in the session.  Could have been placed in the session by EditProjectAction
        request.getSession().removeAttribute("project");
        
		// Go!
		return mapping.findForward("Success");

	}
	
}