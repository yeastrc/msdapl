
<%@ taglib uri="/WEB-INF/yrc-www.tld" prefix="yrcwww" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>

<!-- <script src="<yrcwww:link path='/js/table2CSV.js'/>"></script> -->

<script>
function toggleGoEnrichmentTable() {
	if($("#go_enrichment_table").is(':visible')) {
		$("#go_enrichment_table").hide();
		$("#go_enrichment_table_link").text("Show All Enriched Terms");
	}
	else {
		$("#go_enrichment_table").show();
		$("#go_enrichment_table_link").text("Hide Table");
	}
}
function toggleEnrichPieChart() {
	if($("#enrich_pie_chart_div").is(':visible')) {
		$("#enrich_pie_chart_div").hide();
		$("#enrich_pie_chart_link").text("Show Pie Chart");
	}
	else {
		$("#enrich_pie_chart_div").show();
		$("#enrich_pie_chart_link").text("Hide");
	}
}
function toggleEnrichBarChart() {
	if($("#enrich_bar_chart_div").is(':visible')) {
		$("#enrich_bar_chart_div").hide();
		$("#enrich_bar_chart_link").text("Show Bar Chart");
	}
	else {
		$("#enrich_bar_chart_div").show();
		$("#enrich_bar_chart_link").text("Hide");
	}
}
</script>

<div style="background-color:#ED9A2E;width:100%; margin:40 0 0 0; padding:3 0 3 0; color:white;" align="left">
<span style="margin-left:10;" 
	  class="foldable fold-open" id="goenrich_fold" onclick="toggleGoEnrichmentDetails();">&nbsp;&nbsp;&nbsp;&nbsp; </span>
<b>GO Enrichment</b>
</div>
	  
<div align="center" style="border:1px dotted gray;" id="goenrich_fold_target">

