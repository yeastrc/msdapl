/**
 * AnalysisPeptideTerminiStatsDAO.java
 * @author Vagisha Sharma
 * Mar 1, 2011
 */
package org.yeastrc.ms.dao.analysis;

import org.yeastrc.ms.domain.analysis.impl.PeptideTerminalAAResult;

/**
 * 
 */
public interface PeptideTerminiStatsDAO {

	
	public PeptideTerminalAAResult load(int searchAnalysisId);
	
	public void save(PeptideTerminalAAResult result);
	
	public void delete(int searchAnalysisId);
}
