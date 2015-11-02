
<%@ taglib uri="/WEB-INF/yrc-www.tld" prefix="yrcwww" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>

<SCRIPT LANGUAGE="JavaScript">
 function confirmDelete() {
    if(confirm("Are you sure you want to delete this protein inference job?")) {
        document.location.href="<yrcwww:link path='deleteProteinInferJob.do?'/>pinferId=<bean:write name='pinferJob' property='pinferId'/>&projectId=<bean:write name='projectId'/>";
        return 1;
    }
 }
</SCRIPT>

<yrcwww:notauthenticated>
 <logic:forward name="authenticate" />
</yrcwww:notauthenticated>

<%@ include file="/includes/header.jsp" %>

<%@ include file="/includes/errors.jsp" %>

<yrcwww:contentbox title="Protein Inference Job" width="700" >
<center>
<logic:present name="pinferJob">
<table align="center" width="80%">
<yrcwww:colorrow scheme="project">
<td>Job ID: </td>
<td><bean:write name="pinferJob" property="id"/></td>
</yrcwww:colorrow>
<yrcwww:colorrow scheme="project">
<td>Submitted By: </td>
<td>
	<bean:write name="pinferJob" property="researcher.firstName"/>
	<bean:write name="pinferJob" property="researcher.lastName"/>
</td>
</yrcwww:colorrow>
<yrcwww:colorrow scheme="project">
<td>Submitted On: </td>
<td><bean:write name="pinferJob" property="submitDate"/></td>
</yrcwww:colorrow>
<yrcwww:colorrow scheme="project">
<td>Status: </td>
<td>
	<bean:write name="pinferJob" property="statusDescription"/>
	<logic:notEqual name="pinferJob" property="status" value="1"><!-- not running -->
		[<span style="color:red; text-decoration: underline; cursor: pointer;" 
		onClick="confirmDelete()"
		>Delete</span>]
	</logic:notEqual>
</td>
</yrcwww:colorrow>
<yrcwww:colorrow scheme="project">
<td>Comments: </td>
<td><bean:write name="pinferJob" property="comments"/></td>
</yrcwww:colorrow>

<bean:define name="program" id="program" type="org.yeastrc.ms.domain.protinfer.ProteinInferenceProgram"/>
<logic:present name="params">
<yrcwww:colorrow scheme="project">
<td colspan="2" align="center"><b>Parameters</b></td>
</yrcwww:colorrow>
<logic:iterate name="params" id="param" type="org.yeastrc.ms.domain.protinfer.idpicker.IdPickerParam">
<yrcwww:colorrow  scheme="project" repeat="true">
	<td><%=program.getDisplayNameForParam(param.getName()) %></td>
	<td><bean:write name="param" property="value"/></td>
</yrcwww:colorrow>
</logic:iterate>
</logic:present>

<yrcwww:colorrow ><td colspan="2" align="center"><b>Input</b></td></yrcwww:colorrow>
<logic:iterate name="inputList" id="input">
<yrcwww:colorrow scheme="project" repeat="true">
<td colspan="2"><bean:write name="input" property="fileName" /></td>
</yrcwww:colorrow>
</logic:iterate>

<logic:present name="pinferJob" property="log">
<yrcwww:colorrow scheme="project">
<td colspan="2" align="center"><b>Log</b></td>
</yrcwww:colorrow>
<yrcwww:colorrow  scheme="project" repeat="true">
	<td colspan="2" align="left"><pre style="font-size:8pt;"><bean:write name="pinferJob" property="log"/></pre></td>
</yrcwww:colorrow>


</logic:present>

</table>
</logic:present>
</center>
</yrcwww:contentbox>

<%@ include file="/includes/footer.jsp" %>