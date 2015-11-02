/**
 * 
 */
package org.yeastrc.ms2.data;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * @author Mike
 *
 */
public class DTAPeptideSaver {

	private DTAPeptideSaver() { }
	
	/**
	 * Get an instance of this class
	 * @return
	 */
	public static DTAPeptideSaver getInstance() {
		return new DTAPeptideSaver();
	}
	
	public void update (DTAPeptide peptide, Connection conn) throws Exception {
	       
        Statement stmt = null;
        ResultSet rs = null;

        try {
            stmt = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);

            // Get our updatable result set
            String sqlStr = "SELECT * FROM tblYatesResultPeptide WHERE id = " + peptide.getId();
            rs = stmt.executeQuery(sqlStr);
            
            // Make sure this row is actually in the database.  This shouldn't ever happen.
            if (!rs.next()) {
                throw new Exception("ID was set for YatesPeptide, but not found in database on save()");
            }
            
            updateResultSet(peptide, rs);
            
            // Update the row
            rs.updateRow();
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
	 * Save this peptide to the database
	 * @param peptide
	 * @return
	 * @throws Exception
	 */
	public int save( DTAPeptide peptide, Connection conn ) throws Exception {
		int id = 0;
		Statement stmt = null;
		ResultSet rs = null;
		
		if (peptide.getId() != 0)
			throw new Exception( "Peptide is already in the database." );
		
		try {
			stmt = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
			
			String sql = "SELECT * FROM tblYatesResultPeptide WHERE id = " + peptide.getId();
			rs = stmt.executeQuery( sql );
			
			rs.moveToInsertRow();
			
			updateResultSet(peptide, rs);
			
			rs.insertRow();
			rs.last();
			
			id = rs.getInt( "id" );
			peptide.setId( id );
			
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
	
	private void updateResultSet(DTAPeptide peptide, ResultSet rs) throws Exception {
	    
	    if (peptide.getResultID() == 0) { throw new Exception("No resultID set in ResultPeptide on save()."); }
        else { rs.updateInt("resultID", peptide.getResultID() ); }
        
        if (peptide.getSearchID() != 0) { 
            rs.updateInt("searchID", peptide.getSearchID()); 
        }
        
        if (peptide.getScanID() != 0) { 
            rs.updateInt("scanID", peptide.getScanID()); 
        }

        if (peptide.isUnique()) { rs.updateString("pepUnique", "T"); }
        else { rs.updateString("pepUnique", "F"); }
        
        if (peptide.getFilename() == null) { rs.updateNull("filename"); }
        else { rs.updateString("filename", peptide.getFilename() ); }
        
        if (peptide.getXCorr() == 0.0) { rs.updateNull("XCorr"); }
        else { rs.updateDouble("XCorr", peptide.getXCorr()); }
        
        if (peptide.getDeltaCN() == 0.0) { rs.updateNull("deltaCN"); }
        else { rs.updateDouble("deltaCN", peptide.getDeltaCN()); }

        if (peptide.getMH() == 0.0) { rs.updateNull("MH"); }
        else { rs.updateDouble("MH", peptide.getMH()); }
        
        if (peptide.getCalcMH() == 0.0) { rs.updateNull("calcMH"); }
        else { rs.updateDouble("calcMH", peptide.getCalcMH()); }

        if (peptide.getTotalIntensity() == 0.0) { rs.updateNull("totalIntensity"); }
        else { rs.updateDouble("totalIntensity", peptide.getTotalIntensity()); }

        if (peptide.getSpRank() == 0) { rs.updateNull("spRank"); }
        else { rs.updateInt("spRank", peptide.getSpRank()); }

        if (peptide.getSpScore() == 0.0) { rs.updateNull("spScore"); }
        else { rs.updateDouble("spScore", peptide.getSpScore() ); }

        if (peptide.getIonProportion() == 0.0) { rs.updateNull("ionProportion"); }
        else { rs.updateDouble("ionProportion", peptide.getIonProportion()); }
        
        if (peptide.getRedundancy() == 0) { rs.updateNull("redundancy"); }
        else { rs.updateInt("redundancy", peptide.getRedundancy()); }
        
        if (peptide.getSequence() == null) { rs.updateNull("sequence"); }
        else { rs.updateString("sequence", peptide.getSequence()); }
        
        if (peptide.getPI() == 0.0) { rs.updateNull("pI"); }
        else { rs.updateDouble("pI", peptide.getPI()); }

        if (peptide.getConfPercent() == 0.0) { rs.updateNull("confPercent"); }
        else { rs.updateDouble( "confPercent", peptide.getConfPercent() ); }
        
        if (peptide.getZScore() == 0.0) { rs.updateNull("ZScore"); }
        else { rs.updateDouble( "ZScore", peptide.getZScore() ); }
        
        if (peptide.getPpm() == null) { rs.updateNull( "ppm" ); }
        else { rs.updateString( "ppm", peptide.getPpm() ); }
    }
}
