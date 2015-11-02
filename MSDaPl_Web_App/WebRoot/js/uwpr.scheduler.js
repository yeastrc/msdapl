//You need an anonymous function to wrap around your function to avoid conflict
(function ($) {
	
	//Attach this new method to jQuery
	$.fn.extend ( {
		
		// plugin name - uwpr_scheduler
		uwpr_scheduler: function (opts, args) {
		
		 	//Set the default values, use comma to separate the setting
			var defaults = {
				instrumentId: 0,
				projectId: 0,
				eventJSONSourceUrl: null,
				onAddEventSuccessFn: null,
				eventDeleteUrl: null,
				eventEditUrl: null,
				onDeleteSuccessFn: null,
				projectLinkUrlFn: null,
				requestInformationFn: null,
				startTimes:null,
				endTimes:null,
				canAddEvents: false
			};
	
			if (opts && typeof(opts) == 'object') {
				opts = $.extend( {}, defaults, opts );
            }

			
			// alert(opts.onAddEventSuccessFn);
		
			//Iterate over the current set of matched elements
			return this.each (function () {
				
				new $.uwpr_scheduler( $(this), opts, args);
			});
		}
	});
	
	$.uwpr_scheduler = function (elem, options, args) {
		
		if (options && typeof(options) == 'string') {
           if (options == 'refresh') {
        	   // alert('refreshing');
               refreshCalendar(elem);
           }
           else if (options == 'updateEventSource') {
        	   // alert('Current event source: '+args[0]+";  New event source: "+args[1]+"; "+options);
        	   $(elem).fullCalendar( 'removeEventSource', args[0]);
        	   $(elem).fullCalendar( 'addEventSource', args[1]);
           }
           return;
       }

		if (!options.eventJSONSourceUrl) {
			alert("Did not find an event source url for the calendar. Calender initialization incomplete.");
			return;
		}
		
		if (options.timeBlocks) {
			var sortedTimeBlockKeys = [];
		 	for(var key in options.timeBlocks) {
		      	sortedTimeBlockKeys.push(key);
		  	}
		 	sortedTimeBlockKeys.sort();
		 	options.sortedTimeBlockKeys = sortedTimeBlockKeys;
		}
		
		initDialogDivs(elem);
		initCalendar(elem, options);
	}
	
	
	function initDialogDivs (div) {
		
		// create placeholder div for the error dialog
		var err_div = '<div id="dialog-error" style="display:none;">';
		err_div +=' <p style="ui-state-error">';
		err_div += ' <span class="ui-icon ui-icon-alert" style="float:left; margin:0 7px 50px 0;"></span>';
		err_div += ' <span id="error-message"></span>';
		err_div += ' </p></div>';
		div.after(err_div);
		
		// create placeholder div for alert message dialog
		var confirm_div = '<div id="dialog-confirm" style="display:none;">';
		confirm_div += ' <p><span class="ui-icon ui-icon-alert" style="float:left; margin:0 7px 20px 0;"></span>';
		confirm_div += 'Are you sure you want to delete the selected time block(s)?';
		confirm_div += ' </p></div>';
		div.after(confirm_div);
		
		// create a placeholder div for blocking dialog while data loads
		var block_div = '<div id="dialog-block-interaction" title="Sending Request" style="display:none;">';
		block_div += '<p>Please wait while your request is being processed</p></div>';
		div.after(block_div);
	}
	
	function initCalendar (calendar_div, options) {
		
		calendar_div.fullCalendar({
			header: {
				left: 'prev,next today',
				center: 'title',
				right: 'month,agendaWeek,agendaDay'
			},
			editable: true,
			slotMinutes: 60,
			allDaySlot: false,
			selectable: true,
			theme: true,
			year: options.year,
			month: options.month,
			// timeFormat: 'h(:mm)t',
			timeFormat: 'h(:mm)t{ - h(:mm)t}', // uppercase H for 24-hour clock
			
			
			events: {
		        url: options.eventJSONSourceUrl,
		        type: 'POST',
		        data: {
		            instrumentID: options.instrumentId,
		            projectID: options.projectId
		        },
		        error: function(jqXHR, textStatus, errorThrown) {
		        	// alert(textStatus);
		        	var obj;
	    			try { 
	     				obj = $.parseJSON(jqXHR.responseText); // An exception can be thrown if response is not JSON.
	     			}
	     			catch(e){
	     				showErrorDialog("There was an error processing the server's response. Please contact us.");
	     			}
	       					
	     			if(obj != undefined)
	     				showErrorDialog(obj.message);
		            else
		            	alert('there was an error while fetching events!');
		        }
		    },
		    
		    eventRender: function(event, element) {
				
				if(event.hasDetails) {
					
					// alert("Rendering event "+event.id);
					
					 var tooltipcolor = 'blue';
					 
					 var menudiv = "<div class='project_detail_tooltip'>";
					 var title = "";
					 
					 var hasLongTooltip = false;
					 
					 if(event.projectId) {
						 
					 	title += "<b>Project ID: ";
					 	if(options.projectLinkUrlFn) {
					 		title += "<a href="+options.projectLinkUrlFn(event.projectId)+">";
					 	}
					 	title += event.projectId;
					 	if(options.projectLinkUrlFn) {
					 		title+= "</a>";
					 	}
					 	title += "</b>";
					 	// title += "<br>";
					 	
					 	hasLongTooltip = true;
					 	
					 }
					 if(event.projectTitle) {
					 	menudiv += "<b>Title</b>: "+event.projectTitle+"";
					 	menudiv += "<br>";
					 }
					 if(event.projectPI) {
					 	menudiv += "<nobr><b>PI: "+event.projectPI+"</b></nobr>";
					 	menudiv += "<br>";
					 }
					 
					 if(event.blocks) {
						 
						 var event_blocks = event.blocks;
						 
						 var hasEditableBlocks = false;
						 
						 if(event_blocks.length > 1) {
							 
							 // menudiv += "<br>";
							 for (var i = 0; i < event_blocks.length; i++) {
								 
								var event_blk = event_blocks[i];
								menudiv += '<span style="font-size:8pt;color:black;">';
								if(event_blk.editable) {
									menudiv += '<input name="select_block_for_delete_'+event.id+'" type="checkbox" value="'+event_blk.id+'" ></input>';
									hasEditableBlocks = true;
								}
								// menudiv += event_blk.id+"&nbsp;";
								menudiv += '<nobr>'+event_blk.label+'</nobr>';
								menudiv += '</span><br>';
								
							 }
							 
							 if(hasEditableBlocks) {
								 menudiv += '<span class="select_all_blks_'+event.id+'" style="font-size:8pt;color:black;text-decoration:underline;cursor:pointer">';
								 menudiv += '[Select All]';
								 menudiv += '</span> &nbsp;';
							 }
						 }
						 else if(event_blocks.length == 1) {
							 
							var event_blk = event_blocks[0];
							menudiv += '<span style="font-size:8pt;color:black;">';
							menudiv += '<nobr>';
							menudiv += event_blk.label;
							
							if(event_blk.editable) {
								menudiv += '<input name="select_block_for_delete_'+event.id+'" type="checkbox" value="'+event_blk.id+'" checked="checked" style="visibility:hidden;"></input>';
								hasEditableBlocks = true;
							}
							           
							menudiv += '</nobr>';
							menudiv += '</span><br>';
						 }
					 }
					 
					 if(hasEditableBlocks) {
						 menudiv += "<span id='project_detail_tooltip_delete_"+event.id+"' style='color:red; font-weigt:bold; text-decoration:underline; cursor:pointer'>[Delete]</span>";
						 menudiv += "&nbsp;&nbsp;";
						 menudiv += "<span id='project_detail_tooltip_edit_"+event.id+"' style='color:red; font-weigt:bold; text-decoration:underline; cursor:pointer'>[Edit]</span>";
					 }
					 
					 menudiv += "</div>"
					 
					 $(element).qtip({
						content: {
						 	title: {
				            	text: title,
				            	button: 'X'
				         	},
						 	text: menudiv
					 	},
						style: { 
		      				name:tooltipcolor,
		      				width:"auto",
		      				title: {"font-size": "10pt"},
					 		button: {"font-size": "8pt"}
						},
						position: {
							corner: {
								target: 'bottomLeft',
								tooltip: 'topLeft'
							}
						},
						hide: false,
						show: { 
								when: 'click', // Show it on click
								solo: true // And hide all other tooltips
						},
						api: {
					        onRender:function() {
							
								// console.log("onRender called");
								$("#project_detail_tooltip_delete_"+event.id).click(function() {
									
									var selectedBlockIds = [];
									var i = 0;
									// var id_string = "";
									$("input[name='select_block_for_delete_"+event.id+"']:checked").each(function(){
										// id_string += ","+$(this).val();
										selectedBlockIds[i] = $(this).val();
										i++;
									});
									// id_string = id_string.substr(1);
									// alert("deleting "+selectedBlockIds)
									
									if(selectedBlockIds.length > 0) {
										// alert("Deleting");
										deleteTimeBlock(selectedBlockIds, calendar_div, options, event.projectId);
									}
									else {
										alert("No blocks were selected for deletion")
									}
									
									$(element).qtip("hide");
									var cboxes = $("input[name='select_block_for_delete_"+event.id+"']");
									if(cboxes.length > 1)
									{
										cboxes.attr("checked", "");
									}
									
								});
								
								$("#project_detail_tooltip_edit_"+event.id).click(function() {
									
									var selectedBlockIds = [];
									var i = 0;
									//var id_string = "";
									$("input[name='select_block_for_delete_"+event.id+"']").each(function(){
										//id_string += ","+$(this).val();
										selectedBlockIds[i] = $(this).val();
										i++;
									});
									//id_string = id_string.substr(1);
									// alert("editing "+selectedBlockIds)
									
									if(selectedBlockIds.length > 0) {
										// alert("Editing");
										editTimeBlock(selectedBlockIds, calendar_div, options, event.projectId, event.instrumentId);
									}
									else {
										alert("No blocks were selected for editing")
									}
									
									$(element).qtip("hide");
									$("input[name='select_block_for_delete_"+event.id+"']").attr("checked", "");
									
								});
								
								$(".select_all_blks_"+event.id).click(function() {
									
									var text = $(this).text();
									
									//alert(event.id+" "+text);
									if(text == "[Select All]") {
										$(this).text("[Deselect All]");
										$("input[name='select_block_for_delete_"+event.id+"']").attr("checked", "checked");
									}
									else {
										$(this).text("[Select All]");
										$("input[name='select_block_for_delete_"+event.id+"']").attr("checked", "");
									}
								});
					        }
					    }

					});
					 
				}
				 
			},
			
			dayClick: function (date, allDay, jsEvent, view) {
		   		
	   			if(!options.canAddEvents) {
	   				showErrorDialog("Cannot add events to this calendar");
	   				return;
	   			}
	   				
	   			//alert("day clicked "+$('#eventBubble').length);
	   			
	   			// add the div only if it does not already exist
	   			// hack to avoid the 'dayClick' function being called twice
	   			if($('#eventBubble').length > 0) {
	   				$('#eventBubble').remove();
	   			}
	   			
	   			var div = '<div id="eventBubble" title="Add Instrument Time"';
	   			div += ' style="padding:10px; font-size:12px; ">';
	   			div += '<div style="text-align:left">';
	   			
	   			
	   			// START date and time
	   			div += '<div style="padding:30px 0 0 0">';
	   			div += 'Start: <input type="text" class="uwpr_datepicker" id="uwpr_datepicker_start"></input>';
	   			div += '<span>&nbsp;</span> ';
	   			div += '<select id="startTimeSelectorList" onchange="uwpr_scheduler_updateEndTime()"> ';
	   			var stimes = options.startTimes;
	   			for (var i = 0; i < stimes.length; i++) {
	   				var stime = stimes[i];
	   				div += '<option value="'+stime.value+'" ';
	   				if(stime.selected) 
	   					div += ' selected="selected" ';
	   				div += '>';
	   				div += stime.display;
	   				div +='</option> ';
	   			}
				div += '</select> ';
	   			div += '</div>';
	   			
	   			// END date and time
	   			div += '<div style="padding:10px 0 0 0">';
	   			div += 'End:&nbsp;&nbsp; <input type="text" class="uwpr_datepicker" id="uwpr_datepicker_end"></input>';
	   			div += '<span>&nbsp;</span> ';
	   			div += '<select id="endTimeSelectorList"> ';
	   			var etimes = options.endTimes;
	   			for (var i = 0; i < etimes.length; i++) {
	   				var etime = etimes[i];
	   				div += '<option value="'+etime.value+'" ';
	   				if(etime.selected) 
	   					div += ' selected="selected" ';
	   				div += '>';
	   				div += etime.display;
	   				div +='</option> ';
	   			}
				//div += '<option value="0">12:00 am</option> ';
				//for( var i = 1; i <= 11; i++) {
				//	
				//	div += '<option value="'+i+'">'+i+':00 am</option> ';
				//}
				//div += '<option value="12">12:00 pm</option> ';
				//for( var i = 1; i <= 11; i++) {
				//	if(i == 5)
				//		div += '<option value="'+(12+i)+'" selected="selected">'+(i)+':00 pm</option> ';
				//	else
				//		div += '<option value="'+(12+i)+'">'+i+':00 pm</option> ';
				//}
				div += '</select> ';
	   			div += '</div>';
	   			
	   			// REPEAT checkbox
	   			div += '<div id="repeat_checkbox_datepicker" style="margin: 10px;">';
	   			div += 'Repeat Daily <input type="checkbox" id="repeat_checkbox"/>';
	   			div += '&nbsp; &nbsp; &nbsp; End Date: ';
	   			div += '<input type="text" class="uwpr_datepicker" id="uwpr_datepicker_repeat"></input>'
	   			div += '</div>';
	   			
	   			// Add and Cancel buttons
	   			div += '<div style="text-align: center; font-size:12px; margin-top:20px;">';
	   			// Add button
	   			div += '<button id="addEvent" role="button" aria-disable="false" class="ui-button ui-widget ui-state-default ui-corner-all ui-button-text-only">';
	   			div += '<span class="ui-button-text">Add</span>';
	   			div += '</button>'
	   			div += '&nbsp;&nbsp;';
	   			
	   			// Cancel button
	   			div += '<button id="cancel" role="button" aria-disable="false" class="ui-button ui-widget ui-state-default ui-corner-all ui-button-text-only">';
	   			div += '<span class="ui-button-text">Cancel</span>';
	   			div += '</button>'
	   			div += '</div>';
			
	   			div += '</div>';
	   			div += '</div>';
	   			$(this).append(div);
	   			
				
	   			//console.log("clicked: Y "+jsEvent.pageY+"; scroll top: "+$(document).scrollTop()+"; clicked X: "+jsEvent.pageX+"; outerWidth: "+$(this).outerWidth());
	   			$('#eventBubble').dialog(
   					{ modal:true, 
   					  height:300,
   					  width:400
   					});
	   			
	   			// add the datepicker
	   			// add this after calling .dialog(), 
	   			// otherwise, the calendar is displayed when the dialog is initialized
	   			$(".uwpr_datepicker").datepicker( {
	   				defaultDate:date
	   			});
	   			$("#uwpr_datepicker_start").datepicker( "setDate" , date );
	   			$("#uwpr_datepicker_end").datepicker( "setDate" , date );
	   			
	   			
	       		//$('#eventBubble').css({ left: jsEvent.pageX, top: jsEvent.pageY }).show().fadeIn();
	       		
	       		$('#addEvent').click(function() {
	       		
	       			var information = getRequestInformation();
	       			
	       			if(information.errorMessage) {
	       				$('#eventBubble').remove();
	       				showErrorDialog(information.errorMessage);
	       				return;
	       			}
	       			
	       			
	       			// get the selected start date and time
	       			var selectedStartDate = $("#uwpr_datepicker_start").val();
	       			var selectedStartTime = $("#startTimeSelectorList option:selected").val();
	       			// get the selected end date and time
	       			var selectedEndDate = $("#uwpr_datepicker_end").val();
	       			var selectedEndTime = $("#endTimeSelectorList option:selected").val();
	       			
	       			
	       			var repeat = $("#repeat_checkbox").is(":checked") ? "true" : "false";
	       			//alert("repeat is "+repeat+"; date "+$("#uwpr_datepicker_repeat").val());
	       			
	       			
	       			$.ajax({
	       				url:information.requestUrl,
	       				cache: false,
	       				dataType: "json",
	       				data: {"projectId":options.projectId,
	       				       "instrumentId": options.instrumentId,
	       				       "paymentMethodId1": information.paymentMethodId1,
	       				       "paymentMethod1Percent": information.paymentMethod1Perc,
	       				       "paymentMethodId2": information.paymentMethodId2,
	       				       "paymentMethod2Percent": information.paymentMethod2Perc,
	       				       "startDate": selectedStartDate,
	       				       "startTime": selectedStartTime,
	       				       "endDate": selectedEndDate,
	       				       "endTime": selectedEndTime,
	       				       "repeatdaily": repeat,
	       				       "repeatenddate":$("#uwpr_datepicker_repeat").val()
	       				       },
	       				       
	       				beforeSend: function(jqXHR, settings) {
	       				
	       					
	       					$( "#dialog-block-interaction" ).dialog({
								modal: true,
								draggable:false,
								resizable:false,
								closeOnEscape: false,
								open: function(event, ui) { 
									$(".ui-dialog-titlebar-close", ui.dialog).hide()
	 							} // hide the title bar with the "close" icon
								
							});
	       					
							$('#eventBubble').remove();
							
	       				},
	       				success: function(data, textStatus, jqXHR){
	       					
	       					// alert("success: "+textStatus+"; data: "+data);
	       					
	       					var obj;
	       					try {
	       						obj = $.parseJSON(jqXHR.responseText); // An exception can be thrown if response is not JSON.
	       					}
	       					catch(e) {
	       						showErrorDialog("There was an error processing the server's response. Please contact us.");
	       					}
	       					
	       					// alert(obj);
	       					
	       					refreshCalendar(calendar_div);
	       					
	       					$( "#dialog-block-interaction" ).dialog( "close" );
	       					
	       					if(options.onAddEventSuccessFn && obj != undefined) {
	       						options.onAddEventSuccessFn(obj);
	       					}
	       					
	       				},
	       				error: function(jqXHR, textStatus, errorThrown) {
	       					var obj;
	       					try { 
	       						obj = $.parseJSON(jqXHR.responseText); // An exception can be thrown if response is not JSON.
	       					}
	       					catch(e){
	       						showErrorDialog("There was an error processing the server's response. Please contact us.");
	       					}
	       					
	       					
	       					$( "#dialog-block-interaction" ).dialog( "close" );
	       					
	       					refreshCalendar(calendar_div);
	       					
	       					if(obj != undefined)
	       						showErrorDialog(obj.message);
	       						
	       					
	       					
	       				},
						complete: function(jqXHR, textStatus) {

						}
	       			});
					
				});
				
				$('#cancel').click(function() {
					$('#eventBubble').remove();
				});
				
	   		}
		});
	}
	

	function deleteTimeBlock(selectedBlockIds, calendar_div, options, projectId) {

		// alert("project is: "+projectId);
		
		//if(!confirm("Are you sure you want to delete this time block?")) {
		//	return;
		//}
		
		
		var eventIdString = "";
		for(var i = 0; i < selectedBlockIds.length; i++) {
			eventIdString += ","+selectedBlockIds[i];
		}
		if(eventIdString.length > 0) {
			eventIdString = eventIdString.substr(1);
		}
		//alert(eventIdString);
		
		$( "#dialog-confirm" ).dialog({
			resizable: false,
			modal: true,
			buttons: {
				"Yes": function() {
						$.ajax({
				   		url: options.eventDeleteUrl,
				   		cache: false,
				   		dataType: "text",
				   		data: {"usageBlockIds": eventIdString,
				   		       "projectId": projectId
				   		       },
				   		success: function(data, textStatus, jqXHR){
				  					//$('#eventBubble').remove();
				  					refreshCalendar(calendar_div);
				  					if(options.onDeleteSuccessFn) {
				  						options.onDeleteSuccessFn(selectedBlockIds);
				  					}
				  					//alert("Deleted");
				  				},
						error: function(jqXHR, textStatus, errorThrown) {
						
				  			refreshCalendar(calendar_div);
	       					showErrorDialog(jqXHR.responseText);
						}
		   				});
		   				
						$( this ).dialog( "close" );
				},
				Cancel: function() {
					$( this ).dialog( "close" );
				}
			}
		});
	}
	
	function editTimeBlock(selectedBlockIds, calendar_div, options, projectId, instrumentId) {

		var eventIdString = "";
		for(var i = 0; i < selectedBlockIds.length; i++) {
			eventIdString += ","+selectedBlockIds[i];
		}
		if(eventIdString.length > 0) {
			eventIdString = eventIdString.substr(1);
		}
		var url = options.eventEditUrl+"?projectId="+projectId+"&instrumentId="+instrumentId+"&usageBlockIds="+eventIdString;
		// alert(url);
		window.location = url;
	}
	
	function showErrorDialog(errorMessage, errTitle) {

		var title = errTitle;
		if(title == undefined)
			title="Request Failed";
		$( "#error-message" ).text(errorMessage);
	   	$( "#dialog-error" ).dialog({
				modal: true,
				draggable:false,
				resizable:false,
				title:title,
				buttons: {
				Ok: function() {
					$( this ).dialog( "close" );
					}
				}
		});
	}
	
	function refreshCalendar(div) {
		
		// remove all tooltips (TODO there must be a better way to do this)
		// Old tooltip div's will prevent any link on the new tooltips to be functional
		// because the div IDs of the new ones will be the same as the old ones
		$(".project_detail_tooltip").remove();
		$(div).fullCalendar( 'refetchEvents' );
	}

//pass jQuery to the function,
//So that we will able to use any valid Javascript variable name
//to replace "$" SIGN. But, we'll stick to $ (I like dollar sign: ) ) 
})(jQuery);


function disableRepeatDatepicker() {
	
	//$("#repeat_checkbox").attr("checked", ""); // .attr("disabled", true);
	//$("#datepicker").val(""); // .attr("disabled", true);
	//$("#repeat_checkbox_datepicker").hide();
	
}
function enableRepeatDatepicker() {
	
	//$("#repeat_checkbox").removeAttr("disabled");
	//$("#datepicker").removeAttr("disabled");
	//$("#repeat_checkbox_datepicker").show();
	
}

function uwpr_scheduler_updateEndTime(timeBlockId, block_duration_hour) {
	
	// Get the selected start time
	//var selectedStartTime = $("#startTimeSelectorList_"+timeBlockId+" option:selected").val();
}
