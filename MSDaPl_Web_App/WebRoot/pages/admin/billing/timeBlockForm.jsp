<%@ taglib uri="/WEB-INF/yrc-www.tld" prefix="yrcwww" %>

<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>

<%@ include file="/includes/header.jsp" %>
<%@ include file="/includes/errors.jsp" %>


<yrcwww:contentbox title="Add New Time Block" centered="true" width="700">
<center>
<html:form action="addTimeBlock" method="POST">

<table border="0" cellpadding="7">

	<tbody>
	<tr>
		<td>Num. Hours: </td>
		<td><html:text property="numHoursString"></html:text></td>
	</tr>
	<tr>
    	<td>Start Time:</td>
    	<td>
    		H:
    		<html:select property="startTimeHour">
    		<html:option value="0">Select</html:option>
			<html:option value="1">1</html:option>
			<html:option value="2">2</html:option>
			<html:option value="3">3</html:option>
			<html:option value="4">4</html:option>
			<html:option value="5">5</html:option>
			<html:option value="6">6</html:option>
			<html:option value="7">7</html:option>
			<html:option value="8">8</html:option>
			<html:option value="9">9</html:option>
			<html:option value="10">10</html:option>
			<html:option value="11">11</html:option>
			<html:option value="12">12</html:option>
			</html:select>
    	
    		M:
    		<html:select property="startTimeMin">
			<html:option value="00">00</html:option>
			<html:option value="15">15</html:option>
			<html:option value="30">30</html:option>
			<html:option value="45">45</html:option>
			</html:select>
    	
    		<html:select property="startTimeAmPm">
			<html:option value="AM">AM</html:option>
			<html:option value="PM">PM</html:option>
			</html:select>
    	</td>
   </tr>
   
   <tr>
   		<td>Name:</td>
   		<td><html:text property="name"/></td>
   
   </tr>
   <tr>
   		<td colspan="2" align="center">
   			<html:submit>Save</html:submit>
   			<input type="button" onclick="document.location='<yrcwww:link path='/viewTimeBlocks.do'/>';" value="Cancel" />
   		</td>
   		
   </tr>
   </tbody>
   
</table>

</html:form>

</center>

</yrcwww:contentbox>

<%@ include file="/includes/footer.jsp"%>