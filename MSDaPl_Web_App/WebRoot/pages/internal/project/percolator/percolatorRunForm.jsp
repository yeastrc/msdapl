
<%@page import="org.yeastrc.www.upload.PercolatorRunForm"%>
<%@page import="org.yeastrc.ms.domain.protinfer.ProteinInferenceProgram"%>
<%@page import="org.yeastrc.ms.domain.protinfer.ProgramParam"%>
<%@page import="org.yeastrc.ms.domain.protinfer.ProgramParam.ParamValidator"%>
<%@page import="org.yeastrc.ms.domain.protinfer.ProgramParam.TYPE"%>
<%@page import="org.yeastrc.ms.domain.protinfer.ProgramParam.DoubleValidator"%>
<%@page import="org.yeastrc.ms.domain.protinfer.ProgramParam.IntegerValidator"%>
<%@ taglib uri="/WEB-INF/yrc-www.tld" prefix="yrcwww" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>

<%@ include file="/includes/header.jsp" %>

<%@ include file="/includes/errors.jsp" %>
<logic:notPresent name="percolatorRunForm" scope="request">
	<logic:forward name="percolatorRunForm" />
</logic:notPresent>

<script language="javascript">


$(document).ready(function(){
	
	$(".toggle_selection").click(function() {
		toggleSelection($(this));
	});
	
	$(".pi_opts_link").click(function() {
		var text = $(this).text();
		if(text == "Show Protein Inference Options") {
			$(this).text("Hide Protein Inference Options");
			$("#pi_opts_table").show();
		}
		else {
			$(this).text("Show Protein Inference Options");
			$("#pi_opts_table").hide();
		}
	});
	
	$("input[name='runProteinInference']").click(function() {
		if($(this).is(":checked")) {
			$(".pi_opts_link").show();
		}
		else {
			$(".pi_opts_link").hide();
		}
	});
});

function onCancel(projectId, experimentId) {
	if(projectId == 0) {
		document.location = "<yrcwww:link path='viewFrontPage.do' />";
	}
	else {
		document.location = "<yrcwww:link path='viewProject.do?ID=' />"+projectId;
	}
}

