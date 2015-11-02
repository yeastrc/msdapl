/*
 * RegisterAction.java
 *
 * Created on October 17, 2003
 *
 * Created by Michael Riffle <mriffle@u.washington.edu>
 *
 */

package org.yeastrc.register;

import javax.servlet.http.*;
import org.apache.struts.action.*;

import org.yeastrc.www.user.*;
import org.yeastrc.project.Researcher;

/**
 * Implements the logic to register a user
 */
public class RegisterAction extends Action {

	public ActionForward execute( ActionMapping mapping,
								  ActionForm form,
								  HttpServletRequest request,
								  HttpServletResponse response )
	throws Exception {
		
		// These items should have already been validated in the ActionForm
		String username = ((RegisterForm)form).getUsername();
		String password = ((RegisterForm)form).getPassword();
		String firstName = ((RegisterForm)form).getFirstName();
		String lastName = ((RegisterForm)form).getLastName();
		String email = ((RegisterForm)form).getEmail();
		String degree = ((RegisterForm)form).getDegree();
		String organization = ((RegisterForm)form).getOrganization();
		String department = ((RegisterForm)form).getDepartment();
		String state = ((RegisterForm)form).getState();
		String zip = ((RegisterForm)form).getZip();
		String country = ((RegisterForm)form).getCountry();

		// Make sure this username doesn't already exist!
		if (UserUtils.userExists(username)) {
			ActionErrors errors = new ActionErrors();
			errors.add("username", new ActionMessage("error.register.usertaken"));
			saveErrors( request, errors );
			return mapping.findForward("Failure");
		}

		// Make sure this email doesn't already exist!
		if (UserUtils.emailExists(email) != -1) {
			ActionErrors errors = new ActionErrors();
			errors.add("username", new ActionMessage("error.register.emailtaken"));
			saveErrors( request, errors );
			return mapping.findForward("Failure");
		}
	
		// Create a new Researcher with this information
		Researcher researcher = new Researcher();
		researcher.setFirstName(firstName);
		researcher.setLastName(lastName);
		researcher.setEmail(email);
		researcher.setDegree(degree);
		researcher.setOrganization(organization);
		researcher.setDepartment(department);
		researcher.setState(state);
		researcher.setZipCode(zip);
		researcher.setCountry(country);
		
		// Save this new researcher (any problems will toss an exception)
		researcher.save();
		
		// Create a new User with this information
		User user = new User();
		user.setUsername(username);
		user.setPassword(password);
		user.setLastLoginTime(new java.util.Date());
		user.setLastLoginIP(request.getRemoteAddr());
		user.setResearcher(researcher);
		
		// Save the User
		user.save();

		// Save this user in their session, and consider them authenticated
		HttpSession session = request.getSession();
		session.removeAttribute("user");
		session.setAttribute("user", user);
		
		// Forward them on to the happy success page!
		return mapping.findForward("Success");
	}
	
}