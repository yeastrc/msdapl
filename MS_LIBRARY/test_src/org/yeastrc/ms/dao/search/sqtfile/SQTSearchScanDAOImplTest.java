package org.yeastrc.ms.dao.search.sqtfile;

import java.math.BigDecimal;
import java.util.List;

import org.yeastrc.ms.domain.search.sqtfile.SQTSearchScan;
import org.yeastrc.ms.domain.search.sqtfile.SQTSearchScanIn;
import org.yeastrc.ms.domain.search.sqtfile.impl.SQTSearchScanWrap;

public class SQTSearchScanDAOImplTest extends SQTBaseDAOTestCase {

    protected void setUp() throws Exception {
        super.setUp();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void testOperationsOnSQTSpectrumData() {
        
        assertNull(sqtSpectrumDao.load(34, 1, 1, new BigDecimal("0"))); // runSearchId = 34; scanId = 1; charge = 1
        
        SQTSearchScanIn data = makeSearchScan(3, 0); // charge = 3; processtime = 0
        
        try {
            sqtSpectrumDao.save(new SQTSearchScanWrap(data, 0, 1)); // runSearchId = 0; scanId = 1;
            fail("Cannot save search scan with runSearchId of 0");
        }
        catch (RuntimeException e) {System.out.println("RuntimeException");}
        
        try {
            sqtSpectrumDao.save(new SQTSearchScanWrap(data, 1, 0)); // runSearchId = 1; scanId = 0;
            fail("Cannot save search scan with scanId of 0");
        }
        catch (RuntimeException e) {System.out.println("RuntimeException");}
        
        data = makeSearchScan(0,0); // charge = 0; processtime = 0
        try {
            sqtSpectrumDao.save(new SQTSearchScanWrap(data, 1, 1)); // runearchId = 1; scanId = 1;
            fail("Cannot save search scan with charge of 0");
        }
        catch (RuntimeException e) {System.out.println("RuntimeException");}
        
        data = makeSearchScan(3,0); // charge = 3; processtime = 0
        
        sqtSpectrumDao.save(new SQTSearchScanWrap(data, 1024, 4201)); // runSearchId = 1024; scanId = 4201
        
        SQTSearchScan data_db = sqtSpectrumDao.load(1024, 4201, 3, data.getObservedMass());
        assertNotNull(data_db);
        
        assertEquals(1024, data_db.getRunSearchId());
        assertEquals(4201, data_db.getScanId());
        assertEquals(null, data_db.getLowestSp());
        checkSearchScan(data, data_db);
        
        sqtSpectrumDao.deleteForRunSearch(1024);
        data_db = sqtSpectrumDao.load(1024, 4201, 3, data.getObservedMass());
        assertNull(data_db);
    }
    
    private void checkSearchScan(SQTSearchScanIn input, SQTSearchScan output) {
        assertEquals(input.getCharge(), output.getCharge());
        assertEquals(input.getLowestSp(), output.getLowestSp());
        assertEquals(input.getProcessTime(), output.getProcessTime());
        assertEquals(input.getServerName(), output.getServerName());
        assertEquals(input.getTotalIntensity().doubleValue(), output.getTotalIntensity().doubleValue());
        assertEquals(input.getSequenceMatches(), output.getSequenceMatches());
//        assertEquals(input.getObservedMass().doubleValue(), output.getObservedMass().doubleValue());
    }
    
    private SQTSearchScanIn makeSearchScan(final int charge, final int processTime) {
        SQTSearchScanIn scan = new SQTSearchScanIn() {

            public int getCharge() {
                return charge;
            }

            public BigDecimal getLowestSp() {
                return null;
            }

            public int getProcessTime() {
                return processTime;
            }

            public String getServerName() {
                return "pumice.gs.washington.edu";
            }

            public Double getTotalIntensity() {
                return new Double("12345.12345");
            }

            @Override
            public int getScanNumber() {
                // TODO Auto-generated method stub
                return 0;
            }

            @Override
            public int getSequenceMatches() {
                return 0;
            }

            @Override
            public BigDecimal getObservedMass() {
                return new BigDecimal("1124.08");
            }

            @Override
            public List getScanResults() {
                throw new UnsupportedOperationException();
            }};
            return scan;
    }
}
