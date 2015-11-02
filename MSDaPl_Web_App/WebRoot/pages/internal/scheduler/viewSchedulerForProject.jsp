<%@ taglib uri="/WEB-INF/yrc-www.tld" prefix="yrcwww" %>

<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>

<%@ include file="/includes/header.jsp" %>
<%@ include file="/includes/errors.jsp" %>

<logic:notPresent name="paymentMethods">
  <logic:forward name="viewScheduler" />
</logic:notPresent>


<!-- <link rel='stylesheet' type='text/css' href='css/jquery_ui/cupertino/jquery-ui-1.8.13.custom.css' /> -->
<link rel='stylesheet' type='text/css' href='<yrcwww:link path="/css/jquery_ui/ui-lightness/jquery-ui-1.8.12.custom.css"/>' />
<link rel='stylesheet' type='text/css' href='<yrcwww:link path="/css/fullcalendar.css"/>' />

<script type='text/javascript' src="<yrcwww:link path='/js/jquery-1.5.min.js'/>" ></script>
<script type='text/javascript' src="<yrcwww:link path='/js/jquery-ui-1.8.12.custom.min.js'/>" ></script>
<script type='text/javascript' src="<yrcwww:link path='/js/fullcalendar.min.js'/>"></script>
<script type='text/javascript' src="<yrcwww:link path='/js/jquery.qtip-1.0.0-rc3.min.js'/>" ></script>
<script type='text/javascript' src="<yrcwww:link path='/js/uwpr.scheduler.js'/>" ></script>

<style type='text/css'>

	#calendar {
		width: 900px;
		margin: 0 auto;
		}
		
	
	#scheduledTimeDiv table th
	{
		font-size:8pt;
		font-weight:bold;
	}
	#scheduledTimeDiv table td
	{
		font-size:8pt;
		font-weight:bold;
	}

</style>

<script type='text/javascript'>


$(document).ready(function() {

	initCalendar();
	// select the correct instrument in the drop-down menu
	$("#instrumentSelector").val(<bean:write name="instrumentId"/>);
	
	// select the correct project in the drop-down menu
	// project selector is displayed only for site admins.
	$("#projectSelector").val(<bean:write name="projectId"/>);
	
	// If we have only one payment method for this project select it now
	// We will have 2 <option> elements if there is only 1 payment method
	//alert($('select#paymentMethodSelector_1 option').length);
	if($('select#paymentMethodSelector_1 option').length == 2) {
		
		$('select#paymentMethodSelector_1 option:last').attr("selected", "selected");
	}
	// in case this is visible hide it and reset percent of first payment method to 100%
	hideSecondPaymentMethod();
	
	
});


function getEventSourceUrl() {
	return "<yrcwww:link path='/instrumentUsageBlocks.do'/>";
}
function getProjectUrl(projectId) {
	return "<yrcwww:link path='/viewProject.do'/>?ID="+projectId;
}
function getDeleteTimeBlockUrl() {
	return "<yrcwww:link path='/deleteInstrumentTimeAjax.do'/>";
}
function getEditTimeBlockUrl() {
	return "<yrcwww:link path='/viewEditInstrumentTimeForm.do'/>";
}
function getProjectId() {
	return <bean:write name="projectId"/>;
}
function getInstrumentId() {
	return <bean:write name="instrumentId"/>;
}

function getRequestInformation() {
	
	var information = {};
	information.requestUrl = "<yrcwww:link path='/requestInstrumentTimeAjax.do'/>";
	information.projectId = getProjectId();
	information.instrumentId = getInstrumentId();
	information.hasPaymentMethodInfo = true;
	information.paymentMethodId1 = $("#paymentMethodSelector_1 :selected").val();
	information.paymentMethod1Perc = $("#paymentMethodPercent_1").val();
    // check if there is a second payment method
	information.paymentMethodId2 = $("#paymentMethodSelector_2 :selected").val();
	information.paymentMethod2Perc = $("#paymentMethodPercent_2").val();
    
    
    if (information.paymentMethodId1 == 0) {
    	information.errorMessage = "Please select a payment method.";
    	return information;
    }
     			
	if (information.paymentMethodId1 === information.paymentMethodId2) {
		information.errorMessage = "Selected payments methods are the same.";
		return information;
	}
	
	if (information.paymentMethodId2 !== 0 && information.paymentMethod2Perc === 0.0) {
		information.errorMessage = "Percent entered for the second payment method should be greater than 0.";
		return information;
	}
       						
     return information;		
}

