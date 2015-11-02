/*
 * Researcher.java
 *
 * Created October 15, 2003
 *
 * Michael Riffle <mriffle@u.washington.edu>
 *
 */

package org.yeastrc.project;

import org.yeastrc.db.*;
import org.yeastrc.data.*;

import java.sql.*;

/**
 * This class represents a researcher involved with a collaboration, plasma dissemination
 * or some other project with the Yeast Resource Center.  Specifically, this object holds the
 * data from a single row in the Researchers database table, and provides methods for manipulation
 * of that data, as well as other actions related to researchers.
 * @version 2003-11-14
 * @author Michael Riffle <mriffle@u.washington.edu>
 */
public class Researcher implements Comparable, IData {

	/**
	 * Create an instance of Researcher
	 */
	public Researcher() {
		// initialize the instance variables
		id = 0;
		NCRRID = 0;
		firstName = null;
		lastName = null;
		degree = null;
		email = null;
		organization = null;
		department = null;
		state = null;
		zipCode = null;
		country = null;
		phone = null;
	}



	/**
	 * For Comparable.  Does an alphabetical comparison of last names.
	 * @param o The researcher to compare this one to.
	 */
	public int compareTo(Object o) {
		Researcher researcher = (Researcher)o;
		
		String rLName = researcher.getLastName();
		String rFName = researcher.getFirstName();
		String rEmail = researcher.getEmail();

		if (rLName.equals(this.lastName)) {
			if (rFName.equals(this.firstName)) {
				if(rEmail.equals(this.email)) {
					// If the full name and email are the same, assume equality.
					return 0;
				}
				return this.email.compareTo(rEmail);
			}
			return this.firstName.compareTo(rFName);
		}
		return this.lastName.compareTo(rLName);
	}
	
	/**
	 * Test these two researchers to see if they represent the same researcher
	 * Yes, it's possible to have two distinct Researcher objects representing the same researcher
	 * The test is based on the researcherID of each Researcher
	 * @param researcher The researcher to test against this one.
	 * @return true if they represent the same researcher, false if not
	 */
	public boolean equals(Object o) {
		return this.id == ((Researcher)o).getID();
	}

	public int hashCode() {
		return this.id;
	}


