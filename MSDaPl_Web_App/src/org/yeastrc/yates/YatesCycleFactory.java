/*
 * YatesCycleFactory.java
 * Created on Oct 20, 2004
 * Created by Michael Riffle <mriffle@u.washington.edu>
 */

package org.yeastrc.yates;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.yeastrc.db.DBConnectionManager;

/**
 * Description of class goes here.
 * 
 * @author Michael Riffle <mriffle@u.washington.edu>
 * @version Oct 20, 2004
 */

public class YatesCycleFactory {

	public static YatesCycleFactory getInstance() {
		return new YatesCycleFactory();
	}
	
	/**
	 * Get a YatesCycle object based on the suplied filename and Yates Run
	 * @param filename
	 * @param run
	 * @return
	 * @throws Exception
	 */
	public YatesCycle getCycle(String filename, YatesRun run) throws Exception {
		YatesCycle retCycle = null;
		
		// Get our connection to the database.
		Connection conn = DBConnectionManager.getConnection("yrc");
		PreparedStatement stmt = null;
		ResultSet rs = null;
	
		try {
			// Our SQL statement
			String sqlStr = "SELECT cycleID FROM tblYatesCycles WHERE cycleFileName = ? AND runID = ?";
			stmt = conn.prepareStatement(sqlStr);
			stmt.setString(1, filename);
			stmt.setInt(2, run.getId());
			rs = stmt.executeQuery();

			// No rows returned.
			if( !rs.next() ) {
				return null;
			}
			
			retCycle = new YatesCycle();
			retCycle.load(rs.getInt(1));
			
			rs.close(); rs = null;
			stmt.close(); stmt = null;
			conn.close(); conn = null;
		}
		finally {

			// Always make sure result sets and statements are closed,
			// and the connection is returned to the pool
			if (rs != null) {
				try { rs.close(); } catch (SQLException e) { ; }
				rs = null;
			}
			if (stmt != null) {
				try { stmt.close(); } catch (SQLException e) { ; }
				stmt = null;
			}
			if (conn != null) {
				try { conn.close(); } catch (SQLException e) { ; }
				conn = null;
			}
		}
			
		
		return retCycle;
	}
	
	public boolean hasCyclesForRun(int runId) throws SQLException {
	 // Get our connection to the database.
        Connection conn = DBConnectionManager.getConnection("yrc");
        PreparedStatement stmt = null;
        ResultSet rs = null;
    
        try {
            // Our SQL statement
            String sqlStr = "SELECT cycleID FROM tblYatesCycles WHERE runID = ?";
            stmt = conn.prepareStatement(sqlStr);
            stmt.setInt(1, runId);
            rs = stmt.executeQuery();

            // No rows returned.
            if( !rs.next() ) {
                return false;
            }
            
            return true;
        }
        finally {

            // Always make sure result sets and statements are closed,
            // and the connection is returned to the pool
            if (rs != null) {
                try { rs.close(); } catch (SQLException e) { ; }
                rs = null;
            }
            if (stmt != null) {
                try { stmt.close(); } catch (SQLException e) { ; }
                stmt = null;
            }
            if (conn != null) {
                try { conn.close(); } catch (SQLException e) { ; }
                conn = null;
            }
        }
	}
}
