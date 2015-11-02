<%@ taglib uri="/WEB-INF/yrc-www.tld" prefix="yrcwww" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>

<div style="background-color:#ED9A2E;width:100%; margin:40 0 0 0; padding:3 0 3 0; color:white;" align="left">
	 <span style="margin-left:10;" 
	  class="foldable fold-open" id="psm_delta_mass_fold" >&nbsp;&nbsp;&nbsp;&nbsp; </span>
		<b>Delta precursor mass historgram</b>
	</div>
	
	<div style="padding:10 7 10 7; margin-bottom:5; border: 1px dashed gray;background-color: #FFFFFF;" id="psm_delta_mass_fold_target">
	
		<b>qvalue</b>: <input type="text" id="psm_delta_mass_qval_input" value="<bean:write name='qvalue'/>"/>
		<logic:equal name="usePpmMassDiff" value="true">
			<input type="radio" name="massType" value="Da"/>Da
			<input type="radio" name="massType" value="ppm" checked="checked"/>ppm
		</logic:equal>
		<logic:equal name="usePpmMassDiff" value="false">
			<input type="radio" value="Da"  name="massType" checked="checked"/>Da
			<input type="radio" name="massType" value="ppm"/>ppm
		</logic:equal>
		
		<br/>
		<input type="button" onclick="updatePsmDeltaMassResults()" value="Update"/>
		
	
		<logic:present name="deltaMassStats">
		<table>
		<tbody>
			<tr>
			<td colspan="2"><b># Filtered:</b></td>
			<td colspan="2"><bean:write name="deltaMassStats" property="totalCount"/></td>
			</tr>
			<tr>
			<td><b>Min. delta mass: </b></td>
			<td><bean:write name="deltaMassStats" property="minDiff"/></td>
			<td><b>Max. delta mass: </b></td>
			<td><bean:write name="deltaMassStats" property="maxDiff"/></td>
			</tr>
			
			<tr>
			<td><b>Mean. delta mass: </b></td>
			<td><bean:write name="deltaMassStats" property="mean"/></td>
			<td><b>Std. Dev.: </b></td>
			<td><bean:write name="deltaMassStats" property="stdDev"/></td>
			</tr>
		</tbody>
		</table>	
		<img src="<bean:write name="deltaMassStats" property="googleChartUrl" />" align="top" alt="Delta mass plot" style="padding-right:20px;"></img>
			
		</logic:present>
	</div>