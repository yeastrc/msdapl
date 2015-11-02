/*
 * RegisterAction.java
 *
 * Created on October 17, 2003
 *
 * Created by Michael Riffle <mriffle@u.washington.edu>
 *
 */

package org.yeastrc.www.login;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

/**
 * Implements the logic to register a user
 */
public class LogoutAction extends Action {

	public ActionForward execute( ActionMapping mapping,
								  ActionForm form,
								  HttpServletRequest request,
								  HttpServletResponse response )
	throws Exception {

		// Get their session first.  Disallow them from logging in if they already are
		HttpSession session = request.getSession();
		session.removeAttribute("user");
		session.removeAttribute("history");
		session.removeAttribute("researchers");
		session.removeAttribute("projectsSearch");
		
		// Terminate the session
		session.invalidate();
		
		// Free up resources
		System.gc();

		// Forward them on to the happy success page!
		return mapping.findForward("Done");
	}
	
}