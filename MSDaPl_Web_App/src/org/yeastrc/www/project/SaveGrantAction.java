package org.yeastrc.www.project;

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
import org.yeastrc.grant.FundingSource;
import org.yeastrc.grant.Grant;
import org.yeastrc.grant.GrantDAO;
import org.yeastrc.project.Researcher;
import org.yeastrc.www.user.User;
import org.yeastrc.www.user.UserUtils;

public class SaveGrantAction extends Action {

	public ActionForward execute( ActionMapping mapping,
			  ActionForm form,
			  HttpServletRequest request,
			  HttpServletResponse response ) throws Exception {
		
		// User making this request
		User user = UserUtils.getUser(request);
		if (user == null) {
			ActionErrors errors = new ActionErrors();
			errors.add("username", new ActionMessage("error.login.notloggedin"));
			saveErrors( request, errors );
			return mapping.findForward("authenticate");
		}
		
		EditGrantForm grantForm = (EditGrantForm) form;
		Grant grant = new Grant();
		grant.setID(grantForm.getGrantID());
		grant.setTitle(grantForm.getGrantTitle());
		Researcher PI = new Researcher();
		PI.load(grantForm.getPI());
		grant.setGrantPI(PI);
		grant.setFundingSource(FundingSource.getFundingSource(grantForm.getFundingType(), grantForm.getFundingSourceName()));
		grant.setGrantNumber(grantForm.getGrantNumber());
		grant.setGrantAmount(grantForm.getGrantAmount());
		
		// save the grant in the database
		GrantDAO.getInstance().save(grant);
		
		//if the grant id in the form is 0, it means this is a new grant.
		if (grantForm.getGrantID() <= 0) {
			
			List <Integer> piIDs = piIDs((String)request.getSession().getAttribute("PIs"));
			if (!piIDs.contains(PI.getID()))
				piIDs.add(PI.getID());
			
			String path = mapping.findForward("ListGrants").getPath()+"?PIs="+commaSeparated(piIDs);
			
			String selectedGrants = (String)request.getSession().getAttribute("selectedGrants");
			if (selectedGrants == null)	selectedGrants = "";
			else 						selectedGrants += ",";
			selectedGrants += grant.getID();
			path += "&selectedGrants="+selectedGrants;
			
//			System.out.println("Forwarding to: "+path);
			
			return new ActionForward(path);
		}
		// the user is editing an existing grant.
		else {
			request.setAttribute("grant", grant);
			return mapping.findForward("SavedGrant");
		}
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
