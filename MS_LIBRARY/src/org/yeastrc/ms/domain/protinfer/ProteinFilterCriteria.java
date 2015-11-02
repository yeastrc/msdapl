/**
 * ProteinFilterCriteria.java
 * @author Vagisha Sharma
 * Jan 6, 2009
 * @version 1.0
 */
package org.yeastrc.ms.domain.protinfer;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 
 */
public class ProteinFilterCriteria implements Serializable {

    private int numPeptides = 1;
    private int numMaxPeptides = Integer.MAX_VALUE;
    private int numUniquePeptides = 0;
    private int numMaxUniquePeptides = Integer.MAX_VALUE;
    private PeptideDefinition peptideDefinition = new PeptideDefinition();
    
    private boolean parsimonious = true;
    private boolean nonParsimonious = true;
    
    private boolean subset = true;
    private boolean nonSubset = true;
    
    private boolean groupProteins = true;
    
    private boolean excludeIndistinGroups = false;
    
    private String peptide;
    private boolean exactPeptideMatch = true;
    
    private List<ProteinUserValidation> validationStatus = new ArrayList<ProteinUserValidation>();
    
    private List<Integer> chargeStates = new ArrayList<Integer>();
    private int chargeGreaterThan = -1;
    
    private int numSpectra = 1;
    private int numMaxSpectra = Integer.MAX_VALUE;
    private double coverage = 0.0;
    private double maxCoverage = 100.0;
    
    private String commonNameLike;
    private String accessionLike;
    private String descriptionLike;
    private String descriptionNotLike;
    private boolean searchAllDescriptions = false;
    
    private GOProteinFilterCriteria goFilterCriteria;
    
    private double minMolWt = 0.0;
    private double maxMolWt = Double.MAX_VALUE;
    
    private double minPi = 0.0;
    private double maxPi = Double.MAX_VALUE;
    
    private SORT_BY sortBy = SORT_BY.NONE;
    private SORT_ORDER sortOrder = SORT_ORDER.ASC;
    
    
    public SORT_ORDER getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(SORT_ORDER sortOrder) {
        this.sortOrder = sortOrder;
    }

    public SORT_BY getSortBy() {
        return sortBy;
    }
    
    public static SORT_BY defaultSortBy() {
        return SORT_BY.COVERAGE;
    }
    
    public static SORT_ORDER defaultSortOrder() {
        return SORT_ORDER.DESC;
    }
    
    public void setSortBy(SORT_BY sortBy) {
        this.sortBy = sortBy;
    }

    public int getNumPeptides() {
        return numPeptides;
    }
    
    public void setNumPeptides(int numPeptides) {
        this.numPeptides = numPeptides;
    }
    
    public int getNumUniquePeptides() {
        return numUniquePeptides;
    }
    
    public void setNumUniquePeptides(int numUniquePeptides) {
        this.numUniquePeptides = numUniquePeptides;
    }
    
    public PeptideDefinition getPeptideDefinition() {
        return peptideDefinition;
    }
    
    public void setPeptideDefinition(PeptideDefinition peptideDefinition) {
        this.peptideDefinition = peptideDefinition;
    }
    
    public int getNumSpectra() {
        return numSpectra;
    }
    
    public void setNumSpectra(int numSpectra) {
        this.numSpectra = numSpectra;
    }
    
    public double getCoverage() {
        return coverage;
    }
    
    public void setCoverage(double coverage) {
        this.coverage = coverage;
    }

    public boolean getParsimonious() {
        return parsimonious;
    }
    
    public void setParsimonious(boolean parsimonious) {
        this.parsimonious = parsimonious;
    }
    
    public boolean getNonParsimonious() {
        return nonParsimonious;
    }
    
    public void setNonParsimonious(boolean nonParsimonious) {
        this.nonParsimonious = nonParsimonious;
    }
    
    public boolean getSubset() {
		return subset;
	}

	public void setSubset(boolean subset) {
		this.subset = subset;
	}

	public boolean getNonSubset() {
		return nonSubset;
	}

	public void setNonSubset(boolean nonSubset) {
		this.nonSubset = nonSubset;
	}

	public void setParsimoniousOnly() {
        setParsimonious(true);
        setNonParsimonious(false);
    }
    
    public void setNonParsimoniousOnly() {
        setParsimonious(false);
        setNonParsimonious(true);
    }
    
