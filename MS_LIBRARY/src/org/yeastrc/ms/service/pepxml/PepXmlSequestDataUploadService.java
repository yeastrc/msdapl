/**
 * PepXmlSequestDataUploadService.java
 * @author Vagisha Sharma
 * Oct 6, 2009
 * @version 1.0
 */
package org.yeastrc.ms.service.pepxml;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.yeastrc.ms.dao.DAOFactory;
import org.yeastrc.ms.dao.search.MsSearchDAO;
import org.yeastrc.ms.dao.search.sequest.SequestSearchDAO;
import org.yeastrc.ms.dao.search.sequest.SequestSearchResultDAO;
import org.yeastrc.ms.domain.analysis.peptideProphet.SequestPeptideProphetResultIn;
import org.yeastrc.ms.domain.general.MsEnzymeIn;
import org.yeastrc.ms.domain.search.MsResidueModificationIn;
import org.yeastrc.ms.domain.search.MsSearch;
import org.yeastrc.ms.domain.search.MsSearchDatabaseIn;
import org.yeastrc.ms.domain.search.MsSearchIn;
import org.yeastrc.ms.domain.search.MsTerminalModificationIn;
import org.yeastrc.ms.domain.search.Param;
import org.yeastrc.ms.domain.search.Program;
import org.yeastrc.ms.domain.search.SearchFileFormat;
import org.yeastrc.ms.domain.search.pepxml.sequest.PepXmlSequestSearchScanIn;
import org.yeastrc.ms.domain.search.sequest.SequestResultData;
import org.yeastrc.ms.domain.search.sequest.SequestResultDataWId;
import org.yeastrc.ms.domain.search.sequest.SequestSearchIn;
import org.yeastrc.ms.domain.search.sequest.SequestSearchResultIn;
import org.yeastrc.ms.domain.search.sequest.impl.SequestResultDataWrap;
import org.yeastrc.ms.parser.DataProviderException;
import org.yeastrc.ms.parser.pepxml.PepXmlGenericFileReader;
import org.yeastrc.ms.parser.pepxml.PepXmlSequestFileReader;
import org.yeastrc.ms.parser.sequestParams.SequestParamsParser;
import org.yeastrc.ms.service.UploadException;
import org.yeastrc.ms.service.UploadException.ERROR_CODE;

/**
 * 
 */
