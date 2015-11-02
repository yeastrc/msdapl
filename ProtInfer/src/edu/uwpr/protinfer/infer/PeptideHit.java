package edu.uwpr.protinfer.infer;

import java.util.ArrayList;
import java.util.List;


public class PeptideHit {

    private Peptide peptide;
    private List<Protein> proteins;
    
    public PeptideHit(Peptide peptide) {
        this.peptide = peptide;
        proteins = new ArrayList<Protein>();
    }
    
    public PeptideHit(Peptide peptide, List<Protein> proteins) {
        this(peptide);
        if (proteins != null)
            this.proteins = proteins;
    }
    
    public Peptide getPeptide() {
        return peptide;
    }
    
    public void addProtein(Protein protein) {
        if(!proteins.contains(protein))
            this.proteins.add(protein);
    }
    
    public List<Protein> getProteinList() {
        return proteins;
    }
    
    public int getMatchProteinCount() {
        return proteins.size();
    }
    
    public boolean isDecoyPeptide() {
        for (Protein prot: getProteinList()) {
            if (!prot.isDecoy())
                return false;
        }
        return true;
    }
    
    public String toString() {
        StringBuilder buf = new StringBuilder();
        buf.append("peptide: "+peptide.getPeptideSequence()+"\n");
        buf.append("key: "+peptide.getPeptideKey()+"\n");
        for(Protein prot: proteins) {
            buf.append("\t"+prot+"\n");
        }
        return buf.toString();
    }
}
