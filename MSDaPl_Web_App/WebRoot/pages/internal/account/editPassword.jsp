<%@ taglib uri="/WEB-INF/yrc-www.tld" prefix="yrcwww" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>

<yrcwww:notauthenticated>
 <logic:forward name="authenticate" />
</yrcwww:notauthenticated>

<%@ include file="/includes/header.jsp" %>

<%@ include file="/includes/errors.jsp" %>
<logic:present name="saved" scope="request">
 <P align="center"><B>Your password was successfully updated.</B>
</logic:present>

<yrcwww:contentbox title="Change Your Password" centered="true" width="600">
 <P>To change your password, fill out this form.  Passwords are limited to 20 characters.
 <CENTER> 
  <html:form action="savePassword" method="post">
  <TABLE CELLPADDING="no" CELLSPACING="0" border="0">
  
   <TR>
    <TD VALIGN="top"><B>New Password:</B></TD>
    <TD VALIGN="top"><html:password property="password" size="20" maxlength="20"/></TD>
   </TR>

   <TR>
    <TD VALIGN="top"><B>Verify Password:</B></TD>
    <TD VALIGN="top"><html:password property="password2" size="20" maxlength="20"/></TD>
   </TR>

  </TABLE>

  <P><html:submit value="SAVE"/>
  
  </html:form>
  
 </CENTER>
</yrcwww:contentbox>

<%@ include file="/includes/footer.jsp" %>