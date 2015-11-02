/**
 * 
 */
package org.yeastrc.jobqueue;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.yeastrc.data.InvalidIDException;
import org.yeastrc.db.DBConnectionManager;
import org.yeastrc.ms.domain.protinfer.ProteinInferenceProgram;
import org.yeastrc.www.proteinfer.job.ProgramParameters;
import org.yeastrc.www.proteinfer.job.ProgramParameters.Param;
import org.yeastrc.www.upload.PercolatorRunForm.PercolatorInputFile;

/**
 * @author Mike
 *
 */
public class MSJobFactory {

    private static MSJobFactory instance;
    
	private MSJobFactory() { }
	
	/**
	 * Get an instance of this class
	 * @return
	 */
	public static MSJobFactory getInstance() {
	    if(instance == null)
	        instance = new MSJobFactory();
	    return instance;
	}
	
	
	
	/**
	 * Get the given Job from the database
	 * @param jobID
	 * @return
	 * @throws Exception
	 */
	public Job getJob( int jobID) throws SQLException, InvalidIDException {
		return getJob(jobID, true);
	}
	
	/**
	 * Get the given Job from the database. Returns a "Liter-re" version of PercolatorJob
	 * @param jobID
	 * @return
	 * @throws Exception
	 */
	public Job getJobLite( int jobID) throws SQLException, InvalidIDException {
		return getJob(jobID, false);
	}
	
