package org.yeastrc.project;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.yeastrc.db.DBConnectionManager;

public class ProjectLiteDAO {

    
    private static ProjectLiteDAO instance;
    
    public static ProjectLiteDAO instance () {
        if(instance != null)
            return instance;
        else {
            instance = new ProjectLiteDAO();
            return instance;
        }
    }
    
    public List<ProjectLite> getResearcherWritableProjects(int researcherId) throws SQLException {
        
        // Get our connection to the database.
        Connection conn = DBConnectionManager.getConnection(DBConnectionManager.MAIN_DB);
        Statement stmt = null;
        ResultSet rs = null;
        
        List<ProjectLite> projects = new ArrayList<ProjectLite>();
        try {
            
            String sql = "select p.projectID, p.projectTitle "+
            "FROM tblProjects AS p "+
            "LEFT OUTER JOIN projectResearcher AS pr "+
            "ON p.projectID = pr.projectID "+
            "WHERE (p.projectPI = "+researcherId+" OR pr.researcherID = "+researcherId+")";

            stmt = conn.createStatement();
            rs = stmt.executeQuery(sql);
            while(rs.next()) {
                ProjectLite pl = new ProjectLite();
                pl.setId(rs.getInt("projectID"));
                pl.setTitle(rs.getString("projectTitle"));
                projects.add(pl);
            }
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
        return projects;
    }
}
