/**
 * ViewProteinProphetResults.java
 * @author Vagisha Sharma
 * Aug 5, 2009
 * @version 1.0
 */
package org.yeastrc.www.proteinfer.proteinProphet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.yeastrc.www.user.User;
import org.yeastrc.www.user.UserUtils;

/**
 * 
 */
public class ViewProteinProphetResultAction extends Action {

private static final Logger log = Logger.getLogger(ViewProteinProphetResultAction.class);
    
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

        // form for filtering and display options
        ProteinProphetFilterForm filterForm = (ProteinProphetFilterForm)form;
        request.setAttribute("proteinProphetFilterForm", filterForm);
        
        int pinferId = 0;
        try {pinferId = Integer.parseInt(request.getParameter("pinferId"));}
        catch(NumberFormatException e){
        	log.error("Error parsing 'pinferId': "+request.getParameter("pinferId"));
        }
        
        // if we still do not have a valid protein inference run id
        // return an error.
        if(pinferId <= 0) {
            log.error("Invalid protein inference run id: "+pinferId);
            ActionErrors errors = new ActionErrors();
            errors.add("proteinfer", new ActionMessage("error.proteinfer.invalid.pinferId"));
            saveErrors( request, errors );
            return mapping.findForward("Failure");
        }
        
        filterForm.setPinferId(pinferId);
        
        
        request.setAttribute("newRequest", true);
        
        // Go!
        return mapping.findForward("Success");
    }
}
