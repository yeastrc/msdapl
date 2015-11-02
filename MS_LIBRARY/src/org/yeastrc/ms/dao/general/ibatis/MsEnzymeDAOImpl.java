/**
 * MsDigestionEnzymeDAOImpl.java
 * @author Vagisha Sharma
 * Jul 1, 2008
 * @version 1.0
 */
package org.yeastrc.ms.dao.general.ibatis;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.yeastrc.ms.dao.general.MsEnzymeDAO;
import org.yeastrc.ms.dao.ibatis.BaseSqlMapDAO;
import org.yeastrc.ms.domain.general.MsEnzyme;
import org.yeastrc.ms.domain.general.MsEnzymeIn;
import org.yeastrc.ms.domain.general.MsEnzyme.Sense;

import com.ibatis.sqlmap.client.SqlMapClient;
import com.ibatis.sqlmap.client.extensions.ParameterSetter;
import com.ibatis.sqlmap.client.extensions.ResultGetter;
import com.ibatis.sqlmap.client.extensions.TypeHandlerCallback;

/**
 * 
 */
public class MsEnzymeDAOImpl extends BaseSqlMapDAO implements MsEnzymeDAO {

    public MsEnzymeDAOImpl(SqlMapClient sqlMap) {
        super(sqlMap);
    }

    //------------------------------------------------------------------------------------------------
    // SAVE, LOAD and DELETE enzymes (msDigestionEnzyme table)
    //------------------------------------------------------------------------------------------------
    public MsEnzyme loadEnzyme(int enzymeId) {
        return (MsEnzyme) queryForObject("MsEnzyme.selectEnzymeById", enzymeId);
    }

    public List<MsEnzyme> loadEnzymes(String name) {
        Map<String, Object> properties = new HashMap<String, Object>(1);
        properties.put("name", name);
        return loadEnzymes(properties);
    }

    public List<MsEnzyme> loadEnzymes(String name, Sense sense, String cut,
            String nocut) {
        Map<String, Object> map = new HashMap<String, Object>(4);
        map.put("name", name);
        map.put("sense", getSenseForDb(sense));
        map.put("cut", cut);
        map.put("nocut", nocut);
        return loadEnzymes(map);
    }

    private Short getSenseForDb(Sense sense) {
        if (sense == null || sense == Sense.UNKNOWN) return null;
        return (sense.getShortVal());
    }
    
    private List<MsEnzyme> loadEnzymes(Map<String, Object> properties) {
        if (properties == null || properties.size() == 0)
            return null;
        return queryForList("MsEnzyme.selectEnzymes", properties);
    }
    
    public int saveEnzyme(MsEnzymeIn enzyme) {
        return saveEnzyme(enzyme, Arrays.asList(EnzymeProperties.values()));
    }
    
    public int saveEnzyme(MsEnzymeIn enzyme, List<EnzymeProperties> params) {
        
        Map<String, Object> properties = new HashMap<String, Object>(params.size());
        for (EnzymeProperties param: params) {
            if (param == EnzymeProperties.NAME)
                properties.put("name", enzyme.getName());
            else if (param == EnzymeProperties.SENSE)
                properties.put("sense",getSenseForDb(enzyme.getSense()));
            else if (param == EnzymeProperties.CUT)
                properties.put("cut",enzyme.getCut());
            else if (param == EnzymeProperties.NOTCUT)
                properties.put("nocut",enzyme.getNocut());
        }
        
        List<MsEnzyme> enzymesFromDb = loadEnzymes(properties);
        // if we found an enzyme return its database id
        if (enzymesFromDb.size() > 0)   
            return enzymesFromDb.get(0).getId();
        
        // otherwise save the enzyme and return its database id
        return saveAndReturnId("MsEnzyme.insert", enzyme);
    }
    
    public void deleteEnzymeById(int enzymeId) {
        delete("MsEnzyme.deleteEnzymeById", enzymeId);
    }
    
