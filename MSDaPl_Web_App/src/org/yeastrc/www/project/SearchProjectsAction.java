/* SearchProjectsAction.java
 * Created on Apr 6, 2004
 */
package org.yeastrc.www.project;

import java.util.List;
import java.util.StringTokenizer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.yeastrc.project.Project;
import org.yeastrc.project.ProjectsSearcher;
import org.yeastrc.project.Researcher;
import org.yeastrc.www.user.User;
import org.yeastrc.www.user.UserUtils;
/**
 * Add one sentence class summary here.
 * Add class description here
 * 
 * @author Michael Riffle <mriffle@alumni.washington.edu>
 * @version 1.0, Apr 6, 2004
 *
 */
public class SearchProjectsAction extends Action {

	public ActionForward execute( ActionMapping mapping,
								  ActionForm form,
								  HttpServletRequest request,
								  HttpServletResponse response )
	throws Exception {
		String searchString;
		String[] groups;
		
		HttpSession session = request.getSession();
		
		// User making this request
		User user = UserUtils.getUser(request);
		if (user == null) {
			ActionErrors errors = new ActionErrors();
			errors.add("username", new ActionMessage("error.login.notloggedin"));
			saveErrors( request, errors );
			return mapping.findForward("authenticate");
		}

		// The Researcher
		Researcher researcher = user.getResearcher();

		// Get our search parameters
		searchString = ((SearchProjectsForm)(form)).getSearchString();
		groups = ((SearchProjectsForm)(form)).getGroups();
		
		// Get our project search orject
		ProjectsSearcher ps = new ProjectsSearcher();
		
		// Add our search tokens
		if (searchString != null && !searchString.equals("")) {
			StringTokenizer st = new StringTokenizer(searchString);			
			while (st.hasMoreTokens()) {
					 ps.addSearchToken(st.nextToken());
			}
		}

		// Add our groups
		if (groups != null) {
			for (int i = 0; i < groups.length; i++) {
				ps.addGroup(groups[i]);
			}
		}
		
		// Put the access constraint on the search
		ps.setResearcher(researcher);
		
		// Get our list of projects
		List<Project> projects = ps.search();
		
		// Set this list into the request
		session.setAttribute("projectsSearch", projects);
		
		// Set the size of this list into the request
		session.setAttribute("projectsSearchSize", new Integer(projects.size()));

		// Go!
		return mapping.findForward("Success");

	}
}
