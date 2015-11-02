package org.yeastrc.ms.service.ms2file;

import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import org.yeastrc.ms.dao.BaseDAOTestCase;
import org.yeastrc.ms.dao.DAOFactory;
import org.yeastrc.ms.dao.run.ms2file.MS2HeaderDAO;
import org.yeastrc.ms.dao.run.ms2file.MS2RunDAO;
import org.yeastrc.ms.dao.run.ms2file.MS2ScanDAO;
import org.yeastrc.ms.domain.general.MsExperiment;
import org.yeastrc.ms.domain.run.DataConversionType;
import org.yeastrc.ms.domain.run.MsRunLocation;
import org.yeastrc.ms.domain.run.RunFileFormat;
import org.yeastrc.ms.domain.run.ms2file.MS2NameValuePair;
import org.yeastrc.ms.domain.run.ms2file.MS2Run;
import org.yeastrc.ms.domain.run.ms2file.MS2Scan;
import org.yeastrc.ms.service.MsDataUploader;
import org.yeastrc.ms.util.Sha1SumCalculator;

public class MS2DataUploadServiceTest extends BaseDAOTestCase {

    protected void setUp() throws Exception {
        super.setUp();
        resetDatabase();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void testUploadRuns1() {
        String dir = "test_resources/validSequestData_dir";
        
        // -------------------------------------------------------------------------------------------
        // UPLOAD 1
        MsDataUploader uploader = new MsDataUploader();
        int experimentId1 = 0;
        uploader.setSpectrumDataDirectory(dir);
        uploader.setSearchDirectory(dir);
        uploader.setSearchDate(new Date());
        uploader.setRemoteServer("remoteServer");
        uploader.setRemoteSpectrumDataDirectory("remote/directory");
        uploader.uploadData();
        experimentId1 = uploader.getUploadedExperimentId();
        assertEquals(0, uploader.getUploadExceptionList().size());
        
        
        assertTrue(experimentId1 > 0);
        List<Integer> runIds = runDao.loadRunIdsForFileName("1");
        assertEquals(1, runIds.size());
        int runId1 = runIds.get(0);
        assertNotSame(0, runId1);

        runIds = runDao.loadRunIdsForFileName("2");
        assertEquals(1, runIds.size());
        int runId2 = runIds.get(0);
        assertNotSame(0, runId2);
        
        // make sure there in an entry in the msExperimentRun table for the two runs;
//        assertEquals(1, exptDao.getMatchingExptRunCount(experimentId1, runId1));
//        assertEquals(1, exptDao.getMatchingExptRunCount(experimentId1, runId2));
        
        // make sure there is only one run location for runId1
        // check values from msRunLocation table
        List<MsRunLocation> locs = runDao.loadLocationsForRun(runId1);
        assertEquals(1, locs.size());
        assertEquals("remote/directory", locs.get(0).getServerDirectory());
        assertEquals(runId1, locs.get(0).getRunId());
        
        locs = runDao.loadLocationsForRun(runId2);
        assertEquals(1, locs.size());
        assertEquals("remote/directory", locs.get(0).getServerDirectory());
        assertEquals(runId2, locs.get(0).getRunId());
        
        // -------------------------------------------------------------------------------------------
        // UPLOAD 2
        // upload with different values for serverAddress and serverDirectory
        uploader = new MsDataUploader();
        int experimentId2 = 0;
        uploader.setSpectrumDataDirectory(dir);
        uploader.setSearchDirectory(dir);
        uploader.setSearchDate(new Date());
        uploader.setRemoteServer("remote.server.again");
        uploader.setRemoteSpectrumDataDirectory("remote/directory/2");
        uploader.uploadData();
        experimentId2 = uploader.getUploadedExperimentId();
        assertNotSame(0, experimentId2);
        assertEquals(0, uploader.getUploadExceptionList().size());
        
        assertTrue(experimentId2 > 0);
        // make sure there in an entry in the msExperimentRun table for the two runs;
//        assertEquals(1, exptDao.getMatchingExptRunCount(experimentId2, runId1));
//        assertEquals(1, exptDao.getMatchingExptRunCount(experimentId2, runId2));
        
        // the runs will not be uploaded again but we should have two locations for the each run
        locs = runDao.loadLocationsForRun(runId1);
        Collections.sort(locs, new Comparator<MsRunLocation>(){
            public int compare(MsRunLocation o1, MsRunLocation o2) {
                return Integer.valueOf(o1.getId()).compareTo(Integer.valueOf(o2.getId()));
            }});
        assertEquals(2, locs.size());
        assertEquals("remote/directory", locs.get(0).getServerDirectory());
        assertEquals(runId1, locs.get(0).getRunId());
        assertEquals("remote/directory/2", locs.get(1).getServerDirectory());
        assertEquals(runId1, locs.get(1).getRunId());
        
        locs = runDao.loadLocationsForRun(runId2);
        Collections.sort(locs, new Comparator<MsRunLocation>(){
            public int compare(MsRunLocation o1, MsRunLocation o2) {
                return Integer.valueOf(o1.getId()).compareTo(Integer.valueOf(o2.getId()));
            }});
        assertEquals(2, locs.size());
        assertEquals("remote/directory", locs.get(0).getServerDirectory());
        assertEquals(runId2, locs.get(0).getRunId());
        assertEquals("remote/directory/2", locs.get(1).getServerDirectory());
        assertEquals(runId2, locs.get(1).getRunId());
        
        
        // -------------------------------------------------------------------------------------------
        // UPLOAD 3
        // upload again but this time use the same values for serverAddress and serverDirectory
        // as the first upload
        uploader = new MsDataUploader();
        int experimentId3 = 0;
        uploader.setSpectrumDataDirectory(dir);
        uploader.setSearchDirectory(dir);
        uploader.setSearchDate(new Date());
        uploader.setRemoteServer("remote.server");
        uploader.setRemoteSpectrumDataDirectory("remote/directory");
        uploader.uploadData();
        experimentId3 = uploader.getUploadedExperimentId();
        assertNotSame(0, experimentId3);
        assertEquals(0, uploader.getUploadExceptionList().size());
        
        assertTrue(experimentId3 > 0);
        // make sure there in an entry in the msExperimentRun table for the two runs;
//        assertEquals(1, exptDao.getMatchingExptRunCount(experimentId3, runId1));
//        assertEquals(1, exptDao.getMatchingExptRunCount(experimentId3, runId2));
        
        // no additional entries should be created 
        assertEquals(1, runDao.loadRunIdsForFileName("1").size());
        assertEquals(1, runDao.loadRunIdsForFileName("2").size());
        locs = runDao.loadLocationsForRun(runId1);
        assertEquals(2, locs.size());
        locs = runDao.loadLocationsForRun(runId2);
        assertEquals(2, locs.size());
        
        // -------------------------------------------------------------------------------------------
        //UPLOAD 4 upload again. This time use an existing experimentId
        try {
            Thread.currentThread().sleep(3*1000);
        }
        catch (InterruptedException e1) {
            e1.printStackTrace();
        }
        List<Integer> searchIds = searchDao.getSearchIdsForExperiment(experimentId2);
        int oldSearchId = searchIds.get(0);
        assertEquals(1, searchIds.size());
        assertEquals(2, runSearchDao.loadRunSearchIdsForSearch(oldSearchId).size());
        uploader = new MsDataUploader();
        uploader.setSpectrumDataDirectory(dir);
        uploader.setSearchDirectory(dir);
        uploader.setSearchDate(new Date());
        uploader.uploadData(experimentId2);
        
        // make sure the lastUpdate date is > the upload date
        MsExperiment expt = exptDao.loadExperiment(experimentId2);
        assertEquals("remote.server.again", expt.getServerAddress());
        assertEquals("remote/directory/2", expt.getServerDirectory());
        System.out.println(expt.getLastUpdateDate());
        System.out.println(expt.getUploadDate());
        assertTrue(expt.getLastUpdateDate().getTime() >  expt.getUploadDate().getTime());
        
        // the runs should not have been reloaded and we should not have any new entries
        // in the msExperimentRun table
//        assertEquals(1, exptDao.getMatchingExptRunCount(experimentId2, runId1));
//        assertEquals(1, exptDao.getMatchingExptRunCount(experimentId2, runId2));
        // NOTE(03/29/09) old searches are no longer deleted
        // NOTE (04/10/09) if a searchId exists the search is NOT uploaded again.
        // any old searches for the experiment should have been deleted; we should still 
        // have the same number of search as before
        searchIds = searchDao.getSearchIdsForExperiment(experimentId2);
        int newSearchId = searchIds.get(0);
        assertEquals(1, searchIds.size()); 
        assertTrue(oldSearchId == newSearchId);// see NOTE(03/29/09)
        assertEquals(2, runSearchDao.loadRunSearchIdsForSearch(newSearchId).size());
    }
    
    public void testUploadRuns2() {
        String dir = "test_resources/validSequestData_dir";
        MsDataUploader uploader = new MsDataUploader();
        
        uploader.setSpectrumDataDirectory(dir);
        uploader.setSearchDirectory(dir);
        uploader.setSearchDate(new Date());
        uploader.setRemoteServer("remoteServer");
        uploader.setRemoteSpectrumDataDirectory("remoteDirectory");
        uploader.uploadData();
        int experimentId1 = uploader.getUploadedExperimentId();
        
        assertEquals(0, uploader.getUploadExceptionList().size());
        
        // make sure all the data got uploaded
        try {
            checkRun(experimentId1, "1", Sha1SumCalculator.instance().sha1SumFor(new File(dir+File.separator+"1.ms2")));
            checkRun(experimentId1, "2", Sha1SumCalculator.instance().sha1SumFor(new File(dir+File.separator+"2.ms2")));
        }
        catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            fail("There should be no exception in sha1sum calculation");
        }
        catch (IOException e) {
            e.printStackTrace();
            fail("There should be no exception in sha1sum calculation");
        }
    }
    