public class PepXmlSequestDataUploadService extends PepXmlDataUploadService<PepXmlSequestSearchScanIn, 
                                                                            SequestPeptideProphetResultIn,
                                                                            SequestSearchResultIn,
                                                                            SequestSearchIn> {

    private final SequestSearchResultDAO sqtResultDao;
    
    private List<SequestResultDataWId> sequestResultDataList; // sequest scores
    
    private boolean usesEvalue = false;
    
    private static final Logger log = Logger.getLogger(PepXmlSequestDataUploadService.class.getName());
    
    
    public PepXmlSequestDataUploadService() {
        
        this.sequestResultDataList = new ArrayList<SequestResultDataWId>(BUF_SIZE);
        
        DAOFactory daoFactory = DAOFactory.instance();
        this.sqtResultDao = daoFactory.getSequestResultDAO();
    }
    
    @Override
    public Program getSearchProgram() {
        return Program.SEQUEST;
    }
    
    public boolean preUploadCheckPassed() {
        
        if(!super.preUploadCheckPassed())
            return false;
        
        // Make sure the search parameters file is present
        File paramsFile = new File(dataDirectory+File.separator+searchParamsFile());
        if(!paramsFile.exists()) {
            appendToMsg("Cannot find search parameters file: "+paramsFile.getAbsolutePath());
            return false;
        }
        
        preUploadCheckDone = true;
        
        return true;
    }

    protected String searchParamsFile() {
        SequestParamsParser parser = new SequestParamsParser();
        return parser.paramsFileName();
    }
    

    protected void matchSearchParams(int searchId, MsSearchIn parsedSearch, String fileName) throws UploadException {
        
        // load the search and its parameters, enzyme information, database information
        // and modification information
        MsSearchDAO searchDao = DAOFactory.instance().getMsSearchDAO();
        MsSearch search = searchDao.loadSearch(searchId);

        SearchParamMatcher matcher = new SearchParamMatcher();
        boolean matches = matcher.matchSearchParams(search, parsedSearch, fileName);

        if(!matches) {
            
            // TODO: if the only thing that mismatched was the enzyme information ignore it
            // pepxml files can have wrong enzyme information. e.g. Trypsin vs Trypsin_K in sequest.params
            if(!matcher.isEnzymesMatch() &&
                (matcher.isDatabasesMatch() && 
                 matcher.isDynamicResidueModsMatch() &&
                 matcher.isStaticResidueModsMatch() && 
                 matcher.isDynamicTerminalModsMatch() &&
                 matcher.isStaticTerminalModsMatch())) {
                log.error(matcher.getErrorMessage());
            }
            else {
                log.error(matcher.getErrorMessage());
                UploadException ex = new UploadException(ERROR_CODE.GENERAL);
                ex.setErrorMessage(matcher.getErrorMessage());
                throw ex;
            }
        }
        // TODO do we need to match some other key parameters e.g. min_enzymatic_termini etc. 
    }
    
    protected void flush() {
        super.flush();
        if (sequestResultDataList.size() > 0) {
            uploadSequestResultBuffer();
        }
    }
    
    protected void reset() {
        super.reset();
        usesEvalue = false;
    }

    // called before uploading each msms_run_search in the interact.pep.xml file and in the reset() method.
    protected void resetCaches() {
        super.resetCaches();
        sequestResultDataList.clear();
    }

    // -------------------------------------------------------------------------------------------
    // UPLOAD SEQUEST SCORES
    // -------------------------------------------------------------------------------------------
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
    
 
    protected SequestSearchIn getSearchAndParams(String paramFileDirectory, 
            String remoteServer, String remoteDirectory,
            Date searchDate) throws UploadException {
        
        SequestParamsParser parser = parseParamsFile(paramFileDirectory, remoteServer);
        
        usesEvalue = parser.reportEvalue();
        
        SequestSearchIn search = makeSearchObject(parser, getSearchProgram(), remoteDirectory, searchDate);
        return search;
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
    
    private static SequestSearchIn makeSearchObject(final SequestParamsParser parser, final Program searchProgram,
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
    
    
    
    @Override
    protected int getNumEnzymaticTermini(int searchId) {
        SequestSearchDAO seqDao = DAOFactory.instance().getSequestSearchDAO();
        return seqDao.getNumEnzymaticTermini(searchId);
    }
    
    @Override
    protected boolean getClipNtermMethionine(int searchId) {
        SequestSearchDAO seqDao = DAOFactory.instance().getSequestSearchDAO();
        return seqDao.getClipNterMethionine(searchId);
    }

    @Override
    protected PepXmlGenericFileReader<PepXmlSequestSearchScanIn, SequestPeptideProphetResultIn, SequestSearchResultIn, SequestSearchIn> getPepXmlReader() {
       PepXmlSequestFileReader reader = new PepXmlSequestFileReader();
       reader.setParseEvalue(this.usesEvalue);
       return reader;
    }

    @Override
    protected SearchFileFormat getSearchFileFormat() {
        return SearchFileFormat.PEPXML_SEQ;
    }

    @Override
    protected int saveSearch(SequestSearchIn search, int experimentId,
            int sequenceDatabaseId) {
        SequestSearchDAO searchDAO = DAOFactory.instance().getSequestSearchDAO();
        return searchDAO.saveSearch(search, experimentId, sequenceDatabaseId);
    }

    @Override
    protected void uploadProgramSpecificResultData(SequestSearchResultIn result, int resultId) {
        this.uploadSequestResultData(result.getSequestResultData(), resultId);
    }
    
    public static void main(String[] args) throws UploadException {
        PepXmlSequestDataUploadService p = new PepXmlSequestDataUploadService();
        List<String> spectrumFileNames = new ArrayList<String>();
        
        spectrumFileNames.add("M_102908_Y_Lys_ETD_EPI_contol");
        spectrumFileNames.add("M_102908_Y_Lys_ETD_EPI_poly2");
        spectrumFileNames.add("M_121808_Yeast_LysC_ETD_EPIQ_01");
        spectrumFileNames.add("M_123008_Yeast_short_ETD_EPI_01");
        spectrumFileNames.add("M_123008_Yeast_short_ETD_EPI_02");
        spectrumFileNames.add("M_123008_Yeast_short_ETD_EPI_03");
        
        p.setSpectrumFileNames(spectrumFileNames);
        
        p.setDirectory("/Users/silmaril/WORK/UW/FLINT/Jimmy_Test");
        p.setSearchDate(new Date());
        p.setExperimentId(37);
        p.upload();
    }
}
