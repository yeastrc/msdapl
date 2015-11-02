package org.yeastrc.qc_plots.premz_scan_count_plot.service;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

import org.apache.log4j.Logger;
import org.yeastrc.ms.dao.DAOFactory;
import org.yeastrc.qc_plots.constants.QC_Plots_Constants;
import org.yeastrc.qc_plots.dao.QCPlotDataDAO;
import org.yeastrc.qc_plots.dao.jdbc.QCPlotDataDAOImpl;
import org.yeastrc.qc_plots.dto.QCPlotDataDTO;


/**
 * Creates the JSON to define the Precursor MZ chart
 * 
 * The computed JSON is stored using QCPlotDataDAO
 * and retrieve uses that for the next request for the same
 * experiment id and if the data_version matches CURRENT_DATA_VERSION
 *
 */
public class PreMZScanCountPlotter {


	private static final Logger log = Logger.getLogger(PreMZScanCountPlotter.class);

    private static DAOFactory daoFactory = DAOFactory.instance();

	private static final int BIN_COUNT_PREMZ_VALUES = 50;  //  Number of bars on the chart

	
	private static final int NUMBER_SIGNIFICANT_DIGITS_TO_START_WITH = 3;
	
	private static final int NUMBER_SIGNIFICANT_DIGITS_ADDED_BEYOND_UNIQUE = 2;

	 //  Increment this when change the JSON format so the existing data stored in the table will not be used
	private static final int CURRENT_DATA_VERSION = 2;
	


	
	/**
	 * @param experimentId
	 * @return
	 */
	public static String getMZScanCountPlot( int experimentId ) {

		String plotData = getStoredMZScanCountPlotFromDB( experimentId );
		
		if ( plotData != null ) {
			
			return plotData;
		}
		QCPlotDataDAO qcPlotDataDAO = new QCPlotDataDAOImpl();
		
		QCPlotDataDTO qcPlotDatadto = generateMZScanCountPlotFromDB( experimentId );
		
		qcPlotDatadto.setPlotType( QC_Plots_Constants.PLOT_TYPE__PREMZ_SCAN_COUNT );
		qcPlotDatadto.setDataVersion( CURRENT_DATA_VERSION );

		try {
			
			qcPlotDataDAO.saveOrUpdate( qcPlotDatadto );
		
		} catch (Exception ex ) {
			
			log.error( "PreMZScanCountPlotter: getMZScanCountPlot(): Error saving stored plot data to DB", ex);
		}
		
		return qcPlotDatadto.getPlotData();
	}
	

