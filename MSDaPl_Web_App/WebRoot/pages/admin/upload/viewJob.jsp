<%@ taglib uri="/WEB-INF/yrc-www.tld" prefix="yrcwww" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>

<%@ include file="/includes/header.jsp" %>

<%@ include file="/includes/errors.jsp" %>

<logic:notPresent name="job" scope="request">
  <logic:forward name="viewUploadJob" />
</logic:notPresent>


<yrcwww:contentbox title="View Upload Job" centered="true" width="80" widthRel="true" scheme="upload">
	<center>
	
	<a href="<yrcwww:link path='listUploadJobs.do?status=pending'/>"><b>View Pending Jobs</b></a> ||
	<a href="<yrcwww:link path='listUploadJobs.do?status=complete'/>"><b>View Completed Jobs</b></a>
	<br><br><br>
	
	<table border="0" width="85%" align="center" class="table_basic">
	
		<thead>
		<tr>
			<th width="100%" colspan="2" align="center"><span style="margin-bottom:20px;font-size:10pt;font-weight:bold;">Job Data</span></th>
		</tr>
		</thead>
		
		<tbody>
		<tr>
			<td width="20%" align="left" valign="top" class="left_align">Submitted By:</td>
			<td width="80%" align="left" valign="top" class="left_align">
				<a href="<yrcwww:link path='viewResearcher.do?'/>id=<bean:write name="job" property="submitter" />">
					<bean:write name="job" property="researcher.firstName" /> <bean:write name="job" property="researcher.lastName" /></a>
			</td>
		</tr>	

		<tr >
			<td width="20%" align="left" valign="top" class="left_align">Submitted On:</td>
			<td width="80%" align="left" valign="top" class="left_align"><bean:write name="job" property="submitDate" /></td>
		</tr>
	
		<tr >
			<td width="20%" align="left" valign="top" class="left_align">Last Change:</td>
			<td width="80%" align="left" valign="top" class="left_align"><bean:write name="job" property="lastUpdate" /></td>
		</tr>

		<tr >
			<td width="20%" align="left" valign="top" class="left_align">Status:</td>
			<td width="80%" align="left" valign="top" class="left_align">
				<logic:equal name="job" property="status" value="4">
					<bean:write name="job" property="statusDescription" /> <a href="<yrcwww:link path='viewProject.do?ID='/><bean:write name="job" property="projectID" />#Expt<bean:write name="job" property="experimentID" />"><span style="color:red;">[View Experiment]</span></a>
				</logic:equal>
				<logic:notEqual name="job" property="status" value="4"><!-- not completed -->
					<bean:write name="job" property="statusDescription" />
					
					<logic:notEqual name="job" property="status" value="1"><!-- not running -->
						[<a style="color:red;" href="<yrcwww:link path='deleteJob.do?'/>id=<bean:write name="job" property="id" scope="request"/>">Delete</a>]
						
					<logic:notEqual name="job" property="status" value="0"><!-- not waiting to run -->
					
						<!-- Retry is available only for full experiment uploads -->
						<logic:present name="experimentUploadJob">
						[<a style="color:red;" href="<yrcwww:link path='resetJob.do?'/>id=<bean:write name="job" property="id" scope="request"/>">Retry</a>]
						</logic:present>
					
					</logic:notEqual>
					</logic:notEqual>
					
				</logic:notEqual>
			</td>
		</tr>

		<logic:notEmpty name="job" property="log">
			<tr >
				<td width="100%" colspan="2" class="left_align">
					<div style="width:100%;height:auto;overflow:auto;">
						Log Text:<br><br>
						<pre style="font-size:8pt;"><bean:write name="job" property="log" /></pre>
					</div>
				</td>
			</tr>
		</logic:notEmpty>
		</tbody>
	</table>
	<br><br>
	<table border="0" width="85%" style="margin-top:10px;" align="center" class="table_basic">

		<!-- Experiment Upload Job -->
		<logic:present name="experimentUploadJob">
		<thead>
		<tr >
			<th width="100%" colspan="2" align="center"><span style="margin-bottom:20px;font-size:10pt;font-weight:bold;">Experiment Details</span></th>
		</tr>
		</thead>
		
		<tbody>
		<tr >
			<td width="20%" align="left" valign="top" class="left_align">Project:</td>
			<td width="80%" align="left" valign="top" class="left_align">
				<a href="<yrcwww:link path='viewProject.do?'/>ID=<bean:write name="job" property="projectID" />">
					<bean:write name="job" property="project.title" /></a>
			</td>
		</tr>

		<tr >
			<td width="20%" align="left" valign="top" class="left_align">Job Type:</td>
			<td width="80%" align="left" valign="top" class="left_align">
				<bean:write name="job" property="typeDescription" />
			</td>
		</tr>
		
		<tr >
			<td width="20%" align="left" valign="top" class="left_align">Directory:</td>
			<td width="80%" align="left" valign="top" class="left_align">
				<bean:write name="job" property="serverDirectory" />
			</td>
		</tr>
		
		<tr >
			<td width="20%" align="left" valign="top" class="left_align">Instrument:</td>
			<td width="80%" align="left" valign="top" class="left_align">
				<logic:present name="job" property="instrument">
					<bean:write name="job" property="instrument.name" />
				</logic:present>
				<logic:notPresent name="job" property="instrument">
					UNKNOWN
				</logic:notPresent>
			</td>
		</tr>
		
		<tr >
			<td width="20%" align="left" valign="top" class="left_align">Pipeline:</td>
			<td width="80%" align="left" valign="top" class="left_align">
				<bean:write name="job" property="pipelineLongName" />
			</td>
		</tr>

		<tr >
			<td width="20%" align="left" valign="top" class="left_align">Run Date:</td>
			<td width="80%" align="left" valign="top" class="left_align">
				<bean:write name="job" property="runDate" />
			</td>
		</tr>

		<tr>
			<td width="20%" align="left" valign="top" class="left_align">Species:</td>
			<td width="80%" align="left" valign="top" class="left_align">
				<logic:equal name="species" property="id" value="0">
					<bean:write name="species" property="name"/>
				</logic:equal>
				
				<logic:notEqual name="species" property="id" value="0">
					<a target="ncbi_window" href="http://www.ncbi.nlm.nih.gov/Taxonomy/Browser/wwwtax.cgi?mode=Info&id=<bean:write name="species" property="id"/>">
    				<i><bean:write name="species" property="name" /></i></a>
				</logic:notEqual>
			</td>
			
		</tr>
		
		<tr >
			<td width="20%" align="left" valign="top" class="left_align">Bait Desc:</td>
			<td width="80%" align="left" valign="top" class="left_align">
				<bean:write name="job" property="baitProteinDescription" />
			</td>
		</tr>

		<tr >
			<td width="20%" align="left" valign="top" class="left_align">Comments:</td>
			<td width="80%" align="left" valign="top" class="left_align">
				<bean:write name="job" property="comments" />
			</td>
		</tr>
		</tbody>
		</logic:present>
		
		
		<!-- Analysis Upload Job -->
		<logic:present name="analysisUploadJob">
		<thead>
		<tr >
			<th width="100%" colspan="2" align="center"><span style="margin-bottom:20px;font-size:10pt;font-weight:bold;">Upload Details</span></th>
		</tr>
		</thead>
		
		<tbody>
		<tr >
			<td width="20%" align="left" valign="top" class="left_align">Project:</td>
			<td width="80%" align="left" valign="top" class="left_align">
				<a href="<yrcwww:link path='viewProject.do?'/>ID=<bean:write name="job" property="projectID" />">
					<bean:write name="job" property="project.title" /></a>
			</td>
		</tr>

		<tr >
			<td width="20%" align="left" valign="top" class="left_align">Job Type:</td>
			<td width="80%" align="left" valign="top" class="left_align">
				<bean:write name="job" property="typeDescription" />
			</td>
		</tr>
		
		<tr >
			<td width="20%" align="left" valign="top" class="left_align">Directory:</td>
			<td width="80%" align="left" valign="top" class="left_align">
				<bean:write name="job" property="serverDirectory" />
			</td>
		</tr>
		
		<tr >
			<td width="20%" align="left" valign="top" class="left_align">Experiment ID:</td>
			<td width="80%" align="left" valign="top" class="left_align">
				<bean:write name="job" property="experimentID" />
			</td>
		</tr>
		
		<tr >
			<td width="20%" align="left" valign="top" class="left_align">Analysis ID:</td>
			<td width="80%" align="left" valign="top" class="left_align">
				<bean:write name="job" property="searchAnalysisId" />
			</td>
		</tr>

		<tr >
			<td width="20%" align="left" valign="top" class="left_align">Comments:</td>
			<td width="80%" align="left" valign="top" class="left_align">
				<bean:write name="job" property="comments" />
			</td>
		</tr>
		</tbody>
		</logic:present>
		
		<!-- Percolator Job -->
		<logic:present name="percolatorJob">
		<thead>
		<tr >
			<th width="100%" colspan="2" align="center"><span style="margin-bottom:20px;font-size:10pt;font-weight:bold;"> Details</span></th>
		</tr>
		</thead>
		
		<tbody>
		<tr >
			<td width="20%" align="left" valign="top" class="left_align">Project:</td>
			<td width="80%" align="left" valign="top" class="left_align">
				<a href="<yrcwww:link path='viewProject.do?'/>ID=<bean:write name="job" property="projectID" />">
					<bean:write name="job" property="project.title" /></a>
			</td>
		</tr>

		<tr >
			<td width="20%" align="left" valign="top" class="left_align">Job Type:</td>
			<td width="80%" align="left" valign="top" class="left_align">
				<bean:write name="job" property="typeDescription" />
			</td>
		</tr>
		
		<tr >
			<td width="20%" align="left" valign="top" class="left_align">Directory:</td>
			<td width="80%" align="left" valign="top" class="left_align">
				<bean:write name="job" property="serverDirectory" />
			</td>
		</tr>
		
		<tr >
			<td width="20%" align="left" valign="top" class="left_align">Experiment ID:</td>
			<td width="80%" align="left" valign="top" class="left_align">
				<bean:write name="job" property="experimentID" />
			</td>
		</tr>
		
		<tr >
			<td width="20%" align="left" valign="top" class="left_align">Search ID:</td>
			<td width="80%" align="left" valign="top" class="left_align">
				<bean:write name="job" property="searchId" />
			</td>
		</tr>

		<tr>
			<td width="20%" align="left" valign="top" class="left_align">Input Files:</td>
			<td width="80%" align="left" valign="top" class="left_align">
				<logic:iterate name="job" property="percolatorInputFiles" id="inputFile">
					<bean:write name="inputFile" property="runName"/><br/>
				</logic:iterate>
			</td>
		</tr>
		
		<tr >
			<td width="20%" align="left" valign="top" class="left_align">Run Protein Inference:</td>
			<td width="80%" align="left" valign="top" class="left_align">
				<bean:write name="job" property="runProteinInference" />
			</td>
		</tr>
		
		<logic:equal name="job" property="runProteinInference" value="true">
			<tr>
			<td width="20%" align="left" valign="top" class="left_align">Protein Inference Options:</td>
			<td width="80%" align="left" valign="top" class="left_align">
				<table width="95%" style="border: 1px dashed black;"><tbody>
				<logic:iterate name="job" property="programParams.paramList" id="param">
					<tr>
						<td><bean:write name="param" property="displayName"/></td>
						<td><bean:write name="param" property="value"/></td>
					</tr>
				</logic:iterate>
				</tbody></table>
			</td>
			</tr>
		</logic:equal>
		
		<tr >
			<td width="20%" align="left" valign="top" class="left_align">Comments:</td>
			<td width="80%" align="left" valign="top" class="left_align">
				<bean:write name="job" property="comments" />
			</td>
		</tr>
		</tbody>
		</logic:present>
		
		
	</table>
	</center>
	<br><br>
</yrcwww:contentbox>

<%@ include file="/includes/footer.jsp" %>