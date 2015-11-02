/**
 * ProlucidSearchResultDAOImpl.java
 * @author Vagisha Sharma
 * Aug 26, 2008
 * @version 1.0
 */
package org.yeastrc.ms.dao.search.prolucid.ibatis;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.yeastrc.ms.dao.ibatis.BaseSqlMapDAO;
import org.yeastrc.ms.dao.search.MsRunSearchDAO;
import org.yeastrc.ms.dao.search.MsSearchModificationDAO;
import org.yeastrc.ms.dao.search.MsSearchResultDAO;
import org.yeastrc.ms.dao.search.prolucid.ProlucidSearchResultDAO;
import org.yeastrc.ms.domain.search.MsResidueModification;
import org.yeastrc.ms.domain.search.MsResultResidueMod;
import org.yeastrc.ms.domain.search.MsRunSearch;
import org.yeastrc.ms.domain.search.MsSearchResult;
import org.yeastrc.ms.domain.search.MsSearchResultIn;
import org.yeastrc.ms.domain.search.ValidationStatus;
import org.yeastrc.ms.domain.search.impl.ResultResidueModBean;
import org.yeastrc.ms.domain.search.impl.SearchResultPeptideBean;
import org.yeastrc.ms.domain.search.prolucid.ProlucidResultDataWId;
import org.yeastrc.ms.domain.search.prolucid.ProlucidSearchResult;
import org.yeastrc.ms.domain.search.prolucid.ProlucidSearchResultIn;
import org.yeastrc.ms.domain.search.prolucid.impl.ProlucidResultDataWrap;
import org.yeastrc.ms.domain.search.prolucid.impl.ProlucidSearchResultBean;

import com.ibatis.sqlmap.client.SqlMapClient;

/**
 * 
 */
