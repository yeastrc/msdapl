package org.yeastrc.ms.dao.run;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.yeastrc.ms.dao.BaseDAOTestCase;
import org.yeastrc.ms.domain.run.DataConversionType;
import org.yeastrc.ms.domain.run.MsScan;
import org.yeastrc.ms.domain.run.MsScanIn;
import org.yeastrc.ms.domain.run.Peak;

public class MsScanDAOImplTest extends BaseDAOTestCase {


    protected void setUp() throws Exception {
        super.setUp();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void testSaveLoadDelete() {
        MsScanIn scan = makeMsScan(2, 1, DataConversionType.CENTROID); // scanNumber = 2; precursorScanNum = 1;
        int scanId = scanDao.save(scan, 99, 230); // runId = 99; precursorScanId = 230;
        MsScan scanDb = scanDao.load(scanId);
        checkScan(scan, scanDb);
        // clean up
        scanDao.delete(scanId);
        assertEquals(0, scanDao.loadScanIdsForRun(99).size());
        assertNull(scanDao.load(scanId));
    }


    public void testInvalidValues() {
        MsScanIn scan = makeMsScan(2, 1, DataConversionType.CENTROID); // scanNumber = 2; precursorScanNum = 1;
        try {
            scanDao.save(scan, 0, 1); // runId = 0; precursorScanId  1;
            fail("RunId cannot be 0");
        }
        catch(RuntimeException e){}
    }

    public void testSaveScanWithNoPrecursorScanId() {
        MsScanIn scan = makeMsScan(2, 1,DataConversionType.CENTROID); // scanNumber = 2; precursorScanNum = 1;
        int scanId = scanDao.save(scan, 99); // runID = 99
        MsScan scanDb = scanDao.load(scanId);
        checkScan(scan, scanDb);
        // clean up
        scanDao.delete(scanId);
        assertEquals(0, scanDao.loadScanIdsForRun(99).size());
        assertNull(scanDao.load(scanId));
    }

    public void testLoadScanIdsForRun() {
        int[] ids = new int[3];
        ids[0] = scanDao.save((makeMsScan(2, 1,DataConversionType.CENTROID)), 3); // runId = 3
        ids[1] = scanDao.save((makeMsScan(3, 1,DataConversionType.CENTROID)), 3);
        ids[2] = scanDao.save((makeMsScan(4, 1,DataConversionType.CENTROID)), 3);
        
        List<Integer> scanIdList = scanDao.loadScanIdsForRun(3);
        Collections.sort(scanIdList);
        assertEquals(3, scanIdList.size());
        for (int i = 0; i < ids.length; i++) {
            assertEquals(Integer.valueOf(ids[i]), scanIdList.get(i));
        }
        // clean up
        List<Integer> scanIds = scanDao.loadScanIdsForRun(3);
        for (Integer id: scanIds)
            scanDao.delete(id);
        assertEquals(0, scanDao.loadScanIdsForRun(3).size());
        for (int id: ids)
            assertNull(scanDao.load(id));
    }
    
    public void testSaveLoadPeakData() {
        MsScanIn scan = makeMsScanWithPeakData(2, 1,DataConversionType.CENTROID); // scanNumber = 2; precursorScanNum = 1;
        
        int scanId = scanDao.save(scan, 1, 1); // runId = 1; precursorScanId = 1;
        MsScan scanDb = scanDao.load(scanId);
        checkScan(scan, scanDb);
        // clean up
        scanDao.delete(scanId);
        assertEquals(0, scanDao.loadScanIdsForRun(1).size());
        assertNull(scanDao.load(scanId));
    }

    public void testDataConversionTypeForScan() {
        MsScanIn scan = makeMsScan(35, 53, null);
        try {
            scanDao.save(scan, 56);
        }
        catch(Exception e) {e.printStackTrace(); fail("DataConversionType can be null");}
        
        scan = makeMsScan(35, 53, DataConversionType.CENTROID);
        int id = scanDao.save(scan, 56);
        MsScan scan_db = scanDao.load(id);
        checkScan(scan, scan_db);
        
        scan = makeMsScan(36, 35, DataConversionType.NON_CENTROID);
        id = scanDao.save(scan, 56);
        scan_db = scanDao.load(id);
        checkScan(scan, scan_db);
        
        scan = makeMsScan(37, 35, DataConversionType.UNKNOWN);
        id = scanDao.save(scan, 56);
        scan_db = scanDao.load(id);
        checkScan(scan, scan_db);
        
        // clean up
        List<Integer> scanIds = scanDao.loadScanIdsForRun(56);
        for (Integer i: scanIds)
            scanDao.delete(i);
        assertEquals(0, scanDao.loadScanIdsForRun(56).size());
    }
    
    public void testPeakCount() {
        MsScanIn scan = makeMsScanWithPeakData(35, 53, DataConversionType.CENTROID);
        assertTrue(scan.getPeakCount() > 0);
        int scanId = scanDao.save(scan, 27);
        MsScan scan_db = scanDao.load(scanId);
        checkScan(scan, scan_db);
        assertEquals(scan.getPeakCount(), scan_db.getPeakCount());
        scanDao.delete(scanId);
        assertNull(scanDao.load(scanId));
    }
    
    public static class MsScanTest implements MsScanIn {

        private int startScanNum;
        private BigDecimal retentionTime;
        private int precursorScanNum;
        private BigDecimal precursorMz;
        private int msLevel;
        private String fragmentationType;
        private int endScanNum;
        private List<String[]> peakList = new ArrayList<String[]>();
        private DataConversionType convType;

        public int getEndScanNum() {
            return this.endScanNum;
        }

        public String getFragmentationType() {
            return this.fragmentationType;
        }

        public int getMsLevel() {
            return this.msLevel;
        }

        @Override
        public List<Peak> getPeaks() {
            List<Peak> peakNList = new ArrayList<Peak>(peakList.size());
            for(String[] peakArr: peakList) {
                double mz = Double.parseDouble(peakArr[0]);
                float intensity = Float.parseFloat(peakArr[1]);
                peakNList.add(new Peak(mz, intensity));
            }
            return peakNList;
        }

        @Override
        public List<String[]> getPeaksString() {
            return peakList;
        }
        
        public BigDecimal getPrecursorMz() {
            return this.precursorMz;
        }

        public int getPrecursorScanNum() {
            return this.precursorScanNum;
        }

        public BigDecimal getRetentionTime() {
            return this.retentionTime;
        }

        public int getStartScanNum() {
            return this.startScanNum;
        }

        public void setRetentionTime(BigDecimal retentionTime) {
            this.retentionTime = retentionTime;
        }

        public void setPrecursorScanNum(int precursorScanNum) {
            this.precursorScanNum = precursorScanNum;
        }

        public void setPrecursorMz(BigDecimal precursorMz) {
            this.precursorMz = precursorMz;
        }

        public void setPeaks(List<String[]> peaks) {
            this.peakList = peaks;
        }

        public int getPeakCount() {
            return peakList.size();
        }
        
        public void setMsLevel(int msLevel) {
            this.msLevel = msLevel;
        }

        public void setFragmentationType(String fragmentationType) {
            this.fragmentationType = fragmentationType;
        }

        public void setEndScanNum(int endScanNum) {
            this.endScanNum = endScanNum;
        }

        public void setStartScanNum(int scanNum) {
            this.startScanNum = scanNum;
        }

        @Override
        public DataConversionType getDataConversionType() {
            return convType;
        }
        
        public void setDataConversionType(DataConversionType convType) {
            this.convType = convType;
        }
    }
}
