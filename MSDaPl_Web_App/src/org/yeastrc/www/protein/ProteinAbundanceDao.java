package org.yeastrc.www.protein;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.yeastrc.db.DBConnectionManager;

/**
 * ProteinAbundanceDao.java
 * @author Vagisha Sharma
 * May 9, 2010
 * @version 1.0
 */

/**
 * 
 */
public class ProteinAbundanceDao {

	private static ProteinAbundanceDao instance = null;
	
	private ProteinAbundanceDao() {}
	
	public static synchronized ProteinAbundanceDao getInstance() {
		if(instance == null)
			instance = new ProteinAbundanceDao();
		return instance;
	}
	
	public List<YeastOrfAbundance> getAbundance(int nrseqProteinId) throws SQLException {
		
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		
		try {
			
			String sql = "SELECT orfName, abundance from yeastProteinAbundance.yeastProteinAbundance WHERE proteinID = "+nrseqProteinId;
			conn = DBConnectionManager.getConnection(DBConnectionManager.MAIN_DB);
			stmt = conn.createStatement();
			rs = stmt.executeQuery(sql);
			
			List<YeastOrfAbundance> orfs = new ArrayList<YeastOrfAbundance>();
			while(rs.next()) {
				YeastOrfAbundance oa = new YeastOrfAbundance();
				if(rs.getObject("abundance") != null) 
					oa.setAbundance(rs.getDouble("abundance"));
				oa.setOrfName(rs.getString("orfName"));
				orfs.add(oa);
			}
			return orfs;
			
		} finally {

			if (rs != null) {
				try { rs.close(); rs = null; } catch (Exception e) { ; }
			}
			
			if (stmt != null) {
				try { stmt.close(); stmt = null; } catch (Exception e) { ; }
			}
			
			if (conn != null) {
				try { conn.close(); conn = null; } catch (Exception e) { ; }
			}			
		}
	}
	
	public static final class YeastOrfAbundance {
		
		private String orfName;
		private Double abundance;
		public String getOrfName() {
			return orfName;
		}
		public void setOrfName(String orfName) {
			this.orfName = orfName;
		}
		public Double getAbundance() {
			return abundance;
		}
		public boolean isAbundanceNull() {
			return abundance == null;
		}
		public String getAbundanceString() {
			if(abundance == null)
				return "NOT DETECTED";
			
			double ab = Math.round(abundance.doubleValue());
			if(ab == 0.0) // band detected but unquantifiable due to experimental problems
				return "EXPT. ERROR";
			else if(ab == -1.0) // band detected but extremely low signal (< 50 copies / cell)
				return "< 50"; 
				
			if(abundance == Math.round(abundance.doubleValue())) {
				return String.valueOf((int)abundance.doubleValue());
			}
			else
				return String.valueOf(abundance);
		}
		public String getAbundanceAndOrfNameString() {
			return orfName+":"+getAbundanceString();
		}
		public String getAbundanceToPrint() {
			if(abundance == null)
				return "NOT_DETECTED";
			
			double ab = Math.round(abundance.doubleValue());
			if(ab == 0.0) // band detected but unquantifiable due to experimental problems
				return "EXPT_ERROR";
			else if(ab == -1.0) // band detected but extremely low signal (< 50 copies / cell)
				return "LOW"; 
				
			if(abundance == Math.round(abundance.doubleValue())) {
				return String.valueOf((int)abundance.doubleValue());
			}
			else
				return String.valueOf(abundance);
		}
		public void setAbundance(Double abundance) {
			this.abundance = abundance;
		}
	}
}
