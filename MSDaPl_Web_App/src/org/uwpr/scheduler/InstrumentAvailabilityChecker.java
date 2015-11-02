/**
 * 
 */
package org.uwpr.scheduler;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.yeastrc.db.DBConnectionManager;

/**
 * InstrumentAvailabilityChecker.java
 * @author Vagisha Sharma
 * Jun 3, 2011
 * 
 */
public class InstrumentAvailabilityChecker {

	private static InstrumentAvailabilityChecker instance = new InstrumentAvailabilityChecker();
	
	private static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss");
	
	private InstrumentAvailabilityChecker () {}
	
	public static InstrumentAvailabilityChecker getInstance() {
		return instance;
	}
	
	public boolean isInstrumentAvailable(int instrumentId, Date startDate, Date endDate) throws SQLException{
		
		return isInstrumentAvailable(instrumentId, startDate, endDate, null);
	}
	
	public boolean isInstrumentAvailable(int instrumentId, Date startDate, Date endDate, List<Integer> excludeUsageIds) throws SQLException{
		
		String sql = "SELECT * from instrumentUsage WHERE instrumentID="+instrumentId+
		             " AND startDate < '"+(dateFormat.format(endDate))+"'"+
		             " AND endDate > '"+(dateFormat.format(startDate))+"'";
		
		if(excludeUsageIds != null && excludeUsageIds.size() > 0) {
			sql += " AND id NOT IN ("+StringUtils.join(excludeUsageIds.toArray(), ',')+")";
		}
		
		//System.out.println(sql);
		
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		
		try {
			conn = getConnection();
			stmt = conn.createStatement();
			rs = stmt.executeQuery(sql);
			
			if(rs.next()) {
				return false;
			}
			else {
				return true;
			}
		}
		finally {
			if(conn != null) try {conn.close();} catch(SQLException e){}
			if(stmt != null) try {stmt.close();} catch(SQLException e){}
			if(rs != null) try {rs.close();} catch(SQLException e){}
		}
	}

	private Connection getConnection() throws SQLException {
		return DBConnectionManager.getConnection(DBConnectionManager.MAIN_DB);
	}
}
