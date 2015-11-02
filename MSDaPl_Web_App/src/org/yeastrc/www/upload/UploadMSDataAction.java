/*
 * UploadMSDataAction.java
 * Created on Oct 12, 2004
 * Created by Michael Riffle <mriffle@u.washington.edu>
 */

package org.yeastrc.www.upload;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.yeastrc.project.Project;
import org.yeastrc.project.ProjectDAO;
import org.yeastrc.www.user.Groups;
import org.yeastrc.www.user.User;
import org.yeastrc.www.user.UserUtils;

public class UploadMSDataAction extends Action {

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
		
		UploadMSDataForm uform = (UploadMSDataForm)form;
		int projectID = uform.getProjectID();
		
		// User has to be a member of a group.
		Groups groupMan = Groups.getInstance();
        if (!groupMan.isInAGroup(user.getResearcher().getID())) {
            ActionErrors errors = new ActionErrors();
            errors.add("access", new ActionMessage("error.access.invalidgroup"));
            saveErrors( request, errors );
            return mapping.findForward("standardHome");
        }
        
		
		// Restrict access to administrators and researchers associated with the project
		Project project = ProjectDAO.instance().load(projectID);
		if(!project.checkAccess(user.getResearcher())) {
			ActionErrors errors = new ActionErrors();
			errors.add("access", new ActionMessage("error.access.invalidgroup"));
			saveErrors( request, errors );
			return mapping.findForward("adminHome");
		}
		
		// If the user is uploading data from a remote server make sure the user is a member of the 
		// lab / group the server belongs to.
		if(!uform.getDataServer().equals("local")) {
		    String dataServerGroup = uform.getDataServer();
		    
		    if(!groupMan.isMember(user.getResearcher().getID(), dataServerGroup)) {
		        ActionErrors errors = new ActionErrors();
		        errors.add("upload", new ActionMessage("error.upload.saveerror", 
		                "You do not have access to upload data from the "+dataServerGroup+" server."));
	            saveErrors( request, errors );
	            return mapping.findForward("Failure");
		    }
		}
		
		Date runDate = uform.getExperimentDate();
		int species = uform.getSpecies();
		String comments = uform.getComments();
		String directory = uform.getDirectory();
		
		
		MSUploadJobSaver jobSaver = new MSUploadJobSaver();
        
		int jobGroupId = UserGroupIdGetter.getOneGroupId(user);
        jobSaver.setGroupID(jobGroupId);
        
		
		jobSaver.setProjectID( projectID );
		jobSaver.setRunDate( runDate );
		jobSaver.setTargetSpecies( species );
		jobSaver.setComments( comments );
		jobSaver.setPipeline(uform.getPipeline());
		jobSaver.setInstrumentId(uform.getInstrumentId());
		
		jobSaver.setServerDirectory(uform.getDataServer()+":"+directory);

		jobSaver.setSubmitter( user.getID() );

		int jobId;
		try {
		    // Save data to the queue database
		    jobId = jobSaver.savetoDatabase();

		} catch (Exception e) {
		    ActionErrors errors = new ActionErrors();
		    errors.add("upload", new ActionMessage("error.upload.saveerror", e.getMessage()));
		    saveErrors (request, errors );
		    return mapping.findForward("Failure");
		}
			
		ActionForward fwd = mapping.findForward( "Success" );
		return new ActionForward(fwd.getPath()+"?queued="+jobId, fwd.getRedirect());
	}
}