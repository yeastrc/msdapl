/**
 * 
 */
package org.yeastrc.www.project.experiment;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.yeastrc.experiment.ProjectExperimentDAO;
import org.yeastrc.ms.dao.DAOFactory;
import org.yeastrc.ms.dao.analysis.MsSearchAnalysisDAO;
import org.yeastrc.ms.dao.search.MsSearchDAO;
import org.yeastrc.ms.domain.search.MsSearch;
import org.yeastrc.project.Project;
import org.yeastrc.project.ProjectDAO;
import org.yeastrc.www.user.User;
import org.yeastrc.www.user.UserUtils;

/**
 * SaveAnalysisCommentsAjaxAction.java
 * @author Vagisha Sharma
 * Oct 30, 2010
 * 
 */
public class SaveAnalysisCommentsAjaxAction extends Action {

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
        
        int analysisId = 0;
        try {analysisId = Integer.parseInt(request.getParameter("id"));}
        catch(NumberFormatException e) {}

        if(analysisId == 0) {
            response.setContentType("text/html");
            response.getWriter().write("<b>Invalid Analysis ID: "+analysisId+"</b>");
            return null;
        }

        List<Integer> searchIds = DAOFactory.instance().getMsSearchAnalysisDAO().getSearchIdsForAnalysis(analysisId);
        List<Integer> experimentIds = new ArrayList<Integer>();
        MsSearchDAO searchDao = DAOFactory.instance().getMsSearchDAO();
        for(Integer searchId: searchIds) {
        	MsSearch search = searchDao.loadSearch(searchId);
        	if(search != null) {
        		if(!experimentIds.contains(search.getExperimentId()))
        			experimentIds.add(search.getExperimentId());
        	}
        }
        
        if(experimentIds.size()  > 0) {
        	// Get the project for this experiment.  If the user making the request is not listed 
        	// as a researcher on the project they should not be able to edit comments
        	int projectId = ProjectExperimentDAO.instance().getProjectIdForExperiment(experimentIds.get(0));
        	Project project = ProjectDAO.instance().load(projectId);
        	if(!project.checkAccess(user.getResearcher())) {
        		response.setContentType("text/html");
        		response.getWriter().write("FAIL You may edit experiment comments only for project to which you are affiliated");
        		return null;
        	}
        }
        else {
        	response.setContentType("text/html");
            response.getWriter().write("FAIL No experiment Ids found for the given analysis Id.");
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
            MsSearchAnalysisDAO aDao = DAOFactory.instance().getMsSearchAnalysisDAO();
            aDao.updateComments(analysisId, comments);
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
