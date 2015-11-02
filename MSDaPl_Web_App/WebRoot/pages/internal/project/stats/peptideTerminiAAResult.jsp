<%@ taglib uri="/WEB-INF/yrc-www.tld" prefix="yrcwww" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>

<div style="background-color:#ED9A2E;width:100%; margin:40 0 0 0; padding:3 0 3 0; color:white;" align="left">
	 <span style="margin-left:10;" 
	  class="foldable fold-open" id="peptide_termini_aa_result_fold" >&nbsp;&nbsp;&nbsp;&nbsp; </span>
		<b>Amino Acid Frequency at Peptide Termini</b>
	</div>
	
	<div style="padding:10 7 10 7; margin-bottom:5; border: 1px dashed gray;background-color: #FFFFFF;" id="peptide_termini_aa_result_fold_target">
	
		<logic:present name="peptideTerminalAAResult">
		<table>
			<tr>
				<td style="padding-bottom:20px;">
					<table class="table_basic stripe_table">
						<tbody>
						<tr>
						<td align="center"># Unique Peptides</td>
						<td align="left"><bean:write name="peptideTerminalAAResult" property="totalResultCount"/></td>
						</tr>
						<tr>
						<td align="center"># Peptides with <b>2</b> enzymatic termini</td>
						<td align="center"><bean:write name="peptideTerminalAAResult" property="numResultsWithEnzTerm_2"/>
						(<bean:write name="peptideTerminalAAResult" property="pctResultsWithEnzTerm_2"/>%)</td>
						</tr>
						<tr>
						<td align="center"># Peptides with <b>1</b> enzymatic termini</td>
						<td align="center"><bean:write name="peptideTerminalAAResult" property="numResultsWithEnzTerm_1"/>
						(<bean:write name="peptideTerminalAAResult" property="pctResultsWithEnzTerm_1"/>%)</td>
						</tr>
						<tr>
						<td align="center"># Peptides with <b>0</b> enzymatic termini</td>
						<td align="center"><bean:write name="peptideTerminalAAResult" property="numResultsWithEnzTerm_0"/>
						(<bean:write name="peptideTerminalAAResult" property="pctResultsWithEnzTerm_0"/>%)</td>
						</tr>
						<tr>
						<td colspan="2" style="padding:10 5 0 5;">
						<logic:equal name="peptideTerminalAAResult" property="scoreType" value="PERC_PEPTIDE_QVAL">
							<span style="font-weight:bold;">
								A peptide-level qvalue cutoff of 0.01 was applied to the results.
							</span>
						</logic:equal>
						<logic:equal name="peptideTerminalAAResult" property="scoreType" value="PERC_PSM_QVAL">
							<span style="font-weight:bold;">
								A PSM-level qvalue cutoff of 0.01 was applied and unique peptides were used.
							</span>
						</logic:equal>
						</td>
						</tr>
						</tbody>
					</table>
				
				</td>
			</tr>
			
				
			<tr>
			<td valign="top" align="center">
				<img src="<bean:write name="peptideTerminalAAResultUrl"/>" align="top" alt="Frequency of Amino Acids at Termini" style="padding-right:20px;"></img>
			</td>
			</tr>
		</table>
		</logic:present>
	</div>