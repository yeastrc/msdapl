/**
 * InstrumentUsageDAO.java
 * @author Vagisha Sharma
 * Jun 13, 2011
 */
package org.uwpr.instrumentlog;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.List;

import org.apache.log4j.Logger;
import org.yeastrc.db.DBConnectionManager;

/**
 * 
 */
public class InstrumentUsageDAO {

	private static final InstrumentUsageDAO instance = new InstrumentUsageDAO();
	
	private static final Logger log = Logger.getLogger(InstrumentUsageDAO.class);
	
	private InstrumentUsageDAO () {}
	
	public static InstrumentUsageDAO getInstance() {
		return instance;
	}
	
	public void save(UsageBlockBase block) throws Exception {

		if (block == null)
			return;

		log.info("Saving usage block on instrument: "+block.getInstrumentID());
		
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		
		try {

			boolean newBlock = false;
			if (block.getID() == 0) newBlock = true;

			String sql = "SELECT * FROM instrumentUsage WHERE id = " + block.getID();

			conn = getConnection();
			stmt = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
			rs = stmt.executeQuery( sql );

			if (!newBlock) {
				if (!rs.next())
					throw new Exception( "InstrumentUsageBlock had id, but was not in database.  Aborting save." );
			} else {
				rs.moveToInsertRow();
			}

			rs.updateInt("projectID", block.getProjectID());
			rs.updateInt("enteredBy", block.getResearcherID());
			if(block.getUpdaterResearcherID() != 0) {
				rs.updateInt("updatedBy", block.getUpdaterResearcherID());
			}
			
			rs.updateInt("instrumentID", block.getInstrumentID());
			rs.updateInt("instrumentRateID", block.getInstrumentRateID());
			rs.updateTimestamp("startDate", new Timestamp(block.getStartDate().getTime()));
			rs.updateTimestamp("endDate", new Timestamp(block.getEndDate().getTime()));
//			rs.updateTimestamp("lastChanged", new Timestamp(new java.util.Date().getTime()));
			rs.updateString("notes", block.getNotes());
			if (newBlock) {
				if(block.getDateCreated() == null)
					rs.updateTimestamp("dateEntered", new Timestamp(System.currentTimeMillis()));
				else
					rs.updateTimestamp("dateEntered", new Timestamp(block.getDateCreated().getTime()));
			}
			
			if (!newBlock) {
				rs.updateRow();
			} else {
				rs.insertRow();
				rs.last();
				block.setID( rs.getInt("id") );
			}

			rs.close(); rs = null;
			stmt.close(); stmt = null;
			conn.close(); conn = null;

		} finally {

			if(conn != null) try {conn.close();} catch(SQLException e){}
			if(stmt != null) try {stmt.close();} catch(SQLException e){}
			if(rs != null) try {rs.close();} catch(SQLException e){}
		}
	}

	public void updateBlocksDates(List<? extends UsageBlockBase> blocks) throws Exception {

		if (blocks == null || blocks.size() == 0)
			return;

		log.info("Updating usage blocks on instrument: " + blocks.get(0).getInstrumentID());


		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;

        StringBuilder sql = new StringBuilder("Update InstrumentUsage SET");
        sql.append(" startDate = ?");
        sql.append(", endDate = ?");
        sql.append(", updatedBy = ?");
        sql.append(" WHERE id = ?");
		try {

            conn = getConnection();
            conn.setAutoCommit(false);
            stmt = conn.prepareStatement(sql.toString());

            for(UsageBlockBase block: blocks)
            {
                stmt.setTimestamp(1, new Timestamp(block.getStartDate().getTime()));
                stmt.setTimestamp(2, new Timestamp(block.getEndDate().getTime()));
                stmt.setInt(3, block.getUpdaterResearcherID());
                stmt.setInt(4, block.getID());
                stmt.executeUpdate();
            }
            conn.commit();

		} finally {

			if(conn != null) try {conn.close();} catch(SQLException e){}
			if(stmt != null) try {stmt.close();} catch(SQLException e){}
			if(rs != null) try {rs.close();} catch(SQLException e){}
		}
	}


	private Connection getConnection() throws SQLException {
		return DBConnectionManager.getConnection(DBConnectionManager.MAIN_DB);
	}
	
	public boolean hasInstrumentUsageForInstrumentRate(int instrumentRateId) throws SQLException {
		
		String sql = "SELECT count(*) FROM instrumentUsage WHERE instrumentRateID = "+instrumentRateId;
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		
		try {
			conn = getConnection();
			stmt = conn.createStatement();
			rs = stmt.executeQuery(sql);
			
			if(rs.next()) {
				int count = rs.getInt(1);
				if(count == 0)
					return false;
				else
					return true;
			}
		}
		finally {
			if(conn != null) try {conn.close();} catch(SQLException e){}
			if(stmt != null) try {stmt.close();} catch(SQLException e){}
			if(rs != null) try {rs.close();} catch(SQLException e){}
		}
		
		return false;
	}
	
	public void delete(int usageId) throws SQLException {
		
		
		// NOTE: There is a trigger on instrumentUsage table that will 
		//       delete all entries in the instrumentUsagePayment where instrumentUsageID is equal to 
		//       the given usageId
		
		log.info("Deleting usage block ID "+usageId);
		Connection conn = null;
		Statement stmt = null;
		
		try {
			
			String sql = "DELETE FROM instrumentUsage WHERE id="+usageId;
			// System.out.println(sql);
			conn = getConnection();
			stmt = conn.createStatement();
			stmt.execute(sql);
			
			//InstrumentUsagePaymentDAO.getInstance().deletePaymentsForUsage(usageId);
			
		} finally {

				// Always make sure result sets and statements are closed,
				// and the connection is returned to the pool
				if (stmt != null) {
					try { stmt.close(); } catch (SQLException ignored) { ; }
				}
				if (conn != null) {
					try { conn.close(); } catch (SQLException ignored) { ; }
				}
		}
	}

	public void delete(List<Integer> usageIds) throws SQLException {

		// NOTE: There is a trigger on instrumentUsage table that will
		//       delete all entries in the instrumentUsagePayment where instrumentUsageID is equal to
		//       the given usageId

		Connection conn = null;
		PreparedStatement stmt = null;
		String sql = "DELETE FROM instrumentUsage WHERE id=?";

		try {
			conn = getConnection();
			conn.setAutoCommit(false);
			stmt = conn.prepareStatement( sql );

			for(Integer usageId: usageIds)
			{
				stmt.setInt(1, usageId);
				stmt.executeUpdate();
			}
			conn.commit();

		} finally {

			// Always make sure result sets and statements are closed,
			// and the connection is returned to the pool
			if (stmt != null) {
				try { stmt.close(); } catch (SQLException ignored) { ; }
			}
			if (conn != null) {
				try { conn.close(); } catch (SQLException ignored) { ; }
			}
		}
	}
}
