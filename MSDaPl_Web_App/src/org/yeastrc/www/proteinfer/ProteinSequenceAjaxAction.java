package org.yeastrc.www.proteinfer;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.yeastrc.ms.dao.ProteinferDAOFactory;
import org.yeastrc.ms.dao.protinfer.ibatis.ProteinferProteinDAO;
import org.yeastrc.ms.domain.protinfer.ProteinferPeptide;
import org.yeastrc.ms.domain.protinfer.ProteinferProtein;
import org.yeastrc.nrseq.dao.NrSeqLookupUtil;
import org.yeastrc.www.user.User;
import org.yeastrc.www.user.UserUtils;

public class ProteinSequenceAjaxAction extends Action {

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

        int pinferProteinId = 0;
        try {pinferProteinId = Integer.parseInt(request.getParameter("pinferProteinId"));}
        catch(NumberFormatException e) {}

        if(pinferProteinId == 0) {
            response.setContentType("text/html");
            response.getWriter().write("<b>Invalid protein inference protein ID: "+pinferProteinId+"</b>");
            return null;
        }

        ProteinferProteinDAO protDao = ProteinferDAOFactory.instance().getProteinferProteinDao();
        ProteinferProtein protein = protDao.loadProtein(pinferProteinId);
        List<ProteinferPeptide> peptides = protein.getPeptides();
        Set<String> peptideSeqs = new HashSet<String>(peptides.size());
        for(ProteinferPeptide peptide: peptides) {
            peptideSeqs.add(peptide.getSequence());
        }
        
        System.out.println("Got request for protein inference protein id: "+pinferProteinId);

        String sequence = NrSeqLookupUtil.getProteinSequence(protein.getNrseqProteinId());
        String html = ProteinSequenceHtmlBuilder.getInstance().build(sequence, peptideSeqs);
        
        // Go!
        response.setContentType("text/html");
        response.getWriter().write("<pre>"+html+"</pre>");
        return null;
    }

}
