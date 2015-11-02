package edu.uwpr.protinfer.infer;

import java.util.ArrayList;
import java.util.List;


public class PeptideEvidence <T extends SpectrumMatch>{
    
    private Peptide peptide;
    private List<T> spectrumMatchList;
//    private double bestFdr = 1.0;
    
    public PeptideEvidence(Peptide peptide) {
        this.peptide = peptide;
        spectrumMatchList = new ArrayList<T>();
    }
    
    public PeptideEvidence(Peptide peptide, List<T> spectrumMatchList) {
        this(peptide);
        if (spectrumMatchList != null)
            this.spectrumMatchList = spectrumMatchList;
    }
    
    public void addSpectrumMatch(T spectrumMatch) {
    	int resultId = spectrumMatch.getResultId();
    	for(T sm: spectrumMatchList) {
    		if(sm.getResultId() == resultId)
    			return;
    	}
        spectrumMatchList.add(spectrumMatch);
    }
    
    public void addSpectrumMatchList(List<T> spectrumMatchList) {
        spectrumMatchList.addAll(spectrumMatchList);
    }
    
    public List<T> getSpectrumMatchList() {
        return spectrumMatchList;
    }
    
    public int getSpectrumMatchCount() {
        return spectrumMatchList.size();
    }
    
    public Peptide getPeptide() {
        return peptide;
    }
    
//    public double getBestFdr() {
//        return bestFdr;
//    }
//    
//    public void setBestFdr(double fdr) {
//        fdr = Math.round((fdr*100.0))/100.0;
//        this.bestFdr = fdr;
//    }
    
    public String toString() {
        StringBuilder buf = new StringBuilder();
        buf.append(peptide.toString()+"\n");
        for(T psm: spectrumMatchList) {
            buf.append("\t"+psm.toString()+"\n");
        }
        return buf.toString();
    }
    
}
