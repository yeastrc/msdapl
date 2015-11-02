package org.yeastrc.ms.dao.run.ms2file;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.yeastrc.ms.domain.run.ms2file.MS2ScanCharge;

public class MS2ScanChargeDAOImplTest extends MS2BaseDAOtestCase {

    protected void setUp() throws Exception {
        super.setUp();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void testLoadAndSave() {
        
        // nothing in the database right now
        assertEquals(0, chargeDao.loadScanChargeIdsForScan(1).size());
        
        // put some data in (don't add any charge dependent data)
        MS2ScanCharge sc11 = makeMS2ScanCharge(1, "100.0", false); // charge = 1; mass = 100.0
        chargeDao.save(sc11, 1);
        
        // read it back
        List<MS2ScanCharge> sclist1 = chargeDao.loadScanChargesForScan(1);
        assertEquals(1, sclist1.size());
        
        // make sure NO charge dependent data was saved 
        assertEquals(0, sclist1.get(0).getChargeDependentAnalysisList().size());
        
        // put some data in (ADD charge dependent data)
        MS2ScanCharge sc12 = makeMS2ScanCharge(2, "200.0", true);
        chargeDao.save(sc12, 1);
        
        // read it back
        sclist1 = chargeDao.loadScanChargesForScan(1);
        assertEquals(2, sclist1.size());
        Collections.sort(sclist1, new Comparator<MS2ScanCharge>() {
            public int compare(MS2ScanCharge o1, MS2ScanCharge o2) {
                return new Integer(o1.getCharge()).compareTo(new Integer(o2.getCharge()));
            }});
        
        // make sure charge dependent data was saved (this will be for the second object in the list)
        assertEquals(2, sclist1.get(1).getChargeDependentAnalysisList().size());
        
        
        // compare values
        compare(sc11, sclist1.get(0));
        compare(sc12, sclist1.get(1));
        
        // delete everything
        chargeDao.deleteByScanId(1);
        assertEquals(0, chargeDao.loadScanChargeIdsForScan(1).size());
        
    }
    
    private void compare(MS2ScanCharge input, MS2ScanCharge output) {
        assertEquals(input.getCharge(), output.getCharge());
        assertEquals(input.getMass().doubleValue(), output.getMass().doubleValue());
        assertEquals(input.getChargeDependentAnalysisList().size(), output.getChargeDependentAnalysisList().size());
    }

    public void testNullValues() {
        MS2ScanCharge sc = makeMS2ScanCharge(1, null, false);
        try {
            chargeDao.save(sc, 0);
            fail("Should not be able to save with null scan id");
        }
        catch(RuntimeException e){}
        
        sc = makeMS2ScanCharge(0, null, false);
        try {
            chargeDao.save(sc, 1);
            fail("Should not be able to save with null charge");
        }
        catch(RuntimeException e){}
    }
    
    public void testDelete() {
        MS2ScanCharge sc = makeMS2ScanCharge(2, "200.0", true);
        int scanChargeId = chargeDao.save(sc, 1);
        assertEquals(2, dAnalDao.loadAnalysisForScanCharge(scanChargeId).size());
        
        // delete 
        chargeDao.deleteByScanId(1);
        assertEquals(0, chargeDao.loadScanChargeIdsForScan(1).size());
        
        // make sure the charge dependent analysis was deleted
        assertEquals(0, dAnalDao.loadAnalysisForScanCharge(scanChargeId).size());
    }
    
}
