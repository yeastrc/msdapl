/**
 * ProteinGroupProperties.java
 * @author Vagisha Sharma
 * Mar 22, 2010
 * @version 1.0
 */
package org.yeastrc.www.proteinfer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.yeastrc.ms.domain.protinfer.SORT_BY;
import org.yeastrc.ms.domain.protinfer.SORT_ORDER;

public class ProteinGroupProperties {
    
    private List<ProteinProperties> proteinPropsList;
    private SORT_ORDER sortOrder;
    
    public ProteinGroupProperties(SORT_ORDER sortOrder) {
        proteinPropsList = new ArrayList<ProteinProperties>();
        this.sortOrder = sortOrder;
    }
    
    public void add(ProteinProperties props) {
        this.proteinPropsList.add(props);
    }
    
    public double getGroupMolecularWt() {
        return getSortedByMolWt().get(0).getMolecularWt();
    }
    
    public double getGroupPi() {
        return getSortedByPi().get(0).getPi();
    }
    
    public String getGroupAccession() {
    	return getSortedByAccession().get(0).getAccession(sortOrder);
    }
    
    public List<ProteinProperties> getProteinProperties() {
    	return proteinPropsList;
    }
    
    public int getProteinGroupId() {
        return proteinPropsList.get(0).getProteinGroupId();
    }
    
    public List<ProteinProperties> getSortedByMolWt() {
    	if(sortOrder == SORT_ORDER.DESC) {
    		Collections.sort(proteinPropsList, new Comparator<ProteinProperties>() {
    			@Override
    			public int compare(ProteinProperties o1, ProteinProperties o2) {
    				return Double.valueOf(o2.getMolecularWt()).compareTo(o1.getMolecularWt());
    			}});
    	}
    	else {
    		Collections.sort(proteinPropsList, new Comparator<ProteinProperties>() {
    			@Override
    			public int compare(ProteinProperties o1, ProteinProperties o2) {
    				return Double.valueOf(o1.getMolecularWt()).compareTo(o2.getMolecularWt());
    			}});
    	}
        return proteinPropsList;
    }
    
    List<ProteinProperties> getSortedByPi() {
    	if(sortOrder == SORT_ORDER.DESC) {
    		Collections.sort(proteinPropsList, new Comparator<ProteinProperties>() {
    			@Override
    			public int compare(ProteinProperties o1, ProteinProperties o2) {
    				return Double.valueOf(o2.getPi()).compareTo(o1.getPi());
    			}});
    	}
    	else {
    		Collections.sort(proteinPropsList, new Comparator<ProteinProperties>() {
    			@Override
    			public int compare(ProteinProperties o1, ProteinProperties o2) {
    				return Double.valueOf(o1.getPi()).compareTo(o2.getPi());
    			}});
    	}
        return proteinPropsList;
    }
    
    List<ProteinProperties> getSortedByAccession() {
    	if(sortOrder == SORT_ORDER.DESC) {
    		Collections.sort(proteinPropsList, new Comparator<ProteinProperties>() {
    			@Override
    			public int compare(ProteinProperties o1, ProteinProperties o2) {
    				return o2.getAccession(sortOrder).compareTo(o1.getAccession(sortOrder));
    			}});
    	}
    	else {
    		Collections.sort(proteinPropsList, new Comparator<ProteinProperties>() {
    			@Override
    			public int compare(ProteinProperties o1, ProteinProperties o2) {
    				return o1.getAccession(sortOrder).compareTo(o2.getAccession(sortOrder));
    			}});
    	}
        return proteinPropsList;
    }
    
    public static class ProteinGroupPropertiesComparator implements Comparator<ProteinGroupProperties> {

    	private SORT_BY sortBy;
    	private SORT_ORDER sortOrder;
    	
    	public ProteinGroupPropertiesComparator(SORT_BY sortBy, SORT_ORDER sortOrder) {
    		this.sortBy = sortBy;
    		this.sortOrder = sortOrder;
    	}
    	
		@Override
		public int compare(ProteinGroupProperties o1, ProteinGroupProperties o2) {
			
			if(sortBy == SORT_BY.MOL_WT)
				return compareByMolWt(o1, o2);
			
			if(sortBy == SORT_BY.PI)
				return compareByPi(o1, o2);
			
			if(sortBy == SORT_BY.ACCESSION)
				return compareByAccession(o1, o2);
			
			return 0;
		}

		private int compareByAccession(ProteinGroupProperties o1,
				ProteinGroupProperties o2) {
			
			if(sortOrder == SORT_ORDER.DESC) {
				return o2.getGroupAccession().compareTo(o1.getGroupAccession());
	    	}
	    	else {
	    		return o1.getGroupAccession().compareTo(o2.getGroupAccession());
	    	}
		}

		private int compareByPi(ProteinGroupProperties o1,
				ProteinGroupProperties o2) {
			if(sortOrder == SORT_ORDER.DESC) {
				return Double.valueOf(o2.getGroupPi()).compareTo(o1.getGroupPi());
	    	}
	    	else {
	    		return Double.valueOf(o1.getGroupPi()).compareTo(o2.getGroupPi());
	    	}
		}

		private int compareByMolWt(ProteinGroupProperties o1,
				ProteinGroupProperties o2) {
			if(sortOrder == SORT_ORDER.DESC) {
				return Double.valueOf(o2.getGroupMolecularWt()).compareTo(o1.getGroupMolecularWt());
	    	}
	    	else {
	    		return Double.valueOf(o1.getGroupMolecularWt()).compareTo(o2.getGroupMolecularWt());
	    	}
		}
    }
}