/**
 * PercolatorPsmFilteredResultDAO.java
 * @author Vagisha Sharma
 * Sep 29, 2010
 */
package org.yeastrc.ms.dao.analysis.percolator;

import java.util.List;

import org.yeastrc.ms.domain.analysis.percolator.impl.PercolatorFilteredPsmResult;

/**
 * 
 */
public interface PercolatorFilteredPsmResultDAO {

	public double getPopulationAvgFilteredPercent();
	
	public double getPopulationStdDevFilteredPercent();
	
	public double getPopulationMin();
	
	public double getPopulationMax();
	
	
	public PercolatorFilteredPsmResult load(int runSearchAnalysisId);
	
	public List<PercolatorFilteredPsmResult> loadForAnalysis(int searchAnalysisId);
	
	public void save(PercolatorFilteredPsmResult result);
	
	public void delete(int runSearchAnalysisId);
}
