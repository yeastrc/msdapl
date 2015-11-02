/**
 * ProphetFilteredPsmResultDAO.java
 * @author Vagisha Sharma
 * Aug 7, 2011
 */
package org.yeastrc.ms.dao.analysis.peptideProphet;

import java.util.List;

import org.yeastrc.ms.domain.analysis.peptideProphet.impl.ProphetFilteredPsmResult;

/**
 * 
 */
public interface ProphetFilteredPsmResultDAO {

	public double getPopulationAvgFilteredPercent();
	
	public double getPopulationStdDevFilteredPercent();
	
	public double getPopulationMin();
	
	public double getPopulationMax();
	
	
	public ProphetFilteredPsmResult load(int runSearchAnalysisId);
	
	public List<ProphetFilteredPsmResult> loadForAnalysis(int searchAnalysisId);
	
	public void save(ProphetFilteredPsmResult result);
	
	public void delete(int runSearchAnalysisId);
}
