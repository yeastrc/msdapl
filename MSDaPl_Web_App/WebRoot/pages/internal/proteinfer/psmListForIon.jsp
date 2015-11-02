<%@page import="org.yeastrc.ms.domain.search.Program"%>
<%@page import="org.yeastrc.ms.domain.protinfer.ProteinInferenceProgram"%>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>

<table align="center" width="70%"
		id="allpsms_<bean:write name="pinferIonId" />"
     	style="margin-top: 6px; margin-bottom: 6px;" 
     	class="sortable allpsms allPeptideSpectra table_pinfer_small">
       			
     <thead><tr>
	     <th class="sort-alpha" align="left">Scan Number</th>
	     <th class="sort-int" align="left">Charge</th>
	     <logic:equal name="protInferProgram" value="<%= ProteinInferenceProgram.PROTINFER_SEQ.name()%>">
	     	<th class="sort-float" align="left">FDR</th>
	     </logic:equal>
	     <logic:equal name="protInferProgram" value="<%= ProteinInferenceProgram.PROTINFER_PLCID.name()%>">
	     	<th class="sort-float" align="left">FDR</th>
	     </logic:equal>
	     <logic:equal name="inputGenerator" value="<%=Program.SEQUEST.name() %>">
	     	<th class="sort-float" align="left">DeltaCN</th>
	     	<th class="sort-float" align="left">XCorr</th>
	     </logic:equal>
	     <logic:equal name="inputGenerator" value="<%=Program.PROLUCID.name() %>">
	     	<th class="sort-float" align="left">DeltaCN</th>
	     	<th class="sort-float" align="left">Primary Score</th>
	     </logic:equal>
	     <logic:equal name="inputGenerator" value="<%=Program.PERCOLATOR.name() %>">
	     	<th class="sort-float" align="left">qValue</th>
	     	<th class="sort-float" align="left">PEP</th>
	     </logic:equal>
     	<logic:equal name="inputGenerator" value="<%=Program.PEPTIDE_PROPHET.name() %>">
     		<!-- <th class="sort-float" align="left">Probability</th> -->
     		<th class="sort-float" align="left">NET</th>
     		<th class="sort-float" align="left">NMC</th>
     		<th class="sort-float" align="left">Mass Diff.</th>
     		<th class="sort-float" align="left">FVal</th>
     	</logic:equal>
	     
	     <th style="text-decoration: underline;font-size: 10pt;" align="left">Spectrum</th>
	</tr></thead>
       			
	<tbody>
        <logic:iterate name="psmList" id="psm">
        <tr>
     		<td><bean:write name="psm" property="scanNumber" /></td>
     		<td><bean:write name="psm" property="spectrumMatch.charge" /></td>
     		
     		<logic:equal name="protInferProgram" value="<%= ProteinInferenceProgram.PROTINFER_SEQ.name()%>">
     			<bean:define name="psm" property="proteinferSpectrumMatch" id="psm_idp" type="org.yeastrc.ms.domain.protinfer.idpicker.IdPickerSpectrumMatch"/>
     			<td><bean:write name="psm_idp" property="fdrRounded" /></td>
     		</logic:equal>
     		
     		<logic:equal name="protInferProgram" value="<%= ProteinInferenceProgram.PROTINFER_PLCID.name()%>">
     			<bean:define name="psm" property="proteinferSpectrumMatch" id="psm_idp" type="org.yeastrc.ms.domain.protinfer.idpicker.IdPickerSpectrumMatch"/>
     			<td><bean:write name="psm_idp" property="fdrRounded" /></td>
     		</logic:equal>
     		
     		<logic:equal name="inputGenerator" value="<%=Program.SEQUEST.name() %>">
     			<bean:define name="psm" property="spectrumMatch" id="psm_seq" type="org.yeastrc.ms.domain.search.sequest.SequestSearchResult"/>
     			<td><bean:write name="psm_seq" property="sequestResultData.deltaCN" /></td>
     			<td><bean:write name="psm_seq" property="sequestResultData.xCorr" /></td>
     			<td>
     			<span class="clickable underline" title="Java Spectrum Viewer"
				onclick="viewSpectrum(<bean:write name="psm" property="scanId" />, <bean:write name="psm_seq" property="id" />, 'java')" >
				J
				</span>
				&nbsp;
	     		<span class="clickable underline" title="JavaScript Spectrum Viewer"
				onclick="viewSpectrum(<bean:write name="psm" property="scanId" />, <bean:write name="psm_seq" property="id" />)" >
				JS
				</span>
				</td>
     		</logic:equal>
     
     		<logic:equal name="inputGenerator" value="<%=Program.PROLUCID.name() %>">
     		 	<bean:define name="psm" property="spectrumMatch" id="psm_plc" type="org.yeastrc.ms.domain.search.prolucid.ProlucidSearchResult"/>
     		 	<td><bean:write name="psm_plc" property="prolucidResultData.primaryScore" /></td>
				<td><bean:write name="psm_plc" property="prolucidResultData.deltaCN" /></td>
				<td>
				<span class="clickable underline" title="Java Spectrum Viewer"
				onclick="viewSpectrum(<bean:write name="psm" property="scanId" />, <bean:write name="psm_plc" property="id" />, 'java')" >
				J
				</span>
				&nbsp;
	     		<span class="clickable underline" title="JavaScript Spectrum Viewer"
				onclick="viewSpectrum(<bean:write name="psm" property="scanId" />, <bean:write name="psm_plc" property="id" />)" >
				JS
				</span>
				</td>
     		</logic:equal>
     		 
     		<logic:equal name="inputGenerator" value="<%=Program.PERCOLATOR.name() %>">
     		 	<bean:define name="psm" property="spectrumMatch" id="psm_perc" type="org.yeastrc.ms.domain.analysis.percolator.PercolatorResult"/>
     		 	<td><bean:write name="psm_perc" property="qvalueRounded" /></td>
     			<td><bean:write name="psm_perc" property="posteriorErrorProbabilityRounded" /></td>
     			<td>
     			<span class="clickable underline" title="Java Spectrum Viewer"
				onclick="viewSpectrum(<bean:write name="psm" property="scanId" />, <bean:write name="psm_perc" property="id" />, 'java')" >
				J
				</span>
				&nbsp;
	     		<span class="clickable underline" title="JavaScript Spectrum Viewer"
				onclick="viewSpectrum(<bean:write name="psm" property="scanId" />, <bean:write name="psm_perc" property="id" />)" >
				JS
				</span>
			</td>
     		</logic:equal>
     		 
     		 <logic:equal name="inputGenerator" value="<%=Program.PEPTIDE_PROPHET.name() %>">
     		 	<bean:define name="psm" property="spectrumMatch" id="psm_peptProphet" type="org.yeastrc.ms.domain.analysis.peptideProphet.PeptideProphetResult"/>
     		 	<!--  <td><bean:write name="psm_peptProphet" property="probabilityRounded" /></td> -->
	     		<td><bean:write name="psm_peptProphet" property="numEnzymaticTermini" /></td>
	     		<td><bean:write name="psm_peptProphet" property="numMissedCleavages" /></td>
	     		<td><bean:write name="psm_peptProphet" property="massDifferenceRounded" /></td>
	     		<td><bean:write name="psm_peptProphet" property="fValRounded" /></td>
	     		<td>
	     		<span class="clickable underline" title="Java Spectrum Viewer"
				onclick="viewSpectrum(<bean:write name="psm" property="scanId" />, <bean:write name="psm_peptProphet" property="id" />, 'java')" >
				J
				</span>
				&nbsp;
	     		<span class="clickable underline" title="JavaScript Spectrum Viewer"
				onclick="viewSpectrum(<bean:write name="psm" property="scanId" />, <bean:write name="psm_peptProphet" property="id" />)" >
				JS
				</span>
				</td>
     		</logic:equal>
     		
   			</tr>
        </logic:iterate>
	</tbody>
</table>