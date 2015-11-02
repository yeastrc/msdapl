/*
 * YatesUtils.java
 * Created on Sep 3, 2004
 * Created by Michael Riffle <mriffle@u.washington.edu>
 */

package org.yeastrc.yates;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.SQLException;

import org.yeastrc.db.DBConnectionManager;
import org.yeastrc.utils.*;
import java.io.*;

/**
 * Description of class goes here.
 * 
 * @author Michael Riffle <mriffle@u.washington.edu>
 * @version Sep 3, 2004
 */

public class YatesUtils {

	/**
	 * Check to see if the given run has data for the given DTASelect data type, w/o having to select
	 * all of that information from the database.
	 * @param run The YatesRun to check
	 * @param type The DTASelect data type (DTASelectFilterTXT, DTASelectTXT, DTASelectHTML)
	 * @return true if it has it, false if not
	 * @throws Exception If there is a problem
	 */
	protected static boolean runContainsDTAData(YatesRun run, String type) throws Exception {

		// Get our connection to the database.
		Connection conn = DBConnectionManager.getConnection("yrc");
		Statement stmt = null;
		ResultSet rs = null;

		try {
			stmt = conn.createStatement();

			// Our SQL statement
			String sqlStr = "SELECT LENGTH(" + type + ") FROM tblYatesRun WHERE id = " + run.getId();

			// Our results
			rs = stmt.executeQuery(sqlStr);
			if (!rs.next()) return false;
			if (rs.getInt(1) < 1) return false;
			
			// Close up shop
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
		
		return true;
	}
	
	/**
	 * Returns the text for the supplied type of DTA Select field for the supplied run.
	 * @param run The YatesRun for which we want DTA Select info
	 * @param type The type of DTA Select info, valid arguments are ("DTASelectTXT", "DTASelectFilterTXT", "DTASelectHTML", and "DTASelectParams");
	 * @return The text for this type of DTASelect info for this Run, null if no data is found
	 * @throws SQLException If there is a database problem
	 */
	protected static String getDTADataForRun(YatesRun run, String type) throws SQLException, Exception {
		String retString = null;
		
		// Get our connection to the database.
		Connection conn = DBConnectionManager.getConnection("yrc");
		Statement stmt = null;
		ResultSet rs = null;

		try {
			stmt = conn.createStatement();

			// Our SQL statement
			String sqlStr = "SELECT " + type + " FROM tblYatesRun WHERE id = " + run.getId();

			// Our results
			rs = stmt.executeQuery(sqlStr);
			
			if (rs.next()) {
				if (type.equals("DTASelectTXT")) {
					Reader reader = new InputStreamReader ( Decompresser.getInstance().decompressString(rs.getBytes(1)) );
					StringBuffer sb = new StringBuffer();
					int buffSize = 8172;
					int charsRead = 0;
					
					while (charsRead != -1) {
						char[] chars = new char[buffSize];
						charsRead = reader.read(chars);
						if (charsRead > 0) {
							if (charsRead == buffSize)
								sb.append(chars);
							else
								sb.append(chars, 0, charsRead);
						} else {
							break;
						}
					}
					
					retString = sb.toString();
					sb = null;
					reader = null;
					System.gc();
				} else {
					retString = rs.getString(1);
				}
			}
			
			// Close up shop
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

		return retString;	
	}
}
