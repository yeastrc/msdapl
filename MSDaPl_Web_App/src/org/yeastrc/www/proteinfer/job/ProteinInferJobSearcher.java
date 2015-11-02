package org.yeastrc.www.proteinfer.job;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.yeastrc.db.DBConnectionManager;
import org.yeastrc.jobqueue.JobUtils;
import org.yeastrc.ms.dao.DAOFactory;
import org.yeastrc.ms.dao.ProteinferDAOFactory;
import org.yeastrc.ms.dao.analysis.MsSearchAnalysisDAO;
import org.yeastrc.ms.dao.protinfer.ibatis.ProteinferRunDAO;
import org.yeastrc.ms.dao.search.MsRunSearchDAO;
import org.yeastrc.ms.dao.search.MsSearchDAO;
import org.yeastrc.ms.domain.analysis.MsSearchAnalysis;
import org.yeastrc.ms.domain.protinfer.ProteinInferenceProgram;
import org.yeastrc.ms.domain.protinfer.ProteinferRun;
import org.yeastrc.ms.domain.search.MsSearch;

public class ProteinInferJobSearcher {

    private static final Logger log = Logger.getLogger(ProteinInferJobSearcher.class.getName());
    
    private static final ProteinferDAOFactory factory = ProteinferDAOFactory.instance();
    private static final ProteinferRunDAO runDao = factory.getProteinferRunDao();
    private static final MsSearchDAO searchDao = DAOFactory.instance().getMsSearchDAO();
    private static final MsSearchAnalysisDAO analysisDao = DAOFactory.instance().getMsSearchAnalysisDAO();
    
    private static ProteinInferJobSearcher instance;
    
    private ProteinInferJobSearcher() {}
    
    public static ProteinInferJobSearcher getInstance() {
        if(instance == null)
            instance = new ProteinInferJobSearcher();
        return instance;
    }
    
    public List<ProteinferJob> getProteinferJobsForMsExperiment(int experimentId) {
        
        List<Integer> pinferRunIds = getProteinferIdsForMsExperiment(experimentId);
        
        if(pinferRunIds == null || pinferRunIds.size() == 0)
            return new ArrayList<ProteinferJob>(0);
        
        List<ProteinferJob> jobs = new ArrayList<ProteinferJob>(pinferRunIds.size());
        for(int pid: pinferRunIds) {
            ProteinferJob job = getJobForPiRunId(pid);
            if(job != null)
                jobs.add(job);
        }
        // sort jobs by id
        Collections.sort(jobs, new Comparator<ProteinferJob>() {
            public int compare(ProteinferJob o1, ProteinferJob o2) {
                return Integer.valueOf(o1.getPinferId()).compareTo(o2.getPinferId());
            }});
        return jobs;
    }
    
    public List<Integer> getProteinferIdsForMsExperiment(int experimentId) {
        
        Set<Integer> pinferRunIds = new HashSet<Integer>();
        
        // Get the searchIds for this experiment
        List<Integer> searchIds = DAOFactory.instance().getMsSearchDAO().getSearchIdsForExperiment(experimentId);
        for(int searchId: searchIds) {
            List<Integer> piRunIds = getPinferRunIdsForSearch(searchId);
            pinferRunIds.addAll(piRunIds);
        }
        
        if(pinferRunIds == null || pinferRunIds.size() == 0)
            return new ArrayList<Integer>(0);
        
        return new ArrayList<Integer>(pinferRunIds);
    }

