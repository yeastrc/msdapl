<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/yrc-www.tld" prefix="yrcwww" %>

<logic:present name="psmList">

<table align="center" width="98%"
  			style="margin-top: 10px; margin-bottom: 10px; border: 1px dashed gray;"
  			class="sortable table_pinfer_smal"
  			id="psmlist_<bean:write name='percolatorPeptideId'/>"
  			>

	<thead>
		<tr>
			<th class="sort-alpha clickable">File</th>
			<th class="sort-int clickable">Scan</th>
			<th class="sort-int clickable">Charge</th>
			<th class="sort-float clickable">RT</th>
			<th class="sort-float clickable">qValue</th>
			<th class="sort-float clickable">PEP</th>
			<th>Spectrum</th>
		</tr>
	</thead>

	<tbody>
		<logic:iterate name="psmList" id="psm">
			<tr>
				<td><bean:write name="psm" property="filename"/></td>
				<td><bean:write name="psm" property="scanNumber"/></td>
				<td><bean:write name="psm" property="charge"/></td>
				<td><bean:write name="psm" property="retentionTime"/></td>
				<td><bean:write name="psm" property="qvalueRounded3SignificantDigits"/></td>
				<!--
				<td><bean:write name="psm" property="qvalueRounded"/></td>
				-->
				<td><bean:write name="psm" property="posteriorErrorProbabilityRounded3SignificantDigits"/></td>

				<td>
					<a
					href="<yrcwww:link path='viewSpectrum.do'/>?scanID=<bean:write name='psm' property='scanId'/>&runSearchResultID=<bean:write name='psm' property='id'/>&runSearchAnalysisID=<bean:write name='psm' property='runSearchAnalysisId'/>&java=true"
					target="spec_view_java"
					>
					<span style='font-size:8pt;' title='Java Spectrum Viewer'>(J)</span>
					</a>

					&nbsp;
					<a
					href="<yrcwww:link path='viewSpectrum.do'/>?scanID=<bean:write name='psm' property='scanId'/>&runSearchResultID=<bean:write name='psm' property='id'/>&runSearchAnalysisID=<bean:write name='psm' property='runSearchAnalysisId'/>"
					target="spec_view_js"
					>
					<span style='font-size:8pt;' title='JavaScript Spectrum Viewer'>(JS)</span>
					</a>

				</td>

			</tr>
		</logic:iterate>

	</tbody>

</table>

</logic:present>