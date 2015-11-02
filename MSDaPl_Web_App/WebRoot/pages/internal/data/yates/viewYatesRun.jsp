<%@ taglib uri="/WEB-INF/yrc-www.tld" prefix="yrcwww" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>

<yrcwww:notauthenticated>
 <logic:forward name="authenticate" />
</yrcwww:notauthenticated>

<logic:empty name="run">
  <logic:forward name="viewYatesRun" />
</logic:empty>

<jsp:useBean id="params" class="java.util.HashMap"/>

<%@ include file="/includes/header.jsp" %>

<yrcwww:contentbox title="DTASelect Results" centered="true" width="1000" scheme="ms">

 <CENTER>
  <B>Run Information:</B><BR><BR>
 <TABLE CELLPADDING="no" CELLSPACING="0"> 
  <logic:notEmpty name="run" property="baitProtein">
   <yrcwww:colorrow scheme="ms">
    <TD valign="top" width="25%">Bait Protein:</TD>
    <TD valign="top" width="75%">
     <html:link action="viewProtein.do" paramId="id" paramName="run" paramProperty="baitProtein.id">
     <bean:write name="run" property="baitProtein.listing"/></html:link>
    </TD>
   </yrcwww:colorrow>
  </logic:notEmpty>

  <logic:notEmpty name="run" property="baitDesc">
  <yrcwww:colorrow scheme="ms">
   <TD valign="top" width="25%">Bait Desc.:</TD>
   <TD valign="top" width="75%"><bean:write name="run" property="baitDesc"/></TD>
  </yrcwww:colorrow>
  </logic:notEmpty>

  <yrcwww:colorrow scheme="ms">
   <TD valign="top" width="25%">Organism:</TD>
   <TD valign="top" width="75%">
    <a target="ncbi_window" href="http://www.ncbi.nlm.nih.gov/Taxonomy/Browser/wwwtax.cgi?mode=Info&id=<bean:write name="run" property="targetSpecies.id"/>">
    <i><bean:write name="run" property="targetSpecies.name" /></i></a>
   </TD>
  </yrcwww:colorrow>
  <yrcwww:colorrow scheme="ms">
   <TD valign="top" width="25%">Run Date:</TD>
   <TD valign="top" width="75%"><bean:write name="run" property="runDate"/></TD>
  </yrcwww:colorrow>
  <yrcwww:colorrow scheme="ms">
   <TD valign="top" width="25%">Project:</TD>
   <TD valign="top" width="75%">
     <html:link action="viewProject.do" paramId="ID" paramName="run" paramProperty="projectID">
     <bean:write name="run" property="project.title"/></html:link></TD>
  </yrcwww:colorrow>

  <yrcwww:colorrow scheme="ms">
   <TD valign="top" width="25%">DTA SELECT:</TD>
   <TD valign="top" width="75%">
   	
   	<html:link action="viewDTASelectFilter.do" paramId="id" paramName="run" paramProperty="id">
     Download DTASelect Filter text</html:link>
	
	<logic:equal name="run" property="containsDTASelectHTML" value="true">
		   	<br><html:link action="viewDTASelectHTML.do" paramId="id" paramName="run" paramProperty="id">
		     Download DTASelect HTML file</html:link>
	</logic:equal>

	<logic:equal name="run" property="containsDTASelectTXT" value="true">
		   	<br><html:link action="viewDTASelect.do" paramId="id" paramName="run" paramProperty="id">
		     Download Unfiltered DTASelect text file</html:link>
	</logic:equal>
    
   	<!--<br><html:link href="/yrc/viewDTASelect.do" paramId="id" paramName="run" paramProperty="id">
     Download DTA Select text</html:link>-->
   
   </TD>
  </yrcwww:colorrow>

   <yrcwww:colorrow scheme="ms">

   <TD valign="top" width="25%">Comments:
   
   			<logic:empty name="run" property="comments">
   				<br><font style="font-size:8pt;">[<a href="javascript:showEditBox()">Add Comments</a>]<font>
   			</logic:empty>
   			<logic:notEmpty name="run" property="comments">
   				<br><font style="font-size:8pt;">[<a href="javascript:showEditBox()">Edit Comments</a>]<font>
			</logic:notEmpty>   
		
   </TD>
   
   
   <TD valign="top" width="75%">
   		
   		<div id="comments_text">
	   		<logic:empty name="run" property="comments">
	   			None entered.
	   		</logic:empty>
	   		<logic:notEmpty name="run" property="comments">
	   			<bean:write name="run" property="comments"/>
			</logic:notEmpty>
		</div>
		<div id="comments_edit_box" style="display:none;">
			<html:form action="saveMSComments" method="post">
				<input type="hidden" name="id" value="<bean:write name="run" property="id" />">
				<textarea name="comments" rows="5" cols="50"><bean:write name="run" property="comments" /></textarea><br>
				<input type="button" value="Cancel Edit" onClick="javascript:hideEditBox()">
				<input type="submit" value="Save Comments">
			</html:form>
		</div>
   		
   		<script language="JavaScript">
   			var editbox=document.all? document.all["comments_edit_box"] : document.getElementById? document.getElementById("comments_edit_box") : ""
   			var commentstext=document.all? document.all["comments_text"] : document.getElementById? document.getElementById("comments_text") : ""

   			function showEditBox() {
   				commentstext.style.display = "none";
   				editbox.style.display = "inline";
   			}
   			function hideEditBox() {
   			   	editbox.style.display = "none";
   				commentstext.style.display = "inline";
   			}
   		</script>
   		
   </TD>


   </yrcwww:colorrow>

 </TABLE>
 
 <P><TABLE CELLPADDING="no" CELLSPACING="0" WIDTH="95%">

	<logic:equal name="filter" value="yes">
		<yrcwww:colorrow scheme="ms">
			<TD colspan="7" align="center"><B>FILTERED Run Results:</B>
				<%
					params.put( "filter", "no");
					params.put( "id", new Integer (  ((org.yeastrc.yates.YatesRun)(request.getAttribute("run"))).getId() ));
					params.put( "sortby", request.getAttribute("sortby"));
				%>
		   [<html:link action="viewYatesRun.do" name="params">unfilter</html:link>]<BR><BR></TD>
		</yrcwww:colorrow>
	</logic:equal>
	<logic:equal name="filter" value="no">
			<yrcwww:colorrow scheme="ms">
				<TD colspan="7" align="center"><B>UNFILTERED Run Results:</B>
				<%
					params.put( "filter", "yes");
					params.put( "id", new Integer (  ((org.yeastrc.yates.YatesRun)(request.getAttribute("run"))).getId() ));
					params.put( "sortby", request.getAttribute("sortby"));
				%>
		   [<html:link action="viewYatesRun.do" name="params">filter</html:link>]

			   
			   </TD>
			</yrcwww:colorrow>
	</logic:equal>
	
	<yrcwww:colorrow scheme="ms">
		<TD width="16%">&nbsp;</TD>
		<TD width="16%">
			<%
		 		params.put( "filter", request.getAttribute("filter"));
		 		params.put( "id", new Integer (  ((org.yeastrc.yates.YatesRun)(request.getAttribute("run"))).getId() ));
		 		params.put( "sortby", "proteinListing");
		 	%>
		   <html:link action="viewYatesRun.do" name="params"><b>Hit Protein</b></html:link>
		</TD>
		<TD width="35%">
		   <u><b>Protein Desc</b></u>
		</TD>
		<TD width="9%">
			<%
		 		params.put( "filter", request.getAttribute("filter"));
		 		params.put( "id", new Integer (  ((org.yeastrc.yates.YatesRun)(request.getAttribute("run"))).getId() ));
		 		params.put( "sortby", "sequenceCount");
		 	%>
		   <html:link action="viewYatesRun.do" name="params"><b>Sequence Count</b></html:link>
		</TD>
		<TD width="9%">
			<%
		 		params.put( "filter", request.getAttribute("filter"));
		 		params.put( "id", new Integer (  ((org.yeastrc.yates.YatesRun)(request.getAttribute("run"))).getId() ));
		 		params.put( "sortby", "spectrumCount");
		 	%>
		   <html:link action="viewYatesRun.do" name="params"><b>Spectrum Count</b></html:link>
		</TD>
		<TD width="8%">
			<%
		 		params.put( "filter", request.getAttribute("filter"));
		 		params.put( "id", new Integer (  ((org.yeastrc.yates.YatesRun)(request.getAttribute("run"))).getId() ));
		 		params.put( "sortby", "sequenceCoverage");
		 	%>
		   <html:link action="viewYatesRun.do" name="params"><b>Sequence Coverage</b></html:link>
		</TD>
		<TD width="7%">
			<%
		 		params.put( "filter", request.getAttribute("filter"));
		 		params.put( "id", new Integer (  ((org.yeastrc.yates.YatesRun)(request.getAttribute("run"))).getId() ));
		 		params.put( "sortby", "molecularWeight");
		 	%>
		   <html:link action="viewYatesRun.do" name="params"><b>Mol. Wt.</b></html:link>
		</TD>

	</yrcwww:colorrow>

		<logic:iterate id="result" name="yatesResults">
			<yrcwww:colorrow scheme="ms">
				<TD width="16%" valign="top">[<html:link action="viewYatesResult.do" paramId="id" paramName="result" paramProperty="id">
				 <nobr>View Peptides</nobr></html:link>]&nbsp;&nbsp;</TD>
				<TD width="16%" valign="top">
				 <html:link action="viewProtein.do" paramId="id" paramName="result" paramProperty="hitProtein.id">				 
				  <bean:write name="result" property="hitProtein.listing"/></html:link>
				</TD>
				<TD width="35%" valign="top"><font style="font-size:8pt;"><bean:write name="result" property="hitProtein.description"/></font></TD>
				<TD width="9%" valign="top"><bean:write name="result" property="sequenceCount"/></TD>
				<TD width="9%" valign="top"><bean:write name="result" property="spectrumCount"/></TD>
				<TD width="8%" valign="top"><bean:write name="result" property="sequenceCoverage"/>%</TD>
				<TD width="7%" valign="top"><bean:write name="result" property="molecularWeight"/></TD>
			</yrcwww:colorrow>
		</logic:iterate>
	
 </TABLE>

 </CENTER>

</yrcwww:contentbox>

<%@ include file="/includes/footer.jsp" %>