	private Job getJob( int jobID, boolean fullJob ) throws SQLException, InvalidIDException {
		
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		try {
			
			conn = DBConnectionManager.getConnection(DBConnectionManager.JOB_QUEUE);
			String sql = "SELECT submitter, type, submitDate, lastUpdate, status, attempts, log FROM tblJobs WHERE id = ?";
			stmt = conn.prepareStatement( sql );
			stmt.setInt( 1, jobID );
			rs = stmt.executeQuery();
			
			if ( !rs.next() )
				throw new InvalidIDException( "Invalid Job ID: " + jobID );
			
			if(rs.getInt("type") == JobUtils.TYPE_MASS_SPEC_UPLOAD) {
				return getMSJob(jobID, conn, stmt, rs);
			}
			else if(rs.getInt("type") == JobUtils.TYPE_ANALYSIS_UPLOAD) {
				return getMsAnalysisUploadJob(jobID, conn, stmt, rs);
			}
			else if(rs.getInt("type") == JobUtils.TYPE_PERC_EXE) {
				if(fullJob)
					return getPercolatorJob(jobID, conn, stmt, rs);
				else
					return getPercolatorJobLite(jobID, conn, stmt, rs);
			}
			else
				return null;
			
			
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
	}

	private MSJob getMSJob(int jobID, Connection conn, PreparedStatement stmt, ResultSet rs) throws SQLException, InvalidIDException {
		
		MSJob job;
		job = new MSJob();
		job.setId( jobID );
		job.setSubmitter( rs.getInt( "submitter" ) );
		job.setType( rs.getInt( "type" ) );
		job.setSubmitDate( rs.getDate( "submitDate" ) );
		job.setLastUpdate( rs.getDate( "lastUpdate" ) );
		job.setStatus( rs.getInt( "status" ) );
		job.setAttempts( rs.getInt( "attempts" ) );
		job.setLog( rs.getString( "log" ) );
		
		
		rs.close(); rs = null;
		stmt.close(); stmt = null;
		
		String sql = "SELECT * FROM tblMSJobs WHERE jobID = ?";
		stmt = conn.prepareStatement( sql );
		stmt.setInt( 1, jobID );
		rs = stmt.executeQuery();

		if (!rs.next())
			throw new InvalidIDException( "Invalid job ID on MSJob.getJob().  Job ID: " + jobID );

		job.setProjectID( rs.getInt( "projectID" ) );
		job.setServerDirectory( rs.getString( "serverDirectory" ) );
		job.setRunDate( rs.getDate( "runDate" ) );
		job.setBaitProtein( rs.getInt( "baitProtein" ) );
		job.setBaitProteinDescription( rs.getString( "baitDescription" ) );
		job.setTargetSpecies( rs.getInt( "targetSpecies" ) );
		job.setComments( rs.getString( "comments" ) );
		job.setRunID( rs.getInt( "runID" ) );
		job.setExperimentID( rs.getInt( "experimentID" ) );
		job.setGroup( rs.getInt( "groupID" ) );
		job.setPipeline(rs.getString("pipeline"));
		job.setInstrumentId(rs.getInt("instrumentID"));
		
		return job;
	}
	
	private MsAnalysisUploadJob getMsAnalysisUploadJob(int jobID, Connection conn, PreparedStatement stmt, ResultSet rs) throws SQLException, InvalidIDException {
	
		MsAnalysisUploadJob job;
		job = new MsAnalysisUploadJob();
		job.setId( jobID );
		job.setSubmitter( rs.getInt( "submitter" ) );
		job.setType( rs.getInt( "type" ) );
		job.setSubmitDate( rs.getDate( "submitDate" ) );
		job.setLastUpdate( rs.getDate( "lastUpdate" ) );
		job.setStatus( rs.getInt( "status" ) );
		job.setAttempts( rs.getInt( "attempts" ) );
		job.setLog( rs.getString( "log" ) );
		
		
		rs.close(); rs = null;
		stmt.close(); stmt = null;
		
		String sql = "SELECT * FROM tblMSAnalysisUploadJobs WHERE jobID = ?";
		stmt = conn.prepareStatement( sql );
		stmt.setInt( 1, jobID );
		rs = stmt.executeQuery();

		if (!rs.next())
			throw new InvalidIDException( "Invalid job ID on MSJob.getMsAnalysisUploadJob().  Job ID: " + jobID );

		job.setProjectID( rs.getInt( "projectID" ) );
		job.setExperimentID( rs.getInt( "experimentID" ) );
		job.setSearchAnalysisId( rs.getInt( "searchAnalysisID" ) );
		job.setServerDirectory( rs.getString( "serverDirectory" ) );
		job.setComments( rs.getString( "comments" ) );
		
		return job;
	}
	
	private PercolatorJob getPercolatorJobLite(int jobID, Connection conn, PreparedStatement stmt, ResultSet rs) throws SQLException, InvalidIDException {
		
		PercolatorJob job;
		job = new PercolatorJob();
		job.setId( jobID );
		job.setSubmitter( rs.getInt( "submitter" ) );
		job.setType( rs.getInt( "type" ) );
		job.setSubmitDate( rs.getDate( "submitDate" ) );
		job.setLastUpdate( rs.getDate( "lastUpdate" ) );
		job.setStatus( rs.getInt( "status" ) );
		job.setAttempts( rs.getInt( "attempts" ) );
		job.setLog( rs.getString( "log" ) );
		
		
		rs.close(); rs = null;
		stmt.close(); stmt = null;
		
		String sql = "SELECT * FROM tblPercolatorJobs WHERE jobID = ?";
		stmt = conn.prepareStatement( sql );
		stmt.setInt( 1, jobID );
		rs = stmt.executeQuery();

		if (!rs.next())
			throw new InvalidIDException( "Invalid job ID on MSJob.getPercolatorJobLite().  Job ID: " + jobID );

		job.setProjectID( rs.getInt( "projectID" ) );
		job.setExperimentID( rs.getInt( "experimentID" ) );
		job.setSearchId( rs.getInt( "searchID" ) );
		job.setResultDirectory( rs.getString( "resultDirectory" ) );
		int runProtInfer = rs.getInt("runProteinInference");
		if(runProtInfer == 0)
			job.setRunProteinInference(false);
		else
			job.setRunProteinInference(true);
		job.setComments( rs.getString( "comments" ) );
		
		return job;
	}
	
	private PercolatorJob getPercolatorJob(int jobID, Connection conn, PreparedStatement stmt, ResultSet rs) throws SQLException, InvalidIDException {
		
		PercolatorJob job;
		job = new PercolatorJob();
		job.setId( jobID );
		job.setSubmitter( rs.getInt( "submitter" ) );
		job.setType( rs.getInt( "type" ) );
		job.setSubmitDate( rs.getDate( "submitDate" ) );
		job.setLastUpdate( rs.getDate( "lastUpdate" ) );
		job.setStatus( rs.getInt( "status" ) );
		job.setAttempts( rs.getInt( "attempts" ) );
		job.setLog( rs.getString( "log" ) );
		
		
		rs.close(); rs = null;
		stmt.close(); stmt = null;
		
		String sql = "SELECT * FROM tblPercolatorJobs WHERE jobID = ?";
		stmt = conn.prepareStatement( sql );
		stmt.setInt( 1, jobID );
		rs = stmt.executeQuery();

		if (!rs.next())
			throw new InvalidIDException( "Invalid job ID on MSJob.getPercolatorJob().  Job ID: " + jobID );

		job.setProjectID( rs.getInt( "projectID" ) );
		job.setExperimentID( rs.getInt( "experimentID" ) );
		job.setSearchId( rs.getInt( "searchID" ) );
		job.setResultDirectory( rs.getString( "resultDirectory" ) );
		int runProtInfer = rs.getInt("runProteinInference");
		if(runProtInfer == 0)
			job.setRunProteinInference(false);
		else
			job.setRunProteinInference(true);
		job.setComments( rs.getString( "comments" ) );
		
		
		sql = "SELECT * FROM tblPercolatorJobInput WHERE jobID=? ORDER BY runSearchID";
		stmt = conn.prepareStatement( sql );
		stmt.setInt( 1, jobID );
		rs = stmt.executeQuery();
		List<PercolatorInputFile> percInputFiles = new ArrayList<PercolatorInputFile>();
		while(rs.next()) {
			PercolatorInputFile iFile = new PercolatorInputFile();
			iFile.setIsSelected(true);
			iFile.setRunSearchId(rs.getInt("runSearchID"));
			iFile.setRunName(rs.getString("fileName"));
			percInputFiles.add(iFile);
		}
		job.setPercolatorInputFiles(percInputFiles);
		
		if(job.isRunProteinInference()) {
			ProgramParameters params = new ProgramParameters(ProteinInferenceProgram.PROTINFER_PERC_PEPT);
			Map<String, Param> paramMap = new HashMap<String, Param>();
			for(Param param: params.getParamList()) {
				paramMap.put(param.getName(), param);
			}
			
			sql = "SELECT * FROM tblPercolatorToProteinInferParams WHERE jobID=?";
			stmt = conn.prepareStatement(sql);
			stmt.setInt( 1, jobID );
			rs = stmt.executeQuery();
			while(rs.next()) {
				String name = rs.getString("name");
				String value = rs.getString("value");
				Param param = paramMap.get(name);
				param.setValue(value);
			}
			
			job.setProgramParams(params);
		}
		
		return job;
	}
	
	/**
     * Get the MSJob from the database for the given projectId and experimentId
     * @param experimentId
     * @return
     * @throws Exception
     */
    public MSJob getMsJobForProjectExperiment( int projectId, int experimentId ) throws Exception {
        
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        MSJob job = null;
        
        try {
            
            conn = DBConnectionManager.getConnection(DBConnectionManager.JOB_QUEUE);
            String sql = "SELECT * from tblJobs AS j, tblMSJobs AS mj WHERE mj.projectID=? AND mj.experimentID=? AND j.id = mj.jobID";
            stmt = conn.prepareStatement( sql );
            stmt.setInt(1, projectId);
            stmt.setInt( 2, experimentId );
            rs = stmt.executeQuery();
            
            if ( !rs.next() )
                throw new Exception( "No MSJobs found for experimentID: " + experimentId );
            
            job = new MSJob();
            job.setId(rs.getInt("id"));
            job.setSubmitter( rs.getInt( "submitter" ) );
            job.setType( rs.getInt( "type" ) );
            job.setSubmitDate( rs.getDate( "submitDate" ) );
            job.setLastUpdate( rs.getDate( "lastUpdate" ) );
            job.setStatus( rs.getInt( "status" ) );
            job.setAttempts( rs.getInt( "attempts" ) );
            job.setLog( rs.getString( "log" ) );
            
            job.setProjectID( rs.getInt( "projectID" ) );
            job.setServerDirectory( rs.getString( "serverDirectory" ) );
            job.setRunDate( rs.getDate( "runDate" ) );
            job.setBaitProtein( rs.getInt( "baitProtein" ) );
            job.setBaitProteinDescription( rs.getString( "baitDescription" ) );
            job.setTargetSpecies( rs.getInt( "targetSpecies" ) );
            job.setComments( rs.getString( "comments" ) );
            job.setRunID( rs.getInt( "runID" ) );
            job.setGroup( rs.getInt( "groupID" ) );
            job.setPipeline(rs.getString("pipeline"));
            job.setInstrumentId(rs.getInt("instrumentID"));
            
            
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
        
        return job;
    }
    
    
    /**
     * Get the MsAnalysisUploadJob from the database for the given and searchAnalysisId
     * @param searchAnalysisId
     * @return
     * @throws Exception
     */
    public MsAnalysisUploadJob getJobForAnalysis( int searchAnalysisId ) throws Exception {
        
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        MsAnalysisUploadJob job = null;
        
        try {
            
            conn = DBConnectionManager.getConnection(DBConnectionManager.JOB_QUEUE);
            String sql = "SELECT * from tblJobs AS j, tblMSAnalysisUploadJobs AS mj WHERE mj.searchAnalysisID=? AND j.id = mj.jobID";
            stmt = conn.prepareStatement( sql );
            stmt.setInt( 1, searchAnalysisId );
            rs = stmt.executeQuery();
            
            if ( !rs.next() )
                throw new Exception( "No MsAnalysisUploadJobs found for searchAnalysisID: " + searchAnalysisId );
            
            job = new MsAnalysisUploadJob();
            job.setId(rs.getInt("id"));
            job.setSubmitter( rs.getInt( "submitter" ) );
            job.setType( rs.getInt( "type" ) );
            job.setSubmitDate( rs.getDate( "submitDate" ) );
            job.setLastUpdate( rs.getDate( "lastUpdate" ) );
            job.setStatus( rs.getInt( "status" ) );
            job.setAttempts( rs.getInt( "attempts" ) );
            job.setLog( rs.getString( "log" ) );
            
            job.setProjectID( rs.getInt( "projectID" ) );
            job.setExperimentID( rs.getInt( "experimentID" ) );
            job.setSearchAnalysisId( rs.getInt( "searchAnalysisID" ) );
            job.setServerDirectory( rs.getString( "serverDirectory" ) );
            job.setComments( rs.getString( "comments" ) );
            
            
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
        
        return job;
    }
    
    
    /**
     * Get the MSJob from the database for the given experimentId
     * @param experimentId
     * @return
     * @throws Exception
     */
    public MSJob getMsJobForExperiment( int experimentId ) throws Exception {
        
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        MSJob job = null;
        
        try {
            
            conn = DBConnectionManager.getConnection(DBConnectionManager.JOB_QUEUE);
            String sql = "SELECT * from tblJobs AS j, tblMSJobs AS mj WHERE mj.experimentID=? AND j.id = mj.jobID";
            stmt = conn.prepareStatement( sql );
            stmt.setInt( 1, experimentId );
            rs = stmt.executeQuery();
            
            if ( !rs.next() )
                throw new Exception( "No MSJobs found for experimentID: " + experimentId );
            
            job = new MSJob();
            job.setId(rs.getInt("id"));
            job.setSubmitter( rs.getInt( "submitter" ) );
            job.setType( rs.getInt( "type" ) );
            job.setSubmitDate( rs.getDate( "submitDate" ) );
            job.setLastUpdate( rs.getDate( "lastUpdate" ) );
            job.setStatus( rs.getInt( "status" ) );
            job.setAttempts( rs.getInt( "attempts" ) );
            job.setLog( rs.getString( "log" ) );
            
            job.setProjectID( rs.getInt( "projectID" ) );
            job.setServerDirectory( rs.getString( "serverDirectory" ) );
            job.setRunDate( rs.getDate( "runDate" ) );
            job.setBaitProtein( rs.getInt( "baitProtein" ) );
            job.setBaitProteinDescription( rs.getString( "baitDescription" ) );
            job.setTargetSpecies( rs.getInt( "targetSpecies" ) );
            job.setComments( rs.getString( "comments" ) );
            job.setRunID( rs.getInt( "runID" ) );
            job.setGroup( rs.getInt( "groupID" ) );
            
            
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
        
        return job;
    }
	
	
}