function getStartTimes() {

	return getAllTimes(9); // select 9am
}

function getEndTimes() {

	return getAllTimes(17); // select 5pm
}

function getAllTimes(select) {

	var times = [];
	
	times[0] = { value: 0,display:  "12:00 am", selected:false };
	for(var i = 1; i <= 11; i++) {
		if(i == select) {
			times[i] = { value: i,display:  i+":00 am", selected:true };
		}
		else {
			times[i] = { value: i,display:  i+":00 am", selected:false };
		}
	}
	times[12] = { value: 12,display:  "12:00 pm", selected:false };
	for(var i = 13; i <= 23; i++) {
		if(i == select) {
			times[i] = { value: i,display:  (i-12)+":00 pm", selected:true };
		}
		else {
			times[i] = { value: i,display:  (i-12)+":00 pm", selected:false };
		}
	}
	
	return times;
}

function initCalendar() {

	var date = new Date();
	// var d = date.getDate();
	var m = date.getMonth();
	var y = date.getFullYear();
	
	$('#calendar').uwpr_scheduler({
			instrumentId: getInstrumentId(),
			projectId: getProjectId(),
			eventJSONSourceUrl: getEventSourceUrl(),
			onAddEventSuccessFn: addToScheduledTimeTable,
			eventDeleteUrl: getDeleteTimeBlockUrl(),
			eventEditUrl: getEditTimeBlockUrl(),
			onDeleteSuccessFn: deleteFromScheduledTimeTable,
			projectLinkUrlFn: getProjectUrl,
			requestInformationFn: getRequestInformation,
			startTimes: getStartTimes(),
			endTimes: getEndTimes(),
			canAddEvents: true,
			<logic:present name="year">
				year: <bean:write name="year"/>,
				month: <bean:write name="month"/>
			</logic:present>
			<logic:notPresent name="year">
				year: y,
				month: m
			</logic:notPresent>
	});
}

function refreshCalendar() {

	$('#calendar').uwpr_scheduler('refresh');
}



function deleteFromScheduledTimeTable(usageBlockIds) {
	
	for(var i = 0; i < usageBlockIds.length; i++) {
	
		var usageBlockId = usageBlockIds[i];
		
		//alert("I am deleting: "+usageBlockId);
		
		var row = $("#usage_block_"+usageBlockId);
		if(row == undefined || row.length == 0) {
			//alert("You are removing a time block that was not added in the current session. \"Time Scheduled\" table will not be updated.");
		}
		else {
			//alert("removing from table");
			var fee = $("#fee_"+usageBlockId).text();
			deleteCost(fee.replace("$",""));
			row.remove();
		}
	}
}

function addToScheduledTimeTable (jsonobj) {

	
	$("#scheduledTimeDiv").show();
	var blocks = jsonobj.blocks;
	
	var totalcost = 0.0;
	for (var i = 0; i < blocks.length; i++) {
	
		// alert(block.id);
		var block = blocks[i];
		var row = "<tr id='usage_block_"+block.id+"'>";
		row += "<td style='display:none;'>"+block.id+"</td>";
		row += "<td class='ui-state-default'>"+block.start_date+"</td>";
		row += "<td class='ui-state-default'>"+block.end_date+"</td>";
		row += "<td class='ui-state-default' id='fee_"+block.id+"'>$"+block.fee+"</td>";
		row += "</tr>";
		$("#scheduledTimeDiv table > tbody:last").append(row);
		
		totalcost += parseFloat(block.fee);
		// alert(block.fee+" total: "+totalcost);
		//console.log("adding usage "+$("#scheduledTimeDiv table > tbody:last"));
	}
	
	addCost(totalcost);
}

