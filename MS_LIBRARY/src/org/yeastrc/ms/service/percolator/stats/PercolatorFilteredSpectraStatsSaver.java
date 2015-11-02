package org.yeastrc.ms.service.percolator.stats;
import java.util.List;

import org.apache.log4j.Logger;
import org.yeastrc.ms.dao.DAOFactory;
import org.yeastrc.ms.dao.analysis.percolator.PercolatorFilteredSpectraResultDAO;
import org.yeastrc.ms.domain.analysis.percolator.impl.PercolatorFilteredSpectraResult;

/**
 * PercolatorFilteredSpectraStatsSaver.java
 * @author Vagisha Sharma
 * Oct 3, 2010
 */

/**
 * 
 */
public class PercolatorFilteredSpectraStatsSaver {

	
	private static final Logger log = Logger.getLogger(PercolatorFilteredSpectraStatsSaver.class);
	private static PercolatorFilteredSpectraStatsSaver instance;
	
	private PercolatorFilteredSpectraStatsSaver() {}
	
	public static synchronized PercolatorFilteredSpectraStatsSaver getInstance() {
		if(instance == null) {
			instance = new PercolatorFilteredSpectraStatsSaver();
		}
		return instance;
	}
	
	public void save(int searchAnalysisId, double qvalue) {

		
		DAOFactory fact = DAOFactory.instance();
		PercolatorFilteredSpectraResultDAO filtPsmDao = fact.getPrecolatorFilteredSpectraResultDAO();
		
		log.info("Saving results for searchAnalysisID: "+searchAnalysisId);
		PercolatorFilteredSpectraDistributionCalculator calc = new PercolatorFilteredSpectraDistributionCalculator(searchAnalysisId, qvalue);
		calc.calculate();
		List<PercolatorFilteredSpectraResult> filteredResults = calc.getFilteredResults();
		if(filteredResults == null || filteredResults.size() == 0) {
			log.warn("No results for searchAnalysisID: "+searchAnalysisId+". Skipping....");
		}
		for(PercolatorFilteredSpectraResult res: filteredResults) {
			log.info("\tSaving for: "+res.getRunSearchAnalysisId());
			filtPsmDao.save(res);
		}
	}
}
