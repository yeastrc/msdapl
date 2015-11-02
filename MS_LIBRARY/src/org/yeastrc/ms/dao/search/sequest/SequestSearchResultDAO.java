package org.yeastrc.ms.dao.search.sequest;

import java.sql.SQLException;
import java.util.List;

import org.yeastrc.ms.dao.search.GenericSearchResultDAO;
import org.yeastrc.ms.domain.search.ResultSortCriteria;
import org.yeastrc.ms.domain.search.sequest.SequestResultDataWId;
import org.yeastrc.ms.domain.search.sequest.SequestResultFilterCriteria;
import org.yeastrc.ms.domain.search.sequest.SequestSearchResult;
import org.yeastrc.ms.domain.search.sequest.SequestSearchResultIn;

public interface SequestSearchResultDAO extends GenericSearchResultDAO<SequestSearchResultIn, SequestSearchResult> {

    public abstract void saveAllSequestResultData(List<SequestResultDataWId> dataList);

    public abstract List<Integer> loadTopResultIdsForRunSearch(int runSearchId);

    /**
     * Returns the search results without any associated proteins.
     * The peptide for each result can optionally be associated with itd 
     * dynamic residue modifications.  Terminal and static modification information is not
     * added. 
     * @param runSearchId
     * @return
     */
    public abstract List<SequestSearchResult> loadTopResultsForRunSearchN(int runSearchId, 
            boolean getDynaResMods);


    public abstract List<Integer> loadResultIdsForRunSearch(int runSearchId, 
            SequestResultFilterCriteria filterCriteria, 
            ResultSortCriteria sortCriteria);

    public abstract List<Integer> loadResultIdsForSearch(int searchId, 
            SequestResultFilterCriteria filterCriteria, 
            ResultSortCriteria sortCriteria);
    
    public abstract void disableKeys() throws SQLException;
    
    public abstract void enableKeys() throws SQLException;
}
