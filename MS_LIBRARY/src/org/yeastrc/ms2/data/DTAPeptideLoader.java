/**
 * DTAPeptideLoader.java
 * @author Vagisha Sharma
 * Sep 15, 2008
 * @version 1.0
 */
package org.yeastrc.ms2.data;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Connection;


/**
 * 
 */
public class DTAPeptideLoader {

    private static final DTAPeptideLoader instance = new DTAPeptideLoader();
    
    public static DTAPeptideLoader getInstance() {
        return instance;
    }
    
    public DTAPeptide load (int id, Connection conn) throws Exception{
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
                throw new Exception("Loading YatesPeptide failed due to invalid ID ( " + id + ".");

            // Populate the object from this row.
            DTAPeptide peptide = new DTAPeptide();
            peptide.setId(id);
            peptide.setResultID(rs.getInt("resultID"));

            if (rs.getString("pepUnique").equals("T")) 
                peptide.setUnique(true);
            else 
                peptide.setUnique(false);
            
            peptide.setFilename(rs.getString("filename"));
            peptide.setXCorr(rs.getDouble("XCorr"));
            peptide.setDeltaCN(rs.getDouble("deltaCN"));
            peptide.setMH(rs.getDouble("MH"));
            peptide.setCalcMH(rs.getDouble("calcMH"));
            peptide.setTotalIntensity(rs.getDouble("totalIntensity"));
            peptide.setSpRank(rs.getInt("spRank"));
            peptide.setSpScore(rs.getDouble("spScore"));
            peptide.setIonProportion(rs.getDouble("ionProportion"));
            peptide.setRedundancy(rs.getInt("redundancy"));
            peptide.setSequence(rs.getString("sequence"));
            peptide.setPI(rs.getDouble("pI"));
            peptide.setConfPercent(rs.getDouble("confPercent"));
            peptide.setZScore(rs.getDouble("ZScore"));
            Integer searchIdI = rs.getInt("searchID");
            peptide.setSearchID(searchIdI == null ? 0 : searchIdI);
            Integer scanIdI = rs.getInt("scanID");
            peptide.setScanID(scanIdI == null ? 0 : scanIdI);
            
            // Close up shop
            rs.close(); rs = null;
            stmt.close(); stmt = null;
            conn.close(); conn = null;
            
            return peptide;
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
}
