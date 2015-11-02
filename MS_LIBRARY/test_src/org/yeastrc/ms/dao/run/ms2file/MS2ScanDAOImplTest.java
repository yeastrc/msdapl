package org.yeastrc.ms.dao.run.ms2file;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import org.yeastrc.ms.dao.run.MsScanDAOImplTest.MsScanTest;
import org.yeastrc.ms.domain.run.DataConversionType;
import org.yeastrc.ms.domain.run.ms2file.MS2NameValuePair;
import org.yeastrc.ms.domain.run.ms2file.MS2ScanIn;
import org.yeastrc.ms.domain.run.ms2file.MS2ScanCharge;
import org.yeastrc.ms.domain.run.ms2file.MS2Scan;


public class MS2ScanDAOImplTest extends MS2BaseDAOtestCase {

    private final int runId = 35;

    protected void setUp() throws Exception {
        super.setUp();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
        
        // clean up
        List<Integer> scanIds = scanDao.loadScanIdsForRun(runId);
        for (Integer id: scanIds)
            ms2ScanDao.delete(id);
        assertEquals(0, ms2ScanDao.loadScanIdsForRun(runId).size());
    }

    public void testOperationsOnMS2FileScan() {
        
        // there should not be anything in the database right now
        assertEquals(0, ms2ScanDao.loadScanIdsForRun(1).size());
        
        // put some scans in the database
        Random random = new Random();
        int[] scanIds = new int[10];
        for (int i = 0; i < 10; i++) {
            int scanNum = random.nextInt(100);
            MS2ScanIn scan = makeMS2Scan(scanNum, 0, DataConversionType.CENTROID, false, false); // precursorScanNum = 0;
            scanIds[i] = ms2ScanDao.save(scan, runId);
        }
        
        assertEquals(10, ms2ScanDao.loadScanIdsForRun(runId).size());

        // make sure we get the correct scanIds
        List<Integer> scanIdList = ms2ScanDao.loadScanIdsForRun(runId);
        Collections.sort(scanIdList);
        assertEquals(scanIds.length, scanIdList.size());
        for(int i = 0; i < 10; i++)
            assertEquals(scanIds[i], scanIdList.get(i).intValue());

        // get the scan for the first scan id and make sure it does NOT have any 
        // charge dependent analysis or scan charges associated with it
        MS2Scan scanDb = ms2ScanDao.load(scanIds[0]);
        assertNotNull(scanDb);
        assertEquals(0, scanDb.getChargeIndependentAnalysisList().size());
        assertEquals(0, scanDb.getScanChargeList().size());
        
        // save a scan WITH both charge independent analysis and scan charges
        MS2ScanIn scan = makeMS2Scan(420, 0, DataConversionType.CENTROID, true, true);// scanNum = 420; precursorScanNum = 0s
        int scanId = ms2ScanDao.save(scan, runId);
        MS2Scan scan_db = ms2ScanDao.load(scanId);
        assertEquals(DataConversionType.CENTROID, scan_db.getDataConversionType());
        assertEquals(3, scan_db.getChargeIndependentAnalysisList().size());
        assertEquals(2, scan_db.getScanChargeList().size());
        
        // delete the scan and make sure everything got deleted
        List<Integer> ids = scanDao.loadScanIdsForRun(runId);
        for (Integer id: ids)
            ms2ScanDao.delete(id);
        // make sure the 10 scans saved first are deleted
        for (int id: scanIds) {
            assertNull(ms2ScanDao.load(id));
        }
        // make sure last scan (WITH charge dependent and independent analysis) is deleted.
        assertNull(ms2ScanDao.load(scanId));
        assertEquals(0, chargeDao.loadScanChargesForScan(scanId).size());
        assertEquals(0, iAnalDao.loadAnalysisForScan(scanId).size());
        
    }
    
    public static final class MS2ScanTest extends MsScanTest implements MS2ScanIn {

        private List<MS2NameValuePair> analysisList = new ArrayList<MS2NameValuePair>();
        private List<MS2ScanCharge> scanChargeList = new ArrayList<MS2ScanCharge>();
        
        public List<MS2NameValuePair> getChargeIndependentAnalysisList() {
            return analysisList;
        }

        public List<MS2ScanCharge> getScanChargeList() {
            return scanChargeList;
        }

        public void addScanCharge(MS2ScanCharge scanCharge) {
            scanChargeList.add(scanCharge);
        }

        public void addChargeIndependentAnalysis(MS2NameValuePair analysis) {
            analysisList.add(analysis);
        }
    }
}
