
<%@page import="org.yeastrc.ms.domain.search.Program"%>
<%@page import="org.yeastrc.ms.domain.protinfer.ProteinInferenceProgram"%>
<%@ taglib uri="/WEB-INF/yrc-www.tld" prefix="yrcwww" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>


<logic:present name="proteinId">
<table align="center" width="98%"
  			style="margin-top: 6px; margin-bottom: 6px;"
  			class="sortable peptlist table_pinfer_small"
  			id="peptforprottbl_<bean:write name="proteinId" />_<bean:write name="proteinGroupId" />">
</logic:present>

<logic:notPresent name="proteinId">
<table align="center" width="98%"
  			style="border: 1px dashed gray;margin-top: 6px; margin-bottom: 6px;"
  			class="sortable peptlist table_pinfer_small"
  			id="peptforprottbl_<bean:write name="proteinGroupId" />">
</logic:notPresent>

  	 <thead><tr>
  	 <th class="sort-alpha" align="left">Uniq</th>
     <th class="sort-alpha" align="left">Peptide</th>
     <th class="sort-int" align="left">Charge</th>
     <th class="sort-int" align="left"># Spectra</th>
     <th class="sort-float" align="left">RT</th>

     <logic:equal name="protInferProgram" value="<%= ProteinInferenceProgram.PROTINFER_SEQ.name()%>">
     	<th class="sort-float" align="left">Best FDR</th>
     </logic:equal>
     <logic:equal name="protInferProgram" value="<%= ProteinInferenceProgram.PROTINFER_PLCID.name()%>">
     	<th class="sort-float" align="left">Best FDR</th>
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
     	<logic:equal name="hasPrecursorArea" value="true">
     		<th class="sort-alpha" align="left">Area</th>
     	</logic:equal>
     	<th class="sort-float" align="left">qvalue</th>
     	<logic:notPresent name="oldPercolator">
     		<th class="sort-float" align="left">PEP</th>
     	</logic:notPresent>
     	<logic:present name="oldPercolator">
     		<th class="sort-float" align="left">DS</th>
     	</logic:present>
     	<logic:equal name="hasPeptideScores" value="true">
     		<th class="sort-float" align="left"><span class="tooltip" title="Peptide-level qvalue">qvalue(P)</span></th>
     		<th class="sort-float" align="left"><span class="tooltip" title="Peptide-level PEP">PEP(P)</span></th>
     	</logic:equal>
     </logic:equal>
     <logic:equal name="protInferProgram" value="<%= ProteinInferenceProgram.PROTEIN_PROPHET.name()%>">
     	<th class="sort-int" align="left">Conrib. Evidence</th>
     	<th class="sort-float" align="left">nsp</th>
     	<th class="sort-float" align="left">Wt.</th>
     	<th class="sort-float" align="left">Init.Prob.</th>
     	<th class="sort-float" align="left">nsp adj. Prob.</th>
     </logic:equal>
     <logic:equal name="inputGenerator" value="<%=Program.PEPTIDE_PROPHET.name() %>">
     	<!-- <th class="sort-float" align="left">Probability</th> -->
     	<th class="sort-float" align="left">NET</th>
     	<th class="sort-float" align="left">NMC</th>
     	<th class="sort-float" align="left">Mass Diff.</th>
     	<th class="sort-float" align="left">FVal</th>
     </logic:equal>

     <th align="left">Spectrum</th>
     </tr></thead>


     <tbody>
     <logic:iterate name="proteinPeptideIons" id="ion">

     	<tr>
     		<td>
     			<logic:equal name="ion" property="isUniqueToProteinGroup" value="true">*</logic:equal>
     			<logic:equal name="ion" property="isUniqueToProteinGroup" value="false"></logic:equal>
     		</td>
     		<td class="left_align"><span class="peptide"><bean:write name="ion" property="ionSequence" /></span></td>
     		<td><bean:write name="ion" property="charge" /></td>
     		<td><bean:write name="ion" property="spectrumCount" /></td>
     		<td><bean:write name="ion" property="retentionTime" /></td>

     		<logic:equal name="protInferProgram" value="<%= ProteinInferenceProgram.PROTINFER_SEQ.name()%>">
     			<bean:define name="ion" property="ion.bestSpectrumMatch" id="psm_idp" type="org.yeastrc.ms.domain.protinfer.idpicker.IdPickerSpectrumMatch"/>
     			<td><bean:write name="psm_idp" property="fdrRounded" /></td>
     		</logic:equal>

     		<logic:equal name="protInferProgram" value="<%= ProteinInferenceProgram.PROTINFER_PLCID.name()%>">
     			<bean:define name="ion" property="ion.bestSpectrumMatch" id="psm_idp" type="org.yeastrc.ms.domain.protinfer.idpicker.IdPickerSpectrumMatch"/>
     			<td><bean:write name="psm_idp" property="fdrRounded" /></td>
     		</logic:equal>

     		<logic:equal name="inputGenerator" value="<%=Program.SEQUEST.name() %>">
     			<bean:define name="ion" property="bestSpectrumMatch" id="psm_seq" type="org.yeastrc.ms.domain.search.sequest.SequestSearchResult"/>
     			<td><bean:write name="psm_seq" property="sequestResultData.deltaCN" /></td>
     			<td><bean:write name="psm_seq" property="sequestResultData.xCorr" /></td>
     			<td>
     			<span class="clickable underline" title="Java Spectrum Viewer"
				onclick="viewSpectrum(<bean:write name="ion" property="scanId" />, <bean:write name="ion" property="bestSpectrumMatch.id" />, 'java')" >
				J
				</span>
				&nbsp;
	     		<span class="clickable underline" title="JavaScript Spectrum Viewer"
				onclick="viewSpectrum(<bean:write name="ion" property="scanId" />, <bean:write name="ion" property="bestSpectrumMatch.id" />)" >
				JS
				</span>
				</td>
     		</logic:equal>

     		<logic:equal name="inputGenerator" value="<%=Program.PROLUCID.name() %>">
     		 	<bean:define name="ion" property="bestSpectrumMatch" id="psm_plc" type="org.yeastrc.ms.domain.search.prolucid.ProlucidSearchResult"/>
     		 	<td><bean:write name="psm_plc" property="prolucidResultData.primaryScore" /></td>
				<td><bean:write name="psm_plc" property="prolucidResultData.deltaCN" /></td>
				<td>
				<span class="clickable underline" title="Java Spectrum Viewer"
				onclick="viewSpectrum(<bean:write name="ion" property="scanId" />, <bean:write name="ion" property="bestSpectrumMatch.id" />, 'java')" >
				J
				</span>
				&nbsp;
	     		<span class="clickable underline" title="JavaScript Spectrum Viewer"
				onclick="viewSpectrum(<bean:write name="ion" property="scanId" />, <bean:write name="ion" property="bestSpectrumMatch.id" />)" >
				JS
				</span>
				</td>
     		</logic:equal>

     		<logic:equal name="inputGenerator" value="<%=Program.PERCOLATOR.name() %>">
     			<logic:equal name="hasPrecursorArea" value="true">
     				<td><bean:write name="ion" property="precursorArea" /></td>
     			</logic:equal>
     		 	<bean:define name="ion" property="bestSpectrumMatch" id="psm_perc" type="org.yeastrc.ms.domain.analysis.percolator.PercolatorResult"/>
     		 	<td><bean:write name="psm_perc" property="qvalueRounded3SignificantDigits" /></td>

     		 	<logic:notPresent name="oldPercolator">
	     			<td><bean:write name="psm_perc" property="posteriorErrorProbabilityRounded3SignificantDigits" /></td>
	     		</logic:notPresent>
	     		<logic:present name="oldPercolator">
	     			<td><bean:write name="psm_perc" property="discriminantScoreRounded3SignificantDigits" /></td>
	     		</logic:present>
	     		<logic:equal name="hasPeptideScores" value="true">
	     			<td><bean:write name="ion" property="percolatorPeptideResult.qvalueRounded3SignificantDigits"/></td>
	     			<td><bean:write name="ion" property="percolatorPeptideResult.posteriorErrorProbabilityRounded3SignificantDigits"/></td>
	     		</logic:equal>
	     		<td>
	     		<span class="clickable underline" title="Java Spectrum Viewer"
				onclick="viewSpectrum(<bean:write name="ion" property="scanId" />, <bean:write name="psm_perc" property="id" />, 'java')" >
				J
				</span>
				&nbsp;
	     		<span class="clickable underline" title="JavaScript Spectrum Viewer"
				onclick="viewSpectrum(<bean:write name="ion" property="scanId" />, <bean:write name="psm_perc" property="id" />)" >
				JS
				</span>
				</td>
     		</logic:equal>


     		<logic:equal name="protInferProgram" value="<%= ProteinInferenceProgram.PROTEIN_PROPHET.name()%>">
     			<bean:define name="ion" property="ion" id="prophetIon" type="org.yeastrc.ms.domain.protinfer.proteinProphet.ProteinProphetProteinPeptideIon"/>
     		 	<td style="text-align:center">
     		 		<logic:equal name="prophetIon" property="isContributingEvidence" value="true">
     		 		1
     		 		</logic:equal>
     		 		<logic:equal name="prophetIon" property="isContributingEvidence" value="false">
     		 		0
     		 		</logic:equal>
     		 	</td>
     		 	<td><bean:write name="prophetIon" property="numSiblingPeptides" /></td>
     		 	<td><bean:write name="prophetIon" property="weight" /></td>
	     		<td><bean:write name="prophetIon" property="initialProbability" /></td>
	     		<td><bean:write name="prophetIon" property="nspAdjProbability" /></td>
     		</logic:equal>

     		<logic:equal name="inputGenerator" value="<%=Program.PEPTIDE_PROPHET.name() %>">
     		 	<bean:define name="ion" property="bestSpectrumMatch" id="psm_peptProphet" type="org.yeastrc.ms.domain.analysis.peptideProphet.PeptideProphetResult"/>
     		 	<!--  <td><bean:write name="psm_peptProphet" property="probabilityRounded" /></td> -->
	     		<td><bean:write name="psm_peptProphet" property="numEnzymaticTermini" /></td>
	     		<td><bean:write name="psm_peptProphet" property="numMissedCleavages" /></td>
	     		<td><bean:write name="psm_peptProphet" property="massDifferenceRounded" /></td>
	     		<td><bean:write name="psm_peptProphet" property="fValRounded" /></td>
	     		<td>
	     		<span class="clickable underline" title="Java Spectrum Viewer"
				onclick="viewSpectrum(<bean:write name="ion" property="scanId" />, <bean:write name="psm_peptProphet" property="id" />, 'java')" >
				J
				</span>
				&nbsp;
	     		<span class="clickable underline" title="JavaScript Spectrum Viewer"
				onclick="viewSpectrum(<bean:write name="ion" property="scanId" />, <bean:write name="psm_peptProphet" property="id" />)" >
				JS
				</span>
				</td>
     		</logic:equal>



     	</tr>
     </logic:iterate>
     </tbody>
</table>
