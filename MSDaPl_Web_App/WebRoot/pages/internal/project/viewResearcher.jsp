<%@ taglib uri="/WEB-INF/yrc-www.tld" prefix="yrcwww" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>

<yrcwww:notauthenticated>
 <logic:forward name="authenticate" />
</yrcwww:notauthenticated>

<logic:empty name="researcher">
  <logic:forward name="viewResearcher" />
</logic:empty>
 
<%@ include file="/includes/header.jsp" %>

<yrcwww:contentbox title="View Researcher" centered="true" width="650" scheme="project">

 <CENTER>
 <TABLE CELLPADDING="no" CELLSPACING="0" WIDTH="75%">
  
  <!--
  <yrcwww:colorrow>
   <TD valign="top" width="25%">ID:</TD>
   <TD valign="top" width="75%"><bean:write name="researcher" property="ID"/></TD>
  </yrcwww:colorrow>
  -->

  <yrcwww:colorrow>
   <TD valign="top" width="35%">First Name:</TD>
   <TD valign="top" width="65%"><bean:write name="researcher" property="firstName"/></TD>
  </yrcwww:colorrow>

  <yrcwww:colorrow>
   <TD valign="top" width="35%">Last Name:</TD>
   <TD valign="top" width="65%"><bean:write name="researcher" property="lastName"/></TD>
  </yrcwww:colorrow>

  <yrcwww:colorrow>
   <TD valign="top" width="35%">Email:</TD>
   <TD valign="top" width="65%"><a href="mailto:<bean:write name="researcher" property="email"/>"><bean:write name="researcher" property="email"/></a></TD>
  </yrcwww:colorrow>

	<!--
  <yrcwww:colorrow>
   <TD valign="top" width="35%">Phone:</TD>
   <TD valign="top" width="65%"><bean:write name="researcher" property="phone"/></TD>
  </yrcwww:colorrow>
   -->

  <yrcwww:colorrow>
   <TD valign="top" width="35%">Degree:</TD>
   <TD valign="top" width="65%"><bean:write name="researcher" property="degree"/></TD>
  </yrcwww:colorrow>

  <yrcwww:colorrow>
   <TD valign="top" width="35%">Department:</TD>
   <TD valign="top" width="65%"><bean:write name="researcher" property="department"/></TD>
  </yrcwww:colorrow>
  
  <yrcwww:colorrow>
     <TD valign="top" width="35%">Organization:</TD>
     <TD valign="top" width="65%"><bean:write name="researcher" property="organization"/></TD>
  </yrcwww:colorrow>

  <yrcwww:colorrow>
   <TD valign="top" width="35%">State:</TD>
   <TD valign="top" width="65%"><bean:write name="researcher" property="state"/></TD>
  </yrcwww:colorrow>

  <yrcwww:colorrow>
   <TD valign="top" width="35%">Zip/Postal Code:</TD>
   <TD valign="top" width="65%"><bean:write name="researcher" property="zipCode"/></TD>
  </yrcwww:colorrow>

  <yrcwww:colorrow>
   <TD valign="top" width="35%">Country:</TD>
   <TD valign="top" width="65%"><bean:write name="researcher" property="country"/></TD>
  </yrcwww:colorrow>

	<logic:equal name="mayEdit" scope="request" value="true">
		<yrcwww:colorrow>
			<TD valign="top" align="center" colspan="2" width="100%">
				<br><br>
				[ <html:link action="editResearcher.do" paramId="id" paramName="researcher" paramProperty="ID">
				Edit Researcher Information</html:link>
				]

			</TD>
		</yrcwww:colorrow>
	</logic:equal>

 </TABLE>
 
 <br><br>

 <p><b><font style="font-size:12pt;">Projects involving this researcher:</font></b></p>
 
 <logic:notEmpty name="projects">
 <TABLE CELLPADDING="no" CELLSPACING="0" WIDTH="95%">
  
  <yrcwww:colorrow>
   <td valign="top" width="15%"><b>ID</b></td>
   <td valign="top" width="25%"><b>SUBMIT DATE</b></td>
   <td valign="top" width="60%"><b>PROJECT TITLE</b></td>
  </yrcwww:colorrow>
  
  <logic:iterate id="project" name="projects">
  
     <yrcwww:colorrow>
	  <TD valign="top" width="15%">
	   <NOBR>
		<html:link action="viewProject.do" paramId="ID" paramName="project" paramProperty="ID">
		 <bean:write name="project" property="ID"/></html:link>
	   </NOBR>
	  </TD>
	  <TD valign="top" width="25%"><bean:write name="project" property="submitDate"/></TD>
	  <TD valign="top" width="60%"><bean:write name="project" property="title"/></TD>
   </yrcwww:colorrow> 
  </logic:iterate>
  
  
 </TABLE>
 </logic:notEmpty>
 <logic:empty name="projects">
  <p>No projects found...
 </logic:empty>
 
 </CENTER>

</yrcwww:contentbox>

<%@ include file="/includes/footer.jsp" %>