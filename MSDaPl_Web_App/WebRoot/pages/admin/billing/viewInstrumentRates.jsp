<%@ taglib uri="/WEB-INF/yrc-www.tld" prefix="yrcwww" %>

<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>

<%@ include file="/includes/header.jsp" %>
<%@ include file="/includes/errors.jsp" %>

<logic:notPresent name="instrumentRates">
	<logic:forward name="viewInstrumentRates"/>
</logic:notPresent>

<link rel='stylesheet' type='text/css' href="<yrcwww:link path='/css/tablesorter.css'/>" />
<script type='text/javascript' src="<yrcwww:link path='/js/jquery-1.5.min.js'/>"></script>
<script type='text/javascript' src="<yrcwww:link path='/js/jquery.tablesorter.min.js'/>"></script>

<script>

$(document).ready(function() { 
	// extend the default setting to always include the zebra widget. 
    $.tablesorter.defaults.widgets = ['zebra']; 
    // extend the default setting to always sort on the second (Instrument) column 
    // column indexes are 0-based.
    $.tablesorter.defaults.sortList = [[1,0]]; 
    $("#ratelist_table").tablesorter({
    	headers: { 
            // assign the eighth column (we start counting zero) 
            7: { 
                // disable it by setting the property sorter to false 
                sorter: false 
            } 
        } 
    }); 
});

function confirmDelete(instrumentRateId) {
   if(confirm("Are you sure you want to delete this entry?")) {
   	document.location.href="<yrcwww:link path='/deleteInstrumentRate.do'/>?instrumentRateId=" + instrumentRateId;
    	return 1;
   }
}

function addInstrumentRate() {
	document.location.href="<yrcwww:link path='/viewInstrumentRateForm.do'/>";
}

</script>

<yrcwww:contentbox title="Instrument Rates">
<center>

<div style="margin:20px">
	<input type="button" value="Add New" onclick="addInstrumentRate();"/>
</div>
<div style="margin:20px; color:red; font-size:8pt;text-align:left;">
	<ul>
		<li>An instrument rate can be edited or deleted as long as it is not in use, i.e. it has not been assigned to a scheduled time block on an instrument</li>
		<li>Once an instrument rate is in use only its status (current or obsolete) can be edited</li>
		<li>In order to update the rate for an instrument, click on the "Change Rate" link.  This will make the old rate obsolete and create a new entry with the current rate.</li>
	</ul>
</div>

<div style="margin:20px; text-align:left; align:center">
	<html:form action="viewInstrumentRates" method="POST">
		<table align="center">
			<tr>
				<td>
					Instrument: 
				</td>
				<td>
					<html:select property="instrumentId">
						<html:option value="0">ALL</html:option>
						<html:optionsCollection name="instrumentList" value="ID" label="name"/>
					</html:select>
				</td>
				<td>
					Time Block: 
				</td>
				<td>
					<html:select property="timeBlockId">
						<html:option value="0">ALL</html:option>
						<html:optionsCollection name="timeBlockList" value="id" label="name"/>
					</html:select>
				</td>
			</tr>
			
			<tr>
				<td>
					Rate Type: 
				</td>
				<td>
					<html:select property="rateTypeId">
						<html:option value="0">ALL</html:option>
						<html:optionsCollection name="rateTypeList" value="id" label="name"/>
					</html:select>
				</td>
				<td>
					Current: 
				</td>
				<td>
					<html:radio property="current" value="-1">ALL</html:radio>
					<html:radio property="current" value="1">Current</html:radio>
					<html:radio property="current" value="0">Not Current</html:radio>
				</td>
			</tr>
			<tr>
				<td colspan="4" align="center">
					<input type="submit" value="Update"/>
				</td>
			</tr>
		</table>
	</html:form>
</div>

<table border="0" cellpadding="7" id="ratelist_table" class="tablesorter">
	<thead>
		<tr>
			<th><span style="padding:0 10 0 0;">ID</span></th>
			<th><span style="padding:0 10 0 0;">Instrument</span></th>
			<th><span style="padding:0 10 0 0;">Time Block</span></th>
			<th><span style="padding:0 10 0 0;">Rate Type</span></th>
			<th><span style="padding:0 10 0 0;">Rate</span></th>
			<th></th>
			<th><span style="padding:0 10 0 0;">Create Date</span></th>
			<th></th>
		</tr>
	</thead>
	
	<tbody>
	
		<logic:iterate name="instrumentRates" id="instrumentRate">
		
			<yrcwww:colorrow scheme="logs">
				<td><bean:write name="instrumentRate" property="id"/></td>
				<td><bean:write name="instrumentRate" property="instrument.name"/></td>
				<td><bean:write name="instrumentRate" property="timeBlock.displayString"/></td>
				<td><bean:write name="instrumentRate" property="rateType.name"/></td>
				<td><bean:write name="instrumentRate" property="rate"/></td>
				<logic:equal name="instrumentRate" property="current" value="true">
					<td><span style="font-weight:bold; color:green;">current</span></td>
				</logic:equal>
				<logic:equal name="instrumentRate" property="current" value="false">
					<td><span style="font-weight:bold; color:green;">-</span></td>
				</logic:equal>
				<td><bean:write name="instrumentRate" property="createDateString"/></td>
				
				<td>
					<a href="" onclick="confirmDelete('<bean:write name="instrumentRate" property="id" />'); return false;">
						<span style="color:red">[Delete]</span>
					</a>
					&nbsp;
					<html:link action="editInstrumentRateForm.do" paramId="instrumentRateId" paramName="instrumentRate" paramProperty="id">
						<span style="color:red;">[Edit]</span>
					</html:link>
					&nbsp;
					<html:link action="changeInstrumentRateForm.do" paramId="instrumentRateId" paramName="instrumentRate" paramProperty="id">
						<span style="color:red;"><nobr>[Change Rate]</nobr></span>
					</html:link>
				</td>
			</yrcwww:colorrow>
		</logic:iterate>
	</tbody>
	
</table>

<div style="margin:20px">
	<input type="button" value="Add New" onclick="addInstrumentRate();"/>
</div>

</center>

</yrcwww:contentbox>

<%@ include file="/includes/footer.jsp"%>