/**
 * ProteinPropertiesFilters.java
 * @author Vagisha Sharma
 * Mar 5, 2010
 * @version 1.0
 */
package org.yeastrc.www.compare;

import org.yeastrc.ms.domain.protinfer.GOProteinFilterCriteria;



/**
 * 
 */
public class ProteinPropertiesFilters {

	private String commonNameLike;
	private String accessionLike;
    private String descriptionLike;
    private String descriptionNotLike;
    private boolean searchAllDescriptions = false;
    
    private GOProteinFilterCriteria goFilter;
    
    private double minMolWt = 0.0;
    private double maxMolWt = Double.MAX_VALUE;
    
    private double minPi = 0.0;
    private double maxPi = Double.MAX_VALUE;
    
    private int minPeptideCount = 0;
    private int maxPeptideCount = Integer.MAX_VALUE;
    private int minUniqPeptideCount = 0;
    private int maxUniqPeptideCount = Integer.MAX_VALUE;
    
    private int minSpectrumCount = 0;
    private int maxSpectrumCount = Integer.MAX_VALUE;
    
    // ProteinProphet related
    private boolean hasProteinProphetFilters = false;
	private double peptideProbability = 0.0;
    private boolean applyToPeptide = true;
    private boolean applyToUniqPeptide = false;
    private double proteinProphetError = 0.01;
    private boolean useGroupProbability = true;
    
   
    public ProteinPropertiesFilters() {}
    
    public boolean equals(Object o) {
    	
    	if(this == o)
            return true;
        if(!(o instanceof ProteinPropertiesFilters))
            return false;
        ProteinPropertiesFilters that = (ProteinPropertiesFilters)o;
        
        if(this.commonNameLike != that.commonNameLike)					return false;
        if(this.accessionLike != that.accessionLike)          			return false;
        if(this.descriptionLike != that.descriptionLike)      			return false;
        if(this.descriptionNotLike!= that.descriptionNotLike)          	return false;
        if(this.searchAllDescriptions != that.searchAllDescriptions)	return false;
        
        if(this.minMolWt != that.minMolWt)								return false;
        if(this.maxMolWt != that.maxMolWt)								return false;
        if(this.minPi != that.minPi)									return false;
        if(this.maxPi != that.maxPi)									return false;
        
        if(this.minPeptideCount != that .minPeptideCount)				return false;
        if(this.maxPeptideCount != that.maxPeptideCount)				return false;
        if(this.minUniqPeptideCount != that.minUniqPeptideCount)		return false;
        if(this.maxUniqPeptideCount != that.maxUniqPeptideCount)		return false;
        
        if(this.minSpectrumCount != that.minSpectrumCount)				return false;
        if(this.maxSpectrumCount != that.maxSpectrumCount)				return false;
        
        if(this.hasProteinProphetFilters != that.hasProteinProphetFilters)	return false;
        if(this.peptideProbability != that.peptideProbability)				return false;
        if(this.applyToPeptide != that.applyToPeptide)						return false;
        if(this.applyToUniqPeptide != that.applyToUniqPeptide)				return false;
        if(this.proteinProphetError != that.proteinProphetError)			return false;
        if(this.useGroupProbability != that.useGroupProbability)			return false;
        
        return true;
    }
 
    public boolean getHasProteinProphetFilters() {
		return hasProteinProphetFilters;
	}

	public void setHasProteinProphetFilters(boolean hasProteinProphetFilters) {
		this.hasProteinProphetFilters = hasProteinProphetFilters;
	}

	public double getPeptideProbability() {
		return peptideProbability;
	}

	public void setPeptideProbability(double peptideProbability) {
		this.peptideProbability = peptideProbability;
	}

	public boolean isApplyToPeptide() {
		return applyToPeptide;
	}

	public void setApplyToPeptide(boolean applyToPeptide) {
		this.applyToPeptide = applyToPeptide;
	}

	public boolean isApplyToUniqPeptide() {
		return applyToUniqPeptide;
	}

	public void setApplyToUniqPeptide(boolean applyToUniqPeptide) {
		this.applyToUniqPeptide = applyToUniqPeptide;
	}

