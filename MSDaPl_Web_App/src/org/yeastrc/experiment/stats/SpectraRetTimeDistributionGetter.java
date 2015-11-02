/**
 * SpectraRetTimeDistributionGetter.java
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
import org.yeastrc.ms.dao.analysis.peptideProphet.ProphetFilteredSpectraResultDAO;
import org.yeastrc.ms.dao.analysis.percolator.PercolatorFilteredSpectraResultDAO;
import org.yeastrc.ms.domain.analysis.MsSearchAnalysis;
import org.yeastrc.ms.domain.analysis.peptideProphet.PeptideProphetROC;
import org.yeastrc.ms.domain.analysis.peptideProphet.impl.ProphetBinnedSpectraResult;
import org.yeastrc.ms.domain.analysis.peptideProphet.impl.ProphetFilteredSpectraResult;
import org.yeastrc.ms.domain.analysis.percolator.impl.PercolatorBinnedSpectraResult;
import org.yeastrc.ms.domain.analysis.percolator.impl.PercolatorFilteredSpectraResult;
import org.yeastrc.ms.domain.search.Program;
import org.yeastrc.ms.service.pepxml.stats.ProphetFilteredSpectraDistributionCalculator;
import org.yeastrc.ms.service.percolator.stats.PercolatorFilteredSpectraDistributionCalculator;
import org.yeastrc.www.util.RoundingUtils;

/**
 * 
 */
public class SpectraRetTimeDistributionGetter {

	private int analysisId;
	private double scoreCutoff;

	private static final Logger log = Logger.getLogger(SpectraRetTimeDistributionGetter.class);
	
	public SpectraRetTimeDistributionGetter(int analysisId, double scoreCutoff) {
        this.analysisId = analysisId;
        this.scoreCutoff = scoreCutoff;
    }
	
	public SpectraRetTimeDistribution getDistribution() {
		
		MsSearchAnalysis analysis = DAOFactory.instance().getMsSearchAnalysisDAO().load(analysisId);
		if(analysis.getAnalysisProgram() == Program.PERCOLATOR) {
			return getPercolatorResults();
		}
		else if(analysis.getAnalysisProgram() == Program.PEPTIDE_PROPHET) {
			return getPeptideProphetResults();
		}
		
		return null;
	}
	