    public boolean parsimoniousOnly() {
        return getParsimonious() && !getNonParsimonious();
    }
    
    public boolean nonParsimoniousOnly() {
        return !getParsimonious() && getNonParsimonious();
    }
    
    public boolean subsetOnly() {
        return getSubset() && !getNonSubset();
    }
    
    public boolean nonSubsetOnly() {
        return !getSubset() && getNonSubset();
    }

	public boolean isGroupProteins() {
        return groupProteins;
    }

    public void setGroupProteins(boolean groupProteins) {
        this.groupProteins = groupProteins;
    }
    
    public boolean isExcludeIndistinGroups() {
        return this.excludeIndistinGroups;
    }
    
    public void setExcludeIndistinGroups(boolean exclude) {
        this.excludeIndistinGroups = exclude;
    }

    
    public GOProteinFilterCriteria getGoFilterCriteria() {
		return goFilterCriteria;
	}

	public void setGoFilterCriteria(GOProteinFilterCriteria goFilterCriteria) {
		this.goFilterCriteria = goFilterCriteria;
	}

	public String getCommonNameLike() {
		return commonNameLike;
	}

	public void setCommonNameLike(String commonNameLike) {
		this.commonNameLike = commonNameLike;
	}

	public String getAccessionLike() {
        return accessionLike;
    }

    public void setAccessionLike(String accessionLike) {
        this.accessionLike = accessionLike;
    }
    
    public String getDescriptionLike() {
        return descriptionLike;
    }
    
    public void setDescriptionLike(String descriptionLike) {
        this.descriptionLike = descriptionLike;
    }
    
    public String getDescriptionNotLike() {
        return descriptionNotLike;
    }
    
    public void setDescriptionNotLike(String descriptionNotLike) {
        this.descriptionNotLike = descriptionNotLike;
    }
    
    public boolean isSearchAllDescriptions() {
		return searchAllDescriptions;
	}

	public void setSearchAllDescriptions(boolean searchAllDescriptions) {
		this.searchAllDescriptions = searchAllDescriptions;
	}
	
    public List<ProteinUserValidation> getValidationStatus() {
        return validationStatus;
    }

    public void setValidationStatus(List<ProteinUserValidation> validationStatus) {
        this.validationStatus = validationStatus;
    }
    
    public void setValidationStatus(String[] validationStatusArr) {
        for(String vs: validationStatusArr) {
            if(vs != null && vs.length() == 1) {
                ProteinUserValidation s = ProteinUserValidation.getStatusForChar(vs.charAt(0));
                if(s != null)   this.validationStatus.add(s);
            }
        }
    }
    
    public List<Integer> getChargeStates() {
        return this.chargeStates;
    }
    
    public void setChargeStates(List<Integer> chargeStates) {
        this.chargeStates = chargeStates;
    }
    
    public int getChargeGreaterThan() {
        return this.chargeGreaterThan;
    }
    
    public void setChargeGreaterThan(int chargeGreaterThan) {
        this.chargeGreaterThan = chargeGreaterThan;
    }

    public int getNumMaxPeptides() {
        return numMaxPeptides;
    }

    public void setNumMaxPeptides(int numMaxPeptides) {
        this.numMaxPeptides = numMaxPeptides;
    }

    public int getNumMaxUniquePeptides() {
        return numMaxUniquePeptides;
    }

    public void setNumMaxUniquePeptides(int numMaxUniquePeptides) {
        this.numMaxUniquePeptides = numMaxUniquePeptides;
    }

    public int getNumMaxSpectra() {
        return numMaxSpectra;
    }

    public void setNumMaxSpectra(int numMaxSpectra) {
        this.numMaxSpectra = numMaxSpectra;
    }

    public double getMaxCoverage() {
        return maxCoverage;
    }

    public void setMaxCoverage(double maxCoverage) {
        this.maxCoverage = maxCoverage;
    }
    
    public void setMinMolecularWt(double molWt) {
        this.minMolWt = molWt;
    }
    
    public double getMinMolecularWt() {
        return minMolWt;
    }
    
    public void setMaxMolecularWt(double molWt) {
        this.maxMolWt = molWt;
    }
    
    public double getMaxMolecularWt() {
        return maxMolWt;
    }
    
    public boolean hasMolecularWtFilter() {
        return (minMolWt != 0 || maxMolWt != Double.MAX_VALUE);
    }
    
    
    public void setMinPi(double pi) {
        this.minPi = pi;
    }
    
