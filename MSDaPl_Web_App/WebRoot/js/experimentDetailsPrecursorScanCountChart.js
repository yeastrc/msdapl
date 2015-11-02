

//   experimentDetailsPrecursorScanCountChart.js


//   Set up parts of experimentDetails.jsp

//     Only some of the JS for setting up experimentDetails.jsp is here.
//     Other JS code is in JSP files that include experimentDetails.jsp.


//    WARNING   Only place this script in the <body> section after the input field with id="webAppContextPath_WebApp_Wide".
//               That input field is added to the header.jsp

//////////////////////////////////

// JavaScript directive:   all variables have to be declared with "var", maybe other things

"use strict";




////////////////////////////////////////////


//Called from the Google Chart load callback
function createAllInitialDisplayPrecursorScanCountCharts() {


	//	delay to disconnect from page loading Javascript execution
	setTimeout(function(){

		CreatePrecursorScanCountCharts.createAllInitialDisplayPrecursorScanCountCharts();

	}, 50); // delay in milliseconds

}


/////////////////////////////////////

/////   WARNING   Do not pass a reference to a method as a callback function, the "this" will not point to the object.


//Constructor

var CreatePrecursorScanCountChartsClass = function () {


	this.webAppContextPath_WebApp_Wide_Val = $("#webAppContextPath_WebApp_Wide").val();

	this.SERVICE_URLS = {
			
			GET_CHART_DATA_FOR_EXPERIMENT_IDS: this.webAppContextPath_WebApp_Wide_Val + "/getPrecursorScanCountChartDataService.do"
	};
	
};


/////////////////////////////////////

//Create the PrecursorScanCount Chart for the passed in experiment id

CreatePrecursorScanCountChartsClass.prototype.createDisplayPrecursorScanCountChartForExperimentId = function( experimentId ) {
	
	var objectThis = this;
	
	var experiment_details_outer_div_id = "exp_root_target_" + experimentId;
	
	var $experimentDetailsDiv = $("#" + experiment_details_outer_div_id);
	
	var uploadSuccess = $experimentDetailsDiv.attr("experiment_upload_success");
	
	if ( uploadSuccess === "true" ) {
		
		//  Only get chart data for uploadSuccess === "true"
			
		var experimentIdString = $experimentDetailsDiv.attr("experiment_id");
		
		var experimentIdsForGetViaAjax = [ experimentIdString ];


	//  Load the chart that need computing via ajax

		this.getChartDataViaAjax( experimentIdsForGetViaAjax, $experimentDetailsDiv );
				
	}
};


/////////////////////////////////////

//  Create the PrecursorScanCount Chart for all the experiments on the page that loaded successfully

CreatePrecursorScanCountChartsClass.prototype.createAllInitialDisplayPrecursorScanCountCharts = function(  ) {
	
	var objectThis = this;
	
	var $experiment_details_outer_div_list = $(".experiment_details_outer_div_jq");
	
	var experimentIdsForGetViaAjax = [];
	
	$experiment_details_outer_div_list.each( function(index) {
		
		var $experimentDetailsDiv = $(this);
		
		var uploadSuccess = $experimentDetailsDiv.attr("experiment_upload_success");

//		var experiment_has_full_information = $experimentDetailsDiv.attr("experiment_has_full_information");

		//  WAS
//		if ( uploadSuccess === "true" && experiment_has_full_information === "true" ) {
		//  Only get chart data for uploadSuccess === "true" && experiment_has_full_information === "true"
		

		if ( uploadSuccess === "true" ) {
			
			//  Only get chart data for uploadSuccess === "true" 
				
			var experimentIdString = $experimentDetailsDiv.attr("experiment_id");
			
			//  get chart data from hidden input field

			var $chartData = $experimentDetailsDiv.find(".experiment_precursor_scan_count_chart_data_jq");

			var chartDataString = $chartData.val();

			if ( chartDataString === "" ) {

				//  the chart data was not pre-computed so must request it via ajax

				experimentIdsForGetViaAjax.push( experimentIdString );

			} else {

				try {

					var chartDataFromServer = JSON.parse( chartDataString );

				} catch (e) { 
					var stracktrace = e.stack;
					throw "Error parsing JSON on page.  experiment_id: " + experimentIdString + ", Error: " + e.message; 
				}				

				objectThis.createPrecursorScanCountChartActual( $experimentDetailsDiv, chartDataFromServer, experimentIdString );
			}
		}
	});
	
	if ( experimentIdsForGetViaAjax.length > 0 ) {  
		
		//  Load the charts that need computing via ajax
	
		objectThis.getChartDataViaAjax( experimentIdsForGetViaAjax, $experiment_details_outer_div_list );
	}
};	


