<%@ taglib uri="/WEB-INF/yrc-www.tld" prefix="yrcwww" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>


<%@ include file="/includes/header.jsp" %>

<%@ include file="/includes/errors.jsp" %>

<yrcwww:contentbox title="Forgot Password" centered="true" width="400">

<html:form action="sendPassword" method="POST">
  
  <P>To have a <I>newly generated password</I> emailed to you, simply supply your username or email address below.  If either is found
  in the database, your username and <I>newly generated password</I> will be sent to the email address we have on file for
  that user.
  
  <P>If you can not remember your username or email address with which you registered, please
   <A HREF="mailto:<bean:write name="globalAdminEmailConfigCache" property="globalAdminEmail" />">email</A> us.
    <CENTER>
	 <TABLE BORDER="0">
	  <TR>
	   <TD>Username:</TD>
	   <TD><html:text property="username" size="20" maxlength="30"/></TD>
	  </TR>

	  <TR>
	   <TD COLSPAN="2" ALIGN="center">
	    <B>-OR-</B>
	   </TD>
	  </TR>

	  <TR>
	   <TD>Email address:</TD>
	   <TD><html:text property="email" size="20" maxlength="255"/></TD>
	  </TR>

	 </TABLE>
   </CENTER> 
 <P ALIGN="center"><INPUT TYPE="submit" VALUE="Click to Send">

</html:form>

<P align="center">Need to register? <html:link href="../../pages/register/register.jsp">Click here.</html:link>

</yrcwww:contentbox>

<%@ include file="/includes/footer.jsp" %>