<%@ taglib uri="/WEB-INF/yrc-www.tld" prefix="yrcwww" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>

<div style="background-color:#ED9A2E;width:100%; margin:40 0 0 0; padding:3 0 3 0; color:white;" align="left">
	 <span style="margin-left:10;" 
	  class="foldable fold-open" id="rt_spectra_fold" >&nbsp;&nbsp;&nbsp;&nbsp; </span>
		<b>Retention Time vs MS/MS Spectra</b>
	</div>
	
	<div style="padding:10 7 10 7; margin-bottom:5; border: 1px dashed gray;background-color: #FFFFFF;" id="rt_spectra_fold_target">
	
		<!-- 
		<b>qvalue</b>: <input type="text" id="spectrart_qval_input" value="<bean:write name='qvalue'/>"/>
		<input type="button" onclick="updateSpectraRetTimeResults()" value="Update"/>
		-->
		<b><bean:write name='scorecutoff_string'/></b>
		
		<logic:present name="spectraRTDistributionChart">
		<table>
			<tr>
			<td colspan="2" align="center" style="padding-bottom: 7px;">
				<b>
				Total MS/MS Spectra: <bean:write name="spectraAnalysisStats" property="totalCount"/>
				&nbsp;
				Filtered Spectra: <bean:write name="spectraAnalysisStats" property="goodCount"/> &nbsp; 
				(<bean:write name="spectraAnalysisStats" property="percentGoodCount" />%)
				</b>
				<br>
				<logic:equal name="spectraAnalysisStats" property="hasPopulationStats" value="true">
					<img src="<bean:write name='spectraAnalysisStats' property='googleChartWithPinUrl'/>" alt="oops!"/>
				</logic:equal>
			</td>
			</tr>
			<tr>
			<td valign="top" align=>
				<img src="<bean:write name="spectraRTDistributionChart"/>" align="top" alt="#Spectra-RT Plot" style="padding-right:20px;"></img>
			</td>
			<td valign="top">
				<table class="table_basic stripe_table" width="100%">
				<thead>
				<tr>
				<th>File</th>
				<th>Total</th>
				<th>Filtered</th>
				<th>% Filtered</th>
				<logic:equal name="spectraAnalysisStats" property="hasPopulationStats" value="true">
					<th></th>
				</logic:equal>
				</tr>
				</thead>
				<tbody>
				<logic:iterate name="spectraRtFileStats" id="file">
					<tr>
						<td><bean:write name="file" property="fileName"/></td>
						<td><bean:write name="file" property="totalCount"/></td>
						<td><bean:write name="file" property="goodCount"/></td>
						<td><bean:write name="file" property="percentGoodCount"/>%</td>
						<logic:equal name="file" property="hasPopulationStats" value="true">
							<td>
								<img src="<bean:write name='file' property='googleChartUrl'/>" alt="oops!"/>
							</td>
						</logic:equal>
					</tr>
				</logic:iterate>
				</tbody>
				</table>
			</td>
			</tr>
		</table>
		</logic:present>
	</div>