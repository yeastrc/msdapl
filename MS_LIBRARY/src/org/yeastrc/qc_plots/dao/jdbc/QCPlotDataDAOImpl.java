package org.yeastrc.qc_plots.dao.jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.log4j.Logger;
import org.yeastrc.ms.dao.DAOFactory;
import org.yeastrc.qc_plots.dao.QCPlotDataDAO;
import org.yeastrc.qc_plots.dto.QCPlotDataDTO;



public class QCPlotDataDAOImpl implements QCPlotDataDAO {

    private static final Logger log = Logger.getLogger(QCPlotDataDAOImpl.class);

	
	private static final String insertSQL 
	= "INSERT INTO qc_plot_data "
	+ 		"(experiment_id, plot_type, plot_data, scan_count, create_time_in_seconds, data_version ) "
	+ 		"VALUES ( ?, ?, ?, ?, ?, ? )"
	
    + " ON DUPLICATE KEY UPDATE "
    + "     plot_data = ?, scan_count = ?, create_time_in_seconds = ?, data_version = ?";

	
    @Override
    public int saveOrUpdate(QCPlotDataDTO qcPlotDataDTO) {
    	

//    	CREATE TABLE mz_scan_count_plot_data (
//		  experiment_id int(10) unsigned NOT NULL,
//		  plot_data varchar(4000) NOT NULL,
//		  scan_count int(10) unsigned NOT NULL,
//		  create_time_in_seconds int(10) unsigned NOT NULL,
//		  data_version int(10) unsigned NOT NULL,
//		  PRIMARY KEY (experiment_id)
//		) ENGINE=MyISAM DEFAULT CHARSET=latin1;

		Connection connection = null;

		PreparedStatement pstmt = null;

		try {
			
			connection = DAOFactory.instance().getConnection();

			pstmt = connection.prepareStatement( insertSQL );
			
			int paramCounter = 0;
			
			//  For insert portion of statement
			paramCounter++;
			pstmt.setInt( paramCounter, qcPlotDataDTO.getExperimentId() );
			paramCounter++;
			pstmt.setString( paramCounter, qcPlotDataDTO.getPlotType() );
			paramCounter++;
			pstmt.setString( paramCounter, qcPlotDataDTO.getPlotData() );
			paramCounter++;
			pstmt.setInt( paramCounter, qcPlotDataDTO.getScanCount() );
			paramCounter++;
			pstmt.setInt( paramCounter, qcPlotDataDTO.getCreateTimeInSeconds() );
			paramCounter++;
			pstmt.setInt( paramCounter, qcPlotDataDTO.getDataVersion() );
			
			//  For update portion of statement
			paramCounter++;
			pstmt.setString( paramCounter, qcPlotDataDTO.getPlotData() );
			paramCounter++;
			pstmt.setInt( paramCounter, qcPlotDataDTO.getScanCount() );
			paramCounter++;
			pstmt.setInt( paramCounter, qcPlotDataDTO.getCreateTimeInSeconds() );
			paramCounter++;
			pstmt.setInt( paramCounter, qcPlotDataDTO.getDataVersion() );
			
			int rowsUpdated = pstmt.executeUpdate();

			if ( rowsUpdated == 0 ) {
				
			}

		} catch (Exception sqlEx) {
			String msg = "save :Exception '" + sqlEx.toString() + ".\nSQL = " + insertSQL ;
			log.error( msg, sqlEx);
			throw new RuntimeException( msg, sqlEx );

		} finally {

			if (pstmt != null) {
				try {
					pstmt.close();
				} catch (SQLException ex) {
					// ignore
				}
			}

			if (connection != null) {
				try {
					connection.close();
				} catch (SQLException ex) {
					// ignore
				}
			}
		}
		return qcPlotDataDTO.getExperimentId() ;
    }
    
