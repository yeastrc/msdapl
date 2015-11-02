<%@ taglib uri="/WEB-INF/yrc-www.tld" prefix="yrcwww" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>

<logic:notPresent name="projectsSearch" scope="session">
  <logic:redirect forward="projectSearchForm" />
</logic:notPresent>

<%@ include file="/includes/header.jsp" %>

<%@ include file="/includes/errors.jsp" %>


<yrcwww:contentbox title="Search Projects Results" centered="true" width="750" scheme="groups">

<logic:notEmpty name="projectsSearch" scope="session">

<div style="margin: 10 10 10 10;">
<table border="0" width="100%" class="table_basic">
 
 <thead>
 <tr>
  <th><b><html:link action="sortProjectSearch.do?sortby=id">ID</html:link></b></th>
  <th><b><html:link action="sortProjectSearch.do?sortby=pi">PI</html:link></b></th>
  <th><b><html:link action="sortProjectSearch.do?sortby=title">Title</html:link></b></th>
  <th><b><html:link action="sortProjectSearch.do?sortby=submit"><font style="font-size:8pt;">Submit Date</font></html:link></b></th>
  <th><b><html:link action="sortProjectSearch.do?sortby=change"><font style="font-size:8pt;">Changed</font></html:link></b></th>
  </tr>
 </thead>
 
 <tbody>
<logic:iterate id="project" name="projectsSearch" scope="session">
 <tr>
  <TD valign="top" width="5%">
   <NOBR>
    <html:link action="viewProject.do" paramId="ID" paramName="project" paramProperty="ID">
     <bean:write name="project" property="ID"/></html:link>
   </NOBR>
  </TD>
  <TD valign="top" width="13%">
   <logic:present name="project" property="PI"><bean:write name="project" property="PI.lastName"/></logic:present>
  </TD>
  <TD valign="top" width="43%"><bean:write name="project" property="title"/></TD>
  <TD valign="top" width="12%"><bean:write name="project" property="submitDate"/></TD>
  <TD valign="top" width="12%"><bean:write name="project" property="lastChange"/></TD>
  </tr>
</logic:iterate>
</tbody>
</table>
</div>

</logic:notEmpty>

<logic:empty name="projectsSearch" scope="session">
<B>Found 0 matches to your query...</B>
</logic:empty>
 


</yrcwww:contentbox>

<%@ include file="/includes/footer.jsp" %>