package org.yeastrc.ms.service.pepxml;

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
import org.yeastrc.ms.dao.analysis.peptideProphet.PeptideProphetResultDAO;
import org.yeastrc.ms.dao.analysis.peptideProphet.PeptideProphetRocDAO;
import org.yeastrc.ms.dao.run.MsScanDAO;
import org.yeastrc.ms.dao.search.MsRunSearchDAO;
import org.yeastrc.ms.dao.search.MsSearchDAO;
import org.yeastrc.ms.dao.search.MsSearchResultDAO;
import org.yeastrc.ms.dao.search.MsSearchResultProteinDAO;
import org.yeastrc.ms.domain.analysis.MsSearchAnalysis;
import org.yeastrc.ms.domain.analysis.impl.RunSearchAnalysisBean;
import org.yeastrc.ms.domain.analysis.impl.SearchAnalysisBean;
import org.yeastrc.ms.domain.analysis.peptideProphet.BasePeptideProphetResultIn;
import org.yeastrc.ms.domain.analysis.peptideProphet.PeptideProphetROC;
import org.yeastrc.ms.domain.analysis.peptideProphet.PeptideProphetResultDataWId;
import org.yeastrc.ms.domain.analysis.peptideProphet.impl.PeptideProphetResultDataBean;
import org.yeastrc.ms.domain.search.MsResidueModification;
import org.yeastrc.ms.domain.search.MsRunSearch;
import org.yeastrc.ms.domain.search.MsSearch;
import org.yeastrc.ms.domain.search.MsSearchResult;
import org.yeastrc.ms.domain.search.MsSearchResultProtein;
import org.yeastrc.ms.domain.search.MsSearchResultProteinIn;
import org.yeastrc.ms.domain.search.MsTerminalModification;
import org.yeastrc.ms.domain.search.Program;
import org.yeastrc.ms.domain.search.SearchFileFormat;
import org.yeastrc.ms.domain.search.impl.SearchResultProteinBean;
import org.yeastrc.ms.domain.search.pepxml.PepXmlBaseSearchScanIn;
import org.yeastrc.ms.parser.DataProviderException;
import org.yeastrc.ms.parser.pepxml.PepXmlBaseFileReader;
import org.yeastrc.ms.service.AnalysisDataUploadService;
import org.yeastrc.ms.service.ModifiedSequenceBuilderException;
import org.yeastrc.ms.service.MsDataUploadProperties;
import org.yeastrc.ms.service.UploadException;
import org.yeastrc.ms.service.UploadException.ERROR_CODE;
import org.yeastrc.ms.service.pepxml.stats.ProphetFilteredPsmStatsSaver;
import org.yeastrc.ms.service.pepxml.stats.ProphetFilteredSpectraStatsSaver;
import org.yeastrc.ms.util.TimeUtils;

// This will upload the corresponding PeptideProphet results. 
public class PepxmlAnalysisDataUploadService implements AnalysisDataUploadService {

    private static final int BUF_SIZE = 500;
    
    private int searchId;
    private List<Integer> analysisIds;
    
    private String dataDirectory;
    private String remoteServer;
    private String remoteDirectory;
    private StringBuilder preUploadCheckMsg;
    
    private List<String> searchDataFileNames;
    private List<String> interactPepxmlFiles;

    private boolean preUploadCheckDone;
    
    private final MsScanDAO scanDao;
    private final MsRunSearchDAO runSearchDao;
    private final MsSearchResultDAO resDao;
    private final MsSearchDAO searchDao;
    private final MsSearchAnalysisDAO analysisDao;
    private final MsRunSearchAnalysisDAO runSearchAnalysisDao;
    private final PeptideProphetRocDAO rocDao;
    private final PeptideProphetResultDAO ppResDao;
    private final MsSearchResultProteinDAO proteinMatchDao;
    
    
    // these are the things we will cache and do bulk-inserts
    private List<PeptideProphetResultDataWId> prophetResultDataList; // PeptideProphet scores
    private List<MsSearchResultProtein> proteinMatchList;
    
