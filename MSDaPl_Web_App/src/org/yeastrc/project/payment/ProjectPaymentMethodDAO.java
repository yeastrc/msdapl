/**
 * ProjectPaymentMethodDAO.java
 * @author Vagisha Sharma
 * May 20, 2011
 */
package org.yeastrc.project.payment;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.yeastrc.db.DBConnectionManager;


/**
 * 
 */
public class ProjectPaymentMethodDAO {

	private ProjectPaymentMethodDAO() {}
	
	private static ProjectPaymentMethodDAO instance = new ProjectPaymentMethodDAO();
	
	private static final Logger log = Logger.getLogger(ProjectPaymentMethodDAO.class);
	
	public static ProjectPaymentMethodDAO getInstance() {
		return instance;
	}
	
	public List<PaymentMethod> getPaymentMethods(int projectId) throws SQLException {
		
		List<Integer> paymentMethodIds = new ArrayList<Integer>();
		String sql = "SELECT paymentMethodID FROM projectPaymentMethod WHERE projectID="+projectId;
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		
		try {
			conn = getConnection();
			
			stmt = conn.createStatement();
			rs = stmt.executeQuery(sql);
			while(rs.next()) {
				paymentMethodIds.add(rs.getInt("paymentMethodID"));
			}
			
		}
		finally {
			if(conn != null) try {conn.close();} catch(SQLException e){}
			if(stmt != null) try {stmt.close();} catch(SQLException e){}
			if(rs != null) try {rs.close();} catch(SQLException e){}
		}
		
		List<PaymentMethod> paymentMethodList = new ArrayList<PaymentMethod>(paymentMethodIds.size());
		
		PaymentMethodDAO pmdao = PaymentMethodDAO.getInstance();
		for(Integer paymentMethodId: paymentMethodIds) {
			
			PaymentMethod pm = pmdao.getPaymentMethod(paymentMethodId);
			if(pm != null) {
				paymentMethodList.add(pm);
			}
		}
		
		return paymentMethodList;
	}
	
	public List<PaymentMethod> getCurrentPaymentMethods(int projectId) throws SQLException {
		
		List<Integer> paymentMethodIds = new ArrayList<Integer>();
		String sql = "SELECT paymentMethodID FROM projectPaymentMethod WHERE projectID="+projectId;
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		
		try {
			conn = getConnection();
			
			stmt = conn.createStatement();
			rs = stmt.executeQuery(sql);
			while(rs.next()) {
				paymentMethodIds.add(rs.getInt("paymentMethodID"));
			}
			
		}
		finally {
			if(conn != null) try {conn.close();} catch(SQLException e){}
			if(stmt != null) try {stmt.close();} catch(SQLException e){}
			if(rs != null) try {rs.close();} catch(SQLException e){}
		}
		
		List<PaymentMethod> paymentMethodList = new ArrayList<PaymentMethod>(paymentMethodIds.size());
		
		PaymentMethodDAO pmdao = PaymentMethodDAO.getInstance();
		for(Integer paymentMethodId: paymentMethodIds) {
			
			PaymentMethod pm = pmdao.getPaymentMethod(paymentMethodId);
			if(pm != null && pm.isCurrent()) {
				paymentMethodList.add(pm);
			}
		}
		
		return paymentMethodList;
	}

	private Connection getConnection() throws SQLException {
		return DBConnectionManager.getConnection(DBConnectionManager.MAIN_DB);
	}
	
	public void savePaymentMethod(int projectId, PaymentMethod paymentMethod) throws SQLException {
		
		// first save the payment method
		int paymentMethodId = PaymentMethodDAO.getInstance().savePaymentMethod(paymentMethod);
		
		// now create an entry in the bridge table
		String sql = "INSERT INTO projectPaymentMethod (projectID, paymentMethodID) VALUES (?,?)";
		
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		try {
			conn = getConnection();
			
			stmt = conn.prepareStatement(sql);
			stmt.setInt(1, projectId);
			stmt.setInt(2, paymentMethodId);
			
			int numRowsInserted = stmt.executeUpdate();
			if(numRowsInserted == 0) {
				throw new SQLException("Creating project payment method failed, no rows affected.");
			}
		}
		finally {
			if(conn != null) try {conn.close();} catch(SQLException e){}
			if(stmt != null) try {stmt.close();} catch(SQLException e){}
			if(rs != null) try {rs.close();} catch(SQLException e){}
		}
	}
	
	public void deletePaymentMethod(int paymentMethodId) throws SQLException {
		
		// first delete the payment method
		PaymentMethodDAO.getInstance().deletePaymentMethod(paymentMethodId);
		
		// now delete the entry in the bridge table
		// NOTE: there is a trigger on paymentMethod that will 
		//       delete all entries in projectPaymentMethod that have this paymentMethodId
		// unlinkProjectPaymentMethod(paymentMethodId, 0);
		
	}

	public void unlinkProjectPaymentMethod(int paymentMethodId, int projectId) throws SQLException {
		
		if(paymentMethodId == 0 && projectId == 0) {
			log.error("paymentMethodId and projectId are both 0 in unlinkProjectPaymentMethod. Skipping...");
			return;
		}
		
		String sql = "DELETE FROM projectPaymentMethod WHERE ";
		if(paymentMethodId != 0) {
			sql += "paymentMethodID="+paymentMethodId;
		}
		if(paymentMethodId != 0 && projectId != 0)
			sql += " AND ";
		if(projectId != 0) {
			sql += " projectID="+projectId;
		}
		
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		
		try {
			conn = getConnection();
			stmt = conn.createStatement();
			int numRowsDeleted = stmt.executeUpdate(sql);
			
			if(numRowsDeleted == 0) {
				throw new SQLException("Deleting project payment method failed, no rows affected.");
			}
			
		}
		finally {
			if(conn != null) try {conn.close();} catch(SQLException e){}
			if(stmt != null) try {stmt.close();} catch(SQLException e){}
			if(rs != null) try {rs.close();} catch(SQLException e){}
		}
	}
}
