package edu.uwpr.protinfer.database.dao.idpicker.ibatis;

import java.util.List;

import junit.framework.TestCase;
import edu.uwpr.protinfer.database.dao.ProteinferDAOFactory;
import edu.uwpr.protinfer.database.dao.ProteinferDAOTestSuite;
import edu.uwpr.protinfer.database.dto.idpicker.IdPickerSpectrumMatch;

public class IdPickerSpectrumMatchDAOTest extends TestCase {

    private final ProteinferDAOFactory factory = ProteinferDAOFactory.testInstance();
    private final IdPickerSpectrumMatchDAO idpPsmDao = factory.getIdPickerSpectrumMatchDao();
    
    protected void setUp() throws Exception {
        super.setUp();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public final void testSaveSpectrumMatch() {
        ProteinferDAOTestSuite.resetDatabase();
        
        IdPickerSpectrumMatch psm = createIdPickerSpectrumMatch(21, 123, 34, 0.25);
        int id = idpPsmDao.saveSpectrumMatch(psm);
        assertEquals(1, id);
        
        id = idpPsmDao.saveSpectrumMatch(createIdPickerSpectrumMatch(21, 123, 34, 0.25));
        assertEquals(2, id);
        
        id = idpPsmDao.saveSpectrumMatch(createIdPickerSpectrumMatch(21, 123, 34, 0.25));
        assertEquals(3, id);
    }

    public final void testGetSpectrumMatch() {
        IdPickerSpectrumMatch psm = idpPsmDao.loadSpectrumMatch(1); // we inserted this in the test above
        assertEquals(1, psm.getId());
        assertEquals(21, psm.getMsRunSearchResultId());
        assertEquals(123, psm.getProteinferIonId());
//        assertEquals(34, psm.getRank());
        assertEquals(0.25, psm.getFdr());
    }

    public final void testGetSpectrumMatchesForPeptide() {
        List<IdPickerSpectrumMatch> psmList = idpPsmDao.loadSpectrumMatchesForPeptide(123);
        assertEquals(3, psmList.size());
        for(IdPickerSpectrumMatch psm: psmList) {
            assertEquals(21, psm.getMsRunSearchResultId());
            assertEquals(123, psm.getProteinferIonId());
//            assertEquals(34, psm.getRank());
            assertEquals(0.25, psm.getFdr());
        }
    }
    
    public static final IdPickerSpectrumMatch createIdPickerSpectrumMatch(int runSearchResultId, int pinferPeptideId, int rank, double fdr) {
        IdPickerSpectrumMatch psm = new IdPickerSpectrumMatch();
        psm.setMsRunSearchResultId(runSearchResultId);
        psm.setProteinferIonId(pinferPeptideId);
//        psm.setRank(rank);
        psm.setFdr(fdr);
        return psm;
    }

}
