<%@ taglib uri="/WEB-INF/yrc-www.tld" prefix="yrcwww" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>

<yrcwww:notauthenticated>
 <logic:forward name="authenticate" />
</yrcwww:notauthenticated>

<logic:empty name="filterForm">
  <logic:forward name="sequestPepxmlDownload" />
</logic:empty>


<%@ include file="/includes/header.jsp" %>

<%@ include file="/includes/errors.jsp" %>


<yrcwww:contentbox centered="true" title="Sequest to PepXML Export Options" width="750">


<html:form action="sequestPepXmlDownload" method="POST">
		
			<html:text name="filterForm" property="searchId"/>
		
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
					<td>Min. XCorr (+1)</td> <td> <html:text name="filterForm" property="minXCorr_1" size="5"/> </td>
					<td>Min. XCorr (+2)</td> <td> <html:text name="filterForm" property="minXCorr_2" size="5"/> </td>
					<td></td>
					<td>Min. DeltaCN</td><td> <html:text name="filterForm" property="minDeltaCN" size="5" /> </td>
					<td>Min. Sp</td><td> <html:text name="filterForm" property="minSp" size="5" /> </td>
				</tr>
				
				<tr>
					<td>Min. XCorr (+3)</td> <td> <html:text name="filterForm" property="minXCorr_3" size="5"/> </td>
					<td>Min. XCorr (>3)</td> <td> <html:text name="filterForm" property="minXCorr_H" size="5"/> </td>
					<td></td>
					<td></td>
					<td></td>
				</tr>
				
			</table>
			
			<br>
			
			<div align="center" style="font-weight:bold;">Select files to export</div>
			<table class="table_basic" width="90%" align="center">
				<thead>
					<tr>
						<th>&nbsp;</th><th>File</th>
					</tr>
				</thead>
				<tbody>
					<logic:iterate name="filterForm" property="fileList" id="file">
					<tr>
						<td>
							<html:checkbox name="file" property="selected" indexed="true"/>
						</td>
						<td>
							<html:hidden name="file" property="fileName" indexed="true"/>
							<html:hidden name="file" property="id" indexed="true"/>
							<bean:write name="file" property="fileName"/>
						</td>
						
					</tr>
					</logic:iterate>
				</tbody>
			</table>
			
			<div align="center">
				<html:submit value="Submit" styleClass="plain_button"/>
			</div>
		</html:form>


</yrcwww:contentbox>

<%@ include file="/includes/footer.jsp" %>
