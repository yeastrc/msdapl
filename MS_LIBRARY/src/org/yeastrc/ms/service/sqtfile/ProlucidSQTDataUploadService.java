/**
 * ProlucidSQTDataUploadService.java
 * @author Vagisha Sharma
 * Aug 31, 2008
 * @version 1.0
 */
package org.yeastrc.ms.service.sqtfile;

import java.io.File;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.yeastrc.ms.dao.DAOFactory;
import org.yeastrc.ms.dao.search.prolucid.ProlucidSearchDAO;
import org.yeastrc.ms.dao.search.prolucid.ProlucidSearchResultDAO;
import org.yeastrc.ms.domain.general.MsEnzymeIn;
import org.yeastrc.ms.domain.search.MsResidueModificationIn;
import org.yeastrc.ms.domain.search.MsSearchDatabaseIn;
import org.yeastrc.ms.domain.search.MsTerminalModificationIn;
import org.yeastrc.ms.domain.search.Program;
import org.yeastrc.ms.domain.search.SearchFileFormat;
import org.yeastrc.ms.domain.search.prolucid.ProlucidParamIn;
import org.yeastrc.ms.domain.search.prolucid.ProlucidResultData;
import org.yeastrc.ms.domain.search.prolucid.ProlucidResultDataWId;
import org.yeastrc.ms.domain.search.prolucid.ProlucidSearchIn;
import org.yeastrc.ms.domain.search.prolucid.ProlucidSearchResultIn;
import org.yeastrc.ms.domain.search.prolucid.ProlucidSearchScan;
import org.yeastrc.ms.domain.search.prolucid.impl.ProlucidResultDataWrap;
import org.yeastrc.ms.parser.DataProviderException;
import org.yeastrc.ms.parser.prolucidParams.ProlucidParamsParser;
import org.yeastrc.ms.parser.sqtFile.prolucid.ProlucidSQTFileReader;
import org.yeastrc.ms.service.UploadException;
import org.yeastrc.ms.service.UploadException.ERROR_CODE;

/**
 * 
 */
public final class ProlucidSQTDataUploadService extends AbstractSQTDataUploadService {

    private final ProlucidSearchResultDAO sqtResultDao;
    
    List<ProlucidResultDataWId> prolucidResultDataList; // cached prolucid search result data
    
    private MsSearchDatabaseIn db = null;
    private List<MsResidueModificationIn> dynaResidueMods;
    private List<MsTerminalModificationIn> dynaTermMods;
    
    
    public ProlucidSQTDataUploadService() {
        super();
        this.prolucidResultDataList = new ArrayList<ProlucidResultDataWId>();
        this.dynaResidueMods = new ArrayList<MsResidueModificationIn>();
        this.dynaTermMods = new ArrayList<MsTerminalModificationIn>();
        
        DAOFactory daoFactory = DAOFactory.instance();
        sqtResultDao = daoFactory.getProlucidResultDAO();
    }
    
    void reset() {
        super.reset();
        dynaResidueMods.clear();
        dynaTermMods.clear();
        db = null;
    }
    
    // resetCaches() is called by reset() in the superclass.
    void resetCaches() {
        super.resetCaches();
        prolucidResultDataList.clear();
    }
    
    MsSearchDatabaseIn getSearchDatabase() {
        return db;
    }

    public Program getSearchProgram() {
        return Program.PROLUCID;
    }
    
    @Override
    int uploadSearchParameters(int experimentId, String paramFileDirectory, 
            String remoteServer, String remoteDirectory,
            java.util.Date searchDate) throws UploadException {
        
        // parse the parameter file 
        ProlucidParamsParser parser = parseProlucidParams(paramFileDirectory, remoteServer);
        
        db = parser.getSearchDatabase();
        dynaResidueMods = parser.getDynamicResidueMods();
        dynaTermMods = parser.getDynamicTerminalMods();
        
        // get the id of the search database used (will be used to look up protein ids later)
        sequenceDatabaseId = getSearchDatabaseId(parser.getSearchDatabase());
        
        // create a new entry in the MsSearch table and upload the search options, databases, enzymes etc.
        try {
            ProlucidSearchDAO searchDAO = DAOFactory.instance().getProlucidSearchDAO();
            return searchDAO.saveSearch(makeSearchObject(parser, remoteDirectory, searchDate), experimentId, sequenceDatabaseId);
        }
        catch(RuntimeException e) {
            UploadException ex = new UploadException(ERROR_CODE.RUNTIME_SQT_ERROR, e);
            ex.setErrorMessage(e.getMessage());
            throw ex;
        }
    }
    
