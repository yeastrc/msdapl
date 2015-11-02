<%@ taglib uri="/WEB-INF/yrc-www.tld" prefix="yrcwww" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>

<yrcwww:notauthenticated>
 <logic:forward name="authenticate" />
</yrcwww:notauthenticated>

<logic:empty name="run">
  <logic:forward name="uploadYatesFormAction" />
</logic:empty>

<%@ include file="/includes/header.jsp" %>

<yrcwww:contentbox title="Run Successfully Uploaded!" centered="true" width="700" scheme="ms">

 <CENTER>

<p><b>The Run was successfully uploaded to the YRC Database!</b>

<p>Below is a summary of the run from the database.  To view the run itself 
<html:link action="viewYatesRun.do" paramId="id" paramName="run" paramProperty="id"><b>click here</b></html:link>.

<p>To upload another run, <html:link action="uploadYatesFormAction.do">click here</html:link>.


<p><B>Run Information:</B><BR><BR>
 <TABLE CELLPADDING="no" CELLSPACING="0"> 
  <yrcwww:colorrow scheme="ms">
   <TD valign="top" width="25%">Bait Protein:</TD>
   <TD valign="top" width="75%">
    <logic:empty name="run" property="baitProtein">
     N/A
    </logic:empty>
    <logic:notEmpty name="run" property="baitProtein">
      <html:link action="viewProtein.do" paramId="id" paramName="run" paramProperty="baitProtein.id">
     <bean:write name="run" property="baitProtein.listing"/></html:link>
    </logic:notEmpty>
   </TD>
  </yrcwww:colorrow>

  <logic:notEmpty name="run" property="baitDesc">
  <yrcwww:colorrow scheme="ms">
   <TD valign="top" width="25%">Bait Desc.:</TD>
   <TD valign="top" width="75%"><bean:write name="run" property="baitDesc"/></TD>
  </yrcwww:colorrow>
  </logic:notEmpty>

  <yrcwww:colorrow scheme="ms">
   <TD valign="top" width="25%">Organism:</TD>
   <TD valign="top" width="75%">
    <a target="ncbi_window" href="http://www.ncbi.nlm.nih.gov/Taxonomy/Browser/wwwtax.cgi?mode=Info&id=<bean:write name="run" property="targetSpecies.id"/>">
    <i><bean:write name="run" property="targetSpecies.name" /></a></i>
   </TD>
  </yrcwww:colorrow>
  <yrcwww:colorrow scheme="ms">
   <TD valign="top" width="25%">Run Date:</TD>
   <TD valign="top" width="75%"><bean:write name="run" property="runDate"/></TD>
  </yrcwww:colorrow>
  <yrcwww:colorrow scheme="ms">
   <TD valign="top" width="25%">Project:</TD>
   <TD valign="top" width="75%">
     <html:link action="viewProject.do" paramId="ID" paramName="run" paramProperty="projectID">
     <bean:write name="run" property="project.title"/></html:link></TD>
  </yrcwww:colorrow>


 </TABLE>
 </CENTER>

</yrcwww:contentbox>

<%@ include file="/includes/footer.jsp" %>