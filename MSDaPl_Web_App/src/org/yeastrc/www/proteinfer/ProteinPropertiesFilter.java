/**
 * ProteinPropertiesFilter.java
 * @author Vagisha Sharma
 * Nov 4, 2009
 * @version 1.0
 */
package org.yeastrc.www.proteinfer;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.yeastrc.ms.domain.protinfer.ProteinFilterCriteria;
import org.yeastrc.ms.util.ProteinUtils;
import org.yeastrc.nrseq.dao.NrSeqLookupUtil;

/**
 * 
 */
public class ProteinPropertiesFilter {

	private static ProteinPropertiesFilter instance = null;
	
    private ProteinPropertiesFilter() {}
    
    public static ProteinPropertiesFilter getInstance() {
    	if(instance == null) 
    		instance = new ProteinPropertiesFilter();
    	return instance;
    }
    
    // -------------------------------------------------------------------------------------------------
    // FILTER PROTEIN INFER PROTEIN-IDS BY MOLECULAR WT.
    // -------------------------------------------------------------------------------------------------
    public List<Integer> filterForProtInferByMolecularWt(int pinferId,
            List<Integer> allProteinIds, double minWt, double maxWt) {
        
        // get a map of the protein ids and protein properties
        Map<Integer, ? extends ProteinProperties> propsMap = ProteinPropertiesStore.getInstance().getPropertiesMapForMolecularWt(pinferId);
        return filterForProtInferByMolecularWt(pinferId, allProteinIds, propsMap, minWt, maxWt);
    }
    
    private List<Integer> filterForProtInferByMolecularWt(int pinferId,
            List<Integer> allProteinIds,
            Map<Integer, ? extends ProteinProperties> proteinPropertiesMap, double minWt, double maxWt) {
        
        List<Integer> filtered = new ArrayList<Integer>();
        
        
        //  map look in there
        if(proteinPropertiesMap != null) {
        
            for(int id: allProteinIds) {
                ProteinProperties props = proteinPropertiesMap.get(id);
                if(props.getMolecularWt() >= minWt && props.getMolecularWt() <= maxWt)
                    filtered.add(id);
            }
        }
        return filtered;
    }
    

    // -------------------------------------------------------------------------------------------------
    // FILTER PROTEIN INFER PROTEIN-IDS BY PI.
    // -------------------------------------------------------------------------------------------------
    public List<Integer> filterForProtInferByPi(int pinferId,
            List<Integer> allProteinIds, double minPi, double maxPi) {
        
        // get a map of the protein ids and protein properties
        Map<Integer, ? extends ProteinProperties> propsMap = ProteinPropertiesStore.getInstance().getPropertiesMapForPi(pinferId);
        return filterForProtInferByPi(pinferId, allProteinIds, propsMap, minPi, maxPi);
    }
    
    private List<Integer> filterForProtInferByPi(int pinferId,
            List<Integer> allProteinIds,
            Map<Integer, ? extends ProteinProperties> proteinPropertiesMap, double minPi, double maxPi) {
        
        List<Integer> filtered = new ArrayList<Integer>();
        
        //  map look in there
        if(proteinPropertiesMap != null) {
        
            for(int id: allProteinIds) {
                ProteinProperties props = proteinPropertiesMap.get(id);
                if(props.getPi() >= minPi && props.getPi() <= maxPi)
                    filtered.add(id);
            }
        }
        return filtered;
    }
    
    

    // -------------------------------------------------------------------------------------------------
    // FILTER NRSEQ IDS.
    // -------------------------------------------------------------------------------------------------
    public List<Integer> filterNrseqIdsyMolecularWtAndPi(List<Integer> allProteinIds, ProteinFilterCriteria filterCriteria) {
        
    	double minWt = filterCriteria.getMinMolecularWt();
    	double maxWt = filterCriteria.getMaxMolecularWt();
    	
    	double minPi = filterCriteria.getMinPi();
    	double maxPi = filterCriteria.getMaxPi();
    	
        List<Integer> filtered = new ArrayList<Integer>();
        
        for(int nrseqId: allProteinIds) {
        	String sequence = NrSeqLookupUtil.getProteinSequence(nrseqId);
        	double molWt = ProteinUtils.calculateMolWt(sequence);
        	double pi = ProteinUtils.calculatePi(sequence);
        	
        	if((molWt >= minWt && molWt <= maxWt) &&  // pass mol filter  AND
        	   (pi >= minPi && pi <= maxPi)) {		  // pass pI filter
        		filtered.add(nrseqId);
        	}
        }
        return filtered;
    }
}
