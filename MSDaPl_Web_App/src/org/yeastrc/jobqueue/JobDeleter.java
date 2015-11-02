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
public class JobDeleter {

	// private constructor
	private JobDeleter() { }
	
	/**
	 * Get an instance of this class
	 * @return
	 */
	public static JobDeleter getInstance() {
		return new JobDeleter();
	}
	
	/**
	 * Delete the supplied job from the database
	 * @param job
	 * @throws Exception
	 */
	public boolean deleteJob( Job job ) throws Exception {
		
		// do not delete jobs that are being processed
		if (job.getStatus() == JobUtils.STATUS_OUT_FOR_WORK)
			return false;
		
		Connection conn = null;
		PreparedStatement stmt = null;
		
		try {
			
			String sql = "DELETE FROM tblJobs WHERE id = ?";
			conn = DBConnectionManager.getConnection(DBConnectionManager.JOB_QUEUE);
			stmt = conn.prepareStatement( sql );
			stmt.setInt( 1, job.getId() );
			stmt.executeUpdate();
			
			return true;
			
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
