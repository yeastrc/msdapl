<%@page import="org.yeastrc.ms.domain.protinfer.ProteinUserValidation"%>
<%@page import="org.yeastrc.www.compare.dataset.DatasetColor"%>
<%@ taglib uri="/WEB-INF/yrc-www.tld" prefix="yrcwww" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>

<yrcwww:notauthenticated>
 <logic:forward name="authenticate" />
</yrcwww:notauthenticated>

<logic:notPresent name="datasetFiltersForm" scope="request">
	<logic:forward  name="selectComparisonDatasets" />
</logic:notPresent>


<%@ include file="/includes/header.jsp" %>

<%@ include file="/includes/errors.jsp" %>


<script>
// ---------------------------------------------------------------------------------------
// SETUP THE ROC tables for PeptideProphet
// ---------------------------------------------------------------------------------------
$(document).ready(function() {
   $(".sortable_table").each(function() {
   		var $table = $(this);
   		//$('tbody > tr:odd', $table).addClass("tr_odd");
   		//$('tbody > tr:even', $table).addClass("tr_even");
   		//makeSortableTable($table);
   });
   
   $(".roc_row").mouseover(function() {
   		$(this).css({backgroundColor: 'yellow', color: 'red'});
   });
   $(".roc_row").mouseout(function() {
   		if(!$(this).is(".row_selected")) {
   			$(this).css({backgroundColor: '', color: ''});
   		}
   });

});
  
function toggleRoc(proteinProphetId){
	var text = $("#view_roc_"+proteinProphetId).text();
	if(text == "View") {
		$("#view_roc_"+proteinProphetId).text("Hide");
		$("#roc_table_"+proteinProphetId).show();
		
	}
	else if (text == "Hide") {
		$("#view_roc_"+proteinProphetId).text("View");
		$("#roc_table_"+proteinProphetId).hide();
	}
}

function selectError(proteinProphetId, row, error, probability) {
	$("#error_prob_"+proteinProphetId).text(error+" ("+probability+")");
	$("input#error_"+proteinProphetId).val(error);
	$("input#prob_"+proteinProphetId).val(probability);
	
	$(".roc_row").each(function() {
		$(this).css({backgroundColor: '', color: ''}).removeClass("row_selected");
	});
	$("#roc_row_"+proteinProphetId+"_"+row).css({backgroundColor: 'yellow', color: 'red'}).addClass("row_selected");
}

// ---------------------------------------------------------------------------------------
// TOGGLE AND, OR, NOT FILTERS
// ---------------------------------------------------------------------------------------
function toggleAndSelect(dsIndex, red, green, blue) {
	
	var id = "AND_"+dsIndex+"_select";
	var value = $("input#"+id).val();
	if(value == "true") {
		$("input#"+id).val("false");	
		$("td#AND_"+dsIndex+"_td").css("background-color", "#FFFFFF");
	}
	else {
		var color = "rgb("+red+","+green+","+blue+")";
		$("input#"+id).val("true");	
		$("td#AND_"+dsIndex+"_td").css("background-color", color);
	}
}
function toggleOrSelect(dsIndex, red, green, blue) {
	var id = "OR_"+dsIndex+"_select";
	var value = $("input#"+id).val();
	if(value == "true") {
		$("input#"+id).val("false");	
		$("td#OR_"+dsIndex+"_td").css("background-color", "#FFFFFF");
	}
	else {
		var color = "rgb("+red+","+green+","+blue+")";
		$("input#"+id).val("true");	
		$("td#OR_"+dsIndex+"_td").css("background-color", color);
	}
}
function toggleNotSelect(dsIndex, red, green, blue) {
	var id = "NOT_"+dsIndex+"_select";
	var value = $("input#"+id).val();
	if(value == "true") {
		$("input#"+id).val("false");	
		$("td#NOT_"+dsIndex+"_td").css("background-color", "#FFFFFF");
	}
	else {
		var color = "rgb("+red+","+green+","+blue+")";
		$("input#"+id).val("true");	
		$("td#NOT_"+dsIndex+"_td").css("background-color", color);
	}
}
function toggleXorSelect(dsIndex, red, green, blue) {
	var id = "XOR_"+dsIndex+"_select";
	var value = $("input#"+id).val();
	if(value == "true") {
		$("input#"+id).val("false");	
		$("td#XOR_"+dsIndex+"_td").css("background-color", "#FFFFFF");
	}
	else {
		var color = "rgb("+red+","+green+","+blue+")";
		$("input#"+id).val("true");	
		$("td#XOR_"+dsIndex+"_td").css("background-color", color);
	}
}