/////////////////////////////////////

//  Load the charts that need computing via ajax

CreatePrecursorScanCountChartsClass.prototype.getChartDataViaAjax = function ( experimentIdsForGetViaAjax, $experiment_details_outer_div_list, experimentIdIndex ) {
	
	//  This function will get the chart data via AJAX for the experiment ids in experimentIdsForGetViaAjax.
	
	//   It will do the AJAX call for the experiment id specified by experimentIdIndex.
	//       After the AJAX callback, it will call itself after incrementing experimentIdIndex to process the next experiment id
	
	var objectThis = this;
	
	if ( experimentIdIndex === undefined ) {
		experimentIdIndex = 0;
	}
	
	var experimentId = experimentIdsForGetViaAjax[ experimentIdIndex ];
	
	var context = { $experiment_details_outer_div_list : $experiment_details_outer_div_list };
	
	
	//  Optional way to combine the ids into comma delim list to get all at once (then need to remove code in callback that calls this function again) 
//	var experimentIdsCommaDelim = experimentIdsForGetViaAjax.join(",");

	
	//  Actually sending the experiment ids one at a time but could send a comma delimited list of experiment ids
	var ajaxData = { "experimentIds" : experimentId };

	$.ajax(
		{ url : this.SERVICE_URLS.GET_CHART_DATA_FOR_EXPERIMENT_IDS,
			cache: false, //  always need to send this request to the server

			success :  function( data ) {

				objectThis.processChartDataFromAjax( { chartDataArrayFromServer: data, context: context } );

				experimentIdIndex++;  // advance to next experiment id
				
				//  Continue if there are any more to load
				while ( experimentIdIndex < experimentIdsForGetViaAjax.length) {
					
					//  get next experiment id
					var nextExperimentIdForGetViaAjax = experimentIdsForGetViaAjax[ experimentIdIndex ];
					
					var $experimentDetailsDiv = objectThis.get$experimentDetailsDivFromExperimentId( nextExperimentIdForGetViaAjax );

					if ( ! objectThis.isThumbnailAlreadyCreated( $experimentDetailsDiv ) ) {

						//  If not already loaded, break this loop and it will be loaded in the code next
						break;
					}
					
					experimentIdIndex++;  //  advance to skip over experiment id where thumbnail chart already created
				}
					
				//  Load the data for the next chart if there are any more charts to load data for
				if ( experimentIdIndex < experimentIdsForGetViaAjax.length) {
						
					var getNextChartDataDelay = 200; // delay in milliseconds
					
					if ( experimentIdIndex > 3 ) {
						getNextChartDataDelay = 4000; // after the 4th chart loaded via AJAX, increase the delay to 4 seconds 
					}

					//  delay to load each experiment chart data after a delay to space out the load on the server
					setTimeout(function(){

						objectThis.getChartDataViaAjax( experimentIdsForGetViaAjax, $experiment_details_outer_div_list, experimentIdIndex );

					}, getNextChartDataDelay ); // delay in milliseconds
				}
			},

			error: function(jqXHR, textStatus, errorThrown) {

				throw "AJAX error:  textStatus: " + textStatus + ", errorThrown: " + errorThrown;
			},
			data: ajaxData,  //  The data sent as params on the URL
			dataType : "json"
		});
};



/////////////////////////////////////

CreatePrecursorScanCountChartsClass.prototype.processChartDataFromAjax = function ( param ) {
	
	var objectThis = this;

	var chartDataArrayFromServer = param.chartDataArrayFromServer.data;

//	var context = param.context;

	for ( var index = 0; index < chartDataArrayFromServer.length; index++ ) {

		var chartDataFromServer = chartDataArrayFromServer[ index ];

		var $experimentDetailsDiv = this.get$experimentDetailsDivFromExperimentId( chartDataFromServer.experimentId );

		objectThis.createPrecursorScanCountChartActual( $experimentDetailsDiv, chartDataFromServer, chartDataFromServer.experimentId );
	}
};

