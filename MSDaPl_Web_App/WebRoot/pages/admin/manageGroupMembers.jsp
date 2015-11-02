<%@ taglib uri="/WEB-INF/yrc-www.tld" prefix="yrcwww" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>

<yrcwww:notauthenticated>
 <logic:forward name="authenticate" />
</yrcwww:notauthenticated>

<logic:notPresent name="memberList">
  <logic:forward name="manageGroupMembers" />
</logic:notPresent>
 
<%@ include file="/includes/header.jsp" %>

<%@ include file="/includes/errors.jsp" %>

<yrcwww:contentbox title="Manage Group Members" centered="true" width="600" scheme="groups">

 <P>Listed below are members of the group: <B><bean:write name="groupName" scope="request"/></B>.

 <P>Current members:<BR>

  <TABLE BORDER="0" WIDTH="80%">

   <logic:iterate id="researcher" name="memberList">

    <TR>
     <TD WIDTH="50%">
     <html:link action="viewResearcher.do" paramId="id" paramName="researcher" paramProperty="ID">
     	<bean:write name="researcher" property="firstName"/>
     	<bean:write name="researcher" property="lastName"/></TD>
     </html:link>
     
     <TD WIDTH="50%">
      <jsp:useBean id="params" class="java.util.HashMap"/>
	   <%
	    params.put( "action", "delete");
	    params.put( "groupName", request.getAttribute("groupName"));
	    params.put( "researcherID", new Integer(((org.yeastrc.project.Researcher)(researcher)).getID()) );
       %>
       <html:link action="manageGroupMembers.do" name="params">Remove from Group</html:link>
     </TD>
    </TR>

   </logic:iterate>

  </TABLE>
  <logic:empty name="memberList">
   <B>No Members</B>
  </logic:empty>

  <HR>
  
   <CENTER>
   <P>Add a new member to this group:

   <html:form action="manageGroupMembers" method="POST">
    <html:hidden name="manageGroupMembersForm" property="groupName"/>
    <input type="hidden" name="action" value="add">
   <P>Select researcher:
        <html:select property="researcherID">
				<html:options collection="researchers" property="ID" labelProperty="listing"/>
    	</html:select><BR>
    	<html:submit value="Add to Group"/>
   </html:form>
   </CENTER>
   

</yrcwww:contentbox>

<%@ include file="/includes/footer.jsp" %>