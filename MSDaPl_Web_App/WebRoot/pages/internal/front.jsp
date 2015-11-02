<%@ taglib uri="/WEB-INF/yrc-www.tld" prefix="yrcwww" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>

<yrcwww:notauthenticated>
 <logic:forward name="authenticate" />
</yrcwww:notauthenticated>

<!-- Make sure we have our Collections defined, if not, go get them -->
<logic:notPresent name="userProjects" scope="request">
	<logic:forward name="standardHome"/>
</logic:notPresent>

<%@ include file="/includes/header.jsp" %>

<%@ include file="/includes/errors.jsp" %>


<!-- SHOW ALL PROJECTS, FOR WHICH THIS USER IS LISTED AS A RESEARCHER -->
<p><yrcwww:contentbox title="Your Projects" centered="true" width="700">


<!--
<div style="color:red;margin:20px;">
	<b><em>02/04/2011:</em></b> Please note that the default options for protein inference have been changed.  
	If you've inferred proteins using results from Percolator 1.17 please re-run protein inference using the new default options.
	<br/>
	<span id="morelink" class="clickable underline" onclick="$('#moreinfo').show(); $(this).hide(); return false";><b>More...</b></span>
       	<div id="moreinfo" style="display:none">
       	The default PSM-level qvalue cutoff has been changed from 1.0 to 0.01. 
       	Applying a peptide qvalue cutoff but not a PSM qvalue cutoff will include all PSMs for a peptide
       	even if they did not have a good qvalue.  This will lead to the following problems:
       	<ol>
       	<li>Percolator calculates a single qvalue for a peptide over all the input files.  This is usually all the files in an experiment. 
       	   If you then run protein inference separately on each file in the experiment, using only a peptide qvalue cutoff,
       	   a peptide will get included in the analysis for a particular file even if there are no good PSMs for that peptide in the file.
       	   You should ideally run Percolator separately on each file first, before inferring proteins.
       	</li>
       	<li>Spectrum counts for a protein will include spectra from bad PSMs.</li> 
       	<li>In combination with the "Remove Ambiguous Spectra" option this can lead to removal of peptides from the analysis that would not have been removed if a 
       	PSM cutoff had been applied.</li>
       	</ol>
       	<span class="clickable underline" onclick="$('#moreinfo').hide(); $('#morelink').show(); return false;">Hide</span>
       	</div> 
</div>
-->

 <logic:empty name="userProjects">
 	<p>You do not have any projects at this time.  
 	Click <b><html:link action="newProject.do">here</html:link></b> to create a new project.</p>
 </logic:empty>
 
 <logic:notEmpty name="userProjects">
 <TABLE BORDER="0" WIDTH="100%" class="table_basic">
 <thead>
  <TR>
   <TH>&nbsp;</TH>
   <TH>ID</TH>
   <TH>Title</TH>
   <TH>Submit Date</TH>
  </TR>
  </thead>

<tbody>
<logic:iterate id="project" name="userProjects" scope="request">
 <TR>
  <TD valign="top">
   <NOBR>
    <html:link action="viewProject.do" paramId="ID" paramName="project" paramProperty="ID">View</html:link>
   </NOBR>
  </TD>
  <TD valign="top"><bean:write name="project" property="ID"/></TD>
  <TD valign="top" class="left_align"><bean:write name="project" property="title"/></TD>
  <TD valign="top"><bean:write name="project" property="submitDate"/></TD>
 </TR>
</logic:iterate>
</tbody>
 </TABLE>
 </logic:notEmpty>
</yrcwww:contentbox>

<br>
<!-- SHOW ANY RECENT SUBMISSIONS TO THIS USER'S GROUP -->
<yrcwww:member group="any">
	<yrcwww:contentbox title="Recent Submissions" centered="true" width="700">
	 	<logic:notEmpty name="newProjects" scope="request">
		 <p>Below are projects submitted by researchers to your group(s) within the last month.
	 
		 <p>
		 <table border="0" width="100%" class="table_basic">
		 <thead>
		  <tr>
		   <th>&nbsp;</th>
		   <th>ID</th>
		   <th>PI</th>
		   <th>Title</th>
		   <th>Submit Date</th>
		  </tr>
	 	</thead>
	 	<tbody>
		<logic:iterate id="project" name="newProjects" scope="request">
		 <TR>
		  <TD valign="top">
		   <NOBR>
		    <html:link action="viewProject.do" paramId="ID" paramName="project" paramProperty="ID">View</html:link>
		   </NOBR>
		  </TD>
		  <TD valign="top"><bean:write name="project" property="ID"/></TD>
		  <TD valign="top"><bean:write name="project" property="PI.lastName"/></TD>
		  <TD valign="top" class="left_align"><bean:write name="project" property="title"/></TD>
		  <TD valign="top"><bean:write name="project" property="submitDate"/></TD>
		 </TR>
		</logic:iterate>
		</tbody>
		</table>
   		</logic:notEmpty>
   		
   		<logic:empty name="newProjects" scope="request">
   		 <p>There have been no projects submitted to your group in the last month.
   		</logic:empty>
	</yrcwww:contentbox>
</yrcwww:member>





<!-- List the 10 most recently submitted MS -->
<yrcwww:member group="administrators">

	<!-- List the YATES Data here: -->
	<%@ include file="listRecentMS.jsp" %>

</yrcwww:member>


<%@ include file="/includes/footer.jsp" %>