package org.yeastrc.www.proteinfer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.yeastrc.ms.dao.ProteinferDAOFactory;
import org.yeastrc.ms.dao.protinfer.ibatis.ProteinferProteinDAO;
import org.yeastrc.ms.domain.protinfer.ProteinferProtein;
import org.yeastrc.www.user.User;
import org.yeastrc.www.user.UserUtils;

public class DeleteProteinAnnotationAjaxAction extends Action {

    private static final Logger log = Logger.getLogger(DeleteProteinAnnotationAjaxAction.class);
    
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


        int pinferProtId = 0;
        try {pinferProtId = Integer.parseInt(request.getParameter("pinferProtId"));}
        catch(NumberFormatException e) {}

        if(pinferProtId <= 0) {
            response.setContentType("text/html");
            response.getWriter().write("ERROR: Invalid protein inference protein ID: "+pinferProtId);
            return null;
        }

        System.out.println("Got request for deleting annotation for protien inference protein ID: "+pinferProtId);


        
        // get the protein 
        ProteinferProteinDAO protDao = ProteinferDAOFactory.instance().getProteinferProteinDao();
        ProteinferProtein protein = protDao.loadProtein(pinferProtId);
        if(protein == null) {
            response.setContentType("text/html");
            response.getWriter().write("ERROR: No protein found with protein ID: "+pinferProtId);
            return null;
        }
        
        try {
            protDao.updateUserAnnotation(pinferProtId, null);
            protDao.updateUserValidation(pinferProtId, null);
        }
        catch(RuntimeException e) {
            log.error("Error deleting annotation for protein ID: "+pinferProtId, e);
            response.setContentType("text/html");
            response.getWriter().write("ERROR: Exception deleting annotation for protein with protein ID: "+pinferProtId);
            return null; 
        }
        
        response.getWriter().write("OK");
        return null;
    }
}
