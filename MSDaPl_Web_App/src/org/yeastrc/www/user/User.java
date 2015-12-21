/*
 * User.java
 *
 * Created January 21, 2004
 *
 * Michael Riffle <mriffle@u.washington.edu>
 *
 */

package org.yeastrc.www.user;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.List;

import org.yeastrc.data.IData;
import org.yeastrc.data.InvalidIDException;
import org.yeastrc.db.DBConnectionManager;
import org.yeastrc.project.Project;
import org.yeastrc.project.Projects;
import org.yeastrc.project.Researcher;

import com.Ostermiller.util.MD5;

/**
 * This is the data object that holds all information pertaining to a User
 * of the web site.  It's really just a wrapper around the Researcher object,
 * with the addition of a username, password and web site group access info.
 *
 * @version 2004-01-21
 * @author Michael Riffle <mriffle@u.washington.edu>
 */
public class User implements IData {	

	/**
	 * Get a populated list of Project objects to which this researcher belongs.
	 * Note: The researcher MUST BE LOADED before calling this object.
	 * @return A list of projects
	 */
	public List<Project> getProjects() throws SQLException {
		return Projects.getProjectsByResearcher(this.id);
	}

	/**
	 * Get a populated list of Project objects, which are Projects submitted to this YRC
	 * personnel's groups within the last 30 days.  If this user is not a YRC personnel member,
	 * that is this User belongs to no YRC groups, null is returned.
	 * @return A list of projects
	 * @throws SQLException
	 */
	public List getNewProjects() throws SQLException {
		return Projects.getNewProjectsForYRCMember(this.getResearcher());
	}

	/**
	 * Instantiate a new project.
	 */
	public User() {		
		this.id = 0;
		this.password = null;
		this.username = null;
		this.researcher = null;
		this.lastLoginTime = null;
		this.lastLoginIP = null;
		this.lastPasswordChange = null;
	}


