/**
 * InvoiceInstrumentUsageDAO.java
 * @author Vagisha Sharma
 * Jul 16, 2011
 */
package org.uwpr.costcenter;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.log4j.Logger;
import org.yeastrc.db.DBConnectionManager;

/**
 * 
 */
public class InvoiceInstrumentUsageDAO {

	private static final Logger log = Logger.getLogger(InvoiceInstrumentUsageDAO.class);
	
	private static InvoiceInstrumentUsageDAO instance = new InvoiceInstrumentUsageDAO();
	
	public static InvoiceInstrumentUsageDAO getInstance() {
		return instance;
	}
	
	public void save(InvoiceInstrumentUsage invoiceBlock) throws SQLException {
		
		String sql = "INSERT INTO invoiceInstrumentUsage (invoiceID, instrumentUsageID) VALUES (?,?)";
		Connection conn = null;
		PreparedStatement stmt = null;
		
		try {
			conn = getConnection();
			stmt = conn.prepareStatement(sql);
			stmt.setInt(1, invoiceBlock.getInvoiceId());
			stmt.setInt(2, invoiceBlock.getInstrumentUsageId());
			stmt.executeUpdate();
		}
		finally {
			if(conn != null) try {conn.close();} catch(SQLException e){}
			if(stmt != null) try {stmt.close();} catch(SQLException e){}
		}
		
	}
	
	public InvoiceInstrumentUsage getInvoiceBlock (int instrumentUsageId) throws SQLException {
		
		String sql = "SELECT * FROM invoiceInstrumentUsage WHERE instrumentUsageID="+instrumentUsageId;
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		
		try {
			conn = getConnection();
			stmt = conn.createStatement();
			rs = stmt.executeQuery(sql);
			
			if(rs.next()) {
				InvoiceInstrumentUsage invoiceBlock = new InvoiceInstrumentUsage();
				invoiceBlock.setId(rs.getInt("id"));
				invoiceBlock.setInvoiceId(rs.getInt("invoiceID"));
				invoiceBlock.setInstrumentUsageId(rs.getInt("instrumentUsageID"));
				return invoiceBlock;
			}
			else {
				log.error("No entry found in table invoiceInstrumentUsage for instrumentUsageID: "+instrumentUsageId);
			}
		}
		finally {
			if(conn != null) try {conn.close();} catch(SQLException e){}
			if(stmt != null) try {stmt.close();} catch(SQLException e){}
			if(rs != null) try {rs.close();} catch(SQLException e){}
		}
		
		return null;
	}

	private Connection getConnection() throws SQLException {
		return DBConnectionManager.getConnection(DBConnectionManager.MAIN_DB);
	}
}
