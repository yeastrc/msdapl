/**
 * ProteinPropertiesSorter.java
 * @author Vagisha Sharma
 * Nov 3, 2009
 * @version 1.0
 */
package org.yeastrc.www.proteinfer.proteinProphet;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import org.yeastrc.ms.domain.protinfer.SORT_BY;
import org.yeastrc.ms.domain.protinfer.SORT_ORDER;
import org.yeastrc.www.proteinfer.ProteinGroupProperties;
import org.yeastrc.www.proteinfer.ProteinProperties;
import org.yeastrc.www.proteinfer.ProteinPropertiesStore;
import org.yeastrc.www.proteinfer.ProteinGroupProperties.ProteinGroupPropertiesComparator;

/**
 * 
 */
public class ProphetProteinPropertiesSorter {

    private ProphetProteinPropertiesSorter() {}
    
    // -------------------------------------------------------------------------------------------
    // SORT BY MOLECULAR WT.
    // -------------------------------------------------------------------------------------------
    /**
     * groupProteins == true if members of ProteinProphet groups are to returned together.
     */
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
        List<ProphetProteinProperties> propsList = new ArrayList<ProphetProteinProperties>(proteinIds.size());
        for(int proteinId: proteinIds) {
            propsList.add((ProphetProteinProperties) proteinPropertiesMap.get(proteinId));
        }
        
        // If we are not grouping proteins by ProteinProphet group
        // sort indistinguishable groups by molecular wt.
        if(!groupProteins) {
        	
        	// group by indistinguishable proteinID
            List<ProteinGroupProperties> grpPropsList = createIGroups(sortOrder, propsList);
            
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
        // We are grouping ProteinProphet groups
        else {
            
        	// group by ProteinProphet group ID
            List<ProphetGroupProperties> grpPropsList = createProphetGroups(sortOrder, propsList);
            
            if(sortOrder == SORT_ORDER.DESC) {
            	Collections.sort(grpPropsList, new Comparator<ProphetGroupProperties>() {
            		@Override
            		public int compare(ProphetGroupProperties o1,
            				ProphetGroupProperties o2) {
            			return Double.valueOf(o2.getGroupMolecularWt()).compareTo(o1.getGroupMolecularWt());
            		}
            	});
            }
            else {
            	Collections.sort(grpPropsList, new Comparator<ProphetGroupProperties>() {
            		@Override
            		public int compare(ProphetGroupProperties o1,
            				ProphetGroupProperties o2) {
            			return Double.valueOf(o1.getGroupMolecularWt()).compareTo(o2.getGroupMolecularWt());
            		}
            	});
            }
            
            // get a list of sorted ids
            List<Integer> sortedIds = new ArrayList<Integer>(propsList.size());
            for(ProphetGroupProperties gp: grpPropsList) {
                for(ProteinGroupProperties igprops: gp.getIndistinguishableGroups()) 
                	for(ProteinProperties props: igprops.getProteinProperties()) 
                		sortedIds.add(props.getPinferProteinId());
            }
            
            return sortedIds;
        }
        
    }

    // -------------------------------------------------------------------------------------------
    // SORT BY PI.
    // -------------------------------------------------------------------------------------------
	/**
     * groupProteins == true if members of ProteinProphet groups are to returned together.
     */
    public static List<Integer> sortIdsByPi(List<Integer> proteinIds, int pinferId, boolean groupProteins, SORT_ORDER sortOrder) {
        
        Map<Integer, ? extends ProteinProperties> proteinPropertiesMap = ProteinPropertiesStore.getInstance().getPropertiesMapForPi(pinferId);
        return sortIdsByPi(proteinIds, groupProteins, proteinPropertiesMap, sortOrder);
    }

