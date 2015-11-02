/**
 * submitPhiliusJobAjaxAction.java
 * @author Vagisha Sharma
 * Feb 3, 2010
 * @version 1.0
 */
package org.yeastrc.www.philiusws;

import java.util.HashSet;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.yeastrc.ms.dao.ProteinferDAOFactory;
import org.yeastrc.ms.domain.protinfer.ProteinferPeptide;
import org.yeastrc.ms.domain.protinfer.ProteinferProtein;
import org.yeastrc.nrseq.dao.NrSeqLookupUtil;
import org.yeastrc.nrseq.domain.NrProtein;
import org.yeastrc.philius.dao.PhiliusDAOFactory;
import org.yeastrc.philius.domain.PhiliusResult;
import org.yeastrc.www.proteinfer.ProteinSequenceHtmlBuilder;
import org.yeastrc.www.user.User;
import org.yeastrc.www.user.UserUtils;

/**
 * 
 */
public class PhiliusSubmitJobAjaxAction extends Action {

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

        // get the NR_SEQ protein ID
        int nrseqId = 0;
        try {nrseqId = Integer.parseInt(request.getParameter("nrseqId"));}
        catch(NumberFormatException e){};
        // if we  do not have a valid protein inference run id
        // return an error.
        if(nrseqId <= 0) {
        	response.setContentType("text/html");
            response.getWriter().write("FAILED: Invalid protein ID: "+nrseqId);
            return null;
        }

        
     // get the sequence for the protein
        String sequence = NrSeqLookupUtil.getProteinSequence(nrseqId);
//        sequence = " 1          11         21         31         41         51         |          |          |          |          |          |";          
//        sequence += "1 MSDQESVVSF NSQNTSMVDV EGQQPQQYVP SKTNSRANQL KLTKTETVKS LQDLGVTSAA  60";
//        sequence += "61 PVPDINAPQT AKNNIFPEEY TMETPSGLVP VATLQSMGRT ASALSRTRTK QLNRTATNSS 120";
//        sequence += "121 STGKEEMEEE ETEEREDQSG ENELDPEIEF VTFVTGDPEN PHNWPSWVRW SYTVLLSILV 180";
//        sequence += "181 ICVAYGSACI SGGLGTVEKK YHVGMEAAIL SCSLMVIGFS LGPLIWSPVS DLYGRRVAYF 240";
//        sequence += "241 VSMGLYVIFN IPCALAPNLG CLLACRFLCG VWSSSGLCLV GGSIADMFPS ETRGKAIAFF 300";
//        sequence += "301 AFAPYVGPVV GPLVNGFISV STGRMDLIFW VNMAFAGVMW IISSAIPETY APVILKRKAA 360";
//        sequence += "361 RLRKETGNPK IMTEQEAQGV SMSEMMRACL LRPLYFAVTE PVLVATCFYV CLIYSLLYAF 420";
//        sequence += "421 FFAFPVIFGE LYGYKDNLVG LMFIPIVIGA LWALATTFYC ENKYLQIVKQ RKPTPEDRLL 480";
//        sequence += "481 GAKIGAPFAA IALWILGATA YKHIIWVGPA SAGLAFGFGM VLIYYSLNNY IIDCYVQYAS 540";
//        sequence += "541 SALATKVFLR SAGGAAFPLF TIQMYHKLNL HWGSWLLAFI STAMIALPFA FSYWGKGLRH 600";
//        sequence += "601 KLSKKDYSID SVEM";
        if(sequence == null) {
        	response.setContentType("text/html");
            response.getWriter().write("FAILED: No sequence found for protein ID: "+nrseqId);
            return null;
        }
        
        
        // Check if we already have the Philius result for this sequence
        NrProtein nrProtein = NrSeqLookupUtil.getNrProtein(nrseqId);
        PhiliusResult philiusResult = PhiliusDAOFactory.getInstance().getPhiliusResultDAO().
        															loadForSequence(nrProtein.getSequenceId());
        if(philiusResult != null) {
        	
        	int pinferProteinId = 0;
        	try {pinferProteinId = Integer.parseInt(request.getParameter("pinferProteinId"));}
        	catch(NumberFormatException e){}
        	
        	// Get the covered peptide sequences for this protein, if we are given a protein inference protein ID
            Set<String>peptideSequences = new HashSet<String>(0);
        	if(pinferProteinId != 0) {
        		
        		ProteinferProtein protein = ProteinferDAOFactory.instance().getProteinferProteinDao().loadProtein(pinferProteinId);
            	if(protein == null) {
            		response.setContentType("text/html");
                    response.getWriter().write("FAILED: No database entry found for protein inference protein ID: "+pinferProteinId);
                    return null;
            	}
            	
            	// Get the unique peptide sequences for this protein (for building the protein sequence HTML)
                peptideSequences = new HashSet<String>(protein.getPeptideCount());
                for(ProteinferPeptide peptide: protein.getPeptides()) {
                    peptideSequences.add(peptide.getSequence());
                }
        	}
        	
        	PhiliusResultPlus resultPlus = new PhiliusResultPlus();
        	resultPlus.setResult(philiusResult);
        	resultPlus.setSequence(sequence);
        	resultPlus.setCoveredSequences(peptideSequences);
        	
            
            String html = "";
            if(!(philiusResult.getSegments() == null || philiusResult.getSegments().size() == 0)) {
            	html = PhiliusSequenceHtmlFormatter.getInstance().format(philiusResult, sequence, peptideSequences);
            }
            else {
            	html = ProteinSequenceHtmlBuilder.getInstance().build(sequence, peptideSequences);
            }
            
            request.setAttribute("philiusAnnotation", resultPlus);
            request.setAttribute("sequenceHtml", html);
        	request.setAttribute( "philiusmap", PhiliusImageMapMaker.getInstance().getImageMap(philiusResult, sequence));
        	
    		// set the result in the session for future use.  Will be needed for building the 
    		// Philius graphic
    		request.getSession().setAttribute( "philiusResult", resultPlus);
    		
    		// hack to prevent caching of philius image
    		request.setAttribute("philiusToken", sequence.hashCode());
            return mapping.findForward("Success");
        }
        
        
        
        // submit a Philius job
        int token = 0;
        try {
            PhiliusPredictorService service = new PhiliusPredictorService();
            PhiliusPredictorDelegate port = service.getPhiliusPredictorPort();
            token = port.submitSequence(sequence);
            // Return the Philius job token
            response.setContentType("text/html");
            response.getWriter().write("SUMBITTED "+token);
        }
        catch (PhiliusWSException_Exception e) {
            e.printStackTrace();
            response.setContentType("text/html");
            response.getWriter().write("FAILED: "+e.getMessage());
        }
        return null;
    }

//	private PhiliusSequenceAnnotationWS makePhiliusResult(
//			PhiliusResult philiusResult) {
//		
//		
//		PhiliusSequenceAnnotationWS result = new PhiliusSequenceAnnotationWS();
//		result.setHasSp(philiusResult.isSignalPeptide());
//		result.setHasTm(philiusResult.isTransMembrane());
//		result.setSequence(value);
//		result.setSpProbabilitySum(philiusResult.getSpProbabilitySum());
//		result.setTmProbabilitySum(philiusResult.getTmProbabilitySum());
//		result.setTopologyConfidence(philiusResult.getTopologyConfidenceScore());
//		result.setType(value);
//		result.setTypeScore(philiusResult.getTypeScore());
//		result.setTypeString(philiusResult.getAnnotation());
//		result.set
//		
//		return result;
//	}
}
