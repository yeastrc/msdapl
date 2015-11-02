/**
 * PercolatorSQTDataUploadService.java
 * @author Vagisha Sharma
 * Dec 11, 2008
 * @version 1.0
 */
package org.yeastrc.ms.service.sqtfile;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.yeastrc.ms.dao.DAOFactory;
import org.yeastrc.ms.dao.analysis.MsRunSearchAnalysisDAO;
import org.yeastrc.ms.dao.analysis.MsSearchAnalysisDAO;
import org.yeastrc.ms.dao.analysis.percolator.PercolatorParamsDAO;
import org.yeastrc.ms.dao.analysis.percolator.PercolatorResultDAO;
import org.yeastrc.ms.dao.run.MsScanDAO;
import org.yeastrc.ms.dao.search.MsRunSearchDAO;
import org.yeastrc.ms.dao.search.MsSearchDAO;
import org.yeastrc.ms.dao.search.MsSearchResultDAO;
import org.yeastrc.ms.domain.analysis.impl.RunSearchAnalysisBean;
import org.yeastrc.ms.domain.analysis.impl.SearchAnalysisBean;
import org.yeastrc.ms.domain.analysis.percolator.PercolatorResultDataWId;
import org.yeastrc.ms.domain.analysis.percolator.PercolatorResultIn;
import org.yeastrc.ms.domain.analysis.percolator.PercolatorSearchScan;
import org.yeastrc.ms.domain.analysis.percolator.impl.PercolatorParamBean;
import org.yeastrc.ms.domain.analysis.percolator.impl.PercolatorResultDataBean;
import org.yeastrc.ms.domain.search.MsResidueModificationIn;
import org.yeastrc.ms.domain.search.MsRunSearch;
import org.yeastrc.ms.domain.search.MsSearch;
import org.yeastrc.ms.domain.search.MsSearchResult;
import org.yeastrc.ms.domain.search.MsTerminalModificationIn;
import org.yeastrc.ms.domain.search.Program;
import org.yeastrc.ms.domain.search.SearchFileFormat;
import org.yeastrc.ms.domain.search.sqtfile.SQTHeaderItem;
import org.yeastrc.ms.domain.search.sqtfile.SQTRunSearchIn;
import org.yeastrc.ms.parser.DataProviderException;
import org.yeastrc.ms.parser.sqtFile.SQTFileReader;
import org.yeastrc.ms.parser.sqtFile.SQTHeader;
import org.yeastrc.ms.parser.sqtFile.percolator.PercolatorSQTFileReader;
import org.yeastrc.ms.service.AnalysisDataUploadService;
import org.yeastrc.ms.service.UploadException;
import org.yeastrc.ms.service.UploadException.ERROR_CODE;
import org.yeastrc.ms.service.percolator.stats.PercolatorFilteredPsmStatsSaver;
import org.yeastrc.ms.service.percolator.stats.PercolatorFilteredSpectraStatsSaver;

/**
 * 
 */
public class PercolatorSQTDataUploadService implements AnalysisDataUploadService {
    
    
    private static final Logger log = Logger.getLogger(PercolatorSQTDataUploadService.class);

    private final MsSearchResultDAO resultDao;
    private final MsSearchAnalysisDAO analysisDao;
    private final MsRunSearchDAO runSearchDao;
    private final PercolatorParamsDAO paramDao;
    private final MsSearchDAO searchDao;
    private final MsRunSearchAnalysisDAO runSearchAnalysisDao;
    private final PercolatorResultDAO percResultDao;

    private static final int BUF_SIZE = 1000;

    private List<? extends MsResidueModificationIn> dynaResidueMods;
    private List<? extends MsTerminalModificationIn> dynaTermMods;
    
    // these are the things we will cache and do bulk-inserts
    private List<PercolatorResultDataWId> percolatorResultDataList; // percolator scores
    
    private Set<Integer> uploadedResultIds;

    private int numAnalysisUploaded = 0;
    
    // This is information we will get from the SQT files and then update the entries in the msPostSearchAnalysis
    private String programVersion = "uninit";

    private Map<String, String> params;
    private int analysisId = 0;
    private String comments;
    
    private int numResultsNotFound = 0;
    
    
    private int searchId;
    private String dataDirectory;
    private StringBuilder preUploadCheckMsg;
    private boolean preUploadCheckDone = false;
    
