/**
 * MsExperimentDAOImpl.java
 * @author Vagisha Sharma
 * Sep 18, 2008
 * @version 1.0
 */
package org.yeastrc.ms.dao.general.ibatis;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.yeastrc.ms.dao.general.MsExperimentDAO;
import org.yeastrc.ms.dao.ibatis.BaseSqlMapDAO;
import org.yeastrc.ms.domain.general.ExperimentSearchCriteria;
import org.yeastrc.ms.domain.general.MsExperiment;

import com.ibatis.sqlmap.client.SqlMapClient;

/**
 * 
 */
public class MsExperimentDAOImpl extends BaseSqlMapDAO implements MsExperimentDAO {

    public MsExperimentDAOImpl(SqlMapClient sqlMap) {
        super(sqlMap);
    }

    @Override
    public List<Integer> getAllExperimentIds() {
        return queryForList("MsExperiment.selectAllExperimentIds");
    }
    
    @Override
    public List<Integer> getExperimentIds(ExperimentSearchCriteria searchCriteria) {
        
    	Map<String, Object> map = new HashMap<String, Object>();
    	
    	List<Integer> searchDbIds = searchCriteria.getSearchDatabaseIds();
    	if(searchDbIds != null && searchDbIds.size() > 0) {
    		String dbIdStr = "";
    		for(Integer dbId: searchDbIds) {
    			dbIdStr += ","+dbId;
    		}
    		dbIdStr = dbIdStr.substring(1);
    		dbIdStr = "("+dbIdStr+")";
    		map.put("searchDbIds", dbIdStr);
    	}
    	
    	if(searchCriteria.getStartDate() != null) 
    		map.put("startDate", searchCriteria.getStartDate());
    	
    	if(searchCriteria.getEndDate() != null) 
    		map.put("endDate", searchCriteria.getEndDate());
    	
    	return queryForList("MsExperiment.searchExperiments", map);
    }
    
    @Override
    public List<Integer> getExperimentIdsForRun(int runId) {
        return queryForList("MsExperiment.selectExperimentIdsForRun", runId);
    }

    @Override
    public List<Integer> getRunIdsForExperiment(int experimentId) {
        return queryForList("MsExperiment.selectRunIdsForExperiment", experimentId);
    }

    @Override
    public MsExperiment loadExperiment(int experimentId) {
        return (MsExperiment) queryForObject("MsExperiment.selectExperiment", experimentId);
    }

    @Override
    public int saveExperiment(MsExperiment experiment) {
        return saveAndReturnId("MsExperiment.insertExperiment", experiment);
    }

    @Override
    public void saveExperimentRun(int experimentId, int runId) {
        Map<String, Integer> map = new HashMap<String, Integer>(2);
        map.put("experimentId", experimentId);
        map.put("runId", runId);
        // if an entry for this experimentId and runId already exists don't 
        // upload another one
        if (getMatchingExptRunCount(experimentId, runId) == 0)
            save("MsExperiment.insertExperimentRun", map);
    }
    
    public void updateLastUpdateDate(int experimentId) {
       update("MsExperiment.updateLastUpdate", experimentId); 
    }
    
    @Override
    public void updateComments(int experimentId, String comments) {
        MsExperiment experiment = loadExperiment(experimentId);
        if(experiment == null) {
            log.error("Failed to update comments. No experiment found with ID: "+experimentId);
            throw new RuntimeException("Failed to update comments. No experiment found with ID: "+experimentId);
        }
        else {
            experiment.setComments(comments);
            update("MsExperiment.update", experiment);
        }
    }
    
    public void update(MsExperiment experiment) {
        MsExperiment oldExpt = loadExperiment(experiment.getId());
        if(oldExpt != null) {
            update("MsExperiment.update", experiment);
        }
        else {
            log.error("Failed to execute MsExperiment.update. No experiment found with ID: "+experiment.getId());
            throw new RuntimeException("Failed to execute MsExperiment.update. No experiment found with ID: "+experiment.getId());
        }
    }
    
    private int getMatchingExptRunCount(int experimentId, int runId) {
        Map<String, Integer> map = new HashMap<String, Integer>(2);
        map.put("experimentId", experimentId);
        map.put("runId", runId);
        Integer cnt = (Integer) queryForObject("MsExperiment.getExperimentRunCount", map);
        if (cnt == null)
            return 0;
        return cnt;
    }

    @Override
    public void deleteExperiment(int experimentId) {
        delete("MsExperiment.deleteExperiment", experimentId);
    }
}
