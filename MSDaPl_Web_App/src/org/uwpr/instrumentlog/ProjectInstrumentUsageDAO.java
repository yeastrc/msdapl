/**
 * 
 */
package org.uwpr.instrumentlog;

import java.sql.Connection;
import java.util.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.uwpr.costcenter.InstrumentRate;
import org.uwpr.costcenter.InstrumentRateDAO;
import org.yeastrc.db.DBConnectionManager;

/**
 * ProjectInstrumentUsageDAO.java
 * @author Vagisha Sharma
 * May 31, 2011
 * 
 */
public class ProjectInstrumentUsageDAO {

	private static final ProjectInstrumentUsageDAO instance = new ProjectInstrumentUsageDAO();
	
	private static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss");
	
	private ProjectInstrumentUsageDAO () {}
	
	public static ProjectInstrumentUsageDAO getInstance() {
		return instance;
	}
	
	public int getUsageBlockCountForProject(int projectId) throws SQLException {
		
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		
		String sql = "SELECT COUNT(*) FROM instrumentUsage WHERE projectID="+projectId;
       
        //System.out.println(sql);
        
        try {
        	conn = getConnection();
            stmt = conn.createStatement();
            rs = stmt.executeQuery(sql);
            if(rs.next())
            	return rs.getInt(1);
            else
            	return 0;
        } 
        
        finally {
            // Always make sure result sets and statements are closed,
        	if(conn != null) try {conn.close();} catch(SQLException e){}
			if(stmt != null) try {stmt.close();} catch(SQLException e){}
			if(rs != null) try {rs.close();} catch(SQLException e){}
        }
	}

	private Connection getConnection() throws SQLException {
		return DBConnectionManager.getConnection(DBConnectionManager.MAIN_DB);
	}

	public List<UsageBlock> getUsageBlocksForProject(int projectId) throws SQLException {
		
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		
		StringBuilder buf = new StringBuilder();
        buf.append("SELECT insUsg.*"+
                ", ins.name"+
                ", proj.projectTitle, proj.projectPI"+
                ", r.researcherLastName"+
                ", invoice.createDate AS invoiceDate"
                 );
        buf.append(" FROM ( "+DBConnectionManager.MS_DATA+".msInstrument AS ins, instrumentUsage AS insUsg, tblProjects AS proj, tblResearchers AS r )");
        buf.append(" LEFT JOIN ( invoice, invoiceInstrumentUsage as invBlk )");
        buf.append(" ON ( invBlk.instrumentUsageID = insUsg.id AND invoice.id=invBlk.invoiceID )");
        buf.append(" WHERE proj.projectID=insUsg.projectID ");
        buf.append(" AND r.researcherID=proj.projectPI ");
        buf.append(" AND ins.id=insUsg.instrumentID ");
        buf.append(" AND insUsg.projectID="+projectId);
       
        String sql = buf.toString();
        //System.out.println(sql);
        
        List <UsageBlock> usageBlks = new ArrayList<UsageBlock>();
        
        InstrumentUsagePaymentDAO iupDao = InstrumentUsagePaymentDAO.getInstance();
        try {
        	conn = getConnection();
            stmt = conn.createStatement();
            rs = stmt.executeQuery(sql);
            while (rs.next()) {
                
                UsageBlock newBlk = makeUsageBlock(rs);
                usageBlks.add(newBlk);
                
                List<InstrumentUsagePayment> payments = iupDao.getPaymentsForUsage(newBlk.getID());
                newBlk.setPayments(payments);
                
                InstrumentRateDAO rateDao = InstrumentRateDAO.getInstance();
                InstrumentRate rate = rateDao.getInstrumentRate(newBlk.getInstrumentRateID());
                if(rate == null)
                	throw new SQLException("No instrument rate found for ID: "+newBlk.getInstrumentRateID());
                newBlk.setRate(rate.getRate());
            }
            return usageBlks;
        } 
        
        finally {
            // Always make sure result sets and statements are closed,
        	if(conn != null) try {conn.close();} catch(SQLException e){}
			if(stmt != null) try {stmt.close();} catch(SQLException e){}
			if(rs != null) try {rs.close();} catch(SQLException e){}
        }
	}
	
