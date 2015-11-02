package org.yeastrc.ms.service.pepxml;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.yeastrc.ms.dao.DAOFactory;
import org.yeastrc.ms.dao.run.MsRunDAO;
import org.yeastrc.ms.dao.run.MsScanDAO;
import org.yeastrc.ms.dao.search.MsRunSearchDAO;
import org.yeastrc.ms.dao.search.MsSearchDAO;
import org.yeastrc.ms.dao.search.MsSearchDatabaseDAO;
import org.yeastrc.ms.dao.search.MsSearchModificationDAO;
import org.yeastrc.ms.dao.search.MsSearchResultDAO;
import org.yeastrc.ms.dao.search.MsSearchResultProteinDAO;
import org.yeastrc.ms.domain.analysis.peptideProphet.GenericPeptideProphetResultIn;
import org.yeastrc.ms.domain.search.MsResidueModification;
import org.yeastrc.ms.domain.search.MsResidueModificationIn;
import org.yeastrc.ms.domain.search.MsResultResidueMod;
import org.yeastrc.ms.domain.search.MsResultResidueModIds;
import org.yeastrc.ms.domain.search.MsResultTerminalModIds;
import org.yeastrc.ms.domain.search.MsRunSearchIn;
import org.yeastrc.ms.domain.search.MsSearch;
import org.yeastrc.ms.domain.search.MsSearchDatabaseIn;
import org.yeastrc.ms.domain.search.MsSearchIn;
import org.yeastrc.ms.domain.search.MsSearchResult;
import org.yeastrc.ms.domain.search.MsSearchResultIn;
import org.yeastrc.ms.domain.search.MsSearchResultProtein;
import org.yeastrc.ms.domain.search.MsSearchResultProteinIn;
import org.yeastrc.ms.domain.search.MsTerminalModificationIn;
import org.yeastrc.ms.domain.search.SearchFileFormat;
import org.yeastrc.ms.domain.search.impl.ResultResidueModIds;
import org.yeastrc.ms.domain.search.impl.ResultTerminalModIds;
import org.yeastrc.ms.domain.search.impl.RunSearchBean;
import org.yeastrc.ms.domain.search.impl.SearchResultProteinBean;
import org.yeastrc.ms.domain.search.pepxml.PepXmlSearchScanIn;
import org.yeastrc.ms.parser.DataProviderException;
import org.yeastrc.ms.parser.pepxml.PepXmlBaseFileReader;
import org.yeastrc.ms.parser.pepxml.PepXmlGenericFileReader;
import org.yeastrc.ms.service.DynamicModLookupUtil;
import org.yeastrc.ms.service.SearchDataUploadService;
import org.yeastrc.ms.service.UploadException;
import org.yeastrc.ms.service.UploadException.ERROR_CODE;
import org.yeastrc.ms.util.TimeUtils;
import org.yeastrc.nrseq.dao.NrSeqLookupUtil;

