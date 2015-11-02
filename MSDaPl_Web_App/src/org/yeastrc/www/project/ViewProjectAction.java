/*
 *
 * Created on February 5, 2004
 *
 * Created by Michael Riffle <mriffle@u.washington.edu>
 *
 */

package org.yeastrc.www.project;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.yeastrc.experiment.ExperimentProteinProphetRun;
import org.yeastrc.experiment.ExperimentProteinferRun;
import org.yeastrc.experiment.ExperimentSearch;
import org.yeastrc.experiment.ProjectExperiment;
import org.yeastrc.experiment.ProjectExperimentDAO;
import org.yeastrc.experiment.ProjectProteinInferBookmarkDAO;
import org.yeastrc.experiment.SearchAnalysis;
import org.yeastrc.experiment.proteinfer.ProteinferRunSummaryLookup;
import org.yeastrc.experiment.stats.FileStats;
import org.yeastrc.experiment.stats.QCStatsGetter;
import org.yeastrc.jobqueue.JobUtils;
import org.yeastrc.jobqueue.MSJob;
import org.yeastrc.jobqueue.MSJobFactory;
import org.yeastrc.jobqueue.MsAnalysisUploadJob;
import org.yeastrc.ms.dao.DAOFactory;
import org.yeastrc.ms.dao.ProteinferDAOFactory;
import org.yeastrc.ms.dao.analysis.MsSearchAnalysisDAO;
import org.yeastrc.ms.dao.analysis.peptideProphet.PeptideProphetRocDAO;
import org.yeastrc.ms.dao.protinfer.ibatis.ProteinferRunDAO;
import org.yeastrc.ms.dao.protinfer.proteinProphet.ProteinProphetRunDAO;
import org.yeastrc.ms.domain.analysis.MsSearchAnalysis;
import org.yeastrc.ms.domain.analysis.peptideProphet.PeptideProphetROC;
import org.yeastrc.ms.domain.general.MsExperiment;
import org.yeastrc.ms.domain.general.MsInstrument;
import org.yeastrc.ms.domain.protinfer.ProteinInferenceProgram;
import org.yeastrc.ms.domain.protinfer.ProteinferRun;
import org.yeastrc.ms.domain.protinfer.ProteinferRunSummary;
import org.yeastrc.ms.domain.protinfer.proteinProphet.ProteinProphetRun;
import org.yeastrc.ms.domain.protinfer.proteinProphet.ProteinProphetRunSummary;
import org.yeastrc.ms.domain.search.MsSearch;
import org.yeastrc.ms.domain.search.Program;
import org.yeastrc.project.Project;
import org.yeastrc.project.ProjectFactory;
import org.yeastrc.properties.ApplicationProperties;
import org.yeastrc.qc_plots.plot_intensity_per_experiment.service.QC_Plot_IntensityPerExperiment_Plotter;
import org.yeastrc.qc_plots.plot_peaks_per_scan_per_experiment.service.QC_Plot_PeaksPerScanPerExperiment_Plotter;
import org.yeastrc.qc_plots.premz_scan_count_plot.service.PreMZScanCountPlotter;
import org.yeastrc.www.project.experiment.QCPlot;
import org.yeastrc.www.proteinfer.job.ProteinInferJobSearcher;
import org.yeastrc.www.proteinfer.job.ProteinferJob;
import org.yeastrc.www.user.User;
import org.yeastrc.www.user.UserUtils;
import org.yeastrc.yates.YatesRun;
import org.yeastrc.yates.YatesRunMsSearchLinker;
import org.yeastrc.yates.YatesRunSearcher;

/**
 * 
 */
public class ViewProjectAction extends Action {

	private static final int EXPERIMENT_DETAILS_INITIAL_LOAD_LIMIT = 5;
    
    private static DAOFactory daoFactory = DAOFactory.instance();
    private static final Logger log = Logger.getLogger(ViewProjectAction.class.getName());
    
