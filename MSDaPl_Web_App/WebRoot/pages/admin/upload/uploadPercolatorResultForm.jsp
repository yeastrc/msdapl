
<%@page import="org.yeastrc.www.upload.Pipeline"%>
<%@ taglib uri="/WEB-INF/yrc-www.tld" prefix="yrcwww" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>

<%@ include file="/includes/header.jsp" %>

<%@ include file="/includes/errors.jsp" %>
<logic:notPresent name="uploadPercolatorResultForm" scope="request">
	<logic:forward name="percolatorUploadForm" />
</logic:notPresent>

<script language="javascript">

function onCancel(projectId, experimentId) {
	if(projectId == 0) {
		document.location = "<yrcwww:link path='viewFrontPage.do' />";
	}
	else {
		document.location = "<yrcwww:link path='viewProject.do?ID=' />"+projectId;
	}
		
}
</script>

<yrcwww:contentbox title="Upload Percolator Results" centered="true" width="700" scheme="upload">

<html:form action="uploadPercolatorResult" method="POST">

 <CENTER>
  <table border="0">
  
   <tr>
    <td valign="top"><b>Project ID:</b></td>
    <td valign="top"><bean:write name="uploadPercolatorResultForm" property="projectId"/><html:hidden property="projectId"/></td>
   </tr>
   
   <tr>
    <td valign="top"><b>Experiment ID:</b></td>
    <td valign="top"><bean:write name="uploadPercolatorResultForm" property="experimentId"/><html:hidden property="experimentId"/></td>
   </tr>
   
    <tr>
    <td valign="top"><b>Data directory:</b></td>
    <td valign="top"><html:text property="directory" size="50" maxlength="255"/>
    <br/>
    <span class="small_font">This should be the path to the directory that contains Percolator generated .sqt files or combined-results.xml.</span></td>
    </td>
   </tr>
   
   <tr>
    <td valign="top"><b>Comments:</b></td>
    <td><html:textarea property="comments" cols="50" rows="5"/></td>
   </tr>

  </table> 
 
 <p>
 <nobr>
<html:submit styleClass="plain_button" value="Upload Data"/>
<input type="button" class="plain_button" onclick="javascript:onCancel(<bean:write name='uploadPercolatorResultForm' property='projectId' />, <bean:write name='uploadPercolatorResultForm' property='experimentId' />);" value="Cancel" />
 </nobr>
 </p>
 </CENTER>

</html:form>

</yrcwww:contentbox>

<%@ include file="/includes/footer.jsp" %>