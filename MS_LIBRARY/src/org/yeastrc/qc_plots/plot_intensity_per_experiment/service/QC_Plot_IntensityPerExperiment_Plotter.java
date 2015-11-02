package org.yeastrc.qc_plots.plot_intensity_per_experiment.service;


import java.io.IOException;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.List;

import org.apache.log4j.Logger;
import org.yeastrc.ms.dao.DAOFactory;
import org.yeastrc.ms.dao.general.MsExperimentDAO;
import org.yeastrc.ms.dao.run.MsScanDAO;
import org.yeastrc.ms.domain.run.MsScan;
import org.yeastrc.ms.domain.run.Peak;
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
public class QC_Plot_IntensityPerExperiment_Plotter {


	private static final Logger log = Logger.getLogger(QC_Plot_IntensityPerExperiment_Plotter.class);

	private static DAOFactory daoFactory = DAOFactory.instance();

	private static final int BIN_COUNT = 50;  //  Number of bars on the chart

	private static final int MS_LEVEL_2 = 2;    //  Use MS2 records

	private static final float INTENSITY_VALUE_SKIP_ZERO = 0f;  // skip intensity value of zero
	
	private static final float INTENSITY_PERCENT_MAX = 100;
	
	private static final int PERCENTAGE_SIGNIFICANT_DIGITS = 3;


	//  Increment this when change the JSON format so the existing data stored in the table will not be used
	private static final int CURRENT_DATA_VERSION = 1;

	
	
	/**
	 * @param experimentId
	 * @return
	 * @throws IOException 
	 */
	public static String getIntensityPerExperimentPlot( int experimentId ) throws IOException {

		String plotData = getStoredIntensityPerExperimentPlotFromDB( experimentId );

		if ( plotData != null ) {

			return plotData;
		}

		QCPlotDataDAO qcPlotDataDAO = new QCPlotDataDAOImpl();

		QCPlotDataDTO qcPlotDataDTO = generateQCPlotDataDTOFromDB( experimentId );

		qcPlotDataDTO.setDataVersion( CURRENT_DATA_VERSION );

		try {

			qcPlotDataDAO.saveOrUpdate( qcPlotDataDTO );

		} catch (Exception ex ) {

			log.error( "QC_Plot_IntensityPerExperiment_Plotter: getIntensityPerExperimentPlot(): Error saving generated plot data to DB", ex);
		}

		return qcPlotDataDTO.getPlotData();
	}


	/**
	 * @param experimentId
	 * @return
	 */
	public static String getStoredIntensityPerExperimentPlotFromDB( int experimentId ) {

		QCPlotDataDAO qcPlotDataDAO = new QCPlotDataDAOImpl();

		QCPlotDataDTO qcPlotDataDTO = null;

		try {

			qcPlotDataDTO = qcPlotDataDAO.loadFromExperimentIdPlotTypeAndDataVersion( experimentId, 
					QC_Plots_Constants.PLOT_TYPE__INTENSITY_PER_EXPERIMENT, CURRENT_DATA_VERSION);

			if ( qcPlotDataDTO != null ) {

				return qcPlotDataDTO.getPlotData();
			}

		} catch (Exception ex ) {

			log.error( "QC_Plot_IntensityPerExperiment_Plotter: getStoredIntensityPerExperimentPlotFromDB(): Error getting stored plot data from DB", ex);
		}

		return null;
	}


