/**
 * ProteinPropertiesSorter.java
 * @author Vagisha Sharma
 * Nov 3, 2009
 * @version 1.0
 */
package org.yeastrc.www.proteinfer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import org.yeastrc.ms.domain.protinfer.SORT_BY;
import org.yeastrc.ms.domain.protinfer.SORT_ORDER;
import org.yeastrc.www.proteinfer.ProteinGroupProperties.ProteinGroupPropertiesComparator;

/**
 * 
 */
public class ProteinPropertiesSorter {

    private ProteinPropertiesSorter() {}
    
    // -------------------------------------------------------------------------------------------
    // SORT BY MOLECULAR WT.
    // -------------------------------------------------------------------------------------------
    public static List<Integer> sortIdsByMolecularWt(List<Integer> proteinIds, int pinferId, boolean groupProteins,
    		SORT_ORDER sortOrder) {
        
    	// A new map will be created if one does not exist.
        Map<Integer, ? extends ProteinProperties> proteinPropertiesMap = ProteinPropertiesStore.getInstance().getPropertiesMapForMolecularWt(pinferId);
        return sortIdsByMolecularWt(proteinIds, groupProteins, proteinPropertiesMap, sortOrder);
    }

    private  static List<Integer> sortIdsByMolecularWt(List<Integer> proteinIds, boolean groupProteins,
            Map<Integer, ? extends ProteinProperties> proteinPropertiesMap, SORT_ORDER sortOrder) {
        
        if(proteinPropertiesMap == null)
            return new ArrayList<Integer>(0);
        
        
        // get the protein properties for the subset of proteinIds we are interested in
        List<ProteinProperties> propsList = new ArrayList<ProteinProperties>(proteinIds.size());
        for(int proteinId: proteinIds) {
            propsList.add(proteinPropertiesMap.get(proteinId));
        }
        
        // If we are not grouping proteins by indistinguishable protein group
        // simply sort by the molecular wt.
        if(!groupProteins) {
            Collections.sort(propsList, new Comparator<ProteinProperties>() {
                public int compare(ProteinProperties o1, ProteinProperties o2) {
                    return Double.valueOf(o2.getMolecularWt()).compareTo(o1.getMolecularWt());
                }});
            
            List<Integer> sortedIds = new ArrayList<Integer>(propsList.size());
            for(ProteinProperties props: propsList)
                sortedIds.add(props.getPinferProteinId());
            
            if(sortOrder == SORT_ORDER.DESC)
            	Collections.reverse(sortedIds);
            
            return sortedIds;
        }
        // If we are grouping indistinguishable protein groups sort by min / max molecular wt 
        // for a protein group (based on sortOrder)
        else {
            
            // group by indistinguishable proteinID
            List<ProteinGroupProperties> grpPropsList = createIGroups(
					sortOrder, propsList);
            
            // sort each group by sorting criteria
            Collections.sort(grpPropsList, new ProteinGroupPropertiesComparator(SORT_BY.MOL_WT, sortOrder));
            
            // get a list of sorted ids
            List<Integer> sortedIds = new ArrayList<Integer>(propsList.size());
            for(ProteinGroupProperties gp: grpPropsList) {
                for(ProteinProperties props: gp.getProteinProperties()) 
                    sortedIds.add(props.getPinferProteinId());
            }
            
            return sortedIds;
        }
        
    }

	private static List<ProteinGroupProperties> createIGroups(
			SORT_ORDER sortOrder, List<ProteinProperties> propsList) {
		Collections.sort(propsList, new Comparator<ProteinProperties>() {
		    @Override
		    public int compare(ProteinProperties o1, ProteinProperties o2) {
		        return Integer.valueOf(o1.getProteinGroupId()).compareTo(o2.getProteinGroupId());
		    }});
		
		// create the protein groups
		List<ProteinGroupProperties> grpPropsList = new ArrayList<ProteinGroupProperties>();
		ProteinGroupProperties grpProps = null;
		for(ProteinProperties props: propsList) {
		    if(grpProps == null || grpProps.getProteinGroupId() != props.getProteinGroupId()) {
		        grpProps = new ProteinGroupProperties(sortOrder);
		        grpProps.add(props);
		        grpPropsList.add(grpProps);
		    }
		    else {
		        grpProps.add(props);
		    }
		}
		return grpPropsList;
	}
    
    // -------------------------------------------------------------------------------------------
    // SORT BY PI.
    // -------------------------------------------------------------------------------------------
    public static List<Integer> sortIdsByPi(List<Integer> proteinIds, int pinferId, boolean groupProteins, SORT_ORDER sortOrder) {
        
        Map<Integer, ? extends ProteinProperties> proteinPropertiesMap = ProteinPropertiesStore.getInstance().getPropertiesMapForPi(pinferId);
        return sortIdsByPi(proteinIds, groupProteins, proteinPropertiesMap, sortOrder);
    }

