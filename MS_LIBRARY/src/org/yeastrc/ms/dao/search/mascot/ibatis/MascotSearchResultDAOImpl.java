/**
 * MascotSearchResultDAOImpl.java
 * @author Vagisha Sharma
 * Oct 5, 2009
 * @version 1.0
 */
package org.yeastrc.ms.dao.search.mascot.ibatis;

import java.math.BigDecimal;
import java.sql.Connection;
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
import org.yeastrc.ms.dao.search.mascot.MascotSearchResultDAO;
import org.yeastrc.ms.domain.search.MsSearchResult;
import org.yeastrc.ms.domain.search.MsSearchResultIn;
import org.yeastrc.ms.domain.search.ResultSortCriteria;
import org.yeastrc.ms.domain.search.SORT_BY;
import org.yeastrc.ms.domain.search.mascot.MascotResultDataWId;
import org.yeastrc.ms.domain.search.mascot.MascotResultFilterCriteria;
import org.yeastrc.ms.domain.search.mascot.MascotSearchResult;
import org.yeastrc.ms.domain.search.mascot.MascotSearchResultIn;
import org.yeastrc.ms.domain.search.mascot.impl.MascotResultDataWrap;

import com.ibatis.sqlmap.client.SqlMapClient;

/**
 * 
 */
public class MascotSearchResultDAOImpl extends BaseSqlMapDAO implements MascotSearchResultDAO {

    private MsSearchResultDAO resultDao;
    private MsRunSearchDAO runSearchDao;
    private MsSearchModificationDAO modDao;
    
    public MascotSearchResultDAOImpl(SqlMapClient sqlMap,
            MsSearchResultDAO resultDao, MsRunSearchDAO runSearchDao, MsSearchModificationDAO modDao) {
        super(sqlMap);
        this.resultDao = resultDao;
        this.runSearchDao = runSearchDao;
        this.modDao = modDao;
    }
    
