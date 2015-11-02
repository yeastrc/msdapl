package org.yeastrc.www.project;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.yeastrc.data.InvalidIDException;
import org.yeastrc.grant.FundingSourceType;
import org.yeastrc.grant.Grant;
import org.yeastrc.grant.GrantDAO;
import org.yeastrc.grant.FundingSourceName;
import org.yeastrc.project.Projects;
import org.yeastrc.project.Researcher;
import org.yeastrc.www.user.User;
import org.yeastrc.www.user.UserUtils;

public class EditGrantAction extends Action {

	public ActionForward execute( ActionMapping mapping,
			  ActionForm form,
			  HttpServletRequest request,
			  HttpServletResponse response ) throws SQLException, InvalidIDException {
		
		// User making this request
		User user = UserUtils.getUser(request);
		if (user == null) {
			ActionErrors errors = new ActionErrors();
			errors.add("username", new ActionMessage("error.login.notloggedin"));
			saveErrors( request, errors );
			return mapping.findForward("authenticate");
		}
		
		// get the funding source types
		List<FundingSourceType> sources = FundingSourceType.getFundingSources();
		request.getSession().setAttribute("sourceTypes", sources);
		
		// get the federal funding agency names
		List<FundingSourceName> federalSources = FundingSourceType.FEDERAL.getAcceptedSourceNames();
		request.getSession().setAttribute("federalSources", federalSources);
		
		// Set up a Collection of all the Researchers to use in the form as a pull-down menu for researchers
		List <Researcher> researchers = Projects.getAllResearchers(); // this is already sorted by last name!
		request.getSession().setAttribute("researchers", researchers);
		
		// user wants to edit an existing grant
		if (request.getParameter("grantID") != null) {
			return editGrantForm(mapping, request);
		}
		// user wants to create a new grant
		else {
			return newGrantForm(mapping, request, user);
		}
	}

	private ActionForward newGrantForm(ActionMapping mapping,
			HttpServletRequest request, User user) {
		// if PI IDs were sent with the request, use the first one in the list in the form
		List<Integer> piIDs = piIDs(request.getParameter("PIs"));
		
		int PI = 0;
		
		// if no PI IDs were found in the request, use the ID of the user
		if (piIDs.size() == 0) {
			PI = user.getResearcher().getID();
			piIDs.add(PI);
		}
		else
			PI = piIDs.get(0);
		
		// put the PIs in the session for future use
		request.getSession().setAttribute("PIs", commaSeparated(piIDs));
		
		// if some selected grant IDs were sent with this request save them in the session for future use
		if (request.getParameter("selectedGrants") != null) {
			String selectedGrants = request.getParameter("selectedGrants");
			// save in session for future use
			request.getSession().setAttribute("selectedGrants", selectedGrants);
		}
		// Create a new form and set the selected PI
		EditGrantForm grantForm = new EditGrantForm();
		grantForm.setPI(PI);
		request.setAttribute("editGrantForm", grantForm);
		
		return mapping.findForward("Success");
	}

	private ActionForward editGrantForm(ActionMapping mapping,
			HttpServletRequest request) throws SQLException, InvalidIDException {
		
		int grantID = 0;
		try {
			grantID = Integer.parseInt(request.getParameter("grantID"));
		}
		catch (NumberFormatException e) {}
		
		if (grantID == 0) {
			ActionErrors errors = new ActionErrors();
			errors.add("grant", new ActionMessage("error.grant.invalidid"));
			saveErrors( request, errors );
			return mapping.findForward("Failure");
		}
		
		// get the grant for the given ID;
		Grant grant = GrantDAO.getInstance().load(grantID);
		if (grant == null) {
			ActionErrors errors = new ActionErrors();
			errors.add("grant", new ActionMessage("error.grant.notfound"));
			saveErrors( request, errors );
			return mapping.findForward("Failure");
		}
		
		// Create a new form and set the values from the grant
		EditGrantForm grantForm = new EditGrantForm();
		grantForm.setGrantID(grant.getID());
		grantForm.setGrantTitle(grant.getTitle());
		grantForm.setPI(grant.getGrantPI().getID());
		FundingSourceType sourceType = grant.getFundingSource().getSourceType();
		grantForm.setFundingType(sourceType.getName());
		FundingSourceName sourceName = grant.getFundingSource().getSourceName();
		if (grant.getFundingSource().isFederal()) {
			grantForm.setFedFundingAgencyName(sourceName.getName());
		}
		else {
			grantForm.setFundingAgencyName(sourceName.getDisplayName());
		}
		grantForm.setGrantNumber(grant.getGrantNumber());
		grantForm.setGrantAmount(grant.getGrantAmount());
		
		request.setAttribute("editGrantForm", grantForm);
		
		return mapping.findForward("Success");
	}
	
	private List <Integer> piIDs(String piIDStr) {
		if (piIDStr == null || piIDStr.length() == 0)
			return new ArrayList<Integer>(0);
		String[] tokens = piIDStr.split(",");
		List <Integer> ids = new ArrayList<Integer>(tokens.length);
		for (String tok: tokens) {
			try {
				int id = Integer.parseInt(tok);
				ids.add(id);
			}
			catch (NumberFormatException e){}
		}
		return ids;
	}
	
	private String commaSeparated(List <Integer> piIDs) {
		StringBuilder buf = new StringBuilder();
		for (Integer id: piIDs) {
			buf.append(",");
			buf.append(id);
		}
		if (buf.length() > 0)
			buf.deleteCharAt(0); // remove first comma
		return buf.toString();
	}
}
