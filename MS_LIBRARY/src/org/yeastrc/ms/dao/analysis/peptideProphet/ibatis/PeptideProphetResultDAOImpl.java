/**
 * PeptideProphetResultDAOImpl.java
 * @author Vagisha Sharma
 * Aug 4, 2009
 * @version 1.0
 */
package org.yeastrc.ms.dao.analysis.peptideProphet.ibatis;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.yeastrc.ms.dao.analysis.MsRunSearchAnalysisDAO;
import org.yeastrc.ms.dao.analysis.peptideProphet.PeptideProphetResultDAO;
import org.yeastrc.ms.dao.ibatis.BaseSqlMapDAO;
import org.yeastrc.ms.domain.analysis.peptideProphet.PeptideProphetResult;
import org.yeastrc.ms.domain.analysis.peptideProphet.PeptideProphetResultDataWId;
import org.yeastrc.ms.domain.analysis.peptideProphet.PeptideProphetResultFilterCriteria;
import org.yeastrc.ms.domain.search.ResultSortCriteria;
import org.yeastrc.ms.domain.search.SORT_BY;

import com.ibatis.sqlmap.client.SqlMapClient;

/**
 * 
 */
public class PeptideProphetResultDAOImpl extends BaseSqlMapDAO implements PeptideProphetResultDAO {

    private static final String namespace = "PeptideProphetResult"; 
    
    private MsRunSearchAnalysisDAO runSearchAnalysisDao;
    
    public PeptideProphetResultDAOImpl(SqlMapClient sqlMap, MsRunSearchAnalysisDAO rsaDao) {
        super(sqlMap);
        this.runSearchAnalysisDao = rsaDao;
    }

    public PeptideProphetResult loadForProphetResultId(int peptideProphetResultId) {
        return (PeptideProphetResult) queryForObject(namespace+".select", peptideProphetResultId);
    }
    
    @Override
    public PeptideProphetResult loadForRunSearchAnalysis(int searchResultId, int runSearchAnalysisId) {
       Map<String, Integer> map = new HashMap<String, Integer>(4);
       map.put("searchResultId", searchResultId);
       map.put("runSearchAnalysisId", runSearchAnalysisId);
       return (PeptideProphetResult) queryForObject(namespace+".selectForRunSearchAnalysis", map);
    }

    @Override
    public PeptideProphetResult loadForSearchAnalysis(int searchResultId, int searchAnalysisId) {
        Map<String, Integer> map = new HashMap<String, Integer>(4);
        map.put("searchResultId", searchResultId);
        map.put("searchAnalysisId", searchAnalysisId);
        return (PeptideProphetResult) queryForObject(namespace+".selectForSearchAnalysis", map);
    }
    
    @Override
    public List<Integer> loadIdsForRunSearchAnalysis(int runSearchAnalysisId) {
        return queryForList(namespace+".selectResultIdsForRunSearchAnalysis", runSearchAnalysisId);
    }
    
    @Override
    public List<Integer> loadIdsForRunSearchAnalysis(int runSearchAnalysisId, int limit, int offset) {
        Map<String, Integer> map = new HashMap<String, Integer>(5);
        map.put("runSearchAnalysisId", runSearchAnalysisId);
        map.put("limit", limit);
        map.put("offset", offset);
        return queryForList(namespace+".selectResultIdsLimitedForRunSearchAnalysis", map);
    }

    @Override
    public List<Integer> loadIdsForRunSearchAnalysisScan(int runSearchAnalysisId, int scanId) {
        Map<String, Integer> map = new HashMap<String, Integer>(4);
        map.put("runSearchAnalysisId", runSearchAnalysisId);
        map.put("scanId", scanId);
        return queryForList(namespace+".selectResultIdsForRunSearchAnalysisScan", map);
    }
    
    @Override
    public List<Integer> loadIdsForAnalysis(int analysisId) {
        return queryForList(namespace+".selectResultIdsForAnalysis", analysisId);
    }
    
    @Override
    public List<Integer> loadIdsForAnalysis(int searchAnalyisId, int limit, int offset) {
        Map<String, Integer> map = new HashMap<String, Integer>(5);
        map.put("searchAnalyisId", searchAnalyisId);
        map.put("limit", limit);
        map.put("offset", offset);
        return queryForList(namespace+".selectResultIdsLimitedForAnalysis", map);
    }
    
