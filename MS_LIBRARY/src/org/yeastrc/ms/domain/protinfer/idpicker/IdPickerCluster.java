package org.yeastrc.ms.domain.protinfer.idpicker;

import java.util.ArrayList;
import java.util.List;


public class IdPickerCluster {

    private final int pinferId;
    private final int clusterLabel;
    private List<IdPickerProteinGroup> proteinGroups;
    private List<IdPickerPeptideGroup> peptideGroups;
    
    public IdPickerCluster(int pinferId, int clusterLabel) {
        this.pinferId = pinferId;
        this.clusterLabel = clusterLabel;
        proteinGroups = new ArrayList<IdPickerProteinGroup>();
        peptideGroups = new ArrayList<IdPickerPeptideGroup>();
    }
    
    public int getProteinferId() {
        return pinferId;
    }
    
    public int getClusterLabel() {
        return clusterLabel;
    }
    
    public List<IdPickerPeptideGroup> getPeptideGroups() {
        return peptideGroups;
    }

    public List<IdPickerProteinGroup> getProteinGroups() {
        return proteinGroups;
    }

    public void setProteinGroups(List<IdPickerProteinGroup> proteinGroups) {
        this.proteinGroups = proteinGroups;
    }
    
    public void addProteinGroup(IdPickerProteinGroup group) {
        proteinGroups.add(group);
    }

    public void setPeptideGroups(List<IdPickerPeptideGroup> peptideGroups) {
        this.peptideGroups = peptideGroups;
    }
    
    public void addPeptideGroup(IdPickerPeptideGroup group) {
        this.peptideGroups.add(group);
    }
    
    public boolean proteinAndPeptideGroupsMatch(int protGrpLabel, int peptGrpLabel) {
        for(IdPickerPeptideGroup peptGrp: peptideGroups) {
            if(peptGrp.getGroupLabel() == peptGrpLabel) {
                return peptGrp.matchesProteinGroup(protGrpLabel);
            }
        }
        return false;
    }
    
}
