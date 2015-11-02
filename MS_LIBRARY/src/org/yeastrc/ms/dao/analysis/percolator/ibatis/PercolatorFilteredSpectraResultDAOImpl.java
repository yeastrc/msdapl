/**
 * PercolatorFilteredSpectraResultsDAOImpl.java
 * @author Vagisha Sharma
 * Oct 3, 2010
 */
package org.yeastrc.ms.dao.analysis.percolator.ibatis;

import java.util.ArrayList;
import java.util.List;

import org.yeastrc.ms.dao.analysis.MsRunSearchAnalysisDAO;
import org.yeastrc.ms.dao.analysis.percolator.PercolatorFilteredSpectraResultDAO;
import org.yeastrc.ms.dao.ibatis.BaseSqlMapDAO;
import org.yeastrc.ms.domain.analysis.percolator.impl.PercolatorBinnedSpectraResult;
import org.yeastrc.ms.domain.analysis.percolator.impl.PercolatorFilteredSpectraResult;

import com.ibatis.sqlmap.client.SqlMapClient;

/**
 * 
 */
public class PercolatorFilteredSpectraResultDAOImpl extends BaseSqlMapDAO implements
	PercolatorFilteredSpectraResultDAO {

	private static final String namespace = "PercolatorFilteredSpectraResult";
	private final MsRunSearchAnalysisDAO rsaDao;
	
	public PercolatorFilteredSpectraResultDAOImpl(SqlMapClient sqlMap, MsRunSearchAnalysisDAO rsaDao) {
		super(sqlMap);
		this.rsaDao = rsaDao;
	}


	@Override
	public List<PercolatorFilteredSpectraResult> loadForAnalysis(int searchAnalysisId) {
		
		List<Integer> runSearchAnalysisIds = rsaDao.getRunSearchAnalysisIdsForAnalysis(searchAnalysisId);
		List<PercolatorFilteredSpectraResult> list = new ArrayList<PercolatorFilteredSpectraResult>(runSearchAnalysisIds.size());
		for(Integer runSearchAnalysisId: runSearchAnalysisIds) {
			PercolatorFilteredSpectraResult res = this.load(runSearchAnalysisId);
			if(res != null)
				list.add(res);
		}
		
		return list;
	}
	
	@Override
	public PercolatorFilteredSpectraResult load(int runSearchAnalysisId) {
		
		return (PercolatorFilteredSpectraResult) queryForObject(namespace+".select", runSearchAnalysisId);
	}

	@Override
	public void save(PercolatorFilteredSpectraResult result) {
		
		int percSpectraResultId = 0;
		try {
			percSpectraResultId = saveAndReturnId(namespace+".insert",result);
		
			for(PercolatorBinnedSpectraResult binnedResult: result.getBinnedResults()) {
				binnedResult.setPercolatorFilteredSpectraId(percSpectraResultId);
				save(namespace+".insertBinnedResult",binnedResult);
			}
		}
		catch(RuntimeException e) {
			delete(percSpectraResultId);
			throw e;
		}
	}

	@Override
	public void delete(int runSearchAnalysisId) {
		delete(namespace+".delete",runSearchAnalysisId);
	}
	
	@Override
	public double getPopulationAvgFilteredPercent() {
		Double d = (Double)queryForObject(namespace+".selectPopulationAvgPerc", null);
		if(d != null)
			return d;
		return 0;
	}

	@Override
	public double getPopulationMax() {
		Double d = (Double)queryForObject(namespace+".selectPopulationMax", null);
		if(d != null)
			return d;
		return 0;
	}

	@Override
	public double getPopulationMin() {
		Double d = (Double)queryForObject(namespace+".selectPopulationMin", null);
		if(d != null)
			return d;
		return 0;
	}

	@Override
	public double getPopulationStdDevFilteredPercent() {
		Double d = (Double)queryForObject(namespace+".selectPopulationStdDevPerc", null);
		if(d != null)
			return d;
		return 0;
	}

}