public class ProlucidSearchResultDAOImpl extends BaseSqlMapDAO implements
ProlucidSearchResultDAO {

    private MsSearchResultDAO resultDao;
    private MsRunSearchDAO runSearchDao;
    private MsSearchModificationDAO modDao;

    public ProlucidSearchResultDAOImpl(SqlMapClient sqlMap,
            MsSearchResultDAO resultDao, MsRunSearchDAO runSearchDao, MsSearchModificationDAO modDao) {
        super(sqlMap);
        this.resultDao = resultDao;
        this.runSearchDao = runSearchDao;
        this.modDao = modDao;
    }

    @Override
    public ProlucidSearchResult load(int resultId) {
        return (ProlucidSearchResult) queryForObject("ProlucidResult.select", resultId);
    }

    @Override
    public List<MsSearchResult> loadResultForSearchScanChargePeptide(int runSearchId,
            int scanId, int charge, String peptide) {
        return resultDao.loadResultForSearchScanChargePeptide(runSearchId, scanId, charge, peptide);
    }
    
    @Override
    public List<Integer> loadResultIdsForSearchChargePeptide(int searchId,
            int charge, String peptide) {
        return resultDao.loadResultIdsForSearchChargePeptide(searchId, charge, peptide);
    }
    
    @Override
    public List<Integer> loadResultIdsForSearchPeptide(int searchId, String peptide) {
        return resultDao.loadResultIdsForSearchPeptide(searchId, peptide);
    }
    
    @Override
    public List<Integer> loadResultIdsForSearchPeptides(int searchId, List<String> peptides) {
        return resultDao.loadResultIdsForSearchPeptides(searchId, peptides);
    }
    
    @Override
    public List<Integer> loadResultIdsForSearchPeptideRegex(int searchId,
            String peptideRegex) {
        return resultDao.loadResultIdsForSearchPeptideRegex(searchId, peptideRegex);
    }
    
    @Override
    public int numResultsForRunSearchScanChargeMass(int runSearchId,
            int scanId, int charge, BigDecimal mass) {
        return resultDao.numResultsForRunSearchScanChargeMass(runSearchId, scanId, charge, mass);
    }
    
    @Override
    public List<Integer> loadResultIdsForRunSearch(int runSearchId) {
        return resultDao.loadResultIdsForRunSearch(runSearchId);
    }

    @Override
    public List<Integer> loadResultIdsForRunSearch(int runSearchId, int limit,
            int offset) {
        return resultDao.loadResultIdsForRunSearch(runSearchId, limit, offset);
    }

    @Override
    public List<Integer> loadResultIdsForSearch(int searchId) {
        return resultDao.loadResultIdsForSearch(searchId);
    }

    @Override
    public List<Integer> loadResultIdsForSearch(int searchId, int limit,
            int offset) {
        return resultDao.loadResultIdsForSearch(searchId, limit, offset);
    }

    @Override
    public int numRunSearchResults(int runSearchId) {
        return resultDao.numRunSearchResults(runSearchId);
    }

    @Override
    public int numSearchResults(int searchId) {
        return resultDao.numSearchResults(searchId);
    }
    
    @Override
    public List<Integer> loadTopResultIdsForRunSearch(int runSearchId) {
        return queryForList("ProlucidResult.selectTopResultIdsForRunSearch", runSearchId);
    }
    
//    @Override
//    public List<ProlucidSearchResult> loadTopResultsForRunSearchN(int runSearchId, boolean getDynaResMods) {
//        return queryForList("ProlucidResult.selectTopResultsForRunSearchN", runSearchId);
//    }
    
    /**
     * Returns the top hits (XCorr rank = 1) for a search. If multiple rank=1 hits
     * are found for a scan + charge combination all are returned.
     */
    public List<ProlucidSearchResult> loadTopResultsForRunSearchN(int runSearchId, boolean getDynaResMods) {
        
        if(!getDynaResMods)
            return loadTopResultsForRunSearchNNoMods(runSearchId);
        else
            return loadTopResultsForRunSearchNWMods(runSearchId);
    }
 
    private List<ProlucidSearchResult> loadTopResultsForRunSearchNWMods(int runSearchId) {
        
        // get the dynamic residue modifications for the search
        MsRunSearch runSearch = runSearchDao.loadRunSearch(runSearchId);
        if(runSearch == null) {
            log.error("No run search found with ID: "+runSearchId);
            throw new IllegalArgumentException("No run search found with ID: "+runSearchId);
        }
        List<MsResidueModification> searchDynaMods = modDao.loadDynamicResidueModsForSearch(runSearch.getSearchId());
        Map<Integer, MsResidueModification> dynaModMap = new HashMap<Integer, MsResidueModification>();
        for(MsResidueModification mod: searchDynaMods) {
            dynaModMap.put(mod.getId(), mod);
        }
        
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        String sql = "SELECT * FROM (msRunSearchResult AS res, ProLuCIDSearchResult AS pres) "+
        "LEFT JOIN (msDynamicModResult AS dmod) ON (dmod.resultID = res.id) "+
        "WHERE res.id = pres.resultID "+
        "AND pres.primaryScoreRank=1 "+
        "AND runSearchID=? "+
        "ORDER BY res.id";
        
        try {
            
            conn = super.getConnection();
            stmt = conn.prepareStatement( sql );
            stmt.setInt( 1, runSearchId );
            rs = stmt.executeQuery();
            
            List<ProlucidSearchResult> resultList = new ArrayList<ProlucidSearchResult>();
            
            ProlucidSearchResultBean lastResult = null;
            List<MsResultResidueMod> resultDynaMods = new ArrayList<MsResultResidueMod>();
            
            
            while ( rs.next() ) {
            
                int resultId = rs.getInt("id");
                
                if(lastResult == null || resultId != lastResult.getId()) {
                    
                    if(lastResult != null) {
                        lastResult.getResultPeptide().setDynamicResidueModifications(resultDynaMods);
                    }
                    
                    ProlucidSearchResultBean result = makeProlucidSearchResult(rs);
                    resultList.add(result);
                    
                    lastResult = result;
                    resultDynaMods = new ArrayList<MsResultResidueMod>();
                }
                
                int modId = rs.getInt("modID");
                if(modId != 0) {
                    ResultResidueModBean resMod = makeResultResidueMod(rs, dynaModMap.get(modId));
                    
                    resultDynaMods.add(resMod);
                }
            
            }
            if(lastResult != null)
                lastResult.getResultPeptide().setDynamicResidueModifications(resultDynaMods);
            
            return resultList;
          
        }
        catch (Exception e) {
            log.error("Failed to execute query: "+sql, e);
            throw new RuntimeException("Failed to execute query: "+sql, e);
        } finally {
            
            if (rs != null) {
                try { rs.close(); rs = null; } catch (Exception e) { ; }
            }

            if (stmt != null) {
                try { stmt.close(); stmt = null; } catch (Exception e) { ; }
            }
            
            if (conn != null) {
                try { conn.close(); conn = null; } catch (Exception e) { ; }
            }           
        }
    }
    
    
    
    private ResultResidueModBean makeResultResidueMod(ResultSet rs, MsResidueModification searchDynaMod)
                        throws SQLException {
        ResultResidueModBean resMod = new ResultResidueModBean();
        resMod.setModifiedPosition(rs.getInt("position"));
        resMod.setModificationMass(searchDynaMod.getModificationMass());
        resMod.setModificationSymbol(searchDynaMod.getModificationSymbol());
        resMod.setModifiedResidue(searchDynaMod.getModifiedResidue());
        return resMod;
    }
    
    private List<ProlucidSearchResult> loadTopResultsForRunSearchNNoMods(int runSearchId) {
        
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        String sql = "SELECT * from msRunSearchResult as res, ProLuCIDSearchResult as pres WHERE"+
        " res.id = pres.resultID AND pres.primaryScoreRank=1 AND res.runSearchID = ?"+
        " ORDER BY res.id";
        // " GROUP BY res.scanID, res.charge ORDER BY res.id";
        
        try {
            
            conn = super.getConnection();
            stmt = conn.prepareStatement( sql );
            stmt.setInt( 1, runSearchId );
            rs = stmt.executeQuery();
            
            List<ProlucidSearchResult> resultList = new ArrayList<ProlucidSearchResult>();
            
            while ( rs.next() ) {
            
                ProlucidSearchResultBean result = makeProlucidSearchResult(rs);
                
                resultList.add(result);
            
            }
            rs.close(); rs = null;
            stmt.close(); stmt = null;
            conn.close(); conn = null;
            
            return resultList;
            
        }
        catch (Exception e) {
            log.error("Failed to execute query: "+sql, e);
            throw new RuntimeException("Failed to execute query: "+sql, e);
        } finally {
            
            if (rs != null) {
                try { rs.close(); rs = null; } catch (Exception e) { ; }
            }

            if (stmt != null) {
                try { stmt.close(); stmt = null; } catch (Exception e) { ; }
            }
            
            if (conn != null) {
                try { conn.close(); conn = null; } catch (Exception e) { ; }
            }           
        }
    }


    private ProlucidSearchResultBean makeProlucidSearchResult(ResultSet rs)
            throws SQLException {
        
        ProlucidSearchResultBean result = new ProlucidSearchResultBean();
        result.setId(rs.getInt("id"));
        result.setRunSearchId(rs.getInt("runSearchID"));
        result.setScanId(rs.getInt("scanID"));
        result.setCharge(rs.getInt("charge"));
        result.setObservedMass(rs.getBigDecimal("observedMass"));
        SearchResultPeptideBean peptide = new SearchResultPeptideBean();
        peptide.setPeptideSequence(rs.getString("peptide"));
        String preRes = rs.getString("preResidue");
        if(preRes != null)
            peptide.setPreResidue(preRes.charAt(0));
        String postRes = rs.getString("postResidue");
        if(postRes != null)
            peptide.setPostResidue(postRes.charAt(0));
        result.setResultPeptide(peptide);
        String vStatus = rs.getString("validationStatus");
        if(vStatus != null)
            result.setValidationStatus(ValidationStatus.instance(vStatus.charAt(0)));
        result.setPrimaryScore(rs.getDouble("primaryScore"));
        result.setPrimaryScoreRank(rs.getInt("primaryScoreRank"));
        result.setSecondaryScore(rs.getDouble("secondaryScore"));
        result.setSecondaryScoreRank(rs.getInt("secondaryScoreRank"));
        result.setDeltaCN(rs.getBigDecimal("deltaCN"));
        result.setCalculatedMass(rs.getBigDecimal("calculatedMass"));
        result.setMatchingIons(rs.getInt("matchingIons"));
        result.setPredictedIons(rs.getInt("predictedIons"));
        return result;
    }
    
    @Override
    public List<Integer> loadResultIdsForSearchScanCharge(int runSearchId,
            int scanId, int charge) {
        return resultDao.loadResultIdsForSearchScanCharge(runSearchId, scanId, charge);
    }

    @Override
    public List<Integer> loadResultIdsForSearchScan(int runSearchId, int scanId) {
        return resultDao.loadResultIdsForSearchScan(runSearchId, scanId);
    }

    @Override
    public int save(int searchId, ProlucidSearchResultIn searchResult, int runSearchId, int scanId) {
        // first save the base result
        int resultId = resultDao.save(searchId, searchResult, runSearchId, scanId);

        // now save the ProLuCID specific information
        ProlucidResultDataWrap resultDb = new ProlucidResultDataWrap(searchResult.getProlucidResultData(), resultId);
        save("ProlucidResult.insert", resultDb);
        return resultId;
    }

    @Override
    public int saveResultOnly(MsSearchResultIn searchResult,
            int runSearchId, int scanId) {
        // save the base result (saves data to msRunSearchResult table only).
        return resultDao.saveResultOnly(searchResult, runSearchId, scanId);
    }
    
    @Override
    public <T extends MsSearchResult> List<Integer> saveResultsOnly(List<T> results) {
        return resultDao.saveResultsOnly(results);
    }

    /**
     * resultID, 
        primaryScoreRank,
        secondaryScoreRank,
        primaryScore,
        secondaryScore,
        deltaCN, 
        calculatedMass,
        matchingIons,
        predictedIons
     */
    @Override
    public void saveAllProlucidResultData(
            List<ProlucidResultDataWId> resultDataList) {
        if (resultDataList.size() == 0)
            return;
        StringBuilder values = new StringBuilder();
        for (ProlucidResultDataWId data: resultDataList) {
            values.append(",(");
            values.append(data.getResultId() == 0 ? "NULL" : data.getResultId());
            values.append(",");
            values.append(data.getPrimaryScoreRank() == -1 ? "NULL" : data.getPrimaryScoreRank());
            values.append(",");
            values.append(data.getSecondaryScoreRank()== -1 ? "NULL" : data.getSecondaryScoreRank());
            values.append(",");
            values.append(data.getPrimaryScore());
            values.append(",");
            values.append(data.getSecondaryScore());
            values.append(",");
            values.append(data.getDeltaCN());
            values.append(",");
            values.append(data.getCalculatedMass());
            values.append(",");
            values.append(data.getMatchingIons() == -1 ? "NULL" : data.getMatchingIons());
            values.append(",");
            values.append(data.getPredictedIons() == -1 ? "NULL" : data.getPredictedIons()  );
            values.append(")\n");
        }
        values.deleteCharAt(0);
        
        String insertSQL = values.toString();
        
        String statementName = "ProlucidResult.insertAll";

        try {
            save(statementName, insertSQL);
        }
        catch (RuntimeException e) {
            log.error("Failed to execute save statement: " + statementName + " , insert SQL: \n" + insertSQL, e);
            throw e;
            
        } 
//        catch (Exception e) { 
//            log.error("Failed to execute save statement: " + statementName + " , insert SQL: \n" + insertSQL, e);
//            throw new RuntimeException("Failed to execute save statement: " + statementName, e);
//        }

    }

    @Override
    public void delete(int resultId) {
        resultDao.delete(resultId);
    }

    @Override
    public void deleteResultsForRunSearch(int runSearchId) {
        resultDao.deleteResultsForRunSearch(runSearchId);
    }
}
