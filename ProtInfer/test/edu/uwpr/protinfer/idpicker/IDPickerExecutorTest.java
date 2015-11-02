package edu.uwpr.protinfer.idpicker;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.framework.TestCase;
import edu.uwpr.protinfer.infer.InferredProtein;
import edu.uwpr.protinfer.infer.Peptide;
import edu.uwpr.protinfer.infer.PeptideHit;
import edu.uwpr.protinfer.infer.Protein;
import edu.uwpr.protinfer.infer.SearchSource;

public class IDPickerExecutorTest extends TestCase {

    protected void setUp() throws Exception {
        super.setUp();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public final void testPeptideProteinMatch() {
        
        String peptide = "ABCD";
        String protein = null;
        assertFalse(IDPickerExecutor.peptideProteinMatch(protein, peptide));
        
        protein = "ABCDEGFH";
        assertFalse(IDPickerExecutor.peptideProteinMatch(protein, peptide));
        
        protein = "KABCACBABC";
        assertFalse(IDPickerExecutor.peptideProteinMatch(protein, peptide));
        
        protein = "KABCDXYZABC";
        assertTrue(IDPickerExecutor.peptideProteinMatch(protein, peptide));
        
        protein = "ABCDKABCDXYZ";
        assertTrue(IDPickerExecutor.peptideProteinMatch(protein, peptide));
        
    }
    
    public final void testInferProteins() {
        
        IDPickerParams params = new IDPickerParams();
        params.setMaxFdr(0.05f);
        
        List<PeptideSpectrumMatchIDP> searchHits = makeSequestHits();
        
        IDPickerExecutor executor = new IDPickerExecutor();
        executor.doNotCalculateCoverage(); // don't calculate coverage; we don't want to 
                                           // to look in nrseq database
        
        List<InferredProtein<SpectrumMatchIDP>> proteins = null;
        try {
            proteins = executor.inferProteins(searchHits, params);
        }
        catch (Exception e) {
            e.printStackTrace();
            fail("failed");
        }
        
        assertEquals(9, proteins.size());
        int parsimonious = 0;
        for(InferredProtein<SpectrumMatchIDP> prot: proteins) {
            if(prot.getIsAccepted())    parsimonious++;
        }
        assertEquals(5, parsimonious);
        
        Collections.sort(proteins, new Comparator<InferredProtein<SpectrumMatchIDP>>() {
            public int compare(InferredProtein<SpectrumMatchIDP> o1,
                    InferredProtein<SpectrumMatchIDP> o2) {
                return Integer.valueOf(o1.getProtein().getId()).compareTo(o2.getProtein().getId());
            }});
        
        int minCluster = Integer.MAX_VALUE;
        int maxCluster = 0;
        for (InferredProtein<SpectrumMatchIDP> prot: proteins) {
            minCluster = Math.min(minCluster, prot.getProteinClusterLabel());
            maxCluster = Math.max(maxCluster, prot.getProteinClusterLabel());
            System.out.println(prot.getAccession()+"; cluster: "+prot.getProteinClusterLabel()+"; group: "+prot.getProteinGroupLabel());
        }
        
        // create a map for the proteins
        Map<String, InferredProtein<SpectrumMatchIDP>>  map = new HashMap<String, InferredProtein<SpectrumMatchIDP>>();
        for (InferredProtein<SpectrumMatchIDP> prot: proteins) {
           map.put(prot.getAccession(), prot);
        }
        
        // CHECK THE CLUSTERS
        // proteins 1, 2, 5, and 8 should be in the same cluster
        int clusterId1 = map.get("protein_1").getProteinClusterLabel();
        assertTrue(clusterId1 > 0);
        assertEquals(clusterId1, map.get("protein_2").getProteinClusterLabel());
        assertEquals(clusterId1, map.get("protein_5").getProteinClusterLabel());
        assertEquals(clusterId1, map.get("protein_8").getProteinClusterLabel());

        // proteins 3, 4, 6, and 9 should be in the same cluster
        int clusterId2 = map.get("protein_3").getProteinClusterLabel();
        assertTrue(clusterId2 > 0);
        assertNotSame(clusterId1, clusterId2);
        assertEquals(clusterId2, map.get("protein_4").getProteinClusterLabel());
        assertEquals(clusterId2, map.get("protein_6").getProteinClusterLabel());
        assertEquals(clusterId2, map.get("protein_9").getProteinClusterLabel());

        // protein 7 should be in a cluster by itself
        int clusterId3 = map.get("protein_7").getProteinClusterLabel();
        assertTrue(clusterId3 > 0);
        assertNotSame(clusterId1, clusterId3);
        assertNotSame(clusterId2, clusterId3);

        // CHECK THE PROTEIN GROUPS
        // protein_1
        int groupId1 = map.get("protein_1").getProteinGroupLabel();
        assertTrue(groupId1 > 0);
        
        // protein_2, protein_8
        int groupId2 = map.get("protein_2").getProteinGroupLabel();
        assertTrue(groupId2 > 0);
        assertNotSame(groupId2, groupId1);
        assertEquals(groupId2, map.get("protein_8").getProteinGroupLabel());
        
        // protein_5
        int groupId3 = map.get("protein_5").getProteinGroupLabel();
        assertTrue(groupId3 > 0);
        assertNotSame(groupId3, groupId1);
        assertNotSame(groupId3, groupId2);
        
        // protein_3
        int groupId4 = map.get("protein_3").getProteinGroupLabel();
        assertTrue(groupId4 > 0);
        assertNotSame(groupId4, groupId1);
        assertNotSame(groupId4, groupId2);
        assertNotSame(groupId4, groupId3);

        // protein_4, protein_9
        int groupId5 = map.get("protein_4").getProteinGroupLabel();
        assertTrue(groupId5 > 0);
        assertNotSame(groupId5, groupId1);
        assertNotSame(groupId5, groupId2);
        assertNotSame(groupId5, groupId3);
        assertNotSame(groupId5, groupId4);
        assertEquals(groupId5, map.get("protein_9").getProteinGroupLabel());

        // protein_6
        int groupId6 = map.get("protein_6").getProteinGroupLabel();
        assertTrue(groupId6 > 0);
        assertNotSame(groupId6, groupId1);
        assertNotSame(groupId6, groupId2);
        assertNotSame(groupId6, groupId3);
        assertNotSame(groupId6, groupId4);
        assertNotSame(groupId6, groupId5);
        
        // protein_7
        int groupId7 = map.get("protein_7").getProteinGroupLabel();
        assertTrue(groupId7 > 0);
        assertNotSame(groupId7, groupId1);
        assertNotSame(groupId7, groupId2);
        assertNotSame(groupId7, groupId3);
        assertNotSame(groupId7, groupId4);
        assertNotSame(groupId7, groupId5);
        assertNotSame(groupId7, groupId6);

        
        InferredProtein<SpectrumMatchIDP> prot = map.get("protein_1");
        assertEquals(1, prot.getProtein().getId());
        assertEquals("protein_1", prot.getAccession());
        assertEquals(5, prot.getPeptides().size());
        assertTrue(prot.getIsAccepted());
        
        prot = map.get("protein_2");
        assertEquals(2, prot.getProtein().getId());
        assertEquals("protein_2", prot.getAccession());
        assertEquals(1, prot.getPeptides().size());
        assertFalse(prot.getIsAccepted());
        
        prot = map.get("protein_3");
        assertEquals(3, prot.getProtein().getId());
        assertEquals("protein_3", prot.getAccession());
        assertEquals(1, prot.getPeptides().size());
        assertFalse(prot.getIsAccepted());
        
        prot = map.get("protein_4");
        assertEquals(4, prot.getProtein().getId());
        assertEquals("protein_4", prot.getAccession());
        assertEquals(2, prot.getPeptides().size());
        assertTrue(prot.getIsAccepted());
        
        prot = map.get("protein_5");
        assertEquals(5, prot.getProtein().getId());
        assertEquals("protein_5", prot.getAccession());
        assertEquals(2, prot.getPeptides().size());
        assertFalse(prot.getIsAccepted());
        
        prot = map.get("protein_6");
        assertEquals(6, prot.getProtein().getId());
        assertEquals("protein_6", prot.getAccession());
        assertEquals(2, prot.getPeptides().size());
        assertTrue(prot.getIsAccepted());
        
        prot = map.get("protein_7");
        assertEquals(7, prot.getProtein().getId());
        assertEquals("protein_7", prot.getAccession());
        assertEquals(2, prot.getPeptides().size());
        assertTrue(prot.getIsAccepted());
        
        prot = map.get("protein_8");
        assertEquals(8, prot.getProtein().getId());
        assertEquals("protein_8", prot.getAccession());
        assertEquals(1, prot.getPeptides().size());
        assertFalse(prot.getIsAccepted());
        
        prot = map.get("protein_9");
        assertEquals(9, prot.getProtein().getId());
        assertEquals("protein_9", prot.getAccession());
        assertEquals(2, prot.getPeptides().size());
        assertTrue(prot.getIsAccepted());
        
        assertEquals(1, minCluster);
        assertEquals(3, maxCluster);
    }
    
    private List<PeptideSpectrumMatchIDP> makeSequestHits() {
        List<PeptideSpectrumMatchIDP> hits = new ArrayList<PeptideSpectrumMatchIDP>();
        SearchSource source = new SearchSource("test");
        
        Protein[] proteins = new Protein[10];
        for (int i = 1; i < proteins.length; i++) {
            proteins[i] = new Protein("protein_"+i, i);
        }
        
        int proteinId = 1;
        int scanId = 1;
        // peptide_1: matches protein 7
        addSearchHits(proteinId++, hits, source, scanId, new Protein[]{proteins[7]});
        
        // peptide_2: matches protein 4, 6, 9
        addSearchHits(proteinId++, hits, source, scanId+=2, new Protein[]{proteins[4], proteins[6], proteins[9]});
        
        // peptide_3: matches protein 1
        addSearchHits(proteinId++, hits, source, scanId+=2, new Protein[]{proteins[1]});
        
        // peptide_4: matches protein 1,5
        addSearchHits(proteinId++, hits, source, scanId+=2, new Protein[]{proteins[1], proteins[5]});
        
        // peptide_5: matches protein 7
        addSearchHits(proteinId++, hits, source, scanId+=2, new Protein[]{proteins[7]});
        
        // peptide_6: matches protein 3,6
        addSearchHits(proteinId++, hits, source, scanId+=2, new Protein[]{proteins[3], proteins[6]});
        
        // peptide_7: matches protein 1
        addSearchHits(proteinId++, hits, source, scanId+=2, new Protein[]{proteins[1]});
        
        // peptide_8: matches protein 1,2,5,8
        addSearchHits(proteinId++, hits, source, scanId+=2, new Protein[]{proteins[1], proteins[2], proteins[5], proteins[8]});
        
        // peptide_9: matches protein 1
        addSearchHits(proteinId++, hits, source, scanId+=2, new Protein[]{proteins[1]});
        
        // peptide_10: matches protein 4, 9
        addSearchHits(proteinId++, hits, source, scanId+=2, new Protein[]{proteins[4], proteins[9]});
        
        return hits;
    }
    
    private void addSearchHits(int peptideId, List<PeptideSpectrumMatchIDP> hits, SearchSource source, int scanId, Protein[] proteins) {
        Peptide p = new Peptide("peptide_"+peptideId, "peptide_"+peptideId, peptideId);
        PeptideHit peptHit = new PeptideHit(p);
        for(Protein prot: proteins) {
            peptHit.addProtein(prot);
        }
        PeptideSpectrumMatchIDPImpl h1 = new PeptideSpectrumMatchIDPImpl(); //(source, scanId++, 2, peptHit);
        SpectrumMatchIDPImpl sm = new SpectrumMatchIDPImpl();
        sm.setScanId(scanId++);
        sm.setCharge(2);
        sm.setSourceId(source.getId());
        h1.setPeptide(peptHit);
        h1.setSpectrumMatch(sm);
        
        hits.add(h1);
        peptHit = new PeptideHit(p);
        for(Protein prot: proteins) {
            peptHit.addProtein(prot);
        }
//        SequestHit h2 = new SequestHit(source, scanId, 3, peptHit);
        PeptideSpectrumMatchIDPImpl h2 = new PeptideSpectrumMatchIDPImpl(); //(source, scanId++, 2, peptHit);
        sm = new SpectrumMatchIDPImpl();
        sm.setScanId(scanId++);
        sm.setCharge(2);
        sm.setSourceId(source.getId());
        h2.setPeptide(peptHit);
        h2.setSpectrumMatch(sm);
        hits.add(h2);
    }

}
