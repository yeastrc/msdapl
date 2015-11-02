
<%@ taglib uri="/WEB-INF/yrc-www.tld" prefix="yrcwww" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>

<yrcwww:notauthenticated>
 <logic:forward name="authenticate" />
</yrcwww:notauthenticated>


<%@ include file="/includes/header.jsp" %>

<%@ include file="/includes/errors.jsp" %>

<script>
// ---------------------------------------------------------------------------------------
// COMPARE SELECTED PROTEIN INFERENCE RUNS
// --------------------------------------------------------------------------------------- 
function compareSelectedProtInfer() {
	var pinferIds = "";
	var forDisplay = "\n";
	var i = 0;
	$("input.compare_cb:checked").each(function() {
		if(i > 0) {
			pinferIds += ",";
		}
		pinferIds += $(this).val();
		forDisplay += $(this).val()+"\n";
		i++;
	});
	if(i < 2) {
		alert("Please select at least two protein inference results to compare");
		return false;
	}
	var groupIndistinguishable = $("input#grpProts:checked").val() != null;
	forDisplay +="Group Indistinguishable Proteins: "+groupIndistinguishable;
	
	// var doCompare = confirm("Compare protein inference results: "+forDisplay);
	// if(doCompare) {
		// var url = "<yrcwww:link path='setComparisonFilters.do?'/>"+"piRunIds="+pinferIds+"&groupProteins="+groupIndistinguishable;
		var url = "<yrcwww:link path='doProteinSetComparison.do?'/>"+"piRunIds="+pinferIds+"&groupProteins="+groupIndistinguishable;
		window.location.href = url;
	// }
}
$(document).ready(function() {
   $(".sorttable").each(function() {
   		makeSortableTable($(this));
   });
});
</script>

<CENTER>

<yrcwww:contentbox centered="true" title="Available Datasets" width="700">


<!-- Proceed only if we have available datasets -->

<logic:empty name="datasetList">

	<div><b>There are no available protein datasets at this time.</b></div>

</logic:empty>

<div><b>Please select 2 or more datasets from the list below</b></div>
<br>

<logic:notEmpty name="datasetList">

	<table width="90%" class="table_basic sorttable" align="center">
	<thead>
		<tr align="left">
		<th></th>
		<th class="sort-int sortable">Prot. Infer ID</th>
		<th class="sort-alpha sortable">Program</th>
		<th class="sort-int sortable">ProjectID</th>
		<th>Date</th>
		<th class="sort-alpha sortable">Comments</th>
		</tr>
	</thead>
	<tbody>
	
		<logic:iterate name="datasetList" id="proteinferRun">
		<yrcwww:colorrow>
			<td>
				<logic:equal name="proteinferRun" property="selected" value="false">
					<input type="checkbox" class="compare_cb" 
					value="<bean:write name='proteinferRun' property='runId'/>" />
				</logic:equal>
				<logic:equal name="proteinferRun" property="selected" value="true">
					<input type="checkbox" class="compare_cb" 
					value="<bean:write name='proteinferRun' property='runId'/>"
					checked="checked" />
				</logic:equal>
			</td>
			<td>
				<html:link action="viewProteinInferenceResult.do" paramId="pinferId" paramName="proteinferRun" paramProperty="runId">
					<bean:write name="proteinferRun" property="runId" />
				</html:link>
			</td>
			<td class="left_align">
				<bean:write name="proteinferRun" property="programDisplayName" />
			</td>
			
			<td>
				<logic:iterate name="proteinferRun" property="projectIdList" id="projectId">
					<html:link action="viewProject.do" paramId="ID" paramName="projectId">
						<bean:write name="projectId" />
					</html:link>
					&nbsp;
				</logic:iterate>
			</td>
			
			<td>
				<logic:notEmpty name="proteinferRun" property="runDate">
					<bean:write name="proteinferRun" property="runDate" />
				</logic:notEmpty>
			</td>
			
			<td class="left_align">
				<bean:write name="proteinferRun" property="comments" />
			</td>
		</yrcwww:colorrow>
	</logic:iterate>
	</tbody>
	</table>
</logic:notEmpty>	

<br>

<div align="center">
	<br>
	<input type="checkbox" id="grpProts" value="group" checked="checked" />Group Indistinguishable Proteins<br>
	
	 <input type="button" class="plain_button" value="Compare" onClick="compareSelectedProtInfer()">
</div>

<br>

	
</yrcwww:contentbox>
</CENTER>


<%@ include file="/includes/footer.jsp" %>