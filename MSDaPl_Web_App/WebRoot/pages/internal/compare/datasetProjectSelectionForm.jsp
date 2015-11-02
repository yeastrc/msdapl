
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
function selectProjects() {
	var projectIds = "";
	var forDisplay = "\n";
	var i = 0;
	$("input.project_cb:checked").each(function() {
		if(i > 0) {
			projectIds += ",";
		}
		projectIds += $(this).val();
		forDisplay += $(this).val()+"\n";
		i++;
	});
	if(i < 1) {
		alert("Please select at least one project");
		return false;
	}
	
	var url = "<yrcwww:link path='selectComparisonDatasets.do?'/>"+
	"projectIds="+projectIds+
	"&piRunIds=<bean:write name='piRunIds'/>";
	
	//alert(url);
	window.location.href = url;
}

function cancel(projectId) {
	var url = "<yrcwww:link path='viewProject.do?'/>"+"ID="+projectId;
	window.location.href = url;
}
$(document).ready(function() {
   $(".sorttable").each(function() {
   		makeSortableTable($(this));
   });
});

</script>

<CENTER>

<yrcwww:contentbox centered="true" title="Available Projects" width="700">


<!-- Proceed only if we have available datasets -->

<logic:empty name="userProjects">

	<div align="center" ><b>There are no available projects at this time.</b></div>

</logic:empty>


<logic:notEmpty name="userProjects">

	<div align="center"><b>Please select one more projects. 
	Protein inference IDs from the selected projects will be displayed on the next page.</b></div>
	<br>

	<table width="90%" class="table_basic sorttable" align="center">
	<thead>
		<tr align="left">
			<th></th>
			<th class="sort-int sortable">Project ID</th>
			<th class="sort-alpha sortable">Title</th>
			<th>Date</th>
		</tr>
	</thead>
	<tbody>
	
		<logic:iterate name="userProjects" id="project">
		<tr>
			<td>
				<input type="checkbox" class="project_cb" 
					value="<bean:write name='project' property='ID'/>" />
			</td>
			<td>
				<html:link action="viewProject.do" paramId="ID" paramName="project" paramProperty="ID">
					<bean:write name="project" property="ID" />
				</html:link>
			</td>
			<td class="left_align">
				<bean:write name="project" property="title" />
			</td>
			
			<td>
				<logic:notEmpty name="project" property="submitDate">
					<bean:write name="project" property="submitDate" />
				</logic:notEmpty>
			</td>
		</tr>	
	</logic:iterate>
	</tbody>
	</table>
</logic:notEmpty>	

<br>

<div align="center">
	 <input type="button" class="plain_button" value="Select" onClick="selectProjects()">
	 <input type="button" class="plain_button" value="Cancel" onClick="cancel(<bean:write name='projectId'/>)">
</div>

<br>

	
</yrcwww:contentbox>
</CENTER>


<%@ include file="/includes/footer.jsp" %>