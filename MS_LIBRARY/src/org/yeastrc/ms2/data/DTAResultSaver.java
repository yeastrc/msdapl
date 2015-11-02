/**
 * 
 */
package org.yeastrc.ms2.data;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * @author Mike
 *
 */
public class DTAResultSaver {

	private DTAResultSaver() { }
	
	/**
	 * Get an instance of this class
	 * @return
	 */
	public static DTAResultSaver getInstance() {
		return new DTAResultSaver();
	}
	
	
	/**
	 * Save the DTAResult to the database
	 * @param result
	 * @return
	 * @throws Exception
	 */
	public int save( DTAResult result, Connection conn ) throws Exception {
		int id = 0;
		Statement stmt = null;
		ResultSet rs = null;
		
		if (result.getId() != 0)
			throw new Exception( "Result is already in the database." );
		
		try {
			stmt = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
			
			String sql = "SELECT * FROM tblYatesRunResult WHERE id = " + result.getId();
			rs = stmt.executeQuery( sql );
			
			rs.moveToInsertRow();

			if ( result.getRunID() == 0 ) { throw new Exception("No runID set in RunResult on save()."); }
			else { rs.updateInt( "runID", result.getRunID() ); }

			if ( result.getHitProtein() == 0 ) {
				throw new Exception("No hitProtein set in RunResult on save().");
			} else {
				rs.updateInt( "hitProteinID", result.getHitProtein() );
			}

			if (result.getSequenceCount() == 0) { rs.updateNull("sequenceCount"); }
			else { rs.updateInt("sequenceCount", result.getSequenceCount() ); }
			
			if (result.getSpectrumCount() == 0) { rs.updateNull( "spectrumCount" ); }
			else { rs.updateInt( "spectrumCount", result.getSpectrumCount() ); }
			
			if (result.getSequenceCoverage() == 0.0) { rs.updateNull("sequenceCoverage"); }
			else { rs.updateDouble("sequenceCoverage", result.getSequenceCoverage()); }
			
			if (result.getLength() == 0) { rs.updateNull("length"); }
			else { rs.updateInt("length", result.getLength()); }
			
			if (result.getMolecularWeight() == 0) { rs.updateNull("molecularWeight"); }
			else { rs.updateInt("molecularWeight", result.getMolecularWeight()); }
			
			if (result.getPI() == 0.0) { rs.updateNull("pI"); }
			else { rs.updateDouble("pI", result.getPI()); }
			
			if (result.getValidationStatus() == null) { rs.updateNull("validationStatus"); }
			else { rs.updateString("validationStatus", result.getValidationStatus()); }
			
			if (result.getDescription() == null) { rs.updateNull("description"); }
			else { rs.updateString("description", result.getDescription()); }

			rs.insertRow();
			rs.last();
			
			id = rs.getInt( "id" );
			result.setId( id );
			
			rs.close(); rs = null;
			stmt.close(); stmt = null;
			conn.close(); conn = null;
			
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
		
		
		return id;
	}
	
}
