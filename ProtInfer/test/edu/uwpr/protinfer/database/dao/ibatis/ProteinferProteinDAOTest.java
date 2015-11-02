package edu.uwpr.protinfer.database.dao.ibatis;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import junit.framework.TestCase;
import edu.uwpr.protinfer.database.dao.ProteinferDAOFactory;
import edu.uwpr.protinfer.database.dto.ProteinUserValidation;
import edu.uwpr.protinfer.database.dto.ProteinferPeptide;
import edu.uwpr.protinfer.database.dto.ProteinferProtein;

public class ProteinferProteinDAOTest extends TestCase {

    private static final ProteinferDAOFactory factory = ProteinferDAOFactory.testInstance();
    private static final ProteinferProteinDAO protDao = factory.getProteinferProteinDao();
    private static final ProteinferPeptideDAO peptDao = factory.getProteinferPeptideDao();
    
    protected void setUp() throws Exception {
        super.setUp();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public final void testSaveProteinferProtein1() {
        int id = protDao.saveProteinferProtein(createProteinferProtein(456, 123, 10.0, 2));
        assertEquals(1, id);
        
        id = protDao.saveProteinferProtein(createProteinferProtein(456, 124, 20.0, 3));
        assertEquals(2, id);
        
        id = protDao.saveProteinferProtein(createProteinferProtein(456, 125, 30.0, 4));
        assertEquals(3, id);
    }
    

    public final void testGetFilteredProteinCount() {
        assertEquals(3, protDao.getProteinCount(456));
    }
    
    public final void testGetProteinferProtein() {
        ProteinferProtein protein = protDao.loadProtein(2);
        assertEquals(2, protein.getId());
        assertEquals(456, protein.getProteinferId());
        assertEquals(124, protein.getNrseqProteinId());
        assertEquals(20.0, protein.getCoverage());
        assertEquals(3, protein.getPeptideCount());
        assertEquals(6, protein.getSpectralCount());
        assertNull(protein.getUserAnnotation());
        assertNull(protein.getUserValidation());
        
        List<ProteinferPeptide> peptList = protein.getPeptides();
        Collections.sort(peptList, new Comparator<ProteinferPeptide>() {
            public int compare(ProteinferPeptide o1, ProteinferPeptide o2) {
                return Integer.valueOf(o1.getId()).compareTo(o2.getId());
            }});
        
        assertEquals(3, peptList.size());
        int i = 1;
        for(ProteinferPeptide pept: peptList) {
//            assertEquals(456, pept.getProteinferId());
            assertEquals(i, pept.getSpectralCount());
            i++;
            assertEquals(1, pept.getMatchingProteinIds().size());
        }
    }
    
    public final void testSaveProteinferProtein2() {
        ProteinferProtein protein = new ProteinferProtein();
        protein.setCoverage(50.0);
        protein.setNrseqProteinId(66);
        protein.setProteinferId(789);
        protein.setUserAnnotation("Not Annotated");
        protein.setUserValidation(ProteinUserValidation.REJECTED);
        
        List<ProteinferPeptide> peptList = new ArrayList<ProteinferPeptide>(2);
        // add a new peptide
        ProteinferPeptide p1 = new ProteinferPeptide();
//        p1.setProteinferId(789);
        peptList.add(p1);
        
        // add an existing peptide
        ProteinferPeptide p2 = peptDao.load(3);
        protein.setPeptides(peptList);
        peptList.add(p2);
        
        int id = protDao.saveProteinferProtein(protein);
        assertEquals(4, id);
        
        p2 = peptDao.load(3);
        assertEquals(2, p2.getMatchingProteinIds().size());
        
        ProteinferProtein prot = protDao.loadProtein(4);
        List<ProteinferPeptide> plist = prot.getPeptides();
        assertEquals(2, plist.size());
        Collections.sort(plist, new Comparator<ProteinferPeptide>() {
            public int compare(ProteinferPeptide o1, ProteinferPeptide o2) {
                return Integer.valueOf(o1.getId()).compareTo(o2.getId());
            }});
        
        assertEquals(3, plist.get(0).getId());
    }
    
    public final void testUpdateUserAnnotation() {
        ProteinferProtein prot = protDao.loadProtein(2);
        assertNull(prot.getUserAnnotation());
        protDao.updateUserAnnotation(2, "Annotation 1");
        prot = protDao.loadProtein(2);
        assertEquals("Annotation 1", prot.getUserAnnotation());
        protDao.updateUserAnnotation(2, "Annotation 2");
        prot = protDao.loadProtein(2);
        assertEquals("Annotation 2", prot.getUserAnnotation());
    }

    public final void testUpdateUserValidation() {
        ProteinferProtein prot = protDao.loadProtein(3);
        assertNull(prot.getUserValidation());
        protDao.updateUserValidation(3, ProteinUserValidation.NOT_SURE);
        prot = protDao.loadProtein(3);
        assertEquals(ProteinUserValidation.NOT_SURE, prot.getUserValidation());
        protDao.updateUserValidation(3, ProteinUserValidation.ACCEPTED);
        prot = protDao.loadProtein(3);
        assertEquals(ProteinUserValidation.ACCEPTED, prot.getUserValidation());
    }
    
    public final void testGetProteinferProteinIds() {
        assertEquals(3, protDao.getProteinferProteinIds(456).size());
    }
    
    public final void testGetProteinferProteins() {
        assertEquals(3, protDao.loadProteins(456).size());
    }
    
    


    public static final ProteinferProtein createProteinferProtein(int pinferId, int nrseqId, double coverage, int numPept) {
        ProteinferProtein protein = new ProteinferProtein();
        protein.setProteinferId(pinferId);
        protein.setNrseqProteinId(nrseqId);
        protein.setCoverage(coverage);
        
        List<ProteinferPeptide> peptList = new ArrayList<ProteinferPeptide>(numPept);
        for(int i = 1; i <= numPept; i++) {
            ProteinferPeptide pept = ProteinferPeptideDAOTest.createProteinferPeptide(pinferId, i);
            peptList.add(pept);
        }
        protein.setPeptides(peptList);
        
        return protein;
    }
}
