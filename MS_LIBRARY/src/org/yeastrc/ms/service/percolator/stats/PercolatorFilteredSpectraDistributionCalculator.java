/**
 * PercolatorFilteredSpectraDistributionCalculator.java
 * @author Vagisha Sharma
 * Dec 31, 2009
 * @version 1.0
 */
package org.yeastrc.ms.service.percolator.stats;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.yeastrc.ms.dao.DAOFactory;
import org.yeastrc.ms.dao.analysis.MsRunSearchAnalysisDAO;
import org.yeastrc.ms.dao.analysis.percolator.PercolatorResultDAO;
import org.yeastrc.ms.dao.run.MsScanDAO;
import org.yeastrc.ms.domain.analysis.MsRunSearchAnalysis;
import org.yeastrc.ms.domain.analysis.percolator.PercolatorResult;
import org.yeastrc.ms.domain.analysis.percolator.impl.PercolatorBinnedSpectraResult;
import org.yeastrc.ms.domain.analysis.percolator.impl.PercolatorFilteredSpectraResult;
import org.yeastrc.ms.domain.run.MsScan;
import org.yeastrc.ms.domain.search.Program;
import org.yeastrc.ms.util.TimeUtils;


/**
 * 
 */
public class PercolatorFilteredSpectraDistributionCalculator {

    private int analysisId;
    private double scoreCutoff;
    
    private int[] allSpectraCounts;
    private int[] filteredSpectraCounts;
    public static double BIN_SIZE = 1.0;
    private int numBins;
    
    private List<PercolatorFilteredSpectraResult> filteredResults;
    
    private static final Logger log = Logger.getLogger(PercolatorFilteredSpectraDistributionCalculator.class.getName());
    
    public PercolatorFilteredSpectraDistributionCalculator(int analysisId, double scoreCutoff) {
        this.analysisId = analysisId;
        this.scoreCutoff = scoreCutoff;
        filteredResults = new ArrayList<PercolatorFilteredSpectraResult>();
    }
    
    public List<PercolatorFilteredSpectraResult> getFilteredResults() {
        return filteredResults;
    }
    
