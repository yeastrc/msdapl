/**
 * PercolatorFilteredSpectraResultsDAOImpl.java
 * @author Vagisha Sharma
 * Oct 3, 2010
 */
package org.yeastrc.ms.dao.analysis.ibatis;

import org.yeastrc.ms.dao.analysis.PeptideTerminiStatsDAO;
import org.yeastrc.ms.dao.ibatis.BaseSqlMapDAO;
import org.yeastrc.ms.domain.analysis.impl.PeptideTerminalAAResult;
import org.yeastrc.ms.domain.analysis.impl.PeptideTerminalAAResultConverter;
import org.yeastrc.ms.domain.analysis.impl.PeptideTerminalAAResultDb;

import com.ibatis.sqlmap.client.SqlMapClient;

/**
 * 
 */
public class PeptideTerminiStatsDAOImpl extends BaseSqlMapDAO implements
	PeptideTerminiStatsDAO {

	private static final String namespace = "PeptideTerminiStats";
	
	public PeptideTerminiStatsDAOImpl(SqlMapClient sqlMap) {
		super(sqlMap);
	}


	@Override
	public PeptideTerminalAAResult load(int searchAnalysisId) {
		
		PeptideTerminalAAResultDb dbResult = (PeptideTerminalAAResultDb) queryForObject(namespace+".select", searchAnalysisId);
		if(dbResult == null)
			return null;
		else
			return PeptideTerminalAAResultConverter.convert(dbResult);
	}
	

	@Override
	public void save(PeptideTerminalAAResult result) {
		
		PeptideTerminalAAResultDb dbResult = PeptideTerminalAAResultConverter.convert(result);
		save(namespace+".insert", dbResult);
		
	}
	
	
	@Override
	public void delete(int searchAnalysisId) {
		delete(namespace+".delete",searchAnalysisId);
	}
}
