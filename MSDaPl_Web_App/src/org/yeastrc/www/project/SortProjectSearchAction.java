/* SortProjectSearchAction.java
 * Created on May 12, 2004
 */
package org.yeastrc.www.project;

import javax.servlet.http.*;
import org.apache.struts.action.*;
import java.util.*;

import org.yeastrc.project.*;
import org.yeastrc.www.user.*;

/**
 * Add one sentence class summary here.
 * Add class description here
 * 
 * @author Michael Riffle <mriffle@alumni.washington.edu>
 * @version 1.0, May 12, 2004
 *
 */
public class SortProjectSearchAction extends Action {

	public ActionForward execute( ActionMapping mapping,
								  ActionForm form,
								  HttpServletRequest request,
								  HttpServletResponse response )
	throws Exception {
		String searchString;
		String[] groups;
		String[] types;
		
		HttpSession session = request.getSession();
		
		// User making this request
		User user = UserUtils.getUser(request);
		if (user == null) {
			ActionErrors errors = new ActionErrors();
			errors.add("username", new ActionMessage("error.login.notloggedin"));
			saveErrors( request, errors );
			return mapping.findForward("authenticate");
		}

		// Make sure we have the pre-existing list of projects from their previous search
		List projectsList;
		try {
			projectsList = (List)(session.getAttribute("projectsSearch"));
		} catch (Exception e) {
			return mapping.findForward("Failure");
		}
		
		// Figure out how we're sorting the list
		String sortby = request.getParameter("sortby");
		if (sortby == null || sortby.equals("")) {
			return mapping.findForward("Failure");
		}

		Comparator comp = null;		
		if (sortby.equals("change")) { comp = new ProjectLastChangeComparator(); }
		else if (sortby.equals("pi")) { comp = new ProjectPIComparator(); }
		else if (sortby.equals("title")) { comp = new ProjectTitleComparator(); }
		else if (sortby.equals("submit")) { comp = new ProjectSubmitDateComparator(); }
		else if (sortby.equals("id")) { comp = new ProjectIDComparator(); }
		else {
			return mapping.findForward("Failure");
		}

		// Sort the list appropriately
		Collections.sort(projectsList, comp);
		
		return mapping.findForward("Success");
	}
}
