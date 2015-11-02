<%@ taglib uri="/WEB-INF/yrc-www.tld" prefix="yrcwww" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>

<%@ include file="/includes/header.jsp" %>

<%@ include file="/includes/errors.jsp" %>

<yrcwww:contentbox title="Search Projects Form" centered="true" width="700" scheme="groups">

<P>To search the projects in the YRC database, enter your search terms below.  Only results containing all of the terms
you entered will be returned.  The researcher names on the project are also searched.

<html:form action="searchProjects" method="POST">

 <CENTER>
 
 <html:text property="searchString" size="50"/>

 
 <P ALIGN="center"><html:submit value="Search Projects"/>

</html:form>

</yrcwww:contentbox>

<%@ include file="/includes/footer.jsp" %>