    private List<String> filenames;
    private List<String> searchDataFileNames;
    private Program searchProgram;
    
    
    public PercolatorSQTDataUploadService() {
        this.percolatorResultDataList = new ArrayList<PercolatorResultDataWId>(BUF_SIZE);
        this.dynaResidueMods = new ArrayList<MsResidueModificationIn>();
        this.dynaTermMods = new ArrayList<MsTerminalModificationIn>();
        this.params = new HashMap<String, String>();
        
        uploadedResultIds = new HashSet<Integer>();
        filenames = new ArrayList<String>();
        
        DAOFactory daoFactory = DAOFactory.instance();
        this.analysisDao = daoFactory.getMsSearchAnalysisDAO();
        this.runSearchDao = daoFactory.getMsRunSearchDAO();
        this.paramDao  = daoFactory.getPercoltorParamsDAO();
        this.searchDao = daoFactory.getMsSearchDAO();
        this.runSearchAnalysisDao  = daoFactory.getMsRunSearchAnalysisDAO();
        
        this.resultDao = daoFactory.getMsSearchResultDAO();
        this.percResultDao = daoFactory.getPercolatorResultDAO();
        
    }
    
    void reset() {

        //analysisId = 0;
        
        numAnalysisUploaded = 0;

        resetCaches();

        programVersion = "uninit";
        
        dynaResidueMods.clear();
        dynaTermMods.clear();
        
        params.clear();
        
    }

    // called before uploading each sqt file and in the reset() method.
    void resetCaches() {
        percolatorResultDataList.clear();
        numResultsNotFound = 0;
        uploadedResultIds.clear();
    }


    private void updateProgramVersion(int analysisId, String programVersion) {
        try {
            analysisDao.updateAnalysisProgramVersion(analysisId, programVersion);
        }
        catch(RuntimeException e) {
            log.warn("Error updating program version for analysisID: "+analysisId, e);
        }
    }

    @Override
    public void upload() throws UploadException {

        reset();// reset all caches etc.
        
        if(!preUploadCheckDone) {
            if(!preUploadCheckPassed()) {
                UploadException ex = new UploadException(ERROR_CODE.PREUPLOAD_CHECK_FALIED);
                ex.appendErrorMessage(this.getPreUploadCheckMsg());
                ex.appendErrorMessage("\n\t!!!PERCOLATOR ANALYSIS WILL NOT BE UPLOADED\n");
                throw ex;
            }
        }
        
        Map<String,Integer> runSearchIdMap;
        try {
           runSearchIdMap = createRunSearchIdMap();
        }
        catch(UploadException e) {
            e.appendErrorMessage("\n\t!!!PERCOLATOR ANALYSIS WILL NOT BE UPLOADED\n");
            throw e;
        }
        
        // create a new entry in the msSearchAnalysis table
        try {
        	if(analysisId == 0)
        		analysisId = saveTopLevelAnalysis();
        }
        catch (UploadException e) {
            e.appendErrorMessage("\n\t!!!PERCOLATOR ANALYSIS WILL NOT BE UPLOADED\n");
            throw e;
        }
        
        // get the modifications used for this search. Will be used for parsing the peptide sequence
        getSearchModifications(searchId);
        
        // now upload the individual sqt files
        for (String file: filenames) {
            String filePath = dataDirectory+File.separator+file;
            // if the file does not exist skip over to the next
            if (!(new File(filePath).exists()))
                continue;
            Integer runSearchId = runSearchIdMap.get(file); 
            if (runSearchId == null) {
                UploadException ex = new UploadException(ERROR_CODE.NO_RUNSEARCHID_FOR_ANALYSIS_FILE);
                ex.appendErrorMessage("\n\tDELETING PERCOLATOR ANALYSIS...ID: "+analysisId+"\n");
                deleteAnalysis(analysisId);
                numAnalysisUploaded = 0;
                throw ex;
            }
            resetCaches();
            try {
                uploadSqtFile(filePath, runSearchId, searchProgram);
                numAnalysisUploaded++;
                log.info("Number of results not found: "+numResultsNotFound);
            }
            catch (UploadException ex) {
                ex.appendErrorMessage("\n\tDELETING PERCOLATOR ANALYSIS...ID: "+analysisId+"\n");
                deleteAnalysis(analysisId);
                numAnalysisUploaded = 0;
                throw ex;
            }
        }
        
        // if no sqt files were uploaded delete the top level search analysis
        if (numAnalysisUploaded == 0) {
            UploadException ex = new UploadException(ERROR_CODE.NO_PERC_ANALYSIS_UPLOADED);
            ex.appendErrorMessage("\n\tDELETING PERCOLATOR ANALYSIS...ID: "+analysisId+"\n");
            deleteAnalysis(analysisId);
            numAnalysisUploaded = 0;
            throw ex;
        }
        
        // Update the "analysisProgramVersion" in the msSearchAnalysis table
        if (this.programVersion != null && this.programVersion != "uninit") {
            updateProgramVersion(analysisId, programVersion);
        }
        
        // Add the Percolator parameters
        try {
            addPercolatorParams(params, analysisId);
        }
        catch(UploadException e) {
            e.appendErrorMessage("\n\tDELETING PERCOLATOR ANALYSIS...ID: "+analysisId+"\n");
            log.error(e.getMessage(), e);
            deleteAnalysis(analysisId);
            numAnalysisUploaded = 0;
            throw e;
        }
        
        // Finally, save the filtered results stats
        try {
        	PercolatorFilteredPsmStatsSaver psmStatsSaver = PercolatorFilteredPsmStatsSaver.getInstance();
        	psmStatsSaver.save(analysisId, 0.01);
        	PercolatorFilteredSpectraStatsSaver spectraStatsSaver = PercolatorFilteredSpectraStatsSaver.getInstance();
        	spectraStatsSaver.save(analysisId, 0.01);
        }
        catch(Exception e) {
        	log.error("Error saving filtered stats for analysisID: "+analysisId, e);
        }
    }
    
