/**
 * NSAFCalculator.java
 * @author Vagisha Sharma
 * Jan 28, 2009
 * @version 1.0
 */
package edu.uwpr.protinfer.idpicker;

import java.util.List;

import org.apache.log4j.Logger;
import org.yeastrc.nrseq.dao.NrSeqLookupUtil;

import edu.uwpr.protinfer.infer.InferredProtein;
import edu.uwpr.protinfer.infer.SpectrumMatch;
import edu.uwpr.protinfer.util.TimeUtils;

/**
 * 
 */
public class NSAFCalculator {

    private static final Logger log = Logger.getLogger(NSAFCalculator.class);
    
    private static final NSAFCalculator instance = new NSAFCalculator();
    
    private NSAFCalculator () {}
    
    public static NSAFCalculator instance() {
        return instance;
    }
    
    public <S extends SpectrumMatch> void calculateNSAF(List<InferredProtein<S>> proteins, boolean calculateForAll) throws Exception {
        double totalSpC_L = 0;
        
        if(calculateForAll)
        	log.info("Calculating NSAF for all proteins");
        else
        	log.info("Calculating NSAF for parsimonious proteins only");
        
        long s = System.currentTimeMillis();
        for(InferredProtein<S> protein: proteins) {
            
            // calculate this only for parsimonious proteins, unless we are calculating NSAF for all proteins
            if(!protein.getIsAccepted() && !calculateForAll)
                continue;
                
            
            String proteinSeq = null;
            try {
//                proteinSeq = NrSeqLookupUtil.getProteinSequenceForNrSeqDbProtId(protein.getProteinId());
                proteinSeq = NrSeqLookupUtil.getProteinSequence(protein.getProteinId());
            }
            catch (Exception e) {
                log.error("Exception getting nrseq protein for proteinId: "+protein.getProteinId(), e);
                throw e;
            }
            
            if(proteinSeq == null || proteinSeq.length() == 0) {
                log.error("Protein sequence for proteinId: "+protein.getProteinId()+" is null.");
                throw new Exception("Protein sequence for proteinId: "+protein.getProteinId()+" is null.");
            }
            
            double spc_L = (double)protein.getSpectralEvidenceCount() / (double)proteinSeq.length();
            totalSpC_L += spc_L;
            protein.setNSAF(spc_L);
        }
        
        for(InferredProtein<S> protein: proteins) {
        	
        	// If we are not calculating NSAF for non-parsimonious proteins, set the NSAF to 2
        	// Update: NSAF ranges from 0 to 1. We set NSAF of non-parsimonious proteins to 2
        	//         instead of -1 since the "nsaf" column in IDPickerProtein table is 
        	//         "double unsigned"
        	if(!protein.getIsAccepted() && !calculateForAll) {
        		protein.setNSAF(2);
        	}
        	else	
        		protein.setNSAF(protein.getNSAF() / totalSpC_L);
        }
        long e = System.currentTimeMillis();
        log.info("Time to calculate NSAF: "+TimeUtils.timeElapsedSeconds(s, e)+" seconds");
    }
}
