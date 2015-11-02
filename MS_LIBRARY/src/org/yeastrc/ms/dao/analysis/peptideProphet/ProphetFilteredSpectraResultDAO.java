/**
 * ProphetFilteredSpectraResultDAO.java
 * @author Vagisha Sharma
 * Aug 7, 2011
 */
package org.yeastrc.ms.dao.analysis.peptideProphet;

import java.util.List;

import org.yeastrc.ms.domain.analysis.peptideProphet.impl.ProphetFilteredSpectraResult;

/**
 * 
 */
public interface ProphetFilteredSpectraResultDAO {

	public double getPopulationAvgFilteredPercent();
	
	public double getPopulationStdDevFilteredPercent();
	
	public double getPopulationMin();
	
	public double getPopulationMax();
	
	
	public ProphetFilteredSpectraResult load(int runSearchAnalysisId);
	
	public List<ProphetFilteredSpectraResult> loadForAnalysis(int searchAnalysisId);
	
	public void save(ProphetFilteredSpectraResult result);
	
	public void delete(int runSearchAnalysisId);
}
