import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.yeastrc.ms.dao.DAOFactory;

/**
 * PercolatorFilteredSpectraStatsSaver.java
 * @author Vagisha Sharma
 * Oct 3, 2010
 */

/**
 * 
 */
public class PercolatorFilteredSpectraStatsSaver {

	public static void main(String[] args) throws SQLException {

		List<Integer> searchAnalysisIds = getSearchAnalysisIds();
		
		org.yeastrc.ms.service.percolator.stats.PercolatorFilteredSpectraStatsSaver saver = 
			org.yeastrc.ms.service.percolator.stats.PercolatorFilteredSpectraStatsSaver.getInstance();
		
		for(Integer saId: searchAnalysisIds) {
			saver.save(saId, 0.01);
		}
	}
	
	private static List<Integer> getSearchAnalysisIds() throws SQLException {

		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;

		try {
			conn = DAOFactory.instance().getConnection();
			String sql = "SELECT id FROM msSearchAnalysis ORDER BY id DESC";
			stmt = conn.createStatement();
			rs = stmt.executeQuery(sql);

			List<Integer> pinferIds = new ArrayList<Integer>();
			while(rs.next()) {
				pinferIds.add(rs.getInt("id"));
			}
			return pinferIds;
		}
		finally {
			if(conn != null) try {conn.close();}catch(Exception e){}
			if(stmt != null) try {stmt.close();}catch(Exception e){}
			if(rs != null) try {rs.close();}catch(Exception e){}
		}
	}
}