    private List<MsResidueModification> dynaResidueMods;
    private List<MsTerminalModification> dynaTermMods;
    
    private int numAnalysisUploaded = 0;
    
    private StringBuilder uploadMsg;
    
    private Set<String> checkedPeptideProteinMatches; // TODO this should be removed soon
    private boolean checkPeptideProteinMatches = false;
    
    private String comments;
    
    //private static final Pattern fileNamePattern = Pattern.compile("interact\\S*.pep.xml");
    
    private static final Logger log = Logger.getLogger(PepxmlAnalysisDataUploadService.class.getName());
    
    public PepxmlAnalysisDataUploadService() {
        
        this.searchDataFileNames = new ArrayList<String>();
        this.interactPepxmlFiles = new ArrayList<String>();
        
        this.prophetResultDataList = new ArrayList<PeptideProphetResultDataWId>(BUF_SIZE);
        this.proteinMatchList = new ArrayList<MsSearchResultProtein>(BUF_SIZE);
        
        this.dynaResidueMods = new ArrayList<MsResidueModification>();
        this.dynaTermMods = new ArrayList<MsTerminalModification>();
        
        DAOFactory daoFactory = DAOFactory.instance();
        
        this.scanDao = daoFactory.getMsScanDAO();
        this.searchDao = daoFactory.getMsSearchDAO();
        this.runSearchDao = daoFactory.getMsRunSearchDAO();
        resDao = DAOFactory.instance().getMsSearchResultDAO();
        
        this.analysisDao = daoFactory.getMsSearchAnalysisDAO();
        this.runSearchAnalysisDao  = daoFactory.getMsRunSearchAnalysisDAO();
        
        this.rocDao = daoFactory.getPeptideProphetRocDAO();
        this.ppResDao = daoFactory.getPeptideProphetResultDAO();
        
        this.proteinMatchDao = daoFactory.getMsProteinMatchDAO();
        
        uploadMsg = new StringBuilder();
        this.analysisIds = new ArrayList<Integer>();
        
        checkedPeptideProteinMatches = new HashSet<String>();
        this.checkPeptideProteinMatches = MsDataUploadProperties.getCheckPeptideProteinMatches();
    }
    

    @Override
    public void setDirectory(String directory) {
        this.dataDirectory = directory;
    }

    @Override
    public void setRemoteDirectory(String remoteDirectory) {
        this.remoteDirectory = remoteDirectory;
    }

    @Override
    public void setRemoteServer(String remoteServer) {
        this.remoteServer = remoteServer;
    }
    
    @Override
    public String getPreUploadCheckMsg() {
        return preUploadCheckMsg.toString();
    }

