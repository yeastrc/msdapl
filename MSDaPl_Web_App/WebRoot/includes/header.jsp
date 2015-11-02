<%@ taglib uri="/WEB-INF/yrc-www.tld" prefix="yrcwww" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>

<%
response.setHeader("Pragma", "No-cache");
response.setHeader("Cache-Control","no-cache");
response.setDateHeader("Expires", 0);
response.addHeader("Cache-control", "no-store"); // tell proxy not to cache
response.addHeader("Cache-control", "max-age=0"); // stale right away
%>

<html>

<head>
 <yrcwww:title />
 <link REL="stylesheet" TYPE="text/css" HREF="<yrcwww:link path='/css/global.css' />">
 <link REL="stylesheet" TYPE="text/css" HREF="<yrcwww:link path='/css/colorbox.css' />">
 <link REL="stylesheet" TYPE="text/css" HREF="<yrcwww:link path='/css/table_dnd.css' />">
</head>

<body>

<input type="hidden" id="webAppContextPath_WebApp_Wide" value="${ webAppContextPath }" />



<script src="<yrcwww:link path='js/jquery-1.4.2.js'/>"></script>
<script src="<yrcwww:link path='js/msdapl.js'/>"></script>
<script src="<yrcwww:link path='/js/jquery.blockUI.js'/>"></script>
<script src="<yrcwww:link path='js/tooltip.js'/>"></script>

<script>
// ---------------------------------------------------------------------------------------
// SETUP ANY BASIC TABLES
// ---------------------------------------------------------------------------------------
$(document).ready(function() {
   $(".table_basic").each(function() {
   		makeStripedTable(this);
   });
   
   $(".foldable").each(function() {
   		$(this).click(function() {
   			fold($(this));
   		});
   });
   tooltip();
});
</script>

<div id="mainContainer"> <!-- This will end in footer.jsp -->

<yrcwww:notauthenticated>
<div id="login_header" align="center">
	<table BORDER="0" WIDTH="100%" CELLPADDING="0" CELLSPACING="0">
	<tr>
	<td width="33%">&nbsp;</td>
	<td width="34%" align="center"><img src="<yrcwww:link path='images/MSDAPL_logo_big.png'/>" /></td>
	<td width="33%" align="right" valign="middle">
	<div align="right" style="padding-right:20;color:white; font-size:8pt">
    	<html:link forward="authenticate">Login</html:link>
	</div>
	</div></td>
	</tr>
	</table>
</div>
</yrcwww:notauthenticated>
   
<yrcwww:authenticated>

<div id="top_header">

<%
	String home_class = ""; boolean home_menus = false;
	String account_class = ""; boolean account_menus = false;
	String admin_class = ""; boolean admin_menus = false;
	String docs_class = ""; boolean docs_menus = false;
 %>
 
 <logic:equal name="dir" scope="request" value="internal">
 	<%home_class = "current"; home_menus = true;%>
 </logic:equal>
 <logic:equal name="dir" scope="request" value="project">
 	<%home_class = "current"; home_menus = true;%>
 </logic:equal>
 <logic:equal name="dir" scope="request" value="protein">
 	<%home_class = "current"; home_menus = true;%>
 </logic:equal>
 <logic:equal name="dir" scope="request" value="percolator">
 	<%home_class = "current"; home_menus = true;%>
 </logic:equal>
 <logic:equal name="dir" scope="request" value="stats">
 	<%home_class = "current"; home_menus = true;%>
 </logic:equal>
 <logic:equal name="dir" scope="request" value="peptideProphet">
 	<%home_class = "current"; home_menus = true;%>
 </logic:equal>
 <logic:equal name="dir" scope="request" value="sequest">
 	<%home_class = "current"; home_menus = true;%>
 </logic:equal>
  <logic:equal name="dir" scope="request" value="tide">
 	<%home_class = "current"; home_menus = true;%>
 </logic:equal>
 <logic:equal name="dir" scope="request" value="mascot">
 	<%home_class = "current"; home_menus = true;%>
 </logic:equal>
 <logic:equal name="dir" scope="request" value="xtandem">
 	<%home_class = "current"; home_menus = true;%>
 </logic:equal>
 <logic:equal name="dir" scope="request" value="yates">
 	<%home_class = "current"; home_menus = true;%>
 </logic:equal>
 <logic:equal name="dir" scope="request" value="proteinfer">
 	<%home_class = "current"; home_menus = true;%>
 </logic:equal>
 <logic:equal name="dir" scope="request" value="proteinProphet">
 	<%home_class = "current"; home_menus = true;%>
 </logic:equal>
 <logic:equal name="dir" scope="request" value="compare">
 	<%home_class = "current"; home_menus = true;%>
 </logic:equal>
 <logic:equal name="dir" scope="request" value="upload">
 	<%home_class = "current"; home_menus = true;%>
 </logic:equal>
 <logic:equal name="dir" scope="request" value="search">
 	<%home_class = "current"; home_menus = true;%>
 </logic:equal>
 <logic:equal name="dir" scope="request" value="docs">
 	<%docs_class = "current"; docs_menus = true;%>
 </logic:equal>
 
 <logic:equal name="dir" scope="request" value="account">
 	<%account_class = "current"; account_menus = true;%>
 </logic:equal>
 <logic:equal name="dir" scope="request" value="admin">
 	<%admin_class = "current"; admin_menus = true;%>
 </logic:equal>
 <logic:equal name="dir" scope="request" value="billing">
 	<%admin_class = "current"; admin_menus = true;%>
 </logic:equal>
