package edu.uwpr.protinfer.idpicker;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import edu.uwpr.protinfer.infer.PeptideEvidence;
import edu.uwpr.protinfer.infer.SpectrumMatch;

public class InferredPeptideGroup <S extends SpectrumMatch>{
    
    private int groupId;
    private List<PeptideEvidence<S>> peptides;
    private Set<Integer> matchingProteinGroupIds;
    
    public InferredPeptideGroup(int groupId) {
        this.groupId = groupId;
        peptides = new ArrayList<PeptideEvidence<S>>();
        matchingProteinGroupIds = new HashSet<Integer>();
    }
    
    public int getGroupId() {
        return groupId;
    }
    
    public List<PeptideEvidence<S>> getPeptideEvidenceListList() {
        return peptides;
    }
    
    public void addPeptideEvidence(PeptideEvidence<S> peptide) {
        peptides.add(peptide);
    }
    
    public List<Integer> getMatchingProteinGroupIds() {
        return new ArrayList<Integer>(matchingProteinGroupIds);
    }
    
    public void addMatchingProteinGroupId(int proteinGroupId) {
        matchingProteinGroupIds.add(proteinGroupId);
    }
    
    public boolean isUniqueToProtein() {
        return matchingProteinGroupIds.size() == 1;
    }
}
