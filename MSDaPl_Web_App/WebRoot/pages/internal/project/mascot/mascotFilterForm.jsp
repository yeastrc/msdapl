<%@ taglib uri="/WEB-INF/yrc-www.tld" prefix="yrcwww" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>

<div style="padding:7 7 0 7; margin-bottom:30;">

		<html:form action="viewMascotResults" method="POST">
		
		<logic:present name="filterForm" property="searchId">
			<html:hidden name="filterForm" property="searchId"/>
		</logic:present>
		<logic:present name="filterForm" property="runSearchId">
			<html:hidden name="filterForm" property="runSearchId"/>
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
					<td>Max. Rank</td> <td> <html:text name="filterForm" property="maxRank" size="5"/> </td>
					<td></td> <td></td>
					<td></td>
					<td>Min. Ion Score</td><td> <html:text name="filterForm" property="minIonScore" size="5" /> </td>
					<td>Min. Identity Score</td><td> <html:text name="filterForm" property="minIdentityScore" size="5" /> </td>
				</tr>
				
				<tr>
					<td>Min. Homology Score</td> <td> <html:text name="filterForm" property="minHomologyScore" size="5"/> </td>
					<td>Min. Expect. Score</td> <td> <html:text name="filterForm" property="minExpect" size="5"/> </td>
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
					<td valign="top">Modified peptides</td><td valign="top"> <html:checkbox name="filterForm" property="showModified" /> </td>
					<td valign="top">Unmodified peptides</td><td valign="top"> <html:checkbox name="filterForm" property="showUnmodified" /> </td>
				</tr>
				
				<tr>
					<td valign="top">File(s)</td> 
					<td colspan=6 align="left" valign="top" style="font-size:8pt;"> 
						<html:text name="filterForm" property="fileNameFilter" size="50" /><br>
						Enter comma-separated file names
					</td>
				</tr>
				
				<tr>
					<td colspan="9" align="center"><html:submit value="Update" 
									styleClass="plain_button" 
									onclick="javascript:updateResults();return false;"/></td>
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