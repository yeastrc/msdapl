/**
 * ViewExperimentFilesAction.java
 * @author Vagisha Sharma
 * Apr 13, 2009
 * @version 1.0
 */
package org.yeastrc.www.project.experiment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.yeastrc.experiment.AnalysisFile;
import org.yeastrc.experiment.ExperimentSearch;
import org.yeastrc.experiment.MsFile;
import org.yeastrc.experiment.ProjectExperiment;
import org.yeastrc.experiment.SearchAnalysis;
import org.yeastrc.experiment.SearchFile;
import org.yeastrc.ms.dao.DAOFactory;
import org.yeastrc.ms.dao.analysis.MsRunSearchAnalysisDAO;
import org.yeastrc.ms.dao.analysis.MsSearchAnalysisDAO;
import org.yeastrc.ms.dao.analysis.peptideProphet.PeptideProphetResultDAO;
import org.yeastrc.ms.dao.analysis.percolator.PercolatorResultDAO;
import org.yeastrc.ms.dao.run.MsRunDAO;
import org.yeastrc.ms.dao.run.MsScanDAO;
import org.yeastrc.ms.dao.search.MsRunSearchDAO;
import org.yeastrc.ms.domain.analysis.MsRunSearchAnalysis;
import org.yeastrc.ms.domain.analysis.MsSearchAnalysis;
import org.yeastrc.ms.domain.general.MsExperiment;
import org.yeastrc.ms.domain.run.MsRun;
import org.yeastrc.ms.domain.run.RunFileFormat;
import org.yeastrc.ms.domain.search.MsRunSearch;
import org.yeastrc.ms.domain.search.MsSearch;
import org.yeastrc.ms.domain.search.Program;
import org.yeastrc.www.user.User;
import org.yeastrc.www.user.UserUtils;

/**
 * 
 */
public class ViewExperimentFilesAjaxAction extends Action {

	private static DAOFactory daoFactory = DAOFactory.instance();
    private static final Logger log = Logger.getLogger(ViewExperimentFilesAjaxAction.class.getName());
    
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


        // Get the experimentId they're after
        int experimentId = 0;
        try {
            String strID = request.getParameter("experimentId");

            if (strID == null || strID.equals("")) {
                response.setContentType("text/html");
                response.getWriter().write("<b>Invalid Experiment Id ID: "+experimentId+"</b>");
                return null;
            }

            experimentId = Integer.parseInt(strID);

        } catch (NumberFormatException nfe) {
            response.setContentType("text/html");
            response.getWriter().write("<b>Invalid Experiment Id ID: "+experimentId+"</b>");
            return null;
        }

        
        // Get the data for this experiment
        ProjectExperiment experiment = getProjectExperiment(experimentId);
        
        
        request.setAttribute("experiment", experiment);