	public ActionForward execute( ActionMapping mapping,
								  ActionForm form,
								  HttpServletRequest request,
								  HttpServletResponse response )
	throws Exception {
		// Get the projectID they're after
		int projectID;
		
		// User making this request
		User user = UserUtils.getUser(request);
		if (user == null) {
			ActionErrors errors = new ActionErrors();
			errors.add("username", new ActionMessage("error.login.notloggedin"));
			saveErrors( request, errors );
			return mapping.findForward("authenticate");
		}


		try {
			String strID = request.getParameter("ID");

			if (strID == null || strID.equals("")) {
			    strID = request.getParameter("projectId");
			    
			    if(strID == null || strID.equals("")) {
			        ActionErrors errors = new ActionErrors();
			        errors.add("username", new ActionMessage("error.project.noprojectid"));
			        saveErrors( request, errors );
			        return mapping.findForward("Failure");
			    }
			}

			projectID = Integer.parseInt(strID);

		} catch (NumberFormatException nfe) {
			ActionErrors errors = new ActionErrors();
			errors.add("username", new ActionMessage("error.project.invalidprojectid"));
			saveErrors( request, errors );
			return mapping.findForward("Failure");
		}

		// Load our project
		Project project;
		
		try {
			project = ProjectFactory.getProject(projectID);
			if (!project.checkReadAccess(user.getResearcher())) {
				
				// This user doesn't have access to this project.
				ActionErrors errors = new ActionErrors();
				errors.add("username", new ActionMessage("error.project.noaccess"));
				saveErrors( request, errors );
				return mapping.findForward("Failure");
			}
		} catch (Exception e) {
			
			// Couldn't load the project.
			ActionErrors errors = new ActionErrors();
			errors.add("username", new ActionMessage("error.project.projectnotfound"));
			saveErrors( request, errors );
			return mapping.findForward("Failure");	
		}

		// Set this project in the request, as a bean to be displayed on the view
		request.setAttribute("project", project);
		
		boolean writeAccess = false;
		if(project.checkAccess(user.getResearcher()))
		    writeAccess = true;
		request.setAttribute("writeAccess", writeAccess);
		
		// Check for experiment data for this project  (  get details for the last EXPERIMENT_DETAILS_INITIAL_LOAD_LIMIT experiments uploaded  )
		List<ProjectExperiment> experiments = getProjectExperiments(projectID, EXPERIMENT_DETAILS_INITIAL_LOAD_LIMIT); 
		
		
		// get any bookmarked protein inferences
		List<Integer> bookmarked = ProjectProteinInferBookmarkDAO.getInstance().getBookmarkedProteinInferenceIds(projectID);
    	Collections.sort(bookmarked);
		List<ExperimentProteinferRun> piRuns = getBookmarkedProteinferRuns(bookmarked);
		List<ExperimentProteinProphetRun> prophetRuns = getBookmarkedProteinProphetRuns(bookmarked);
		if(prophetRuns != null && prophetRuns.size() > 0) {
			// sort by filename
			Collections.sort(prophetRuns, new Comparator<ExperimentProteinProphetRun>() {
				@Override
				public int compare(ExperimentProteinProphetRun o1, ExperimentProteinProphetRun o2) {
					return o1.getProteinProphetRun().getFilename().compareTo(o2.getProteinProphetRun().getFilename());
				}});
		}
		
		if(bookmarked.size() > 0) {
			request.setAttribute("hasBookmarks", true);
		}
		request.setAttribute("proteinInferBookmarks", piRuns);
		request.setAttribute("proteinProphetBookmarks", prophetRuns);
		
		
		// load the dtaselect results;
		// Check for yates MS data
        YatesRunSearcher yrs = new YatesRunSearcher();
        yrs.setProjectID(project.getID());
        yrs.setMostRecent( true );
        List<YatesRun> yatesRuns = yrs.search();
        
        
        // Associate the DTASelect runs with the respective experiments
        linkExperimentsAndDtaSelect(experiments, yatesRuns);
		
        
        request.setAttribute("experiments", experiments);
		
		
		// TODO Check uploads for a project

		
		// Should the user be able to upload data to this project.
        // If the user has write access to the project he/she should be able to upload data
        if(writeAccess)
        	request.setAttribute( "showMSDataUpload", true );
        

		// Set a list of available instruments in the request. 
		List<MsInstrument> instrumentList = DAOFactory.instance().getInstrumentDAO().loadAllInstruments();
		request.setAttribute("instrumentList", instrumentList);
		
		// Is the option to run Percolator available
		if(ApplicationProperties.canRunPercolator())
			request.setAttribute("canRunPercolator", "true");
		
	
		// Forward them on to the happy success view page!
		return mapping.findForward("Success");

	}


