/*
 * YatesCycle.java
 *
 * Created November 11, 2003
 *
 * Michael Riffle <mriffle@u.washington.edu>
 *
 */

package org.yeastrc.yates;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.yeastrc.data.IData;
import org.yeastrc.data.InvalidIDException;
import org.yeastrc.db.DBConnectionManager;
import org.yeastrc.utils.Compresser;
import org.yeastrc.utils.Decompresser;
import java.io.InputStream;
import java.io.*;

/**
 * 
 */
public class YatesMS2 implements IData {

	// Our constructor
	public YatesMS2() {
		this.cycleID = 0;
		this.ms2Data = null;
		this.isNew = true;
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
			String sqlStr = "SELECT cycleID, data FROM tblYatesCycleMS2Data WHERE cycleID = " + this.cycleID;
			rs = stmt.executeQuery(sqlStr);

			// See if we're updating a row or adding a new row.
			if (this.isNew == false) {
			
				// Make sure this row is actually in the database.  This shouldn't ever happen.
				if (!rs.next()) {
					throw new InvalidIDException("ID was set for this MS2 data, but not found in database on save()");
				}

				// Make sure the result set is set up w/ current values from this object
				try {
					rs.updateBytes("data", this.ms2Data);
				} catch (Exception e) {
					throw new SQLException ("Error compressing MS2 data: " + e.getMessage());
				}
				
				rs.updateRow();

			} else {
				// We're adding a new row.
			
				rs.moveToInsertRow();
				rs.updateInt("cycleID", this.cycleID);

				try {
					rs.updateBytes("data", this.ms2Data);
				} catch (Exception e) {
					throw new SQLException ("Error compressing MS2 data: " + e.getMessage());
				}
			
				rs.insertRow();
			
				// This data is no longer new, since it's now in the database.
				this.isNew = false;
			}

			// Close our statement handle
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
			String sqlStr = "SELECT data FROM tblYatesCycleMS2Data WHERE cycleID = " + id;
		
			// Our results
			rs = stmt.executeQuery(sqlStr);
		
			// No rows returned.
				if( !rs.next() ) {
				throw new InvalidIDException("ID not found for this experiment.");
			}
		
			// Populate the object from this row.
			this.cycleID = id;
			
			try {
				this.ms2Data = rs.getBytes("data");
			} catch (Exception e) {
				throw new SQLException ("Error loading ms2 data from database: " + e.getMessage());
			}
			
			this.isNew = false;
		
			// Close our statement handle
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
			String sqlStr = "SELECT cycleID FROM tblYatesCycleMS2Data WHERE cycleID = " + this.cycleID;

			// Our results
			rs = stmt.executeQuery(sqlStr);

			// No rows returned.
			if( !rs.next() ) {
				throw new InvalidIDException("ID not found for this ms2.");
			}
			
			// Delete the row.
			rs.deleteRow();		

			rs.close(); rs = null;
			stmt.close(); stmt = null;
			conn.close(); conn = null;
			
			this.cycleID = 0;
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
	 * Set the cycle ID to which this data belongs.
	 * @param id The cycle ID to which this data belongs.
	 */
	public void setCycleID(int id) { this.cycleID = id; }

	/**
	 * Set the contents of the supplied file as the MS2 Data
	 * @param file
	 * @throws Exception
	 */
	public void setMS2File(File file) throws Exception {
		this.ms2Data = Compresser.getInstance().compressFile(file);
	}
	
	/**
	 * Set the MS2 data
	 * @param arg
	 */
	public void setMS2Data(String arg) throws Exception {
		try {
			this.ms2Data = Compresser.getInstance().compressString(arg);
		} catch (Exception e) {
			throw new Exception ("Error compressing MS2 Data: " + e.getMessage() );
		} finally {
			arg = "";
			System.gc();
		}
	}

	/**
	 * Sets the MS2 data
	 * @param arg A StringBuffer containing the uncompressed MS2 data
	 * @throws Exception If there is a problem compression the data
	 */
	public void setMS2Data(StringBuffer arg) throws Exception {
		try {
			this.ms2Data = Compresser.getInstance().compressStringBuffer(arg);
		} catch (Exception e) {
			throw new Exception ("Error compressing MS2 Data: " + e.getMessage() );
		} finally {
			System.gc();
		}
	}
	

	/**
	 * Get the cycle ID.
	 * @return the cycle ID
	 */
	public int getCycleID() { return this.cycleID; }

	/**
	 * Get the MS2 data in InputStream form
	 * @return the MS2 data
	 */
	public InputStream getMS2Data() throws Exception {
		try {
			return Decompresser.getInstance().decompressString(this.ms2Data);
		} catch (Exception e) {
			throw new Exception ("Error decompressing MS2 Data: " + e.getMessage());
		}
	}


	// The cycle ID
	private int cycleID;
	
	// The MS2 Data as a compressed byte array (as it is stored in the database)
	private byte[] ms2Data;

	// Is this a new set of data?
	private boolean isNew;

}