    public List<Integer> getUploadedAnalysisIds() {
        List<Integer> analysisIds = new ArrayList<Integer>();
        analysisIds.add(analysisId);
        return analysisIds;
    }
    
    public void setAnalysisId(int analysisId) {
    	this.analysisId = analysisId;
    }
    
    public void setComments(String comments) {
    	this.comments = comments;
    }
    
    
    private Map<String, Integer> createRunSearchIdMap() throws UploadException {
        
        Map<String, Integer> runSearchIdMap = new HashMap<String, Integer>(filenames.size()*2);
        
        for(String file: filenames) {
            String filenoext = removeFileExtension(file);
            int runSearchId = runSearchDao.loadIdForSearchAndFileName(searchId, filenoext);
            if(runSearchId == 0) {
                UploadException ex = new UploadException(ERROR_CODE.NO_RUNSEARCHID_FOR_ANALYSIS_FILE);
                ex.appendErrorMessage("File: "+filenoext);
                ex.appendErrorMessage("; SearchID: "+searchId);
                throw ex;
            }
            runSearchIdMap.put(file, runSearchId);
        }
        return runSearchIdMap;
    }


    private void addPercolatorParams(Map<String, String> params, int analysisId) throws UploadException {
        
        for (String name: params.keySet()) {
            String val = params.get(name);
            PercolatorParamBean param = new PercolatorParamBean();
            param.setParamName(name);
            param.setParamValue(val);
            try {
                paramDao.saveParam(param, analysisId);
            }
            catch(RuntimeException e) {
                UploadException ex = new UploadException(ERROR_CODE.RUNTIME_SQT_ERROR, e);
                ex.appendErrorMessage("Exception saving Percolator param (name: "+name+", value: "+val+")");
                throw ex;
            }
        }
    }

    private void getSearchModifications(int searchId) {
       MsSearch search = searchDao.loadSearch(searchId);
       this.dynaResidueMods = search.getDynamicResidueMods();
       this.dynaTermMods = search.getDynamicTerminalMods();
    }

    private int saveTopLevelAnalysis() throws UploadException {
        
        SearchAnalysisBean analysis = new SearchAnalysisBean();
//        analysis.setSearchId(searchId);
        analysis.setAnalysisProgram(Program.PERCOLATOR);
        analysis.setComments(comments);
        try {
            return analysisDao.save(analysis);
        }
        catch(RuntimeException e) {
            UploadException ex = new UploadException(ERROR_CODE.RUNTIME_SQT_ERROR, e);
            ex.setErrorMessage(e.getMessage());
            throw ex;
        }
    }
    
