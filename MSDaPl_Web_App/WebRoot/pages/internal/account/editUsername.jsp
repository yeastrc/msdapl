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
 <P align="center"><B>Your username was successfully updated.</B>
</logic:present>

<yrcwww:contentbox title="Change Your Username" centered="true" width="600">
 <CENTER>
  <html:form action="saveUsername" method="post">
  <TABLE CELLPADDING="no" CELLSPACING="0" border="0">
  
   <TR>
    <TD VALIGN="top"><B>New Username:</B></TD>
    <TD VALIGN="top"><html:text property="username" size="30" maxlength="255"/></TD>
   </TR>

   <TR>
    <TD VALIGN="top"><B>Verify Username:</B></TD>
    <TD VALIGN="top"><html:text property="username2" size="30" maxlength="255"/></TD>
   </TR>

  </TABLE>

  <P><html:submit value="SAVE"/>
  
  </html:form>
  
 </CENTER>
</yrcwww:contentbox>

<%@ include file="/includes/footer.jsp" %>