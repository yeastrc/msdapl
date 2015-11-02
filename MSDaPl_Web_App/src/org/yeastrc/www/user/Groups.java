/* Groups.java
 * Created on Mar 23, 2004
 */
package org.yeastrc.www.user;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.yeastrc.db.DBConnectionManager;
import org.yeastrc.project.Researcher;

/**
 * Singleton class providing access and methods for YRC group information.
 * 
 * @author Michael Riffle <mriffle@alumni.washington.edu>
 * @version 1.0, Mar 23, 2004
 *
 */
public class Groups {

	// Our single instance of this class
	private static final Groups INSTANCE = new Groups();

	// Our HashMap of groups
	private HashMap<String, ArrayList<Integer>> groups;
	
	// Our constructor
	private Groups() {
		this.groups = new HashMap<String, ArrayList<Integer>>();

		try { this.loadGroups(); }
		catch (Exception e) { ; }
	}

	// Load the groups from the database.
	private void loadGroups() throws SQLException {
		
		// Get our connection to the database.
		Connection conn = DBConnectionManager.getConnection("yrc");	
		Statement stmt = null;
		ResultSet rs = null;
		
		try {
			stmt = conn.createStatement();
			
			rs = stmt.executeQuery("SELECT g.groupName AS groupName, gm.researcherID AS researcherID FROM tblYRCGroups AS g LEFT OUTER JOIN tblYRCGroupMembers AS gm ON g.groupID = gm.groupID ORDER BY g.groupName, gm.researcherID");

			while (rs.next()) {
				String groupName = rs.getString("groupName");
				int researcherID = rs.getInt("researcherID");
				ArrayList<Integer> rList;
				
				// Get our ArrayList of members of this group, so far
				if (groups.get(groupName) == null) {
					rList = new ArrayList<Integer>();
					groups.put(groupName, rList);
				} else {
					rList = groups.get(groupName);
				}

				// Add this member to the group
				rList.add(new Integer(researcherID));
			}

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
	}
	
	/**
	 * Gets the groupID for the supplied group name
	 * @param groupName the name of the group
	 * @return The id of the group, 0 if not found
	 */
	public int getGroupID(String groupName) throws SQLException {
		int groupID = 0;
		if (groupName == null) return 0;
		
		// Get our connection to the database.
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		try {
			String sqlStr = "SELECT groupID FROM tblYRCGroups WHERE groupName = ?";
			
			conn = DBConnectionManager.getConnection("yrc");	
			stmt = conn.prepareStatement(sqlStr);
			stmt.setString(1, groupName);
			
			rs = stmt.executeQuery();
			
			if (rs.next()) {
				groupID = rs.getInt("groupID");
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

		return groupID;

	}
	
	/**
	 * @return The singleton instance
	 */
	public static Groups getInstance() { return INSTANCE; }


	/**
	 * Reload the groups list from the database
	 */
	public void reloadGroups() throws SQLException {
		this.groups = new HashMap<String, ArrayList<Integer>>();

		this.loadGroups();
	}

	/**
	 * Test to see if the given researcher ID is a member of the given group.
	 * @param rID The researcher ID
	 * @param gName The group name
	 */
	public boolean isMember(int rID, String gName) {
		ArrayList<Integer> members;
		if (gName == null) { return false; }
		
		members = this.groups.get(gName);
		if (members == null) { return false; }
		
		return members.contains(new Integer(rID));
	}
	
	/**
	 * Checks to see if the supplied researcher ID belongs to ANY YRC group.
	 * If it does, then it's YRC personell, if not it's a normal researcher
	 * @param rID The researcher ID
	 * @return true if it is in a YRC group, false if not
	 */
	public boolean isInAGroup(int rID) {
		
		// Look through each group check for membership
		List<String> groupList = this.getGroups();
		Iterator<String> iter = groupList.iterator();
		while (iter.hasNext()) {
			String groupName = (String)(iter.next());
			if (this.isMember(rID, groupName)) return true;
		}
		
		return false;
	}
	
	/**
	 * List all of the groups.
	 * @return An ordered list of the groups by name
	 */
	public List<String> getGroups() {
		Set<String> groupSet = this.groups.keySet();
		ArrayList<String> groupNames = new ArrayList<String>(groupSet);
		Collections.sort(groupNames);
		
		return groupNames;
	}

	/**
	 * Returns a sorted list of Researcher objects, sorted by last name, belonging to the supplied group.
	 * @param groupName The name of the group.
	 * @return A sorted list of Researcher objects.
	 */
	public List<Researcher> getMembers(String groupName) {
		ArrayList<Researcher> retList = new ArrayList<Researcher>();
			
		if (groupName == null) { return retList; }
		
		ArrayList<Integer> members = this.groups.get(groupName);
		if (members == null) { return retList; }
		
		Iterator<Integer> iter = members.iterator();
		while (iter.hasNext()) {
			Integer resID = (Integer)(iter.next());
			
			Researcher res = new Researcher();
			try {
				res.load(resID.intValue());
			} catch(Exception e) {
				continue;
			}
			
			retList.add(res);
		}
		Collections.sort(retList);
		return retList;
	}
	
	/**
	 * Returns a list of group names of which the given user is a member.
	 * @param researcherId
	 * @return
	 * @throws SQLException 
	 */
	public List<String> getUserGroups(int researcherId) throws SQLException {
		
		List<String> groupNames = new ArrayList<String>();
		for(String groupName: this.getGroups()) {
			ArrayList<Integer> memberIds = this.groups.get(groupName);
			if(memberIds != null && memberIds.contains(researcherId))
				groupNames.add(groupName);
		}
		return groupNames;
	}

	/**
	 * Add the given researcherID to the given group name
	 * @param groupName
	 * @param researcherID
	 * @throws SQLException
	 */
	public void addToGroup(String groupName, int researcherID) throws SQLException {
		int groupID = 0;
		
		if (groupName == null) return;
		
		// Invalid group name
		List<Researcher> members = this.getMembers(groupName);
		if (members == null) return;
		
		// Researcher is already in that group
		if (members.contains(new Integer(researcherID))) return;
	
		// Add this researcher to this group in the database
		
		// Get the groupID value for this group from the table
		groupID = this.getGroupID(groupName);
		if (groupID == 0) return;		

		// Get our connection to the database.
		Connection conn = null;
		PreparedStatement stmt = null;

		try {
			String sqlStr = "INSERT INTO tblYRCGroupMembers (groupID, researcherID) VALUES (?, ?)";
			
			conn = DBConnectionManager.getConnection("yrc");	
			stmt = conn.prepareStatement(sqlStr);
			stmt.setInt(1, groupID);
			stmt.setInt(2, researcherID);
			
			stmt.executeUpdate();
			
			stmt.close();
			stmt = null;
			
			conn.close();
			conn = null;

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
		
		// Refresh the group list.
		this.reloadGroups();
	}

	/**
	 * Remove the given researcherID from the given group name
	 * @param groupName
	 * @param researcherID
	 * @throws SQLException
	 */
	public void removeFromGroup(String groupName, int researcherID) throws SQLException {
		int groupID = 0;
		
		if (groupName == null) return;
		
		// Invalid group name
		List<Researcher> members = this.getMembers(groupName);
		if (members == null) return;
		
		// Researcher is already in that group
		if (members.contains(new Integer(researcherID))) return;
	
		// Remove this researcher from this group in the database
		
		// Get the groupID value for this group from the table
		groupID = this.getGroupID(groupName);
		if (groupID == 0) return;		

		// Get our connection to the database.
		Connection conn = null;
		PreparedStatement stmt = null;

		try {
			String sqlStr = "DELETE FROM tblYRCGroupMembers WHERE groupID = ? AND researcherID = ?";
			
			conn = DBConnectionManager.getConnection("yrc");	
			stmt = conn.prepareStatement(sqlStr);
			stmt.setInt(1, groupID);
			stmt.setInt(2, researcherID);
			
			stmt.executeUpdate();
			
			stmt.close();
			stmt = null;
			
			conn.close();
			conn = null;

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
		
		// Refresh the groups list
		this.reloadGroups();
	}

}
