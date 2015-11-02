<%@ page language="java" pageEncoding="ISO-8859-1"%>
<%@page import="java.net.URL"%>

<%@ taglib uri="/WEB-INF/yrc-www.tld" prefix="yrcwww" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
  <head>
    <title>SpectrumViewer</title>
	<link REL="stylesheet" TYPE="text/css" HREF="<yrcwww:link path='css/global.css'/>">
	<link REL="stylesheet" TYPE="text/css" HREF="<yrcwww:link path='css/lorikeet.css'/>">
	
  </head>
  
  <body>
 
 <!--  
 <script src="<yrcwww:link path='js/jquery-1.4.2.js'/>"></script>
<script src="<yrcwww:link path='js/ui.core.js'/>"></script>
<script src="<yrcwww:link path='js/ui.draggable.js'/>"></script>
<script src="<yrcwww:link path='js/ui.droppable.js'/>"></script>

-->
<script type="text/javascript" src="http://ajax.googleapis.com/ajax/libs/jquery/1.4.2/jquery.min.js"></script>
<script type="text/javascript" src="http://ajax.googleapis.com/ajax/libs/jqueryui/1.8.4/jquery-ui.min.js"></script>

<script>
// ---------------------------------------------------------------------------------------
// SETUP THE TABLE
// ---------------------------------------------------------------------------------------
$(document).ready(function() {

   $(".other_results").each(function() {
   		var $table = $(this);
   		$table.attr('width', "100%");
   		$table.attr('align', 'center');
   		$('th', $table).attr("align", "left");
   		
   		// #ED9A2E #D74D2D
   		$('th', $table).each(function() {
   			if($(this).is('.sorted-asc') || $(this).is('.sorted-desc')) {
   				$(this).addClass('th_selected');
   			}
   			else
   			$(this).addClass('th_normal');
   		});
   		
   		$("tbody > tr:even", $table).each(function() {
   			if(!($(this).is('.tr_highlight'))) {
   				$(this).addClass('project_A');
   			}
   		});
   		//$('tbody > tr:odd', $table).css("background-color", "F0FFF0");
   });
});
</script>


<%@ include file="/includes/errors.jsp" %>

<div style="margin:10;">

<!--<yrcwww:contentbox centered="true" title="Peptide Spectrum" width="95" widthRel="true" scheme="content">-->
<center>
<div style="padding:5px;background:#FFFFFF;">
<div class="content_header" style="width:100%;">Spectrum Viewer</div>
</div>
<!--==================================================================== -->
<!-- APPLET -->
<!--==================================================================== -->
<logic:present  name="params">
<table border="0">
 <tr>
  <td><b>Sequence:</b></td>
  <td><b><bean:write name="peptideSeq" filter="false"/></b></td>
 </tr>
</table>

<table border=0 ALIGN="CENTER" width="100%">
 <TR><TD><B>Mass: <bean:write  name="firstMass"/></B></TD>
  <TD colspan="2"><B>File: <bean:write  name="filename"/></B></TD>
  <TD colspan="2"><B>Scan number: <bean:write  name="scanNumber"/></B></TD>
  <TD><B>Charge: <bean:write  name="firstCharge"/></B></TD>
  <TD colspan="2"><B>Database: <bean:write  name="database"/></TD>
 </TR>

 <TR>
  <TD colspan="8" ALIGN="center">
  <% String baseUrl = new URL(request.getScheme(), request.getServerName(), request.getServerPort(), request.getContextPath()).toString(); %>
   <applet 
   		code="ed.SpectrumViewerApp.SpectrumApplet.class"
   		archive="SpectrumApplet.jar" 
   		CODEBASE="<%=baseUrl %>/applets" 
   		width=100% 
   		height=500>
    <logic:iterate name="params" id="param" scope="request">
     <bean:write name="param" filter="false" />
    </logic:iterate>
   </applet>
  </TD>
 </TR>
</table>
</logic:present>

<!--==================================================================== -->
<!-- LORIKEET SPECTRUM VIEWER -->
<!--==================================================================== -->
<logic:present name="jsonParams">

<!--[if IE]><script language="javascript" type="text/javascript" src="<yrcwww:link path='js/excanvas.min.js'/>"></script><![endif]-->
<script src="<yrcwww:link path='js/jquery.flot.js'/>"></script>
<script src="<yrcwww:link path='js/jquery.flot.selection.js'/>"></script>

<script src="<yrcwww:link path='js/specview.js'/>"></script>
<script src="<yrcwww:link path='js/peptide.js'/>"></script>
<script src="<yrcwww:link path='js/aminoacid.js'/>"></script>
<script src="<yrcwww:link path='js/ion.js'/>"></script>

<script type="text/javascript">
$(document).ready(function () {

	/* render the spectrum with the given options */
	var params = <bean:write name="jsonParams" filter="false"/>;
	<logic:present name="ms1ScanId">
		params.precursorPeakClickFn = precursorPeakClicked;
	</logic:present>
	$("#lorikeet").specview(params);	

});

<logic:present name="ms1ScanId">

function precursorPeakClicked(precursorMz) {
	var ms1scan = <bean:write name="ms1ScanId" filter="false"/>;
	
	<logic:present name="runSearchId">
		var runSearchId = <bean:write name="runSearchId" filter="false"/>;
		var url = "viewSpectrum.do?ms1scanID="+ms1scan+"&runSearchID="+runSearchId+"&precursorMz="+precursorMz;
	</logic:present>
	
	<logic:present name="runSearchAnalysisId">
		var runSearchAnalysisId = <bean:write name="runSearchAnalysisId" filter="false"/>;
		var url = "viewSpectrum.do?ms1scanID="+ms1scan+"&runSearchAnalysisID="+runSearchAnalysisId+"&precursorMz="+precursorMz;
	</logic:present>
	
	window.location = url;
	// alert(url);
}

</logic:present>

</script>

<!-- PLACE HOLDER DIV FOR THE SPECTRUM -->
<div id="lorikeet"></div>

</logic:present>


<!-- OTHER RESULTS FOR THIS SCAN -->
	<div style="background-color: #FFFFFF; padding:10px;" > 
	<logic:present name="results">
		<yrcwww:table name="results" tableId='other_results' tableClass=" table_basic other_results" center="true" />
	</logic:present>
	<logic:notPresent name="results">
		No other results for for this scan.
	</logic:notPresent>
	</div>

</center>
<!--</yrcwww:contentbox>-->

</div>
</body>
</html>    
