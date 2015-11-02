<div style="background-color: #FFFFF0; margin:5 5 5 5; padding:5; border: 1px dashed gray;" > 
		<div><b>ProteinProphet Results</b></div> 
		<table width="100%" class="sortable_table">
		<thead>
		<tr align="left">
			<th></th>
			<th class="sort-int sortable" style="color:black; " valign="top">ID</th>
			<th valign="top">Version</th>
			<th class="sort-alpha sortable" style="color:black; " valign="top">File</th>
			<th class="sort-int sortable" style="color:black; " valign="top" align="center">#Prophet<br>Groups</th>
			<th class="sort-int sortable" style="color:black; " valign="top" align="center">#Indist.<br>Groups</th>
			<th class="sort-int sortable" style="color:black; " valign="top" align="center">#Proteins</th>
			<th class="sort-int sortable" style="color:black; " valign="top" align="center">#Pept.<br/>Seq.</th>
			<th class="sort-int sortable" style="color:black; " valign="top" align="center">#Ions</th>
			<th class="sort-alpha sortable" style="color:black; " valign="top">Comments</th>
			<th valign="top"></th>
			<th valign="top">Compare</th></tr>
		</thead>
		<tbody>
		<logic:iterate name="experiment" property="proteinProphetRuns" id="prpRun" type="org.yeastrc.experiment.ExperimentProteinProphetRun">
			<tr>
			
			<!-- bookmark link is editable -->
			<logic:equal name="writeAccess" value="true">
			<logic:equal name="prpRun" property="isBookmarked" value="true">
				<td valign="top"><img alt="B" class="clickable has_bookmark"
						src="<yrcwww:link path="images/bookmark.png"/>"
						id="expt_piRun_<bean:write name='prpRun' property='proteinProphetRun.id'/>"
						onclick="editBookmark(this, <bean:write name='prpRun' property='proteinProphetRun.id'/>)"/>
				</td>
			</logic:equal>
			<logic:equal name="prpRun" property="isBookmarked" value="false">
				<td valign="top"><img alt="B" class="clickable no_bookmark"
						src="<yrcwww:link path="images/no_bookmark.png"/>"
						id="expt_piRun_<bean:write name='prpRun' property='proteinProphetRun.id'/>"
						onclick="javascript:editBookmark(this, <bean:write name='prpRun' property='proteinProphetRun.id'/>)"/>
				</td>
			</logic:equal>
			</logic:equal>
			
			
			<!-- bookmark link is NOT editable -->
			<logic:equal name="writeAccess" value="false">
				<logic:equal name="prpRun" property="isBookmarked" value="true">
				<td valign="top"><img alt="B" src="<yrcwww:link path="images/bookmark.png"/>"/>
				</td>
				</logic:equal>
				<logic:equal name="prpRun" property="isBookmarked" value="false">
				<td valign="top"><img alt="B" src="<yrcwww:link path="images/no_bookmark.png"/>"/>
				</td>
				</logic:equal>
			</logic:equal>
			
			<td valign="top"><b><bean:write name="prpRun" property="proteinProphetRun.id"/></b></td>
			<td valign="top"><bean:write name="prpRun" property="programVersionShort"/></td>
			<td valign="top"><b><bean:write name="prpRun" property="proteinProphetRun.filename"/></b></td>
			<td valign="top" align="center" style="font-weight:bold; color:#191970; padding:0 3 0 3"><bean:write name="prpRun" property="numParsimoniousProteinProphetGroups"/></td>
			<td valign="top" align="center" style="font-weight:bold; color:#191970; padding:0 3 0 3"><bean:write name="prpRun" property="numParsimoniousProteinGroups"/></td>
			<td valign="top" align="center" style="font-weight:bold; color:#191970; padding:0 3 0 3"><bean:write name="prpRun" property="numParsimoniousProteins"/></td>
			<td valign="top" align="center" style="font-weight:bold; color:#191970; padding:0 3 0 3"><bean:write name="prpRun" property="uniqPeptideSequenceCount"/></td>
			<td valign="top" align="center" style="font-weight:bold; color:#191970; padding:0 3 0 3"><bean:write name="prpRun" property="uniqIonCount"/></td>
			<td valign="top">
				<span id="piRun_<bean:write name='prpRun' property='proteinProphetRun.id'/>_main_text"><bean:write name="prpRun" property="proteinProphetRun.comments"/></span>
				<logic:equal name="writeAccess" value="true">
				<span class="editablePiRunComment clickable"
				data-editable_id="<bean:write name='prpRun' property='proteinProphetRun.id'/>_main" style="font-size:8pt; color:red;">[Edit]</span>
				</logic:equal>
			</td>
			<td valign="top">
			<a href="<yrcwww:link path='viewProteinProphetResult.do?'/>pinferId=<bean:write name='prpRun' property='proteinProphetRun.id'/>">
				<b><font color="green">View</font></b></a>
			</td>
			<td valign="top" align="center" >
 		 		<input type="checkbox" class="compare_cb" name="<bean:write name='experiment' property='id'/>" value="<bean:write name='prpRun' property='proteinProphetRun.id'/>"></input>
			</td>
			</tr>
			
		</logic:iterate>
		</tbody>
		</table>
		
		<table width="100%">
		<tr>
		<td style="font-size:8pt;" width="90%">
			<ul>
			<li>Subsumed proteins are excluded in calculating group and protein counts</li>
			<li>#Indist. Groups = number of indistinguishable protein groups</li>
			<li>#Ions = number of unique combinations of sequence + modifications + charge</li>
			</ul>
		</td>
		<td style="text-align:center" >
			<span class="clickable small_font" style="text-decoration:underline;" onclick="javascript:selectAllProtInfer(<bean:write name='experiment' property='id'/>);">[Select All]</span>
			<br/>
			<span class="clickable small_font" style="text-decoration:underline;" onclick="javascript:clearSelectedProtInfer(<bean:write name='experiment' property='id'/>);">[Clear Selected]</span>
		</td>
		</tr>
		</table>
	</div>