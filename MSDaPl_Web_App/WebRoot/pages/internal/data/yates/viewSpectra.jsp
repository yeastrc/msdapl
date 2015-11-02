
<%@page import="java.net.URL"%><%@ taglib uri="/WEB-INF/yrc-www.tld" prefix="yrcwww" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>

<yrcwww:notauthenticated>
 <logic:forward name="authenticate" />
</yrcwww:notauthenticated>

<logic:empty name="result">
  <logic:forward name="viewSpectra" />
</logic:empty>

<%@ include file="/includes/header.jsp" %>

<yrcwww:contentbox title="View Peptide Spectra" centered="true" width="1000" scheme="ms">

<center>
<table border="0">

  <yrcwww:colorrow scheme="ms">
   <TD valign="top" width="25%">Organism:</TD>
   <TD valign="top" width="75%"><i><bean:write name="result" property="hitProtein.species.name"/></i></TD>
  </yrcwww:colorrow>

  <yrcwww:colorrow scheme="ms">
   <TD valign="top" width="25%">Project:</TD>
   <TD valign="top" width="75%">
     <html:link action="viewProject.do" paramId="ID" paramName="run" paramProperty="projectID">
     <bean:write name="result" property="project.title"/></html:link></TD>
  </yrcwww:colorrow>

  <yrcwww:colorrow scheme="ms">
   <TD valign="top" width="25%">MS Run:</TD>
   <TD valign="top" width="75%">
     <html:link action="viewYatesRun.do" paramId="id" paramName="run" paramProperty="id">
     View Run</html:link></TD>
  </yrcwww:colorrow>

  <yrcwww:colorrow scheme="ms">
   <TD valign="top" width="25%">Hit Protein:</TD>

   <TD>
				<nobr>
				 <html:link action="viewProtein.do" paramId="id" paramName="result" paramProperty="hitProtein.id">				 
				  <bean:write name="result" property="hitProtein.listing"/></html:link>
				</nobr>
   </TD>

  </yrcwww:colorrow>

  <yrcwww:colorrow scheme="ms">
   <TD valign="top" width="25%">Peptide List:</TD>
   <TD valign="top" width="75%">
     <html:link action="viewYatesResult.do" paramId="id" paramName="result" paramProperty="id">
     View Peptide List</html:link></TD>
  </yrcwww:colorrow>


 <yrcwww:colorrow scheme="ms">
  <td>Sequence:</td>
  <td><bean:write name="peptide" property="HTMLSequence" filter="false"/></td>
 </yrcwww:colorrow>
</table>

<p><table border=0 ALIGN="CENTER">
 <TR><TD><B>Mass: <FONT COLOR="green"><bean:write  name="firstMass"/></B></TD>
  <TD colspan="2"><B>Datfile: <FONT COLOR="green"><bean:write  name="datfile"/></B></TD>
  <TD colspan="2"><B>Scan number: <FONT COLOR="green"><bean:write  name="scanNumber"/></B></TD>
  <TD><B>Charge: <FONT COLOR="green"><bean:write  name="firstCharge"/></B></TD>
  <TD colspan="2"><B>Database: <FONT COLOR="green"><bean:write  name="database"/></TD>
 </TR>

 <TR>
  <TD colspan="8" ALIGN="center">
  <% String baseUrl = new URL(request.getScheme(), request.getServerName(), request.getServerPort(), request.getContextPath()).toString(); %>
   <applet 
   		code="ed.SpectrumViewerApp.SpectrumApplet.class"
   		archive="SpectrumApplet.jar" 
   		CODEBASE="<%=baseUrl %>/applets" 
   		width=970 
   		height=500>
    <logic:iterate name="params" id="param" scope="request">
     <bean:write name="param" filter="false" />
    </logic:iterate>
   </applet>
  </TD>
 </TR>
</table>

</yrcwww:contentbox>

<%@ include file="/includes/footer.jsp" %>