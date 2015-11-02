/**
 * PsmRetTimeDistributionGetter.java
 * @author Vagisha Sharma
 * Oct 3, 2010
 */
package org.yeastrc.experiment.stats;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.log4j.Logger;
import org.yeastrc.ms.dao.DAOFactory;
import org.yeastrc.ms.dao.analysis.MsRunSearchAnalysisDAO;
import org.yeastrc.ms.dao.analysis.peptideProphet.PeptideProphetRocDAO;
import org.yeastrc.ms.dao.analysis.peptideProphet.ProphetFilteredPsmResultDAO;
import org.yeastrc.ms.dao.analysis.percolator.PercolatorFilteredPsmResultDAO;
import org.yeastrc.ms.domain.analysis.MsSearchAnalysis;
import org.yeastrc.ms.domain.analysis.peptideProphet.PeptideProphetROC;
import org.yeastrc.ms.domain.analysis.peptideProphet.impl.ProphetBinnedPsmResult;
import org.yeastrc.ms.domain.analysis.peptideProphet.impl.ProphetFilteredPsmResult;
import org.yeastrc.ms.domain.analysis.percolator.impl.PercolatorBinnedPsmResult;
import org.yeastrc.ms.domain.analysis.percolator.impl.PercolatorFilteredPsmResult;
import org.yeastrc.ms.domain.search.Program;
import org.yeastrc.ms.service.pepxml.stats.ProphetFilteredPsmDistributionCalculator;
import org.yeastrc.ms.service.percolator.stats.PercolatorFilteredPsmDistributionCalculator;
import org.yeastrc.www.util.RoundingUtils;

/**
 * 
 */
public class PsmRetTimeDistributionGetter {

	
	private int analysisId;
	private double scoreCutoff;

	private static final Logger log = Logger.getLogger(PsmRetTimeDistributionGetter.class);
	
	public PsmRetTimeDistributionGetter(int analysisId, double scoreCutoff) {
        this.analysisId = analysisId;
        this.scoreCutoff = scoreCutoff;
    }
	
	public PsmRetTimeDistribution getDistribution() {
		
		
		MsSearchAnalysis analysis = DAOFactory.instance().getMsSearchAnalysisDAO().load(analysisId);
		if(analysis.getAnalysisProgram() == Program.PERCOLATOR) {
			return getPercolatorResults();
		}
		else if(analysis.getAnalysisProgram() == Program.PEPTIDE_PROPHET) {
			return getPeptideProphetResults();
		}
		
		return null;
	}

