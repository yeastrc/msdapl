/**
 * 
 */
package org.uwpr.instrumentlog;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.yeastrc.db.DBConnectionManager;
import org.yeastrc.project.payment.PaymentMethod;
import org.yeastrc.project.payment.PaymentMethodDAO;

/**
 * InstrumentUsagePaymentDAO.java
 * @author Vagisha Sharma
 * Jun 2, 2011
 * 
 */
public class InstrumentUsagePaymentDAO {

	private static final InstrumentUsagePaymentDAO instance = new InstrumentUsagePaymentDAO();
	
	private InstrumentUsagePaymentDAO () {}
	
	public static InstrumentUsagePaymentDAO getInstance() {
		return instance;
	}
	
	public List<InstrumentUsagePayment> getPaymentsForUsage(int instrumentUsageId) throws SQLException {
		
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		
		
        String sql = "SELECT * FROM instrumentUsagePayment WHERE instrumentUsageID="+instrumentUsageId;
        
        PaymentMethodDAO pmDao = PaymentMethodDAO.getInstance();
        
        List <InstrumentUsagePayment> payments = new ArrayList<InstrumentUsagePayment>();
        
        try {
        	conn = getConnection();
            stmt = conn.createStatement();
            rs = stmt.executeQuery(sql);
            while (rs.next()) {
                
            	InstrumentUsagePayment payment = new InstrumentUsagePayment();
            	payment.setInstrumentUsageId(instrumentUsageId);
            	int paymentMethodId = rs.getInt("paymentMethodID");
            	payment.setPercent(rs.getBigDecimal("percentPayment"));
            	payments.add(payment);
            	
            	PaymentMethod pm = pmDao.getPaymentMethod(paymentMethodId);
            	if(pm == null) {
            		throw new SQLException("No payment method found for ID: "+paymentMethodId);
            	}
            	payment.setPaymentMethod(pm);
            		
            }
            return payments;
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        finally {
            // Always make sure result sets and statements are closed,
        	if(conn != null) try {conn.close();} catch(SQLException e){}
			if(stmt != null) try {stmt.close();} catch(SQLException e){}
			if(rs != null) try {rs.close();} catch(SQLException e){}
        }
        return null;
	}

	private Connection getConnection() throws SQLException {
		return DBConnectionManager.getConnection(DBConnectionManager.MAIN_DB);
	}
	
	public void savePayment(InstrumentUsagePayment payment) throws SQLException {
		
		String sql = "INSERT INTO instrumentUsagePayment (instrumentUsageID, paymentMethodID, percentPayment) VALUES (?,?,?)";
		Connection conn = null;
		PreparedStatement stmt = null;
		
		try {
			conn = getConnection();
			stmt = conn.prepareStatement(sql);
			stmt.setInt(1, payment.getInstrumentUsageId());
			stmt.setInt(2, payment.getPaymentMethod().getId());
			stmt.setBigDecimal(3, payment.getPercent());
			
			stmt.executeUpdate();
		}
		finally {
			if(conn != null) try {conn.close();} catch(SQLException e){}
			if(stmt != null) try {stmt.close();} catch(SQLException e){}
		}
	}
	
	public boolean hasInstrumentUsageForPayment(int paymentMethodId) throws SQLException {
		
		String sql = "SELECT count(*) FROM instrumentUsagePayment WHERE paymentMethodID = "+paymentMethodId;
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

//	public void deletePaymentsForUsage (int instrumentUsageId) throws SQLException {
//		
//		String sql = "DELETE FROM instrumentUsagePayment where instrumentUsageID="+instrumentUsageId;
//		Connection conn = null;
//		PreparedStatement stmt = null;
//		
//		try {
//			conn = DBConnectionManager.getConnection("pr");
//			stmt = conn.prepareStatement(sql);
//			stmt.executeUpdate();
//		}
//		finally {
//			if(conn != null) try {conn.close();} catch(SQLException e){}
//			if(stmt != null) try {stmt.close();} catch(SQLException e){}
//		}
//	}
}
