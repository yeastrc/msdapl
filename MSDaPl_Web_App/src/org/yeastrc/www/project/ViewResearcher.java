/*
 * ViewResearcher.java
 * Created on Jan 10, 2006
 * Created by Michael Riffle <mriffle@u.washington.edu>
 */

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
import org.yeastrc.project.Projects;
import org.yeastrc.project.Researcher;
import org.yeastrc.www.user.User;
import org.yeastrc.www.user.UserUtils;
import org.yeastrc.www.user.*;
import java.util.*;
import org.yeastrc.project.Project;
/**
 * Description of class goes here.
 * 
 * @author Michael Riffle <mriffle@u.washington.edu>
 * @version Jan 10, 2006
 */

public class ViewResearcher extends Action {

	public ActionForward execute( ActionMapping mapping,
								  ActionForm form,
								  HttpServletRequest request,
								  HttpServletResponse response )
	throws Exception {
		
		// User making this request
		User user = UserUtils.getUser(request);
		if (user == null) {
			ActionErrors errors = new ActionErrors();
			errors.add("username", new ActionMessage("error.login.notloggedin"));
			saveErrors( request, errors );
			return mapping.findForward("authenticate");
		}
		
		/*
		// Restrict access to administrators
		Groups groupMan = Groups.getInstance();
		if (!groupMan.isMember(user.getResearcher().getID(), "administrators")) {
			ActionErrors errors = new ActionErrors();
			errors.add("access", new ActionMessage("error.access.invalidgroup"));
			saveErrors( request, errors );
			return mapping.findForward("Failure");
		}
		*/
		
		Researcher researcher = new Researcher();
		List<Project> projects = new ArrayList<Project>();
		
		try {
			researcher.load( Integer.parseInt( request.getParameter( "id" ) ) );
		} catch (Exception e) {
			
			ActionErrors errors = new ActionErrors();
			errors.add("researcher", new ActionMessage("error.general"));
			saveErrors( request, errors );
			return mapping.findForward("Failure");
			
		}
			
		request.setAttribute( "researcher", researcher );
		
		if (user.getResearcher().equals( researcher ) || Groups.getInstance().isMember( user.getResearcher().getID(), "administrators")) {
			request.setAttribute( "mayEdit", new Boolean( true ) );
		} else {
			request.setAttribute( "mayEdit", new Boolean( false ) );
		}
		
		projects = Projects.getProjectsByResearcher( Integer.parseInt( request.getParameter( "id" ) ) );
		Iterator<Project> iter = projects.iterator();
		while (iter.hasNext()) {
			Project p = iter.next();
			if (!p.checkReadAccess( user.getResearcher() ))
				iter.remove();
		}
		
		request.setAttribute( "projects", projects );
		
		return mapping.findForward( "Success" );
	}

}
