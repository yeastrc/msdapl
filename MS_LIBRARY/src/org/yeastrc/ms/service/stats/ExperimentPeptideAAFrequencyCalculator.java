/**
 * ExperimentPeptideAAFrequencyCalculator.java
 * @author Vagisha Sharma
 * Mar 7, 2011
 */
package org.yeastrc.ms.service.stats;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.yeastrc.ms.dao.DAOFactory;
import org.yeastrc.ms.dao.analysis.MsSearchAnalysisDAO;
import org.yeastrc.ms.dao.analysis.PeptideTerminiStatsDAO;
import org.yeastrc.ms.dao.analysis.percolator.PercolatorPeptideResultDAO;
import org.yeastrc.ms.domain.analysis.MsSearchAnalysis;
import org.yeastrc.ms.domain.analysis.impl.PeptideTerminalAAResult;
import org.yeastrc.ms.domain.general.EnzymeFactory;
import org.yeastrc.ms.domain.general.MsEnzyme;
import org.yeastrc.ms.domain.general.MsExperiment;
import org.yeastrc.ms.domain.search.MsSearch;
import org.yeastrc.ms.domain.search.Program;
import org.yeastrc.ms.service.percolator.stats.termini.PeptideAAFrequencyCalculator;
import org.yeastrc.ms.service.percolator.stats.termini.PsmAAFrequencyCalculator;

/**
 * 
 */
public class ExperimentPeptideAAFrequencyCalculator {

	
	private static final Logger log = Logger.getLogger(ExperimentPeptideAAFrequencyCalculator.class);
	
	public static void main(String[] args) {
		
		
		int minExperiment = 0;
		int maxExperiment = 0;
		
		try {
			minExperiment = Integer.parseInt(args[0]);
			maxExperiment = Integer.parseInt(args[1]);
		}
		catch (Exception e) {
			log.error("\nUsage: ExperimentPeptideAAFrequencyCalculator <min_experiment_id> <max_experiment_id>");
			System.exit(-1);
		}
		
		
		for(int i = minExperiment; i <= maxExperiment; i++) {
			
			// first make sure the experiment exists
			MsExperiment experiment = DAOFactory.instance().getMsExperimentDAO().loadExperiment(i);
			if(experiment == null)
				continue;
			
			log.info("ExperimentID: "+i);
			// load the analysisIds for the experiment
			List<Integer> searchIds = DAOFactory.instance().getMsSearchDAO().getSearchIdsForExperiment(i);
			if(searchIds == null || searchIds.size() == 0)
				continue;
			
			for(int searchId: searchIds) {
				log.info("\tSearchID: "+searchId);
				
				// load the search and get the enzyme(s) used for digestion
				MsSearch search = DAOFactory.instance().getMsSearchDAO().loadSearch(searchId);
				List<MsEnzyme> enzymeList = search.getEnzymeList();
				if(enzymeList == null) {
					enzymeList = new ArrayList<MsEnzyme>(1);
					enzymeList.add(EnzymeFactory.getTrypsin());
				}
				else if(enzymeList.size() == 0) {
					enzymeList.add(EnzymeFactory.getTrypsin());
				}
				if(enzymeList.size() > 1) {
					log.error("Don't know how to handle multiple enzymes / search");
					continue;
				}
				MsEnzyme enzyme = enzymeList.get(0);
				double qvalCutoff = 0.01;
				
				MsSearchAnalysisDAO aDao = DAOFactory.instance().getMsSearchAnalysisDAO();
				List<Integer> analysisIds = aDao.getAnalysisIdsForSearch(searchId);
				
				for(int analysisId: analysisIds) {
					
					// First load the analysis
					MsSearchAnalysis analysis = aDao.load(analysisId);
					if(analysis == null)
						continue;
					
					log.info("\t\tAnalysisID: "+analysisId);
					if(analysis.getAnalysisProgram() != Program.PERCOLATOR) {
						log.error("Cannot calculate for analysis program: "+analysis.getAnalysisProgram().displayName());
						continue;
					}
					
					// Does this Percolator run have Peptide-level results?
					boolean havePeptideScores = false;
					PercolatorPeptideResultDAO peptResDao = DAOFactory.instance().getPercolatorPeptideResultDAO();
			        if(peptResDao.peptideCountForAnalysis(analysis.getId()) > 0)
			        	havePeptideScores = true;
			        
			       
			        // calculate
			        PeptideTerminalAAResult result = null;
			        if(havePeptideScores) {
			        	log.info("Looking at peptide-level results");
			        	PeptideAAFrequencyCalculator calculator = new PeptideAAFrequencyCalculator(enzyme, qvalCutoff);
			        	result = calculator.calculateForAnalysis(analysisId);
			        }
			        else {
			        	log.info("Looking at PSM-level results");
			        	PsmAAFrequencyCalculator calculator = new PsmAAFrequencyCalculator(enzyme, qvalCutoff);
			        	calculator.setUniquePeptides(true); // only look at unique peptides
						result = calculator.calculateForAnalysis(analysisId);
			        }
			        
			        // save the result
			        PeptideTerminiStatsDAO tdao = DAOFactory.instance().getPeptideTerminiStatsDAO();
			        try {
			        	tdao.save(result);
			        }
			        catch(Exception e) {
			        	log.error("Error saving results for analysisID: "+analysisId);
			        }
				}
			}
		}
	}
}
