/**
 * RerunProteinInferenceAction.java
 * @author Vagisha Sharma
 * Apr 8, 2010
 * @version 1.0
 */
package org.yeastrc.www.proteinfer.job;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
import org.yeastrc.www.user.Groups;
import org.yeastrc.www.user.User;
import org.yeastrc.www.user.UserUtils;

/**
 * 
 */
public class RerunProteinInferenceAction extends Action {

	private static final Logger log = Logger.getLogger("protinferRerunLog");
    
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

        // get the protein inference ID
        int piRunId = -1;
        if (request.getParameter("pinferId") != null) {
            try {piRunId = Integer.parseInt(request.getParameter("pinferId"));}
            catch(NumberFormatException e) {piRunId = -1;}
        }
        
        if (piRunId == -1) {
            ActionErrors errors = new ActionErrors();
            errors.add("proteinfer", new ActionMessage("error.general.errorMessage", "No protein inference id found"));
            saveErrors( request, errors );
            return mapping.findForward("Failure");
        }
        
        // get the project ID so we can redirect back to the project
        int projectId = -1;
        if (request.getParameter("projectId") != null) {
            try {projectId = Integer.parseInt(request.getParameter("projectId"));}
            catch(NumberFormatException e) {projectId = -1;}
        }
        if(projectId == -1) {
            log.error("Invalid project id: "+projectId);
            ActionErrors errors = new ActionErrors();
            errors.add("proteinfer", new ActionMessage("error.project.noprojectid"));
            saveErrors( request, errors );
            return mapping.findForward("standardHome");
        }
        
        log.info("Got request to re-run protein inference piRunID: "+piRunId);
        
        ProteinferJob job = ProteinInferJobSearcher.getInstance().getJobForPiRunId(piRunId);
        if(job == null) {
        	ActionErrors errors = new ActionErrors();
            errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("error.general.errorMessage", "No job found for piRunID: "+piRunId));
            saveErrors( request, errors );
            return mapping.findForward("Failure");
        }
        
        // Only an administrator or the user who initially ran this protein inference can re-run it.
        Groups groupMan = Groups.getInstance();
        if(job.getSubmitter() != user.getResearcher().getID() && 
           !groupMan.isMember(user.getResearcher().getID(), "administrators")) {
        	ActionErrors errors = new ActionErrors();
            errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("error.general.errorMessage", "User does not have access to re-run piRunID: "+piRunId));
            saveErrors( request, errors );
            return mapping.findForward("Failure");
        }
        
        // RERUN
        ProteinInferenceRerunner.reRun(piRunId, false);
        
        // Go!
        ActionForward success = mapping.findForward( "Success" ) ;
        success = new ActionForward( success.getPath() + "?ID="+projectId, success.getRedirect() ) ;
        return success;

    }
}
