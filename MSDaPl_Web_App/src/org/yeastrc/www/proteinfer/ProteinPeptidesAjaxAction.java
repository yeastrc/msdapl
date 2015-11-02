package org.yeastrc.www.proteinfer;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.yeastrc.ms.dao.ProteinferDAOFactory;
import org.yeastrc.ms.domain.protinfer.ProteinInferenceProgram;
import org.yeastrc.ms.domain.protinfer.ProteinferRun;
import org.yeastrc.www.proteinfer.idpicker.IdPickerResultsLoader;
import org.yeastrc.www.proteinfer.idpicker.WIdPickerIon;
import org.yeastrc.www.proteinfer.proteinProphet.ProteinProphetResultsLoader;
import org.yeastrc.www.proteinfer.proteinProphet.WProteinProphetIon;
import org.yeastrc.www.user.User;
import org.yeastrc.www.user.UserUtils;

public class ProteinPeptidesAjaxAction extends Action {

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

        int proteinGroupId = 0;
        try {proteinGroupId = Integer.parseInt(request.getParameter("proteinGroupId"));}
        catch(NumberFormatException e) {}

        if(proteinGroupId == 0) {
            response.setContentType("text/html");
            response.getWriter().write("<b>Invalid Protein Inference Protein ID: "+proteinGroupId+"</b>");
            return null;
        }
        
        // protein ID is optional. This is sent by a request that requires the
        // peptides table sent in the response to have the proteinId as part of its html id.
        int proteinId = 0;
        try {proteinId = Integer.parseInt(request.getParameter("proteinId"));}
        catch(NumberFormatException e) {}

        System.out.println("Got request for protein group Id: "+proteinGroupId+" of protein inference run: "+pinferId+
                "(protein Id: "+proteinId+")");

        request.setAttribute("pinferId", pinferId);
        request.setAttribute("proteinGroupId", proteinGroupId);
        if(proteinId != 0) {
            request.setAttribute("proteinId", proteinId); // this is needed so that the peptides table has the right html id.
        }
        
        ProteinferRun run = ProteinferDAOFactory.instance().getProteinferRunDao().loadProteinferRun(pinferId);
        request.setAttribute("protInferProgram", run.getProgram().name());
        request.setAttribute("inputGenerator", run.getInputGenerator().name());
        if(run.getProgram() == ProteinInferenceProgram.PROTINFER_PERC_OLD) {
            request.setAttribute("oldPercolator", true);
        }
        
        if(ProteinInferenceProgram.isIdPicker(run.getProgram())) {
            List<WIdPickerIon> ionList = IdPickerResultsLoader.getPeptideIonsForProteinGroup(pinferId, proteinGroupId);
            request.setAttribute("proteinPeptideIons", ionList);
            boolean hasPrecursorArea = false;
            for(WIdPickerIon ion: ionList) {
                if(ion.hasPrecursorArea()) {
                    hasPrecursorArea = true;
                }
                break;
            }
            request.setAttribute("hasPrecursorArea", hasPrecursorArea);
            
            boolean hasPeptideScores = false;
            for(WIdPickerIon ion: ionList) {
            	if(ion.getPercolatorPeptideResult() != null) {
            		hasPeptideScores = true;
            	}
            	break;
            }
            request.setAttribute("hasPeptideScores", hasPeptideScores);
            return mapping.findForward("Success");
        }
        else if(run.getProgram() == ProteinInferenceProgram.PROTEIN_PROPHET) {
            List<WProteinProphetIon> ionList = ProteinProphetResultsLoader.getPeptideIonsForProteinGroup(pinferId, proteinGroupId);
            request.setAttribute("proteinPeptideIons", ionList);
            return mapping.findForward("Success");
        }
        
        return null;
    }
}
