/*
 * SaveEditResearcherAction.java
 * Michael Riffle <mriffle@u.washington.edu>
 * Mar 21, 2008
 */
package org.yeastrc.www.project;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.yeastrc.project.Researcher;

import org.yeastrc.www.account.EditInformationForm;
import org.yeastrc.www.user.User;
import org.yeastrc.www.user.UserUtils;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;

/**
 * @author Michael Riffle <mriffle@u.washington.edu>
 * @date Mar 21, 2008
 * Description of class here.
 */
public class SaveEditResearcherAction extends Action {

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

		
		Researcher researcher = new Researcher();
		try {
			researcher.load( ((EditInformationForm)form).getId() );
		} catch (Exception e) {
			
			ActionErrors errors = new ActionErrors();
			errors.add("researcher", new ActionMessage("error.general"));
			saveErrors( request, errors );
			return mapping.findForward("Failure");
		}
		
		
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
			int ee = UserUtils.emailExists(email);
			
			if (ee != -1 && ee != researcher.getID()) {
				ActionErrors errors = new ActionErrors();
				errors.add("username", new ActionMessage("error.edit.researcher.emailtaken"));
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
		ActionForward success = mapping.findForward("Success") ;
		success = new ActionForward( success.getPath() + "?id=" + researcher.getID(), success.getRedirect() ) ;
		return success ;


	}
}
