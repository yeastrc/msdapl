/**
 * 
 */
package org.yeastrc.ms.service;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.yeastrc.ms.dao.DAOFactory;
import org.yeastrc.ms.dao.general.MsExperimentDAO;
import org.yeastrc.ms.dao.search.MsRunSearchDAO;
import org.yeastrc.ms.dao.search.MsSearchDAO;
import org.yeastrc.ms.domain.general.MsExperiment;
import org.yeastrc.ms.domain.search.MsSearch;
import org.yeastrc.ms.service.UploadException.ERROR_CODE;
import org.yeastrc.ms.service.percolator.PercolatorXmlDataUploadService;
import org.yeastrc.ms.service.sqtfile.PercolatorSQTDataUploadService;

/**
 * MsAnalysisUploader.java
 * @author Vagisha Sharma
 * Oct 27, 2010
 * 
 */
public class MsAnalysisUploader {

	private static final Logger log = Logger.getLogger(MsExperimentUploader.class);

	private String analysisDirectory;
	private AnalysisDataUploadService adus;

	private StringBuilder preUploadCheckMsg;

	private int searchId = 0; // uploaded searchId
	private int experimentId = 0; // uploaded experimentId
	private int searchAnalysisId = 0;
	private String comments;
	
	private List<UploadException> uploadExceptionList = new ArrayList<UploadException>();


	public MsAnalysisUploader(int experimentId) {
		this.experimentId = experimentId;
	}

	public void setAnalysisDirectory(String analysisDirectory) {
		if(analysisDirectory == null)
			return;
		this.analysisDirectory = analysisDirectory.trim();
	}

	public int getSearchAnalysisId() {
		return this.searchAnalysisId;
	}
	
	public void setSearchAnalysisId(int searchAnalysisId) {
		this.searchAnalysisId = searchAnalysisId;
	}
	
	public void setComments(String comments) {
		this.comments = comments;
	}
	
