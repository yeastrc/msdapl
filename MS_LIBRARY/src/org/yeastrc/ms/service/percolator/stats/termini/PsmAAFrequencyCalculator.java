/**
 * PeptideAaFrequencyCalculator.java
 * @author Vagisha Sharma
 * Feb 25, 2011
 */
package org.yeastrc.ms.service.percolator.stats.termini;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.yeastrc.ms.dao.DAOFactory;
import org.yeastrc.ms.dao.analysis.MsRunSearchAnalysisDAO;
import org.yeastrc.ms.dao.analysis.percolator.PercolatorResultDAO;
import org.yeastrc.ms.domain.analysis.impl.PeptideTerminalAAResult;
import org.yeastrc.ms.domain.analysis.impl.PeptideTerminalAAResultBuilder;
import org.yeastrc.ms.domain.analysis.percolator.PercolatorResult;
import org.yeastrc.ms.domain.analysis.percolator.PercolatorResultFilterCriteria;
import org.yeastrc.ms.domain.general.EnzymeFactory;
import org.yeastrc.ms.domain.general.EnzymeRule;
import org.yeastrc.ms.domain.general.MsEnzyme;
import org.yeastrc.ms.domain.search.MsSearchResultPeptide;
import org.yeastrc.ms.util.TimeUtils;

/**
 * 
 */
public class PsmAAFrequencyCalculator {

	private double scoreCutoff;
	private MsEnzyme enzyme;
	private EnzymeRule rule;
	
	private boolean lookAtUniqPeptides = false;
	private Set<String> peptides;
	
	private static final Logger log = Logger.getLogger(PsmAAFrequencyCalculator.class);
	
	public PsmAAFrequencyCalculator (MsEnzyme enzyme, double minQvalue) {
		
		this.enzyme = enzyme;
		// If we are not given an enzyme; assume it is Trypsin.
		if(this.enzyme == null) {
	        this.enzyme = EnzymeFactory.getTrypsin();
		}
		if(this.enzyme != null)
			this.rule = new EnzymeRule(this.enzyme);
		
		this.scoreCutoff = minQvalue;
	}
	
	public void setUniquePeptides(boolean uniqPeptides) {
		this.lookAtUniqPeptides = uniqPeptides;
	}
	
	
	public PeptideTerminalAAResult calculateForAnalysis(int analysisId) {
		
		DAOFactory daoFactory = DAOFactory.instance();
		
		MsRunSearchAnalysisDAO rsaDao = daoFactory.getMsRunSearchAnalysisDAO();
		List<Integer> runSearchAnalysisIds = rsaDao.getRunSearchAnalysisIdsForAnalysis(analysisId);
		
		PeptideTerminalAAResult result = calculateForRunSearchAnalysisIds(analysisId, runSearchAnalysisIds);
		result.setEnzyme(enzyme);
		return result;
		
	}

	public PeptideTerminalAAResult calculateForRunSearchAnalysisIds(int analysisId, List<Integer> runSearchAnalysisIds) {
		
		
		long s = System.currentTimeMillis();
		
		PeptideTerminalAAResult result = new PeptideTerminalAAResult();
		result.setAnalysisId(analysisId);
		result.setScoreCutoff(this.scoreCutoff);
		result.setScoreType("PERC_PSM_QVAL");
		result.setEnzyme(this.enzyme);
		
		for(Integer rsaId: runSearchAnalysisIds) {
			
			PeptideTerminalAAResult rsaResult = calculateForRunSearchAnalysis(analysisId, rsaId);
			result.combineWith(rsaResult);
		}
		
		long e = System.currentTimeMillis();
		
		log.info("Total time to get results "+TimeUtils.timeElapsedSeconds(s, e)+" seconds");
		log.info("Total filtered results: "+result.getTotalResultCount());
		
		result.setEnzyme(enzyme);
		return result;
	}
	
	
	public PeptideTerminalAAResult calculateForRunSearchAnalysis(int analysisId, int runSearchAnalysisId) {
		
		
		if(lookAtUniqPeptides) {
			if(this.peptides == null) {
				peptides = new HashSet<String>();
			}
		}
		
		PercolatorResultDAO percDao = DAOFactory.instance().getPercolatorResultDAO();
		
		PercolatorResultFilterCriteria filterCriteria = new PercolatorResultFilterCriteria();
		filterCriteria.setMaxQValue(scoreCutoff);
		
		long s = System.currentTimeMillis();
		
		PeptideTerminalAAResultBuilder builder = new PeptideTerminalAAResultBuilder(analysisId, enzyme);
		builder.setScoreCutoff(this.scoreCutoff);
		builder.setScoreType("PERC_PSM_QVAL");
		
		int totalResults = 0;
			
		List<Integer> percResultIds = percDao.loadIdsForRunSearchAnalysis(runSearchAnalysisId, filterCriteria, null);

		log.info("Found "+percResultIds.size()+" Percolator results at qvalue <= "+scoreCutoff+
				" for runSearchAnalysisID "+runSearchAnalysisId);

		
		for(Integer percResultId: percResultIds) {

			PercolatorResult pres = percDao.loadForPercolatorResultId(percResultId);
			MsSearchResultPeptide peptide = pres.getResultPeptide();
			String seq = peptide.getPeptideSequence();

			if(this.lookAtUniqPeptides) {
				String fullseq = peptide.getPreResidue()+"."+seq+"."+peptide.getPostResidue();
				if(peptides.contains(fullseq))
					continue;
				else
					peptides.add(fullseq);
			}
			
			totalResults++;
			
			char ntermMinusOne = peptide.getPreResidue(); // nterm - 1 residue
			builder.addNtermMinusOneCount(ntermMinusOne);

			char nterm = seq.charAt(0); // nterm residue
			builder.addNtermCount(nterm);

			char cterm = seq.charAt(seq.length() - 1); // cterm residue
			builder.addCtermCount(cterm);

			char ctermPlusOne = peptide.getPostResidue(); // cterm + 1 residue
			builder.addCtermPlusOneCount(ctermPlusOne);

			int numEnzTerm = 0;
			if(this.rule != null)
				numEnzTerm = rule.getNumEnzymaticTermini(seq, ntermMinusOne, ctermPlusOne);
			builder.addEnzymaticTerminiCount(numEnzTerm);
		}
			
		long e = System.currentTimeMillis();
		
		log.info("Time to get results for runSearchAnalysisId "+runSearchAnalysisId+" "+TimeUtils.timeElapsedSeconds(s, e)+" seconds");
		log.info("# filtered results: "+totalResults);
		
		builder.setTotalResultCount(totalResults);
		
		return builder.getResult();
	}
	
}