public abstract class PepXmlDataUploadService <T extends PepXmlSearchScanIn<G, R>,
                                               G extends GenericPeptideProphetResultIn<R>, 
                                               R extends MsSearchResultIn,
                                               S extends MsSearchIn>

    implements SearchDataUploadService {

    protected static final int BUF_SIZE = 500;
    private int experimentId;
    private int searchId;

    protected String dataDirectory;
    private Date searchDate;
    private String remoteServer;
    private String remoteDirectory;

    protected StringBuilder preUploadCheckMsg;
    protected boolean preUploadCheckDone;

    protected List<String> inputXmlFileNames; // names WITH extensions
    private Set<String> runFileNames; // run names WITHOUT extension
    private List<String> spectrumFileNames;
    

    private final MsRunDAO runDao;
    private final MsScanDAO scanDao;
    private final MsSearchDatabaseDAO sequenceDbDao;
    private final MsRunSearchDAO runSearchDao;
    private final MsSearchResultProteinDAO proteinMatchDao;
    private final MsSearchModificationDAO modDao;
    private final MsSearchResultDAO resultDao;
    private final MsSearchDAO searchDao;

    private List<MsSearchResultProtein> proteinMatchList;
    private List<MsResultResidueModIds> resultResidueModList;
    private List<MsResultTerminalModIds> resultTerminalModList;
    private MsSearchDatabaseIn db = null;
    private List<MsResidueModificationIn> dynaResidueMods;
    private List<MsTerminalModificationIn> dynaTermMods;

    private int sequenceDatabaseId;
    private DynamicModLookupUtil dynaModLookup;
    private int numSearchesUploaded = 0;

    private static final Logger log = Logger.getLogger(PepXmlDataUploadService.class.getName());

    public PepXmlDataUploadService() {

        super();
        this.inputXmlFileNames = new ArrayList<String>();
        this.runFileNames = new HashSet<String>();

        this.proteinMatchList = new ArrayList<MsSearchResultProtein>(BUF_SIZE);
        this.resultResidueModList = new ArrayList<MsResultResidueModIds>(BUF_SIZE);
        this.resultTerminalModList = new ArrayList<MsResultTerminalModIds>(BUF_SIZE);

        this.dynaResidueMods = new ArrayList<MsResidueModificationIn>();
        this.dynaTermMods = new ArrayList<MsTerminalModificationIn>();

        //proteinMatches = new HashMap<String, List<PeptideProteinMatch>>();

        DAOFactory daoFactory = DAOFactory.instance();

        this.runDao = daoFactory.getMsRunDAO(); 
        this.scanDao = daoFactory.getMsScanDAO();
        this.sequenceDbDao = daoFactory.getMsSequenceDatabaseDAO();
        this.searchDao = daoFactory.getMsSearchDAO();
        this.runSearchDao = daoFactory.getMsRunSearchDAO();
        this.proteinMatchDao = daoFactory.getMsProteinMatchDAO();
        this.modDao = daoFactory.getMsSearchModDAO();
        this.resultDao = daoFactory.getMsSearchResultDAO();
    }

    @Override
    public void setExperimentId(int experimentId) {
        this.experimentId = experimentId;
    }

    @Override
    public void setSearchDate(java.util.Date date) {
        this.searchDate = date;
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
    public void setSpectrumFileNames(List<String> fileNames) {
        this.spectrumFileNames = fileNames;
    }

    @Override
    public String getPreUploadCheckMsg() {
        return preUploadCheckMsg.toString();
    }

    @Override
    public String getUploadSummary() {
        return "\tSearch file format: "+getSearchFileFormat()+
        "\n\t#Input PepXml(interact) files in Directory: "+inputXmlFileNames.size()
        + "; #Run searches " + runFileNames.size() + "; #Uploaded: "+numSearchesUploaded;
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

        // 2. Look for *.xml or *.pep.xml file that may contain search results
        File[] files = dir.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                String name_uc = name.toLowerCase();
                return (name_uc.endsWith(".pep.xml") || name_uc.endsWith(".xml"));
            }});
        // Keep only files where PeptideProphet has been run.
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
            
            if(!parser.isTPPFile()) {
            	parser.close();
            	continue;
            }
            else if (!parser.isPeptideProphetRun()) {
            	parser.close();
            	continue;
            }
            // This is a TPP-generated file and contains PeptideProphet results
            inputXmlFileNames.add(files[i].getName());
            
            try {
                while(parser.hasNextRunSearch()) {
                	// Add all the run file names to our list. Each interact pepxml file may contain
                	// results for more than one run.
                    runFileNames.add(parser.getRunSearchName());
                }
            }
            catch (DataProviderException e) {
            	appendToMsg("Error opening file: "+fileName+"\n"+e.getMessage());
                return false;
            }
            
            parser.close();
        }
        
        
        // 3. If we know the raw data file names that will be uploaded match them with up with the 
        //    *.pep.xml file and make sure there is a spectrum data file for each one.
        if(spectrumFileNames != null) {
            for(String file:runFileNames) {
                if(!spectrumFileNames.contains(file)) {
                    appendToMsg("No corresponding spectrum data file found for: "+file);
                    return false;
                }
            }
        }

        preUploadCheckDone = true;

        return true;
    }

    protected void appendToMsg(String msg) {
        this.preUploadCheckMsg.append(msg+"\n");
    }

    // ----------------------------------------------------------------------------------------------------
    // To be implemented by subclasses
    // ----------------------------------------------------------------------------------------------------
    
    protected abstract PepXmlGenericFileReader<T, G, R, S> getPepXmlReader();
    
    protected abstract S getSearchAndParams(String dataDirectory, String remoteServer,
            String remoteDirectory, Date searchDate) throws UploadException;
    
    protected abstract int saveSearch(S search, int experimentId, int sequenceDatabaseId);
    
    protected abstract void uploadProgramSpecificResultData(R result, int resultId);
    
    protected abstract int getNumEnzymaticTermini(int searchId);
    
    protected abstract boolean getClipNtermMethionine(int searchId);
    
    protected abstract SearchFileFormat getSearchFileFormat();
    
    // ----------------------------------------------------------------------------------------------------


    @Override
    public void upload() throws UploadException {


        reset();// reset all caches etc.

        if(!preUploadCheckDone) {
            if(!preUploadCheckPassed()) {
                UploadException ex = new UploadException(ERROR_CODE.PREUPLOAD_CHECK_FALIED);
                ex.appendErrorMessage(this.getPreUploadCheckMsg());
                ex.appendErrorMessage("\n\t!!!SEARCH WILL NOT BE UPLOADED\n");
                throw ex;
            }
        }

        searchId = 0;
        // parse and upload the search parameters
        try {
            S search = getSearchAndParams(dataDirectory, remoteServer, remoteDirectory, searchDate);
            searchId = this.uploadSearch(experimentId, search);
            
        }
        catch (UploadException e) {
            e.appendErrorMessage("\n\t!!!SEARCH WILL NOT BE UPLOADED\n");
            throw e;
        }

        // initialize the Modification lookup map; will be used when uploading modifications for search results
        dynaModLookup = new DynamicModLookupUtil(searchId);
        
        // now upload the search data in the *.pep.xml or *.xml files
        for (String file: inputXmlFileNames) {
            
            String filePath = dataDirectory+File.separator+file;
            log.info("Reading file: "+filePath);
            
            resetCaches();

            long s = System.currentTimeMillis();
            log.info("Uploading search results in file: "+file);
            PepXmlGenericFileReader<T,G,R,S> parser = getPepXmlReader();
            try {
                parser.open(filePath);
            }
            catch (DataProviderException e) {
                UploadException ex = new UploadException(ERROR_CODE.PEPXML_ERROR, e);
                ex.appendErrorMessage("\n\tDELETING SEARCH...\n");
                deleteSearch(searchId);
                numSearchesUploaded = 0;
                throw ex;
            }

            try {
                while(parser.hasNextRunSearch()) {

                	String runName = parser.getRunSearchName();
                	int runId = getRunId(runName);
                			
                    // match the search parameters found in the file against the ones we saved
                    // for this search
                    matchSearchParams(searchId, parser.getSearch(), parser.getRunSearchName());

                    try {
                        uploadRunSearch(filePath, searchId, runId, parser);
                        numSearchesUploaded++;
                    }
                    catch (UploadException ex) {
                        ex.appendErrorMessage("\n\tDELETING SEARCH ..."+searchId+"\n");
                        deleteSearch(searchId);
                        numSearchesUploaded = 0;

                        throw ex;
                    }
                }
            }
            catch (DataProviderException e) {
                UploadException ex = new UploadException(ERROR_CODE.PEPXML_ERROR, e);
                ex.appendErrorMessage("\n\tDELETING SEARCH...\n");
                deleteSearch(searchId);
                numSearchesUploaded = 0;
                throw ex;
            }

            parser.close();

            long e = System.currentTimeMillis();
            log.info("Finished uploading search results in file: "+file+"; Time: "+TimeUtils.timeElapsedSeconds(s, e));
        }


        // if no searches were uploaded delete the top level search
        if (numSearchesUploaded == 0) {
            UploadException ex = new UploadException(ERROR_CODE.NO_RUN_SEARCHES_UPLOADED);
            ex.appendErrorMessage("\n\tDELETING SEARCH...\n");
            deleteSearch(searchId);
            numSearchesUploaded = 0;
            throw ex;
        }

    }

    protected void matchSearchParams(int searchId, MsSearchIn parsedSearch, String fileName)
    throws UploadException {

        // load the search and its parameters, enzyme information, database information
        // and modification information
        MsSearchDAO searchDao = DAOFactory.instance().getMsSearchDAO();
        MsSearch search = searchDao.loadSearch(searchId);

        SearchParamMatcher matcher = new SearchParamMatcher();
        boolean matches = matcher.matchSearchParams(search, parsedSearch, fileName);

        if(!matches) {
            log.error(matcher.getErrorMessage());
            UploadException ex = new UploadException(ERROR_CODE.GENERAL);
            ex.setErrorMessage(matcher.getErrorMessage());
            throw ex;
        }
        // TODO do we need to match some other key parameters e.g. min_enzymatic_termini etc. 
    }

    private int getScanId(int runId, int scanNumber) throws UploadException {

        int scanId = scanDao.loadScanIdForScanNumRun(scanNumber, runId);
        if (scanId == 0) {
            UploadException ex = new UploadException(ERROR_CODE.NO_SCANID_FOR_SCAN);
            ex.setErrorMessage("No scanId found for scan number: "+scanNumber+" and runId: "+runId);
            throw ex;
        }
        return scanId;
    }

    private void uploadRunSearch(String filename, int searchId, int runId, PepXmlGenericFileReader<T,G,R,S> parser) 
        throws UploadException {

    	log.info("Loading search results for file: "+filename);
    	
    	String runName = parser.getRunSearchName();
    	
    	int runSearchId = getRunSearchId(runId);
    	if(runSearchId > 0)
    	{
    		// If we have already uploaded the search results for this run don't upload them again.
    		// This could happen, for example, if there is one interact*.pep.xml file per run, and 
    		// also a combined interact.pep.xml
    		log.info("Search results for file " + parser.getRunSearchName() + " have been uploaded. Adding to existing results, if required...");
    	}
    	else
    	{
    		runSearchId = uploadRunSearchHeader(searchId, runId, parser);
    		log.info("Created entry in msRunSearch table: "+runSearchId);
    	}
        
        
        // upload the search results for each scan + charge combination
        int numResults = 0;
        try {
            while(parser.hasNextSearchScan()) {
                T scan = parser.getNextSearchScan();

                int scanId = getScanId(runId, scan.getScanNumber());

                for(G result: scan.getScanResults()) {
                	
                	int charge = result.getSearchResult().getCharge();
                	String sequence = result.getSearchResult().getResultPeptide().getPeptideSequence();
                	
                	List<MsSearchResult> results = resultDao.loadResultForSearchScanChargePeptide(runSearchId, scanId, charge, sequence);
                	if(results.size() == 0)
                	{
	                    int resultId = uploadBaseSearchResult(result.getSearchResult(), runSearchId, scanId);
	                    uploadProgramSpecificResultData(result.getSearchResult(), resultId); // Program specific scores
	                    numResults++;
                	}
                }
            }
        }
        catch (DataProviderException e) {
            UploadException ex = new UploadException(ERROR_CODE.PEPXML_ERROR, e);
            throw ex;
        }

        flush(); // save any cached data
        log.info("Uploaded search results in file: "+filename+ " for run " + runName + ", with "+numResults+
                " results. (runSearchId: "+runSearchId+")");

    }

    protected void flush() {
        if (proteinMatchList.size() > 0) {
            uploadProteinMatchBuffer();
        }
        if (resultResidueModList.size() > 0) {
            uploadResultResidueModBuffer();
        }
        if (resultTerminalModList.size() > 0) {
            uploadResultTerminalModBuffer();
        }
    }

    private int uploadBaseSearchResult(MsSearchResultIn result, int runSearchId, int scanId)
            throws UploadException {

        int resultId = resultDao.saveResultOnly(result, runSearchId, scanId); // uploads data to the msRunSearchResult table ONLY

        // upload the protein matches
        uploadProteinMatches(result, resultId, sequenceDatabaseId);

        // upload dynamic mods for this result
        uploadResultResidueMods(result, resultId, runSearchId);

        // no dynamic terminal mods for sequest
        uploadResultTerminalMods(result, resultId, searchId);

        return resultId;
    }

