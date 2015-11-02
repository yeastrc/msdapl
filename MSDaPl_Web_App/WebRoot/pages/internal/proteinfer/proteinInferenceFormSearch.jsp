<%@page import="org.yeastrc.www.proteinfer.job.ProteinInferenceForm"%>
<%@page import="org.yeastrc.ms.domain.protinfer.ProteinInferenceProgram"%>
<%@page import="org.yeastrc.ms.domain.protinfer.ProgramParam"%>
<%@page import="org.yeastrc.ms.domain.protinfer.ProgramParam.TYPE"%>
<%@page import="org.yeastrc.ms.domain.protinfer.ProgramParam.DoubleValidator"%>
<%@page import="org.yeastrc.ms.domain.protinfer.ProgramParam.IntegerValidator"%>
<%@ taglib uri="/WEB-INF/yrc-www.tld" prefix="yrcwww" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>

<bean:define name="proteinInferenceFormSearch" property="inputSummary.inputGroupId" id="searchIdInt" 
    			scope="request"/>
<bean:define name="proteinInferenceFormSearch" property="inputSummary.programName" id="searchProgram" scope="request"/>

<script type="text/javascript">

var currentSearchIds = [<%=searchIdInt%>];
var searchProgram = '<%=searchProgram%>';

$(document).ready(function(){
	$("#addSearchesButton").click(function() {
		requestSearchList();
		return false;
	});
});

function requestSearchList() {
	
	var haveSearches = "";
	for (var i = 0; i < currentSearchIds.length; i+= 1) {
		if (i > 0)
			haveSearches += ",";
		haveSearches += currentSearchIds[i];
	}
	var winHeight = 500
	var winWidth = 700;
	var doc = <yrcwww:link path='/listInputGroups.do?'/>+"excludeInputGroups="+haveSearches+"&inputGenerator="+searchProgram;
	//alert(doc);
	window.open(doc, "ADD_PROTINFER_INPUT", "width=" + winWidth + ",height=" + winHeight + ",status=no,resizable=yes,scrollbars=yes");
}

function addSearches(selectedSearches) {
	
	if(selectedSearches.length > 0) {
		var selected = selectedSearches.split(",");
		for(i = 0; i < selected.length; i++) {
			currentSearchIds[currentSearchIds.length] = selected[i];
			$("#searchInputList").append("<br><div id=search_"+selected[i]+"></div")
		}
		var currentInputCount = 0;
		for(i = 0; i < currentSearchIds.length; i++) {
			currentInputCount += $("input[id='toggle_search_"+currentSearchIds[i]+"_file']").length;
		}
		//alert("current input count: "+currentInputCount);
		
		$.ajax({
  			type: "GET",
  			url: "getInputList.do",
  			dataType: "html",
  			data: "inputIds="+selectedSearches+"&inputType=S&index="+currentInputCount,
  			beforeSend: function(xhr) {
  							$.blockUI(); 
  						},
  			success: function(html) {
  			
  				$("#searchInputList").append(html);
  				
  				// enable the file selection toggle and the link to fold the file list
  				for(i = 0; i < selected.length; i++) {
  					var id = "search_"+selected[0];
  					$("#foldable_"+id).click(function() {
						fold($(this));
					});
					
  					$("#toggle_"+id).click(function() {
						toggleSelection($(this));
					});
  				}
  			}
		});
	}
}

