/**
 * DeleteExperimentSuccessAction.java
 * @author Vagisha Sharma
 * Dec 17, 2009
 * @version 1.0
 */
package org.yeastrc.www.project.experiment;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

/**
 * 
 */
public class DeleteExperimentSuccessAction extends Action {

    public ActionForward execute( ActionMapping mapping,
            ActionForm form,
            HttpServletRequest request,
            HttpServletResponse response )
    throws Exception {

        int experimentId = 0;
        // get the experiment ID
        String strId = request.getParameter("experimentId");
        try {
            if(strId != null)
                experimentId = Integer.parseInt(strId.trim());
        }
        catch (NumberFormatException nfe) {
            experimentId = 0;
        }

        int projectId = 0;
        strId = request.getParameter("projectId");
        try {
            if(strId != null)
                projectId = Integer.parseInt(strId.trim());
        }
        catch (NumberFormatException nfe) {
            experimentId = 0;
        }
        
        request.setAttribute("experimentId", experimentId);
        request.setAttribute("projectId", projectId);
        return mapping.findForward("Success");
    }
}
