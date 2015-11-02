/**
 * SQTSearchResultDAOImpl.java
 * @author Vagisha Sharma
 * Jul 4, 2008
 * @version 1.0
 */
package org.yeastrc.ms.dao.search.sequest.ibatis;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.yeastrc.ms.dao.ibatis.BaseSqlMapDAO;
import org.yeastrc.ms.dao.search.MsRunSearchDAO;
import org.yeastrc.ms.dao.search.MsSearchModificationDAO;
import org.yeastrc.ms.dao.search.MsSearchResultDAO;
import org.yeastrc.ms.dao.search.sequest.SequestSearchResultDAO;
import org.yeastrc.ms.domain.search.MsResidueModification;
import org.yeastrc.ms.domain.search.MsResultResidueMod;
import org.yeastrc.ms.domain.search.MsRunSearch;
import org.yeastrc.ms.domain.search.MsSearchResult;
import org.yeastrc.ms.domain.search.MsSearchResultIn;
import org.yeastrc.ms.domain.search.ResultSortCriteria;
import org.yeastrc.ms.domain.search.SORT_BY;
import org.yeastrc.ms.domain.search.ValidationStatus;
import org.yeastrc.ms.domain.search.impl.ResultResidueModBean;
import org.yeastrc.ms.domain.search.sequest.SequestResultDataWId;
import org.yeastrc.ms.domain.search.sequest.SequestResultFilterCriteria;
import org.yeastrc.ms.domain.search.sequest.SequestSearchResult;
import org.yeastrc.ms.domain.search.sequest.SequestSearchResultIn;
import org.yeastrc.ms.domain.search.sequest.impl.SequestResultDataWrap;
import org.yeastrc.ms.domain.search.sequest.impl.SequestSearchResultBean;

import com.ibatis.sqlmap.client.SqlMapClient;

/**
 * 
 */
public class SequestSearchResultDAOImpl extends BaseSqlMapDAO implements SequestSearchResultDAO {

    private MsSearchResultDAO resultDao;
    private MsRunSearchDAO runSearchDao;
    private MsSearchModificationDAO modDao;
    
    public SequestSearchResultDAOImpl(SqlMapClient sqlMap,
            MsSearchResultDAO resultDao, MsRunSearchDAO runSearchDao, MsSearchModificationDAO modDao) {
        super(sqlMap);
        this.resultDao = resultDao;
        this.runSearchDao = runSearchDao;
        this.modDao = modDao;
    }
    
    public SequestSearchResult load(int resultId) {
        return (SequestSearchResult) queryForObject("SequestResult.select", resultId);
    }
    
