/*
 * YatesRun.java
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
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.yeastrc.bio.protein.Protein;
import org.yeastrc.bio.taxonomy.Species;
import org.yeastrc.bio.taxonomy.TaxonomyUtils;
import org.yeastrc.data.IData;
import org.yeastrc.data.InvalidIDException;
import org.yeastrc.db.DBConnectionManager;
import org.yeastrc.ms.IMSRun;
import org.yeastrc.nr_seq.NRProtein;
import org.yeastrc.nr_seq.NRProteinFactory;
import org.yeastrc.project.Project;
import org.yeastrc.project.ProjectFactory;
import org.yeastrc.sgd.SGDUtils;
import org.yeastrc.utils.*;
import java.io.*;

/**
 * 
 */
public class YatesRun implements IData, IMSRun {

	/**
	 * Get the project to which this run belongs
	 * @return
	 * @throws Exception
	 */
	public Project getProject() throws Exception {
		return ProjectFactory.getProject(this.getProjectID());
	}

	public List<YatesResult> getFilteredResults() throws SQLException, Exception {
		
		List<YatesResult> retList = new ArrayList<YatesResult>();
		
		// Get our connection to the database.
		Connection conn = DBConnectionManager.getConnection("yrc");
		Statement stmt = null;
		ResultSet rs = null;

		try {
			stmt = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);

			// Get our updatable result set
			String sqlStr = "SELECT id FROM tblYatesRunResult WHERE runID = " + this.id;
			sqlStr += " ORDER BY sequenceCoverage DESC";
			rs = stmt.executeQuery(sqlStr);
			
			while (rs.next()) {
				int resID = rs.getInt(1);
				YatesResult yr = new YatesResult();
				yr.load(resID);

				// Skip it if it is a single peptide hit
				if (yr.getSequenceCount() < 2) { continue; }
				
				// If our target species is different than the hit species, skip it
				//  Mike Riffle - November 9, 2007 - but only do this if there IS a target species
				if ( this.targetSpecies != null &&
						this.targetSpecies.getId() != 0 &&
						yr.getHitProtein().getSpecies().getId() != this.targetSpecies.getId() ) {
					continue;
				}
				
				// Filter out specific categories of hits based on gene names
				if (this.targetSpecies.getId() == TaxonomyUtils.SACCHAROMYCES_CEREVISIAE) {
					boolean foundJunk = false;
					Collection sn = yr.getHitProtein().getSystematicNames();
					Iterator iter = sn.iterator();
					while (iter.hasNext()) {
						String name = (String)(iter.next());
						name = SGDUtils.getStandardName(name);
						if (name == null) continue;
						if ( name.startsWith("RPS") || name.startsWith("RPL") || name.startsWith("TDH") || name.startsWith("SSB") ||
						     name.startsWith("SSA") || name.startsWith("PAB") || name.startsWith("RPP")) {
							foundJunk = true;
							break;
						}
					}

					// If we found a contaminate, skip this result
					if (foundJunk) continue;
				}
				// Add it to our list of filtered results
				retList.add(yr);
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
		
		return retList;
	}
	
	/**
	 * Get the YatesResults associated with this YatesRun
	 * @throws SQLException if there is a database error
	 * @throws InvalidIDException if an invalid resultID load attempt is made.
	 */
	public List<YatesResult> getResults() throws SQLException, Exception {
		List<YatesResult> retList = new ArrayList<YatesResult>();
		
		// Get our connection to the database.
		Connection conn = DBConnectionManager.getConnection("yrc");
		Statement stmt = null;
		ResultSet rs = null;

		try {
			stmt = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);

			// Get our updatable result set
			String sqlStr = "SELECT id FROM tblYatesRunResult WHERE runID = " + this.id + " ORDER BY sequenceCoverage DESC";
			rs = stmt.executeQuery(sqlStr);
			
			while (rs.next()) {
				int resID = rs.getInt(1);
				YatesResult yr = new YatesResult();
				yr.load(resID);
				
				retList.add(yr);
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
		
		return retList;
	}
	
	public List<Integer> getNrseqIds() throws SQLException, Exception {
        List<Integer> retList = new ArrayList<Integer>();
        
        // Get our connection to the database.
        Connection conn = DBConnectionManager.getConnection(DBConnectionManager.MAIN_DB);
        Statement stmt = null;
        ResultSet rs = null;

        try {
            stmt = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);

            // Get our updatable result set
            String sqlStr = "SELECT DISTINCT(hitProteinID) FROM tblYatesRunResult WHERE runID = " + this.id;
            rs = stmt.executeQuery(sqlStr);
            
            while (rs.next()) {
                int nrseqId = rs.getInt(1);
                retList.add(nrseqId);
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
        
        return retList;
    }
	
	/**
	 * Use this method to save this object's data to the database.
	 * These objects should generally represent a single row of data in a table.
	 * This will update the row if this object represents a current row of data, or it will
	 * insert a new row if this data is not in the database (that is, if there is no ID in the object).
	 * @throws InvalidIDException If there is an ID set in this object, but it is not in the database.
	 * @throws SQLException If there is a problem interracting with the database
	 */
	public void save() throws InvalidIDException, SQLException, Exception {

		// Get our connection to the database.
		Connection conn = DBConnectionManager.getConnection("yrc");
		Statement stmt = null;
		ResultSet rs = null;

		try {
			stmt = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);

			// Get our updatable result set
			String sqlStr = "SELECT * FROM tblYatesRun WHERE id = " + this.id;
			rs = stmt.executeQuery(sqlStr);

			// See if we're updating a row or adding a new row.
			if (this.id > 0) {

				// Make sure this row is actually in the database.  This shouldn't ever happen.
				if (!rs.next()) {
					throw new InvalidIDException("ID was set for YatesRun, but not found in database on save()");
				}

				// Make sure the result set is set up w/ current values from this object
				if (this.projectID == 0) { rs.updateNull("projectID"); }
				else { rs.updateInt("projectID", this.projectID); }

		
				if (this.baitDesc == null)
					rs.updateNull("baitDesc");
				else
					rs.updateString("baitDesc", this.baitDesc);
				
				if (this.baitProtein == null)
					rs.updateNull("baitProteinID");
				else
					rs.updateInt("baitProteinID", this.baitProtein.getId());
				
				if (this.targetSpecies == null)
					rs.updateNull("targetSpecies");
				else
					rs.updateInt("targetSpecies", this.targetSpecies.getId());

				if (this.runDate == null) { rs.updateNull("runDate"); }
				else { rs.updateDate("runDate", new java.sql.Date(this.runDate.getTime())); }
				
				if (this.directoryName == null) { rs.updateNull("directoryName"); }
				else { rs.updateString("directoryName", this.directoryName); }
				
				if (this.DTASelectTXTChanged) {
					if (this.DTASelectTXTData == null) { rs.updateNull("DTASelectTXT"); }
					else {
						rs.updateBytes("DTASelectTXT", this.DTASelectTXTData);
					}
				}
				
				if (this.DTASelectFilterTXTChanged) {
					if (this.DTASelectFilterTXT == null) { rs.updateNull("DTASelectFilterTXT"); }
					else { rs.updateString("DTASelectFilterTXT", this.DTASelectFilterTXT); }
				}
				
				if (this.DTASelectHTMLChanged) {
					if (this.DTASelectHTML == null) { rs.updateNull("DTASelectHTML"); }
					else { rs.updateString("DTASelectHTML", this.DTASelectHTML); }
				}
				
				if (this.DTASelectParamsChanged) {
					if (this.DTASelectParams == null) { rs.updateNull("DTASelectParams"); }
					else { rs.updateString("DTASelectParams", this.DTASelectParams); }
				}
				
				if (this.comments == null) { rs.updateNull("comments"); }
				else { rs.updateString("comments", this.comments); }

				if (this.databaseName == null) { rs.updateNull("databaseName"); }
				else { rs.updateString("databaseName", this.databaseName); }

				// Update the row
				rs.updateRow();

			} else {
				// We're adding a new row.

				rs.moveToInsertRow();

				if (this.projectID == 0) { rs.updateNull("projectID"); }
				else { rs.updateInt("projectID", this.projectID); }

				if (this.baitDesc == null)
					rs.updateNull("baitDesc");
				else
					rs.updateString("baitDesc", this.baitDesc);
				
				if (this.baitProtein == null)
					rs.updateNull("baitProteinID");
				else
					rs.updateInt("baitProteinID", this.baitProtein.getId());

				if (this.targetSpecies == null)
					rs.updateNull("targetSpecies");
				else
					rs.updateInt("targetSpecies", this.targetSpecies.getId());
				
				if (this.runDate == null) { rs.updateNull("runDate"); }
				else { rs.updateDate("runDate", new java.sql.Date(this.runDate.getTime())); }
				
				if (this.directoryName == null) { rs.updateNull("directoryName"); }
				else { rs.updateString("directoryName", this.directoryName); }
				
				if (this.DTASelectTXTChanged) {
					if (this.DTASelectTXTData == null) { rs.updateNull("DTASelectTXT"); }
					else {
						rs.updateBytes("DTASelectTXT", this.DTASelectTXTData);
					}
				}
				
				if (this.DTASelectFilterTXTChanged) {
					if (this.DTASelectFilterTXT == null) { rs.updateNull("DTASelectFilterTXT"); }
					else { rs.updateString("DTASelectFilterTXT", this.DTASelectFilterTXT); }
				}
				
				if (this.DTASelectHTMLChanged) {
					if (this.DTASelectHTML == null) { rs.updateNull("DTASelectHTML"); }
					else { rs.updateString("DTASelectHTML", this.DTASelectHTML); }
				}
				
				if (this.DTASelectParamsChanged) {
					if (this.DTASelectParams == null) { rs.updateNull("DTASelectParams"); }
					else { rs.updateString("DTASelectParams", this.DTASelectParams); }
				}
				
				if (this.comments == null) { rs.updateNull("comments"); }
				else { rs.updateString("comments", this.comments); }
				
				if (this.databaseName == null) { rs.updateNull("databaseName"); }
				else { rs.updateString("databaseName", this.databaseName); }

				rs.insertRow();
				
				// Get the ID generated for this item from the database, and set ID
				rs.last();
				this.id = rs.getInt("id");
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
	}


	/**
	 * Use this method to populate this object with data from the datbase.
	 * @param id The experiment ID to load.
	 * @throws InvalidIDException If this ID is not valid (or not found)
	 * @throws SQLException If there is a problem interracting with the database
	 */
	public void load(int id) throws InvalidIDException, SQLException, Exception {

		// Get our connection to the database.
		Connection conn = DBConnectionManager.getConnection("yrc");
		Statement stmt = null;
		ResultSet rs = null;

		try{
			stmt = conn.createStatement();

			// Our SQL statement
			String sqlStr = "SELECT projectID, baitDesc, targetSpecies, runDate, directoryName, comments, baitProteinID, databaseName, uploadDate FROM tblYatesRun WHERE id = " + id;

			// Our results
			rs = stmt.executeQuery(sqlStr);

			// No rows returned.
			if( !rs.next() )
				throw new InvalidIDException("Loading YatesRun failed due to invalid ID ( " + id + ".");

			// Populate the object from this row.
			this.id = id;
			this.projectID = rs.getInt("projectID");
			
			this.baitDesc = rs.getString("baitDesc");
			this.databaseName = rs.getString("databaseName");
			
			if (rs.getInt("baitProteinID") != 0) {
				NRProteinFactory nrpf = NRProteinFactory.getInstance();
				this.baitProtein = (NRProtein)(nrpf.getProtein(rs.getInt("baitProteinID")));
			}
			
			this.targetSpecies = new Species();
			this.targetSpecies.setId(rs.getInt("targetSpecies"));
			
			this.runDate = rs.getDate("runDate");
			this.uploadDate = rs.getDate("uploadDate");
			this.directoryName = rs.getString("directoryName");
			this.comments = rs.getString("comments");
			
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
	public void delete() throws InvalidIDException, SQLException, Exception {

		// Get our connection to the database.
		Connection conn = DBConnectionManager.getConnection("yrc");
		Statement stmt = null;
		ResultSet rs = null;

		try {
			stmt = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);

			// Our SQL statement
			String sqlStr = "SELECT id FROM tblYatesRun WHERE id = " + this.id;

			// Our results
			rs = stmt.executeQuery(sqlStr);

			// No rows returned.
			if( !rs.next() ) {
				throw new InvalidIDException("Attempted to delete a YatesRun not found in the database.");
			}

			// Delete all results associated with this run
			List results = this.getResults();
			Iterator iter = results.iterator();
			while (iter.hasNext()) {
				YatesResult yr = (YatesResult)(iter.next());
				yr.delete();
			}
			
			
			// Delete the result row (run).
			rs.deleteRow();		

			// Close up shop
			rs.close(); rs = null;
			stmt.close(); stmt = null;
			conn.close(); conn = null;
			
			// re-initialize the id
			this.id = 0;
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
	
	// Constructor
	public YatesRun () {
		this.id = 0;
		this.projectID = 0;
		this.baitProtein = null;
		this.baitDesc = null;
		this.runDate = null;
		this.directoryName = null;
		this.DTASelectTXTData = null;
		this.DTASelectHTML = null;
		this.DTASelectFilterTXT = null;
		this.DTASelectParams = null;
		this.comments = null;
		this.targetSpecies = null;
		this.uploadDate = null;
		
		this.DTASelectTXTChanged = false;
		this.DTASelectFilterTXTChanged = false;
		this.DTASelectHTMLChanged = false;
		this.DTASelectParamsChanged = false;
	}

	// Instance variables.
	private int id;
	private int projectID;
	private String baitDesc;
	private NRProtein baitProtein;
	private Species targetSpecies;
	private java.util.Date runDate;
	private java.util.Date uploadDate;
	private String directoryName;
	private byte[] DTASelectTXTData;
	private String DTASelectHTML;
	private String DTASelectFilterTXT;
	private String DTASelectParams;
	private String comments;
	private String databaseName;
	
	private boolean DTASelectTXTChanged;
	private boolean DTASelectFilterTXTChanged;
	private boolean DTASelectHTMLChanged;
	private boolean DTASelectParamsChanged;

	
	/**
	 * Get the target species for this MS Run
	 * @return The target species (the species from which we're expecting identified protein), null if not set
	 */
	public Species getTargetSpecies() {
		return this.targetSpecies;
	}
	
	/**
	 * Set the target species (the species from which we're expecting identified proteins)
	 * @param target The target species
	 */
	public void setTargetSpecies(Species target) {
		this.targetSpecies = target;
	}

	/**
	 * @return Returns the comments.
	 */
	public String getComments() {
		return comments;
	}
	/**
	 * @param comments The comments to set.
	 */
	public void setComments(String comments) {
		this.comments = comments;
	}
	/**
	 * @return Returns the directoryName.
	 */
	public String getDirectoryName() {
		return directoryName;
	}
	/**
	 * @param directoryName The directoryName to set.
	 */
	public void setDirectoryName(String directoryName) {
		this.directoryName = directoryName;
	}
	/**
	 * @return Returns the dTASelectFilterTXT.
	 */
	public String getDTASelectFilterTXT() throws Exception {
		if (this.DTASelectFilterTXT == null)
			this.DTASelectFilterTXT = YatesUtils.getDTADataForRun(this, "DTASelectFilterTXT");

		return this.DTASelectFilterTXT;
	}
	/**
	 * @param selectFilterTXT The DTASelectFilterTXT to set.
	 */
	public void setDTASelectFilterTXT(String selectFilterTXT) {
		DTASelectFilterTXT = selectFilterTXT;
		this.DTASelectFilterTXTChanged = true;
	}
	/**
	 * @return Returns the DTASelectHTML.
	 */
	public String getDTASelectHTML() throws Exception {
		if (this.DTASelectHTML == null)
			this.DTASelectHTML = YatesUtils.getDTADataForRun(this, "DTASelectHTML");
		
		return this.DTASelectHTML;
	}
	/**
	 * Does this run have DTASelectHTML data in the database?
	 * @return true if yes, false if no
	 * @throws Exception If there is a database problem.
	 */
	public boolean getContainsDTASelectHTML() throws Exception {
		return YatesUtils.runContainsDTAData(this, "DTASelectHTML");
	}	
	/**
	 * @param selectHTML The dTASelectHTML to set.
	 */
	public void setDTASelectHTML(String selectHTML) {
		DTASelectHTML = selectHTML;
		this.DTASelectHTMLChanged = true;
	}
	/**
	 * @return Returns the dTASelectParams.
	 */
	public String getDTASelectParams() throws Exception {
		if (this.DTASelectParams == null)
			this.DTASelectParams = YatesUtils.getDTADataForRun(this, "DTASelectParams");
		
		return this.DTASelectParams;
	}
	/**
	 * @param selectParams The dTASelectParams to set.
	 */
	public void setDTASelectParams(String selectParams) {
		DTASelectParams = selectParams;
		this.DTASelectParamsChanged = true;
	}
	/**
	 * @return Returns the dTASelectTXT.
	 */
	public String getDTASelectTXT() throws Exception {
		return YatesUtils.getDTADataForRun(this, "DTASelectTXT");
	}
	
	/**
	 * does this run have DTASelectTXT data in the database?
	 * @return true if yes, false if not
	 */
	public boolean getContainsDTASelectTXT() {
		
		try {
			return YatesUtils.runContainsDTAData(this, "DTASelectTXT");
		} catch (Exception e ) { ; }
		
		return false;
	}
	
	/**
	 * @param selectTXT The File containing the DTASelect data to set
	 */
	public void setDTASelectTXTFile(File file) throws Exception {
		this.DTASelectTXTData = Compresser.getInstance().compressFile(file);
		this.DTASelectTXTChanged = true;
	}
	/**
	 * @return Returns the id.
	 */
	public int getId() {
		return id;
	}
	/**
	 * @param id The id to set.
	 */
	public void setId(int id) {
		this.id = id;
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
	 * @return Returns the runDate.
	 */
	public java.util.Date getRunDate() {
		return runDate;
	}
	/**
	 * @param runDate The runDate to set.
	 */
	public void setRunDate(java.util.Date runDate) {
		this.runDate = runDate;
	}
	/**
	 * @return Returns the uploadDate.
	 */
	public java.util.Date getUploadDate() {
		return uploadDate;
	}
	/**
	 * @return Returns the baitProtein.
	 */
	public Protein getBaitProtein() {
		return baitProtein;
	}
	/**
	 * @param baitProtein The baitProtein to set.
	 */
	public void setBaitProtein(NRProtein baitProtein) {
		this.baitProtein = baitProtein;
	}

	/**
	 * @return Returns the baitDesc.
	 */
	public String getBaitDesc() {
		return baitDesc;
	}
	/**
	 * @param baitDesc The baitDesc to set.
	 */
	public void setBaitDesc(String baitDesc) {
		this.baitDesc = baitDesc;
	}
	
	/**
	 * @return Returns the databaseName.
	 */
	public String getDatabaseName() {
		return databaseName;
	}
	/**
	 * @param databaseName The databaseName to set.
	 */
	public void setDatabaseName(String databaseName) {
		this.databaseName = databaseName;
	}
}