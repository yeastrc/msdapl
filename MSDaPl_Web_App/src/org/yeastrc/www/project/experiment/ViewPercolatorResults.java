/**
 * ViewPercolatorResults.java
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
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.yeastrc.experiment.PercolatorResultPlus;
import org.yeastrc.experiment.ProjectExperimentDAO;
import org.yeastrc.experiment.TabularPercolatorPeptideResults;
import org.yeastrc.experiment.TabularPercolatorResults;
import org.yeastrc.ms.dao.DAOFactory;
import org.yeastrc.ms.dao.analysis.MsRunSearchAnalysisDAO;
import org.yeastrc.ms.dao.analysis.percolator.PercolatorPeptideResultDAO;
import org.yeastrc.ms.dao.analysis.percolator.PercolatorResultDAO;
import org.yeastrc.ms.dao.run.MsScanDAO;
import org.yeastrc.ms.dao.run.ms2file.MS2RunDAO;
import org.yeastrc.ms.dao.run.ms2file.MS2ScanDAO;
import org.yeastrc.ms.dao.search.MsRunSearchDAO;
import org.yeastrc.ms.dao.search.MsSearchDAO;
import org.yeastrc.ms.dao.search.prolucid.ProlucidSearchResultDAO;
import org.yeastrc.ms.dao.search.sequest.SequestSearchResultDAO;
import org.yeastrc.ms.domain.analysis.MsSearchAnalysis;
import org.yeastrc.ms.domain.analysis.percolator.PercolatorPeptideResult;
import org.yeastrc.ms.domain.analysis.percolator.PercolatorResult;
import org.yeastrc.ms.domain.run.MsScan;
import org.yeastrc.ms.domain.run.ms2file.MS2Scan;
import org.yeastrc.ms.domain.search.MsResidueModification;
import org.yeastrc.ms.domain.search.MsSearch;
import org.yeastrc.ms.domain.search.SORT_BY;
import org.yeastrc.ms.domain.search.SORT_ORDER;
import org.yeastrc.ms.domain.search.prolucid.ProlucidSearchResult;
import org.yeastrc.ms.domain.search.sequest.SequestSearchResult;
import org.yeastrc.project.Project;
import org.yeastrc.project.ProjectFactory;
import org.yeastrc.www.misc.Pageable;
import org.yeastrc.www.misc.ResultsPager;
import org.yeastrc.www.misc.Tabular;
import org.yeastrc.www.user.User;
import org.yeastrc.www.user.UserUtils;

/**
 * 
 */
public class ViewPercolatorResults extends Action {

	private static final Logger log = Logger.getLogger(ViewPercolatorResults.class);

