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
import org.yeastrc.grant.Grant;
import org.yeastrc.grant.GrantDAO;
import org.yeastrc.grant.GrantSorter;
import org.yeastrc.www.user.User;
import org.yeastrc.www.user.UserUtils;

public class ViewGrantsAction extends Action {

	public ActionForward execute( ActionMapping mapping,
			  ActionForm form,
			  HttpServletRequest request,
			  HttpServletResponse response ) throws InvalidIDException, SQLException {
		
		// User making this request
		User user = UserUtils.getUser(request);
		if (user == null) {
			ActionErrors errors = new ActionErrors();
			errors.add("username", new ActionMessage("error.login.notloggedin"));
			saveErrors( request, errors );
			return mapping.findForward("authenticate");
		}

		String piIDStr = request.getParameter("PIs");
		List<Integer> piIdList = piIDs(piIDStr);
		
		// get all the grants associated with this user and given PI
		List<Grant> grants = GrantDAO.getInstance().getGrantForUserAndPIs(user, piIdList);
		if (request.getParameter("sortby") != null) {
			sortGrantsBy(grants, request.getParameter("sortby"));
		}
		
		// if a list of selected grant IDs was sent with the request, grab them
		List <Integer> grantIDs = new ArrayList<Integer>();
		String selGrantParam = request.getParameter("selectedGrants");
		if (selGrantParam != null && selGrantParam.length() > 0) {
			String[] selectedGrants = request.getParameter("selectedGrants").split(",");
			for (String idStr: selectedGrants) {
				grantIDs.add(Integer.parseInt(idStr));
			}
		}
		// select the given grants
		selectGrants(grants, grantIDs);
		
		// remove this just in case it is hanging around (from EditGrantAction)
		request.getSession().removeAttribute("selectedGrants");
		request.getSession().removeAttribute("PIs");
		
		request.setAttribute("grants", grants);
		
		return mapping.findForward("Success");
		
	}
	
	private void selectGrants(List<Grant> grants, List<Integer> selectedGrants) {
		for (Grant grant: grants) {
			if (selectedGrants.contains(grant.getID()))
				grant.setSelected(true);
		}
	}
	
	private void sortGrantsBy(List<Grant> grants, String sortBy) {
		if (sortBy.equalsIgnoreCase("title")) 
			GrantSorter.getInstance().sortByTitle(grants);
		
		else if (sortBy.equalsIgnoreCase("pi"))
			GrantSorter.getInstance().sortByPI(grants);
		
		else if (sortBy.equalsIgnoreCase("sourceType"))
			GrantSorter.getInstance().sortBySourceType(grants);
		
		else if (sortBy.equalsIgnoreCase("sourceName"))
			GrantSorter.getInstance().sortBySourceName(grants);
		
		else if (sortBy.equalsIgnoreCase("grantNum"))
			GrantSorter.getInstance().sortByGrantNumber(grants);
		
		else if (sortBy.equalsIgnoreCase("grantAmount"))
			GrantSorter.getInstance().sortByGrantAmount(grants);
		
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
}
