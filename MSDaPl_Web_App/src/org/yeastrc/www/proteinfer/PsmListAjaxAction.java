/**
 * IonHitsAjaxAction.java
 * @author Vagisha Sharma
 * Feb 27, 2009
 * @version 1.0
 */
package org.yeastrc.www.proteinfer;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.yeastrc.ms.dao.ProteinferDAOFactory;
import org.yeastrc.ms.domain.protinfer.ProteinInferenceProgram;
import org.yeastrc.ms.domain.protinfer.ProteinferRun;
import org.yeastrc.ms.util.TimeUtils;
import org.yeastrc.www.proteinfer.idpicker.IdPickerResultsLoader;
import org.yeastrc.www.proteinfer.idpicker.WIdPickerSpectrumMatch;
import org.yeastrc.www.proteinfer.proteinProphet.ProteinProphetResultsLoader;
import org.yeastrc.www.proteinfer.proteinProphet.ProteinProphetSpectrumMatch;
import org.yeastrc.www.user.User;
import org.yeastrc.www.user.UserUtils;

/**
 * 
 */
public class PsmListAjaxAction extends Action {

    private static final Logger log = Logger.getLogger(PsmListAjaxAction.class.getName());
    
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

        int pinferId = 0;
        try {pinferId = Integer.parseInt(request.getParameter("pinferId"));}
        catch(NumberFormatException e) {}

        if(pinferId == 0) {
            response.setContentType("text/html");
            response.getWriter().write("<b>Invalid Protein Inference ID: "+pinferId+"</b>");
            return null;
        }
        
        int pinferIonId = 0;
        try {pinferIonId = Integer.parseInt(request.getParameter("pinferIonId"));}
        catch(NumberFormatException e) {}

        if(pinferIonId == 0) {
            response.setContentType("text/html");
            response.getWriter().write("<b>Invalid protein inference ion ID: "+pinferIonId+"</b>");
            return null;
        }
        
        System.out.println("Got request for all spectra for protein inference ion id: "+pinferIonId);
        long s = System.currentTimeMillis();
        
        ProteinferRun run = ProteinferDAOFactory.instance().getProteinferRunDao().loadProteinferRun(pinferId);
        request.setAttribute("protInferProgram", run.getProgram().name());
        request.setAttribute("inputGenerator", run.getInputGenerator().name());
        
        if(ProteinInferenceProgram.isIdPicker(run.getProgram())) {
            List<WIdPickerSpectrumMatch> ionsWAllSpectra = IdPickerResultsLoader.getHitsForIon(pinferIonId, run.getInputGenerator(), run.getProgram());
            request.setAttribute("psmList", ionsWAllSpectra);
            request.setAttribute("pinferIonId", pinferIonId);
            long e = System.currentTimeMillis();
            log.info("Total time (PsmListAjaxAction): "+TimeUtils.timeElapsedSeconds(s, e));
            return mapping.findForward("Success");
        }
        else if(run.getProgram() == ProteinInferenceProgram.PROTEIN_PROPHET) {
            List<ProteinProphetSpectrumMatch> ionsWAllSpectra = ProteinProphetResultsLoader.getHitsForIon(pinferIonId, run.getInputGenerator());
            request.setAttribute("psmList", ionsWAllSpectra);
            request.setAttribute("pinferIonId", pinferIonId);
            long e = System.currentTimeMillis();
            log.info("Total time (PsmListAjaxAction): "+TimeUtils.timeElapsedSeconds(s, e));
            return mapping.findForward("Success");
        }
        
        return mapping.findForward("Failure");
    }
}
