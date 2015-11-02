/**
 * PercolatorPeptideResultDAOImpl.java
 * @author Vagisha Sharma
 * Sep 19, 2010
 */
package org.yeastrc.ms.dao.analysis.percolator.ibatis;

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
import org.yeastrc.ms.dao.analysis.MsRunSearchAnalysisDAO;
import org.yeastrc.ms.dao.analysis.percolator.PercolatorPeptideResultDAO;
import org.yeastrc.ms.dao.analysis.percolator.PercolatorResultDAO;
import org.yeastrc.ms.dao.ibatis.BaseSqlMapDAO;
import org.yeastrc.ms.dao.search.MsRunSearchDAO;
import org.yeastrc.ms.dao.search.MsSearchModificationDAO;
import org.yeastrc.ms.domain.analysis.MsRunSearchAnalysis;
import org.yeastrc.ms.domain.analysis.percolator.PercolatorPeptideResult;
import org.yeastrc.ms.domain.analysis.percolator.PercolatorResult;
import org.yeastrc.ms.domain.analysis.percolator.PercolatorResultFilterCriteria;
import org.yeastrc.ms.domain.search.ResultSortCriteria;
import org.yeastrc.ms.domain.search.SORT_BY;
import org.yeastrc.ms.service.ModifiedSequenceBuilderException;

import com.ibatis.sqlmap.client.SqlMapClient;

/**
 * 
 */