    public ActionForward execute( ActionMapping mapping,
            ActionForm form,
            HttpServletRequest request,
            HttpServletResponse response )
    throws Exception {


		int searchAnalysisId = 0;
    	
    	try {


    		// User making this request
    		User user = UserUtils.getUser(request);
    		if (user == null) {
    			ActionErrors errors = new ActionErrors();
    			errors.add("username", new ActionMessage("error.login.notloggedin"));
    			saveErrors( request, errors );
    			return mapping.findForward("authenticate");
    		}

    		// Get the form
    		PercolatorFilterResultsForm myForm = (PercolatorFilterResultsForm)form;

    		searchAnalysisId = myForm.getSearchAnalysisId();
    		if(searchAnalysisId == 0) {
    			try {
    				String strID = request.getParameter("ID");
    				if(strID != null)
    					searchAnalysisId = Integer.parseInt(strID);


    			} catch (NumberFormatException nfe) {
    				ActionErrors errors = new ActionErrors();
    				errors.add(ActionErrors.GLOBAL_ERROR, new ActionMessage("error.general.invalid.id", "Percolator analysis"));
    				saveErrors( request, errors );
    				return mapping.findForward("Failure");
    			}
    		}
    		// If we still don't have a valid id, return an error
    		if(searchAnalysisId == 0) {
    			ActionErrors errors = new ActionErrors();
    			errors.add(ActionErrors.GLOBAL_ERROR, new ActionMessage("error.general.invalid.id", "Percolator analysis"));
    			saveErrors( request, errors );
    			return mapping.findForward("Failure");
    		}

    		// If this is a brand new form
    		if(myForm.getSearchAnalysisId() == 0) {
    			myForm.setSearchAnalysisId(searchAnalysisId);
    			myForm.setShowModified(true);
    			myForm.setShowUnmodified(true);
    			myForm.setExactPeptideMatch(true);
    			myForm.setMaxQValue("0.01");
    			myForm.setSortBy(SORT_BY.QVAL);
    			myForm.setSortOrder(SORT_ORDER.ASC);

    			List<Integer> searchIds = DAOFactory.instance().getMsSearchAnalysisDAO().getSearchIdsForAnalysis(searchAnalysisId);
    			// We should only have one searchID
    			if(searchIds.size() == 1) {
    				List<MsResidueModification> mods = DAOFactory.instance().getMsSearchModDAO().loadDynamicResidueModsForSearch(searchIds.get(0));
    				List<SelectableModificationBean> modBeans = new ArrayList<SelectableModificationBean>(mods.size());
    				for(MsResidueModification mod: mods) {
    					SelectableModificationBean modBean = new SelectableModificationBean();
    					modBean.setId(mod.getId());
    					modBean.setModificationMass(mod.getModificationMass());
    					modBean.setModificationSymbol(mod.getModificationSymbol());
    					modBean.setModifiedResidue(mod.getModifiedResidue());
    					modBean.setSelected(true);
    					modBeans.add(modBean);
    				}
    				myForm.setModificationList(modBeans);
    			}
    		}

    		boolean usePeptideResults = myForm.isPeptideResults();

    		// GET THE SUMMARY 
    		List<Integer> projectIds = new ArrayList<Integer>();
    		List<Integer> experimentIds = new ArrayList<Integer>();
    		String programString = "";
    		int numResults = 0;
    		int numResultsFiltered = 0;


    		MsSearchAnalysis analysis = DAOFactory.instance().getMsSearchAnalysisDAO().load(searchAnalysisId);
    		programString = analysis.getAnalysisProgram()+" "+analysis.getAnalysisProgramVersion();


    		List<Integer> searchIds = DAOFactory.instance().getMsSearchAnalysisDAO().getSearchIdsForAnalysis(searchAnalysisId);
    		MsSearchDAO searchDao = DAOFactory.instance().getMsSearchDAO();


    		for(int searchId: searchIds) {
    			MsSearch search = searchDao.loadSearch(searchId);
    			experimentIds.add(search.getExperimentId());
    		}
    		if(experimentIds.size() > 0) {
    			// Get the projects for these experiments
    			ProjectExperimentDAO projExpDao = ProjectExperimentDAO.instance();
    			projectIds = projExpDao.getProjectIdsForExperiments(experimentIds);
    		}

    		// Does the user have access to look at these results? 
    		for(int projectId: projectIds) {
    			Project project;
    			try {
    				project = ProjectFactory.getProject(projectId);
    				if (!project.checkReadAccess(user.getResearcher())) {

    					// This user doesn't have access to this project.
    					ActionErrors errors = new ActionErrors();
    					errors.add("username", new ActionMessage("error.project.noaccess"));
    					saveErrors( request, errors );
    					return mapping.findForward("Failure");
    				}
    			} catch (Exception e) {

    	    		log.error( "Error loading project, projectId: " + projectId + ", searchAnalysisId: " + searchAnalysisId, e);

    				// Couldn't load the project.
    				ActionErrors errors = new ActionErrors();
    				errors.add("username", new ActionMessage("error.project.projectnotfound"));
    				saveErrors( request, errors );
    				return mapping.findForward("Failure");  
    			}
    		}

    		// Get ALL the filtered and sorted resultIds
    		numResults = getUnfilteredResultCount(searchAnalysisId, usePeptideResults);
    		// peptide IDs if we are getting peptide-level results. Otherwise, psm IDs
    		List<Integer> resultIds = getFilteredResultIds(searchAnalysisId, myForm);

    		if(myForm.isDoDownload()) {
    			// Forward to the Download action
    			request.setAttribute("analysisId", analysis.getId());
    			request.setAttribute("analysisResultIds", resultIds);
    			request.setAttribute("analysisProgram", analysis.getAnalysisProgram());
    			request.setAttribute("filterForm", myForm);
    			return mapping.findForward("Download");
    		}
    		numResultsFiltered = resultIds.size();


    		// Extract the ones we will display
    		int numResultsPerPage = myForm.getNumPerPage();
    		int pageNum = myForm.getPageNum();
    		if(pageNum <= 0) {
    			pageNum = 1;
    			myForm.setPageNum(pageNum);
    		}
    		ResultsPager pager = ResultsPager.instance();
    		boolean desc = false;
    		if(myForm.getSortOrder() != null)
    			desc = myForm.getSortOrder() == SORT_ORDER.DESC ? true : false;
    		// TODO if the pageNum is out of range .....
    		List<Integer> forPage = pager.page(resultIds, pageNum, numResultsPerPage, desc);



    		// Get details for the result we will display
    		// Set up for tabular display
    		Tabular tabResults = getTabularPercolatorResults(analysis, forPage, numResultsPerPage, 
    				myForm, usePeptideResults);

    		((Pageable)tabResults).setCurrentPage(pageNum);
    		((Pageable)tabResults).setNumPerPage(numResultsPerPage);
    		int pageCount = pager.getPageCount(resultIds.size(), numResultsPerPage);
    		((Pageable)tabResults).setLastPage(pageCount);
    		List<Integer> pageList = pager.getPageList(resultIds.size(), pageNum, numResultsPerPage);
    		((Pageable)tabResults).setDisplayPageNumbers(pageList);



    		// required attributes in the request
    		request.setAttribute("projectIds", projectIds);
    		request.setAttribute("experimentIds", experimentIds);
    		request.setAttribute("program", programString);
    		request.setAttribute("numResults", numResults);
    		request.setAttribute("numResultsFiltered", numResultsFiltered);

    		request.setAttribute("filterForm", myForm);
    		request.setAttribute("results", tabResults);
    		request.setAttribute("searchAnalysisId", searchAnalysisId);

    	} catch (Exception e) {

    		log.error( "Error processing request, searchAnalysisId: " + searchAnalysisId, e);
    		
    		throw e;
    	}


        // Forward them on to the happy success view page!
        return mapping.findForward("Success");
    }
    
