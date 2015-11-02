/**
 * LabDirector.java
 * @author Vagisha Sharma
 * Mar 25, 2010
 * @version 1.0
 */
package org.yeastrc.www.project;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.yeastrc.db.DBConnectionManager;
import org.yeastrc.www.user.Groups;


/**
 * 
 */
public class LabDirector {

	private LabDirector() {}
	
	private static final Logger log = Logger.getLogger(LabDirector.class.getName());
	
	// key = group name; value = Lab Director's researcherID
	private static Map<String, Integer> groupLabDirectorMap = new HashMap<String, Integer>();
	
	/**
	 * Returns the ID of the lab director of first group in the list that has a lab director. 
	 * "administrators" group is not considered. 
	 * Return value is 0 if none of the groups have a lab director.
	 * @param groupNames
	 * @return
	 */
	public static int get(List<String> groupNames) {
		
		if(groupNames == null || groupNames.size() == 0)
			return 0;
		
		groupNames.remove("administrators");  // remove "administrators" group.
		
		
		if(groupNames.size() > 0) {
			for(String groupName: groupNames) {
				
				int labDirectorId = get(groupName);
				if(labDirectorId != 0)
					return labDirectorId;
			}
		}
		return 0;
	}

	public static int get(String groupName) {
		
		Integer groupId = groupLabDirectorMap.get(groupName);
		
		if(groupId != null)
			return groupId;
		
		try {
			groupId = Groups.getInstance().getGroupID(groupName);
		} catch (SQLException e) {
			log.error("Error getting groupID for group: "+groupName, e);
			return 0;
		}
		if(groupId != 0) {
			int labDirectorId = get(groupId);
			groupLabDirectorMap.put(groupName, labDirectorId);
			return labDirectorId;
		}
		else
			return 0;
	}
	
	private static int get(int groupId) {
		
		// Get our connection to the database.
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;

		try {
			conn = DBConnectionManager.getConnection(DBConnectionManager.MAIN_DB);
			stmt = conn.createStatement();
			String sql = "SELECT researcherID FROM tblLabDirector WHERE groupID = "+groupId;
			rs = stmt.executeQuery(sql);

			if (rs.next()) {
				return rs.getInt("researcherID");
			}
			else
				return 0;
		}
		catch(SQLException e) {
			log.error("Error getting Lab director for groupID: "+groupId, e);
			return 0;
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
