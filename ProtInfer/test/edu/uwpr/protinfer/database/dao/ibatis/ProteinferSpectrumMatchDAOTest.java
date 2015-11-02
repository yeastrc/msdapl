package edu.uwpr.protinfer.database.dao.ibatis;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import junit.framework.TestCase;
import edu.uwpr.protinfer.database.dao.ProteinferDAOFactory;
import edu.uwpr.protinfer.database.dao.ProteinferDAOTestSuite;
import edu.uwpr.protinfer.database.dto.ProteinferSpectrumMatch;

public class ProteinferSpectrumMatchDAOTest extends TestCase {

    private final ProteinferDAOFactory factory = ProteinferDAOFactory.testInstance();
    private final ProteinferSpectrumMatchDAO psmDao = factory.getProteinferSpectrumMatchDao();
    
    protected void setUp() throws Exception {
        super.setUp();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }
    
    public final void testSaveSpectrumMatch() {
        ProteinferDAOTestSuite.resetDatabase();
        int id = psmDao.saveSpectrumMatch(createProteinferSpectrumMatch(21, 124, 34));
        assertEquals(1, id);
        
        id = psmDao.saveSpectrumMatch(createProteinferSpectrumMatch(22, 124, 35));
        assertEquals(2, id);
        
        id = psmDao.saveSpectrumMatch(createProteinferSpectrumMatch(23, 124, 36));
        assertEquals(3, id);
        
        ProteinferSpectrumMatch psm = psmDao.loadSpectrumMatch(1);
        assertEquals(1, psm.getId());
        assertEquals(21, psm.getMsRunSearchResultId());
        assertEquals(124, psm.getProteinferIonId());
        assertEquals(34, psm.getRank());
        
        psm = psmDao.loadSpectrumMatch(2);
        assertEquals(2, psm.getId());
        assertEquals(22, psm.getMsRunSearchResultId());
        assertEquals(124, psm.getProteinferIonId());
        assertEquals(35, psm.getRank());
        
        psm = psmDao.loadSpectrumMatch(3);
        assertEquals(3, psm.getId());
        assertEquals(23, psm.getMsRunSearchResultId());
        assertEquals(124, psm.getProteinferIonId());
        assertEquals(36, psm.getRank());
    }
    
    public final void testGetSpectrumMatchesForIon() {
        List<ProteinferSpectrumMatch> psmList = psmDao.loadSpectrumMatchesForIon(124);
        assertEquals(3, psmList.size());
        Collections.sort(psmList, new Comparator<ProteinferSpectrumMatch> () {
            @Override
            public int compare(ProteinferSpectrumMatch o1,
                    ProteinferSpectrumMatch o2) {
                return Integer.valueOf(o1.getId()).compareTo(o2.getId());
            }});
        int i = 0;
        for(ProteinferSpectrumMatch psm: psmList) {
            assertEquals(21+i, psm.getMsRunSearchResultId());
            assertEquals(124, psm.getProteinferIonId());
            assertEquals(34+i, psm.getRank());
            i++;
        }
    }
    
    public static final ProteinferSpectrumMatch createProteinferSpectrumMatch(int runSearchResultId, int pinferIonId, int rank) {
        ProteinferSpectrumMatch psm = new ProteinferSpectrumMatch();
        psm.setMsRunSearchResultId(runSearchResultId);
        psm.setProteinferIonId(pinferIonId);
        psm.setRank(rank);
        return psm;
    }
}
