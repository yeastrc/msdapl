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
<div align="center" style="margin:10; padding:10px; width:700px; border: 1px dashed gray; background:white;" >
	<b><a href="<yrcwww:link path='listProtInferJobs.do?status=pending' />">List Protein Inference Jobs</a></b>
</div>


<yrcwww:contentbox title="Re-run Protein Inferences" centered="true" width="700" scheme="groups">

<html:form name="rerunForm" type="org.yeastrc.www.proteinfer.job.RerunProteinInferenceForm" 
			action="rerunProteinInferences"
			method="post" enctype="multipart/form-data">
	<table align="center">
		<tr>
			<td>File:</td>
			<td>
				<html:file name="rerunForm" property="inputFile"></html:file>
				<br/>
				<span style="color:red;" class="small_font">File containing protein inference IDs, one per line</span>
			</td>
		</tr>
		<tr>
			<td>Delete Original:</td>
			<td>
				<html:checkbox name="rerunForm" property="deleteOriginal"></html:checkbox>
			</td>
		</tr>
		<tr>
			<td colspan="2" align="center">
				<html:submit>Re-Run</html:submit>			
			</td>
		</tr>
	</table>
</html:form>


</yrcwww:contentbox>

<br/>
<br/>


<yrcwww:contentbox title="Delete Protein Inferences" centered="true" width="700" scheme="groups">

<html:form name="deleteForm" type="org.yeastrc.www.proteinfer.job.DeleteProteinInferenceForm" 
			action="deleteProteinInferences"
			method="post" enctype="multipart/form-data">
	<table align="center">
		<tr>
			<td>File:</td>
			<td>
				<html:file name="deleteForm" property="inputFile"></html:file>
				<br/>
				<span style="color:red;" class="small_font">File containing protein inference IDs, one per line</span>
			</td>
		</tr>
		<tr>
			<td colspan="2" align="center">
				<html:submit>Delete</html:submit>			
			</td>
		</tr>
	</table>
</html:form>

</yrcwww:contentbox>

</center>


<%@ include file="/includes/footer.jsp" %>