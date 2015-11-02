
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/yrc-www.tld" prefix="yrcwww" %>



<div align="center">
	<table>
		<tr>
		<td valign="top">
		<table CELLPADDING="5px" CELLSPACING="2px" align="center" style="border: 1px solid gray;">
			<tr>
				<td style="border: 1px dotted #AAAAAA;" align="center">
				
   				<a href="" onclick="openInformationPopup('pages/internal/docs/proteinInference.jsp#PI_PARSIM_SUBSET'); return false;">
   				<img src="<yrcwww:link path="images/info_16.png"/>" align="bottom" border="0"/></a>
   				
   				
				</td>
				<td style="border: 1px dotted #AAAAAA;font-weight:bold;" align="center"># Groups (# Proteins)</td>
				<td style="border: 1px dotted #AAAAAA;font-weight:bold;" align="center">Parsimonious<br/># Groups (# Proteins)</td>
				<logic:equal name="resultSummary" property="hasSubsetInformation" value="true">
					<td style="border: 1px dotted #AAAAAA;font-weight:bold;" align="center">Non-Subset<br/># Groups (# Proteins)</td>
				</logic:equal>
			</tr>
			<tr>
				<td style="border: 1px dotted #AAAAAA;">All:</td>
				<td style="border: 1px dotted #AAAAAA;">
					<b><bean:write name="resultSummary" property="allProteinGroupCount" /></b>(<bean:write name="resultSummary" property="allProteinCount" />)
				</td>
				<td style="border: 1px dotted #AAAAAA;">
					<b><bean:write name="resultSummary" property="allParsimoniousProteinGroupCount" /></b>(<bean:write name="resultSummary" property="allParsimoniousProteinCount" />)
				</td>
				<logic:equal name="resultSummary" property="hasSubsetInformation" value="true">
				<td style="border: 1px dotted #AAAAAA;">
					<b><bean:write name="resultSummary" property="allNonSubsetProteinGroupCount" /></b>(<bean:write name="resultSummary" property="allNonSubsetProteinCount" />)
				</td>
				</logic:equal>
			</tr>
			<tr>
				<td style="border: 1px dotted #AAAAAA;">Filtered:</td>
				<td style="border: 1px dotted #AAAAAA;color:red;">
					<b><bean:write name="resultSummary" property="filteredProteinGroupCount" /></b>(<bean:write name="resultSummary" property="filteredProteinCount" />)
				</td>
				<td style="border: 1px dotted #AAAAAA;color:red;">
					<b><bean:write name="resultSummary" property="filteredParsimoniousProteinGroupCount" /></b>(<bean:write name="resultSummary" property="filteredParsimoniousProteinCount" />)
				</td>
				<logic:equal name="resultSummary" property="hasSubsetInformation" value="true">
				<td style="border: 1px dotted #AAAAAA;color:red;">
					<b><bean:write name="resultSummary" property="filteredNonSubsetProteinGroupCount" /></b>(<bean:write name="resultSummary" property="filteredNonSubsetProteinCount" />)
				</td>
				</logic:equal>
			</tr>
		</table>
		</td>
		
		
		
		</tr>
	</table>
</div>

<div align="center" style="color:green; margin:5px;font-size:10pt;">
Displaying <span style="color:red; font-weight:bold;"><bean:write name="resultSummary" property="filteredProteinGroupCount" /></span> protein groups 
and <span style="color:red; font-weight:bold;"><bean:write name="resultSummary" property="filteredProteinCount" /></span> proteins
</div>

<bean:define name="proteinInferFilterForm" property="joinGroupProteins" id="groupProteins"></bean:define>
			
<div id="proteinListTable">
	<%@ include file="proteinListTable.jsp" %>
</div>