/**
 * GenericSearchResultDAO.java
 * @author Vagisha Sharma
 * Sep 7, 2008
 * @version 1.0
 */
package org.yeastrc.ms.dao.search;

import java.math.BigDecimal;
import java.util.List;

import org.yeastrc.ms.domain.search.MsSearchResult;
import org.yeastrc.ms.domain.search.MsSearchResultIn;

/**
 * 
 */
public interface GenericSearchResultDAO <I extends MsSearchResultIn, O extends MsSearchResult> {

    public abstract O load(int resultId);

    public abstract int numRunSearchResults(int runSearchId);
    
    public abstract int numSearchResults(int searchId);
    
    public abstract List<Integer> loadResultIdsForSearch(int searchId);
    
    public abstract List<Integer> loadResultIdsForSearch(int searchId, int limit, int offset);
    
    public abstract List<Integer> loadResultIdsForRunSearch(int runSearchId);
    
    public abstract List<Integer> loadResultIdsForRunSearch(int runSearchId, int limit, int offset);
    
    public abstract List<Integer> loadResultIdsForSearchScanCharge(int runSearchId, int scanId, int charge);
    
    public abstract int numResultsForRunSearchScanChargeMass(int runSearchId, int scanId, int charge, BigDecimal mass);
    
    public abstract List<Integer> loadResultIdsForSearchScan(int runSearchId, int scanId);
    
    public abstract List<MsSearchResult> loadResultForSearchScanChargePeptide(int runSearchId, int scanId, int charge, String peptide);
    
    public abstract List<Integer> loadResultIdsForSearchChargePeptide(int searchId, int charge, String peptide);
    
    public abstract List<Integer> loadResultIdsForSearchPeptideRegex(int searchId, String peptideRegex);
    
    public abstract List<Integer> loadResultIdsForSearchPeptide(int searchId, String peptide);
    
    public abstract List<Integer> loadResultIdsForSearchPeptides(int searchId, List<String> peptides);
    
    
    /**
     * Saves the search result in the msRunSearchResult table. 
     * Any associated protein matches for this result are also saved in the 
     * msProteinMatch table.
     * Any dynamic modifications associated with this result are saved in 
     * the msDynamicModResult table
     * 
     * @param searchId
     * @param searchResult
     * @param runSearchId
     * @param scanId
     * @return id (in msPeptideSearchResult) for this search result
     */
    public abstract int save(int searchId, I searchResult, int runSearchId, int scanId);

    /**
     * Saves the search result in the msRunSearchResult table. 
     * Any data associated with the result (e.g. protein matches, dynamic modifications)
     * are NOT saved.
     * @param searchResult
     * @param runSearchId
     * @param scanId
     * @return
     */
    public abstract int saveResultOnly(MsSearchResultIn searchResult, int runSearchId, int scanId);
    
    
    public abstract <T extends MsSearchResult> List<Integer> saveResultsOnly(List<T> results);
    
    /**
     * Deletes this search result (msRunSearchResult table) along with any 
     * associated protein matches (msProteinMatch)
     * and dynamic modifications (msDynamicModResult).
     * @param resultId
     */
    public abstract void delete(int resultId);
    
    /**
     * Deletes the search results (msRunSearchResult table) with the 
     * given runSearchId
     * @param resultId
     */
    public abstract void deleteResultsForRunSearch(int runSearchId);
}
