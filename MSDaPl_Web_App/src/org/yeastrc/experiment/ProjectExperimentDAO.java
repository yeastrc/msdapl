/**
 * ProjectExperimentDAO.java
 * @author Vagisha Sharma
 * Apr 2, 2009
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
import org.yeastrc.project.Project;

/**
 * 
 */
public class ProjectExperimentDAO {

    private static ProjectExperimentDAO instance;
    
    private ProjectExperimentDAO() {}
    
    public static ProjectExperimentDAO instance() {
        if(instance == null)
            instance = new ProjectExperimentDAO();
        return instance;
    }
    
    public List<Integer> getExperimentIdsForProjects(List<Project> projects) throws SQLException {
        if(projects == null || projects.size() == 0) 
            return new ArrayList<Integer>(0);
        
        String projIdStr = "";
        for(Project proj: projects) {
            projIdStr += ","+proj.getID();
        }
        projIdStr = projIdStr.substring(1); // remove first comma
        
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        
        try {
            
            String sql = "SELECT experimentID FROM tblProjectExperiment WHERE projectID in ("+projIdStr+")";
                    
            conn = DBConnectionManager.getConnection(DBConnectionManager.MAIN_DB);
            stmt = conn.createStatement();
            rs = stmt.executeQuery(sql);
            
            List<Integer> experimentIds = new ArrayList<Integer>();
            while (rs.next()) {
                experimentIds.add( rs.getInt("experimentID"));
            }
            return experimentIds;
            
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
    
    public List<Integer> getExperimentIdsForProject(int projectId) throws SQLException {
        
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        
        try {
            
            String sql = "SELECT experimentID FROM tblProjectExperiment WHERE projectID="+projectId;
                    
            conn = DBConnectionManager.getConnection(DBConnectionManager.MAIN_DB);
            stmt = conn.createStatement();
            rs = stmt.executeQuery(sql);
            
            List<Integer> experimentIds = new ArrayList<Integer>();
            while (rs.next()) {
                experimentIds.add( rs.getInt("experimentID"));
            }
            return experimentIds;
            
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
    
    public void deleteProjectExperiment(int experimentId) throws SQLException {
        Connection conn = null;
        Statement stmt = null;
        
        try {
            
            String sql = "DELETE FROM tblProjectExperiment WHERE experimentID="+experimentId;
                    
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
    
    public List<Integer> getProjectIdsForExperiments(List<Integer> experimentIds) throws SQLException {
        if(experimentIds == null || experimentIds.size() == 0) 
            return new ArrayList<Integer>(0);
        
        String exptIdStr = "";
        for(Integer exptId: experimentIds) {
            exptIdStr += ","+exptId;
        }
        exptIdStr = exptIdStr.substring(1); // remove first comma
        
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        
        try {
            
            String sql = "SELECT distinct(projectID) FROM tblProjectExperiment WHERE experimentID in ("+exptIdStr+")";
                    
            conn = DBConnectionManager.getConnection(DBConnectionManager.MAIN_DB);
            stmt = conn.createStatement();
            rs = stmt.executeQuery(sql);
            
            List<Integer> projectIds = new ArrayList<Integer>();
            while (rs.next()) {
                projectIds.add( rs.getInt("projectID"));
            }
            return projectIds;
            
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
    
    public int getProjectIdForExperiment(int experimentId) throws SQLException {
        
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        
        try {
            
            String sql = "SELECT projectID FROM tblProjectExperiment WHERE experimentID = "+experimentId+" ";
                    
            conn = DBConnectionManager.getConnection(DBConnectionManager.MAIN_DB);
            stmt = conn.createStatement();
            rs = stmt.executeQuery(sql);
            
            if (rs.next()) {
                return rs.getInt("projectID");
            }
            
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
        return 0;
    }
    
}