	/**
	 * <P>Use this method to save this object's data to the database.
	 * These objects should generally represent a single row of data in a table.
	 * This will update the row if this object represents a current row of data, or it will
	 * insert a new row if this data is not in the database (that is, if there is no ID in the object).
	 * @throws InvalidIDException If there is an ID set in this object, but it is not in the database.
	 * <P>Calling this will NOT save the underlying Researcher to the database.  To do that, call getReseacher()
	 * and call save() on that object.  Or, call save() on whatever researcher object you used in setResearcher()
	 * @throws SQLException If there is a problem interracting with the database
	 * @throws InvalidIDException if there is an invalid researcher ID associated with this User
	 */
	public void save() throws InvalidIDException, SQLException {

		// Get our connection to the database.
		Connection conn = DBConnectionManager.getConnection("yrc");	
		Statement stmt = null;
		ResultSet rs = null;

		try {
			stmt = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);

			// Get our updatable result set
			String sqlStr = "SELECT * FROM tblUsers WHERE researcherID = " + this.id;
			rs = stmt.executeQuery(sqlStr);

			// See if we're updating a row or adding a new row.
			if (this.id > 0) {

				// Make sure this row is actually in the database.  This shouldn't ever happen.
				if (!rs.next()) {
					throw new InvalidIDException("ID was set in a User, but not found in database on save()");
				}

				
				rs.updateString("username", this.username);

				if (this.password != null) { rs.updateString("password", this.password); }
				
				if (this.lastLoginTime == null) { rs.updateNull("lastLoginTime"); }
				else { rs.updateTimestamp("lastLoginTime", new Timestamp(this.lastLoginTime.getTime())); }

				if (this.lastPasswordChange == null) { rs.updateNull("lastPasswordChange"); }
				else { rs.updateDate("lastPasswordChange", this.lastPasswordChange); }

				if (this.lastLoginIP == null) { rs.updateNull("lastLoginIP"); }
				else { rs.updateString("lastLoginIP", this.lastLoginIP); }


				// Update the row
				rs.updateRow();

			} else {
				// We're adding a new row.

				rs.moveToInsertRow();

				rs.updateInt("researcherID", this.researcher.getID());
				this.id = this.researcher.getID();

				rs.updateString("username", this.username);

				if (this.password != null) { rs.updateString("password", this.password); }
				
				if (this.lastLoginTime == null) { rs.updateNull("lastLoginTime"); }
				else { rs.updateTimestamp("lastLoginTime", new Timestamp(this.lastLoginTime.getTime())); }

				if (this.lastPasswordChange == null) { rs.updateNull("lastPasswordChange"); }
				else { rs.updateDate("lastPasswordChange", this.lastPasswordChange); }

				if (this.lastLoginIP == null) { rs.updateNull("lastLoginIP"); }
				else { rs.updateString("lastLoginIP", this.lastLoginIP); }


				rs.insertRow();

			}

			rs.close();
			rs = null;
			
			stmt.close();
			stmt = null;
			
			conn.close();
			conn = null;
		}
		catch(SQLException e) { throw e; }
		catch(InvalidIDException e) { throw e; }
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
	 * Use this method to populate this object with data from the datbase.
	 * @param id The experiment ID to load.
	 * <P>This WILL load the underlying researcher object as well, or croak
	 * @throws InvalidIDException If this ID is not valid (or not found)
	 * @throws SQLException If there is a problem interracting with the database
	 */
	public void load(int id) throws InvalidIDException, SQLException {

		// Load the underlying Researcher object first.  No sense in continuing to load if the researcher isn't found.
		// This will automatically throw an exception if there is a problem loading the researcher.
		this.researcher = new Researcher();
		this.researcher.load(id);

		// Get our connection to the database.
		Connection conn = DBConnectionManager.getConnection("yrc");	
		Statement stmt = null;
		ResultSet rs = null;

		try{
			stmt = conn.createStatement();

			// Our SQL statement
			String sqlStr = "SELECT * FROM tblUsers WHERE researcherID = " + id;

			// Our results
			rs = stmt.executeQuery(sqlStr);

			// No rows returned.
			if( !rs.next() ) {
				throw new InvalidIDException("Load failed due to invalid Researcher/User ID.");
			}

			// Populate the object from this row.
			this.id = rs.getInt("researcherID");
			this.username = rs.getString("username");
			this.password = rs.getString("password");
			this.lastLoginTime = rs.getTimestamp("lastLoginTime");
			this.lastLoginIP = rs.getString("lastLoginIP");
			this.lastPasswordChange = rs.getDate("lastPasswordChange");
			
			rs.close();
			rs = null;
			
			stmt.close();
			stmt = null;
			
			conn.close();
			conn = null;
		}
		catch(SQLException e) { throw e; }
		catch(InvalidIDException e) { throw e; }
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
	 * Use this method to delete the data underlying this object from the database.
	 * Doing so will delete the row from the table corresponding to this object, and
	 * will remove the ID value from the object (since it represents the primary key)
	 * in the database.  This will cause subsequent calls to save() on the object to
	 * insert a new row into the database and generate a new ID.
	 * This will also call delete() on instantiated IData objects for all rows in the
	 * database which are dependent on this row.  For example, calling delete() on a
	 * MS Run objects would call delete() on all Run Result objects, which would then
	 * call delete() on all dependent Peptide objects for those results.
	 * Pre: object is populated with a valid ID.
	 * <P><B>IMPORTANT</B>This will NOT delete the underlying researcher.  To delete the
	 * researcher AND the user, just call delete() in the underlying researcher.
	 * @throws SQLException if there is a problem working with the database.
	 * @throws InvalidIDException if the ID isn't set in this object, or if the ID isn't
	 * valid (that is, not found in the database).
	 */
	public void delete() throws InvalidIDException, SQLException {

		// Get our connection to the database.
		Connection conn = DBConnectionManager.getConnection("yrc");	
		Statement stmt = null;
		ResultSet rs = null;

		try {
			stmt = conn.createStatement();

			// Our SQL statement
			String sqlStr = "SELECT researcherID FROM tblUsers WHERE researcherID = " + this.id;

			// Our results
			rs = stmt.executeQuery(sqlStr);

			// No rows returned.
			if( !rs.next() ) {
				throw new InvalidIDException("Attempted to delete a User not found in the database.");
			}

			// Delete the result row.
			rs.deleteRow();		

			rs.close();
			rs = null;
			
			stmt.close();
			stmt = null;
			
			conn.close();
			conn = null;
			
			this.id = 0;
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
	 * Set the password for this user. This will perform an MD5 hash on the string and use that as the password.
	 * @param password The unencrypted string to use as the password.
	 */
	public void setPassword(String password) {
		this.password = MD5.getHashString(password);
	}

	/**
	 * Set the username for this user.  This is not recommended if your intent is to CHANGE the username, so be sure you know what you're doing.
	 * @param username the username to use for this User
	 */
	public void setUsername(String username) {
		this.username = username;
	}

	/**
	 * Set the last login time for this user
	 * @param lastlogin The Date representing their last login.
	 */
	public void setLastLoginTime(java.util.Date lastlogin) {
		this.lastLoginTime = new java.sql.Date(lastlogin.getTime());
	}

	/**
	 * Set the last time they changed their password
	 * @param lastchange The Date representing the last time they changed their password
	 */
	public void setLastPasswordChange(java.util.Date lastchange) {
		this.lastPasswordChange = new java.sql.Date(lastchange.getTime());
	}


	/**
	 * Set the underlying researcher for this User to the supplied researcher.
	 * @param researcher the researcher object to use for the underlying researcher
	 */
	public void setResearcher(Researcher researcher) {
		this.researcher = researcher;
		//this.id = researcher.getID();
	}
	
	/**
	 * Set the value for the last IP number from which the user accessed the site
	 * @param The value for the last IP number from which the user accessed the site
	 */
	public void setLastLoginIP(String lastip) { this.lastLoginIP = lastip; }
	

	
	/**
	 * Get the id for this User
	 * @return the id for this User
	 */
	public int getID() { return this.id; }
	
	/**
	 * Get the password for this User
	 * @return the MD5 encrypted password for this User
	 */
	public String getPassword() { return this.password; }
	
	/**
	 * Get the username for this User
	 * @return The username for this User
	 */
	public String getUsername() { return this.username; }
	
	/**
	 * Get the last login date/time for this User
	 * @return The last login date/time for this User
	 */
	public java.util.Date getLastLoginTime() { return (java.util.Date)this.lastLoginTime; }
	
	/**
	 * Get the last date/time the password was changed
	 * @return The last date/time the password was changed
	 */
	public java.util.Date getLastPasswordChange() { return (java.util.Date)this.lastPasswordChange; }
	
	/**
	 * Get the last IP address they logged in from
	 * @return The last IP address this User logged in from
	 */
	public String getLastLoginIP() { return this.lastLoginIP; }
	
	/**
	 * Get the underlying Researcher that is this user
	 * @return The underlying Researcher that is this user.
	 */
	public Researcher getResearcher() { return this.researcher; }

	public String getIdAndName()
	{
		StringBuilder buf = new StringBuilder();
		buf.append(String.valueOf(getID()));
		buf.append(": ").append(this.getUsername());
		if(this.getResearcher() != null)
		{
			buf.append(" (").append(getResearcher().getFirstName()).append(" ").append(getResearcher().getLastName()).append(")");
		}
		return buf.toString();
	}
	
	// instance variables

	// The researcher id of this User
	private int id;
	
	// The username of this User
	private String username;
	
	// The password of this User
	private String password;

	// The researcher associated with this User
	private Researcher researcher;
	
	// The last time they logged in
	private java.util.Date lastLoginTime;
	
	// The last time they changed their password
	private java.sql.Date lastPasswordChange;
	
	// The IP they last logged in from
	private String lastLoginIP;

}