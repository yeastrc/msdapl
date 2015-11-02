/**
 * ProphetFilteredPsmResultDAOImpl.java
 * @author Vagisha Sharma
 * Sep 29, 2010
 */
package org.yeastrc.ms.dao.analysis.peptideProphet.ibatis;

import java.util.ArrayList;
import java.util.List;

import org.yeastrc.ms.dao.analysis.MsRunSearchAnalysisDAO;
import org.yeastrc.ms.dao.analysis.peptideProphet.ProphetFilteredPsmResultDAO;
import org.yeastrc.ms.dao.ibatis.BaseSqlMapDAO;
import org.yeastrc.ms.domain.analysis.peptideProphet.impl.ProphetBinnedPsmResult;
import org.yeastrc.ms.domain.analysis.peptideProphet.impl.ProphetFilteredPsmResult;

import com.ibatis.sqlmap.client.SqlMapClient;

/**
 * 
 */
public class ProphetFilteredPsmResultDAOImpl extends BaseSqlMapDAO implements
		ProphetFilteredPsmResultDAO {

	private static final String namespace = "ProphetFilteredPsmResult";
	private final MsRunSearchAnalysisDAO rsaDao;
	
	public ProphetFilteredPsmResultDAOImpl(SqlMapClient sqlMap, MsRunSearchAnalysisDAO rsaDao) {
		super(sqlMap);
		this.rsaDao = rsaDao;
	}
	

	@Override
	public List<ProphetFilteredPsmResult> loadForAnalysis(int searchAnalysisId) {
		
		List<Integer> runSearchAnalysisIds = rsaDao.getRunSearchAnalysisIdsForAnalysis(searchAnalysisId);
		List<ProphetFilteredPsmResult> list = new ArrayList<ProphetFilteredPsmResult>(runSearchAnalysisIds.size());
		for(Integer runSearchAnalysisId: runSearchAnalysisIds) {
			ProphetFilteredPsmResult res = this.load(runSearchAnalysisId);
			if(res != null)
				list.add(res);
		}
		
		return list;
	}
	
	@Override
	public ProphetFilteredPsmResult load(int runSearchAnalysisId) {
		
		return (ProphetFilteredPsmResult) queryForObject(namespace+".select", runSearchAnalysisId);
	}

	@Override
	public void save(ProphetFilteredPsmResult result) {
		
		int percPsmResultId = 0;
		try {
			percPsmResultId = saveAndReturnId(namespace+".insert",result);
		
			for(ProphetBinnedPsmResult binnedResult: result.getBinnedResults()) {
				binnedResult.setProphetFilteredPsmId(percPsmResultId);
				save(namespace+".insertBinnedResult",binnedResult);
			}
		}
		catch(RuntimeException e) {
			delete(percPsmResultId);
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
