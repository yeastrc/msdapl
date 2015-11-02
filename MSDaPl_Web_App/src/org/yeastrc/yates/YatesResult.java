/*
 * YatesResult.java
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
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.yeastrc.bio.protein.Protein;
import org.yeastrc.data.IData;
import org.yeastrc.data.InvalidIDException;
import org.yeastrc.db.DBConnectionManager;
import org.yeastrc.ms.IMSResult;
import org.yeastrc.ms.IMSRun;
import org.yeastrc.nr_seq.NRProtein;
import org.yeastrc.nr_seq.NRProteinFactory;
import org.yeastrc.project.Project;
/**
 * 
 */
public class YatesResult implements IData, IMSResult {

	public String getFormatedSequenceCoverageMap() throws Exception {
		List<YatesPeptide> peptides = this.getPeptides();
		String parentSequence = this.getHitProtein().getPeptide().getSequenceString();
		
		char[] reschars = parentSequence.toCharArray();
		String[] residues = new String[reschars.length];		// the array of strings, which are the residues of the matched protein
		for (int i = 0; i < reschars.length; i++) { residues[i] = String.valueOf(reschars[i]); }
		reschars = null;
		
		// structure of these maps is: Integer=>Integer (Residue index (0..residues.length))=>(number of peptides marking that residue thusly)
		Map<Integer, Integer> starResidues = new HashMap<Integer, Integer>();		// residues marked with a *
		Map<Integer, Integer> atResidues = new HashMap<Integer, Integer>();			// residues marked with a @
		Map<Integer, Integer> hashResidues = new HashMap<Integer, Integer>();		// residues marked with a #

		for( YatesPeptide ypep : peptides ) {
			
			String peptideSequence = ypep.getPeptide().getSequenceString();
			if (peptideSequence == null) continue;			
			int pepIndex = parentSequence.indexOf( peptideSequence );
			
			// skip this peptide if it's not in the parent protein sequence
			if (pepIndex == -1)
				continue;

			if (ypep.getSequence().indexOf( "*" ) != -1 || ypep.getSequence().indexOf("@") != -1 || ypep.getSequence().indexOf("#") != -1) {
				char[] aas = ypep.getSequence().toCharArray();
				int modCount = 0;
				for (int k = 3; k < aas.length; k++) {
					Integer residueIndex = new Integer( pepIndex + k - 3 - modCount );
					Map<Integer, Integer> countMap = null;
					
					if (aas[k] == '*') {
						countMap = starResidues;
						modCount++;
					} else if (aas[k] == '@') {						
						countMap = atResidues;
						modCount++;
					} else if (aas[k] == '#') {						
						countMap = hashResidues;
						modCount++;
					} else {
						continue;
					}
					
					if (!countMap.containsKey( residueIndex ))
						countMap.put( residueIndex, new Integer( 1 ) );
					else {
						int count = ((Integer)countMap.get( residueIndex)).intValue();
						countMap.remove( residueIndex );
						countMap.put( residueIndex, new Integer( count + 1 ) );
					}					
				}
			}
		}
		
		/* 
		 * at this point, the 3 residues maps should contain a count of the number of peptides reporting the
		 * respective modifications for each reportedly modified residues.
		 * Go through and label each of those with styled <SPAN> tags for labelling
		 * 
		 */
		for ( int index : starResidues.keySet() ) {
			int count = starResidues.get( index );

			if (count == 1)
				residues[index] = "<span class=\"single_star_residue\">" + residues[index] + "</span>";
			else
				residues[index] = "<span class=\"multiple_star_residue\">" + residues[index] + "</span>";
		}
		

		for ( int index : atResidues.keySet() ) {
			int count = atResidues.get( index );

			if (count == 1)
				residues[index] = "<span class=\"single_at_residue\">" + residues[index] + "</span>";
			else
				residues[index] = "<span class=\"multiple_at_residue\">" + residues[index] + "</span>";
		}
		

		for ( int index : hashResidues.keySet() ) {
			int count = hashResidues.get( index );

			if (count == 1)
				residues[index] = "<span class=\"single_hash_residue\">" + residues[index] + "</span>";
			else
				residues[index] = "<span class=\"multiple_hash_residue\">" + residues[index] + "</span>";
		}	
		
		/*
		 * All modified residues in the residues array should be surrounded by appropriately classed <span> tags
		 */
		
		// clean up
		starResidues = null;
		atResidues = null;
		hashResidues = null;
		
		/*
		 * Now add in font tags for labelling covered sequences in the parent sequence
		 */

		for ( YatesPeptide ypep : peptides ) {
			String pseq = ypep.getPeptide().getSequenceString();
			if (pseq == null) continue;
			
			int index = parentSequence.indexOf(pseq);
			if (index == -1) continue;					//shouldn't happen
			if (index > residues.length - 1) continue;	//shouldn't happen

			// Place a red font start at beginning of this sub sequence in main sequence
			residues[index] = "<span class=\"covered_peptide\">" + residues[index];
			
			// this means that the sub-peptide extends beyond the main peptide's sequence... shouldn't happen but check for it
			if (index + pseq.length() > residues.length - 1) {
				
				// just stop the red font at the end of the main sequence string
				residues[residues.length - 1] = residues[residues.length - 1] + "</span>";
			} else {
				
				// add the font end tag after the last residue in the sub sequence
				residues[index + pseq.length() - 1] = residues[index + pseq.length() - 1] + "</span>";
			}
		}
		
		// clean up
		peptides = null;
		parentSequence = null;

		// String array should be set up appropriately with red font tags for sub peptide overlaps, format it into a displayable peptide sequence
		String retStr =	"      1          11         21         31         41         51         \n";
		retStr +=		"      |          |          |          |          |          |          \n";
		retStr +=		"    1 ";

		int counter = 0;
		
		// retStr += "RESIDUE 0: [" + residues[0] + "]";
		
		for (int i = 0; i < residues.length; i++ ) {
			retStr += residues[i];
			
			counter++;
			if (counter % 60 == 0) {
				if (counter < 1000) retStr += " ";
				if (counter< 100) retStr += " ";

				retStr += "<font style=\"color:black;\">" + String.valueOf(counter) + "</font>";
				retStr += "\n ";

				if (counter < 100) retStr += " ";
				if (counter < 1000) retStr += " ";
				retStr += "<font style=\"color:black;\">" + String.valueOf(counter + 1) + "</font> ";

			} else if (counter % 10 == 0) {
				retStr += " ";
			}
		
		}
		
		return retStr;
	}
	