    private void uploadSqtFile(String filePath, int runSearchId, Program searchProgram) throws UploadException {
        
        log.info("BEGIN PERCOLATOR SQT FILE UPLOAD: "+(new File(filePath).getName())+"; RUN_SEARCH_ID: "+runSearchId);
        
        long startTime = System.currentTimeMillis();
        PercolatorSQTFileReader provider = new PercolatorSQTFileReader();
        
        try {
            provider.open(filePath, searchProgram);
            provider.setDynamicResidueMods(this.dynaResidueMods);
            provider.setDynamicTerminalMods(this.dynaTermMods);
        }
        catch (DataProviderException e) {
            provider.close();
            UploadException ex = new UploadException(ERROR_CODE.READ_ERROR_SQT, e);
            ex.setFile(filePath);
            ex.setErrorMessage(e.getMessage()+"\n\t!!!PERCOLATOR SQT FILE WILL NOT BE UPLOADED!!!");
            throw ex;
        }
        
        try {
            uploadPercolatorSqtFile(provider, runSearchId);
        }
        catch (UploadException ex) {
            ex.setFile(filePath);
            ex.appendErrorMessage("\n\t!!!PERCOLATOR SQT FILE WILL NOT BE UPLOADED!!!");
            throw ex;
        }
        catch (RuntimeException e) { // most likely due to SQL exception
            UploadException ex = new UploadException(ERROR_CODE.RUNTIME_SQT_ERROR, e);
            ex.setFile(filePath);
            ex.setErrorMessage(e.getMessage()+"\n\t!!!PERCOLATOR SQT FILE WILL NOT BE UPLOADED!!!");
            throw ex;
        }
        finally {provider.close();}
        
        long endTime = System.currentTimeMillis();
        
        log.info("END PERCOLATOR SQT FILE UPLOAD: "+provider.getFileName()+"; RUN_SEARCH_ID: "+runSearchId+ " in "+(endTime - startTime)/(1000L)+"seconds\n");
        
    }
    
    // parse and upload a sqt file
    private void uploadPercolatorSqtFile(PercolatorSQTFileReader provider, int runSearchId) throws UploadException {
        
        int runSearchAnalysisId = 0;
        try {
            runSearchAnalysisId = uploadRunSearchAnalysis(provider, analysisId, runSearchId);
            log.info("Extracted info from Percolator sqt file header: "+provider.getFileName());
            log.info("Uploaded top-level info for sqt file. runSearchAnalysisId: "+runSearchAnalysisId);
        }
        catch(DataProviderException e) {
            UploadException ex = new UploadException(ERROR_CODE.INVALID_SQT_HEADER, e);
            ex.setErrorMessage(e.getMessage());
            throw ex;
        }

        // get the runID. Will be needed later to get the scan number
        int runId = getRunIdForRunSearch(runSearchId);
        if(runId == 0) {
            UploadException ex = new UploadException(ERROR_CODE.NO_RUNID_FOR_SQT);
            ex.setErrorMessage("No runID found for runSearchID: "+runSearchId);
            throw ex;
        }
        
        // upload the search results for each scan + charge combination
        int numResults = 0;
        while (provider.hasNextSearchScan()) {
            PercolatorSearchScan scan = null;
            try {
                scan = provider.getNextSearchScan();
            }
            catch (DataProviderException e) {
                UploadException ex = new UploadException(ERROR_CODE.INVALID_SQT_SCAN);
                ex.setErrorMessage(e.getMessage());
                throw ex;
            }
            
            // Data from MacCoss Lab can have duplicate results for the same scan 
            // and charge. When uploading the sequest results the "observed mass" for
            // a search scan is matched with the Z lines of the MS2 file.  The scan
            // is uploaded only if the charge and mass match a Z line for the scan in the MS2 file. 
            // Before uploading the Percolator results for this search scan make
            // sure that the observed mass is the same as the observed mass
            // for the uploaded sequest search scan. If not, ignore this scan
            int scanId = getScanId(runId, scan.getScanNumber());
            
            int numMatches = resultDao.numResultsForRunSearchScanChargeMass(runSearchId, scanId, scan.getCharge(), scan.getObservedMass());
            if(numMatches == 0) {
                String msg = "No matching results found with runSearchId: "+runSearchId+
                "; scanId: "+scanId+"; charge: "+scan.getCharge()+
                "; mass: "+scan.getObservedMass();
                
//                log.error(msg);
                UploadException ex = new UploadException(ERROR_CODE.NO_MATCHING_SEARCH_SCAN);
                ex.setErrorMessage(msg);
                throw ex;
            }
            else {
                // save all the search results for this scan
                for (PercolatorResultIn result: scan.getScanResults()) {
                    boolean uploaded = uploadSearchResult(result, runSearchAnalysisId, runSearchId, scanId);
                    if(uploaded)    numResults++;
                }
            }
        }
        flush(); // save any cached data
        log.info("Uploaded Percolator SQT file: "+provider.getFileName()+", with "+numResults+" results.");
                
    }
    