</script> 


<CENTER>

<yrcwww:contentbox centered="true" title="Filtering Options for Selected Datasets" width="80" widthRel="true">

<br>

<!-- ======================================================================================== -->
<!-- Datasets and their colors -->
<!-- ======================================================================================== -->
<table  align="center" style="border: 1px dashed gray;" width="80%">
<tbody>
<logic:iterate name="datasetFiltersForm" property="andList" id="dataset" indexId="row">
	<bean:define id="mod" value="<%=String.valueOf(row%2)%>"></bean:define>
	<logic:equal name="mod" value="0"><tr></logic:equal>
	<td width="2%"style="background-color: rgb(<bean:write name='dataset' property='red'/>,<bean:write name='dataset' property='green'/>,<bean:write name='dataset' property='blue'/>);">
		&nbsp;&nbsp;
	</td>
	<td style="font-size:8pt;text-align:left;"><html:link action="viewProteinInferenceResult.do" paramId="pinferId" paramName="dataset" paramProperty="datasetId">ID <bean:write name="dataset" property="datasetId" /></html:link></td>
	<td width="42%" style="font-size:8pt;" ><bean:write name="dataset" property="datasetComments" /></td>
	<logic:equal name="mod" value="1"></tr></logic:equal>
</logic:iterate>
</tbody>
</table>