	/*
	 * OLD VERSION OF THIS METHOD, COMMENT OUT
	public String getFormatedSequenceCoverageMap2() throws Exception {
		List peps = this.getPeptides();
		String seq = this.getHitProtein().getPeptide().getSequenceString();

		char[] reschars = seq.toCharArray();
		String[] residues = new String[reschars.length];
		for (int i = 0; i < reschars.length; i++) { residues[i] = String.valueOf(reschars[i]); }
		reschars = null;
		
		Iterator iter = peps.iterator();
		while (iter.hasNext()) {
			YatesPeptide ypep = (YatesPeptide)(iter.next());
			String pseq = ypep.getPeptide().getSequenceString();
			if (pseq == null) continue;
			
			int index = seq.indexOf(pseq);
			if (index == -1) continue;					//shouldn't happen
			if (index > residues.length - 1) continue;	//shouldn't happen

			// Now search for and mark modified residues in BOLD PURPLE
			if (ypep.getSequence().indexOf("*") != -1 || ypep.getSequence().indexOf("@") != -1 || ypep.getSequence().indexOf("#") != -1) {
				char[] aas = ypep.getSequence().toCharArray();
				int starcnt = 0;
				for (int k = 3; k < aas.length; k++) {
					if (aas[k] == '*') {
						residues[index + k - 3 - starcnt] = "<span style=\"background-color:yellow;color:red;font-weight:bold;\">" + residues[index + k - 3 - starcnt] + "</span>";
						starcnt++;

					} else if (aas[k] == '@') {
						residues[index + k - 3 - starcnt] = "<span style=\"background-color:#FFB7EA;color:red;font-weight:bold;\">" + residues[index + k - 3 - starcnt] + "</span>";
						starcnt++;

					} else if (aas[k] == '#') {
						residues[index + k - 3 - starcnt] = "<span style=\"background-color:#BCB7FF;color:red;font-weight:bold;\">" + residues[index + k - 3 - starcnt] + "</span>";
						starcnt++;
					}
				}
			}
			
			// Place a red font start at beginning of this sub sequence in main sequence
			residues[index] = "<font style=\"color:red;\">" + residues[index];
			
			// this means that the sub-peptide extends beyond the main peptide's sequence... shouldn't happen but check for it
			if (index + pseq.length() > residues.length - 1) {
				
				// just stop the red font at the end of the main sequence string
				residues[residues.length - 1] = residues[residues.length - 1] + "</font>";
			} else {
				
				// add the font end tag after the last residue in the sub sequence
				residues[index + pseq.length() - 1] = residues[index + pseq.length() - 1] + "</font>";
			}

		}
		
		// we don't need this anymore... should get into the habit of cleaning up
		peps = null;
		seq = null;
		
		// String array should be set up appropriately with red font tags for sub peptide overlaps, format it into a displayable peptide sequence
		String retStr =	"      1          11         21         31         41         51         \n";
		retStr +=		"      |          |          |          |          |          |          \n";
		retStr +=		"    1 ";

		int counter = 0;
		
		// retStr += "RESIDUE 0: [" + residues[0] + "]";
		
		for (int i = 0; i < residues.length; i++ ) {
			retStr += residues[i];
			
			counter++;
			if (counter % 60 == 0) {
				if (counter < 1000) retStr += " ";
				if (counter< 100) retStr += " ";

				retStr += "<font style=\"color:black;\">" + String.valueOf(counter) + "</font>";
				retStr += "\n ";

				if (counter < 100) retStr += " ";
				if (counter < 1000) retStr += " ";
				retStr += "<font style=\"color:black;\">" + String.valueOf(counter + 1) + "</font> ";

			} else if (counter % 10 == 0) {
				retStr += " ";
			}
		
		}
		
		return retStr;
	}
	*/
	
