/**
 * SaveExperimentCommentsAjaxAction.java
 * @author Vagisha Sharma
 * Apr 30, 2009
 * @version 1.0
 */
package org.yeastrc.www.project.experiment;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.yeastrc.experiment.ProjectExperimentDAO;
import org.yeastrc.ms.dao.DAOFactory;
import org.yeastrc.ms.dao.general.MsExperimentDAO;
import org.yeastrc.project.Project;
import org.yeastrc.project.ProjectDAO;
import org.yeastrc.www.user.User;
import org.yeastrc.www.user.UserUtils;

/**
 * 
 */
public class SaveExperimentCommentsAjaxAction extends Action {

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
        
        int experimentId = 0;
        try {experimentId = Integer.parseInt(request.getParameter("id"));}
        catch(NumberFormatException e) {}

        if(experimentId == 0) {
            response.setContentType("text/html");
            response.getWriter().write("<b>Invalid Experiment ID: "+experimentId+"</b>");
            return null;
        }

        // Get the project for this experiment.  If the user making the request is not listed 
        // as a researcher on the project they should not be able to edit comments
        int projectId = ProjectExperimentDAO.instance().getProjectIdForExperiment(experimentId);
        Project project = ProjectDAO.instance().load(projectId);
        if(!project.checkAccess(user.getResearcher())) {
            response.setContentType("text/html");
            response.getWriter().write("FAIL You may edit experiment comments only for project to which you are affiliated");
            return null;
        }
        
        
        String comments = request.getParameter("text");
        if(comments == null) {
            response.setContentType("text/html");
            response.getWriter().write("<b>No comments found.</b>");
            return null;
        }
        
        // Save
        try {
            MsExperimentDAO exptDao = DAOFactory.instance().getMsExperimentDAO();
            exptDao.updateComments(experimentId, comments);
        }
        catch(Exception e) {
            response.setContentType("text/html");
            response.getWriter().write("FAIL "+e.getMessage());
            return null;
        }
        
        // Everything went well.
        response.setContentType("text/html");
        response.getWriter().write("OK");
        return null;
       
    }

}
