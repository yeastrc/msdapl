package org.yeastrc.www.proteinfer.idpicker;

import java.util.List;

public class WIdPickerCluster {

    private int pinferId;
    private int clusterLabel;
    private List<WIdPickerProteinGroup> proteinGroups;
    private List<WIdPickerPeptideGroup> peptideGroups;
    
    public WIdPickerCluster(int pinferId, int clusterLabel) {
        this.pinferId = pinferId;
        this.clusterLabel = clusterLabel;
    }
    
    public void setProteinGroups(List<WIdPickerProteinGroup> proteinGroups) {
        this.proteinGroups = proteinGroups;
    }
    
    public void setPeptideGroups(List<WIdPickerPeptideGroup> peptideGroups) {
        this.peptideGroups = peptideGroups;
    }

    public int getPinferId() {
        return pinferId;
    }

    public void setPinferId(int pinferId) {
        this.pinferId = pinferId;
    }

    public int getClusterLabel() {
        return clusterLabel;
    }

    public void setClusterLabel(int clusterLabel) {
        this.clusterLabel = clusterLabel;
    }

    public List<WIdPickerProteinGroup> getProteinGroups() {
        return proteinGroups;
    }

    public List<WIdPickerPeptideGroup> getPeptideGroups() {
        return peptideGroups;
    }
    
    public boolean proteinAndPeptideGroupsMatch(int proteinGroupLabel, int peptideGroupLabel) {
        for(WIdPickerPeptideGroup peptGrp: peptideGroups) {
            if(peptGrp.getPeptideGroupLabel() == peptideGroupLabel) {
                return peptGrp.matchesProteinGroup(proteinGroupLabel);
            }
        }
        return false;
    }
    
}
