/**
 * SQTDataUploadService.java
 * @author Vagisha Sharma
 * Jul 15, 2008
 * @version 1.0
 */
package org.yeastrc.ms.service.sqtfile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.yeastrc.ms.dao.DAOFactory;
import org.yeastrc.ms.dao.search.sequest.SequestSearchDAO;
import org.yeastrc.ms.dao.search.sequest.SequestSearchResultDAO;
import org.yeastrc.ms.domain.general.MsEnzymeIn;
import org.yeastrc.ms.domain.search.MsResidueModificationIn;
import org.yeastrc.ms.domain.search.MsSearchDatabaseIn;
import org.yeastrc.ms.domain.search.MsTerminalModificationIn;
import org.yeastrc.ms.domain.search.Param;
import org.yeastrc.ms.domain.search.Program;
import org.yeastrc.ms.domain.search.SearchFileFormat;
import org.yeastrc.ms.domain.search.sequest.SequestResultData;
import org.yeastrc.ms.domain.search.sequest.SequestResultDataWId;
import org.yeastrc.ms.domain.search.sequest.SequestSearchIn;
import org.yeastrc.ms.domain.search.sequest.SequestSearchResultIn;
import org.yeastrc.ms.domain.search.sequest.SequestSearchScan;
import org.yeastrc.ms.domain.search.sequest.impl.SequestResultDataWrap;
import org.yeastrc.ms.parser.DataProviderException;
import org.yeastrc.ms.parser.sequestParams.SequestParamsParser;
import org.yeastrc.ms.parser.sqtFile.sequest.SequestSQTFileReader;
import org.yeastrc.ms.service.MsDataUploadProperties;
import org.yeastrc.ms.service.UploadException;
import org.yeastrc.ms.service.UploadException.ERROR_CODE;
import org.yeastrc.ms.util.FileUtils;

/**
 * 
 */
public class SequestSQTDataUploadService extends AbstractSQTDataUploadService {

    
    private final SequestSearchResultDAO sqtResultDao;
    
    // these are the things we will cache and do bulk-inserts
    List<SequestResultDataWId> sequestResultDataList; // sequest scores
    
    private MsSearchDatabaseIn db = null;
    private boolean usesEvalue = false;
    private List<MsResidueModificationIn> dynaResidueMods;
    private List<MsTerminalModificationIn> dynaTermMods;
    
    private SearchFileFormat format = null;
    
    public SequestSQTDataUploadService() {
        super();
        this.sequestResultDataList = new ArrayList<SequestResultDataWId>();
        this.dynaResidueMods = new ArrayList<MsResidueModificationIn>();
        this.dynaTermMods = new ArrayList<MsTerminalModificationIn>();
        
        DAOFactory daoFactory = DAOFactory.instance();
        
        this.sqtResultDao = daoFactory.getSequestResultDAO();
    }
    
    void reset() {
        super.reset();
        usesEvalue = false;
        dynaResidueMods.clear();
        dynaTermMods.clear();
        db = null;
    }
    // resetCaches() is called by reset() in the superclass.
    void resetCaches() {
        super.resetCaches();
        sequestResultDataList.clear();
    }
    
    MsSearchDatabaseIn getSearchDatabase() {
        return db;
    }

    public Program getSearchProgram() {
        return Program.SEQUEST;
    }
    
    @Override
    int uploadSearchParameters(int experimentId, String paramFileDirectory, 
            String remoteServer, String remoteDirectory,
            Date searchDate) throws UploadException {
        
        SequestParamsParser parser = parseParamsFile(paramFileDirectory, remoteServer);
        
        usesEvalue = parser.reportEvalue();
        db = parser.getSearchDatabase();
        dynaResidueMods = parser.getDynamicResidueMods();
        dynaTermMods = parser.getDynamicTerminalMods();
        // 05/30/2012 -- Commented out check for print_duplicate_references.  We no longer rely on 
        // peptide to protein matches reported in SQT files. 
//        if(!parser.printDuplicateReferences()) {
//            UploadException ex = new UploadException(ERROR_CODE.GENERAL);
//            ex.appendErrorMessage("print_duplicate_references in sequest.params should be set to 1");
//            throw ex; 
//        }
        
        // get the id of the search database used (will be used to look up protein ids later)
        sequenceDatabaseId = getSearchDatabaseId(parser.getSearchDatabase());
        
        // create a new entry in the MsSearch table and upload the search options, databases, enzymes etc.
        try {
            SequestSearchDAO searchDAO = DAOFactory.instance().getSequestSearchDAO();
            return searchDAO.saveSearch(makeSearchObject(parser, getSearchProgram(),
                    remoteDirectory, searchDate), experimentId, sequenceDatabaseId);
        }
        catch(RuntimeException e) {
            UploadException ex = new UploadException(ERROR_CODE.RUNTIME_SQT_ERROR, e);
            ex.setErrorMessage(e.getMessage());
            throw ex;
        }
    }
    
