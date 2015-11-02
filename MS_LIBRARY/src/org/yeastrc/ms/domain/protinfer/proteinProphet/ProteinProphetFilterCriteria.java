/**
 * ProteinProphetFilterCriteria.java
 * @author Vagisha Sharma
 * Aug 28, 2009
 * @version 1.0
 */
package org.yeastrc.ms.domain.protinfer.proteinProphet;

import org.yeastrc.ms.domain.protinfer.ProteinFilterCriteria;
import org.yeastrc.ms.domain.protinfer.SORT_BY;
import org.yeastrc.ms.domain.protinfer.SORT_ORDER;

/**
 * 
 */
public class ProteinProphetFilterCriteria extends ProteinFilterCriteria {

    private double minGroupProbability = 0.0;
    private double maxGroupProbability = 1.0;
    
    private double minProteinProbability = 0.0;
    private double maxProteinProbability = 1.0;
    
    private double minPeptideProbability = 0.0;
    private double minUniqPeptideProbability = 0.0;
    
    public static SORT_BY defaultSortBy() {
        return SORT_BY.PROTEIN_PROPHET_GROUP;
    }
    
    public static SORT_ORDER defaultSortOrder() {
        return SORT_ORDER.ASC;
    }
    
    public ProteinProphetFilterCriteria() {}
    
    public ProteinProphetFilterCriteria(ProteinFilterCriteria filterCriteria) {
    	// coverage
        super.setCoverage(filterCriteria.getCoverage());
        super.setMaxCoverage(filterCriteria.getMaxCoverage());
        // molecular wt
        super.setMinMolecularWt(filterCriteria.getMinMolecularWt());
        super.setMaxMolecularWt(filterCriteria.getMaxMolecularWt());
        // pI
        super.setMinPi(filterCriteria.getMinPi());
        super.setMaxPi(filterCriteria.getMaxPi());
        // num peptides
        super.setNumPeptides(filterCriteria.getNumPeptides());
        super.setNumMaxPeptides(filterCriteria.getNumMaxPeptides());
        // num unique peptides
        super.setNumUniquePeptides(filterCriteria.getNumUniquePeptides());
        super.setNumMaxUniquePeptides(filterCriteria.getNumMaxUniquePeptides());
        // num spectra
        super.setNumSpectra(filterCriteria.getNumSpectra());
        super.setNumMaxSpectra(filterCriteria.getNumMaxSpectra());
        // accession
        super.setAccessionLike(filterCriteria.getAccessionLike());
        // description
        super.setDescriptionLike(filterCriteria.getDescriptionLike());
        super.setDescriptionNotLike(filterCriteria.getDescriptionNotLike());
        super.setSearchAllDescriptions(filterCriteria.isSearchAllDescriptions());
        // common name
        super.setCommonNameLike(filterCriteria.getCommonNameLike());
        // peptide string
        super.setPeptide(filterCriteria.getPeptide());
        super.setExactPeptideMatch(filterCriteria.getExactPeptideMatch());
        // exclude I-groups
        super.setExcludeIndistinGroups(filterCriteria.isExcludeIndistinGroups());
        // group proteins
        super.setGroupProteins(filterCriteria.isGroupProteins());
        // parsimonious-ness
        super.setNonParsimonious(filterCriteria.getNonParsimonious());
        super.setParsimonious(filterCriteria.getParsimonious());
        // peptide definition
        super.setPeptideDefinition(filterCriteria.getPeptideDefinition());
        // validation status
        super.setValidationStatus(filterCriteria.getValidationStatus());
        // charge states
        super.setChargeStates(filterCriteria.getChargeStates());
        super.setChargeGreaterThan(filterCriteria.getChargeGreaterThan());
        // sorting 
        super.setSortOrder(filterCriteria.getSortOrder());
        super.setSortBy(filterCriteria.getSortBy());
        // gene ontology filter(s)
        super.setGoFilterCriteria(filterCriteria.getGoFilterCriteria());
    }
    
    public boolean equals(Object o) {
        if(!super.equals(o))
            return false;
        if(!(o instanceof ProteinProphetFilterCriteria))
            return false;
        if(!super.equals(o))
        	return false;
        ProteinProphetFilterCriteria that = (ProteinProphetFilterCriteria)o;
        if(this.minGroupProbability != that.minGroupProbability)          return false;
        if(this.maxGroupProbability != that.maxGroupProbability)          return false;
        
        if(this.minProteinProbability != that.minProteinProbability)          return false;
        if(this.maxProteinProbability != that.maxProteinProbability)          return false;

        if(this.minPeptideProbability != that.minPeptideProbability)			return false;
        if(this.minUniqPeptideProbability != that.minUniqPeptideProbability)	return false;
        return true;
    }

    public double getMinPeptideProbability() {
		return minPeptideProbability;
	}

	public void setMinPeptideProbability(double minPeptideProbability) {
		this.minPeptideProbability = minPeptideProbability;
	}

	public double getMinUniqPeptideProbability() {
		return minUniqPeptideProbability;
	}

	public void setMinUniqPeptideProbability(double minUniqPeptideProbability) {
		this.minUniqPeptideProbability = minUniqPeptideProbability;
	}

	public double getMinGroupProbability() {
        return minGroupProbability;
    }

    public void setMinGroupProbability(double minProbability) {
        this.minGroupProbability = minProbability;
    }

    public double getMaxGroupProbability() {
        return maxGroupProbability;
    }

    public void setMaxGroupProbability(double maxProbability) {
        this.maxGroupProbability = maxProbability;
    }
    
    
    public double getMinProteinProbability() {
        return minProteinProbability;
    }

    public void setMinProteinProbability(double minProteinProbability) {
        this.minProteinProbability = minProteinProbability;
    }

    public double getMaxProteinProbability() {
        return maxProteinProbability;
    }

    public void setMaxProteinProbability(double maxProteinProbability) {
        this.maxProteinProbability = maxProteinProbability;
    }
}
