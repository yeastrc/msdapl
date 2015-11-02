/**
 * ProteinProphetProteinGroup.java
 * @author Vagisha Sharma
 * Aug 5, 2009
 * @version 1.0
 */
package org.yeastrc.www.proteinfer.proteinProphet;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.yeastrc.ms.domain.protinfer.proteinProphet.ProteinProphetProtein;

/**
 * 
 */
public class WProteinProphetIndistProteinGroup {

    private int groupId;
    private double probability;
    private List<WProteinProphetProtein> proteins;
    private int matchingPeptideCount;
    private int uniqMatchingPeptideCount;
    private int spectrumCount;
    private Set<Integer> nonUniqPeptGrpIds;
    private Set<Integer> uniqPeptGrpIds;
    
    
    public WProteinProphetIndistProteinGroup(List<WProteinProphetProtein> groupProteins) {
       if(groupProteins != null)
           proteins = groupProteins;
       if(groupProteins == null)
           groupProteins = new ArrayList<WProteinProphetProtein>(0);
       if(groupProteins.size() > 0) {
           ProteinProphetProtein prot = groupProteins.get(0).getProtein();
           this.groupId = prot.getGroupId();
           this.probability = prot.getProbability();
           this.spectrumCount = prot.getSpectrumCount();
           this.matchingPeptideCount = prot.getPeptideCount();
           this.uniqMatchingPeptideCount = prot.getUniquePeptideCount();
       }
       nonUniqPeptGrpIds = new HashSet<Integer>();
       uniqPeptGrpIds = new HashSet<Integer>();
    }
    
    public String getNonUniqMatchingPeptideGroupIdsString() {
        StringBuilder buf = new StringBuilder();
        for(Integer grpId: nonUniqPeptGrpIds) {
//            if(!uniqPeptideGroupIds.contains(grpId))
            buf.append(","+grpId);
        }
        if(buf.length() > 0)    buf.deleteCharAt(0);
        return buf.toString();
    }

    public String getUniqMatchingPeptideGroupIdsString() {
        StringBuilder buf = new StringBuilder();
        for(Integer grpId: uniqPeptGrpIds) {
            buf.append(","+grpId);
        }
        if(buf.length() > 0)    buf.deleteCharAt(0);
        return buf.toString();
    }
    
    public void addNonUniqPeptideGrpId(int id) {
        nonUniqPeptGrpIds.add(id);
    }
    
    public void addUniqPeptideGrpId(int id) {
        uniqPeptGrpIds.add(id);
    }
    
    public int getGroupId() {
        return groupId;
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
    
    public List<WProteinProphetProtein> getProteins() {
        return proteins;
    }
    
    public double getProbability() {
        return this.probability;
    }
}
