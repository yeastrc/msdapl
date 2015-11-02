/**
 * ProphetFilteredSpectraResultDAOImpl.java
 * @author Vagisha Sharma
 * Aug 7, 2011
 */
package org.yeastrc.ms.dao.analysis.peptideProphet.ibatis;

import java.util.ArrayList;
import java.util.List;

import org.yeastrc.ms.dao.analysis.MsRunSearchAnalysisDAO;
import org.yeastrc.ms.dao.analysis.peptideProphet.ProphetFilteredSpectraResultDAO;
import org.yeastrc.ms.dao.ibatis.BaseSqlMapDAO;
import org.yeastrc.ms.domain.analysis.peptideProphet.impl.ProphetBinnedSpectraResult;
import org.yeastrc.ms.domain.analysis.peptideProphet.impl.ProphetFilteredSpectraResult;

import com.ibatis.sqlmap.client.SqlMapClient;

/**
 * 
 */
public class ProphetFilteredSpectraResultDAOImpl extends BaseSqlMapDAO implements
	ProphetFilteredSpectraResultDAO {

	private static final String namespace = "ProphetFilteredSpectraResult";
	private final MsRunSearchAnalysisDAO rsaDao;
	
	public ProphetFilteredSpectraResultDAOImpl(SqlMapClient sqlMap, MsRunSearchAnalysisDAO rsaDao) {
		super(sqlMap);
		this.rsaDao = rsaDao;
	}


	@Override
	public List<ProphetFilteredSpectraResult> loadForAnalysis(int searchAnalysisId) {
		
		List<Integer> runSearchAnalysisIds = rsaDao.getRunSearchAnalysisIdsForAnalysis(searchAnalysisId);
		List<ProphetFilteredSpectraResult> list = new ArrayList<ProphetFilteredSpectraResult>(runSearchAnalysisIds.size());
		for(Integer runSearchAnalysisId: runSearchAnalysisIds) {
			ProphetFilteredSpectraResult res = this.load(runSearchAnalysisId);
			if(res != null)
				list.add(res);
		}
		
		return list;
	}
	
	@Override
	public ProphetFilteredSpectraResult load(int runSearchAnalysisId) {
		
		return (ProphetFilteredSpectraResult) queryForObject(namespace+".select", runSearchAnalysisId);
	}

	@Override
	public void save(ProphetFilteredSpectraResult result) {
		
		int percSpectraResultId = 0;
		try {
			percSpectraResultId = saveAndReturnId(namespace+".insert",result);
		
			for(ProphetBinnedSpectraResult binnedResult: result.getBinnedResults()) {
				binnedResult.setProphetFilteredSpectraId(percSpectraResultId);
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