    public double getMinPi() {
        return minPi;
    }
    
    public void setMaxPi(double pi) {
        this.maxPi = pi;
    }
    
    public double getMaxPi() {
        return maxPi;
    }
    
    public boolean hasPiFilter() {
        return (minPi != 0 || maxPi != Double.MAX_VALUE);
    }
    
    
    //-------------------------------------------------------------
    // PEPTIDE FILTER
    //-------------------------------------------------------------
    public String getPeptide() {
        return peptide;
    }
    public void setPeptide(String peptide) {
        this.peptide = peptide;
    }
    public void setExactPeptideMatch(boolean exact) {
        this.exactPeptideMatch = exact;
    }
    public boolean getExactPeptideMatch() {
        return this.exactPeptideMatch;
    }
    
    
    public boolean equals(Object o) {
        if(this == o)
            return true;
        if(!(o instanceof ProteinFilterCriteria))
            return false;
        ProteinFilterCriteria that = (ProteinFilterCriteria)o;
        if(this.numPeptides != that.numPeptides)                return false;
        if(this.numMaxPeptides != that.numMaxPeptides)          return false;
        if(this.numUniquePeptides != that.numUniquePeptides)    return false;
        if(this.numMaxUniquePeptides != that.numMaxUniquePeptides)  return false;
        if(this.numSpectra != that.numSpectra)                  return false;
        if(this.numMaxSpectra != that.numMaxSpectra)            return false;
        if(this.coverage != that.coverage)                      return false;
        if(this.maxCoverage != that.maxCoverage)                return false;
        if(this.minMolWt != that.minMolWt)                      return false;
        if(this.maxMolWt != that.maxMolWt)                      return false;
        if(this.minPi != that.minPi)                            return false;
        if(this.maxPi != that.maxPi)                            return false;
        if(this.excludeIndistinGroups != that.excludeIndistinGroups)    return false;
//        if(this.groupProteins != that.groupProteins)            return false;
        if(this.parsimonious != that.parsimonious)              return false;
        if(this.nonParsimonious != that.nonParsimonious)        return false;
        if(this.subset != that.subset)							return false;
        if(this.nonSubset != that.nonSubset)					return false;
        
        if(this.chargeStates.size() != that.chargeStates.size())
            return false;
        else {
            for(int chg: this.chargeStates) {
                if(!that.chargeStates.contains(chg))
                    return false;
            }
        }
        if(this.chargeGreaterThan != that.chargeGreaterThan) 
            return false;
        
        if(this.validationStatus.size() != that.validationStatus.size()) return false;
        else {
            for(ProteinUserValidation vs: this.validationStatus)
                if(!that.validationStatus.contains(vs))  return false;
        }
        
        if(!this.peptideDefinition.equals(that.peptideDefinition))  return false;
        
        if(goFilterCriteria == null) {
        	if(that.goFilterCriteria != null)	return false;
        }
        else {
        	if(!(this.goFilterCriteria.equals(that.goFilterCriteria)))
        		return false;
        }
        
        if(commonNameLike == null) {
            if(that.commonNameLike != null)  return false;
        }
        else {
            if(!this.commonNameLike.equalsIgnoreCase(that.commonNameLike))
                return false;
        }
        
        if(accessionLike == null) {
            if(that.accessionLike != null)  return false;
        }
        else {
            if(!this.accessionLike.equalsIgnoreCase(that.accessionLike))
                return false;
        }
        
        if(descriptionLike == null) {
            if(that.descriptionLike != null)  return false;
        }
        else {
            if(!this.descriptionLike.equalsIgnoreCase(that.descriptionLike))
                return false;
        }
        
        if(descriptionNotLike == null) {
            if(that.descriptionNotLike != null)  return false;
        }
        else {
            if(!this.descriptionNotLike.equalsIgnoreCase(that.descriptionNotLike))
                return false;
        }
        
        if(this.searchAllDescriptions != that.searchAllDescriptions)
        	return false;
        
        if(peptide == null) {
            if(that.peptide != null)  return false;
        }
        else {
            if(!this.peptide.equalsIgnoreCase(that.peptide))
                return false;
            if(this.exactPeptideMatch != that.exactPeptideMatch)
                return false;
        }
        
        return true;
    }
    
}