    private int getRunIdForRunSearch(int runSearchId) {
        MsRunSearch rs = runSearchDao.loadRunSearch(runSearchId);
        if(rs != null)
            return rs.getRunId();
        else
            return 0;
    }
    

    // EXTRACT INFO FROM PERCOLATOR SQT HEADER AND save an entry in the MsRunSearchAnalysis table
    private final int uploadRunSearchAnalysis(PercolatorSQTFileReader provider, int analysisId, int runSearchId)
        throws DataProviderException {

        SQTRunSearchIn search = provider.getSearchHeader();
        if (search instanceof SQTHeader) {
            SQTHeader header = (SQTHeader)search;
            
            // SEARCH PROGRAM VERSION IN THE SQT FILE
            // this is the first time we are assigning a value to program version
            if ("uninit".equals(programVersion))
                this.programVersion = header.getSearchEngineVersion();

            // make sure the SQTGeneratorVersion value is same in all sqt headers
            if (programVersion == null) {
                if (header.getSearchEngineVersion() != null)
                    throw new DataProviderException("Value of Percolator version is not the same in all SQT files.");
            }
            else if (!programVersion.equals(header.getSearchEngineVersion())) {
                throw new DataProviderException("Value of Percolator version is not the same in all SQT files.");
            }
            
            Map<String, String> sqtParams = PercolatorSQTFileReader.getPercolatorParams(header);
            // get the Percolator parameters
            for(String param: sqtParams.keySet()) {
                String pval = params.get(param);
                if(pval == null)
                    params.put(param, sqtParams.get(param));
                else {
                    if(!pval.equals(sqtParams.get(param))) {
                        throw new DataProviderException("Parameter \""+param+"\" values do not match");
                    }
                }
            }
        }
        // save the run search analysis and return the database id
        RunSearchAnalysisBean rsa = new RunSearchAnalysisBean();
        rsa.setAnalysisFileFormat(SearchFileFormat.SQT_PERC);
        rsa.setAnalysisId(analysisId);
        rsa.setRunSearchId(runSearchId);
        return runSearchAnalysisDao.save(rsa);
    }

    private boolean uploadSearchResult(PercolatorResultIn result, int rsAnalysisId, int runSearchId, int scanId) throws UploadException {
        
        MsSearchResult searchResult = null;
        try {
            List<MsSearchResult> matchingResults = resultDao.loadResultForSearchScanChargePeptide(runSearchId, scanId, 
                        result.getCharge(), 
                        result.getResultPeptide().getPeptideSequence());
            
            if(matchingResults.size() == 1) 
                searchResult = matchingResults.get(0);
            
            else if(matchingResults.size() > 1) { // this can happen if 
                                                  // 1. scan searched with same charge but different M+H (due to Bullseye) results in same peptide match
                                                  // 2. we have the same sequence with different mods (TODO will this ever happen??)
                // check for 1. first
                int numMatching = 0;
                for(MsSearchResult res: matchingResults) {
//                    log.info("My mass: "+result.getObservedMass()+". Looking at: "+res.getObservedMass());
                    if(result.getObservedMass().doubleValue() == res.getObservedMass().doubleValue()) {
                        searchResult = res;
                        numMatching++;
                    }
                }
                
                // If we still have multiple matches check for 2.
                if(numMatching > 1) {
                    numMatching = 0;
                    String myPeptide = result.getResultPeptide().getModifiedPeptidePS();
                    for(MsSearchResult res: matchingResults) {
                        // If this does not have the same observed mass as our result skip it
                        if(result.getObservedMass().doubleValue() != res.getObservedMass().doubleValue())
                            continue;
                        if(myPeptide.equals(res.getResultPeptide().getModifiedPeptidePS())) {
                            searchResult = res;
                            numMatching++;
                        }
                    }
                }
                
                // If we still don't have a definite match
                if(numMatching > 1) {
                    UploadException ex = new UploadException(ERROR_CODE.MULTI_MATCHING_SEARCH_RESULT);
                    ex.setErrorMessage("Multiple matching search results were found for runSearchId: "+runSearchId+
                            " scanId: "+scanId+"; charge: "+result.getCharge()+"; mass: "+result.getObservedMass()+
                            "; peptide: "+result.getResultPeptide().getPeptideSequence()+
                            "; modified peptide: "+result.getResultPeptide().getModifiedPeptidePS());
                    throw ex;
                }
            }
        }
        catch(RuntimeException e) {
            UploadException ex = new UploadException(ERROR_CODE.RUNTIME_SQT_ERROR, e);
            ex.setErrorMessage(e.getMessage());
            throw ex;
        }
        if(searchResult == null) {
            numResultsNotFound++;
            UploadException ex = new UploadException(ERROR_CODE.NO_MATCHING_SEARCH_RESULT);
            ex.setErrorMessage("No matching search result was found for runSearchId: "+runSearchId+
                    " scanId: "+scanId+"; charge: "+result.getCharge()+"; mass: "+result.getObservedMass()+
                    "; peptide: "+result.getResultPeptide().getPeptideSequence()+
                    "; modified peptide: "+result.getResultPeptide().getModifiedPeptidePS());
            throw ex;
            //log.warn(ex.getErrorMessage());
            //return false;
        }
        
        // upload the Percolator specific information for this result.
        return uploadPercolatorResultData(result, rsAnalysisId, searchResult.getId());
    }

