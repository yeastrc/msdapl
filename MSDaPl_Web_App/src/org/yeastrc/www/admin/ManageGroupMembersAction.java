/* ManageGroupMembersAction.java
 * Created on Mar 23, 2004
 */
package org.yeastrc.www.admin;

import javax.servlet.http.*;
import org.apache.struts.action.*;
import java.util.Collection;

import org.yeastrc.www.user.*;
import org.yeastrc.project.Projects;

/**
 * Add one sentence class summary here.
 * Add class description here
 * 
 * @author Michael Riffle <mriffle@alumni.washington.edu>
 * @version 1.0, Mar 23, 2004
 *
 */
public class ManageGroupMembersAction extends Action {

	public ActionForward execute( ActionMapping mapping,
								  ActionForm form,
								  HttpServletRequest request,
								  HttpServletResponse response )
	throws Exception {

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

		// Get the name of the group they're trying to edit
		String groupName = request.getParameter("groupName");
		if (groupName == null || groupName.equals("")){
			return mapping.findForward("Failure");
		}

		String action = request.getParameter("action");

		// Are we adding someone to a group?
		if (action != null && action.equals("add")) {
			int researcherID = ((ManageGroupMembersForm)form).getResearcherID();
			try {
				groupMan.addToGroup(groupName, researcherID);
			} catch (Exception e) {
				ActionErrors errors = new ActionErrors();
				errors.add("access", new ActionMessage("error.groups.adddberror"));
				saveErrors( request, errors );
			}
		}
		
		// Are we deleting someone from a group
		else if (action != null && action.equals("delete")) {
			int researcherID = 0;
			researcherID = Integer.parseInt(request.getParameter("researcherID"));

			if (researcherID != 0)
				groupMan.removeFromGroup(groupName, researcherID);

		}
		

		// Set up the name of this group in the request for easier access
		request.setAttribute("groupName", groupName);
		
		// Get a list of all of the members of this group
		java.util.List memberList = groupMan.getMembers(groupName);
		request.setAttribute("memberList", memberList);
		
		// Set up a Collection of all the Researchers to use in the form as a pull-down menu for researchers
		Collection researchers = Projects.getAllResearchers();
		request.setAttribute("researchers", researchers);
		
		// Set up an ActionForm for this Action
		ManageGroupMembersForm newForm = new ManageGroupMembersForm();
		newForm.setGroupName(groupName);
		request.setAttribute("manageGroupMembersForm", newForm);
		
		// Kick it to the view page
		return mapping.findForward("Success");

	}
}