	public double getProteinProphetError() {
		return proteinProphetError;
	}

	public void setProteinProphetError(double proteinProphetError) {
		this.proteinProphetError = proteinProphetError;
	}

	public boolean isUseGroupProbability() {
		return useGroupProbability;
	}

	public void setUseGroupProbability(boolean useGroupProbability) {
		this.useGroupProbability = useGroupProbability;
	}
	
    public int getMinPeptideCount() {
		return minPeptideCount;
	}

	public void setMinPeptideCount(int minPeptideCount) {
		this.minPeptideCount = minPeptideCount;
	}

	public int getMaxPeptideCount() {
		return maxPeptideCount;
	}

	public void setMaxPeptideCount(int maxPeptideCount) {
		this.maxPeptideCount = maxPeptideCount;
	}

	public int getMinUniqPeptideCount() {
		return minUniqPeptideCount;
	}

	public void setMinUniqPeptideCount(int minUniqPeptideCount) {
		this.minUniqPeptideCount = minUniqPeptideCount;
	}

	public int getMaxUniqPeptideCount() {
		return maxUniqPeptideCount;
	}

	public void setMaxUniqPeptideCount(int maxUniqPeptideCount) {
		this.maxUniqPeptideCount = maxUniqPeptideCount;
	}
	
    public int getMinSpectrumCount() {
		return minSpectrumCount;
	}

	public void setMinSpectrumCount(int minSpectrumCount) {
		this.minSpectrumCount = minSpectrumCount;
	}

	public int getMaxSpectrumCount() {
		return maxSpectrumCount;
	}

	public void setMaxSpectrumCount(int maxSpectrumCount) {
		this.maxSpectrumCount = maxSpectrumCount;
	}

	public String getCommonNameLike() {
		return commonNameLike;
	}

	public void setCommonNameLike(String commonNameLike) {
		this.commonNameLike = commonNameLike;
	}
	
	public boolean hasCommonNameFilter() {
    	return (this.commonNameLike != null && this.commonNameLike.trim().length() > 0);
    }

	public String getAccessionLike() {
        return accessionLike;
    }

    public void setAccessionLike(String accessionLike) {
        this.accessionLike = accessionLike;
    }
    
    public boolean hasAccessionFilter() {
    	return (this.accessionLike != null && this.accessionLike.trim().length() > 0);
    }
    
    public String getDescriptionLike() {
        return descriptionLike;
    }
    
    public void setDescriptionLike(String descriptionLike) {
        this.descriptionLike = descriptionLike;
    }
    
    public boolean hasDescriptionLikeFilter() {
    	return (this.descriptionLike != null && this.descriptionLike.trim().length() > 0);
    }
    
    public String getDescriptionNotLike() {
        return descriptionNotLike;
    }
    
    public void setDescriptionNotLike(String descriptionNotLike) {
        this.descriptionNotLike = descriptionNotLike;
    }
    
    public boolean hasDescriptionNotLikeFilter() {
    	return (this.descriptionNotLike != null && this.descriptionNotLike.trim().length() > 0);
    }
    
    public boolean isSearchAllDescriptions() {
		return searchAllDescriptions;
	}

	public void setSearchAllDescriptions(boolean searchAllDescriptions) {
		this.searchAllDescriptions = searchAllDescriptions;
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
    
    public boolean hasPeptideCountFilter() {
    	return (minPeptideCount != 0 || maxPeptideCount != Integer.MAX_VALUE);
    }
    
    public boolean hasUniquePeptideCountFilter() {
    	return (minUniqPeptideCount != 0 || maxUniqPeptideCount != Integer.MAX_VALUE);
    }
    
    public boolean hasSpectrumCountFilter() {
    	return (minSpectrumCount != 0 && maxSpectrumCount != Integer.MAX_VALUE);
    }

	public GOProteinFilterCriteria getGoFilter() {
		return goFilter;
	}

	public void setGoFilter(GOProteinFilterCriteria goFilter) {
		this.goFilter = goFilter;
	}
}
