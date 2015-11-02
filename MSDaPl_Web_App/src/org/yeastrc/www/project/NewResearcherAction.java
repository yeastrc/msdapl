/*
 * Created by Michael Riffle <mriffle@u.washington.edu>
 *
 */

package org.yeastrc.www.project;

import javax.servlet.http.*;
import org.apache.struts.action.*;

import org.yeastrc.project.*;
import org.yeastrc.www.user.*;
import org.yeastrc.utils.*;

/**
 * Controller class for editing a project.
 */
public class NewResearcherAction extends Action {

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

		// The Researcher we're editing (is the one logged in)
		Researcher researcher = user.getResearcher();

		
		// Create our ActionForm
		EditResearcherForm newForm = new EditResearcherForm();
		request.setAttribute("editResearcherForm", newForm);
		
		newForm.setDepartment(researcher.getDepartment());
		newForm.setOrganization(researcher.getOrganization());
		newForm.setState(researcher.getState());
		newForm.setZipCode(researcher.getZipCode());
		newForm.setCountry(researcher.getCountry());

		// Save our states bean
		StatesBean sb = StatesBean.getInstance();
		session.setAttribute("states", sb.getStates());

		// Save our countries bean
		CountriesBean cb = CountriesBean.getInstance();
		session.setAttribute("countries", cb.getCountries());

		// Go!
		return mapping.findForward("Success");

	}
	
}