    @Override
    public String getUploadSummary() {
        return "\tAnalysis file format: "+SearchFileFormat.PEPXML+
                uploadMsg.toString();
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
        
        // 2. Look for files with PeptideProphet results
        File[] files = dir.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                String name_uc = name.toLowerCase();
                return name_uc.endsWith(".pep.xml");
            }});
        
        boolean found = false;
        for (int i = 0; i < files.length; i++) {
        	String fileName = files[i].getName();
        	
        	PepXmlBaseFileReader parser = new PepXmlBaseFileReader();
            try {
                parser.open(dataDirectory+File.separator+fileName);
            }
            catch (DataProviderException e) {
                appendToMsg("Error opening file: "+fileName+"\n"+e.getMessage());
                return false;
            }
            if (parser.isPeptideProphetRun()) {
            	interactPepxmlFiles.add(fileName);
            	found = true;
            }
            parser.close();
        }
        if(!found) {
            appendToMsg("Could not find PeptideProphet file(s) in directory: "+dataDirectory);
            return false;
        }
        
        // 3. If we know the search data file names that were uploaded match them with up with the 
        //    file names in the PeptideProphet *.pep.xml file(s) 
        for(String file: interactPepxmlFiles) {
            PepXmlBaseFileReader parser = new PepXmlBaseFileReader();
            try {
                parser.open(dataDirectory+File.separator+file);
            }
            catch (DataProviderException e) {
                appendToMsg("Error opening file: "+file+"\n"+e.getMessage());
                return false;
            }
            List<String> inputFileNames = new ArrayList<String>();
            try {
                while(parser.hasNextRunSearch()) {
                    inputFileNames.add(parser.getRunSearchName());
                }
            }
            catch (DataProviderException e) {
                appendToMsg("Error opening file: "+file+"\n"+e.getMessage());
                return false;
            }
            parser.close();
        
            if(searchDataFileNames != null) {
	            for(String input:inputFileNames) {
	            	if(!searchDataFileNames.contains(input)) {
	                    appendToMsg("No corresponding search data file found for: "+input);
	                    return false;
	                }
	            }
            }
        }
        
        preUploadCheckDone = true;
        
        return true;
    }

    
    private void appendToMsg(String msg) {
        this.preUploadCheckMsg.append(msg+"\n");
    }

    @Override
    public void upload() throws UploadException {
        
        reset();// reset all caches etc.
        
        if(!preUploadCheckDone) {
            if(!preUploadCheckPassed()) {
                UploadException ex = new UploadException(ERROR_CODE.PREUPLOAD_CHECK_FALIED);
                ex.appendErrorMessage(this.getPreUploadCheckMsg());
                ex.appendErrorMessage("\n\t!!!PEPTIDE_PROPHET ANALYSIS WILL NOT BE UPLOADED\n");
                throw ex;
            }
        }
        
        // get the modifications used for this search. Will be used for parsing the peptide sequence
        getSearchModifications(searchId);
        
        // now upload the analysis (PeptideProphet) data in the interact*.pep.xml file(s)
        for(String file: interactPepxmlFiles) {
            
            resetCaches(); // clear all old cached results.
            numAnalysisUploaded = 0;
            
            // determine if a file with this name has already been uploaded for this experiment
            MsSearchAnalysis ppAnalysis = analysisDao.loadAnalysisForFileName(file, searchId);
            if(ppAnalysis != null) {
                log.info("Analysis file: "+file+" has already been uploaded. AnalysisID: "+ppAnalysis.getId());
                this.analysisIds.add(ppAnalysis.getId());
                continue;
            }
            
            // file has not been uploaded upload it now
            long s = System.currentTimeMillis();
            log.info("Uploading analysis results in interact pepxml file: "+file);
            PepXmlBaseFileReader parser = new PepXmlBaseFileReader();
            try {
                parser.open(dataDirectory+File.separator+file);
            }
            catch (DataProviderException e) {
                UploadException ex = new UploadException(ERROR_CODE.PEPXML_ERROR, e);
                throw ex;
            }

            if(!parser.isRefreshParserRun()) {
                UploadException ex = new UploadException(ERROR_CODE.PEPXML_ERROR);
                ex.setErrorMessage("Refresh parser has not been run");
                throw ex;
            }

            // create a new entry for PeptideProphet analysis.
            int analysisId = 0;
            try {
                analysisId = createPeptideProphetAnalysis(parser, file);
            }
            catch(UploadException ex) {
                ex.appendErrorMessage("\n\tANALYSIS WILL NOT BE UPLOADED..."+file+"\n");
                throw ex;
            }

            try {
                while(parser.hasNextRunSearch()) {
                    String filename = parser.getRunSearchName();

                    Integer runSearchId = getRunSearchIdForFile(filename);
                    try {
                        uploadRunSearchAnalysis(filename, searchId, analysisId, runSearchId, parser);
                    }
                    catch (UploadException ex) {
                        ex.appendErrorMessage("\n\tDELETING ANALYSIS..."+analysisId+"\n");
                        deleteAnalysis(analysisId);
                        throw ex;
                    }
                    numAnalysisUploaded++;

                    resetCaches();
                }
            }
            catch (DataProviderException e) {
                UploadException ex = new UploadException(ERROR_CODE.PEPXML_ERROR, e);
                ex.appendErrorMessage("\n\tDELETING ANALYSIS..."+analysisId+"\n");
                deleteAnalysis(analysisId);
                throw ex;
            }
            finally {
                parser.close();
            }

            long e = System.currentTimeMillis();
            log.info("Finished uploading analysis results in interact pepxml file: "+file+"; Time: "+TimeUtils.timeElapsedSeconds(s, e));
            
            // if no PeptideProphet analyses were uploaded delete the top level search analysis
            if (numAnalysisUploaded == 0) {
                UploadException ex = new UploadException(ERROR_CODE.NO_PEPTPROPH_ANALYSIS_UPLOADED);
                ex.appendErrorMessage("\n\tDELETING PEPTIDE PROPHET ANALYSIS...ID: "+analysisId+"\n");
                deleteAnalysis(analysisId);
                numAnalysisUploaded = 0;
                throw ex;
            }
            
            uploadMsg.append("\n\t#Analyses in "+file+" : "+numAnalysisUploaded);
            this.analysisIds.add(analysisId);
            
            // Finally, save the filtered results stats
        	try {
        		ProphetFilteredPsmStatsSaver psmStatsSaver = ProphetFilteredPsmStatsSaver.getInstance();
        		// apply error rate 0.01
        		psmStatsSaver.save(analysisId, 0.01);
        		ProphetFilteredSpectraStatsSaver spectraStatsSaver = ProphetFilteredSpectraStatsSaver.getInstance();
        		// apply error rate 0.01
        		spectraStatsSaver.save(analysisId,0.01);
        	}
        	catch(Exception ex) {
        		log.error("Error saving filtered stats for analysisID: "+analysisId, ex);
        	}
        }
    }
    
    private void getSearchModifications(int searchId) {
        MsSearch search = searchDao.loadSearch(searchId);
        this.dynaResidueMods = search.getDynamicResidueMods();
        this.dynaTermMods = search.getDynamicTerminalMods();
     }
    
    // ---------------------------------------------------------------------------------------------
    // SAVE THE PEPTIDE PROPHET ANALYSIS INFORMATION
    // ---------------------------------------------------------------------------------------------
    private int createPeptideProphetAnalysis(PepXmlBaseFileReader parser, String pepxmlFile) throws UploadException {
            
        SearchAnalysisBean analysis = new SearchAnalysisBean();
//      analysis.setSearchId(searchId);
        analysis.setAnalysisProgram(Program.PEPTIDE_PROPHET);
        analysis.setAnalysisProgramVersion(parser.getPeptideProphetVersion());
        analysis.setFilename(pepxmlFile);
        analysis.setComments(comments);
        int analysisId;
        try {
            analysisId = analysisDao.save(analysis);
        }
        catch(RuntimeException e) {
            UploadException ex = new UploadException(ERROR_CODE.RUNTIME_ERROR, e);
            ex.setErrorMessage(e.getMessage());
            throw ex;
        }
        PeptideProphetROC roc = parser.getPeptideProphetRoc();
        roc.setSearchAnalysisId(analysisId);
        rocDao.saveRoc(roc);
        
        return analysisId;
    }

    private int getScanId(int runId, int scanNumber)
            throws UploadException {

        int scanId = scanDao.loadScanIdForScanNumRun(scanNumber, runId);
        if (scanId == 0) {
            UploadException ex = new UploadException(ERROR_CODE.NO_SCANID_FOR_SCAN);
            ex.setErrorMessage("No scanId found for scan number: "+scanNumber+" and runId: "+runId);
            throw ex;
        }
        return scanId;
    }
    
    
    private void uploadRunSearchAnalysis(String filename, int searchId, int analysisId, int runSearchId,
            PepXmlBaseFileReader parser) throws UploadException {
        
        int runSearchAnalysisId = uploadRunSearchAnalysis(analysisId, runSearchId);
        int runId = getRunIdForRunSearch(runSearchId);
        
        // upload the search results for each scan + charge combination
        int numResults = 0;
        try {
            while(parser.hasNextSearchScan()) {
                PepXmlBaseSearchScanIn scan = parser.getNextSearchScan();
                
                int scanId = getScanId(runId, scan.getScanNumber());
                
                for(BasePeptideProphetResultIn result: scan.getScanResults()) {
                    int resultId = getUploadedResultId(result, runSearchId, scanId);
                    updatePeptideProteinMatches(resultId, result);
                    uploadAnalysisResult(result, resultId, runSearchAnalysisId);      // PeptideProphet scores
                    numResults++;
                }
            }
        }
        catch (DataProviderException e) {
            UploadException ex = new UploadException(ERROR_CODE.PEPXML_ERROR, e);
            throw ex;
        }
        
        flush(); // save any cached data
        log.info("Uploaded analysis results for file: "+filename+", with "+numResults+
                " results. (runSearchId: "+runSearchId+")");
        
    }
    
    private void checkPeptideProteinMatches(int matchingSearchResultId, BasePeptideProphetResultIn result) throws UploadException {
        
        // Peptide to protein matches should be the same for all PSMs with the same peptide sequence
        // If we have already checked the results for a PSM with this peptide sequence no need 
        // to check again
        if(checkedPeptideProteinMatches.contains(result.getSearchResult().getResultPeptide().getPeptideSequence())) {
            return;
        }
        
        //log.info("matching proteins");
        
        // load the stored result
        MsSearchResult storedResult = resDao.load(matchingSearchResultId);
        List<MsSearchResultProtein> storedMatches = storedResult.getProteinMatchList();
        Set<String> storedAccessions = new HashSet<String>(storedMatches.size()*2);
        for(MsSearchResultProtein prot: storedMatches) {
            storedAccessions.add(prot.getAccession());
        }
        
        List<MsSearchResultProteinIn> pepXmlMatches = result.getSearchResult().getProteinMatchList();
        // Get the unique accessions from the interact pepxml file
        // If the accessions are longer than 500 chars truncate them. 
        // YRC_NRSEQ supports only 500 length accessions
        Set<String> pepXmlAccessions = new HashSet<String>(storedMatches.size()*2);
        for(MsSearchResultProteinIn match: pepXmlMatches) {
        	String accession = match.getAccession();
        	if(accession.length() > 500)
        		accession = accession.substring(0, 500);
        	pepXmlAccessions.add(accession);
        }
        
        if(storedMatches.size() != pepXmlAccessions.size()) {
        	
//        	if(storedMatches.size() < pepXmlAccessions.size()) {
//        		
//        		String notFound = "";
//        		for(String pepxmlAcc: pepXmlAccessions) {
//        			if(!storedAccessions.contains(pepxmlAcc)) {
//                    	notFound += ","+pepxmlAcc;
//                    }
//        		}
//        		if(notFound.length() > 0)
//        			notFound = notFound.substring(1);
//        		
//        		UploadException ex = new UploadException(ERROR_CODE.GENERAL);
//        		ex.setErrorMessage("Number of protein matches stored: "+storedMatches.size()+
//        				" LESS THAN the number of matches found in interact files: "+pepXmlMatches.size()+
//        				" for searchResultID: "+matchingSearchResultId+"\nMissing: "+notFound);
//        		throw ex;
//        	}
//        	else { // otherwise simply log a warning.  TPP converted Mascot pepXML files can have
//        		   // incorrect number of protein matchces.
//        		log.warn("Number of protein matches stored: "+storedMatches.size()+
//        				" MORE THAN the number of matches found in interact files: "+pepXmlMatches.size()+
//        				" for searchResultID: "+matchingSearchResultId);
//        	}
            
//            log.error("Number of protein matches stored: "+storedMatches.size()+
//            		" does not match the number of matches found in interact files: "+pepXmlMatches.size()+
//            		" for searchResultID: "+matchingSearchResultId);
//            if(pepXmlMatches.size() > storedMatches.size()) {
//            	log.error("MORE PROTEINS IN interact pepxml");
//            }
            
            //UploadException ex = new UploadException(ERROR_CODE.GENERAL);
    		//ex.setErrorMessage("Number of protein matches stored: "+storedMatches.size()+
    		//		" NOT EQUAL to the number of matches found in interact files: "+pepXmlMatches.size()+
    		//		" for searchResultID: "+matchingSearchResultId+"; peptide: "+storedResult.getResultPeptide().getPeptideSequence());
    		log.warn("Number of protein matches stored: "+storedMatches.size()+
    				" NOT EQUAL to the number of matches found in interact files: "+pepXmlMatches.size()+
    				" for searchResultID: "+matchingSearchResultId+"; peptide: "+storedResult.getResultPeptide().getPeptideSequence());
    		//throw ex;
            
        }
        
        
        Set<String> shortStoredAccessionIPI = null;
        
        // Make sure we have stored all proteins that are  associated with the result in the interact pepxml file
        for(String pepxmlAcc: pepXmlAccessions) {
        	boolean matchFound = true;
            if(!storedAccessions.contains(pepxmlAcc)) {
            	matchFound = false;
            }
            
            if(!matchFound) {
            	// We may have a shorter version of the accession (255 chars before the table was modified)
            	if(pepxmlAcc.length() > 255) {
            		if(storedAccessions.contains(pepxmlAcc.substring(0,255))) {
            			matchFound = true;
            		}
            	}
            }
            if(!matchFound) {
            	
            	// If these are IPI accessions, refresh parser may truncate the accessions
            	if(pepxmlAcc.startsWith("IPI:IPI")) {
            		
            		if(shortStoredAccessionIPI == null) {
            			shortStoredAccessionIPI = new HashSet<String>(storedMatches.size() * 2);
            			for(String acc: storedAccessions) {
            				int idx = acc.indexOf("|");
            				if(idx == -1) shortStoredAccessionIPI.add(acc);
            				else		  shortStoredAccessionIPI.add(acc.substring(0, idx));
            			}
            		}
            		
            		if(shortStoredAccessionIPI.contains(pepxmlAcc))
            			matchFound = true;
            	}
            }
            // no match found
            if(!matchFound) {
            	
              UploadException ex = new UploadException(ERROR_CODE.GENERAL);
              ex.setErrorMessage("Protein in interact file not found in database: "+pepxmlAcc+
                      "; searchResultID: "+matchingSearchResultId+
                      "; peptide: "+result.getSearchResult().getResultPeptide().getPeptideSequence());
              throw ex;
//              log.error("Protein in interact file not found in database: "+pepxmlAcc+
//                    "; searchResultID: "+matchingSearchResultId+
//                    "; peptide: "+result.getSearchResult().getResultPeptide().getPeptideSequence());
            }
        }
    }
    
    private void updatePeptideProteinMatches(int matchingSearchResultId, BasePeptideProphetResultIn result) throws UploadException {
        
//        log.info("Updating matching proteins");
        
        // load the stored result
        MsSearchResult storedResult = resDao.load(matchingSearchResultId);
        List<MsSearchResultProtein> storedMatches = storedResult.getProteinMatchList();
        Set<String> storedAccessions = new HashSet<String>(storedMatches.size()*2);
        for(MsSearchResultProtein prot: storedMatches) {
            storedAccessions.add(prot.getAccession());
        }
        
        List<MsSearchResultProteinIn> pepXmlMatches = result.getSearchResult().getProteinMatchList();
        // Get the unique accessions from the interact pepxml file
        // If the accessions are longer than 500 chars truncate them. 
        // YRC_NRSEQ supports only 500 length accessions
        Set<String> newAccessions = new HashSet<String>();
        for(MsSearchResultProteinIn match: pepXmlMatches) {
        	String accession = match.getAccession();
        	if(accession.length() > 500)
        		accession = accession.substring(0, 500);
        	
        	if(!storedAccessions.contains(accession))
        	{
        		newAccessions.add(accession);
        	}
        }
        
        uploadProteinMatches(newAccessions, matchingSearchResultId);
    }


    private void uploadProteinMatches(Set<String>accessions, final int resultId)
    throws UploadException {
        // upload the protein matches if the cache has enough entries
        if (proteinMatchList.size() >= BUF_SIZE) {
            uploadProteinMatchBuffer();
        }
        // add the protein matches for this result to the cache
        for (String accession: accessions) {
            log.debug("Adding match: resultID: "+resultId+"; Accession : "+accession);
            proteinMatchList.add(new SearchResultProteinBean(resultId, accession));
        }
    }
    
    private void uploadProteinMatchBuffer() {

        List<MsSearchResultProtein> list = new ArrayList<MsSearchResultProtein>(proteinMatchList.size());
        list.addAll(proteinMatchList);
        proteinMatchDao.saveAll(list);
        proteinMatchList.clear();
    }
    
    private int getRunIdForRunSearch(int runSearchId) {
        MsRunSearch rs = runSearchDao.loadRunSearch(runSearchId);
        if(rs != null)
            return rs.getRunId();
        else
            return 0;
    }
    
    private int getUploadedResultId(BasePeptideProphetResultIn result, int runSearchId, int scanId) throws UploadException {
        
        MsSearchResult searchResult = null;
        try {
            List<MsSearchResult> matchingResults = resDao.loadResultForSearchScanChargePeptide(runSearchId, scanId, 
                        result.getSearchResult().getCharge(), 
                        result.getSearchResult().getResultPeptide().getPeptideSequence());
            
            if(matchingResults.size() == 1) 
                searchResult = matchingResults.get(0);
            
            else if(matchingResults.size() > 1) { // this can happen if we have the same sequence with different mods
                
                String myPeptide = result.getSearchResult().getResultPeptide().getModifiedPeptide();
                for(MsSearchResult res: matchingResults) {
                    if(myPeptide.equals(res.getResultPeptide().getModifiedPeptide())) {
                        if(searchResult != null) {
                            UploadException ex = new UploadException(ERROR_CODE.MULTI_MATCHING_SEARCH_RESULT);
                            ex.setErrorMessage("Multiple matching search results were found for runSearchId: "+runSearchId+
                                    " scanId: "+scanId+"; charge: "+result.getSearchResult().getCharge()+
                                    "; peptide: "+result.getSearchResult().getResultPeptide().getPeptideSequence()+
                                    "; modified peptide: "+result.getSearchResult().getResultPeptide().getModifiedPeptidePS());
                            throw ex;
                        }
                        searchResult = res;
                    }
                }
            }
        }
        catch(RuntimeException e) {
            UploadException ex = new UploadException(ERROR_CODE.RUNTIME_ERROR, e);
            ex.setErrorMessage(e.getMessage());
            throw ex;
        }
        catch (ModifiedSequenceBuilderException e) {
            UploadException ex = new UploadException(ERROR_CODE.GENERAL, e);
            ex.setErrorMessage(e.getMessage());
            throw ex;
        }
        if(searchResult == null) {
            UploadException ex = new UploadException(ERROR_CODE.NO_MATCHING_SEARCH_RESULT);
            ex.setErrorMessage("No matching search result was found for runSearchId: "+runSearchId+
                    " scanId: "+scanId+"; charge: "+result.getSearchResult().getCharge()+
                    "; peptide: "+result.getSearchResult().getResultPeptide().getPeptideSequence()+
                    "; modified peptide: "+result.getSearchResult().getResultPeptide().getModifiedPeptidePS());
            throw ex;
        }
        return searchResult.getId();
    }

    
    private void flush() {
        
        if(prophetResultDataList.size() > 0) {
            uploadPeptideProphetResultBuffer();
        }
        
        if (proteinMatchList.size() > 0) {
            uploadProteinMatchBuffer();
        }
    }
    // -------------------------------------------------------------------------------------------
    // UPLOAD PEPTIDE PROPHET ANALYSIS RESULT
    // -------------------------------------------------------------------------------------------
    private boolean uploadAnalysisResult(BasePeptideProphetResultIn result, int searchResultId, int rsAnalysisId) 
        throws UploadException {
        
        
        // upload the PeptideProphet specific result information if the cache has enough entries
        if (prophetResultDataList.size() >= BUF_SIZE) {
            uploadPeptideProphetResultBuffer();
        }
        
        // add the PeptideProphet specific information for this result to the cache
        PeptideProphetResultDataBean res = new PeptideProphetResultDataBean();
        res.setRunSearchAnalysisId(rsAnalysisId);
        res.setSearchResultId(searchResultId);
        res.setProbability(result.getProbability());
        res.setfVal(result.getfVal());
        res.setNumEnzymaticTermini(result.getNumEnzymaticTermini());
        res.setNumMissedCleavages(result.getNumMissedCleavages());
        res.setMassDifference(result.getMassDifference());
        res.setProbabilityNet_0(result.getProbabilityNet_0());
        res.setProbabilityNet_1(result.getProbabilityNet_1());
        res.setProbabilityNet_2(result.getProbabilityNet_2());
       
        prophetResultDataList.add(res);
        return true;
    }

    private void uploadPeptideProphetResultBuffer() {
        ppResDao.saveAllPeptideProphetResultData(prophetResultDataList);
        prophetResultDataList.clear();
    }
    
    // -------------------------------------------------------------------------------
    // UPLOAD DATA INTO THE msRunSearchAnalysis TABLE
    // -------------------------------------------------------------------------------
    private int uploadRunSearchAnalysis(int analysisId, int runSearchId) 
        throws UploadException {
        
        // TODO save any PeptideProphet params?
        // save the run search analysis and return the database id
        RunSearchAnalysisBean rsa = new RunSearchAnalysisBean();
        rsa.setAnalysisFileFormat(SearchFileFormat.PEPXML);
        rsa.setAnalysisId(analysisId);
        rsa.setRunSearchId(runSearchId);
        return runSearchAnalysisDao.save(rsa);
    }
    
    private int getRunSearchIdForFile(String file) throws UploadException 
    {
		int runSearchId = runSearchDao.loadIdForSearchAndFileName(searchId, file);
		if(runSearchId == 0) {
			UploadException ex = new UploadException(ERROR_CODE.NO_RUNSEARCHID_FOR_ANALYSIS_FILE);
			ex.appendErrorMessage("File: "+file);
			ex.appendErrorMessage("; SearchID: "+searchId);
			throw ex;
		}
		return runSearchId;
	}


    void reset() {

        numAnalysisUploaded = 0;

        resetCaches();

//        searchId = 0;
        analysisIds.clear();
        
        preUploadCheckMsg = new StringBuilder();
        uploadMsg = new StringBuilder();
        
        dynaResidueMods.clear();
        dynaTermMods.clear();
    }

    // called before uploading each msms_run_search in the interact.pep.xml file and in the reset() method.
    private void resetCaches() {
        
        prophetResultDataList.clear();
        proteinMatchList.clear();
    }
    
    @Override
    public SearchFileFormat getAnalysisFileFormat() {
        return SearchFileFormat.PEPXML;
    }

    @Override
    public void setSearchDataFileNames(List<String> searchDataFileNames) {
        this.searchDataFileNames = searchDataFileNames;
    }

    @Override
    public void setSearchId(int searchId) {
        this.searchId = searchId;
    }
    
    public void setComments(String comments) {
    	this.comments = comments;
    }

    @Override
    public void setSearchProgram(Program searchProgram) {
//        throw new UnsupportedOperationException();
        // TODO 
    }
    
    public void deleteAnalysis(int analysisId) {
        if (analysisId == 0)
            return;
        log.info("Deleting analysis ID: "+analysisId);
        analysisDao.delete(analysisId);
    }


    @Override
    public List<Integer> getUploadedAnalysisIds() {
        return this.analysisIds;
    }
}
