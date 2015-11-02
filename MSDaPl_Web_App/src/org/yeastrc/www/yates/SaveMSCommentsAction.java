/*
 * SaveMSCommentsAction.java
 * Created on Jul 13, 2006
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
import org.yeastrc.project.Project;
import org.yeastrc.www.user.User;
import org.yeastrc.www.user.UserUtils;
import org.yeastrc.yates.YatesRun;

/**
 * Description of class goes here.
 * 
 * @author Michael Riffle <mriffle@u.washington.edu>
 * @version Jul 13, 2006
 */

public class SaveMSCommentsAction extends Action {

	public ActionForward execute( ActionMapping mapping,
								  ActionForm form,
								  HttpServletRequest request,
								  HttpServletResponse response )
	throws Exception {
	
		// The run we're viewing
		int runID = ((MSCommentsForm)form).getId();
		String comments = ((MSCommentsForm)form).getComments();
		
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

			// Load our screen
			yr = new YatesRun();
			yr.load(runID);

			Project project = yr.getProject();
			if (!project.checkReadAccess(user.getResearcher()))
				throw new Exception( "No access." );

			yr.setComments( comments );
			yr.save();
			
		} catch (Exception e) { ; }		
		
		// Kick it to the view page
		ActionForward success = mapping.findForward( "Success" ) ;
		success = new ActionForward( success.getPath() + "?id=" + yr.getId(), success.getRedirect() ) ;
		return success ;
		
	}
}