    private List<ExperimentProteinProphetRun> getBookmarkedProteinProphetRuns(
			List<Integer> bookmarked) {
    	return this.getProteinProphetRuns(bookmarked);
	}


	private List<ExperimentProteinferRun> getBookmarkedProteinferRuns(
			List<Integer> bookmarked) {
		return getProteinInferRuns(bookmarked);
	}


	private List<ProjectExperiment> getProjectExperiments(int projectId, int limitCount) throws Exception {
        
        List<Integer> experimentIds = ProjectExperimentDAO.instance().getExperimentIdsForProject(projectId);
        Collections.sort(experimentIds, Collections.reverseOrder());
        
        if(experimentIds.size() == 0)
            return new ArrayList<ProjectExperiment>(0);
        
        
        List<ProjectExperiment> experiments = new ArrayList<ProjectExperiment>(experimentIds.size());
        
        int count = 0;
        for(int experimentId: experimentIds) {
        	
            MsExperiment expt = daoFactory.getMsExperimentDAO().loadExperiment(experimentId);
            
            // First check if this experiment is still getting uploaded
            // Add to list only if the upload is failed or complete.
            MSJob job = null;
            int status = 0;
            try {
                job = MSJobFactory.getInstance().getMsJobForProjectExperiment(projectId, experimentId);
                status = job.getStatus();
                if(status == JobUtils.STATUS_QUEUED || status == JobUtils.STATUS_OUT_FOR_WORK)
                    continue;
            }
            catch(Exception e) {
            	log.error("No job found for experimentID: "+experimentId, e);
            	continue;
            } 
            
            ProjectExperiment pExpt = new ProjectExperiment(expt);
            pExpt.setProjectId(projectId);
            pExpt.setUploadJobId(job.getId());
            if(status != JobUtils.STATUS_COMPLETE)
                pExpt.setUploadSuccess(false);
            
            experiments.add(pExpt);
            
            if ( pExpt.isUploadSuccess() ) {
            	
            	//  Load Precursor Mass Chart Data
            	String precursorMassChartData = PreMZScanCountPlotter.getStoredMZScanCountPlotFromDB(experimentId);
            	if ( precursorMassChartData == null ) {
            		precursorMassChartData = "";
            	}
            	pExpt.setPrecursorMassChartData(precursorMassChartData);



            	//  Load Peak Count Chart Data
            	String peakCountChartData = QC_Plot_PeaksPerScanPerExperiment_Plotter.getStoredPeaksPerScanPerExperimentPlotFromDB(experimentId);
            	if ( peakCountChartData == null ) {
            		peakCountChartData = "";
            	}
            	pExpt.setPeakCountChartData( peakCountChartData );


            	//  Load Intensity Count Chart Data
            	String intensityCountChartData = QC_Plot_IntensityPerExperiment_Plotter.getStoredIntensityPerExperimentPlotFromDB( experimentId );
            	if ( intensityCountChartData == null ) {
            		intensityCountChartData = "";
            	}
            	pExpt.setIntensityCountChartData( intensityCountChartData );
            }
            
            
            count++;
            if(count > limitCount) {
            	pExpt.setHasFullInformation(false);
            	continue; // don't get the details if we have hit the limit
            }
            else
            	pExpt.setHasFullInformation(true);
            
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
            Collections.sort(analysisIdsList, Collections.reverseOrder());
            for(int analysisId: analysisIdsList) {
            	SearchAnalysis a = getSearchAnalysis(analysisId, projectId);
            	if(a != null)
            		analyses.add(a);
            }
            pExpt.setAnalyses(analyses);
            
            
            // load protein prophet results, if any
            List<Integer> piRunIds = ProteinInferJobSearcher.getInstance().getProteinferIdsForMsExperiment(experimentId);
            Collections.sort(piRunIds, Collections.reverseOrder());
            
            if(piRunIds.size() > 0)
            	pExpt.setHasProtinferRuns(true);
            
            // loop over and see if any are ProteinProphet runs
            List<ExperimentProteinProphetRun> prophetRunList = getProteinProphetRuns(piRunIds);
            // sort by filename
            if(prophetRunList != null && prophetRunList.size() > 0) {
    			// sort by filename
    			Collections.sort(prophetRunList, new Comparator<ExperimentProteinProphetRun>() {
    				@Override
    				public int compare(ExperimentProteinProphetRun o1, ExperimentProteinProphetRun o2) {
    					return o1.getProteinProphetRun().getFilename().compareTo(o2.getProteinProphetRun().getFilename());
    				}});
    		}
            pExpt.setProteinProphetRun(prophetRunList);
            
            
//            // load the protein inference jobs, if any
//            List<ExperimentProteinferRun> piRuns = getProteinInferRuns(piRunIds);
//            pExpt.setProtInferRuns(piRuns);
//            
//            
            // If any of the protein inferences have been bookmarked, mark them now.
            getBookmarkedProteinInferences(pExpt);
        }
        
        
        return experiments;
    }


