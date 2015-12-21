/**
 * PaymentMethodDAO.java
 * @author Vagisha Sharma
 * May 20, 2011
 */
package org.yeastrc.project.payment;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.yeastrc.db.DBConnectionManager;

/**
 * 
 */
public class PaymentMethodDAO {

	private PaymentMethodDAO() {}
	
	private static final Logger log = Logger.getLogger(PaymentMethodDAO.class);
	
	private static PaymentMethodDAO instance = new PaymentMethodDAO();
	
	public static PaymentMethodDAO getInstance() {
		return instance;
	}
	
	public PaymentMethod getPaymentMethod (int paymentMethodId) throws SQLException {
		
		String sql = "SELECT * FROM paymentMethod WHERE id="+paymentMethodId;
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		
		try {
			conn = getConnection();
			stmt = conn.createStatement();
			rs = stmt.executeQuery(sql);
			
			if(rs.next()) {
				PaymentMethod paymentMethod = new PaymentMethod();
				paymentMethod.setId(paymentMethodId);
				paymentMethod.setUwbudgetNumber(rs.getString("UWBudgetNumber"));
				paymentMethod.setPonumber(rs.getString("PONumber"));
				paymentMethod.setPaymentMethodName(rs.getString("paymentMethodName"));
				paymentMethod.setContactFirstName(rs.getString("contactNameFirst"));
				paymentMethod.setContactLastName(rs.getString("contactLastName"));
				paymentMethod.setContactEmail(rs.getString("contactEmail"));
				paymentMethod.setContactPhone(rs.getString("contactPhone"));
				paymentMethod.setOrganization(rs.getString("organization"));
				paymentMethod.setAddressLine1(rs.getString("addressLine1"));
				paymentMethod.setAddressLine2(rs.getString("addressLine2"));
				paymentMethod.setCity(rs.getString("city"));
				paymentMethod.setState(rs.getString("state"));
				paymentMethod.setZip(rs.getString("zip"));
				paymentMethod.setCountry(rs.getString("country"));
				paymentMethod.setCreatorId(rs.getInt("createdBy"));
				paymentMethod.setCreateDate(rs.getTimestamp("dateCreated"));
				paymentMethod.setLastUpdateDate(rs.getTimestamp("lastUpdated"));
				paymentMethod.setCurrent(rs.getBoolean("isCurrent"));
				paymentMethod.setFederalFunding(rs.getBoolean("federalFunding"));
				
				return paymentMethod;
			}
			else {
				log.error("No entry found in table isCurrent for id: "+paymentMethodId);
				return null;
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
	
	public List<PaymentMethod> getPaymentMethods (String contactFirstName, String contactLastName) throws SQLException {
		
		String sql = "SELECT * FROM paymentMethod WHERE contactNameFirst=\""+contactFirstName+"\" AND contactLastName=\""+contactLastName+"\"";
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		
		try {
			conn = getConnection();
			stmt = conn.createStatement();
			rs = stmt.executeQuery(sql);
			
			List<PaymentMethod> methods = new ArrayList<PaymentMethod>();
			
			while(rs.next()) {
				PaymentMethod paymentMethod = new PaymentMethod();
				paymentMethod.setId(rs.getInt("id"));
				paymentMethod.setUwbudgetNumber(rs.getString("UWBudgetNumber"));
				paymentMethod.setPonumber(rs.getString("PONumber"));
				paymentMethod.setPaymentMethodName(rs.getString("paymentMethodName"));
				paymentMethod.setContactFirstName(rs.getString("contactNameFirst"));
				paymentMethod.setContactLastName(rs.getString("contactLastName"));
				paymentMethod.setContactEmail(rs.getString("contactEmail"));
				paymentMethod.setContactPhone(rs.getString("contactPhone"));
				paymentMethod.setOrganization(rs.getString("organization"));
				paymentMethod.setAddressLine1(rs.getString("addressLine1"));
				paymentMethod.setAddressLine2(rs.getString("addressLine2"));
				paymentMethod.setCity(rs.getString("city"));
				paymentMethod.setState(rs.getString("state"));
				paymentMethod.setZip(rs.getString("zip"));
				paymentMethod.setCountry(rs.getString("country"));
				paymentMethod.setCreatorId(rs.getInt("createdBy"));
				paymentMethod.setCreateDate(rs.getTimestamp("dateCreated"));
				paymentMethod.setLastUpdateDate(rs.getTimestamp("lastUpdated"));
				paymentMethod.setCurrent(rs.getBoolean("isCurrent"));
				paymentMethod.setFederalFunding(rs.getBoolean("federalFunding"));
				
				methods.add(paymentMethod);
			}
			
			return methods;
		}
		finally {
			if(conn != null) try {conn.close();} catch(SQLException e){}
			if(stmt != null) try {stmt.close();} catch(SQLException e){}
			if(rs != null) try {rs.close();} catch(SQLException e){}
		}
	}

	
	public int savePaymentMethod(PaymentMethod paymentMethod) throws SQLException {
		
		String sql = "INSERT INTO paymentMethod (UWBudgetNumber, PONumber, paymentMethodName, contactNameFirst, contactLastName, contactEmail,";
		sql += " contactPhone, organization, addressLine1, addressLine2, city, state, zip, country, ";
		sql += " dateCreated,  createdBy, isCurrent, federalFunding) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
		
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		try {
			conn = getConnection();
			int i = 1;
			stmt = conn.prepareStatement(sql);
			stmt.setString(i++, paymentMethod.getUwbudgetNumber());
			stmt.setString(i++, paymentMethod.getPonumber());
			stmt.setString(i++, paymentMethod.getPaymentMethodName());
			stmt.setString(i++, paymentMethod.getContactFirstName());
			stmt.setString(i++, paymentMethod.getContactLastName());
			stmt.setString(i++, paymentMethod.getContactEmail());
			stmt.setString(i++, paymentMethod.getContactPhone());
			stmt.setString(i++, paymentMethod.getOrganization());
			stmt.setString(i++, paymentMethod.getAddressLine1());
			stmt.setString(i++, paymentMethod.getAddressLine2());
			stmt.setString(i++, paymentMethod.getCity());
			stmt.setString(i++, paymentMethod.getState());
			stmt.setString(i++, paymentMethod.getZip());
			stmt.setString(i++, paymentMethod.getCountry());
			stmt.setTimestamp(i++, new Timestamp(new Date().getTime()));
			stmt.setInt(i++, paymentMethod.getCreatorId());
			stmt.setInt(i++, 1);
			if(paymentMethod.isFederalFunding()) {
				stmt.setInt(i, 1);
			}
			else {
				stmt.setInt(i, 0);
			}
			
			int numRowsInserted = stmt.executeUpdate();
			if(numRowsInserted == 0) {
				throw new SQLException("Creating payment method failed, no rows affected.");
			}
			
			rs = stmt.getGeneratedKeys();
	        if (rs.next()) {
	        	paymentMethod.setId(rs.getInt(1));
	        } else {
	            throw new SQLException("Creating payment method failed, no generated key obtained.");
	        }

			return paymentMethod.getId();
			
		}
		finally {
			if(conn != null) try {conn.close();} catch(SQLException e){}
			if(stmt != null) try {stmt.close();} catch(SQLException e){}
			if(rs != null) try {rs.close();} catch(SQLException e){}
		}
	}
	
	public void updatePaymentMethod(PaymentMethod paymentMethod) throws SQLException {
		
		String sql = "UPDATE paymentMethod ";
		sql += "SET UWBudgetNumber = ?";
		sql += ", PONumber = ?";
		sql += ", paymentMethodName = ?";
		sql += ", contactNameFirst= ?";
		sql += ", contactLastName = ?";
		sql += ", contactEmail = ?";
		sql += ", contactPhone = ?";
		sql += ", organization = ?";
		sql += ", addressLine1 = ?";
		sql += ", addressLine2 = ?";
		sql += ", city = ?";
		sql += ", state = ?";
		sql += ", zip = ?";
		sql += ", country = ?";
		sql += ", createdBy = ?";
		sql += ", isCurrent = ?";
		sql += ", federalFunding = ?";
		sql += " WHERE id=?";
		
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		try {
			conn = getConnection();
			stmt = conn.prepareStatement(sql);
			int i = 0;
			stmt.setString(++i, paymentMethod.getUwbudgetNumber());
			stmt.setString(++i, paymentMethod.getPonumber());
			stmt.setString(++i, paymentMethod.getPaymentMethodName());
			stmt.setString(++i, paymentMethod.getContactFirstName());
			stmt.setString(++i, paymentMethod.getContactLastName());
			stmt.setString(++i, paymentMethod.getContactEmail());
			stmt.setString(++i, paymentMethod.getContactPhone());
			stmt.setString(++i, paymentMethod.getOrganization());
			stmt.setString(++i, paymentMethod.getAddressLine1());
			stmt.setString(++i, paymentMethod.getAddressLine2());
			stmt.setString(++i, paymentMethod.getCity());
			stmt.setString(++i, paymentMethod.getState());
			stmt.setString(++i, paymentMethod.getZip());
			stmt.setString(++i, paymentMethod.getCountry());
			stmt.setInt(++i, paymentMethod.getCreatorId());
			i++;
			if(paymentMethod.isCurrent())
				stmt.setInt(i, 1);
			else
				stmt.setInt(i, 0);
			i++;
			if(paymentMethod.isFederalFunding())
				stmt.setInt(i, 1);
			else
				stmt.setInt(i, 0);
			
			stmt.setInt(++i,paymentMethod.getId());
			
			int numRowsInserted = stmt.executeUpdate();
			if(numRowsInserted == 0) {
				throw new SQLException("Updating payment method failed, no rows affected.");
			}
			
		}
		finally {
			if(conn != null) try {conn.close();} catch(SQLException e){}
			if(stmt != null) try {stmt.close();} catch(SQLException e){}
			if(rs != null) try {rs.close();} catch(SQLException e){}
		}
	}
	
	public void deletePaymentMethod(int paymentMethodId) throws SQLException {
	
		String sql = "DELETE FROM paymentMethod WHERE id="+paymentMethodId;
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		
		try {
			conn = getConnection();
			stmt = conn.createStatement();
			int numRowsDeleted = stmt.executeUpdate(sql);
			
			if(numRowsDeleted == 0) {
				throw new SQLException("Deleting payment method failed, no rows affected.");
			}
			
		}
		finally {
			if(conn != null) try {conn.close();} catch(SQLException e){}
			if(stmt != null) try {stmt.close();} catch(SQLException e){}
			if(rs != null) try {rs.close();} catch(SQLException e){}
		}
	}
	
}
