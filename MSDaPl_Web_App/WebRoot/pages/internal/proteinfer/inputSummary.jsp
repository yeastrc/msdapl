
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>

	<bean:define name="idpickerRun" property="program" id="program" type="org.yeastrc.ms.domain.protinfer.ProteinInferenceProgram"/>
	
	<table align="center" class="table_pinfer_small">
  		<tr>
  			<th colspan="2" style="font-size: 10pt;">Parameters</th>
  		</tr>
  		<logic:iterate name="idpickerRun" property="sortedParams" id="param" type="org.yeastrc.ms.domain.protinfer.idpicker.IdPickerParam">
  		<tr>
    	<td VALIGN="top" align="left" style="border: 1px #F2F2F2 solid;">
    		<%=program.getDisplayNameForParam(param.getName()) %>
    	</td>
    	<td VALIGN="top" align="left" style="border: 1px #F2F2F2 solid;">
    		<bean:write name="param" property="value" />
    	</td>
   		</tr>
   		</logic:iterate>
	</table>
	    <br><br>
	<table cellpadding="4" cellspacing="2" align="center" width="90%" style="border:1px dashed #7F7F7F;">
	<tr><td style="background-color:#F2F2F2; font-weight:bold;">
		# Unique peptide sequences: <bean:write name="filteredUniquePeptideCount"/>
	</td></tr>
	<tr><td style="background-color:#F2F2F2; font-weight:bold;">
		# Unique ions (sequence + modifications + charge): <bean:write name="filteredUniqueIonCount"/>
	</td></tr>
	<tr>
	<td style="background-color:#F2F2F2; font-weight:bold;">Total Hits: <bean:write name="totalTargetHits" />&nbsp;&nbsp; Filtered Hits: <bean:write name="filteredTargetHits"  /> &nbsp; (<bean:write name="filteredPercent"/>%)</td>
	</tr>
	</table>
	
	<table cellpadding="4" cellspacing="2" align="center" width="90%" class="sortable stripe_table table_basic">
	<logic:notEmpty name="inputSummary" >
	<thead>
	<tr>
	<th class="sort-alpha" align="left"><b><font size="2pt">File Name</font></b></th>
	<!-- <th class="sort-int" align="left"><b><font size="2pt">Decoy Hits</font></b></th> -->
	<th class="sort-int" align="left"><b><font size="2pt"># Hits</font></b></th>
	<th class="sort-int" align="left"><b><font size="2pt"># Filtered Hits</font></b></th>
	<th class="sort-int" align="left"><b><font size="2pt">% Filtered Hits</font></b></th>
	</tr>
	</thead>
	</logic:notEmpty>
	<tbody>
	 	<logic:iterate name="inputSummary"  id="input">
 			<tr>
 				<td>
 					<!--  <span style="text-decoration: underline; cursor: pointer;"
 								onclick="showSpectrumMatches(<bean:write name="input" property="input.inputId" />, '<bean:write name="input" property="fileName" />')">
 					-->
 					<bean:write name="input" property="fileName" />
 					<!-- </span> -->
 				</td>
 				<!--  <td><bean:write name="input" property="input.numDecoyHits" /></td> -->
 				<td><bean:write name="input" property="numHits" /></td>
 				<td><bean:write name="input" property="numFilteredHits" /></td>
 				<td><bean:write name="input" property="percentFilteredHits"/>%</td>
 			</tr>
	 	</logic:iterate>
	 	
	 	</tbody>
		</table>
	<br><br>
	<logic:iterate name="inputSummary" id="input">
		<div id="psm_<bean:write name="input" property="input.inputId" />" style="display: none;" class="input_psm"></div>
	</logic:iterate>