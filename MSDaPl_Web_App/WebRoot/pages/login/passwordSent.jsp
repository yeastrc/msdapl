<%@ taglib uri="/WEB-INF/yrc-www.tld" prefix="yrcwww" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>


<%@ include file="/includes/header.jsp" %>

<yrcwww:contentbox title="Password Sent" centered="true" width="400">

<html:form action="sendPassword" method="POST">
  
  <P>Your username and newly generated password have been successfully sent to the email address we have on file for
  that account.
  
  <P>If you do not receive your email, or have any other problems, please <A HREF="mailto:<bean:write name="globalAdminEmailConfigCache" property="globalAdminEmail" />">email</A> us.

</html:form>

<P align="center">Need to register? <html:link href="<yrcwww:link path='pages/register/register.jsp'/>">Click here.</html:link>

</yrcwww:contentbox>

<%@ include file="/includes/footer.jsp" %>