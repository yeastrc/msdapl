package edu.uwpr.protinfer.database.dao.idpicker.ibatis;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import junit.framework.TestCase;
import edu.uwpr.protinfer.database.dao.ProteinferDAOFactory;
import edu.uwpr.protinfer.database.dao.ProteinferDAOTestSuite;
import edu.uwpr.protinfer.database.dto.idpicker.IdPickerCluster;
import edu.uwpr.protinfer.database.dto.idpicker.IdPickerPeptide;
import edu.uwpr.protinfer.database.dto.idpicker.IdPickerProtein;
import edu.uwpr.protinfer.database.dto.idpicker.IdPickerProteinGroup;

public class IdPickerProteinDAOTest extends TestCase {

    private static final ProteinferDAOFactory factory = ProteinferDAOFactory.testInstance();
    private static final IdPickerProteinDAO protDao = factory.getIdPickerProteinDao();
    private static final IdPickerPeptideDAO peptDao = factory.getIdPickerPeptideDao();
    
    protected void setUp() throws Exception {
        super.setUp();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public final void testSaveIdPickerProtein() {
        ProteinferDAOTestSuite.resetDatabase();
        
        IdPickerProtein p1 = createIdPickerProtein(456, 123, 10.0, 12, 21, true, 2); // clusterID 12; groupID 21
        int id = protDao.saveIdPickerProtein(p1);
        assertEquals(1, id);
        for(IdPickerPeptide peptide: p1.getPeptides()) {
            int peptId = peptDao.saveIdPickerPeptide(peptide);
            protDao.saveProteinferProteinPeptideMatch(id, peptId);
            protDao.saveProteinPeptideGroupAssociation(456, p1.getGroupId(), peptide.getGroupId());
        }
        
        IdPickerProtein p2 = createIdPickerProtein(456, 124, 20.0, 12, 21, true, 3); // clusterID 12; groupID 21
        id = protDao.saveIdPickerProtein(p2);
        assertEquals(2, id);
        for(IdPickerPeptide peptide: p2.getPeptides()) {
            int peptId = peptDao.saveIdPickerPeptide(peptide);
            protDao.saveProteinferProteinPeptideMatch(id, peptId);
            protDao.saveProteinPeptideGroupAssociation(456, p2.getGroupId(), peptide.getGroupId());
        }
        
        IdPickerProtein p3 = createIdPickerProtein(456, 125, 30.0, 12, 22, false, 4); // clusterID 12; groupID 22
        id = protDao.saveIdPickerProtein(p3); 
        assertEquals(3, id);
        for(IdPickerPeptide peptide: p3.getPeptides()) {
            int peptId = peptDao.saveIdPickerPeptide(peptide);
            protDao.saveProteinferProteinPeptideMatch(id, peptId);
            protDao.saveProteinPeptideGroupAssociation(456, p3.getGroupId(), peptide.getGroupId());
        }
        
        IdPickerProtein p4 = createIdPickerProtein(456, 125, 30.0, 13, 23, true, 4);// clusterID 13; groupID 23
        id = protDao.saveIdPickerProtein(p4); 
        assertEquals(4, id);
        for(IdPickerPeptide peptide: p4.getPeptides()) {
            int peptId = peptDao.saveIdPickerPeptide(peptide);
            protDao.saveProteinferProteinPeptideMatch(id, peptId);
            protDao.saveProteinPeptideGroupAssociation(456, p4.getGroupId(), peptide.getGroupId());
        }
    }

    public final void testGetIdPickerClusterProteins() {
        assertEquals(3, protDao.loadIdPickerClusterProteins(456, 12).size());
        assertEquals(1, protDao.loadIdPickerClusterProteins(456, 13).size());
    }
    
    public final void testGetGroupProteins() {
        assertEquals(2, protDao.loadIdPickerGroupProteins(456, 21).size());
        assertEquals(1, protDao.loadIdPickerGroupProteins(456, 22).size());
        assertEquals(1, protDao.loadIdPickerGroupProteins(456, 23).size());
    }
    
    public final void testGetFilteredParsimoniousProteinCount() {
        assertEquals(3, protDao.getFilteredParsimoniousProteinCount(456));
    }
    
    public final void testGetProtein() {
        IdPickerProtein protein = protDao.loadProtein(2);
        assertEquals(2, protein.getId());
        assertEquals(456, protein.getProteinferId());
        assertEquals(124, protein.getNrseqProteinId());
        assertEquals(20.0, protein.getCoverage());
        assertEquals(3, protein.getPeptideCount());
        assertEquals(6, protein.getSpectralCount());
        assertNull(protein.getUserAnnotation());
        assertNull(protein.getUserValidation());
        assertEquals(12, protein.getClusterId());
        assertEquals(21, protein.getGroupId());
        assertTrue(protein.getIsParsimonious());
        
        List<IdPickerPeptide> peptList = protein.getPeptides();
        Collections.sort(peptList, new Comparator<IdPickerPeptide>() {
            public int compare(IdPickerPeptide o1, IdPickerPeptide o2) {
                return Integer.valueOf(o1.getId()).compareTo(o2.getId());
            }});
        
        assertEquals(3, peptList.size());
        int i = 1;
        for(IdPickerPeptide pept: peptList) {
//            assertEquals(456, pept.getProteinferId());
            assertEquals(i, pept.getSpectralCount());
            i++;
            assertEquals(1, pept.getMatchingProteinIds().size());
        }
    }

    public final void testGetProteins() {
        List<IdPickerProtein> protList = protDao.loadProteins(456);
        assertEquals(4, protList.size());
        Collections.sort(protList, new Comparator<IdPickerProtein>() {
            public int compare(IdPickerProtein o1, IdPickerProtein o2) {
                return Integer.valueOf(o1.getId()).compareTo(o2.getId());
            }});
        int i = 0;
        assertEquals(12, protList.get(i).getClusterId());
        assertEquals(21, protList.get(i).getGroupId());
        i++;
        assertEquals(12, protList.get(i).getClusterId());
        assertEquals(21, protList.get(i).getGroupId());
        i++;
        assertEquals(12, protList.get(i).getClusterId());
        assertEquals(22, protList.get(i).getGroupId());
        i++;
        assertEquals(13, protList.get(i).getClusterId());
        assertEquals(23, protList.get(i).getGroupId());
    }

    public final void testGetIdPickerProteinGroup() {
        IdPickerProteinGroup group = protDao.getIdPickerProteinGroup(456, 21);
        assertNotNull(group);
        assertEquals(21, group.getGroupId());
        assertEquals(2, group.getProteinCount());
        assertEquals(5, group.getMatchingPeptideCount());
        assertEquals(1, group.getMatchingPeptideGroups().size());
    }

    public final void testGetIdPickerCluster() {
        IdPickerCluster cluster = protDao.getIdPickerCluster(456, 12);
        assertEquals(2, cluster.getPeptideGroups().size());
        assertEquals(2, cluster.getPeptideGroups().size());
    }

    public static final IdPickerProtein createIdPickerProtein(int pinferId, int nrseqId, double coverage, 
            int clusterId, int groupId, boolean parsim, int numPept) {
        IdPickerProtein protein = new IdPickerProtein();
        protein.setProteinferId(pinferId);
        protein.setNrseqProteinId(nrseqId);
        protein.setCoverage(coverage);
        protein.setClusterId(clusterId);
        protein.setGroupId(groupId);
        protein.setIsParsimonious(parsim);
        
        List<IdPickerPeptide> peptList = new ArrayList<IdPickerPeptide>(numPept);
        for(int i = 1; i <= numPept; i++) {
            IdPickerPeptide pept = IdPickerPeptideDAOTest.createIdPickerPeptide(pinferId, groupId, i); // group ID = 98
            peptList.add(pept);
        }
        protein.setPeptides(peptList);
        
        return protein;
    }
}
