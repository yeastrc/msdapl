/**
 * GenericProteinInferProtein.java
 * @author Vagisha Sharma
 * Dec 30, 2008
 * @version 1.0
 */
package org.yeastrc.ms.domain.protinfer;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 */
public class GenericProteinferProtein <T extends GenericProteinferPeptide<?,?>>{

    private int id;
    private int pinferId;
    private int nrseqProteinId;
    private double coverage;
    private String userAnnotation;
    private ProteinUserValidation userValidation;
    
    private List<T> peptideList;
    private int spectrumCount;
    
    private PeptideDefinition peptideDefinition;
    
    public GenericProteinferProtein() {
        peptideList = new ArrayList<T>();
        peptideDefinition = new PeptideDefinition(false, false);
    }

    public void setPeptideDefinition(PeptideDefinition peptideDefinition) {
        this.peptideDefinition = peptideDefinition;
    }
    
    public PeptideDefinition getPeptideDefinition() {
        return this.peptideDefinition;
    }
    
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getProteinferId() {
        return pinferId;
    }

    public void setProteinferId(int pinferId) {
        this.pinferId = pinferId;
    }

    public int getNrseqProteinId() {
        return nrseqProteinId;
    }

    public void setNrseqProteinId(int nrseqProteinId) {
        this.nrseqProteinId = nrseqProteinId;
    }

    public double getCoverage() {
        return Math.round(coverage*100.0) / 100.0;
    }

    public void setCoverage(double coverage) {
        this.coverage = coverage;
    }

    public String getUserAnnotation() {
        return userAnnotation;
    }

    public void setUserAnnotation(String userAnnotation) {
        this.userAnnotation = userAnnotation;
    }

    public ProteinUserValidation getUserValidation() {
        return userValidation;
    }
    
    public String getUserValidationString() {
        return String.valueOf(userValidation.getStatusChar());
    }

    public void setUserValidation(ProteinUserValidation userValidation) {
        this.userValidation = userValidation;
    }

    public List<T> getPeptides() {
        return peptideList;
    }

    public void setPeptides(List<T> peptideList) {
        this.peptideList = peptideList;
    }
    
    public void addPeptide(T peptide) {
        peptideList.add(peptide);
    }
    
    public int getSequenceCount() {
    	return peptideList.size();
    }
    
    public int getUniqueSequenceCount() {
    	PeptideDefinition def = new PeptideDefinition();
    	def.setUseCharge(false);
    	def.setUseMods(false);
    	return getUniquePeptideCountForDefinition(def);
    }
    
    /**
     * Returns the number of unique combinations of sequence + mods + charge
     * @return
     */
    public int getIonCount() {
    	PeptideDefinition def = new PeptideDefinition();
    	def.setUseCharge(true);
    	def.setUseMods(true);
    	return getPeptideCountForDefinition(def);
    }
    
    public int getUniqueIonCount() {
    	PeptideDefinition def = new PeptideDefinition();
    	def.setUseCharge(true);
    	def.setUseMods(true);
    	return getUniquePeptideCountForDefinition(def);
    }
    
    public int getPeptideCount() {
        // peptide is uniquely defined by its sequence
        if(!peptideDefinition.isUseCharge() && !peptideDefinition.isUseMods()) 
            return getSequenceCount();
        
        else {
            return getPeptideCountForDefinition(peptideDefinition);
        }
    }

	private int getPeptideCountForDefinition(PeptideDefinition def) {
		int cnt = 0;
		for(T peptide: peptideList)
		    cnt += peptide.getNumDistinctPeptides(def);
		return cnt;
	}
    
    public int getUniquePeptideCount() {
        
        return getUniquePeptideCountForDefinition(peptideDefinition);
    }
    
    private int getUniquePeptideCountForDefinition(PeptideDefinition def) {
    	
    	int uniqCnt = 0;
        for(T peptide: peptideList) {
            if(!peptide.isUniqueToProtein())
                continue;
            uniqCnt += peptide.getNumDistinctPeptides(def);
        }
        return uniqCnt;
    }
    
    public int getSpectrumCount() {
        return this.spectrumCount;
    }
    
    public void setSpectrumCount(int spectrumCount) {
    	this.spectrumCount = spectrumCount;
    }
}
