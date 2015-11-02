package edu.uwpr.protinfer.infer;


public class ProteinHit {

    private Protein protein;
    private char preResidue = '\u0000'; 
    private char postResidue = '\u0000';
    
    public ProteinHit(Protein protein) {
        this.protein = protein;
    }
    
    public ProteinHit(Protein protein, char preResidue, char postResidue) {
        this.protein = protein;
        this.preResidue = preResidue;
        this.postResidue = postResidue;
    }
    
    public Protein getProtein() {
        return protein;
    }
    
    public String getAccession() {
        return protein.getAccession();
    }
    
    /**
     * @return the preResidue
     */
    public char getPreResidue() {
        return preResidue;
    }

    /**
     * @param preResidue the preResidue to set
     */
    public void setPreResidue(char preResidue) {
        this.preResidue = preResidue;
    }

    /**
     * @return the postResidue
     */
    public char getPostResidue() {
        return postResidue;
    }

    /**
     * @param postResidue the postResidue to set
     */
    public void setPostResidue(char postResidue) {
        this.postResidue = postResidue;
    }
    
    public String toString() {
        return protein.toString()+"\t"+preResidue+"\t"+postResidue;
    }
}
