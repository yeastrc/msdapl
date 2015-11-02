package org.yeastrc.ms.dao.search;

import java.sql.SQLException;

import org.yeastrc.ms.domain.search.MsSearchResultIn;
import org.yeastrc.ms.domain.search.MsSearchResult;

public interface MsSearchResultDAO extends GenericSearchResultDAO<MsSearchResultIn, MsSearchResult> {
    
    public abstract void disableKeys() throws SQLException;
    
    public abstract void enableKeys() throws SQLException;
}