function addCost(addThis) {

	var currentCost = $("#totalCost").text();
	//alert(currentCost);
	var newCost = parseFloat(currentCost) + parseFloat(addThis);
	
	$("#totalCost").text((newCost).toFixed(2));
	
}

function deleteCost(deleteThis) {

	var currentCost = $("#totalCost").text();
	//console.log(deleteThis);
	var newCost = parseFloat(currentCost) - parseFloat(deleteThis);
	
	$("#totalCost").text((newCost).toFixed(2));
}

function switchInstrument(projectId) {
	var instrumentId = $("#instrumentSelector :selected").val();
	//alert("instrumentID: "+instrumentId+"\nURL:"+"/pr/viewScheduler.do?instrumentId="+instrumentId+"&projectId="+projectId);
	document.location.href="<yrcwww:link path='/viewScheduler.do'/>?instrumentId="+instrumentId+"&projectId="+projectId;
}

function switchProject() {
	var projectId = $("#projectSelector :selected").val();
	var instrumentId = $("#instrumentSelector :selected").val();
	// alert("instrumentID: "+instrumentId+"\nURL:"+"/pr/viewScheduler.do?instrumentId="+instrumentId+"&projectId="+projectId);
	document.location.href="<yrcwww:link path='/viewScheduler.do'/>?instrumentId="+instrumentId+"&projectId="+projectId;
}


function showSecondPaymentMethod() {

	// show the second payment row
	$("#secondPaymentMethodRow").show();
	// hide the link to add a second payment
	$("#addPaymentMethodLinkRow").hide();
	
	// make the percent field for the first payment method editable
	$("#paymentMethodPercent_1").removeAttr("disabled"); 
	
}

function hideSecondPaymentMethod() {
	$("#paymentMethodPercent_1").val(100); // reset percent for payment method 1 to 100%
	$("#paymentMethodPercent_2").val(0); // set the percent for payment method 2 to 0%
	
	// for payment method 1 make the percent field not editable
	$("#paymentMethodPercent_1").attr("disabled", "disabled");
	
	$("#paymentMethodSelector_2").val(0); // deselect the payment method
	
	// hide the second payment row
	$("#secondPaymentMethodRow").hide();
	// show the link to add a second payment
	$("#addPaymentMethodLinkRow").show();
}

function updatePercent() {
	
	if($("#paymentMethodPercent_1").val() == "")
		return;
	var percent1 = parseFloat($("#paymentMethodPercent_1").val());
	if(isNaN(percent1)) {
		alert("Invalid number entered in the percent field. Please enter a number between 0 and 100");
	}
	if(percent1 > 100.0)
		percent1 = 100.0;
	if(percent1 < 0.0)
		percent1 = 0;
	var percent2 = 100.0 - percent1;
	$("#paymentMethodPercent_2").val(Math.round(percent2*100.0)/100.0);
	$("#paymentMethodPercent_1").val(Math.round(percent1*100.0)/100.0);
}

</script>

<yrcwww:contentbox title="Schedule Instrument Time" width="90" widthRel="true">
<center>

<div class="ui-state-default">

<div style="margin:5px 0 0 0; text-align:center;">
Project ID: <bean:write name="projectId"/>
<span style="text-decoration:underline; font-size:8pt;">
	<html:link action="viewProject.do" paramId="ID" paramName="projectId"> (back to project)</html:link>
</span>
</div>

<!-- Instrument Selector -->
<div style="margin:20px 0 20px 0; text-align:center;">
Select Instrument:
<select id="instrumentSelector" onchange='switchInstrument(<bean:write name="projectId" />)'>
<logic:iterate name="instruments" id="instrument">
	<logic:equal name="instrument" property="active" value="true">
		<option value='<bean:write name="instrument" property="ID" />'><bean:write name="instrument" property="name" /></option>
	</logic:equal>
</logic:iterate>

</select>
</div>

