/**
 * ProlucidSearchDAOImpl.java
 * @author Vagisha Sharma
 * Aug 26, 2008
 * @version 1.0
 */
package org.yeastrc.ms.dao.search.prolucid.ibatis;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.yeastrc.ms.dao.ibatis.BaseSqlMapDAO;
import org.yeastrc.ms.dao.search.MsSearchDAO;
import org.yeastrc.ms.dao.search.prolucid.ProlucidSearchDAO;
import org.yeastrc.ms.domain.search.Program;
import org.yeastrc.ms.domain.search.prolucid.ProlucidParam;
import org.yeastrc.ms.domain.search.prolucid.ProlucidParamIn;
import org.yeastrc.ms.domain.search.prolucid.ProlucidSearch;
import org.yeastrc.ms.domain.search.prolucid.ProlucidSearchIn;

import com.ibatis.sqlmap.client.SqlMapClient;

/**
 * 
 */
public class ProlucidSearchDAOImpl extends BaseSqlMapDAO implements ProlucidSearchDAO {

    
    private MsSearchDAO searchDao;
    
    public ProlucidSearchDAOImpl(SqlMapClient sqlMap, MsSearchDAO searchDao) {
        super(sqlMap);
        this.searchDao = searchDao;
    }
    
    public ProlucidSearch loadSearch(int searchId) {
        return (ProlucidSearch) queryForObject("ProlucidSearch.select", searchId);
    }
    
    public int saveSearch(ProlucidSearchIn search, int experimentId, int sequenceDatabaseId) {
        
        int searchId = searchDao.saveSearch(search, experimentId, sequenceDatabaseId);
        // save ProLuCID search parameters
        try {
            for (ProlucidParamIn param: search.getProlucidParams()) {
                // insert top level elements with parentID=0
                insertProlucidParam(param, 0, searchId);
            }
        }
        catch(RuntimeException e) {
            deleteSearch(searchId);
            throw e;
        }
        return searchId;
    }
    
    // recursively insert all the param elements
    private void insertProlucidParam(ProlucidParamIn param, int parentParamId, int searchId) {
        int paramId = saveAndReturnId("ProlucidSearch.insertParams", new ProlucidParamSqlMapParam(searchId, parentParamId, param));
        for (ProlucidParamIn child: param.getChildParamElements()) {
            insertProlucidParam(child, paramId, searchId);
        }
    }
    
    @Override
    public List<Integer> getSearchIdsForExperiment(int experimentId) {
        return searchDao.getSearchIdsForExperiment(experimentId);
    }

	@Override
	public List<String> getAnalysisProgramNamesForSearchAnalysisID(int searchAnalysisID) {
		return searchDao.getAnalysisProgramNamesForSearchAnalysisID( searchAnalysisID );
	}

    
    @Override
    public MassType getFragmentMassType(int searchId) {
        String val = getSearchParamValue(searchId, "fragment", "isotopes");
        if (val == null)                return null;
        if (val.equals("avg"))          return MassType.AVG;
        else if (val.equals("mono"))   return MassType.MONO;
        return null; // we don't recognize this value
    }

    @Override
    public MassType getParentMassType(int searchId) {
        String val = getSearchParamValue(searchId, "precursor", "isotopes");
        if (val == null)                return null;
        if (val.equals("avg"))          return MassType.AVG;
        else if (val.equals("mono"))   return MassType.MONO;
        return null; // we don't recognize this value
    }
    
    
    @Override
    public int getSpecificity(int searchId) {
        String val = getSearchParamValue(searchId, "specificity", "enzyme_info");
        if(val == null)
            return 2;  // default to 2 if not found
        
        return Integer.valueOf(val);
    }

    
    private String getSearchParamValue(int searchId, String paramName, String parentParamName) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("searchId", searchId);
        map.put("paramName", paramName);
        map.put("parentParamName", parentParamName);
        return (String) queryForObject("ProlucidSearch.selectSearchParamValue", map);
    }
    
    @Override
    public int updateSearchProgramVersion(int searchId,
            String versionStr) {
        return searchDao.updateSearchProgramVersion(searchId, versionStr);
    }
    
    @Override
    public int updateSearchProgram(int searchId, Program program) {
        return searchDao.updateSearchProgram(searchId, program);
    }
    
    public void deleteSearch(int searchId) {
        searchDao.deleteSearch(searchId);
    }
    
    public static final class ProlucidParamSqlMapParam implements ProlucidParam {

        private int searchId;
        private int parentId;
        private String elName;
        private String elValue;
        
        public ProlucidParamSqlMapParam(int searchId, int parentId, ProlucidParamIn param) {
            this.searchId = searchId;
            this.parentId = parentId;
            this.elName = param.getParamElementName();
            this.elValue = param.getParamElementValue();
        }
        
        public int getSearchId() {
            return searchId;
        }
        public int getId() {
            throw new UnsupportedOperationException("getId() not supported by ProlucidParamSqlMapParam");
        }

        @Override
        public String getParamElementName() {
            return elName;
        }

        @Override
        public String getParamElementValue() {
            return elValue;
        }

        @Override
        public int getParentParamElementId() {
            return parentId;
        }
    }
}
