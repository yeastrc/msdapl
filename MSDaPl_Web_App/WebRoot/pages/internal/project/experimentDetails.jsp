<%@page import="org.yeastrc.ms.domain.search.Program"%>
<%@page import="org.yeastrc.ms.domain.general.MsInstrument"%>
<%@ taglib uri="/WEB-INF/yrc-www.tld" prefix="yrcwww" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>


<A name='Expt<bean:write name="experiment" property="id"/>'></A> 


			<div style="border:1px dotted gray;margin:5 5 5 5; padding:0 0 5 0;" 
				id="exp_root_target_${ experiment.id }"
				class="experiment_details_outer_div_jq"
				experiment_id="${ experiment.id }"
				experiment_has_full_information="${ experiment.hasFullInformation }"
				experiment_upload_success="${ experiment.uploadSuccess }"
				>

				
			<div style="background-color:#ED9A2E;width:100%; margin:0; padding:3 0 3 0; color:white;" >
			
			
				<logic:equal name="experiment" property="hasFullInformation" value="false">
				<span style="margin-left:10;" 
				      class="foldable fold-close" id="expt_fold_<bean:write name="experiment" property="id"/>" 
				      onclick="showExperimentDetails(<bean:write name="experiment" property="id"/>)">
				&nbsp;&nbsp;&nbsp;&nbsp;</span>
				</logic:equal>
				
				<logic:equal name="experiment" property="hasFullInformation" value="true">
				<span style="margin-left:10;" 
				      class="foldable fold-open" id="expt_fold_<bean:write name="experiment" property="id"/>" 
				      onclick="showExperimentDetails(<bean:write name="experiment" property="id"/>)">
				&nbsp;&nbsp;&nbsp;&nbsp;</span>
				</logic:equal>
				
				<span style="padding-left:10;"><b>Experiment ID: <bean:write name="experiment" property="id"/></b></span>
			</div>
			
			
			<div style="padding:0; margin:0;"> 
			<div style="margin:0; padding:5;">

			<table cellspacing="0" cellpadding="0" style="width: 100%">		
			 <tr>	
			  <td>
			
			
				<table cellspacing="0" cellpadding="0">		
					<tr>	
						<td><b>Date Uploaded: </b></td>
						<td style="padding-left:10px; padding-right: 10px;">
							<bean:write name="experiment" property="uploadDate"/> 
							<logic:equal name="writeAccess" value="true">
								&nbsp; &nbsp;
								<span class="clickable underline" style="color:red; font-weight:bold;" 
									onClick="confirmDeleteExperiment('<bean:write name="experiment" property="id"/>')">[Delete Experiment]</span>
							</logic:equal>
						</td>
					</tr>
					<tr>
						<td><b>Location: </b></td>
						<td style="padding-left:10px; padding-right: 10px;"><bean:write name="experiment" property="serverDirectory"/></td>
					</tr>
					<tr>
						<td style="white-space:nowrap;"><b>Instrument: </b>
							<logic:equal name="writeAccess" value="true">
								<span class="editableInstrument clickable" 
								      id="instrumentfor_<bean:write name='experiment' property='id'/>" 
								      title="<bean:write name='experiment' property='instrumentId'/>_<bean:write name='experiment' property='id'/>"
								      style="font-size:8pt; color:red;">[Change]</span>
							</logic:equal>
						</td>
						<td style="padding-left:10px; padding-right: 10px;">
							<span
								id="instrumentfor_<bean:write name='experiment' property='id'/>_select"
							>
								<bean:write name="experiment" property="instrumentName"/>
							</span>
						</td>
					</tr>
					<tr>
						<td valign="top"  style="white-space:nowrap;"><b>Comments </b>
							<logic:equal name="writeAccess" value="true">
							<span class="editableComment clickable" data-editable_id="experiment_<bean:write name='experiment' property='id'/>" style="font-size:8pt; color:red;">[Edit]</span>
							</logic:equal>
							<b>: </b></td>
						<td style="padding-left:10">
							<div id="experiment_<bean:write name='experiment' property='id'/>_text"><bean:write name="experiment" property="comments"/></div>
							<div id="experiment_<bean:write name='experiment' property='id'/>_edit" align="center"
							     style="display:none;">
							     <textarea rows="5" cols="60" class="edit_text"></textarea>
							     <br>
							     <button class="saveExptComments" data-editable_id="<bean:write name='experiment' property='id'/>">Save</button>
							     <button class="cancelExptComments" data-editable_id="<bean:write name='experiment' property='id'/>">Cancel</button>
							</div>
						</td>
					</tr>
					<logic:equal name="experiment" property="uploadSuccess" value="false">
						<tr>
							<td style="color:red; font-weight:bold;">Upload Failed</td>
							<td><html:link action="viewUploadJob.do" 
										   paramId="id" 
										   paramName="experiment" paramProperty="uploadJobId">View Log</html:link></td>
						</tr>
					</logic:equal>
				</table>
			  </td>
			  
			  <%--  td and divs for showing preMZ chart thumbnail --%>
			  <td  style="width: 150px;">  <%-- Set td width as needed for image --%>
			  
			   <div class="experiment_details_qc_plots_div_jq" style="display: none;" >
			   
			   <table cellspacing="0" cellpadding="0" style="width: 100%">		
			 	<tr>	
			  	 <td><span style="white-space:nowrap;">QC Plots</span></td>
			  	</tr>
			  	<tr>
			  	 <td>

			   	  <logic:equal name="experiment" property="uploadSuccess" value="true">
			   		<%--  Only create this chart for experiments that have successfully been loaded --%>
			   		
				   <div class="experiment_precursor_scan_count_chart_outer_div_jq" style="display: none; float: left; padding-right: 5px;">
				    <%--  Outer div used to crop thumbnail image --%>
				  	<div style="width: 40px; height: 40px; position: relative; overflow: hidden; border-color: grey; border-style: solid; border-width: 2px; background-color: transparent;"
				  		>
				  		<%-- Div that Google chart will put the thumbnail image in.  Positioned to show just the chart through the clipping of the parent div --%>
				  		<div style="position: absolute; left: -10px; top: -12px;"
				  					 class="experiment_precursor_scan_count_chart_div_jq"  
				  					 id="experiment_<bean:write name='experiment' property='id'/>_preMZ_Chart">
				  	    </div>
				  	    <%-- overlay div on top of chart for click handling and tool tip --%>
				  		<div style="position: absolute; left: 0px; top: 0px; width: 40px; height: 40px; cursor: pointer;"
				  			class="experiment_precursor_scan_count_chart_click_for_full_size_jq "
				  			title="View Precursor M/Z distribution">
				  		
				  			&nbsp;
				  	    </div>
				    </div>
				   </div>
				   
				   <div class="experiment_peak_count_chart_outer_div_jq" style="display: none; float: left; padding-right: 5px;">
				    <%--  Outer div used to crop thumbnail image --%>
				  	<div style="width: 40px; height: 40px; position: relative; overflow: hidden; border-color: grey; border-style: solid; border-width: 2px; background-color: transparent;"
				  		>
				  		<%-- Div that Google chart will put the thumbnail image in.  Positioned to show just the chart through the clipping of the parent div --%>
				  		<div style="position: absolute; left: -10px; top: -12px;"
				  					 class="experiment_peak_count_chart_div_jq"  
				  					 id="experiment_<bean:write name='experiment' property='id'/>_peak_count_Chart">
				  	    </div>
				  	    <%-- overlay div on top of chart for click handling and tool tip --%>
				  		<div style="position: absolute; left: 0px; top: 0px; width: 40px; height: 40px; cursor: pointer;"
				  			class="experiment_peak_count_chart_click_for_full_size_jq "
				  			title="View Scan Peak Count distribution">
				  		
				  			&nbsp;
				  	    </div>
				    </div>
				   </div>
				   
				   <div class="experiment_intensity_count_chart_outer_div_jq" style="display: none;">
				    <%--  Outer div used to crop thumbnail image --%>
				  	<div style="width: 40px; height: 40px; position: relative; overflow: hidden; border-color: grey; border-style: solid; border-width: 2px; background-color: transparent;"
				  		>
				  		<%-- Div that Google chart will put the thumbnail image in.  Positioned to show just the chart through the clipping of the parent div --%>
				  		<div style="position: absolute; left: -10px; top: -12px;"
				  					 class="experiment_intensity_count_chart_div_jq"  
				  					 id="experiment_<bean:write name='experiment' property='id'/>_intensity_count_Chart">
				  	    </div>
				  	    <%-- overlay div on top of chart for click handling and tool tip --%>
				  		<div style="position: absolute; left: 0px; top: 0px; width: 40px; height: 40px; cursor: pointer;"
				  			class="experiment_intensity_count_chart_click_for_full_size_jq "
				  			title="View Intensity distribution">
				  		
				  			&nbsp;
				  	    </div>
				    </div>
				   </div>
				   
				  </logic:equal>

				 </td>
			  	</tr>
			   </table>
			   
			   </div>
			   
			  </td>
			 </tr>
			</table>
			</div>
			
			<input type="hidden" class="experiment_precursor_scan_count_chart_data_jq" 
				experiment_id="<bean:write name='experiment' property='id'/>"
				value='<bean:write name="experiment" property="precursorMassChartData" filter="false"/>' />

			<input type="hidden" class="experiment_peak_count_chart_data_jq" 
				experiment_id="<bean:write name='experiment' property='id'/>"
				value='<bean:write name="experiment" property="peakCountChartData" filter="false"/>' />
			
			<input type="hidden" class="experiment_intensity_count_chart_data_jq" 
				experiment_id="<bean:write name='experiment' property='id'/>"
				value='<bean:write name="experiment" property="intensityCountChartData" filter="false"/>' />
			
			
			
			
			<%--  Divs for showing preMZ chart full size in overlay using "colorbox" jQuery plugin --%>
			<div style="display: none;" >
				<div class="experiment_precursor_scan_count_chart_full_size_jq" style="width: 850px; height: 450px; " 
					title="   " ></div>
			</div>
			
			<%--  Divs for showing Peak Count chart full size in overlay using "colorbox" jQuery plugin --%>
			<div style="display: none;" >
				<div class="experiment_peak_count_full_size_jq" style="width: 850px; height: 450px; " 
					title="   " ></div>
			</div>
			
			<%--  Divs for showing Intensity Count chart full size in overlay using "colorbox" jQuery plugin --%>
			<div style="display: none;" >
				<div class="experiment_intensity_count_full_size_jq" style="width: 850px; height: 450px; " 
					title="   " ></div>
			</div>
										
			
			<logic:equal name="experiment" property="hasFullInformation" value="false">
				<div id="expt_fold_<bean:write name="experiment" property="id"/>_target"></div>
			</logic:equal>
			
			
			<logic:equal name="experiment" property="hasFullInformation" value="true">
			<div id="expt_fold_<bean:write name="experiment" property="id"/>_target"> <!-- begin collapsible div -->
			
				<%@ include file="experimentFullDetails.jsp" %>
			
			</div> <!-- end of collapsible div -->
			</logic:equal>
		</div> 
		
			
		
		</div> <!-- End of one experiment -->