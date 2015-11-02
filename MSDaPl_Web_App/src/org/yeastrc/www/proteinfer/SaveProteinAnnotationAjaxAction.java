/**
 * SaveProteinAnnotationAjaxAction.java
 * @author Vagisha Sharma
 * Dec 10, 2008
 * @version 1.0
 */
package org.yeastrc.www.proteinfer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.yeastrc.ms.dao.ProteinferDAOFactory;
import org.yeastrc.ms.dao.protinfer.ibatis.ProteinferProteinDAO;
import org.yeastrc.ms.domain.protinfer.ProteinUserValidation;
import org.yeastrc.ms.domain.protinfer.ProteinferProtein;
import org.yeastrc.www.user.Groups;
import org.yeastrc.www.user.User;
import org.yeastrc.www.user.UserUtils;

/**
 * 
 */
public class SaveProteinAnnotationAjaxAction extends Action {

    public ActionForward execute( ActionMapping mapping,
            ActionForm form,
            HttpServletRequest request,
            HttpServletResponse response )
    throws Exception {


        // User making this request
        User user = UserUtils.getUser(request);
        if (user == null) {
            response.getWriter().write("You are not logged in!");
            response.setStatus(HttpServletResponse.SC_SEE_OTHER); // Status code (303) indicating that the response to the request can be found under a different URI.
            return null;
        }

        // Restrict access to yrc members
        Groups groupMan = Groups.getInstance();
        if (!groupMan.isInAGroup(user.getResearcher().getID())) {
            ActionErrors errors = new ActionErrors();
            errors.add("access", new ActionMessage("error.access.invalidgroup"));
            saveErrors( request, errors );
            return mapping.findForward( "Failure" );
        }
        
        int pinferProtId = 0;
        try {pinferProtId = Integer.parseInt(request.getParameter("pinferProtId"));}
        catch(NumberFormatException e) {}

        if(pinferProtId == 0) {
            response.setContentType("text/html");
            response.getWriter().write("ERROR: Invalid protein inference protein ID: "+pinferProtId);
            return null;
        }

        System.out.println("Got request for protien inference protein ID: "+pinferProtId);


        
        // get the protein 
        ProteinferProteinDAO protDao = ProteinferDAOFactory.instance().getProteinferProteinDao();
        ProteinferProtein protein = protDao.loadProtein(pinferProtId);
        if(protein == null) {
            response.setContentType("text/html");
            response.getWriter().write("ERROR: No protein found with protein ID: "+pinferProtId);
            return null;
        }
        
        String comments = request.getParameter("comments");
        comments = comments.trim();
        String validationStatus = request.getParameter("validation");
        ProteinUserValidation validation = ProteinUserValidation.getStatusForChar(validationStatus.charAt(0));
        
        if(comments != null && comments.length() > 0) {
            protDao.updateUserAnnotation(pinferProtId, comments);
            System.out.println("Saving comments for "+pinferProtId);
        }
        if(validation != null) {
            protDao.updateUserValidation(pinferProtId, validation);
            System.out.println("Saving validation status for "+pinferProtId);
        }
        
        response.getWriter().write("OK");
        return null;
    }
}
