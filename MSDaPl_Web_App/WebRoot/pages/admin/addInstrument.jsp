<%@ taglib uri="/WEB-INF/yrc-www.tld" prefix="yrcwww" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>

<yrcwww:notauthenticated>
 <logic:forward name="authenticate" />
</yrcwww:notauthenticated>

<%@ include file="/includes/header.jsp" %>

<%@ include file="/includes/errors.jsp" %>

<yrcwww:contentbox title="Add MS Instrument" centered="true" width="700" scheme="groups">

<html:form action="saveInstrument.do">

<html:hidden name="addInstrumentForm" property="id"/>

<table align="center" width="98%" class="table_basic">
<tr>
	<td class="left_align"><b>Instrument Name:</b>
	</td><td class="left_align"><html:text name="addInstrumentForm" property="name" size="75"></html:text></td>
</tr>
<tr>
	<td class="left_align"><b>Description:</b></td>
	<td class="left_align"><html:text name="addInstrumentForm" property="description" size="75"></html:text></td>
</tr>
</table>
<div align="center">
<html:submit styleClass="plain_button">Save</html:submit>
<script>
	function cancel() { document.location.href = "<yrcwww:link path='manageInstruments.do' />";}
</script>
<input type="button" value="Cancel" class="plain_button" onclick="cancel()"/>
</div>
</html:form>


</yrcwww:contentbox>

<%@ include file="/includes/footer.jsp" %>