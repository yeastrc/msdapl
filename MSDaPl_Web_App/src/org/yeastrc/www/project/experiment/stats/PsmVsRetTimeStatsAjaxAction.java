/**
 * PsmVsRetTimeStatsAjaxAction.java
 * @author Vagisha Sharma
 * Oct 5, 2010
 */
package org.yeastrc.www.project.experiment.stats;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.yeastrc.experiment.stats.QCStatsGetter;
import org.yeastrc.ms.dao.DAOFactory;
import org.yeastrc.ms.dao.analysis.peptideProphet.PeptideProphetRocDAO;
import org.yeastrc.ms.domain.analysis.MsSearchAnalysis;
import org.yeastrc.ms.domain.analysis.peptideProphet.PeptideProphetROC;
import org.yeastrc.ms.domain.search.Program;

/**
 * 
 */
public class PsmVsRetTimeStatsAjaxAction extends Action {

	private static final Logger log = Logger.getLogger(PsmVsRetTimeStatsAjaxAction.class);

    public ActionForward execute( ActionMapping mapping,
            ActionForm form,
            HttpServletRequest request,
            HttpServletResponse response )
    throws Exception {
        
        
        int analysisId = 0;
        String strVal = null;
        try {
            strVal = request.getParameter("analysisId");
            if(strVal != null)
                analysisId = Integer.parseInt(strVal);


        } catch (NumberFormatException nfe) {
           analysisId = 0;
        }
        
        if(analysisId == 0) {
            response.setContentType("text/html");
            response.getWriter().write("<b>Invalid analysis ID: "+strVal+"</b>");
            return null;
        }

        Double scoreCutoff = null;
        try {
        	strVal = request.getParameter("scoreCutoff");
        	if(strVal != null) {
        		scoreCutoff = Double.parseDouble(strVal);
        	}
        }
        catch(Exception e) {scoreCutoff = null;}
        if(scoreCutoff == null) {
            response.setContentType("text/html");
            response.getWriter().write("<b>Invalid qvalue: "+strVal+"</b>");
            return null;
        }
        // request.setAttribute("scoreCutoff", scoreCutoff);
        
        
        QCStatsGetter statsGetter = new QCStatsGetter();
        statsGetter.setGetPsmRtStats(true);
        statsGetter.getStats(analysisId, scoreCutoff);
        
        MsSearchAnalysis analysis = DAOFactory.instance().getMsSearchAnalysisDAO().load(analysisId);
        
        if(analysis.getAnalysisProgram() == Program.PEPTIDE_PROPHET) {
        	
        	PeptideProphetRocDAO rocDao = DAOFactory.instance().getPeptideProphetRocDAO();
    		PeptideProphetROC roc = rocDao.loadRoc(analysisId);
    		double errRate = roc.getClosestError(scoreCutoff);
    		double probability = roc.getMinProbabilityForError(errRate);
    		log.info("Probability for error rate of "+errRate+" is: "+probability);
    		
    		request.setAttribute("scorecutoff_string", "PeptideProphet Error rate: "+errRate+
    				" (probability "+probability+")");
    		
        }
        else if(analysis.getAnalysisProgram() == Program.PERCOLATOR) {
        	
        	request.setAttribute("scorecutoff_string", "Percolator q-value: "+QCStatsGetter.PERC_QVAL_DEFAULT);
        	
        	request.setAttribute("is_percolator", true);
        }
        
        
        // -----------------------------------------------------------------------------
        // PSM-RT plot
        // -----------------------------------------------------------------------------
        request.setAttribute("psmRTDistributionChart", statsGetter.getPsmDistrUrl());
        request.setAttribute("psmRtFileStats", statsGetter.getPsmFileStats());
        request.setAttribute("psmAnalysisStats", statsGetter.getPsmAnalysisStats());
        
        
        return mapping.findForward("Success");
    }
}
