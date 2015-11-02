package org.yeastrc.ms.domain.protinfer.idpicker;

import java.util.ArrayList;
import java.util.List;

public class IdPickerProteinGroup {

    private final int pinferId;
    
    private List<IdPickerProteinBase> proteins;
    
    public IdPickerProteinGroup(int pinferId) {
        this.pinferId = pinferId;
        proteins = new ArrayList<IdPickerProteinBase>();
    }
    
    public int getProteinferId() {
        return pinferId;
    }

    public int getGroupLabel() {
    	if(proteins.size() > 0)
            return proteins.get(0).getProteinGroupLabel();
        else
            return 0;
    }
    
    public int getClusterLabel() {
        if(proteins.size() > 0)
            return proteins.get(0).getClusterLabel();
        else
            return 0;
    }
    public void setProteins(List<IdPickerProteinBase> proteins) {
        if(proteins != null)
            this.proteins = proteins;
    }
    
    public void addProtein(IdPickerProteinBase protein) {
        this.proteins.add(protein);
    }
    
    public List<IdPickerProteinBase> getProteins() {
        return this.proteins;
    }
    
    public int getProteinCount() {
        return proteins.size();
    }
    
    public boolean getIsParsimonious() {
    	if(proteins.size() > 0)
            return proteins.get(0).getIsParsimonious();
        else
            return false;
    }
    
    public boolean getIsSubset() {
    	if(proteins.size() > 0)
            return proteins.get(0).getIsSubset();
        else
            return false;
	}
    
    public int getNumPeptides() {
    	if(proteins.size() > 0)
            return proteins.get(0).getPeptideCount();
        else
            return 0;
    }
    
    public int getNumUniquePeptides() {
    	if(proteins.size() > 0)
            return proteins.get(0).getUniquePeptideCount();
        else
            return 0;
    }
    
    public int getSpectrumCount() {
    	if(proteins.size() > 0)
            return proteins.get(0).getSpectrumCount();
        else
            return 0;
    }
}