    private  static List<Integer> sortIdsByPi(List<Integer> proteinIds, boolean groupProteins,
            Map<Integer, ? extends ProteinProperties> proteinPropertiesMap, SORT_ORDER sortOrder) {
        
    	if(proteinPropertiesMap == null)
            return new ArrayList<Integer>(0);
        
        
        // get the protein properties for the subset of proteinIds we are interested in
        List<ProphetProteinProperties> propsList = new ArrayList<ProphetProteinProperties>(proteinIds.size());
        for(int proteinId: proteinIds) {
            propsList.add((ProphetProteinProperties) proteinPropertiesMap.get(proteinId));
        }
        
        // If we are not grouping proteins by ProteinProphet group
        // sort indistinguishable groups by pI.
        if(!groupProteins) {
        	
        	// group by indistinguishable proteinID
            List<ProteinGroupProperties> grpPropsList = createIGroups(sortOrder, propsList);
            
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
        // We are grouping ProteinProphet groups
        else {
            
        	// group by ProteinProphet group ID
            List<ProphetGroupProperties> grpPropsList = createProphetGroups(sortOrder, propsList);
            
            if(sortOrder == SORT_ORDER.DESC) {
            	Collections.sort(grpPropsList, new Comparator<ProphetGroupProperties>() {
            		@Override
            		public int compare(ProphetGroupProperties o1,
            				ProphetGroupProperties o2) {
            			return Double.valueOf(o2.getGroupPi()).compareTo(o1.getGroupPi());
            		}
            	});
            }
            else {
            	Collections.sort(grpPropsList, new Comparator<ProphetGroupProperties>() {
            		@Override
            		public int compare(ProphetGroupProperties o1,
            				ProphetGroupProperties o2) {
            			return Double.valueOf(o1.getGroupPi()).compareTo(o2.getGroupPi());
            		}
            	});
            }
            
            // get a list of sorted ids
            List<Integer> sortedIds = new ArrayList<Integer>(propsList.size());
            for(ProphetGroupProperties gp: grpPropsList) {
                for(ProteinGroupProperties igprops: gp.getIndistinguishableGroups()) 
                	for(ProteinProperties props: igprops.getProteinProperties()) 
                		sortedIds.add(props.getPinferProteinId());
            }
            
            return sortedIds;
        }
    }
    
    // -------------------------------------------------------------------------------------------
    // SORT BY ACCESSION
    // -------------------------------------------------------------------------------------------
    /**
     * groupProteins == true if members of ProteinProphet groups are to returned together.
     */
    public static List<Integer> sortIdsByAccession(List<Integer> proteinIds, int pinferId, boolean groupProteins, SORT_ORDER sortOrder) {
        
    	Map<Integer, ? extends ProteinProperties> proteinPropertiesMap  = ProteinPropertiesStore.getInstance().getPropertiesMapForAccession(pinferId);
        return sortIdsByAccession(proteinIds, proteinPropertiesMap, groupProteins, sortOrder);
    }

    private static List<Integer> sortIdsByAccession(List<Integer> proteinIds, Map<Integer, ? extends ProteinProperties> proteinPropertiesMap,
    		boolean groupProteins, final SORT_ORDER sortOrder) {
        
    	if(proteinPropertiesMap == null)
            return new ArrayList<Integer>(0);
        
        
        // get the protein properties for the subset of proteinIds we are interested in
        List<ProphetProteinProperties> propsList = new ArrayList<ProphetProteinProperties>(proteinIds.size());
        for(int proteinId: proteinIds) {
            propsList.add((ProphetProteinProperties) proteinPropertiesMap.get(proteinId));
        }
        
        // If we are not grouping proteins by ProteinProphet group
        // sort indistinguishable groups by pI.
        if(!groupProteins) {
        	
        	// group by indistinguishable proteinID
            List<ProteinGroupProperties> grpPropsList = createIGroups(sortOrder, propsList);
            
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
        // We are grouping ProteinProphet groups
        else {
            
        	// group by ProteinProphet group ID
            List<ProphetGroupProperties> grpPropsList = createProphetGroups(sortOrder, propsList);
            
            if(sortOrder == SORT_ORDER.DESC) {
            	Collections.sort(grpPropsList, new Comparator<ProphetGroupProperties>() {
            		@Override
            		public int compare(ProphetGroupProperties o1,
            				ProphetGroupProperties o2) {
            			return o2.getGroupAccession().compareTo(o1.getGroupAccession());
            		}
            	});
            }
            else {
            	Collections.sort(grpPropsList, new Comparator<ProphetGroupProperties>() {
            		@Override
            		public int compare(ProphetGroupProperties o1,
            				ProphetGroupProperties o2) {
            			return o1.getGroupAccession().compareTo(o2.getGroupAccession());
            		}
            	});
            }
            
            // get a list of sorted ids
            List<Integer> sortedIds = new ArrayList<Integer>(propsList.size());
            for(ProphetGroupProperties gp: grpPropsList) {
                for(ProteinGroupProperties igprops: gp.getIndistinguishableGroups()) 
                	for(ProteinProperties props: igprops.getProteinProperties()) 
                		sortedIds.add(props.getPinferProteinId());
            }
            
            return sortedIds;
        }
    }
    
    
    private static List<ProteinGroupProperties> createIGroups(
			SORT_ORDER sortOrder, List<ProphetProteinProperties> propsList) {
		
		// sort by protein group id first
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
		        grpPropsList.add(grpProps);
		    }
		    grpProps.add(props);
		}
		return grpPropsList;
	}
    
    private static List<ProphetGroupProperties> createProphetGroups(
			SORT_ORDER sortOrder, List<ProphetProteinProperties> propsList) {
		
		// sort by prophet groupID then protein group id
		Collections.sort(propsList, new Comparator<ProphetProteinProperties>() {
		    @Override
		    public int compare(ProphetProteinProperties o1, ProphetProteinProperties o2) {
		        int value = Integer.valueOf(o1.getProteinProphetGroupId()).compareTo(o2.getProteinProphetGroupId());
		        if(value != 0)
		        	return value;
		        else
		        	return Integer.valueOf(o1.getProteinGroupId()).compareTo(o2.getProteinGroupId());
		    }});
		
		// create the prophet groups
		List<ProphetGroupProperties> grpPropsList = new ArrayList<ProphetGroupProperties>();
		
		List<ProphetProteinProperties> prophetGrpProps = new ArrayList<ProphetProteinProperties>();
		int lastProphetGrpId = -1;
		
		for(ProphetProteinProperties props: propsList) {
		    if(lastProphetGrpId != props.getProteinProphetGroupId()) {
		    	
		    	if(lastProphetGrpId != -1) {
		    		ProphetGroupProperties grpProps = new ProphetGroupProperties(lastProphetGrpId, sortOrder);
		    		List<ProteinGroupProperties> igrpProps = createIGroups(sortOrder, prophetGrpProps);
		    		for (ProteinGroupProperties igrp: igrpProps)
		    			grpProps.add(igrp);
		    		
		    		grpPropsList.add(grpProps);
		    		
		    	}
		    	
		    	lastProphetGrpId = props.getProteinProphetGroupId();
		    	prophetGrpProps = new ArrayList<ProphetProteinProperties>();
		    }
		    prophetGrpProps.add(props);
		}
		
		// last one
		if(lastProphetGrpId != -1) {
    		ProphetGroupProperties grpProps = new ProphetGroupProperties(lastProphetGrpId, sortOrder);
    		List<ProteinGroupProperties> igrpProps = createIGroups(sortOrder, prophetGrpProps);
    		for (ProteinGroupProperties igrp: igrpProps)
    			grpProps.add(igrp);
    		
    		grpPropsList.add(grpProps);
    		
    	}
		
		return grpPropsList;
	}
}
