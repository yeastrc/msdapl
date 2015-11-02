/**
 * PeptideProphetResultDataWId.java
 * @author Vagisha Sharma
 * Jun 24, 2009
 * @version 1.0
 */
package org.yeastrc.ms.domain.analysis.peptideProphet;

/**
 * 
 */
public interface PeptideProphetResultDataWId extends PeptideProphetResultDataIn {

    public abstract int getRunSearchAnalysisId();
    
    /**
     * This will return the ID of the base search results which this PeptideProphet result is based on
     * (id column in the msRunSearchResult table).
     * @return
     */
    public abstract int getSearchResultId();
    
}