    public double getScoreCutoff() {
        return this.scoreCutoff;
    }
    
    
    public void calculate() {
        
        Program analysisProgram = DAOFactory.instance().getMsSearchAnalysisDAO().load(analysisId).getAnalysisProgram();
        
        
        if(analysisProgram == Program.PERCOLATOR) {
            
        	if(!initBins()) {
        		log.error("There was an error iniitalizing bins for searchAnalysisID: "+analysisId);
        		return;
        	}
        	
            // we will calculate two things: 
            // 1. RT distribution of all acquired MS/MS spectra for the analysis
            // 2. RT distribution of spectra with IDs >= given qvalue cutoff
            
            MsRunSearchAnalysisDAO rsDao = DAOFactory.instance().getMsRunSearchAnalysisDAO();
            List<Integer> runSearchAnalysisIds = rsDao.getRunSearchAnalysisIdsForAnalysis(analysisId);
            
            
            long s = System.currentTimeMillis();
            log.info("Binning data..");
            
            for(int runSearchAnalysisId: runSearchAnalysisIds) {
                
            	MsRunSearchAnalysis rsAnalysis = rsDao.load(runSearchAnalysisId);
            	int runId = DAOFactory.instance().getMsRunSearchDAO().loadRunSearch(rsAnalysis.getRunSearchId()).getRunId();
            	
            	log.info("Getting data for runSearchAnalysis: "+runSearchAnalysisId+"; runId: "+runId);
                
            	binUsingMsLib(scoreCutoff, runSearchAnalysisId, runId);
            	//binUsingJDBC(scoreCutoff, runSearchAnalysisId, runId);
            	
            	log.info("Calculated results for runSearchAnalysisID: "+runSearchAnalysisId);
            }
            
            long e = System.currentTimeMillis();
            log.info("Binned data in: "+TimeUtils.timeElapsedSeconds(s, e)+"seconds");
        }
        else {
            log.error("Don't know how to build RT distribution for analysis program: "+analysisProgram);
        }
    }

    
    private void binUsingMsLib(double scoreCutoff, int runSearchAnalysisId, int runId) {
    	
        long s = System.currentTimeMillis();
        
        allSpectraCounts = new int[numBins];
        filteredSpectraCounts = new int[numBins];
        
        int scanCnt = 0;
        int goodScanCnt = 0;
        
        DAOFactory daoFactory = DAOFactory.instance();
        MsScanDAO scanDao = daoFactory.getMsScanDAO();
        PercolatorResultDAO percResDao = daoFactory.getPercolatorResultDAO();
        
            
        List<Integer> scanIds = scanDao.loadScanIdsForRun(runId);

        for(Integer scanId: scanIds) {

        	MsScan scan = scanDao.loadScanLite(scanId);
        	scanCnt++;

        	boolean filtered = false;
        	List<Integer> percResultIds = percResDao.loadIdsForRunSearchAnalysisScan(runSearchAnalysisId, scanId);
        	if(percResultIds != null && percResultIds.size() > 0) {

        		for(Integer percResultId: percResultIds) {
        			PercolatorResult pres = percResDao.loadForPercolatorResultId(percResultId);
        			if(pres.getQvalue() <= scoreCutoff) {
        				filtered = true;
        				goodScanCnt++;
        				break;
        			}
        		}
        	}

        	// If we don't have retention time for a scan skip the whole runSearchAnalysis
    		if(scan.getRetentionTime() == null) {
    			log.debug("!!!RETENTION TIME NOT FOUND for runSearchAnalysisID: "+runSearchAnalysisId+". Will not be binned....");
    		}
    		else {
    			double rt = scan.getRetentionTime().doubleValue();
    			putScanInBin(rt, filtered);
    		}

        }
        
        
        // add to list
    	PercolatorFilteredSpectraResult stat = new PercolatorFilteredSpectraResult();
    	stat.setRunSearchAnalysisId(runSearchAnalysisId);
    	stat.setTotal(scanCnt);
    	stat.setFiltered(goodScanCnt);
    	stat.setQvalue(scoreCutoff);
    	List<PercolatorBinnedSpectraResult> binnedResults = new ArrayList<PercolatorBinnedSpectraResult>();
    	for(int i = 0; i < numBins; i++) {
    		PercolatorBinnedSpectraResult bin = new PercolatorBinnedSpectraResult();
    		bin.setBinStart(i*BIN_SIZE);
    		bin.setBinEnd(bin.getBinStart() + BIN_SIZE);
    		bin.setTotal(allSpectraCounts[i]);
    		bin.setFiltered(filteredSpectraCounts[i]);
    		binnedResults.add(bin);
    	}
    	stat.setBinnedResults(binnedResults);
    	filteredResults.add(stat);
    	
        long e = System.currentTimeMillis();
        log.info("Binned data in: "+TimeUtils.timeElapsedSeconds(s, e)+" seconds");
    }
    