    public List<ProteinferJob> getProteinferJobsForMsSearchAnalysis(int msSearchAnalysisId) {
        
    	MsSearchAnalysis analysis = analysisDao.load(msSearchAnalysisId);
        List<Integer> piRunIds = runDao.loadProteinferIdsForInputIds(
                getRunSearchAnalysisIdsForAnalysis(msSearchAnalysisId), analysis.getAnalysisProgram());
        
        Set<Integer> pinferRunIds = new HashSet<Integer>();
        pinferRunIds.addAll(piRunIds);
        
        if(pinferRunIds == null || pinferRunIds.size() == 0)
            return new ArrayList<ProteinferJob>(0);
        
        List<ProteinferJob> jobs = new ArrayList<ProteinferJob>(pinferRunIds.size());
        for(int pid: pinferRunIds) {
            ProteinferRun run = runDao.loadProteinferRun(pid);
            if(run != null) {
                
                ProteinferJob job = getJobForPiRunId(run.getId());
                if(job != null)
                    jobs.add(job);
            }
        }
        // sort jobs by id
        Collections.sort(jobs, new Comparator<ProteinferJob>() {
            public int compare(ProteinferJob o1, ProteinferJob o2) {
                return Integer.valueOf(o1.getPinferId()).compareTo(o2.getPinferId());
            }});
        return jobs;
    }
    
    public List<Integer> getProteinferIdsForMsSearchAnalysis(int msSearchAnalysisId) {
        
    	MsSearchAnalysis analysis = analysisDao.load(msSearchAnalysisId);
        List<Integer> piRunIds = runDao.loadProteinferIdsForInputIds(
                getRunSearchAnalysisIdsForAnalysis(msSearchAnalysisId), analysis.getAnalysisProgram());
        
        Set<Integer> pinferRunIds = new HashSet<Integer>();
        pinferRunIds.addAll(piRunIds);
        
        return new ArrayList<Integer>(pinferRunIds);
    }


