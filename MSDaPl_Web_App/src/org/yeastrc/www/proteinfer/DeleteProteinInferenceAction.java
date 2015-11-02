/**
 * DeleteProteinInferenceAction.java
 * @author Vagisha Sharma
 * Mar 3, 2009
 * @version 1.0
 */
package org.yeastrc.www.proteinfer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.yeastrc.experiment.ProjectProteinInferBookmarkDAO;
import org.yeastrc.jobqueue.JobDeleter;
import org.yeastrc.ms.dao.ProteinferDAOFactory;
import org.yeastrc.project.Project;
import org.yeastrc.project.ProjectDAO;
import org.yeastrc.www.proteinfer.job.ProteinInferJobSearcher;
import org.yeastrc.www.proteinfer.job.ProteinferJob;
import org.yeastrc.www.user.User;
import org.yeastrc.www.user.UserUtils;

/**
 * 
 */
public class DeleteProteinInferenceAction extends Action {

    private static final Logger log = Logger.getLogger(DeleteProteinInferenceAction.class.getName());
    
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

        // get the project ID so we can redirect back in case of failure
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
        
        // User making the request to delete protein inference should be affiliated with the project
        Project project = ProjectDAO.instance().load(projectId);
        if(!project.checkAccess(user.getResearcher())) {
             ActionErrors errors = new ActionErrors();
             errors.add("username", new ActionMessage("error.general.errorMessage", 
                     "You may delete protein inference jobs only for projects to which you are affiliated"));
             saveErrors( request, errors );
             return mapping.findForward( "Failure" );
        }
        
        // get the protein inference id
        int pinferId = 0;
        try {pinferId = Integer.parseInt(request.getParameter("pinferId"));}
        catch(NumberFormatException e){};
        // if we  do not have a valid protein inference run id
        // return an error.
        if(pinferId <= 0) {
            ActionErrors errors = new ActionErrors();
            errors.add("proteinfer", new ActionMessage("error.proteinfer.invalid.pinferId", pinferId));
            saveErrors( request, errors );
            return mapping.findForward( "Failure" );
        }
        
        // first delete the job from the job queue database
        ProteinferJob job = ProteinInferJobSearcher.getInstance().getJobForPiRunId(pinferId);
        
        boolean deleted = false;
        if(job != null) {
            deleted = JobDeleter.getInstance().deleteJob(job);
        }
        else {
            ActionErrors errors = new ActionErrors();
            errors.add(ActionErrors.GLOBAL_ERROR, new ActionMessage("error.proteinfer.deletejob", "Could not get job for protein inference ID: "+pinferId));
            saveErrors(request, errors);
            return mapping.findForward( "Failure" );
        }
        
        // If the entry in jobQueue was not deleted stop here.
        if(!deleted) {
            ActionErrors errors = new ActionErrors();
            errors.add("proteinfer", new ActionMessage("error.proteinfer.deletejob", "Could not delete protein inference ID: "+pinferId));
            saveErrors(request, errors);
            return mapping.findForward( "Failure" );
        }
        
        
        // now delete the protein inference run from the mass spec database.
        ProteinferDAOFactory fact = ProteinferDAOFactory.instance();
        try {
            fact.getProteinferRunDao().delete(pinferId);
        }
        catch(Exception e) {
        	
        	log.error( "Error deleting protein inference ID: " + pinferId, e ); 
        	
            ActionErrors errors = new ActionErrors();
            errors.add("proteinfer", new ActionMessage("error.proteinfer.deletejob", "Error deleting protein inference ID: "+pinferId));
            saveErrors(request, errors);
            return mapping.findForward( "Failure" );
        }
        
        // If this protein inference was bookmarked delete the entry
        try {
            ProjectProteinInferBookmarkDAO dao = ProjectProteinInferBookmarkDAO.getInstance();
            dao.deleteBookmark(pinferId);
        }
        catch(Exception e) {
        	
        	log.error( "Protein inference ID: "+pinferId+" was deleted"+
            		" but there was an error deleting the bookmark entry.", e ); 

        	ActionErrors errors = new ActionErrors();
            errors.add("proteinfer", new ActionMessage("error.proteinfer.deletejob", "Protein inference ID: "+pinferId+" was deleted"+
            		" but there was an error deleting the bookmark entry."));
            saveErrors(request, errors);
            return mapping.findForward( "Failure" );
        }
        
        // Go!
        ActionForward success = mapping.findForward( "Success" ) ;
        success = new ActionForward( success.getPath() + "?ID="+projectId, success.getRedirect() ) ;
        return success;
    }
}
