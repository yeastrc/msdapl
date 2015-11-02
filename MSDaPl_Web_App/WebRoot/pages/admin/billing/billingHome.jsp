<%@ taglib uri="/WEB-INF/yrc-www.tld" prefix="yrcwww" %>

<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>

<%@ include file="/includes/header.jsp" %>
<%@ include file="/includes/errors.jsp" %>

<link rel='stylesheet' type='text/css' href="<yrcwww:link path='/css/jquery_ui/ui-lightness/jquery-ui-1.8.12.custom.css'/>" />
<script type='text/javascript' src="<yrcwww:link path='/js/jquery-1.5.min.js'/>"></script>
<script type='text/javascript' src="<yrcwww:link path='/js/jquery-ui-1.8.12.custom.min.js'/>"></script>


<logic:notPresent name="billedProjects">
	<logic:forward name="costCenterHome"/>
</logic:notPresent>

<script>
	$(function() {
		$( ".datepicker" ).datepicker();
	});
	
	function confirmExportInvoice() {
	
		var msg = "Are you sure you want to generate an invoice? ";
		msg += "Any instrument time included in an invoice will be marked as \"billed\" and can no longer be edited. ";
		var agree = confirm(msg);
		
		if(agree) 
			return true;
		else
			return false;
	}
	
</script>

<yrcwww:contentbox title="Billing">
<center>
<div style="width:90%";" align="left">
<table style="margin: 0 auto;">

	<tr>
		<td><b>Time Blocks:</b></td>
		<td><html:link action="viewTimeBlocks.do?showall=true">[View All]</html:link></td>
		<td><html:link action="viewTimeBlockForm">[Add New Block]</html:link></td>
	</tr>
	
	<tr>
		<td><b>Instrument Rates:</b></td>
		<td><html:link action="viewInstrumentRates.do">[View All]</html:link></td>
		<td><html:link action="viewInstrumentRateForm.do">[Add New]</html:link></td>
	</tr>
	
</table>

<html:form action="exportBillingInformation.do" method="POST">
<table width="90%" style="margin: 0 auto;">
	<tbody>
		<tr>
			<td colspan="3"  style="padding-top:50px;"><b>Export Billing Information</b></td>
		</tr>
		<tr>
			<td align="left">Start Date:</td>
			<td>
				<html:text name="exportBillingInformationForm" property="startDateString" styleClass="datepicker"></html:text>
				<span style="font-size:8pt;">e.g. 04/29/2011</span>
			</td>
		</tr>
		<tr>
			<td align="left">End Date:</td>
			<td>
				<html:text name="exportBillingInformationForm" property="endDateString" styleClass="datepicker"></html:text>
			</td>
		</tr>
		<tr>
			<td align="left">Select Project:</td>
			<td colspan="3">
				<html:select name="exportBillingInformationForm" property="projectId">
					<html:option value="0">All Billed Projects</html:option>
					<html:optionsCollection name="billedProjects" value="ID" label="label"/>
				</html:select>
			</td>
		</tr>
		
		<tr>
				<td colspan="4" align="center" style="padding:10px 10px 0 10px;">
				<!-- Summarize <html:checkbox name="exportBillingInformationForm" property="summarize"></html:checkbox>-->
				<button type="submit" value="true" name="exportOnly">Export</button>
				</td>
		</tr>

<!-- 
		<tr>
			<td colspan="4" style="padding:0 0 20px 0">
				<span style="color:black; font-size:8pt;">
					Check  the "Summarize" checkbox to combine entries for the same project, instrument and payment method into a single entry. 
				</span>
			</td>
		</tr>
-->
<!-- 
		<tr>
			<td colspan="4" style="border-width:1px 1px 0 1px; border-style:dashed; border-color:black; padding:5px;" align="center">
				<button type="submit" value="true" name="exportInvoice" onclick="return confirmExportInvoice();">Get Invoice</button>
			</td>
		</tr>
		
		
		<tr>
			<td colspan="4" style="border-width:0 1px 1px 1px; border-style:dashed; border-color:black; padding: 5px;" >
				<span style="color:red; font-size:8pt; font-weight:bold;">
					Click "Get Invoice" only at the end of a billing cycle.  
					Any instrument time included in an invoice will be marked as "billed" and can no longer be edited. 
					Click the "Export" button instead to simply view the instrument time that will be billed
					in a given time period without marking it as "billed".
				</span>
			</td>
			
		</tr>
-->
		
	</tbody>
</table>
</html:form>
</div>
</center>
</yrcwww:contentbox>

<%@ include file="/includes/footer.jsp"%>