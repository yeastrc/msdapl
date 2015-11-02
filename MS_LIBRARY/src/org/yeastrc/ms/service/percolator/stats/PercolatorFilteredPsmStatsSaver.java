package org.yeastrc.ms.service.percolator.stats;
import java.util.List;

import org.apache.log4j.Logger;
import org.yeastrc.ms.dao.DAOFactory;
import org.yeastrc.ms.dao.analysis.percolator.PercolatorFilteredPsmResultDAO;
import org.yeastrc.ms.domain.analysis.percolator.impl.PercolatorFilteredPsmResult;

/**
 * PercolatorFilteredStatsSaver.java
 * @author Vagisha Sharma
 * Oct 3, 2010
 */

/**
 * 
 */
public class PercolatorFilteredPsmStatsSaver {

	
	private static final Logger log = Logger.getLogger(PercolatorFilteredPsmStatsSaver.class);
	private static PercolatorFilteredPsmStatsSaver instance;
	
	private PercolatorFilteredPsmStatsSaver() {}
	
	public static synchronized PercolatorFilteredPsmStatsSaver getInstance() {
		if(instance == null) {
			instance = new PercolatorFilteredPsmStatsSaver();
		}
		return instance;
	}
	
	public void save(int searchAnalysisId, double qvalue) {

		DAOFactory fact = DAOFactory.instance();
		PercolatorFilteredPsmResultDAO filtPsmDao = fact.getPrecolatorFilteredPsmResultDAO();
		
		log.info("Saving results for searchAnalysisID: "+searchAnalysisId);
		PercolatorFilteredPsmDistributionCalculator calc = new PercolatorFilteredPsmDistributionCalculator(searchAnalysisId, qvalue);
		calc.calculate();
		List<PercolatorFilteredPsmResult> filteredResults = calc.getFilteredResults();
		if(filteredResults == null || filteredResults.size() == 0) {
			log.warn("No results for searchAnalysisID: "+searchAnalysisId+". Skipping....");
		}
		for(PercolatorFilteredPsmResult res: filteredResults) {
			log.info("\tSaving for: "+res.getRunSearchAnalysisId());
			filtPsmDao.save(res);
		}
	}
}
