/*
 * Created by Michael Riffle <mriffle@u.washington.edu>
 *
 */

package org.yeastrc.www.account;

import javax.servlet.http.*;
import org.apache.struts.action.*;

import org.yeastrc.project.*;
import org.yeastrc.www.user.*;

/**
 * Controller class for editing a project.
 */
public class SaveInformationAction extends Action {

	public ActionForward execute( ActionMapping mapping,
								  ActionForm form,
								  HttpServletRequest request,
								  HttpServletResponse response )
	throws Exception {

		String firstName;
		String lastName;
		String email;
		String degree;
		String department;
		String organization;
		String state;
		String zip;
		String country;
		
		// User making this request
		User user = UserUtils.getUser(request);
		if (user == null) {
			ActionErrors errors = new ActionErrors();
			errors.add("username", new ActionMessage("error.login.notloggedin"));
			saveErrors( request, errors );
			return mapping.findForward("authenticate");
		}

		
		// The Researcher we're editing (is the one logged in)
		Researcher researcher = user.getResearcher();

		
		// Set our variables from the form
		firstName = ((EditInformationForm)form).getFirstName();
		lastName = ((EditInformationForm)form).getLastName();
		email = ((EditInformationForm)form).getEmail();
		degree = ((EditInformationForm)form).getDegree();
		department = ((EditInformationForm)form).getDepartment();
		organization = ((EditInformationForm)form).getOrganization();
		state = ((EditInformationForm)form).getState();
		zip = ((EditInformationForm)form).getZipCode();
		country = ((EditInformationForm)form).getCountry();
		
		// Set any empty variables to null
		// Only possible empty value is zip code
		if (zip.equals("")) { zip = null; }
		
		// If they're changing their email addy, make sure the NEW one isn't already in the database
		String pEmail = researcher.getEmail();
		if (!pEmail.equals(email)) {
			if (UserUtils.emailExists(email) != -1) {
				ActionErrors errors = new ActionErrors();
				errors.add("username", new ActionMessage("error.register.emailtaken"));
				saveErrors( request, errors );
				return mapping.findForward("Failure");
			}
		}
		
		// Now set the values in the researcher object
		researcher.setFirstName(firstName);
		researcher.setLastName(lastName);
		researcher.setEmail(email);
		researcher.setDegree(degree);
		researcher.setDepartment(department);
		researcher.setOrganization(organization);
		researcher.setState(state);
		researcher.setZipCode(zip);
		researcher.setCountry(country);
		
		// Save the researcher to the database
		researcher.save();
		request.setAttribute("saved", "true");

		// Go!
		return mapping.findForward("Success");


	}
	
}