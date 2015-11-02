/**
 * 
 */
package org.yeastrc.www.compare;

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
import org.yeastrc.project.Project;
import org.yeastrc.project.ProjectsSearcher;
import org.yeastrc.www.taglib.HistoryTag;
import org.yeastrc.www.user.History;
import org.yeastrc.www.user.User;
import org.yeastrc.www.user.UserUtils;

/**
 * DatasetProjectSelectionFormAction.java
 * @author Vagisha Sharma
 * Jun 23, 2010
 * 
 */
public class DatasetProjectSelectionFormAction extends Action {

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

        
        int projectId = 0;
        try {
        	if(request.getParameter("projectId") != null) {
        		projectId = Integer.parseInt(request.getParameter("projectId"));
        	}
        }
        catch(NumberFormatException e) {}
        	

        // Get a list of the user's projects (all projects to which user has READ access)
        // if the user is an admin get ALL projects
        ProjectsSearcher projSearcher = new ProjectsSearcher();
        projSearcher.setResearcher(user.getResearcher());
        List<Project> allReadableProjects = projSearcher.search();
        
        List<Project> userProjects = new ArrayList<Project>();
        for(Project project: allReadableProjects) {
            
        	// Add only projects on which user is listed as a researcher
            if(!project.checkAccess(user.getResearcher()))
            	continue;
            
            userProjects.add(project);
           
        }
        
        request.setAttribute("userProjects", userProjects);
        request.setAttribute("projectId", projectId);
        // If a protein inference run id was sent with the request get it now
        String idStr = request.getParameter("piRunIds");
        if(idStr == null)
        	request.setAttribute("piRunIds", "");
        else
        	request.setAttribute("piRunIds", idStr);
        
        // Don't add to history
        request.setAttribute(HistoryTag.NO_HISTORY_ATTRIB, true); // We don't want this added to history.
        return mapping.findForward("Success");
        
    }
}
