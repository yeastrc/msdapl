/*
 * RegisterAction.java
 *
 * Created on October 17, 2003
 *
 * Created by Michael Riffle <mriffle@u.washington.edu>
 *
 */

package org.yeastrc.www.login;

import javax.servlet.http.*;
import org.apache.struts.action.*;

import org.yeastrc.www.user.*;

import com.Ostermiller.util.MD5;


/**
 * Implements the logic to register a user
 */
public class LoginAction extends Action {

	public ActionForward execute( ActionMapping mapping,
								  ActionForm form,
								  HttpServletRequest request,
								  HttpServletResponse response )
	throws Exception {

		// Get their session first.  Disallow them from logging in if they already are
		HttpSession session = request.getSession();
		session.removeAttribute("user");


		/*
		if (session.getAttribute("user") != null) {
			ActionErrors errors = new ActionErrors();
			errors.add("username", new ActionMessage("error.login.alreadyloggedin"));
			saveErrors( request, errors );
			return mapping.findForward("Failure");
		}
		*/
		
		// These items should have already been validated in the ActionForm
		String username = ((LoginForm)form).getUsername();
		String password = ((LoginForm)form).getPassword();

		// Make sure this username exists!		
		User user;
		try {
			user = UserUtils.getUser(username);
		} catch (NoSuchUserException nsue) {
			ActionErrors errors = new ActionErrors();
			errors.add("username", new ActionMessage("error.login.invaliduser"));
			saveErrors( request, errors );
			return mapping.findForward("Failure");
		}
		
		String userPassword = user.getPassword();
		password = MD5.getHashString(password);
		if (!userPassword.equals(password)) {
			// Invalid password
			ActionErrors errors = new ActionErrors();
			errors.add("username", new ActionMessage("error.login.invalidpassword"));
			saveErrors( request, errors );
			return mapping.findForward("Failure");
		}

		// Save the login info in the user.
		user.setLastLoginTime(new java.util.Date());
		user.setLastLoginIP(request.getRemoteAddr());
		user.save();

		// Save this user in their session, and consider them authenticated
		session.setAttribute("user", user);


		// Forward them on to the happy success page!
		return mapping.findForward("Success");
	}
	
}