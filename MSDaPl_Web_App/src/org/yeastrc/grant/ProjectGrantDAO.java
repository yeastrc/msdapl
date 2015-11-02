package org.yeastrc.grant;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import org.yeastrc.db.DBConnectionManager;

public class ProjectGrantDAO {

	private static ProjectGrantDAO instance = new ProjectGrantDAO();
	
	private ProjectGrantDAO(){}
	
	public static ProjectGrantDAO getInstance() {
		return instance;
	}
	
	public void saveProjectGrants(int projectID, List<Grant> grants) throws SQLException {
		
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		
		try {
			// first clear all the existing grants for the project
			String sql = "DELETE FROM projectGrant WHERE projectID=" + projectID;
			conn = DBConnectionManager.getConnection(DBConnectionManager.MAIN_DB);
			stmt = conn.createStatement();
			int deleted = stmt.executeUpdate(sql);
			if (deleted == 0) {
				System.out.println("All old project grants deleted");
			}

			stmt.close(); stmt = null;
			conn.close(); conn = null;
			
			// now save the given grants
			conn = DBConnectionManager.getConnection(DBConnectionManager.MAIN_DB);
			stmt = conn.createStatement();
			for(Grant grant: grants) {
				if (grant.getID() <= 0)	continue;
				sql = "INSERT into projectGrant values(0,"+projectID+","+grant.getID()+")";
				stmt.executeUpdate(sql);
			}
			stmt.close(); stmt = null;
			conn.close(); conn = null;

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
}
