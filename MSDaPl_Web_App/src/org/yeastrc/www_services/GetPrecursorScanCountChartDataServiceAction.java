package org.yeastrc.www_services;

import java.io.BufferedWriter;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.yeastrc.experiment.ProjectExperimentDAO;
import org.yeastrc.project.Project;
import org.yeastrc.project.ProjectFactory;
import org.yeastrc.qc_plots.premz_scan_count_plot.service.PreMZScanCountPlotter;
import org.yeastrc.www.user.User;
import org.yeastrc.www.user.UserUtils;

public class GetPrecursorScanCountChartDataServiceAction  extends Action {

    
    private static final Logger log = Logger.getLogger(GetPrecursorScanCountChartDataServiceAction.class.getName());
    
	public ActionForward execute( ActionMapping mapping,
								  ActionForm form,
								  HttpServletRequest request,
								  HttpServletResponse response )
	throws Exception {

		String experimentIDsString = request.getParameter( "experimentIds" );

		try {

			// User making this request
			User user = UserUtils.getUser(request);
			if (user == null) {
				
				response.sendError( 401 /* status code */, "No user logged in"); // Unauthorized

				return null;
			}

			if ( StringUtils.isEmpty( experimentIDsString )) {

				response.sendError( 400 /* status code */, "No data in param 'experimentIds'"); // Bad Request

				return null;
			}

			String[] experimentIDsStringSplit = experimentIDsString.split(",");

			int[] experimentIds = new int[ experimentIDsStringSplit.length ];

			for ( int index = 0; index < experimentIDsStringSplit.length; index++ ) {

				String experimentIdString = experimentIDsStringSplit[ index ];

				try {

					experimentIds[ index ] = Integer.parseInt( experimentIDsStringSplit[ index ] );
				} catch ( Exception e ) {

					response.sendError( 400 /* status code */, "Data param 'experimentIds' is not a comma delimited list of numbers"); // Bad Request

					return null;
				}
			}

			int firstExperimentId = experimentIds[ 0 ];

			int projectIdForFirstExperiment = ProjectExperimentDAO.instance().getProjectIdForExperiment( firstExperimentId );

			if ( projectIdForFirstExperiment == 0 ) {

				response.sendError( 400 /* status code */, "No data found"); // Bad Request

				return null;
			}

			// Load our project
			Project project;

			try {
				project = ProjectFactory.getProject(projectIdForFirstExperiment);
				if (!project.checkReadAccess(user.getResearcher())) {

					response.sendError( 401 /* status code */, "No access to this data"); // Unauthorized

					return null;
				}
			} catch (Exception e) {

				response.sendError( 401 /* status code */, "No access to this data"); // Unauthorized

				return null;
			}

			StringBuilder outputDataSB = new StringBuilder( 10000 );

			outputDataSB.append( "{\"data\":" );

			boolean firstPlotData = true;

			for ( int index = 0; index < experimentIds.length; index++ ) {

				int experimentId = experimentIds[ index ];

				String precursorMassChartData = null;
				
				try {
				
					precursorMassChartData = PreMZScanCountPlotter.getMZScanCountPlot( experimentId );
				
				} catch ( Exception ex ) {
					
					log.error( "GetPrecursorScanCountChartDataServiceAction:  Exception getting precursorMassChartData for experimentId: " + experimentId, ex );
					
					throw ex;
				}

				if ( precursorMassChartData != null ) {

					if ( firstPlotData ) {
						outputDataSB.append( "[" );
						firstPlotData = false;
					} else {

						outputDataSB.append( "," );
					}

					outputDataSB.append( precursorMassChartData );
				}
			}

			if ( firstPlotData ) {
				outputDataSB.append( "null" );
			} else {

				outputDataSB.append( "]" );
			}
			
			outputDataSB.append( "}" );
			
			String outputData = outputDataSB.toString();

			OutputStream os = response.getOutputStream();

			BufferedWriter BufferedWriter = null;

			try {
				BufferedWriter = new BufferedWriter( new OutputStreamWriter(os, "UTF-8"));
				
				BufferedWriter.write( outputData );
				
			} catch ( Exception ex ) {
				
				log.error( "GetPrecursorScanCountChartDataServiceAction:  Exception writing response.  experimentIDs: " + experimentIDsString, ex );
				
				throw ex;

			} finally {
				
				if ( BufferedWriter != null ) {
					
					BufferedWriter.close();
				}
				
				try {
					
					os.close();
					
				} catch( Exception ex ) {
					
					
				}
					
					
			}

		} catch( Exception ex ) {
			
			log.error( "GetPrecursorScanCountChartDataServiceAction:  Sending '500' response.  experimentIDs: " + experimentIDsString, ex );
			
			response.sendError( 500 /* status code */, "Internal Error"); // Internal Error

		}

		return null;
	}


}
