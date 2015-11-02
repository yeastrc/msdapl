package org.yeastrc.ms.domain.protinfer.idpicker;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import org.yeastrc.ms.domain.protinfer.GenericProteinferProtein;

public class GenericIdPickerProtein <T extends GenericIdPickerPeptide<?,?>> extends GenericProteinferProtein<T>{

	private int clusterLabel = -1;
    private int proteinGroupLabel = -1;
    
    private boolean isParsimonious;
    private boolean isSubset;
    
    // normalized spectrum abundance factor
    private double nsaf = 2.0; // NSAF ranges from 0 to 1. 
                               // We set the default to an impossible value.
    	                       // This value means that NSAF was not calculated for this protein.
    
    private List<Integer> superProteinIds;
    private List<Integer> subsetProteinIds;
    
    private static final DecimalFormat df = new DecimalFormat("0.000000");
    
    public GenericIdPickerProtein() {
    	this.superProteinIds = new ArrayList<Integer>();
    	this.subsetProteinIds = new ArrayList<Integer>();
    }
    
	public int getClusterLabel() {
		return clusterLabel;
	}
	public void setClusterLabel(int clusterLabel) {
		this.clusterLabel = clusterLabel;
	}
	
	public int getProteinGroupLabel() {
		return proteinGroupLabel;
	}
	public void setProteinGroupLabel(int proteinGroupLabel) {
		this.proteinGroupLabel = proteinGroupLabel;
	}
	
	public boolean getIsParsimonious() {
		return this.isParsimonious;
	}
	public void setIsParsimonious(boolean isParsimonious) {
		this.isParsimonious = isParsimonious;
	}

	public boolean getIsSubset() {
		return isSubset;
	}
	public void setIsSubset(boolean isSubset) {
		this.isSubset = isSubset;
	}
		
	public double getNsaf() {
        return nsaf;
    }
    public void setNsaf(double nsaf) {
        this.nsaf = nsaf;
    }
    public String getNsafFormatted() {
    	if(nsaf != 2)
    		return df.format(nsaf);
    	else
    		return "-1"; // Display -1 if NSAF was not calculated
    }
    
    public List<Integer> getSuperProteinIds() {
    	return superProteinIds;
	}

	public void setSuperProteinIds(List<Integer> superProteinIds) {
		this.superProteinIds = superProteinIds;
	}
	
	public List<Integer> getSubsetProteinIds() {
		return subsetProteinIds;
	}

	public void setSubsetProteinIds(List<Integer> subProteinIds) {
		this.subsetProteinIds = subProteinIds;
	}

	public boolean matchesPeptideGroup(int peptideGrpLabel) {
        for(GenericIdPickerPeptide<?,?> pept: this.getPeptides()) {
            if(pept.getPeptideGroupLabel() == peptideGrpLabel)
                return true;
        }
        return false;
    }
    
}

