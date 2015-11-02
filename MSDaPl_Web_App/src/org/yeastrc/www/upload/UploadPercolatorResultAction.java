/**
 * 
 */
package org.yeastrc.www.upload;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.yeastrc.ms.dao.DAOFactory;
import org.yeastrc.ms.domain.analysis.impl.SearchAnalysisBean;
import org.yeastrc.ms.domain.search.Program;
import org.yeastrc.project.Project;
import org.yeastrc.project.ProjectDAO;
import org.yeastrc.www.user.User;
import org.yeastrc.www.user.UserUtils;

/**
 * UploadPercolatorResultAction.java
 * @author Vagisha Sharma
 * Oct 27, 2010
 * 
 */
public class UploadPercolatorResultAction extends Action {

	
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

		UploadPercolatorResultForm myForm = (UploadPercolatorResultForm) form;
		
		// User should have access to the project
        Project project = ProjectDAO.instance().load(myForm.getProjectId());
        if(!project.checkAccess(user.getResearcher())) {
        	ActionErrors errors = new ActionErrors();
        	errors.add("general", new ActionMessage("error.general.errorMessage", 
        	"You may add Percolator results only for your projects."));
        	saveErrors( request, errors );
        	ActionForward fwd = mapping.findForward("Failure");
        	return new ActionForward( fwd.getPath() + "?ID=" + myForm.getProjectId(), fwd.getRedirect() ) ;
        }
		
        // We will first create an entry in the msSearchAnalysis table
        SearchAnalysisBean analysis = new SearchAnalysisBean();
        //      analysis.setSearchId(searchId);
        analysis.setAnalysisProgram(Program.PERCOLATOR);
        if(myForm.getComments() != null)
        	analysis.setComments(myForm.getComments().trim());
        int analysisId = 0;
        try {
        	analysisId = DAOFactory.instance().getMsSearchAnalysisDAO().save(analysis);
        }
        catch(RuntimeException e) {
        	ActionErrors errors = new ActionErrors();
        	errors.add("general", new ActionMessage("error.general.errorMessage", 
        	"There was an error saving your request. The error message was: "+e.getMessage()));
        	saveErrors( request, errors );
        	ActionForward fwd = mapping.findForward("Failure");
        	return new ActionForward( fwd.getPath() + "?ID=" + myForm.getProjectId(), fwd.getRedirect() ) ;
        }
        
        MsAnalysisUploadJobSaver jobSaver = new MsAnalysisUploadJobSaver();
		
		jobSaver.setProjectId( myForm.getProjectId() );
		jobSaver.setExperimentId(myForm.getExperimentId());
		jobSaver.setSearchAnalysisId(analysisId);
		jobSaver.setComments( myForm.getComments() );
		
		jobSaver.setServerDirectory("local:"+myForm.getDirectory());

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
