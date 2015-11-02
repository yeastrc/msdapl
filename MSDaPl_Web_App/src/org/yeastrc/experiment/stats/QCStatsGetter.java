/**
 * QCStatsGetter.java
 * @author Vagisha Sharma
 * Jan 22, 2011
 */
package org.yeastrc.experiment.stats;

import java.util.List;

import org.apache.log4j.Logger;
import org.yeastrc.ms.dao.DAOFactory;
import org.yeastrc.ms.dao.analysis.PeptideTerminiStatsDAO;
import org.yeastrc.ms.domain.analysis.impl.PeptideTerminalAAResult;
import org.yeastrc.ms.service.stats.PeptideAAFrequencyGooglePlotUrlBuilder;
import org.yeastrc.www.project.experiment.stats.PlotUrlCache;

/**
 * 
 */
public class QCStatsGetter {

	private static final Logger log = Logger.getLogger(QCStatsGetter.class.getName());
	
	private boolean getPsmRtStats = false;
	private FileStats psmAnalysisStats;		// stats at the analysis level
	private List<FileStats> psmFileStats; 	// stats for individual files in the analysis
	private String psmDistrUrl;
	
	private boolean getSpectraRtStats = false;
	private FileStats spectraAnalysisStats; 	// stats at the analysis level
	private List<FileStats> spectraFileStats;	// stats for the individual files in the analysis
	private String spectraDistrUrl;
	
	
	private boolean getTerminalResidueStats = false;
	private PeptideTerminalAAResult peptideTerminiStats = null;
	private String peptideTerminalResiduePlotUrl = null;
	
	public static final double PERC_QVAL_DEFAULT = 0.01;
	public static final double PEPPROPHET_ERR_RATE_DEFAULT = 0.01;
	
	public QCStatsGetter () {}
	
	public void getStats(int analysisId, double scoreCutoff) {
		
		PlotUrlCache cache = PlotUrlCache.getInstance();
		if(getPsmRtStats)
			getPsmRtStats(analysisId, scoreCutoff, cache);
		if(getSpectraRtStats)
			getSpectraRtStats(analysisId, scoreCutoff, cache);
		if(getTerminalResidueStats)
			getPeptideTerminalAAStats(analysisId, scoreCutoff);
	}
	
	public void setGetPsmRtStats(boolean getPsmRtStats) {
		this.getPsmRtStats = getPsmRtStats;
	}

	public void setGetSpectraRtStats(boolean getSpectraRtStats) {
		this.getSpectraRtStats = getSpectraRtStats;
	}
	
	public void setGetPeptideTerminiStats(boolean getTerminalResidueStats) {
		this.getTerminalResidueStats = getTerminalResidueStats;
	}

	public FileStats getPsmAnalysisStats() {
		return psmAnalysisStats;
	}

	public List<FileStats> getPsmFileStats() {
		return psmFileStats;
	}

	public String getPsmDistrUrl() {
		return psmDistrUrl;
	}

	public FileStats getSpectraAnalysisStats() {
		return spectraAnalysisStats;
	}

	public List<FileStats> getSpectraFileStats() {
		return spectraFileStats;
	}

	public String getSpectraDistrUrl() {
		return spectraDistrUrl;
	}
	
	public PeptideTerminalAAResult getPeptideTerminalResidueStats() {
		return this.peptideTerminiStats;
	}
	
	public String getPeptideTerminalResiduePlotUrl() {
		return this.peptideTerminalResiduePlotUrl;
	}