    private ProlucidParamsParser parseProlucidParams(String fileDirectory, final String remoteServer) throws UploadException {
        
        // parse the parameters file
        final ProlucidParamsParser parser = new ProlucidParamsParser();
        log.info("BEGIN ProLuCID search upload -- parsing parameters file: "+parser.paramsFileName());
        if (!(new File(fileDirectory+File.separator+parser.paramsFileName()).exists())) {
            UploadException ex = new UploadException(ERROR_CODE.MISSING_PROLUCID_PARAMS);
            throw ex;
        }
        try {
            parser.parseParams(remoteServer, fileDirectory);
            return parser;
        }
        catch (DataProviderException e) {
            UploadException ex = new UploadException(ERROR_CODE.PARAM_PARSING_ERROR);
            ex.setFile(fileDirectory+File.separator+parser.paramsFileName());
            ex.setErrorMessage(e.getMessage());
            throw ex;
        }
    }


    @Override
    int uploadSqtFile(String filePath, int runId) throws UploadException {
        
        log.info("BEGIN SQT FILE UPLOAD: "+(new File(filePath).getName())+"; RUN_ID: "+runId+"; SEARCH_ID: "+searchId);
        
        long startTime = System.currentTimeMillis();
        ProlucidSQTFileReader provider = new ProlucidSQTFileReader();
        
        try {
            provider.open(filePath);
            provider.setDynamicResidueMods(dynaResidueMods);
            provider.setDynamicTerminalMods(dynaTermMods);
        }
        catch (DataProviderException e) {
            provider.close();
            UploadException ex = new UploadException(ERROR_CODE.READ_ERROR_SQT, e);
            ex.setFile(filePath);
            ex.setErrorMessage(e.getMessage()+"\n\t!!!SQT FILE WILL NOT BE UPLOADED!!!");
            throw ex;
        }
        
        int runSearchId;
        try {
            runSearchId = uploadProlucidSqtFile(provider, searchId, runId, sequenceDatabaseId);
        }
        catch (UploadException ex) {
            ex.setFile(filePath);
            ex.appendErrorMessage("\n\t!!!SQT FILE WILL NOT BE UPLOADED!!!");
            throw ex;
        }
        catch (RuntimeException e) { // most likely due to SQL exception
            UploadException ex = new UploadException(ERROR_CODE.RUNTIME_SQT_ERROR, e);
            ex.setFile(filePath);
            ex.setErrorMessage(e.getMessage()+"\n\t!!!SQT FILE WILL NOT BE UPLOADED!!!");
            throw ex;
        }
        finally {provider.close();}
        
        long endTime = System.currentTimeMillis();
        
        log.info("END SQT FILE UPLOAD: "+provider.getFileName()+"; RUN_ID: "+runId+ " in "+(endTime - startTime)/(1000L)+"seconds");
        
        return runSearchId;
    }
    
    
    // parse and upload a sqt file
    private int uploadProlucidSqtFile(ProlucidSQTFileReader provider, int searchId, int runId, int searchDbId) throws UploadException {
        
        int runSearchId;
        try {
            runSearchId = uploadSearchHeader(provider, runId, searchId);
            log.info("Uploaded top-level info for sqt file. runSearchId: "+runSearchId);
        }
        catch(DataProviderException e) {
            UploadException ex = new UploadException(ERROR_CODE.INVALID_SQT_HEADER, e);
            ex.setErrorMessage(e.getMessage());
            throw ex;
        }

        // upload the search results for each scan + charge combination
        int numResults = 0;
        int numProteins = 0;
        while (provider.hasNextSearchScan()) {
            ProlucidSearchScan scan = null;
            try {
                scan = provider.getNextSearchScan();
            }
            catch (DataProviderException e) {
                UploadException ex = new UploadException(ERROR_CODE.INVALID_SQT_SCAN);
                ex.setErrorMessage(e.getMessage());
                throw ex;
            }
            
            int scanId = getScanId(runId, scan.getScanNumber());
            // save spectrum data
            if(uploadSearchScan(scan, runSearchId, scanId)) {
            	
                // save ONLY the FIRST search result for this scan
            	
            	if ( ! scan.getScanResults().isEmpty() ) {
            		ProlucidSearchResultIn result = scan.getScanResults().get(0);
            		uploadSearchResult(result, runSearchId, scanId);
            		numResults++;
            		numProteins += result.getProteinMatchList().size();
            	}
            	
            	//  WAS
            	
//                // save all the search results for this scan
//                for (ProlucidSearchResultIn result: scan.getScanResults()) {
//                    uploadSearchResult(result, runSearchId, scanId);
//                    numResults++;
//                    numProteins += result.getProteinMatchList().size();
//                }
            }
            else {
                log.info("Ignoring search scan: "+scan.getScanNumber()+"; scanId: "+scanId+"; charge: "+scan.getCharge()+"; mass: "+scan.getObservedMass());
            }
        }
        
        flush(); // save any cached data
        log.info("Uploaded SQT file: "+provider.getFileName()+", with "+numResults+
                " results, "+numProteins+" protein matches. (runSearchId: "+runSearchId+")");
                
        return runSearchId;
    }

    
    static ProlucidSearchIn makeSearchObject(final ProlucidParamsParser parser, 
                    final String remoteDirectory, final java.util.Date searchDate) {
        return new ProlucidSearchIn() {
            @Override
            public List<ProlucidParamIn> getProlucidParams() {return parser.getParamList();}
            @Override
            public List<MsResidueModificationIn> getDynamicResidueMods() {return parser.getDynamicResidueMods();}
            @Override
            public List<MsTerminalModificationIn> getDynamicTerminalMods() {return parser.getDynamicTerminalMods();}
            @Override
            public List<MsEnzymeIn> getEnzymeList() {
                if (parser.isEnzymeUsedForSearch())
                    return Arrays.asList(new MsEnzymeIn[]{parser.getSearchEnzyme()});
                else 
                    return new ArrayList<MsEnzymeIn>(0);
            }
            @Override
            public List<MsSearchDatabaseIn> getSearchDatabases() {return Arrays.asList(new MsSearchDatabaseIn[]{parser.getSearchDatabase()});}
            @Override
            public List<MsResidueModificationIn> getStaticResidueMods() {return parser.getStaticResidueMods();}
            @Override
            public List<MsTerminalModificationIn> getStaticTerminalMods() {return parser.getStaticTerminalMods();}
            @Override
            public Program getSearchProgram() {return parser.getSearchProgram();}
            @Override
            public String getSearchProgramVersion() {return null;} // we don't have this information in search.xml
            public Date getSearchDate() {return new java.sql.Date(searchDate.getTime());}
            public String getServerDirectory() {return remoteDirectory;}
        };
    }
    
