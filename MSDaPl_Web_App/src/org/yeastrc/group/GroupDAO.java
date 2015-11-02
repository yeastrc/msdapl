/**
 * GroupDAO.java
 * @author Vagisha Sharma
 * Mar 22, 2009
 * @version 1.0
 */
package org.yeastrc.group;

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

/**
 * 
 */
public class GroupDAO {

    private static final GroupDAO instance = new GroupDAO();
    
    public static GroupDAO instance() {
        return instance;
    }
    
    public Group load(int groupId) throws SQLException, InvalidIDException {
        
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {

            String sql = "SELECT * FROM tblYRCGroups WHERE groupID="+groupId;

            conn = DBConnectionManager.getConnection(DBConnectionManager.MAIN_DB);
            stmt = conn.prepareStatement(sql);
            rs = stmt.executeQuery();
            
            if (rs.next()) {
                Group group = new Group();
                group.setId(rs.getInt("groupID"));
                group.setName(rs.getString("groupName"));
                group.setDescription(rs.getString("groupDesc"));
                return group;
            }
            else {
                throw new InvalidIDException("Load failed due to invalid Group ID.");
            }

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
    
    /**
	 * Returns a Group object for the supplied group name
	 * @param groupName the name of the group
	 * @return Group object, null if not found
	 */
    public Group load(String groupName) throws SQLException, InvalidIDException {
    	
		if (groupName == null) return null;
		
		// Get our connection to the database.
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		try {
			String sqlStr = "SELECT * FROM tblYRCGroups WHERE groupName = ?";
			
			conn = DBConnectionManager.getConnection("yrc");	
			stmt = conn.prepareStatement(sqlStr);
			stmt.setString(1, groupName);
			
			rs = stmt.executeQuery();
			
			if (rs.next()) {
				Group group = new Group();
                group.setId(rs.getInt("groupID"));
                group.setName(rs.getString("groupName"));
                group.setDescription(rs.getString("groupDesc"));
                return group;
			}
			
			rs.close();
			rs = null;
			
			stmt.close();
			stmt = null;
			
			conn.close();
			conn = null;

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
		return null;
	}
    
    public List<Group> loadProjectGroups(int projectId) throws SQLException {
        
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {

            String sql = "SELECT g.*, pg.* "+
            "FROM tblYRCGroups AS g, projectGroup AS pg "+
            "WHERE pg.projectID="+projectId+" "+
            "AND pg.groupID=g.groupID "+
            "ORDER BY g.groupID";

            conn = DBConnectionManager.getConnection(DBConnectionManager.MAIN_DB);
            stmt = conn.prepareStatement(sql);
            rs = stmt.executeQuery();

            List<Group> groups = new ArrayList<Group>();
            while (rs.next()) {
                Group group = new Group();
                group.setId(rs.getInt("groupID"));
                group.setName(rs.getString("groupName"));
                group.setDescription(rs.getString("groupDesc"));
                groups.add(group);
            }
            return groups;

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
    
    public void saveProjectGroups(int projectId, List<Integer>groupIds) throws SQLException {
        
        Connection conn = null;
        Statement stmt = null;
        
        try {
            conn = DBConnectionManager.getConnection(DBConnectionManager.MAIN_DB);
            stmt = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            
            
            // delete old entries
            String sqlStr = "DELETE FROM projectGroup WHERE projectID = " + projectId;
            stmt.executeUpdate(sqlStr);
            stmt.close();
            
            // add new ones
            stmt = conn.createStatement();
            Set<Integer> uniqIds = new HashSet<Integer>(groupIds);
            if(uniqIds.size() == 0)
                return;
            sqlStr = "INSERT INTO projectGroup (projectID, groupID) VALUES ";
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
