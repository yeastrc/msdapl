package org.yeastrc.yates;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.yeastrc.db.DBConnectionManager;

public class YatesRunMsSearchLinker {

    private  YatesRunMsSearchLinker() {}
    
    public static int linkYatesRunToMsSearch(int yatesRunId) throws SQLException {
        
        // Get our connection to the database.
        Connection conn = DBConnectionManager.getConnection("yrc");
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            // Our SQL statement
            String sqlStr =  "select searchID from tblYatesResultPeptide where resultID = "+"" +
            		"(select id from tblYatesRunResult where runID="+yatesRunId+" limit 1);";
            
            stmt = conn.prepareStatement(sqlStr);

            // Our results
            rs = stmt.executeQuery();

            int searchId = 0;
            if (rs.next()) {
                searchId = rs.getInt("searchID");           
            }

            rs.close(); rs = null;
            stmt.close(); stmt = null;
            conn.close(); conn = null;
            
            return searchId;
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
