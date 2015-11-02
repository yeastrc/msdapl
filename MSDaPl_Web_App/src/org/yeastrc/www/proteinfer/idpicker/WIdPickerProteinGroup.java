package org.yeastrc.www.proteinfer.idpicker;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.yeastrc.ms.domain.protinfer.idpicker.IdPickerProteinBase;

public class WIdPickerProteinGroup {

    private int proteinGroupLabel;
    private int clusterLabel;
    private List<WIdPickerProtein> proteins;
    private int matchingPeptideCount;
    private int uniqMatchingPeptideCount;
    private int spectrumCount;
    private Set<Integer> nonUniqPeptGrpLabels;
    private Set<Integer> uniqPeptGrpLabels;
    
//    private String nonUniqMatchingPeptideGroupIdsString = "NONE";
//    private String uniqMatchingPeptideGroupIdsString = "NONE";
    
    public WIdPickerProteinGroup(List<WIdPickerProtein> groupProteins) {
       if(groupProteins != null)
           proteins = groupProteins;
       if(groupProteins == null)
           groupProteins = new ArrayList<WIdPickerProtein>(0);
       if(groupProteins.size() > 0) {
           IdPickerProteinBase prot = groupProteins.get(0).getProtein();
           this.proteinGroupLabel = prot.getProteinGroupLabel();
           this.clusterLabel = prot.getClusterLabel();
           this.spectrumCount = prot.getSpectrumCount();
           this.matchingPeptideCount = prot.getPeptideCount();
           this.uniqMatchingPeptideCount = prot.getUniquePeptideCount();
       }
       nonUniqPeptGrpLabels = new HashSet<Integer>();
       uniqPeptGrpLabels = new HashSet<Integer>();
    }
    
    public String getNonUniqMatchingPeptideGroupLabelsString() {
        StringBuilder buf = new StringBuilder();
        for(Integer grpLabel: nonUniqPeptGrpLabels) {
//            if(!uniqPeptideGroupIds.contains(grpId))
            buf.append(","+grpLabel);
        }
        if(buf.length() > 0)    buf.deleteCharAt(0);
        return buf.toString();
    }

    public String getUniqMatchingPeptideGroupLabelsString() {
        StringBuilder buf = new StringBuilder();
        for(Integer grpLabel: uniqPeptGrpLabels) {
            buf.append(","+grpLabel);
        }
        if(buf.length() > 0)    buf.deleteCharAt(0);
        return buf.toString();
    }
    
    public void addNonUniqPeptideGrpLabel(int id) {
        nonUniqPeptGrpLabels.add(id);
    }
    
    public void addUniqPeptideGrpLabel(int id) {
        uniqPeptGrpLabels.add(id);
    }
    
    public int getProteinGroupLabel() {
        return proteinGroupLabel;
    }
    
    public int getClusterLabel() {
        return clusterLabel;
    }
    
    public int getProteinCount() {
        return proteins.size();
    }
    
    public int getMatchingPeptideCount() {
        return matchingPeptideCount;
    }
    
    public int getUniqMatchingPeptideCount() {
        return uniqMatchingPeptideCount;
    }
    
    public int getSpectrumCount() {
        return spectrumCount;
    }
    
    public List<WIdPickerProtein> getProteins() {
        return proteins;
    }
    public boolean getIsParsimonious() {
		return this.getProteins().get(0).getProtein().getIsParsimonious();
	}
	public boolean getIsSubset() {
		return this.getProteins().get(0).getProtein().getIsSubset();
	}
}