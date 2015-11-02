package org.yeastrc.ms.service.pepxml;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.yeastrc.ms.dao.DAOFactory;
import org.yeastrc.ms.dao.search.xtandem.XtandemSearchDAO;
import org.yeastrc.ms.dao.search.xtandem.XtandemSearchResultDAO;
import org.yeastrc.ms.domain.analysis.peptideProphet.XtandemPeptideProphetResultIn;
import org.yeastrc.ms.domain.search.Program;
import org.yeastrc.ms.domain.search.SearchFileFormat;
import org.yeastrc.ms.domain.search.pepxml.xtandem.PepXmlXtandemSearchScanIn;
import org.yeastrc.ms.domain.search.xtandem.XtandemResultData;
import org.yeastrc.ms.domain.search.xtandem.XtandemResultDataWId;
import org.yeastrc.ms.domain.search.xtandem.XtandemSearchIn;
import org.yeastrc.ms.domain.search.xtandem.XtandemSearchResultIn;
import org.yeastrc.ms.domain.search.xtandem.impl.XtandemResultDataWrap;
import org.yeastrc.ms.parser.DataProviderException;
import org.yeastrc.ms.parser.pepxml.PepXmlGenericFileReader;
import org.yeastrc.ms.parser.pepxml.PepXmlXtandemFileReader;
import org.yeastrc.ms.service.UploadException;
import org.yeastrc.ms.service.UploadException.ERROR_CODE;

public class PepXmlXtandemDataUploadService extends PepXmlDataUploadService<PepXmlXtandemSearchScanIn, 
                                                                            XtandemPeptideProphetResultIn,
                                                                            XtandemSearchResultIn,
                                                                            XtandemSearchIn>{

    private final XtandemSearchResultDAO xtandemResultDao;
    
    private List<XtandemResultDataWId> xtandemResultDataList; // Xtandem scores
    
    public PepXmlXtandemDataUploadService() {
        
        super();
        this.xtandemResultDataList = new ArrayList<XtandemResultDataWId>(BUF_SIZE);
        
        DAOFactory daoFactory = DAOFactory.instance();
        this.xtandemResultDao = daoFactory.getXtandemResultDAO();
    }
    
    @Override
    public Program getSearchProgram() {
        return Program.XTANDEM;
    }
    
    protected void flush() {
        super.flush();
        if (xtandemResultDataList.size() > 0) {
            uploadXtandemResultBuffer();
        }
    }
    
    protected void resetCaches() {
        super.resetCaches();
        xtandemResultDataList.clear();
    }
    
    // -------------------------------------------------------------------------------------------
    // UPLOAD XTANDEM SCORES
    // -------------------------------------------------------------------------------------------
    private void uploadXtandemResultData(XtandemResultData resultData, int resultId) {
        // upload the Xtandem specific result information if the cache has enough entries
        if (xtandemResultDataList.size() >= BUF_SIZE) {
            uploadXtandemResultBuffer();
        }
        // add the Xtandem specific information for this result to the cache
        XtandemResultDataWrap resultDataDb = new XtandemResultDataWrap(resultData, resultId);
        xtandemResultDataList.add(resultDataDb);
    }
    
    private void uploadXtandemResultBuffer() {
        xtandemResultDao.saveAllXtandemResultData(xtandemResultDataList);
        xtandemResultDataList.clear();
    }
    
    @Override
    protected int saveSearch(XtandemSearchIn search, int experimentId, int sequenceDatabaseId) {
        XtandemSearchDAO searchDAO = DAOFactory.instance().getXtandemSearchDAO();
        return searchDAO.saveSearch(search, experimentId, sequenceDatabaseId);
    }

    @Override
    protected PepXmlGenericFileReader<PepXmlXtandemSearchScanIn, XtandemPeptideProphetResultIn, XtandemSearchResultIn, XtandemSearchIn> getPepXmlReader() {
        return new PepXmlXtandemFileReader();
    }

    @Override
    protected void uploadProgramSpecificResultData(XtandemSearchResultIn result,
            int resultId) {
        this.uploadXtandemResultData(result.getXtandemResultData(), resultId);
    }
    
    @Override
    protected int getNumEnzymaticTermini(int searchId) {
        XtandemSearchDAO xtandemSearchDao = DAOFactory.instance().getXtandemSearchDAO();
        return xtandemSearchDao.getNumEnzymaticTermini(searchId);
    }
    
    @Override
    protected boolean getClipNtermMethionine(int searchId) {
        return false;
    }
    
    @Override
    protected SearchFileFormat getSearchFileFormat() {
        return SearchFileFormat.PEPXML_MASCOT;
    }
    
    @Override
    protected XtandemSearchIn getSearchAndParams(String dataDirectory,
            String remoteServer, String remoteDirectory, Date searchDate)
            throws UploadException {
        
        // read parameters from one of the pep.xml files
        String filePath = dataDirectory+File.separator+inputXmlFileNames.get(0);
        PepXmlXtandemFileReader parser = new PepXmlXtandemFileReader();
        XtandemSearchIn search = null;
        try {
            parser.open(filePath);
            if(parser.hasNextRunSearch()) {
                search = parser.getSearch();
            }
        }
        catch (DataProviderException e) {
            UploadException ex = new UploadException(ERROR_CODE.PEPXML_ERROR, e);
            ex.appendErrorMessage("\n\tCould not read search parameters from file: "+filePath+"\n");
            throw ex;
        }
        parser.close();
        
        return search;
            
    }
    
    public static void main(String[] args) throws UploadException {
        PepXmlXtandemDataUploadService p = new PepXmlXtandemDataUploadService();
        
        List<String> spectrumFileNames = new ArrayList<String>();
        spectrumFileNames.add("000");
        spectrumFileNames.add("020");
        spectrumFileNames.add("040");
        spectrumFileNames.add("060");
        spectrumFileNames.add("080");
        spectrumFileNames.add("100");
        spectrumFileNames.add("500");
        spectrumFileNames.add("900");
        p.setSpectrumFileNames(spectrumFileNames);
        
        p.setDirectory("/Users/silmaril/WORK/UW/FLINT/xtandem_test");
        p.setSearchDate(new Date());
        p.setExperimentId(47);
        p.upload();
    }

}
