/*
 * EditResearcherAction.java
 * Michael Riffle <mriffle@u.washington.edu>
 * Mar 21, 2008
 */
package org.yeastrc.www.project;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.yeastrc.project.Researcher;
import org.yeastrc.utils.CountriesBean;
import org.yeastrc.utils.StatesBean;

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
public class EditResearcherAction extends Action {

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

		Researcher researcher = new Researcher();
		try {
			researcher.load( Integer.parseInt( request.getParameter( "id" ) ) );
		} catch (Exception e) {
			
			ActionErrors errors = new ActionErrors();
			errors.add("researcher", new ActionMessage("error.general"));
			saveErrors( request, errors );
			return mapping.findForward("Failure");
		}
		
		
		// Create our ActionForm
		EditInformationForm newForm = new EditInformationForm();
		request.setAttribute("editInformationForm", newForm);
		
		newForm.setFirstName(researcher.getFirstName());
		newForm.setLastName(researcher.getLastName());
		newForm.setEmail(researcher.getEmail());
		newForm.setDegree(researcher.getDegree());
		newForm.setDepartment(researcher.getDepartment());
		newForm.setOrganization(researcher.getOrganization());
		newForm.setState(researcher.getState());
		newForm.setZipCode(researcher.getZipCode());
		newForm.setCountry(researcher.getCountry());
		newForm.setId( researcher.getID() );

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
