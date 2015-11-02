/**
 * 
 */
package org.yeastrc.nrseq;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.yeastrc.db.DBConnectionManager;


/**
 * CommonNameCacheDAO.java
 * @author Vagisha Sharma
 * Jul 14, 2010
 * 
 */
public class CommonNameCacheDAO {

	private static CommonNameCacheDAO instance;
	
	private static final Logger log = Logger.getLogger(CommonNameCacheDAO.class.getName());
	
	private CommonNameCacheDAO() {}
	
	public static CommonNameCacheDAO getInstance() {
		if(instance == null) {
			instance = new CommonNameCacheDAO();
		}
		return instance;
	}
	
	public List<Integer> getMatches(List<String> names) {
		
		if(names == null || names.size() == 0)
			return new ArrayList<Integer>(0);
		
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		String sql = "SELECT DISTINCT proteinID FROM CommonNameTable WHERE name LIKE ?";
		
		List<Integer> matches = new ArrayList<Integer>();
		try {
			conn = DBConnectionManager.getConnection(DBConnectionManager.NRSEQ);
			stmt = conn.prepareStatement(sql);
			
			for(String name: names) {
				stmt.setString(1, name+"%");
				
				rs = stmt.executeQuery();
				while(rs.next()) {
					matches.add(rs.getInt(1));
				}
				rs.close();
			}
		
			return matches;
		}
		catch(SQLException e) {
			log.error("Error looking up common names from CommonNameTable", e);
			throw new RuntimeException("Error looking up common names from CommonNameTable", e);
		}
		finally {
			cleanUp(conn, stmt, rs);
		}
	}

	private void cleanUp(Connection conn, PreparedStatement stmt, ResultSet rs) {
		
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
