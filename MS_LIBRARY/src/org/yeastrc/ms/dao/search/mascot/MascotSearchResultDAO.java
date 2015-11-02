package org.yeastrc.ms.dao.search.mascot;

import java.sql.SQLException;
import java.util.List;

import org.yeastrc.ms.dao.search.GenericSearchResultDAO;
import org.yeastrc.ms.domain.search.ResultSortCriteria;
import org.yeastrc.ms.domain.search.mascot.MascotResultDataWId;
import org.yeastrc.ms.domain.search.mascot.MascotResultFilterCriteria;
import org.yeastrc.ms.domain.search.mascot.MascotSearchResult;
import org.yeastrc.ms.domain.search.mascot.MascotSearchResultIn;

public interface MascotSearchResultDAO extends GenericSearchResultDAO<MascotSearchResultIn, MascotSearchResult> {

    public abstract void saveAllMascotResultData(List<MascotResultDataWId> dataList);

    public abstract List<Integer> loadResultIdsForRunSearch(int runSearchId, 
            MascotResultFilterCriteria filterCriteria, 
            ResultSortCriteria sortCriteria);

    public abstract List<Integer> loadResultIdsForSearch(int searchId, 
            MascotResultFilterCriteria filterCriteria, 
            ResultSortCriteria sortCriteria);
    
    public abstract void disableKeys() throws SQLException;
    
    public abstract void enableKeys() throws SQLException;
}
