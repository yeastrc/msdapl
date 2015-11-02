
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>



<div align="center">
	<table>
		<tr>
		<td valign="top">
		<table CELLPADDING="5px" CELLSPACING="2px" align="center" style="border: 1px solid gray;">
			<tr>
				<td style="border: 1px dotted #AAAAAA;">&nbsp;</td>
				<td colspan="2" style="border: 1px dotted #AAAAAA;">Unfiltered</td>
				<td colspan="2" style="border: 1px dotted #AAAAAA;">Filtered</td>
			</tr>
			<tr>
			<td style="border: 1px dotted #AAAAAA;">&nbsp;</td>
			<td style="border: 1px dotted #AAAAAA;">All</td>
			<td style="border: 1px dotted #AAAAAA;">Non-Subsumed</td>
			<td style="border: 1px dotted #AAAAAA;">All</td>
			<td style="border: 1px dotted #AAAAAA;">Non-Subsumed</td>
			</tr>
			<tr>
				<td style="border: 1px dotted #AAAAAA;">
					Protein Prophet Groups
				</td>
				<td style="border: 1px dotted #AAAAAA;">
					<b><bean:write name="resultSummary" property="allProteinProphetGroupCount" /></b>
				</td>
				<td style="border: 1px dotted #AAAAAA;">
					<b><bean:write name="resultSummary" property="allParsimoniousProteinProphetGroupCount" /></b>
				</td>
				<td style="border: 1px dotted #AAAAAA;">
					<b><bean:write name="resultSummary" property="filteredProphetGroupCount" /></b>
				</td>
				<td style="border: 1px dotted #AAAAAA;">
					<b><bean:write name="resultSummary" property="filteredParsimoniousProphetGroupCount" /></b>
				</td>
			</tr>
			<tr>
				<td style="border: 1px dotted #AAAAAA;">
					Indistinguishable Protein Groups
				</td>
				<td style="border: 1px dotted #AAAAAA;">
					<b><bean:write name="resultSummary" property="allProteinGroupCount" /></b>
				</td>
				<td style="border: 1px dotted #AAAAAA;">
					<b><bean:write name="resultSummary" property="allParsimoniousProteinGroupCount" /></b>
				</td>
				<td style="border: 1px dotted #AAAAAA;">
					<b><bean:write name="resultSummary" property="filteredProteinGroupCount" /></b>
				</td>
				<td style="border: 1px dotted #AAAAAA;">
					<b><bean:write name="resultSummary" property="filteredParsimoniousProteinGroupCount" /></b>
				</td>
			</tr>
			<tr>
				<td style="border: 1px dotted #AAAAAA;">
					Proteins
				</td>
				<td style="border: 1px dotted #AAAAAA;">
					<b><bean:write name="resultSummary" property="allProteinCount" /></b>
				</td>
				<td style="border: 1px dotted #AAAAAA;">
					<b><bean:write name="resultSummary" property="allParsimoniousProteinCount" /></b>
				</td>
				<td style="border: 1px dotted #AAAAAA;">
					<b><bean:write name="resultSummary" property="filteredProteinCount" /></b>
				</td>
				<td style="border: 1px dotted #AAAAAA;">
					<b><bean:write name="resultSummary" property="filteredParsimoniousProteinCount" /></b>
				</td>
			</tr>
		</table>
		</td>
		
		
		
		</tr>
	</table>
</div>
<div style="font-weight:bold;font-size:8pt;text-align:center;margin:bottom:20px;">
Displaying <span style="color:red;font-size:10pt;"><bean:write name="resultSummary" property="filteredProphetGroupCount" /></span> ProteinProphet groups, 
<span style="color:red;font-size:10pt;"><bean:write name="resultSummary" property="filteredProteinGroupCount" /></span> indistinguishable groups 
and <span style="color:red;font-size:10pt;"><bean:write name="resultSummary" property="filteredProteinCount" /> </span>proteins.
</div>

<bean:define name="proteinProphetFilterForm" property="joinProphetGroupProteins" id="groupProteins"></bean:define>
			
<div id="proteinListTable">
	<%@ include file="proteinListTable.jsp" %>
</div>