<%@ taglib uri="/WEB-INF/yrc-www.tld" prefix="yrcwww" %>

<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>

<%@ include file="/includes/header.jsp" %>
<%@ include file="/includes/errors.jsp" %>

<logic:notPresent name="instrumentRates">
	<logic:forward name="viewCurrentInstrumentRates"/>
</logic:notPresent>

<link REL="stylesheet" TYPE="text/css" HREF="/pr/css/tablesorter.css">
<script type='text/javascript' src='/pr/js/jquery-1.5.min.js'></script>
<script type='text/javascript' src='/pr/js/jquery.tablesorter.min.js'></script>

<script>

$(document).ready(function() { 
	// extend the default setting to always include the zebra widget. 
    $.tablesorter.defaults.widgets = ['zebra']; 
    // extend the default setting to always sort on the second (Instrument) column 
    // column indexes are 0-based.
    $.tablesorter.defaults.sortList = [[1,0]]; 
    $("#ratelist_table").tablesorter(); 
});


</script>

<yrcwww:contentbox title="Instrument Rates">
<center>

<table border="0" cellpadding="7" id="ratelist_table" class="tablesorter">
	<thead>
		<tr>
			<th><span style="padding:0 10 0 0;">ID</span></th>
			<th><span style="padding:0 10 0 0;">Instrument</span></th>
			<th><span style="padding:0 10 0 0;">Time Block</span></th>
			<th><span style="padding:0 10 0 0;">Rate Type</span></th>
			<th><span style="padding:0 10 0 0;">Rate</span></th>
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
			</yrcwww:colorrow>
		</logic:iterate>
	</tbody>
	
</table>

</center>

</yrcwww:contentbox>

<%@ include file="/includes/footer.jsp"%>