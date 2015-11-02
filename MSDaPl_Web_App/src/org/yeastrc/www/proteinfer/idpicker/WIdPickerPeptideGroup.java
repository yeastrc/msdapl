/**
 * WIdPickerPeptideGroup.java
 * @author Vagisha Sharma
 * Jan 21, 2009
 * @version 1.0
 */
package org.yeastrc.www.proteinfer.idpicker;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.yeastrc.ms.domain.protinfer.idpicker.IdPickerPeptideBase;

/**
 * 
 */
public class WIdPickerPeptideGroup {

    private final int pinferId;
    private final int peptideGroupLabel;
    private List<? extends IdPickerPeptideBase> peptides;
    private Set<Integer> matchingProteinGroupLabels;
    
    public WIdPickerPeptideGroup(List<? extends IdPickerPeptideBase> groupPeptides) {
        if(groupPeptides.size() > 0) {
            this.pinferId = groupPeptides.get(0).getProteinferId();
            this.peptideGroupLabel = groupPeptides.get(0).getPeptideGroupLabel();
        }
        else {
            pinferId = 0;
            peptideGroupLabel = 0;
        }
        this.peptides = groupPeptides;
        matchingProteinGroupLabels = new HashSet<Integer>();
    }
    
    public int getProteinferId() {
        return pinferId;
    }
    
    public int getPeptideGroupLabel() {
        return peptideGroupLabel;
    }
    
    public List<? extends IdPickerPeptideBase> getPeptides() {
        return this.peptides;
    }
    
    public int getPeptideCount() {
        return peptides.size();
    }
    
    public List<Integer> getMatchingProteinGroupLabels() {
        return new ArrayList<Integer>(matchingProteinGroupLabels);
    }
    
    public void addMatchingProteinGroupLabel(int proteinGroupLabel) {
        this.matchingProteinGroupLabels.add(proteinGroupLabel);
    }
    
    public boolean matchesProteinGroup(int proteinGroupLabel) {
        return matchingProteinGroupLabels.contains(proteinGroupLabel);
    }
    
    public boolean isUniqueToProteinGroup() {
        return matchingProteinGroupLabels.size() == 1;
    }
}
