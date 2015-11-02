/**
 * FlyBaseUtils.java
 * @author Vagisha Sharma
 * Jul 7, 2010
 */
package org.yeastrc.nrseq;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.yeastrc.db.DBConnectionManager;

/**
 * 
 */
public class FlyBaseUtils {

	private FlyBaseUtils() {}
	
	/**
	 * Attempts to get the FlyBase description for gene corresponding to the protein accession string passed in
	 * @param arg
	 * @return
	 * @throws SQLException
	 */
	public static List<Integer> getIdsMatchingDescription(List<String> descriptionTerms) throws SQLException {
		
		// Step 1. Get all unique proteinAcc from proteinAnnotation table that match the given description terms
		Set<String> descTerms = new HashSet<String>();
		for(String desc: descriptionTerms)
			descTerms.add(desc);
		Set<String> accessions = getAccessionsMatchingDesciption(descTerms);
		
		// Step 2. Get corresponding proteinIDs from YRC_NRSEQ's flybase. 
		Set<Integer> nrseqProteinIds = getProteinIdForAccession(accessions);
		
		return new ArrayList<Integer>(nrseqProteinIds);
	}
	
	private static Set<String> getAccessionsMatchingDesciption(Set<String> descriptionTerms) throws SQLException {
		
		if(descriptionTerms == null || descriptionTerms.size() == 0)
			return new HashSet<String>(0);
		
		
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;	
		
		String sql = "SELECT DISTINCT(p.proteinAcc) "+
					"FROM  proteinAnnotation AS p, geneAnnotation AS g, proteinGeneMapping AS m "+
					"WHERE p.proteinAcc = m.proteinAcc "+
					"AND g.geneAcc = m.geneAcc "+
					"AND g.geneDescription LIKE ?";
		try {
			conn = DBConnectionManager.getConnection("flybase");
			stmt = conn.prepareStatement(sql);
		
			Set<String> accessions = new HashSet<String>();
			for(String desc: descriptionTerms) {
				
				stmt.setString(1, "%"+desc+"%");
				rs = stmt.executeQuery();
				
				while (rs.next()) {
					accessions.add(rs.getString("proteinAcc"));
				}
			}
			return accessions;
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
	
	private static Set<Integer> getProteinIdForAccession(Set<String> accessions) throws SQLException {
		
		if(accessions == null || accessions.size() == 0) {
			return new HashSet<Integer>(0);
		}
		
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;	
		
		String sql = "SELECT DISTINCT(proteinID) FROM tblProteinDatabase WHERE accessionString=?";
		try {
			conn = DBConnectionManager.getConnection(DBConnectionManager.NRSEQ);
			stmt = conn.prepareStatement(sql);
		
			Set<Integer> proteinIds = new HashSet<Integer>();
			for(String accession: accessions) {
				
				stmt.setString(1, accession);
				rs = stmt.executeQuery();
				
				while (rs.next()) {
					proteinIds.add(rs.getInt("proteinID"));
				}
			}
			return proteinIds;
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
