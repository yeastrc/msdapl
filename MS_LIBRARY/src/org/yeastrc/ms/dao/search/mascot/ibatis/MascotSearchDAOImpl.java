/**
 * MascotSearchDAOImpl.java
 * @author Vagisha Sharma
 * Oct 05, 2009
 * @version 1.0
 */
package org.yeastrc.ms.dao.search.mascot.ibatis;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.yeastrc.ms.dao.ibatis.BaseSqlMapDAO;
import org.yeastrc.ms.dao.search.MsSearchDAO;
import org.yeastrc.ms.dao.search.mascot.MascotSearchDAO;
import org.yeastrc.ms.domain.search.Param;
import org.yeastrc.ms.domain.search.Program;
import org.yeastrc.ms.domain.search.mascot.MascotSearch;
import org.yeastrc.ms.domain.search.mascot.MascotSearchIn;

import com.ibatis.sqlmap.client.SqlMapClient;

/**
 * 
 */
public class MascotSearchDAOImpl extends BaseSqlMapDAO implements MascotSearchDAO {

    private MsSearchDAO searchDao;
    
    public MascotSearchDAOImpl(SqlMapClient sqlMap, MsSearchDAO searchDao) {
        super(sqlMap);
        this.searchDao = searchDao;
    }
    
    public MascotSearch loadSearch(int searchId) {
        return (MascotSearch) queryForObject("MascotSearch.select", searchId);
    }
    
    public int saveSearch(MascotSearchIn search, int experimentId, int sequenceDatabaseId) {
        
        int searchId = searchDao.saveSearch(search, experimentId, sequenceDatabaseId);
        
        // save Mascot search parameters
        try {
            for (Param param: search.getMascotParams()) {
                save("MascotSearch.insertParams", new MascotParamSqlMapParam(searchId, param));
            }
        }
        catch(RuntimeException e) {
           deleteSearch(searchId);
           throw e;
        }
        return searchId;
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
        String val = getSearchParamValue(searchId, "precursor_mass_type");
        if (val == null)            return null;
        if (val.equals("monoisotopic"))        return MassType.MONO;
        else if (val.equals("average"))   return MassType.AVG;
        return null; // we don't recognize this value
    }

    @Override
    public MassType getParentMassType(int searchId) {
        String val = getSearchParamValue(searchId, "precursor_mass_type");
        if (val == null)            return null;
        if (val.equals("monoisotopic"))        return MassType.MONO;
        else if (val.equals("average"))   return MassType.AVG;
        return null; // we don't recognize this value
    }
    
    @Override
    public String getSearchParamValue(int searchId, String paramName) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("searchId", searchId);
        map.put("paramName", paramName);
        return (String) queryForObject("MascotSearch.selectSearchParamValue", map);
    }
    
    @Override
    public int getNumEnzymaticTermini(int searchId) {
        String val = getSearchParamValue(searchId, "min_number_termini");
        if(val == null)
            return 0;
        
        return Integer.valueOf(val);
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
    
    public static final class MascotParamSqlMapParam implements Param {

        private int searchId;
        private Param param;
        
        public MascotParamSqlMapParam(int searchId, Param param) {
            this.searchId = searchId;
            this.param = param;
        }
        
        public int getSearchId() {
            return searchId;
        }
        @Override
        public String getParamName() {
            return param.getParamName();
        }

        @Override
        public String getParamValue() {
            return param.getParamValue();
        }
    }

}