/////////////////////////////////////

//  Create the PrecursorScanCount Chart for one experiment on the page

CreatePrecursorScanCountChartsClass.prototype.createPrecursorScanCountChartActual = function ( $experimentDetailsDiv, chartDataFromServer, experimentIdString ) {
	
	var chartBuckets = chartDataFromServer.chartBuckets;

	//  chart data for Google charts
	var chartData = [];

	//  output columns specification
	chartData.push( ["preMZ","count",{role: "tooltip",  'p': {'html': true} } ] );


	for ( var index = 0; index < chartBuckets.length; index++ ) {

		var bucket = chartBuckets[ index ];
		
		var tooltip = "<div style='margin: 10px;'>scan count: " + bucket.count + 
		"<br>preMZ approximately " + bucket.binStart + " to " + bucket.binEnd + "</div>";

		chartData.push( [bucket.binMiddle, bucket.count, tooltip  ] );
	}

	// create the chart

	var data = google.visualization.arrayToDataTable( chartData );

	// https://developers.google.com/chart/interactive/docs/gallery/columnchart#Configuration_Options

	var optionsThumbnail = {

			axisTitlesPosition: 'none',  //  'in', out or none

			//  X axis label below chart
			hAxis: { baselineColor: 'transparent'   // remove line on left side of chart area bounding the chart lines
					,textPosition: 'none'		  // remove the text labels for values on X axis
					, gridlines: { color: 'transparent' }  // remove horizontal grid lines 
			},  
			//  Y axis label left of chart data
			vAxis: { baseline: 0                    // always start at zero
					, baselineColor: 'transparent'  // remove line on bottom side of chart area bounding the chart lines
					, textPosition: 'none'		    // remove the text labels for values on Y axis
					, gridlines: { color: 'transparent' }  // remove vertical grid lines  
			},

			enableInteractivity: false, //  false for turn off tool tips and other interactive features 
			legend: { position: 'none' }, //  position: 'none':  Don't show legend of bar colors in upper right corner

			width:60, height:60,   // width and height of chart, otherwise controlled by enclosing div

			bar: { groupWidth: '100%' },  // set bar width large to eliminate space between bars
			colors: ['green']  //  Color of bars
	};

//	chartArea:{left:80,top:50,width:"800",height:"80"}, // left and top options will define the amount of padding from left and top
//	chartArea:{left:80,top:50}, // left and top options will define the amount of padding from left and top
//	chartArea:{left:10,top:50,width:"100%",height:"80%"}, // left and top options will define the amount of padding from left and top
//	chartArea:{left:10,top:20,width:"100%",height:"100%"}, // left and top options will define the amount of padding from left and top

	
	//  Detect if thumbnail chart already created
	if ( this.isThumbnailAlreadyCreated( $experimentDetailsDiv ) ) {
		
		return;  ///   EXIT function early if nothing to create
		
	}
	
	
	//  get div to put the chart in, and create sub div

	var $chartThumbnailDiv = $experimentDetailsDiv.find(".experiment_precursor_scan_count_chart_div_jq");
	
	
	if ( $chartThumbnailDiv.length < 1 ) {
		
		throw "unable to find div with class = " + "experiment_precursor_scan_count_chart_div_jq";
	}
	
	var $divInsideThumbnailChartDiv = $("<div actual_thumbnail_chart_holder='true'></div>").appendTo( $chartThumbnailDiv );


	var divInsideThumbnailChartDivHTML = $divInsideThumbnailChartDiv[0];


	var chartThumbnail = new google.visualization.ColumnChart( divInsideThumbnailChartDivHTML );

	var chartThumbnailReadyHandler = function( paramNotProvided) {

		//  Show the created chart
		var $experiment_precursor_scan_count_chart_outer_div_jq = 
			$experimentDetailsDiv.find(".experiment_precursor_scan_count_chart_outer_div_jq");

		$experiment_precursor_scan_count_chart_outer_div_jq.show();
		
		//  Show the enclosing "QC Plots" div
		var $enclosing_qc_plots_div_jq = 
			$experimentDetailsDiv.find(".experiment_details_qc_plots_div_jq");

		$enclosing_qc_plots_div_jq.show();
				
		
	};

	google.visualization.events.addListener(chartThumbnail, 'ready', chartThumbnailReadyHandler);

	chartThumbnail.draw(data, optionsThumbnail);

	//   Set up the creating of the full size chart when the thumbnail chart is clicked 

	var $experiment_precursor_scan_count_chart_click_for_full_size_jq = 
		$experimentDetailsDiv.find(".experiment_precursor_scan_count_chart_click_for_full_size_jq");

	$experiment_precursor_scan_count_chart_click_for_full_size_jq.click( function() {

		var $experiment_precursor_scan_count_chart_full_size_jq = 
			$experimentDetailsDiv.find(".experiment_precursor_scan_count_chart_full_size_jq");
		
		if ( $experiment_precursor_scan_count_chart_full_size_jq.length < 1 ) {
			
			throw "unable to find div with class = " + "experiment_precursor_scan_count_chart_full_size_jq";
		}
		


		var title = $experiment_precursor_scan_count_chart_full_size_jq.attr('title');

		var $divInsideFullSizeChartDiv = $experiment_precursor_scan_count_chart_full_size_jq.find("div");
		
		//  Detect if full size chart already created
		if ( $divInsideFullSizeChartDiv.length === 0 ) {
		
			$divInsideFullSizeChartDiv = $("<div actual_full_size_chart_holder='true'></div>").appendTo( $experiment_precursor_scan_count_chart_full_size_jq );
			
			//////////////////////////////////

			// Full size chart created when the thumbnail is clicked the first time
			
			var optionsFullsize = {
					tooltip: {isHtml: true},

					title: 'Precursor M/Z distribution', // Title above chart

					//  X axis label below chart
					hAxis: { title: 'Precursor M/Z', titleTextStyle: {color: 'black'}
					},  
					//  Y axis label left of chart
					vAxis: { title: 'Scan Count', titleTextStyle: {color: 'black'}
								,baseline: 0                    // always start at zero
					},
					legend: { position: 'none' }, //  position: 'none':  Don't show legend of bar colors in upper right corner
					width:800, height:400,   // width and height of chart, otherwise controlled by enclosing div
					bar: { groupWidth: '100%' },  // set bar width large to eliminate space between bars
					colors: ['green']  //  Color of bars
			};        
			
			
			var chartFullsize = new google.visualization.ColumnChart( $divInsideFullSizeChartDiv[0] );
			chartFullsize.draw(data, optionsFullsize);
		}

		$chartThumbnailDiv.colorbox(
				{transition:"none", 
					title:title,
					returnFocus: false,  // If true, focus will be returned when Colorbox exits to the element it was launched from.
					open:	true, 		 // If true, Colorbox will immediately open.
					inline:true,         //  Use HTML element specified in href to display in colorbox
					href:$experiment_precursor_scan_count_chart_full_size_jq  //  Use HTML element specified in href to display in colorbox
				});
	});
};



/////////////////////////////////////

CreatePrecursorScanCountChartsClass.prototype.get$experimentDetailsDivFromExperimentId = function( experimentId ) {
	
	var html_id = "exp_root_target_" + experimentId;
	
	var $experimentDetailsDiv = $("#" + html_id );
	
	return $experimentDetailsDiv;
};





/////////////////////////////////////

CreatePrecursorScanCountChartsClass.prototype.isThumbnailAlreadyCreated = function( $experimentDetailsDiv ) {
	
	var $chartThumbnailDiv = $experimentDetailsDiv.find(".experiment_precursor_scan_count_chart_div_jq");

	var $divInsideThumbnailChartDiv = $chartThumbnailDiv.find("div");
	
	//  Detect if thumbnail chart already created
	if ( $divInsideThumbnailChartDiv.length !== 0 ) {
		
		return true;  
	}
	
	return false;
};




//  Declare CreatePrecursorScanCountCharts namespace by creating a variable 

var CreatePrecursorScanCountCharts = new CreatePrecursorScanCountChartsClass(); 


//assign to window
window.CreatePrecursorScanCountCharts = CreatePrecursorScanCountCharts;

