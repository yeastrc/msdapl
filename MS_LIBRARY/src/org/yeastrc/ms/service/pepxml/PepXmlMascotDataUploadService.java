/**
 * PepXmlMascotDataUploadService.java
 * @author Vagisha Sharma
 * Oct 6, 2009
 * @version 1.0
 */
package org.yeastrc.ms.service.pepxml;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.yeastrc.ms.dao.DAOFactory;
import org.yeastrc.ms.dao.search.mascot.MascotSearchDAO;
import org.yeastrc.ms.dao.search.mascot.MascotSearchResultDAO;
import org.yeastrc.ms.domain.analysis.peptideProphet.MascotPeptideProphetResultIn;
import org.yeastrc.ms.domain.search.Program;
import org.yeastrc.ms.domain.search.SearchFileFormat;
import org.yeastrc.ms.domain.search.mascot.MascotResultData;
import org.yeastrc.ms.domain.search.mascot.MascotResultDataWId;
import org.yeastrc.ms.domain.search.mascot.MascotSearchIn;
import org.yeastrc.ms.domain.search.mascot.MascotSearchResultIn;
import org.yeastrc.ms.domain.search.mascot.impl.MascotResultDataWrap;
import org.yeastrc.ms.domain.search.pepxml.mascot.PepXmlMascotSearchScanIn;
import org.yeastrc.ms.parser.DataProviderException;
import org.yeastrc.ms.parser.pepxml.PepXmlGenericFileReader;
import org.yeastrc.ms.parser.pepxml.PepXmlMascotFileReader;
import org.yeastrc.ms.service.UploadException;
import org.yeastrc.ms.service.UploadException.ERROR_CODE;

/**
 * 
 */
public class PepXmlMascotDataUploadService extends PepXmlDataUploadService<PepXmlMascotSearchScanIn, 
                                                                           MascotPeptideProphetResultIn,
                                                                           MascotSearchResultIn,
                                                                           MascotSearchIn> {

    private final MascotSearchResultDAO mascotResultDao;
    
    private List<MascotResultDataWId> mascotResultDataList; // mascot scores
    
    public PepXmlMascotDataUploadService() {
        
        super();
        this.mascotResultDataList = new ArrayList<MascotResultDataWId>(BUF_SIZE);
        
        DAOFactory daoFactory = DAOFactory.instance();
        this.mascotResultDao = daoFactory.getMascotResultDAO();
    }
    
    @Override
    public Program getSearchProgram() {
        return Program.MASCOT;
    }
    
    protected void flush() {
        super.flush();
        if (mascotResultDataList.size() > 0) {
            uploadMascotResultBuffer();
        }
    }
    
    protected void resetCaches() {
        super.resetCaches();
        mascotResultDataList.clear();
    }
    
    // -------------------------------------------------------------------------------------------
    // UPLOAD MASCOT SCORES
    // -------------------------------------------------------------------------------------------
    void uploadMascotResultData(MascotResultData resultData, int resultId) {
        // upload the Mascot specific result information if the cache has enough entries
        if (mascotResultDataList.size() >= BUF_SIZE) {
            uploadMascotResultBuffer();
        }
        // add the Mascot specific information for this result to the cache
        MascotResultDataWrap resultDataDb = new MascotResultDataWrap(resultData, resultId);
        mascotResultDataList.add(resultDataDb);
    }
    
    private void uploadMascotResultBuffer() {
        mascotResultDao.saveAllMascotResultData(mascotResultDataList);
        mascotResultDataList.clear();
    }
    
    @Override
    protected int saveSearch(MascotSearchIn search, int experimentId, int sequenceDatabaseId) {
        MascotSearchDAO searchDAO = DAOFactory.instance().getMascotSearchDAO();
        return searchDAO.saveSearch(search, experimentId, sequenceDatabaseId);
    }

    @Override
    protected PepXmlGenericFileReader<PepXmlMascotSearchScanIn, MascotPeptideProphetResultIn, MascotSearchResultIn, MascotSearchIn> getPepXmlReader() {
        return new PepXmlMascotFileReader();
    }

    @Override
    protected void uploadProgramSpecificResultData(MascotSearchResultIn result,
            int resultId) {
        this.uploadMascotResultData(result.getMascotResultData(), resultId);
    }
    
    @Override
    protected int getNumEnzymaticTermini(int searchId) {
        MascotSearchDAO mascotDao = DAOFactory.instance().getMascotSearchDAO();
        return mascotDao.getNumEnzymaticTermini(searchId);
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
    protected MascotSearchIn getSearchAndParams(String dataDirectory,
            String remoteServer, String remoteDirectory, Date searchDate)
            throws UploadException {
        
        // read parameters from one of the pep.xml files
        String filePath = dataDirectory+File.separator+inputXmlFileNames.get(0);
        PepXmlMascotFileReader parser = new PepXmlMascotFileReader();
        MascotSearchIn search = null;
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
        PepXmlMascotDataUploadService p = new PepXmlMascotDataUploadService();
        
        List<String> spectrumFileNames = new ArrayList<String>();
        spectrumFileNames.add("090715_EPO-iT_80mM_HCD.pep.xml");
        p.setSpectrumFileNames(spectrumFileNames);
        
        p.setDirectory("/Users/silmaril/WORK/UW/FLINT/mascot_test");
        p.setSearchDate(new Date());
        p.setExperimentId(37);
        p.upload();
    }

}
