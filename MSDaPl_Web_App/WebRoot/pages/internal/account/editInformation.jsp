<%@ taglib uri="/WEB-INF/yrc-www.tld" prefix="yrcwww" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>

<yrcwww:notauthenticated>
 <logic:forward name="authenticate" />
</yrcwww:notauthenticated>

<logic:notPresent name="editInformationForm" scope="request">
 <logic:forward name="editInformation"/>
</logic:notPresent>

<%@ include file="/includes/header.jsp" %>

<%@ include file="/includes/errors.jsp" %>
<logic:present name="saved" scope="request">
 <P align="center"><B>Your information was successfully updated.</B>
</logic:present>

<yrcwww:contentbox title="Change Your Information" centered="true" width="600">
 <CENTER>
  <html:form action="saveInformation" method="post">
  <TABLE CELLPADDING="no" CELLSPACING="0" border="0">
  
   <TR>
    <TD WIDTH="25%" VALIGN="top"><B>First name:</B></TD>
    <TD WIDTH="75%" VALIGN="top"><html:text property="firstName" size="20" maxlength="255"/></TD>
   </TR>

   <TR>
    <TD WIDTH="25%" VALIGN="top"><B>Last name:</B></TD>
    <TD WIDTH="75%" VALIGN="top"><html:text property="lastName" size="20" maxlength="255"/></TD>
   </TR>

   <TR>
    <TD WIDTH="25%" VALIGN="top"><B>Email address:</B></TD>
    <TD WIDTH="75%" VALIGN="top"><html:text property="email" size="40" maxlength="255"/></TD>
   </TR>

   <TR>
    <TD WIDTH="25%" VALIGN="top"><B>Degree:</B></TD>
    <TD WIDTH="75%" VALIGN="top">
     <html:select property="degree">
      <html:option value="Ph.D.">Ph.D.</html:option>
	  <html:option value="M.S.">M.S.</html:option>
	  <html:option value="M.A.">M.A.</html:option>
	  <html:option value="B.S.">B.S.</html:option>
	  <html:option value="B.A.">B.A.</html:option>
	  <html:option value="M.D., Ph.D.">M.D., Ph.D.</html:option>
	  <html:option value="M.D.">M.D.</html:option>
	  <html:option value="D.M.D.">D.M.D.</html:option>
	  <html:option value="D.V.M.">D.V.M.</html:option>
	  <html:option value="D.D.S.">D.D.S.</html:option>
	  <html:option value="O.D.">O.D.</html:option>
	  <html:option value="not_listed">not listed</html:option>
	  <html:option value="none">none</html:option>
	 </html:select>
    </TD>
   </TR>

   <TR>
    <TD WIDTH="25%" VALIGN="top"><B>Department:</B></TD>
    <TD WIDTH="75%" VALIGN="top"><html:text property="department" size="40" maxlength="255"/></TD>
   </TR>

   <TR>
    <TD WIDTH="25%" VALIGN="top"><B>Organization:</B></TD>
    <TD WIDTH="75%" VALIGN="top"><html:text property="organization" size="40" maxlength="255"/></TD>
   </TR>

   <TR>
    <TD WIDTH="25%" VALIGN="top"><B>State:</B></TD>
	<TD WIDTH="75%" VALIGN="top">
	    <html:select property="state">
	     <html:option value="No">If in US, choose state:</html:option>
	     <html:options collection="states" property="code" labelProperty="name"/>
	    </html:select>
	</TD>
   </TR>

   <TR>
    <TD WIDTH="25%" VALIGN="top"><B>Zip/Postal Code:</B></TD>
    <TD WIDTH="75%" VALIGN="top"><html:text property="zipCode" size="20" maxlength="255"/></TD>
   </TR>

   <TR>
    <TD WIDTH="25%" VALIGN="top"><B>Country:</B></TD>
	<TD WIDTH="75%" VALIGN="top">
	    <html:select property="country">
	     <html:options collection="countries" property="code" labelProperty="name"/>
	    </html:select>
	</TD>
   </TR>

  </TABLE>

  <P><html:submit value="SAVE"/>
  
  </html:form>
  
 </CENTER>
</yrcwww:contentbox>

<%@ include file="/includes/footer.jsp" %>