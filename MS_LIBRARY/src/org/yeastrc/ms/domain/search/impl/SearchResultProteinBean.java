/**
 * MsSearchResultProteinDbImpl.java
 * @author Vagisha Sharma
 * Jul 11, 2008
 * @version 1.0
 */
package org.yeastrc.ms.domain.search.impl;

import org.yeastrc.ms.domain.search.MsSearchResultProtein;


public class SearchResultProteinBean implements MsSearchResultProtein {

    private int resultId;
    private String accession;
    
    public SearchResultProteinBean() {}
    
    public SearchResultProteinBean(int resultId, String accession) {
        this.resultId = resultId;
        this.accession = accession;
    }
    
    public int getResultId() {
        return resultId;
    }
    /**
     * @param resultId the resultId to set
     */
    public void setResultId(int resultId) {
        this.resultId = resultId;
    }
   
    public String getAccession() {
        return accession;
    }
    
    public void setAccession(String accession) {
        this.accession = accession;
    }
}
