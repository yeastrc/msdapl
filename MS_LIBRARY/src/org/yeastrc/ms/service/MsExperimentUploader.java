/**
 * MsExperimentUploader.java
 * @author Vagisha Sharma
 * Mar 25, 2009
 * @version 1.0
 */
package org.yeastrc.ms.service;

import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.yeastrc.ms.dao.DAOFactory;
import org.yeastrc.ms.dao.general.MsExperimentDAO;
import org.yeastrc.ms.domain.general.impl.ExperimentBean;
import org.yeastrc.ms.service.UploadException.ERROR_CODE;
import org.yeastrc.ms.service.percolator.PercolatorXmlDataUploadService;
import org.yeastrc.ms.service.sqtfile.AbstractSQTDataUploadService;
import org.yeastrc.ms.service.sqtfile.PercolatorSQTDataUploadService;

/**
 * 
 */
public class MsExperimentUploader {

    private static final Logger log = Logger.getLogger(MsExperimentUploader.class);
    
    private String remoteServer;
    private String remoteDirectory;
    private String uploadDirectory;
    private String comments;
    private int instrumentId;
    
    private SpectrumDataUploadService rdus;
    private SearchDataUploadService sdus;
    private AnalysisDataUploadService adus;
    private ProtinferUploadService pidus;
    
    private boolean do_sdupload = false;
    private boolean do_adupload = false;
    private boolean do_piupload = false;
    
    
    private StringBuilder preUploadCheckMsg;
    
    private int experimentId = 0; // uploaded experimentId
    
    public MsExperimentUploader () {
        this.preUploadCheckMsg = new StringBuilder();
    }
    
    public void setRemoteServer(String remoteServer) {
        this.remoteServer = remoteServer;
    }

    public void setRemoteDirectory(String remoteDirectory) {
        this.remoteDirectory = remoteDirectory;
    }
    
    public void setComments(String comments) {
        this.comments = comments;
    }

    public void setSpectrumDataUploader(SpectrumDataUploadService rdus) {
        this.rdus = rdus;
    }
    
    public void setInstrumentId(int instrumentId) {
        this.instrumentId = instrumentId;
    }
    
    public int getInstrumentId() {
        return this.instrumentId;
    }

    public void setSearchDataUploader(SearchDataUploadService sdus) {
        this.sdus = sdus;
        this.do_sdupload = true;
    }

    public void setAnalysisDataUploader(AnalysisDataUploadService adus) {
        this.adus = adus;
        this.do_adupload = true;
    }
    
    public void setProtinferUploader(ProtinferUploadService pidus) {
        this.pidus = pidus;
        this.do_piupload = true;
    }

    public void setDirectory(String directory) {
        this.uploadDirectory = directory;
    }
    
	public boolean preUploadCheckPassed() {
        boolean passed = true;
        
        log.info("Doing pre-upload check for spectrum data uploader....");
        // Spectrum data uploader check
        if(rdus == null) {
            appendToMsg("SpectrumDataUploader was null");
            passed = false;
        }
        else if(!rdus.preUploadCheckPassed())  {
            appendToMsg(rdus.getPreUploadCheckMsg());
            passed = false;
        }
        if(!passed) log.info("...FAILED");
        else        log.info("...PASSED");
        
        
        
        // Search data uploader check
        if(do_sdupload) {
            log.info("Doing pre-upload check for search data uploader....");
            if(sdus == null) {
                appendToMsg("SearchDataUploader was null");
                passed = false;
            }
            else {
                sdus.setSpectrumFileNames(rdus.getFileNames());
                if(!sdus.preUploadCheckPassed()) {
                    appendToMsg(sdus.getPreUploadCheckMsg());
                    passed = false;
                }
            }
            if(!passed) log.info("...FAILED");
            else        log.info("...PASSED");
        }
        
        // Analysis data uploader check
        if(do_adupload) {
            log.info("Doing pre-upload check for analysis data uploader....");
            if(!do_sdupload) {
                appendToMsg("No search results uploader found. Cannot upload analysis results without uploading search results first.");
                passed = false;
            }
            if(adus == null) {
                appendToMsg("AnalysisDataUploader was null");
                passed = false;
            }
            else {
                adus.setSearchDataFileNames(sdus.getFileNames());
                if(!adus.preUploadCheckPassed()) {
                    appendToMsg(adus.getPreUploadCheckMsg());
                    passed = false;
                }
            }
            if(!passed) log.info("...FAILED");
            else        log.info("...PASSED");
        }
        
        
        // Protein inference uploader check
        if(do_piupload) {
            log.info("Doing pre-upload check for protein inference data uploader....");
            if(!do_sdupload && !do_adupload) {
                appendToMsg("No search or analysis results uploader found. "+
                        "Cannot upload protein inference results without uploading search and analysis results first.");
                passed = false;
            }
            if(pidus == null) {
                appendToMsg("ProteinInferenceUploader was null");
                passed = false;
            }
            else {
                if(!pidus.preUploadCheckPassed()) {
                    appendToMsg(pidus.getPreUploadCheckMsg());
                    passed = false;
                }
            }
            if(!passed) log.info("...FAILED");
            else        log.info("...PASSED");
        }
        
        doSequestResultRankCheck(); // Are we uploading all or top "N" sequest results
        
        return passed;
    }
    