<html:form action="doProteinSetComparison" method="POST">

	
<!-- ======================================================================================== -->
<!-- AND, OR, NOT, XOR filters -->
<!-- ======================================================================================== -->
<br>
<table align="center" style="border: 1px solid gray;">
<tr>
	<td valign="middle" style="padding-bottom:10px;">Filter: </td>
	<td style="padding-bottom:10px;"  align="left">
	<table>
	<tr>
	<td valign="top"><b>AND</b></td>
	<td style="padding-right:10px">
		<table cellpadding="0" cellspacing="0">
		<tr>
			<logic:iterate name="datasetFiltersForm" property="andList" id="andDataset">
				
				<bean:define name="andDataset" property="datasetIndex" id="datasetIndex"/>
				<logic:equal name="andDataset" property="selected" value="true">
					<td style="background-color:rgb(<bean:write name='andDataset' property='red'/>,<bean:write name='andDataset' property='green'/>,<bean:write name='andDataset' property='blue'/>); border:1px solid #AAAAAA;"
						id="AND_<bean:write name='datasetIndex'/>_td"
					>
				</logic:equal>
				<logic:notEqual name="andDataset" property="selected" value="true">
					<td style="background-color:#FFFFFF; border:1px solid #AAAAAA;" id="AND_<bean:write name='datasetIndex'/>_td" >
				</logic:notEqual>
				<span style="cursor:pointer;" onclick="javascript:toggleAndSelect(<bean:write name='datasetIndex'/>, <bean:write name='andDataset' property='red'/>,<bean:write name='andDataset' property='green'/>,<bean:write name='andDataset' property='blue'/>);">&nbsp;&nbsp;</span>
				<html:hidden name="andDataset" property="datasetId" indexed="true" />
				<html:hidden name="andDataset" property="datasetIndex" indexed="true" />
				<html:hidden name="andDataset" property="sourceString" indexed="true" />
				<html:hidden name="andDataset" property="selected" indexed="true" 
				             styleId='<%= "AND_"+datasetIndex+"_select"%>' />
				</td>
			</logic:iterate>
		</tr>
		</table>
	</td>
	<td valign="top"><b>OR</b></td>
	<td style="padding-right:10px">
		<table  cellpadding="0" cellspacing="0">
		<tr>
			<logic:iterate name="datasetFiltersForm" property="orList" id="orDataset">
				
				<bean:define name="orDataset" property="datasetIndex" id="datasetIndex"/>
				<logic:equal name="orDataset" property="selected" value="true">
					<td style="background-color:rgb(<bean:write name='orDataset' property='red'/>,<bean:write name='orDataset' property='green'/>,<bean:write name='orDataset' property='blue'/>); border:1px solid #AAAAAA;"
						id="OR_<bean:write name='datasetIndex'/>_td"
					>
				</logic:equal>
				<logic:notEqual name="orDataset" property="selected" value="true">
					<td style="background-color:#FFFFFF; border:1px solid #AAAAAA;" id="OR_<bean:write name='datasetIndex'/>_td">
				</logic:notEqual>
				<span style="cursor:pointer;" onclick="javascript:toggleOrSelect(<bean:write name='datasetIndex'/>,<bean:write name='orDataset' property='red'/>,<bean:write name='orDataset' property='green'/>,<bean:write name='orDataset' property='blue'/>);">&nbsp;&nbsp;</span>
				<html:hidden name="orDataset" property="datasetId" indexed="true" />
				<html:hidden name="orDataset" property="datasetIndex" indexed="true" />
				<html:hidden name="orDataset" property="sourceString" indexed="true" />
				<html:hidden name="orDataset" property="selected" indexed="true" 
							styleId='<%= "OR_"+datasetIndex+"_select"%>' />
				</td>
			</logic:iterate>
		</tr>
		</table>
	</td>
	<td valign="top"><b>NOT</b></td>
	<td style="padding-right:10px">
		<table  cellpadding="0" cellspacing="0">
		<tr>
			<logic:iterate name="datasetFiltersForm" property="notList" id="notDataset" indexId="dsIndex">
				
				<bean:define name="notDataset" property="datasetIndex" id="datasetIndex"/>
				<logic:equal name="notDataset" property="selected" value="true">
					<td style="background-color:rgb(<bean:write name='notDataset' property='red'/>,<bean:write name='notDataset' property='green'/>,<bean:write name='notDataset' property='blue'/>); border:1px solid #AAAAAA;"
						id="NOT_<bean:write name='datasetIndex'/>_td"
					>
				</logic:equal>
				<logic:notEqual name="notDataset" property="selected" value="true">
					<td style="background-color:#FFFFFF; border:1px solid #AAAAAA;" id="NOT_<bean:write name='datasetIndex'/>_td">
				</logic:notEqual>
				<span style="cursor:pointer;" onclick="javascript:toggleNotSelect(<bean:write name='datasetIndex'/>,<bean:write name='notDataset' property='red'/>,<bean:write name='notDataset' property='green'/>,<bean:write name='notDataset' property='blue'/>);">&nbsp;&nbsp;</span>
				<html:hidden name="notDataset" property="datasetId" indexed="true" />
				<html:hidden name="notDataset" property="datasetIndex" indexed="true" />
				<html:hidden name="notDataset" property="sourceString" indexed="true" />
				<html:hidden name="notDataset" property="selected" indexed="true" 
							styleId='<%= "NOT_"+datasetIndex+"_select"%>' />
				</td>
				
			</logic:iterate>
		</tr>
		</table>
	</td>
	<td valign="top"><b>XOR</b></td>
	<td>
		<table  cellpadding="0" cellspacing="0">
		<tr>
			<logic:iterate name="datasetFiltersForm" property="xorList" id="xorDataset" indexId="dsIndex">
				
				<bean:define name="xorDataset" property="datasetIndex" id="datasetIndex"/>
				<logic:equal name="xorDataset" property="selected" value="true">
					<td style="background-color:rgb(<bean:write name='xorDataset' property='red'/>,<bean:write name='xorDataset' property='green'/>,<bean:write name='xorDataset' property='blue'/>); border:1px solid #AAAAAA;"
						id="XOR_<bean:write name='datasetIndex'/>_td"
					>
				</logic:equal>
				<logic:notEqual name="xorDataset" property="selected" value="true">
					<td style="background-color:#FFFFFF; border:1px solid #AAAAAA;" id="XOR_<bean:write name='datasetIndex'/>_td">
				</logic:notEqual>
				<span style="cursor:pointer;" onclick="javascript:toggleXorSelect(<bean:write name='datasetIndex'/>,<bean:write name='xorDataset' property='red'/>,<bean:write name='xorDataset' property='green'/>,<bean:write name='xorDataset' property='blue'/>);">&nbsp;&nbsp;</span>
				<html:hidden name="xorDataset" property="datasetId" indexed="true" />
				<html:hidden name="xorDataset" property="datasetIndex" indexed="true" />
				<html:hidden name="xorDataset" property="sourceString" indexed="true" />
				<html:hidden name="xorDataset" property="selected" indexed="true" 
							styleId='<%= "XOR_"+datasetIndex+"_select"%>' />
				</td>
				
			</logic:iterate>
		</tr>
		</table>
	</td>
	</tr>
