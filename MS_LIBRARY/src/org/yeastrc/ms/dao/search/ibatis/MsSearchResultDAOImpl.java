package org.yeastrc.ms.dao.search.ibatis;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.yeastrc.ms.ConnectionFactory;
import org.yeastrc.ms.dao.ibatis.BaseSqlMapDAO;
import org.yeastrc.ms.dao.search.MsSearchModificationDAO;
import org.yeastrc.ms.dao.search.MsSearchResultDAO;
import org.yeastrc.ms.dao.search.MsSearchResultProteinDAO;
import org.yeastrc.ms.domain.search.MsResultResidueMod;
import org.yeastrc.ms.domain.search.MsSearchResult;
import org.yeastrc.ms.domain.search.MsSearchResultIn;
import org.yeastrc.ms.domain.search.MsSearchResultPeptide;
import org.yeastrc.ms.domain.search.MsSearchResultProteinIn;
import org.yeastrc.ms.domain.search.MsTerminalModificationIn;
import org.yeastrc.ms.domain.search.ValidationStatus;
import org.yeastrc.ms.domain.search.impl.MsResidueModificationWrap;
import org.yeastrc.ms.domain.search.impl.MsTerminalModificationWrap;
import org.yeastrc.ms.util.StringUtils;

import com.ibatis.sqlmap.client.SqlMapClient;
import com.ibatis.sqlmap.client.extensions.ParameterSetter;
import com.ibatis.sqlmap.client.extensions.ResultGetter;
import com.ibatis.sqlmap.client.extensions.TypeHandlerCallback;

