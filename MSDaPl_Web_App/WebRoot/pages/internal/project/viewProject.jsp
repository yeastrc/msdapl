<%@ taglib uri="/WEB-INF/yrc-www.tld" prefix="yrcwww" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>

<yrcwww:notauthenticated>
 <logic:forward name="authenticate" />
</yrcwww:notauthenticated>

<logic:notPresent name="project">
  <logic:forward name="viewProject"  />
</logic:notPresent>
 
<jsp:useBean id="project" class="org.yeastrc.project.Project" scope="request"/>

<%@ include file="/includes/header.jsp" %>

<%@ include file="/includes/errors.jsp" %>

<script src="<yrcwww:link path='js/jquery.colorbox.js'/>"></script>

<yrcwww:contentbox title="Project Details" centered="true" width="95" widthRel="true">

<SCRIPT LANGUAGE="JavaScript">
 function confirmDelete(ID) {
    if(confirm("Are you sure you want to delete this project?")) {
       if(confirm("Are you ABSOLUTELY sure you want to delete this project?")) {
          document.location.href="<yrcwww:link path='deleteProject.do?ID='/>" + ID;
          return 1;
       }
    }
 }
 
</SCRIPT>

 <CENTER>
 <TABLE CELLPADDING="no" CELLSPACING="0" width="90%">
  
  <yrcwww:colorrow>
   <TD valign="top" width="25%">ID:</TD>
   <TD valign="top" width="75%"><bean:write name="project" property="ID"/></TD>
  </yrcwww:colorrow>

  <yrcwww:colorrow>
   <TD valign="top" width="25%">Title:</TD>
   <TD valign="top" width="75%"><bean:write name="project" property="title"/></TD>
  </yrcwww:colorrow>
  
  <yrcwww:colorrow>
   <TD valign="top" width="25%">Affiliation:</TD>
   <TD valign="top" width="75%"><bean:write name="project" property="affiliation"/></TD>
  </yrcwww:colorrow>

  <!-- List the Researchers here: -->

	<bean:define id="pi" name="project" property="PI" scope="request"/>

	<yrcwww:colorrow>
		<TD valign="top" width="25%">PI:</TD>
		<TD valign="top" width="75%">
		<html:link action="viewResearcher.do" paramId="id" paramName="pi" paramProperty="ID">
		    <bean:write name="pi" property="firstName"/> <bean:write name="pi" property="lastName"/>
		    <logic:notEmpty name="pi" property="degree">
		    	, <bean:write name="pi" property="degree"/>
		    </logic:notEmpty>
		 </html:link>
		</TD>
	</yrcwww:colorrow>
	
	<logic:iterate name="project" property="researchers" id="researcher">
		<yrcwww:colorrow>
			<TD valign="top" width="25%">Researcher :</TD>
			<TD valign="top" width="75%">
			<html:link action="viewResearcher.do" paramId="id" paramName="researcher" paramProperty="ID">
				<bean:write name="researcher" property="firstName"/> <bean:write name="researcher" property="lastName"/>
				<logic:notEmpty name="researcher" property="degree">
				, <bean:write name="researcher" property="degree"/>
				</logic:notEmpty>
			</html:link>
			</TD>
		</yrcwww:colorrow>
	</logic:iterate>
	
	
	<!-- ========================================================================================= -->
	<!-- List Grants here -->
	<%@ include file="grantList.jsp" %>
	<!-- ========================================================================================= -->
	
	<!-- ========================================================================================= -->
	<!-- List Payment methods here -->
	<yrcwww:colorrow>
	<TD valign="top" width="25%" style="padding:10px 0;">Payment Methods:</TD>
	<TD valign="top" width="75%" style="padding:10px 0;">
		<%@ include file="paymentMethods.jsp" %>
	</td>
	</yrcwww:colorrow>
	<!-- ========================================================================================= -->
	
  <yrcwww:colorrow>
   <TD valign="top" width="25%">Abstract:</TD>
   <TD valign="top" width="75%"><bean:write name="project" property="abstractAsHTML" filter="false"/></TD>
  </yrcwww:colorrow>

  <yrcwww:colorrow>
   <TD valign="top" width="25%">Progress/Results:</TD>
   <TD valign="top" width="75%"><bean:write name="project" property="progressAsHTML" filter="false"/></TD>
  </yrcwww:colorrow>

<logic:notEmpty name="project" property="comments">
  <yrcwww:colorrow>
   <TD valign="top" width="25%">Comments:</TD>
   <TD valign="top" width="75%"><bean:write name="project" property="commentsAsHTML" filter="false"/></TD>
  </yrcwww:colorrow>
</logic:notEmpty>

<logic:notEmpty name="project" property="publications">
  <yrcwww:colorrow>
   <TD valign="top" width="25%">Publications:</TD>
   <TD valign="top" width="75%"><bean:write name="project" property="publicationsAsHTML" filter="false"/></TD>
  </yrcwww:colorrow>
</logic:notEmpty>

  <yrcwww:colorrow>
   <TD valign="top" width="25%">Submit Date:</TD>
   <TD valign="top" width="75%"><bean:write name="project" property="submitDate"/></TD>
  </yrcwww:colorrow>

  <yrcwww:colorrow>
   <TD valign="top" width="25%">Last Updated:</TD>
   <TD valign="top" width="75%"><bean:write name="project" property="lastChange"/></TD>
  </yrcwww:colorrow>

 </TABLE>
 
<script>

	function goMSUpload() { document.location.href = "<yrcwww:link path='uploadMSDataFormAction.do?'/>projectID=<bean:write name="project" property="ID" scope="request" />"; }
	function goEdit() { document.location.href = "<yrcwww:link path='editProject.do?ID='/><bean:write name="project" property="ID" scope="request" />"; }
</script>

 
  <div>
  	<logic:equal name="writeAccess" value="true" scope="request">
  		<input type="button" class="plain_button" value="Edit Project" onClick="goEdit()">
	</logic:equal>
	
	<logic:equal name="showMSDataUpload" value="true" scope="request">
	  	<input type="button" class="plain_button" value="Upload Data" onClick="goMSUpload()">
	</logic:equal>

	<yrcwww:member group="administrators">
	  	<input type="button" class="error_button" value="Delete Project" onClick="confirmDelete('<bean:write name="project" property="ID"/>')">
	</yrcwww:member>
 </div>

 </CENTER>
</yrcwww:contentbox>

<br><br>
<!-- List experiment data here -->
<%@ include file="listExperiments.jsp" %>
<br>

<script src="<yrcwww:link path='js/experimentDetailsPrecursorScanCountChart.js'/>"></script>

<script src="<yrcwww:link path='js/experimentDetailsPeakCountChart.js'/>"></script>

<script src="<yrcwww:link path='js/experimentDetailsIntensityCountChart.js'/>"></script>

<%-- Google Chart API import, for use on experimentDetails.jsp  --%>
<script type="text/javascript" src="https://www.google.com/jsapi"></script>
<script type="text/javascript">
  google.load("visualization", "1", {packages:["corechart"]});
  
  var googleOnLoadCallbackFunction = function() {
	  
	  createAllInitialDisplayPrecursorScanCountCharts();
	  createAllInitialDisplayPeakCountCharts();
	  createAllInitialDisplayIntensityCountCharts();
  }
  
  //  Do NOT call a method on an object here.  The "this" gets set to the window.
  google.setOnLoadCallback(googleOnLoadCallbackFunction);
</script>


<%@ include file="/includes/footer.jsp" %>

