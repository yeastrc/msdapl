/*
 * Created by Michael Riffle <mriffle@u.washington.edu>
 *
 */

package org.yeastrc.www.account;

import javax.servlet.http.*;
import org.apache.struts.action.*;

import org.yeastrc.www.user.*;

/**
 * Controller class for editing a project.
 */
public class SaveUsernameAction extends Action {

	public ActionForward execute( ActionMapping mapping,
								  ActionForm form,
								  HttpServletRequest request,
								  HttpServletResponse response )
	throws Exception {

		String username;
		
		// User making this request
		User user = UserUtils.getUser(request);
		if (user == null) {
			ActionErrors errors = new ActionErrors();
			errors.add("username", new ActionMessage("error.login.notloggedin"));
			saveErrors( request, errors );
			return mapping.findForward("authenticate");
		}
		
		// Set our variables from the form
		username = ((EditUsernameForm)form).getUsername();
		
		// Make sure their new username doesn't already exist in the database
		String pUsername = user.getUsername();
		if (!pUsername.equals(username)) {
			if (UserUtils.userExists(username)) {
				ActionErrors errors = new ActionErrors();
				errors.add("username", new ActionMessage("error.register.taken"));
				saveErrors( request, errors );
				return mapping.findForward("Failure");
			}
		}
		
		// Now set the values in the user object
		user.setUsername(username);
		
		// Save the user to the database
		user.save();

		request.setAttribute("saved", "true");

		// Go!
		return mapping.findForward("Success");


	}
	
}