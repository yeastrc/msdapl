<%@ taglib uri="/WEB-INF/yrc-www.tld" prefix="yrcwww" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>

<script type="text/javascript">
$(document).ready(function() {
	$("input[name='showModified']").click(function() {
		if($("input[name='showModified']:checked").length > 0) {
			
			$("input:checkbox.mod_residue").attr('checked', true);
		}
		else {
			$("input:checkbox.mod_residue").attr('checked', false);
		}
	});
});

</script>


<div style="padding:7 7 0 7; margin-bottom:30;">

		<html:form action="viewPeptideProphetResults" method="POST">
		
		<logic:present name="filterForm" property="searchAnalysisId">
			<html:hidden name="filterForm" property="searchAnalysisId"/>
		</logic:present>
		<logic:present name="filterForm" property="runSearchAnalysisId">
			<html:hidden name="filterForm" property="runSearchAnalysisId"/>
		</logic:present>
		
		<html:hidden name="filterForm" property="pageNum" styleId="pageNum"/>
		<html:hidden name="filterForm" property="numPerPage" styleId="numPerPage"/>
		<html:hidden name="filterForm" property="sortByString" styleId="sortBy"/>
		<html:hidden name="filterForm" property="sortOrderString" styleId="sortOrder"/>
		
			<table cellspacing="0" cellpadding="2" >
				<tr>
					<td>Min. Scan</td> <td> <html:text name="filterForm" property="minScan" size="5"/> </td>
					<td>Max. Scan</td> <td> <html:text name="filterForm" property="maxScan" size="5" /> </td>
					<td>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>
					<td>Min. Charge</td><td> <html:text name="filterForm" property="minCharge" size="5" /> </td>
					<td>Max. Charge</td><td> <html:text name="filterForm" property="maxCharge" size="5" /> </td>
				</tr>
				
				<tr>
					<td>Min. RT</td> <td> <html:text name="filterForm" property="minRT" size="5"/> </td>
					<td>Max. RT</td> <td> <html:text name="filterForm" property="maxRT" size="5" /> </td>
					<td></td>
					<td>Min. Obs. Mass</td><td> <html:text name="filterForm" property="minObsMass" size="5" /> </td>
					<td>Max. Obs. Mass</td><td> <html:text name="filterForm" property="maxObsMass" size="5" /> </td>
				</tr>
				
				<tr>
					<td>Min. Probability</td> <td> <html:text name="filterForm" property="minProbability" size="5"/> </td>
					<td>Max. Probability</td> <td> <html:text name="filterForm" property="maxProbability" size="5" /> </td>
					<td></td>
					<td></td>
					<td></td>
				</tr>
				
				<tr>
					<td valign="top">Peptide</td> 
					<td colspan=4 align="left" valign="top" style="font-size:8pt;"> 
						<html:text name="filterForm" property="peptide" size="25" /><br>
						Exact: <html:checkbox name="filterForm" property="exactPeptideMatch"  />
					</td>
					<td valign="top" colspan="2">
						Modified peptides <html:checkbox name="filterForm" property="showModified" />
						<logic:notEmpty name="filterForm" property="modificationList">
							<div class="small_font">
							Select Modifications:
							<br/>
							<logic:iterate 	name="filterForm" property="modificationList" id="modification">
								<html:checkbox name="modification" property="selected" indexed="true" styleClass="mod_residue"></html:checkbox>
								<bean:write name="modification" property="modifiedResidue"/> (<bean:write name="modification" property="modificationMassString"/>)
								<html:hidden name="modification" property="id" indexed="true" />
								<html:hidden name="modification" property="modifiedResidue" indexed="true" />
								<html:hidden name="modification" property="modificationMass" indexed="true" />
								<br/>
							</logic:iterate>
							</div>
						</logic:notEmpty>
					</td>
					<td valign="top" colspan="2">Unmodified peptides <html:checkbox name="filterForm" property="showUnmodified" /> </td>
				</tr>
				
				<tr>
					<td valign="top">File(s)</td> 
					<td colspan=6 align="left" valign="top" style="font-size:8pt;"> 
						<html:text name="filterForm" property="fileNameFilter" size="50" /><br>
						Enter comma-separated file names
					</td>
					<td></td>
					<!--<td valign="top">Unique peptides</td><td valign="top"> <html:checkbox name="filterForm" property="peptidesView" /> </td>-->
				</tr>
				
				<tr>
					<td colspan="9" align="center">
					<html:submit value="Update" 
									styleClass="plain_button" 
									onclick="javascript:updateResults();return false;"/>
					</td>
				</tr>
				<tr>
					<td colspan="9" align="center">
						<b># Results: </b><bean:write name="numResults" />
						&nbsp; &nbsp; &nbsp;
						<b># Results (filtered):</b><bean:write name="numResultsFiltered" />
					</td>
				</tr>
			</table>
		</html:form>
	</div>