	/**
	 * Use this method to save this object's data to the database.
	 * These objects should generally represent a single row of data in a table.
	 * This will update the row if this object represents a current row of data, or it will
	 * insert a new row if this data is not in the database (that is, if there is no ID in the object).
	 * @throws InvalidIDException If there is an ID set in this object, but it is not in the database.
	 * @throws SQLException If there is a problem interracting with the database
	 */
	public void save() throws InvalidIDException, SQLException {

		// Get our connection to the database.
		Connection conn = DBConnectionManager.getConnection("yrc");
		Statement stmt = null;
		ResultSet rs = null;

		try {
			stmt = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);

			// Get our updatable result set
			String sqlStr = "SELECT * FROM tblResearchers WHERE researcherID = " + this.id;
			rs = stmt.executeQuery(sqlStr);

			// See if we're updating a row or adding a new row.
			if (this.id > 0) {

				// Make sure this row is actually in the database.  This shouldn't ever happen.
				if (!rs.next()) {
					throw new InvalidIDException("ID was set in researcher, but not found in database on save()");
				}

				// Make sure the result set is set up w/ current values from this object
				if (this.firstName == null) { rs.updateNull("researcherFirstName"); }
				else { rs.updateString("researcherFirstName", this.firstName); }

				if (this.lastName == null) { rs.updateNull("researcherLastName"); }
				else { rs.updateString("researcherLastName", this.lastName); }
				
				if (this.degree == null) { rs.updateNull("researcherDegree"); }
				else { rs.updateString("researcherDegree", this.degree); }

				if (this.degree == null) { rs.updateNull("researcherEmail"); }
				else { rs.updateString("researcherEmail", this.email); }

				if (this.organization == null) { rs.updateNull("researcherOrganization"); }
				else { rs.updateString("researcherOrganization", this.organization); }

				if (this.department == null) { rs.updateNull("researcherDept"); }
				else { rs.updateString("researcherDept", this.department); }

				if (this.state == null) { rs.updateNull("researcherState"); }
				else { rs.updateString("researcherState", this.state); }

				if (this.zipCode == null) { rs.updateNull("researcherZip"); }
				else { rs.updateString("researcherZip", this.zipCode); }

				if (this.country == null) { rs.updateNull("researcherCountry"); }
				else { rs.updateString("researcherCountry", this.country); }

				if (this.phone == null) { rs.updateNull("researcherPhone"); }
				else { rs.updateString("researcherPhone", this.phone); }
				
				if (this.NCRRID == 0) { rs.updateNull("NCRR_ID"); }
				else { rs.updateInt("NCRR_ID", this.NCRRID); }


				// Update the row
				rs.updateRow();

			} else {
				// We're adding a new row.

				rs.moveToInsertRow();

				if (this.firstName == null) { rs.updateNull("researcherFirstName"); }
				else { rs.updateString("researcherFirstName", this.firstName); }

				if (this.lastName == null) { rs.updateNull("researcherLastName"); }
				else { rs.updateString("researcherLastName", this.lastName); }
				
				if (this.degree == null) { rs.updateNull("researcherDegree"); }
				else { rs.updateString("researcherDegree", this.degree); }

				if (this.degree == null) { rs.updateNull("researcherEmail"); }
				else { rs.updateString("researcherEmail", this.email); }

				if (this.organization == null) { rs.updateNull("researcherOrganization"); }
				else { rs.updateString("researcherOrganization", this.organization); }

				if (this.department == null) { rs.updateNull("researcherDept"); }
				else { rs.updateString("researcherDept", this.department); }

				if (this.state == null) { rs.updateNull("researcherState"); }
				else { rs.updateString("researcherState", this.state); }

				if (this.zipCode == null) { rs.updateNull("researcherZip"); }
				else { rs.updateString("researcherZip", this.zipCode); }

				if (this.country == null) { rs.updateNull("researcherCountry"); }
				else { rs.updateString("researcherCountry", this.country); }

				if (this.phone == null) { rs.updateNull("researcherPhone"); }
				else { rs.updateString("researcherPhone", this.phone); }
				
				if (this.NCRRID == 0) { rs.updateNull("NCRR_ID"); }
				else { rs.updateInt("NCRR_ID", this.NCRRID); }

				rs.insertRow();

				// Get the ID generated for this item from the database, and set expID
				rs.last();
				this.id = rs.getInt("researcherID");

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
	 * @throws InvalidIDException If this ID is not valid (or not found)
	 * @throws SQLException If there is a problem interracting with the database
	 */
	public void load(int id) throws InvalidIDException, SQLException {

		// Get our connection to the database.
		Connection conn = DBConnectionManager.getConnection("yrc");
		Statement stmt = null;
		ResultSet rs = null;

		try{
			stmt = conn.createStatement();

			// Our SQL statement
			String sqlStr = "SELECT * FROM tblResearchers WHERE researcherID = " + id;

			// Our results
			rs = stmt.executeQuery(sqlStr);

			// No rows returned.
			if( !rs.next() ) {
				throw new InvalidIDException("ID not found for this researcher.");
			}

			// Populate the object from this row.
			this.id = rs.getInt("researcherID");
			this.firstName = rs.getString("researcherFirstName");
			this.lastName = rs.getString("researcherLastName");
			this.degree = rs.getString("researcherDegree");
			this.email = rs.getString("researcherEmail");
			this.organization = rs.getString("researcherOrganization");
			this.department = rs.getString("researcherDept");
			this.state = rs.getString("researcherState");
			this.zipCode = rs.getString("researcherZip");
			this.country = rs.getString("researcherCountry");
			this.phone = rs.getString("researcherPhone");
			this.NCRRID = rs.getInt("NCRR_ID");

			
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
			stmt = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);

			// Our SQL statement
			String sqlStr = "SELECT researcherID FROM tblResearchers WHERE researcherID = " + this.id;

			// Our results
			rs = stmt.executeQuery(sqlStr);

			// No rows returned.
			if( !rs.next() ) {
				throw new InvalidIDException("ID not found for this researcher.");
			}

			// Delete the result row.
			rs.deleteRow();		

			rs.close();
			rs = null;
			
			stmt.close();
			stmt = null;
			
			conn.close();
			conn = null;
			
			// re-initialize the id
			this.id = 0;

			// NEED TO ALSO DELETE THEM FROM THE Users TABLE
			// PUT CODE HERE

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




	// SET METHODS

	/**
	 * Set the researcher's ID.
	 * <P>Only use this method if you really know what you're doing.
	 * @param id The researcher's ID
	 */
	public void setID(int ID) { this.id = ID; }

	/**
	 * Set the researcher's first name.
	 * @param name The researcher's first name
	 */
	public void setFirstName(String name) { this.firstName = name; }

	/**
	 * Set the researcher's last name.
	 * @param name The researcher's first name
	 */
	public void setLastName(String name) { this.lastName = name; }

	/**
	 * Set the researcher's degree.
	 * @param degree The researcher's academic degree
	 */
	public void setDegree(String degree) { this.degree = degree; }

	/**
	 * Set the researcher's email address.
	 * @param name The researcher's email address
	 */
	public void setEmail(String email) { this.email = email; }

	/**
	 * Set the researcher's organization.
	 * @param uni The researcher's organization
	 */
	public void setOrganization(String uni) { this.organization = uni; }

	/**
	 * Set the researcher's department.
	 * @param name The researcher's department
	 */
	public void setDepartment(String name) { this.department = name; }

	/**
	 * Set the researcher's state.
	 * @param state The researcher's state
	 */
	public void setState(String state) { this.state = state; }

	/**
	 * Set the researcher's Zip code.
	 * @param zipcode The researcher's Zip code
	 */
	public void setZipCode(String zipcode) { this.zipCode = zipcode; }

	/**
	 * Set the researcher's country.
	 * @param name The researcher's country
	 */
	public void setCountry(String name) { this.country = name; }
	
	/**
	 * Set the researcher's phone number.
	 * @param arg The researcher's phone number.
	 */
	public void setPhone(String arg) { this.phone = arg; }
	
	/**
	 * Set the NCRR ID for this researcher
	 * @param arg The NCRR id to assign to this reseacher
	 */
	public void setNCRRID(int arg) { this.NCRRID = arg; }
	

	// GET METHODS
	
	/**
	 * Get the researcher's database id number
	 * @return the id column for this researcher's row in the database.
	 *		   Returns 0 if it is unknown
	 */
	public int getID() { return this.id; }

	/**
	 * Get the researcher's first name.
	 * @return The researcher's first name.
	 */
	public String getFirstName() { return this.firstName; }

	/**
	 * Get the researcher's last name.
	 * @return The researcher's last name.
	 */
	public String getLastName() { return this.lastName; }

	/**
	 * Get the researcher's degree.
	 * @return The researcher's degree.
	 */
	public String getDegree() { return this.degree; }

	/**
	 * Get the researcher's email address.
	 * @return The researcher's email address.
	 */
	public String getEmail() { return this.email; }

	/**
	 * Get the researcher's formal string representation in the form of Lastname, Firstname
	 * @return A String representation of this researcher, appropriate for listing
	 */
	public String getListing() { 
		StringBuilder buf = new StringBuilder();
		buf.append(this.lastName + ", " + this.firstName);
		if(this.organization != null && this.organization.trim().length() > 0) {
			buf.append(" <"+this.organization+">");
		}
		return buf.toString();
	}

	/**
	 * Get the researcher's organization.
	 * @return The researcher's organization.
	 */
	public String getOrganization() { return this.organization; }

	/**
	 * Get the researcher's department.
	 * @return The researcher's department.
	 */
	public String getDepartment() { return this.department; }

	/**
	 * Get the researcher's state.
	 * @return The researcher's state.
	 */
	public String getState() { return this.state; }

	/**
	 * Get the researcher's Zip code.
	 * @return The researcher's Zip code.
	 */
	public String getZipCode() { return this.zipCode; }
	
	/**
	 * Get the researcher's country.
	 * @return The researcher's country.
	 */
	public String getCountry() { return this.country; }
	
	/**
	 * Get the researcher's phone number.
	 * @return The researcher's phone number.
	 */
	public String getPhone() { return this.phone; }
	
	/**
	 * Get the ID NCRR has assigned to this researcher in their database
	 * @return The NCRR ID for this researcher
	 */
	public int getNCRRID() { return this.NCRRID; }

	// instance variables server as holders of signup information
	
	// The ID this Researcher has in the database
	private int id;
	
	// first name
	private String firstName;
	
	// last name
	private String lastName;
	
	// academic degree
	private String degree;

	// email address
	private String email;
	
	// organization
	private String organization;
	
	// dept. at organization
	private String department;
	
	// state
	private String state;
	
	// zip code
	private String zipCode;
	
	// country
	private String country;
	
	// phone number
	private String phone;

	// NCRR ID
	private int NCRRID;

}