<!-- Project selector -->
<logic:present name="projects">
	<div style="margin:20px 0 20px 0; text-align:center;">
	Select Project:
	<select id="projectSelector" onchange='switchProject()'>
		<logic:iterate name="projects" id="project">
			<option value='<bean:write name="project" property="ID" />'><bean:write name="project" property="label" /></option>
		</logic:iterate>
	</select>
	</div>
</logic:present>

<!-- Payment Selector -->
<logic:empty name="paymentMethods">
	<div style="color:red;margin:10px 0px 10px 0px;">
            There are no payment methods associated with this project.
            <br/>
            In order to schedule instrument time you must have at least one payment method.
            <br/>  
            Click <html:link action="newPaymentMethod.do" paramId="projectId" paramName="project" paramProperty="ID">here</html:link>
            to add a payment method for this project.
    </div>
</logic:empty>

<logic:notEmpty name="paymentMethods">

<div style="margin:20px 0 20px 0; text-align:center;">
Select Payment Method(s):
<!-- User is allowed to use up to two payment methods -->
<table align="center">
<tr>
<td>
UW Budget # / PO Number:
<select id="paymentMethodSelector_1">
<option value='0'>Select</option>
<logic:iterate name="paymentMethods" id="paymentMethod">
	<option value='<bean:write name="paymentMethod" property="id" />'><bean:write name="paymentMethod" property="displayString" /></option>
</logic:iterate>
</select>
</td>
<td>
<input id="paymentMethodPercent_1" type="text" value="100" size="4" onkeyup="updatePercent()" disabled="disabled" />%
</td>
<td></td>
</tr>

<tr id="addPaymentMethodLinkRow">
	<td colspan="3" align="center">
		<a href="#" onclick="showSecondPaymentMethod(); return false;" style="color:red;font-size:8pt;text-decoration:underline;">[Add a payment method]</a>
	</td>
</tr>

<tr id="secondPaymentMethodRow" style="display:none;">
<td>
UW Budget # / PO Number:
<select id="paymentMethodSelector_2">
<option value='0'>Select</option>

<logic:iterate name="paymentMethods" id="paymentMethod">
	<option value='<bean:write name="paymentMethod" property="id" />'><bean:write name="paymentMethod" property="displayString" /></option>
</logic:iterate>
</select>
</td>
<td>
<input id="paymentMethodPercent_2" type="text" value="0" disabled="disabled" size="4" />%
</td>
<td>
	<a href="#" onclick="hideSecondPaymentMethod(); return false;" style="color:red;font-size:8pt;text-decoration:underline;">[Remove]</a>
</td>
</tr>

</table>
</div>
</div>
</logic:notEmpty>


<!-- Calendar and Scheduled Time table -->
<table style="width:1000px;">
<tr>
<td width="80%" valign="top">
<div id='calendar' style="width:800px;" align="left"></div>
</td>
<td valign="top" style="width:200px;" class="ui-state-default" style="background: none repeat scroll 0 0 transparent;padding:10px;">
	<div id="scheduledTimeDiv" >
		<nobr>Time Scheduled:</nobr>
		<table>
			<thead>
				<tr>
					<th style="display:none;">ID</th>
					<th class="ui-state-default">Start</th>
					<th class="ui-state-default">End</th>
					<th class="ui-state-default">Cost</th>
				</tr>
			</thead>
			<tbody style="color:black;">
			</tbody>
		</table>
		<div style="margin-top:10px;">
			<nobr>Total Cost: <span style="color:red;">$</span><span id="totalCost" style="color:red;">0</span></nobr>
		</div>
		<div style="margin-top:10px;color:black;font-size:8pt;font-weight:normal;">
			Click <html:link action="viewScheduledTimeDetails.do" paramId="projectId" paramName="projectId"><b>here</b></html:link> to view <b>all</b> the time scheduled for this project.
		</div>
		
	</div>
</td>
</tr>
</table>
</center>
</yrcwww:contentbox>

<%@ include file="/includes/footer.jsp"%>