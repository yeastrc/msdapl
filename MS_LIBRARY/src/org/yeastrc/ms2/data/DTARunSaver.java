/**
 * 
 */
package org.yeastrc.ms2.data;

import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;

/**
 * @author Mike
 *
 */
public class DTARunSaver {

	private DTARunSaver() { }
	
	/**
	 * get an instance of this class
	 * @return
	 */
	public static DTARunSaver getInstance() {
		return new DTARunSaver();
	}
	
	/**
	 * Save the supplied DTARun.  Do not use this to save runs already in the database
	 * @param run
	 * @return the run ID created
	 * @throws Exception
	 */
	public int save( DTARun run, Connection conn ) throws Exception {
		
		int runID = 0;
		Statement stmt = null;
		ResultSet rs = null;
		
		if (run.getId() != 0)
			throw new Exception( "Run is already in the database." );
		
		
		try {

			stmt = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
			
			String sql = "SELECT * FROM tblYatesRun WHERE id = " + runID;
			rs = stmt.executeQuery( sql );
			
			rs.moveToInsertRow();
			
			if (run.getProjectID() == 0)
				rs.updateNull( "projectID" );
			else
				rs.updateInt( "projectID", run.getProjectID() );
			
			if (run.getBaitDesc() == null)
				rs.updateNull( "baitDesc" );
			else
				rs.updateString( "baitDesc", run.getBaitDesc() );
			
			if (run.getBaitProtein() == 0)
				rs.updateNull( "baitProteinID" );
			else
				rs.updateInt( "baitProteinID", run.getBaitProtein() );

			if (run.getTargetSpecies() == 0)
				rs.updateNull("targetSpecies");
			else
				rs.updateInt("targetSpecies", run.getTargetSpecies() );
			
			if (run.getRunDate() == null) { rs.updateNull("runDate"); }
			else { rs.updateDate("runDate", new java.sql.Date(run.getRunDate().getTime())); }
			
			if (run.getDirectoryName() == null) { rs.updateNull("directoryName"); }
			else { rs.updateString("directoryName", run.getDirectoryName() ); }
			
			if (run.hasDTASelectTXT()) {
				rs.updateBytes( "DTASelectTXT", run.getDTASelectTXTData() );
			}
			
			if (run.getDTASelectFilterTXT() == null) { rs.updateNull("DTASelectFilterTXT"); }
			else { rs.updateString("DTASelectFilterTXT", run.getDTASelectFilterTXT()); }
			
			if (run.getDTASelectHTML() == null) { rs.updateNull("DTASelectHTML"); }
			else { rs.updateString("DTASelectHTML", run.getDTASelectHTML()); }
			
			if (run.getDTASelectParams() == null) { rs.updateNull("DTASelectParams"); }
			else { rs.updateString("DTASelectParams", run.getDTASelectParams()); }
			
			if (run.getComments() == null) { rs.updateNull("comments"); }
			else { rs.updateString("comments", run.getComments()); }
			
			if (run.getDatabaseName() == null) { rs.updateNull("databaseName"); }
			else { rs.updateString("databaseName", run.getDatabaseName()); }

			rs.insertRow();
			rs.last();
			
			run.setId( rs.getInt( "id" ) );
			runID = run.getId();
			
			rs.close(); rs = null;
			stmt.close(); stmt = null;
			conn.close(); conn = null;
			
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
		
		
		return runID;
	}
	
	
	
}