	/**
	 * Get the MS Run for which this is a result
	 */
	public IMSRun getRun() throws Exception {
		if (this.run == null) {
			YatesMSRunFactory yrf = new YatesMSRunFactory();
			this.run = (YatesRun)(yrf.getRun(this.runID));
		}
		
		return this.run;
	}
	
	/**
	 * Get the Project to which this data belongs
	 * @return
	 * @throws Exception
	 */
	public Project getProject() throws Exception {
		return ((YatesRun)(this.getRun())).getProject();
	}
	
	/**
	 * Set the peptides that belong to this Result.  This will only be called when creating new
	 * Yates Runs via the web upload form.
	 * @param peptides  The peptides to associate with this Result
	 */
	public void setPeptides(List<YatesPeptide> peptides) {
		this.peptides = peptides;
	}
	
	/**
	 * Get the Set of peptides associated with this mass spec result (mass spec hit)
	 */
	public List<YatesPeptide> getPeptides() throws SQLException, Exception {
		if (this.peptides != null)
			return this.peptides;
		
		List<YatesPeptide> retList = new ArrayList<YatesPeptide>();
		
		// Get our connection to the database.
		Connection conn = DBConnectionManager.getConnection("yrc");
		Statement stmt = null;
		ResultSet rs = null;

		try {
			stmt = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);

			// Get our updatable result set
			String sqlStr = "SELECT id FROM tblYatesResultPeptide WHERE resultID = " + this.id;
			rs = stmt.executeQuery(sqlStr);
			
			while (rs.next()) {
				int pepID = rs.getInt(1);
				YatesPeptide yp = new YatesPeptide();
				yp.load(pepID);
				
				retList.add(yp);
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
		
		YatesPeptideSequenceComparator pepcomp = new YatesPeptideSequenceComparator();
		pepcomp.setResult(this);
		Collections.sort(retList, pepcomp);
		pepcomp = null;
		
		this.peptides = retList;
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
			String sqlStr = "SELECT * FROM tblYatesRunResult WHERE id = " + this.id;
			rs = stmt.executeQuery(sqlStr);

			// See if we're updating a row or adding a new row.
			if (this.id > 0) {

				// Make sure this row is actually in the database.  This shouldn't ever happen.
				if (!rs.next()) {
					throw new InvalidIDException("ID was set for YatesRunResult, but not found in database on save()");
				}

				// Make sure the result set is set up w/ current values from this object
				if (this.runID == 0) { throw new Exception("No runID set in RunResult on save()."); }
				else { rs.updateInt("runID", this.runID); }

				if (this.hitProtein == null) {
					throw new Exception("No hitProtein set in RunResult on save().");
				} else {
					rs.updateInt("hitProteinID", ((NRProtein)this.hitProtein).getId());
				}

				if (this.sequenceCount == 0) { rs.updateNull("sequenceCount"); }
				else { rs.updateInt("sequenceCount", this.sequenceCount); }
				
				if (this.spectrumCount == 0) { rs.updateNull("spectrumCount"); }
				else { rs.updateInt("spectrumCount", this.spectrumCount); }
				
				if (this.sequenceCoverage == 0.0) { rs.updateNull("sequenceCoverage"); }
				else { rs.updateDouble("sequenceCoverage", this.sequenceCoverage); }
				
				if (this.length == 0) { rs.updateNull("length"); }
				else { rs.updateInt("length", this.length); }
				
				if (this.molecularWeight == 0) { rs.updateNull("molecularWeight"); }
				else { rs.updateInt("molecularWeight", this.molecularWeight); }
				
				if (this.pI == 0.0) { rs.updateNull("pI"); }
				else { rs.updateDouble("pI", this.pI); }
				
				if (this.validationStatus == null) { rs.updateNull("validationStatus"); }
				else { rs.updateString("validationStatus", this.validationStatus); }
				
				if (this.description == null) { rs.updateNull("description"); }
				else { rs.updateString("description", this.description); }

				// Update the row
				rs.updateRow();

			} else {
				// We're adding a new row.

				rs.moveToInsertRow();

				if (this.runID == 0) { throw new Exception("No runID set in RunResult on save()."); }
				else { rs.updateInt("runID", this.runID); }

				if (this.hitProtein == null) {
					throw new Exception("No hitProtein set in RunResult on save().");
				} else {
					rs.updateInt("hitProteinID", ((NRProtein)this.hitProtein).getId());
				}

				if (this.sequenceCount == 0) { rs.updateNull("sequenceCount"); }
				else { rs.updateInt("sequenceCount", this.sequenceCount); }
				
				if (this.spectrumCount == 0) { rs.updateNull("spectrumCount"); }
				else { rs.updateInt("spectrumCount", this.spectrumCount); }
				
				if (this.sequenceCoverage == 0.0) { rs.updateNull("sequenceCoverage"); }
				else { rs.updateDouble("sequenceCoverage", this.sequenceCoverage); }
				
				if (this.length == 0) { rs.updateNull("length"); }
				else { rs.updateInt("length", this.length); }
				
				if (this.molecularWeight == 0) { rs.updateNull("molecularWeight"); }
				else { rs.updateInt("molecularWeight", this.molecularWeight); }
				
				if (this.pI == 0.0) { rs.updateNull("pI"); }
				else { rs.updateDouble("pI", this.pI); }
				
				if (this.validationStatus == null) { rs.updateNull("validationStatus"); }
				else { rs.updateString("validationStatus", this.validationStatus); }
				
				if (this.description == null) { rs.updateNull("description"); }
				else { rs.updateString("description", this.description); }

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
			String sqlStr = "SELECT * FROM tblYatesRunResult WHERE id = " + id;

			// Our results
			rs = stmt.executeQuery(sqlStr);

			// No rows returned.
			if( !rs.next() )
				throw new InvalidIDException("Loading YatesRun failed due to invalid ID ( " + id + ".");

			// Populate the object from this row.
			this.id = id;
			this.runID = rs.getInt("runID");
			
			NRProteinFactory nrpf = NRProteinFactory.getInstance();
			this.hitProtein = (NRProtein)(nrpf.getProtein(rs.getInt("hitProteinID")));
			
			this.sequenceCount = rs.getInt("sequenceCount");
			this.spectrumCount = rs.getInt("spectrumCount");
			this.sequenceCoverage = rs.getDouble("sequenceCoverage");
			this.length = rs.getInt("length");
			this.molecularWeight = rs.getInt("molecularWeight");
			this.pI = rs.getDouble("pI");
			this.validationStatus = rs.getString("validationStatus");
			this.description = rs.getString("description");
			
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
			String sqlStr = "SELECT id FROM tblYatesRunResult WHERE id = " + this.id;

			// Our results
			rs = stmt.executeQuery(sqlStr);

			// No rows returned.
			if( !rs.next() ) {
				throw new InvalidIDException("Attempted to delete a YatesRun not found in the database.");
			}

			// Delete all results associated with this run
			List peptides = this.getPeptides();
			Iterator iter = peptides.iterator();
			while (iter.hasNext()) {
				YatesPeptide yp = (YatesPeptide)(iter.next());
				yp.delete();
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
	
	
	// Constructor
	public YatesResult() {
		this.id = 0;
		this.runID = 0;
		this.hitProtein = null;
		this.sequenceCount = 0;
		this.spectrumCount = 0;
		this.sequenceCoverage = 0.0;
		this.length = 0;
		this.molecularWeight = 0;
		this.pI = 0;
		this.validationStatus = null;
		this.description = null;
		this.run = null;
	}

	// Instance variables
	private int id;
	private int runID;
	private NRProtein hitProtein;
	private int sequenceCount;
	private int spectrumCount;
	private double sequenceCoverage;
	private int length;
	private int molecularWeight;
	private double pI;
	private String validationStatus;
	private String description;
	private YatesRun run;
	private List<YatesPeptide> peptides;

	/**
	 * @return Returns the description.
	 */
	public String getDescription() {
		return description;
	}
	/**
	 * @param description The description to set.
	 */
	public void setDescription(String description) {
		this.description = description;
	}
	/**
	 * @return Returns the hitORF.
	 */
	public Protein getHitProtein() {
		return hitProtein;
	}
	/**
	 * @param hitORF The hitORF to set.
	 */
	public void setHitProtein(NRProtein hitProtein) {
		this.hitProtein = hitProtein;
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
	 * @return Returns the length.
	 */
	public int getLength() {
		return length;
	}
	/**
	 * @param length The length to set.
	 */
	public void setLength(int length) {
		this.length = length;
	}
	/**
	 * @return Returns the molecularWeight.
	 */
	public int getMolecularWeight() {
		return molecularWeight;
	}
	/**
	 * @param molecularWeight The molecularWeight to set.
	 */
	public void setMolecularWeight(int molecularWeight) {
		this.molecularWeight = molecularWeight;
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
	 * @return Returns the runID.
	 */
	public int getRunID() {
		return runID;
	}
	/**
	 * @param runID The runID to set.
	 */
	public void setRunID(int runID) {
		this.runID = runID;
	}
	/**
	 * @return Returns the sequenceCount.
	 */
	public int getSequenceCount() {
		return sequenceCount;
	}
	/**
	 * @param sequenceCount The sequenceCount to set.
	 */
	public void setSequenceCount(int sequenceCount) {
		this.sequenceCount = sequenceCount;
	}
	/**
	 * @return Returns the sequenceCoverage.
	 */
	public double getSequenceCoverage() {
		return sequenceCoverage;
	}
	/**
	 * @param sequenceCoverage The sequenceCoverage to set.
	 */
	public void setSequenceCoverage(double sequenceCoverage) {
		this.sequenceCoverage = sequenceCoverage;
	}
	/**
	 * @return Returns the spectrumCount.
	 */
	public int getSpectrumCount() {
		return spectrumCount;
	}
	/**
	 * @param spectrumCount The spectrumCount to set.
	 */
	public void setSpectrumCount(int spectrumCount) {
		this.spectrumCount = spectrumCount;
	}
	/**
	 * @return Returns the validationStatus.
	 */
	public String getValidationStatus() {
		return validationStatus;
	}
	/**
	 * @param validationStatus The validationStatus to set.
	 */
	public void setValidationStatus(String validationStatus) {
		this.validationStatus = validationStatus;
	}
}