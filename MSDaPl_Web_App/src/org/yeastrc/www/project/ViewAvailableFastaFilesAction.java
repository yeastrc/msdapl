/**
 * ViewAvailableFastaFilesAction.java
 * @author Vagisha Sharma
 * Jan 25, 2010
 * @version 1.0
 */
package org.yeastrc.www.project;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.yeastrc.nrseq.dao.NrseqDatabaseDAO;
import org.yeastrc.nrseq.domain.NrDatabase;
import org.yeastrc.www.user.Groups;
import org.yeastrc.www.user.User;
import org.yeastrc.www.user.UserUtils;

/**
 * 
 */
public class ViewAvailableFastaFilesAction extends Action {

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

        // Restrict access to users who belong to at least one group
        Groups groupMan = Groups.getInstance();
        int researcherId = user.getResearcher().getID();
        if(!groupMan.isInAGroup(researcherId)) {
            ActionErrors errors = new ActionErrors();
            errors.add("access", new ActionMessage("error.access.invalidgroup"));
            saveErrors( request, errors );
            return mapping.findForward("standardHome");
        }
        
        // Get a list of the available fasta files
        List<NrDatabase> databases = NrseqDatabaseDAO.getInstance().getDatabases(null); // this will return a list of ALL fasta files in the database
        
        request.setAttribute("databaseList", databases);

        // Kick it to the view page
        return mapping.findForward("Success");

    }
}
