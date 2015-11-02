package org.yeastrc.ms.dao.analysis.percolator.ibatis;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.yeastrc.ms.dao.BaseDAOTestCase;
import org.yeastrc.ms.dao.DAOFactory;
import org.yeastrc.ms.dao.analysis.MsRunSearchAnalysisDAO;
import org.yeastrc.ms.dao.analysis.percolator.PercolatorResultDAO;
import org.yeastrc.ms.domain.analysis.impl.RunSearchAnalysisBean;
import org.yeastrc.ms.domain.analysis.percolator.PercolatorResult;
import org.yeastrc.ms.domain.analysis.percolator.PercolatorResultDataWId;
import org.yeastrc.ms.domain.analysis.percolator.impl.PercolatorResultDataBean;
import org.yeastrc.ms.domain.search.MsSearchIn;
import org.yeastrc.ms.domain.search.MsSearchResultIn;
import org.yeastrc.ms.domain.search.SearchFileFormat;

public class PercolatorResultDAOImplTest extends BaseDAOTestCase {

    private static final MsRunSearchAnalysisDAO rsaDao = DAOFactory.instance().getMsRunSearchAnalysisDAO();
    private static final PercolatorResultDAO percResDao = DAOFactory.instance().getPercolatorResultDAO();
    
    protected void setUp() throws Exception {
        super.setUp();
        BaseDAOTestCase.resetDatabase();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public final void testSavePercolatorResultDataWId() {
        PercolatorResultDataBean data = new PercolatorResultDataBean();
        data.setSearchResultId(12);
        data.setRunSearchAnalysisId(21);
        data.setPosteriorErrorProbability(0.23);
        data.setQvalue(0.01);
        try {
            percResDao.save(data);
        }
        catch(Exception e) {
            fail("Error saving valid percolator result");
        }
    }

    public final void testSaveAllPercolatorResultData() {
        
        List<PercolatorResultDataWId> dataList = new ArrayList<PercolatorResultDataWId>(3);
        for(int i = 1; i < 4; i++) {
            PercolatorResultDataBean data = new PercolatorResultDataBean();
            data.setSearchResultId(i);
            data.setRunSearchAnalysisId(21);
            data.setPosteriorErrorProbability(0.1*i);
            data.setQvalue(0.01*i);
            dataList.add(data);
        }
        try {
            percResDao.saveAllPercolatorResultData(dataList);
        }
        catch(Exception e) {
            fail("Error saving valid percolator result list");
        }
    }
    
    public final void testLoadResultIdsWithPepThreshold() {

        List<PercolatorResultDataWId> dataList = new ArrayList<PercolatorResultDataWId>(3);
        for(int i = 1; i < 4; i++) {
            MsSearchResultIn result = this.makeSearchResult(1, 2, "PEPTIDE", new BigDecimal("1024.5"), true); // searchId = 1; charge = 2;
            int id = resultDao.save(1, result, 2, 3); // searchId= 1; runsearchId=2; scanId = 3
            PercolatorResultDataBean data = new PercolatorResultDataBean();
            data.setSearchResultId(id);
            data.setRunSearchAnalysisId(21);
            data.setPosteriorErrorProbability(0.1*i);
            data.setQvalue(0.01*i);
            dataList.add(data);
        }
        percResDao.saveAllPercolatorResultData(dataList);
        List<PercolatorResult> results = percResDao.loadTopPercolatorResultsN(21, null, 0.25, null, false);
//        List<Integer> ids = percResDao.loadResultIdsWithPepThreshold(21, 0.25);
        assertEquals(2, results.size());
        
    }

    public final void testLoadResultIdsWithQvalueThreshold() {
        List<PercolatorResultDataWId> dataList = new ArrayList<PercolatorResultDataWId>(3);
        for(int i = 1; i < 4; i++) {
            MsSearchResultIn result = this.makeSearchResult(1, 2, "PEPTIDE", new BigDecimal("1024.5"), true); // searchId = 1; charge = 2;
            int id = resultDao.save(1, result, 2, 3); // searchId= 1; runsearchId=2; scanId = 3
            PercolatorResultDataBean data = new PercolatorResultDataBean();
            data.setSearchResultId(id);
            data.setRunSearchAnalysisId(21);
            data.setPosteriorErrorProbability(0.1*i);
            data.setQvalue(0.01*i);
            dataList.add(data);
        }
        percResDao.saveAllPercolatorResultData(dataList);
        List<PercolatorResult> results = percResDao.loadTopPercolatorResultsN(21, 0.019, null, null, false);
//        List<Integer> ids = percResDao.loadResultIdsWithQvalueThreshold(21, 0.019);
        assertEquals(1, results.size());
    }
    
    public final void testLoad() {
        
        // save a search
        MsSearchIn search = super.makeSearch(true, false, false, true);
        int searchId = searchDao.saveSearch(search, 2, 56);
        
        // save a couple of search results
        MsSearchResultIn result1 = super.makeSearchResult(searchId, 3, "PEPTIDE", new BigDecimal("1024.5"), false);
        int resultId1 = resultDao.save(searchId, result1, 12, 999);
        
        MsSearchResultIn result2 = super.makeSearchResult(searchId, 2, "EDITPEP", new BigDecimal("1024.5"), false);
        int resultId2 = resultDao.save(searchId, result2, 13, 1999);
        
        // save percolator results for each search result
        PercolatorResultDataBean pres1 = new PercolatorResultDataBean();
        pres1.setSearchResultId(resultId1);
        pres1.setRunSearchAnalysisId(21);
        pres1.setQvalue(0.01);
        pres1.setPosteriorErrorProbability(0.25);
        percResDao.save(pres1);
        
        pres1 = new PercolatorResultDataBean();
        pres1.setSearchResultId(resultId2);
        pres1.setRunSearchAnalysisId(21);
        pres1.setQvalue(0.05);
        pres1.setDiscriminantScore(0.50);
        percResDao.save(pres1);
        
        // load the percolator results
        // TODO fix me
//        PercolatorResult fromDb = percResDao.load(resultId1);
//        assertNotNull(fromDb);
//        assertEquals(3, fromDb.getCharge());
//        assertEquals(999, fromDb.getScanId());
//        assertEquals("PEPTIDE", fromDb.getResultPeptide().getPeptideSequence());
//        assertEquals(12, fromDb.getRunSearchId());
//        assertEquals(resultId1, fromDb.getId());
//        assertEquals(0, fromDb.getResultPeptide().getPostResidue());
//        assertEquals(0, fromDb.getResultPeptide().getPreResidue());
//        assertEquals(21, fromDb.getRunSearchAnalysisId());
//        assertEquals(0.25, fromDb.getPosteriorErrorProbability());
//        assertEquals(0.01, fromDb.getQvalue());
//        assertNull(fromDb.getDiscriminantScore());
//        
//        fromDb = percResDao.load(resultId2);
//        assertNotNull(fromDb);
//        assertEquals(2, fromDb.getCharge());
//        assertEquals(1999, fromDb.getScanId());
//        assertEquals("EDITPEP", fromDb.getResultPeptide().getPeptideSequence());
//        assertEquals(13, fromDb.getRunSearchId());
//        assertEquals(resultId2, fromDb.getId());
//        assertEquals(0, fromDb.getResultPeptide().getPostResidue());
//        assertEquals(0, fromDb.getResultPeptide().getPreResidue());
//        assertEquals(21, fromDb.getRunSearchAnalysisId());
//        assertEquals(-1.0, fromDb.getPosteriorErrorProbability());
//        assertEquals(0.05, fromDb.getQvalue());
//        assertEquals(0.50, fromDb.getDiscriminantScore());
    }


    public final void testLoadResultIdsForRunSearch() {
        
        // save a search
        MsSearchIn search = super.makeSearch(true, false, false, true);
        int searchId = searchDao.saveSearch(search, 2, 56);
        
        // save some of search results
        MsSearchResultIn result1 = super.makeSearchResult(searchId, 3, "PEPTIDE", new BigDecimal("1024.5"), false);
        int resultId1 = resultDao.save(searchId, result1, 12, 999); // runSearchId = 12
        
        MsSearchResultIn result2 = super.makeSearchResult(searchId, 2, "EDITPEP", new BigDecimal("1024.5"), false);
        int resultId2 = resultDao.save(searchId, result2, 13, 1999); // runSearchId = 13
        
        MsSearchResultIn result3 = super.makeSearchResult(searchId, 3, "PEPTIDE2", new BigDecimal("1024.5"), false);
        int resultId3 = resultDao.save(searchId, result3, 13, 2000); // runSearchId = 13
        
        // save percolator results for each search result
        PercolatorResultDataBean pres1 = new PercolatorResultDataBean();
        pres1.setSearchResultId(resultId1);
        pres1.setRunSearchAnalysisId(21);
        pres1.setQvalue(0.01);
        pres1.setPosteriorErrorProbability(0.25);
        percResDao.save(pres1);
        
        pres1 = new PercolatorResultDataBean();
        pres1.setSearchResultId(resultId2);
        pres1.setRunSearchAnalysisId(22);
        pres1.setQvalue(0.05);
        pres1.setDiscriminantScore(0.50);
        percResDao.save(pres1);
        
        pres1 = new PercolatorResultDataBean();
        pres1.setSearchResultId(resultId3);
        pres1.setRunSearchAnalysisId(22);
        pres1.setQvalue(0.03);
        pres1.setDiscriminantScore(0.30);
        percResDao.save(pres1);
        
        List<Integer> resultIds = percResDao.loadIdsForRunSearchAnalysis(22);
        assertEquals(2, resultIds.size());
        
        Collections.sort(resultIds);
        assertEquals(Integer.valueOf(resultId2), resultIds.get(0));
        assertEquals(Integer.valueOf(resultId3), resultIds.get(1));
    }

    
    public final void testLoadResultIdsForPrecolatorAnalysis() {
        
        // save a search
        MsSearchIn search = super.makeSearch(true, false, false, true);
        int searchId = searchDao.saveSearch(search, 2, 56);
        
        // save some of search results
        MsSearchResultIn result1 = super.makeSearchResult(searchId, 3, "PEPTIDE", new BigDecimal("1024.5"), false);
        int resultId1 = resultDao.save(searchId, result1, 12, 999); // runSearchId = 12; scanID=999
        
        MsSearchResultIn result2 = super.makeSearchResult(searchId, 2, "EDITPEP", new BigDecimal("1024.5"), false);
        int resultId2 = resultDao.save(searchId, result2, 13, 1999); // runSearchId = 13; scanID=1999
        
        MsSearchResultIn result3 = super.makeSearchResult(searchId, 3, "PEPTIDE2", new BigDecimal("1024.5"), false);
        int resultId3 = resultDao.save(searchId, result3, 13, 2000); // runSearchId = 13; scanID=2000
        
        
        // save the Percolator run search analyses for runSearchIds 12 and 13
        RunSearchAnalysisBean rsaBean = new RunSearchAnalysisBean();
        rsaBean.setAnalysisFileFormat(SearchFileFormat.SQT_PERC);
        rsaBean.setAnalysisId(21);
        rsaBean.setRunSearchId(12);
        int percRsId1 = rsaDao.save(rsaBean);
        
        rsaBean = new RunSearchAnalysisBean();
        rsaBean.setAnalysisFileFormat(SearchFileFormat.SQT_PERC);
        rsaBean.setAnalysisId(21);
        rsaBean.setRunSearchId(13);
        int percRsId2 = rsaDao.save(rsaBean);
        
        // save percolator results for each search result
        PercolatorResultDataBean pres1 = new PercolatorResultDataBean();
        pres1.setSearchResultId(resultId1); 
        pres1.setRunSearchAnalysisId(percRsId1); // resultId1 if for runSearchId 12, so use percRsId1
        pres1.setQvalue(0.01);
        pres1.setPosteriorErrorProbability(0.25);
        percResDao.save(pres1);
        
        pres1 = new PercolatorResultDataBean();
        pres1.setSearchResultId(resultId2);
        pres1.setRunSearchAnalysisId(percRsId2); // resultId2 if for runSearchId 13, so use percRsId2
        pres1.setQvalue(0.05);
        pres1.setDiscriminantScore(0.50);
        percResDao.save(pres1);
        
        pres1 = new PercolatorResultDataBean();
        pres1.setSearchResultId(resultId3);
        pres1.setRunSearchAnalysisId(percRsId2); // resultId3 if for runSearchId 13, so use percRsId2
        pres1.setQvalue(0.03);
        pres1.setDiscriminantScore(0.30);
        percResDao.save(pres1);
        
        List<Integer> resultIds = percResDao.loadIdsForAnalysis(21);
        assertEquals(3, resultIds.size());
        
        Collections.sort(resultIds);
        assertEquals(Integer.valueOf(resultId1), resultIds.get(0));
        assertEquals(Integer.valueOf(resultId2), resultIds.get(1));
        assertEquals(Integer.valueOf(resultId3), resultIds.get(2));
    }
    
    public final void testLoadResultsWithScoreThresholdForRunSearch() {
        
        List<PercolatorResultDataWId> dataList = new ArrayList<PercolatorResultDataWId>(3);
        for(int i = 1; i < 4; i++) {
            
            MsSearchResultIn result = super.makeSearchResult(1, i, "PEPTIDE", new BigDecimal("1024.5"), false); // searchID = 1; charge = i
            int resultId = resultDao.save(1, result, 12, 111*i); // searchID = 1; // runSearchID = 12; scanID=111*i
            
            PercolatorResultDataBean data = new PercolatorResultDataBean();
            data.setSearchResultId(resultId);
            data.setRunSearchAnalysisId(21);
            data.setPosteriorErrorProbability(0.1*i);
            data.setQvalue(0.01*i);
            dataList.add(data);
        }
        percResDao.saveAllPercolatorResultData(dataList);
        
        List<PercolatorResult> resultList = percResDao.loadTopPercolatorResultsN(21, 0.05, null, null, false);
        assertEquals(3, resultList.size());
        
        resultList = percResDao.loadTopPercolatorResultsN(21, 0.05, 0.25, null, false);
        assertEquals(2, resultList.size());
        
        resultList = percResDao.loadTopPercolatorResultsN(12, 0.05, null, 0.5, false);
        assertEquals(0, resultList.size());
    }
}
