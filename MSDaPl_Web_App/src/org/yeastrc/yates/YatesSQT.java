/*
 * YatesSQT.java
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
import org.yeastrc.utils.*;

import java.io.File;
import java.io.InputStream;

/**
 * 
 */
public class YatesSQT implements IData {

	// Our constructor
	public YatesSQT() {
		this.cycleID = 0;
		this.sqtData = null;
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
			String sqlStr = "SELECT cycleID, data FROM tblYatesCycleSQTData WHERE cycleID = " + this.cycleID;
			rs = stmt.executeQuery(sqlStr);

			// See if we're updating a row or adding a new row.
			if (this.isNew == false) {
			
				// Make sure this row is actually in the database.  This shouldn't ever happen.
				if (!rs.next()) {
					throw new InvalidIDException("ID was set for this SQT data, but not found in database on save()");
				}

				// Make sure the result set is set up w/ current values from this object
				try {
					rs.updateBytes("data", this.sqtData);			
				} catch (Exception e) {
					throw new SQLException ("Error compressing SQT data: " + e.getMessage());
				}

				rs.updateRow();

			} else {
				// We're adding a new row.
			
				rs.moveToInsertRow();
				rs.updateInt("cycleID", this.cycleID);
				
				try {
					rs.updateBytes("data", this.sqtData);
				} catch (Exception e) {
					throw new SQLException ("Error compressing SQT data: " + e.getMessage());
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
			String sqlStr = "SELECT data FROM tblYatesCycleSQTData WHERE cycleID = " + id;
		
			// Our results
			rs = stmt.executeQuery(sqlStr);
		
			// No rows returned.
			if( !rs.next() ) {
				throw new InvalidIDException("ID not found for this SQT (cycle ID).");
			}
		
			// Populate the object from this row.
			this.cycleID = id;
			
			try {
				this.sqtData = rs.getBytes("data");
			} catch (Exception e) {
				throw new SQLException ("Error loading sqt data: " + e.getMessage());
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
			String sqlStr = "SELECT cycleID FROM tblYatesCycleSQTData WHERE cycleID = " + this.cycleID;

			// Our results
			rs = stmt.executeQuery(sqlStr);

			// No rows returned.
			if( !rs.next() ) {
				throw new InvalidIDException("ID not found for this sqt.");
			}
			
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
	 * Set the contents of the supplied file as the SQT Data
	 * @param file
	 * @throws Exception
	 */
	public void setSQTFile(File file) throws Exception {
		this.sqtData = Compresser.getInstance().compressFile(file);
	}
	
	/**
	 * Set the SQT data
	 * @param arg
	 */
	public void setSQTData(String arg) throws Exception {
		try {
			this.sqtData = Compresser.getInstance().compressString(arg);
		} catch (Exception e) {
			throw new Exception ("Error compressing SQT Data: " + e.getMessage() );
		} finally {
			arg = "";
			System.gc();
		}
	}

	/**
	 * Sets the SQT data
	 * @param arg A StringBuffer containing the uncompressed SQT data
	 * @throws Exception If there is a problem compression the data
	 */
	public void setSQTData(StringBuffer arg) throws Exception {
		try {
			this.sqtData = Compresser.getInstance().compressStringBuffer(arg);
		} catch (Exception e) {
			throw new Exception ("Error compressing SQT Data: " + e.getMessage() );
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
	 * Get the SQT data in InputStream form
	 * @return the SQT data
	 */
	public InputStream getSQTData() throws Exception {
		try {
			return Decompresser.getInstance().decompressString(this.sqtData);
		} catch (Exception e) {
			throw new Exception ("Error decompressing SQT Data: " + e.getMessage());
		}
	}


	// The cycle ID
	private int cycleID;
	
	// The SQT Data as a compressed byte array (as it is in the database)
	private byte[] sqtData;

	// Is this a new set of data?
	private boolean isNew;

}