    private void binUsingJDBC(double scoreCutoff, int runSearchAnalysisId, int runId) {
        
        long s = System.currentTimeMillis();
        
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        
        allSpectraCounts = new int[numBins];
        filteredSpectraCounts = new int[numBins];
        
        int scanCnt = 0;
        int goodScanCnt = 0;
        
        try {
            conn = DAOFactory.instance().getConnection();
            String sql = "SELECT scan.id, scan.retentionTime, pres.qvalue "+
                         "FROM msScan AS scan "+
                         "LEFT JOIN (msRunSearchResult AS res, PercolatorResult AS pres) "+
                         "ON (scan.id = res.scanID AND res.id = pres.resultID AND pres.runSearchAnalysisID="+runSearchAnalysisId+") "+
                         "WHERE scan.runID = "+runId+" "+
                         "ORDER BY scan.id,qvalue ASC";
            
            log.info(sql);
            
            stmt = conn.createStatement();
            rs = stmt.executeQuery(sql);
            
            int lastScan = -1;
            
            while(rs.next()) {
                int scanId = rs.getInt("id");
                if(scanId == lastScan)
                    continue;
                lastScan = scanId;
                
                boolean filtered = false;
                if(rs.getObject("qvalue") != null) {
                	filtered = rs.getDouble("qvalue") <= scoreCutoff;
                }
                
                // If we don't have retention time for a scan skip the whole runSearchAnalysis
        		if(rs.getObject("retentionTime") == null) {
        			log.debug("!!!RETENTION TIME NOT FOUND for runSearchAnalysisID: "+runSearchAnalysisId+". Will not be binned....");
        		}
        		else {
        			double rt = rs.getBigDecimal("retentionTime").doubleValue();
        			putScanInBin(rt, filtered);
        		}
                
                scanCnt++;
                if(filtered)
                    goodScanCnt++;
            }
        }
        catch(SQLException ex) {
            log.error("Error binning data",ex);
        }
        finally {
            if(conn != null) try {conn.close();} catch(SQLException e){}
            if(stmt != null) try {stmt.close();} catch(SQLException e){}
            if(rs != null)   try {rs.close();}   catch(SQLException e){}
        }
        
        
        // add to list
    	PercolatorFilteredSpectraResult stat = new PercolatorFilteredSpectraResult();
    	stat.setRunSearchAnalysisId(runSearchAnalysisId);
    	stat.setTotal(scanCnt);
    	stat.setFiltered(goodScanCnt);
    	stat.setQvalue(scoreCutoff);
    	List<PercolatorBinnedSpectraResult> binnedResults = new ArrayList<PercolatorBinnedSpectraResult>();
    	for(int i = 0; i < numBins; i++) {
    		PercolatorBinnedSpectraResult bin = new PercolatorBinnedSpectraResult();
    		bin.setBinStart(i*BIN_SIZE);
    		bin.setBinEnd(bin.getBinStart() + BIN_SIZE);
    		bin.setTotal(allSpectraCounts[i]);
    		bin.setFiltered(filteredSpectraCounts[i]);
    		binnedResults.add(bin);
    	}
    	stat.setBinnedResults(binnedResults);
    	filteredResults.add(stat);
    	
        long e = System.currentTimeMillis();
        log.info("Binned data in: "+TimeUtils.timeElapsedSeconds(s, e)+" seconds");
    }


    public int getNumBins() {
        return numBins;
    }
    
    public double getBinSize() {
        return BIN_SIZE;
    }
    
    public List<PercolatorFilteredSpectraResult> getResult() {
    	return this.filteredResults;
    }
    
    private void putScanInBin(double rt, boolean isFiltered) {
    	
    	int binIndex = (int)(rt / BIN_SIZE);
        allSpectraCounts[binIndex]++;
        
        if(isFiltered) {
            filteredSpectraCounts[binIndex]++;
        }
    }
    
    private boolean initBins() {
        
        long s = System.currentTimeMillis();
        log.info("Initializing bins...");
        // get the runIDs for this analysis
        List<Integer> searchIds = DAOFactory.instance().getMsSearchAnalysisDAO().getSearchIdsForAnalysis(analysisId);
        List<Integer> runIds = new ArrayList<Integer>();
        for(int searchId: searchIds) {
            int experimentId = DAOFactory.instance().getMsSearchDAO().loadSearch(searchId).getExperimentId();
            List<Integer> rIds = DAOFactory.instance().getMsExperimentDAO().getRunIdsForExperiment(experimentId);
            runIds.addAll(rIds);
        }
        
        if(runIds.size() == 0) {
        	log.warn("No runIds found for searchAnalysisID: "+analysisId);
        	return false;
        }
        
        // get max RT and create bins
        double maxRT = DAOFactory.instance().getMsRunDAO().getMaxRetentionTimeForRuns(runIds);
        numBins = (int)(maxRT / BIN_SIZE) + 1;
        
        long e = System.currentTimeMillis();
        log.info("Initialized bins in "+TimeUtils.timeElapsedSeconds(s, e)+"seconds");
        return true;
    }
}
