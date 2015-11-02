/**
 * UpdateAnalysisStats.java
 * @author Vagisha Sharma
 * Jan 8, 2010
 * @version 1.0
 */
package org.yeastrc.www.project.experiment.stats;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.yeastrc.experiment.stats.QCStatsGetter;
import org.yeastrc.ms.dao.DAOFactory;
import org.yeastrc.ms.dao.analysis.peptideProphet.PeptideProphetRocDAO;
import org.yeastrc.ms.domain.analysis.MsSearchAnalysis;
import org.yeastrc.ms.domain.analysis.peptideProphet.PeptideProphetROC;
import org.yeastrc.ms.domain.search.Program;
import org.yeastrc.www.taglib.HistoryTag;
import org.yeastrc.www.user.User;
import org.yeastrc.www.user.UserUtils;

/**
 * 
 */
public class UpdateAnalysisStats extends Action {

    private static final Logger log = Logger.getLogger(ViewAnalysisStats.class.getName());

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

        QcPlotsFilterForm myForm = (QcPlotsFilterForm) form;
        request.setAttribute("filterForm", myForm);
        
        int analysisId = myForm.getAnalysisId();
        // If we don't have a valid id, return an error
        if(analysisId <= 0) {
            ActionErrors errors = new ActionErrors();
            errors.add(ActionErrors.GLOBAL_ERROR, new ActionMessage("error.general.invalid.id", "Percolator analysis: "+analysisId));
            saveErrors( request, errors );
            return mapping.findForward("Failure");
        }
        
        int experimentId = myForm.getExperimentId();
        // If we don't have a valid id, return an error
        if(experimentId <= 0) {
            ActionErrors errors = new ActionErrors();
            errors.add(ActionErrors.GLOBAL_ERROR, new ActionMessage("error.general.invalid.id", "Experiment: "+experimentId));
            saveErrors( request, errors );
            return mapping.findForward("Failure");
        }
        
        MsSearchAnalysis analysis = DAOFactory.instance().getMsSearchAnalysisDAO().load(analysisId);
        
        QCStatsGetter statsGetter = new QCStatsGetter();
        statsGetter.setGetPsmRtStats(true);
        statsGetter.setGetSpectraRtStats(true);
        
        
        if(analysis.getAnalysisProgram() == Program.PEPTIDE_PROPHET) {
        	
        	// NOTE: qvalue will be used as the PeptideProphet error rate
        	statsGetter.getStats(analysisId, myForm.getQvalue());
        	
        	PeptideProphetRocDAO rocDao = DAOFactory.instance().getPeptideProphetRocDAO();
    		PeptideProphetROC roc = rocDao.loadRoc(analysisId);
    		double errRate = roc.getClosestError(myForm.getQvalue());
    		double probability = roc.getMinProbabilityForError(errRate);
    		log.info("Probability for error rate of "+errRate+" is: "+probability);
    		
    		request.setAttribute("scorecutoff_string", "PeptideProphet Error rate: "+errRate+
    				" (probability "+probability+")");
    		
        }
        else if(analysis.getAnalysisProgram() == Program.PERCOLATOR) {
        	
        	statsGetter.getStats(analysisId, myForm.getQvalue());
        	
        	request.setAttribute("scorecutoff_string", "Percolator q-value: "+QCStatsGetter.PERC_QVAL_DEFAULT);
        	
        	request.setAttribute("is_percolator", true);
        }
        
        // -----------------------------------------------------------------------------
        // PSM-RT plot
        // -----------------------------------------------------------------------------
        request.setAttribute("psmRTDistributionChart", statsGetter.getPsmDistrUrl());
        request.setAttribute("psmRtFileStats", statsGetter.getPsmFileStats());
        request.setAttribute("psmAnalysisStats", statsGetter.getPsmAnalysisStats());
        
        
        // -----------------------------------------------------------------------------
        // Spectra-RT plot
        // -----------------------------------------------------------------------------
        request.setAttribute("spectraRTDistributionChart", statsGetter.getSpectraDistrUrl());
        request.setAttribute("spectraRtFileStats", statsGetter.getSpectraFileStats());
        request.setAttribute("spectraAnalysisStats", statsGetter.getSpectraAnalysisStats());
        
        
        // don't add this to history
        request.setAttribute(HistoryTag.NO_HISTORY_ATTRIB, true);
        return mapping.findForward("Success");
    }
}
