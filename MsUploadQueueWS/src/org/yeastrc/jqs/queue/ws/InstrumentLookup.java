/**
 * InstrumentLookup.java
 * @author Vagisha Sharma
 * Sep 22, 2010
 */
package org.yeastrc.jqs.queue.ws;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.yeastrc.db.DBConnectionManager;

/**
 * 
 */
public class InstrumentLookup {

	private static InstrumentLookup instance = new InstrumentLookup();
	
	private InstrumentLookup() {}
	
	public static InstrumentLookup getInstance() {
		return instance;
	}
	
	public int forName(String instrumentName) throws SQLException {
		
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		
		try {
			
			conn = DBConnectionManager.getConnection(DBConnectionManager.MS_DATA);
			String sql = "SELECT id FROM msInstrument WHERE name = \""+instrumentName+"\"";
			stmt = conn.createStatement();
			rs = stmt.executeQuery( sql );
			
			if(rs.next()) {
				return rs.getInt(1);
			}
			
			return 0;
			
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
	
	public String nameForId(int instrumentId) throws SQLException {
		
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		
		try {
			
			conn = DBConnectionManager.getConnection(DBConnectionManager.MS_DATA);
			String sql = "SELECT name FROM msInstrument WHERE id = \""+instrumentId+"\"";
			stmt = conn.createStatement();
			rs = stmt.executeQuery( sql );
			
			if(rs.next()) {
				return rs.getString(1);
			}
			
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
	
}