    protected SequestParamsParser parseParamsFile(String fileDirectory, final String remoteServer) throws UploadException {
        
        // parse the parameters file
        final SequestParamsParser parser = new SequestParamsParser();
        log.info("BEGIN Sequest search UPLOAD -- parsing parameters file: "+parser.paramsFileName());
        if (!(new File(fileDirectory+File.separator+parser.paramsFileName()).exists())) {
            UploadException ex = new UploadException(ERROR_CODE.MISSING_SEQUEST_PARAMS);
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
//        lastUploadedRunSearchId = 0;
        long startTime = System.currentTimeMillis();
        SequestSQTFileReader provider = new SequestSQTFileReader();
        
        try {
            provider.open(filePath, usesEvalue);
            provider.setDynamicResidueMods(this.dynaResidueMods);
            provider.setDynamicTerminalMods(this.dynaTermMods);
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
            runSearchId = uploadSequestSqtFile(provider, searchId, runId, sequenceDatabaseId);
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
        
        log.info("END SQT FILE UPLOAD: "+provider.getFileName()+"; RUN_ID: "+runId+ " in "+(endTime - startTime)/(1000L)+"seconds\n");
        
        return runSearchId;
    }
    
    // parse and upload a sqt file
    private int uploadSequestSqtFile(SequestSQTFileReader provider, int searchId, int runId, int searchDbId) throws UploadException {
        
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
        while (provider.hasNextSearchScan()) {
            SequestSearchScan scan = null;
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
                // save all the search results for this scan
                for (SequestSearchResultIn result: scan.getScanResults()) {
                    // upload results only upto the given xCorrRank cutoff.
                    if(useXcorrRankCutoff && result.getSequestResultData().getxCorrRank() > xcorrRankCutoff)
                        continue;
                    uploadSearchResult(result, runSearchId, scanId);
                    numResults++;
                }
            }
            else {
                log.info("Ignoring search scan: "+scan.getScanNumber()+"; scanId: "+scanId+"; charge: "+scan.getCharge()+"; mass: "+scan.getObservedMass());
            }
        }
        flush(); // save any cached data
        log.info("Uploaded SQT file: "+provider.getFileName()+", with "+numResults+
                " results. (runSearchId: "+runSearchId+")");
        
        return runSearchId;
    }

    @Override
    protected void copyFiles(int experimentId) throws UploadException {
        
        log.info("Copying target and decoy SQT files");
        
        String backupDir = MsDataUploadProperties.getBackupDirectory();
        if(!new File(backupDir).exists()) {
            UploadException ex = new UploadException(ERROR_CODE.SQT_BACKUP_ERROR);
            ex.appendErrorMessage("Backup directory: "+backupDir+" does not exist");
            throw ex;
        }
        // create a directory for the experiment
        String exptDir = backupDir+File.separator+experimentId;
        createDirectory(exptDir);
        
        // backup SQT files searched with target database
        backupTargetSqt(exptDir);
        
        // backup SQT files searched with decoy database
        backupDecoySqt(exptDir);
        
    }
    
    private void createDirectory(String directory) throws UploadException {
        if(new File(directory).exists()) {
            UploadException ex = new UploadException(ERROR_CODE.SQT_BACKUP_ERROR);
            ex.appendErrorMessage("Experiment backup directory: "+directory+" already exists");
            throw ex;
        }
        if(!new File(directory).mkdir()) {
            UploadException ex = new UploadException(ERROR_CODE.SQT_BACKUP_ERROR);
            ex.appendErrorMessage("Could not create directory: "+directory);
            throw ex;
        } 
    }
    
    private void backupTargetSqt(String exptDir) throws UploadException {
        
        String destDir = exptDir+File.separator+"sequest";
        createDirectory(destDir);
        
        String srcDir = getDataDirectory();
        List<String> filePaths = new ArrayList<String>();
        for(String file: getFileNames()) {
        	filePaths.add(srcDir+File.separator+file);
        }
        backupSqt(filePaths, destDir, exptDir);
    }
    
    private void backupDecoySqt(String exptDir) throws UploadException {
    	
        String destDir = exptDir+File.separator+"sequest"+File.separator+"decoy";
        createDirectory(destDir);
        String srcDir = getDataDirectory()+File.separator+"decoy";
        File srcDirFile = new File(srcDir);
        
        List<String> filePaths = new ArrayList<String>();
        
        if(srcDirFile.exists()) {
        	for(String file: getFileNames()) {
        		String path = srcDir+File.separator+file;
        		if(new File(path).exists())
        			filePaths.add(path);
            }
        }
        else {
        	// LabKey pipeline puts the decoy and target SQT files in the same directory
        	// Decoy files have .decoy.sqt extension.
        	srcDir = getDataDirectory();
        	for(String file: getFileNames()) {
        		String filenoext = FileUtils.removeExtension(file);
        		String path = srcDir+File.separator+filenoext+".decoy.sqt";
        		if(new File(path).exists())
        			filePaths.add(path);
            }
        }
        
        backupSqt(filePaths, destDir, exptDir);
    }
    
    private void backupSqt(List<String> filePaths, String destDir, String exptDir) throws UploadException {
        // copy sqt files from the source to target directory
        
        for(String filePath: filePaths) {
             File src = new File(filePath);
             File dest = new File(destDir+File.separator+src.getName());
             try {
                FileUtils.copyFile(src, dest);
            }
            catch (IOException e) {
                log.error("Error copying file: "+src.getAbsolutePath());
                deleteBackupDirectory(exptDir);
                UploadException ex = new UploadException(ERROR_CODE.SQT_BACKUP_ERROR, e);
                ex.appendErrorMessage("Error copying file: "+filePath);
                throw ex;
            }
        }
    }
    
    private void deleteBackupDirectory(String experimentDir) throws UploadException {
        File file = new File(experimentDir);
        if(!file.exists()) {
            UploadException ex = new UploadException(ERROR_CODE.GENERAL);
            ex.appendErrorMessage("Cannot delete backup experiment directory: "+experimentDir+". It does not exist");
            throw ex;
        }
        log.info("Deleting backup experiment directory: "+experimentDir);
        FileUtils.deleteFile(new File(experimentDir));
        if(file.exists()) {
            UploadException ex = new UploadException(ERROR_CODE.GENERAL);
            ex.appendErrorMessage("Backup experiment directory was not deleted: "+experimentDir);
            throw ex;
        }
    }
    
    static SequestSearchIn makeSearchObject(final SequestParamsParser parser, final Program searchProgram,
                final String remoteDirectory, final java.util.Date searchDate) {
        return new SequestSearchIn() {
            @Override
            public List<Param> getSequestParams() {return parser.getParamList();}
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
            public Program getSearchProgram() {return searchProgram;}
//            public Program getSearchProgram() {return parser.getSearchProgram();}
            @Override
            public String getSearchProgramVersion() {return null;} // we don't have this information in sequest.params
            public java.sql.Date getSearchDate() {return new java.sql.Date(searchDate.getTime());}
            public String getServerDirectory() {return remoteDirectory;}
        };
    }
    
    void uploadSearchResult(SequestSearchResultIn result, int runSearchId, int scanId) throws UploadException {
        
        int resultId = super.uploadBaseSearchResult(result, runSearchId, scanId);
        
        // upload the SQT sequest specific information for this result.
        uploadSequestResultData(result.getSequestResultData(), resultId);
    }

    private void uploadSequestResultData(SequestResultData resultData, int resultId) {
        // upload the Sequest specific result information if the cache has enough entries
        if (sequestResultDataList.size() >= BUF_SIZE) {
            uploadSequestResultBuffer();
        }
        // add the Sequest specific information for this result to the cache
        SequestResultDataWrap resultDataDb = new SequestResultDataWrap(resultData, resultId);
        sequestResultDataList.add(resultDataDb);
    }
    
    private void uploadSequestResultBuffer() {
        sqtResultDao.saveAllSequestResultData(sequestResultDataList);
        sequestResultDataList.clear();
    }
    
    void flush() {
        super.flush();
        if (sequestResultDataList.size() > 0) {
            uploadSequestResultBuffer();
        }
    }
    
    @Override
    SearchFileFormat getSearchFileFormat() {
        // return SearchFileFormat.SQT_SEQ;
    	return this.format;
    }
    
    public void setSearchFileFormat(SearchFileFormat format) {
    	this.format = format;
    }

    @Override
    String searchParamsFile() {
        SequestParamsParser parser = new SequestParamsParser();
        return parser.paramsFileName();
    }
    
    @Override
    boolean doRefreshPeptideProteinMatches() {
    	return false;
    }

}