    private boolean uploadPercolatorResultData(PercolatorResultIn resultData, int rsAnalysisId, int searchResultId) {
        // upload the Percolator specific result information if the cache has enough entries
        if (percolatorResultDataList.size() >= BUF_SIZE) {
            uploadPercolatorResultBuffer();
        }
        
        // TODO THIS IS TEMP TILL I SORT OUT THE DUPLICATE RESULTS IN PERCOLATOR SQT FILES
        if(uploadedResultIds.contains(searchResultId))
            return false;
        uploadedResultIds.add(searchResultId);
        
        
        // add the Percolator specific information for this result to the cache
        PercolatorResultDataBean res = new PercolatorResultDataBean();
        res.setRunSearchAnalysisId(rsAnalysisId);
        res.setSearchResultId(searchResultId);
        res.setPredictedRetentionTime(resultData.getPredictedRetentionTime());
        res.setDiscriminantScore(resultData.getDiscriminantScore());
        res.setPosteriorErrorProbability(resultData.getPosteriorErrorProbability());
        res.setQvalue(resultData.getQvalue());
        
       
        percolatorResultDataList.add(res);
        return true;
    }
    
    private void uploadPercolatorResultBuffer() {
        percResultDao.saveAllPercolatorResultData(percolatorResultDataList);
        percolatorResultDataList.clear();
    }
    
    private void flush() {
        if (percolatorResultDataList.size() > 0) {
            uploadPercolatorResultBuffer();
        }
    }

    static int getScanId(int runId, int scanNumber) throws UploadException {

        MsScanDAO scanDao = DAOFactory.instance().getMsScanDAO();
        int scanId = scanDao.loadScanIdForScanNumRun(scanNumber, runId);
        if (scanId == 0) {
            UploadException ex = new UploadException(ERROR_CODE.NO_SCANID_FOR_SCAN);
            ex.setErrorMessage("No scanId found for scan number: "+scanNumber+" and runId: "+runId);
            throw ex;
        }
        return scanId;
    }
    
    public void deleteAnalysis(int analysisId) {
        if (analysisId == 0)
            return;
        log.info("Deleting analysis ID: "+analysisId);
        analysisDao.delete(analysisId);
    }
    

    @Override
    public void setSearchId(int searchId) {
        this.searchId = searchId;
    }

    @Override
    public void setDirectory(String directory) {
        this.dataDirectory = directory;
    }

    @Override
    public void setRemoteDirectory(String remoteDirectory) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setRemoteServer(String remoteServer) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public String getPreUploadCheckMsg() {
        return preUploadCheckMsg.toString();
    }
    
    private void appendToMsg(String msg) {
        this.preUploadCheckMsg.append(msg+"\n");
    }
    
