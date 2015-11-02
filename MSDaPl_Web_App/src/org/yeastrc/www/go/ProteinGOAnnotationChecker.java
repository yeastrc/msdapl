/**
 * 
 */
package org.yeastrc.www.go;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.yeastrc.bio.go.EvidenceCode;
import org.yeastrc.db.DBConnectionManager;

/**
 * ProteinGOAnnotationChecker.java
 * @author Vagisha Sharma
 * Jul 12, 2010
 * 
 */
public class ProteinGOAnnotationChecker {

	private ProteinGOAnnotationChecker () {}
	
	/**
	 * Returns the appropriate Annotation type (NONE, EXACT, INDIRECT) for the given protein ID and go term ID
	 * @param nrseqProteinId
	 * @param goTermId
	 * @return
	 * @throws SQLException
	 */
	public static Annotation isProteinAnnotated(int nrseqProteinId, int goTermId) throws SQLException {
		
		// Get our connection to the database.
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		
		try {
			String sqlStr = "SELECT exact "+
							"FROM GOProteinLookup_Ref "+
							"WHERE proteinID="+nrseqProteinId+" "+
							"AND termID="+goTermId;
			
			conn = DBConnectionManager.getConnection("go");	
			stmt = conn.createStatement();
			
			rs = stmt.executeQuery(sqlStr);
			
			
			if (rs.next()) {
				
				if(rs.getInt(1) == 1)
					return Annotation.EXACT;
				else
					return Annotation.INDIRECT;
				
			}
			else
				return Annotation.NONE;
			
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
	 * Returns the appropriate Annotation type (NONE, EXACT, INDIRECT) for the given protein ID and go term ID
	 * @param nrseqProteinId
	 * @param goTermId
	 * @param excludeCodes -- a list of evidence codes to be excluded
	 * @return
	 * @throws SQLException
	 */
	public static Annotation isProteinAnnotated(int nrseqProteinId, int goTermId, List<EvidenceCode> excludeCodes) throws SQLException {
		
		// Get our connection to the database.
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		
		String evCodes = "";
		for(EvidenceCode code: excludeCodes)
			evCodes += ","+code.getId();
		if(evCodes.length() > 0)
			evCodes = evCodes.substring(1);
		
		try {
			String sqlStr = "SELECT exact "+
							"FROM GOProteinLookup_Ref_EvidenceCodes "+
							"WHERE proteinID="+nrseqProteinId+" "+
							"AND termID="+goTermId;
			if(evCodes.length() > 0)
				sqlStr += " AND evidenceCode NOT IN ("+evCodes+")";
			
			conn = DBConnectionManager.getConnection("go");	
			stmt = conn.createStatement();
			
			rs = stmt.executeQuery(sqlStr);
			
			
			if (rs.next()) {
				
				if(rs.getInt(1) == 1)
					return Annotation.EXACT;
				else
					return Annotation.INDIRECT;
				
			}
			else
				return Annotation.NONE;
			
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
	 * Returns a subset of protein Ids in the given list that have a GO annotation.
	 * @param proteinIds
	 * @return
	 * @throws SQLException
	 */
	public static List<Integer> getAnnotatedProteins (List<Integer> proteinIds) throws SQLException {
		
		// Get our connection to the database.
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		List<Integer> annotated = new ArrayList<Integer>(proteinIds.size());
		
		try {
			String sqlStr = "SELECT proteinID "+
							"FROM GOProteinLookup_Ref "+
							"WHERE proteinID=?"+
							" LIMIT 1";
			
			conn = DBConnectionManager.getConnection("go");	
			stmt = conn.prepareStatement(sqlStr);
			
			for(Integer proteinId: proteinIds) {
				stmt.setInt(1, proteinId);
				rs = stmt.executeQuery();
				
				if (rs.next()) {
					annotated.add(proteinId);
				}
				else {
					//System.out.println("No annotations found for "+proteinId);
				}
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
		
		return annotated;
	}
	
	/**
	 * Returns the appropriate Annotation type (NONE, EXACT, INDIRECT) for the given protein ID and go term ID
	 * @param nrseqProteinId
	 * @param goTermId
	 * @return
	 * @throws SQLException
	 */
	public static Annotation isProteinAnnotated(int nrseqProteinId) throws SQLException {
		
		// Get our connection to the database.
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		
		try {
			String sqlStr = "SELECT exact "+
							"FROM GOProteinLookup_Ref "+
							"WHERE proteinID="+nrseqProteinId;
			
			conn = DBConnectionManager.getConnection("go");	
			stmt = conn.createStatement();
			
			rs = stmt.executeQuery(sqlStr);
			
			
			if (rs.next()) {
				
				if(rs.getInt(1) == 1)
					return Annotation.EXACT;
				else
					return Annotation.INDIRECT;
				
			}
			else
				return Annotation.NONE;
			
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