	/**
	 * @param experimentId
	 * @return
	 */
	public static String getStoredMZScanCountPlotFromDB( int experimentId ) {

		QCPlotDataDAO qcPlotDataDAO = new QCPlotDataDAOImpl();
		
		QCPlotDataDTO qcPlotDatadto = null;
		
		try {
		
			qcPlotDatadto = qcPlotDataDAO.loadFromExperimentIdPlotTypeAndDataVersion( experimentId, 
								QC_Plots_Constants.PLOT_TYPE__PREMZ_SCAN_COUNT, CURRENT_DATA_VERSION );

			if ( qcPlotDatadto != null ) {

				return qcPlotDatadto.getPlotData();
			}
		
		} catch (Exception ex ) {
			
			log.error( "PreMZScanCountPlotter: getMZScanCountPlot(): Error getting stored plot data from DB", ex);
		}
		
		return null;
	}
	
	
	/**
	 * @param experimentId
	 * @return
	 */
	public static QCPlotDataDTO generateMZScanCountPlotFromDB( int experimentId ) {
		
		long startTime = System.currentTimeMillis();
		
		double[] preMZArray  = daoFactory.getMsScanDAO().getPreMZArrayForExperimentIdScanLevelNotOnePreMZNotNULL(experimentId);
		
		
//		Arrays.sort( preMZArray );  //  only use for debugging

		
		int numScans = preMZArray.length;
		
		double precursorMZMinDouble = 0;
		double precursorMZMaxDouble =  0;
		
		boolean firstEntry = true;

		//  Find max and min values
		
		for ( double preMZ : preMZArray ) {
			
			if ( firstEntry ) {
				
				firstEntry = false;
				
				precursorMZMinDouble = preMZ;
				precursorMZMaxDouble = preMZ;
				
			} else {
				if ( preMZ < precursorMZMinDouble ) {
					precursorMZMinDouble = preMZ;
				}

				if ( preMZ > precursorMZMaxDouble ) {
					precursorMZMaxDouble = preMZ;
				}
			}
		}
		
		double precursorMZMaxMinusMin = precursorMZMaxDouble - precursorMZMinDouble;

		
		double binSizeAsDouble = ( precursorMZMaxMinusMin ) / BIN_COUNT_PREMZ_VALUES;

		
		int[] scanCounts = new int[ BIN_COUNT_PREMZ_VALUES ];
		
		for ( double preMZ : preMZArray ) {
			
			int bin = (int) ( (  ( preMZ - precursorMZMinDouble )  / precursorMZMaxMinusMin ) * BIN_COUNT_PREMZ_VALUES );
			
			if ( bin < 0 ) {
				
				bin = 0;
			} else if ( bin >= BIN_COUNT_PREMZ_VALUES ) {
				
				bin = BIN_COUNT_PREMZ_VALUES - 1;
			} 
			
			scanCounts[ bin ]++;
		}
		
		
		//  Create list of preMZ values that are approximately the start of each bin
		
		
		
		BigDecimal[] preMZBinStartArray = new BigDecimal[ BIN_COUNT_PREMZ_VALUES + 1 ];
		
		boolean foundMatchingValues = true;
		
		BigDecimal prevValue = null;
		
		int currentSignificantDigits = NUMBER_SIGNIFICANT_DIGITS_TO_START_WITH;
		
		while ( foundMatchingValues ) {
			
			foundMatchingValues = false;
			
			prevValue = null;
			
			MathContext mathContext= new MathContext( currentSignificantDigits, RoundingMode.HALF_EVEN );
			
			for ( int index = 0; index < BIN_COUNT_PREMZ_VALUES + 1; index++ ) {

				double preMZBinStartDouble = ( index * binSizeAsDouble ) + precursorMZMinDouble ;

				BigDecimal preMZBinStart = new BigDecimal( preMZBinStartDouble, mathContext );

				preMZBinStartArray[ index ] = preMZBinStart;
				
				if ( prevValue == null ) {
					
					prevValue = preMZBinStart;
				} else {
					
					if ( preMZBinStart.equals( prevValue ) ) {
						
						foundMatchingValues = true;
						break;
					}
				}
			}
		
		}		
		
		//  compute final version of values
		
		currentSignificantDigits += NUMBER_SIGNIFICANT_DIGITS_ADDED_BEYOND_UNIQUE;

		MathContext mathContext= new MathContext( currentSignificantDigits, RoundingMode.HALF_EVEN );
		
		for ( int index = 0; index < BIN_COUNT_PREMZ_VALUES; index++ ) {

			double preMZBinStartDouble = ( index * binSizeAsDouble ) + precursorMZMinDouble ;

			BigDecimal preMZBinStart = new BigDecimal( preMZBinStartDouble, mathContext );

			preMZBinStartArray[ index ] = preMZBinStart;
			
			if ( prevValue == null ) {
				
				prevValue = preMZBinStart;
			} else {
				
				if ( preMZBinStart.equals( prevValue ) ) {
					
					throw new RuntimeException( "Found matching bin start values with final significant digits: " + currentSignificantDigits );
				}
			}
		}

		
		
		
		//  Generate JSON   All labels/object member names must be in " (double quotes)
		
		StringBuilder plotDataSB = new StringBuilder( 10000 );
		
		plotDataSB.append("{\"experimentId\":");
		plotDataSB.append(experimentId);
		plotDataSB.append(",\"numScans\":");
		plotDataSB.append(numScans);
		plotDataSB.append(",\"precursorMZMax\":");
//		outputDataSB.append(precursorMZMax);
		plotDataSB.append(precursorMZMaxDouble);
		plotDataSB.append(",\"precursorMZMin\":");
//		outputDataSB.append(precursorMZMin);
		plotDataSB.append(precursorMZMinDouble);
		
		
		//  change to output the general data that will be re-formatted in the Javascript
		//  to be what Google chart can use

		plotDataSB.append(",\"chartBuckets\":");		
		
		//  start of array
		plotDataSB.append("[");
		
		double preMZBinHalf = binSizeAsDouble / 2 ;

		
		int index = 0;
		for ( int scanCount : scanCounts ) {

			if ( index > 0 ) {
				plotDataSB.append(",");
			}
			
			double preMZBinMiddleDouble = ( index * binSizeAsDouble ) + preMZBinHalf + precursorMZMinDouble ;


			// start of next entry
			plotDataSB.append("{");
			
			// start/left side of bin
			plotDataSB.append("\"binStart\":");
			plotDataSB.append( preMZBinStartArray[ index ] );
			plotDataSB.append(",");
			// end/right side of bin
			plotDataSB.append("\"binEnd\":");
			plotDataSB.append( preMZBinStartArray[ index + 1 ] );
			plotDataSB.append(",");
			// middle of next entry
			plotDataSB.append("\"binMiddle\":");
			// middle side of bin
			plotDataSB.append( preMZBinMiddleDouble );
			plotDataSB.append(",");
			// count
			plotDataSB.append("\"count\":");
			plotDataSB.append(scanCount);
			//  end of entry
			plotDataSB.append("}");
			
			index++;
		}
		
		//  end of array and end of outermost object
		plotDataSB.append("]}");		
		

		String plotData = plotDataSB.toString();
		
		
		QCPlotDataDTO qcPlotDataDTO = new QCPlotDataDTO();
		
		qcPlotDataDTO.setExperimentId( experimentId );
		qcPlotDataDTO.setPlotData( plotData );
		qcPlotDataDTO.setScanCount( numScans );
		
		long endTime = System.currentTimeMillis();

		int createTimeInSeconds = Math.round( ( (float)( endTime - startTime ) ) / 1000 );
		
		qcPlotDataDTO.setCreateTimeInSeconds( createTimeInSeconds );
		
		return qcPlotDataDTO;
	}
}
