/**
 * PercolatorXmlDataUploadService.java
 * @author Vagisha Sharma
 * Sep 11, 2010
 */
package org.yeastrc.ms.service.percolator;

import java.io.File;
import java.io.FilenameFilter;
import java.math.BigDecimal;
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
import org.yeastrc.ms.dao.analysis.percolator.PercolatorPeptideResultDAO;
import org.yeastrc.ms.dao.analysis.percolator.PercolatorResultDAO;
import org.yeastrc.ms.dao.run.MsScanDAO;
import org.yeastrc.ms.dao.search.MsRunSearchDAO;
import org.yeastrc.ms.dao.search.MsSearchDAO;
import org.yeastrc.ms.dao.search.MsSearchResultDAO;
import org.yeastrc.ms.domain.analysis.MsSearchAnalysis;
import org.yeastrc.ms.domain.analysis.impl.RunSearchAnalysisBean;
import org.yeastrc.ms.domain.analysis.impl.SearchAnalysisBean;
import org.yeastrc.ms.domain.analysis.percolator.PercolatorParam;
import org.yeastrc.ms.domain.analysis.percolator.PercolatorPeptideResult;
import org.yeastrc.ms.domain.analysis.percolator.PercolatorResult;
import org.yeastrc.ms.domain.analysis.percolator.PercolatorResultDataWId;
import org.yeastrc.ms.domain.analysis.percolator.PercolatorResultIn;
import org.yeastrc.ms.domain.analysis.percolator.impl.PercolatorPeptideResultBean;
import org.yeastrc.ms.domain.analysis.percolator.impl.PercolatorResultDataBean;
import org.yeastrc.ms.domain.search.MsResidueModificationIn;
import org.yeastrc.ms.domain.search.MsRunSearch;
import org.yeastrc.ms.domain.search.MsSearch;
import org.yeastrc.ms.domain.search.MsSearchResult;
import org.yeastrc.ms.domain.search.MsSearchResultPeptide;
import org.yeastrc.ms.domain.search.MsTerminalModificationIn;
import org.yeastrc.ms.domain.search.Program;
import org.yeastrc.ms.domain.search.SearchFileFormat;
import org.yeastrc.ms.parser.DataProviderException;
import org.yeastrc.ms.parser.percolator.PercolatorXmlFileChecker;
import org.yeastrc.ms.parser.percolator.PercolatorXmlFileReader;
import org.yeastrc.ms.parser.percolator.PercolatorXmlPeptideResult;
import org.yeastrc.ms.parser.percolator.PercolatorXmlPsmId;
import org.yeastrc.ms.parser.percolator.PercolatorXmlResult;
import org.yeastrc.ms.service.AnalysisDataUploadService;
import org.yeastrc.ms.service.UploadException;
import org.yeastrc.ms.service.UploadException.ERROR_CODE;
import org.yeastrc.ms.service.percolator.stats.PercolatorFilteredPsmStatsSaver;
import org.yeastrc.ms.service.percolator.stats.PercolatorFilteredSpectraStatsSaver;
import org.yeastrc.ms.service.sqtfile.PercolatorSQTDataUploadService;
import org.yeastrc.utils.StringUtils;

/**
 * 
 */
