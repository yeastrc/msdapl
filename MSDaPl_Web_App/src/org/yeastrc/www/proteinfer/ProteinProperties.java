/**
 * ProteinProperties.java
 * @author Vagisha Sharma
 * Nov 3, 2009
 * @version 1.0
 */

package org.yeastrc.www.proteinfer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.yeastrc.ms.domain.protinfer.SORT_ORDER;


/**
 * 
 */
public class ProteinProperties {

    private int pinferProteinId;
    private int nrseqId;
    private int proteinGroupId;
    
    private Set<String> accessions;
    
    private double molecularWt = -1.0;
    private double pi = -1.0;
    
    public ProteinProperties(int pinferProteinId) {
        this.pinferProteinId = pinferProteinId;
    }
    
    public int getPinferProteinId() {
        return pinferProteinId;
    }

    public void setPinferProteinId(int pinferProteinId) {
        this.pinferProteinId = pinferProteinId;
    }

    public int getNrseqId() {
        return nrseqId;
    }

    public void setNrseqId(int nrseqId) {
        this.nrseqId = nrseqId;
    }

    public int getProteinGroupId() {
        return proteinGroupId;
    }
    
    public void setProteinGroupId(int proteinGroupId) {
        this.proteinGroupId = proteinGroupId;
    }

    public double getMolecularWt() {
        return molecularWt;
    }

    public void setMolecularWt(double molecularWt) {
        this.molecularWt = molecularWt;
    }

    public double getPi() {
        return pi;
    }

    public void setPi(double pi) {
        this.pi = pi;
    }
    
    public boolean piInitialized() {
    	return pi != -1.0;
    }
    
    public void setAccession(Set<String> accessions) {
    	this.accessions = accessions;
    }
    
    public Set<String> getAccessions() {
    	return this.accessions;
    }
    
    public String getAccession(SORT_ORDER sortOrder) {
    	List<String> acc = new ArrayList<String>(accessions);
    	if(sortOrder == SORT_ORDER.DESC) 
    		Collections.sort(acc, Collections.reverseOrder());
    	else
    		Collections.sort(acc);
    	return acc.get(0);
    }
    
    public boolean molecularWtInitialized() {
    	return molecularWt != -1.0;
    }
    
    public boolean accessionInitialized() {
    	return accessions != null;
    }
    
    public boolean hasPartialAccession(String accession) {
    	
    	for(String acc: this.accessions) {
    		if(acc.contains(accession))
    			return true;
    	}
    	return false;
    }
    
}
