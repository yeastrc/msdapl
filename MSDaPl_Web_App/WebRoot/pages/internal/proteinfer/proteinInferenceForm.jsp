<%@ taglib uri="/WEB-INF/yrc-www.tld" prefix="yrcwww" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>


<yrcwww:notauthenticated>
 <logic:forward name="authenticate" />
</yrcwww:notauthenticated>

 
<%@ include file="/includes/header.jsp" %>

<%@ include file="/includes/errors.jsp" %>


<link rel="stylesheet" href="<yrcwww:link path='css/proteinfer.css'/>" type="text/css" >
<script type="text/javascript">

// ---------------------------------------------------------------------------------------
// AJAX DEFAULTS
// ---------------------------------------------------------------------------------------
  $.ajaxSetup({
  	type: 'POST',
  	timeout: 30000,
  	dataType: 'html',
  	error: function(xhr) {
  				var statusCode = xhr.status;
		  		// status code returned if user is not logged in
		  		// reloading this page will redirect to the login page
		  		if(statusCode == 303)
 					window.location.reload();
 				
 				// otherwise just display an alert
 				else {
 					alert("Request Failed: "+statusCode+"\n"+xhr.statusText);
 				}
  			}
  });
  
  $.blockUI.defaults.message = '<b>Loading...</b>'; 
  $.blockUI.defaults.css.padding = 20;
  //$().ajaxStart($.blockUI).ajaxStop($.unblockUI);
  $().ajaxStop($.unblockUI);
  
  
	$(document).ready(function(){
		
		$("#searchopt").click(function() {
			$("#inputType_search").show();
			$("#inputType_analysis").hide();
		});
		
		$("#analysisopt").click(function() {
			$("#inputType_search").hide();
			$("#inputType_analysis").show();
		});
		
		$(".toggle_selection").click(function() {
			toggleSelection($(this));
		});
		
		//$(".foldable").click(function() {
		//	fold($(this));
		//});
		
	});

function toggleSelection(button) {
	var id = button.attr("id");
	var idstr = id+"_file";
	if(button.text() == "Deselect All") {
		$("input[id='"+idstr+"']").attr("checked", "");
		button.text("Select All");
	}
	else if(button.text() == "Select All") {
		$("input[id='"+idstr+"']").attr("checked", "checked");
		button.text("Deselect All");
	}
}

function validateInt(value, fieldName, min, max) {
	var intVal = parseInt(value);
	var valid = true;
	if(isNaN(intVal))						valid = false;
	if(valid && intVal < min)				valid = false;
	if(max && (valid && intVal > max))		valid = false;
	return valid;
}
function validateFloat(value, fieldName, min, max) {
	var floatVal = parseFloat(value);
	var valid = true;
	if(isNaN(floatVal))						valid = false;
	if(valid && floatVal < min)			valid = false;
	if(max && (valid && floatVal > max))	valid = false;
	return valid;
}
</script>



<yrcwww:contentbox title="Protein Inference*" centered="true" width="750" scheme="pinfer">

 <CENTER>
 
 
 <div align="center" style="color:black;">
	<b>Select Input Type: </b> 
	<logic:equal name="useSearchInput" value="true">
		<input type="radio" name="inputSelector" value="Search" checked id="searchopt" >Search
		<input type="radio" name="inputSelector" value="Search" id="analysisopt"> Analysis
	</logic:equal>
	
	<logic:equal name="useSearchInput" value="false">
		<input type="radio" name="inputSelector" value="Search" id="searchopt" >Search
		<input type="radio" name="inputSelector" value="Search" checked id="analysisopt"> Analysis
	</logic:equal>
	
 </div>
 <br>
 
<!-- Form when using search results -->
<logic:present name="proteinInferenceFormSearch">
<div id="inputType_search">
</div>
</logic:present>
<logic:notPresent name="proteinInferenceFormSearch">
	<logic:equal name="useSearchInput" value="true">
		<div style="margin-top: 30; margin-bottom: 30;" id="inputType_search">No valid search input found for running Protein Inference.</div>
	</logic:equal>
	<logic:equal name="useSearchInput" value="false">
		<div style="margin-top: 30; margin-bottom: 30; display: none;" id="inputType_search">No valid search input found for running Protein Inference.</div>
	</logic:equal>
</logic:notPresent>

 
<!-- Form when using search analysis results -->
<logic:present name="proteinInferenceFormAnalysis">
<logic:equal name="useSearchInput" value="true">
 	<div id="inputType_analysis" style="display: none;">
		<%@include file="proteinInferenceFormAnalysis.jsp" %>
	</div>
</logic:equal>
<logic:equal name="useSearchInput" value="false">
 	<div id="inputType_analysis">
		<%@include file="proteinInferenceFormAnalysis.jsp" %>
	</div>
</logic:equal>
</logic:present>
<logic:notPresent name="proteinInferenceFormAnalysis">
<logic:equal name="useSearchInput" value="true">
	<div style="margin-top: 30; margin-bottom: 30; display: none;" id="inputType_analysis" >
		No valid analysis input found for running Protein Inference.
	</div>
</logic:equal>
<logic:equal name="useSearchInput" value="false">
	<div style="margin-top: 30; margin-bottom: 30;" id="inputType_analysis" >
		No valid analysis input found for running Protein Inference.
	</div>
</logic:equal>
</logic:notPresent>


<div style="font-size: 8pt;margin-top: 3px;">
 	*This protein inference program is based on the IDPicker algorithm published in:<br>
 	<i>Proteomic Parsimony through Bipartite Graph Analysis Improves Accuracy and Transparency.</i>
 	<br>
	Tabb <i>et. al.</i> <i>J Proteome Res.</i> 2007 Sep;6(9):3549-57
</div>
 	
</CENTER>
</yrcwww:contentbox>

<script type="text/javascript">
	function onCancel(projectID) {
		alert("hello");
		document.location = "<yrcwww:link path='viewProject.do' />?ID="+projectID;
	}
</script>


<%@ include file="/includes/footer.jsp" %>