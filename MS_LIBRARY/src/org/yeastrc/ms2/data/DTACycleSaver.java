/**
 * 
 */
package org.yeastrc.ms2.data;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * @author Mike
 *
 */
public class DTACycleSaver {

	private DTACycleSaver() { }
	
	/**
	 * Get an instance of this class
	 * @return
	 */
	public static DTACycleSaver getInstance() {
		return new DTACycleSaver();
	}
	
	/**
	 * Save the given cycle to the database.  This only saves new cycles, do not try to save changed, old ones
	 * @param cycle
	 * @return
	 * @throws Exception
	 */
	public int save( DTACycle cycle, Connection conn ) throws Exception {
		
		int id = 0;
		Statement stmt = null;
		ResultSet rs = null;
		
		if (cycle.getID() != 0)
			throw new Exception( "Cycle is already in the database." );
		
		try {

			stmt = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
			
			String sql = "SELECT * FROM tblYatesCycles WHERE cycleID = 0";
			rs = stmt.executeQuery( sql );
			
			rs.moveToInsertRow();
			
			rs.updateInt( "runID", cycle.getRunID() );
			rs.updateString( "cycleFileName", cycle.getFileName() );

			rs.insertRow();
			rs.last();
			
			id = rs.getInt( "cycleID" );
			cycle.setID( id );
			
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
		
		return id;
	}
	
}