    // ----------------------------------------------------------------------------------------
    // UN-FILTERED RESULT COUNT
    // Returns the number of peptides if usePeptideResults == true
    // Returns the number or PSMs if usePeptideResults == false
    // ----------------------------------------------------------------------------------------
    private int getUnfilteredResultCount(int searchAnalysisId, boolean usePeptideResults) {
        
    	if(usePeptideResults) {
    		PercolatorPeptideResultDAO peptResDao = DAOFactory.instance().getPercolatorPeptideResultDAO();
    		return peptResDao.peptideCountForAnalysis(searchAnalysisId);
    	}
    	else {
    		PercolatorResultDAO presDao = DAOFactory.instance().getPercolatorResultDAO();
    		return presDao.numAnalysisResults(searchAnalysisId);
    	}
        
    }
    
    // ----------------------------------------------------------------------------------------
    // FILTERED RESULT IDS
    // ----------------------------------------------------------------------------------------
    /*
     * Returns peptide IDs if we are getting peptide-level results. Otherwise, psm IDs are returned
     */
    private List<Integer> getFilteredResultIds(int searchAnalysisId, PercolatorFilterResultsForm myForm) {
        
    	if(myForm.isPeptideResults()) {
    		PercolatorPeptideResultDAO peptResDao = DAOFactory.instance().getPercolatorPeptideResultDAO();
    		return peptResDao.loadIdsForSearchAnalysis(searchAnalysisId, 
    				((PercolatorFilterResultsForm)myForm).getFilterCriteria(), myForm.getSortCriteria());
    	}
    	else {
    		PercolatorResultDAO presDao = DAOFactory.instance().getPercolatorResultDAO();
    		if(myForm.isPeptidesView())
    			return presDao.loadIdsForSearchAnalysisUniqPeptide(searchAnalysisId, 
    				((PercolatorFilterResultsForm)myForm).getFilterCriteria(), myForm.getSortCriteria());
    		else
    			return presDao.loadIdsForSearchAnalysis(searchAnalysisId, 
    					((PercolatorFilterResultsForm)myForm).getFilterCriteria(), 
    					myForm.getSortCriteria());
    	}
    }
    
    
    // ----------------------------------------------------------------------------------------
    // TABULAR RESULTS
    // ----------------------------------------------------------------------------------------
    // -------PERCOLATOR RESULTS
    private Tabular getTabularPercolatorResults(MsSearchAnalysis analysis,
            List<Integer> forPage, int numResultsPerPage, AnalysisFilterResultsForm myForm,
            boolean usePeptideResults) {
        
    	if(usePeptideResults)
    		return getPeptideResults(analysis, forPage, numResultsPerPage, myForm);
    	else
    		return getPsmResults(analysis, forPage, numResultsPerPage, myForm);
    }

