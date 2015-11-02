/**
 * ProjectProteinInferBookmarkDAO.java
 * @author Vagisha Sharma
 * Apr 4, 2010
 * @version 1.0
 */
package org.yeastrc.experiment;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.yeastrc.db.DBConnectionManager;

/**
 * 
 */
public class ProjectProteinInferBookmarkDAO {

private static ProjectProteinInferBookmarkDAO instance;
    
    private ProjectProteinInferBookmarkDAO() {}
    
    public static ProjectProteinInferBookmarkDAO getInstance() {
        if(instance == null)
            instance = new ProjectProteinInferBookmarkDAO();
        return instance;
    }
    
    public void deleteBookmark(int pinferId) throws SQLException {
        Connection conn = null;
        Statement stmt = null;
        
        try {
            
            String sql = "DELETE FROM tblProjectProteinInference WHERE "+
                         "piRunID="+pinferId;
                    
            conn = DBConnectionManager.getConnection(DBConnectionManager.MAIN_DB);
            stmt = conn.createStatement();
            stmt.executeUpdate(sql);
            
        } finally {
            
            if (stmt != null) {
                try { stmt.close(); stmt = null; } catch (Exception e) { ; }
            }
            
            if (conn != null) {
                try { conn.close(); conn = null; } catch (Exception e) { ; }
            }           
        }
    }
    
    public void deleteBookmark(int pinferId, int projectId, int researcherId) throws SQLException {
        Connection conn = null;
        Statement stmt = null;
        
        try {
            
            String sql = "DELETE FROM tblProjectProteinInference WHERE "+
                         "piRunID="+pinferId+" AND "+
                         "projectID="+projectId+" AND "+
                         "researcherID="+researcherId;
                    
            conn = DBConnectionManager.getConnection(DBConnectionManager.MAIN_DB);
            stmt = conn.createStatement();
            stmt.executeUpdate(sql);
            
        } finally {
            
            if (stmt != null) {
                try { stmt.close(); stmt = null; } catch (Exception e) { ; }
            }
            
            if (conn != null) {
                try { conn.close(); conn = null; } catch (Exception e) { ; }
            }           
        }
    }
    
    public void saveBookmark(int pinferId, int projectId, int researcherId) throws SQLException {
    	
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        
        try {
        	
        	conn = DBConnectionManager.getConnection(DBConnectionManager.MAIN_DB);
        	stmt = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
        	
            String sql = "SELECT * FROM tblProjectProteinInference WHERE "+
                         "piRunID="+pinferId+" AND "+
                         "projectID="+projectId+" AND "+
                         "researcherID="+researcherId;
                    
            rs = stmt.executeQuery(sql);
            if(!rs.next()){
            	rs.moveToInsertRow();
            	rs.updateInt("projectID", projectId);
            	rs.updateInt("piRunID", pinferId);
            	rs.updateInt("researcherID", researcherId);
            	rs.insertRow();
            }
            
        } finally {
            
            if (stmt != null) {
                try { stmt.close(); stmt = null; } catch (Exception e) { ; }
            }
            
            if (conn != null) {
                try { conn.close(); conn = null; } catch (Exception e) { ; }
            }           
        }
    }
    
    public List<Integer> getBookmarkedProteinInferenceIds(int projectId) throws SQLException {
        
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        
        try {
            
            String sql = "SELECT distinct(piRunID) FROM tblProjectProteinInference WHERE projectID = "+projectId;
                    
            conn = DBConnectionManager.getConnection(DBConnectionManager.MAIN_DB);
            stmt = conn.createStatement();
            rs = stmt.executeQuery(sql);
            
            List<Integer> pinferIds = new ArrayList<Integer>();
            while (rs.next()) {
            	pinferIds.add( rs.getInt("piRunID"));
            }
            return pinferIds;
            
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
    }
}
