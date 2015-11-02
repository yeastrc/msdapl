package edu.uwpr.protinfer.database;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;
import edu.uwpr.protinfer.idpicker.IDPickerExecutor;
import edu.uwpr.protinfer.infer.InferredProtein;
import edu.uwpr.protinfer.infer.Peptide;
import edu.uwpr.protinfer.infer.PeptideHit;
import edu.uwpr.protinfer.infer.Protein;
import edu.uwpr.protinfer.infer.ProteinHit;
import edu.uwpr.protinfer.infer.SearchSource;

public class ProteinferSaverTest extends TestCase {

    protected void setUp() throws Exception {
        super.setUp();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public final void testProtinferDatabaseOperations() {
        IdPickerParams params = new IdPickerParams();
        params.setDecoyRatio(1.0f);
        params.setDecoyPrefix("Reverse_");
        params.setDoParsimonyAnalysis(true);
        params.setMaxAbsoluteFdr(0.05f);
        params.setMaxRelativeFdr(0.05f);
        
        List<SequestHit> searchHits = makeSequestHits();
        
        IdPickerSummary summary = new IdPickerSummary();
        RunSearchSummary s1 = new RunSearchSummary();
        s1.setRunName("runSearch_1");
        s1.setInputId(10);
        summary.addRunSearch(s1);
        RunSearchSummary s2 = new RunSearchSummary();
        s2.setRunName("runSearch_2");
        s2.setInputId(20);
        summary.addRunSearch(s2);
        
        
        IDPickerExecutor executor = new IDPickerExecutor();
        List<InferredProtein<SequestSpectrumMatch>> proteins = executor.inferProteins(searchHits, summary, params);
        for(InferredProtein<SequestSpectrumMatch> prot: proteins) 
            prot.setPercentCoverage((float) Math.random());
        
        ProteinferSaver.saveProteinInferenceResults(summary, params, proteins);
    }
    
    private List<SequestHit> makeSequestHits() {
        List<SequestHit> hits = new ArrayList<SequestHit>();
        SearchSource source = new SearchSource("test");
        
        Protein[] proteins = new Protein[10];
        for (int i = 1; i < proteins.length; i++) {
            proteins[i] = new Protein("protein_"+i, i);
        }
        
        int proteinId = 1;
        int scanId = 1;
        int hitId = 100;
        // peptide_1: matches protein 7
        addSearchHits(proteinId++, hits, source, scanId, hitId, new Protein[]{proteins[7]});
        
        // peptide_2: matches protein 4, 6, 9
        addSearchHits(proteinId++, hits, source, scanId+=2, hitId+=2, new Protein[]{proteins[4], proteins[6], proteins[9]});
        
        // peptide_3: matches protein 1
        addSearchHits(proteinId++, hits, source, scanId+=2, hitId+=2, new Protein[]{proteins[1]});
        
        // peptide_4: matches protein 1,5
        addSearchHits(proteinId++, hits, source, scanId+=2, hitId+=2, new Protein[]{proteins[1], proteins[5]});
        
        // peptide_5: matches protein 7
        addSearchHits(proteinId++, hits, source, scanId+=2, hitId+=2, new Protein[]{proteins[7]});
        
        // peptide_6: matches protein 3,6
        addSearchHits(proteinId++, hits, source, scanId+=2, hitId+=2, new Protein[]{proteins[3], proteins[6]});
        
        // peptide_7: matches protein 1
        addSearchHits(proteinId++, hits, source, scanId+=2, hitId+=2, new Protein[]{proteins[1]});
        
        // peptide_8: matches protein 1,2,5,8
        addSearchHits(proteinId++, hits, source, scanId+=2, hitId+=2, new Protein[]{proteins[1], proteins[2], proteins[5], proteins[8]});
        
        // peptide_9: matches protein 1
        addSearchHits(proteinId++, hits, source, scanId+=2, hitId+=2, new Protein[]{proteins[1]});
        
        // peptide_10: matches protein 4, 9
        addSearchHits(proteinId++, hits, source, scanId+=2, hitId+=2, new Protein[]{proteins[4], proteins[9]});
        
        return hits;
    }
    
    private void addSearchHits(int peptideId, List<SequestHit> hits, SearchSource source, int scanId, int hitId, Protein[] proteins) {
        Peptide p = new Peptide("peptide_"+peptideId, peptideId);
        PeptideHit peptHit = new PeptideHit(p);
        for(Protein prot: proteins) {
            peptHit.addProteinHit(new ProteinHit(prot, '\u0000', '\u0000'));
        }
        SequestHit h1 = new SequestHit(source, scanId++, 2, peptHit);
        h1.setHitId(hitId++);
        h1.setDeltaCn(new BigDecimal("0.15"));
        h1.setXcorr(new BigDecimal("3.456"));
        h1.setFdr(0.0456);
        hits.add(h1);
        
        peptHit = new PeptideHit(p);
        for(Protein prot: proteins) {
            peptHit.addProteinHit(new ProteinHit(prot, '\u0000', '\u0000'));
        }
        SequestHit h2 = new SequestHit(source, scanId, 3, peptHit);
        h2.setHitId(hitId);
        h2.setDeltaCn(new BigDecimal("0.51"));
        h2.setXcorr(new BigDecimal("6.543"));
        h2.setFdr(0.025);
        hits.add(h2);
    }
}