<logic:present name="goEnrichment">

	<logic:present name="pieChartUrl">
	<div align="center">
		<b>Top 15 enriched <font color="red"><bean:write name="goEnrichment" property="goDomainName" /> </font> terms are displayed</b> 
	</div>
		
	<table width="75%">
	<tr>
	<td>
		<div style="font-weight:bold; font-size:8pt; padding: 1 3 1 3; color:#D74D2D; width:100%; margin-bottom:3px; background: #CBCBCB;">
			<span class="clickable underline" onclick="toggleEnrichPieChart();" id="enrich_pie_chart_link">Hide</span>
		</div>
		<div style="margin-bottom: 10px; padding: 3px; border:1px dashed #BBBBBB; width:100%;" align="center" id="enrich_pie_chart_div">
		<img src="<bean:write name='pieChartUrl'/>" alt="Can't see the Google Pie Chart??"/></img>
		</div>
	</td>
	</tr>
	<tr>
	<td>
		<div style="font-weight:bold; font-size:8pt; padding: 1 3 1 3; color:#D74D2D; width:100%; margin-bottom:3px; background: #CBCBCB;">
			<span class="clickable underline" onclick="toggleEnrichBarChart();" id="enrich_bar_chart_link">Hide</span>
		</div>
		<div style="margin-bottom: 10px; padding: 3px; border:1px dashed #BBBBBB; width:100%;" align="center" id="enrich_bar_chart_div">
		<img src="<bean:write name='barChartUrl'/>" alt="Can't see the Google Bar Chart??"/></img>
		</div>
	</td>
	</tr>
	</table>
	</logic:present>
	
	
	<br/>
	

	<table align="center">
		<tr>
			<td><b># Input Proteins: </b></td><td><bean:write name="goEnrichment" property="numInputProteins" /></td>
		</tr>
		<tr>
			<td><b># Annotated Input Proteins: </b></td><td><bean:write name="goEnrichment" property="numInputAnnotatedProteins" /></td>
		</tr>
		<tr>
			<td><b># Annotated Reference Proteins (
			<a target="ncbi_window" href="http://www.ncbi.nlm.nih.gov/Taxonomy/Browser/wwwtax.cgi?mode=Info&id=<bean:write name="goEnrichment" property="speciesId"/>">
				<bean:write name="goEnrichment" property="speciesName" /></a>)
		   </b></td><td><bean:write name="goEnrichment" property="numAllAnnotatedSpeciesProteins" /></td>
		</tr>
	</table>
	
	<br/>
	<div style="margin: 10 0 10 0; width: 65%">
		<table style="border:1px dotted gray;">
			<tr>
				<td style="font-size:8pt;" valign="top"><b>#Annotated (input):</b></td>
				<td style="font-size:8pt;" valign="top">Number of proteins in the input list annotated with the GO term</td>
			</tr>
			<tr>
				<td style="font-size:8pt;" valign="top"><b>#Annotated (reference):</b></td>
				<td style="font-size:8pt;" valign="top">Number of proteins in the reference set (all proteins of the species) annotated with the GO term</td>
			</tr>
			<tr>
				<td style="font-size:8pt;" valign="top"><b>P-Value:</b></td>
				<td style="font-size:8pt;" valign="top">
				Given the total number of annotated input proteins, 
				the total number of annotated proteins in the reference set, 
				and the number of proteins in the reference set annotated with a given GO term (#Annotated (reference)), 
				the p-value represents the chances of randomly having the number of input proteins annotated with that GO term (#Annotated (input)).
				</td>
			</tr>
		</table>
		
	</div>
	
	<div align="center">
		<span style="font-weight:bold; color:red;"># Enriched Terms (<bean:write name="goEnrichment" property="goDomainName"/>):<bean:write name="goEnrichment" property="enrichedTermCount" /></span>
		
		<div align="center" style="width:75%; font-weight:bold; font-size:8pt; margin-bottom:3px;color:#D74D2D">
		<span class="clickable underline" onclick="toggleGoEnrichmentTable();" id="go_enrichment_table_link">Hide Table</span>
		<!--  &nbsp;<span class="clickable underline" onclick="$('#go_enrichment_table').table2CSV({delivery:'popup'}); return false;">Save Table</span>-->
		</div>
		<table class="table_basic" align="center" id="go_enrichment_table">
			<thead>
				<tr>
				<th class="sort-alpha">GO ID</th>
				<th class="sort-alpha">Name</th>
				<th class="sort-float">P-Value</th>
				<th class="sort-float"><span class="tooltip" title="P-Value after applying multiple test correction">P-Value</span><br/>(Adjusted)</th>
				<th class="sort-int">#Annotated (input)</th>
				<th class="sort-int">#Annotated (reference)</th>
				</tr>
			</thead>
			<tbody>
				<logic:iterate name="goEnrichment" property="enrichedTerms" id="term">
					<tr>
						<td><bean:write name="term" property="goNode.accession"/>
						&nbsp;&nbsp;
						<span style="font-size:8pt;">
						<a target="go_window"
	    				href="http://www.godatabase.org/cgi-bin/amigo/go.cgi?action=query&view=query&search_constraint=terms&query=<bean:write name="term" property="goNode.accession"/>">
        				AmiGO</a>
        				&nbsp;
        				<a target="go_window" href="http://www.yeastrc.org/pdr/viewGONode.do?acc=<bean:write name="term" property="goNode.accession"/>">PDR</a>
        				</span>
        				<span class="go_filter_add" id="<bean:write name='term' property='goNode.accession'/>" title="Add GO term to filters">
        					<img src="<yrcwww:link path='images/filter.png'/>"alt="Filter"/>
        				</span>
        				<span class="go_filter_remove" id="<bean:write name='term' property='goNode.accession'/>" title="Remove" style="display:none;">
        					<img src="<yrcwww:link path='images/filter_yellow.png'/>"alt="Filter"/>
        				</span>
						</td>
						
						<td><span title="<bean:write name="term" property="goNode.definition"/>" class="tooltip"><bean:write name="term" property="goNode.name"/></span></td>
						<logic:equal name="term" property="pvalueString" value="-1.0">
							<td style="color:red;"><bean:write name="term" property="pvalueString"/></td>
							<td style="color:red;"><bean:write name="term" property="numAnnotatedProteins"/></td>
							<td style="color:red;"><bean:write name="term" property="totalAnnotatedProteins"/></td>
						</logic:equal>
						<logic:notEqual name="term" property="pvalueString" value="-1.0">
							<td><bean:write name="term" property="pvalueString"/></td>
							<td><bean:write name="term" property="correctedPvalueString"/></td>
							<td><bean:write name="term" property="numAnnotatedProteins"/></td>
							<td><bean:write name="term" property="totalAnnotatedProteins"/></td>
						</logic:notEqual>
						
					</tr>
				</logic:iterate>
			</tbody>
		</table>
	</div>
	
</logic:present>
</div>