	public void uploadData() {


		// Make sure there is an experiment with the given experimentId
		MsExperimentDAO exptDao = DAOFactory.instance().getMsExperimentDAO();
		MsExperiment expt = exptDao.loadExperiment(experimentId);
		if (expt == null) {
			UploadException ex = new UploadException(ERROR_CODE.EXPT_NOT_FOUND);
			ex.appendErrorMessage("Experiment ID: "+experimentId+" does not exist in the database.");
			ex.appendErrorMessage("!!!ANALYSIS WILL NOT BE UPLOADED!!!");
			uploadExceptionList.add(ex);
			deleteAnalysis(this.searchAnalysisId);
			log.error(ex.getMessage(), ex);
			return;
		}

		// Make sure there is a search has been uploaded for this experiment
		this.searchId = 0;
		try {
			// An experiment can have multiple searches
			// Need to think about how to handle this.
			searchId = getExperimentSearchId(this.experimentId);
		}
		catch (Exception e) {
			UploadException ex = new UploadException(ERROR_CODE.PREUPLOAD_CHECK_FALIED);
			ex.appendErrorMessage(e.getMessage());
			ex.appendErrorMessage("Analysis will not be uploaded\n");
			uploadExceptionList.add(ex);
			deleteAnalysis(this.searchAnalysisId);
			log.error(ex.getMessage());
			log.error("ABORTING ANALYSIS UPLOAD!!!\n\tTime: "+(new Date()).toString()+"\n\n");
			return;
		}
		if(searchId == 0) {
			UploadException ex = new UploadException(ERROR_CODE.PREUPLOAD_CHECK_FALIED);
			ex.appendErrorMessage("No search ID found for experiment "+experimentId);
			ex.appendErrorMessage("Analysis will not be uploaded\n");
			uploadExceptionList.add(ex);
			deleteAnalysis(this.searchAnalysisId);
			log.error(ex.getMessage());
			log.error("ABORTING ANALYSIS UPLOAD!!!\n\tTime: "+(new Date()).toString()+"\n\n");
			return;
		}


		log.info("INITIALIZING ANALYSIS UPLOAD"+
				"\n\tTime: "+(new Date().toString()));

		// ----- INITIALIZE THE UPLOADER
		try {
			initializeUploader();
		}
		catch (UploadException e) {
			uploadExceptionList.add(e);
			deleteAnalysis(this.searchAnalysisId);
			log.error(e.getMessage(), e);
			return;
		}

		// ----- CHECKS BEFORE BEGINNING UPLOAD -----
		log.info("Starting pre-upload checks..");
		if(!adus.preUploadCheckPassed()) {
			UploadException ex = new UploadException(ERROR_CODE.PREUPLOAD_CHECK_FALIED);
			ex.appendErrorMessage(adus.getPreUploadCheckMsg());
			uploadExceptionList.add(ex);
			deleteAnalysis(this.searchAnalysisId);
			log.error(ex.getMessage(), ex);
			return;
		}
		log.info(adus.getPreUploadCheckMsg());


		// ----- NOW WE CAN BEGIN THE UPLOAD -----
		logBeginUpload();
		long start = System.currentTimeMillis();

		// ----- UPDATE THE LAST UPDATE DATE FOR THE EXPERIMENT
		updateLastUpdateDate(experimentId);


		// ----- UPLOAD ANALYSIS DATA
		// disable keys
		try {
			disableAnalysisTableKeys();
		}
		catch (SQLException e) {
			UploadException ex = new UploadException(ERROR_CODE.ERROR_SQL_DISABLE_KEYS, e);
			uploadExceptionList.add(ex);
			deleteAnalysis(this.searchAnalysisId);
			log.error(ex.getMessage(), ex);
			log.error("ABORTING EXPERIMENT UPLOAD!!!\n\tTime: "+(new Date()).toString()+"\n\n");
			return;
		}

		log.info("BEGINNING upload of analysis results");
		try {
			adus.upload();
			this.searchAnalysisId = adus.getUploadedAnalysisIds().get(0);
		}
		catch (UploadException ex) {
			uploadExceptionList.add(ex);
			deleteAnalysis(this.searchAnalysisId);
			log.error(ex.getMessage(), ex);
			log.error("ABORTING ANALYSIS UPLOAD!!!\n\tTime: "+(new Date()).toString()+"\n\n");

			// enable keys
			try {
				enableAnalysisTableKeys();
			}
			catch(SQLException e){log.error("Error enabling keys");}

			return;
		}

		// enable keys
		try {
			enableAnalysisTableKeys();
		}
		catch (SQLException e) {
			UploadException ex = new UploadException(ERROR_CODE.ERROR_SQL_ENABLE_KEYS, e);
			uploadExceptionList.add(ex);
			deleteAnalysis(this.searchAnalysisId);
			log.error(ex.getMessage(), ex);
			log.error("ABORTING ANALYSIS UPLOAD!!!\n\tTime: "+(new Date()).toString()+"\n\n");
			return;
		}

		long end = System.currentTimeMillis();
		logEndUpload(start, end);
	}

	private void deleteAnalysis(int searchAnalysisId) {
		log.info("DELETING analysisID: "+searchAnalysisId);
		if(adus != null)
			adus.deleteAnalysis(searchAnalysisId);
		else 
			DAOFactory.instance().getMsSearchAnalysisDAO().delete(searchAnalysisId);
	}

