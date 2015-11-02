<%@ taglib uri="/WEB-INF/yrc-www.tld" prefix="yrcwww" %>

<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>

<%@ include file="/includes/header.jsp" %>
<%@ include file="/includes/errors.jsp" %>

<logic:notPresent name="instrumentRate">
	<logic:forward name="editInstrumentRateForm"/>
</logic:notPresent>

<yrcwww:contentbox title="Edit Instrument Rate" centered="true" width="700" >
<center>
<html:form action="saveInstrumentRate" method="POST">

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
    		<logic:present name="fullEditable">
    			<html:select property="instrumentId">
					<html:optionsCollection name="instruments" value="ID" label="name"/>
				</html:select>
			</logic:present>
			<logic:notPresent name="fullEditable">
				<html:hidden property="instrumentId" />
				<bean:write name="instrumentRate" property="instrument.name"/>
			</logic:notPresent>
			
    	</td>
   </tr>
   <tr>
   		<td>Rate Type:</td>
   		<td>
   			<logic:present name="fullEditable">
	    		<html:select property="rateTypeId">
					<html:optionsCollection name="rateTypes" value="id" label="name"/>
				</html:select>
			</logic:present>
			<logic:notPresent name="fullEditable">
				<html:hidden property="rateTypeId" />
				<bean:write name="instrumentRate" property="rateType.name"/>
			</logic:notPresent>
    	</td>
   </tr>
   
   <tr>
   		<td>Time Block:</td>
   		<td>
   			<logic:present name="fullEditable">
	    		<html:select property="timeBlockId">
					<html:optionsCollection name="timeBlocks" value="id" label="displayStringLong"/>
				</html:select>
			</logic:present>
			<logic:notPresent name="fullEditable">
				<html:hidden property="timeBlockId" />
				<bean:write name="instrumentRate" property="timeBlock.displayString"/>
			</logic:notPresent>
    	</td>
   </tr>
   
   <tr>
   		<td>Rate:</td>
   		<td>
   			<logic:present name="fullEditable">
	    		<html:text property="rateString">
				</html:text>
				e.g. 155.50
			</logic:present>
			<logic:notPresent name="fullEditable">
				<html:hidden property="rateString" />
				<bean:write name="instrumentRate" property="rate"/>
			</logic:notPresent>
    	</td>
   </tr>
   
   <tr>
   		<td>Current:</td>
   		<td colspan="3">
   			<html:radio property="current" value="true">Yes</html:radio>
   			<html:radio property="current" value="false">No</html:radio>
   		</td>
   </tr>
   
   <tr>
   		<td colspan="4" align="center">
   			<html:submit>Save</html:submit>
   			<input type="button" onclick="document.location='<yrcwww:link path='viewInstrumentRates.do'/>';" value="Cancel" />
   		</td>
   		
   </tr>
   
   <logic:notPresent name="fullEditable">
   	<tr>
   		<td colspan="4" align="center" style="color:red; font-size: 8pt;">
   			This instrument rate is already associated with one or more usage blocks. You may only change its status.
   		</td>
   	</tr>
   </logic:notPresent>
   </tbody>
   
</table>

</html:form>

</center>

</yrcwww:contentbox>

<%@ include file="/includes/footer.jsp"%>