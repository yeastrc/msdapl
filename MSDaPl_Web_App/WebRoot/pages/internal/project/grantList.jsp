<logic:notEmpty name="project" property="grants">
<yrcwww:colorrow>
   	<TD valign="top" width="25%">Funding Source(s):</TD>
   	<TD valign="top" width="75%">
   	<table id="fundingSources" style="width:90%;">
		<logic:notEmpty name="project" property="grants">
			<tr>
				<td style="font-size:8pt;"><nobr><b>Grant Title</b></nobr></td>
				<td style="font-size:8pt;"><nobr><b>PI</b></nobr></td>
				<td style="font-size:8pt;"><nobr><b>Source Type</b></nobr></td>
				<td style="font-size:8pt;"><nobr><b>Source Name</b></nobr></td>
				<td style="font-size:8pt;"><nobr><b>Grant #</b></nobr></td>
			</tr>
		</logic:notEmpty>
		<logic:iterate name="project" property="grants" id="grant">
			<tr>
				<td style="font-size:8pt;padding: 3px;padding-left:0px;"><bean:write name="grant" property="title" /></td>
				<td style="font-size:8pt;padding: 3px;">
				<html:link action="viewResearcher.do" paramId="id" paramName="grant" paramProperty="grantPI.ID"><bean:write name="grant" property="grantPI.lastName" /></html:link>
				</td>
				<td style="font-size:8pt;padding: 3px;"><bean:write name="grant" property="fundingSource.sourceType.displayName" /></td>
				<td style="font-size:8pt;padding: 3px;"><bean:write name="grant" property="fundingSource.sourceName.displayName" /></td>
				<td style="font-size:8pt;padding: 3px;"><bean:write name="grant" property="grantNumber" /></td>
			</tr>
		</logic:iterate>
		</table>
   	
   	</TD>
</yrcwww:colorrow>
</logic:notEmpty>
