<%@ taglib uri="/WEB-INF/yrc-www.tld" prefix="yrcwww" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>

<yrcwww:notauthenticated>
 <logic:forward name="authenticate" />
</yrcwww:notauthenticated>

<logic:notPresent name="instrumentList">
  <logic:forward name="manageInstruments" />
</logic:notPresent>
 
<%@ include file="/includes/header.jsp" %>

<%@ include file="/includes/errors.jsp" %>

<yrcwww:contentbox title="MS Instruments" centered="true" width="700" scheme="groups">

	<div style="text-align:center; margin: 20 auto;">
	<html:link action="viewAllInstrumentCalendar.do">View Instrument Calendar</html:link>
	</div>
	
   <logic:empty name="instrumentList">
   		No instruments found in the database. Click <html:link action="addInstrument.do"><b>here</b></html:link> to add an instrument.
   </logic:empty>
   
   <script>
		function addInstrument() { document.location.href = "<yrcwww:link path='addInstrument.do' />";}
		function confirmDelete(id, name) {
    		if(confirm("Are you sure you want to delete "+name+"?")) {
          		document.location.href="<yrcwww:link path='deleteInstrument.do?instrumentId='/>" + id;
          		return 1;
    		}
		}
	</script>
	
   <logic:notEmpty name="instrumentList">
   		<table class="sortable table_basic stripe_table" width="98%">
   		<thead>
   			<tr>
   			<th>ID</th>
   			<th>Name</th>
   			<th>Description</th>
   			<th></th>
   			<th></th>
   			</tr>
   		</thead>
   		<tbody>
   			<logic:iterate id="instrument" name="instrumentList">
   				<tr>
   				<td><bean:write name="instrument" property="id"/></td>
   				<td class="left_align"><bean:write name="instrument" property="name"/></td>
   				<td class="left_align"><bean:write name="instrument" property="description"/></td>
   				<td><font color="red">
   					<html:link action="/editInstrument.do" paramId="instrumentId" paramName="instrument" paramProperty="id"><font color="green">Edit</font></html:link></font></td>
   				<td>
   					<span class="clickable underline" onClick="confirmDelete('<bean:write name="instrument" property="id"/>', '<bean:write name="instrument" property="name"/>')">
   					<font color="red">Delete</font>
   					</span>
   				</td>
   				</tr>
   			</logic:iterate>
   		</tbody>
   		</table>
   </logic:notEmpty>
   
	<div align="center">
		<input type="button" class="plain_button" value="Add Instrument" onClick="addInstrument()">
	</div>

</yrcwww:contentbox>

<%@ include file="/includes/footer.jsp" %>