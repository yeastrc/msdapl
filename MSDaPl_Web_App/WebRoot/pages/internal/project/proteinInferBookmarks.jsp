
<div style="border:1px dotted gray;margin:5 5 5 5; padding:0 0 5 0;">

<div style="background-color:#ED9A2E;width:100%; margin:0; padding:3 0 3 0; color:white;" >
<span style="margin-left:10;" 
	  class="foldable fold-close" id="starred_fold">
		&nbsp;&nbsp;&nbsp;&nbsp;
</span>
<span style="padding-left:10;"><b>Favorite Protein Inferences </b></span>
</div>

<div id="starred_fold_target" style="display:none; padding:10 0 5 0;"> 

<!-- IDPicker runs -->
<logic:notEmpty name="proteinInferBookmarks">
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
		<logic:iterate name="proteinInferBookmarks" id="piRun" type="org.yeastrc.experiment.ExperimentProteinferRun">
			<tr id="bookmark_<bean:write name='piRun' property='job.pinferId'/>">
			<td valign="top"><img alt="B" class="clickable"
					src="<yrcwww:link path="images/bookmark.png"/>"
					onclick="removeBookmark(this, <bean:write name='piRun' property='job.pinferId'/>)"/>
			</td>
			
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
			</logic:equal>
			
			
			<!-- Name -->
			<td valign="top">
				<span id="piRun_<bean:write name='piRun' property='job.pinferId'/>_name_bkmrk_text"><bean:write name="piRun" property="name"/></span>
				<logic:equal name="writeAccess" value="true">
				<span class="editablePiRunName clickable" 
				data-editable_id="<bean:write name='piRun' property='job.pinferId'/>_name_bkmrk" style="font-size:8pt; color:red;">[Edit]</span>
				</logic:equal>
			</td>
			
			<!-- Comments -->
			<td valign="top">
				<span id="piRun_<bean:write name='piRun' property='job.pinferId'/>_bkmrk_text"><bean:write name="piRun" property="job.comments"/></span>
				<logic:equal name="writeAccess" value="true">
					<span class="editablePiRunComment clickable"
					data-editable_id="<bean:write name='piRun' property='job.pinferId'/>_bkmrk" style="font-size:8pt; color:red;">[Edit]</span>
				</logic:equal>
			</td>
			<td valign="top">
			
			<!-- Job COMPLETE -->
			<logic:equal name="piRun" property="job.complete" value="true">
				<nobr>
				<a href="<yrcwww:link path='viewProteinInferenceResult.do?'/>pinferId=<bean:write name='piRun' property='job.pinferId'/>">
				<b><font color="green">View</font></b></a>
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
 		 		<input type="checkbox" class="compare_cb" name="bookmarked_protinfer" value="<bean:write name='piRun' property='job.pinferId'/>"></input>
			</td>
			</logic:equal>
 		 			
			</tr>
			
		</logic:iterate>
		
		</tbody>
		</table>
		
		<table width="100%">
			<tr>
			<td  style="font-size:8pt;" width="90%">
				<ul>
				<li>Only parsimonious proteins are included in calculating indistinguishable group and protein counts</li>
				<li>#Ions = number of unique combinations of sequence + modifications + charge</li>
				</ul>
			</td>
			<td  style="text-align:center" >
				<span class="clickable small_font" style="text-decoration:underline;" onclick="javascript:selectAllProtInfer('bookmarked_protinfer');">[Select All]</span>
				<br/>
				<span class="clickable small_font" style="text-decoration:underline;" onclick="javascript:clearSelectedProtInfer('bookmarked_protinfer');">[Clear Selected]</span>
			</td>
			</tr>
		</table>
	</div>
</logic:notEmpty>

