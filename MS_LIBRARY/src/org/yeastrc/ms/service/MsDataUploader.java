package org.yeastrc.ms.service;

import java.io.File;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.yeastrc.ms.dao.DAOFactory;
import org.yeastrc.ms.dao.general.MsExperimentDAO;
import org.yeastrc.ms.dao.search.MsSearchDAO;
import org.yeastrc.ms.domain.general.MsExperiment;
import org.yeastrc.ms.domain.search.Program;
import org.yeastrc.ms.service.UploadException.ERROR_CODE;
import org.yeastrc.ms.service.ms2file.MS2DataUploadService;

public class MsDataUploader {

    private static final Logger log = Logger.getLogger(MsDataUploader.class);
    
    private List<Integer> uploadedAnalysisIds = new ArrayList<Integer>();
    private int uploadedSearchId;
    private int uploadedExptId;
    private List<UploadException> uploadExceptionList = new ArrayList<UploadException>();
    
    private String comments;
    private int instrumentId;
    private String remoteServer;
    private String spectrumDataDirectory;
    private String remoteSpectrumDataDirectory;
    private String searchDirectory;
    private String remoteSearchDataDirectory;
    private Date searchDate;
    private String analysisDirectory;
    private String protinferDirectory;
    private boolean doScanChargeMassCheck = false; // For MacCoss lab data
    
    private boolean uploadSearch = false;
    private boolean uploadAnalysis = false;
    private boolean uploadProtinfer = false;
    
    private Set<String> filesToUpload;
    private boolean hasLabkeyBullseyeData = false;
    
    public void setComments(String comments) {
        this.comments = comments;
    }
    
    public void setInstrumentId(int instrumentId) {
        this.instrumentId = instrumentId;
    }

    public void setRemoteServer(String remoteServer) {
        this.remoteServer = remoteServer;
    }

    public void setSpectrumDataDirectory(String rawDataDirectory) {
    	if(rawDataDirectory == null)
    		return;
        this.spectrumDataDirectory = rawDataDirectory.trim();
    }

    public void setRemoteSpectrumDataDirectory(String remoteRawDataDirectory) {
    	if(remoteRawDataDirectory == null)
    		return;
        this.remoteSpectrumDataDirectory = remoteRawDataDirectory.trim();
    }

    public Set<String> getFilesToUpload() {
		return filesToUpload;
	}

	public void setFilesToUpload(Set<String> filesToUpload) {
		this.filesToUpload = filesToUpload;
	}
	
	public boolean isHasLabkeyBullseyeData() {
		return hasLabkeyBullseyeData;
	}

	public void setHasLabkeyBullseyeData(boolean hasLabkeyBullseyeData) {
		this.hasLabkeyBullseyeData = hasLabkeyBullseyeData;
	}

	public void setSearchDirectory(String searchDirectory) {
    	if(searchDirectory == null)
    		return;
        this.searchDirectory = searchDirectory.trim();
        if(searchDirectory != null)
            this.uploadSearch = true;
    }

    public void setRemoteSearchDataDirectory(String remoteSearchDataDirectory) {
    	if(remoteSearchDataDirectory == null)
    		return;
        this.remoteSearchDataDirectory = remoteSearchDataDirectory.trim();
    }

    public void setSearchDate(Date searchDate) {
        this.searchDate = searchDate;
    }

    public void setAnalysisDirectory(String analysisDirectory) {
    	if(analysisDirectory == null)
    		return;
        this.analysisDirectory = analysisDirectory.trim();
        if(analysisDirectory != null)
            this.uploadAnalysis = true;
    }
    
    public void setProtinferDirectory(String protinferDirectory) {
    	if(protinferDirectory == null)
    		return;
        this.protinferDirectory = protinferDirectory.trim();
        if(protinferDirectory != null)
            this.uploadProtinfer = true;
    }
    