// VALIDATE FORM PARAMETERS  
function validateForm() {
	// first make sure that at least one file is selected
	if($("input:checked[class=runSearchId]").size() == 0) {
		alert("Please select at least one file");
		return false;
	}
	
	// now validate the parameters
	var fieldName;
	var value;
	var min;
	var max;
	var valid;
	var allowNull;
	var errorMessage = "";
	
	<%PercolatorRunForm form_a = (PercolatorRunForm)request.getAttribute("percolatorRunForm");
	String programName_a = form_a.getProgramParams().getProgramName();
       ProteinInferenceProgram program_a = ProteinInferenceProgram.getProgramForName(programName_a);
	for(ProgramParam param: program_a.getProgramParams()) {
		if(param.getType() == ProgramParam.TYPE.BOOLEAN || param.getType() == TYPE.CHOICE)
			continue;
		ParamValidator validator = param.getValidator();%>
		fieldName = '<%=param.getDisplayName()%>';
		value = $("form input:text[id='<%=param.getName()%>']").val();
		//alert(value);
		
		<%if(validator != null && validator instanceof DoubleValidator) {%>
			min = <%=((DoubleValidator)validator).getMinVal()%>;
			max = <%=((DoubleValidator)validator).getMaxVal()%>;
			allowNull = new Boolean("<%=((DoubleValidator)validator).allowsNull()%>");
			
			if(allowNull && value.length == 0) {
				valid = true;
			}
			else {
				valid = validateFloat(value, fieldName, min, max);
			}
			if(!valid)
				errorMessage += "-- "+fieldName+" should be between "+min+" and "+max+"\n";
		<%} else if(validator != null && validator instanceof IntegerValidator) {%>
			min = <%=((IntegerValidator)validator).getMinVal()%>;
			max = <%=((IntegerValidator)validator).getMaxVal()%>;
			allowNull = new Boolean("<%=((IntegerValidator)validator).allowsNull()%>");
			
			if(allowNull && value.length == 0) {
				valid = true;
			}
			else {
				valid = validateInt(value, fieldName, min, max);
			}
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

function toggleSelection(button) {
	if(button.text() == "Deselect All") {
		$("input[class=runSearchId]").attr("checked", "");
		button.text("Select All");
	}
	else if(button.text() == "Select All") {
		$("input[class=runSearchId]").attr("checked", "checked");
		button.text("Deselect All");
	}
}
</script>

<yrcwww:contentbox title="Run Percolator" centered="true" width="700" scheme="upload">

<html:form action="percolatorRun" method="POST" onsubmit="return validateForm(this);">

  
 <CENTER>
  <table border="0" width="100%">
  
   <tr>
    <td valign="top"><b>Project ID:</b></td>
    <td valign="top"><bean:write name="percolatorRunForm" property="projectId"/><html:hidden property="projectId"/></td>
   </tr>
   
   <tr>
    <td valign="top"><b>Experiment ID:</b></td>
    <td valign="top"><bean:write name="percolatorRunForm" property="experimentId"/><html:hidden property="experimentId"/></td>
   </tr>
   
   <html:hidden name="percolatorRunForm" property="searchId" />
   
    <!-- Input files -->
    	<logic:iterate name="percolatorRunForm" property="inputFiles" id="inputFile">
    	
		<tr class="project_A">
			<td WIDTH="20%" VALIGN="top"> 
				<html:checkbox name="inputFile" property="isSelected" value="true" indexed="true" 
				styleClass="runSearchId" />
			</td>
			<td>
				<html:hidden name="inputFile" property="runSearchId" indexed="true" />
				<html:hidden name="inputFile" property="runName" indexed="true" />
				<bean:write  name="inputFile" property="runName" />
			</td>
		</tr>
		</logic:iterate>
		
		<tr>
		<td colspan="2" align="left">
		<span class="clickable toggle_selection underline" style="font-size: 7pt; color: #000000;">Deselect All</span>
		</td>
		</tr>
		
		<tr>
		<td colspan="2" align="center">
    	<b>Comments</b><br/>
		<html:textarea name="percolatorRunForm" property="comments" rows="3" cols="70"/>
		</td>
		</tr>
		
		<tr>
			<td colspan="2" align="center">
				<html:checkbox name="percolatorRunForm" property="individualRuns">Individual Runs</html:checkbox><br>
				<span style="font-size: 8pt; color: red;">Check the box above if Percolator should be run separately on each selected file</span>
			</td>
		</tr>
		<tr>
			<td colspan="2" align="center">
				<html:checkbox name="percolatorRunForm" property="runProteinInference">Run Protein Inference</html:checkbox><br>
				<span style="font-size: 8pt; color: red;">Check the box above if you want to run protein inference on the Percolator results</span>
			</td>
		</tr>
		
		
		<tr>
   			<td colspan="2" align="center">
   				<span class="clickable underline pi_opts_link" style="color:red; font-weight:bold;display:none;" >Show Protein Inference Options</span>
   			</td>
    	</tr>
    		
		<tr>
		<td colspan="2">
			<html:hidden name="percolatorRunForm" property="programParams.programName"/>
			<table id="pi_opts_table" style="margin:10px;border:1px dashed;display:none;">
			<tbody>
			
			<logic:iterate name="percolatorRunForm" property="programParams.paramList" id="param"
  				type="org.yeastrc.www.proteinfer.job.ProgramParameters.Param">
    
    		<tr>
    			<td WIDTH="20%" VALIGN="top">
    				<span class="tooltip" title="<bean:write name="param" property="tooltip" />">
    				<bean:write name="param" property="displayName" />
    				</span>
    				<logic:present name="param" property="notes">
    				<br>
    				<span style="color: red; font-size: 8pt;"><bean:write name="param" property="notes" /></span>
    				</logic:present>
    				<logic:equal name="param" property="newOption" value="true">
    					<a href="" onclick="openInformationPopup('pages/internal/docs/proteinInference.jsp#PI_OPTIONS'); return false;">
   						<img src="<yrcwww:link path="images/new_21.gif"/>" align="bottom" border="2" style="background:yellow; border-color:orange;"/></a>
   						<a href="" onclick="openInformationPopup('pages/internal/docs/proteinInference.jsp#PI_OPTIONS'); return false;">
   						<img src="<yrcwww:link path="images/info_16.png"/>" align="bottom" border="0"/></a>
    				</logic:equal>
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
    
   			</tr>
   			</logic:iterate>
   			</tbody>
   			</table>
   
		</td>
		</tr>
		
 		</table>
 	<br>
   
   	
	
	<div>
	<div>
		
		
	</div>
   	<NOBR>
 		<html:submit value="Run Percolator" styleClass="plain_button" />
 		<input type="button" class="plain_button" onclick="javascript:onCancel(<bean:write name="percolatorRunForm" property="projectId" />);" value="Cancel"/>
 	</NOBR>
	</div>
	
	
 </CENTER>

</html:form>

</yrcwww:contentbox>

<%@ include file="/includes/footer.jsp" %>