    private int checkRun(int experimentId, String runFileName, String sha1sum) {
        int runId = runDao.loadRunIdForExperimentAndFileName(experimentId, runFileName);
        assertNotSame(0, runId);
        
        MS2RunDAO ms2runDao = DAOFactory.instance().getMS2FileRunDAO();
        MS2Run run = ms2runDao.loadRun(runId);
        assertNotNull(run);
        
        // check values from msRun table
        assertEquals(runFileName, run.getFileName());
        assertEquals(sha1sum, run.getSha1Sum());
        if (runFileName.equals("1")) {
            assertEquals("3/22/2005 9:46:00 AM", run.getCreationDate());
            assertEquals("MakeMS2", run.getConversionSW());
            assertEquals("1.0", run.getConversionSWVersion());
            assertEquals("MS2", run.getConversionSWOptions());
            assertNull(run.getInstrumentModel());
            assertNull(run.getInstrumentVendor());
            assertNull(run.getInstrumentSN());
            assertNull(run.getAcquisitionMethod());
            assertEquals(RunFileFormat.MS2, run.getRunFileFormat());
            assertEquals("MakeMS2 written by Michael J. MacCoss, 2004", run.getComment());
        }
        else { 
            assertEquals("12/20/2007 2:29:19 PM", run.getCreationDate());
            assertEquals("RAWXtract", run.getConversionSW());
            assertEquals("1.8", run.getConversionSWVersion());
            assertEquals("MS2", run.getConversionSWOptions());
            assertEquals("ITMS", run.getInstrumentModel());
            assertNull(run.getInstrumentVendor());
            assertNull(run.getInstrumentSN());
            assertEquals("Data-Dependent", run.getAcquisitionMethod());
            assertEquals(RunFileFormat.MS2, run.getRunFileFormat());
            assertEquals("RawXtract written by John Venable, 2003", run.getComment());
        }
        
        // check values from msRunEnzyme table
        assertEquals(0, enzymeDao.loadEnzymesForRun(runId).size());
        
        // check values from msRunLocation table
        List<MsRunLocation> locs = runDao.loadLocationsForRun(runId);
        assertEquals(1, locs.size());
        assertEquals("remoteDirectory", locs.get(0).getServerDirectory());
        assertEquals(runId, locs.get(0).getRunId());
        // testting the other method: should really be in test class for runDao
        int locs2 = runDao.loadMatchingRunLocations(runId, "remoteDirectory");
        assertEquals(1, locs2);
       
        
        // check values in MS2FileHeaders table
        MS2HeaderDAO headerDao = DAOFactory.instance().getMS2FileRunHeadersDAO();
        List<MS2NameValuePair> headers = headerDao.loadHeadersForRun(runId);
        
        if (runFileName.equals("1")) {
            assertEquals(5, headers.size());
            int i = 0;
            assertEquals("CreationDate", headers.get(i).getName());
            assertEquals("3/22/2005 9:46:00 AM", headers.get(i++).getValue());
            assertEquals("Extractor", headers.get(i).getName());
            assertEquals("MakeMS2", headers.get(i++).getValue());
            assertEquals("ExtractorVersion", headers.get(i).getName());
            assertEquals("1.0", headers.get(i++).getValue());
            assertEquals("Comments", headers.get(i).getName());
            assertEquals("MakeMS2 written by Michael J. MacCoss, 2004", headers.get(i++).getValue());
            assertEquals("ExtractorOptions", headers.get(i).getName());
            assertEquals("MS2", headers.get(i++).getValue());
        }
        else {
            assertEquals(13, headers.size());
            int i = 0;
            assertEquals("FilteringProgram", headers.get(i).getName());
            assertEquals("Parc", headers.get(i++).getValue());
            assertEquals("CreationDate", headers.get(i).getName());
            assertEquals("12/20/2007 2:29:19 PM", headers.get(i++).getValue());
            assertEquals("Extractor", headers.get(i).getName());
            assertEquals("RAWXtract", headers.get(i++).getValue());
            assertEquals("ExtractorVersion", headers.get(i).getName());
            assertEquals("1.8", headers.get(i++).getValue());
            assertEquals("Comments", headers.get(i).getName());
            assertEquals("RawXtract written by John Venable, 2003", headers.get(i++).getValue());
            assertEquals("ExtractorOptions", headers.get(i).getName());
            assertEquals("MS2", headers.get(i++).getValue());
            assertEquals("AcquisitionMethod", headers.get(i).getName());
            assertEquals("Data-Dependent", headers.get(i++).getValue());
            assertEquals("InstrumentType", headers.get(i).getName());
            assertEquals("ITMS", headers.get(i++).getValue());
            assertEquals("ScanType", headers.get(i).getName());
            assertEquals("MS2", headers.get(i++).getValue());
            assertEquals("DataType", headers.get(i).getName());
            assertEquals("Centroid", headers.get(i++).getValue());
            assertEquals("IsolationWindow", headers.get(i).getName());
            assertNull(headers.get(i++).getValue());
            assertEquals("FirstScan", headers.get(i).getName());
            assertEquals("1", headers.get(i++).getValue());            
            assertEquals("LastScan", headers.get(i).getName());
            assertEquals("17903", headers.get(i++).getValue());
        }
        
        // check values in msScan table and related tables (for each scan in the ms2 file)
        if (runFileName.equals("1")) checkScansFor_1ms2(runId);
        else checkScansFor_2ms2(runId);
        
        return runId;
    }
    
