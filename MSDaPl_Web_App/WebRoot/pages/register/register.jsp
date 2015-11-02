<%@ taglib uri="/WEB-INF/yrc-www.tld" prefix="yrcwww" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>

<logic:notPresent name="states" scope="session">
 <logic:forward name="viewRegister"/>
</logic:notPresent>

<%@ include file="/includes/header.jsp" %>


<yrcwww:contentbox title="Registration Form" centered="true" width="700">

<P>In order to request collaborations, dissemination of plasmids or other interractions
with the YRC, we are required to collect the following information.

<P>Once you have registered, you will not be required to supply this information when
requesting future collaborations.

<P><B>Note:</B> If you have previously collaborated with the YRC, you are already in our database and do not need to register.  Please proceed
to our <a href="<yrcwww:link path='/pages/login/forgotPassword.jsp'/>">forgot password page</a>, and enter your email address.  A username and password will
be sent to you.

<%@ include file="/includes/errors.jsp" %>

<html:form action="register" method="POST">

 <yrcwww:contentbox title="About You" width="400">
	 <TABLE BORDER="0">
	  <TR>
	   <TD>First Name:</TD>
	   <TD><html:text property="firstName" size="20" maxlength="255"/></TD>
	  </TR>

	  <TR>
	   <TD>Last Name:</TD>
	   <TD><html:text property="lastName" size="20" maxlength="255"/></TD>
	  </TR>

	  <TR>
	   <TD>Email:</TD>
	   <TD><html:text property="email" size="30" maxlength="255"/></TD>
	  </TR>

	  <TR>
	   <TD>Degree Level:</TD>
	   <TD>
		<html:select property="degree">
			<html:option value="no_answer">Choose highest degree</html:option>
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
	   <TD>Your Organization:</TD>
	   <TD><html:text property="organization" size="30" maxlength="255"/></TD>
	  </TR>

	  <TR>
	   <TD>Department:</TD>
	   <TD><html:text property="department" size="30" maxlength="255"/></TD>
	  </TR>

	  <TR>
	   <TD>State:</TD>
	   <TD>
	    <html:select property="state">
	     <html:option value="No">If in US, choose state:</html:option>
	     <html:options collection="states" property="code" labelProperty="name"/>
	    </html:select>
	   </TD>
	  </TR>

	  <TR>
	   <TD>Zip/Postal Code:</TD>
	   <TD><html:text property="zip" size="15" maxlength="255"/></TD>
	  </TR>

	  <TR>
	   <TD>Country:</TD>
	   <TD>
	    <html:select property="country">
	     <html:options collection="countries" property="code" labelProperty="name"/>
	    </html:select>
	   </TD>
	  </TR>

	 </TABLE>
 </yrcwww:contentbox>
  <P>
 <yrcwww:contentbox title="Login Information" width="400">
	 <TABLE BORDER="0">

	  <TR>
	   <TD>Choose a username:</TD>
	   <TD><html:text property="username" size="15" maxlength="20"/></TD>
	  </TR>

	  <TR>
	   <TD>Choose a password:</TD>
	   <TD><html:password property="password" size="15" maxlength="20"/></TD>
	  </TR>

	  <TR>
	   <TD>Confirm password:</TD>
	   <TD><html:password property="password2" size="15" maxlength="20"/></TD>
	  </TR>

	 </TABLE>
 </yrcwww:contentbox>
 
 <P ALIGN="center"><INPUT TYPE="submit" VALUE="Click to Register">

</html:form>

</yrcwww:contentbox>

<%@ include file="/includes/footer.jsp" %>