/**

 * PeptideMassDiffDistributionCalculator.java
 * @author Vagisha Sharma
 * Oct 5, 2010
 */
package org.yeastrc.ms.service.percolator.stats;

import java.math.BigDecimal;
import java.util.List;

import org.apache.commons.math.stat.descriptive.DescriptiveStatistics;
import org.apache.log4j.Logger;
import org.yeastrc.ms.dao.DAOFactory;
import org.yeastrc.ms.dao.analysis.MsRunSearchAnalysisDAO;
import org.yeastrc.ms.dao.analysis.percolator.PercolatorResultDAO;
import org.yeastrc.ms.dao.search.sequest.SequestSearchResultDAO;
import org.yeastrc.ms.domain.analysis.percolator.PercolatorResult;
import org.yeastrc.ms.domain.analysis.percolator.PercolatorResultFilterCriteria;
import org.yeastrc.ms.domain.search.sequest.SequestSearchResult;
import org.yeastrc.ms.util.TimeUtils;

/**
 * 
 */
public class PeptideMassDiffDistributionCalculator {

	private int analysisId;
	private double scoreCutoff;
	private double minDiff;
	private double maxDiff;
	private double mean;
	private double stdDev;
	private List<Bin> bins; 
	private int totalCount;
	private boolean usePpmDifference = false;
	
	private static final Logger log = Logger.getLogger(PeptideMassDiffDistributionCalculator.class);
	
	public PeptideMassDiffDistributionCalculator (int analysisId, double scoreCutoff, boolean usePpmDifference) {
		this.analysisId = analysisId;
		this.scoreCutoff = scoreCutoff;
		this.usePpmDifference = usePpmDifference;
	}
	                            
	public double getMinDiff() {
		return minDiff;
	}

	public double getMaxDiff() {
		return maxDiff;
	}

	public double getMean() {
		return mean;
	}

	public double getStdDev() {
		return stdDev;
	}

	public List<Bin> getBins() {
		return bins;
	}
	
	public int getTotalCount() {
		return totalCount;
	}

	public void calculate() {
		
		DAOFactory daoFactory = DAOFactory.instance();
		
		MsRunSearchAnalysisDAO rsaDao = daoFactory.getMsRunSearchAnalysisDAO();
		PercolatorResultDAO percDao = daoFactory.getPercolatorResultDAO();
		SequestSearchResultDAO seqDao = daoFactory.getSequestResultDAO();
		
		List<Integer> runSearchAnalysisIds = rsaDao.getRunSearchAnalysisIdsForAnalysis(analysisId);
		
		PercolatorResultFilterCriteria filterCriteria = new PercolatorResultFilterCriteria();
		filterCriteria.setMaxQValue(scoreCutoff);
		
		
		DescriptiveStatistics stats = new DescriptiveStatistics();
		
		long s = System.currentTimeMillis();
		
		int totalResults = 0;
		for(Integer rsaId: runSearchAnalysisIds) {
			
			List<Integer> percResultIds = percDao.loadIdsForRunSearchAnalysis(rsaId, filterCriteria, null);
			totalResults += percResultIds.size();
			
			log.info("Found "+percResultIds.size()+" Percolator results at qvalue <= "+scoreCutoff+
					" for runSearchAnalysisID "+rsaId);
			
			for(Integer percResultId: percResultIds) {

				PercolatorResult pres = percDao.loadForPercolatorResultId(percResultId);
				BigDecimal obsMass = pres.getObservedMass();

				SequestSearchResult seqRes = seqDao.load(pres.getId());
				BigDecimal calcMass = seqRes.getSequestResultData().getCalculatedMass();

				double diff = obsMass.doubleValue() - calcMass.doubleValue();
				
				if(this.usePpmDifference) {
					
					diff = (diff / calcMass.doubleValue()) * 1000000;
				}
				stats.addValue(diff);
			}
		}
		this.totalCount = totalResults;
		
		long e = System.currentTimeMillis();
		
		log.info("Total time to get results "+TimeUtils.timeElapsedSeconds(s, e)+" seconds");
		log.info("Total filtered results: "+totalResults);
		
		s = System.currentTimeMillis();
		mean = stats.getMean();
		stdDev = stats.getStandardDeviation();
		minDiff = stats.getMin();
		maxDiff = stats.getMax();
		System.out.println("VARIANCE: "+stats.getVariance());
		
		initBins(minDiff, maxDiff);
		
		double[] sortedDiffs = stats.getSortedValues();
		
		
		int idx = 0;
		for(Bin bin: bins) {
			
			int count = 0;
			
			for(int j = idx; j < sortedDiffs.length; j++) {
				
				idx = j;
				double d = sortedDiffs[j];
				if(d > bin.getBinEnd()) {
					break;
				}
				count++;
			}
			
			bin.setBinCount(count);
		}
		
		e = System.currentTimeMillis();
		log.info("Total time to bin results "+TimeUtils.timeElapsedSeconds(s, e)+" seconds");
	}


	private void initBins(double minValue, double maxValue) {
		
		this.bins = BinCalculator.getInstance().getBins(minValue, maxValue, 15);
//		bins = new ArrayList<Bin>();
//		double s = -3.25;
//		for(int i = 0; i < 13; i++) {
//			bins.add(new Bin(s, s+0.5));
//			s += 0.5;
//		}
	}
	
	public static void main(String[] args) {
		
		int analysisId = 99;
		double qvalue = 0.01;
		
		PeptideMassDiffDistributionCalculator calculator = new PeptideMassDiffDistributionCalculator(analysisId, qvalue, false);
		calculator.calculate();
		List<Bin> bins = calculator.getBins();
		System.out.println("Min: "+calculator.getMinDiff());
		System.out.println("Max: "+calculator.getMaxDiff());
		System.out.println("Mean: "+calculator.getMean());
		System.out.println("StdDev: "+calculator.getStdDev());
		int total = 0;
		for(Bin bin: bins) {
			System.out.println(bin);
			total += bin.getBinCount();
		}
		System.out.println("Total filtered results: "+total);
	}
}