public class PercolatorPeptideResultDAOImpl extends BaseSqlMapDAO implements
		PercolatorPeptideResultDAO {

	private static final String namespace = "PercolatorPeptideResult";
	
	private MsRunSearchAnalysisDAO runSearchAnalysisDao;
	private MsRunSearchDAO runSearchDao;
	private MsSearchModificationDAO modDao;
	private PercolatorResultDAO percResDao;
	
	public PercolatorPeptideResultDAOImpl(SqlMapClient sqlMap, MsRunSearchAnalysisDAO rsaDao, 
			MsRunSearchDAO runSearchDao, MsSearchModificationDAO modDao,
			PercolatorResultDAO percResDao) {
		super(sqlMap);
		this.runSearchAnalysisDao = rsaDao;
		this.runSearchDao = runSearchDao;
		this.modDao = modDao;
		this.percResDao = percResDao;
	}

	@Override
	public PercolatorPeptideResult load(int id) {
		PercolatorPeptideResult result = (PercolatorPeptideResult) queryForObject(namespace+".select", id);
		
		return result;
	}
	
	@Override
	public PercolatorPeptideResult loadForPercolatorResult(int percolatorResultId) {
		return (PercolatorPeptideResult) queryForObject(namespace+".selectForPercolatorResult", percolatorResultId);
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
	public int peptideCountForAnalysis(int searchAnalysisId) {
		Integer count = (Integer) queryForObject(namespace+".countForAnalysis", searchAnalysisId);
		if(count == null)
			return 0;
		else
			return count;
	}
	
	
	@Override
	public void saveAllPercolatorPeptideResults(List<PercolatorPeptideResult> peptideResultList) {
		
		String sql = "INSERT INTO PercolatorPeptideResult ";
        sql +=       "( searchAnalysisID, peptide, qvalue, pep, discriminantScore, pvalue )";
        sql +=       " VALUES (?,?,?,?,?,?)";
        
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        List<Integer> generatedKeys; 
        
        try {
            conn = ConnectionFactory.getMsDataConnection();
            stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            conn.setAutoCommit(false);
            
            for(PercolatorPeptideResult result: peptideResultList) {
                if(result.getSearchAnalysisId() == 0)    stmt.setNull(1, Types.INTEGER);
                else                                stmt.setInt(1, result.getSearchAnalysisId());
                
                stmt.setString(2, result.getResultPeptide().getModifiedPeptide());
                
                if(result.getQvalue() == -1.0)      stmt.setNull(3, Types.DOUBLE);
                else                                stmt.setDouble(3, result.getQvalue());
                
                if(result.getPosteriorErrorProbability() == -1.0)      stmt.setNull(4, Types.DOUBLE);
                else                                stmt.setDouble(4, result.getPosteriorErrorProbability());
                
                
                if(result.getDiscriminantScore() == null)	stmt.setNull(5, Types.DOUBLE);
                else										stmt.setDouble(5, result.getDiscriminantScore());
                
                if(result.getPvalue() == -1.0)      stmt.setNull(6, Types.DOUBLE);
                else                                stmt.setDouble(6, result.getPvalue());
                
                stmt.addBatch();
            }
            
            int[] counts = stmt.executeBatch();
            conn.commit();
            
            int numInserted = 0;
            for(int cnt: counts)    numInserted += cnt;
            
            if(numInserted != peptideResultList.size())
                throw new RuntimeException("Number of PercolatorPeptideResult results inserted ("+numInserted+
                        ") does not equal number input ("+peptideResultList.size()+")");
                
            
            // check that we inserted everything and get the generated ids
            rs = stmt.getGeneratedKeys();
            generatedKeys = new ArrayList<Integer>(peptideResultList.size());
            while(rs.next())
                generatedKeys.add(rs.getInt(1));
            
            if(generatedKeys.size() != numInserted)
                throw new RuntimeException("Failed to get auto_increment key for all PercolatorPeptideResult results inserted. Number of keys returned: "
                        +generatedKeys.size());
            
        }
        catch (SQLException e) {
            log.error("Failed to execute sql: "+sql, e);
            throw new RuntimeException("Failed to execute sql: "+sql, e);
        } catch (ModifiedSequenceBuilderException e) {
        	log.error("Failed to build modified peptide sequence.", e);
            throw new RuntimeException("Failed to build modified peptide sequence."+sql, e);
		}
        finally {
            if(rs != null) try { rs.close(); } catch (SQLException e){}
            if(stmt != null) try { stmt.close(); } catch (SQLException e){}
            if(conn != null) try { conn.close(); } catch (SQLException e){}
        }

        
        // now update the PSM results with the peptide resultIds
        updatePeptidePsmMatches(generatedKeys, peptideResultList);
	}
	
	private void updatePeptidePsmMatches(List<Integer> generatedKeys, List<PercolatorPeptideResult> peptideResultList) {
		
		String sql = "UPDATE PercolatorResult SET peptideResultID = ? WHERE id = ?";
        //sql +=       "( peptideResultId, psmResultId )";
        //sql +=       " VALUES (?,?)";
        
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        int numPeptidePsmMatches = 0;
        try {
            conn = ConnectionFactory.getMsDataConnection();
            stmt = conn.prepareStatement(sql);
            conn.setAutoCommit(false);
            
            int idx = 0;
            for(PercolatorPeptideResult result: peptideResultList) {
            	for(Integer psmId: result.getPsmIdList()) {
            		stmt.setInt(1, generatedKeys.get(idx));
            		stmt.setInt(2, psmId);
            		stmt.addBatch();
            		numPeptidePsmMatches++;
            	}
            	idx++;
            }
            
            int[] counts = stmt.executeBatch();
            conn.commit();
            
            int numUpdated = 0;
            for(int cnt: counts)    numUpdated += cnt;
            
            if(numUpdated != numPeptidePsmMatches)
                throw new RuntimeException("Number of PercolatorResults updated ("+numUpdated+
                        ") does not equal number input ("+numPeptidePsmMatches+")");
                
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

	@Override
	public List<Integer> loadIdsForSearchAnalysis(int searchAnalysisId,
			PercolatorResultFilterCriteria filterCriteria,
			ResultSortCriteria sortCriteria) {
		
		if(filterCriteria == null)
            filterCriteria = new PercolatorResultFilterCriteria();
        
        if(sortCriteria == null)
            sortCriteria = new ResultSortCriteria(null, null);
        
        int offset =  sortCriteria.getOffset() == null ? 0 : sortCriteria.getOffset();
        
        // If we don't have filters and nothing to sort by use the simple method
        if(!filterCriteria.hasFilters() && sortCriteria.getSortBy() == null) {
            return loadIdsForAnalysis(searchAnalysisId); 
        }

        boolean useModsTable = filterCriteria.hasMofificationFilter();
        boolean useResultsTable = filterCriteria.superHasFilters() 
                                || SORT_BY.isSearchRelated(sortCriteria.getSortBy())
                                || useModsTable;
        
        
        boolean usePercTable = filterCriteria.hasFilters() || SORT_BY.isPercolatorRelated(sortCriteria.getSortBy());
        
        // If we don't have any filters on the PercolatorPeptideResult, msRunSearchResult and modifications tables use a simpler query
        if(!useResultsTable && !useModsTable && !usePercTable) {
            if(sortCriteria.getLimitCount() != null)
                return loadIdsForAnalysis(searchAnalysisId, sortCriteria.getLimitCount(), offset);
            else 
                return loadIdsForAnalysis(searchAnalysisId); 
        }
        
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT DISTINCT (pept.id) FROM ( ");
        sql.append("PercolatorPeptideResult as pept ");
        
        if(useResultsTable)
            sql.append(", PercolatorResult AS psm, msRunSearchResult AS res ");
        
        sql.append(" ) ");
        
        if(useModsTable) {
            sql.append("LEFT JOIN (msDynamicModResult AS dmod) ON (dmod.resultID = res.id) ");
        }
        
        sql.append("WHERE pept.searchAnalysisID = "+searchAnalysisId+" ");
        
        if(filterCriteria.hasFileNamesFilter()) {
            List<Integer> rsaIds = getRunSearchAnalysisIds(filterCriteria.getFileNames(), searchAnalysisId);
            String rsaIdStr = "";
            for(Integer id: rsaIds) rsaIdStr += ","+id;
            if(rsaIdStr.length() > 0)   rsaIdStr = rsaIdStr.substring(1);
            sql.append("AND psm.runSearchAnalysisID IN ("+rsaIdStr+") ");
        }
        
        if(useResultsTable) {
        	sql.append("AND pept.id = psm.peptideResultID ");
        	sql.append("AND psm.resultID = res.id ");
        }
        // peptide filter
        if(filterCriteria.hasPeptideFilter()) {
            sql.append("AND "+filterCriteria.makePeptideSql("res"));
        }
        // modifications filter
        if(filterCriteria.hasMofificationFilter()) {
            sql.append("AND "+filterCriteria.makeModificationFilter());
        }
        // QValue filter
        if(filterCriteria.hasQValueFilter()) {
            sql.append("AND "+filterCriteria.makeQValueFilterSql("pept"));
        }
        // PValue filter
        if(filterCriteria.hasPValueFilter()) {
            sql.append("AND "+filterCriteria.makePValueFilterSql("pept"));
        }
        // PEP filter
        if(filterCriteria.hasPepFilter()) {
            sql.append("AND "+filterCriteria.makePepFilterSql("pept"));
        }
        // Discriminant Score (SVM score filter)
        if(filterCriteria.hasDsFilter()) {
            sql.append("AND "+filterCriteria.makeDsFilterSql("pept"));
        }
        
        if(sortCriteria != null) {
            if(sortCriteria.getSortBy() != null) {
            	if(SORT_BY.isPercolatorRelated(sortCriteria.getSortBy()))
            		sql.append("ORDER BY pept."+sortCriteria.getSortBy().getColumnName());
            	else if(sortCriteria.getSortBy() == SORT_BY.PEPTIDE)
            		sql.append("ORDER BY res."+sortCriteria.getSortBy().getColumnName());
            	else
            		sql.append("ORDER BY "+sortCriteria.getSortBy().getColumnName());
            }
            else {
                sql.append("ORDER BY pept.id ");
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
	public List<PercolatorResult> loadPercolatorPsms(int runSearchAnalysisId,
			Double qvalue, Double pep, Double discriminantScore) {
		
		MsRunSearchAnalysis msa = runSearchAnalysisDao.load(runSearchAnalysisId);
		if(msa == null) {
			log.error("No runSearchAnalysis found with ID: "+runSearchAnalysisId);
			throw new IllegalArgumentException("No run search analysis found with ID: "+runSearchAnalysisId);
		}

		List<Integer> percPeptideResultIds = getFilteredPeptideResultIds(msa.getAnalysisId(), qvalue, pep, discriminantScore);


		List<PercolatorResult> resultList = new ArrayList<PercolatorResult>();

		for(Integer pepResId: percPeptideResultIds) {

			List<Integer> percResIds = getPercolatorIdsForPeptideAndRunSearchAnalysisId(pepResId, runSearchAnalysisId);

			if(percResIds.size() > 0) {
				for(Integer presId: percResIds) {
					PercolatorResult pres = percResDao.loadForPercolatorResultId(presId);
					resultList.add(pres);
				}
			}
		}

		return resultList;
	}
	
	@Override
	public List<PercolatorResult> loadPercolatorPsms(int runSearchAnalysisId,
			Double peptideQvalue, Double peptidePep,
			Double peptideDiscriminantScore, Double psmQvalue, Double psmPep,
			Double psmDiscriminantScore) {
		
		MsRunSearchAnalysis msa = runSearchAnalysisDao.load(runSearchAnalysisId);
		if(msa == null) {
			log.error("No runSearchAnalysis found with ID: "+runSearchAnalysisId);
			throw new IllegalArgumentException("No run search analysis found with ID: "+runSearchAnalysisId);
		}

		List<Integer> percPeptideResultIds = getFilteredPeptideResultIds(msa.getAnalysisId(),
				peptideQvalue, peptidePep, peptideDiscriminantScore);


		List<PercolatorResult> resultList = new ArrayList<PercolatorResult>();

		for(Integer pepResId: percPeptideResultIds) {

			List<Integer> percResIds = getFilteredPercolatorIdsForPeptideAndRunSearchAnalysisId(pepResId, runSearchAnalysisId,
					psmQvalue, psmPep, psmDiscriminantScore);

			if(percResIds.size() > 0) {
				for(Integer presId: percResIds) {
					PercolatorResult pres = percResDao.loadForPercolatorResultId(presId);
					resultList.add(pres);
				}
			}
		}

		return resultList;
	}
	
	
	
	public List<Integer> getFilteredPeptideResultIds(int searchAnalysisId, Double qvalue, Double pep, 
			Double discriminantScore) {
		
		Map<String, Number> map = new HashMap<String, Number>(6);
		map.put("searchAnalysisID", searchAnalysisId);
		map.put("qvalue", qvalue);
		map.put("pep", pep);
		map.put("discriminantScore", discriminantScore);
		
		return queryForList(namespace+".selectFilteredIdsForAnalysis", map);
	}
	
	private List<Integer> getPercolatorIdsForPeptideAndRunSearchAnalysisId(
			Integer pepResId, int runSearchAnalysisId) {
		
		Map<String, Integer> map = new HashMap<String, Integer>(4);
		map.put("petideResultId", pepResId);
		map.put("runSearchAnalysisId", runSearchAnalysisId);
		
		return queryForList(namespace+".selectPercResIdsForPeptideAndRunSearchAnalysis", map);
	}
	
	private List<Integer> getFilteredPercolatorIdsForPeptideAndRunSearchAnalysisId(
			Integer pepResId, int runSearchAnalysisId, Double qvalue, Double pep, Double discriminantScore) {
		
		Map<String, Number> map = new HashMap<String, Number>(10);
		map.put("petideResultId", pepResId);
		map.put("runSearchAnalysisId", runSearchAnalysisId);
		map.put("qvalue", qvalue);
		map.put("pep", pep);
		map.put("discriminantScore", discriminantScore);
		
		return queryForList(namespace+".selectFilteredPercResIdsForPeptideAndRunSearchAnalysis", map);
	}
}
