package edu.uwpr.protinfer.infer.graph;

import java.util.ArrayList;
import java.util.List;

import edu.uwpr.protinfer.infer.Peptide;

public class PeptideVertex extends Vertex<PeptideVertex> {

    private List<Peptide> peptides;
    
    public PeptideVertex(Peptide peptide) {
        super(String.valueOf(peptide.getId()));
        peptides = new ArrayList<Peptide>();
        peptides.add(peptide);
        
    }
    
    private PeptideVertex(String label, List<Peptide> peptides) {
        super(label);
        this.peptides = peptides;
    }
    
    public void addPeptide(Peptide peptide) {
        this.peptides.add(peptide);
    }
    
    public List<Peptide> getPeptides() {
        return peptides;
    }
    
    public String getPeptideStringLabel() {
        StringBuilder buf = new StringBuilder();
        for(Peptide peptide: peptides) 
            buf.append(peptide.getPeptideKey()+"_");
        buf.deleteCharAt(buf.length() - 1);
        return buf.toString();
    }
    
    @Override
    public PeptideVertex combineWith(PeptideVertex v) {
        List<Peptide> allPeptides = new ArrayList<Peptide>(peptides.size() + v.getPeptides().size());
        allPeptides.addAll(peptides);
        allPeptides.addAll(v.getPeptides());
        
        PeptideVertex newVertex = new PeptideVertex(makeLabel(allPeptides), allPeptides);
        return newVertex;
    }

    @Override
    public PeptideVertex combineWith(List<PeptideVertex> vertices) {
        List<Peptide> allPeptides = new ArrayList<Peptide>();
        allPeptides.addAll(peptides);
        for(PeptideVertex vertex: vertices) {
            allPeptides.addAll(vertex.getPeptides());
        }
        PeptideVertex newVertex = new PeptideVertex(makeLabel(allPeptides), allPeptides);
        return newVertex;
    }
    
    private String makeLabel(List<Peptide> peptides) {
        StringBuilder buf = new StringBuilder();
        for(Peptide peptide: peptides) 
            buf.append(peptide.getId()+"_");
        buf.deleteCharAt(buf.length() - 1);
        return buf.toString();
    }

    @Override
    public int getMemberCount() {
        return peptides.size();
    }
}