	private List<ExperimentProteinProphetRun> getProteinProphetRuns(List<Integer> piRunIds) {
		
		ProteinProphetRunDAO pRunDao = ProteinferDAOFactory.instance().getProteinProphetRunDao();
        ProteinferRunDAO runDao = ProteinferDAOFactory.instance().getProteinferRunDao();
        
		List<ExperimentProteinProphetRun> prophetRunList = new ArrayList<ExperimentProteinProphetRun>();
		for(int piRunId: piRunIds) {
		    ProteinferRun run = runDao.loadProteinferRun(piRunId);
		    if(run.getProgram() == ProteinInferenceProgram.PROTEIN_PROPHET) {
		        ProteinProphetRun ppRun = pRunDao.loadProteinferRun(piRunId);
		        ExperimentProteinProphetRun eppRun = new ExperimentProteinProphetRun(ppRun);
		        
		        ProteinProphetRunSummary summary = ProteinferRunSummaryLookup.getProteinProphetSummary(piRunId);
		        
		        eppRun.setNumParsimoniousProteins(summary.getProteinCount());
		        eppRun.setNumParsimoniousProteinGroups(summary.getIndistGroupCount());
		        eppRun.setNumParsimoniousProteinProphetGroups(summary.getProphetGroupCount());
		        eppRun.setUniqPeptideSequenceCount(summary.getUniqPeptSeqCount());
		        eppRun.setUniqIonCount(summary.getUniqIonCount());
		        prophetRunList.add(eppRun);
		    }
		}
		return prophetRunList;
	}
	
	private List<ExperimentProteinferRun> getProteinInferRuns(List<Integer> piRunIds) {
		
        ProteinferRunDAO runDao = ProteinferDAOFactory.instance().getProteinferRunDao();
        
		List<ExperimentProteinferRun> idPickerRunList = new ArrayList<ExperimentProteinferRun>();
		for(int piRunId: piRunIds) {
			//log.info("Looking at pinfer runID: "+piRunId);
		    ProteinferRun run = runDao.loadProteinferRun(piRunId);
		    if(ProteinInferenceProgram.isIdPicker(run.getProgram())) { // is this a IdPicker run?
		        ProteinferJob job = ProteinInferJobSearcher.getInstance().getJobForPiRunId(piRunId);
		        if(job == null) {
		        	log.warn("No job found for protein inference ID: "+piRunId);
		        	continue;
		        }
		        ExperimentProteinferRun eppRun = new ExperimentProteinferRun(job);
		        eppRun.setName(run.getName());
		        
		        if(job.getStatus() == JobUtils.STATUS_COMPLETE) {
			        ProteinferRunSummary summary = ProteinferRunSummaryLookup.getIdPickerRunSummary(piRunId);
			        
			        eppRun.setNumParsimoniousProteins(summary.getParsimProteinCount());
			        eppRun.setNumParsimoniousProteinGroups(summary.getParsimIndistGroupCount());
			        eppRun.setUniqPeptideSequenceCount(summary.getUniqPeptSeqCount());
			        eppRun.setUniqIonCount(summary.getUniqIonCount());
		        }
		        idPickerRunList.add(eppRun);
		    }
		}
		return idPickerRunList;
	}
    
