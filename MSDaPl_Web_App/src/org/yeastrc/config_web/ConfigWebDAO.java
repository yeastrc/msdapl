package org.yeastrc.config_web;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.log4j.Logger;
import org.yeastrc.db.DBConnectionManager;


/**
 * retrieve configuration values from the table config_system
 *
 */
public class ConfigWebDAO {
	
	
	private static final Logger log = Logger.getLogger(ConfigWebDAO.class);
	
	//  private constructor
	private ConfigWebDAO() {}
	
	private static final ConfigWebDAO instance = new ConfigWebDAO();
	
	
	public static ConfigWebDAO getInstance() {
		return instance;
	}

	
	/**
	 * retrieve configuration value as String from the table config_system for the configKey
	 * 
	 * @param configKey
	 * @return
	 * @throws SQLException
	 */
	public String getStringValueForKey( String configKey ) throws SQLException {
				
		final String sql = "SELECT config_value FROM config_msdapl_webapp WHERE `config_key` = ?";
	  
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		String configValue = null;
	
		try {
			conn = getConnection();
			pstmt = conn.prepareStatement(sql);
			pstmt.setString( 1, configKey );
			
			rs = pstmt.executeQuery();
			
			if(rs.next()) {
				
				configValue = rs.getString( "config_value" );
			}
		}
		catch( SQLException ex ) {
	
			log.error("ConfigWebDAO: Error retrieving config value for config_key: '" + configKey 
					+ "', SQL: " + sql, ex );
			throw ex;
		}
		finally {
			if(rs != null) try {rs.close();} catch(SQLException e){}
			if(pstmt != null) try {pstmt.close();} catch(SQLException e){}
			if(conn != null) try {conn.close();} catch(SQLException e){}
		}
		
		return configValue;
	}
	
	

	private Connection getConnection() throws SQLException {
		return DBConnectionManager.getConnection(DBConnectionManager.MAIN_DB);
	}
	
}