    private Tabular getPeptideResults(MsSearchAnalysis analysis,
			List<Integer> forPage, int numResultsPerPage,
			AnalysisFilterResultsForm myForm) {
		
        
        PercolatorPeptideResultDAO peptResDao = DAOFactory.instance().getPercolatorPeptideResultDAO();
        List<PercolatorPeptideResult> results = new ArrayList<PercolatorPeptideResult>(numResultsPerPage);
        
        for(Integer percPeptideId: forPage) {
            PercolatorPeptideResult result = peptResDao.load(percPeptideId);
            if(result != null)
            	results.add(result);
        }

        TabularPercolatorPeptideResults tabResults = new TabularPercolatorPeptideResults(results);
        tabResults.setSortedColumn(myForm.getSortBy());
        tabResults.setSortOrder(myForm.getSortOrder());
        
        return tabResults;
	}
    
	private Tabular getPsmResults(MsSearchAnalysis analysis,
			List<Integer> forPage, int numResultsPerPage,
			AnalysisFilterResultsForm myForm) {
		
		// Get details for the result we will display
        Map<Integer, String> filenameMap = getFileNames(analysis.getId());
        
        MsScanDAO scanDao = DAOFactory.instance().getMsScanDAO();
        SequestSearchResultDAO seqResDao = DAOFactory.instance().getSequestResultDAO();
        
        ProlucidSearchResultDAO prolucidResDao = DAOFactory.instance().getProlucidResultDAO();
        
        // Do we have Bullseye results for the searched files
        boolean hasBullsEyeArea = false;
        List<Integer> searchIds = DAOFactory.instance().getMsSearchAnalysisDAO().getSearchIdsForAnalysis(analysis.getId());
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
        
        PercolatorPeptideResultDAO peptResDao = DAOFactory.instance().getPercolatorPeptideResultDAO();
        boolean hasPeptideResults = false;
        if(peptResDao.peptideCountForAnalysis(myForm.getSearchAnalysisId()) > 0) {
      	  hasPeptideResults = true;
        }
        
        PercolatorResultDAO presDao = DAOFactory.instance().getPercolatorResultDAO();
        MS2ScanDAO ms2ScanDao = DAOFactory.instance().getMS2FileScanDAO();
        List<PercolatorResultPlus> results = new ArrayList<PercolatorResultPlus>(numResultsPerPage);
        for(Integer percResultId: forPage) {
            PercolatorResult result = presDao.loadForPercolatorResultId(percResultId);
            PercolatorResultPlus resPlus = null;
            
            if(hasBullsEyeArea) {
                MS2Scan scan = ms2ScanDao.loadScanLite(result.getScanId());
                resPlus = new PercolatorResultPlus(result, scan);
            }
            else {
                MsScan scan = scanDao.loadScanLite(result.getScanId());
                resPlus = new PercolatorResultPlus(result, scan);
            }
            
            if(hasPeptideResults) {
            	resPlus.setPeptideResult(peptResDao.load(result.getPeptideResultId()));
            }
            
            resPlus.setFilename(filenameMap.get(result.getRunSearchAnalysisId()));
            
//            Program analysisProgram = analysis.getAnalysisProgram();
//            
//            String analysisProgramDisplayName = analysisProgram.toString();
            
            
            //   Does not work since Program.PERCOLATOR == analysisProgram
            
//            if ( Program.PROLUCID == analysisProgram ) {

            
            SequestSearchResult sequestSearchResult = seqResDao.load(result.getId());
            
            if ( sequestSearchResult != null ) {
            	
           		resPlus.setSequestData(sequestSearchResult.getSequestResultData());
           		
            } else {
           		
            	ProlucidSearchResult prolucidSearchResult = prolucidResDao.load(result.getId());
            	
            	if ( prolucidSearchResult != null ) {
            	
            		resPlus.setProlucidData(prolucidSearchResult.getProlucidResultData());
            	}
            }
            
            results.add(resPlus);
        }
        
        // Which version of Percolator are we using
        String version = analysis.getAnalysisProgramVersion();
        boolean hasPEP = true;
        try {
            float vf = Float.parseFloat(version.trim());
            if(vf < 1.06)   hasPEP = false;
        }
        catch(NumberFormatException e){
            log.error("Cannot determine if this version of Percolator prints PEP. Version: "+version);
        }

        
        
        TabularPercolatorResults tabResults = new TabularPercolatorResults(results, myForm.getSearchAnalysisId(), hasPEP, hasBullsEyeArea, hasPeptideResults);
        tabResults.setSortedColumn(myForm.getSortBy());
        tabResults.setSortOrder(myForm.getSortOrder());
        
        return tabResults;
	}
    
    // ----------------------------------------------------------------------------------------
    // FILENAMES FOR THE ANALYSIS ID
    // ----------------------------------------------------------------------------------------
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
