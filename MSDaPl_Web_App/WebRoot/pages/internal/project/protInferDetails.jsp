<div style="background-color: #F0F8FF; margin:5 5 5 5; padding:5; border: 1px dashed gray;" >
		<div><b>Protein Inference Results</b></div> 
		<table width="100%" class="sortable_table">
		<thead>
		<tr align="left">
			<th></th>
			<th class="sort-int sortable" style="color:black; " valign="top">ID</th>
			<th class="sort-alpha sortable" style="color:black; " valign="top">User</th>
			<th valign="top">Version</th>
			<th valign="top">Date</th>
			<th class="sort-int sortable" style="color:black; " valign="top" align="center">#Indist.<br>Groups</th>
			<th class="sort-int sortable" style="color:black; " valign="top" align="center">#Proteins</th>
			<th class="sort-int sortable" style="color:black; " valign="top" align="center">#Pept.<br/>Seq.</th>
			<th class="sort-int sortable" style="color:black; " valign="top" align="center">#Ions</th>
			<th class="sort-alpha sortable" style="color:black; " valign="top">Name</th>
			<th class="sort-alpha sortable" style="color:black; " valign="top">Comments</th>
			<th valign="top">&nbsp;</th>
			<th valign="top">Compare</th></tr>
		</thead>
		<tbody>
		<logic:iterate name="analysis" property="protInferRuns" id="piRun" type="org.yeastrc.experiment.ExperimentProteinferRun">
			<tr>
			<!-- Protein inference job is complete -->
			<logic:equal name="piRun" property="job.complete" value="true">
			
			<!-- bookmark link is editable -->
			<logic:equal name="writeAccess" value="true">
				<logic:equal name="piRun" property="isBookmarked" value="true">
				<td valign="top"><img alt="B" class="clickable has_bookmark"
						src="<yrcwww:link path="images/bookmark.png"/>"
						id="expt_piRun_<bean:write name='piRun' property='job.pinferId'/>"
						onclick="editBookmark(this, <bean:write name='piRun' property='job.pinferId'/>)"/>
				</td>
				</logic:equal>
				<logic:equal name="piRun" property="isBookmarked" value="false">
				<td valign="top"><img alt="B" class="clickable no_bookmark"
						src="<yrcwww:link path="images/no_bookmark.png"/>"
						id="expt_piRun_<bean:write name='piRun' property='job.pinferId'/>"
						onclick="javascript:editBookmark(this, <bean:write name='piRun' property='job.pinferId'/>)"/>
				</td>
				</logic:equal>
			</logic:equal>
			
			<!-- bookmark link is NOT editable -->
			<logic:equal name="writeAccess" value="false">
				<logic:equal name="piRun" property="isBookmarked" value="true">
				<td valign="top"><img alt="B" src="<yrcwww:link path="images/bookmark.png"/>"/>
				</td>
				</logic:equal>
				<logic:equal name="piRun" property="isBookmarked" value="false">
				<td valign="top"><img alt="B" src="<yrcwww:link path="images/no_bookmark.png"/>"/>
				</td>
				</logic:equal>
			</logic:equal>
			</logic:equal>
			
			
			<!-- Protein inference job is NOT complete -->
			<logic:equal name="piRun" property="job.complete" value="false">
				<td>&nbsp;</td>
			</logic:equal>
			
			<td valign="top"><b><bean:write name="piRun" property="job.pinferId"/></b></td>
			<td valign="top"><bean:write name="piRun" property="job.researcher.lastName"/></td>
			<td valign="top" align="center"><b><bean:write name="piRun" property="job.version"/></b></td>
			<td valign="top"><bean:write name="piRun" property="job.submitDate"/></td>
			
			<logic:equal name="piRun" property="job.complete" value="true">
			<td valign="top" align="center" style="font-weight:bold; color:#191970; padding:0 3 0 3"><bean:write name="piRun" property="numParsimoniousProteinGroups"/></td>
			<td valign="top" align="center" style="font-weight:bold; color:#191970; padding:0 3 0 3"><bean:write name="piRun" property="numParsimoniousProteins"/></td>
			<td valign="top" align="center" style="font-weight:bold; color:#191970; padding:0 3 0 3"><bean:write name="piRun" property="uniqPeptideSequenceCount"/></td>
			<td valign="top" align="center" style="font-weight:bold; color:#191970; padding:0 3 0 3"><bean:write name="piRun" property="uniqIonCount"/></td>
			</logic:equal>
			
			<logic:equal name="piRun" property="job.complete" value="false">
			<td valign="top">&nbsp;</td>
			<td valign="top">&nbsp;</td>
			<td valign="top">&nbsp;</td>
			<td valign="top">&nbsp;</td>
			</logic:equal>
			
			
			<!-- Name -->
			<td valign="top">
				<span id="piRun_<bean:write name='piRun' property='job.pinferId'/>_name_main_text"><bean:write name="piRun" property="name"/></span>
				<logic:equal name="writeAccess" value="true">
				<span class="editablePiRunName clickable" 
				data-editable_id="<bean:write name='piRun' property='job.pinferId'/>_name_main" style="font-size:8pt; color:red;">[Edit]</span>
				</logic:equal>
			</td>
			
			<!-- Comments -->
			<td valign="top">
				<span id="piRun_<bean:write name='piRun' property='job.pinferId'/>_main_text"><bean:write name="piRun" property="job.comments"/></span>
				<logic:equal name="writeAccess" value="true">
				<span class="editablePiRunComment clickable" 
				data-editable_id="<bean:write name='piRun' property='job.pinferId'/>_main" style="font-size:8pt; color:red;">[Edit]</span>
				</logic:equal>
			</td>
			<td valign="top">
			
			<!-- Job COMPLETE -->
			<logic:equal name="piRun" property="job.complete" value="true">
				<nobr>
				<a href="<yrcwww:link path='viewProteinInferenceResult.do?'/>pinferId=<bean:write name='piRun' property='job.pinferId'/>">
				<b><font color="green">View</font></b></a>
				
				<logic:equal name="writeAccess" value="true">
				&nbsp;
				<span class="clickable" style="text-decoration: underline; color:red;" 
				      onclick="javascript:deleteProtInferRun(<bean:write name='piRun' property='job.pinferId'/>);">Delete</span>
				</nobr>
				</logic:equal>
				
				<yrcwww:member group="administrators">
				&nbsp;
				<span class="clickable" style="text-decoration: underline; color:red;" 
				      onclick="javascript:rerunProtInferRun(<bean:write name='piRun' property='job.pinferId'/>);">R</span>
				</yrcwww:member>
				</nobr>
			</logic:equal>
			<!-- Job FAILED -->
			<logic:equal name="piRun" property="job.failed" value="true">
				<a href="<yrcwww:link path='viewProteinInferenceJob.do?'/>pinferId=<bean:write name='piRun' property='job.pinferId'/>&projectId=<bean:write name='experiment' property='projectId'/>">
				<b><font color="red"><bean:write name="piRun" property="job.statusDescription"/></font></b>
				</a>
			</logic:equal>
			<!-- Job RUNNING -->
			<logic:equal name="piRun" property="job.running" value="true">
				<a href="<yrcwww:link path='viewProteinInferenceJob.do?'/>pinferId=<bean:write name='piRun' property='job.pinferId'/>&projectId=<bean:write name='experiment' property='projectId'/>">
				<b><font color="#000000"><bean:write name="piRun" property="job.statusDescription"/></font></b>
				</a>
			</logic:equal>
			
 		 	</td>
 		 			
 		 	<logic:equal name="piRun" property="job.complete" value="true">
 		 	<td valign="top" align="center" >
 		 		<input type="checkbox" class="compare_cb"  name="<bean:write name='analysis' property='id'/>" value="<bean:write name='piRun' property='job.pinferId'/>"></input>
			</td>
			</logic:equal>
 		 			
			</tr>
			
		</logic:iterate>
		
		</tbody>
		</table>
		<table width="100%">
		<tr>
		<td style="font-size:8pt;" width="90%">
			<ul>
			<li>Only parsimonious proteins are included in calculating indistinguishable group and protein counts</li>
			<li>#Ions = number of unique combinations of sequence + modifications + charge</li>
			</ul>
		</td>
		<td style="text-align:center" >
			<span class="clickable small_font" style="text-decoration:underline;" onclick="javascript:selectAllProtInfer(<bean:write name='analysis' property='id'/>);">[Select All]</span>
			<br/>
			<span class="clickable small_font" style="text-decoration:underline;" onclick="javascript:clearSelectedProtInfer(<bean:write name='analysis' property='id'/>);">[Clear Selected]</span>
		</td>
		</tr>
		</table> 
		
	</div>
	<div style="margin:5 5 5 5; padding:5; border: 1px dashed gray;" >
	<table width="100%">
	<tr>
			<td colspan="9" align="right">
				<input type="checkbox" id="grpProts_<bean:write name='analysis' property='id'/>" value="group" checked="checked" />Group Indistinguishable Proteins
				&nbsp;
				<span class="clickable" style="text-decoration:underline;" onclick="javascript:compareSelectedProtInferAndMore(<bean:write name='analysis' property='id'/>);"><b>[Compare More]</b></span>
				&nbsp;
				<span class="clickable" style="text-decoration:underline;" onclick="javascript:compareSelectedProtInfer(<bean:write name='analysis' property='id'/>);"
				title="Protein inferences from multiple experiments in this project can be selected for comparison.  To include protein inferences from other projects click on 'Compare More'.">
					<b>[Compare]</b>
				</span>
			</td>
		</tr>
	</table>
	</div>