	/**
	 * @param experimentId
	 * @return
	 * @throws IOException 
	 */
	private static QCPlotDataDTO generateQCPlotDataDTOFromDB( int experimentId ) throws IOException {

		long startTime = System.currentTimeMillis();


		MsExperimentDAO msExperimentDAO = daoFactory.getMsExperimentDAO();

		MsScanDAO msScanDAO = daoFactory.getMsScanDAO();


		
		int scanCount = 0;


		int numScans = 0;
		
		int scanIdIntensityMin = 0;
		int scanIdIntensityMax = 0;

		float intensityMin = 0;
		float intensityMax =  0;
		
		
		boolean firstIntensityPercentMin = true;

		float intensityPercentOverallMin = 0;


		//  Find max and min values


		List<Integer> runIdsPerExperiment = msExperimentDAO.getRunIdsForExperiment( experimentId );

		boolean firstOverallIntensityEntry = true;


		for ( int runId : runIdsPerExperiment ) {


			List<Integer> scanIdsPerRun = msScanDAO.loadScanIdsForRunAndLevel( runId, MS_LEVEL_2 );

			numScans += scanIdsPerRun.size();



			//		INTENSITY_VALUE_SKIP_ZERO

			for ( int scanId : scanIdsPerRun ) {
				
				scanCount++;
				
				//  TODO  TEMP
				
//				if ( scanCount > 20 ) {
//					
//					break;
//				}
				
				

				MsScan scan = msScanDAO.load(scanId);
				
				
				float intensityPerScanMin = 0;
				float intensityPerScanMax =  0;
				
				boolean firstIntensityEntryInThisScan = true;
				

				List<Peak> peakList = scan.getPeaks();
				for (Peak peak: peakList) {
					
					float intensity = peak.getIntensity();
					
					if ( intensity > INTENSITY_VALUE_SKIP_ZERO ) {

						//  Intensity for this scan statistics

						if ( firstIntensityEntryInThisScan ) {
							
							firstIntensityEntryInThisScan = false;
							
							intensityPerScanMin = intensity;
							intensityPerScanMax = intensity;

						} else {
							if ( intensity < intensityPerScanMin ) {
								intensityPerScanMin = intensity;
							}

							if ( intensity > intensityPerScanMax  ) {
								intensityPerScanMax = intensity;
							}
						}
						
						//  Intensity overall statistics
						
						if ( firstOverallIntensityEntry  ) {

							firstOverallIntensityEntry = false;

							intensityMin = intensity;
							intensityMax = intensity;

							scanIdIntensityMin = scanId;
							scanIdIntensityMax = scanId;

						} else {
							if ( intensity < intensityMin ) {
								intensityMin = intensity;
								scanIdIntensityMin = scanId;
							}

							if ( intensity > intensityMax  ) {
								intensityMax = intensity;
								scanIdIntensityMax = scanId;
							}
						}

					}
				}
				
				float intensityPercentMinForScan = ( intensityPerScanMin / intensityPerScanMax ) * 100;
				
				if ( firstIntensityPercentMin ) {
					
					firstIntensityPercentMin = false;
					
					intensityPercentOverallMin = intensityPercentMinForScan;
				} else {
					
					if ( intensityPercentMinForScan < intensityPercentOverallMin )

						intensityPercentOverallMin = intensityPercentMinForScan;
				}
				
			}
		}

		float intentityPercentMaxMinusMin = INTENSITY_PERCENT_MAX - intensityPercentOverallMin;


		double binSizeAsDouble = ( intentityPercentMaxMinusMin ) / BIN_COUNT;


		int[] intensityCounts = new int[ BIN_COUNT ];

		for ( int runId : runIdsPerExperiment ) {


			List<Integer> scanIdsPerRun = msScanDAO.loadScanIdsForRunAndLevel( runId, MS_LEVEL_2 );

			for ( int scanId : scanIdsPerRun ) {

				MsScan scan = msScanDAO.load(scanId);
				
				float intensityPerScanMin = 0;
				float intensityPerScanMax =  0;
				
				boolean firstIntensityEntryInThisScan = true;
				

				List<Peak> peakList = scan.getPeaks();
				
				for (Peak peak: peakList) {
					
					float intensity = peak.getIntensity();
					
					if ( intensity > INTENSITY_VALUE_SKIP_ZERO ) {

						//  Intensity for this scan statistics

						if ( firstIntensityEntryInThisScan ) {
							
							firstIntensityEntryInThisScan = false;
							
							intensityPerScanMin = intensity;
							intensityPerScanMax = intensity;

						} else {
							if ( intensity < intensityPerScanMin ) {
								intensityPerScanMin = intensity;
							}

							if ( intensity > intensityPerScanMax  ) {
								intensityPerScanMax = intensity;
							}
						}
					}
				}
				
				
				for (Peak peak: peakList) {

					float intensity = peak.getIntensity();

					if ( intensity > INTENSITY_VALUE_SKIP_ZERO ) {
						
						float intensityPercent = ( intensity / intensityPerScanMax ) * 100;


						int bin = (int) ( ( ( (double) ( intensityPercent - intensityPercentOverallMin ) )  / intentityPercentMaxMinusMin ) * BIN_COUNT );

						if ( bin < 0 ) {

							bin = 0;
						} else if ( bin >= BIN_COUNT ) {

							bin = BIN_COUNT - 1;
						} 

						intensityCounts[ bin ]++;
					}
				}
				
				
			}

		}

		//  Generate JSON   All labels/object member names must be in " (double quotes)

		StringBuilder plotDataSB = new StringBuilder( 10000 );

		plotDataSB.append("{\"experimentId\":");
		plotDataSB.append(experimentId);
		plotDataSB.append(",\"numScans\":");
		plotDataSB.append(numScans);
		plotDataSB.append(",\"intensityMax\":");
		plotDataSB.append(intensityMax);
		plotDataSB.append(",\"intensityMin\":");
		plotDataSB.append(intensityMin);
		plotDataSB.append(",\"scanIdIntensityMin\":");
		plotDataSB.append(scanIdIntensityMin);
		plotDataSB.append(",\"scanIdIntensityMax\":");
		plotDataSB.append(scanIdIntensityMax);
		plotDataSB.append(",\"intensityPercentOverallMin\":");
		plotDataSB.append(intensityPercentOverallMin);
		

		//  change to output the general data that will be re-formatted in the Javascript
		//  to be what Google chart can use

		plotDataSB.append(",\"chartBuckets\":");		

		//  start of array
		plotDataSB.append("[");
		
		MathContext mathContext= new MathContext( PERCENTAGE_SIGNIFICANT_DIGITS, RoundingMode.HALF_EVEN );
		
		
		
		double binHalf = binSizeAsDouble / 2 ;

		
		int counter = 0;
		for ( int intensityCount : intensityCounts ) {

			if ( counter > 0 ) {
				plotDataSB.append(",");
			}

			String binStart = null;
			String binEnd = null;

			double binStartDouble = ( ( counter * binSizeAsDouble ) + intensityPercentOverallMin );

			if ( counter == 0 && binStartDouble < 0.1 ) {
				
				binStart = "0";
			} else { 

				binStart = new BigDecimal( binStartDouble, mathContext ).toString();
			}
			
			
			if ( counter == intensityCounts.length - 1 ) {
				
				binEnd = "100";
			} else {
				double binEndDouble = ( ( ( counter + 1 ) * binSizeAsDouble ) + intensityPercentOverallMin );

				binEnd =  new BigDecimal( binEndDouble, mathContext ).toString();
			}
			
			double binMiddleDouble = binStartDouble + binHalf;

			// start of next entry
			plotDataSB.append("{");


			// start/left side of bin
			plotDataSB.append("\"binStart\":");
			plotDataSB.append( binStart );
			plotDataSB.append(",");
			// end/right side of bin
			plotDataSB.append("\"binEnd\":");
			plotDataSB.append( binEnd );
			plotDataSB.append(",");
			// middle of next entry
			plotDataSB.append("\"binMiddle\":");
			// middle side of bin
			plotDataSB.append( binMiddleDouble );
			plotDataSB.append(",");
			// count
			plotDataSB.append("\"count\":");
			plotDataSB.append(intensityCount);
			//  end of entry
			plotDataSB.append("}");

			counter++;
		}

	//  end of array and end of outermost object
		plotDataSB.append("]}");		


		String plotData = plotDataSB.toString();


		QCPlotDataDTO qcPlotDataDTO = new QCPlotDataDTO();

		qcPlotDataDTO.setExperimentId( experimentId );
		qcPlotDataDTO.setPlotType( QC_Plots_Constants.PLOT_TYPE__INTENSITY_PER_EXPERIMENT );
		qcPlotDataDTO.setPlotData( plotData );
		qcPlotDataDTO.setScanCount( numScans );

		long endTime = System.currentTimeMillis();

		int createTimeInSeconds = Math.round( ( (float)( endTime - startTime ) ) / 1000 );

		qcPlotDataDTO.setCreateTimeInSeconds( createTimeInSeconds );

		return qcPlotDataDTO;
	}
}