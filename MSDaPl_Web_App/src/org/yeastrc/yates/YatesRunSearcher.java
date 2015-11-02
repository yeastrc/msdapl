/*
 * YatesRunSearcher.java
 * Created on Sep 9, 2004
 * Created by Michael Riffle <mriffle@u.washington.edu>
 */

package org.yeastrc.yates;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.yeastrc.data.InvalidIDException;
import org.yeastrc.db.DBConnectionManager;
import org.yeastrc.nr_seq.*;
/**
 * Description of class goes here.
 * 
 * @author Michael Riffle <mriffle@u.washington.edu>
 * @version Sep 9, 2004
 */

public class YatesRunSearcher {

	/**
	 * Performs the search of the YatesRun table, based on the search parameters set via method calls such as setProjectID()
	 * @return A List of the matching Screen objects
	 * @throws SQLException if there is a database error
	 */
	public List<YatesRun> search() throws SQLException, InvalidIDException, Exception {
		List<YatesRun> retList = new ArrayList<YatesRun>();
		boolean haveSearchTerm = false;
		
		// Get our connection to the database.
		Connection conn = DBConnectionManager.getConnection("yrc");
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		try {
			// Our SQL statement
			String sqlStr =  "SELECT run.id FROM tblYatesRun AS run";
			
			if (this.protein != null) {
				sqlStr += " INNER JOIN tblYatesRunResult AS result ON run.id = result.runID";
			}
		
			if (this.getProjectID() != 0) {
				haveSearchTerm = true;
				sqlStr += " WHERE run.projectID = " + this.getProjectID();
			}
			
			if (this.protein != null) {
				if (haveSearchTerm)
					sqlStr += " AND";
				else 
					sqlStr += " WHERE";
				
				haveSearchTerm = true;
				sqlStr += " result.hitProteinID = " + this.protein.getId();
			}
			
			
			if (this.mostRecent)
				sqlStr += " ORDER BY runDate DESC";
			else 
				sqlStr += " ORDER BY run.id";

			
			if (this.numResults != 0) {
				sqlStr += " LIMIT " + numResults;
			}
			
			stmt = conn.prepareStatement(sqlStr);

			// Our results
			rs = stmt.executeQuery();

			// Iterate over list and populate our return list
			while (rs.next()) {
				YatesRun yr = new YatesRun();
				yr.load(rs.getInt("id"));			
				retList.add(yr);
			}

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
		
		return retList;
	}

	public YatesRunSearcher() {
		projectID = 0;
		protein = null;
	}

	private int projectID;
	private NRProtein protein;
	private int numResults;
	private boolean mostRecent = false;
	
	
	
	/**
	 * @return Returns the mostRecent.
	 */
	public boolean isMostRecent() {
		return mostRecent;
	}
	/**
	 * @param mostRecent The mostRecent to set.
	 */
	public void setMostRecent(boolean mostRecent) {
		this.mostRecent = mostRecent;
	}
	/**
	 * @return Returns the numResults.
	 */
	public int getNumResults() {
		return numResults;
	}
	/**
	 * @param numResults The numResults to set.
	 */
	public void setNumResults(int numResults) {
		this.numResults = numResults;
	}
	
	
	/**
	 * @return Returns the projectID.
	 */
	public int getProjectID() {
		return projectID;
	}
	/**
	 * @param projectID The projectID to set.
	 */
	public void setProjectID(int projectID) {
		this.projectID = projectID;
	}
	
	/**
	 * @return Returns the protein.
	 */
	public NRProtein getProtein() {
		return protein;
	}
	/**
	 * @param protein The protein to set.
	 */
	public void setProtein(NRProtein protein) {
		this.protein = protein;
	}
}
