<%@ page language="java" pageEncoding="ISO-8859-1"%>
<%@page import="org.yeastrc.ms.domain.protinfer.ProteinferInput.InputType"%>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/yrc-www.tld" prefix="yrcwww" %>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
    <title>Available Results</title>
	<link REL="stylesheet" TYPE="text/css" HREF="<yrcwww:link path='css/global.css'/>">
  </head>
  
  <body>
  
<%@ include file="/includes/errors.jsp" %>

<script src="<yrcwww:link path='js/jquery-1.4.2.js'/>" > </script>

<script type="text/javascript">
	
	$(document).ready(function() {
	
		$("#addInputButton").click(function() {
			var selectedInputGroups = getSelectedInputGroups();
			//alert(selectedInputGroups);
			
			<logic:equal name="inputType" value="<%=InputType.SEARCH.name() %>">
				// SEARCHES
				window.opener.addSearches(selectedInputGroups);
			</logic:equal>
			
			<logic:equal name="inputType" value="<%=InputType.ANALYSIS.name() %>">
				// ANALYSES
				window.opener.addAnalyses(selectedInputGroups);
			</logic:equal>
			
			window.close();
			return false;
		});
		$("#cancel").click(function() {
			window.close();
			return false;
		});
		$(".fileViewer").click(function() {
			toggleFileVisibility($(this));						
		});
	});
	
	function getSelectedInputGroups() {
		var selectedGroups = "";
		var checkboxes = $('input:checkbox:checked').each(function() {
			selectedGroups += ','+$(this).attr("id");
		});
		if (selectedGroups.length > 0) {
			selectedGroups = selectedGroups.substring(1);
		}
		return selectedGroups;
	}
	
	function toggleFileVisibility(button) {
		var searchId = button.attr('id');
		if(button.text() == "Hide Files") {
			button.text("Show Files");
			$("#files_"+searchId).hide();
		}
		else if(button.text() == "Show Files") {
			button.text("Hide Files");
			$("#files_"+searchId).show();
		}
	}
	
</script>

<center>

<bean:define id="title" name="program"/>

<div style="margin:10;">	
<yrcwww:contentbox scheme="project" title='<%="Available " +title+ " results" %>'>

<center>
<logic:notEmpty name="projectInputGroups">
	
	<table cellpadding="0" cellspacing="0" width="80%">
	<thead>
		<yrcwww:colorrow scheme="project">
		<th width="5%"></th>
		<th style="font-size:9pt;" width="25%" align="left">Project ID</th>
		
		<logic:equal name="inputType" value="<%=InputType.SEARCH.name() %>">
			<th style="font-size:9pt;" width="25%" align="left">Search ID</th>
		</logic:equal>
		
		<logic:equal name="inputType" value="<%=InputType.ANALYSIS.name() %>">
			<th style="font-size:9pt;" width="25%" align="left">Analysis ID</th>
		</logic:equal>
		
		<th style="font-size:9pt;" width="25%" align="left">Upload Date</th>
		<th width="20%"></th>
		</yrcwww:colorrow>
	</thead>
	<tbody>
	<logic:iterate name="projectInputGroups" id="inputGroup">
		<yrcwww:colorrow scheme="project">
			<td><input type="checkbox" name="search_cb" id="<bean:write name="inputGroup" property="inputGroupId" />" /></td>
			<td><bean:write name="inputGroup" property="projectId" /></td>
			<td><bean:write name="inputGroup" property="inputGroupId" /></td>
			<td><bean:write name="inputGroup" property="uploadDate" /></td>
			<td style="cursor: pointer; text-decoration: underline; font-size:8pt;" 
			    class="fileViewer" 
			    id="<bean:write name="inputGroup" property="inputGroupId" />">Hide Files</td>
		</yrcwww:colorrow>
		<yrcwww:colorrow scheme="project" repeat="true">
			<td colspan=5">
				<div id="files_<bean:write name="inputGroup" property="inputGroupId" />">
				<table style="margin-top: 3px; margin-bottom: 3px; margin-left:25px;" width="95%"/>
					<logic:iterate name="inputGroup" property="files" id="file">
						<tr><td><bean:write name="file" property="runName" /></td></tr>
					</logic:iterate>
				</table>
				</div>
			</td>
		</yrcwww:colorrow>
	</logic:iterate>
	</tbody>
	</table>
	
</logic:notEmpty>

<logic:empty name="projectInputGroups">
	<logic:equal name="inputType" value="<%=InputType.ANALYSIS.name() %>">
		No search analyses found.
	</logic:equal>
	<logic:equal name="inputType" value="<%=InputType.SEARCH.name() %>">
		No searches found.
	</logic:equal>
	<br>
	<input type="button" class="plain_button" value="Cancel" id="cancel"/>
</logic:empty>

<br>
<logic:notEmpty name="projectInputGroups">
<input type="button" class="plain_button" value="Add" id="addInputButton" />
<input type="button" class="plain_button" value="Cancel" id="cancel" />
</logic:notEmpty>

</center>
</yrcwww:contentbox>
</div>
</center> 	
    
</body>
</html>
