/**
 * ProlucidSearchResultDAO.java
 * @author Vagisha Sharma
 * Aug 26, 2008
 * @version 1.0
 */
package org.yeastrc.ms.dao.search.prolucid;

import java.util.List;

import org.yeastrc.ms.dao.search.GenericSearchResultDAO;
import org.yeastrc.ms.domain.search.prolucid.ProlucidResultDataWId;
import org.yeastrc.ms.domain.search.prolucid.ProlucidSearchResult;
import org.yeastrc.ms.domain.search.prolucid.ProlucidSearchResultIn;

/**
 * 
 */
public interface ProlucidSearchResultDAO extends GenericSearchResultDAO<ProlucidSearchResultIn, ProlucidSearchResult>  {

    public abstract void saveAllProlucidResultData(List<ProlucidResultDataWId> resultDataList);
    
    public abstract List<Integer> loadTopResultIdsForRunSearch(int runSearchId);
    
    /**
     * Returns the search results without any associated proteins.
     * The peptide for each result can optionally be associated with itd 
     * dynamic residue modifications.  Terminal and static modification information is not
     * added. 
     * @param runSearchId
     * @return
     */
    public abstract List<ProlucidSearchResult> loadTopResultsForRunSearchN(int runSearchId, 
                                                        boolean getDynaResMods);
}
