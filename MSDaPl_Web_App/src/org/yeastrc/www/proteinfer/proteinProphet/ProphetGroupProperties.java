/**
 * ProphetGroupProperties.java
 * @author Vagisha Sharma
 * Mar 30, 2010
 * @version 1.0
 */
package org.yeastrc.www.proteinfer.proteinProphet;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.yeastrc.ms.domain.protinfer.SORT_BY;
import org.yeastrc.ms.domain.protinfer.SORT_ORDER;
import org.yeastrc.www.proteinfer.ProteinGroupProperties;
import org.yeastrc.www.proteinfer.ProteinProperties;
import org.yeastrc.www.proteinfer.ProteinGroupProperties.ProteinGroupPropertiesComparator;

/**
 * 
 */
public class ProphetGroupProperties {

	private List<ProteinGroupProperties> iGroupProperties;
    private SORT_ORDER sortOrder;
    private int prophetGroupId;
    
    ProphetGroupProperties(int prophetGroupId, SORT_ORDER sortOrder) {
    	iGroupProperties = new ArrayList<ProteinGroupProperties>();
    	this.prophetGroupId = prophetGroupId;
        this.sortOrder = sortOrder;
    }
    
    void add(ProteinGroupProperties grpProps) {
        this.iGroupProperties.add(grpProps);
    }
    
    List<ProteinGroupProperties> getIndistinguishableGroups() {
    	return iGroupProperties;
    }
    
    double getGroupMolecularWt() {
        return getSortedByMolWt().get(0).getMolecularWt();
    }
    
    double getGroupPi() {
        return getSortedByPi().get(0).getPi();
    }
    
    String getGroupAccession() {
    	return getSortedByAccession().get(0).getAccession(sortOrder);
    }
    
    int getProphetGroupId() {
        return prophetGroupId;
    }
    
    List<ProteinProperties> getSortedByMolWt() {
    	
    	Collections.sort(iGroupProperties, new ProteinGroupPropertiesComparator(SORT_BY.MOL_WT, sortOrder));
    	
    	List<ProteinProperties> propsList = new ArrayList<ProteinProperties>();
    	for(ProteinGroupProperties props: iGroupProperties) {
    		List<ProteinProperties> pl = props.getProteinProperties();
    		propsList.addAll(pl);
    	}
        return propsList;
    }
    
    List<ProteinProperties> getSortedByPi() {
    	
    	Collections.sort(iGroupProperties, new ProteinGroupPropertiesComparator(SORT_BY.PI, sortOrder));
    	
    	List<ProteinProperties> propsList = new ArrayList<ProteinProperties>();
    	for(ProteinGroupProperties props: iGroupProperties) {
    		List<ProteinProperties> pl = props.getProteinProperties();
    		propsList.addAll(pl);
    	}
        return propsList;
    }
    
    List<ProteinProperties> getSortedByAccession() {
    	
    	Collections.sort(iGroupProperties, new ProteinGroupPropertiesComparator(SORT_BY.ACCESSION, sortOrder));
    	
    	List<ProteinProperties> propsList = new ArrayList<ProteinProperties>();
    	for(ProteinGroupProperties props: iGroupProperties) {
    		List<ProteinProperties> pl = props.getProteinProperties();
    		propsList.addAll(pl);
    	}
        return propsList;
    }
}