<!-- ProteinProphet runs -->
<logic:notEmpty name="proteinProphetBookmarks">
<div style="background-color: #FFFFF0; margin:5 5 5 5; padding:5; border: 1px dashed gray;" > 
	<div><b>ProteinProphet Results</b></div> 
	<table width="100%" class="sortable_table">
	<thead>
	<tr align="left">
		<th></th>
		<th class="sort-int sortable" style="color:black; "  valign="top">ID</th>
		<th valign="top">Version</th>
		<th class="sort-alpha sortable" style="color:black; " valign="top">File</th>
		<th class="sort-int sortable" style="color:black; "  valign="top" align="center">#Prophet<br>Groups</th>
		<th class="sort-int sortable" style="color:black; "  valign="top" align="center">#Indist.<br>Groups</th>
		<th class="sort-int sortable" style="color:black; "  valign="top" align="center">#Proteins</th>
		<th class="sort-int sortable" style="color:black; "  valign="top" align="center">#Pept.<br/>Seq.</th>
		<th class="sort-int sortable" style="color:black; "  valign="top" align="center">#Ions</th>
		<th class="sort-alpha sortable" style="color:black; " valign="top">Comments</th>
		<th valign="top"></th>
		<th valign="top">Compare</th></tr>
	</thead>
	<tbody>
	<logic:iterate name="proteinProphetBookmarks" id="prpRun" type="org.yeastrc.experiment.ExperimentProteinProphetRun">
		<tr>
		<td valign="top"><img alt="B" class="clickable has_bookmark"
				src="<yrcwww:link path="images/bookmark.png"/>"
				onclick="editBookmark(this, <bean:write name='prpRun' property='proteinProphetRun.id'/>)"/>
		</td>
		<td valign="top"><b><bean:write name="prpRun" property="proteinProphetRun.id"/></b></td>
		<td valign="top"><bean:write name="prpRun" property="programVersionShort"/></td>
		<td valign="top"><b><bean:write name="prpRun" property="proteinProphetRun.filename"/></b></td>
		<td valign="top" align="center" style="font-weight:bold; color:#191970; padding:0 3 0 3"><bean:write name="prpRun" property="numParsimoniousProteinProphetGroups"/></td>
		<td valign="top" align="center" style="font-weight:bold; color:#191970; padding:0 3 0 3"><bean:write name="prpRun" property="numParsimoniousProteinGroups"/></td>
		<td valign="top" align="center" style="font-weight:bold; color:#191970; padding:0 3 0 3"><bean:write name="prpRun" property="numParsimoniousProteins"/></td>
		<td valign="top" align="center" style="font-weight:bold; color:#191970; padding:0 3 0 3"><bean:write name="prpRun" property="uniqPeptideSequenceCount"/></td>
		<td valign="top" align="center" style="font-weight:bold; color:#191970; padding:0 3 0 3"><bean:write name="prpRun" property="uniqIonCount"/></td>
		<td valign="top">
			<span id="piRun_<bean:write name='prpRun' property='proteinProphetRun.id'/>_bkmrk_text"><bean:write name="prpRun" property="proteinProphetRun.comments"/></span>
			<logic:equal name="writeAccess" value="true">
			<span class="editablePiRunComment clickable"
			data-editable_id="<bean:write name='prpRun' property='proteinProphetRun.id'/>_bkmrk" style="font-size:8pt; color:red;">[Edit]</span>
			</logic:equal>
		</td>
		<td valign="top">
		<a href="<yrcwww:link path='viewProteinProphetResult.do?'/>pinferId=<bean:write name='prpRun' property='proteinProphetRun.id'/>">
			<b><font color="green">View</font></b></a>
		</td>
		<td valign="top" align="center" >
		 	<input type="checkbox" class="compare_cb" name="bookmarked_protprophet" value="<bean:write name='prpRun' property='proteinProphetRun.id'/>"></input>
		</td>
		</tr>
			
	</logic:iterate>
	</tbody>
	</table>
	
	<table width="100%">
		<tr>
		<td style="font-size:8pt;" width="100%" >
			<ul>
			<li>Subsumed proteins are excluded in calculating group and protein counts</li>
			<li>#Indist. Groups = number of indistinguishable protein groups</li>
			<li>#Ions = number of unique combinations of sequence + modifications + charge</li>
			</ul>
		</td>
		<td style="text-align:center" >
			<span class="clickable small_font" style="text-decoration:underline;" onclick="javascript:selectAllProtInfer('bookmarked_protprophet');">[Select All]</span>
			<br/>
			<span class="clickable small_font" style="text-decoration:underline;" onclick="javascript:clearSelectedProtInfer('bookmarked_protprophet');">[Clear Selected]</span>
		</td>
		</tr>
	</table>
</div>
</logic:notEmpty>

<div style="margin:5 5 5 5; padding:5; border: 1px dashed gray;" >
<table width="100%">
	<tr>
			<td colspan="9" align="right">
				<input type="checkbox" id="grpProts_bookmarked" value="group" checked="checked" />Group Indistinguishable Proteins
				&nbsp;
				<span class="clickable" style="text-decoration:underline;" onclick="javascript:compareSelectedProtInferAndMore('bookmarked');"><b>[Compare More]</b></span>
				&nbsp;
				<span class="clickable" style="text-decoration:underline;" onclick="javascript:compareSelectedProtInfer('bookmarked');"><b>[Compare]</b></span>
			</td>
		</tr>
</table>
</div>

</div>
</div>
