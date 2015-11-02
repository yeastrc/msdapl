<%@ taglib uri="/WEB-INF/yrc-www.tld" prefix="yrcwww" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>

<%@ include file="/includes/header.jsp" %>

<%@ include file="/includes/errors.jsp" %>

<yrcwww:contentbox title="List MS Upload Jobs" centered="true" width="100" scheme="upload" widthRel="true">

<logic:notPresent name="jobs" scope="request">
  <logic:forward name="listUploadJobs" />
</logic:notPresent>


<logic:equal name="queued" value="true" scope="request">
 <center>
 <hr width="50%">
  <B><font color="red">Your job request has been successfully added to the queue.<BR>
  The ID(s) for the request are <bean:write name="jobId"/>.<br/>
  You will be notified by email when the job(s) are complete.</font></B>
 <hr width="50%">
 </center>
</logic:equal>

<logic:empty name="jobs" scope="request">

	<logic:equal name="status" scope="request" value="pending">
		<p align="center">There are no pending MS upload jobs.</p>
		<p align="center"><a href="<yrcwww:link path='listUploadJobs.do?status=complete' />">View Completed Jobs</a></p>
	</logic:equal>
	
	<logic:notEqual name="status" scope="request" value="pending">
		<p align="center">There are no completed MS upload jobs.</p>
		<p align="center"><a href="<yrcwww:link path='listUploadJobs.do?status=pending' />">View Pending Jobs</a></p>
	</logic:notEqual>
	
</logic:empty>


<logic:notEmpty name="jobs" scope="request">


	<logic:equal name="status" scope="request" value="pending">
		<p align="center">Listed below are the <b>pending</b> MS uploads.</p>
		<p align="center"><a href="<yrcwww:link path='listUploadJobs.do?status=complete' />">View Completed Jobs</a></p>
	</logic:equal>
	
	<logic:notEqual name="status" scope="request" value="pending">
		<p align="center">Listed below are the <b>completed</b> MS uploads.</p>
		<p align="center"><a href="<yrcwww:link path='listUploadJobs.do?status=pending' />">View Pending Jobs</a></p>
	</logic:notEqual>
	
	
	<p align="center">Showing results <bean:write name="firstResult" scope="request" /> to <bean:write name="lastResult" scope="request" /> of <bean:write name="totalCount" scope="request" /></p>
	
	<p align="center">
		<logic:present name="previousIndex" scope="request">
			<a href="<yrcwww:link path='listUploadJobs.do?'/>status=<bean:write name="status" scope="request" />&index=<bean:write name="previousIndex" scope="request" />">View Previous 50</a>
		</logic:present>
		<logic:present name="nextIndex" scope="request">
			<a href="<yrcwww:link path='listUploadJobs.do?'/>status=<bean:write name="status" scope="request" />&index=<bean:write name="nextIndex" scope="request" />">View Next 50</a>
		</logic:present>
	</p>
		
	<table border="0" cellpadding="3" cellspacing="2" width="95%" class="table_basic" align="center" style="table-layout:fixed;">
	
		<thead>
		<tr>
			<th width="30px">ID</th>
			<th width="20px">&nbsp;</th>
			<th width="40px">Status</th>
			<th width="30px">Type</th>
			<th width="60px">Submitter</th>
			<th width="70px">Sub. Date</th>
			<th width="60%">Directory</th>
			<th width="60px">PI</th>
			<th width="60px">Instrument</th>
			<th width="40%">Comments</th>
		</tr>
		</thead>
	
		<tbody>
		<logic:iterate name="jobs" scope="request" id="job">
			<tr>
				<td align="left" valign="top" style="width:1%;">
					<bean:write name="job" property="id" /> 
				</td>
				
				<td align="left" valign="top" style="width:1%;">
					<a href="<yrcwww:link path='viewUploadJob.do?'/>id=<bean:write name="job" property="id" />"><span class="small_font">View</span>
				</td>
				
				<td align="left" valign="top" style="width:8%;font-size:8pt;">

					<logic:equal name="job" property="status" value="4">
						<a href="<yrcwww:link path='viewProject.do?ID='/><bean:write name="job" property="projectID" />#Expt<bean:write name="job" property="experimentID" />"><bean:write name="job" property="statusDescription" /></a>
					</logic:equal>
					<logic:notEqual name="job" property="status" value="4">
						<bean:write name="job" property="statusDescription" />
					</logic:notEqual>
				</td>
				
				<td align="left" valign="top" style="width:1%;">
					<span class="tooltip" title='<bean:write name="job" property="typeDescription" />'><bean:write name="job" property="typeDescriptionChar" /></span> 
				</td>
				
				<td align="left" valign="top" style="width:10%;font-size:8pt;">
					<div style="width:100%;height:auto;">
						<a href="<yrcwww:link path='viewResearcher.do?'/>id=<bean:write name="job" property="submitter" />">
							<bean:write name="job" property="researcher.lastName" />, <bean:write name="job" property="researcher.firstName" />
						</a>
					</div>
				</td>
					
				<td align="left" valign="top" style="width:10%;font-size:8pt;"><bean:write name="job" property="submitDate" /></td>

				<td align="left" valign="top" style="width:32%;font-size:8pt;" class="left_align hardBreak">
					<div style="width:100%;height:auto;">
						<bean:write name="job" property="serverDirectory" />
					</div>
				</td>
				
				<td align="left" valign="top" style="width:7%;font-size:8pt;">
					<div style="width:100%;height:auto;">
						<bean:write name="job" property="project.PI.lastName" />
					</div>
				</td>

				<td align="left" valign="top" style="width:7%;font-size:8pt;" class="left_align">
					<div style="width:100%;height:auto;">
					<logic:present name="job" property="instrument">
						<bean:write name="job" property="instrument.name" />
					</logic:present>
					<logic:notPresent name="job" property="instrument">
						UNKNOWN
					</logic:notPresent>
					</div>
				</td>
				
				<td align="left" valign="top" style="width:15%;font-size:8pt;" class="left_align hardBreak">
					<div style="width:100%;height:auto;">
						<bean:write name="job" property="comments" />
					</div>
				</td>
				
				
			</tr>
		</logic:iterate>
		</tbody>
	</table>

	<p align="center">
		<logic:present name="previousIndex" scope="request">
			<a href="<yrcwww:link path='listUploadJobs.do?'/>status=<bean:write name="status" scope="request" />&index=<bean:write name="previousIndex" scope="request" />">View Previous 50</a>
		</logic:present>
		<logic:present name="nextIndex" scope="request">
			<a href="<yrcwww:link path='listUploadJobs.do?'/>status=<bean:write name="status" scope="request" />&index=<bean:write name="nextIndex" scope="request" />">View Next 50</a>
		</logic:present>
	</p>

</logic:notEmpty>





</yrcwww:contentbox>

<%@ include file="/includes/footer.jsp" %>