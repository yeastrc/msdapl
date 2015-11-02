/**
 * ProteinDatabaseLookupUtil.java
 * @author Vagisha Sharma
 * May 16, 2009
 * @version 1.0
 */
package org.yeastrc.www.compare;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.yeastrc.ms.dao.DAOFactory;
import org.yeastrc.ms.dao.ProteinferDAOFactory;
import org.yeastrc.ms.dao.protinfer.ibatis.ProteinferRunDAO;
import org.yeastrc.ms.dao.search.MsSearchDAO;
import org.yeastrc.ms.domain.search.MsSearch;
import org.yeastrc.ms.domain.search.MsSearchDatabase;
import org.yeastrc.nr_seq.database.StandardDatabase;
import org.yeastrc.nr_seq.database.StandardDatabaseCache;
import org.yeastrc.nrseq.domain.NrDatabase;

/**
 * 
 */
public class ProteinDatabaseLookupUtil {

    private static ProteinDatabaseLookupUtil instance;
    
    private static final Logger log = Logger.getLogger(ProteinDatabaseLookupUtil.class.getName());
    
    private ProteinDatabaseLookupUtil() {}
    
    public static ProteinDatabaseLookupUtil getInstance() {
        if(instance == null)
            instance = new ProteinDatabaseLookupUtil();
        return instance;
    }
    
    public List<Integer> getDatabaseIdsForProteinInference(int pinferId) {
        
    	return getDatabaseIdsForProteinInference(pinferId, false); // do not add any standard databases
    }
    
    public List<Integer> getDatabaseIdsForProteinInference(int pinferId, boolean addStandardDatabases) {
        
        ProteinferRunDAO runDao = ProteinferDAOFactory.instance().getProteinferRunDao();
        MsSearchDAO searchDao = DAOFactory.instance().getMsSearchDAO();
        
        List<Integer> searchIds = runDao.loadSearchIdsForProteinferRun(pinferId);
        if(searchIds.size() == 0) {
            log.error("No search Ids found for protein inference ID: "+pinferId);
        }
        
        Set<Integer> databaseIds = new HashSet<Integer>();
        for(int searchId: searchIds) {
            MsSearch search = searchDao.loadSearch(searchId);
            for(MsSearchDatabase db: search.getSearchDatabases()) {
                databaseIds.add(db.getSequenceDatabaseId());
            }
        }
        
        if(addStandardDatabases) {
	        
	        for (StandardDatabase sdb: StandardDatabase.values()) {
	        	NrDatabase ndb = StandardDatabaseCache.getNrDatabase(sdb);
	        	if(ndb != null)
	        		databaseIds.add(ndb.getId());
	        }
        }
        return new ArrayList<Integer>(databaseIds);
    }
    
    public List<Integer> getDatabaseIdsForProteinInferences(List<Integer> pinferIds) {
        
       return getDatabaseIdsForProteinInferences(pinferIds, false); // do not add any standard databases
    }
    
    public List<Integer> getDatabaseIdsForProteinInferences(List<Integer> pinferIds, boolean addStandardDatabases) {
        
        ProteinferRunDAO runDao = ProteinferDAOFactory.instance().getProteinferRunDao();
        MsSearchDAO searchDao = DAOFactory.instance().getMsSearchDAO();
        
        Set<Integer> searchIds = new HashSet<Integer>();
        for(int pinferId: pinferIds) {
            List<Integer> ids = runDao.loadSearchIdsForProteinferRun(pinferId);
            if(ids.size() == 0) {
                log.error("No search Ids found for protein inference ID: "+pinferId);
            }
            searchIds.addAll(ids);
        }
        
        Set<Integer> databaseIds = new HashSet<Integer>();
        for(int searchId: searchIds) {
            MsSearch search = searchDao.loadSearch(searchId);
            for(MsSearchDatabase db: search.getSearchDatabases()) {
                databaseIds.add(db.getSequenceDatabaseId());
            }
        }
        
        if(addStandardDatabases) {
	        
	        for (StandardDatabase sdb: StandardDatabase.values()) {
	        	NrDatabase ndb = StandardDatabaseCache.getNrDatabase(sdb);
	        	if(ndb != null)
	        		databaseIds.add(ndb.getId());
	        }
        }
        
        return new ArrayList<Integer>(databaseIds);
    }
}