// VALIDATE FORM PARAMETERS  
function validateFormForSearchInput() {
	
	
	// first make sure that at least one file is selected
	if($("input:checked[id^='toggle_search']").size() == 0) {
		alert("Please select at least one file");
		return false;
	}
	
	// now validate the parameters
	var fieldName;
	var value;
	var min;
	var max;
	var valid;
	var errorMessage = "";
	
	<%
		ProteinInferenceForm form_s = (ProteinInferenceForm)request.getAttribute("proteinInferenceFormSearch");
		String programName_s = form_s.getProgramParams().getProgramName();
        ProteinInferenceProgram program_s = ProteinInferenceProgram.getProgramForName(programName_s);
		for(ProgramParam param: program_s.getProgramParams()) {
			if(param.getType() == TYPE.BOOLEAN || param.getType() == TYPE.CHOICE)
				continue;
			ProgramParam.ParamValidator validator = param.getValidator();
			
	%>
		fieldName = '<%=param.getDisplayName()%>';
		value = $("form[id='form_s'] input:text[id='<%=param.getName()%>']").val();
		//alert(value);
		
		<%if(validator != null && validator instanceof DoubleValidator) {%>
			min = <%=((DoubleValidator)validator).getMinVal()%>;
			max = <%=((DoubleValidator)validator).getMaxVal()%>;
			valid = validateFloat(value, fieldName, min, max);
			if(!valid)
				errorMessage += "-- "+fieldName+" should be between "+min+" and "+max+"\n";
		<%} else if(validator != null && validator instanceof IntegerValidator) {%>
			min = <%=((IntegerValidator)validator).getMinVal()%>;
			max = <%=((IntegerValidator)validator).getMaxVal()%>;
			valid = validateInt(value, fieldName, min, max);
			if(!valid)
				errorMessage += "-- "+fieldName+" should be between "+min+" and "+max+"\n";
		<%}else {%>
			if(value.length == 0) {
				errorMessage += "-- <%=param.getDisplayName()%> cannot be empty\n";
				valid = false;
			}
		<%}%>
		
	<%}%>
	if(errorMessage.length > 0) {
		alert(errorMessage);
		return false;
	}
	
	return true;
}
</script>    			



  <html:form action="doProteinInferenceSearch" method="post" styleId="form_s" onsubmit="return validateFormForSearchInput(this);">
  
  <html:hidden name="proteinInferenceFormSearch" property="projectId" />
  <html:hidden name="proteinInferenceFormSearch" property="inputSummary.inputGroupId" />
  <html:hidden name="proteinInferenceFormSearch" property="inputTypeChar" />
  
  <TABLE CELLPADDING="4px" CELLSPACING="0px" width="90%">
   
    <yrcwww:colorrow scheme="pinfer">
    <td WIDTH="20%" VALIGN="top">
  		<B>Parameters:</B>
  		<html:hidden name="proteinInferenceFormSearch" property="programParams.programName" />
  	</td>
  	<td></td>
  	</yrcwww:colorrow>
  	
  	<logic:iterate name="proteinInferenceFormSearch" property="programParams.paramList" id="param" 
  		           type="org.yeastrc.www.proteinfer.ProgramParameters.Param">
    <yrcwww:colorrow scheme="pinfer" repeat="true">
    
    <td WIDTH="20%" VALIGN="top">
    	<span class="tooltip" title="<bean:write name="param" property="tooltip" />">
    		<bean:write name="param" property="displayName" />
    	</span>
    	<logic:present name="param" property="notes">
    		<br>
    		<span style="color: red; font-size: 8pt;"><bean:write name="param" property="notes" /></span>
    	</logic:present>
    </td>
    
    
    <td WIDTH="20%" VALIGN="top">
    	<html:hidden name="param" property="name" indexed="true" />
    	<logic:equal name="param" property="type" value="text">
    		<html:text name="param" property="value" indexed="true" styleId="<%=param.getName() %>"/>
    	</logic:equal>
    	<logic:equal name="param" property="type" value="checkbox">
    		<html:checkbox name="param" property="value" value="true" indexed="true" />
    	</logic:equal>
    	<logic:equal name="param" property="type" value="radio">
    		<!-- cannot use nested logic:iterate with indexed properties -->
    		<%for(String option: param.getOptions()) { %>
    			<html:radio name="param" property="value" value="<%=option%>" indexed="true"><%=option%></html:radio><br>
    		<%} %>
    	</logic:equal>
    </td>
    
   	</yrcwww:colorrow>
   </logic:iterate>
   
   <yrcwww:colorrow scheme="pinfer">
    <td VALIGN="top" colspan="2">
    <B>Select Input Files:</B>
    
    </td>
   </yrcwww:colorrow>
   
   <yrcwww:colorrow scheme="pinfer" repeat="true">
   
   	<td VALIGN="top" colspan="2">
    	<div style="border: solid 1px #2E8B57; padding-bottom: 5px;">
    	
    	<div id="searchInputList">
    	
    	<div id="search_<bean:write name="proteinInferenceFormSearch" property="inputSummary.inputGroupId"/>">
    	<div style="background-color: #2E8B57; color: white; font-weight: bold;" 
    		 class="foldable fold-open"
    		 id="foldable_search_<bean:write name="proteinInferenceFormSearch" property="inputSummary.inputGroupId"/>">
    		Search ID: <bean:write name="proteinInferenceFormSearch" property="inputSummary.inputGroupId"/>
    	</div>
    	
    	
    	<div id="foldable_search_<bean:write name="proteinInferenceFormSearch" property="inputSummary.inputGroupId"/>_div">
    	
    	<div style="color: black;">
    		Search Program:
    		<html:hidden name="proteinInferenceFormSearch" property="inputSummary.programName" /> 
    		<bean:write name="proteinInferenceFormSearch" property="inputSummary.programName" />&nbsp;
    		<html:hidden name="proteinInferenceFormSearch" property="inputSummary.programVersion" />
  			<bean:write name="proteinInferenceFormSearch" property="inputSummary.programVersion" />
  			<br>
  			Search Database:
  			<bean:write name="proteinInferenceFormSearch" property="inputSummary.searchDatabase" /> 
  			<html:hidden name="proteinInferenceFormSearch" property="inputSummary.searchDatabase" />
    	</div>
    	<br>
    	
    	<% 
    		String mySearchId = String.valueOf(searchIdInt);
    		String toggleIdDiv = "toggle_search_"+mySearchId;
    		String checkboxId = toggleIdDiv+"_file";
    	%>
    
		<table width="100%">
 		<logic:iterate name="proteinInferenceFormSearch" property="inputSummary.inputFiles" id="inputFile" >
		<yrcwww:colorrow scheme="pinfer" repeat="true">
			<td WIDTH="20%" VALIGN="top"> 
				<html:checkbox name="inputFile" property="isSelected" value="true" indexed="true" 
				styleId="<%=checkboxId%>" />
			</td>
			<td>
				<html:hidden name="inputFile" property="inputId" indexed="true" />
				<html:hidden name="inputFile" property="runName" indexed="true" />
				<bean:write  name="inputFile" property="runName" />
			</td>
		</yrcwww:colorrow>
		</logic:iterate>
 		</table>
		<div class="clickable toggle_selection" style="font-size: 7pt; color: #006400;" 
		     id="<%=toggleIdDiv%>">Deselect All</div>
		</div>
		</div>
		</div>
		<center><button class="button" id="addSearchesButton" >Add</button></center>
	</div>
    </td>
   
   </yrcwww:colorrow>
   
    <yrcwww:colorrow scheme="pinfer" repeat="true">
    	<td colspan="2" align="center">Comments<br>
    	<html:textarea name="proteinInferenceFormSearch" property="comments" rows="3" cols="50"/>
    	</td>
    </yrcwww:colorrow>
    
	<yrcwww:colorrow scheme="pinfer" repeat="true">
   <td colspan="2" align="center">
   	<NOBR>
 		<html:submit value="Run Protein Inference" styleClass="button" />
 		<input type="button" class="button" onclick="javascript:onCancel(<bean:write name="projectId" />);" value="Cancel"/>
 	</NOBR>
   </td>
   </yrcwww:colorrow>
  </TABLE>

 
</html:form>
