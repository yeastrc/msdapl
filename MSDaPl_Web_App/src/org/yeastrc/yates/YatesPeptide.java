/*
 * YatesPeptide.java
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
import org.yeastrc.bio.protein.*;
import org.yeastrc.nr_seq.*;

/**
 * 
 */
public class YatesPeptide implements IData {

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
			String sqlStr = "SELECT * FROM tblYatesResultPeptide WHERE id = " + this.id;
			rs = stmt.executeQuery(sqlStr);

			// See if we're updating a row or adding a new row.
			if (this.id > 0) {

				// Make sure this row is actually in the database.  This shouldn't ever happen.
				if (!rs.next()) {
					throw new InvalidIDException("ID was set for YatesPeptide, but not found in database on save()");
				}

				// Make sure the result set is set up w/ current values from this object
				if (this.resultID == 0) { throw new Exception("No resultID set in ResultPeptide on save()."); }
				else { rs.updateInt("resultID", this.resultID); }

				if (this.unique) { rs.updateString("pepUnique", "T"); }
				else { rs.updateString("pepUnique", "F"); }
				
				if (this.filename == null) { rs.updateNull("filename"); }
				else { rs.updateString("filename", this.filename); }
				
				if (this.XCorr == 0.0) { rs.updateNull("XCorr"); }
				else { rs.updateDouble("XCorr", this.XCorr); }
				
				if (this.deltaCN == 0.0) { rs.updateNull("deltaCN"); }
				else { rs.updateDouble("deltaCN", this.deltaCN); }

				if (this.MH == 0.0) { rs.updateNull("MH"); }
				else { rs.updateDouble("MH", this.MH); }
				
				if (this.calcMH == 0.0) { rs.updateNull("calcMH"); }
				else { rs.updateDouble("calcMH", this.calcMH); }

				if (this.totalIntensity == 0.0) { rs.updateNull("totalIntensity"); }
				else { rs.updateDouble("totalIntensity", this.totalIntensity); }

				if (this.spRank == 0) { rs.updateNull("spRank"); }
				else { rs.updateInt("spRank", this.spRank); }

				if (this.spScore == 0.0) { rs.updateNull("spScore"); }
				else { rs.updateDouble("spScore", this.spScore); }

				if (this.ionProportion == 0.0) { rs.updateNull("ionProportion"); }
				else { rs.updateDouble("ionProportion", this.ionProportion); }
				
				if (this.redundancy == 0) { rs.updateNull("redundancy"); }
				else { rs.updateInt("redundancy", this.redundancy); }
				
				if (this.sequence == null) { rs.updateNull("sequence"); }
				else { rs.updateString("sequence", this.sequence); }
				
				if (this.pI == 0.0) { rs.updateNull("pI"); }
				else { rs.updateDouble("pI", this.pI); }
				
				if (this.confPercent == 0.0) { rs.updateNull("confPercent"); }
				else { rs.updateDouble( "confPercent", this.confPercent ); }

				if (this.ZScore == 0.0) { rs.updateNull("ZScore"); }
				else { rs.updateDouble( "ZScore", this.ZScore ); }
				
				// Update the row
				rs.updateRow();

			} else {
				// We're adding a new row.

				rs.moveToInsertRow();

				if (this.resultID == 0) { throw new Exception("No resultID set in ResultPeptide on save()."); }
				else { rs.updateInt("resultID", this.resultID); }

				if (this.unique) { rs.updateString("pepUnique", "T"); }
				else { rs.updateString("pepUnique", "F"); }
				
				if (this.filename == null) { rs.updateNull("filename"); }
				else { rs.updateString("filename", this.filename); }
				
				if (this.XCorr == 0.0) { rs.updateNull("XCorr"); }
				else { rs.updateDouble("XCorr", this.XCorr); }
				
				if (this.deltaCN == 0.0) { rs.updateNull("deltaCN"); }
				else { rs.updateDouble("deltaCN", this.deltaCN); }

				if (this.MH == 0.0) { rs.updateNull("MH"); }
				else { rs.updateDouble("MH", this.MH); }
				
				if (this.calcMH == 0.0) { rs.updateNull("calcMH"); }
				else { rs.updateDouble("calcMH", this.calcMH); }

				if (this.totalIntensity == 0.0) { rs.updateNull("totalIntensity"); }
				else { rs.updateDouble("totalIntensity", this.totalIntensity); }

				if (this.spRank == 0) { rs.updateNull("spRank"); }
				else { rs.updateInt("spRank", this.spRank); }

				if (this.spScore == 0.0) { rs.updateNull("spScore"); }
				else { rs.updateDouble("spScore", this.spScore); }

				if (this.ionProportion == 0.0) { rs.updateNull("ionProportion"); }
				else { rs.updateDouble("ionProportion", this.ionProportion); }
				
				if (this.redundancy == 0) { rs.updateNull("redundancy"); }
				else { rs.updateInt("redundancy", this.redundancy); }
				
				if (this.sequence == null) { rs.updateNull("sequence"); }
				else { rs.updateString("sequence", this.sequence); }
				
				if (this.pI == 0.0) { rs.updateNull("pI"); }
				else { rs.updateDouble("pI", this.pI); }

				if (this.confPercent == 0.0) { rs.updateNull("confPercent"); }
				else { rs.updateDouble( "confPercent", this.confPercent ); }
				
				if (this.ZScore == 0.0) { rs.updateNull("ZScore"); }
				else { rs.updateDouble( "ZScore", this.ZScore ); }
				
				
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
			String sqlStr = "SELECT * FROM tblYatesResultPeptide WHERE id = " + id;

			// Our results
			rs = stmt.executeQuery(sqlStr);

			// No rows returned.
			if( !rs.next() )
				throw new InvalidIDException("Loading YatesPeptide failed due to invalid ID ( " + id + ".");

			// Populate the object from this row.
			this.id = id;
			this.resultID = rs.getInt("resultID");

			if (rs.getString("pepUnique").equals("T")) this.unique = true;
			else this.unique = false;
			
			this.filename = rs.getString("filename");
			this.XCorr = rs.getDouble("XCorr");
			this.deltaCN = rs.getDouble("deltaCN");
			this.MH = rs.getDouble("MH");
			this.calcMH = rs.getDouble("calcMH");
			this.totalIntensity = rs.getDouble("totalIntensity");
			this.spRank = rs.getInt("spRank");
			this.spScore = rs.getDouble("spScore");
			this.ionProportion = rs.getDouble("ionProportion");
			this.redundancy = rs.getInt("redundancy");
			this.sequence = rs.getString("sequence");
			this.pI = rs.getDouble("pI");
			this.confPercent = rs.getDouble("confPercent");
			this.ZScore = rs.getDouble("ZScore");
			Integer searchIdI = rs.getInt("searchID");
			this.searchId = searchIdI == null ? 0 : searchIdI;
			Integer scanIdI = rs.getInt("scanID");
			this.scanId = scanIdI == null ? 0 : scanIdI;
			
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
			String sqlStr = "SELECT id FROM tblYatesResultPeptide WHERE id = " + this.id;

			// Our results
			rs = stmt.executeQuery(sqlStr);

			// No rows returned.
			if( !rs.next() ) {
				throw new InvalidIDException("Attempted to delete a YatesPeptide not found in the database.");
			}
			
			// Delete the result row (run result).
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
	
	// Instance variables
	private int id;
	private int resultID;
	private int searchId;
    private int scanId;
	private boolean unique;
	private String filename;
	private double XCorr;
	private double deltaCN;
	private double MH;
	private double calcMH;
	private double totalIntensity;
	private int spRank;
	private double spScore;
	private double ionProportion;
	private int redundancy;
	private String sequence;
	private double pI;
	private Peptide peptideObject;
	private double confPercent;
	private double ZScore;
	
	/**
	 * Get the Peptide Sequence object that corresponds to the actual sequence found.
	 * @return
	 * @throws Exception
	 */
	public Peptide getPeptide() throws Exception {
		if (this.peptideObject != null) return this.peptideObject;
		
		NRPeptide peptide = new NRPeptide();
		String seq = this.getSequence();

		if (seq == null) return null;
		if (seq.length() < 5)
			peptide.setSequenceString("");
		else {
			seq = seq.substring(2, seq.length()-2);

			// Remove any non word chars from the sequence
			seq = seq.replaceAll("\\W", "");

			peptide.setSequenceString(seq);
		}
				
		this.peptideObject = peptide;
		return peptide;
	}
	
	/**
	 * Get this peptide formatted w/ HTML for a link to NCBI BLAST for the relevant sequence (minus sequest additions)
	 * @return
	 */
	public String getHTMLSequence() {
		String seq = this.sequence;
		String retStr;
		
		if (this.sequence.length() < 5) { return this.sequence; }
		else {
			retStr = seq.substring(0,2);
			retStr += "<a target=\"ncbi_window\" href=\"http://www.ncbi.nlm.nih.gov/blast/Blast.cgi?CMD=Web&LAYOUT=TwoWindows&AUTO_FORMAT=Semiauto&ALIGNMENTS=50&ALIGNMENT_VIEW=Pairwise&CDD_SEARCH=on&CLIENT=web&COMPOSITION_BASED_STATISTICS=on&DATABASE=nr&DESCRIPTIONS=100&ENTREZ_QUERY=(none)&EXPECT=1000&FILTER=L&FORMAT_OBJECT=Alignment&FORMAT_TYPE=HTML&I_THRESH=0.005&MATRIX_NAME=BLOSUM62&NCBI_GI=on&PAGE=Proteins&PROGRAM=blastp&SERVICE=plain&SET_DEFAULTS.x=41&SET_DEFAULTS.y=5&SHOW_OVERVIEW=on&END_OF_HTTPGET=Yes&SHOW_LINKOUT=yes&QUERY=";
			retStr += seq.substring(2, seq.length()-2) + "\">" + seq.substring(2, seq.length()-2);
			retStr += "</a>" + seq.substring(seq.length()-2, seq.length());
		}

		return retStr;
	}
	
	/**
	 * @return Returns the calcMH.
	 */
	public double getCalcMH() {
		return calcMH;
	}
	/**
	 * @param calcMH The calcMH to set.
	 */
	public void setCalcMH(double calcMH) {
		this.calcMH = calcMH;
	}
	/**
	 * @return Returns the deltaCN.
	 */
	public double getDeltaCN() {
		return deltaCN;
	}
	/**
	 * @param deltaCN The deltaCN to set.
	 */
	public void setDeltaCN(double deltaCN) {
		this.deltaCN = deltaCN;
	}
	/**
	 * @return Returns the filename.
	 */
	public String getFilename() {
		return filename;
	}
	/**
	 * @param filename The filename to set.
	 */
	public void setFilename(String filename) {
		this.filename = filename;
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
	 * @return Returns the ionProportion.
	 */
	public double getIonProportion() {
		return ionProportion;
	}
	/**
	 * @param ionProportion The ionProportion to set.
	 */
	public void setIonProportion(double ionProportion) {
		this.ionProportion = ionProportion;
	}
	/**
	 * @return Returns the mH.
	 */
	public double getMH() {
		return MH;
	}
	/**
	 * @param mh The mH to set.
	 */
	public void setMH(double mh) {
		MH = mh;
	}
	/**
	 * @return Returns the pI.
	 */
	public double getPI() {
		return pI;
	}
	/**
	 * @param pi The pI to set.
	 */
	public void setPI(double pi) {
		pI = pi;
	}
	/**
	 * @return Returns the redundancy.
	 */
	public int getRedundancy() {
		return redundancy;
	}
	/**
	 * @param redundancy The redundancy to set.
	 */
	public void setRedundancy(int redundancy) {
		this.redundancy = redundancy;
	}
	/**
	 * @return Returns the resultID.
	 */
	public int getResultID() {
		return resultID;
	}
	/**
	 * @param resultID The resultID to set.
	 */
	public void setResultID(int resultID) {
		this.resultID = resultID;
	}
	/**
	 * @return Returns the sequence.
	 */
	public String getSequence() {
		return sequence;
	}
	/**
	 * @param sequence The sequence to set.
	 */
	public void setSequence(String sequence) {
		this.sequence = sequence;
	}
	/**
	 * @return Returns the spRank.
	 */
	public int getSpRank() {
		return spRank;
	}
	/**
	 * @param spRank The spRank to set.
	 */
	public void setSpRank(int spRank) {
		this.spRank = spRank;
	}
	/**
	 * @return Returns the spScore.
	 */
	public double getSpScore() {
		return spScore;
	}
	/**
	 * @param spScore The spScore to set.
	 */
	public void setSpScore(double spScore) {
		this.spScore = spScore;
	}
	/**
	 * @return Returns the totalIntensity.
	 */
	public double getTotalIntensity() {
		return totalIntensity;
	}
	/**
	 * @param totalIntensity The totalIntensity to set.
	 */
	public void setTotalIntensity(double totalIntensity) {
		this.totalIntensity = totalIntensity;
	}
	/**
	 * @return Returns the unique.
	 */
	public boolean isUnique() {
		return unique;
	}
	/**
	 * @param unique The unique to set.
	 */
	public void setUnique(boolean unique) {
		this.unique = unique;
	}
	/**
	 * @return Returns the xCorr.
	 */
	public double getXCorr() {
		return XCorr;
	}
	/**
	 * @param corr The xCorr to set.
	 */
	public void setXCorr(double corr) {
		XCorr = corr;
	}
	
	/**
	 * @return Returns the confPercent.
	 */
	public double getConfPercent() {
		return confPercent;
	}
	/**
	 * @param confPercent The confPercent to set.
	 */
	public void setConfPercent(double confPercent) {
		this.confPercent = confPercent;
	}

	/**
	 * @return Returns the zScore.
	 */
	public double getZScore() {
		return ZScore;
	}
	/**
	 * @param score The zScore to set.
	 */
	public void setZScore(double score) {
		ZScore = score;
	}
	
	public int getSearchId() {
	    return this.searchId;
	}
	
	public void setSearchId(int searchId) {
	    this.searchId = searchId;
	}
	
	public int getScanId() {
	    return this.scanId;
	}
	
	public void setScanId(int scanId) {
	    this.scanId = scanId;
	}
	
	public boolean getIsSpectraAvailable() {
	    return scanId != 0 && searchId != 0;
	}
}
