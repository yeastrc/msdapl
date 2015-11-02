
<%@page import="org.yeastrc.grant.Grant"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<%@ taglib uri="/WEB-INF/yrc-www.tld" prefix="yrcwww" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>


<html>
   <head>
    <title></title>
	<link REL="stylesheet" TYPE="text/css" HREF="<yrcwww:link path='css/global.css'/>">
	
<script type="text/javascript">
	function updateGrant(grantID, grantTitle, piID, PI, sourceType, sourceName, grantNumber, grantAmount) {
		window.opener.updateGrant(grantID, grantTitle, piID, PI, sourceType, sourceName, grantNumber, grantAmount);
		window.close();
	}
</script>

  </head>
  
  <body>
  <%@ include file="/includes/errors.jsp" %>
  
	 <yrcwww:contentbox title="Grant Saved!" width="500">
	 	<center>
	 		<table>
	 			<bean:define name="grant" property="fundingSource.sourceType.displayName" id="sourceType" />
	 			<bean:define name="grant" property="fundingSource.sourceName.displayName" id="sourceName" /> 
	 			<tr>
	 				<td style="padding:5px;" WIDTH="25%" VALIGN="top">Title:</td>
	 				<td style="padding:5px;" WIDTH="75%" VALIGN="top"><bean:write name="grant" property="title" /></td>
	 			</tr>
	 			<tr>
	 				<td style="padding:5px;" WIDTH="25%" VALIGN="top">PI:</td>
	 				<td style="padding:5px;" WIDTH="75%" VALIGN="top"><bean:write name="grant" property="grantPI.lastName" /></td>
	 			</tr>
	 			<tr>
	 				<td style="padding:5px;" WIDTH="25%" VALIGN="top">Funding Source:</td>
	 				<td style="padding:5px;" WIDTH="75%" VALIGN="top"><bean:write name="sourceType" /></td>
	 				
	 			</tr>
	 			<tr>
	 				<td style="padding:5px;" WIDTH="25%" VALIGN="top">Funding Source Name:</td>
	 				<td style="padding:5px;" WIDTH="75%" VALIGN="top"><bean:write name="sourceName" /></td>
	 			</tr>
	 			
	 			<tr>
	 				<td style="padding:5px;" WIDTH="25%" VALIGN="top">Grant Number:</td>
	 				<td style="padding:5px;" WIDTH="75%" VALIGN="top"><bean:write name="grant" property="grantNumber" /></td>
	 			</tr>
	 			<tr>
	 				<td style="padding:5px;" WIDTH="25%" VALIGN="top">Annual Funds:</td>
	 				<td style="padding:5px;" WIDTH="75%" VALIGN="top"><bean:write name="grant" property="grantAmount" /></td>
	 			</tr>
	 			
	 			<tr>
	 				<td colspan="2"" align="center">
	 					<%
	 						Grant grant = (Grant)request.getAttribute("grant");
	 						int grantID = grant.getID();
	 						String title = escapeQuotes(grant.getTitle());
	 						int piID = grant.getGrantPI().getID();
	 						String piLastName = escapeQuotes(grant.getGrantPI().getLastName());
	 						String grantNum = escapeQuotes(grant.getGrantNumber());
	 						String grantAmt = escapeQuotes(grant.getGrantAmount());
	 						String sourcetype = escapeQuotes((String)sourceType);
	 						String sourcename = escapeQuotes((String)sourceName);
	 						
	 					 %>
	 					 <%!
	 					 	public String escapeQuotes(String str) {
	 							str = str.replaceAll("'", "\\\\'");
	 							return str;
	 						}
	 					  %>
	 					<button onClick="javascript:updateGrant(<%=grantID%>, '<%=title%>', <%=piID%>, '<%=piLastName%>', '<%=sourcetype%>', '<%=sourcename%>', '<%=grantNum%>', '<%=grantAmt%>')">Done</button>
	 				</td>
	 			</tr>
	 		</table>
	 	</center>
	 </yrcwww:contentbox>   
<%@ include file="/includes/footer.jsp" %>

