/**
 * 
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
import org.yeastrc.project.Projects;
import org.yeastrc.www.user.Groups;
import org.yeastrc.www.user.User;
import org.yeastrc.www.user.UserUtils;

/**
 * ViewProtInferAdminOptions.java
 * @author Vagisha Sharma
 * Jun 7, 2010
 * 
 */
public class ViewProtInferAdminOptions extends Action {

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

        // Restrict access to admins
        Groups groupMan = Groups.getInstance();
        if (!groupMan.isMember(user.getResearcher().getID(), "administrators")) {
            ActionErrors errors = new ActionErrors();
            errors.add("access", new ActionMessage("error.access.invalidgroup"));
            saveErrors( request, errors );
            return mapping.findForward( "Failure" );
        }
        
        // Create two forms and put them in the request
        DeleteProteinInferenceForm delForm = new DeleteProteinInferenceForm();
        request.setAttribute("deleteForm", delForm);
        
        RerunProteinInferenceForm rerunForm = new RerunProteinInferenceForm();
        request.setAttribute("rerunForm", rerunForm);
        
        // Go!
        return mapping.findForward( "Success" ) ;

    }
}
