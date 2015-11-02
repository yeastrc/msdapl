/**
 * MS2DatabaseObjectScanReader.java
 * Created by: Michael Riffle <mriffle@u.washington.edu>
 * Created on: May 10, 2007 at 2:53:41 PM
 */

package org.yeastrc.ms2.spectra;

import java.util.*;
import java.sql.*;

/**
 * @author Michael Riffle <mriffle@u.washington.edu>
 * @version May 10, 2007
 *
 * Class definition goes here
 */
public class MS2DatabaseObjectScanReader implements MS2ScanReader {

	public MS2Scan readNext() throws Exception { return null; }
	
	/**
	 * Get the next unread scan for this MS2 file
	 */
	/*
	public MS2Scan readNext() throws Exception {
		
		// initialize the scanIDs
		if (this.scanIDs == null)
			//this.initializeScanIDs();
		
		
		
		
		
		return null;
	}
	*/
	
	
	/**
	 * Close this reader, does nothing for this type of reader
	 */
	public void close() {
		return;
	}

	
	/**
	 * Initialize the list of scan IDs that exist for the parent MS2 object
	 * @throws Exception if there is a problem
	 */
	/*
	private void initializeScanIDs() throws Exception {
		
		// make sure we have what we need
		if (this.getMS2() == null)
			throw new Exception( "No MS2 object set in ScanReader..." );
		if (this.getMS2().getId() == 0)
			throw new Exception( "No ID set in MS2, can intialize database scan reader." );
		
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
			conn = ConnectionFactory.getInstance().getConnection();
			
			String sql = "SELECT id FROM ms2Scan WHERE ms2Run = ? ORDER BY id";
			stmt = conn.prepareStatement( sql );
			stmt.setInt( 1, this.getMS2().getId() );
			rs = stmt.executeQuery();
			
			while (rs.next()) {
				if (this.scanIDs == null) this.scanIDs = new Vector<Integer>();
				
				this.scanIDs.add( rs.getInt( 1 ) );
			}
			
			this.scanIDs.trimToSize();
			
			// Close up shop
			rs.close(); rs = null;
			stmt.close(); stmt = null;
			conn.close(); conn = null;
			
		} finally {

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
	*/
	
	/**
	 * Is there another scan to read?
	 * @return true if there is, false if there isn't
	 * @throws Exception if there is a database problem
	 */
	public boolean hasNext() throws Exception {
		
		// initialize the scanIDs
		if (this.scanIDs == null)
			//this.initializeScanIDs();

		// if we've returned the last index of the vector already, return false
		if ( this.lastScanReturned >= ( this.scanIDs.size() - 1 ) )
			return false;
		
		// we have more
		return true;
	}
	
	
	// instance vars
	private MS2 ms2;
	private Vector<Integer> scanIDs;
	private int lastScanReturned = -1;
	
	/**
	 * @return the ms2
	 */
	public MS2 getMS2() {
		return ms2;
	}

	/**
	 * @param ms2 the ms2 to set
	 */
	public void setMS2(MS2 ms2) {
		this.ms2 = ms2;
	}
	
}
