/*
 * Projects.java
 *
 * Created November 15, 2003
 *
 * Michael Riffle <mriffle@u.washington.edu>
 *
 */

package org.yeastrc.project;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;

import org.yeastrc.data.InvalidIDException;
import org.yeastrc.db.DBConnectionManager;
import org.yeastrc.www.user.Groups;



/**
 * This class is to provide a set of static methods and variable definitions that
 * will provide utility functionality for projects.
 *
 * @version 2003-11-19
 * @author Michael Riffle <mriffle@u.washington.edu>
 */
public class Projects {
	/**
	 * These are the valid GROUPS in the YRC.  All calls to setGroup/addGroup in Project
	 * will have to be present int this array, or no guarantees are made as to method behavior!
	 * This array MUST BE SORTED alphabetically, for binary search purposes
	 */
//	public static final String[] GROUPS = {"Aebersold", "Core", "Informatics", "MacCoss", "Microscopy", "Noble", "PSP","TwoHybrid","Yates"};
//	public static final String[] GROUPS_LONG = {"Mass Spectrometry (Aebersold)", "YRC Core", "Informatics", "Mass Spectrometry (MacCoss)", "Microscopy", "Computational Biology", "Protein Structure Prediction", "Yeast Two-Hybrid", "Mass Spectrometry (Yates)"};
//	
//	/** The defintion for the abersold group */
//	public static final String AEBERSOLD = "Aebersold";
//	
//	/** The defintion for the informatics group */
//	public static final String INFORMATICS = "Informatics";
//
//	/** The defintion for the abersold group */
//	public static final String MICROSCOPY = "Microscopy";
//
//	/** The defintion for the abersold group */
//	public static final String PSP = "PSP";
//
//	/** The defintion for the abersold group */
//	public static final String TWOHYBRID = "TwoHybrid";
//
//	/** The defintion for the abersold group */
//	public static final String YATES = "Yates";
//	
//	/** The definition for the MacCoss group */
//	//public static final String MACCOSS = "MacCoss";
//	
//	/** The definition for the Goodlett group */
//    public static final String GOODLETT = "Goodlett";
//    
//    /** The definition for the Bruce group */
//    public static final String BRUCE = "Bruce";
//    
//    /** The definition for the Villen group */
//    public static final String VILLEN = "Villen";
//
//	/** The definitioni for the Noble group */
//	public static final String NOBLE = "Noble";
//
//	/** The definition for a Collaboration **/
//	public static final String COLLABORATION = "C";
//	
//	/** The definition for a Technology Development **/
//	public static final String TECHNOLOGY = "Tech";
//	
//	/** The definition for a Training **/
//	public static final String TRAINING = "T";
//	
//	/** The definition for a Dissemination **/
//	public static final String DISSEMINATION = "D";
//	
//	/** The definition for the core group */
//	public static final String CORE = "Core";
	
	
	/**
	 * Returns all NEW projects submitted to this YRC member's group(s)
	 * @param researcherID The researcher ID of the YRC member
	 * @return A list of new projects (within the last 30 days) for this member's groups, null if this is not a YRC member
	 */
	public static List<Project> getNewProjectsForYRCMember(Researcher r) throws SQLException {
		int researcherID = r.getID();
		Groups gm = Groups.getInstance();

		// return null if they're not in a YRC group
		if (!gm.isInAGroup(researcherID)) return null;

		ProjectsSearcher ps = new ProjectsSearcher();
		//ps.setResearcher(r);

		// Set the start date of the search to 1 month ago
		Calendar cal = GregorianCalendar.getInstance();
		cal.add(Calendar.MONTH, -1);
		ps.setStartDate(cal.getTime());

		// Set the groups to search
		List groups = gm.getGroups();
		Iterator iter = groups.iterator();
		while (iter.hasNext()) {
			String group = (String)(iter.next());
			if (gm.isMember(researcherID, group)) {
				ps.addGroup(group);		
			}
		}

		List<Project> projects = ps.search();
		return projects;
	}
	
	/**
	 * Get all of the projects for which the supplied researcher ID is associated as a researcher
	 * @param researcherID The researcher ID to use
	 * @return An list of populated Project objects
	 */
	public static List<Project> getProjectsByResearcher(int researcherID) throws SQLException {
		List<Project> retList = new ArrayList<Project>();

		// Get our connection to the database.
		Connection conn = DBConnectionManager.getConnection("yrc");
		PreparedStatement stmt = null;
		ResultSet rs = null;

		try{
			// Our SQL statement
		    String sqlStr = "SELECT DISTINCT  p.projectID "+
		    "FROM tblProjects AS p " +
		    "LEFT OUTER JOIN projectResearcher AS pr "+
		    "ON p.projectID = pr.projectID "+
		    "WHERE (p.projectPI = ? OR pr.researcherID = ?) "+
		    "ORDER BY p.projectID";
			
			stmt = conn.prepareStatement(sqlStr);
			stmt.setInt(1, researcherID);
			stmt.setInt(2, researcherID);

			// Our results
			rs = stmt.executeQuery();

			// Iterate over list and populate our return list
			while (rs.next()) {
				int projectID = rs.getInt("projectID");
				
				try {
				    Project proj = ProjectDAO.instance().load(projectID);
				    retList.add(proj);
				} 
				catch(InvalidIDException iie) {
					continue;
				}
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

		return retList;
	}

	
	/**
	 * Simply takes a Set of groups and returns a comma delimited
	 * listed of those groups.
	 * @param groups a Set of groups, as defined in this document (e.g. Projects.TWOHYBRID)
	 * @return A string consisting of a long hand, comma delimited version of that Set
	 */
//	public static String getGroupsString(Set groups) {
//		if (groups == null) return "None";
//		if (groups.size() < 1) return "None";
//		
//		boolean pastFirst = false;
//		String retString = "";
//		
//		Iterator iter = groups.iterator();
//		while (iter.hasNext()) {
//			String group = (String)(iter.next());
//
//			// Add a comma before all but the first item
//			if (pastFirst) retString += ", ";
//			pastFirst = true;
//			
//			// Get the long version of the group name, and add it to the list
//			int indexOfGroup = Arrays.binarySearch(Projects.GROUPS, group);
//			if (indexOfGroup >= 0)
//				retString += Projects.GROUPS_LONG[indexOfGroup];
//			else
//				retString += group;		
//		}
//
//		return retString;
//	}


	/**
	 * Simply return an ArrayList of all the researchers in the database (as Researcher objects)
	 * @return A list of all the Researchers in the database
	 */
	public static ArrayList getAllResearchers() throws SQLException, InvalidIDException {
		ArrayList retList = new ArrayList();

		// Get our connection to the database.
		Connection conn = DBConnectionManager.getConnection("yrc");
		Statement stmt = null;
		ResultSet rs = null;

		try {
			stmt = conn.createStatement();

			// Our SQL statement
			String sqlStr =  "SELECT researcherID, researcherFirstName, researcherLastName, researcherEmail, researcherOrganization FROM tblResearchers ORDER BY researcherLastName";

			// Our results
			rs = stmt.executeQuery(sqlStr);

			// Iterate over list and populate our return list
			while (rs.next()) {
				Researcher researcher = new Researcher();
				researcher.setID(rs.getInt("researcherID"));
				researcher.setFirstName(rs.getString("researcherFirstName"));
				researcher.setLastName(rs.getString("researcherLastName"));
				researcher.setEmail(rs.getString("researcherEmail"));
				researcher.setOrganization(rs.getString("researcherOrganization"));
				
				retList.add(researcher);
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

		return retList;
	}
}