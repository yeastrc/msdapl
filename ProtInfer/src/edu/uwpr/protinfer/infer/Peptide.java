package edu.uwpr.protinfer.infer;



public class Peptide {

    private final String sequence;
    private final String peptideKey;   // based on the peptide definition
                                       // If peptide definition == sequence; peptideKey = sequence
                                       // If peptide definition == sequence+mods; peptideKey = modifiedSequence
                                       // If peptide definition == sequence+mods+charge; peptideKey = modifiedSequence_charge
    
    private int id;
    private int peptideGroupLabel;
    
    private boolean isUnique = false;
    
    /**
     * @param sequence
     * @param id unique id for this peptide
     */
    public Peptide(String sequence, String peptideKey, int id) {
        this.sequence = sequence;
        this.peptideKey = peptideKey;
        this.id = id;
    }
    
    public String getPeptideSequence() {
        return sequence;
    }
    
    public String getPeptideKey() {
        return peptideKey;
    }
    
    public boolean isUniqueToProtein() {
        return isUnique;
    }
    
    public void markUnique(boolean isUnique) {
        this.isUnique = isUnique;
    }
    
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public int getPeptideGroupLabel() {
        return peptideGroupLabel;
    }

    public void setPeptideGroupLabel(int peptideGroupLabel) {
        this.peptideGroupLabel = peptideGroupLabel;
    }
    
    public String toString() {
       StringBuilder buf = new StringBuilder();
       buf.append("ID: "+id+"\tGroupLabel: "+peptideGroupLabel+"\tSequence: "+sequence+"\tKey: "+peptideKey);
       return buf.toString();
    }
}
