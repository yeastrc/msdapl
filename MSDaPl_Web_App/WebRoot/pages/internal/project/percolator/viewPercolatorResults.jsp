
<%@page import="org.yeastrc.ms.domain.search.SORT_ORDER"%>
<%@ taglib uri="/WEB-INF/yrc-www.tld" prefix="yrcwww" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>

<yrcwww:notauthenticated>
 <logic:forward name="authenticate" />
</yrcwww:notauthenticated>

<logic:empty name="filterForm">
  <logic:forward name="viewPercolatorResults" />
</logic:empty>


<%@ include file="/includes/header.jsp" %>

<%@ include file="/includes/errors.jsp" %>

<%@ include file="/pages/internal/project/resultsTableJS.jsp" %>

<script src="<yrcwww:link path='js/peptideHighlighter.js'/>"></script>


<script>

$(document).ready(function() { 
	$(".peptide").highlightPeptide();
});

// ---------------------------------------------------------------------------------------
// AJAX DEFAULTS
// ---------------------------------------------------------------------------------------
$.ajaxSetup({
	type: 'POST',
	//timeout: 5000,
	dataType: 'html',
	error: function(xhr, textstatus, errorthrown) {
			
				var statusCode = xhr.status;
  		// status code returned if user is not logged in
  		// reloading this page will redirect to the login page
  		if(statusCode == 303)
				window.location.reload();
			
			// otherwise just display an alert
			else {
				alert("Request Failed: "+statusCode+"\n"+xhr.statusText+"\n"+textstatus+"\n"+errorthrown);
			}
	}
});

$.blockUI.defaults.message = '<b>Loading...</b>'; 
$.blockUI.defaults.css.padding = 20;
$.blockUI.defaults.fadeIn = 0;
$.blockUI.defaults.fadeOut = 0;
//$().ajaxStart($.blockUI).ajaxStop($.unblockUI);
$().ajaxStop($.unblockUI);

function viewPsms(searchAnalysisId, peptideResultId) {

	var span = $("span#psm_"+peptideResultId);
	var row = span.parent().parent();
	var msg = "loading results for searchAnalysisID: "+searchAnalysisId+", peptideResultId: "+peptideResultId+", class="+row.attr('class');
	
	if(span.is(".loaded")) {
		if(span.is(".visible")) {
			span.removeClass("visible");
			span.addClass("invisible");
			row.next().hide();
		}
		else {
			span.addClass("visible");
			span.removeClass("invisible");
			row.next().show();
		}
	}
	else {
		span.addClass("loaded");
		span.addClass("visible");
		var newRow = "<tr class='"+row.attr('class')+"'><td colspan='7' style='border:1px solid gray;' id='td_"+peptideResultId+"'></td></tr>";
		row.after(newRow);
		
		
		$.blockUI();
		$("td#td_"+peptideResultId).load("<yrcwww:link path='viewPercolatorPeptidePsms.do'/>", 	//url
					{'percolatorPeptideId': peptideResultId, 		// data
					 'searchAnalysisId': searchAnalysisId
					 },
					 function(responseText, status, xhr) {						// callback
					 	$.unblockUI();
						$(this).show();
						makeSortableTable($("#psmlist_"+peptideResultId));
		 });
  								   
  		/*						   
		$.ajax({
		url:      "<yrcwww:link path='viewPercolatorPeptidePsms.do'/>",
		dataType: "text",
		data:     {'percolatorPeptideId': 	peptideResultId,
		           'searchAnalysisId': searchAnalysisId 
		           },
		success:  function(data) {
			        $("td#td_"+peptideResultId).append(data);
		          }
		});
		*/
	}
}
</script>

<yrcwww:contentbox title="Percolator Results" centered="true" width="95" widthRel="true">
<center>

	<!-- SUMMARY -->
	<div style="padding:0 7 0 7; margin-bottom:5; border: 1px dashed gray;background-color: #F0FFF0;">
		<table>
			<tr>
				<td align="center"><b>Project ID:</b>
					<logic:iterate name="projectIds" id="projId">
						<html:link action="viewProject.do" paramId="ID" paramName="projId"><bean:write name="projId" /></html:link>&nbsp;
					</logic:iterate>
				</td>
				<td align="center"><b>Experiment ID:</b>
					<logic:iterate name="experimentIds" id="exptId">
						<bean:write name="exptId" />&nbsp;
					</logic:iterate>
				</td>
				
				<td align="center"><b>Program: </b><bean:write name="program" /></td>
			</tr>
		</table>
	</div>
	
	
	<!-- FILTER FORM -->
	<%@ include file="percolatorFilterForm.jsp" %>



	<!-- PAGE RESULTS -->
	<bean:define name="results" id="pageable" />
	<%@include file="/pages/internal/pager.jsp" %>
	
	
				
	<!-- RESULTS TABLE -->
	<div style="background-color: #FFFFFF; margin:5 0 5 0; padding:5;" > 
	<yrcwww:table name="results" tableId='perc_results' tableClass="table_basic sortable_table" center="true" />
	</div>
	
	<%@include file="/pages/internal/pager_small.jsp" %>
	
</center>	
</yrcwww:contentbox>

<%@ include file="/includes/footer.jsp" %>