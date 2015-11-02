package org.yeastrc.ms.domain.protinfer.idpicker;

import java.util.ArrayList;
import java.util.List;

public class IdPickerPeptideGroup {

    private final int pinferId;
    private final int peptideGroupLabel;
    private List<IdPickerPeptide> peptides;
    private List<Integer> matchingProteinGroupLabels;
    
    public IdPickerPeptideGroup(int pinferId, int peptideGroupLabel) {
        this.pinferId = pinferId;
        this.peptideGroupLabel = peptideGroupLabel;
        peptides = new ArrayList<IdPickerPeptide>();
        matchingProteinGroupLabels = new ArrayList<Integer>();
    }
    
    public int getProteinferId() {
        return pinferId;
    }
    
    public int getGroupLabel() {
        return peptideGroupLabel;
    }
    
    public void setPeptides(List<IdPickerPeptide> peptides) {
        if(peptides != null)
            this.peptides = peptides;
    }
    
    public List<IdPickerPeptide> getPeptides() {
        return this.peptides;
    }
    
    public int getPeptideCount() {
        return peptides.size();
    }
    
    public List<Integer> getMatchingProteinGroupLabels() {
        return matchingProteinGroupLabels;
    }
    
    public void setMatchingProteinGroupLabels(List<Integer> protGrpLabels) {
        this.matchingProteinGroupLabels = protGrpLabels;
    }
    
    public boolean matchesProteinGroup(int protGrpLabel) {
        return matchingProteinGroupLabels.contains(protGrpLabel);
    }
    
    public boolean isUniqueToProteinGroup() {
        return matchingProteinGroupLabels.size() == 1;
    }
}
