/**
 * ProphetFilteredPsmDistributionCalculator.java
 * @author Vagisha Sharma
 * Aug 6, 2011
 * @version 1.0
 */
package org.yeastrc.ms.service.pepxml.stats;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.yeastrc.ms.dao.DAOFactory;
import org.yeastrc.ms.dao.analysis.MsRunSearchAnalysisDAO;
import org.yeastrc.ms.domain.analysis.peptideProphet.impl.ProphetBinnedPsmResult;
import org.yeastrc.ms.domain.analysis.peptideProphet.impl.ProphetFilteredPsmResult;
import org.yeastrc.ms.domain.search.Program;
import org.yeastrc.ms.util.TimeUtils;

/**
 * 
 */
public class ProphetFilteredPsmDistributionCalculator {

    
    private int analysisId;
    private double scoreCutoff;
    
    private int[] allPsmCounts;
    private int[] filteredPsmCounts;
    public static double BIN_SIZE = 1.0;
    private int numBins;
    
    private List<ProphetFilteredPsmResult> filteredResults;
    
    private static final Logger log = Logger.getLogger(ProphetFilteredPsmDistributionCalculator.class.getName());
    
    public ProphetFilteredPsmDistributionCalculator(int analysisId, double scoreCutoff) {
        this.analysisId = analysisId;
        this.scoreCutoff = scoreCutoff;
        filteredResults = new ArrayList<ProphetFilteredPsmResult>();
    }
    
    public List<ProphetFilteredPsmResult> getFilteredResults() {
        return filteredResults;
    }
    
    public double getScoreCutoff() {
        return this.scoreCutoff;
    }
    
    public void calculate() {
        
        Program analysisProgram = DAOFactory.instance().getMsSearchAnalysisDAO().load(analysisId).getAnalysisProgram();
        
        if(analysisProgram == Program.PEPTIDE_PROPHET) {
            
        	if(!initBins()) {
        		log.error("There was an error iniitalizing bins for searchAnalysisID: "+analysisId);
        		return;
        	}
        	
            // we will calculate two things: 
            // 1. RT distribution of all PeptideProphet PSMs
            // 2. RT distribution of filtered PeptideProphet PSM's (with given probability cutoff)
            
            binUsingJDBC(analysisId, scoreCutoff);
        }
        else {
            log.error("Don't know how to build RT distribution for analysis program: "+analysisProgram);
        }
    }

    private void binUsingJDBC(int analysisId, double scoreCutoff) {
        
        long s = System.currentTimeMillis();
        
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        MsRunSearchAnalysisDAO rsaDao = DAOFactory.instance().getMsRunSearchAnalysisDAO();
        List<Integer> runSearchAnalysisIds = rsaDao.getRunSearchAnalysisIdsForAnalysis(analysisId);
        
        
        try {
            conn = DAOFactory.instance().getConnection();
            String sql = "SELECT scan.retentionTime, pres.probability "+
                         "FROM msScan AS scan, PeptideProphetResult AS pres, msRunSearchResult AS res "+
                         "WHERE pres.runSearchAnalysisID = ? "+
                         "AND pres.resultID = res.id "+
                         "AND res.scanID = scan.id ";
            
            stmt = conn.prepareStatement(sql);
            
            for(Integer runSearchAnalysisId: runSearchAnalysisIds) {
            	
            	stmt.setInt(1, runSearchAnalysisId);
            	rs = stmt.executeQuery();

            	int psmCnt = 0;
            	int goodPsmCnt = 0;
            	
            	while(rs.next()) {
            		
            		double probability = rs.getDouble("probability");
            		
            		psmCnt++;
            		if(probability >= scoreCutoff)
            			goodPsmCnt++;

            		// If we don't have retention time for a scan skip the whole runSearchAnalysis
            		if(rs.getObject("retentionTime") == null) {
            			log.debug("!!!RETENTION TIME NOT FOUND for runSearchAnalysisID: "+runSearchAnalysisId+". Will not be binned....");
            		}
            		else {
            			double rt = rs.getBigDecimal("retentionTime").doubleValue();
            			putInBin(rt, probability, scoreCutoff);
            		}
            	}

            	// add to list
            	ProphetFilteredPsmResult stat = new ProphetFilteredPsmResult();
            	stat.setRunSearchAnalysisId(runSearchAnalysisId);
            	stat.setTotal(psmCnt);
            	stat.setFiltered(goodPsmCnt);
            	stat.setProbability(scoreCutoff);
            	List<ProphetBinnedPsmResult> binnedResults = new ArrayList<ProphetBinnedPsmResult>();
            	for(int i = 0; i < numBins; i++) {
            		ProphetBinnedPsmResult bin = new ProphetBinnedPsmResult();
            		bin.setBinStart(i*BIN_SIZE);
            		bin.setBinEnd(bin.getBinStart() + BIN_SIZE);
            		bin.setTotal(allPsmCounts[i]);
            		bin.setFiltered(filteredPsmCounts[i]);
            		binnedResults.add(bin);
            	}
            	stat.setBinnedResults(binnedResults);
            	filteredResults.add(stat);
            	
            	allPsmCounts = new int[numBins];
            	filteredPsmCounts = new int[numBins];
            	
            	log.info("Calculated results for runSearchAnalysisID: "+runSearchAnalysisId);
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
        
        long e = System.currentTimeMillis();
        log.info("Binned data in: "+TimeUtils.timeElapsedSeconds(s, e)+" seconds");
    }

    
    public int getNumBins() {
        return numBins;
    }
    
    public double getBinSize() {
        return BIN_SIZE;
    }
    
    public List<ProphetFilteredPsmResult> getResult() {
    	return this.filteredResults;
    }
    
    private void putInBin(double rt, double probability, double scoreCutoff) {
        
    	int binIndex = (int)(rt / BIN_SIZE);
        allPsmCounts[binIndex]++;
        
        if(probability >= scoreCutoff) {
            filteredPsmCounts[binIndex]++;
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
        
        allPsmCounts = new int[numBins];
        filteredPsmCounts = new int[numBins];
        
        long e = System.currentTimeMillis();
        log.info("Initialized bins in "+TimeUtils.timeElapsedSeconds(s, e)+"seconds");
        return true;
    }
    
}