	private PsmRetTimeDistribution getPercolatorResults() {
		
		List<PercolatorFilteredPsmResult> filteredResults = null;
		if(scoreCutoff == 0.01) {
			// Look in the database first for pre-calculated results
			PercolatorFilteredPsmResultDAO dao = DAOFactory.instance().getPrecolatorFilteredPsmResultDAO();
			filteredResults = dao.loadForAnalysis(analysisId);
		}
		
		if(filteredResults == null || filteredResults.size() == 0) {
			
			// Results were not found in the database; calculate now
			PercolatorFilteredPsmDistributionCalculator calc = new PercolatorFilteredPsmDistributionCalculator(analysisId, scoreCutoff);
			calc.calculate();
			filteredResults = calc.getFilteredResults();
		}
		
		if(filteredResults == null || filteredResults.size() == 0) {
			log.error("No results for searchAnalysisID: "+analysisId);
			return null;
		}
		
		PercolatorFilteredPsmResultDAO statsDao = DAOFactory.instance().getPrecolatorFilteredPsmResultDAO();
		double populationMax = RoundingUtils.getInstance().roundOne(statsDao.getPopulationMax());
		double populationMin = RoundingUtils.getInstance().roundOne(statsDao.getPopulationMin());
		double populationMean = RoundingUtils.getInstance().roundOne(statsDao.getPopulationAvgFilteredPercent());
		double populationStddev = RoundingUtils.getInstance().roundOne(statsDao.getPopulationStdDevFilteredPercent());
		
		int[] allPsmCounts = null;
	    int[] filteredPsmCounts = null;
	    
	    int maxPsmCount = 0;
	    double maxRt = 0;
	    
	    List<FileStats> fileStats = new ArrayList<FileStats>(filteredResults.size());
	    MsRunSearchAnalysisDAO rsaDao = DAOFactory.instance().getMsRunSearchAnalysisDAO();
	    
	    maxRt = getMaxRtForPercolatorResults(filteredResults.get(0).getBinnedResults());
	    int binIncr = getBinIncrement(maxRt);
	    int numBins = (int)Math.ceil(maxRt / binIncr);
	    
		for(PercolatorFilteredPsmResult res: filteredResults) {
			
			int runSearchAnalysisId = res.getRunSearchAnalysisId();
			String filename = rsaDao.loadFilenameForRunSearchAnalysis(runSearchAnalysisId);
			FileStats stats = new FileStats(res.getRunSearchAnalysisId(), filename);
			stats.setGoodCount(res.getFiltered());
			stats.setTotalCount(res.getTotal());
			if(scoreCutoff == 0.01) {
				stats.setPopulationMean(populationMean);
				stats.setPopulationStandardDeviation(populationStddev);
				stats.setPopulationMin(populationMin);
				stats.setPopulationMax(populationMax);
			}
			
			fileStats.add(stats);
			
			List<PercolatorBinnedPsmResult> binnedResults = res.getBinnedResults();
			Collections.sort(binnedResults, new Comparator<PercolatorBinnedPsmResult>() {
				@Override
				public int compare(PercolatorBinnedPsmResult o1,PercolatorBinnedPsmResult o2) {
					return Double.valueOf(o1.getBinStart()).compareTo(o2.getBinStart());
				}
			});
			
			if(allPsmCounts == null)
				allPsmCounts = new int[numBins];
			if(filteredPsmCounts == null)
				filteredPsmCounts = new int[numBins];
			
			maxRt = Math.max(maxRt, binnedResults.get(binnedResults.size() - 1).getBinEnd());
			
			int idx = 0;
			for(int j = 0; j < binnedResults.size(); j++) {
				PercolatorBinnedPsmResult binned = binnedResults.get(j);
				allPsmCounts[idx] += binned.getTotal();
				filteredPsmCounts[idx] += binned.getFiltered();
				maxPsmCount = Math.max(allPsmCounts[idx], maxPsmCount);
				if(j > 0 && j % binIncr == 0)
					idx++;
			}
		}
		
		PsmRetTimeDistribution distribution = new PsmRetTimeDistribution(Program.PERCOLATOR);
		distribution.setScoreCutoff(this.scoreCutoff);
		distribution.setNumBins(allPsmCounts.length);
		distribution.setMaxRT(maxRt);
		distribution.setMaxPsmCount(maxPsmCount);
		distribution.setFilteredPsmCounts(filteredPsmCounts);
		distribution.setAllPsmCounts(allPsmCounts);
		distribution.setBinSize(binIncr);
		distribution.setFileStatsList(fileStats);
		
		return distribution;
	}
	
