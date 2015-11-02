/**
 * ViewPercolatorPeptidePsmAjaxAction.java
 * @author Vagisha Sharma
 * Sep 20, 2010
 */
package org.yeastrc.www.project.experiment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.yeastrc.experiment.PercolatorResultPlus;
import org.yeastrc.ms.dao.DAOFactory;
import org.yeastrc.ms.dao.analysis.MsRunSearchAnalysisDAO;
import org.yeastrc.ms.dao.analysis.percolator.PercolatorPeptideResultDAO;
import org.yeastrc.ms.dao.run.MsScanDAO;
import org.yeastrc.ms.dao.run.ms2file.MS2RunDAO;
import org.yeastrc.ms.dao.run.ms2file.MS2ScanDAO;
import org.yeastrc.ms.dao.search.MsRunSearchDAO;
import org.yeastrc.ms.dao.search.sequest.SequestSearchResultDAO;
import org.yeastrc.ms.domain.analysis.percolator.PercolatorPeptideResult;
import org.yeastrc.ms.domain.analysis.percolator.PercolatorResult;
import org.yeastrc.ms.domain.run.MsScan;
import org.yeastrc.ms.domain.run.ms2file.MS2Scan;
import org.yeastrc.www.user.User;
import org.yeastrc.www.user.UserUtils;

/**
 * 
 */
public class ViewPercolatorPeptidePsmAjaxAction extends Action {

	private static final Logger log = Logger.getLogger(ViewPercolatorPeptidePsmAjaxAction.class);
	
	public ActionForward execute( ActionMapping mapping,
            ActionForm form,
            HttpServletRequest request,
            HttpServletResponse response )
    throws Exception {



		User user = UserUtils.getUser(request);
        if (user == null) {
            response.getWriter().write("You are not logged in!");
            response.setStatus(HttpServletResponse.SC_SEE_OTHER); // Status code (303) indicating that the response to the request can be found under a different URI.
            return null;
        }

        int percolatorPeptideId = 0;
        try {percolatorPeptideId = Integer.parseInt(request.getParameter("percolatorPeptideId"));}
        catch(NumberFormatException e) {}
        
        if(percolatorPeptideId == 0) {
            response.setContentType("text/html");
            response.getWriter().write("<b>Invalid Percolator peptide ID: "+percolatorPeptideId+"</b>");
            return null;
        }
        
        PercolatorPeptideResultDAO peptResDao = DAOFactory.instance().getPercolatorPeptideResultDAO();
        PercolatorPeptideResult result = peptResDao.load(percolatorPeptideId);
        if(result == null) {
        	response.setContentType("text/html");
            response.getWriter().write("<b>No result found for Percolator peptide ID: "+percolatorPeptideId+"</b>");
            return null;
        }
        
        int searchAnalysisId = 0;
        try {searchAnalysisId = Integer.parseInt(request.getParameter("searchAnalysisId"));}
        catch(NumberFormatException e) {}
        
        if(searchAnalysisId == 0) {
            response.setContentType("text/html");
            response.getWriter().write("<b>Invalid Percolator analysis ID: "+searchAnalysisId+"</b>");
            return null;
        }
        
        
        // Do we have Bullseye results for the searched files
        boolean hasBullsEyeArea = false;
        List<Integer> searchIds = DAOFactory.instance().getMsSearchAnalysisDAO().getSearchIdsForAnalysis(searchAnalysisId);
        MsRunSearchDAO rsDao = DAOFactory.instance().getMsRunSearchDAO();
        MS2RunDAO runDao = DAOFactory.instance().getMS2FileRunDAO();
        List<Integer> runSearchIds = rsDao.loadRunSearchIdsForSearch(searchIds.get(0));
        for(int runSearchId: runSearchIds) {
            int runId = rsDao.loadRunSearch(runSearchId).getRunId();
            if(runDao.isGeneratedByBullseye(runId)) {
                hasBullsEyeArea = true;
                break;
            }
        }
        
        // file names
        Map<Integer, String> filenameMap = getFileNames(searchAnalysisId);
        
        MS2ScanDAO ms2ScanDao = DAOFactory.instance().getMS2FileScanDAO();
        MsScanDAO scanDao = DAOFactory.instance().getMsScanDAO();
        SequestSearchResultDAO seqResDao = DAOFactory.instance().getSequestResultDAO();
        
        List<PercolatorResult> psmList = result.getPsmList();
        List<PercolatorResultPlus> displayList = new ArrayList<PercolatorResultPlus>(psmList.size());
        for(PercolatorResult psm: psmList) {
        	
        	PercolatorResultPlus resPlus = null;
            
            if(hasBullsEyeArea) {
                MS2Scan scan = ms2ScanDao.loadScanLite(psm.getScanId());
                resPlus = new PercolatorResultPlus(psm, scan);
            }
            else {
                MsScan scan = scanDao.loadScanLite(psm.getScanId());
                resPlus = new PercolatorResultPlus(psm, scan);
            }
            
            resPlus.setFilename(filenameMap.get(psm.getRunSearchAnalysisId()));
            resPlus.setSequestData(seqResDao.load(psm.getId()).getSequestResultData());
            displayList.add(resPlus);
        }
        
        request.setAttribute("percolatorPeptideId", percolatorPeptideId);
        request.setAttribute("psmList", displayList);
        return mapping.findForward("Success");
	}
	
	private Map<Integer, String> getFileNames(int searchAnalysisId) {

        MsRunSearchAnalysisDAO saDao = DAOFactory.instance().getMsRunSearchAnalysisDAO();
        List<Integer> runSearchAnalysisIds = saDao.getRunSearchAnalysisIdsForAnalysis(searchAnalysisId);

        Map<Integer, String> filenameMap = new HashMap<Integer, String>(runSearchAnalysisIds.size()*2);
        for(int runSearchAnalysisId: runSearchAnalysisIds) {
            String filename = saDao.loadFilenameForRunSearchAnalysis(runSearchAnalysisId);
            filenameMap.put(runSearchAnalysisId, filename);
        }
        return filenameMap;

    }
}