<logic:equal name="dir" scope="request" value="payment">
 	<%home_class = "current"; home_menus = true;%>
</logic:equal>
<logic:equal name="dir" scope="request" value="scheduler">
 	<%home_class = "current"; home_menus = true;%>
</logic:equal>
 
		
<div id="main_menu">
	<ul>
		<li><a href="<yrcwww:link path='pages/internal/front.jsp'/>" title="Home" class="<%=home_class %>"><span>Home</span></a></li>
		<li><html:link  action="editInformation.do" styleClass="<%=account_class %>"><span>Account</span></html:link></li>
		<li><html:link action="manageGroups.do" styleClass="<%=admin_class %>"><span>Admin</span></html:link></li>
		<li><a href="<yrcwww:link path='pages/internal/docs/documentation.jsp'/>" 
		       title="DOCS" 
		       class="<%=docs_class %>">
		       <span>Docs</span></a></li>
	
	</ul>
	
	<div align="right" style="padding-right:20;color:white; font-size:8pt">
		<jsp:useBean id="user" class="org.yeastrc.www.user.User" scope="session"/>
    	Welcome <yrcwww:user attribute="firstname"/> <yrcwww:user attribute="lastname"/>!&nbsp;&nbsp;
    	<html:link action="logout.do">Logout</html:link>
	</div>
	
</div>

</div>

<div id="sub_header">
<div id="sub_menu">
	<ul>
   <%if(account_menus) { %>
    <li><html:link action="editInformation.do"><span>My Information</span></html:link>
    <li><html:link action="editPassword.do"><span>Password</span></html:link></li>
    <li><html:link action="editUsername.do"><span>Username</span></html:link></li>
   <%} %>
   <%if(home_menus) { %>
        <li><html:link action="newProject.do"><span>New Project</span></html:link></li>
        <yrcwww:member group="any">
   			<li><html:link action="uploadMSDataFormAction.do"><span>Upload Data</span></html:link></li>
   			<li><html:link action="listUploadJobs.do"><span>List Uploads</span></html:link></li>
   			<li><html:link action="availableFasta.do"><span>Available FASTA</span></html:link></li>
   			<li><a href="<yrcwww:link path='pages/admin/search/searchProjects.jsp'/>" title="Search Projects" class="<%=home_class %>"><span>Search Projects</span></a></li>
   			<li><html:link action="viewAllInstrumentCalendar.do"><span>Instrument Calendar</span></html:link></li>
   		</yrcwww:member>
   <%} %>
   <%if(admin_menus) { %>
      <yrcwww:member group="administrators">
        <li><html:link action="manageGroups.do"><span>Manage Groups</span></html:link></li>
      </yrcwww:member>
      <yrcwww:member group="administrators">
        <li><html:link action="manageInstruments.do"><span>Instruments</span></html:link></li>
      </yrcwww:member>
      <yrcwww:member group="administrators">
        <li><html:link action="manageBilling.do"><span>Billing</span></html:link></li>
      </yrcwww:member>
      <yrcwww:member group="administrators">
        <li><html:link action="manageProteinInferences.do"><span>Manage Protein Inferences</span></html:link></li>
      </yrcwww:member>
   <%} %>
   <%if(docs_menus) { %>
   		<li><a href="<yrcwww:link path='pages/internal/docs/documentation.jsp'/>" title="Help Topics" class="<%=docs_class %>"><span>Help Topics</span></a></li>
   		<li><a href="<yrcwww:link path='pages/internal/docs/updates.jsp'/>" title="Updates" class="<%=docs_class %>"><span>Updates</span></a></li>
   <%} %>
  </ul>
</div>
</div>

<div id="history"><yrcwww:history/></div>
</yrcwww:authenticated>

<br><br>