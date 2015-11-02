package org.yeastrc.ms.domain.search;

public interface MsSearchResultProteinIn {


    /**
     * @param the accession
     */
    public abstract String getAccession();
    
    public abstract void setAccession(String accession);
    
    /**
     * @return the description
     */
    public abstract String getDescription();

}