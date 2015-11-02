package org.yeastrc.ms.service.pepxml.stats;
import java.util.List;

import org.apache.log4j.Logger;
import org.yeastrc.ms.dao.DAOFactory;
import org.yeastrc.ms.dao.analysis.peptideProphet.PeptideProphetRocDAO;
import org.yeastrc.ms.dao.analysis.peptideProphet.ProphetFilteredSpectraResultDAO;
import org.yeastrc.ms.domain.analysis.peptideProphet.PeptideProphetROC;
import org.yeastrc.ms.domain.analysis.peptideProphet.impl.ProphetFilteredSpectraResult;

/**
 * ProphetFilteredSpectraStatsSaver.java
 * @author Vagisha Sharma
 * Oct 3, 2010
 */

/**
 * 
 */
public class ProphetFilteredSpectraStatsSaver {

	
	private static final Logger log = Logger.getLogger(ProphetFilteredSpectraStatsSaver.class);
	private static ProphetFilteredSpectraStatsSaver instance;
	
	private ProphetFilteredSpectraStatsSaver() {}
	
	public static synchronized ProphetFilteredSpectraStatsSaver getInstance() {
		if(instance == null) {
			instance = new ProphetFilteredSpectraStatsSaver();
		}
		return instance;
	}
	
	public void save(int searchAnalysisId, double errorRate) {

		
		DAOFactory fact = DAOFactory.instance();
		ProphetFilteredSpectraResultDAO filtPsmDao = fact.getProphetFilteredSpectraResultDAO();
		
		PeptideProphetRocDAO rocDao = DAOFactory.instance().getPeptideProphetRocDAO();
		PeptideProphetROC roc = rocDao.loadRoc(searchAnalysisId);
		double probability = roc.getMinProbabilityForError(0.01);
		log.info("Probability for error rate of 0.01 is: "+probability);
		
		log.info("Saving results for searchAnalysisID: "+searchAnalysisId);
		ProphetFilteredSpectraDistributionCalculator calc = new ProphetFilteredSpectraDistributionCalculator(searchAnalysisId, probability);
		calc.calculate();
		List<ProphetFilteredSpectraResult> filteredResults = calc.getFilteredResults();
		if(filteredResults == null || filteredResults.size() == 0) {
			log.warn("No results for searchAnalysisID: "+searchAnalysisId+". Skipping....");
		}
		for(ProphetFilteredSpectraResult res: filteredResults) {
			log.info("\tSaving for: "+res.getRunSearchAnalysisId());
			filtPsmDao.save(res);
		}
	}
}
