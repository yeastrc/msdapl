/**
 * 
 */
package org.yeastrc.www.upload;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import org.yeastrc.db.DBConnectionManager;
import org.yeastrc.jobqueue.JobUtils;

/**
 * MsAnalysisUploadJobSaver.java
 * @author Vagisha Sharma
 * Oct 27, 2010
 * 
 */
public class MsAnalysisUploadJobSaver {

	public int savetoDatabase() throws Exception {
	
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		
		try {
			
			conn = DBConnectionManager.getConnection(DBConnectionManager.JOB_QUEUE);
			String sql = "SELECT * FROM tblJobs WHERE id = 0";
			stmt = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
			
			rs = stmt.executeQuery( sql );
			rs.moveToInsertRow();
			
			rs.updateInt( "submitter", this.submitter );
			rs.updateInt( "type", JobUtils.TYPE_ANALYSIS_UPLOAD );
			rs.updateDate( "submitDate", new java.sql.Date( (new java.util.Date()).getTime() ) );
			rs.updateInt( "status", 0 );
			rs.updateInt( "attempts", 0 );
			
			rs.insertRow();
			rs.last();
			
			int id = rs.getInt( "id" );
			
			rs.close(); rs = null;
			stmt.close(); stmt = null;
			
			sql = "SELECT * FROM tblMSAnalysisUploadJobs WHERE jobID = 0";
			stmt = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
			
			rs = stmt.executeQuery( sql );
			rs.moveToInsertRow();
			
			rs.updateInt( "jobID", id );
			rs.updateInt( "projectID", this.projectId );
			rs.updateString( "serverDirectory", this.serverDirectory );
			rs.updateString( "comments", this.comments );
			rs.updateInt("experimentID", this.experimentId);
			if(this.searchAnalysisId != 0)
				rs.updateInt("searchAnalysisID", this.searchAnalysisId);
			else
				rs.updateNull("searchAnalysisID");
			rs.updateInt("runProteinInference", 0);
			rs.insertRow();
			
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

	private int submitter;
	private int projectId;
	private int experimentId;
	private int searchAnalysisId;
	private String serverDirectory;
	private String comments;
	

	/**
	 * @return the comments
	 */
	public String getComments() {
		return comments;
	}
	/**
	 * @param comments the comments to set
	 */
	public void setComments(String comments) {
		this.comments = comments;
	}
	
	/**
	 * @return the projectID
	 */
	public int getProjectId() {
		return projectId;
	}
	/**
	 * @param projectID the projectID to set
	 */
	public void setProjectId(int projectId) {
		this.projectId = projectId;
	}
	/**
	 * @return the serverDirectory
	 */
	public String getServerDirectory() {
		return serverDirectory;
	}
	/**
	 * @param serverDirectory the serverDirectory to set
	 */
	public void setServerDirectory(String serverDirectory) {
		this.serverDirectory = serverDirectory;
	}
	/**
	 * @return the submitter
	 */
	public int getSubmitter() {
		return submitter;
	}
	/**
	 * @param submitter the submitter to set
	 */
	public void setSubmitter(int submitter) {
		this.submitter = submitter;
	}
	
	public int getExperimentId() {
		return experimentId;
	}
	
	public void setExperimentId(int experimentId) {
		this.experimentId = experimentId;
	}
	public void setSearchAnalysisId(int searchAnalysisId) {
		this.searchAnalysisId = searchAnalysisId;
	}
}
