/*
 * YatesPeptide.java
 *
 * Created November 11, 2003
 *
 * Michael Riffle <mriffle@u.washington.edu>
 *
 */

package org.yeastrc.yates;

import org.yeastrc.data.*;
import org.yeastrc.db.*;

import java.sql.*;

/**
 * 
 */
public class YatesCycle implements IData {

	// Our constructor
	public YatesCycle() {
		this.cycleID = 0;
		this.runID = 0;
		this.cycleFileName = null;
	}


	/**
	 * Will return the YatesMS2 object corresponding to the MS2 data for this cycle
	 * Will return null if none is found in the database for this cycle ID.
	 * @return the YatesMS2 object, null if none found
	 * @throws SQLException if there is a database error
	 * @throws InvalidIDException if there is no cycle id set in this cycle object
	 */
	public YatesMS2 getMS2() throws SQLException, InvalidIDException {
		if (this.cycleID == 0) { throw new InvalidIDException("No ID set for call to getMS2()."); }

		// What we're returning
		YatesMS2 ms2;

		// Get our connection to the database.
		Connection conn = DBConnectionManager.getConnection("yrc");
		Statement stmt = null;
		ResultSet rs = null;
	
		try {
			stmt = conn.createStatement();
	
			// Our SQL statement
			String sqlStr = "SELECT cycleID FROM tblYatesCycleMS2Data WHERE cycleID = " + this.cycleID;
			rs = stmt.executeQuery(sqlStr);

			// No rows returned.
			if( !rs.next() ) {
				return null;
			}
			
			ms2 = new YatesMS2();
			ms2.load(rs.getInt("cycleID"));
			
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
		
		return ms2;
	}

	/**
	 * Will return the YatesSQT object corresponding to the SQT data for this cycle
	 * Will return null if none is found in the database for this cycle ID.
	 * @return the YatesSQT object, null if none found
	 * @throws SQLException if there is a database error
	 * @throws InvalidIDException if there is no cycle id set in this cycle object
	 */
	public YatesSQT getSQT() throws SQLException, InvalidIDException {
		if (this.cycleID == 0) { throw new InvalidIDException("No ID set for call to getSQT()."); }

		// What we're returning
		YatesSQT sqt;

		// Get our connection to the database.
		Connection conn = DBConnectionManager.getConnection("yrc");
		Statement stmt = null;
		ResultSet rs = null;
	
		try {
			stmt = conn.createStatement();
	
			// Our SQL statement
			String sqlStr = "SELECT cycleID FROM tblYatesCycleSQTData WHERE cycleID = " + this.cycleID;
			rs = stmt.executeQuery(sqlStr);

			// No rows returned.
			if( !rs.next() ) {
				return null;
			}
			
			sqt = new YatesSQT();
			sqt.load(rs.getInt("cycleID"));

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
		
		return sqt;
	}

	/**
	 * Use this method to save this object's data to the database.
	 * These objects should generally represent a single row of data in a table.
	 * This will update the row if this object represents a current row of data, or it will
	 * insert a new row if this data is not in the database (that is, if there is no ID in the object).
	 * @throws InvalidIDException If there is an ID set in this object, but it is not in the database.
	 * @throws SQLException If there is a problem interracting with the database
	 */
	public void save() throws InvalidIDException, SQLException {

		// Get our connection to the database.
		Connection conn = DBConnectionManager.getConnection("yrc");
		Statement stmt = null;
		ResultSet rs = null;

		try {
			stmt = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);

			// Get our updatable result set
			String sqlStr = "SELECT * FROM tblYatesCycles WHERE cycleID = " + this.cycleID;
			rs = stmt.executeQuery(sqlStr);

			// See if we're updating a row or adding a new row.
			if (this.cycleID > 0) {

				// Make sure this row is actually in the database.  This shouldn't ever happen.
				if (!rs.next()) {
					throw new InvalidIDException("ID was set in cycle, but not found in database on save()");
				}
			
				// Make sure the result set is set up w/ current values from this object
				rs.updateInt("runID", this.runID);
				rs.updateString("cycleFileName", this.cycleFileName);
			
				// Update the row
				rs.updateRow();

			} else {
				// We're adding a new row.

				rs.moveToInsertRow();

				rs.updateInt("runID", this.runID);
				rs.updateString("cycleFileName", this.cycleFileName);

				rs.insertRow();

				// Get the ID generated for this item from the database, and set expID
				rs.last();
				this.cycleID = rs.getInt("cycleID");

			}

			rs.close();
			rs = null;
			
			stmt.close();
			stmt = null;
			
			conn.close();
			conn = null;
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


	/**
	 * Use this method to populate this object with data from the datbase.
	 * @param id The experiment ID to load.
	 * @throws InvalidIDException If this ID is not valid (or not found)
	 * @throws SQLException If there is a problem interracting with the database
	 */
	public void load(int id) throws InvalidIDException, SQLException {

		// Get our connection to the database.
		Connection conn = DBConnectionManager.getConnection("yrc");
		Statement stmt = null;
		ResultSet rs = null;

		try {
			stmt = conn.createStatement();

			// Our SQL statement
			String sqlStr = "SELECT * FROM tblYatesCycles WHERE cycleID = " + id;
		
			// Our results
			rs = stmt.executeQuery(sqlStr);
		
			// No rows returned.
			if( !rs.next() ) {
				throw new InvalidIDException("ID not found for this cycle.");
			}
		
			// Populate the object from this row.
			this.cycleID = rs.getInt("cycleID");
			this.runID = rs.getInt("runID");
			this.cycleFileName = rs.getString("cycleFileName");

			rs.close();
			rs = null;
			
			stmt.close();
			stmt = null;
			
			conn.close();
			conn = null;
		}
		catch(SQLException e) { throw e; }
		catch(InvalidIDException e) { throw e; }
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


	/**
	 * Use this method to delete the data underlying this object from the database.
	 * Doing so will delete the row from the table corresponding to this object, and
	 * will remove the ID value from the object (since it represents the primary key)
	 * in the database.  This will cause subsequent calls to save() on the object to
	 * insert a new row into the database and generate a new ID.
	 * This will also call delete() on instantiated IData objects for all rows in the
	 * database which are dependent on this row.  For example, calling delete() on a
	 * MS Run objects would call delete() on all Run Result objects, which would then
	 * call delete() on all dependent Peptide objects for those results.
	 * Pre: object is populated with a valid ID.
	 * @throws SQLException if there is a problem working with the database.
	 * @throws InvalidIDException if the ID isn't set in this object, or if the ID isn't
	 * valid (that is, not found in the database).
	 */
	public void delete() throws InvalidIDException, SQLException {

		// Get our connection to the database.
		Connection conn = DBConnectionManager.getConnection("yrc");
		Statement stmt = null;
		ResultSet rs = null;

		try {
			stmt = conn.createStatement();

			// Our SQL statement
			String sqlStr = "SELECT cycleID FROM tblYatesCycles WHERE cycleID = " + this.cycleID;

			// Our results
			rs = stmt.executeQuery(sqlStr);

			// No rows returned.
			if( !rs.next() ) {
				throw new InvalidIDException("ID not found for this peptide.");
			}


			// Delete any MS2 & SQT data associated with this Cycle
			YatesMS2 ms2 = this.getMS2();
			YatesSQT sqt = this.getSQT();
			if(ms2 != null) { ms2.delete(); }
			if(sqt != null) { sqt.delete(); }
			
			// Delete the row.
			rs.deleteRow();		

			rs.close();
			rs = null;
			
			stmt.close();
			stmt = null;
			
			conn.close();
			conn = null;
			
			this.cycleID = 0;
		}
		catch(SQLException e) { throw e; }
		catch(InvalidIDException e) { throw e; }
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


	// Document these setter methods soon, though they're self explanitory
	public void setRunID(int arg) { this.runID = arg; }
	public void setFileName(String arg) { this.cycleFileName = arg; }	

	// Document these getter methods soon, though they're self explanitory
	public int getID() { return this.cycleID; }
	public int getRunID() { return this.runID; }
	public String getFileName() { return this.cycleFileName; }

	// Our instance variables
	private int cycleID;
	private int runID;
	private String cycleFileName;

}