/**
 * GONRProteinCounter.java
 * Created by: Michael Riffle <mriffle@u.washington.edu>
 * Created on: Mar 29, 2007 at 12:51:39 PM
 */

package org.yeastrc.www.go;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.yeastrc.bio.go.GONode;
import org.yeastrc.db.DBConnectionManager;

public class GOProteinCounter {

	// private constructor
	private GOProteinCounter()  { }

	/**
	 * Get an instance of this class
	 * @return
	 */
	public static GOProteinCounter getInstance() {
		return new GOProteinCounter();
	}
	
	/**
     * Return the number of proteins in the YRC NR_SEQ database that are annotated with this GO term
     * @param node The GO term we're testing
     * @param speciesID proteins with this speciesID are counted
     * @return The number of proteins annotated with the given GO term
     * @throws Exception
     */
    public int countProteins( GONode node, int speciesId ) throws Exception {
        
       return getPrecomputedCount(node, speciesId, false);
    }
    
    /**
     * 
     * @param node The GO term we're testing
     * @param speciesID proteins with this speciesID are counted
     * @param exact If true, the number of proteins annotated exactly with this term are returned.
     * @return The number of proteins annotated with the given GO term
     * @throws Exception
     */
    public int countProteins( GONode node, int speciesId, boolean exact) throws Exception {
    	return getPrecomputedCount(node, speciesId, exact);
    }
    
    private int getPrecomputedCount(GONode node, int speciesId, boolean exact) throws Exception {
        
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = DBConnectionManager.getConnection("go");
            String sql = "SELECT ";
            if(exact)
            	sql += " exactProteinCount FROM YRC_NRSEQ_GO_COUNTS_Ref WHERE goAcc = ? and speciesID = ?";
            else
            	sql += " proteinCount FROM YRC_NRSEQ_GO_COUNTS_Ref WHERE goAcc = ? and speciesID = ?";
            
            stmt = conn.prepareStatement( sql );
            stmt.setString( 1, node.getAccession() );
            stmt.setInt(2, speciesId);
            rs = stmt.executeQuery();

            if (rs.next())
               return rs.getInt( 1 );

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
        return 0;
    }
}
