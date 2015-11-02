/*
 * CycleUtils.java
 *
 * Created November 11, 2003
 *
 * Michael Riffle <mriffle@u.washington.edu>
 *
 */

package org.yeastrc.www.user;

import java.sql.*;
import org.yeastrc.data.*;
import org.yeastrc.db.*;
import java.util.Random;
import javax.servlet.http.*;


/**
 * A set of static methods performing operations related to users
 * of the web site.
 */
public class UserUtils {

	/**
	 * Get a populated User object corresponding to a username.
	 * @param username The username to test
	 * @return The User object corresponding to that username.
	 * @throws NoSuchUserException if that username does not exist.
	 * @throws SQLException if a database error was encountered.
	 */
	public static User getUser(String username) throws NoSuchUserException, SQLException {
		// The User to return
		User theUser;
		
		// Make sure the username isn't null
		if (username == null) { throw new NoSuchUserException("got null for username in UserUtils.getUser"); }

		// Get our connection to the database.
		Connection conn = DBConnectionManager.getConnection("yrc");
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		try {
			stmt = conn.prepareStatement("SELECT researcherID FROM tblUsers WHERE username = ?");
			stmt.setString(1, username);

			rs = stmt.executeQuery();
		
			// No rows returned.
			if( !rs.next() ) {
				throw new NoSuchUserException("Username not found.");
			}

			theUser = new User();

			try {
				theUser.load(rs.getInt("researcherID"));
			} catch(InvalidIDException e) {
				throw new NoSuchUserException("Somehow, we got an invalid ID (" + rs.getInt("researcherID") + ") after we got the ID from the username...  This can't be good.");
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

		return theUser;
	}
	
	/**
	 * Get a populated User object corresponding to a email address.
	 * @param email The email address to test
	 * @return The User object corresponding to that username.
	 * @throws NoSuchUserException if that username does not exist.
	 * @throws SQLException if a database error was encountered.
	 */
	public static User getUserWithEmail(String email) throws NoSuchUserException, SQLException {
		// The User to return
		User theUser;
		
		// Make sure the email isn't null
		if (email == null) { throw new NoSuchUserException("got null for email in UserUtils.getUser"); }

		// Get our connection to the database.
		Connection conn = DBConnectionManager.getConnection("yrc");
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		try {
			stmt = conn.prepareStatement("SELECT researcherID FROM tblResearchers WHERE researcherEmail = ?");
			stmt.setString(1, email);

			rs = stmt.executeQuery();
		
			// No rows returned.
			if( !rs.next() ) {
				throw new NoSuchUserException("email not found.");
			}

			theUser = new User();

			try {
				theUser.load(rs.getInt("researcherID"));
			} catch(InvalidIDException e) {
				throw new NoSuchUserException("Somehow, we got an invalid ID (" + rs.getInt("researcherID") + ") after we got the ID from the username...  This can't be good.");
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

		return theUser;
	}

	/**
	 * Get a populated User object from the request passed in.
	 * @param The request object to check for the user
	 * @return The user object, or null if no user object was found
	 */
	public static User getUser(HttpServletRequest request) {
		HttpSession session = request.getSession();
		if (session == null) { return null; }

		return (User)(session.getAttribute("user"));
	}


	/**
	 * Determine whether or a not a User with the supplied username exists
	 * @param username The username to test
	 * @return true if the user exists, false if not
	 * @throws SQLException if a database error was encountered
	 */
	public static boolean userExists(String username) throws SQLException {
		boolean returnVal = false;
		
		if (username == null || username.equals("") ) { return false; }
	   
		// Get our connection to the database.
		Connection conn = DBConnectionManager.getConnection("yrc");
		PreparedStatement stmt = null;
		ResultSet rs = null;
		

		try {
			stmt = conn.prepareStatement("SELECT researcherID FROM tblUsers WHERE username = ?");
			stmt.setString(1, username);

			rs = stmt.executeQuery();
		
			// No rows returned.
			if( !rs.next() ) {
				returnVal = false;
			} else {
				returnVal = true;
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

		return returnVal;
	}

	/**
	 * Determine whether or a not a User with the supplied researcherID exists
	 * @param username The researcherID to test
	 * @return true if the user exists, false if not
	 * @throws SQLException if a database error was encountered
	 */
	public static boolean userExists(int researcherID) throws SQLException {
		boolean returnVal = false;
	   
		// Get our connection to the database.
		Connection conn = DBConnectionManager.getConnection("yrc");
		PreparedStatement stmt = null;
		ResultSet rs = null;

		try {
			stmt = conn.prepareStatement("SELECT researcherID FROM tblUsers WHERE researcherID = ?");
			stmt.setInt(1, researcherID);

			rs = stmt.executeQuery();
		
			// No rows returned.
			if( !rs.next() ) {
				returnVal = false;
			} else {
				returnVal = true;
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

		return returnVal;
	}


	/**
	 * Determine whether or a not a Researcher with the supplied email exists
	 * @param email The email to test
	 * @return The researcher ID of the researcher if it exists, -1 if it doesn't
	 * @throws SQLException if a database error was encountered
	 */
	public static int emailExists(String email) throws SQLException {
		int returnVal = -1;
		
		if (email == null || email.equals("") ) { return -1; }
	   
		// Get our connection to the database.
		Connection conn = DBConnectionManager.getConnection("yrc");
		PreparedStatement stmt = null;
		ResultSet rs = null;

		try {
			stmt = conn.prepareStatement("SELECT researcherID FROM tblResearchers WHERE researcherEmail = ?");
			stmt.setString(1, email);

			rs = stmt.executeQuery();
		
			// No rows returned.
			if( !rs.next() ) {
				returnVal = -1;
			} else {
				returnVal = rs.getInt("researcherID");
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

		return returnVal;
	
	}

	/**
	 * Generate and return a random password.
	 * @return A randomly generated password.
	 */
	public static String generatePassword() {
		// The list of possible characters to use in the password.
		String[] chars = {"2","3","4","5","6","7","8","9","a","b","c","d","e","f","g","h","i","j","k","m","n","p","q","r","s","t","u","v","w","x","y","z"};
		
		// The size of the password to generate
		int pSize = 6;
		
		// The password to return.
		String password = "";
		
		// Random number generator
		Random rand = new Random();
		
		// Create the password
		for (int i = 0; i < pSize; i++) {
		   int ind = rand.nextInt(chars.length);
		   password = password + chars[ind];
		}
		
		return password;	
	}

}