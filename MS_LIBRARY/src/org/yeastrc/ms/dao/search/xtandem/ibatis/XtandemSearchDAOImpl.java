/**
 * XtandemSearchDAOImpl.java
 * @author Vagisha Sharma
 * Oct 20, 2009
 * @version 1.0
 */
package org.yeastrc.ms.dao.search.xtandem.ibatis;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.yeastrc.ms.dao.ibatis.BaseSqlMapDAO;
import org.yeastrc.ms.dao.search.MsSearchDAO;
import org.yeastrc.ms.dao.search.xtandem.XtandemSearchDAO;
import org.yeastrc.ms.domain.search.Param;
import org.yeastrc.ms.domain.search.Program;
import org.yeastrc.ms.domain.search.xtandem.XtandemSearch;
import org.yeastrc.ms.domain.search.xtandem.XtandemSearchIn;

import com.ibatis.sqlmap.client.SqlMapClient;

/**
 * 
 */
public class XtandemSearchDAOImpl extends BaseSqlMapDAO implements XtandemSearchDAO {

    private MsSearchDAO searchDao;
    
    public XtandemSearchDAOImpl(SqlMapClient sqlMap, MsSearchDAO searchDao) {
        super(sqlMap);
        this.searchDao = searchDao;
    }
    
    public XtandemSearch loadSearch(int searchId) {
        return (XtandemSearch) queryForObject("XtandemSearch.select", searchId);
    }
    
    public int saveSearch(XtandemSearchIn search, int experimentId, int sequenceDatabaseId) {
        
        int searchId = searchDao.saveSearch(search, experimentId, sequenceDatabaseId);
        
        // save Xtandem search parameters
        try {
            for (Param param: search.getXtandemParams()) {
                save("XtandemSearch.insertParams", new XtandemParamSqlMapParam(searchId, param));
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
        return (String) queryForObject("XtandemSearch.selectSearchParamValue", map);
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
    
    public static final class XtandemParamSqlMapParam implements Param {

        private int searchId;
        private Param param;
        
        public XtandemParamSqlMapParam(int searchId, Param param) {
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