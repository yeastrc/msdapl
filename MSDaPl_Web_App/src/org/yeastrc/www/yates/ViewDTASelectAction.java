/*
 * ViewDTASelectAction.java
 * Created on Sep 14, 2004
 * Created by Michael Riffle <mriffle@u.washington.edu>
 */

package org.yeastrc.www.yates;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.yeastrc.data.InvalidIDException;
import org.yeastrc.project.Project;
import org.yeastrc.www.user.User;
import org.yeastrc.www.user.UserUtils;
import org.yeastrc.yates.YatesRun;

/**
 * Description of class goes here.
 * 
 * @author Michael Riffle <mriffle@u.washington.edu>
 * @version Sep 14, 2004
 */

public class ViewDTASelectAction extends Action {

	public ActionForward execute( ActionMapping mapping,
								  ActionForm form,
								  HttpServletRequest request,
								  HttpServletResponse response )
	throws Exception {

		// The run we're viewing
		int runID;
		
		// User making this request
		User user = UserUtils.getUser(request);
		if (user == null) {
			ActionErrors errors = new ActionErrors();
			errors.add("username", new ActionMessage("error.login.notloggedin"));
			saveErrors( request, errors );
			return mapping.findForward("authenticate");
		}


		YatesRun yr = null;
		try {
			String strID = request.getParameter("id");

			if (strID == null || strID.equals("")) {
				ActionErrors errors = new ActionErrors();
				errors.add("yates", new ActionMessage("error.yates.invalidid"));
				saveErrors( request, errors );
				return mapping.findForward("Failure");
			}

			runID = Integer.parseInt(strID);

			// Load our screen
			yr = new YatesRun();
			yr.load(runID);
		

			Project project = yr.getProject();
			if (!project.checkReadAccess(user.getResearcher())) {
				
				// This user doesn't have access to this project.
				ActionErrors errors = new ActionErrors();
				errors.add("username", new ActionMessage("error.project.noaccess"));
				saveErrors( request, errors );
				return mapping.findForward("Failure");
			}
		} catch (NumberFormatException nfe) {
			ActionErrors errors = new ActionErrors();
			errors.add("yates", new ActionMessage("error.project.invalidid"));
			saveErrors( request, errors );
			return mapping.findForward("Failure");
		} catch (InvalidIDException iie) {
			ActionErrors errors = new ActionErrors();
			errors.add("yates", new ActionMessage("error.yates.invalidid"));
			saveErrors( request, errors );
			return mapping.findForward("Failure");
		} 
		
		catch (Exception e) {
			ActionErrors errors = new ActionErrors();
			errors.add("username", new ActionMessage("error.project.projectnotfound"));
			saveErrors( request, errors );
			return mapping.findForward("Failure");	
		}

		// Set this project in the request, as a bean to be displayed on the view
		request.setAttribute("run", yr);
		
		return mapping.findForward("Success");
	}

}