        // Forward them on to the happy success view page!
        return mapping.findForward("Success");

    }


    private ProjectExperiment getProjectExperiment(int experimentId) throws Exception {
        
        MsExperiment expt = daoFactory.getMsExperimentDAO().loadExperiment(experimentId);
        ProjectExperiment pExpt = new ProjectExperiment(expt);

        // load the ms2 file names and the number of spectra in each file
        List<Integer> runIds = daoFactory.getMsExperimentDAO().getRunIdsForExperiment(experimentId);
        List<MsFile> ms2Files = new ArrayList<MsFile>(runIds.size());
        List<MsFile> ms1Files = null;
        
        MsRunDAO runDao = daoFactory.getMsRunDAO();
        MsScanDAO scanDao = daoFactory.getMsScanDAO();
        for(Integer runId: runIds) {
            MsRun run = runDao.loadRun(runId);
            int numScans = scanDao.numScans(runId, 2);
            MsFile file = new MsFile(run, numScans);
            ms2Files.add(file);
            
            if(run.getRunFileFormat() == RunFileFormat.MZXML) {
                numScans = scanDao.numScans(runId, 1);
                file = new MsFile(run, numScans);
                if(ms1Files == null)
                    ms1Files = new ArrayList<MsFile>(runIds.size());
                ms1Files.add(file);
            }
        }
        pExpt.setMs2Files(ms2Files);
        pExpt.setMs1Files(ms1Files);
        
        // if we are looking at mzXML files load the ms1 information
        

        // load the searches
        List<Integer> searchIds = daoFactory.getMsSearchDAO().getSearchIdsForExperiment(experimentId);
        List<ExperimentSearch> searches = new ArrayList<ExperimentSearch>(searchIds.size());
        for(int searchId: searchIds) {
            searches.add(getExperimentSearch(searchId));
        }
        pExpt.setSearches(searches);

        // load the analyses
        Set<Integer> analysisIds = new HashSet<Integer>();
        MsSearchAnalysisDAO saDao = daoFactory.getMsSearchAnalysisDAO();
        for(int searchId: searchIds) {
            List<Integer> aIds = saDao.getAnalysisIdsForSearch(searchId);
            analysisIds.addAll(aIds);
        }
        List<SearchAnalysis> analyses = new ArrayList<SearchAnalysis>(analysisIds.size());
        List<Integer> analysisIdsList = new ArrayList<Integer>(analysisIds);
        Collections.sort(analysisIdsList);
        for(int analysisId: analysisIdsList) {
            analyses.add(getSearchAnalysis(analysisId));
        }
        pExpt.setAnalyses(analyses);

        return pExpt;
    }
    
    
    private ExperimentSearch getExperimentSearch(int searchId) {
        
        MsSearch search = daoFactory.getMsSearchDAO().loadSearch(searchId);
        ExperimentSearch eSearch = new ExperimentSearch(search);
        
        MsRunSearchDAO rsDao = daoFactory.getMsRunSearchDAO();
        
        List<Integer> runSearchIds = rsDao.loadRunSearchIdsForSearch(searchId);
        List<SearchFile> files = new ArrayList<SearchFile>(runSearchIds.size());
        
        for(int runSearchId: runSearchIds) {
            MsRunSearch rs = rsDao.loadRunSearch(runSearchId);
            String filename = rsDao.loadFilenameForRunSearch(runSearchId);
            SearchFile file = new SearchFile(rs, filename);
            file.setNumResults(rsDao.numResults(runSearchId));
            files.add(file);
        }
        eSearch.setFiles(files);
        return eSearch;
    }
    
    private SearchAnalysis getSearchAnalysis(int searchAnalysisId) {
        
        MsSearchAnalysis analysis = daoFactory.getMsSearchAnalysisDAO().load(searchAnalysisId);
        SearchAnalysis sAnalysis = new SearchAnalysis(analysis);
        
        MsRunSearchAnalysisDAO rsaDao = daoFactory.getMsRunSearchAnalysisDAO();
        
        List<Integer> rsAnalysisIds = rsaDao.getRunSearchAnalysisIdsForAnalysis(searchAnalysisId);
        List<AnalysisFile> files = new ArrayList<AnalysisFile>(rsAnalysisIds.size());
        for(int id: rsAnalysisIds) {
            MsRunSearchAnalysis rsa = rsaDao.load(id);
            String filename = rsaDao.loadFilenameForRunSearchAnalysis(id);
            AnalysisFile file = new AnalysisFile(rsa, filename);
            files.add(file);
        }
        
        // If this is Percolator analysis we know how to get the number of 
        // results for each file.
        if(analysis.getAnalysisProgram() == Program.PERCOLATOR) {
            PercolatorResultDAO prDao = daoFactory.getPercolatorResultDAO();
            for(AnalysisFile file: files) {
                file.setNumResults(prDao.numRunAnalysisResults(file.getId()));
            }
        }
        else if(analysis.getAnalysisProgram() == Program.PEPTIDE_PROPHET) {
            PeptideProphetResultDAO ppResDao = daoFactory.getPeptideProphetResultDAO();
            for(AnalysisFile file: files) {
                file.setNumResults(ppResDao.numRunAnalysisResults(file.getId()));
            }
        }
        
        sAnalysis.setFiles(files);
        return sAnalysis;
    }
}
