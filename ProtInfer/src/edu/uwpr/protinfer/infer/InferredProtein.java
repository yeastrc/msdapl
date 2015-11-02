package edu.uwpr.protinfer.infer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class InferredProtein <T extends SpectrumMatch> {

    private Protein protein;
    private int spectrumCount = -1;
    
    private Map<Integer, PeptideEvidence<T>> peptideEvList;
    
    private float percentCoverage;
    private double nsaf = 0;
    
    public InferredProtein(Protein protein) {
        this.protein = protein;
        peptideEvList = new HashMap<Integer, PeptideEvidence<T>>();
    }
    
    public void addPeptideEvidence(PeptideEvidence<T> peptideEv) {
        PeptideEvidence<T> evidence = peptideEvList.get(peptideEv.getPeptide().getId());
        if (evidence == null) {
            this.peptideEvList.put(peptideEv.getPeptide().getId(), peptideEv);
        }
    }
    
    public PeptideEvidence<T> getPeptideEvidence(Peptide peptide) {
        return peptideEvList.get(peptide.getId());
    }
    
    public int getPeptideEvidenceCount() {
        return peptideEvList.size();
    }
    
    public int getSpectralEvidenceCount() {
        if(spectrumCount != -1)
            return spectrumCount;
        int count = 0;
        for(PeptideEvidence<T> ev: peptideEvList.values()) {
            count += ev.getSpectrumMatchCount();
        }
        return count;
    }
    public void setSpectrumMatchCount(int count) {
        this.spectrumCount = count;
    }
    
    public List<PeptideEvidence<T>> getPeptides() {
        List<PeptideEvidence<T>> list = new ArrayList<PeptideEvidence<T>>(peptideEvList.size());
        list.addAll(peptideEvList.values());
        return list;
    }
    
    public Protein getProtein() {
        return protein;
    }
    
    public int getProteinId() {
        return protein.getId();
    }
    
    public String getAccession() {
        return protein.getAccession();
    }

    public boolean getIsAccepted() {
        return protein.isAccepted();
    }
    
    public boolean getIsSubset() {
    	return protein.isSubset();
    }

    public double getNSAF() {
        return nsaf;
    }

    public void setNSAF(double nsaf) {
        this.nsaf = nsaf;
    }

    public int getProteinGroupLabel() {
        return protein.getProteinGroupLabel();
    }
    
    public int getProteinClusterLabel() {
        return protein.getProteinClusterLabel();
    }
    
    public float getPercentCoverage() {
        return percentCoverage;
    }

    public void setPercentCoverage(float percentCoverage) {
        this.percentCoverage = percentCoverage;
    }
    
    public String getDescription() {
        return protein.getDescription();
    }

    public void setDescription(String description) {
       protein.setDescription(description);
    }
}
