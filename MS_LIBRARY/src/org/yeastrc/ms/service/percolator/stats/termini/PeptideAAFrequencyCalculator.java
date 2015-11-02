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
import org.yeastrc.ms.dao.analysis.percolator.PercolatorPeptideResultDAO;
import org.yeastrc.ms.domain.analysis.impl.PeptideTerminalAAResult;
import org.yeastrc.ms.domain.analysis.impl.PeptideTerminalAAResultBuilder;
import org.yeastrc.ms.domain.analysis.percolator.PercolatorPeptideResult;
import org.yeastrc.ms.domain.analysis.percolator.PercolatorResultFilterCriteria;
import org.yeastrc.ms.domain.general.EnzymeFactory;
import org.yeastrc.ms.domain.general.EnzymeRule;
import org.yeastrc.ms.domain.general.MsEnzyme;
import org.yeastrc.ms.domain.search.MsSearchResultPeptide;
import org.yeastrc.ms.util.TimeUtils;

/**
 * 
 */
public class PeptideAAFrequencyCalculator {

	private double scoreCutoff;
	private MsEnzyme enzyme;
	private EnzymeRule rule;
	
	
	private static final Logger log = Logger.getLogger(PeptideAAFrequencyCalculator.class);
	
	public PeptideAAFrequencyCalculator (MsEnzyme enzyme, double minQvalue) {
		
		this.enzyme = enzyme;
		
		// If we are not given an enzyme; assume it is Trypsin.
		if(this.enzyme == null) {
	        this.enzyme = EnzymeFactory.getTrypsin();
		}
		if(this.enzyme != null)
			this.rule = new EnzymeRule(this.enzyme);
		
		this.scoreCutoff = minQvalue;
		
	}
	
	public PeptideTerminalAAResult calculateForAnalysis(int analysisId) {
		
		PercolatorPeptideResultDAO percDao = DAOFactory.instance().getPercolatorPeptideResultDAO();

		PercolatorResultFilterCriteria filterCriteria = new PercolatorResultFilterCriteria();
		filterCriteria.setMaxQValue(scoreCutoff);
		
		long s = System.currentTimeMillis();
		
		PeptideTerminalAAResultBuilder builder = new PeptideTerminalAAResultBuilder(analysisId, this.enzyme);
		builder.setScoreCutoff(this.scoreCutoff);
		builder.setScoreType("PERC_PEPTIDE_QVAL");
		
		int totalResults = 0;
			
		List<Integer> percPeptideResultIds = percDao.loadIdsForSearchAnalysis(analysisId, filterCriteria, null);

		log.info("Found "+percPeptideResultIds.size()+" Percolator (peptide) results at qvalue <= "+scoreCutoff+
				" for analysisID "+analysisId);

		
		for(Integer percPeptideResultId: percPeptideResultIds) {

			PercolatorPeptideResult pres = percDao.load(percPeptideResultId);
			MsSearchResultPeptide peptide = pres.getResultPeptide();
			String seq = peptide.getPeptideSequence();
			
			
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
			
			totalResults++;
		}
			
		long e = System.currentTimeMillis();
		
		log.info("Time to get results for analysisId "+analysisId+" "+TimeUtils.timeElapsedSeconds(s, e)+" seconds");
		log.info("# filtered peptide sequences: "+totalResults);
		
		builder.setTotalResultCount(totalResults);
		
		return builder.getResult();
	}
}
