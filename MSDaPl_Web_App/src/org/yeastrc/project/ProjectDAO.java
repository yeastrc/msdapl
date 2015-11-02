/**
 * ProjectDAO.java
 * @author Vagisha Sharma
 * Mar 22, 2009
 * @version 1.0
 */
package org.yeastrc.project;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.yeastrc.data.InvalidIDException;
import org.yeastrc.db.DBConnectionManager;
import org.yeastrc.grant.GrantDAO;
import org.yeastrc.grant.ProjectGrantDAO;
import org.yeastrc.group.Group;
import org.yeastrc.group.GroupDAO;
import org.yeastrc.project.payment.ProjectPaymentMethodDAO;

/**
 * 
 */
public class ProjectDAO {

    private static final ProjectDAO instance = new ProjectDAO();
    
    public static ProjectDAO instance() {
        return instance;
    }
    
    public int save(Project project) throws SQLException, InvalidIDException {
        
        // Get our connection to the database.
        Connection conn = DBConnectionManager.getConnection(DBConnectionManager.MAIN_DB);
        Statement stmt = null;
        ResultSet rs = null;    

        try {
            stmt = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);

            // Get our updatable result set
            String sqlStr = "SELECT * FROM tblProjects WHERE projectID = " + project.getID();
            rs = stmt.executeQuery(sqlStr);

            
            // See if we're updating a row or adding a new row.
            if (project.getID() > 0) {

                // Make sure this row is actually in the database.  This shouldn't ever happen.
                if (!rs.next()) {
                    throw new InvalidIDException("ID was set in a Project, but not found in database on save()");
                }
            }
            else {
                rs.moveToInsertRow();

                java.util.Date uDate = new java.util.Date();
                project.submitDate = new java.sql.Date(uDate.getTime());
                rs.updateDate("projectSubmitDate", project.submitDate);
            }
            
            if (project.getTitle() == null) { rs.updateNull("projectTitle"); }
            else { rs.updateString("projectTitle", project.getTitle()); }

            /*
             * Update our researchers.  The value for the researcher ID will be taken from
             * the respective researcher object and set in the Project table.  If there is no
             * respective researcher object (it didn't exist when the Project object was loaded, and non were created)
             * it will be set to 0.  This should be a self-correcting system if Researchers are
             * deleted from the system causing Projects to contain invalid researcher IDs.
             */
            if (project.getPI() != null) {
                rs.updateInt("projectPI", project.getPI().getID());
            } else { rs.updateNull("projectPI"); }
            
            if (project.getAbstract() == null) { rs.updateNull("projectAbstract"); }
            else { rs.updateString("projectAbstract", project.getAbstract()); }

            if (project.getProgress() == null) { rs.updateNull("projectProgress"); }
            else { rs.updateString("projectProgress", project.getProgress()); }
            
            if (project.getPublications() == null) { rs.updateNull("projectPublications"); }
            else { rs.updateString("projectPublications", project.getPublications()); }

            if (project.getComments() == null) { rs.updateNull("projectComments"); }
            else { rs.updateString("projectComments", project.getComments()); }

            if (project.getProgressLastChange() == null) { rs.updateNull("progressLastChange"); }
            else { rs.updateDate("progressLastChange", project.getProgressLastChange()); }
            
            if (project.getAffiliation() == null) {rs.updateNull("affiliation");}
            else rs.updateString("affiliation", project.getAffiliation().name());
            
            // See if we're updating a row or adding a new row.
            if (project.getID() > 0) {
                // Update the row
                rs.updateRow();
            }
            else {
               
                rs.insertRow();

                // Get the ID generated for this item from the database, and set expID
                rs.last();
                project.id = rs.getInt("projectID");
            }
            
            try {
                project.lastChange = rs.getDate("lastChange");
            } catch (Exception e) { ; }
            
            // save the project researchers
            List<Integer> researcherIds = new ArrayList<Integer>(project.getResearchers().size());
            for(Researcher r: project.getResearchers()) if(r != null)   researcherIds.add(r.getID());
            saveResearchersForProject(project.getID(), researcherIds);
            
            // save the project grants;
            ProjectGrantDAO.getInstance().saveProjectGrants(project.getID(), project.getGrants());
            
            // save the project groups
            List<Integer> groupIds = new ArrayList<Integer>(project.getGroups().size());
            for(Group grp: project.getGroups()) if(grp != null) groupIds.add(grp.getId());
            GroupDAO.instance().saveProjectGroups(project.getID(), groupIds);
            
            return project.getID();
            
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
     * Use this method to populate this object with data from the database.
     * @param id The project ID to load.
     * @throws InvalidIDException If this ID is not valid (or not found)
     * @throws SQLException If there is a problem interracting with the database
     */
    public Project load(int id) throws InvalidIDException, SQLException {

        Project project = new Project();
        
        // Get our connection to the database.
        Connection conn = DBConnectionManager.getConnection(DBConnectionManager.MAIN_DB);
        Statement stmt = null;
        ResultSet rs = null;    

        try{
            stmt = conn.createStatement();

            // Our SQL statement
            String sqlStr = "SELECT * FROM tblProjects WHERE projectID = " + id;

            // Our results
            rs = stmt.executeQuery(sqlStr);

            // No rows returned.
            if( !rs.next() ) {
                throw new InvalidIDException("Load failed due to invalid Project ID.");
            }

            // Populate the object from this row.
            project.id = rs.getInt("projectID");
            project.submitDate = rs.getDate("projectSubmitDate");
            project.setTitle(rs.getString("projectTitle"));
            
            /*
             * Populate the researchers associated with this project.  If a problem is
             * encountered loading a researcher, we will catch that here and set the object
             * to null, instead of throwing back an exception.  This enables invalid entries
             * to be entered into the database for researcher IDs (or for researchers to somehow
             * be deleted from the database) in the Project table without
             * blowing up project display pages.
             */
            int tmpID;
            tmpID = rs.getInt("projectPI");
            if (tmpID != 0) {
                Researcher PI = new Researcher();
                try { PI.load(tmpID); }
                catch (InvalidIDException e) { PI = null; }
                project.setPI(PI);
            }

            // Set up the rest of the object variables
            project.setAbstract(rs.getString("projectAbstract"));
            project.setProgress(rs.getString("projectProgress"));
            project.setPublications(rs.getString("projectPublications"));
            project.setComments(rs.getString("projectComments"));
            project.lastChange = rs.getDate("lastChange");
            project.progressLastChange = rs.getDate("progressLastChange");
            project.setAffiliation(Affiliation.forName(rs.getString("affiliation")));
            
            project.setPaymentMethods(ProjectPaymentMethodDAO.getInstance().getPaymentMethods(project.id));
            
            
            rs.close();
            rs = null;
            
            stmt.close();
            stmt = null;
            
            conn.close();
            conn = null;
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
        
        // set the researchers for this project
        project.setResearchers(getResearchersForProject(project.getID()));
        
        // set the grants for this project
        project.setGrants(GrantDAO.getInstance().getGrantsForProject(project.getID()));
        
        // set the groups for this project
        project.setGroups(GroupDAO.instance().loadProjectGroups(project.getID()));
        
        return project;
    }
    
    public void delete(int projectId) throws InvalidIDException, SQLException {

        // Get our connection to the database.
        Connection conn = DBConnectionManager.getConnection(DBConnectionManager.MAIN_DB);
        Statement stmt = null;
        ResultSet rs = null;

        try {
            stmt = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);

            // Our SQL statement
            String sqlStr = "SELECT projectID FROM tblProjects WHERE projectID = " + projectId;

            // Our results
            rs = stmt.executeQuery(sqlStr);

            // No rows returned.
            if( !rs.next() ) {
                throw new InvalidIDException("Attempted to delete a Project not found in the database.");
            }

            // Delete the result row.
            // Triggers should delete all related entries
            rs.deleteRow();     

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
    
    private List<Researcher> getResearchersForProject(int projectId) throws SQLException {
        
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {

            String sql = "SELECT * FROM projectResearcher WHERE projectID = "+projectId;

            conn = DBConnectionManager.getConnection(DBConnectionManager.MAIN_DB);
            stmt = conn.prepareStatement(sql);
            rs = stmt.executeQuery();

            List<Researcher> researchers = new ArrayList<Researcher>();
            while (rs.next()) {
                int tmpId = (rs.getInt("researcherID"));
                Researcher r = new Researcher();
                try {r.load(tmpId);}
                catch(InvalidIDException e) { r = null;}
                if(r != null)
                    researchers.add(r);
            }
            return researchers;

        } finally {

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
    
    private void saveResearchersForProject(int projectId, List<Integer> researcherIds) throws SQLException {
        
        Connection conn = null;
        Statement stmt = null;
        
        try {
            conn = DBConnectionManager.getConnection(DBConnectionManager.MAIN_DB);
            stmt = conn.createStatement();

            
            // delete old entries
            String sqlStr = "DELETE FROM projectResearcher WHERE projectID = " + projectId;
            stmt.executeUpdate(sqlStr);
            stmt.close();
            
            // add new ones
            stmt = conn.createStatement();
            Set<Integer> uniqIds = new HashSet<Integer>(researcherIds);
            if(uniqIds.size() == 0)
                return;
            sqlStr = "INSERT INTO projectResearcher (projectID, researcherID) VALUES ";
            for(Integer id: uniqIds) {
                sqlStr += "("+projectId+","+id+"),";
            }
            sqlStr = sqlStr.substring(0, sqlStr.length() - 1); // remove last comma
            stmt.executeUpdate(sqlStr);

        } finally {

            // Always make sure result sets and statements are closed,
            // and the connection is returned to the pool
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
