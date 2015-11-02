
<%@page import="org.yeastrc.bio.go.slim.GOSlimTermResult"%>
<%@page import="org.yeastrc.bio.go.slim.GOSlimAnalysis"%>
<%@page import="org.yeastrc.www.util.RoundingUtils"%>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/yrc-www.tld" prefix="yrcwww" %>

<script>
function toggleSpeciesTable() {
	if($("#go_slim_species_table").is(":visible")) {
		$("#go_slim_species_table").hide();
	}
	else {
		$("#go_slim_species_table").show();
	}
}
function toggleGoSlimTable() {
	if($("#go_slim_table_div").is(':visible')) {
		$("#go_slim_table_div").hide();
		$("#go_slim_table_link").text("Show All GO Slim Terms");
	}
	else {
		$("#go_slim_table_div").show();
		$("#go_slim_table_link").text("Hide Table");
	}
}
function toggleSlimPieChart() {
	if($("#slim_pie_chart_div").is(':visible')) {
		$("#slim_pie_chart_div").hide();
		$("#slim_pie_chart_link").text("Show Pie Chart");
	}
	else {
		$("#slim_pie_chart_div").show();
		$("#slim_pie_chart_link").text("Hide");
	}
}
function toggleSlimBarChart() {
	if($("#slim_bar_chart_div").is(':visible')) {
		$("#slim_bar_chart_div").hide();
		$("#slim_bar_chart_link").text("Show Bar Chart");
	}
	else {
		$("#slim_bar_chart_div").show();
		$("#slim_bar_chart_link").text("Hide");
	}
}
</script>

<div style="background-color:#ED9A2E;width:100%; margin:40 0 0 0; padding:3 0 3 0; color:white;" align="left">
<span style="margin-left:10;" 
	  class="foldable fold-open" id="goslim_fold" onclick="toggleGoSlimDetails();">&nbsp;&nbsp;&nbsp;&nbsp; </span>
<b>GO Slim Analysis</b>
</div>
	  