    @Override
    public int numRunAnalysisResults(int runSearchAnalysisId) {
        return (Integer)queryForObject(namespace+".countRunSearchAnalysisResults", runSearchAnalysisId);
    }
    
    @Override
    public int numAnalysisResults(int searchAnalysisId) {
        return (Integer)queryForObject(namespace+".countSearchAnalysisResults", searchAnalysisId);
    }
    
    @Override
    public void save(PeptideProphetResultDataWId data) {
        save(namespace+".insert", data);
    }

    @Override
    public void deleteResultsForRunSearchAnalysis(int runSearchAnalysisId) {
        delete(namespace+".deleteForRunSearchAnalysis", runSearchAnalysisId);
    }

    @Override
    public List<Integer> loadIdsForSearchAnalysis(int searchAnalysisId,
            PeptideProphetResultFilterCriteria filterCriteria,
            ResultSortCriteria sortCriteria) {
        
        if(filterCriteria == null)
            filterCriteria = new PeptideProphetResultFilterCriteria();
        
        if(sortCriteria == null)
            sortCriteria = new ResultSortCriteria(null, null);
        
        int offset =  sortCriteria.getOffset() == null ? 0 : sortCriteria.getOffset();
        
        // If we don't have filters and nothing to sort by use the simple method
        if(!filterCriteria.hasFilters() && sortCriteria.getSortBy() == null) {
            return loadIdsForAnalysis(searchAnalysisId); 
        }

        boolean useScanTable = filterCriteria.hasScanFilter() || SORT_BY.isScanRelated(sortCriteria.getSortBy());
        boolean useModsTable = filterCriteria.hasMofificationFilter();
        boolean useResultsTable = filterCriteria.superHasFilters() 
                                || SORT_BY.isSearchRelated(sortCriteria.getSortBy())
                                || useScanTable
                                || useModsTable;
        
        boolean userProphetTable = filterCriteria.hasFilters() || SORT_BY.isPeptideProphetRelated(sortCriteria.getSortBy());
        
        // If we don't have any filters on the msRunSearchResult, msScan and modifications tables use a simpler query
        if(!useScanTable && !useResultsTable && !useModsTable && !userProphetTable) {
            if(sortCriteria.getLimitCount() != null)
                return loadIdsForAnalysis(searchAnalysisId, sortCriteria.getLimitCount(), offset);
            else 
                return loadIdsForAnalysis(searchAnalysisId); 
        }
        
        StringBuilder sql = new StringBuilder();
        if(!useModsTable)
        	sql.append("SELECT pres.id FROM ( ");
        else 
        	sql.append("SELECT DISTINCT pres.id FROM ( ");
        	
        sql.append("msRunSearchAnalysis AS rsa, PeptideProphetResult AS pres");
        
        
        if(useResultsTable)
            sql.append(", msRunSearchResult AS res");
        if(useScanTable)
            sql.append(", msScan AS scan");
        sql.append(" ) ");
        
        if(useModsTable) {
            sql.append("LEFT JOIN (msDynamicModResult AS dmod) ON (dmod.resultID = res.id) ");
        }
        
        sql.append("WHERE rsa.searchAnalysisID = "+searchAnalysisId+" ");
        
        if(filterCriteria.hasFileNamesFilter()) {
            List<Integer> rsaIds = getRunSearchAnalysisIds(filterCriteria.getFileNames(), searchAnalysisId);
            String rsaIdStr = "";
            for(Integer id: rsaIds) rsaIdStr += ","+id;
            if(rsaIdStr.length() > 0)   rsaIdStr = rsaIdStr.substring(1);
            sql.append("AND rsa.id IN ("+rsaIdStr+") ");
        }
        
        sql.append("AND rsa.id = pres.runSearchAnalysisID ");
        if(useResultsTable)
            sql.append("AND res.id = pres.resultID ");
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
        // Probability filter
        if(filterCriteria.hasProbabilityFilter()) {
            sql.append("AND "+filterCriteria.makeProbabilityFilterSql());
        }
      
        
        
        if(sortCriteria != null) {
            if(sortCriteria.getSortBy() != null) {
                sql.append("ORDER BY "+sortCriteria.getSortBy().getColumnName());
            }
            else {
                sql.append("ORDER BY pres.id ");
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
    public List<Integer> loadIdsForSearchAnalysisUniqPeptide(int searchAnalysisId,
            PeptideProphetResultFilterCriteria filterCriteria,
            ResultSortCriteria sortCriteria) {
        
        if(filterCriteria == null)
            filterCriteria = new PeptideProphetResultFilterCriteria();
        
        if(sortCriteria == null)
            sortCriteria = new ResultSortCriteria(null, null);
        
        int offset =  sortCriteria.getOffset() == null ? 0 : sortCriteria.getOffset();
        
        // If we don't have filters and nothing to sort by use the simple method
        if(!filterCriteria.hasFilters() && sortCriteria.getSortBy() == null) {
            return loadIdsForAnalysis(searchAnalysisId); 
        }

        boolean useScanTable = filterCriteria.hasScanFilter() || SORT_BY.isScanRelated(sortCriteria.getSortBy());
        boolean useModsTable = filterCriteria.hasMofificationFilter();
        boolean useResultsTable = filterCriteria.superHasFilters() 
                                || SORT_BY.isSearchRelated(sortCriteria.getSortBy())
                                || useScanTable
                                || useModsTable;
        
        boolean userProphetTable = filterCriteria.hasFilters() || SORT_BY.isPeptideProphetRelated(sortCriteria.getSortBy());
        
        // If we don't have any filters on the msRunSearchResult, msScan and modifications tables use a simpler query
        if(!useScanTable && !useResultsTable && !useModsTable && !userProphetTable) {
            if(sortCriteria.getLimitCount() != null)
                return loadIdsForAnalysis(searchAnalysisId, sortCriteria.getLimitCount(), offset);
            else 
                return loadIdsForAnalysis(searchAnalysisId); 
        }
        
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT pres.id, max(pres.probability) AS mp FROM ( ");
        sql.append("msRunSearchAnalysis AS rsa, PeptideProphetResult AS pres, msRunSearchResult AS res");
        
        
        if(useScanTable)
            sql.append(", msScan AS scan");
        sql.append(" ) ");
        
        if(useModsTable) {
            sql.append("LEFT JOIN (msDynamicModResult AS dmod) ON (dmod.resultID = res.id) ");
        }
        
        sql.append("WHERE rsa.searchAnalysisID = "+searchAnalysisId+" ");
        
        if(filterCriteria.hasFileNamesFilter()) {
            List<Integer> rsaIds = getRunSearchAnalysisIds(filterCriteria.getFileNames(), searchAnalysisId);
            String rsaIdStr = "";
            for(Integer id: rsaIds) rsaIdStr += ","+id;
            if(rsaIdStr.length() > 0)   rsaIdStr = rsaIdStr.substring(1);
            sql.append("AND rsa.id IN ("+rsaIdStr+") ");
        }
        
        sql.append("AND rsa.id = pres.runSearchAnalysisID ");
        sql.append("AND res.id = pres.resultID ");
        
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
        // Probability filter
        if(filterCriteria.hasProbabilityFilter()) {
            sql.append("AND "+filterCriteria.makeProbabilityFilterSql());
        }
        
        sql.append("GROUP BY res.peptide ");
        
        if(sortCriteria != null) {
            if(sortCriteria.getSortBy() != null && !sortCriteria.getSortBy().getColumnName().equals("probability")) {
                sql.append("ORDER BY "+sortCriteria.getSortBy().getColumnName());
            }
            else {
                sql.append("ORDER BY mp ");
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
    public List<Integer> loadIdsForRunSearchAnalysis(
            int runSearchAnalysisId,
            PeptideProphetResultFilterCriteria filterCriteria,
            ResultSortCriteria sortCriteria) {
        
        if(filterCriteria == null)
            filterCriteria = new PeptideProphetResultFilterCriteria();
        
        if(sortCriteria == null)
            sortCriteria = new ResultSortCriteria(null, null);
        
        int offset =  sortCriteria.getOffset() == null ? 0 : sortCriteria.getOffset();
        
        // If we don't have filters and nothing to sort by use the simple method
        if(!filterCriteria.hasFilters() && sortCriteria.getSortBy() == null) {
            return loadIdsForRunSearchAnalysis(runSearchAnalysisId); 
        }
        
        
        boolean useScanTable = filterCriteria.hasScanFilter() || SORT_BY.isScanRelated(sortCriteria.getSortBy());
        boolean useModsTable = filterCriteria.hasMofificationFilter();
        boolean useResultsTable = filterCriteria.superHasFilters() 
                                || SORT_BY.isSearchRelated(sortCriteria.getSortBy())
                                || useScanTable
                                || useModsTable;
        
        boolean userProphetTable = filterCriteria.hasFilters() || SORT_BY.isPeptideProphetRelated(sortCriteria.getSortBy());
        
        // If we don't have any filters on the msRunSearchResult, msScan and modifications tables use a simpler query
        if(!useScanTable && !useResultsTable && !useModsTable && !userProphetTable) {
            if(sortCriteria.getLimitCount() != null)
                return loadIdsForRunSearchAnalysis(runSearchAnalysisId, sortCriteria.getLimitCount(), offset);
            else 
                return loadIdsForRunSearchAnalysis(runSearchAnalysisId); 
        }
        
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT pres.id FROM ( ");
        sql.append("PeptideProphetResult as pres");
        if(useResultsTable)
            sql.append(", msRunSearchResult AS res");
        if(useScanTable)
            sql.append(", msScan AS scan");
        sql.append(" ) ");
        
        if(useModsTable) {
            sql.append("LEFT JOIN (msDynamicModResult AS dmod) ON (dmod.resultID = res.id) ");
        }
        
        sql.append("WHERE pres.runSearchAnalysisID = "+runSearchAnalysisId+" ");
        if(useResultsTable)
            sql.append("AND res.id = pres.resultID ");
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
        // Probability filter
        if(filterCriteria.hasProbabilityFilter()) {
            sql.append("AND "+filterCriteria.makeProbabilityFilterSql());
        }
        
        
        
        if(sortCriteria != null) {
            if(sortCriteria.getSortBy() != null) {
                sql.append("ORDER BY "+sortCriteria.getSortBy().getColumnName());
            }
            else {
                sql.append("ORDER BY pres.id ");
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

    private List<Integer> getRunSearchAnalysisIds(String[] fileNames, int searchAnalysisId) {
        
        List<Integer> runSearchAnalysisIds = runSearchAnalysisDao.getRunSearchAnalysisIdsForAnalysis(searchAnalysisId);

        Map<String, Integer> filenameMap = new HashMap<String, Integer>(runSearchAnalysisIds.size()*2);
        for(int runSearchAnalysisId: runSearchAnalysisIds) {
            String filename = runSearchAnalysisDao.loadFilenameForRunSearchAnalysis(runSearchAnalysisId);
            filenameMap.put(filename, runSearchAnalysisId);
        }
        List<Integer> ids = new ArrayList<Integer>();
        for(String name: fileNames) {
            if(filenameMap.containsKey(name)) 
                ids.add(filenameMap.get(name));
        }
        return ids;
    }

    @Override
    public void saveAllPeptideProphetResultData(
            List<PeptideProphetResultDataWId> dataList) {
    	
    	 if(dataList == null || dataList.size() == 0)
             return;
         StringBuilder values = new StringBuilder();
         for ( PeptideProphetResultDataWId data: dataList) {
             values.append(",(");
             values.append(data.getSearchResultId() == 0 ? "NULL" : data.getSearchResultId());
             values.append(",");
             values.append(data.getRunSearchAnalysisId() == 0 ? "NULL" : data.getRunSearchAnalysisId());
             values.append(",");
             double probability = data.getProbability();
             values.append(probability == -1.0 ? "NULL" : probability);
             values.append(",");
             double fVal = data.getfVal();
             values.append(fVal == -1.0 ? "NULL" : fVal);
             values.append(",");
             int ntt = data.getNumEnzymaticTermini();
             values.append(ntt == -1 ? "NULL" : ntt);
             values.append(",");
             int nmc = data.getNumMissedCleavages();
             values.append(nmc == -1 ? "NULL" : nmc);
             values.append(",");
             values.append(data.getMassDifference());
             values.append(",");
             values.append(data.getProbabilityNet_0() == -1.0 ? "NULL" : data.getProbabilityNet_0());
             values.append(",");
             values.append(data.getProbabilityNet_1() == -1.0 ? "NULL" : data.getProbabilityNet_1());
             values.append(",");
             values.append(data.getProbabilityNet_2() == -1.0 ? "NULL" : data.getProbabilityNet_2());
             values.append(")");
         }
         values.deleteCharAt(0);
         save(namespace+".insertAll", values.toString());
    }
}
