<%@ taglib uri="/WEB-INF/yrc-www.tld" prefix="yrcwww" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>


<yrcwww:notauthenticated>
 <logic:forward name="authenticate" />
</yrcwww:notauthenticated>

<logic:notPresent name="proteinInferenceFormAnalysis">
	<logic:forward name="newPercolatorProteinInference"/>
</logic:notPresent>
 
<%@ include file="/includes/header.jsp" %>

<%@ include file="/includes/errors.jsp" %>

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
		$(".toggle_selection").click(function() {
			toggleSelection($(this));
		});
		
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
	if(valid && floatVal < min)				valid = false;
	if(max && (valid && floatVal > max))	valid = false;
	return valid;
}
</script>

<yrcwww:contentbox title="Protein Inference*" centered="true" width="750">

 <CENTER>
 
<%@ include file="proteinInferenceFormAnalysis.jsp" %>


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
		document.location = "<yrcwww:link path='viewProject.do'/>?ID="+projectID;
	}
</script>


<%@ include file="/includes/footer.jsp" %>