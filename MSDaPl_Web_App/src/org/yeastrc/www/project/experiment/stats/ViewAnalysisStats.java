/**
 * ViewExperimentStats.java
 * @author Vagisha Sharma
 * Dec 8, 2009
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
import org.yeastrc.ms.domain.analysis.impl.PeptideTerminalAAResult;
import org.yeastrc.ms.domain.analysis.peptideProphet.PeptideProphetROC;
import org.yeastrc.ms.domain.search.Program;
import org.yeastrc.www.user.User;
import org.yeastrc.www.user.UserUtils;

/**
 * 
 */
public class ViewAnalysisStats extends Action {

    private static final Logger log = Logger.getLogger(ViewAnalysisStats.class);

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

        int analysisId = 0;
        try {
            String strID = request.getParameter("analysisId");
            if(strID != null)
                analysisId = Integer.parseInt(strID);


        } catch (NumberFormatException nfe) {
            ActionErrors errors = new ActionErrors();
            errors.add(ActionErrors.GLOBAL_ERROR, new ActionMessage("error.general.invalid.id", "Percolator analysis: "+analysisId));
            saveErrors( request, errors );
            return mapping.findForward("Failure");
        }
        
        // If we still don't have a valid analysis id, return an error
        if(analysisId == 0) {
            ActionErrors errors = new ActionErrors();
            errors.add(ActionErrors.GLOBAL_ERROR, new ActionMessage("error.general.invalid.id", "Percolator analysis: "+analysisId));
            saveErrors( request, errors );
            return mapping.findForward("Failure");
        }
        request.setAttribute("analysisId", analysisId);
        request.setAttribute("qvalue", 0.01);
        
        int experimentId = 0;
        try {
            String strID = request.getParameter("experimentId");
            if(strID != null)
                experimentId = Integer.parseInt(strID);


        } catch (NumberFormatException nfe) {
            ActionErrors errors = new ActionErrors();
            errors.add(ActionErrors.GLOBAL_ERROR, new ActionMessage("error.general.invalid.id", "Experiment: "+experimentId));
            saveErrors( request, errors );
            return mapping.findForward("Failure");
        }
        
        // If we still don't have a valid experiment id, return an error
        if(experimentId == 0) {
            ActionErrors errors = new ActionErrors();
            errors.add(ActionErrors.GLOBAL_ERROR, new ActionMessage("error.general.invalid.id", "Experiment: "+experimentId));
            saveErrors( request, errors );
            return mapping.findForward("Failure");
        }
        
        // -----------------------------------------------------------------------------
        // Set up the form
        // -----------------------------------------------------------------------------
        QcPlotsFilterForm myForm = (QcPlotsFilterForm) form;
        myForm.setExperimentId(experimentId);
        myForm.setAnalysisId(analysisId);
        myForm.setQvalue(0.01);
        //request.setAttribute("filterForm", myForm);
        
        
        QCStatsGetter statsGetter = new QCStatsGetter();
        statsGetter.setGetPsmRtStats(true);
        statsGetter.setGetSpectraRtStats(true);
        statsGetter.setGetPeptideTerminiStats(true);
        
        MsSearchAnalysis analysis = DAOFactory.instance().getMsSearchAnalysisDAO().load(analysisId);
        
        if(analysis.getAnalysisProgram() == Program.PEPTIDE_PROPHET) {
        	
        	statsGetter.getStats(analysisId, QCStatsGetter.PEPPROPHET_ERR_RATE_DEFAULT);
        	
        	PeptideProphetRocDAO rocDao = DAOFactory.instance().getPeptideProphetRocDAO();
    		PeptideProphetROC roc = rocDao.loadRoc(analysisId);
    		double errRate = roc.getClosestError(QCStatsGetter.PEPPROPHET_ERR_RATE_DEFAULT);
    		double probability = roc.getMinProbabilityForError(errRate);
    		log.info("Probability for error rate of "+errRate+" is: "+probability);
    		
    		request.setAttribute("scorecutoff_string", "PeptideProphet Error rate: "+errRate+
    				" (probability "+probability+")");
    		
        }
        else if(analysis.getAnalysisProgram() == Program.PERCOLATOR) {
        	
        	statsGetter.getStats(analysisId, QCStatsGetter.PERC_QVAL_DEFAULT);
        	
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
        
//        request.setAttribute("spectraRTDistributionChart", "http://chart.apis.google.com/chart?cht=bvs&chbh=a&chs=500x325&chdl=MS/MS%20scans%20with%20good%20results%20(qvalue%3C=0.01)|All%20MS/MS%20Scans&chdlp=t&chg=10,10&chf=c,s,EFEFEF&chxt=x,y,x,y&chxl=2:|Retention%20Time|3:|Scans|&chxp=2,50|3,50&chxr=0,0,119.96055,11|1,0,1491,149&chds=0,1501,0,1501&chco=008000,800080&chd=t:87,26,27,47,24,28,90,232,381,398,407,360,475,503,577,532,537,533,546,447,504,513,477,497,477,422,501,440,426,406,468,443,496,502,437,470,449,448,427,461,446,443,429,444,463,421,373,327,156,7|669,427,672,699,487,402,451,774,1066,1045,1078,1115,1015,988,914,950,944,946,936,1024,965,939,979,959,979,1028,940,994,1009,1020,958,979,928,914,962,928,948,940,951,916,925,918,905,874,860,903,912,824,371,31");
        
        // -----------------------------------------------------------------------------
        // Terminal residue plot
        // -----------------------------------------------------------------------------
        PeptideTerminalAAResult peptideTerminalAAResult = statsGetter.getPeptideTerminalResidueStats();
        String peptideTerminalAAResultUrl = statsGetter.getPeptideTerminalResiduePlotUrl();
        if(peptideTerminalAAResult != null && peptideTerminalAAResultUrl != null) {
        	
        	request.setAttribute("peptideTerminalAAResult", peptideTerminalAAResult);
            request.setAttribute("peptideTerminalAAResultUrl", peptideTerminalAAResultUrl);
        }
        
        return mapping.findForward("Success");
    }
}
