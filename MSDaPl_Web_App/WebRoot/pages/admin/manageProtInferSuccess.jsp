<%@ taglib uri="/WEB-INF/yrc-www.tld" prefix="yrcwww" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>

<yrcwww:notauthenticated>
 <logic:forward name="authenticate" />
</yrcwww:notauthenticated>

<%@ include file="/includes/header.jsp" %>

<%@ include file="/includes/errors.jsp" %>

<center>
<logic:present name="rerunEntries">
<yrcwww:contentbox title="Queued Protein Inferences" centered="true" width="600" scheme="groups">

<table class="table_basic" align="center">

<thead>
	<tr>
		<th>Old Protein Inference ID</th>
		<th>New Protein Inference ID</th>
		<th>Project ID</th>
	</tr>
</thead>
<tbody>
	<logic:iterate name="rerunEntries" id="entry">
		<tr>
			<td>
				<a href="<yrcwww:link path='/viewProteinInferenceResult.do?'/>pinferId=<bean:write name='entry' property='oldPiRunId'/>">
					<bean:write name="entry" property="oldPiRunId"/>
				</a>
			</td>
			<td>
				<a href="<yrcwww:link path='viewProteinInferenceJob.do?'/>pinferId=<bean:write name='entry' property='newPiRunId'/>&projectId=<bean:write name='entry' property='projectId'/>">
				<bean:write name="entry" property="newPiRunId"/>
				</a>
			</td>
			<td>
				<a href="<yrcwww:link path='viewProject.do?'/>ID=<bean:write name='entry' property='projectId'/>">
				<bean:write name="entry" property="projectId"/>
				</a>
			</td>
		</tr>
	</logic:iterate>
</tbody>
</table>


</yrcwww:contentbox>
</logic:present>



<logic:present name="deleted">
<yrcwww:contentbox title="Deleted Protein Inferences" centered="true" width="600" scheme="groups">

<table class="table_basic" align="center">

<thead>
	<tr>
		<th>Protein Inference ID</th>
	</tr>
</thead>
<tbody>
	<logic:iterate name="deleted" id="entry">
		<tr>
			<td>
				<bean:write name="entry"/>
			</td>
		</tr>
	</logic:iterate>
</tbody>
</table>

</yrcwww:contentbox>
</logic:present>


</center>
<%@ include file="/includes/footer.jsp" %>