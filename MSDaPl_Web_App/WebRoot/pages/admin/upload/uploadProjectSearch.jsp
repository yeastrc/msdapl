<%@ taglib uri="/WEB-INF/yrc-www.tld" prefix="yrcwww" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>

<HTML>
	 <head><title>Mini Project Searcher</title>
	 <link REL="stylesheet" TYPE="text/css" HREF="<yrcwww:link path='css/global.css'/>">

	<script>

		function useProject(project) {
			window.opener.currentProjectField.value = project;
			window.close();	
		}
	</script>
</head>

<body>

<input type="hidden" id="webAppContextPath_WebApp_Wide" value="${ webAppContextPath }" />

<yrcwww:contentbox title="Mini Project Searcher" centered="true" width="550" scheme="upload">
<center>

<p align="center">Fill out the form below to search projects.</p>

<html:form action="miniProjectSearch" method="POST">

 <html:text style="font-size:8pt;" property="searchString" size="50"/>
 <html:hidden property="types" value="C" />
 <html:hidden property="types" value="Tech" />
 <html:hidden property="groups" value="Yates" />
 <html:hidden property="groups" value="MacCoss" />
 
<center>
	 <html:submit style="background-color:#FFBF59;color:#000000;font-size:10pt;font-weight:bold;margin-top:10px;" value="Search Projects"/>
	 <input type="button" style="background-color:#FFBF59;color:#000000;font-size:10pt;font-weight:bold;margin-top:10px;" value="Cancel" onClick="window.close()">
</center>

</html:form>


<logic:notEmpty name="projectsSearch">

	<table border="0" width="100%">

		<tr>
			<th style="font-size:10pt;" valign="top">&nbsp;</td>
			<th style="font-size:10pt;" valign="top" align="left">ID</td>
			<th style="font-size:10pt;" valign="top" align="left">PI</td>
			<th style="font-size:10pt;" valign="top" align="left">Title</td>
		</tr>

		<logic:iterate name="projectsSearch" id="project">
		
			<yrcwww:colorrow scheme="upload">
				<td width="10%" valign="top" style="font-size:8pt;"><a href="javascript:useProject('<bean:write name="project" property="ID" />')"><font style="font-size:8pt;color:red;">[Select]</font></a></td>
				<td width="10%" valign="top" style="font-size:8pt;"><bean:write name="project" property="ID" /></td>
				<td width="20%" valign="top" style="font-size:8pt;"><logic:present name="project" property="PI"><bean:write name="project" property="PI.lastName"/></logic:present></td>
				<td width="60%" valign="top" style="font-size:8pt;"><bean:write name="project" property="title" /></td>
			</yrcwww:colorrow>
		
		
		</logic:iterate>

	</table>

</logic:notEmpty>



</center>
</yrcwww:contentbox>

<%@ include file="/includes/footer.jsp" %>