    public MascotSearchResult load(int resultId) {
        return (MascotSearchResult) queryForObject("MascotResult.select", resultId);
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
    public List<Integer> loadResultIdsForRunSearch(int runSearchId,
            MascotResultFilterCriteria filterCriteria,
            ResultSortCriteria sortCriteria) {
        
        if(filterCriteria == null)
            filterCriteria = new MascotResultFilterCriteria();
        
        if(sortCriteria == null)
            sortCriteria = new ResultSortCriteria(null, null);
        
        int offset =  sortCriteria.getOffset() == null ? 0 : sortCriteria.getOffset();
        
        // If we don't have filters and nothing to sort by use the simple method
        if(!filterCriteria.hasFilters() && sortCriteria.getSortBy() == null) {
            return loadResultIdsForRunSearch(runSearchId);
        }
        
        boolean useScanTable = filterCriteria.hasScanFilter() || SORT_BY.isScanRelated(sortCriteria.getSortBy());
        boolean useModsTable = filterCriteria.hasMofificationFilter();
        boolean useResultsTable = filterCriteria.superHasFilters() 
                                || SORT_BY.isSearchRelated(sortCriteria.getSortBy())
                                || useScanTable
                                || useModsTable;
        
        boolean useMascotTable = filterCriteria.hasFilters() || SORT_BY.isMascotRelated(sortCriteria.getSortBy());
        
        // If we don't have any filters on the msRunSearchResult, msScan and modifications tables use a simpler query
        if(!useScanTable && !useResultsTable && !useModsTable && !useMascotTable) {
            if(sortCriteria.getLimitCount() != null)
                return loadResultIdsForRunSearch(runSearchId, sortCriteria.getLimitCount(), offset);
            else 
                return loadResultIdsForRunSearch(runSearchId); 
        }
        
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT res.id FROM ( ");
        sql.append("msRunSearchResult AS res ");
        if(useMascotTable)
            sql.append(", MascotSearchResult AS sres");
        if(useScanTable)
            sql.append(", msScan AS scan");
        sql.append(" ) ");
        
        if(useModsTable) {
            sql.append("LEFT JOIN (msDynamicModResult AS dmod) ON (dmod.resultID = res.id) ");
        }
        
        sql.append("WHERE res.runSearchID = "+runSearchId+" ");
        if(useMascotTable)
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
        // Ion Score filter
        if(filterCriteria.hasIonScoreFilter()) {
            sql.append("AND "+filterCriteria.makeIonScoreFilterSql());
        }
        // Identity Score filter
        if(filterCriteria.hasIdentityScoreFilter()) {
            sql.append("AND "+filterCriteria.makeIdentityScoreFilterSql());
        }
        // Homology Score filter
        if(filterCriteria.hasHomologyScoreFilter()) {
            sql.append("AND "+filterCriteria.makeHomologyScoreFilterSql());
        }
        // Expect filter
        if(filterCriteria.hasExpectFilter()) {
            sql.append("AND "+filterCriteria.makeExpectFilterSql());
        }
        // star filter
        if(filterCriteria.hasStarFilter()) {
            sql.append("AND "+filterCriteria.makeStarFilterSql());
        }
        // rank filter
        if(filterCriteria.hasRankFilter()) {
            sql.append("AND "+filterCriteria.makeRankFilterSql());
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
            MascotResultFilterCriteria filterCriteria,
            ResultSortCriteria sortCriteria) {
        
        if(filterCriteria == null)
            filterCriteria = new MascotResultFilterCriteria();
        
        if(sortCriteria == null)
            sortCriteria = new ResultSortCriteria(null, null);
        
        int offset =  sortCriteria.getOffset() == null ? 0 : sortCriteria.getOffset();
        
        // If we don't have filters and nothing to sort by use the simple method
        if(!filterCriteria.hasFilters() && sortCriteria.getSortBy() == null) {
            return loadResultIdsForSearch(searchId);
        }
        
        boolean useScanTable = filterCriteria.hasScanFilter() || SORT_BY.isScanRelated(sortCriteria.getSortBy());
        boolean useModsTable = filterCriteria.hasMofificationFilter();
        boolean useResultsTable = filterCriteria.superHasFilters() 
                                || SORT_BY.isSearchRelated(sortCriteria.getSortBy())
                                || useScanTable
                                || useModsTable;
        
        boolean useMascotTable = filterCriteria.hasFilters() || SORT_BY.isMascotRelated(sortCriteria.getSortBy());
        
        // If we don't have any filters on the msRunSearchResult, msScan and modifications tables use a simpler query
        if(!useScanTable && !useResultsTable && !useModsTable && !useMascotTable) {
            if(sortCriteria.getLimitCount() != null)
                return loadResultIdsForSearch(searchId, sortCriteria.getLimitCount(), offset);
            else 
                return loadResultIdsForSearch(searchId); 
        }
        
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT res.id FROM ( ");
        sql.append("msRunSearchResult AS res, msRunSearch AS rs");
        if(useMascotTable)
            sql.append(", MascotSearchResult AS sres");
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
        if(useMascotTable)
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
        // Ion Score filter
        if(filterCriteria.hasIonScoreFilter()) {
            sql.append("AND "+filterCriteria.makeIonScoreFilterSql());
        }
        // Identity Score filter
        if(filterCriteria.hasIdentityScoreFilter()) {
            sql.append("AND "+filterCriteria.makeIdentityScoreFilterSql());
        }
        // Homology Score filter
        if(filterCriteria.hasHomologyScoreFilter()) {
            sql.append("AND "+filterCriteria.makeHomologyScoreFilterSql());
        }
        // Expect filter
        if(filterCriteria.hasExpectFilter()) {
            sql.append("AND "+filterCriteria.makeExpectFilterSql());
        }
        // star filter
        if(filterCriteria.hasStarFilter()) {
            sql.append("AND "+filterCriteria.makeStarFilterSql());
        }
        // rank filter
        if(filterCriteria.hasRankFilter()) {
            sql.append("AND "+filterCriteria.makeRankFilterSql());
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
    
    public int save(int searchId, MascotSearchResultIn searchResult, int runSearchId, int scanId) {
        
        // first save the base result
        int resultId = resultDao.save(searchId, searchResult, runSearchId, scanId);
        
        // now save the Mascot specific information
        MascotResultDataWrap resultDb = new MascotResultDataWrap(searchResult.getMascotResultData(), resultId);
        save("MascotResult.insert", resultDb);
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
    public void saveAllMascotResultData(List<MascotResultDataWId> resultDataList) {
        if (resultDataList.size() == 0)
            return;
        StringBuilder values = new StringBuilder();
        for ( MascotResultDataWId data: resultDataList) {
            values.append(",(");
            values.append(data.getResultId() == 0 ? "NULL" : data.getResultId());
            values.append(",");
            values.append(data.getRank() == 0 ? "NULL" : data.getRank());
            values.append(",");
            values.append(data.getIonScore());
            values.append(",");
            values.append(data.getIdentityScore());
            values.append(",");
            values.append(data.getHomologyScore());
            values.append(",");
            values.append(data.getExpect());
            values.append(",");
            int star = data.getStar();
            values.append(star == -1 ? "NULL" : star);
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
        
        save("MascotResult.insertAll", values.toString());
    }
    
    /**
     * Deletes the search result and any Mascot specific information associated with the result
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
            String sql = "ALTER TABLE MascotSearchResult DISABLE KEYS";
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
            String sql = "ALTER TABLE MascotSearchResult ENABLE KEYS";
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
