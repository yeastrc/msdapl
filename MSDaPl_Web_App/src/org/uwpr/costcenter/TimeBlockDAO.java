/**
 * TimeBlockDAO.java
 * @author Vagisha Sharma
 * Apr 30, 2011
 */
package org.uwpr.costcenter;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Time;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.yeastrc.db.DBConnectionManager;

/**
 * 
 */
public class TimeBlockDAO {

	private TimeBlockDAO() {}
	
	private static final Logger log = Logger.getLogger(TimeBlockDAO.class);
	
	private static TimeBlockDAO instance = new TimeBlockDAO();
	
	public static TimeBlockDAO getInstance() {
		return instance;
	}
	
	public TimeBlock getTimeBlock (int timeBlockId) throws SQLException {
		
		String sql = "SELECT * FROM timeBlock WHERE id="+timeBlockId;
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		
		try {
			conn = getConnection();
			stmt = conn.createStatement();
			rs = stmt.executeQuery(sql);
			
			if(rs.next()) {
				TimeBlock timeBlock = new TimeBlock();
				timeBlock.setId(timeBlockId);
				timeBlock.setNumHours(rs.getInt("numHours"));
				timeBlock.setName(rs.getString("name"));
				timeBlock.setStartTime(rs.getTime("startTime"));
				timeBlock.setCreateDate(rs.getDate("createDate"));
				
				return timeBlock;
			}
			else {
				log.error("No entry found in table timeBlock for id: "+timeBlockId);
			}
		}
		finally {
			if(conn != null) try {conn.close();} catch(SQLException e){}
			if(stmt != null) try {stmt.close();} catch(SQLException e){}
			if(rs != null) try {rs.close();} catch(SQLException e){}
		}
		
		return null;
	}
	
	public List<TimeBlock> getAllTimeBlocks() throws SQLException {
		
		String sql = "SELECT * FROM timeBlock ORDER BY id ASC";
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		
		try {
			conn = getConnection();
			stmt = conn.createStatement();
			rs = stmt.executeQuery(sql);
			
			List<TimeBlock> timeBlocks = new ArrayList<TimeBlock>();
			
			while(rs.next()) {
				TimeBlock timeBlock = new TimeBlock();
				timeBlock.setId(rs.getInt("id"));
				timeBlock.setNumHours(rs.getInt("numHours"));
				timeBlock.setName(rs.getString("name"));
				timeBlock.setStartTime(rs.getTime("startTime"));
				timeBlock.setCreateDate(rs.getDate("createDate"));
				timeBlocks.add(timeBlock);
			}
			
			
			return timeBlocks;
			
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
	
	public void saveTimeBlock(TimeBlock block) throws SQLException {
		
		String sql = "INSERT INTO timeBlock (numHours, startTime, endTime, name, createDate) VALUES (?,?,?,?,?)";
		Connection conn = null;
		PreparedStatement stmt = null;
		
		try {
			conn = getConnection();
			stmt = conn.prepareStatement(sql);
			stmt.setInt(1, block.getNumHours());
			if(block.getStartTime() != null)
				stmt.setTime(2, new Time(block.getStartTime().getTime()));
			else
				stmt.setNull(2, Types.TIME);
			if(block.getEndTime() != null)
				stmt.setTime(3, new Time(block.getEndTime().getTime()));
			else
				stmt.setNull(3, Types.TIME);
			stmt.setString(4, block.getName());
			stmt.setTimestamp(5, new Timestamp(System.currentTimeMillis()));
			
			stmt.executeUpdate();
		}
		finally {
			if(conn != null) try {conn.close();} catch(SQLException e){}
			if(stmt != null) try {stmt.close();} catch(SQLException e){}
		}
		
	}
	
	public void deleteTimeBlock(int blockId) throws SQLException {
		
		// NOTE: There is a trigger on timeBlock table that will 
		//       delete all entries in the instrumentRate where blockID is equal to 
		//       the given blockId
		String sql = "DELETE FROM timeBlock where id="+blockId;
		Connection conn = null;
		PreparedStatement stmt = null;
		
		try {
			conn = getConnection();
			stmt = conn.prepareStatement(sql);
			stmt.executeUpdate();
		}
		finally {
			if(conn != null) try {conn.close();} catch(SQLException e){}
			if(stmt != null) try {stmt.close();} catch(SQLException e){}
		}
	}
}
