/**
 * 
 */
package org.yeastrc.www.jobqueue;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.yeastrc.jobqueue.Job;
import org.yeastrc.jobqueue.JobDeleter;
import org.yeastrc.jobqueue.JobUtils;
import org.yeastrc.jobqueue.MSJob;
import org.yeastrc.jobqueue.MSJobFactory;
import org.yeastrc.jobqueue.MsAnalysisUploadJob;
import org.yeastrc.jobqueue.PercolatorJob;
import org.yeastrc.ms.dao.DAOFactory;
import org.yeastrc.project.Project;
import org.yeastrc.www.user.Groups;
import org.yeastrc.www.user.User;
import org.yeastrc.www.user.UserUtils;

/**
 * @author Mike
 *
 */
public class DeleteJobAction extends Action {

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

		// Restrict access to yrc members
		Groups groupMan = Groups.getInstance();
		if (!groupMan.isInAGroup(user.getResearcher().getID())) {
			ActionErrors errors = new ActionErrors();
			errors.add("access", new ActionMessage("error.access.invalidgroup"));
			saveErrors( request, errors );
			return mapping.findForward( "Failure" );
		}
		
		try {
			Job job = MSJobFactory.getInstance().getJobLite( Integer.parseInt( request.getParameter( "id" ) ) );
			
			if (job == null) {
				ActionErrors errors = new ActionErrors();
				errors.add("general", new ActionMessage("error.general.errorMessage","No job found with ID "+request.getParameter( "id" )));
				saveErrors( request, errors );
				return mapping.findForward( "Failure" );
			}
			
			Project project = null;
			if(job instanceof MSJob)
				project = ((MSJob)job).getProject();
			else if(job instanceof MsAnalysisUploadJob)
				project = ((MsAnalysisUploadJob)job).getProject();
			else if(job instanceof PercolatorJob)
				project = ((PercolatorJob)job).getProject();
			
			if(project == null) {
				ActionErrors errors = new ActionErrors();
				errors.add("general", new ActionMessage("error.general.errorMessage","No project found for job ID "+request.getParameter( "id" )));
				saveErrors( request, errors );
				return mapping.findForward("Failure");
			}
			
			if(!project.checkAccess(user.getResearcher())) {
			     ActionErrors errors = new ActionErrors();
			     errors.add("username", new ActionMessage("error.general.errorMessage", 
			             "You may delete upload jobs only for projects to which you are affiliated"));
			     saveErrors( request, errors );
			     return mapping.findForward( "Failure" );
			}
			

			JobDeleter.getInstance().deleteJob( job );
			
			if(job instanceof MsAnalysisUploadJob && !(job.getStatus() == JobUtils.STATUS_COMPLETE)) {
				// delete the entry in the msSearchAnalysis table
				int searchAnalysisId = ((MsAnalysisUploadJob)job).getSearchAnalysisId();
				DAOFactory.instance().getMsSearchAnalysisDAO().delete(searchAnalysisId);
			}
			job = null;
			
		} catch (Exception e) {
			return mapping.findForward( "Failure" );
		}
		
		
		return mapping.findForward( "Success" );
	}
}