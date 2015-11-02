package edu.uwpr.protinfer.infer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import edu.uwpr.protinfer.util.TimeUtils;

public class ProteinInferrerMaximal implements ProteinInferrer {

    private static final Logger log = Logger.getLogger(ProteinInferrerMaximal.class);
    
    @Override
    public <S extends SpectrumMatch, T extends PeptideSpectrumMatch<S>> 
        List<InferredProtein<S>> inferProteins(List<T> psmList) {
        
        long s = System.currentTimeMillis();
        
        Map<Integer, InferredProtein<S>> proteinMap = new HashMap<Integer, InferredProtein<S>>();
        Map<Integer, PeptideEvidence<S>> peptideMap = new HashMap<Integer, PeptideEvidence<S>>();
        
        
        // for each peptide sequence match
        for (T psm: psmList) {
            
            // peptide for this peptide-spectrum-match
            Peptide psmPeptide = psm.getPeptideHit().getPeptide();
            
            // add this to the peptideMap if not already there
            PeptideEvidence<S> evidence = peptideMap.get(psmPeptide.getId());
            if(evidence == null) {
                evidence = new PeptideEvidence<S>(psmPeptide);
                psmPeptide.markUnique(psm.getPeptideHit().getMatchProteinCount() == 1);
                peptideMap.put(psmPeptide.getId(), evidence);
            }
            evidence.addSpectrumMatch(psm.getSpectrumMatch());
            
            // for each protein match to the peptide
            List<Protein> protHitList = psm.getPeptideHit().getProteinList();
            for (Protein protHit: protHitList) {
                
                InferredProtein<S> inferredProtein = proteinMap.get(protHit.getId());
                
                // if we have not seen this protein add a new InferredProtein to the proteinMap
                if (inferredProtein == null) {
                    inferredProtein = new InferredProtein<S>(protHit);
                    proteinMap.put(protHit.getId(), inferredProtein);
                }
                
                // if this protein does not already have this peptide evidence add it
                if(inferredProtein.getPeptideEvidence(psmPeptide) == null) {
                    inferredProtein.addPeptideEvidence(evidence);
                }
            }
        }
        
        List<InferredProtein<S>> inferredProteins = new ArrayList<InferredProtein<S>>(proteinMap.size());
        inferredProteins.addAll(proteinMap.values());
        
        long e = System.currentTimeMillis();
        log.info("Inferred proteins (maximal) in: "+TimeUtils.timeElapsedSeconds(s, e)+" seconds \nAll: "+
                inferredProteins.size());
        
        return inferredProteins;
    }
}