	private String loadFromExperimentIdPlotType
	= "SELECT plot_data, data_version FROM qc_plot_data WHERE experiment_id = ? AND plot_type = ?";

    
    @Override
    public QCPlotDataDTO load( int experimentId, String plotType ) {

    	final String querySqlStringComplete = loadFromExperimentIdPlotType;

    	QCPlotDataDTO qcPlotDataDTO = null;

    	Connection connection = null;

    	PreparedStatement pstmt = null;

    	ResultSet rs = null;

    	try {

    		connection = DAOFactory.instance().getConnection();

    		pstmt = connection.prepareStatement( querySqlStringComplete );

    		pstmt.setInt( 1, experimentId );

    		rs = pstmt.executeQuery();

    		if ( rs.next() ) {

    			qcPlotDataDTO = new QCPlotDataDTO();

    			qcPlotDataDTO.setExperimentId( experimentId );
    			qcPlotDataDTO.setPlotType( plotType );

    			qcPlotDataDTO.setPlotData( rs.getString( "plot_data" ) );
    			qcPlotDataDTO.setDataVersion( rs.getInt( "data_version" ) );
    		}


    	} catch (Exception sqlEx) {

    		String msg = "load :Exception '" + sqlEx.toString() + '.';
    		log.error( msg, sqlEx);
    		throw new RuntimeException( msg, sqlEx );

    	} finally {

    		if (rs != null) {
    			try {
    				rs.close();
    			} catch (SQLException ex) {
    				// ignore
    			}
    		}

    		if (pstmt != null) {
    			try {
    				pstmt.close();
    			} catch (SQLException ex) {
    				// ignore
    			}
    		}

    		if (connection != null) {
    			try {
    				connection.close();
    			} catch (SQLException ex) {
    				// ignore
    			}
    		}
    	}

    	return qcPlotDataDTO;

    }

    
    
	private String loadFromExperimentIdPlotTypeAndDataVersionSqlString 
	= "SELECT plot_data FROM qc_plot_data WHERE experiment_id = ? AND plot_type = ? AND data_version = ?";
    
    @Override
    public QCPlotDataDTO loadFromExperimentIdPlotTypeAndDataVersion(int experimentId, String plotType, int dataVersion) {

    	final String querySqlStringComplete = loadFromExperimentIdPlotTypeAndDataVersionSqlString;

    	QCPlotDataDTO qcPlotDataDTO = null;

    	Connection connection = null;

    	PreparedStatement pstmt = null;

    	ResultSet rs = null;

    	try {

    		connection = DAOFactory.instance().getConnection();

    		pstmt = connection.prepareStatement( querySqlStringComplete );

    		pstmt.setInt( 1, experimentId );
    		pstmt.setString( 2, plotType );
    		pstmt.setInt( 3, dataVersion );

    		rs = pstmt.executeQuery();

    		if ( rs.next() ) {

    			qcPlotDataDTO = new QCPlotDataDTO();

    			qcPlotDataDTO.setExperimentId( experimentId );

    			qcPlotDataDTO.setPlotData( rs.getString( "plot_data" ) );
    			qcPlotDataDTO.setDataVersion( dataVersion );
    		}


    	} catch (Exception sqlEx) {

    		String msg = "loadFromExperimentIdPlotTypeAndDataVersion :Exception '" + sqlEx.toString() + '.';
    		log.error( msg, sqlEx);
    		throw new RuntimeException( msg, sqlEx );

    	} finally {

    		if (rs != null) {
    			try {
    				rs.close();
    			} catch (SQLException ex) {
    				// ignore
    			}
    		}

    		if (pstmt != null) {
    			try {
    				pstmt.close();
    			} catch (SQLException ex) {
    				// ignore
    			}
    		}

    		if (connection != null) {
    			try {
    				connection.close();
    			} catch (SQLException ex) {
    				// ignore
    			}
    		}
    	}

    	return qcPlotDataDTO;
    }

    
    
	private String deleteFromExperimentIdPlotTypeSqlString 
	= "DELETE FROM qc_plot_data WHERE experiment_id = ? AND plot_type = ?";
    


	@Override
	public void deleteForExperimentIdPlotType(	int experimentId, String plotType ) {


    	final String querySqlStringComplete = deleteFromExperimentIdPlotTypeSqlString;

    	Connection connection = null;

    	PreparedStatement pstmt = null;


    	try {

    		connection = DAOFactory.instance().getConnection();

    		pstmt = connection.prepareStatement( querySqlStringComplete );

    		pstmt.setInt( 1, experimentId );
    		pstmt.setString( 2, plotType );

    		pstmt.executeUpdate();



    	} catch (Exception sqlEx) {

    		String msg = "deleteForExperimentIdPlotType :Exception '" + sqlEx.toString() + '.';
    		log.error( msg, sqlEx);
    		throw new RuntimeException( msg, sqlEx );

    	} finally {

    		if (pstmt != null) {
    			try {
    				pstmt.close();
    			} catch (SQLException ex) {
    				// ignore
    			}
    		}

    		if (connection != null) {
    			try {
    				connection.close();
    			} catch (SQLException ex) {
    				// ignore
    			}
    		}
    	}

		
	}


}