    private void doSequestResultRankCheck() {
    	
        if(do_sdupload && do_adupload) {
        	
        	int maxPsmRank = Integer.MAX_VALUE;
        	if(sdus instanceof AbstractSQTDataUploadService) {
        		
        		if(adus instanceof PercolatorSQTDataUploadService) {
        			PercolatorSQTDataUploadService ps = (PercolatorSQTDataUploadService) adus;
        			maxPsmRank = ps.getMaxPsmRank();
        		}
        		
        		else if (adus instanceof PercolatorXmlDataUploadService) {
        			PercolatorXmlDataUploadService ps = (PercolatorXmlDataUploadService) adus;
        			maxPsmRank = ps.getMaxPsmRank();
        		}
        		
        		log.info("SEQUEST results upto xCorrRank: "+maxPsmRank+" will be uploaded");
                ((AbstractSQTDataUploadService)sdus).setXcorrRankCutoff(maxPsmRank);
        	}
        }
    }
    
    public String getPreUploadCheckMsg() {
        return preUploadCheckMsg.toString();
    }
    
    
    public String getUploadSummary() {
        StringBuilder summary = new StringBuilder();
        summary.append("\n\tExperiment ID: "+experimentId+"\n");
        summary.append("\tRemote server: "+remoteServer+"\n");
        summary.append("\tRemote directory: "+remoteDirectory+"\n");
        summary.append(rdus.getUploadSummary()+"\n");
        if(do_sdupload)
            summary.append(sdus.getUploadSummary()+"\n");
        if(do_adupload)
            summary.append(adus.getUploadSummary()+"\n");
        if(do_piupload)
            summary.append(pidus.getUploadSummary()+"\n");
        summary.append("\n");
        return summary.toString();
    }
    
    private void appendToMsg(String msg) {
        preUploadCheckMsg.append(msg+"\n");
    }
    
    public int uploadSpectrumData() throws UploadException {
        
        // first create an entry in the msExperiment table
        experimentId = saveExperiment();
        log.info("\n\nAdded entry for experiment ID: "+experimentId+"\n\n");
        
        uploadSpectrumData(experimentId);
        return experimentId;
    }
    
    public void uploadSpectrumData(int experimentId) throws UploadException {
        
        this.experimentId = experimentId;
        
        // upload spectrum data
        rdus.setExperimentId(experimentId);
        
        try {
            rdus.upload();
        }
        catch(UploadException ex) {
            deleteExperiment(experimentId);
            throw ex;
        }
    }
    
    public int uploadSearchData(int experimentId) throws UploadException {
        
        // if we have search data upload that next
        int searchId;
        if(do_sdupload) {
            sdus.setExperimentId(experimentId);
            sdus.setSpectrumFileNames(rdus.getFileNames());
            sdus.upload();
            searchId = sdus.getUploadedSearchId();
        }
        else {
            log.error("No SearchDataUploadService found");
            return 0;
        }
        return searchId;
        
    }
    
    public List<Integer> uploadAnalysisData(int searchId) throws UploadException {
        
        List<Integer> uploadedAnalysisIds = null;
        if(do_adupload) {
            adus.setSearchId(searchId);
            adus.setSearchDataFileNames(sdus.getFileNames());
            adus.upload();
            uploadedAnalysisIds = adus.getUploadedAnalysisIds();
        }
        else {
            log.error("No AnalysisDataUploadService found");
            return null;
        }
        return uploadedAnalysisIds;
    }
    
    public void uploadProtinferData(int searchId, List<Integer> analysisIds) throws UploadException {
        
        if(do_piupload) {
            pidus.setSearchId(searchId);
            if(analysisIds != null && analysisIds.size() == 1)
                pidus.setAnalysisId(analysisIds.get(0));
            pidus.upload();
        }
        else {
            log.error("No ProtinferUploadService found");
        }
    }
    
    private int saveExperiment() throws UploadException {
        MsExperimentDAO experimentDao = DAOFactory.instance().getMsExperimentDAO();
        ExperimentBean experiment = new ExperimentBean();
        experiment.setServerAddress(remoteServer);
        experiment.setServerDirectory(remoteDirectory);
        experiment.setComments(comments);
        experiment.setInstrumentId(instrumentId);
        experiment.setUploadDate(new java.sql.Date(new Date().getTime()));
        try { return experimentDao.saveExperiment(experiment);}
        catch(RuntimeException e) {
            UploadException ex = new UploadException(ERROR_CODE.CREATE_EXPT_ERROR);
            ex.appendErrorMessage("!!!\n\tERROR CREATING EXPERIMENT. EXPERIMENT WILL NOT BE UPLOADED\n!!!");
            ex.setErrorMessage(e.getMessage());
            throw ex;
        }
    }
    
    private void deleteExperiment(int experimentId) {
        log.error("\n\tDELETING EXPERIMENT: "+experimentId);
        MsExperimentDAO exptDao = DAOFactory.instance().getMsExperimentDAO();
        exptDao.deleteExperiment(experimentId);
    }
}
