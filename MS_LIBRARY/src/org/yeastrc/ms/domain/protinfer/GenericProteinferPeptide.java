package org.yeastrc.ms.domain.protinfer;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public abstract class GenericProteinferPeptide <S extends ProteinferSpectrumMatch, T extends GenericProteinferIon<S>> {

    private int id;
    private int pinferId;
    private String sequence;
    private boolean uniqueToProtein;
    private List<T> ionList;
    private int spectrumCount;

    public GenericProteinferPeptide() {
        ionList = new ArrayList<T>();
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

    public List<T> getIonList() {
        return ionList;
    }

    public void setIonList(List<T> spectrumMatchList) {
        this.ionList = spectrumMatchList;
    }

    public void addIon(T ion) {
        ionList.add(ion);
    }
    
    public String getSequence() {
        return sequence;
    }

    public void setSequence(String sequence) {
        this.sequence = sequence;
    }

    public boolean isUniqueToProtein() {
        return uniqueToProtein;
    }
    
    public void setUniqueToProtein(boolean unique) {
        this.uniqueToProtein = unique;
    }

    public void setSpectrumCount(int spectrumCount) {
		this.spectrumCount = spectrumCount;
	}

	public int getSpectrumCount() {
        return this.spectrumCount;
    }
    
    public S getBestSpectrumMatch() {
        S best = null;
        for(T ion: ionList) {
            if(best == null)
                best = ion.getBestSpectrumMatch();
            else {
                S s = ion.getBestSpectrumMatch();
                best = best.getRank() < s.getRank() ? best : s;
            }
        }
        return best;
    }
    
    /**
     * Returns the number of distinct ions this peptide represents based on the 
     * given PeptideDefinition. 
     * @param peptideDef
     * @return
     */
    public int getNumDistinctPeptides(PeptideDefinition peptideDef) {
        
        // peptide uniquely defined by sequence
        if(!peptideDef.isUseCharge() && !peptideDef.isUseMods())
            return 1;
        
        // peptide uniquely defined by sequence and charge
        if(peptideDef.isUseCharge() && !peptideDef.isUseMods()) {
            Set<Integer> chgStates = new HashSet<Integer>();
            for(T ion: ionList) {
                chgStates.add(ion.getCharge());
            }
            return chgStates.size();
        }
        // peptide uniquely defined by sequence and modification state
        if(!peptideDef.isUseCharge() && peptideDef.isUseMods()) {
            Set<Integer> modStates = new HashSet<Integer>();
            for(T ion: ionList) {
                modStates.add(ion.getModificationStateId());
            }
            return modStates.size();
        }
        // peptide uniquely defined by sequence, charge and modification state
        if(peptideDef.isUseCharge() && peptideDef.isUseMods()) {
            return ionList.size();
        }
        
        // this should never happen
        return 0;
    }
    
    protected abstract GenericProteinferPeptide<S,T>  newPeptide();
    
    public String toString() {
    	
    	StringBuilder buf = new StringBuilder();
    	if(this.isUniqueToProtein())
    		buf.append("U\t");
    	else
    		buf.append("\t");
    	buf.append(this.sequence);
    	return buf.toString();
    }
    
}