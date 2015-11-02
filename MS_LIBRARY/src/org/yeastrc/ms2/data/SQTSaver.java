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
public class SQTSaver {

	private SQTSaver() { }
	
	/**
	 * Get an instance of this class
	 * @return
	 */
	public static SQTSaver getInstance() {
		return new SQTSaver();
	}

	public int save( SQT sqt, Connection conn ) throws Exception {
		int id = 0;
		Statement stmt = null;
		ResultSet rs = null;
		
		try {
			stmt = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
			
			String sql = "SELECT * FROM tblYatesCycleSQTData WHERE cycleID = " + sqt.getCycleID();
			rs = stmt.executeQuery( sql );
			
			rs.moveToInsertRow();
			
			rs.updateInt( "cycleID", sqt.getCycleID() );
			
			try {
				rs.updateBytes( "data", sqt.getSqtData() );
			} catch (Exception e) {
				throw new SQLException ("Error compressing SQT data: " + e.getMessage());
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