public class MsSearchResultDAOImpl extends BaseSqlMapDAO 
        implements MsSearchResultDAO {

    private MsSearchResultProteinDAO matchDao;
    private MsSearchModificationDAO modDao;
    
    public MsSearchResultDAOImpl(SqlMapClient sqlMap, MsSearchResultProteinDAO matchDao,
            MsSearchModificationDAO modDao) {
        super(sqlMap);
        this.matchDao = matchDao;
        this.modDao = modDao;
    }

    public MsSearchResult load(int id) {
        return (MsSearchResult) queryForObject("MsSearchResult.select", id);
    }
    
    @Override
    public List<MsSearchResult> loadResultForSearchScanChargePeptide(int runSearchId,
            int scanId, int charge, String peptide) {
        Map<String, Object> map = new HashMap<String, Object>(4);
        map.put("runSearchId", runSearchId);
        map.put("scanId", scanId);
        map.put("charge", charge);
        map.put("peptide", peptide);
        return queryForList("MsSearchResult.selectResultForRunSearchScanChargePeptide", map);
    }
    
    public List<Integer> loadResultIdsForRunSearch(int runSearchId) {
        return queryForList("MsSearchResult.selectResultIdsForRunSearch", runSearchId);
    }
    
    @Override
    public List<Integer> loadResultIdsForRunSearch(int runSearchId, int limit, int offset) {
        Map<String, Integer> map = new HashMap<String, Integer>(5);
        map.put("runSearchId", runSearchId);
        map.put("limit", limit);
        map.put("offset", offset);
        return queryForList("MsSearchResult.selectResultIdsLimitedForRunSearch", map);
    }

    @Override
    public List<Integer> loadResultIdsForSearch(int searchId) {
        return queryForList("MsSearchResult.selectResultIdsForSearch", searchId);
    }

    @Override
    public List<Integer> loadResultIdsForSearch(int searchId, int limit, int offset) {
        Map<String, Integer> map = new HashMap<String, Integer>(5);
        map.put("searchId", searchId);
        map.put("limit", limit);
        map.put("offset", offset);
        return queryForList("MsSearchResult.selectResultIdsLimitedForSearch", map);
    }

    @Override
    public int numRunSearchResults(int runSearchId) {
        return (Integer)queryForObject("MsSearchResult.countRunSearchResults", runSearchId);
    }

    @Override
    public int numSearchResults(int searchId) {
        return (Integer)queryForObject("MsSearchResult.countSearchResults", searchId);
    }
    
    public List<Integer> loadResultIdsForSearchScanCharge(int runSearchId, int scanId, int charge) {
        Map<String, Integer> map = new HashMap<String, Integer>(3);
        map.put("runSearchId", runSearchId);
        map.put("scanId", scanId);
        map.put("charge", charge);
        return queryForList("MsSearchResult.selectResultIdsForRunSearchScanCharge", map);
    }
    
    @Override
    public int numResultsForRunSearchScanChargeMass(int runSearchId, int scanId,
            int charge, BigDecimal mass) {
        Map<String, Object> map = new HashMap<String, Object>(6);
        map.put("runSearchId", runSearchId);
        map.put("scanId", scanId);
        map.put("charge", charge);
        map.put("observedMass", mass);
        Integer count = (Integer) queryForObject("MsSearchResult.countResultsForRunSearchScanChargeMass", map);
        if(count == null)
            return 0;
        return count;
    }
    
    public List<Integer> loadResultIdsForSearchScan(int runSearchId, int scanId) {
        Map<String, Integer> map = new HashMap<String, Integer>(3);
        map.put("runSearchId", runSearchId);
        map.put("scanId", scanId);
        return queryForList("MsSearchResult.selectResultIdsForRunSearchScan", map);
    }
    
    @Override
    public List<Integer> loadResultIdsForSearchChargePeptide(int searchId,
            int charge, String peptide) {
        Map<String, Object> map = new HashMap<String, Object>(6);
        map.put("searchId", searchId);
        map.put("charge", charge);
        map.put("peptide", peptide);
        return queryForList("MsSearchResult.selectResultIdsForSearchChargePeptide", map);
    }
    
    @Override
    public List<Integer> loadResultIdsForSearchPeptide(int searchId, String peptide) {
        Map<String, Object> map = new HashMap<String, Object>(4);
        map.put("searchId", searchId);
        map.put("peptide", peptide);
        return queryForList("MsSearchResult.selectResultIdsForSearchPeptide", map);
    }
    
    
    @Override
    public List<Integer> loadResultIdsForSearchPeptides(int searchId, List<String> peptides) {
        Map<String, Object> map = new HashMap<String, Object>(4);
        map.put("searchId", searchId);
        map.put("peptides", "("+StringUtils.makeQuotedCommaSeparated(peptides)+")");
        return queryForList("MsSearchResult.selectResultIdsForSearchPeptides", map);
    }
    
    @Override
    public List<Integer> loadResultIdsForSearchPeptideRegex(int searchId,
            String peptideRegex) {
        Map<String, Object> map = new HashMap<String, Object>(4);
        map.put("searchId", searchId);
        map.put("peptideRegex", peptideRegex);
        return queryForList("MsSearchResult.selectResultIdsForSearchPeptideRegex", map);
    }
    
    public int save(int searchId, MsSearchResultIn searchResult, int runSearchId, int scanId) {
        
        int resultId = saveResultOnly(searchResult, runSearchId, scanId);
        
        // save any protein matches
        for(MsSearchResultProteinIn protein: searchResult.getProteinMatchList()) {
            matchDao.save(protein, resultId);
        }
        
        // save any dynamic (residue and terminal) modifications for this result
        saveDynamicModsForResult(searchId, resultId, searchResult.getResultPeptide());
        
        return resultId;
    }
    
    public int saveResultOnly(MsSearchResultIn searchResult, int runSearchId, int scanId) {

        MsSearchResultWrap resultDb = new MsSearchResultWrap(searchResult, runSearchId, scanId);
        return saveAndReturnId("MsSearchResult.insert", resultDb);
    }

    private void saveDynamicModsForResult(int searchId, int resultId, MsSearchResultPeptide peptide) {
        
        saveDynamicResidueMods(searchId, resultId, peptide);
        saveDynamicTerminalMods(searchId, resultId, peptide);
    }

    private void saveDynamicResidueMods(int searchId, int resultId,
            MsSearchResultPeptide peptide) {
        for (MsResultResidueMod mod: peptide.getResultDynamicResidueModifications()) {
            if (mod == null)
                continue;
            int modId = modDao.loadMatchingDynamicResidueModId(new MsResidueModificationWrap(mod, searchId));
            modDao.saveDynamicResidueModForResult(resultId, modId, mod.getModifiedPosition());
        }
    }
    
    private void saveDynamicTerminalMods(int searchId, int resultId,
            MsSearchResultPeptide peptide) {
        for (MsTerminalModificationIn mod: peptide.getResultDynamicTerminalModifications()) {
            if (mod == null)
                continue;
            int modId = modDao.loadMatchingDynamicTerminalModId(new MsTerminalModificationWrap(mod, searchId));
            modDao.saveDynamicTerminalModForResult(resultId, modId);
        }
    }
    
    @Override
    public <T extends MsSearchResult> List<Integer> saveResultsOnly(List<T> results) {
        
        String sql = "INSERT INTO msRunSearchResult ";
        sql +=       "( runSearchID, scanID, charge, observedMass, peptide, preResidue, postResidue, validationStatus )";
        sql +=       " VALUES (?,?,?,?,?,?,?,?)";
        
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            conn = ConnectionFactory.getMsDataConnection();
            stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            conn.setAutoCommit(false);
            
            for(MsSearchResult result: results) {
                if(result.getRunSearchId() == 0)    stmt.setNull(1, Types.INTEGER);
                else                                stmt.setInt(1, result.getRunSearchId());
                
                if(result.getScanId() == 0)         stmt.setNull(2, Types.INTEGER);
                else                                stmt.setInt(2, result.getScanId());
                
                if(result.getCharge() == 0)      stmt.setNull(3, Types.INTEGER);
                else                                stmt.setInt(3, result.getCharge());
                
                stmt.setBigDecimal(4, result.getObservedMass());
                
                stmt.setString(5, result.getResultPeptide().getPeptideSequence());
                
                String preResidue = Character.toString(result.getResultPeptide().getPreResidue());
                stmt.setString(6, preResidue);
                
                String postResidue = Character.toString(result.getResultPeptide().getPostResidue());
                stmt.setString(7, postResidue);
                
                ValidationStatus validationStatus = result.getValidationStatus();
                if(validationStatus == null || validationStatus == ValidationStatus.UNKNOWN)
                    stmt.setNull(8, Types.CHAR);
                else
                    stmt.setString(8, Character.toString(validationStatus.getStatusChar()));
               
                stmt.addBatch();
            }
            
            int[] counts = stmt.executeBatch();
            conn.commit();
            
            int numInserted = 0;
            for(int cnt: counts)    numInserted += cnt;
            
            if(numInserted != results.size())
                throw new RuntimeException("Number of results inserted ("+numInserted+
                        ") does not equal number input ("+results.size()+")");
                
            
            // check that we inserted everything and get the generated ids
            rs = stmt.getGeneratedKeys();
            List<Integer> generatedKeys = new ArrayList<Integer>(results.size());
            while(rs.next())
                generatedKeys.add(rs.getInt(1));
            
            if(generatedKeys.size() != numInserted)
                throw new RuntimeException("Failed to get auto_increment key for all results inserted. Number of keys returned: "
                        +generatedKeys.size());
            
            return generatedKeys;
        }
        catch (SQLException e) {
            log.error("Failed to execute sql: "+sql, e);
            throw new RuntimeException("Failed to execute sql: "+sql, e);
        }
        finally {
            if(rs != null) try { rs.close(); } catch (SQLException e){}
            if(stmt != null) try { stmt.close(); } catch (SQLException e){}
            if(conn != null) try { conn.close(); } catch (SQLException e){}
        }
    }
    
    public void delete(int resultId) {
        delete("MsSearchResult.delete", resultId);
    }
    
    @Override
    public void deleteResultsForRunSearch(int runSearchId) {
        delete("MsSearchResult.deleteForRunSearch", runSearchId);
    }
    
    @Override
    public void disableKeys() throws SQLException {
        Connection conn = null;
        Statement stmt = null;
        try {
            conn = super.getConnection();
            String sql = "ALTER TABLE msRunSearchResult DISABLE KEYS";
            stmt = conn.createStatement();
            stmt.executeUpdate(sql);
        }
        finally {
            try {if(conn != null) conn.close();}
            catch(SQLException e){}
            try {if(stmt != null) stmt.close();}
            catch(SQLException e){}
        }
    }

    @Override
    public void enableKeys() throws SQLException {
        Connection conn = null;
        Statement stmt = null;
        try {
            conn = super.getConnection();
            String sql = "ALTER TABLE msRunSearchResult ENABLE KEYS";
            stmt = conn.createStatement();
            stmt.executeUpdate(sql);
        }
        finally {
            try {if(conn != null) conn.close();}
            catch(SQLException e){}
            try {if(stmt != null) stmt.close();}
            catch(SQLException e){}
        }
    }
    

    /**
     * Type handler for converting between ValidationType and SQL's CHAR type.
     */
    public static final class ValidationStatusTypeHandler implements TypeHandlerCallback {

        public Object getResult(ResultGetter getter) throws SQLException {
            return stringToValidationStatus(getter.getString());
        }

        public void setParameter(ParameterSetter setter, Object parameter)
                throws SQLException {
            ValidationStatus status = (ValidationStatus) parameter;
            if (status == null || status == ValidationStatus.UNKNOWN)
                setter.setNull(java.sql.Types.CHAR);
            else
                setter.setString(Character.toString(status.getStatusChar()));
        }

        public Object valueOf(String statusStr) {
            return stringToValidationStatus(statusStr);
        }

        private Object stringToValidationStatus(String statusStr) {
            if (statusStr == null)
                return ValidationStatus.UNKNOWN;
            if (statusStr.length() != 1)
                throw new IllegalArgumentException("Cannot convert \""+statusStr+"\" to ValidationStatus");
            ValidationStatus status = ValidationStatus.instance(statusStr.charAt(0));
            if (status == ValidationStatus.UNKNOWN)
                throw new IllegalArgumentException("Unrecognized validation status: "+statusStr);
            return status;
        }
    }

}
