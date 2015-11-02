package org.yeastrc.ms.parser.sqtFile;

import org.yeastrc.ms.domain.search.MsSearchResultProteinIn;

/**
 * Represents a L line in the SQT file. 
 */
public class DbLocus implements MsSearchResultProteinIn {

    private String accession; // Locus in which this sequence is found
    private String description; // Description of this locus from database (optional) 
    private char ntermResidue;
    private char ctermResidue;
    private int numEnzymaticTermini;


    public DbLocus(String accession, String description) {
        this.accession = accession;
        this.description = description;
    }

    /**
     * @return the accession
     */
    public String getAccession() {
        return accession;
    }
    
    public void setAccession(String accession) {
    	this.accession = accession;
    }

    /**
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    public char getNtermResidue() {
        return ntermResidue;
    }

    public void setNtermResidue(char ntermResidue) {
        this.ntermResidue = ntermResidue;
    }

    public char getCtermResidue() {
        return ctermResidue;
    }

    public void setCtermResidue(char ctermResidue) {
        this.ctermResidue = ctermResidue;
    }
    
    public int getNumEnzymaticTermini() {
        return numEnzymaticTermini;
    }

    public void setNumEnzymaticTermini(int numEnzymaticTermini) {
        this.numEnzymaticTermini = numEnzymaticTermini;
    }
    
    public String toString() {
        StringBuilder buf = new StringBuilder();
        buf.append("L\t");
        buf.append(accession);
        if (description != null) {
            buf.append("\t");
            buf.append(description);
        }
        return buf.toString();
    }

}