    private void getBookmarkedProteinInferences(ProjectExperiment pExpt) throws SQLException {
    	
		int projectId = pExpt.getProjectId();
    	List<Integer> bookmarked = ProjectProteinInferBookmarkDAO.getInstance().getBookmarkedProteinInferenceIds(projectId);
    	Collections.sort(bookmarked);
    	
    	for(ExperimentProteinferRun run: pExpt.getProtInferRuns()) {
    		if(Collections.binarySearch(bookmarked, run.getJob().getPinferId()) >= 0)
    			run.setBookmarked(true);
    		else
    			run.setBookmarked(false);
    	}
    	
    	for(ExperimentProteinProphetRun run: pExpt.getProteinProphetRuns()) {
    		if(Collections.binarySearch(bookmarked, run.getProteinProphetRun().getId()) >= 0)
    			run.setBookmarked(true);
    		else
    			run.setBookmarked(false);
    	}
	}
    
    private void getBookmarkedProteinInferences(SearchAnalysis sAnalysis, int projectId) throws SQLException {
    	
    	List<Integer> bookmarked = ProjectProteinInferBookmarkDAO.getInstance().getBookmarkedProteinInferenceIds(projectId);
    	Collections.sort(bookmarked);
    	
    	for(ExperimentProteinferRun run: sAnalysis.getProtInferRuns()) {
    		if(Collections.binarySearch(bookmarked, run.getJob().getPinferId()) >= 0)
    			run.setBookmarked(true);
    		else
    			run.setBookmarked(false);
    	}
    	
    	for(ExperimentProteinProphetRun run: sAnalysis.getProteinProphetRuns()) {
    		if(Collections.binarySearch(bookmarked, run.getProteinProphetRun().getId()) >= 0)
    			run.setBookmarked(true);
    		else
    			run.setBookmarked(false);
    	}
	}


	private ExperimentSearch getExperimentSearch(int searchId) {
        
        MsSearch search = daoFactory.getMsSearchDAO().loadSearch(searchId);
        ExperimentSearch eSearch = new ExperimentSearch(search);
        return eSearch;
    }
    
