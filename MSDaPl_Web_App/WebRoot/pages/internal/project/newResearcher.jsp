<%@ taglib uri="/WEB-INF/yrc-www.tld" prefix="yrcwww" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>

<yrcwww:notauthenticated>
 <logic:forward name="authenticate" />
</yrcwww:notauthenticated>

<logic:notPresent name="editResearcherForm" scope="request">
 <logic:forward name="newResearcher"/>
</logic:notPresent>

<%@ include file="/includes/header.jsp" %>

<%@ include file="/includes/errors.jsp" %>
<logic:present name="saved" scope="request">
 <P align="center"><B>The researcher was successfully added to the database.</B>
</logic:present>

<yrcwww:contentbox title="Create a new Researcher" centered="true" width="600" scheme="search">

 <CENTER>

  Fill out this form to enter a new researcher into our database.

  <p><html:form action="saveResearcher" method="post">
  <TABLE CELLPADDING="no" CELLSPACING="0" border="0">
  
   <TR>
    <TD VALIGN="top"><B>First name:</B></TD>
    <TD VALIGN="top"><html:text property="firstName" size="20" maxlength="255"/></TD>
   </TR>

   <TR>
    <TD VALIGN="top"><B>Last name:</B></TD>
    <TD VALIGN="top"><html:text property="lastName" size="20" maxlength="255"/></TD>
   </TR>

   <TR>
    <TD VALIGN="top"><B>Email address:</B></TD>
    <TD VALIGN="top"><html:text property="email" size="40" maxlength="255"/></TD>
   </TR>

   <TR>
    <TD VALIGN="top"><B>Degree:</B></TD>
    <TD VALIGN="top">
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
    <TD VALIGN="top"><B>Department:</B></TD>
    <TD VALIGN="top"><html:text property="department" size="40" maxlength="255"/></TD>
   </TR>

   <TR>
    <TD VALIGN="top"><B>Organization:</B></TD>
    <TD VALIGN="top"><html:text property="organization" size="40" maxlength="255"/></TD>
   </TR>

   <TR>
    <TD VALIGN="top"><B>State:</B></TD>
	<TD VALIGN="top">
	    <html:select property="state">
	     <html:option value="No">If in US, choose state:</html:option>
	     <html:options collection="states" property="code" labelProperty="name"/>
	    </html:select>
	</TD>
   </TR>

   <TR>
    <TD VALIGN="top"><B>Zip/Postal Code:</B></TD>
    <TD VALIGN="top"><html:text property="zipCode" size="20" maxlength="255"/></TD>
   </TR>

   <TR>
    <TD VALIGN="top"><B>Country:</B></TD>
	   <TD VALIGN="top">
	    <html:select property="country">
	     <html:options collection="countries" property="code" labelProperty="name"/>
	    </html:select>
	   </TD>
   </TR>
   
   <TR>
    <TD COLSPAN="2">
     <html:checkbox property="sendEmail"/> Check here if you would like the system to create and send a
     username and password to this researcher.  If you choose NOT to do this, they will be able to do this
     themselves at a later date.
    </TD>
   </TR>

  </TABLE>

  <P><html:submit value="SAVE"/>
  
  </html:form>
  
 </CENTER>
</yrcwww:contentbox>

<%@ include file="/includes/footer.jsp" %>