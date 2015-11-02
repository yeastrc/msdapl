package org.uwpr.instrumentlog;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.uwpr.costcenter.InstrumentRate;
import org.uwpr.costcenter.InstrumentRateDAO;
import org.yeastrc.db.DBConnectionManager;

public class MsInstrumentUtils {

	private static MsInstrumentUtils instance = new MsInstrumentUtils();

	private MsInstrumentUtils() {}

	public static MsInstrumentUtils instance() {
		return instance;
	}

	//--------------------------------------------------------------------------------------------
	// MS instrument
	//--------------------------------------------------------------------------------------------
	/**
	 * Returns a list of all instruments (active and retired).
	 */
	public List <MsInstrument> getMsInstruments() {
		return getMsInstruments(false); 
	}

	public List <MsInstrument> getMsInstruments(boolean activeOnly) {

		// Get our connection to the database.
		Connection conn = null;

		try {
			conn = getConnection();
			return getMsInstruments(conn, activeOnly);

		} catch (SQLException e) {
			e.printStackTrace();
		}

		finally {
			// Make sure the connection is returned to the pool
			if (conn != null) {
				try { conn.close(); } catch (SQLException e) { ; }
				conn = null;
			}
		}
		return null;
	}

	public List <MsInstrument> getMsInstruments(Connection conn, boolean activeOnly) {

		ArrayList<MsInstrument> list = new ArrayList<MsInstrument>();

		PreparedStatement stmt = null;
		ResultSet rs = null;

		try {
			String sql = "select * from "+DBConnectionManager.MS_DATA+".msInstrument";
			if(activeOnly) {
				sql += " WHERE active=1";
			}
			stmt = conn.prepareStatement(sql);
			rs = stmt.executeQuery();
			while (rs.next()) {
				int id = rs.getInt("id");
				String name = rs.getString("name");
				String desc = rs.getString("description");
				boolean active = rs.getBoolean("active");
				list.add(new MsInstrument(id, name, desc, active));
			}

		} catch (SQLException e) {
			e.printStackTrace();
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
		}

		return list;
	}

	public MsInstrument getMsInstrument(int instrumentID) {

		// Get our connection to the database.
		Connection conn = null;

		try {
			conn = getConnection();
			return getMsInstrument(instrumentID, conn);

		} catch (SQLException e) {
			e.printStackTrace();
		}

		finally {
			// Make sure the connection is returned to the pool
			if (conn != null) {
				try { conn.close(); } catch (SQLException e) { ; }
				conn = null;
			}
		}
		return null;
	}

