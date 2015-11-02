<%@ taglib uri="/WEB-INF/yrc-www.tld" prefix="yrcwww" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>


<%@ include file="/includes/header.jsp" %>

<%@ include file="/includes/errors.jsp" %>

<yrcwww:contentbox title="User Login" centered="true" width="400">

<html:form action="login" method="POST">
  <CENTER>
   <P>

	 <TABLE BORDER="0">
	  <TR>
	   <TD>Username:</TD>
	   <TD><html:text property="username" size="20" maxlength="30"/></TD>
	  </TR>

	  <TR>
	   <TD>Password:</TD>
	   <TD><html:password property="password" size="20" maxlength="30"/></TD>
	  </TR>

	 </TABLE>
   </CENTER> 
 <P ALIGN="center"><INPUT TYPE="submit" VALUE="Click to Login">

</html:form>


<logic:equal name="forgotPasswordConfigCache" property="forgotPasswordConfigInitialized" value="true">

  <logic:equal name="forgotPasswordConfigCache" property="forgotPasswordConfigured" value="true">

	<P align="center">Forgot your password? <a href="<yrcwww:link path='pages/login/forgotPassword.jsp'/>">Click here.</a>

  </logic:equal>
</logic:equal>

<P align="center">Not registered? <html:link action="viewRegister.do">Click here.</html:link>

</yrcwww:contentbox>
<%@ include file="/includes/footer.jsp" %>