	private PsmRetTimeDistribution getPeptideProphetResults() {
		
		List<ProphetFilteredPsmResult> filteredResults = null;
		if(scoreCutoff == QCStatsGetter.PEPPROPHET_ERR_RATE_DEFAULT) {
			// Look in the database first for pre-calculated results
			ProphetFilteredPsmResultDAO dao = DAOFactory.instance().getProphetFilteredPsmResultDAO();
			filteredResults = dao.loadForAnalysis(analysisId);
		}
		
		if(filteredResults == null || filteredResults.size() == 0) {
			
			// Results were not found in the database; calculate now
			
			PeptideProphetRocDAO rocDao = DAOFactory.instance().getPeptideProphetRocDAO();
			PeptideProphetROC roc = rocDao.loadRoc(analysisId);
			double probability = roc.getMinProbabilityForError(scoreCutoff);
			log.info("Probability for error rate of "+scoreCutoff+" is: "+probability);
			
			ProphetFilteredPsmDistributionCalculator calc = new ProphetFilteredPsmDistributionCalculator(analysisId, probability);
			calc.calculate();
			filteredResults = calc.getFilteredResults();
		}
		
		if(filteredResults == null || filteredResults.size() == 0) {
			log.error("No results for searchAnalysisID: "+analysisId);
			return null;
		}
		
		ProphetFilteredPsmResultDAO statsDao = DAOFactory.instance().getProphetFilteredPsmResultDAO();
		double populationMax = RoundingUtils.getInstance().roundOne(statsDao.getPopulationMax());
		double populationMin = RoundingUtils.getInstance().roundOne(statsDao.getPopulationMin());
		double populationMean = RoundingUtils.getInstance().roundOne(statsDao.getPopulationAvgFilteredPercent());
		double populationStddev = RoundingUtils.getInstance().roundOne(statsDao.getPopulationStdDevFilteredPercent());
		
		int[] allPsmCounts = null;
	    int[] filteredPsmCounts = null;
	    
	    int maxPsmCount = 0;
	    double maxRt = 0;
	    
	    List<FileStats> fileStats = new ArrayList<FileStats>(filteredResults.size());
	    MsRunSearchAnalysisDAO rsaDao = DAOFactory.instance().getMsRunSearchAnalysisDAO();
	    
	    maxRt = getMaxRtForProphetResults(filteredResults.get(0).getBinnedResults());
	    int binIncr = getBinIncrement(maxRt);
	    int numBins = (int)Math.ceil(maxRt / binIncr);
	    
	    double actualScoreCutoff = -1.0; // actual probability cutoff used (that corresponds to the given error rate)
	    
		for(ProphetFilteredPsmResult res: filteredResults) {
			
			actualScoreCutoff = res.getProbability();
			
			int runSearchAnalysisId = res.getRunSearchAnalysisId();
			String filename = rsaDao.loadFilenameForRunSearchAnalysis(runSearchAnalysisId);
			FileStats stats = new FileStats(res.getRunSearchAnalysisId(), filename);
			stats.setGoodCount(res.getFiltered());
			stats.setTotalCount(res.getTotal());
			if(scoreCutoff == 0.01) {
				stats.setPopulationMean(populationMean);
				stats.setPopulationStandardDeviation(populationStddev);
				stats.setPopulationMin(populationMin);
				stats.setPopulationMax(populationMax);
			}
			
			fileStats.add(stats);
			
			List<ProphetBinnedPsmResult> binnedResults = res.getBinnedResults();
			Collections.sort(binnedResults, new Comparator<ProphetBinnedPsmResult>() {
				@Override
				public int compare(ProphetBinnedPsmResult o1,ProphetBinnedPsmResult o2) {
					return Double.valueOf(o1.getBinStart()).compareTo(o2.getBinStart());
				}
			});
			
			if(allPsmCounts == null)
				allPsmCounts = new int[numBins];
			if(filteredPsmCounts == null)
				filteredPsmCounts = new int[numBins];
			
			maxRt = Math.max(maxRt, binnedResults.get(binnedResults.size() - 1).getBinEnd());
			
			int idx = 0;
			for(int j = 0; j < binnedResults.size(); j++) {
				ProphetBinnedPsmResult binned = binnedResults.get(j);
				allPsmCounts[idx] += binned.getTotal();
				filteredPsmCounts[idx] += binned.getFiltered();
				maxPsmCount = Math.max(allPsmCounts[idx], maxPsmCount);
				if(j > 0 && j % binIncr == 0)
					idx++;
			}
		}
		
		PsmRetTimeDistribution distribution = new PsmRetTimeDistribution(Program.PEPTIDE_PROPHET);
		distribution.setScoreCutoff(actualScoreCutoff);
		distribution.setNumBins(allPsmCounts.length);
		distribution.setMaxRT(maxRt);
		distribution.setMaxPsmCount(maxPsmCount);
		distribution.setFilteredPsmCounts(filteredPsmCounts);
		distribution.setAllPsmCounts(allPsmCounts);
		distribution.setBinSize(binIncr);
		distribution.setFileStatsList(fileStats);
		
		return distribution;
	}

	private int getBinIncrement(double maxRt) {
		
		// PercolatorFilteredPsmDistributionCalculator bins results in 1 unit RT bins.
		// We will re-bin them if the number of bins is too large
		int numBins = (int)Math.ceil(maxRt);
		// get a number closest to 50
		int diff = Math.abs(numBins - 50);
		
		int incr = 1;
		for(int i = 2; ; i++) {
			int nb = numBins / i;
			int d = Math.abs(nb - 50);
			if(d < diff) {
				diff = d;
				incr = i;
			}
			else
				break;
		}
		
		return incr;
	}

	private double getMaxRtForPercolatorResults(List<PercolatorBinnedPsmResult> binnedResults) {
		
		double max = 0;
		for(PercolatorBinnedPsmResult res: binnedResults) {
			max = Math.max(max, res.getBinEnd());
		}
		return max;
	}
	
	private double getMaxRtForProphetResults(List<ProphetBinnedPsmResult> binnedResults) {
		
		double max = 0;
		for(ProphetBinnedPsmResult res: binnedResults) {
			max = Math.max(max, res.getBinEnd());
		}
		return max;
	}
}