	/**
	 * Returns a list of usage blocks for the given project that have their start and end dates
	 * within the given start and end dates. 
	 * If startDate or endDate are null they are ignored.
	 * e.g. if startDate is null, all blocks that have end dates <= endDate are returned.
	 * @param projectId
	 * @param startDate
	 * @param endDate
	 * @return
	 * @throws SQLException
	 */
	public List<UsageBlockBase> getUsageBlocksForProjectInRange(int projectId, Date startDate, Date endDate) throws SQLException {
		
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		
		StringBuilder buf = new StringBuilder();
        buf.append("SELECT * FROM instrumentUsage");
        buf.append(" WHERE projectID="+projectId);
        if(startDate != null)
        	buf.append(" AND startDate >= '"+(dateFormat.format(startDate))+"'");
        if(endDate != null)
            buf.append(" AND endDate <= '"+(dateFormat.format(endDate))+"'");
        String sql = buf.toString();
        //System.out.println(sql);
        
        List <UsageBlockBase> usageBlks = new ArrayList<UsageBlockBase>();
        
        try {
        	conn = getConnection();
            stmt = conn.createStatement();
            rs = stmt.executeQuery(sql);
            while (rs.next()) {
                
                UsageBlockBase newBlk = makeUsageBlockBase(rs);
                usageBlks.add(newBlk);
                
            }
            return usageBlks;
            
        } 
        
        finally {
            // Always make sure result sets and statements are closed,
        	if(conn != null) try {conn.close();} catch(SQLException e){}
			if(stmt != null) try {stmt.close();} catch(SQLException e){}
			if(rs != null) try {rs.close();} catch(SQLException e){}
        }
	}
	
	/**
	 * Returns a list of usage blocks for the given project that have either their start OR end dates
	 * within the given start and end dates. 
	 * @param projectId
	 * @param startDate
	 * @param endDate
	 * @return
	 * @throws SQLException
	 */
	public List<UsageBlockBase> getUsageBlocksForProject(int projectId, Date startDate, Date endDate) throws SQLException {
		
		if(startDate == null) {
			throw new SQLException("startDate cannot be null");
		}
		if(endDate == null) {
			throw new SQLException("endDate cannot be null");
		}
		
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		
		StringBuilder buf = new StringBuilder();
        buf.append("SELECT * FROM instrumentUsage");
        buf.append(" WHERE projectID="+projectId);
        buf.append(" AND startDate < '"+(dateFormat.format(endDate))+"'");
        buf.append(" AND endDate > '"+(dateFormat.format(startDate))+"'");
        String sql = buf.toString();
        //System.out.println(sql);
        
        List <UsageBlockBase> usageBlks = new ArrayList<UsageBlockBase>();
        
        try {
        	conn = getConnection();
            stmt = conn.createStatement();
            rs = stmt.executeQuery(sql);
            while (rs.next()) {
                
                UsageBlockBase newBlk = makeUsageBlockBase(rs);
                usageBlks.add(newBlk);
                
            }
            return usageBlks;
            
        } 
        
        finally {
            // Always make sure result sets and statements are closed,
        	if(conn != null) try {conn.close();} catch(SQLException e){}
			if(stmt != null) try {stmt.close();} catch(SQLException e){}
			if(rs != null) try {rs.close();} catch(SQLException e){}
        }
	}
	
	private UsageBlockBase makeUsageBlockBase(ResultSet rs) throws SQLException {
        UsageBlockBase blk = new UsageBlockBase();
        blk.setID(rs.getInt("id"));
        blk.setResearcherID(rs.getInt("enteredBy"));
        Integer updaterResearcherId = rs.getInt("updatedBy");
        if(updaterResearcherId != null) {
        	blk.setUpdaterResearcherID(updaterResearcherId);
        }
        blk.setInstrumentID(rs.getInt("instrumentID"));
        blk.setInstrumentRateID(rs.getInt("instrumentRateID"));
        blk.setProjectID(rs.getInt("projectID"));
        blk.setStartDate(rs.getTimestamp("startDate"));
        blk.setEndDate(rs.getTimestamp("endDate"));
        blk.setDateCreated(rs.getTimestamp("dateEntered"));
        blk.setDateChanged(rs.getTimestamp("lastChanged"));
        blk.setNotes(rs.getString("notes"));
        return blk;
    }
	
	private UsageBlock makeUsageBlock(ResultSet rs) throws SQLException {
        UsageBlock blk = new UsageBlock();
        blk.setID(rs.getInt("id"));
        blk.setResearcherID(rs.getInt("enteredBy"));
        Integer updaterResearcherId = rs.getInt("updatedBy");
        if(updaterResearcherId != null) {
        	blk.setUpdaterResearcherID(updaterResearcherId);
        }
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
        blk.setInvoiceDate(rs.getTimestamp("invoiceDate"));
        return blk;
    }
}
