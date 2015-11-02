<%@ taglib uri="/WEB-INF/yrc-www.tld" prefix="yrcwww" %>

<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>

<%@ include file="/includes/header.jsp" %>
<%@ include file="/includes/errors.jsp" %>


<logic:notPresent name="instrumentRate">
	<logic:forward name="changeInstrumentRateForm"/>
</logic:notPresent>

<yrcwww:contentbox title="Change Instrument Rate" centered="true" width="700">
<center>
<html:form action="saveChangeInstrumentRate" method="POST">

<table border="0" cellpadding="7">

	<tbody>
	<tr>
    	<td>ID:</td>
    	<td>
    		<html:hidden property="instrumentRateId" />
    		<bean:write name="instrumentRateForm" property="instrumentRateId"/>
    	</td>
   </tr>
	<tr>
    	<td>Instrument:</td>
    	<td>
			<html:hidden property="instrumentId" />
			<bean:write name="instrumentRate" property="instrument.name"/>
			
    	</td>
   </tr>
   <tr>
   		<td>Rate Type:</td>
   		<td>
			<html:hidden property="rateTypeId" />
			<bean:write name="instrumentRate" property="rateType.name"/>
    	</td>
   </tr>
   
   <tr>
   		<td>Time Block:</td>
   		<td>
			<html:hidden property="timeBlockId" />
			<bean:write name="instrumentRate" property="timeBlock.displayString"/>
    	</td>
   </tr>
   
   <tr>
   		<td>Rate:</td>
   		<td>
    		<html:text property="rateString">
			</html:text>
			e.g. 155.50
			
    	</td>
   </tr>
   
   <tr>
   		<td>Current:</td>
   		<td colspan="3">
   			<html:hidden property="current" />
   			<bean:write name="instrumentRate" property="current"/>
   		</td>
   </tr>
   
   <tr>
   		<td colspan="4" align="center">
   			<html:submit>Save</html:submit>
   			<input type="button" onclick="document.location='<yrcwww:link path='viewInstrumentRates.do'/>';" value="Cancel" />
   		</td>
   		
   </tr>
   
  	<tr>
  		<td colspan="4" align="center" style="color:red; font-size: 8pt;">
  			Changing the rate will create a new entry in the database. The old one will be marked as obsolete.
  		</td>
  	</tr>
   </tbody>
   
</table>

</html:form>

</center>

</yrcwww:contentbox>

<%@ include file="/includes/footer.jsp"%>