public class PercolatorXmlDataUploadService implements
		AnalysisDataUploadService {


	private static final Logger log = Logger.getLogger(PercolatorSQTDataUploadService.class);

	private List<String> percXmlFiles;
	
    private final MsSearchResultDAO resultDao;
    private final MsSearchAnalysisDAO analysisDao;
    private final MsRunSearchDAO runSearchDao;
    private final PercolatorParamsDAO paramDao;
    private final MsSearchDAO searchDao;
    private final MsRunSearchAnalysisDAO runSearchAnalysisDao;
    private final PercolatorResultDAO percResultDao;
    private final PercolatorPeptideResultDAO peptResultDao;

    private static final int BUF_SIZE = 1000;

    private List<? extends MsResidueModificationIn> dynaResidueMods;
    private List<? extends MsTerminalModificationIn> dynaTermMods;
    
    // these are the things we will cache and do bulk-inserts
    private List<PercolatorResultDataWId> percolatorResultDataList; // percolator scores (PSM - level)
    private List<PercolatorPeptideResult> percolatorPeptideResultList; // percolator peptide-level scores
    
    private Set<Integer> uploadedResultIds;

    private List<Integer> analysisIds;
    private boolean presetAnalysisId = false;
    private int numFilesUploaded;
    
    //private int analysisId;
    private String comments;
    private Map<String,Integer> runSearchIdMap; // key = filename; value = runSearchId
    private Map<String, Integer> runSearchAnalysisIdMap; // key = filename; value runSearchAnalysisId
    Map<Integer, Integer> runIdMap = new HashMap<Integer, Integer>(); // key = runSearchId; value = runId
    
    private int searchId;
    private String dataDirectory;
    private StringBuilder preUploadCheckMsg;
    private boolean preUploadCheckDone = false;
    
//    private List<String> filenames;
    private List<String> searchDataFileNames;
    private Program searchProgram;

	private int numPsmUploaded;
	private int numPeptUploaded;
	
	private Map<String, List<PercolatorXmlPeptideResult>> duplicatePeptides = new HashMap<String, List<PercolatorXmlPeptideResult>>();
	
	
    
    public PercolatorXmlDataUploadService() {
    	
        this.percolatorResultDataList = new ArrayList<PercolatorResultDataWId>(BUF_SIZE);
        this.percolatorPeptideResultList = new ArrayList<PercolatorPeptideResult>(BUF_SIZE);
        this.dynaResidueMods = new ArrayList<MsResidueModificationIn>();
        this.dynaTermMods = new ArrayList<MsTerminalModificationIn>();
        
        uploadedResultIds = new HashSet<Integer>();
        
        DAOFactory daoFactory = DAOFactory.instance();
        this.analysisDao = daoFactory.getMsSearchAnalysisDAO();
        this.runSearchDao = daoFactory.getMsRunSearchDAO();
        this.paramDao  = daoFactory.getPercoltorParamsDAO();
        this.searchDao = daoFactory.getMsSearchDAO();
        this.runSearchAnalysisDao  = daoFactory.getMsRunSearchAnalysisDAO();
        
        this.resultDao = daoFactory.getMsSearchResultDAO();
        this.percResultDao = daoFactory.getPercolatorResultDAO();
        this.peptResultDao = daoFactory.getPercolatorPeptideResultDAO();
        
    }
    
    void reset() {

        //analysisId = 0;
    	if(this.analysisIds == null)
    		this.analysisIds = new ArrayList<Integer>();
    	this.analysisIds.clear();
    	
        if(runSearchIdMap != null)
        	runSearchIdMap.clear();
        
        numFilesUploaded = 0;

        resetCaches();

        dynaResidueMods.clear();
        dynaTermMods.clear();
        
    }

    void resetCaches() {
    	
        percolatorResultDataList.clear();
        percolatorPeptideResultList.clear();
        uploadedResultIds.clear();
        numPsmUploaded = 0;
        numPeptUploaded = 0;
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
        
        
        // get the modifications used for this search. Will be used for parsing the peptide sequence
        getSearchModifications(searchId);
        
        
        // now upload results in the Precolator Xml file(s)
        
        for(String percXmlFile: this.percXmlFiles) {
        	
        	resetCaches();
        	
        	// determine if a file with this name has already been uploaded for this experiment
            MsSearchAnalysis ppAnalysis = analysisDao.loadAnalysisForFileName(percXmlFile, searchId);
            if(ppAnalysis != null) {
                log.info("Analysis file: "+percXmlFile+" has already been uploaded. AnalysisID: "+ppAnalysis.getId());
                this.analysisIds.add(ppAnalysis.getId());
                continue;
            }
            
        	String filePath = dataDirectory+File.separator+percXmlFile;

        	log.info("Uploading analysis results in Percolator XML file: "+percXmlFile);
        	
        	// Open the XML file and read the Percolator version, params etc.
        	PercolatorXmlFileReader reader = new PercolatorXmlFileReader();
        	reader.setSearchProgram(Program.SEQUEST);
        	reader.setDynamicResidueMods(this.dynaResidueMods);
        	try {
        		reader.open(filePath);
        	}
        	catch(DataProviderException e) {
        		reader.close();
        		UploadException ex = new UploadException(ERROR_CODE.PERC_XML_ERROR, e);
        		ex.setFile(percXmlFile);
        		ex.setErrorMessage(e.getMessage()+"\n\t!!!PERCOLATOR XML FILE WILL NOT BE UPLOADED!!!");
        		throw ex;
        	}

        	// create a new entry in the msSearchAnalysis table (if required)
        	int analysisId = 0;
        	if(presetAnalysisId)
        		analysisId = analysisIds.get(0);
        	
        	try {
        		if(!presetAnalysisId) {
        			analysisId = saveTopLevelAnalysis(percXmlFile, reader.getPercolatorVersion());
        			this.analysisIds.add(analysisId);
        		}
        		else
        			updateProgramVersion(analysisId, reader.getPercolatorVersion());
        	}
        	catch (UploadException e) {
        		e.appendErrorMessage("\n\t!!!PERCOLATOR ANALYSIS WILL NOT BE UPLOADED\n");
        		log.error(e.getMessage(), e);
        		reader.close();
        		throw e;
        	}


        	// Add the Percolator parameters
        	try {
        		addPercolatorParams(reader.getPercolatorParams(), analysisId);
        	}
        	catch(UploadException e) {
        		e.appendErrorMessage("\n\tDELETING PERCOLATOR ANALYSIS...ID: "+analysisId+"\n");
        		log.error(e.getMessage(), e);
        		deleteAnalysis(analysisId);
        		reader.close();
        		throw e;
        	}

        	// NOTE: 12/16/11; Added to deal with duplicate peptide_ids in the <peptides> section of Percolator output
            // Read the file once and get a list of all the duplicate peptides in the file
            PercolatorXmlFileChecker checker = new PercolatorXmlFileChecker();
            Set<String> duplicates = null;
			try {
				duplicates = checker.getDuplicatePeptides(filePath);
				for (String duplicate : duplicates) {
					this.duplicatePeptides.put(duplicate, new ArrayList<PercolatorXmlPeptideResult>());
				}
			} catch (DataProviderException e1) {
				log.error("Error reading file while looking for duplicate peptides.", e1);
			}
			if(duplicatePeptides.size() > 0) {
				log.error("DUPLICATE PEPTIDES: "+duplicatePeptides.size()+"\n"+StringUtils.makeCommaSeparated(duplicates));
			}
            

        	int numRunSearchAnalysisUploaded = 0;
        	try {
        		numRunSearchAnalysisUploaded = uploadXml(reader, analysisId);
        	}
        	catch (UploadException ex) {
        		ex.appendErrorMessage("\n\tDELETING PERCOLATOR ANALYSIS...ID: "+analysisId+"\n");
        		deleteAnalysis(analysisId);
        		numRunSearchAnalysisUploaded = 0;
        		reader.close();
        		throw ex;
        	}
        	finally {
        		reader.close();
        	}

        	// if no analyses were uploaded delete the top level search analysis
        	if (numRunSearchAnalysisUploaded == 0) {
        		UploadException ex = new UploadException(ERROR_CODE.NO_PERC_ANALYSIS_UPLOADED);
        		ex.appendErrorMessage("\n\tDELETING PERCOLATOR ANALYSIS...ID: "+analysisId+"\n");
        		deleteAnalysis(analysisId);
        		numRunSearchAnalysisUploaded = 0;
        		reader.close();
        		throw ex;
        	}


        	reader.close();

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
        	
        	numFilesUploaded++;
        }
    }
    
    public List<Integer> getUploadedAnalysisIds() {
        return this.analysisIds;
    }
    
    /**
     * Method used only by MsAnalysisUploader for uploading results of a Percolator execute job
     * @param analysisId
     */
	public void setAnalysisId(int analysisId) {
		// This method is to be used only by MsAnalysisUploader. 
		analysisIds = new ArrayList<Integer>();
		analysisIds.add(analysisId);
		this.presetAnalysisId = true;
    }
    
    public void setComments(String comments) {
    	this.comments = comments;
    }
    
    
    private Map<String, Integer> createRunSearchIdMap() throws UploadException {
        
        Map<String, Integer> runSearchIdMap = new HashMap<String, Integer>(this.searchDataFileNames.size()*2);
        
        for(String file: searchDataFileNames) {
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


    private void addPercolatorParams(List<PercolatorParam> params, int analysisId) throws UploadException {
        
        for (PercolatorParam param: params) {
            try {
                paramDao.saveParam(param, analysisId);
            }
            catch(RuntimeException e) {
                UploadException ex = new UploadException(ERROR_CODE.RUNTIME_ERROR, e);
                ex.appendErrorMessage("Exception saving Percolator param (name: "+param.getParamName()+", value: "+param.getParamValue()+")");
                throw ex;
            }
        }
    }

    private void getSearchModifications(int searchId) throws UploadException {
       MsSearch search = searchDao.loadSearch(searchId);
       if(search == null) {
    	   UploadException ex = new UploadException(ERROR_CODE.GENERAL);
    	   ex.setErrorMessage("No search found with ID: "+searchId);
    	   throw ex;
       }
       this.dynaResidueMods = search.getDynamicResidueMods();
       this.dynaTermMods = search.getDynamicTerminalMods();
    }

    private int saveTopLevelAnalysis(String filename, String version) throws UploadException {
        
        SearchAnalysisBean analysis = new SearchAnalysisBean();
//        analysis.setSearchId(searchId);
        analysis.setAnalysisProgram(Program.PERCOLATOR);
        analysis.setAnalysisProgramVersion(version);
        analysis.setComments(comments);
        analysis.setFilename(filename);
        try {
            return analysisDao.save(analysis);
        }
        catch(RuntimeException e) {
            UploadException ex = new UploadException(ERROR_CODE.RUNTIME_SQT_ERROR, e);
            ex.setErrorMessage(e.getMessage());
            throw ex;
        }
    }
    
    private void updateProgramVersion(int analysisId, String programVersion) {
        try {
            analysisDao.updateAnalysisProgramVersion(analysisId, programVersion);
        }
        catch(RuntimeException e) {
            log.warn("Error updating program version for analysisID: "+analysisId, e);
        }
    }
    
    private int uploadXml(PercolatorXmlFileReader reader, int analysisId) throws UploadException {
        
        log.info("BEGIN PERCOLATOR XML FILE UPLOAD");
        
        long startTime = System.currentTimeMillis();
        
        try {
           runSearchIdMap = createRunSearchIdMap();
        }
        catch(UploadException e) {
            e.appendErrorMessage("\n\t!!!PERCOLATOR ANALYSIS WILL NOT BE UPLOADED\n");
            throw e;
        }
        
        // read the PSMs
        int numRunSearchAnalysesUploaded = uploadPsms(reader, analysisId);
        
        // Now read peptide-level results, if any
    	uploadPeptideResults(reader, analysisId);
        
    	// If there were any duplicate peptides upload them now. 
    	uploadDuplicatePeptides(analysisId);
    	
        long endTime = System.currentTimeMillis();
        
        log.info("END PERCOLATOR XML FILE UPLOAD; SEARCH_ANALYSIS_ID: "+analysisId+ " in "+(endTime - startTime)/(1000L)+"seconds\n");
        
        return numRunSearchAnalysesUploaded;
    }

    private void uploadDuplicatePeptides(int analysisId) throws UploadException {
		
    	if(duplicatePeptides.size() == 0)
    		return;
    	
    	StringBuilder buf = new StringBuilder();
    	
    	for(String key: this.duplicatePeptides.keySet()) {
    		
    		List<PercolatorXmlPeptideResult> results = this.duplicatePeptides.get(key);
    		Set<PercolatorXmlPsmId> uniqPsmIds = new HashSet<PercolatorXmlPsmId>();
    		
    		PercolatorXmlPeptideResult bestResult = results.get(0);
    		buf.append(key+" -- qvalues: ");
    		buf.append(bestResult.getQvalue());
    		
    		uniqPsmIds.addAll(bestResult.getPsmIds());
    		
    		for(int i = 1; i < results.size(); i++) {
    			buf.append(", ");
    			buf.append(results.get(i).getQvalue());
    			uniqPsmIds.addAll(results.get(i).getPsmIds());
    		}
    		
    		bestResult.getPsmIds().clear();
    		bestResult.getPsmIds().addAll(uniqPsmIds);
    		
    		PercolatorPeptideResultBean peptRes = makePeptideResult(analysisId, bestResult);
			uploadPercolatorPeptideResult(peptRes);
			
			buf.append("\n");
    	}
		
    	flushPeptideBuffer();
        log.info("Uploaded duplicate peptides");
        log.error("------------------ DUPLICATE Peptides ---------------\n"+buf.toString());
	}

	private int uploadPsms(PercolatorXmlFileReader reader, int analysisId) throws UploadException {
		
		runSearchAnalysisIdMap = new HashMap<String, Integer>(); // key = filename; value runSearchAnalysisId
        runIdMap = new HashMap<Integer, Integer>(); // key = runSearchId; value = runId
        
        
		try {
        	// First read the PSMs
        	while(reader.hasNextPsm()) {

        		PercolatorXmlResult result = (PercolatorXmlResult) reader.getNextPsm();
        		String sourceFileName = result.getFileName();
        		
        		// !! --------- IMPORTANT ---------------------------!
        		// THIS IS TO GET THE PEPTIPEDIA DATA UPLOADED
        		// PERCOLATOR CONSIDERS EVERYTHING AFTER THE FIRST DOT IN THE FILNAME AS THE EXTENSION
        		// sourceFileName += ".renum";
        		// !! -----------------------------------------------!
        		
        		Integer runSearchId = runSearchIdMap.get(sourceFileName);

        		// !! --------- IMPORTANT ---------------------------!
        		// THIS IS TO GET THE PEPTIPEDIA DATA UPLOADED
        		// PERCOLATOR CONSIDERS EVERYTHING AFTER THE FIRST DOT IN THE FILNAME AS THE EXTENSION
        		// if(runSearchId == null) {
        		//	sourceFileName = result.getFileName()+".ms3.renum";
        		//	runSearchId = runSearchIdMap.get(sourceFileName);
        		// }
        		// !! -----------------------------------------------!
        		
        		
        		if (runSearchId == null) {
        			UploadException ex = new UploadException(ERROR_CODE.NO_RUNSEARCHID_FOR_ANALYSIS_FILE);
        			ex.appendErrorMessage("File was: "+sourceFileName);
        			ex.appendErrorMessage("\n\tDELETING PERCOLATOR ANALYSIS...ID: "+analysisId+"\n");
        			deleteAnalysis(analysisId);
        			throw ex;
        		}

        		Integer runSearchAnalysisId = runSearchAnalysisIdMap.get(sourceFileName);
        		if(runSearchAnalysisId == null) {
        			runSearchAnalysisId = uploadRunSearchAnalysis(analysisId, runSearchId);
        			log.info("Created new msRunSearchAnalysis entry for file: "+result.getFileName()+"; runSearchID: "+runSearchId+
        					" (runSearchAnalysisId: "+runSearchAnalysisId+")");
        			runSearchAnalysisIdMap.put(sourceFileName, runSearchAnalysisId);
        		}

        		// get the runID. Will be needed to get the scan ID
                Integer runId = runIdMap.get(runSearchId);
                if(runId == null) {
                	runId = getRunIdForRunSearch(runSearchId);
                	if(runId == 0) {
                		UploadException ex = new UploadException(ERROR_CODE.NO_RUNID_FOR_SEARCH_FILE);
                        ex.setErrorMessage("No runID found for runSearchID: "+runSearchId);
                        throw ex;
                	}
                	runIdMap.put(runSearchId, runId);
                }
                
                BigDecimal observedMassBD = result.getObservedMass();
                double obsMass = observedMassBD != null ? observedMassBD.doubleValue() : -1.0;
        		int runSearchResultId = getMatchingSearchResultId(runSearchId, result.getScanNumber(), 
        				result.getCharge(), obsMass, result.getResultPeptide());
        		// upload the Percolator specific information for this result.
                uploadPercolatorResultData(result, runSearchAnalysisId, runSearchResultId);

        	}
        	flushPsmBuffer(); // save any remaining cached data
        	log.info("Uploaded PSMs");
        	
        }
        catch(DataProviderException e) {
        	UploadException ex = new UploadException(ERROR_CODE.PERC_XML_ERROR, e);
        	ex.setErrorMessage(e.getMessage()+"\n\t!!!PERCOLATOR XML FILE WILL NOT BE UPLOADED!!!");
        	throw ex;
        }
        catch (RuntimeException e) { // most likely due to SQL exception
        	UploadException ex = new UploadException(ERROR_CODE.RUNTIME_ERROR, e);
        	ex.setErrorMessage(e.getMessage()+"\n\t!!!PERCOLATOR XML FILE WILL NOT BE UPLOADED!!!");
        	throw ex;
        }
        
        return runSearchAnalysisIdMap.size();
	}
    
	private void uploadPeptideResults(PercolatorXmlFileReader reader, int analysisId)
			throws UploadException {
		
		
		try {
			while(reader.hasNextPeptide()) {
				PercolatorXmlPeptideResult peptide = (PercolatorXmlPeptideResult) reader.getNextPeptide();
				
				// log.info("Found peptide: "+peptide.getResultPeptide().getModifiedPeptidePS(false));
				// NOTE: 12/16/11; Added to deal with duplicate peptide_ids in the <peptides> section of Percolator output
				// If this peptide is present more than once in the peptides section, save it later
				String seq = peptide.getResultPeptide().getModifiedPeptidePS(false);
				if(this.duplicatePeptides.containsKey(seq)) {
					duplicatePeptides.get(seq).add(peptide);
					continue;
				}
				
				PercolatorPeptideResultBean peptRes = makePeptideResult(analysisId, peptide);
				uploadPercolatorPeptideResult(peptRes);
			}
			
		} catch (DataProviderException e) {
			UploadException ex = new UploadException(ERROR_CODE.PERC_XML_ERROR, e);
        	ex.setErrorMessage(e.getMessage()+"\n\t!!!PERCOLATOR XML FILE WILL NOT BE UPLOADED!!!");
        	throw ex;
        }
        catch (RuntimeException e) { // most likely due to SQL exception
        	UploadException ex = new UploadException(ERROR_CODE.RUNTIME_ERROR, e);
        	ex.setErrorMessage(e.getMessage()+"\n\t!!!PERCOLATOR XML FILE WILL NOT BE UPLOADED!!!");
        	throw ex;
        }
        
        flushPeptideBuffer();
        log.info("Uploaded peptides");
	}

	private PercolatorPeptideResultBean makePeptideResult(int analysisId,
			PercolatorXmlPeptideResult peptide) throws UploadException {
		
		PercolatorPeptideResultBean peptRes = new PercolatorPeptideResultBean();
		peptRes.setSearchAnalysisId(analysisId);
		peptRes.setResultPeptide(peptide.getResultPeptide());
		peptRes.setQvalue(peptide.getQvalue());
		peptRes.setPosteriorErrorProbability(peptide.getPosteriorErrorProbability());
		peptRes.setPvalue(peptide.getPvalue());
		peptRes.setDiscriminantScore(peptide.getDiscriminantScore());
		
		Set<Integer> psmIds = new HashSet<Integer>(peptide.getPsmIds().size());
		
		
		for(PercolatorXmlPsmId psmId: peptide.getPsmIds()) {
			// get a PercolatorResult ID for this psm;
			String sourceFileName = psmId.getFileName();
			
			// !! --------- IMPORTANT ---------------------------!
			// THIS IS TO GET THE PEPTIPEDIA DATA UPLOADED
			// PERCOLATOR CONSIDERS EVERYTHING AFTER THE FIRST DOT IN THE FILNAME AS THE EXTENSION
			// sourceFileName += ".renum";
			// !! -----------------------------------------------!
			
			Integer runSearchId = runSearchIdMap.get(sourceFileName);

			// !! --------- IMPORTANT ---------------------------!
			// THIS IS TO GET THE PEPTIPEDIA DATA UPLOADED
			// PERCOLATOR CONSIDERS EVERYTHING AFTER THE FIRST DOT IN THE FILNAME AS THE EXTENSION
			// if(runSearchId == null) {
			//	sourceFileName = psmId.getFileName()+".ms3.renum";
			//	runSearchId = runSearchIdMap.get(sourceFileName);
			// }
			// !! -----------------------------------------------!
			
			
			if (runSearchId == null) {
				UploadException ex = new UploadException(ERROR_CODE.NO_RUNSEARCHID_FOR_ANALYSIS_FILE);
				ex.appendErrorMessage("File was: "+sourceFileName);
				ex.appendErrorMessage("\n\tDELETING PERCOLATOR ANALYSIS...ID: "+analysisId+"\n");
				deleteAnalysis(analysisId);
				throw ex;
			}
			
			List<Integer> runSearchResultIds = null;
			try {
				// We may have multiple matching results for the same scan, charge and peptide sequence
				// due to Bullseye.  Add them all
				runSearchResultIds = this.getMatchingSearchResultIds(runSearchId, psmId.getScanNumber(), 
						psmId.getCharge(),peptide.getResultPeptide());
			}
			catch(UploadException e) {
				// TODO -- This is probably because this PSM had XCorrRank > 1 in the sequest results
				// We only upload XCorrRank = 1 results. Chances are there is no matching Percolator PSM
				// (in the <psms></psms> list) either.  Ignore it for now.
//				if(e.getErrorCode() == ERROR_CODE.NO_MATCHING_SEARCH_RESULT) {
//					log.warn(e.getErrorMessage());
//					log.error("runSearchId: "+runSearchId+"; scan number: "+psmId.getScanNumber()+"; charge: "+psmId.getCharge()+"; peptide: "+peptide.getResultPeptide().getPeptideSequence());
//					continue;
//				}
//				else
					throw e;
			}
					
			Integer runSearchAnalysisId = runSearchAnalysisIdMap.get(sourceFileName);
			if(runSearchAnalysisId == null) {
				UploadException ex = new UploadException(ERROR_CODE.NO_RSANALYSISID_FOR_ANALYSIS_FILE);
				ex.appendErrorMessage("File was: "+sourceFileName);
				ex.appendErrorMessage("\n\tDELETING PERCOLATOR ANALYSIS...ID: "+analysisId+"\n");
				deleteAnalysis(analysisId);
				throw ex;
			}
			
			// get the PercolatorResult ID for this runSearchResultId;
			for(Integer runSearchResultId: runSearchResultIds) {
				PercolatorResult percolatorResult = percResultDao.loadForRunSearchAnalysis(runSearchResultId, runSearchAnalysisId);
				if(percolatorResult == null) {
					UploadException ex = new UploadException(ERROR_CODE.GENERAL);
					ex.appendErrorMessage("NO MATCHING PERCOLATOR RESULT FOUND FOR runSearchResultId: "+runSearchResultId+" and runSearchAnalysisId: "+runSearchAnalysisId);
					ex.appendErrorMessage("scan number: "+psmId.getScanNumber()+"; charge: "+psmId.getCharge()+"; peptide: "+peptide.getResultPeptide().getPeptideSequence());
					//ex.appendErrorMessage("\n\tDELETING PERCOLATOR ANALYSIS...ID: "+analysisId+"\n");
					//deleteAnalysis(analysisId);
					//numAnalysisUploaded = 0;
					//throw ex;
					log.error(ex.getMessage());
					continue;
				}
				psmIds.add(percolatorResult.getPercolatorResultId());
			}
		}
		peptRes.setPsmIdList(new ArrayList<Integer>(psmIds));
		return peptRes;
	}

	
    // get a matching runSearchResultId
    private int getMatchingSearchResultId(int runSearchId, int scanNumber, int charge, 
    		double observedMass, MsSearchResultPeptide peptide) 
    	throws UploadException {
        
    	Integer runId = runIdMap.get(runSearchId);
    	if(runId == null) { // by now we should already have a matching runID, but just in case
    		UploadException ex = new UploadException(ERROR_CODE.NO_RUNID_FOR_SEARCH_FILE);
            ex.setErrorMessage("No runID found for runSearchID: "+runSearchId);
            throw ex;
    	}
    	
        int scanId = getScanId(runId, scanNumber);
           
        try {
        	
        	List<MsSearchResult> matchingResults = resultDao.loadResultForSearchScanChargePeptide(runSearchId, scanId, 
                    charge, 
                    peptide.getPeptideSequence());
        	
        	// no matches were found
            if(matchingResults.size() == 0) {
            	UploadException ex = new UploadException(ERROR_CODE.NO_MATCHING_SEARCH_RESULT);
                ex.setErrorMessage("No matching search result was found for runSearchId: "+runSearchId+
                		" scanId: "+scanId+"; charge: "+charge+"; mass: "+observedMass+
                		"; peptide: "+peptide.getPeptideSequence()+
                		"; modified peptide: "+peptide.getModifiedPeptidePS(false));
                throw ex;
                //log.warn(ex.getErrorMessage());
            }
            
            else if(matchingResults.size() == 1) 
                return matchingResults.get(0).getId();
        	
        	// matchingResults.size() > 1
        	else { // this can happen if 
        		   // a scan searched with same charge but different M+H (due to Bullseye) results in same peptide match
        		   // OR results with same peptide sequence but different modifications
        		
        		MsSearchResult bestRes = null;
        		
        		// Get the match where the observed mass is the same
        		List<MsSearchResult> matchingResults2 = new ArrayList<MsSearchResult>();
        		
        		for(MsSearchResult res: matchingResults) {
        			// <exp_mass> in Percolator's xml output is rounded to the 4th decimal place,
        			// observed mass in Sequest's SQT output is not. Do a mass match after rounding.
        			double roundedToFourth = ((int)((10000 * res.getObservedMass().doubleValue()) + 0.5)) / 10000.0;
        			if(roundedToFourth == observedMass) {
        				matchingResults2.add(res);
        			}
        		}
        		
        		if(matchingResults2.size() == 1)
        			bestRes = matchingResults2.get(0);
        		else {
        		
        			String modifiedPeptideSeq = peptide.getModifiedPeptidePS(false);
        			if(matchingResults2.size() > 0)
        			{
            			// check for exact modified sequence
            			List<MsSearchResult> matchingResults3 = new ArrayList<MsSearchResult>();
            			for(MsSearchResult res: matchingResults2) {
            				if(res.getResultPeptide().getModifiedPeptidePS(false).equals(modifiedPeptideSeq)) {
            					matchingResults3.add(res);
            				}
            			}
            			
            			if(matchingResults3.size() == 1)
                			bestRes = matchingResults3.get(0);
        			}
        			else if(matchingResults2.size() == 0)
        			{
        				// Note: 11/06/13
        				// Added for Alex Zelter's data, searched via Comet and manipulated to conform to 
        				// MSDaPl's requirements.
        				// <exp_mass> in his Percolator output has m/z values instead of M+H, so the 
        				// check for matching <exp_mass> and observed mass in SQT fails. 
        				// I could move the check for modified sequence before the check for mass, 
        				// but that check takes longer since it involves querying the database for 
        				// modifications. For most of the files from the MacCoss lab's pipelines (hermie and LabKey)
        				// matching the mass is sufficient.
        				
        				// Check for exact modified sequence
            			List<MsSearchResult> matchingResults4 = new ArrayList<MsSearchResult>();
            			for(MsSearchResult res: matchingResults) {
            				if(res.getResultPeptide().getModifiedPeptidePS(false).equals(modifiedPeptideSeq)) {
            					matchingResults4.add(res);
            				}
            			}
            			
            			if(matchingResults4.size() == 1)
                			bestRes = matchingResults4.get(0);
        				
        			}
        		}
        		
        		
        		// If we still don't have a definite match
        		if(bestRes == null) {
        			UploadException ex = new UploadException(ERROR_CODE.MULTI_MATCHING_SEARCH_RESULT);
        			ex.setErrorMessage("Multiple matching search results were found for Percolator result with runSearchId: "+runSearchId+
        					" scanId: "+scanId+"; charge: "+charge+"; mass: "+observedMass+
        					"; peptide: "+peptide.getPeptideSequence()+
        					"; modified peptide: "+peptide.getModifiedPeptidePS(false));
        			throw ex;
        		}
        		else
        			return bestRes.getId();
        	}
        	
        }
        catch(RuntimeException e) {
            UploadException ex = new UploadException(ERROR_CODE.RUNTIME_ERROR, e);
            ex.setErrorMessage(e.getMessage());
            throw ex;
        } 
    }
    
    
    private List<Integer> getMatchingSearchResultIds(int runSearchId, int scanNumber, int charge, 
    		MsSearchResultPeptide peptide) 
    	throws UploadException {
        
    	Integer runId = runIdMap.get(runSearchId);
    	if(runId == null) { // by now we should already have a matching runID, but just in case
    		UploadException ex = new UploadException(ERROR_CODE.NO_RUNID_FOR_SEARCH_FILE);
            ex.setErrorMessage("No runID found for runSearchID: "+runSearchId);
            throw ex;
    	}
    	
    	int scanId = getScanId(runId, scanNumber);
           
        try {
        	
        	String modifiedPeptideSeq = peptide.getModifiedPeptidePS(false);
        	
        	List<Integer> matchingResultIds = new ArrayList<Integer>();
        	
        	List<MsSearchResult> matchingResults = resultDao.loadResultForSearchScanChargePeptide(runSearchId, scanId, 
                    charge, 
                    peptide.getPeptideSequence());
        	
        	// no matches were found
            if(matchingResults.size() == 0) {
            	UploadException ex = new UploadException(ERROR_CODE.NO_MATCHING_SEARCH_RESULT);
                ex.setErrorMessage("No matching search result was found for runSearchId: "+runSearchId+
                		" scanId: "+scanId+"; charge: "+charge+//"; mass: "+observedMass+
                		"; peptide: "+peptide.getPeptideSequence()+
                		"; modified peptide: "+peptide.getModifiedPeptidePS(false));
                throw ex;
                //log.warn(ex.getErrorMessage());
            }
            
            for(MsSearchResult mRes: matchingResults) {
            	// make sure the modified sequence is the same
            	if(mRes.getResultPeptide().getModifiedPeptidePS(false).equals(modifiedPeptideSeq))
            		matchingResultIds.add(mRes.getId());
            }
            
            if(matchingResultIds.size() == 0) {
            	UploadException ex = new UploadException(ERROR_CODE.NO_MATCHING_SEARCH_RESULT);
                ex.setErrorMessage("No matching search results found after matching modified sequences for runSearchId: "+runSearchId+
                		" scanId: "+scanId+"; charge: "+charge+//"; mass: "+observedMass+
                		"; peptide: "+peptide.getPeptideSequence()+
                		"; modified peptide: "+peptide.getModifiedPeptidePS(false));
                throw ex;
            }
        	
        	return matchingResultIds;
        	
        }
        catch(RuntimeException e) {
            UploadException ex = new UploadException(ERROR_CODE.RUNTIME_ERROR, e);
            ex.setErrorMessage(e.getMessage());
            throw ex;
        }
    }
    
    private int getRunIdForRunSearch(int runSearchId) {
        MsRunSearch rs = runSearchDao.loadRunSearch(runSearchId);
        if(rs != null)
            return rs.getRunId();
        else
            return 0;
    }
    

    // Create an entry in the msRunSearchAnalysis table
    private final int uploadRunSearchAnalysis(int analysisId, int runSearchId)
        throws DataProviderException {

        // save the run search analysis and return the database id
        RunSearchAnalysisBean rsa = new RunSearchAnalysisBean();
        rsa.setAnalysisFileFormat(SearchFileFormat.XML_PERC);
        rsa.setAnalysisId(analysisId);
        rsa.setRunSearchId(runSearchId);
        return runSearchAnalysisDao.save(rsa);
    }

    private boolean uploadPercolatorResultData(PercolatorResultIn resultData, int rsAnalysisId, int searchResultId) {
        // upload the Percolator specific result information if the cache has enough entries
        if (percolatorResultDataList.size() >= BUF_SIZE) {
            uploadPercolatorResultBuffer();
        }
        
        // TODO THIS IS TEMP TILL I SORT OUT THE DUPLICATE RESULTS IN PERCOLATOR SQT FILES
        if(uploadedResultIds.contains(searchResultId)) {
        	log.warn("MULTIPLE RESULTS FOR searchResultId: "+searchResultId);
            return false;
        }
        uploadedResultIds.add(searchResultId);
        
        
        // add the Percolator specific information for this result to the cache
        PercolatorResultDataBean res = new PercolatorResultDataBean();
        res.setRunSearchAnalysisId(rsAnalysisId);
        res.setSearchResultId(searchResultId);
        res.setPredictedRetentionTime(resultData.getPredictedRetentionTime());
        res.setDiscriminantScore(resultData.getDiscriminantScore());
        res.setPosteriorErrorProbability(resultData.getPosteriorErrorProbability());
        res.setQvalue(resultData.getQvalue());
        res.setPvalue(resultData.getPvalue());
        
       
        percolatorResultDataList.add(res);
        return true;
    }
    
    private boolean uploadPercolatorPeptideResult(PercolatorPeptideResult peptideResult)  {
    	
        // upload the Percolator specific result information if the cache has enough entries
        if (percolatorPeptideResultList.size() >= BUF_SIZE) {
            uploadPercolatorPeptideResultBuffer();
        }
        
        percolatorPeptideResultList.add(peptideResult);
        return true;
    }
    
    private void uploadPercolatorResultBuffer() {
        percResultDao.saveAllPercolatorResultData(percolatorResultDataList);
    	numPsmUploaded += percolatorResultDataList.size();
    	if(numPsmUploaded % 10000 == 0)
    		log.info(numPsmUploaded+"  psms uploaded...");
        percolatorResultDataList.clear();
    }
    
    private void flushPsmBuffer() {
        if (percolatorResultDataList.size() > 0) {
            uploadPercolatorResultBuffer();
        }
    }
    
    private void uploadPercolatorPeptideResultBuffer() {
        peptResultDao.saveAllPercolatorPeptideResults(percolatorPeptideResultList);
    	numPeptUploaded += percolatorPeptideResultList.size();
    	if(numPeptUploaded % 10000 == 0)
    		log.info(numPeptUploaded+"  peptides uploaded...");
        percolatorPeptideResultList.clear();
    }
    
    private void flushPeptideBuffer() {
        if (percolatorPeptideResultList.size() > 0) {
            uploadPercolatorPeptideResultBuffer();
        }
    }

    private int getScanId(int runId, int scanNumber) throws UploadException {

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
        
        // 2. Make sure we have Percolator-generated xml files
        percXmlFiles = new ArrayList<String>();
        File[] files = dir.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                String name_lc = name.toLowerCase();
                return (name_lc.endsWith(".xml") && !(name_lc.endsWith("presubperc.xml") && !(name_lc.endsWith(".pin.xml"))));
            }});
        for (int i = 0; i < files.length; i++) {
        	
        	File ifile = files[i];
        	try {
				if(PercolatorXmlFileReader.isSupportedPercolatorXml(ifile.getAbsolutePath())) {
					percXmlFiles.add(ifile.getName());
				}
			} catch (DataProviderException e) {
				appendToMsg("Error opening file: "+ifile.getAbsolutePath()+"\n"+e.getMessage());
                return false;
			}
        }
        if(percXmlFiles.size() == 0) {
        	appendToMsg("Could not find Percolator-generated XML files in directory: "+dataDirectory);
        	return false;
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
        "\n\t# files uploaded: "+numFilesUploaded;
    }

    @Override
    public SearchFileFormat getAnalysisFileFormat() {
        return SearchFileFormat.XML_PERC;
    }
    
    @Override
    public void setSearchProgram(Program searchProgram) {
        this.searchProgram = searchProgram;
    }
    
    @Override
    public void setSearchDataFileNames(List<String> searchDataFileNames) {
        this.searchDataFileNames = new ArrayList<String>(searchDataFileNames.size());
        for(String fileName: searchDataFileNames) {
        	int idx = fileName.lastIndexOf(".");
        	if(idx != -1) {
        		fileName = fileName.substring(0,idx);
        	}
        	this.searchDataFileNames.add(fileName);
        }
    }

    public int getMaxPsmRank() {

    	
    		// open one of the Percolator xml files 
            String filePath = dataDirectory+File.separator+percXmlFiles.get(0);
            
            PercolatorXmlFileReader provider = new PercolatorXmlFileReader();
            try {
                provider.open(filePath);
            }
            catch (DataProviderException e) {
                provider.close();
                log.error("Error opening PercolatorXmlFileReader", e);
                return Integer.MAX_VALUE;
            }
            finally { provider.close(); }
            
            List<PercolatorParam> params = provider.getPercolatorParams();
            for(PercolatorParam param: params) {
            	if(param.getParamName().equalsIgnoreCase("command_line")) {
            		String cmdline = param.getParamValue();
            		String[] tokens = cmdline.split(",");
                    for(int i = 0; i < tokens.length; i++) {
                        String val = tokens[i];
                        if(val.startsWith("-m")) {
                            int rank = Integer.parseInt(tokens[++i]);
                            return rank;
                        }
                    }
                    // If we are here it means we did not find the -m flag in the percolator command-line
                    // This means percolator will only use the top hit for each scan+charge combination
                    return 1;
            	}
            }
            log.warn("Could not read percolator command-line to determine value of -m argument. "+
            "ALL sequest results will be uploaded.");
            return Integer.MAX_VALUE;
    }

}