<div align="center" style="border:1px dotted gray;" id="goslim_fold_target">
	
	<div style="color:red; font-weight:bold;" align="center">
		# Proteins: <bean:write name="goAnalysis" property="totalProteinCount"/> &nbsp; &nbsp; 
		# Not annotated: <bean:write name="goAnalysis" property="numProteinsNotAnnotated"/>
		&nbsp;&nbsp; <span class="underline clickable" onClick="toggleSpeciesTable()" style="color:#666666;">(Details)</span>
	</div>
	
	
	<div align="center">
		<table style="border: 1px dotted gray; display:none; margin-bottom:10px;" id="go_slim_species_table">
			<tr>
				<td style="border: 1px dotted gray;"><b>Species</b></td>
				<td style="border: 1px dotted gray;"><b>Total</b></td>
				<td style="border: 1px dotted gray;"><b>Annotated</b></td>
				<td style="border: 1px dotted gray;"><b>Not Annotated</b></td>
			</tr>
			<logic:iterate name="goAnalysis" property="speciesProteinCount" id="speciesCount">
				<tr>
					<td style="border: 1px dotted gray;"><bean:write name="speciesCount" property="speciesName"/></td>
					<td style="border: 1px dotted gray;"><bean:write name="speciesCount" property="count"/></td>
					<td style="border: 1px dotted gray;"><bean:write name="speciesCount" property="annotated"/></td>
					<td style="border: 1px dotted gray;"><bean:write name="speciesCount" property="notAnnotated"/></td>
				</tr>
			</logic:iterate>
		</table>
	</div>
	
	<logic:present name="pieChartUrlGroup">
	<div align="center">
		<b><bean:write name="goAnalysis" property="goSlimName" /> has <bean:write name="goAnalysis" property="slimTermCount"/> 
		terms for <font color="red"><bean:write name="goAnalysis" property="goAspectString"/></font>. 
		Top 15 terms 
		<logic:equal name="goAnalysis" property="termsIncludeAspectRoot" value="true">
			(excluding <bean:write name="goAnalysis" property="aspectRootName"/>)
		</logic:equal>
		are displayed.</b>
		<br/>
		<a href="" onclick="javascript:viewGoSlimGraph();return false;" style="font-size:11pt;"><b>[View Tree]</b></a>
		&nbsp; &nbsp; &nbsp;
		<a href="http://www.geneontology.org/GO.slims.shtml" target="go_window" style="font-size:10pt;"><b>[More information on GO Slims]</b></a>
	</div>
	
	<table width="75%">
	
	<!--  ====================================================== -->
	<!-- Pie chart at the protein group level -->
	<!--  ====================================================== -->
	<!-- 
	<logic:present name="pieChartUrlGroup">
	<tr>
	<td>
		<div style="font-weight:bold; font-size:8pt; padding: 1 3 1 3; color:#D74D2D; width:100%; margin-bottom:3px; background: #CBCBCB;">
			<span class="clickable underline" onclick="toggleSlimPieChart();" id="slim_pie_chart_link">Hide</span>
		</div>
		<div style="margin-bottom: 10px; padding: 3px; border:1px dashed #BBBBBB; width:100%;" align="center" id="slim_pie_chart_div">
		<img src="<bean:write name='pieChartUrlGroup'/>" alt="Can't see the Google Pie Chart??"/></img>
		</div>
	</td>
	</tr>
	</logic:present>
	-->
	<!--  ====================================================== -->
	<!-- Pie chart at the protein level -->
	<!--  ====================================================== -->
	<logic:present name="pieChartUrlProtein">
	<tr>
	<td>
		<div style="font-weight:bold; font-size:8pt; padding: 1 3 1 3; color:#D74D2D; width:100%; margin-bottom:3px; background: #CBCBCB;">
			<span class="clickable underline" onclick="toggleSlimPieChart();" id="slim_pie_chart_link">Hide</span>
		</div>
		<div style="margin-bottom: 10px; padding: 3px; border:1px dashed #BBBBBB; width:100%;" align="center" id="slim_pie_chart_div">
		<img src="<bean:write name='pieChartUrlProtein'/>" alt="Can't see the Google Pie Chart??"/></img>
		</div>
	</td>
	</tr>
	</logic:present>
	
	
	<!--  ====================================================== -->
	<!-- Bar chart at the protein group level -->
	<!--  ====================================================== -->
	<!--
	<logic:present name="barChartUrlGroup">
	<tr>
	<td>
		<div style="font-weight:bold; font-size:8pt; padding: 1 3 1 3; color:#D74D2D; width:100%; margin-bottom:3px; background: #CBCBCB;">
			<span class="clickable underline" onclick="toggleSlimBarChart();" id="slim_bar_chart_link">Hide</span>
		</div>
		<div style="margin-bottom: 10px; padding: 3px; border:1px dashed #BBBBBB; width:100%;" align="center" id="slim_bar_chart_div">
		<img src="<bean:write name='barChartUrlGroup'/>" alt="Can't see the Google Bar Chart??"/></img>
		</div>
	</td>
	</tr>
	</logic:present>
	-->
	
	<!--  ====================================================== -->
	<!-- Bar chart at the protein level -->
	<!--  ====================================================== -->
	<logic:present name="barChartUrlProtein">
	<tr>
	<td>
		<div style="font-weight:bold; font-size:8pt; padding: 1 3 1 3; color:#D74D2D; width:100%; margin-bottom:3px; background: #CBCBCB;">
			<span class="clickable underline" onclick="toggleSlimBarChart();" id="slim_bar_chart_link">Hide</span>
		</div>
		<div style="margin-bottom: 10px; padding: 3px; border:1px dashed #BBBBBB; width:100%;" align="center" id="slim_bar_chart_div">
		<img src="<bean:write name='barChartUrlProtein'/>" alt="Can't see the Google Bar Chart??"/></img>
		</div>
	</td>
	</tr>
	</logic:present>
	
	
	</table>
	</logic:present>
	
	
	<br/>
	<div align="center" style="width:75%; font-weight:bold; font-size:8pt; margin-bottom:3px;color:#D74D2D">
		<span class="clickable underline" onclick="toggleGoSlimTable();" id="go_slim_table_link">Hide Table</span>
	</div>
	<div id="go_slim_table_div">
	<table  style="border:1px dotted gray;margin-bottom:5px;">
		<tr>
			<td style="font-size:8pt;" valign="top"><b># Proteins(exact):</b></td>
			<td style="font-size:8pt;" valign="top">Number of proteins in the input list annotated with the GO term</td>
		</tr>
		<tr>
			<td style="font-size:8pt;" valign="top"><b># Proteins:</b></td>
			<td style="font-size:8pt;" valign="top">Number of proteins in the input list annotated with the GO term or any of its descendants</td>
		</tr>
	</table>
	
	<bean:define name="goAnalysis" property="totalProteinCount" id="totalProteinCount" type="java.lang.Integer"/>
	<bean:define name="goAnalysis" property="totalProteinGroupCount" id="totalProteinGroupCount" type="java.lang.Integer"/>
	
	<table class="table_basic" id="go_slim_table" width="95%">
	<thead>
	<tr>
	<th class="sort-alpha clickable">GO ID</th>
	<th class="sort-alpha clickable">Name</th>
	<th class="sort-int clickable"># Proteins (exact)</th>
	<th class="sort-int clickable"># Proteins</th>
	<th class="sort-float clickable">%</th>
	<!-- 
	<th class="sort-int clickable"># Protein Groups</th>
	<th class="sort-float clickable">%</th>
	-->
	</tr>
	</thead>
	<tbody>
	<logic:iterate name="goAnalysis" property="termNodes" id="node">
	<tr>
		<td>
		<bean:write name="node" property="accession"/>
		&nbsp;&nbsp;
		<span style="font-size:8pt;">
		<nobr>
		<a target="go_window"
	    href="http://www.godatabase.org/cgi-bin/amigo/go.cgi?action=query&view=query&search_constraint=terms&query=<bean:write name="node" property="accession"/>">
        AmiGO</a>
        &nbsp;
        <a target="go_window" href="http://www.yeastrc.org/pdr/viewGONode.do?acc=<bean:write name="node" property="accession"/>">PDR</a>
        &nbsp;
        <span class="go_filter_add" id="<bean:write name='node' property='accession'/>" title="Add GO term to filters">
        	<img src="<yrcwww:link path='images/filter.png'/>"alt="Filter"/>
        </span>
        <span class="go_filter_remove" id="<bean:write name='node' property='accession'/>" title="Remove" style="display:none;">
        	<img src="<yrcwww:link path='images/filter_yellow.png'/>"alt="Filter"/>
        </span>
        </nobr>
        </span>
        </td>
		<td><span title="<bean:write name="node" property="goNode.definition"/>" class="tooltip"><bean:write name="node" property="name"/></span></td>
		<td><bean:write name="node" property="exactAnnotatedProteinCount"/></td>
		<td><bean:write name="node" property="annotatedProteinCount"/></td>
		<td>
			<%=RoundingUtils.getInstance().roundOne((((GOSlimTermResult)node).getAnnotatedProteinCount() * 100.0) / (double)totalProteinCount) %>
		</td>
		
		<!--
		<td><bean:write name="node" property="annotatedGroupCount"/></td>
		<td>
			<%=RoundingUtils.getInstance().roundOne((((GOSlimTermResult)node).getAnnotatedGroupCount() * 100.0) / (double)totalProteinGroupCount) %>
		</td>
		-->
		
	</tr>
	
	</logic:iterate>
	</tbody>
	</table>
	</div>
</div>