</table>
</td>
</tr>
</table>
<br>

<!-- ======================================================================================== -->
<!-- Filtering Options -->
<!-- ======================================================================================== -->
<TABLE CELLPADDING="5px" CELLSPACING="5px" align="center" style="border: 1px solid gray;" width="90%">
	<tr>
 		<td><table>
 		<tr>
 		<td>Peptides: </td>
 		<td>
 			Min <html:text name="datasetFiltersForm" property="minPeptides" size="3" ></html:text>
 			Max <html:text name="datasetFiltersForm" property="maxPeptides" size="3" ></html:text>
 		</td>
		</tr>
 		<tr>
 		<td>Unique Peptides: </td>
 		<td>
 			Min <html:text name="datasetFiltersForm" property="minUniquePeptides" size="3" ></html:text>
 			Max <html:text name="datasetFiltersForm" property="maxUniquePeptides" size="3" ></html:text>
 		</td>
 		</tr>
 		</table></td>
 
 		<td><table>
 		<tr>
 		<td>Coverage(%):</td>
 		<td>
 			Min <html:text name="datasetFiltersForm" property="minCoverage" size="3" ></html:text>
 			Max <html:text name="datasetFiltersForm" property="maxCoverage" size="3" ></html:text>
 		</td>
 		</tr>
 		<tr>
 		<td>Spectrum Matches: </td>
 		<td>
 			Min <html:text name="datasetFiltersForm" property="minSpectrumMatches" size="3" ></html:text>
 			Max <html:text name="datasetFiltersForm" property="maxSpectrumMatches" size="3" ></html:text>
 		</td>
 		</tr>
 		</table></td>
 
 		<td><table>
  	<tr>
  	<td colspan="2">Select Proteins: </td>
  	<td>
  		<html:radio name="datasetFiltersForm" property="useAllProteins" value="true" >All</html:radio>
  	</td>
  	<td>
  		<html:radio name="datasetFiltersForm" property="useAllProteins" value="false" >Parsimonious</html:radio>
  	</td>
  	</tr>
  	</table></td>
  	</tr>
 
  	<tr>
  	<td colspan="2">
  		Validation Status: 
  		<html:multibox name="datasetFiltersForm" property="validationStatus" value="All" /> All
  		<html:multibox name="datasetFiltersForm" property="validationStatus" 
  					   value="<%=String.valueOf(ProteinUserValidation.UNVALIDATED.getStatusChar()) %>"/> Unvalidated 
  		<html:multibox name="datasetFiltersForm" property="validationStatus"  
  		               value="<%=String.valueOf(ProteinUserValidation.ACCEPTED.getStatusChar()) %>"/> Accepted
  		<html:multibox name="datasetFiltersForm" property="validationStatus"  
  		               value="<%=String.valueOf(ProteinUserValidation.REJECTED.getStatusChar()) %>"/> Rejected
  		<html:multibox name="datasetFiltersForm" property="validationStatus"  
  		               value="<%=String.valueOf(ProteinUserValidation.NOT_SURE.getStatusChar()) %>"/> Not Sure
  	</td>
 	</tr>
 
  	<tr>
  	<td colspan="3">
  	<table align="left">
  		<tr>
  			<td valign="top">Accession(s): </td>
  			<td valign="top"><html:text name="datasetFiltersForm" property="accessionLike" size="40" ></html:text><br>
  				<span style="font-size:8pt;">Enter a comma-separated list of complete or partial identifiers</span>
  			</td>
  			<td valign="top">Description: </td>
  			<td valign="top">
  				<html:text name="datasetFiltersForm" property="descriptionLike" size="40" ></html:text>
  			</td>
  		</tr>
  		<tr>
  		</tr>
  	</table>
  	</td>
  	</tr>
 
	</TABLE>


