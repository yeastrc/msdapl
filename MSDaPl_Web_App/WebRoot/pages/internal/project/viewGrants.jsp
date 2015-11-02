<%@ page language="java" pageEncoding="ISO-8859-1"%>
<%@page import="org.yeastrc.grant.Grant"%>

<%@ taglib uri="/WEB-INF/yrc-www.tld" prefix="yrcwww" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
    <title>Grants</title>
	<link REL="stylesheet" TYPE="text/css" HREF="<yrcwww:link path='css/global.css'/>">
  </head>
  
  <body>
  
<%@ include file="/includes/errors.jsp" %>

<script type="text/javascript">
	
	function addGrantsToProject() {
		var checkboxes = document.getElementsByName("Grant_CheckBox");
		for (var i = 0; i < checkboxes.length; i++) {
			if (checkboxes[i].checked) {
				var grantInfo = checkboxes[i].value.split(",");
				window.opener.addGrant(grantInfo[0], grantInfo[1], grantInfo[2], grantInfo[3], grantInfo[4], grantInfo[5], grantInfo[6], grantInfo[7]);
			}
		}
		window.close();
	}
	
	<% String PIs = request.getParameter("PIs"); %>
	
	function sortGrants(sortCrit) {
		var url = "<yrcwww:link path='viewGrants.do?PIs='/><%=PIs%>&sortby="+sortCrit;
		var selectedGrants = getSelectedGrants();
		if (selectedGrants.length > 0) {
			url += '&selectedGrants='+selectedGrants;
		}
		//alert("url is "+url);
		document.location = url;
	}
	
	function addNewGrant() {
		var url = "<yrcwww:link path='editGrant.do?PIs='/><%=PIs%>";
		var selectedGrants = getSelectedGrants();
		if (selectedGrants.length > 0) {
			url += '&selectedGrants='+selectedGrants;
		}
		//alert("url is "+url);
		document.location = url;
	}
	
	function getSelectedGrants() {
		var selectedGrants = "";
		var checkboxes = document.getElementsByName("Grant_CheckBox");
		for (var i = 0; i < checkboxes.length; i++) {
			if (checkboxes[i].checked) {
				selectedGrants += ','+checkboxes[i].id;
			}
		}
		if (selectedGrants.length > 0) {
			selectedGrants = selectedGrants.substring(1);
		}
		return selectedGrants;
	}
	
</script>

<center>
<div class="project_header" style="width:90%; margin-top:40px;">
	<center>Grants</center>
</div>
<div class="project" style="width:90%">
	<logic:notEmpty name="grants">
		<table style="margin:5px">
		<yrcwww:colorrow>
			<td style="display:none;"></td>
			<td></td>
			<td style="font-size:8pt; padding:5px;"><b><nobr><a href='javascript:sortGrants("title")'>Grant Title</a></nobr></b></td>
			<td style="font-size:8pt; padding:5px;"><b><nobr><a href='javascript:sortGrants("pi")'>PI</a></nobr></b></td>
			<td style="font-size:8pt; padding:5px;"><b><nobr><a href='javascript:sortGrants("sourceType")'>Source Type</a></nobr></b></td>
			<td style="font-size:8pt; padding:5px;"><b><nobr><a href='javascript:sortGrants("sourceName")'>Source Name</a></nobr></b></td>
			<td style="font-size:8pt; padding:5px;"><b><nobr><a href='javascript:sortGrants("grantNum")'>Grant #</a></nobr></b></td>
			<td style="font-size:8pt; padding:5px;"><b><nobr><a href='javascript:sortGrants("grantAmount")'>Annual Funds</a></nobr></b></td>
			<td></td>
		</yrcwww:colorrow>
		<logic:iterate name="grants" id="grant">
		<yrcwww:colorrow>
			
			<% String checked = ""; if (((Grant)grant).isSelected()) checked = "checked"; %>
						
			<bean:define name="grant" property="fundingSource.sourceType.displayName" id="sourceType" />
			<bean:define name="grant" property="fundingSource.sourceName.displayName" id="sourceName" />
			
			<td style="display:none;"><bean:write name="grant" property="ID" /></td>
			<td><input type="checkbox" name="Grant_CheckBox" <%=checked%> id="<bean:write name="grant" property="ID" />" value="<bean:write name="grant" property="ID" />,<bean:write name="grant" property="title" />,<bean:write name="grant" property="grantPI.ID" />,<bean:write name="grant" property="grantPI.lastName" />,<bean:write name="sourceType" />,<bean:write name="sourceName" />,<bean:write name="grant" property="grantNumber" />,<bean:write name="grant" property="grantAmount" />"/></td>
			<td style="font-size:8pt; padding:5px;"><bean:write name="grant" property="title" /></td>
			<td style="font-size:8pt; padding:5px;"><bean:write name="grant" property="grantPI.lastName" /></td>
			<td style="font-size:8pt; padding:5px;"><bean:write name="sourceType" /></td>
			<td style="font-size:8pt; padding:5px;"><bean:write name="sourceName" /></td>
			<td style="font-size:8pt; padding:5px;"><bean:write name="grant" property="grantNumber" /></td>
			<td style="font-size:8pt; padding:5px;"><bean:write name="grant" property="grantAmount" /></td>
		</yrcwww:colorrow>
		</logic:iterate>
		</table>
		<div align="center"><button onclick="javascript:addGrantsToProject();">Done</button></div>
	</logic:notEmpty>
	
	
	<logic:empty name="grants">
		No grants found!!
	</logic:empty>
	
	<br><br><br>
	<div class="project_header" style="width: 120px; padding: 5px;"><a href="javascript:addNewGrant();" style="text-decoration: none; color:white; font-weight: bold; font-size: 12px;">Add New Grant</a></div>
</div>

</center> 	
    