    private void checkScansFor_1ms2(int runId) {
        List<Integer> scanIds = scanDao.loadScanIdsForRun(runId);
        assertEquals(22, scanIds.size());
        Collections.sort(scanIds);
        MS2ScanDAO ms2scanDo = DAOFactory.instance().getMS2FileScanDAO();
        
        for (int id = 0; id < scanIds.size(); id++) {
            MS2Scan scan = ms2scanDo.load(scanIds.get(id));
            assertEquals(-1, scan.getPrecursorScanNum());
            assertEquals(0, scan.getPrecursorScanId());
            assertNull(scan.getFragmentationType());
            assertEquals(DataConversionType.UNKNOWN, scan.getDataConversionType());
            assertEquals(2, scan.getMsLevel());
            assertNull(scan.getRetentionTime());
            assertEquals(0, scan.getChargeIndependentAnalysisList().size());
        }
        int i = 0;
        MS2Scan scan = ms2scanDo.load(scanIds.get(i++));
        assertEquals(2, scan.getStartScanNum());
        assertEquals(2, scan.getEndScanNum());
        assertEquals(535.96, scan.getPrecursorMz().doubleValue());
        assertEquals(20, scan.getPeakCount());
        
        scan = ms2scanDo.load(scanIds.get(i++));
        assertEquals(3, scan.getStartScanNum());
        assertEquals(3, scan.getEndScanNum());
        assertEquals(447.03, scan.getPrecursorMz().doubleValue());
        assertEquals(19, scan.getPeakCount());
        
        scan = ms2scanDo.load(scanIds.get(i++));
        assertEquals(4, scan.getStartScanNum());
        assertEquals(4, scan.getEndScanNum());
        assertEquals(434.09, scan.getPrecursorMz().doubleValue());
        assertEquals(12, scan.getPeakCount());
        
        scan = ms2scanDo.load(scanIds.get(scanIds.size() - 2));
        assertEquals(26, scan.getStartScanNum());
        assertEquals(26, scan.getEndScanNum());
        assertEquals(441.4, scan.getPrecursorMz().doubleValue());
        assertEquals(15, scan.getPeakCount());
        
        scan = ms2scanDo.load(scanIds.get(scanIds.size() - 1));
        assertEquals(27, scan.getStartScanNum());
        assertEquals(27, scan.getEndScanNum());
        assertEquals(451.23, scan.getPrecursorMz().doubleValue());
        assertEquals(14, scan.getPeakCount());
        
    }
    
