/**
 * 
 */
package org.yeastrc.jobqueue;

import java.sql.Connection;
import java.sql.PreparedStatement;

import org.yeastrc.db.DBConnectionManager;

/**
 * @author Mike
 *
 */
public class JobResetter {

	// private constructor
	private JobResetter() { }
	
	/**
	 * Get an instance of this class
	 * @return
	 */
	public static JobResetter getInstance() {
		return new JobResetter();
	}
	
	/**
	 * Reset the supplied job in the database (reset its status to "queued")
	 * @param job
	 * @throws Exception
	 */
	public void resetJob( Job job ) throws Exception {
		
		Connection conn = null;
		PreparedStatement stmt = null;
		
		try {
			
			String sql = "UPDATE tblJobs SET status = ? WHERE id = ?";
			conn = DBConnectionManager.getConnection(DBConnectionManager.JOB_QUEUE);
			stmt = conn.prepareStatement( sql );
			stmt.setInt( 1, JobUtils.STATUS_QUEUED );
			stmt.setInt( 2, job.getId() );
			
			stmt.executeUpdate();
			
			job.setStatus( JobUtils.STATUS_QUEUED );
			
			stmt.close(); stmt = null;
			conn.close(); conn = null;
			
		} finally {

			if (stmt != null) {
				try { stmt.close(); stmt = null; } catch (Exception e) { ; }
			}
			
			if (conn != null) {
				try { conn.close(); conn = null; } catch (Exception e) { ; }
			}			
		}
		
	}
	
	
}
