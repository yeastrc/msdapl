/**
 * SaveProtInferNameAjaxAction.java
 * @author Vagisha Sharma
 * Mar 7, 2011
 * @version 1.0
 */
package org.yeastrc.www.proteinfer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.yeastrc.ms.dao.ProteinferDAOFactory;
import org.yeastrc.ms.dao.protinfer.ibatis.ProteinferRunDAO;
import org.yeastrc.ms.domain.protinfer.ProteinferRun;
import org.yeastrc.www.user.User;
import org.yeastrc.www.user.UserUtils;

/**
 * 
 */
public class SaveProtInferNameAjaxAction extends Action {

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
        try {piRunId = Integer.parseInt(request.getParameter("id"));}
        catch(NumberFormatException e) {}

        if(piRunId == 0) {
            response.setContentType("text/html");
            response.getWriter().write("<b>Invalid Protein inference ID: "+piRunId+"</b>");
            return null;
        }

        String name = request.getParameter("text");
        if(name == null)
            name = "";
        
        // Save
        try {
            ProteinferRunDAO runDao = ProteinferDAOFactory.instance().getProteinferRunDao();
            ProteinferRun run = runDao.loadProteinferRun(piRunId);
            if(run == null) {
                response.setContentType("text/html");
                response.getWriter().write("<b>No protein inference run found with ID: "+piRunId+"</b>");
                return null;
            }
            run.setName(name);
            runDao.update(run);
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
