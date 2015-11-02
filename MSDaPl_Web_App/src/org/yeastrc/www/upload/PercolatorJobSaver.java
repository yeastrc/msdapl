/**
 * 
 */
package org.yeastrc.www.upload;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.yeastrc.db.DBConnectionManager;
import org.yeastrc.jobqueue.JobUtils;
import org.yeastrc.jobqueue.PercolatorJob;
import org.yeastrc.www.proteinfer.job.ProgramParameters;
import org.yeastrc.www.proteinfer.job.ProgramParameters.Param;
import org.yeastrc.www.upload.PercolatorRunForm.PercolatorInputFile;

/**
 * PercolatorJobSaver.java
 * @author Vagisha Sharma
 * Oct 27, 2010
 * 
 */
public class PercolatorJobSaver {

	private static PercolatorJobSaver instance = null;
	
	private PercolatorJobSaver() {}
	
	public static PercolatorJobSaver getInstance() {
		if(instance == null)
			instance = new PercolatorJobSaver();
		return instance;
	}
	
	/**
	 * Save this Percolator job to the database
	 * @param job
	 * @return
	 * @throws Exception
	 */
	public int save( PercolatorJob job ) throws Exception {
		
		
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		
		try {
			
			conn = DBConnectionManager.getConnection(DBConnectionManager.JOB_QUEUE);
			String sql = "SELECT * FROM tblJobs WHERE id = 0";
			stmt = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
			
			rs = stmt.executeQuery( sql );
			rs.moveToInsertRow();
			
			rs.updateInt( "submitter", job.getSubmitter() );
			rs.updateInt( "type", JobUtils.TYPE_PERC_EXE);
			rs.updateDate( "submitDate", new java.sql.Date( (new java.util.Date()).getTime() ) );
			rs.updateInt( "status", 0 );
			rs.updateInt( "attempts", 0 );
			
			rs.insertRow();
			rs.last();
			
			int id = rs.getInt( "id" );
			job.setId(id);
			
			rs.close(); rs = null;
			stmt.close(); stmt = null;
			
			
			conn = DBConnectionManager.getConnection(DBConnectionManager.JOB_QUEUE);
			stmt = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
			
			sql = "SELECT * FROM tblPercolatorJobs WHERE jobID = 0";
			rs = stmt.executeQuery( sql );
			
			rs.moveToInsertRow();
			
			rs.updateInt( "jobID", job.getId() );
			rs.updateInt( "projectID", job.getProjectID() );
			rs.updateInt( "experimentID", job.getExperimentID() );
			rs.updateInt( "searchID", job.getSearchId() );
			rs.updateString( "resultDirectory", job.getResultDirectory() );
			if(job.isRunProteinInference())
				rs.updateInt("runProteinInference", 1);
			else
				rs.updateInt("runProteinInference", 0);
			rs.updateString( "comments", job.getComments() );
			
			
			rs.insertRow();
			
			
			saveInputFileInfo(job);
			
			if(job.isRunProteinInference()) {
				saveProtInferParams(job);
			}
			
			return id;
			
			
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
	
	private void saveProtInferParams(PercolatorJob job) throws SQLException {
		
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		try {
			
			conn = DBConnectionManager.getConnection(DBConnectionManager.JOB_QUEUE);
			String sql = "INSERT INTO tblPercolatorToProteinInferParams (jobID, name, value) VALUES(?,?,?)";
			stmt = conn.prepareStatement(sql);
			
			ProgramParameters params = job.getProgramParams();
		
			for(Param param: params.getParamList()) {
				stmt.setInt(1, job.getId());
				stmt.setString(2, param.getName());
				stmt.setString(3, param.getValue());
				stmt.execute();
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
	}

	private void saveInputFileInfo(PercolatorJob job) throws SQLException {
		
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		try {
			
			conn = DBConnectionManager.getConnection(DBConnectionManager.JOB_QUEUE);
			String sql = "INSERT INTO tblPercolatorJobInput VALUES(?,?,?)";
			stmt = conn.prepareStatement(sql);
			
			for(PercolatorInputFile percInput: job.getPercolatorInputFiles()) {
				stmt.setInt(1, job.getId());
				stmt.setInt(2, percInput.getRunSearchId());
				stmt.setString(3, percInput.getRunName());
				stmt.execute();
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
		
	}
}
