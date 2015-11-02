/**
 * 
 */
package org.yeastrc.www.upload;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.yeastrc.experiment.ProjectExperimentDAO;
import org.yeastrc.project.Project;
import org.yeastrc.project.ProjectDAO;
import org.yeastrc.www.user.Groups;
import org.yeastrc.www.user.User;
import org.yeastrc.www.user.UserUtils;

/**
 * UploadPercolatorResultFormAction.java
 * @author Vagisha Sharma
 * Oct 27, 2010
 * 
 */
public class UploadPercolatorResultFormAction extends Action {

	private static final Logger log = Logger.getLogger(UploadPercolatorResultFormAction.class);
	
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

		// Restrict access to researchers who are members of a group
		Groups groupMan = Groups.getInstance();
		if (!groupMan.isInAGroup(user.getResearcher().getID())) {
			ActionErrors errors = new ActionErrors();
			errors.add("access", new ActionMessage("error.access.invalidgroup"));
			saveErrors( request, errors );
			return mapping.findForward("standardHome");
		}
		
		String val = null;
		int experimentId = 0;
		try {
			val = request.getParameter("experimentId");
			if(val != null)
				experimentId = Integer.parseInt(val);
		}
		catch (NumberFormatException e) {
			log.error("Invalid experimentId in request: "+val);
		}

		
		int projectId = ProjectExperimentDAO.instance().getProjectIdForExperiment(experimentId);
		
		// User should have access to the project
        Project project = ProjectDAO.instance().load(projectId);
        if(!project.checkAccess(user.getResearcher())) {
        	ActionErrors errors = new ActionErrors();
        	errors.add("username", new ActionMessage("error.general.errorMessage", 
        	"You may add Percolator results only for your projects."));
        	saveErrors( request, errors );
        	ActionForward fwd = mapping.findForward("Failure");
        	return new ActionForward( fwd.getPath() + "?ID=" + projectId, fwd.getRedirect() ) ;
        }
		
		// If we don't have a valid experiment ID in the request we cannot go ahead with Percolator result upload
		if(experimentId == 0) {
			ActionErrors errors = new ActionErrors();
			errors.add("upload", new ActionMessage("error.general.errorMessage", 
        	"No valid experiment ID found in request."));
			saveErrors( request, errors );
			ActionForward fwd = mapping.findForward("Failure");
			return new ActionForward( fwd.getPath() + "?ID=" + projectId, fwd.getRedirect() ) ;
		}

		UploadPercolatorResultForm myForm = new UploadPercolatorResultForm();
		myForm.setExperimentId(experimentId);
		myForm.setProjectId(projectId);
		request.setAttribute("uploadPercolatorResultForm", myForm);
		
		return mapping.findForward("Success");
	}
}