	private void initializeUploader() throws UploadException  {

		log.info("Initializing AnalysisDataUploadService");
		log.info("\tDirectory: "+analysisDirectory);

		try {
			adus = UploadServiceFactory.instance().getAnalysisDataUploadService(analysisDirectory);
			adus.setSearchId(this.searchId);
			MsSearch search = DAOFactory.instance().getMsSearchDAO().loadSearch(searchId);
			adus.setSearchProgram(search.getSearchProgram());
			List<String> searchFileNames = getSearchFileNames(searchId);
			adus.setSearchDataFileNames(searchFileNames);
			adus.setComments(comments);
			
			if(this.searchAnalysisId != 0) {
				if(adus instanceof PercolatorSQTDataUploadService) {
					((PercolatorSQTDataUploadService)adus).setAnalysisId(searchAnalysisId);
				}
				else if(adus instanceof PercolatorXmlDataUploadService) {
					((PercolatorXmlDataUploadService)adus).setAnalysisId(searchAnalysisId);
				}
			}
		}
		catch (UploadServiceFactoryException e1) {
			UploadException ex = new UploadException(ERROR_CODE.PREUPLOAD_CHECK_FALIED);
			ex.appendErrorMessage("Error getting AnalysisDataUploadService: "+e1.getMessage());
			throw ex;
		}
		log.info(adus.getClass().getName());
	}

	public boolean preUploadCheckPassed() {

		boolean passed = true;
		// Analysis data uploader check
		log.info("Doing pre-upload check for analysis data uploader....");
		if(adus == null) {
			appendToMsg("AnalysisDataUploader was null");
			passed = false;
		}
		else {
			if(!adus.preUploadCheckPassed()) {
				appendToMsg(adus.getPreUploadCheckMsg());
				passed = false;
			}
		}
		if(!passed) log.info("...FAILED");
		else        log.info("...PASSED");

		return passed;
	}

	private List<String> getSearchFileNames(int searchId) {

		MsRunSearchDAO rsDao = DAOFactory.instance().getMsRunSearchDAO();
		List<Integer> runSearchIds = rsDao.loadRunSearchIdsForSearch(searchId);
		List<String> searchFileNames = new ArrayList<String>(runSearchIds.size());
		for(Integer runSearchId: runSearchIds)
			searchFileNames.add(rsDao.loadFilenameForRunSearch(runSearchId)+".sqt");
		return searchFileNames;
	}

	private int getExperimentSearchId(int experimentId) throws Exception {

		MsSearchDAO searchDao = DAOFactory.instance().getMsSearchDAO();
		List<Integer> searchIds = searchDao.getSearchIdsForExperiment(experimentId);

		if(searchIds.size() == 0)
			return 0;

		if(searchIds.size() > 1) {
			throw new Exception("Multiple search ids found for experimentID: "+experimentId);
		}

		return searchIds.get(0);
	}

	private void appendToMsg(String msg) {
		preUploadCheckMsg.append(msg+"\n");
	}

	private void logEndUpload(long start, long end) {
		log.info("END ANALYSIS UPLOAD: "+((end - start)/(1000L))+"seconds"+
				"\n\tTime: "+(new Date().toString())+"\n"+
				adus.getUploadSummary());
	}

	private void logBeginUpload() {

		StringBuilder msg = new StringBuilder();
		msg.append("BEGIN ANALYSIS UPLOAD");
		msg.append("\n\t\t Directory: "+this.analysisDirectory);
		msg.append("\n\tTime: "+(new Date().toString()));
		log.info(msg.toString());
	}

	private void updateLastUpdateDate(int experimentId) {
		MsExperimentDAO experimentDao = DAOFactory.instance().getMsExperimentDAO();
		experimentDao.updateLastUpdateDate(experimentId);

	}

	private void disableAnalysisTableKeys() throws SQLException {

		// disable keys on msRunSearchResult table
		//        log.info("Disabling keys on PercolatorResult table");
		//        DAOFactory.instance().getPercolatorResultDAO().disableKeys();

		//        log.info("Disabled keys");
	}

	private void enableAnalysisTableKeys() throws SQLException {

		// enable keys on msRunSearchResult table
		//        log.info("Enabling keys on PercolatorResult table");
		//        DAOFactory.instance().getPercolatorResultDAO().enableKeys();

		//        log.info("Enabled keys");
	}
	
	public List<UploadException> getUploadExceptionList() {
        return this.uploadExceptionList;
    }
    
    public String getUploadWarnings() {
        StringBuilder buf = new StringBuilder();
        for (UploadException e: uploadExceptionList) {
            buf.append(e.getMessage()+"\n");
        }
        return buf.toString();
    }
}
