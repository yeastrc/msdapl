<%@ taglib uri="/WEB-INF/yrc-www.tld" prefix="yrcwww" %>

<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>

<%@ include file="/includes/header.jsp" %>
<%@ include file="/includes/errors.jsp" %>


<yrcwww:contentbox title="Add New Instrument Rate">
<center>
<html:form action="addInstrumentRate" method="POST">

<table border="0" cellpadding="7">

	<tbody>
	<tr>
    	<td>Instrument:</td>
    	<td>
    		<html:select property="instrumentId">
				<html:optionsCollection name="instruments" value="ID" label="name"/>
			</html:select>
    	</td>
   </tr>
   <tr>
   		<td>Rate Type:</td>
   		<td>
    		<html:select property="rateTypeId">
				<html:optionsCollection name="rateTypes" value="id" label="name"/>
			</html:select>
    	</td>
   </tr>
   
   <tr>
   		<td>Time Block:</td>
   		<td>
    		<html:select property="timeBlockId">
				<html:optionsCollection name="timeBlocks" value="id" label="displayStringLong"/>
			</html:select>
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
   		<td colspan="4" align="center">
   			<html:submit>Save</html:submit>
   			<input type="button" onclick="document.location='<yrcwww:link path='viewInstrumentRates.do'/>';" value="Cancel" />
   		</td>
   		
   </tr>
   </tbody>
   
</table>

</html:form>

</center>

</yrcwww:contentbox>

<%@ include file="/includes/footer.jsp"%>