    private SearchAnalysis getSearchAnalysis(int searchAnalysisId, int projectId) throws SQLException {
        
        MsSearchAnalysis analysis = daoFactory.getMsSearchAnalysisDAO().load(searchAnalysisId);
        SearchAnalysis sAnalysis = new SearchAnalysis(analysis);
        
        try {
			MsAnalysisUploadJob job = MSJobFactory.getInstance().getJobForAnalysis(searchAnalysisId);
			sAnalysis.setJob(job);
		} catch (Exception e) {
			// Analyses uploaded as part of full experiment upload will not have a separate job entry in the tblMsAnalysisUploadJobs table
			log.error("No job found for searchAnalysisID: "+searchAnalysisId);
		}
		
		// associate any protein inferences with this analysis
        List<Integer> piRunIds = ProteinInferJobSearcher.getInstance().getProteinferIdsForMsSearchAnalysis(searchAnalysisId);
        Collections.sort(piRunIds, Collections.reverseOrder());
        
        // load the protein inference jobs, if any
        List<ExperimentProteinferRun> piRuns = getProteinInferRuns(piRunIds);
        sAnalysis.setProtInferRuns(piRuns);
        
        // If any of the protein inferences have been bookmarked, mark them now.
        getBookmarkedProteinInferences(sAnalysis, projectId);
        
        // get QC results for this analysis
        if(analysis.getAnalysisProgram() == Program.PERCOLATOR) {
        	QCStatsGetter qcStatsGetter = new QCStatsGetter();
        	qcStatsGetter.setGetPsmRtStats(true);
        	qcStatsGetter.setGetSpectraRtStats(true);
        	qcStatsGetter.getStats(searchAnalysisId, QCStatsGetter.PERC_QVAL_DEFAULT);
        	FileStats analysisPsmStat = qcStatsGetter.getPsmAnalysisStats();
        	String qcSummaryString = analysisPsmStat.getPercentGoodCount()+"% PSMs at qvalue <= 0.01";
        	sAnalysis.addQcSummaryString(qcSummaryString);
        	FileStats analysisSpectraRtStats = qcStatsGetter.getSpectraAnalysisStats();
        	qcSummaryString = analysisSpectraRtStats.getPercentGoodCount()+"% spectra with results at qvalue <= 0.01";
        	sAnalysis.addQcSummaryString(qcSummaryString);
			 
        	List<QCPlot> qcPlots = new ArrayList<QCPlot>(2);
        	qcPlots.add(new QCPlot(qcStatsGetter.getPsmDistrUrl(), "Retention Time vs # PSM"));
        	qcPlots.add(new QCPlot(qcStatsGetter.getSpectraDistrUrl(), "Retention Time vs # Spectra"));
        	sAnalysis.setQcPlots(qcPlots);
        }
        else if(analysis.getAnalysisProgram() == Program.PEPTIDE_PROPHET) {
        	
        	QCStatsGetter qcStatsGetter = new QCStatsGetter();
        	qcStatsGetter.setGetPsmRtStats(false); // TODO This doesn't look right. Ask Jimmy.
        	qcStatsGetter.setGetSpectraRtStats(true);
        	qcStatsGetter.getStats(searchAnalysisId, QCStatsGetter.PEPPROPHET_ERR_RATE_DEFAULT);
        	// FileStats analysisPsmStat = qcStatsGetter.getPsmAnalysisStats();
        	
        	PeptideProphetRocDAO rocDao = DAOFactory.instance().getPeptideProphetRocDAO();
    		PeptideProphetROC roc = rocDao.loadRoc(analysis.getId());
    		if(roc.getRocPoints().size() > 0)
    		{
	    		double errRate = roc.getClosestError(QCStatsGetter.PEPPROPHET_ERR_RATE_DEFAULT);
	    		double probability = roc.getMinProbabilityForError(errRate);
	    		
	    		FileStats analysisSpectraRtStat = qcStatsGetter.getSpectraAnalysisStats();
	    		String qcSummaryString = "";
	    		if(analysisSpectraRtStat != null)
	    		{
	    			qcSummaryString = analysisSpectraRtStat.getPercentGoodCount()+"% spectra with results at error rate "+errRate+" (prob. "+probability+")";
	    		}
	        	sAnalysis.addQcSummaryString(qcSummaryString);
	        	
	        	List<QCPlot> qcPlots = new ArrayList<QCPlot>(2);
	        	// qcPlots.add(new QCPlot(qcStatsGetter.getPsmDistrUrl(), "Retention Time vs # PSM"));
	        	qcPlots.add(new QCPlot(qcStatsGetter.getSpectraDistrUrl(), "Retention Time vs # Spectra"));
	        	sAnalysis.setQcPlots(qcPlots);
    		}
        }
        
        return sAnalysis;
    }
    
    private void linkExperimentsAndDtaSelect(List<ProjectExperiment> experiments, List<YatesRun> yatesRuns) throws SQLException {
        
        // We will get the searchIds from the tblYatesRun* tables
        // Create a map of searchId and experimentId
        Map<Integer, Integer> searchIdToExperimentId = new HashMap<Integer, Integer>();
        for(ProjectExperiment experiment: experiments) {
        	if(!experiment.getHasFullInformation())
        		continue;
            for(ExperimentSearch search: experiment.getSearches())
                searchIdToExperimentId.put(search.getId(), experiment.getId());
        }
        
        List<Integer> experimentIds = new ArrayList<Integer>(experiments.size());
        for(ProjectExperiment pe: experiments)
            experimentIds.add(pe.getId());
        
        
        // put the DTASelect results in the appropriate experiments
        for(YatesRun run: yatesRuns) {
            int runId = run.getId();
            int searchId = YatesRunMsSearchLinker.linkYatesRunToMsSearch(runId);
            
            if(searchId > 0) {
                Integer experimentId = searchIdToExperimentId.get(searchId);
                if(experimentId != null) {
                    
                	// ProjectExperiments are sorted in reverse order
                    int idx = Collections.binarySearch(experimentIds, experimentId, Collections.reverseOrder());
                    if(idx >= 0) {
                        ProjectExperiment pe = experiments.get(idx);
                        pe.setDtaSelect(run);
                        pe.setHasProtinferRuns(true);
                    }
                    else {
                    	log.error("Could not link DTASelect run to experiment. Yates runID: "
                    			+runId+"; searchID: "+searchId+"; experimentID: "+experimentId);
                    }
                }
            }
            else {
            	log.error("Could not associate a search ID with DTASelect run: "+runId);
            }
        }
    }
}