    private List<Integer> getPinferRunIdsForSearch(int msSearchId) {
        
        // load the search
        MsSearch search = searchDao.loadSearch(msSearchId);
        
        Set<Integer> pinferIdsSet = new HashSet<Integer>();
        
        // first get all the runSearchIds for this search
        List<Integer> msRunSearchIds = getRunSearchIdsForMsSearch(msSearchId);
        // load protein inference results for the runSearchIDs where the input generator was 
        // the search program
        List<Integer> searchInputIds = runDao.loadProteinferIdsForInputIds(msRunSearchIds, search.getSearchProgram());
        pinferIdsSet.addAll(searchInputIds);
        
        
        // now check if there is any analysis associated with this search
        List<Integer> analysisIds = getAnalysisIdsForMsSearch(msSearchId);
        
        // get all the runSearchAnalysisIds for each analysis done on the search
        for(int analysisId: analysisIds) {
            // load the analysis
            MsSearchAnalysis analysis = analysisDao.load(analysisId);
            List<Integer> piRunIds = runDao.loadProteinferIdsForInputIds(
                    getRunSearchAnalysisIdsForAnalysis(analysisId), analysis.getAnalysisProgram());
            pinferIdsSet.addAll(piRunIds);
        }
        
        return new ArrayList<Integer>(pinferIdsSet);
    }
    
    
    /**
     * Returns a ProteinferJob object if the protein inference run with the given id exists in the database
     * @param pinferRunId
     * @return
     */
    public ProteinferJob getJobForPiRunId(int pinferRunId) {
        
        ProteinferRun run = runDao.loadProteinferRun(pinferRunId);
        
        if(run == null || !ProteinInferenceProgram.isIdPicker(run.getProgram())) {
        	log.error("No entry found for protein inference ID: "+pinferRunId+" OR this is not a IDPicker protein inference");
            return null;
        }
        
//      // make sure the input generator for this protein inference program was
//      // a search program or an analysis program
//      if(!Program.isSearchProgram(run.getInputGenerator()) && !Program.isAnalysisProgram(run.getInputGenerator()))
//      continue;
        ProteinferJob job = null;
        try {
            job = getPiJobForPiRunId(run.getId());
        }
        catch (SQLException e) {
            log.error("Exception getting ProteinferJob", e);
            return null;
        }
        if(job == null) {
            log.error("No job found with protein inference run id: "+pinferRunId);
            return null;
        }
        job.setProgram(run.getProgramString());
        job.setVersion(run.getProgramVersion());
        job.setComments(run.getComments());
        job.setDateRun(run.getDate());
        return job;
    }
    
    
    private ProteinferJob getPiJobForPiRunId(int pinferRunId) throws SQLException {
        
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            
            String sql = "SELECT * FROM tblJobs AS j, tblProteinInferJobs AS pj "+
                        "WHERE j.id = pj.jobID AND pj.piRunID="+pinferRunId;
            
            conn = DBConnectionManager.getConnection(DBConnectionManager.JOB_QUEUE);
            stmt = conn.prepareStatement( sql );
            rs = stmt.executeQuery();
            
            if (rs.next()) {
                ProteinferJob job = new ProteinferJob();
                job.setId( rs.getInt("jobID"));
                job.setSubmitter( rs.getInt( "submitter" ) );
                job.setType( rs.getInt( "type" ) );
                job.setSubmitDate( rs.getDate( "submitDate" ) );
                job.setLastUpdate( rs.getDate( "lastUpdate" ) );
                job.setStatus( rs.getInt( "status" ) );
                job.setAttempts( rs.getInt( "attempts" ) );
                job.setLog( rs.getString( "log" ) );
                job.setPinferRunId(pinferRunId);
                return job;
            }
            
        } finally {
            
            if (rs != null) {
                try { rs.close(); rs = null; } catch (Exception e) { ; }
            }

            if (stmt != null) {
                try { stmt.close(); stmt = null; } catch (Exception e) { ; }
            }
            
            if (conn != null) {
                try { conn.close(); conn = null; } catch (Exception e) { ; }
            }           
        }
        return null;
    }
    
    /**
     * Returns a ProteinferJob object if a protein inference job (IDPicker) with the given id exists in the database
     * @param jobId
     * @return
     */
    public ProteinferJob getJob(int jobId) {
        
        ProteinferJob job = null;
        try {
            job = getPiJob(jobId);
        }
        catch (SQLException e) {
            log.error("Exception getting ProteinferJob", e);
            return null;
        }
        if(job == null) {
            log.error("No job found with protein inference run id: "+jobId);
            return null;
        }
        if(job == null) {
        	log.error("No protein inference job found for jobID: "+jobId);
        	return null;
        }
        
        // Load the protein inference run
        ProteinferRun run = runDao.loadProteinferRun(job.getPinferId());
        
        // This should be a IDPicker run. We don't support other 
        if(run == null || !ProteinInferenceProgram.isIdPicker(run.getProgram())) {
        	log.error("No entry found for protein inference ID: "+job.getPinferId()+" OR this is not a IDPicker protein inference");
            return null;
        }
        
        job.setProgram(run.getProgramString());
        job.setVersion(run.getProgramVersion());
        job.setComments(run.getComments());
        job.setDateRun(run.getDate());
        return job;
    }
    
    
    private ProteinferJob getPiJob(int jobId) throws SQLException {
        
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            
            String sql = "SELECT * FROM tblJobs AS j, tblProteinInferJobs AS pj "+
                        "WHERE j.id = pj.jobID AND j.id="+jobId;
            
            conn = DBConnectionManager.getConnection(DBConnectionManager.JOB_QUEUE);
            stmt = conn.prepareStatement( sql );
            rs = stmt.executeQuery();
            
            if (rs.next()) {
                ProteinferJob job = new ProteinferJob();
                job.setId(jobId);
                job.setSubmitter( rs.getInt( "submitter" ) );
                job.setType( rs.getInt( "type" ) );
                job.setSubmitDate( rs.getDate( "submitDate" ) );
                job.setLastUpdate( rs.getDate( "lastUpdate" ) );
                job.setStatus( rs.getInt( "status" ) );
                job.setAttempts( rs.getInt( "attempts" ) );
                job.setLog( rs.getString( "log" ) );
                job.setPinferRunId(rs.getInt("piRunID"));
                return job;
            }
            
        } finally {
            
            if (rs != null) {
                try { rs.close(); rs = null; } catch (Exception e) { ; }
            }

            if (stmt != null) {
                try { stmt.close(); stmt = null; } catch (Exception e) { ; }
            }
            
            if (conn != null) {
                try { conn.close(); conn = null; } catch (Exception e) { ; }
            }           
        }
        return null;
    }

    private List<Integer> getRunSearchIdsForMsSearch(int msSearchId) {
        org.yeastrc.ms.dao.DAOFactory factory = org.yeastrc.ms.dao.DAOFactory.instance();
        MsRunSearchDAO runSearchDao = factory.getMsRunSearchDAO();
        return runSearchDao.loadRunSearchIdsForSearch(msSearchId);
    }
    
    private List<Integer> getAnalysisIdsForMsSearch(int msSearchId) {
        return analysisDao.getAnalysisIdsForSearch(msSearchId);
    }
    
    private List<Integer> getRunSearchAnalysisIdsForAnalysis(int analysisId) {
        org.yeastrc.ms.dao.DAOFactory factory = org.yeastrc.ms.dao.DAOFactory.instance();
        return factory.getMsRunSearchAnalysisDAO().getRunSearchAnalysisIdsForAnalysis(analysisId);
    }
    
    public int getJobCount(List<Integer> statusCodes) throws SQLException {
    	
    	int count = 0;
		
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		try {
			
			String sql = "SELECT COUNT(*) FROM tblJobs";
			sql += " WHERE type="+JobUtils.TYPE_PROTEINFER_RUN;
			if (statusCodes != null && statusCodes.size() > 0) {
				sql += " AND status IN (";
				int cnt = 0;
				for (int st : statusCodes) {
					if (cnt != 0) sql += ",";
					else cnt++;
					
					sql += st;
				}
				sql += ")";
			}
			
			conn = DBConnectionManager.getConnection(DBConnectionManager.JOB_QUEUE);
			stmt = conn.prepareStatement( sql );
			rs = stmt.executeQuery();
			
			while (rs.next()) {
				try {
					count = rs.getInt( 1 );
				} catch (Exception e) { ; }
			}
			
		} finally {
			
			if (rs != null) {
				try { rs.close(); rs = null; } catch (Exception e) { ; }
			}

			if (stmt != null) {
				try { stmt.close(); stmt = null; } catch (Exception e) { ; }
			}
			
			if (conn != null) {
				try { conn.close(); conn = null; } catch (Exception e) { ; }
			}			
		}
		return count;
    }
    
    public List<ProteinferJob> getJobs(List<Integer> statusCodes, int offset) throws SQLException {
    	
    	List<ProteinferJob> jobs = new ArrayList<ProteinferJob>();
		
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		try {
			
			String sql = "SELECT id FROM tblJobs WHERE type="+JobUtils.TYPE_PROTEINFER_RUN;
			if (statusCodes != null && statusCodes.size() > 0) {
				sql += " AND status IN (";
				int cnt = 0;
				for (int st : statusCodes) {
					if (cnt != 0) sql += ",";
					else cnt++;
					
					sql += st;
				}
				sql += ") ORDER BY id DESC LIMIT " +offset + ", 50";
			}
			
			conn = DBConnectionManager.getConnection(DBConnectionManager.JOB_QUEUE);
			stmt = conn.prepareStatement( sql );
			rs = stmt.executeQuery();
			
			while (rs.next()) {
				try {
					jobs.add(this.getJob( rs.getInt( "id" ) ) );
				} catch (Exception e) { ; }
			}
			
		} finally {
			
			if (rs != null) {
				try { rs.close(); rs = null; } catch (Exception e) { ; }
			}

			if (stmt != null) {
				try { stmt.close(); stmt = null; } catch (Exception e) { ; }
			}
			
			if (conn != null) {
				try { conn.close(); conn = null; } catch (Exception e) { ; }
			}			
		}
		
		return jobs;
    }
}