	MsInstrument getMsInstrument(int instrumentID, Connection conn) {

		if (conn == null)
			return null;

		PreparedStatement stmt = null;
		ResultSet rs = null;

		try {
			String sql = "select * from "+DBConnectionManager.MS_DATA+".msInstrument where id="+instrumentID;
			stmt = conn.prepareStatement(sql);
			rs = stmt.executeQuery();
			if (rs.next()) {
				int id = rs.getInt("id");
				String name = rs.getString("name");
				String desc = rs.getString("description");
				boolean active = rs.getBoolean("active");
				return new MsInstrument(id, name, desc, active);
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}

		finally {

			// Always make sure result sets and statements are closed,
			if (rs != null) {
				try { rs.close(); } catch (SQLException e) { ; }
				rs = null;
			}
			if (stmt != null) {
				try { stmt.close(); } catch (SQLException e) { ; }
				stmt = null;
			}
		}

		return null;
	}

	//--------------------------------------------------------------------------------------------
	// UsageBlockBase
	//--------------------------------------------------------------------------------------------
	public UsageBlockBase getUsageBlockBase(int usageID) {
		// Get our connection to the database.
		Connection conn = null;
		try {
			conn = getConnection();
			return getUsageBlockBase(usageID, conn);

		} catch (SQLException e) {
			e.printStackTrace();
		}
		finally {
			// Make sure the connection is returned to the pool
			if (conn != null) {
				try { conn.close(); } catch (SQLException e) { ; }
				conn = null;
			}
		}
		return null;
	}

	UsageBlockBase getUsageBlockBase(int usageID, Connection conn) {

		if (conn == null)
			return null;
		PreparedStatement stmt = null;
		ResultSet rs = null;

		try {
			String sql = "SELECT * from instrumentUsage WHERE id="+usageID;
			stmt = conn.prepareStatement(sql);
			rs = stmt.executeQuery();

			if (rs.next()) {
				UsageBlockBase blk = makeUsageBlockBase(rs);
				return blk;
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}

		finally {

			// Always make sure result sets and statements are closed,
			if (rs != null) {
				try { rs.close(); } catch (SQLException e) { ; }
				rs = null;
			}
			if (stmt != null) {
				try { stmt.close(); } catch (SQLException e) { ; }
				stmt = null;
			}
		}

		return null;
	}

	private UsageBlockBase makeUsageBlockBase(ResultSet rs) throws SQLException {
		UsageBlockBase blk = new UsageBlockBase();
		blk.setID(rs.getInt("id"));
		blk.setResearcherID(rs.getInt("enteredBy"));
		blk.setInstrumentID(rs.getInt("instrumentID"));
		blk.setProjectID(rs.getInt("projectID"));
		blk.setStartDate(rs.getTimestamp("startDate"));
		blk.setEndDate(rs.getTimestamp("endDate"));
		blk.setDateCreated(rs.getTimestamp("dateEntered"));
		Integer updaterResearcherId = rs.getInt("updatedBy");
		if(updaterResearcherId != null) {
			blk.setUpdaterResearcherID(updaterResearcherId);
		}
		blk.setDateChanged(rs.getTimestamp("lastChanged"));
		blk.setNotes(rs.getString("notes"));
		return blk;
	}


	private Connection getConnection() throws SQLException {
		return DBConnectionManager.getConnection(DBConnectionManager.MAIN_DB);
	}

	//--------------------------------------------------------------------------------------------
	// UsageBlocks for instrument
	//--------------------------------------------------------------------------------------------
	public List<UsageBlock> getUsageBlocksForInstrument(int instrumentID, java.util.Date startDate, java.util.Date endDate) throws SQLException {
		return getUsageBlocksForInstrument(instrumentID, startDate, endDate, true, null); // truncate blocks to fit the start and end dates
	}

	public List<UsageBlock> getUsageBlocksForInstrument(int instrumentID, java.util.Date startDate, java.util.Date endDate,
			boolean truncate) throws SQLException {
		return getUsageBlocksForInstrument(instrumentID, startDate, endDate, truncate, null);
	}

	List<UsageBlock> getUsageBlocksForInstrument(int instrumentID, java.util.Date startDate, java.util.Date endDate, 
			boolean truncate, String sortBy) throws SQLException {
		// Get our connection to the database.
		Connection conn = null;
		try {
			conn = getConnection();
			return getUsageBlocksForInstrument(instrumentID, startDate, endDate, truncate, sortBy, conn);

		} 
		finally {
			// Make sure the connection is returned to the pool
			if (conn != null) {
				try { conn.close(); } catch (SQLException e) { ; }
				conn = null;
			}
		}
	}

	List<UsageBlock> getUsageBlocksForInstrument(int instrumentID, 
			java.util.Date startDate, java.util.Date endDate, boolean truncate,
			String sortBy, Connection conn) throws SQLException {
		if (conn == null)
			return null;
		List <UsageBlock> usageBlks = new ArrayList<UsageBlock>();

		PreparedStatement stmt = null;
		ResultSet rs = null;

		try {
			String sql = makeUsageSql(instrumentID, startDate, endDate, sortBy);
			stmt = conn.prepareStatement(sql);
			rs = stmt.executeQuery();
			while (rs.next()) {

				UsageBlock newBlk = makeUsageBlock(rs);

				// Truncate the actual usage time, if required, to match the given start and end dates.
				if(truncate) {
					truncateBlock(newBlk, startDate, endDate);
				}
				usageBlks.add(newBlk);
			}
			return usageBlks;

		} 

		finally {
			// Always make sure result sets and statements are closed,
			if (rs != null) {
				try { rs.close(); } catch (SQLException e) { ; }
				rs = null;
			}
			if (stmt != null) {
				try { stmt.close(); } catch (SQLException e) { ; }
				stmt = null;
			}
		}
	}

	private UsageBlock makeUsageBlock(ResultSet rs) throws SQLException {
		UsageBlock blk = new UsageBlock();
		blk.setID(rs.getInt("id"));
		blk.setResearcherID(rs.getInt("enteredBy"));
		blk.setInstrumentID(rs.getInt("instrumentID"));
		blk.setInstrumentRateID(rs.getInt("instrumentRateID"));
		blk.setInstrumentName(rs.getString("name"));
		blk.setProjectID(rs.getInt("projectID"));
		blk.setProjectTitle(rs.getString("projectTitle"));
		blk.setPIID(rs.getInt("projectPI"));
		blk.setProjectPI(rs.getString("researcherLastName"));
		blk.setStartDate(rs.getTimestamp("startDate"));
		blk.setEndDate(rs.getTimestamp("endDate"));
		blk.setDateCreated(rs.getTimestamp("dateEntered"));
		blk.setDateChanged(rs.getTimestamp("lastChanged"));
		blk.setNotes(rs.getString("notes"));

		InstrumentUsagePaymentDAO iupDao = InstrumentUsagePaymentDAO.getInstance();
		List<InstrumentUsagePayment> payments = iupDao.getPaymentsForUsage(blk.getID());
		blk.setPayments(payments);

		InstrumentRateDAO rateDao = InstrumentRateDAO.getInstance();
		InstrumentRate rate = rateDao.getInstrumentRate(blk.getInstrumentRateID());
		if(rate == null)
			throw new SQLException("No instrument rate found for ID: "+blk.getInstrumentRateID());
		blk.setRate(rate.getRate());

		return blk;
	}

	private void truncateBlock(UsageBlockBase newBlk, java.util.Date startDate, java.util.Date endDate) {
		if(startDate != null) {
			Date start = newBlk.getStartDate();
			start = start.after(startDate) ? start : startDate; // DateUtils.defaultStartTime(startDate);
			newBlk.setStartDate(start);
		}
		if(endDate != null) {
			Date end = newBlk.getEndDate();
			end = end.before(endDate) ? end : endDate; // DateUtils.defaultEndTime(endDate);
			newBlk.setEndDate(end);
		}

	}

	//--------------------------------------------------------------------------------------------
	// SQL
	//--------------------------------------------------------------------------------------------
	private String makeUsageSql(int instrumentID, java.util.Date startDate, java.util.Date endDate, String orderBy) {
		StringBuilder buf = new StringBuilder();
		buf.append(baseSql());
		if (instrumentID != -1) {
			buf.append("AND instrumentID=");
			buf.append(instrumentID);
			buf.append(" ");
		}
		if(startDate != null) {
			buf.append("AND startDate <= ");
			buf.append(makeDateForQuery(endDate));
			buf.append(" ");
		}
		if(endDate != null) {
			buf.append("AND endDate >= ");
			buf.append(makeDateForQuery(startDate));
			buf.append(" ");
		}
		if (orderBy != null)
			buf.append("ORDER BY "+orderBy);

		//  System.out.println(buf.toString());
		return buf.toString();

	}

	private String baseSql() {
		StringBuilder buf = new StringBuilder();
		buf.append("SELECT insUsg.*, "+
				"ins.name, "
				+"proj.projectTitle, proj.projectPI, "+
				"r.researcherLastName "
		);
		buf.append("FROM "+DBConnectionManager.MS_DATA+".msInstrument AS ins, instrumentUsage AS insUsg, tblProjects AS proj, tblResearchers AS r ");
		buf.append("WHERE proj.projectID=insUsg.projectID ");
		buf.append("AND r.researcherID=proj.projectPI ");
		buf.append("AND ins.id=insUsg.instrumentID ");
		return buf.toString();
	}

	private String makeDateForQuery(java.util.Date date) {
		//  StringBuilder buf = new StringBuilder("DATE('"+DateUtils.getYear(date)+"-"+DateUtils.getMonth(date)+"-"+DateUtils.getDay(date));
		//  buf.append(" "+DateUtils.getHour24(date)+":"+DateUtils.getMinutes(date)+":"+DateUtils.getSeconds(date)+"')");

		StringBuilder buf = new StringBuilder("'"+DateUtils.getYear(date)+"-"+DateUtils.getMonth(date)+"-"+DateUtils.getDay(date));
		buf.append(" "+DateUtils.getHour24(date)+":"+DateUtils.getMinutes(date)+":"+DateUtils.getSeconds(date)+"'");

		return buf.toString();
	}

}