    //------------------------------------------------------------------------------------------------
    // Enzymes for a RUN
    //------------------------------------------------------------------------------------------------
    public List<MsEnzyme> loadEnzymesForRun(int runId) {
        return queryForList("MsEnzyme.selectEnzymesForRun", runId);
    }
    
    public int saveEnzymeforRun(MsEnzymeIn enzyme, int runId) {
        
        return saveEnzymeforRun(enzyme, runId, Arrays.asList(EnzymeProperties.values()));
    }
    
    public int saveEnzymeforRun(MsEnzymeIn enzyme, int runId, List<EnzymeProperties> properties) {
        
        int enzymeId = saveEnzyme(enzyme, properties);
        
        // now save an entry in the msRunEnzyme table liking this enzyme to the given runId
        saveEnzymeForRun(enzymeId, runId);
        
        return enzymeId;
    }
    
    public void saveEnzymeForRun(int enzymeId, int runId) {
        Map<String, Integer> map = new HashMap<String, Integer>(2);
        map.put("runID", runId);
        map.put("enzymeID", enzymeId);
        save("MsEnzyme.insertRunEnzyme", map);
    }

    public void deleteEnzymesForRun(int runId) {
        delete("MsEnzyme.deleteEnzymesByRunId", runId);
    }

    public void deleteEnzymesForRuns(List<Integer> runIds) {
        if (runIds == null || runIds.size() == 0) return;
        Map<String, List<Integer>> map = new HashMap<String, List<Integer>>(1);
        map.put("runIdList", runIds);
        delete("MsEnzyme.deleteEnzymesByRunIds", map);
    }
    
    
    //------------------------------------------------------------------------------------------------
    // Enzymes for a SEARCH
    //------------------------------------------------------------------------------------------------
    @Override
    public List<MsEnzyme> loadEnzymesForSearch(int searchId) {
        return queryForList("MsEnzyme.selectEnzymesForSearch", searchId);
    }
    
    @Override
    public int saveEnzymeforSearch(MsEnzymeIn enzyme, int searchId) {
        return saveEnzymeforSearch(enzyme, searchId, Arrays.asList(EnzymeProperties.values()));
    }

    @Override
    public int saveEnzymeforSearch(MsEnzymeIn enzyme, int searchId,
            List<EnzymeProperties> properties) {
        int enzymeId = saveEnzyme(enzyme, properties);
        
        // now save an entry in the msRunEnzyme table liking this enzyme to the given runId
        saveEnzymeForSearch(enzymeId, searchId);
        
        return enzymeId;
    }
    
    public void saveEnzymeForSearch(int enzymeId, int searchId) {
        Map<String, Integer> map = new HashMap<String, Integer>(2);
        map.put("searchID", searchId);
        map.put("enzymeID", enzymeId);
        save("MsEnzyme.insertSearchEnzyme", map);
    }
    
    @Override
    public void deleteEnzymesForSearch(int searchId) {
        delete("MsEnzyme.deleteEnzymesBySearchId", searchId);
    }

    @Override
    public void deleteEnzymesForSearches(List<Integer> searchIds) {
        if (searchIds == null || searchIds.size() == 0) return;
        Map<String, List<Integer>> map = new HashMap<String, List<Integer>>(1);
        map.put("searchIdList", searchIds);
        delete("MsEnzyme.deleteEnzymesBySearchIds", map);
    }

    
    
    /**
     * Type handler for converting between Sense and JDBC's SMALLINT types. 
     */
    public static class SenseTypeHandler implements TypeHandlerCallback {

        public Object getResult(ResultGetter getter) throws SQLException {
            short sense = getter.getShort();
            if (getter.wasNull())
                return Sense.UNKNOWN;
            return Sense.instance(sense);
        }

        public void setParameter(ParameterSetter setter, Object parameter)
                throws SQLException {
            if (parameter == null || parameter == Sense.UNKNOWN)
                setter.setNull(java.sql.Types.TINYINT);
            else 
                setter.setShort(((Sense)parameter).getShortVal());
        }

        public Object valueOf(String s) {
            return Sense.instance(Short.valueOf(s));
        }
    }

}