    private void checkScansFor_2ms2(int runId) {
        List<Integer> scanIds = scanDao.loadScanIdsForRun(runId);
        assertEquals(13, scanIds.size());
        Collections.sort(scanIds);
        MS2ScanDAO ms2scanDo = DAOFactory.instance().getMS2FileScanDAO();
        
        for (int id = 0; id < scanIds.size(); id++) {
            MS2Scan scan = ms2scanDo.load(scanIds.get(id));
            assertEquals(0, scan.getPrecursorScanId());
            assertEquals("CID", scan.getFragmentationType());
            assertEquals(DataConversionType.CENTROID, scan.getDataConversionType());
            assertEquals(2, scan.getMsLevel());
            assertNotNull(scan.getRetentionTime());
            assertNotSame(0, scan.getChargeIndependentAnalysisList().size());
        }
        int i = 0;
        MS2Scan scan = ms2scanDo.load(scanIds.get(i++));
        assertEquals(2, scan.getStartScanNum());
        assertEquals(2, scan.getEndScanNum());
        assertEquals(1, scan.getPrecursorScanNum());
        assertEquals(0.01, scan.getRetentionTime().doubleValue());
        assertEquals(475.42, scan.getPrecursorMz().doubleValue());
        assertEquals(109, scan.getPeakCount());
        
        scan = ms2scanDo.load(scanIds.get(i++));
        assertEquals(9, scan.getStartScanNum());
        assertEquals(9, scan.getEndScanNum());
        assertEquals(7, scan.getPrecursorScanNum());
        assertEquals(0.03, scan.getRetentionTime().doubleValue());
        assertEquals(1372.55, scan.getPrecursorMz().doubleValue());
        assertEquals(357, scan.getPeakCount());
        
        scan = ms2scanDo.load(scanIds.get(i++));
        assertEquals(10, scan.getStartScanNum());
        assertEquals(10, scan.getEndScanNum());
        assertEquals(7, scan.getPrecursorScanNum());
        assertEquals(0.04, scan.getRetentionTime().doubleValue());
        assertEquals(717.62, scan.getPrecursorMz().doubleValue());
        assertEquals(293, scan.getPeakCount());
        
        scan = ms2scanDo.load(scanIds.get(scanIds.size() - 2));
        assertEquals(24, scan.getStartScanNum());
        assertEquals(24, scan.getEndScanNum());
        assertEquals(19, scan.getPrecursorScanNum());
        assertEquals(0.09, scan.getRetentionTime().doubleValue());
        assertEquals(1374.58, scan.getPrecursorMz().doubleValue());
        assertEquals(711, scan.getPeakCount());
        
        scan = ms2scanDo.load(scanIds.get(scanIds.size() - 1));
        assertEquals(26, scan.getStartScanNum());
        assertEquals(26, scan.getEndScanNum());
        assertEquals(25, scan.getPrecursorScanNum());
        assertEquals(0.1, scan.getRetentionTime().doubleValue());
        assertEquals(817.33, scan.getPrecursorMz().doubleValue());
        assertEquals(319, scan.getPeakCount());
    }
}