    void uploadSearchResult(ProlucidSearchResultIn result, int runSearchId, int scanId) throws UploadException {
        
        int resultId = super.uploadBaseSearchResult(result, runSearchId, scanId);
        
        // upload the SQT prolucid specific information for this result.
        uploadProlucidResultData(result.getProlucidResultData(), resultId);
    }

    private void uploadProlucidResultData(ProlucidResultData resultData, int resultId) {
        // upload the Prolucid specific result information if the cache has enough entries
        if (prolucidResultDataList.size() >= BUF_SIZE) {
            uploadProlucidResultBuffer();
        }
        // add the Prolucid specific information for this result to the cache
        ProlucidResultDataWrap resultDataDb = new ProlucidResultDataWrap(resultData, resultId);
        prolucidResultDataList.add(resultDataDb);
    }
    
    private void uploadProlucidResultBuffer() {
        sqtResultDao.saveAllProlucidResultData(prolucidResultDataList);
        prolucidResultDataList.clear();
    }
    
    protected void flush() {
        super.flush();
        if (prolucidResultDataList.size() > 0) {
            uploadProlucidResultBuffer();
        }
    }

    @Override
    SearchFileFormat getSearchFileFormat() {
        return SearchFileFormat.SQT_PLUCID;
    }

    @Override
    String searchParamsFile() {
        ProlucidParamsParser parser = new ProlucidParamsParser();
        return parser.paramsFileName();
    }

    @Override
    protected void copyFiles(int experimentId) throws UploadException {
        // Does nothing
    }
    
    @Override
    boolean doRefreshPeptideProteinMatches() {
    	return false;
    }
}
