/**
 * 
 */
package org.yeastrc.www.jobqueue;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.yeastrc.bio.taxonomy.Species;
import org.yeastrc.jobqueue.Job;
import org.yeastrc.jobqueue.MSJob;
import org.yeastrc.jobqueue.MSJobFactory;
import org.yeastrc.jobqueue.MsAnalysisUploadJob;
import org.yeastrc.jobqueue.PercolatorJob;
import org.yeastrc.www.user.Groups;
import org.yeastrc.www.user.User;
import org.yeastrc.www.user.UserUtils;

/**
 * @author Mike
 *
 */
public class ViewJobAction extends Action {

	private static final Logger log = Logger.getLogger(ViewJobAction.class);
	
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
			Job job = MSJobFactory.getInstance().getJob( Integer.parseInt( request.getParameter( "id" ) ) );
			
			if (job == null) {
				ActionErrors errors = new ActionErrors();
				errors.add("general", new ActionMessage("error.general.errorMessage","No job found with ID "+request.getParameter( "id" )));
				saveErrors( request, errors );
				return mapping.findForward( "Failure" );
			}
			
			request.setAttribute( "job", job );
			if(job instanceof MSJob) {
				int speciesId = ((MSJob)job).getTargetSpecies();
				Species species = Species.getInstance(speciesId);
				request.setAttribute("species", species);
				request.setAttribute("experimentUploadJob",true);
			}
			else if(job instanceof MsAnalysisUploadJob) {
				request.setAttribute("analysisUploadJob",true);
			}
			else if(job instanceof PercolatorJob) {
				request.setAttribute("percolatorJob", true);
			}
			
		} catch (Exception e) {
			
			log.error("Error loading job with ID: "+request.getParameter( "id" ), e);
			ActionErrors errors = new ActionErrors();
			errors.add("general", new ActionMessage("error.general.errorMessage","Error reading job with ID: "
					+request.getParameter( "id" )+". Error was: "+e.getMessage()));
			saveErrors( request, errors );
			return mapping.findForward( "Failure" );
		}
		
		
		return mapping.findForward( "Success" );
	}
}
