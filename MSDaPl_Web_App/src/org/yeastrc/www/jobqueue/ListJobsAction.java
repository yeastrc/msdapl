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
import org.yeastrc.jobqueue.JobUtils;
import org.yeastrc.jobqueue.MsJobSearcher;
import org.yeastrc.www.user.Groups;
import org.yeastrc.www.user.User;
import org.yeastrc.www.user.UserUtils;

/**
 * @author Mike
 *
 */
public class ListJobsAction extends Action {

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
		
		String status = request.getParameter( "status" );
		if (status == null || status.equals( "" ))
			status = "pending";
		
		int index = 0;
		try {
			index = Integer.parseInt( request.getParameter( "index" ) );
		} catch (Exception e) { ; }
		
		request.setAttribute( "status", status );

		MsJobSearcher js = new MsJobSearcher();
		js.setOffset( index );
		
		if (status.equals( "pending" ) ) {
			js.addStatus( JobUtils.STATUS_HARD_ERROR );
			js.addStatus( JobUtils.STATUS_OUT_FOR_WORK );
			js.addStatus( JobUtils.STATUS_QUEUED );
			js.addStatus( JobUtils.STATUS_SOFT_ERROR );
		} else {
			js.addStatus( JobUtils.STATUS_COMPLETE );
		}
		
		int jobCount = js.getJobCount();
		
		request.setAttribute( "firstResult", new Integer( index + 1 ) );

		if (jobCount < index + 50)
			request.setAttribute( "lastResult", new Integer( jobCount ) );
		else
			request.setAttribute( "lastResult", new Integer( index + 50 ) );
		
		request.setAttribute( "totalCount", new Integer( jobCount ) );
		
		if (index > 0 && index <= 50)
			request.setAttribute( "previousIndex", new Integer( 0 ) );
		else if (index > 0)
			request.setAttribute( "previousIndex", new Integer( index - 50) );
		
		if ( jobCount > index + 50 )
			request.setAttribute( "nextIndex", new Integer( index + 50) );
		
		
		try {
			request.setAttribute( "jobs", js.getJobs() );
		} catch (Exception e) {
			return mapping.findForward( "Failure" );
		}
		
		if(request.getParameter("queued") != null) {
			request.setAttribute( "queued", Boolean.TRUE );
			String jobId = request.getParameter("queued");
			request.setAttribute("jobId", jobId);
		}
		return mapping.findForward( "Success" );
	}
}