	public SpectraRetTimeDistribution getPeptideProphetResults() {
		
		List<ProphetFilteredSpectraResult> filteredResults = null;
		// Look in the database first for pre-calculated results
		if(scoreCutoff == QCStatsGetter.PEPPROPHET_ERR_RATE_DEFAULT) {
			ProphetFilteredSpectraResultDAO dao = DAOFactory.instance().getProphetFilteredSpectraResultDAO();
			filteredResults = dao.loadForAnalysis(analysisId);
		}
		
		if(filteredResults == null || filteredResults.size() == 0) {
			
//			// Results were not found in the database; calculate now
//			PeptideProphetRocDAO rocDao = DAOFactory.instance().getPeptideProphetRocDAO();
//			PeptideProphetROC roc = rocDao.loadRoc(analysisId);
//			double probability = roc.getMinProbabilityForError(0.01);
//			log.info("Probability for error rate of "+scoreCutoff+" is: "+probability);
//			
//			ProphetFilteredSpectraDistributionCalculator calc = new ProphetFilteredSpectraDistributionCalculator(analysisId, probability);
//			calc.calculate();
//			filteredResults = calc.getFilteredResults();
		}
		
		if(filteredResults == null || filteredResults.size() == 0) {
			log.error("No results for searchAnalysisID: "+analysisId);
			return null;
		}
		
		ProphetFilteredSpectraResultDAO statsDao = DAOFactory.instance().getProphetFilteredSpectraResultDAO();
		double populationMax = RoundingUtils.getInstance().roundOne(statsDao.getPopulationMax());
		double populationMin = RoundingUtils.getInstance().roundOne(statsDao.getPopulationMin());
		double populationMean = RoundingUtils.getInstance().roundOne(statsDao.getPopulationAvgFilteredPercent());
		double populationStddev = RoundingUtils.getInstance().roundOne(statsDao.getPopulationStdDevFilteredPercent());
		
		
		int[] allSpectraCounts = null;
	    int[] filteredSpectraCounts = null;
	    
	    int maxPsmCount = 0;
	    double maxRt = 0;
	    
	    List<FileStats> fileStats = new ArrayList<FileStats>(filteredResults.size());
	    MsRunSearchAnalysisDAO rsaDao = DAOFactory.instance().getMsRunSearchAnalysisDAO();
	    
	    maxRt = getMaxRtForProphetResults(filteredResults.get(0).getBinnedResults());
	    int binIncr = getBinIncrement(maxRt);
	    int numBins = (int)Math.ceil(maxRt / binIncr);
	    
	    double actualScoreCutoff = -1.0; // actual probability cutoff used (that corresponds to the given error rate)
	    
		for(ProphetFilteredSpectraResult res: filteredResults) {
			
			actualScoreCutoff = res.getProbability();
			
			int runSearchAnalysisId = res.getRunSearchAnalysisId();
			String filename = rsaDao.loadFilenameForRunSearchAnalysis(runSearchAnalysisId);
			FileStats stats = new FileStats(res.getRunSearchAnalysisId(), filename);
			stats.setGoodCount(res.getFiltered());
			stats.setTotalCount(res.getTotal());
			if(scoreCutoff == QCStatsGetter.PEPPROPHET_ERR_RATE_DEFAULT) {
				stats.setPopulationMean(populationMean);
				stats.setPopulationStandardDeviation(populationStddev);
				stats.setPopulationMin(populationMin);
				stats.setPopulationMax(populationMax);
				fileStats.add(stats);
			}
			
			List<ProphetBinnedSpectraResult> binnedResults = res.getBinnedResults();
			Collections.sort(binnedResults, new Comparator<ProphetBinnedSpectraResult>() {
				@Override
				public int compare(ProphetBinnedSpectraResult o1,ProphetBinnedSpectraResult o2) {
					return Double.valueOf(o1.getBinStart()).compareTo(o2.getBinStart());
				}
			});
			
			if(allSpectraCounts == null)
				allSpectraCounts = new int[numBins];
			if(filteredSpectraCounts == null)
				filteredSpectraCounts = new int[numBins];
			
			maxRt = Math.max(maxRt, binnedResults.get(binnedResults.size() - 1).getBinEnd());
			
			int idx = 0;
			for(int j = 0; j < binnedResults.size(); j++) {
				ProphetBinnedSpectraResult binned = binnedResults.get(j);
				allSpectraCounts[idx] += binned.getTotal();
				filteredSpectraCounts[idx] += binned.getFiltered();
				maxPsmCount = Math.max(allSpectraCounts[idx], maxPsmCount);
				if(j > 0 && j % binIncr == 0)
					idx++;
			}
		}
		
		SpectraRetTimeDistribution distribution = new SpectraRetTimeDistribution(Program.PEPTIDE_PROPHET);
		distribution.setScoreCutoff(actualScoreCutoff);
		distribution.setNumBins(allSpectraCounts.length);
		distribution.setMaxRT(maxRt);
		distribution.setMaxSpectraCount(maxPsmCount);
		distribution.setFilteredSpectraCounts(filteredSpectraCounts);
		distribution.setAllSpectraCounts(allSpectraCounts);
		distribution.setBinSize(binIncr);
		distribution.setFileStatsList(fileStats);
		
		return distribution;
		
	}

