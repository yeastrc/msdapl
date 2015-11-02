/**
 * MsPeptideSearchDAOImpl.java
 * @author Vagisha Sharma
 * Jul 4, 2008
 * @version 1.0
 */
package org.yeastrc.ms.dao.search.ibatis;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.yeastrc.ms.dao.general.MsEnzymeDAO;
import org.yeastrc.ms.dao.ibatis.BaseSqlMapDAO;
import org.yeastrc.ms.dao.search.MsSearchDAO;
import org.yeastrc.ms.dao.search.MsSearchDatabaseDAO;
import org.yeastrc.ms.dao.search.MsSearchModificationDAO;
import org.yeastrc.ms.domain.general.MsEnzymeIn;
import org.yeastrc.ms.domain.search.MsResidueModificationIn;
import org.yeastrc.ms.domain.search.MsSearch;
import org.yeastrc.ms.domain.search.MsSearchDatabaseIn;
import org.yeastrc.ms.domain.search.MsSearchIn;
import org.yeastrc.ms.domain.search.MsTerminalModificationIn;
import org.yeastrc.ms.domain.search.Program;
import org.yeastrc.ms.domain.search.impl.MsResidueModificationWrap;
import org.yeastrc.ms.domain.search.impl.MsSearchDatabaseWrap;
import org.yeastrc.ms.domain.search.impl.MsTerminalModificationWrap;

import com.ibatis.sqlmap.client.SqlMapClient;
import com.ibatis.sqlmap.client.extensions.ParameterSetter;
import com.ibatis.sqlmap.client.extensions.ResultGetter;
import com.ibatis.sqlmap.client.extensions.TypeHandlerCallback;

/**
 * 
 */
public class MsSearchDAOImpl extends BaseSqlMapDAO implements MsSearchDAO {

    private MsSearchDatabaseDAO seqDbDao;
    private MsSearchModificationDAO modDao;
    private MsEnzymeDAO enzymeDao;
    
    public MsSearchDAOImpl(SqlMapClient sqlMap, 
            MsSearchDatabaseDAO seqDbDao,
            MsSearchModificationDAO modDao,
            MsEnzymeDAO enzymeDao) {
        super(sqlMap);
        this.seqDbDao = seqDbDao;
        this.modDao = modDao;
        this.enzymeDao = enzymeDao;
    }
    
    public MsSearch loadSearch(int searchId) {
        return (MsSearch) queryForObject("MsSearch.select", searchId);
    }
    
    public int saveSearch(MsSearchIn search, int experimentId, int sequenceDatabaseId) {
        
        final int searchId = saveAndReturnId("MsSearch.insert", new MsSearchWrap(search, experimentId));
        
        try {
            // save any database information associated with the search 
            for (MsSearchDatabaseIn seqDb: search.getSearchDatabases()) {
                seqDbDao.saveSearchDatabase(new MsSearchDatabaseWrap(seqDb, sequenceDatabaseId), searchId);
            }

            // save any static residue modifications used for the search
            for (final MsResidueModificationIn staticMod: search.getStaticResidueMods()) {
                modDao.saveStaticResidueMod(new MsResidueModificationWrap(staticMod, searchId));
            }

            // save any dynamic residue modifications used for the search
            for (final MsResidueModificationIn dynaMod: search.getDynamicResidueMods()) {
                modDao.saveDynamicResidueMod(new MsResidueModificationWrap(dynaMod, searchId));
            }

            // save any static terminal modifications used for the search
            for (final MsTerminalModificationIn staticMod: search.getStaticTerminalMods()) {
                modDao.saveStaticTerminalMod(new MsTerminalModificationWrap(staticMod, searchId));
            }

            // save any dynamic residue modifications used for the search
            for (final MsTerminalModificationIn dynaMod: search.getDynamicTerminalMods()) {
                modDao.saveDynamicTerminalMod(new MsTerminalModificationWrap(dynaMod, searchId));
            }

            // save any enzymes used for the search
            List<MsEnzymeIn> enzymes = search.getEnzymeList();
            for (MsEnzymeIn enzyme: enzymes) 
                // use all enzyme attributes to look for a matching enzyme.
                enzymeDao.saveEnzymeforSearch(enzyme, searchId);
        }
        catch(RuntimeException e) {
            deleteSearch(searchId); // this will delete anything that got saved with the searchId
            throw e;
        }
        return searchId;
    }
    
    @Override
    public int updateSearchProgramVersion(int searchId,
            String versionStr) {
        Map<String, Object> map = new HashMap<String, Object>(2);
        map.put("searchId", searchId);
        map.put("analysisProgramVersion", versionStr);
        return update("MsSearch.updateAnalysisProgramVersion", map);
    }
    
    @Override
    public int updateSearchProgram(int searchId, Program program) {
        Map<String, Object> map = new HashMap<String, Object>(2);
        map.put("searchId", searchId);
        map.put("analysisProgram", program);
        return update("MsSearch.updateAnalysisProgram", map);
    }
    
    @Override
    public List<Integer> getSearchIdsForExperiment(int experimentId) {
        return queryForList("MsSearch.selectSearchIdsForExperiment", experimentId);
    }
    
    
    @Override
    public List<String> getAnalysisProgramNamesForSearchAnalysisID(int searchAnalysisID) {
        return queryForList("MsSearch.selectAnalysisProgramNamesForSearchAnalysisID", searchAnalysisID);
    }
    
//    SELECT msSearch.*
//    FROM msSearch WHERE id IN (
//
//       SELECT DISTINCT( msRunSearch.searchID ) FROM msRunSearchAnalysis 
//            INNER JOIN msRunSearch ON msRunSearchAnalysis.runSearchID = msRunSearch.id
//            WHERE msRunSearchAnalysis.searchAnalysisID = 1
//    )
    
    public void deleteSearch(int searchId) {
        delete("MsSearch.delete", searchId);
    }
    
    //---------------------------------------------------------------------------------------
    /** 
     * Type handler for converting between SearchProgram and JDBC's VARCHAR types. 
     */
    public static class SearchProgramTypeHandler implements TypeHandlerCallback {

        public Object getResult(ResultGetter getter) throws SQLException {
            String program = getter.getString();
            if (getter.wasNull())
                return Program.UNKNOWN;
            return Program.instance(program);
        }

        public void setParameter(ParameterSetter setter, Object parameter)
                throws SQLException {
            if (parameter == null)
                setter.setNull(java.sql.Types.VARCHAR);
            else
                setter.setString(((Program)parameter).name());
        }

        public Object valueOf(String s) {
            return Program.instance(s);
        }
    }
}
