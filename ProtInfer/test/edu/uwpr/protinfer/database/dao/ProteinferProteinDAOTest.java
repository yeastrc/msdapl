package edu.uwpr.protinfer.database.dao;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import junit.framework.TestCase;
import edu.uwpr.protinfer.database.dao.ibatis.ProteinferProteinDAO;
import edu.uwpr.protinfer.database.dto.idpicker.IdPickerCluster;
import edu.uwpr.protinfer.database.dto.idpicker.IdPickerPeptideGroup;
import edu.uwpr.protinfer.database.dto.idpicker.IdPickerProteinGroup;

public class ProteinferProteinDAOTest extends TestCase {

    protected void setUp() throws Exception {
        super.setUp();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public final void testGetProteinferCluster() {
        int pinferId = 1;
        int clusterId = 17;
        
        ProteinferProteinDAO protDao = ProteinferDAOFactory.instance().getProteinferProteinDao();
        IdPickerCluster cluster = protDao.getProteinferCluster(pinferId, clusterId);
        assertEquals(17, cluster.getClusterId());
        assertEquals(1, cluster.getProteinferId());
        List<IdPickerProteinGroup> protGroups = cluster.getProteinGroups();
        assertEquals(3, protGroups.size());
        List<IdPickerPeptideGroup> peptGroups = cluster.getPeptideGroups();
        assertEquals(4, peptGroups.size());
        
        Collections.sort(protGroups, new Comparator<IdPickerProteinGroup>() {
            public int compare(IdPickerProteinGroup o1,
                    IdPickerProteinGroup o2) {
                return Integer.valueOf(o1.getGroupId()).compareTo(o2.getGroupId());
            }});
        
        IdPickerProteinGroup gp = protGroups.get(0);
        assertEquals(19, gp.getGroupId());
        assertEquals(2, gp.getProteinCount());
        assertEquals(2, gp.getMatchingPeptideCount());
        assertEquals(1, gp.getUniqMatchingPeptideCount());
        assertEquals(2, gp.getMatchingPeptideGroupIds().size());
        
        gp = protGroups.get(1);
        assertEquals(31, gp.getGroupId());
        assertEquals(1, gp.getProteinCount());
        assertEquals(3, gp.getMatchingPeptideCount());
        assertEquals(2, gp.getUniqMatchingPeptideCount());
        assertEquals(2, gp.getMatchingPeptideGroupIds().size());
        
        gp = protGroups.get(2);
        assertEquals(37, gp.getGroupId());
        assertEquals(1, gp.getProteinCount());
        assertEquals(2, gp.getMatchingPeptideCount());
        assertEquals(0, gp.getUniqMatchingPeptideCount());
        assertEquals(2, gp.getMatchingPeptideGroupIds().size());
    }

}
