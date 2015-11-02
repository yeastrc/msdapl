package org.yeastrc.qc_plots.plot_peaks_per_scan_per_experiment.service;


import org.apache.log4j.Logger;
import org.yeastrc.ms.dao.DAOFactory;
import org.yeastrc.qc_plots.constants.QC_Plots_Constants;
import org.yeastrc.qc_plots.dao.QCPlotDataDAO;
import org.yeastrc.qc_plots.dao.jdbc.QCPlotDataDAOImpl;
import org.yeastrc.qc_plots.dto.QCPlotDataDTO;


/**
 * Creates the JSON to define the Peaks per scan chart
 * 
 * The computed JSON is stored using QCPlotDataDAO
 * and retrieve uses that for the next request for the same
 * experiment id and if the data_version matches CURRENT_DATA_VERSION
 *
 */
public class QC_Plot_PeaksPerScanPerExperiment_Plotter {


	private static final Logger log = Logger.getLogger(QC_Plot_PeaksPerScanPerExperiment_Plotter.class);

    private static DAOFactory daoFactory = DAOFactory.instance();

	private static final int BIN_COUNT = 50;  //  Number of bars on the chart


	 //  Increment this when change the JSON format so the existing data stored in the table will not be used
	private static final int CURRENT_DATA_VERSION = 1;
	


	
	/**
	 * @param experimentId
	 * @return
	 */
	public static String getPeaksPerScanPerExperimentPlot( int experimentId ) {

		String plotData = getStoredPeaksPerScanPerExperimentPlotFromDB( experimentId );
		
		if ( plotData != null ) {
			
			return plotData;
		}
		
		QCPlotDataDAO qcPlotDataDAO = new QCPlotDataDAOImpl();
		
		QCPlotDataDTO qcPlotDataDTO = generateQCPlotDataDTOFromDB( experimentId );
		
		qcPlotDataDTO.setDataVersion( CURRENT_DATA_VERSION );

		try {
			
			qcPlotDataDAO.saveOrUpdate( qcPlotDataDTO );
		
		} catch (Exception ex ) {
			
			log.error( "QC_Plot_PeaksPerScanPerExperiment_Plotter: getPeaksPerScanPerExperimentPlot(): Error saving stored plot data to DB", ex);
		}
		
		return qcPlotDataDTO.getPlotData();
	}
	

	/**
	 * @param experimentId
	 * @return
	 */
	public static String getStoredPeaksPerScanPerExperimentPlotFromDB( int experimentId ) {

		QCPlotDataDAO qcPlotDataDAO = new QCPlotDataDAOImpl();
		
		QCPlotDataDTO qcPlotDataDTO = null;
		
		try {
		
			qcPlotDataDTO = qcPlotDataDAO.loadFromExperimentIdPlotTypeAndDataVersion( experimentId, 
					QC_Plots_Constants.PLOT_TYPE__PEAKS_PER_SCAN_PER_EXPERIMENT, CURRENT_DATA_VERSION);
					
			if ( qcPlotDataDTO != null ) {

				return qcPlotDataDTO.getPlotData();
			}
		
		} catch (Exception ex ) {
			
			log.error( "QC_Plot_PeaksPerScanPerExperiment_Plotter: getStoredPeaksPerScanPerExperimentPlotFromDB(): Error getting stored plot data from DB", ex);
		}
		
		return null;
	}
	
	
	/**
	 * @param experimentId
	 * @return
	 */
	public static QCPlotDataDTO generateQCPlotDataDTOFromDB( int experimentId ) {
		
		long startTime = System.currentTimeMillis();
		
		int[] peakCountArray  = daoFactory.getMsScanDAO().getPeakCountArrayForExperimentIdScanLevelNotOne(experimentId);
		
		
//		Arrays.sort( peakCountArray );  //  only use for debugging
		
		int numScans = peakCountArray.length;

		int numPeaksMin = 0;
		int numPeaksMax =  0;
		
		boolean firstEntry = true;
		
		//  Find max and min values
		
		for ( int peakCount : peakCountArray ) {
			
			if ( firstEntry ) {
				
				firstEntry = false;
				
				numPeaksMin = peakCount;
				numPeaksMax = peakCount;
				
			} else {
				if ( peakCount < numPeaksMin ) {
					numPeaksMin = peakCount;
				}

				if ( peakCount > numPeaksMax  ) {
					numPeaksMax = peakCount;
				}
			}
		}
		
		int peakCountMaxMinusMin = numPeaksMax - numPeaksMin;

		
		double binSizeAsDouble = ( peakCountMaxMinusMin ) / BIN_COUNT;

		
		int[] scanCounts = new int[ BIN_COUNT ];
		
		for ( int peakCount : peakCountArray ) {
			
			int bin = (int) ( ( ( (double) ( peakCount - numPeaksMin ) )  / peakCountMaxMinusMin ) * BIN_COUNT );
			
			if ( bin < 0 ) {
				
				bin = 0;
			} else if ( bin >= BIN_COUNT ) {
				
				bin = BIN_COUNT - 1;
			} 
			
			scanCounts[ bin ]++;
		}
		
		
		
		//  Generate JSON   All labels/object member names must be in " (double quotes)
		
		StringBuilder plotDataSB = new StringBuilder( 10000 );
		
		plotDataSB.append("{\"experimentId\":");
		plotDataSB.append(experimentId);
		plotDataSB.append(",\"numScans\":");
		plotDataSB.append(numScans);
		plotDataSB.append(",\"numPeaksMax\":");
//		outputDataSB.append(precursorMZMax);
		plotDataSB.append(numPeaksMax);
		plotDataSB.append(",\"numPeaksMin\":");
//		outputDataSB.append(precursorMZMin);
		plotDataSB.append(numPeaksMin);
		
		
		//  change to output the general data that will be re-formatted in the Javascript
		//  to be what Google chart can use

		plotDataSB.append(",\"chartBuckets\":");		
		
		//  start of array
		plotDataSB.append("[");
		
		int counter = 0;
		for ( int scanCount : scanCounts ) {

			if ( counter > 0 ) {
				plotDataSB.append(",");
			}

			// start of next entry
			plotDataSB.append("{\"numPeaks\":");
			// start/left side of bin
			plotDataSB.append( ( ( counter * binSizeAsDouble ) + numPeaksMin ) );
			plotDataSB.append(",");
			// count
			plotDataSB.append("\"scanCount\":");
			plotDataSB.append(scanCount);
			//  end of entry
			plotDataSB.append("}");
			
			counter++;
		}
		
		//  end of array
		plotDataSB.append("]}");		
		

		String plotData = plotDataSB.toString();
		
		
		QCPlotDataDTO qcPlotDataDTO = new QCPlotDataDTO();
		
		qcPlotDataDTO.setExperimentId( experimentId );
		qcPlotDataDTO.setPlotType( QC_Plots_Constants.PLOT_TYPE__PEAKS_PER_SCAN_PER_EXPERIMENT );
		qcPlotDataDTO.setPlotData( plotData );
		qcPlotDataDTO.setScanCount( numScans );
		
		long endTime = System.currentTimeMillis();

		int createTimeInSeconds = Math.round( ( (float)( endTime - startTime ) ) / 1000 );
		
		qcPlotDataDTO.setCreateTimeInSeconds( createTimeInSeconds );
		
		return qcPlotDataDTO;
	}
}
