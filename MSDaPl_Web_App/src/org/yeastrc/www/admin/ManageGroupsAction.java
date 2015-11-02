/* ManageGroupsAction.java
 * Created on Mar 23, 2004
 */
package org.yeastrc.www.admin;

import javax.servlet.http.*;
import org.apache.struts.action.*;

import org.yeastrc.www.user.*;

/**
 * Add one sentence class summary here.
 * Add class description here
 * 
 * @author Michael Riffle <mriffle@alumni.washington.edu>
 * @version 1.0, Mar 23, 2004
 *
 */
public class ManageGroupsAction extends Action {

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

		// Set up the list of groups in the request
		java.util.List groupList = groupMan.getGroups();
		request.setAttribute("groupList", groupList);
		
		// Kick it to the view page
		return mapping.findForward("Success");

	}
}