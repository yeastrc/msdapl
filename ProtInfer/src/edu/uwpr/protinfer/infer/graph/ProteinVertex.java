package edu.uwpr.protinfer.infer.graph;

import java.util.ArrayList;
import java.util.List;

import edu.uwpr.protinfer.infer.Protein;

public class ProteinVertex extends Vertex<ProteinVertex> {

    private List<Protein> proteins;
    
    public ProteinVertex(Protein protein) {
        super(String.valueOf(protein.getId()));
        proteins = new ArrayList<Protein>();
        proteins.add(protein);
    }
    
    public void setAccepted(boolean isAccepted) {
        for(Protein prot: proteins)
            prot.setAccepted(isAccepted);
    }
    
    public boolean isAccepted() {
        return proteins.get(0).isAccepted();
    }
    
    private ProteinVertex(List<Protein> proteins) {
        super(makeLabel(proteins));
        this.proteins = proteins;
    }
    
    public void addProtein(Protein protein) {
        this.proteins.add(protein);
    }
    
    public List<Protein> getProteins() {
        return proteins;
    }
    
    public String getProteinStringLabel() {
        StringBuilder buf = new StringBuilder();
        for(Protein protein: proteins) 
            buf.append(protein.getAccession()+"_");
        buf.deleteCharAt(buf.length() - 1);
        return buf.toString();
    }
    
    public void setComponentIndex(int index) {
        super.setComponentIndex(index);
        for(Protein prot: proteins)
            prot.setProteinClusterLabel(index);
    }
    
    @Override
    public ProteinVertex combineWith(ProteinVertex v) {
        List<Protein> allProteins = new ArrayList<Protein>(proteins.size() + v.getProteins().size());
        allProteins.addAll(proteins);
        allProteins.addAll(v.getProteins());
        
        ProteinVertex newVertex = new ProteinVertex(allProteins);
        return newVertex;
    }

    @Override
    public ProteinVertex combineWith(List<ProteinVertex> vertices) {
        List<Protein> allProteins = new ArrayList<Protein>();
        allProteins.addAll(proteins);
        for(ProteinVertex vertex: vertices) {
            allProteins.addAll(vertex.getProteins());
        }
        ProteinVertex newVertex = new ProteinVertex(allProteins);
        return newVertex;
    }
    
    private static String makeLabel(List<Protein> proteins) {
        StringBuilder buf = new StringBuilder();
        for(Protein protein: proteins) 
            buf.append(protein.getId()+"_");
        buf.deleteCharAt(buf.length() - 1);
        return buf.toString();
    }
    
    @Override
    public int getMemberCount() {
        return proteins.size();
    }
}
