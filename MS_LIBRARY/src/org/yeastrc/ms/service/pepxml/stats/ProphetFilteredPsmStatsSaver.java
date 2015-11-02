package org.yeastrc.ms.service.pepxml.stats;
import java.util.List;

import org.apache.log4j.Logger;
import org.yeastrc.ms.dao.DAOFactory;
import org.yeastrc.ms.dao.analysis.peptideProphet.PeptideProphetRocDAO;
import org.yeastrc.ms.dao.analysis.peptideProphet.ProphetFilteredPsmResultDAO;
import org.yeastrc.ms.domain.analysis.peptideProphet.PeptideProphetROC;
import org.yeastrc.ms.domain.analysis.peptideProphet.impl.ProphetFilteredPsmResult;

/**
 * ProphetFilteredPsmStatsSaver.java
 * @author Vagisha Sharma
 * Aug 7, 2011
 */

/**
 * 
 */
public class ProphetFilteredPsmStatsSaver {

	
	private static final Logger log = Logger.getLogger(ProphetFilteredPsmStatsSaver.class);
	private static ProphetFilteredPsmStatsSaver instance;
	
	private ProphetFilteredPsmStatsSaver() {}
	
	public static synchronized ProphetFilteredPsmStatsSaver getInstance() {
		if(instance == null) {
			instance = new ProphetFilteredPsmStatsSaver();
		}
		return instance;
	}
	
	public void save(int searchAnalysisId, double errorRate) {

		DAOFactory fact = DAOFactory.instance();
		ProphetFilteredPsmResultDAO filtPsmDao = fact.getProphetFilteredPsmResultDAO();
		
		log.info("Saving results for searchAnalysisID: "+searchAnalysisId);
		
		PeptideProphetRocDAO rocDao = DAOFactory.instance().getPeptideProphetRocDAO();
		PeptideProphetROC roc = rocDao.loadRoc(searchAnalysisId);
		double probability = roc.getMinProbabilityForError(0.01);
		log.info("Probability for error rate of 0.01 is: "+probability);
		
		ProphetFilteredPsmDistributionCalculator calc = new ProphetFilteredPsmDistributionCalculator(searchAnalysisId, probability);
		calc.calculate();
		List<ProphetFilteredPsmResult> filteredResults = calc.getFilteredResults();
		if(filteredResults == null || filteredResults.size() == 0) {
			log.warn("No results for searchAnalysisID: "+searchAnalysisId+". Skipping....");
		}
		for(ProphetFilteredPsmResult res: filteredResults) {
			log.info("\tSaving for: "+res.getRunSearchAnalysisId());
			filtPsmDao.save(res);
		}
	}
}