<!-- ======================================================================================== -->
<!-- ProteinProphet Runs (for setting probability filters)-->
<!-- ======================================================================================== -->
<logic:notEmpty name="datasetFiltersForm" property="proteinProphetRunList">
	<br>
	
	<yrcwww:contentbox centered="true" title="ProteinProphet Datasets" width="89" widthRel="true">
	
	<table align="center" width="99%" class="table_basic">
	<thead>
		<tr>
		<th>ID</th>
		<th>ProjectID</th>
		<th>Comments</th>
		<th>Error(Prob.)</th>
		<th>ROC</th>
		</tr>
	</thead>
	<tbody>
		<logic:iterate name="datasetFiltersForm" property="proteinProphetRunList" id="ppRun">
		
		<logic:equal name="ppRun" property="selected" value="true">
			<html:hidden name="ppRun" property="runId" indexed="true" />
			<html:hidden name="ppRun" property="selected" indexed="true" />
		</logic:equal>
		
		<tr>
		<td><b><bean:write name="ppRun" property="runId"/><b></td>
		<td>
			<b>
			<logic:iterate name="ppRun" property="projectIdList" id="projectId">
				<bean:write name="projectId"/>
			</logic:iterate>
			</b>
		</td>
		<td><bean:write name="ppRun" property="comments"/>
		<td>
			<bean:define name="ppRun" property="runId" id="runId"/>
			<span id="error_prob_<bean:write name="ppRun" property="runId"/>">
			<bean:write name="ppRun" property="errorRate"/> (<bean:write name="ppRun" property="probability"/>)
			</span>
			<html:hidden name="ppRun" property="errorRate" styleId='<%="error_"+runId %>' />
			<html:hidden name="ppRun" property="probability" styleId='<%="prob_"+runId %>' />
		</td>
		<td><span class="clickable underline" 
				  id="view_roc_<bean:write name="ppRun" property="runId"/>"
				  onclick="javascript:toggleRoc(<bean:write name="ppRun" property="runId"/>);">View</span></td>
		</tr>
		<tr>
		<td colspan="5">
			<table align="center" width="50%" class="table_basic sortable_table" style="display: none;" id="roc_table_<bean:write name="ppRun" property="runId"/>">
			<thead>
			<tr>
				<th class="sortable sort-float" align="left"><b><font size="2pt">Min. Probability</font></b></th>
				<th class="sortable sort-float" align="left"><b><font size="2pt">Error</font></b></th>
				<th class="sortable sort-float" align="left"><b><font size="2pt">Sensitivity</font></b></th>
				<th class="sortable sort-int" align="left"><b><font size="2pt"># Correct</font></b></th>
				<th class="sortable sort-int" align="left"><b><font size="2pt"># Incorrect</font></b></th>
				<th></th>
			</tr>
			</thead>
			<tbody>
			<logic:iterate name="ppRun"  property="roc.rocPoints" id="rocPoint" indexId="row">
	 		<tr id="roc_row_<bean:write name="ppRun" property="runId"/>_<bean:write name="row"/>" class="roc_row">
	 			<td><bean:write name="rocPoint" property="minProbability" /></td>
	 			<td><bean:write name="rocPoint" property="falsePositiveErrorRate" /></td>
	 			<td><bean:write name="rocPoint" property="sensitivity" /></td>
	 			<td><bean:write name="rocPoint" property="numCorrect" /></td>
	 			<td><bean:write name="rocPoint" property="numIncorrect" /></td>
	 			<td><span class="underline clickable" 
	 			          onclick="javascript:selectError(<bean:write name="ppRun" property="runId"/>, <bean:write name="row"/>, <bean:write name="rocPoint" property="falsePositiveErrorRate" />, <bean:write name="rocPoint" property="minProbability" />);">Select</span>
	 			</td>
	 		</tr>
	 		</logic:iterate>
			</tbody>
			</table>
		</td>
		</tr>
		</logic:iterate>
	</tbody>
	</table>
	
	</yrcwww:contentbox>
	
</logic:notEmpty>

<div align="center">
<html:submit value="Submit" styleClass="plain_button"/>
</div>

</html:form>

</yrcwww:contentbox>
</CENTER>

<%@ include file="/includes/footer.jsp" %>