    private  static List<Integer> sortIdsByPi(List<Integer> proteinIds, boolean groupProteins,
            Map<Integer, ? extends ProteinProperties> proteinPropertiesMap, SORT_ORDER sortOrder) {
        
        if(proteinPropertiesMap == null)
            return new ArrayList<Integer>(0);
        
        // get the protein properties for the subset of proteinIds we are interested in
        List<ProteinProperties> propsList = new ArrayList<ProteinProperties>(proteinIds.size());
        for(int proteinId: proteinIds) {
            propsList.add(proteinPropertiesMap.get(proteinId));
        }
        
        // If we are not grouping proteins by indistinguishable protein group
        // simply sort by the pI
        if(!groupProteins) {
            Collections.sort(propsList, new Comparator<ProteinProperties>() {
                public int compare(ProteinProperties o1, ProteinProperties o2) {
                    return Double.valueOf(o2.getPi()).compareTo(o1.getPi());
                }});
            
            List<Integer> sortedIds = new ArrayList<Integer>(propsList.size());
            for(ProteinProperties props: propsList)
                sortedIds.add(props.getPinferProteinId());
            
            if(sortOrder == SORT_ORDER.DESC)
            	Collections.reverse(sortedIds);
            
            return sortedIds;
        }
        // If we are grouping indistinguishable protein groups sort by min / max pI
        // for a protein group (based on sortOrder)
        else {
            
        	// group by indistinguishable proteinID
            List<ProteinGroupProperties> grpPropsList = createIGroups(
					sortOrder, propsList);
            
            // sort each group by sorting criteria
            Collections.sort(grpPropsList, new ProteinGroupPropertiesComparator(SORT_BY.PI, sortOrder));
            
            // get a list of sorted ids
            List<Integer> sortedIds = new ArrayList<Integer>(propsList.size());
            for(ProteinGroupProperties gp: grpPropsList) {
                for(ProteinProperties props: gp.getProteinProperties()) 
                    sortedIds.add(props.getPinferProteinId());
            }
            
            return sortedIds;
        }
    }
    
    // -------------------------------------------------------------------------------------------
    // SORT BY ACCESSION
    // -------------------------------------------------------------------------------------------
    public static List<Integer> sortIdsByAccession(List<Integer> proteinIds, int pinferId, boolean groupProteins, SORT_ORDER sortOrder) {
        
    	Map<Integer, ? extends ProteinProperties> proteinPropertiesMap  = ProteinPropertiesStore.getInstance().getPropertiesMapForAccession(pinferId);
        return sortIdsByAccession(proteinIds, proteinPropertiesMap, groupProteins, sortOrder);
    }

    private static List<Integer> sortIdsByAccession(List<Integer> proteinIds, Map<Integer, ? extends ProteinProperties> proteinPropertiesMap,
    		boolean groupProteins, final SORT_ORDER sortOrder) {
        
    	if(proteinPropertiesMap == null)
            return new ArrayList<Integer>(0);
        
        // get the protein properties for the subset of proteinIds we are interested in
        List<ProteinProperties> propsList = new ArrayList<ProteinProperties>(proteinIds.size());
        for(int proteinId: proteinIds) {
            propsList.add(proteinPropertiesMap.get(proteinId));
        }
        
        // If we are not grouping proteins by indistinguishable protein group
        // simply sort by the accession
        if(!groupProteins) {
        	if(sortOrder == SORT_ORDER.DESC) {
        		Collections.sort(propsList, new Comparator<ProteinProperties>() {
        			public int compare(ProteinProperties o1, ProteinProperties o2) {
        				return o2.getAccession(sortOrder).compareTo(o1.getAccession(sortOrder));
        			}});
        	}
        	else {
        		Collections.sort(propsList, new Comparator<ProteinProperties>() {
        			public int compare(ProteinProperties o1, ProteinProperties o2) {
        				return o1.getAccession(sortOrder).compareTo(o2.getAccession(sortOrder));
        			}});
        	}
            
            List<Integer> sortedIds = new ArrayList<Integer>(propsList.size());
            for(ProteinProperties props: propsList)
                sortedIds.add(props.getPinferProteinId());
            
            
            return sortedIds;
        }
        // If we are grouping indistinguishable protein groups sort by accession
        // for a protein group (based on sortOrder)
        else {
            
            // sort by protein group id first
        	// group by indistinguishable proteinID
            List<ProteinGroupProperties> grpPropsList = createIGroups(
					sortOrder, propsList);
            
            // sort each group by sorting criteria
            Collections.sort(grpPropsList, new ProteinGroupPropertiesComparator(SORT_BY.ACCESSION, sortOrder));
            
            // get a list of sorted ids
            List<Integer> sortedIds = new ArrayList<Integer>(propsList.size());
            for(ProteinGroupProperties gp: grpPropsList) {
                for(ProteinProperties props: gp.getProteinProperties()) 
                    sortedIds.add(props.getPinferProteinId());
            }
            
            return sortedIds;
        }
    }
}
