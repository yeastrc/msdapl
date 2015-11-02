package edu.uwpr.protinfer.idpicker;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import edu.uwpr.protinfer.infer.InferredProtein;
import edu.uwpr.protinfer.infer.SpectrumMatch;

public class InferredProteinGroup <S extends SpectrumMatch>{

    private int groupId;
    private List<InferredProtein<S>> proteins;
    private Set<Integer> matchingPeptideGroupIds;
    
    public InferredProteinGroup(int groupId) {
        this.groupId = groupId;
        proteins = new ArrayList<InferredProtein<S>>();
        matchingPeptideGroupIds = new HashSet<Integer>();
    }
    
    public int getGroupId() {
        return groupId;
    }
    
    public List<InferredProtein<S>> getInferredProteinList() {
        return proteins;
    }
    
    public void addInferredProtein(InferredProtein<S> protein) {
        proteins.add(protein);
    }
    
    public List<Integer> getMatchingPeptideGroupIds() {
        return new ArrayList<Integer>(matchingPeptideGroupIds);
    }
    
    public void addMatchingPeptideGroupId(int peptideGroupId) {
        matchingPeptideGroupIds.add(peptideGroupId);
    }
}
