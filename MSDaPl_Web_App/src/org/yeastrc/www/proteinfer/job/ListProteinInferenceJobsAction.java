/**
 * 
 */
package org.yeastrc.www.proteinfer.job;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.yeastrc.jobqueue.JobUtils;
import org.yeastrc.www.user.Groups;
import org.yeastrc.www.user.User;
import org.yeastrc.www.user.UserUtils;


/**
 * ListProteinInferenceJobsAction.java
 * @author Vagisha Sharma
 * Jun 8, 2010
 * 
 */
public class ListProteinInferenceJobsAction extends Action {

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

		// Restrict access to admins
		Groups groupMan = Groups.getInstance();
		if (!groupMan.isMember(user.getResearcher().getID(), "administrators")) {
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

		ProteinInferJobSearcher js = ProteinInferJobSearcher.getInstance();

		List<Integer> statusCodes = new ArrayList<Integer>();
		if (status.equals( "pending" ) ) {
			statusCodes.add( JobUtils.STATUS_HARD_ERROR );
			statusCodes.add( JobUtils.STATUS_OUT_FOR_WORK );
			statusCodes.add( JobUtils.STATUS_QUEUED );
			statusCodes.add( JobUtils.STATUS_SOFT_ERROR );
		} else {
			statusCodes.add( JobUtils.STATUS_COMPLETE );
		}

		int jobCount = js.getJobCount(statusCodes);

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
			request.setAttribute( "jobs", js.getJobs(statusCodes, index) );
		} catch (Exception e) {
			return mapping.findForward( "Failure" );
		}

		return mapping.findForward( "Success" );
	}
}
