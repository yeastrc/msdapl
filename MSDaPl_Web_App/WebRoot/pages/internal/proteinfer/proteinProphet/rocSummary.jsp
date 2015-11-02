
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>

	<bean:define name="proteinProphetRun" property="program" id="program" type="org.yeastrc.ms.domain.protinfer.ProteinInferenceProgram"/>
	
	<table cellpadding="4" cellspacing="2" align="center" width="80%" class="sortable stripe_table table_basic">
	<logic:notEmpty name="rocSummary" >
	<thead>
	<tr>
	<th class="sort-float" align="left"><b><font size="2pt">Min. Probability</font></b></th>
	<th class="sort-float" align="left"><b><font size="2pt">Error</font></b></th>
	<th class="sort-float" align="left"><b><font size="2pt">Sensitivity</font></b></th>
	<th class="sort-int" align="left"><b><font size="2pt"># Correct</font></b></th>
	<th class="sort-int" align="left"><b><font size="2pt"># Incorrect</font></b></th>
	</tr>
	</thead>
	</logic:notEmpty>
	<tbody>
	 	<logic:iterate name="rocSummary"  property="rocPoints" id="rocPoint">
	 		<tr>
	 		<td><bean:write name="rocPoint" property="minProbability" /></td>
	 		<td><bean:write name="rocPoint" property="falsePositiveErrorRate" /></td>
	 		<td><bean:write name="rocPoint" property="sensitivity" /></td>
	 		<td><bean:write name="rocPoint" property="numCorrect" /></td>
	 		<td><bean:write name="rocPoint" property="numIncorrect" /></td>
	 		</tr>
	 	</logic:iterate>
	 	</tbody>
		</table>