    @Override
    public boolean preUploadCheckPassed() {
        
        preUploadCheckMsg = new StringBuilder();
        
        // checks for
        // 1. valid data directory
        File dir = new File(dataDirectory);
        if(!dir.exists()) {
            appendToMsg("Data directory does not exist: "+dataDirectory);
            return false;
        }
        if(!dir.isDirectory()) {
            appendToMsg(dataDirectory+" is not a directory");
            return false;
        }
        
        // 2. valid and supported analysis data format
        // 3. consistent data format 
        File[] files = dir.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                String name_uc = name.toLowerCase();
                return name_uc.endsWith(".sqt");
            }});
        for (int i = 0; i < files.length; i++) {
            filenames.add(files[i].getName());
        }
        
        // make sure all files are of the same type
        for (String file: filenames) {
            String sqtFile = dataDirectory+File.separator+file;
            SearchFileFormat myType = SQTFileReader.getSearchFileType(sqtFile);
            
            if (myType == null) {
                appendToMsg("Cannot determine SQT type for file: "+file);
                return false;
            }
            
            
            if(myType != getAnalysisFileFormat()) {
                appendToMsg("Unsupported SQT type for uploader. Expected: "+getAnalysisFileFormat()+"; Found: "+myType+". File: "+file);
                return false;
            }
        }
        
        // 4. If we know the search data file names that will be uploaded match them with up with the analysis SQT files
        //    and make sure there is a raw data file for each SQT file
        if(searchDataFileNames != null) {
            for(String file:filenames) {
//                String filenoext = removeFileExtension(file);
                if(!searchDataFileNames.contains(file)) {
                    appendToMsg("No corresponding search data file found for: "+file);
                    return false;
                }
            }
        }
        
        preUploadCheckDone = true;
        
        return true;
    }
    
    private String removeFileExtension(String file) {
        int idx = file.lastIndexOf(".sqt");
        if(idx == -1)
            idx = file.lastIndexOf(".SQT");
        if(idx != -1)
            return file.substring(0, idx);
        else
            return file;
    }  
    
    
    @Override
    public String getUploadSummary() {
        return "\tAnalysis file format: "+getAnalysisFileFormat()+
        "\n\t#Analysis files in Directory: "+filenames.size()+"; #Uploaded: "+numAnalysisUploaded;
    }

    @Override
    public SearchFileFormat getAnalysisFileFormat() {
        return SearchFileFormat.SQT_PERC;
    }
    
    @Override
    public void setSearchProgram(Program searchProgram) {
        this.searchProgram = searchProgram;
    }
    
    @Override
    public void setSearchDataFileNames(List<String> searchDataFileNames) {
        this.searchDataFileNames = searchDataFileNames;
    }

    public int getMaxPsmRank() {

        for (String file: filenames) {
            String filePath = dataDirectory+File.separator+file;
            // if the file does not exist skip over to the next
            if (!(new File(filePath).exists()))
                continue;
            
            PercolatorSQTFileReader provider = new PercolatorSQTFileReader();
            try {
                provider.open(filePath, searchProgram);
            }
            catch (DataProviderException e) {
                provider.close();
                log.error("Error opening PercolatorSQTFileReader", e);
                return Integer.MAX_VALUE;
            }
             
            try {
                SQTRunSearchIn search = provider.getSearchHeader();

                if (search instanceof SQTHeader) {
                    SQTHeader header = (SQTHeader)search;
                    for(SQTHeaderItem h: header.getHeaders()) {
                        if(h.getName().equalsIgnoreCase("percolator")) {
                            String cmdline = h.getValue();
                            String[] tokens = cmdline.split("\\s+");
                            for(int i = 0; i < tokens.length; i++) {
                                String val = tokens[i];
                                if(val.startsWith("-m")) {
                                    int rank = Integer.parseInt(tokens[++i]);
                                    return rank;
                                }
                                                    
                            }
                        }
                    }
                    // If we are here it means we did not find the -m flag in the percolator command-line
                    // This means percolator will only use the top hit for each scan+charge combination
                    return 1;
                }
            }
            catch (DataProviderException e) {
                log.error("Error reading percolator SQT file", e);
                return Integer.MAX_VALUE;
            }
            finally {provider.close();}
        }
        log.warn("Could not read percolator command-line to determine value of -m argument. "+
                "ALL sequest results will be uploaded.");
        return Integer.MAX_VALUE;
    }
}
