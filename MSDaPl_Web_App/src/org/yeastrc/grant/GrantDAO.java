package org.yeastrc.grant;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.yeastrc.data.InvalidIDException;
import org.yeastrc.db.DBConnectionManager;
import org.yeastrc.project.Project;
import org.yeastrc.project.Researcher;
import org.yeastrc.www.user.User;

public class GrantDAO {

	private static GrantDAO instance = new GrantDAO();
	
	private GrantDAO(){}

	public static GrantDAO getInstance() {
		return instance;
	}
	
	public void save(Grant grant) throws Exception {

		if (grant == null)
			return;

		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;

		try {

			boolean newGrant = false;
			if (grant.getID() == 0) newGrant = true;

			String sql = "SELECT * FROM grants WHERE id = " + grant.getID();

			conn = DBConnectionManager.getConnection(DBConnectionManager.MAIN_DB);
			stmt = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
			rs = stmt.executeQuery( sql );

			if (!newGrant) {
				if (!rs.next())
					throw new Exception( "Grant had id, but was not in database.  Aborting save." );
			} else {
				rs.moveToInsertRow();
			}

			rs.updateString("title", grant.getTitle());
			rs.updateInt("PI", grant.getGrantPI().getID());
			FundingSourceType sourceType = grant.getFundingSource().getSourceType();
			rs.updateString("sourceType", sourceType.getName());
			FundingSourceName sourceName = grant.getFundingSource().getSourceName();
			rs.updateString("sourceName", sourceName.getName());
			rs.updateString("grantNum", grant.getGrantNumber());
			rs.updateString("grantAmount", grant.getGrantAmount());

			if (!newGrant) {
				rs.updateRow();
			} else {
				rs.insertRow();
				rs.last();
				grant.setID( rs.getInt("id") );
			}

			rs.close(); rs = null;
			stmt.close(); stmt = null;
			conn.close(); conn = null;

		} finally {

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
			if (conn != null) {
				try { conn.close(); } catch (SQLException e) { ; }
				conn = null;
			}
		}
	}

	public Grant load(int grantID) throws SQLException, InvalidIDException {
		
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		try {

			String sql = "SELECT * FROM grants WHERE id="+grantID;

			conn = DBConnectionManager.getConnection(DBConnectionManager.MAIN_DB);
			stmt = conn.prepareStatement(sql);
			rs = stmt.executeQuery();
			
			if (rs.next()) {
				Grant grant = new Grant();
				grant.setID(rs.getInt("id"));
				grant.setGrantPI(getPI(rs.getInt("PI")));
				grant.setFundingSource(FundingSource.getFundingSource(rs.getString("sourceType"), rs.getString("sourceName")));
				grant.setGrantNumber(rs.getString("grantNum"));
				grant.setGrantAmount(rs.getString("grantAmount"));
				grant.setTitle(rs.getString("title"));
				return grant;
			}

		} finally {

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
			if (conn != null) {
				try { conn.close(); } catch (SQLException e) { ; }
				conn = null;
			}
		}
		return null;
	}
	
	public List<Grant> getGrantsForProject(int projectID) throws SQLException, InvalidIDException {

		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;

		try {

			String sql = "SELECT grants.id, grants.PI, grants.title, grants.sourceType, grants.sourceName, grants.grantNum, grants.grantAmount, projectGrant.projectID "+
			"FROM grants, projectGrant "+
			"WHERE projectGrant.projectID="+projectID+" "+
			"AND projectGrant.grantID=grants.id "+
			"ORDER BY grants.id";

//			System.out.println(sql);
			conn = DBConnectionManager.getConnection(DBConnectionManager.MAIN_DB);
			stmt = conn.prepareStatement(sql);
			rs = stmt.executeQuery();

			List<Grant> grants = new ArrayList<Grant>();
			while (rs.next()) {
				Grant grant = new Grant();
				grant.setID(rs.getInt("id"));
				grant.setGrantPI(getPI(rs.getInt("PI")));
				grant.setFundingSource(FundingSource.getFundingSource(rs.getString("sourceType"), rs.getString("sourceName")));
				grant.setGrantNumber(rs.getString("grantNum"));
				grant.setGrantAmount(rs.getString("grantAmount"));
				grant.setTitle(rs.getString("title"));
				grants.add(grant);
			}
			return grants;

		} finally {

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
			if (conn != null) {
				try { conn.close(); } catch (SQLException e) { ; }
				conn = null;
			}
		}
	}

	public List<Grant> getGrantForUserAndPIs(User user, List<Integer> piIDs) throws SQLException, InvalidIDException {
		
		// get a list of all the projects for this user
		List<Project> projects = user.getProjects();
		StringBuilder projIDStr = new StringBuilder();
		for (Project proj: projects) {
			projIDStr.append(","+proj.getID());
		}
		if (projIDStr.length() > 0)
			projIDStr.deleteCharAt(0); // remove the first comma
		
		Researcher thisResearcher = user.getResearcher();
		StringBuilder piIDStr = new StringBuilder();
		piIDStr.append(thisResearcher.getID());
		for(Integer piID: piIDs) {
			piIDStr.append(",");
			piIDStr.append(piID);
		}
		
		
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		try {

			StringBuilder sql = new StringBuilder("SELECT grants.id, grants.PI, grants.title, grants.sourceType, grants.sourceName, grants.grantNum, grants.grantAmount, projectGrant.projectID ");
			sql.append("FROM grants ");
			sql.append("LEFT JOIN projectGrant ON grants.id=projectGrant.grantID ");
			sql.append("WHERE grants.PI in ("+piIDStr.toString()+") ");
			if (projIDStr.length() > 0)
				sql.append("OR projectGrant.projectID in ("+projIDStr.toString()+") ");
					
			sql.append("GROUP BY grants.id ");
			sql.append("ORDER BY grants.id");

			System.out.println(sql);
			conn = DBConnectionManager.getConnection(DBConnectionManager.MAIN_DB);
			stmt = conn.prepareStatement(sql.toString());
			rs = stmt.executeQuery();
			
			List<Grant> grants = new ArrayList<Grant>();
			while (rs.next()) {
				Grant grant = new Grant();
				grant.setID(rs.getInt("id"));
				grant.setGrantPI(getPI(rs.getInt("PI")));
				grant.setFundingSource(FundingSource.getFundingSource(rs.getString("sourceType"), rs.getString("sourceName")));
				grant.setGrantNumber(rs.getString("grantNum"));
				grant.setGrantAmount(rs.getString("grantAmount"));
				grant.setTitle(rs.getString("title"));
				grants.add(grant);
			}
			return grants;

		} finally {

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
			if (conn != null) {
				try { conn.close(); } catch (SQLException e) { ; }
				conn = null;
			}
		}
	}

	private Researcher getPI(int researcherID) throws InvalidIDException, SQLException {
		Researcher PI = new Researcher();
		PI.load(researcherID);
		return PI;
	}
}