//    private <T extends MsSearchResult> List<Integer> uploadBaseSearchResults(
//            List<T> results) throws UploadException {
//
//        List<Integer> autoIncrIds = resultDao.saveResultsOnly(results);
//        for(int i = 0; i < results.size(); i++) {
//            MsSearchResult result = results.get(i);
//            int resultId = autoIncrIds.get(i);
//
//            // upload the protein matches
//            uploadProteinMatches(result, resultId);
//
//            // upload dynamic mods for this result
//            uploadResultResidueMods(result, resultId, result.getRunSearchId());
//
//            // no dynamic terminal mods for sequest
//            uploadResultTerminalMods(result, resultId, searchId);
//        }
//
//        return autoIncrIds;
//    }

    private void uploadProteinMatches(MsSearchResultIn result, final int resultId, int databaseId)
    throws UploadException {
        // upload the protein matches if the cache has enough entries
        if (proteinMatchList.size() >= BUF_SIZE) {
            uploadProteinMatchBuffer();
        }
        // add the protein matches for this result to the cache
        Set<String> accSet = new HashSet<String>(result.getProteinMatchList().size());
        for (MsSearchResultProteinIn match: result.getProteinMatchList()) {
            // only UNIQUE accession strings for this result will be added.
            if (accSet.contains(match.getAccession()))
                continue;
            log.debug("Adding match: resultID: "+resultId+"; Accession : "+match.getAccession());
            accSet.add(match.getAccession());
            proteinMatchList.add(new SearchResultProteinBean(resultId, match.getAccession()));
        }
    }

    private void uploadProteinMatchBuffer() {

        List<MsSearchResultProtein> list = new ArrayList<MsSearchResultProtein>(proteinMatchList.size());
        list.addAll(proteinMatchList);
        proteinMatchDao.saveAll(list);
        proteinMatchList.clear();
    }

    private void uploadResultResidueMods(MsSearchResultIn result, int resultId,
            int searchId) throws UploadException {
        // upload the result dynamic residue modifications if the cache has enough entries
        if (resultResidueModList.size() >= BUF_SIZE) {
            uploadResultResidueModBuffer();
        }
        // add the dynamic residue modifications for this result to the cache
        for (MsResultResidueMod mod: result.getResultPeptide().getResultDynamicResidueModifications()) {
            if (mod == null)
                continue;
            MsResidueModification modMatch = dynaModLookup.getDynamicResidueModification(
                    mod.getModifiedResidue(),
                    mod.getModificationMass(), false);
            if (modMatch == null) {
                UploadException ex = new UploadException(ERROR_CODE.MOD_LOOKUP_FAILED);
                ex.setErrorMessage("No matching dynamic residue modification found for: searchId: "+
                        searchId+
                        "; peptide: "+result.getResultPeptide().getPeptideSequence()+
                        "; position: "+mod.getModifiedPosition()+
                        "; modResidue: "+mod.getModifiedResidue()+
                        "; modMass: "+mod.getModificationMass().doubleValue());
                throw ex;
            }
            ResultResidueModIds resultMod = new ResultResidueModIds(resultId, modMatch.getId(), mod.getModifiedPosition());
            resultResidueModList.add(resultMod);
        }
    }

    private void uploadResultResidueModBuffer() {
        modDao.saveAllDynamicResidueModsForResult(resultResidueModList);
        resultResidueModList.clear();
    }

    void uploadResultTerminalMods(MsSearchResultIn result, int resultId, int searchId)
    throws UploadException {
        // upload the result dynamic terminal modifications if the cache has enough entries
        if (resultTerminalModList.size() >= BUF_SIZE) {
            uploadResultTerminalModBuffer();
        }
        // add the dynamic terminal modifications for this result to the cache
        for (MsTerminalModificationIn mod: result.getResultPeptide().getResultDynamicTerminalModifications()) {
            if (mod == null)
                continue;
            int modId = dynaModLookup.getDynamicTerminalModificationId( 
                    mod.getModifiedTerminal(), mod.getModificationMass()); 
            if (modId == 0) {
                UploadException ex = new UploadException(ERROR_CODE.MOD_LOOKUP_FAILED);
                ex.setErrorMessage("No matching dynamic terminal modification found for: searchId: "+
                        searchId+
                        "; modTerminal: "+mod.getModifiedTerminal()+
                        "; modMass: "+mod.getModificationMass().doubleValue());
                throw ex;
            }
            ResultTerminalModIds resultMod = new ResultTerminalModIds(resultId, modId);
            resultTerminalModList.add(resultMod);
        }
    }

    private void uploadResultTerminalModBuffer() {
        modDao.saveAllDynamicTerminalModsForResult(resultTerminalModList);
        resultTerminalModList.clear();
    }

    private int uploadRunSearchHeader(int searchId, int runId, PepXmlGenericFileReader<T,G,R,S> parser)
    throws UploadException {

        MsRunSearchIn runSearch;
        try {
            runSearch = parser.getRunSearchHeader();
        }
        catch (DataProviderException e) {
            UploadException ex = new UploadException(ERROR_CODE.PEPXML_ERROR, e);
            ex.setErrorMessage(e.getMessage());
            throw ex;
        }
        if(runSearch instanceof RunSearchBean) {
            RunSearchBean rsb = (RunSearchBean) runSearch;
            rsb.setRunId(runId);
            rsb.setSearchId(searchId);
            rsb.setSearchDate(new java.sql.Date(searchDate.getTime()));
            return runSearchDao.saveRunSearch(rsb);
        }
        else {
            UploadException ex = new UploadException(ERROR_CODE.PEPXML_ERROR);
            ex.setErrorMessage("Invalid header type for run search");
            throw ex;
        }
    }


    private int getRunId(String filename) throws UploadException
    {
    	int runId = 0;
		try {runId = runDao.loadRunIdForExperimentAndFileName(experimentId, filename);}
		catch(Exception e) {
		    UploadException ex = new UploadException(ERROR_CODE.RUNTIME_ERROR);
		    throw ex;
		}
		if(runId == 0) {
		    UploadException ex = new UploadException(ERROR_CODE.NO_RUNID_FOR_SEARCH_FILE);
		    ex.appendErrorMessage("File: "+filename);
		    throw ex;
		}
		return runId;
    }
    
	private int getRunSearchId(int runId) throws UploadException {
		
		return runSearchDao.loadIdForRunAndSearch(runId, searchId);
	}

    private int uploadSearch(int experimentId, S search) throws UploadException {

        db = search.getSearchDatabases().get(0);
        dynaResidueMods = search.getDynamicResidueMods();
        dynaTermMods = search.getDynamicTerminalMods();

        // get the id of the search database used (will be used to look up protein ids later)
        sequenceDatabaseId = getSearchDatabaseId(db);

        // create a new entry in the MsSearch table and upload the search options, databases, enzymes etc.
        try {
            return saveSearch(search, experimentId, sequenceDatabaseId);
        }
        catch(RuntimeException e) {
            UploadException ex = new UploadException(ERROR_CODE.RUNTIME_ERROR, e);
            ex.setErrorMessage(e.getMessage());
            throw ex;
        }
    }


    private int getSearchDatabaseId(MsSearchDatabaseIn db) throws UploadException {
        String searchDbName = null;
        int dbId = 0;
        if (db != null) {

            // look in the msSequenceDatabaseDetail table first. We might already have this 
            // database in there
            dbId = sequenceDbDao.getSequenceDatabaseId(db.getServerPath());
            if(dbId == 0) {
                searchDbName = db.getDatabaseFileName();
                dbId = NrSeqLookupUtil.getDatabaseId(searchDbName);
            }
        }
        if (dbId == 0) {
            UploadException ex = new UploadException(ERROR_CODE.SEARCHDB_NOT_FOUND);
            ex.setErrorMessage("No database ID found for: "+searchDbName);
            throw ex;
        }
        return dbId;
    }

    protected void reset() {

        // RESET THE DYNAMIC MOD LOOKUP UTILITY
        dynaModLookup = null;

        numSearchesUploaded = 0;

        resetCaches();

        searchId = 0;
        sequenceDatabaseId = 0;

        preUploadCheckMsg = new StringBuilder();

        dynaResidueMods.clear();
        dynaTermMods.clear();
        db = null;
    }

    protected void resetCaches() {

        proteinMatchList.clear();
        resultResidueModList.clear();
        resultTerminalModList.clear();
    }

    @Override
    public void checkResultChargeMass(boolean check) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setDecoyDirectory(String directory) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setRemoteDecoyDirectory(String directory) {
        throw new UnsupportedOperationException();
    }

    public void deleteSearch(int searchId) {
        if (searchId == 0)
            return;
        log.info("Deleting search ID: "+searchId);
        searchDao.deleteSearch(searchId);
    }

    @Override
    public int getUploadedSearchId() {
        return this.searchId;
    }

    @Override
    public List<String> getFileNames() {
        return new ArrayList<String>(runFileNames);
    }

}