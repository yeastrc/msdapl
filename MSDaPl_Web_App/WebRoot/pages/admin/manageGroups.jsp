<%@ taglib uri="/WEB-INF/yrc-www.tld" prefix="yrcwww" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>

<yrcwww:notauthenticated>
 <logic:forward name="authenticate" />
</yrcwww:notauthenticated>

<logic:empty name="groupList">
  <logic:forward name="manageGroups" />
</logic:empty>
 
<%@ include file="/includes/header.jsp" %>

<yrcwww:contentbox title="Manage Groups" centered="true" width="600" scheme="groups">

 <P>Click on the name of the group whose members you'd like to manage:

  <UL>
   <logic:iterate id="name" name="groupList">
     <LI><html:link action="manageGroupMembers.do" paramId="groupName" paramName="name"><bean:write name="name"/></html:link>
   </logic:iterate>
  </UL>

</yrcwww:contentbox>

<%@ include file="/includes/footer.jsp" %>