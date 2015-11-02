package edu.uwpr.protinfer.database.dao.idpicker.ibatis;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import junit.framework.TestCase;
import edu.uwpr.protinfer.database.dao.ProteinferDAOFactory;
import edu.uwpr.protinfer.database.dao.ProteinferDAOTestSuite;
import edu.uwpr.protinfer.database.dto.ProteinferSpectrumMatch;
import edu.uwpr.protinfer.database.dto.idpicker.IdPickerPeptide;
import edu.uwpr.protinfer.database.dto.idpicker.IdPickerSpectrumMatch;

public class IdPickerPeptideDAOTest extends TestCase {

    private static final ProteinferDAOFactory factory = ProteinferDAOFactory.testInstance();
    private static final IdPickerPeptideDAO peptDao = factory.getIdPickerPeptideDao();
    
    protected void setUp() throws Exception {
        super.setUp();
        ProteinferDAOTestSuite.resetDatabase();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public final void testSaveIdPickerPeptide() {
        savePeptides();
    }
    
    private final void savePeptides() {
        int id = peptDao.saveIdPickerPeptide(createIdPickerPeptide(456, 654, 3));
        assertEquals(1, id);
        
        id = peptDao.saveIdPickerPeptide(createIdPickerPeptide(456, 654, 4));
        assertEquals(2, id);
        
        id = peptDao.saveIdPickerPeptide(createIdPickerPeptide(456, 654, 2));
        assertEquals(3, id);
    }

    public final void testGetPeptide() {
        
        savePeptides();
        
        IdPickerPeptide peptide = peptDao.load(2);
//        assertEquals(456, peptide.getProteinferId());
        assertEquals(654, peptide.getGroupId());
        assertEquals(2, peptide.getId());
        assertEquals(4, peptide.getSpectralCount());
        assertEquals(4, peptide.getSpectrumMatchList().size());
        
        ProteinferSpectrumMatch bestPsm = peptide.getBestSpectrumMatch();
//        assertEquals(1, bestPsm.getRank());
        
        List<IdPickerSpectrumMatch> psmList = peptide.getSpectrumMatchList();
        assertTrue(spectrumMatchesCorrect(2, psmList));
    }
    
//    public final void testGetPeptideIdsForProteinferRun() {
//        
//        savePeptides();
//        
//        List<Integer> peptList = peptDao.getPeptideIdsForProteinferRun(456);
//        assertEquals(3, peptList.size());
//    }
//    
//    public final void testGetPeptidesForProteinferRun() {
//        
//        savePeptides();
//        
//        List<IdPickerPeptide> peptList = peptDao.getPeptidesForProteinferRun(456);
//        assertEquals(3, peptList.size());
//        Collections.sort(peptList, new Comparator<IdPickerPeptide> (){
//            public int compare(IdPickerPeptide o1, IdPickerPeptide o2) {
//                return Integer.valueOf(o1.getId()).compareTo(o2.getId());
//            }});
//        assertEquals(3, peptList.get(0).getSpectralCount());
//        spectrumMatchesCorrect(1, peptList.get(0).getSpectrumMatchList());
//        assertEquals(4, peptList.get(1).getSpectralCount());
//        spectrumMatchesCorrect(2, peptList.get(1).getSpectrumMatchList());
//        assertEquals(2, peptList.get(2).getSpectralCount());
//        spectrumMatchesCorrect(3, peptList.get(2).getSpectrumMatchList());
//    }
    
//    public final void testGetIdPickerGroupPeptides() {
//        
//        savePeptides();
//        
//        List<IdPickerPeptide> peptList = peptDao.getIdPickerGroupPeptides(456, 654);
//        assertEquals(3, peptList.size());
//        Collections.sort(peptList, new Comparator<IdPickerPeptide> (){
//            public int compare(IdPickerPeptide o1, IdPickerPeptide o2) {
//                return Integer.valueOf(o1.getId()).compareTo(o2.getId());
//            }});
//        assertEquals(3, peptList.get(0).getSpectralCount());
//        spectrumMatchesCorrect(1, peptList.get(0).getSpectrumMatchList());
//        assertEquals(4, peptList.get(1).getSpectralCount());
//        spectrumMatchesCorrect(2, peptList.get(1).getSpectrumMatchList());
//        assertEquals(2, peptList.get(2).getSpectralCount());
//        spectrumMatchesCorrect(3, peptList.get(2).getSpectrumMatchList());
//    }

    public final void testGetIdPickerPeptideGroup() {
        fail("Not yet implemented"); // TODO
    }
    
    public final void testGetPeptidesForProteinferProtein() {
        fail("Not yet implemented"); // TODO
    }

    public final void testGetPeptideIdsForProteinferProtein() {
        fail("Not yet implemented"); // TODO
    }

    

    private static boolean spectrumMatchesCorrect(int pinferProteinId, List<IdPickerSpectrumMatch> psmList) {
        Collections.sort(psmList, new Comparator<IdPickerSpectrumMatch>() {
            public int compare(IdPickerSpectrumMatch o1,
                    IdPickerSpectrumMatch o2) {
                return Double.valueOf(o1.getFdr()).compareTo(o2.getFdr());
            }});
        
        int i = 1;
        for(IdPickerSpectrumMatch psm: psmList) {
            int runSearchResultId = 22 + i;
//            int rank = 0 + i;
            double fdr = (double)i/10.0;
            assertEquals(runSearchResultId, psm.getMsRunSearchResultId());
//            assertEquals(rank, psm.getRank());
            assertEquals(pinferProteinId, psm.getProteinferIonId());
            assertEquals(fdr, psm.getFdr());
            i++;
        }
        return true;
    }
    
    public static final IdPickerPeptide createIdPickerPeptide(int pinferId, int groupId, int numPsm) {
        IdPickerPeptide peptide = new IdPickerPeptide();
//        peptide.setProteinferId(pinferId);
        peptide.setGroupId(groupId);
        peptide.setSequence("PEPTIDE");
        List<IdPickerSpectrumMatch> psmList = new ArrayList<IdPickerSpectrumMatch>();
        for (int i = 1; i <= numPsm; i++) {
            int runSearchResultId = 22 + i;
            int rank = 0 + i;
            double fdr = (double)i/10.0;
            IdPickerSpectrumMatch psm1 = IdPickerSpectrumMatchDAOTest.createIdPickerSpectrumMatch(runSearchResultId,0,rank,fdr);
            psmList.add(psm1);
        }
        
        peptide.setSpectrumMatchList(psmList);
        
        return peptide;
    }
}
