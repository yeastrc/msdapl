/**
 * MsSequenceDatabaseDAOImpl.java
 * @author Vagisha Sharma
 * Jul 4, 2008
 * @version 1.0
 */
package org.yeastrc.ms.dao.search.ibatis;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.yeastrc.ms.dao.ibatis.BaseSqlMapDAO;
import org.yeastrc.ms.dao.search.MsSearchDatabaseDAO;
import org.yeastrc.ms.domain.search.MsSearchDatabase;

import com.ibatis.sqlmap.client.SqlMapClient;

/**
 * 
 */
public class MsSearchDatabaseDAOImpl extends BaseSqlMapDAO implements MsSearchDatabaseDAO {

    public MsSearchDatabaseDAOImpl(SqlMapClient sqlMap) {
        super(sqlMap);
    }

    public List<MsSearchDatabase> loadSearchDatabases(int searchId) {
        return queryForList("MsDatabase.selectSearchDatabases", searchId);
    }
    
    public void deleteSearchDatabases(int searchId) {
        delete("MsDatabase.deleteSearchDatabases", searchId);
    }
    
    public int saveSearchDatabase(MsSearchDatabase database, int searchId) {
        
        int databaseId = 0;
        try {databaseId = database.getId();}
        catch(Exception e){}
        if(databaseId == 0) {
            List<Integer> dbIds = loadMatchingDatabaseIds(database);
            if (dbIds.size() > 0) {
                databaseId = dbIds.get(0);
            }
            else {
                databaseId = saveDatabase(database);
            }
        }
        linkDatabaseAndSearch(databaseId, searchId);
        return databaseId;
    }
    
    public void linkDatabaseAndSearch(int databaseId, int searchId) {
        Map<String, Integer> map = new HashMap<String, Integer>(2);
        map.put("searchId", searchId);
        map.put("databaseId", databaseId);
        save("MsDatabase.insertSearchDatabase", map);
    }
    
    public List<Integer> loadMatchingDatabaseIds(MsSearchDatabase database) {
        return queryForList("MsDatabase.selectDatabaseIdMatchAllCols", database);
    }
    
    public int saveDatabase(MsSearchDatabase database) {
        return saveAndReturnId("MsDatabase.insertDatabase", database);
    }

    @Override
    public int getSequenceDatabaseId(String serverPath) {
        List<Integer> ids = queryForList("MsDatabase.getNrseqDbId", serverPath);
        if(ids.size() == 1) // return an id only if there is a unique entry with this filepath
            return ids.get(0);
        return 0;
    }
    
    public List<MsSearchDatabase> findSearchDatabases(String matchString) {
    	if(matchString == null || matchString.trim().length() == 0)
    		return new ArrayList<MsSearchDatabase>(0);
    	
    	matchString = "%"+matchString+"%";
    	return queryForList("MsDatabase.findSearchDatabases", matchString);
    }
}
