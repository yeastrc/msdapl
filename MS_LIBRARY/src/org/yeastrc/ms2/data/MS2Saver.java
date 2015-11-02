/**
 * 
 */
package org.yeastrc.ms2.data;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * @author Mike
 *
 */
public class MS2Saver {

	private MS2Saver() { }
	
	/**
	 * Get an instance of this class
	 * @return
	 */
	public static MS2Saver getInstance() {
		return new MS2Saver();
	}
	
	
	public int save( MS2 ms2, Connection conn ) throws Exception {
		int id = 0;
		Statement stmt = null;
		ResultSet rs = null;
		
		try {

			stmt = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
			
			String sql = "SELECT * FROM tblYatesCycleMS2Data WHERE cycleID = " + ms2.getCycleID();
			rs = stmt.executeQuery( sql );
			
			rs.moveToInsertRow();
			
			rs.updateInt( "cycleID", ms2.getCycleID() );
			
			try {
				rs.updateBytes( "data", ms2.getMs2Data() );
			} catch (Exception e) {
				throw new SQLException ("Error compressing MS2 data: " + e.getMessage());
			}
			
			rs.insertRow();
			rs.last();
			
			id = rs.getInt( "cycleID" );
			
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
		
		
		return id;
	}
	
	
}