    public void checkResultChargeMass(boolean doScanChargeMassCheck) {
        this.doScanChargeMassCheck = doScanChargeMassCheck;
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
    

    public List<String> getUploadWarningsStringList() {

    	List<String> uploadWarningsStringList = new ArrayList<String>();

    	for (UploadException e: uploadExceptionList) {
    		uploadWarningsStringList.add( e.getMessage() );
        }
        return uploadWarningsStringList;
    }

    public int getUploadedSearchId() {
        return this.uploadedSearchId;
    }
    
    public int getUploadedExperimentId() {
        return this.uploadedExptId;
    }
    
    public void uploadData() {

        log.info("INITIALIZING EXPERIMENT UPLOAD"+
                "\n\tTime: "+(new Date().toString()));
        
        
        // ----- INITIALIZE THE EXPERIMENT UPLOADER
        MsExperimentUploader exptUploader = null;
        try {
            exptUploader = initializeExperimentUploader();
        }
        catch (UploadException e) {
            uploadExceptionList.add(e);
            log.error(e.getMessage(), e);
            return;
        }
        
        // ----- CHECKS BEFORE BEGINNING UPLOAD -----
        log.info("Starting pre-upload checks..");
        if(!exptUploader.preUploadCheckPassed()) {
            UploadException ex = new UploadException(ERROR_CODE.PREUPLOAD_CHECK_FALIED);
            ex.appendErrorMessage(exptUploader.getPreUploadCheckMsg());
            uploadExceptionList.add(ex);
            log.error(ex.getMessage(), ex);
            return;
        }
        log.info(exptUploader.getPreUploadCheckMsg());
        
        
        // ----- NOW WE CAN BEGIN THE UPLOAD -----
        logBeginExperimentUpload();
        long start = System.currentTimeMillis();
        
        try {
            this.uploadedExptId = exptUploader.uploadSpectrumData();
        }
        catch (UploadException ex) {
            uploadExceptionList.add(ex);
            log.error(ex.getMessage(), ex);
            log.error("ABORTING EXPERIMENT UPLOAD!!!\n\tTime: "+(new Date()).toString()+"\n\n");
            return;
        }
        
        // ----- UPLOAD SEARCH DATA
        if(uploadSearch) {
            
            // disable keys
            try {
                disableSearchTableKeys();
            }
            catch (SQLException e) {
                UploadException ex = new UploadException(ERROR_CODE.ERROR_SQL_DISABLE_KEYS, e);
                uploadExceptionList.add(ex);
                log.error(ex.getMessage(), ex);
                log.error("ABORTING EXPERIMENT UPLOAD!!!\n\tTime: "+(new Date()).toString()+"\n\n");
                return;
            }
            
            log.info("BEGINNING upload of search results");
            try {
                this.uploadedSearchId = exptUploader.uploadSearchData(this.uploadedExptId);
            }
            catch (UploadException ex) {
                uploadExceptionList.add(ex);
                log.error(ex.getMessage(), ex);
                
                // If there was an error making a backup of the SQT files we still go forward.
                if(ex.getErrorCode() != ERROR_CODE.SQT_BACKUP_ERROR) {
                    log.error("ABORTING EXPERIMENT UPLOAD!!!\n\tTime: "+(new Date()).toString()+"\n\n");
                    
                    // enable keys
                    try {
                        enableSearchTableKeys();
                    }
                    catch(SQLException e){log.error("Error enabling keys");}
                    return;
                }
            }
            
            // enable keys
            try {
                enableSearchTableKeys();
            }
            catch (SQLException e) {
                UploadException ex = new UploadException(ERROR_CODE.ERROR_SQL_ENABLE_KEYS, e);
                uploadExceptionList.add(ex);
                log.error(ex.getMessage(), ex);
                log.error("ABORTING EXPERIMENT UPLOAD!!!\n\tTime: "+(new Date()).toString()+"\n\n");
                return;
            }
        }
        
        // ----- UPLOAD ANALYSIS DATA
        if(uploadAnalysis) {
            
            // disable keys
            try {
                disableAnalysisTableKeys();
            }
            catch (SQLException e) {
                UploadException ex = new UploadException(ERROR_CODE.ERROR_SQL_DISABLE_KEYS, e);
                uploadExceptionList.add(ex);
                log.error(ex.getMessage(), ex);
                log.error("ABORTING EXPERIMENT UPLOAD!!!\n\tTime: "+(new Date()).toString()+"\n\n");
                return;
            }
            
            log.info("BEGINNING upload of analysis results");
            try {
                this.uploadedAnalysisIds = exptUploader.uploadAnalysisData(this.uploadedSearchId);
            }
            catch (UploadException ex) {
                uploadExceptionList.add(ex);
                log.error(ex.getMessage(), ex);
                log.error("ABORTING EXPERIMENT UPLOAD!!!\n\tTime: "+(new Date()).toString()+"\n\n");
                
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
                log.error(ex.getMessage(), ex);
                log.error("ABORTING EXPERIMENT UPLOAD!!!\n\tTime: "+(new Date()).toString()+"\n\n");
                return;
            }
        }
        
        // ----- UPLOAD PROTEIN INFERENCE DATA
        if(uploadProtinfer) {
            log.info("BEGINNING upload of protein inference results");
            try {
                exptUploader.uploadProtinferData(this.uploadedSearchId, this.uploadedAnalysisIds);
            }
            catch (UploadException ex) {
                uploadExceptionList.add(ex);
                log.error(ex.getMessage(), ex);
                log.error("ABORTING EXPERIMENT UPLOAD!!!\n\tTime: "+(new Date()).toString()+"\n\n");
                return;
            }
        }
        
        long end = System.currentTimeMillis();
        logEndExperimentUpload(exptUploader, start, end);
        
    }
    
    private void disableSearchTableKeys() throws SQLException {
        
        // disable keys on msRunSearchResult table
//        log.info("Disabling keys on msRunSearchResult table");
//        DAOFactory.instance().getMsSearchResultDAO().disableKeys();
        
        // disable keys on SQTSearchResult
//        log.info("Disabling keys on SQTSearchResult table");
//        DAOFactory.instance().getSequestResultDAO().disableKeys();
//        
//        // disable keys on SQTSpectrumData
//        log.info("Disabling keys on SQTSpectrumData table");
//        DAOFactory.instance().getSqtSpectrumDAO().disableKeys();
//        
//        // disable keys on msProteinMatch
//        log.info("Disabling keys on msProteinMatch table");
//        DAOFactory.instance().getMsProteinMatchDAO().disableKeys();
        
//        log.info("Disabled keys");
    }
    
    private void enableSearchTableKeys() throws SQLException {
        
        // enable keys on msRunSearchResult table
//        log.info("Enabling keys on msRunSearchResult table");
//        DAOFactory.instance().getMsSearchResultDAO().enableKeys();
        
//        // enable keys on SQTSearchResult
//        log.info("Enabling keys on SQTSearchResult table");
//        DAOFactory.instance().getSequestResultDAO().enableKeys();
//        
//        // enable keys on SQTSpectrumData
//        log.info("Enabling keys on SQTSpectrumData table");
//        DAOFactory.instance().getSqtSpectrumDAO().enableKeys();
//        
//        // enable keys on msProteinMatch
//        log.info("Enabling keys on msProteinMatch table");
//        DAOFactory.instance().getMsProteinMatchDAO().enableKeys();
        
//        log.info("Enabled keys");
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

    private MsExperimentUploader initializeExperimentUploader() throws UploadException  {
        
        MsExperimentUploader exptUploader = new MsExperimentUploader();
        exptUploader.setDirectory(spectrumDataDirectory);
        exptUploader.setRemoteDirectory(remoteSpectrumDataDirectory);
        exptUploader.setRemoteServer(remoteServer);
        exptUploader.setComments(comments);
        exptUploader.setInstrumentId(instrumentId);
        
        // Get the spectrum data uploader
        log.info("Initializing SpectrumDataUploadService");
        log.info("\tDirectory: "+spectrumDataDirectory);
        SpectrumDataUploadService rdus = getSpectrumDataUploader(spectrumDataDirectory, remoteSpectrumDataDirectory,
        		                                                 filesToUpload);
        exptUploader.setSpectrumDataUploader(rdus);
        log.info(rdus.getClass().getName());
        
        
        // We cannot upload analysis data without uploading search data first.
        if(uploadAnalysis && !uploadSearch) {
            UploadException ex = new UploadException(ERROR_CODE.PREUPLOAD_CHECK_FALIED);
            ex.appendErrorMessage("Cannot upload analysis results without search results");
            throw ex;
        }
        
        SearchDataUploadService sdus  = null;
        // Get the search data uploader
        if(uploadSearch) {
            log.info("Initializing SearchDataUploadService");
            log.info("\tDirectory: "+searchDirectory);
             sdus = getSearchDataUploader(searchDirectory, 
                    remoteServer, remoteSearchDataDirectory, searchDate);
            exptUploader.setSearchDataUploader(sdus);
            log.info(sdus.getClass().getName());
        }
        // Get the analysis data uploader
        if(uploadAnalysis) {
            log.info("Initializing AnalysisDataUploadService");
            log.info("\tDirectory: "+analysisDirectory);
            AnalysisDataUploadService adus = getAnalysisDataUploader(analysisDirectory, sdus.getSearchProgram());
            exptUploader.setAnalysisDataUploader(adus);
            log.info(adus.getClass().getName());
        }
        // Get the protein inference uploader
        if(uploadProtinfer) {
            log.info("Initializing ProtinferUploadService");
            log.info("\tDirectory: "+protinferDirectory);
            ProtinferUploadService pidus = getProtinferUploader(protinferDirectory);
            exptUploader.setProtinferUploader(pidus);
            log.info(pidus.getClass().getName());
        }
        
        return exptUploader;
    }

    private ProtinferUploadService getProtinferUploader(String dataDirectory) throws UploadException {
        ProtinferUploadService pidus = null;
        try {
            pidus = UploadServiceFactory.instance().getProtinferUploadService(dataDirectory);
        }
        catch (UploadServiceFactoryException e1) {
            UploadException ex = new UploadException(ERROR_CODE.PREUPLOAD_CHECK_FALIED);
            ex.appendErrorMessage("Error getting ProtinferUploadService: "+e1.getMessage());
            throw ex;
        }
        return pidus;
    }
    
    private AnalysisDataUploadService getAnalysisDataUploader(String dataDirectory,
            Program searchProgram) throws UploadException {
        AnalysisDataUploadService adus = null;
        try {
            adus = UploadServiceFactory.instance().getAnalysisDataUploadService(dataDirectory);
            adus.setSearchProgram(searchProgram);
        }
        catch (UploadServiceFactoryException e1) {
            UploadException ex = new UploadException(ERROR_CODE.PREUPLOAD_CHECK_FALIED);
            ex.appendErrorMessage("Error getting AnalysisDataUploadService: "+e1.getMessage());
            throw ex;
        }
        return adus;
    }
    
    private SearchDataUploadService getSearchDataUploader(String dataDirectory,
            String remoteServer, String remoteDirectory, Date searchDate) throws UploadException {
        SearchDataUploadService sdus = null;
        try {
            sdus = UploadServiceFactory.instance().getSearchDataUploadService(dataDirectory);
            sdus.setRemoteServer(remoteServer);
            sdus.setRemoteDirectory(remoteDirectory);
            sdus.setSearchDate(searchDate);
            if(doScanChargeMassCheck)
                sdus.checkResultChargeMass(doScanChargeMassCheck);
        }
        catch (UploadServiceFactoryException e1) {
            UploadException ex = new UploadException(ERROR_CODE.PREUPLOAD_CHECK_FALIED);
            ex.appendErrorMessage("Error getting SearchDataUploadService: "+e1.getMessage());
            throw ex;
        }
        return sdus;
    }

    private SpectrumDataUploadService getSpectrumDataUploader(String dataDirectory, String remoteDirectory, 
    		Set<String> filesToUpload) throws UploadException {
        
        SpectrumDataUploadService rdus = null;
        try {
        	// Support for LabKey pipeline
        	if(hasLabkeyBullseyeData) {
        		Set<String> bullseyeFilesToUpload = new HashSet<String>();
        		for(String file: filesToUpload) {
        			bullseyeFilesToUpload.add(file+".matches");
        		}
        		rdus = UploadServiceFactory.instance().getSpectrumDataUploadService(dataDirectory, bullseyeFilesToUpload);
        		if(rdus instanceof MS2DataUploadService) {
        			((MS2DataUploadService)rdus).setHasLabkeyBullseyeOutput(hasLabkeyBullseyeData);
        		}
        			
        	}
        	else {
        		rdus = UploadServiceFactory.instance().getSpectrumDataUploadService(dataDirectory, filesToUpload);
        	}
            rdus.setRemoteDirectory(remoteDirectory);
            rdus.setUploadFileNames(filesToUpload);
        }
        catch(UploadServiceFactoryException e1) {
            UploadException ex = new UploadException(ERROR_CODE.PREUPLOAD_CHECK_FALIED);
            ex.appendErrorMessage("Error getting SpectrumDataUploadService: "+e1.getMessage());
            throw ex;
        }
        return rdus;
    }
    
    public void uploadData(int experimentId) {
        
        MsExperimentDAO exptDao = DAOFactory.instance().getMsExperimentDAO();
        MsExperiment expt = exptDao.loadExperiment(experimentId);
        if (expt == null) {
            UploadException ex = new UploadException(ERROR_CODE.EXPT_NOT_FOUND);
            ex.appendErrorMessage("Experiment ID: "+experimentId+" does not exist in the database.");
            ex.appendErrorMessage("!!!EXPERIMENT WILL NOT BE UPLOADED!!!");
            uploadExceptionList.add(ex);
            log.error(ex.getMessage(), ex);
            return;
        }
        this.remoteServer = expt.getServerAddress();
        this.uploadedExptId = experimentId;
        
        
        log.info("INITIALIZING EXPERIMENT UPLOAD"+
                "\n\tTime: "+(new Date().toString()));
        
        
        // ----- INITIALIZE THE EXPERIMENT UPLOADER
        MsExperimentUploader exptUploader = null;
        try {
            exptUploader = initializeExperimentUploader();
        }
        catch (UploadException e) {
            uploadExceptionList.add(e);
            log.error(e.getMessage(), e);
            return;
        }
        
        // ----- CHECKS BEFORE BEGINNING UPLOAD -----
        log.info("Starting pre-upload checks..");
        if(!exptUploader.preUploadCheckPassed()) {
            UploadException ex = new UploadException(ERROR_CODE.PREUPLOAD_CHECK_FALIED);
            ex.appendErrorMessage(exptUploader.getPreUploadCheckMsg());
            uploadExceptionList.add(ex);
            log.error(ex.getMessage(), ex);
            return;
        }
        log.info(exptUploader.getPreUploadCheckMsg());
        
        
        // ----- NOW WE CAN BEGIN THE UPLOAD -----
        logBeginExperimentUpload();
        long start = System.currentTimeMillis();
        
        // ----- UPDATE THE LAST UPDATE DATE FOR THE EXPERIMENT
        updateLastUpdateDate(experimentId);
        
        // ----- UPLOAD SCAN DATA
        try {
            exptUploader.uploadSpectrumData(experimentId);
        }
        catch (UploadException ex) {
            uploadExceptionList.add(ex);
            log.error(ex.getMessage(), ex);
            log.error("ABORTING EXPERIMENT UPLOAD!!!\n\tTime: "+(new Date()).toString()+"\n\n");
            return;
        }
        
        // ----- UPLOAD SEARCH DATA
        if(uploadSearch) {
            // If the search is already uploaded, don't re-upload it.
            int searchId = 0;
            try {
                // An experiment can have multiple searches
                // Need to think about how to handle this.
                searchId = getExperimentSearchId(this.uploadedExptId);
            }
            catch (Exception e) {
                UploadException ex = new UploadException(ERROR_CODE.PREUPLOAD_CHECK_FALIED);
                ex.appendErrorMessage(e.getMessage());
                uploadExceptionList.add(ex);
                log.error(ex.getMessage());
                log.error("ABORTING EXPERIMENT UPLOAD!!!\n\tTime: "+(new Date()).toString()+"\n\n");
                return;
            }
            if(searchId == 0) {
                
                // disable keys
                try {
                    disableSearchTableKeys();
                }
                catch (SQLException e) {
                    UploadException ex = new UploadException(ERROR_CODE.ERROR_SQL_DISABLE_KEYS, e);
                    uploadExceptionList.add(ex);
                    log.error(ex.getMessage(), ex);
                    log.error("ABORTING EXPERIMENT UPLOAD!!!\n\tTime: "+(new Date()).toString()+"\n\n");
                    return;
                }
                  
                log.info("BEGINNING upload of search results"); 
                try {
                    this.uploadedSearchId = exptUploader.uploadSearchData(this.uploadedExptId);
                }
                catch (UploadException ex) {
                    uploadExceptionList.add(ex);
                    log.error(ex.getMessage(), ex);
                    log.error("ABORTING EXPERIMENT UPLOAD!!!\n\tTime: "+(new Date()).toString()+"\n\n");
                    
                    // enable keys
                    try {
                        enableSearchTableKeys();
                    }
                    catch(SQLException e){log.error("Error enabling keys");}
                    
                    return;
                }
                
                // enable keys
                try {
                    enableSearchTableKeys();
                }
                catch (SQLException e) {
                    UploadException ex = new UploadException(ERROR_CODE.ERROR_SQL_ENABLE_KEYS, e);
                    uploadExceptionList.add(ex);
                    log.error(ex.getMessage(), ex);
                    log.error("ABORTING EXPERIMENT UPLOAD!!!\n\tTime: "+(new Date()).toString()+"\n\n");
                    return;
                }
            }
            else {
                this.uploadedSearchId = searchId;
                log.info("Search was uploaded previously. SearchID: "+uploadedSearchId);
            }
        }
        
        // ----- UPLOAD ANALYSIS DATA
        if(uploadAnalysis) {
            
            // disable keys
            try {
            	disableAnalysisTableKeys();
            }
            catch (SQLException e) {
            	UploadException ex = new UploadException(ERROR_CODE.ERROR_SQL_DISABLE_KEYS, e);
            	uploadExceptionList.add(ex);
            	log.error(ex.getMessage(), ex);
            	log.error("ABORTING EXPERIMENT UPLOAD!!!\n\tTime: "+(new Date()).toString()+"\n\n");
            	return;
            }

            log.info("BEGINNING upload of analysis results");
            try {
            	this.uploadedAnalysisIds = exptUploader.uploadAnalysisData(this.uploadedSearchId);
            }
            catch (UploadException ex) {
            	uploadExceptionList.add(ex);
            	log.error(ex.getMessage(), ex);
            	log.error("ABORTING EXPERIMENT UPLOAD!!!\n\tTime: "+(new Date()).toString()+"\n\n");

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
            	log.error(ex.getMessage(), ex);
            	log.error("ABORTING EXPERIMENT UPLOAD!!!\n\tTime: "+(new Date()).toString()+"\n\n");
            	return;
            }
        }
        
        // ----- UPLOAD PROTEIN INFERENCE DATA
        if(uploadProtinfer) {
            log.info("BEGINNING upload of protein inference results");
            try {
                exptUploader.uploadProtinferData(this.uploadedSearchId, this.uploadedAnalysisIds);
            }
            catch (UploadException ex) {
                uploadExceptionList.add(ex);
                log.error(ex.getMessage(), ex);
                log.error("ABORTING EXPERIMENT UPLOAD!!!\n\tTime: "+(new Date()).toString()+"\n\n");
                return;
            }
        }
        
        long end = System.currentTimeMillis();
        logEndExperimentUpload(exptUploader, start, end);
       
    }

    private int getExperimentSearchId(int uploadedExptId2) throws Exception {
       
        MsSearchDAO searchDao = DAOFactory.instance().getMsSearchDAO();
        List<Integer> searchIds = searchDao.getSearchIdsForExperiment(uploadedExptId2);
        
        if(searchIds.size() == 0)
            return 0;
        
        if(searchIds.size() > 1) {
            throw new Exception("Multiple search ids found for experimentID: "+uploadedExptId2);
        }
        
        return searchIds.get(0);
    }

    private void updateLastUpdateDate(int experimentId) {
        MsExperimentDAO experimentDao = DAOFactory.instance().getMsExperimentDAO();
        experimentDao.updateLastUpdateDate(experimentId);
        
    }

    
    private void logEndExperimentUpload(MsExperimentUploader uploader, long start, long end) {
        log.info("END EXPERIMENT UPLOAD: "+((end - start)/(1000L))+"seconds"+
                "\n\tTime: "+(new Date().toString())+"\n"+
                uploader.getUploadSummary());
    }

    private void logBeginExperimentUpload() {
        
        StringBuilder msg = new StringBuilder();
        msg.append("BEGIN EXPERIMENT UPLOAD");
        msg.append("\n\tRemote server: "+remoteServer);
        msg.append("\n\tSPECTRUM DATA ");
        msg.append("\n\t\t Directory: "+spectrumDataDirectory);
        msg.append("\n\t\t Remote Directory: "+remoteSpectrumDataDirectory);
        if(uploadSearch) {
            msg.append("\n\tSEARCH DATA");
            msg.append("\n\t\t Directory: "+searchDirectory);
            msg.append("\n\t\t Remote Directory: "+remoteSearchDataDirectory);
        }
        if(uploadAnalysis) {
            msg.append("\n\tANALYSIS DATA");
            msg.append("\n\t\t Directory: "+analysisDirectory);
        }
        if(uploadProtinfer) {
            msg.append("\n\tPROTEIN INFERENCE");
            msg.append("\n\t\t Directory: "+protinferDirectory);
        }
        msg.append("\n\tTime: "+(new Date().toString()));
        log.info(msg.toString());
    }

    
    public static void main(String[] args) throws UploadException {
        long start = System.currentTimeMillis();

        
//        for(int i = 0; i < 10; i++) {
//        String directory = args[0];
        String directory = "/Users/silmaril/WORK/UW/SQT_BKUP_TEST";
        
        if(directory == null || directory.length() == 0 || !(new File(directory).exists()))
            System.out.println("Invalid directory: "+directory);
        
//        boolean maccossData = Boolean.parseBoolean(args[1]);
        boolean maccossData = true;
        
        System.out.println("Directory: "+directory+"; Maccoss Data: "+maccossData);
        
        MsDataUploader uploader = new MsDataUploader();
        uploader.setRemoteServer("local");
        uploader.setSpectrumDataDirectory(directory);
        uploader.setSearchDirectory(directory+File.separator+"pipeline"+File.separator+"sequest");
        uploader.setAnalysisDirectory(directory+File.separator+"pipeline"+File.separator+"percolator");
//        uploader.setProtinferDirectory(directory);
        
        uploader.setRemoteSpectrumDataDirectory(directory);
        uploader.setRemoteSearchDataDirectory(directory+File.separator+"pipeline"+File.separator+"sequest");
        uploader.setSearchDate(new Date());
        uploader.checkResultChargeMass(maccossData);
        
//        uploader.uploadData(1);
        uploader.uploadData();
//        }
        long end = System.currentTimeMillis();
        log.info("TOTAL TIME: "+((end - start)/(1000L))+"seconds.");
    }
}
