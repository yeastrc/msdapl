package org.yeastrc.ms.dao.search;

import java.sql.SQLException;
import java.util.List;

import org.yeastrc.ms.domain.search.MsSearchResultProteinIn;
import org.yeastrc.ms.domain.search.MsSearchResultProtein;

public interface MsSearchResultProteinDAO {

    public abstract List<MsSearchResultProtein> loadResultProteins(int resultId);

    /**
     * @param protein
     * @param resultId
     */
    public abstract void save(MsSearchResultProteinIn protein, int resultId);

    public abstract void delete(int resultId);

    public abstract void saveAll(List<MsSearchResultProtein> proteinMatchList);
    
    public abstract void disableKeys() throws SQLException;
    
    public abstract void enableKeys() throws SQLException;

}