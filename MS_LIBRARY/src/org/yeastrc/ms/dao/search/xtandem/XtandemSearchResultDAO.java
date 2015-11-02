package org.yeastrc.ms.dao.search.xtandem;

import java.sql.SQLException;
import java.util.List;

import org.yeastrc.ms.dao.search.GenericSearchResultDAO;
import org.yeastrc.ms.domain.search.ResultSortCriteria;
import org.yeastrc.ms.domain.search.xtandem.XtandemResultDataWId;
import org.yeastrc.ms.domain.search.xtandem.XtandemResultFilterCriteria;
import org.yeastrc.ms.domain.search.xtandem.XtandemSearchResult;
import org.yeastrc.ms.domain.search.xtandem.XtandemSearchResultIn;

public interface XtandemSearchResultDAO extends GenericSearchResultDAO<XtandemSearchResultIn, XtandemSearchResult> {

    public abstract void saveAllXtandemResultData(List<XtandemResultDataWId> dataList);

    public abstract List<Integer> loadResultIdsForRunSearch(int runSearchId, 
            XtandemResultFilterCriteria filterCriteria, 
            ResultSortCriteria sortCriteria);

    public abstract List<Integer> loadResultIdsForSearch(int searchId, 
            XtandemResultFilterCriteria filterCriteria, 
            ResultSortCriteria sortCriteria);
    
    public abstract void disableKeys() throws SQLException;
    
    public abstract void enableKeys() throws SQLException;
}
