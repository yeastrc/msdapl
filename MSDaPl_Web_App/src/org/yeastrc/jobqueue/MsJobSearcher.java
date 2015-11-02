/**
 * 
 */
package org.yeastrc.jobqueue;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import org.yeastrc.db.DBConnectionManager;

/**
 * @author Mike
 *
 */
public class MsJobSearcher {

	/**
	 * Get the number of jobs in the queue
	 * @return
	 * @throws Exception
	 */
	public int getJobCount() throws Exception {
		int count = 0;
		
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		try {
			
			String sql = "SELECT COUNT(*) FROM tblJobs";
			sql += " WHERE (type="+JobUtils.TYPE_MASS_SPEC_UPLOAD+" OR type="+JobUtils.TYPE_ANALYSIS_UPLOAD
			+" OR type="+JobUtils.TYPE_PERC_EXE+")";
			if (this.status != null && this.status.size() > 0) {
				sql += " AND status IN (";
				int cnt = 0;
				for (int st : this.status) {
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
			
			rs.close(); rs = null;
			stmt.close(); stmt = null;
			conn.close(); conn = null;
			
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
	
	/**
	 * Get all jobs in the database with the supplied status
	 * @return
	 * @throws Exception
	 */
	public List<Job> getJobs() throws Exception {
		List<Job> jobs = new ArrayList<Job>();
		
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		try {
			
			String sql = "SELECT id FROM tblJobs ";
			sql += " WHERE (type="+JobUtils.TYPE_MASS_SPEC_UPLOAD+" OR type="+JobUtils.TYPE_ANALYSIS_UPLOAD
			+" OR type="+JobUtils.TYPE_PERC_EXE+")";
			
			if (this.status != null && this.status.size() > 0) {
				sql += " AND status IN (";
				int cnt = 0;
				for (int st : this.status) {
					if (cnt != 0) sql += ",";
					else cnt++;
					
					sql += st;
				}
				sql += ") ORDER BY id DESC LIMIT " + this.offset + ", 50";
			}
			
			conn = DBConnectionManager.getConnection(DBConnectionManager.JOB_QUEUE);
			stmt = conn.prepareStatement( sql );
			rs = stmt.executeQuery();
			
			while (rs.next()) {
				try {
					jobs.add( MSJobFactory.getInstance().getJobLite( rs.getInt( "id" ) ) );
				} catch (Exception e) { ; }
			}
			
			rs.close(); rs = null;
			stmt.close(); stmt = null;
			conn.close(); conn = null;
			
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

	/**
	 * Add the supplied status as a search constraint
	 * @param status
	 */
	public void addStatus( int status ) {
		if (this.status == null)
			this.status = new ArrayList<Integer>();
		
		if (this.status.contains( status ))
			return;
		
		this.status.add( status );
	}
	
	
	
	/**
	 * @return the index
	 */
	public int getOffset() {
		return offset;
	}

	/**
	 * @param index the index to set
	 */
	public void setOffset(int index) {
		this.offset = index;
	}

	/**
	 * Get status
	 * @return
	 */
	public List<Integer> getStatus() { return this.status; }
	
	private List<Integer> status;
	private int offset;

}
