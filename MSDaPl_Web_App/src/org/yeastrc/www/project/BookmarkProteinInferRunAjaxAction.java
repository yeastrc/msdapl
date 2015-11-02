/**
 * BookmarkProteinInferRun.java
 * @author Vagisha Sharma
 * Apr 3, 2010
 * @version 1.0
 */
package org.yeastrc.www.project;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.yeastrc.experiment.ProjectProteinInferBookmarkDAO;
import org.yeastrc.www.user.User;
import org.yeastrc.www.user.UserUtils;

/**
 * 
 */
public class BookmarkProteinInferRunAjaxAction extends Action {

	private static final Logger log = Logger.getLogger(BookmarkProteinInferRunAjaxAction.class.getName());
	
	@Override
    public ActionForward execute(ActionMapping mapping, ActionForm form,
            HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        
        // User making this request
        User user = UserUtils.getUser(request);
        if (user == null) {
            response.getWriter().write("You are not logged in!");
            response.setStatus(HttpServletResponse.SC_SEE_OTHER); // Status code (303) indicating that the response to the request can be found under a different URI.
            return null;
        }
        
        int piRunId = 0;
        try {piRunId = Integer.parseInt(request.getParameter("pinferId"));}
        catch(NumberFormatException e) {}

        if(piRunId == 0) {
            response.setContentType("text/html");
            response.getWriter().write("<b>Invalid Protein inference ID: "+piRunId+"</b>");
            return null;
        }
        
        int projectId = 0;
        try {projectId = Integer.parseInt(request.getParameter("projectId"));}
        catch(NumberFormatException e) {}

        if(projectId == 0) {
            response.setContentType("text/html");
            response.getWriter().write("<b>Invalid Project ID: "+projectId+"</b>");
            return null;
        }
        
        boolean save = false;
        if(request.getParameter("save") == null)
        	save = false;
        else
        	save = true;
        
        
        if(save) {
        	log.info("Saving bookmark for piRunId: "+piRunId);
        	// Save
            try {
                ProjectProteinInferBookmarkDAO dao = ProjectProteinInferBookmarkDAO.getInstance();
                dao.saveBookmark(piRunId, projectId, user.getResearcher().getID());
            }
            catch(Exception e) {
                response.setContentType("text/html");
                response.getWriter().write("FAIL "+e.getMessage());
                return null;
            }
        }
        else {
        	log.info("Removing bookmark for piRunId: "+piRunId);
        	// Remove
            try {
                ProjectProteinInferBookmarkDAO dao = ProjectProteinInferBookmarkDAO.getInstance();
                dao.deleteBookmark(piRunId, projectId, user.getResearcher().getID());
            }
            catch(Exception e) {
                response.setContentType("text/html");
                response.getWriter().write("FAIL "+e.getMessage());
                return null;
            }
        }
        
        // Everything went well.
        response.setContentType("text/html");
        response.getWriter().write("OK");
        return null;
       
    }
}