	public SpectraRetTimeDistribution getPercolatorResults() {
		
		List<PercolatorFilteredSpectraResult> filteredResults = null;
		// Look in the database first for pre-calculated results
		if(scoreCutoff == 0.01) {
			PercolatorFilteredSpectraResultDAO dao = DAOFactory.instance().getPrecolatorFilteredSpectraResultDAO();
			filteredResults = dao.loadForAnalysis(analysisId);
		}
		
		if(filteredResults == null || filteredResults.size() == 0) {
			
			// Results were not found in the database; calculate now
			PercolatorFilteredSpectraDistributionCalculator calc = new PercolatorFilteredSpectraDistributionCalculator(analysisId, scoreCutoff);
			calc.calculate();
			filteredResults = calc.getFilteredResults();
		}
		
		if(filteredResults == null || filteredResults.size() == 0) {
			log.error("No results for searchAnalysisID: "+analysisId);
			return null;
		}
		
		PercolatorFilteredSpectraResultDAO statsDao = DAOFactory.instance().getPrecolatorFilteredSpectraResultDAO();
		double populationMax = RoundingUtils.getInstance().roundOne(statsDao.getPopulationMax());
		double populationMin = RoundingUtils.getInstance().roundOne(statsDao.getPopulationMin());
		double populationMean = RoundingUtils.getInstance().roundOne(statsDao.getPopulationAvgFilteredPercent());
		double populationStddev = RoundingUtils.getInstance().roundOne(statsDao.getPopulationStdDevFilteredPercent());
		
		
		int[] allSpectraCounts = null;
	    int[] filteredSpectraCounts = null;
	    
	    int maxPsmCount = 0;
	    double maxRt = 0;
	    
	    List<FileStats> fileStats = new ArrayList<FileStats>(filteredResults.size());
	    MsRunSearchAnalysisDAO rsaDao = DAOFactory.instance().getMsRunSearchAnalysisDAO();
	    
	    maxRt = getMaxRtForPercolatorResults(filteredResults.get(0).getBinnedResults());
	    int binIncr = getBinIncrement(maxRt);
	    int numBins = (int)Math.ceil(maxRt / binIncr);
	    
		for(PercolatorFilteredSpectraResult res: filteredResults) {
			
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
				fileStats.add(stats);
			}
			
			List<PercolatorBinnedSpectraResult> binnedResults = res.getBinnedResults();
			Collections.sort(binnedResults, new Comparator<PercolatorBinnedSpectraResult>() {
				@Override
				public int compare(PercolatorBinnedSpectraResult o1,PercolatorBinnedSpectraResult o2) {
					return Double.valueOf(o1.getBinStart()).compareTo(o2.getBinStart());
				}
			});
			
			if(allSpectraCounts == null)
				allSpectraCounts = new int[numBins];
			if(filteredSpectraCounts == null)
				filteredSpectraCounts = new int[numBins];
			
			maxRt = Math.max(maxRt, binnedResults.get(binnedResults.size() - 1).getBinEnd());
			
			int idx = 0;
			for(int j = 0; j < binnedResults.size(); j++) {
				PercolatorBinnedSpectraResult binned = binnedResults.get(j);
				allSpectraCounts[idx] += binned.getTotal();
				filteredSpectraCounts[idx] += binned.getFiltered();
				maxPsmCount = Math.max(allSpectraCounts[idx], maxPsmCount);
				if(j > 0 && j % binIncr == 0)
					idx++;
			}
		}
		
		SpectraRetTimeDistribution distribution = new SpectraRetTimeDistribution(Program.PERCOLATOR);
		distribution.setScoreCutoff(this.scoreCutoff);
		distribution.setNumBins(allSpectraCounts.length);
		distribution.setMaxRT(maxRt);
		distribution.setMaxSpectraCount(maxPsmCount);
		distribution.setFilteredSpectraCounts(filteredSpectraCounts);
		distribution.setAllSpectraCounts(allSpectraCounts);
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

	private double getMaxRtForPercolatorResults(List<PercolatorBinnedSpectraResult> binnedResults) {
		
		double max = 0;
		for(PercolatorBinnedSpectraResult res: binnedResults) {
			max = Math.max(max, res.getBinEnd());
		}
		return max;
	}
	
	private double getMaxRtForProphetResults(List<ProphetBinnedSpectraResult> binnedResults) {
		
		double max = 0;
		for(ProphetBinnedSpectraResult res: binnedResults) {
			max = Math.max(max, res.getBinEnd());
		}
		return max;
	}
}
