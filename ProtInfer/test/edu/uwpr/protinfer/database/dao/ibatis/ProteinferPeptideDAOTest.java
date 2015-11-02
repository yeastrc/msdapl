package edu.uwpr.protinfer.database.dao.ibatis;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import junit.framework.TestCase;
import edu.uwpr.protinfer.PeptideDefinition;
import edu.uwpr.protinfer.database.dao.ProteinferDAOFactory;
import edu.uwpr.protinfer.database.dao.ProteinferDAOTestSuite;
import edu.uwpr.protinfer.database.dto.GenericProteinferPeptide;
import edu.uwpr.protinfer.database.dto.ProteinferIon;
import edu.uwpr.protinfer.database.dto.ProteinferPeptide;
import edu.uwpr.protinfer.database.dto.ProteinferSpectrumMatch;

public class ProteinferPeptideDAOTest extends TestCase {

    private static final ProteinferDAOFactory factory = ProteinferDAOFactory.testInstance();
    private static final ProteinferPeptideDAO peptDao = factory.getProteinferPeptideDao();
    private static final ProteinferIonDAO ionDao = factory.getProteinferIonDao();
    private static final ProteinferSpectrumMatchDAO psmDao = factory.getProteinferSpectrumMatchDao();
    
    protected void setUp() throws Exception {
        super.setUp();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public final void testSaveProteinferPeptide() {
        ProteinferDAOTestSuite.resetDatabase();
        
        ProteinferPeptide peptide1 = createProteinferPeptide(123, "PEPTIDE_A", true);
        int id1 = peptDao.save(peptide1);
        
        // save some ions for this peptide
        ProteinferIon ion11 = createProteinferIon(id1, 2, 1, "PEPTIDE_A*");
        int ionid1 = ionDao.save(ion11);
        ProteinferIon ion12 = createProteinferIon(id1, 2, 2, "P*EPTIDE_A");
        int ionid2 = ionDao.save(ion12);
        ProteinferIon ion13 = createProteinferIon(id1, 3, 1, "PEPTIDE_A*");
        int ionid3 = ionDao.save(ion13);
        ProteinferIon ion14 = createProteinferIon(id1, 3, 2, "P*EPTIDE_A");
        int ionid4 = ionDao.save(ion14);
        
        
        // save some spectra for each ion
        ProteinferSpectrumMatch psm111 = createPsm(ionid1, 1, 8);
        ProteinferSpectrumMatch psm112 = createPsm(ionid1, 2, 7);
        psmDao.saveSpectrumMatch(psm111);
        psmDao.saveSpectrumMatch(psm112);
        
        ProteinferSpectrumMatch psm121 = createPsm(ionid2, 3, 6);
        ProteinferSpectrumMatch psm122 = createPsm(ionid2, 4, 5);
        psmDao.saveSpectrumMatch(psm121);
        psmDao.saveSpectrumMatch(psm122);
        
        ProteinferSpectrumMatch psm131 = createPsm(ionid3, 5, 4);
        ProteinferSpectrumMatch psm132 = createPsm(ionid3, 6, 3);
        psmDao.saveSpectrumMatch(psm131);
        psmDao.saveSpectrumMatch(psm132);
        
        ProteinferSpectrumMatch psm141 = createPsm(ionid4, 7, 2);
        ProteinferSpectrumMatch psm142 = createPsm(ionid4, 8, 1);
        psmDao.saveSpectrumMatch(psm141);
        psmDao.saveSpectrumMatch(psm142);
        
        
        // load the peptide
        ProteinferPeptide peptdb = peptDao.load(id1);
        assertEquals(123, peptdb.getProteinferId());
        assertEquals("PEPTIDE_A", peptdb.getSequence());
        assertEquals(8, peptdb.getSpectrumCount());
        assertEquals(4, peptdb.getIonList().size());
        // Get the best spectrum match for the peptide
        ProteinferSpectrumMatch psm = peptdb.getBestSpectrumMatch();
        assertEquals(1, psm.getRank());
        assertEquals(ionid4, psm.getProteinferIonId());
        assertEquals(8, psm.getMsRunSearchResultId());
        
        // try various peptide definitions
        PeptideDefinition peptideDef = new PeptideDefinition();
        peptideDef.setUseCharge(false);
        peptideDef.setUseMods(false);
        assertEquals(1, peptdb.getNumDistinctPeptides(peptideDef));
        List<GenericProteinferPeptide<ProteinferSpectrumMatch, ProteinferIon>> peptides = peptdb.getDistinctPeptides(peptideDef);
        assertEquals(1, peptides.size());
        assertEquals(4, peptides.get(0).getIonList().size());
        psm = peptides.get(0).getBestSpectrumMatch();
        assertEquals(1, psm.getRank());
        assertEquals(ionid4, psm.getProteinferIonId());
        assertEquals(8, psm.getMsRunSearchResultId());
        
        
        peptideDef.setUseCharge(true);
        assertEquals(2, peptdb.getNumDistinctPeptides(peptideDef));
        peptides = peptdb.getDistinctPeptides(peptideDef);
        assertEquals(2, peptides.size());
        assertEquals(2, peptides.get(0).getIonList().size());
        assertEquals(2, peptides.get(1).getIonList().size());
        // sort by charge
        Collections.sort(peptides, new Comparator<GenericProteinferPeptide<ProteinferSpectrumMatch, ProteinferIon>>() {
            @Override
            public int compare(
                    GenericProteinferPeptide<ProteinferSpectrumMatch, ProteinferIon> o1,
                    GenericProteinferPeptide<ProteinferSpectrumMatch, ProteinferIon> o2) {
                return Integer.valueOf(o1.getIonList().get(0).getCharge()).compareTo(o2.getIonList().get(0).getCharge());
            }});
        assertEquals(4, peptides.get(0).getSpectrumCount());
        psm = peptides.get(0).getBestSpectrumMatch();
        assertEquals(5, psm.getRank());
        assertEquals(ionid2, psm.getProteinferIonId());
        assertEquals(4, psm.getMsRunSearchResultId());
        
        assertEquals(4, peptides.get(1).getSpectrumCount());
        psm = peptides.get(1).getBestSpectrumMatch();
        assertEquals(1, psm.getRank());
        assertEquals(ionid4, psm.getProteinferIonId());
        assertEquals(8, psm.getMsRunSearchResultId());
        
        
        peptideDef.setUseCharge(false);
        peptideDef.setUseMods(true);
        assertEquals(2, peptdb.getNumDistinctPeptides(peptideDef));
        
        peptideDef.setUseCharge(true);
        peptideDef.setUseMods(true);
        assertEquals(4, peptdb.getNumDistinctPeptides(peptideDef));
    }


    
//    public final void testGetPeptideIdsForProteinferRun() {
//        List<Integer> peptList = peptDao.getPeptideIdsForProteinferRun(456);
//        assertEquals(3, peptList.size());
//    }

//    public final void testGetPeptidesForProteinferRun() {
//        List<ProteinferPeptide> peptList = peptDao.getPeptidesForProteinferRun(456);
//        assertEquals(3, peptList.size());
//        Collections.sort(peptList, new Comparator<ProteinferPeptide> (){
//            public int compare(ProteinferPeptide o1, ProteinferPeptide o2) {
//                return Integer.valueOf(o1.getId()).compareTo(o2.getId());
//            }});
//        assertEquals(3, peptList.get(0).getSpectralCount());
//        spectrumMatchesCorrect(1, peptList.get(0).getSpectrumMatchList());
//        assertEquals(4, peptList.get(1).getSpectralCount());
//        spectrumMatchesCorrect(2, peptList.get(1).getSpectrumMatchList());
//        assertEquals(2, peptList.get(2).getSpectralCount());
//        spectrumMatchesCorrect(3, peptList.get(2).getSpectrumMatchList());
//        
//    }
//    
//    public final void testGetPeptideIdsForProteinferProtein() {
//        fail("Not yet implemented"); // TODO
//    }
//
//    public final void testGetPeptidesForProtein() {
//        fail("Not yet implemented"); // TODO
//    }
    
    private static boolean spectrumMatchesCorrect(int pinferProteinId, List<ProteinferSpectrumMatch> psmList) {
        Collections.sort(psmList, new Comparator<ProteinferSpectrumMatch>() {
            public int compare(ProteinferSpectrumMatch o1,
                    ProteinferSpectrumMatch o2) {
                return Integer.valueOf(o1.getMsRunSearchResultId()).compareTo(o2.getMsRunSearchResultId());
            }});
        
        int i = 1;
        for(ProteinferSpectrumMatch psm: psmList) {
            int runSearchResultId = 22 + i;
            int rank = 0 + i;
            assertEquals(runSearchResultId, psm.getMsRunSearchResultId());
//            assertEquals(rank, psm.getRank());
            assertEquals(pinferProteinId, psm.getProteinferIonId());
            i++;
        }
        return true;
    }

    public static final ProteinferPeptide createProteinferPeptide(int pinferId, String peptideseq, boolean unique) {
        ProteinferPeptide peptide = new ProteinferPeptide();
        peptide.setProteinferId(pinferId);
        peptide.setSequence(peptideseq);
        peptide.setUniqueToProtein(unique);
        return peptide;
    }
    
    public static final ProteinferIon createProteinferIon(int peptideId, int charge, int modId, String sequence) {
        ProteinferIon ion = new ProteinferIon();
        ion.setCharge(charge);
        ion.setModificationStateId(modId);
        ion.setProteinferPeptideId(peptideId);
        ion.setSequence(sequence);
        return ion;
    }
    
    public static final ProteinferSpectrumMatch createPsm(int pinferIonId, int msRunSearchResultId, int rank) {
        ProteinferSpectrumMatch psm = new ProteinferSpectrumMatch();
        psm.setProteinferIonId(pinferIonId);
        psm.setMsRunSearchResultId(msRunSearchResultId);
        psm.setRank(rank);
        return psm;
    }
}
