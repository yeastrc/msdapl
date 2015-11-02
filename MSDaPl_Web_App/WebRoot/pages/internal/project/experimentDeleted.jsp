<%@ taglib uri="/WEB-INF/yrc-www.tld" prefix="yrcwww" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>


<%@ include file="/includes/header.jsp" %>

<yrcwww:contentbox title="Experiment Deleted" centered="true" width="400">

<P>The experiment (ID: <bean:write name="experimentId"/>) was successfully marked for deletion.</P>
<P align="center"><html:link action="viewProject.do" paramId="ID" paramName="projectId">Back to project.</html:link></P>


</yrcwww:contentbox>

<%@ include file="/includes/footer.jsp" %>