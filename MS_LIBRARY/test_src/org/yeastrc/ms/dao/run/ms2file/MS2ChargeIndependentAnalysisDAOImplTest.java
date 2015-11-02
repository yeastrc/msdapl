package org.yeastrc.ms.dao.run.ms2file;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.yeastrc.ms.domain.run.ms2file.MS2NameValuePair;


public class MS2ChargeIndependentAnalysisDAOImplTest extends MS2BaseDAOtestCase {

    protected void setUp() throws Exception {
        super.setUp();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void testOperationsOnMS2FileChargeIndependentAnalysis() {
        
        // nothing in the database right now
        assertEquals(0, iAnalDao.loadAnalysisForScan(1).size());
        
        // save something
        MS2NameValuePair da11 = makeAnalysis("name_11", "value_11");
        MS2NameValuePair da12 = makeAnalysis("name_12", "value_12");
        MS2NameValuePair da21 = makeAnalysis("name_21", "value_21");
        MS2NameValuePair da31 = makeAnalysis("name_31", "value_31");
        
        iAnalDao.save(da11, 1);
        iAnalDao.save(da12, 1);
        iAnalDao.save(da21, 2);
        iAnalDao.save(da31, 3);
        
        // check saved entries
        assertEquals(2, iAnalDao.loadAnalysisForScan(1).size());
        List<MS2NameValuePair> daList = iAnalDao.loadAnalysisForScan(1);
        // sort so that we get the entries in the order we inserted them
        Collections.sort(daList, new Comparator<MS2NameValuePair>() {
            public int compare(MS2NameValuePair o1,
                    MS2NameValuePair o2) {
                return o1.getName().compareTo(o2.getName());
            }});
        compare(da11, daList.get(0));
        compare(da12, daList.get(1));
        
        assertEquals(1, iAnalDao.loadAnalysisForScan(2).size());
        compare(da21, iAnalDao.loadAnalysisForScan(2).get(0));
        
        assertEquals(1, iAnalDao.loadAnalysisForScan(3).size());
        compare(da31, iAnalDao.loadAnalysisForScan(3).get(0));
        
        // delete
        iAnalDao.deleteByScanId(1);
        iAnalDao.deleteByScanId(2);
        iAnalDao.deleteByScanId(3);
        
        // really deleted everything?
        assertEquals(0, iAnalDao.loadAnalysisForScan(1).size());
        assertEquals(0, iAnalDao.loadAnalysisForScan(2).size());
        assertEquals(0, iAnalDao.loadAnalysisForScan(3).size());
    }
    
}
