package edu.uwpr.protinfer.infer;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

public class ProteinInferrerMaximalTest extends TestCase {

    protected void setUp() throws Exception {
        super.setUp();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public final void testInferProteins() {
        SearchSource source = new SearchSource("dummy");
        
        Peptide p1 = new Peptide("ABCDE", "ABCDE", 1);
        Peptide p2 = new Peptide("ABCDE", "ABCDE", 1);
        PeptideHit hit1 = new PeptideHit(p1);
        hit1.addProtein(new Protein("Protein1", 1));
        hit1.addProtein(new Protein("Protein2", 2));
        PeptideHit hit2 = new PeptideHit(p2);
        hit2.addProtein(new Protein("Protein1", 1));
        hit2.addProtein(new Protein("Protein2", 2));
        
        SequestHit shit1 = new SequestHit(source, 1, 2, hit1);
        SequestHit shit2 = new SequestHit(source, 2, 3, hit2);
        
        List<SequestHit> hits = new ArrayList<SequestHit>();
        hits.add(shit1);
        hits.add(shit2);
        
        assertFalse(p1 == p2);
        
        ProteinInferrerMaximal pinferrer = new ProteinInferrerMaximal();
        List<InferredProtein<SequestSpectrumMatch>> infList = pinferrer.inferProteins(hits);
        assertEquals(2, infList.size());
        
        assertEquals(1, infList.get(0).getPeptides().size());
        assertEquals("ABCDE", infList.get(0).getPeptides().get(0).getPeptide().getModifiedSequence());
        assertEquals(1, infList.get(1).getPeptides().size());
        assertEquals("ABCDE", infList.get(1).getPeptides().get(0).getPeptide().getModifiedSequence());
        
        assertTrue(infList.get(0).getPeptides().get(0) == infList.get(1).getPeptides().get(0));
        assertTrue(infList.get(0).getPeptides().get(0).getPeptide() == infList.get(1).getPeptides().get(0).getPeptide());
        
        PeptideEvidence<SequestSpectrumMatch> pev = infList.get(0).getPeptides().get(0);
        assertEquals(2, pev.getSpectrumMatchCount());
        List<SequestSpectrumMatch> specList = pev.getSpectrumMatchList();
        assertEquals(2, specList.size());
        assertTrue(specList.get(0) == shit1.getSpectrumMatch());
        assertTrue(specList.get(1) == shit2.getSpectrumMatch());
        
        
        Peptide p3 = new Peptide("XYZ", 2);
        PeptideHit hit3 = new PeptideHit(p3);
        hit3.addProteinHit(new ProteinHit(new Protein("Protein1", 1)));
        SequestHit shit3 = new SequestHit(source, 2, 3, hit3);
        hits.add(shit3);
        
        pinferrer = new ProteinInferrerMaximal();
        infList = pinferrer.inferProteins(hits);
        assertEquals(2, infList.size());
        for(InferredProtein<SequestSpectrumMatch> pr: infList) {
            if(pr.getAccession().equals("Protein1")) {
                assertEquals(2, pr.getPeptideEvidenceCount());
                assertEquals(3, pr.getSpectralEvidenceCount());
            }
        }
    }

}