    @Override
    public List<MsSearchResult> loadResultForSearchScanChargePeptide(int runSearchId,
            int scanId, int charge, String peptide) {
        return resultDao.loadResultForSearchScanChargePeptide(runSearchId, scanId, charge, peptide);
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
        return queryForList("SequestResult.selectTopResultIdsForRunSearch", runSearchId);
    }
    
//    @Override
//    public List<SequestSearchResult> loadTopResultsForRunSearchN(int runSearchId, boolean getDynaResMods) {
//        return queryForList("SequestResult.selectTopResultsForRunSearchN", runSearchId);
//    }
    
    
    @Override
    public List<Integer> loadResultIdsForRunSearch(int runSearchId,
            SequestResultFilterCriteria filterCriteria,
            ResultSortCriteria sortCriteria) {
        
        if(filterCriteria == null)
            filterCriteria = new SequestResultFilterCriteria();
        
        if(sortCriteria == null)
            sortCriteria = new ResultSortCriteria(null, null);
        
        int offset =  sortCriteria.getOffset() == null ? 0 : sortCriteria.getOffset();
        
        // If we don't have filters and nothing to sort by use the simple method
        if(!filterCriteria.hasFilters() && sortCriteria.getSortBy() == null) {
            return loadResultIdsForRunSearch(runSearchId);
        }
        
        boolean useScanTable = filterCriteria.hasScanFilter()
        						|| filterCriteria.hasRTFilter()
        						|| SORT_BY.isScanRelated(sortCriteria.getSortBy());
        boolean useModsTable = filterCriteria.hasMofificationFilter();
        boolean useResultsTable = filterCriteria.superHasFilters() 
                                || SORT_BY.isSearchRelated(sortCriteria.getSortBy())
                                || useScanTable
                                || useModsTable;
        
        boolean useSeqTable = filterCriteria.hasFilters() || SORT_BY.isSequestRelated(sortCriteria.getSortBy());
        
        // If we don't have any filters on the msRunSearchResult, msScan and modifications tables use a simpler query
        if(!useScanTable && !useResultsTable && !useModsTable && !useSeqTable) {
            if(sortCriteria.getLimitCount() != null)
                return loadResultIdsForRunSearch(runSearchId, sortCriteria.getLimitCount(), offset);
            else 
                return loadResultIdsForRunSearch(runSearchId); 
        }
        
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT res.id FROM ( ");
        sql.append("msRunSearchResult AS res ");
        if(useSeqTable)
            sql.append(", SQTSearchResult AS sres");
        if(useScanTable)
            sql.append(", msScan AS scan");
        sql.append(" ) ");
        
        if(useModsTable) {
            sql.append("LEFT JOIN (msDynamicModResult AS dmod) ON (dmod.resultID = res.id) ");
        }
        
        sql.append("WHERE res.runSearchID = "+runSearchId+" ");
        if(useSeqTable)
            sql.append("AND res.id = sres.resultID ");
        if(useScanTable) {
            sql.append("AND res.scanID = scan.id ");
        }
        
        // filter of scan number
        if(filterCriteria.hasScanFilter()) {
            sql.append("AND "+filterCriteria.makeScanFilterSql());
        }
        // filter on retention time
        if(filterCriteria.hasRTFilter()) {
            sql.append("AND "+filterCriteria.makeRTFilterSql());
        }
        // filter on charge
        if(filterCriteria.hasChargeFilter()) {
            sql.append("AND "+filterCriteria.makeChargeFilterSql());
        }
        // observed mass filter
        if(filterCriteria.hasMassFilter()) {
            sql.append("AND "+filterCriteria.makeMassFilterSql());
        }
        // peptide filter
        if(filterCriteria.hasPeptideFilter()) {
            sql.append("AND "+filterCriteria.makePeptideSql());
        }
        // modifications filter
        if(filterCriteria.hasMofificationFilter()) {
            sql.append("AND "+filterCriteria.makeModificationFilter());
        }
        // XCorr rank filter
        if(filterCriteria.hasXcorrRankFilter()) {
            sql.append("AND "+filterCriteria.makeXCorrRankFilterSql());
        }
        // XCorr filter
        if(filterCriteria.hasXCorrFilter()) {
            sql.append("AND "+filterCriteria.makeXCorrFilterSql());
        }
        // DeltaCN filter
        if(filterCriteria.hasDeltaCnFilter()) {
            sql.append("AND "+filterCriteria.makeDeltaCnFilterSql());
        }
        // Sp filter
        if(filterCriteria.hasSpFilter()) {
            sql.append("AND "+filterCriteria.makeSpFilterSql());
        }
        
        
        if(sortCriteria != null) {
            if(sortCriteria.getSortBy() != null) {
                sql.append("ORDER BY "+sortCriteria.getSortBy().getColumnName());
            }
            else {
                sql.append("ORDER BY res.id ");
            }
        }
        
        if(sortCriteria.getLimitCount() != null) {
            sql.append("LIMIT "+sortCriteria.getLimitCount()+", "+offset);
        }
        
        System.out.println(sql.toString());
        
        
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = super.getConnection();
            stmt = conn.createStatement();
            rs = stmt.executeQuery(sql.toString());
            
            List<Integer> resultIds = new ArrayList<Integer>();
            
            while ( rs.next() ) {
                resultIds.add(rs.getInt("id"));
            }
            return resultIds;
        }
        catch (Exception e) {
            log.error("Failed to execute query: "+sql.toString(), e);
            throw new RuntimeException("Failed to execute query: "+sql, e);
        }
        finally {
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
    
    private List<Integer> getRunSearchIds(String[] fileNames, int searchId) {
    
        List<Integer> runSearchIds = runSearchDao.loadRunSearchIdsForSearch(searchId);

        Map<String, Integer> filenameMap = new HashMap<String, Integer>(runSearchIds.size()*2);
        for(int runSearchId: runSearchIds) {
            String filename = runSearchDao.loadFilenameForRunSearch(runSearchId);
            filenameMap.put(filename, runSearchId);
        }
        List<Integer> ids = new ArrayList<Integer>();
        for(String name: fileNames) {
            if(filenameMap.containsKey(name)) 
                ids.add(filenameMap.get(name));
        }
        return ids;
    }

    @Override
    public List<Integer> loadResultIdsForSearch(int searchId,
            SequestResultFilterCriteria filterCriteria,
            ResultSortCriteria sortCriteria) {
        
        if(filterCriteria == null)
            filterCriteria = new SequestResultFilterCriteria();
        
        if(sortCriteria == null)
            sortCriteria = new ResultSortCriteria(null, null);
        
        int offset =  sortCriteria.getOffset() == null ? 0 : sortCriteria.getOffset();
        
        // If we don't have filters and nothing to sort by use the simple method
        if(!filterCriteria.hasFilters() && sortCriteria.getSortBy() == null) {
            return loadResultIdsForSearch(searchId);
        }
        
        boolean useScanTable = filterCriteria.hasScanFilter() 
        						|| filterCriteria.hasRTFilter()
        						|| SORT_BY.isScanRelated(sortCriteria.getSortBy());
        boolean useModsTable = filterCriteria.hasMofificationFilter();
        boolean useResultsTable = filterCriteria.superHasFilters() 
                                || SORT_BY.isSearchRelated(sortCriteria.getSortBy())
                                || useScanTable
                                || useModsTable;
        
        boolean useSeqTable = filterCriteria.hasFilters() || SORT_BY.isSequestRelated(sortCriteria.getSortBy());
        
        // If we don't have any filters on the msRunSearchResult, msScan and modifications tables use a simpler query
        if(!useScanTable && !useResultsTable && !useModsTable && !useSeqTable) {
            if(sortCriteria.getLimitCount() != null)
                return loadResultIdsForSearch(searchId, sortCriteria.getLimitCount(), offset);
            else 
                return loadResultIdsForSearch(searchId); 
        }
        
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT res.id FROM ( ");
        sql.append("msRunSearchResult AS res, msRunSearch AS rs");
        if(useSeqTable)
            sql.append(", SQTSearchResult AS sres");
        if(useScanTable)
            sql.append(", msScan AS scan");
        sql.append(" ) ");
        
        if(useModsTable) {
            sql.append("LEFT JOIN (msDynamicModResult AS dmod) ON (dmod.resultID = res.id) ");
        }
        
        sql.append("WHERE rs.searchID = "+searchId+" ");
        if(filterCriteria.hasFileNamesFilter()) {
            List<Integer> rsIds = getRunSearchIds(filterCriteria.getFileNames(), searchId);
            String rsIdStr = "";
            for(Integer id: rsIds) rsIdStr += ","+id;
            if(rsIdStr.length() > 0)   rsIdStr = rsIdStr.substring(1);
            sql.append("AND rs.id IN ("+rsIdStr+") ");
        }
        sql.append("AND rs.id = res.runSearchID ");
        if(useSeqTable)
            sql.append("AND res.id = sres.resultID ");
        if(useScanTable) {
            sql.append("AND res.scanID = scan.id ");
        }
        
        // filter of scan number
        if(filterCriteria.hasScanFilter()) {
            sql.append("AND "+filterCriteria.makeScanFilterSql());
        }
        // filter on retention time
        if(filterCriteria.hasRTFilter()) {
            sql.append("AND "+filterCriteria.makeRTFilterSql());
        }
        // filter on charge
        if(filterCriteria.hasChargeFilter()) {
            sql.append("AND "+filterCriteria.makeChargeFilterSql());
        }
        // observed mass filter
        if(filterCriteria.hasMassFilter()) {
            sql.append("AND "+filterCriteria.makeMassFilterSql());
        }
        // peptide filter
        if(filterCriteria.hasPeptideFilter()) {
            sql.append("AND "+filterCriteria.makePeptideSql());
        }
        // modifications filter
        if(filterCriteria.hasMofificationFilter()) {
            sql.append("AND "+filterCriteria.makeModificationFilter());
        }
        // XCorr rank filter
        if(filterCriteria.hasXcorrRankFilter()) {
            sql.append("AND "+filterCriteria.makeXCorrRankFilterSql());
        }
        // XCorr filter
        if(filterCriteria.hasXCorrFilter()) {
            sql.append("AND "+filterCriteria.makeXCorrFilterSql());
        }
        // DeltaCN filter
        if(filterCriteria.hasDeltaCnFilter()) {
            sql.append("AND "+filterCriteria.makeDeltaCnFilterSql());
        }
        // Sp filter
        if(filterCriteria.hasSpFilter()) {
            sql.append("AND "+filterCriteria.makeSpFilterSql());
        }
        
        
        if(sortCriteria != null) {
            if(sortCriteria.getSortBy() != null) {
                sql.append("ORDER BY "+sortCriteria.getSortBy().getColumnName());
            }
            else {
                sql.append("ORDER BY res.id ");
            }
        }
        
        if(sortCriteria.getLimitCount() != null) {
            sql.append("LIMIT "+sortCriteria.getLimitCount()+", "+offset);
        }
        
        System.out.println(sql.toString());
        
        
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = super.getConnection();
            stmt = conn.createStatement();
            rs = stmt.executeQuery(sql.toString());
            
            List<Integer> resultIds = new ArrayList<Integer>();
            
            while ( rs.next() ) {
                resultIds.add(rs.getInt("id"));
            }
            return resultIds;
        }
        catch (Exception e) {
            log.error("Failed to execute query: "+sql.toString(), e);
            throw new RuntimeException("Failed to execute query: "+sql, e);
        }
        finally {
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
    
    
    /**
     * Returns the top hits (XCorr rank = 1) for a search. If multiple rank=1 hits
     * are found for a scan + charge combination all are returned. 
     */
    public List<SequestSearchResult> loadTopResultsForRunSearchN(int runSearchId, boolean getDynaResMods) {
        
        if(!getDynaResMods)
            return loadTopResultsForRunSearchNNoMods(runSearchId);
        else
            return loadTopResultsForRunSearchNWMods(runSearchId);
    }
    
    private List<SequestSearchResult> loadTopResultsForRunSearchNWMods(int runSearchId) {
    
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
        
        String sql = "SELECT * FROM (msRunSearchResult AS res, SQTSearchResult AS sres) "+
        "LEFT JOIN (msDynamicModResult AS dmod) ON (dmod.resultID = res.id) "+
        "WHERE res.id = sres.resultID "+
        "AND sres.XCorrRank = 1 "+
        "AND runSearchID=? "+
        "ORDER BY res.id";
        
        try {
            
            conn = super.getConnection();
            stmt = conn.prepareStatement( sql );
            stmt.setInt( 1, runSearchId );
            rs = stmt.executeQuery();
            
            
            List<SequestSearchResult> resultList = new ArrayList<SequestSearchResult>();
            
            SequestSearchResultBean lastResult = null;
            List<MsResultResidueMod> resultDynaMods = new ArrayList<MsResultResidueMod>();
            
            
            while ( rs.next() ) {
            
                int resultId = rs.getInt("id");
                
                if(lastResult == null || resultId != lastResult.getId()) {
                    
                    if(lastResult != null) {
                        lastResult.getResultPeptide().setDynamicResidueModifications(resultDynaMods);
                    }
                    
                    SequestSearchResultBean result = makeSequestSearchResult(rs);
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
        catch (SQLException e) {
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
    
    private List<SequestSearchResult> loadTopResultsForRunSearchNNoMods(int runSearchId) {
        
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        String sql = "SELECT * from msRunSearchResult as res, SQTSearchResult as sres WHERE"+
        " res.id = sres.resultID AND sres.XCorrRank = 1 AND res.runSearchID = ?"+
        " ORDER BY res.id";
        //" GROUP BY res.scanID, res.charge ORDER BY res.id";
        
        try {
            
            conn = super.getConnection();
            stmt = conn.prepareStatement( sql );
            stmt.setInt( 1, runSearchId );
            rs = stmt.executeQuery();
            
            
            List<SequestSearchResult> resultList = new ArrayList<SequestSearchResult>();
            
            
            while ( rs.next() ) {
            
                SequestSearchResultBean result = makeSequestSearchResult(rs);
                resultList.add(result);
            
            }
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

    private SequestSearchResultBean makeSequestSearchResult(ResultSet rs)
            throws SQLException {
        SequestSearchResultBean result = new SequestSearchResultBean();
        result.setId(rs.getInt("resultID"));
        result.setRunSearchId(rs.getInt("runSearchID"));
        result.setScanId(rs.getInt("scanID"));
        result.setCharge(rs.getInt("charge"));
        result.setObservedMass(rs.getBigDecimal("observedMass"));
        result.setPeptideSequence(rs.getString("peptide"));
        String preRes = rs.getString("preResidue");
        if(preRes != null)
            result.setPreResidue(preRes.charAt(0));
        String postRes = rs.getString("postResidue");
        if(postRes != null)
            result.setPostResidue(postRes.charAt(0));
        String vStatus = rs.getString("validationStatus");
        if(vStatus != null)
            result.setValidationStatus(ValidationStatus.instance(vStatus.charAt(0)));
        result.setSp(rs.getBigDecimal("sp"));
        result.setSpRank(rs.getInt("spRank"));
        result.setxCorr(rs.getBigDecimal("XCorr"));
        result.setxCorrRank(rs.getInt("XCorrRank"));
        result.setDeltaCN(rs.getBigDecimal("deltaCN"));
        if(rs.getObject("deltaCNstar") != null)
            result.setDeltaCNstar(rs.getBigDecimal("deltaCNstar"));
        if(rs.getObject("evalue") != null)
            result.setEvalue(rs.getDouble("evalue"));
        result.setCalculatedMass(rs.getBigDecimal("calculatedMass"));
        result.setMatchingIons(rs.getInt("matchingIons"));
        result.setPredictedIons(rs.getInt("predictedIons"));
        return result;
    }
    
    
    @Override
    public List<Integer> loadResultIdsForSearchScanCharge(int runSearchId, int scanId, int charge) {
        return resultDao.loadResultIdsForSearchScanCharge(runSearchId, scanId, charge);
    }
    
    @Override
    public int numResultsForRunSearchScanChargeMass(int runSearchId,
            int scanId, int charge, BigDecimal mass) {
        return resultDao.numResultsForRunSearchScanChargeMass(runSearchId, scanId, charge, mass);
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
    public List<Integer> loadResultIdsForSearchScan(int runSearchId, int scanId) {
        return resultDao.loadResultIdsForSearchScan(runSearchId, scanId);
    }
    
    public int save(int searchId, SequestSearchResultIn searchResult, int runSearchId, int scanId) {
        
        // first save the base result
        int resultId = resultDao.save(searchId, searchResult, runSearchId, scanId);
        
        // now save the Sequest specific information
        SequestResultDataWrap resultDb = new SequestResultDataWrap(searchResult.getSequestResultData(), resultId);
        save("SequestResult.insert", resultDb);
        return resultId;
    }
    
    @Override
    public int saveResultOnly(MsSearchResultIn searchResult, int runSearchId,
            int scanId) {
        // save the base result (saves data to msRunSearchResult table only).
        return resultDao.saveResultOnly(searchResult, runSearchId, scanId);
    }
    
    @Override
    public <T extends MsSearchResult> List<Integer> saveResultsOnly(List<T> results) {
        return resultDao.saveResultsOnly(results);
    }
    
    @Override
    public void saveAllSequestResultData(List<SequestResultDataWId> resultDataList) {
        if (resultDataList.size() == 0)
            return;
        StringBuilder values = new StringBuilder();
        for ( SequestResultDataWId data: resultDataList) {
            values.append(",(");
            values.append(data.getResultId() == 0 ? "NULL" : data.getResultId());
            values.append(",");
            values.append(data.getSp());
            values.append(",");
            int spRank = data.getSpRank();
            values.append(spRank == -1 ? "NULL" : spRank);
            values.append(",");
            values.append(data.getxCorr());
            values.append(",");
            int xcorrRank = data.getxCorrRank();
            values.append(xcorrRank == -1 ? "NULL" : xcorrRank);
            values.append(",");
            values.append(data.getDeltaCN());
            values.append(",");
            values.append(data.getDeltaCNstar());
            values.append(",");
            values.append(data.getEvalue());
            values.append(",");
            values.append(data.getCalculatedMass());
            values.append(",");
            int mIons = data.getMatchingIons();
            values.append(mIons == -1 ? "NULL" : mIons);
            values.append(",");
            int pIons = data.getPredictedIons();
            values.append(pIons == -1 ? "NULL" : pIons);
            values.append(")");
        }
        values.deleteCharAt(0);
        
        save("SequestResult.insertAll", values.toString());
    }
    
    /**
     * Deletes the search result and any Sequest specific information associated with the result
     * @param resultId
     */
    public void delete(int resultId) {
        resultDao.delete(resultId);
    }

    @Override
    public void deleteResultsForRunSearch(int runSearchId) {
        resultDao.deleteResultsForRunSearch(runSearchId);
    }
    
    @Override
    public void disableKeys() throws SQLException {
        Connection conn = null;
        Statement stmt = null;
        try {
            conn = super.getConnection();
            String sql = "ALTER TABLE SQTSearchResult DISABLE KEYS";
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
            String sql = "ALTER TABLE SQTSearchResult ENABLE KEYS";
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
}