	// -----------------------------------------------------------------------------
    // PSM-RT stats
    // -----------------------------------------------------------------------------
	private void getPsmRtStats(int analysisId, double scoreCutoff, PlotUrlCache cache) {
		
        psmDistrUrl = cache.getPsmRtPlotUrl(analysisId, scoreCutoff);
        psmFileStats = cache.getPsmRtFileStats(analysisId, scoreCutoff);
        if(psmDistrUrl == null) {
        	PsmRetTimeDistributionGetter distrGetter = new PsmRetTimeDistributionGetter(analysisId, scoreCutoff);
        	PsmRetTimeDistribution result = distrGetter.getDistribution();
            psmFileStats = result.getFileStatsList();
            psmDistrUrl = result.getGoogleChartUrl();
            cache.addPsmRtPlotUrl(analysisId, scoreCutoff, psmDistrUrl, psmFileStats);
        }
        
        log.info("#PSM-RT Plot URL: "+psmDistrUrl);
        
        // stats at the experiment (analysis) level
        psmAnalysisStats = new FileStats(analysisId, "none");
        int totalCount = 0; int goodCount = 0;
        for(FileStats stat: psmFileStats) { totalCount += stat.getTotalCount(); goodCount += stat.getGoodCount();}
    	psmAnalysisStats.setTotalCount(totalCount);
    	psmAnalysisStats.setGoodCount(goodCount);
    	
        if(psmFileStats.get(0).getHasPopulationStats()) {
        	FileStats st = psmFileStats.get(0);
        	psmAnalysisStats.setPopulationMin(st.getPopulationMin());
        	psmAnalysisStats.setPopulationMax(st.getPopulationMax());
        	psmAnalysisStats.setPopulationMean(st.getPopulationMean());
        	psmAnalysisStats.setPopulationStandardDeviation(st.getPopulationStandardDeviation());
        }
	}
	
	// -----------------------------------------------------------------------------
    // Spectra-RT stats
    // -----------------------------------------------------------------------------
	private void getSpectraRtStats(int analysisId, double scoreCutoff, PlotUrlCache cache) {
		
		spectraDistrUrl = cache.getSpectraRtPlotUrl(analysisId, scoreCutoff);
		spectraFileStats = cache.getSpectraRtFileStats(analysisId, scoreCutoff);
        
        if(spectraDistrUrl == null) {
        	SpectraRetTimeDistributionGetter distrGetter = new SpectraRetTimeDistributionGetter(analysisId, scoreCutoff);
        	SpectraRetTimeDistribution result = distrGetter.getDistribution();
        	if(result != null)
        	{
	            spectraFileStats = result.getFileStatsList();
	            spectraDistrUrl = result.getGoogleChartUrl();
	            PlotUrlCache.getInstance().addSpectraRtPlotUrl(analysisId, scoreCutoff, spectraDistrUrl, spectraFileStats);
        	}
        }
        
        log.info("#Spectra-RT Plot URL: "+spectraDistrUrl);
        
        if(spectraFileStats != null)
        {
	        // stats at the experiment (analysis) level
	        spectraAnalysisStats = new FileStats(analysisId, "none");
	        int totalSpectraCount = 0; int goodSpectraCount = 0;
	        for(FileStats stat: spectraFileStats) { totalSpectraCount += stat.getTotalCount(); goodSpectraCount += stat.getGoodCount();}
	        spectraAnalysisStats.setTotalCount(totalSpectraCount);
	        spectraAnalysisStats.setGoodCount(goodSpectraCount);
	    	
	        if(spectraFileStats.get(0).getHasPopulationStats()) {
	        	FileStats st = spectraFileStats.get(0);
	        	spectraAnalysisStats.setPopulationMin(st.getPopulationMin());
	        	spectraAnalysisStats.setPopulationMax(st.getPopulationMax());
	        	spectraAnalysisStats.setPopulationMean(st.getPopulationMean());
	        	spectraAnalysisStats.setPopulationStandardDeviation(st.getPopulationStandardDeviation());
	        }
        }
	}
	
	// -----------------------------------------------------------------------------
    // Peptide terminal residue stats
    // -----------------------------------------------------------------------------
	private void getPeptideTerminalAAStats(int analysisId, double scoreCutoff) {
		
		PeptideTerminiStatsDAO dao = DAOFactory.instance().getPeptideTerminiStatsDAO();
		PeptideTerminalAAResult result = dao.load(analysisId);
		
		// We are only saving stats for qvalue of 0.01
		// For now we are not giving the user the option of recalculating at another qvalue cutoff
		if(result != null && result.getScoreCutoff() == scoreCutoff) {
			
			this.peptideTerminiStats = result;
			this.peptideTerminalResiduePlotUrl = PeptideAAFrequencyGooglePlotUrlBuilder.getUrl(result);
		}
	}
	
}
