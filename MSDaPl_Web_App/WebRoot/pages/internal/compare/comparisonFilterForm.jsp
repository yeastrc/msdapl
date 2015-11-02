
<%@page import="org.yeastrc.bio.go.GOUtils"%>
<%@page import="org.yeastrc.www.compare.DisplayColumns"%>
<%@page import="org.yeastrc.www.compare.dataset.DatasetColor"%>
<%@page import="org.yeastrc.www.compare.clustering.ClusteringConstants"%>
<%@page import="org.yeastrc.www.compare.ComparisonCommand"%>
<%@ taglib uri="/WEB-INF/yrc-www.tld" prefix="yrcwww" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>

<script type="text/javascript" src="<yrcwww:link path='/js/jquery.cookie.js'/>"></script>
<!-- http://www.isocra.com/2008/02/table-drag-and-drop-jquery-plugin/ -->
<script type="text/javascript" src="<yrcwww:link path='/js/jquery.tablednd_0_5.js'/>"></script>
<script type="text/javascript" src="<yrcwww:link path='/js/jquery.disable.text.select.pack.js'/>"></script>

<script type="text/javascript">
// Popup window code
function newPopup(url) {
	popupWindow = window.open(
		url,'popUpWindow','height=600,width=600,left=10,top=10,resizable=yes,scrollbars=yes,toolbar=no,menubar=no,location=yes,directories=no,status=yes')
}
function toggleColumnChooser() {
	var text = $("#columnChooser").text();
	//alert(text);
	if(text == "Choose Columns") {
		$("#columnChooser").text("Hide Column Chooser");
		$("#columnChooserTgt").show();
	}
	else {
		$("#columnChooser").text("Choose Columns");
		$("#columnChooserTgt").hide();
	}
}

function toggleOrderChanger() {

	var text = $("#orderChanger").text();
	//alert(text);
	if(text == "Order Datasets") {
		$("#orderChanger").text("Hide Dataset Order");
		$("#orderChangerTgt").show();
	}
	else {
		$("#orderChanger").text("Order Datasets");
		$("#orderChangerTgt").hide();
	}
}

function saveDisplayColumnsCookie() {
	//alert("saving cookie");
	var cookieVal = "";
	$(".colChooser").each(function() {
		if($(this).is(":checked")) {}
		else {cookieVal += "_"+$(this).attr('title')};
	});
	
	if(cookieVal.length > 0) {
		cookieVal = cookieVal.substring(1);
		//alert(cookieVal);
		var COOKIE_NAME = 'noDispCols_compare';
		var options = { path: '/', expires: 100 };
    	$.cookie(COOKIE_NAME, cookieVal, options);
    }
	
	return false;
}
$(document).ready(function() {
	
	// reset the form.  When clicking the reload button the form is 
	// not resest, so we reset it manually. 
 	$("form")[0].reset();
 	
 	// If the browser supports text rotation apply the rotation
 	rotateText();
 	
 	
 	$("#datasetOrderTbl").tableDnD({
	    onDragClass: "myDragClass",
	    onDrop: function(table, row) {
            var rows = table.tBodies[0].rows;
            //var debugStr = "Row dropped was "+row.id+". New order: ";
            for (var i=0; i<rows.length; i++) {
            	var rowid = rows[i].id;
                //debugStr += rowid+" (new idx: "+i+")";
                
                $("#AND_index_"+rowid).val(i);
				$("#OR_index_"+rowid).val(i);
				$("#NOT_index_"+rowid).val(i);
				$("#XOR_index_"+rowid).val(i);
            }
            //alert(debugStr);
	    },
		dragHandle: "draggable"
	});
 	
 	makeSortable($('#datasetOrderTbl'));
 	
	$('#resetDatasetOrder').click(function(){
	
		var newOrder = new Array();
		var trIdOrder = new Array();
		// get the current rows in the table;
		$("#datasetOrderTbl >tbody >tr").each(function() {
			var originalIdx = $(this).attr('id');
			newOrder[originalIdx] = $(this);
			trIdOrder[originalIdx] = originalIdx;
		});
		redoDatasetOrder(newOrder, trIdOrder);
		return false;
	});
});

function rotateText() {

	// If the browser supports text rotation apply the rotation
	var b = document.body || document.documentElement;
	var s = b.style;
	// No css support detected
	if(typeof s == 'undefined') { return; }
		
	// Tests if the required CSS property is supported
	var supported = false;
	if(typeof s['MozTransform'] == 'string') { supported = true; }	 // Mozilla support
	else if(typeof s['WebkitTransform'] == 'string') { supported = true; } // Safari, Chrome support
	else if(typeof s['OTransform'] == 'string') { supported = true; } // Opera support
		
	if(supported == true) {
		$(".rotated_text").each(function() {
		
			// element
	    	var $elem = $(this);
	    	// parent
	    	var parent = $elem.parent().get(0);
	    
	    	var o_width = $elem.outerWidth(true); 	// width before rotation; this will be the height after rotation
	    	var n_width = $elem.outerHeight(true);	// this should be the width after rotation
	    
	    	$(parent).css("height", "74px");  // resize the parent to fit the rotated element
	    
		    $elem.css("display", "block");
		    $elem.css("position", "relative");
			$elem.css("width", n_width+"px");			// manually change the width so that the parent (th or td) resizes.
													// Don't know what the behavior would be if the parent is not a th or td
			$elem.css("bottom", "-40px");
			$elem.css("-o-transform", "rotate(-90deg)");  		/* Opera 10.5 */
    		$elem.css("-o-transform-origin", "0% 0%"); 
    		$elem.css("-moz-transform", "rotate(-90deg)"); 		/* FF3.5+ */
    		$elem.css("-moz-transform-origin", "0% 0%"); 
    		$elem.css("-webkit-transform", "rotate(-90deg)"); 	/* Saf3.1+, Chrome */
    		$elem.css("-webkit-transform-origin", "0% 0%"); 
		});
	}
}

function makeSortable(table) {
  	
  	
	var $table = $(table);
	$('th', $table).each(function(column) {
  		
  		if ($(this).is('.sort-alpha') || $(this).is('.sort-int')) {
  			
  			var $header = $(this);
      		$(this).click(function() {
      		
				// sorting direction
				var newDirection = 1;
        		if ($(this).is('.sorted-asc')) {
          			newDirection = -1;
        		}
        				
        		var rows = $table.find('tbody > tr').get();
        				
        		if ($header.is('.sort-alpha')) {
        			$.each(rows, function(index, row) {
						row.sortKey = $(row).children('td').eq(column).text().toUpperCase();
					});
				}
				
				if ($header.is('.sort-int')) {
        					$.each(rows, function(index, row) {
								var key = parseInt($(row).children('td').eq(column).text());
						row.sortKey = isNaN(key) ? 0 : key;
					});
				}
				
     			rows.sort(function(a, b) {
       				if (a.sortKey < b.sortKey) return -newDirection;
					if (a.sortKey > b.sortKey) return newDirection;
					return 0;
     			});

     			$.each(rows, function(index, row) {
       				$table.children('tbody').append(row);
       				row.sortKey = null;
     			});
     			
     			// the header for the column used for sorting is highlighted
				$('th', $table).each(function(){
					$(this).removeClass('sorted-desc');
	    			$(this).removeClass('sorted-asc');
				});
				
     			var $sortHead = $table.find('th').filter(':nth-child(' + (column + 1) + ')');

	          	if (newDirection == 1) {$sortHead.addClass('sorted-asc'); $sortHead.removeClass('sorted-desc');} 
	          	else {$sortHead.addClass('sorted-desc'); $sortHead.removeClass('sorted-asc');}
        
        		$.each(rows, function(index, row) {
        			var id = $(row).attr('id');
       				$("#AND_index_"+id).val(index);
					$("#OR_index_"+id).val(index);
					$("#NOT_index_"+id).val(index);
					$("#XOR_index_"+id).val(index);
     			});
     			
     			// make rows draggable and droppable again
				$("#datasetOrderTbl").tableDnDUpdate();
        		
      		});
	}
  });
}

function redoDatasetOrder(newOrder, trIdOrder) {

	// remove all rows from the table
	$("#datasetOrderTbl >tbody >tr").remove();
	// add rows in the original order
	for(var i = 0; i < newOrder.length; i = i+1) {
		$('#datasetOrderTbl > tbody:last').append('<tr id="'+trIdOrder[i]+'">'+newOrder[i].html()+'</tr>');
		$("#AND_index_"+trIdOrder[i]).val(i);
		$("#OR_index_"+trIdOrder[i]).val(i);
		$("#NOT_index_"+trIdOrder[i]).val(i);
		$("#XOR_index_"+trIdOrder[i]).val(i);
	}
	
	// make rows draggable and droppable again
	$("#datasetOrderTbl").tableDnDUpdate();
}
 	

function showActionOptions() {
	var action = $("#actionOptions").val();
	if(action == <%=ComparisonCommand.CLUSTER.getId()%>) {
		foldOpen($("#comparison_culster_opts_fold"));
	}
	if(action != <%=ComparisonCommand.CLUSTER.getId()%>) {
		foldClose($("#comparison_culster_opts_fold"));
	}
	
	if(action == <%=ComparisonCommand.GO_SLIM.getId()%> ||
	   action == <%=ComparisonCommand.GO_ENRICH.getId()%>) {
		foldOpen($("#comparison_go_opts_fold"));
	}
	if(action != <%=ComparisonCommand.GO_SLIM.getId()%> &&
	   action != <%=ComparisonCommand.GO_ENRICH.getId()%>) {
		foldClose($("#comparison_go_opts_fold"));
	}
}
</script>


<html:form action="updateProteinSetComparison" method="POST">

	<!-- Does the user want to download the results -->
	<html:hidden name="proteinSetComparisonForm" property="download" value="false" styleId="download" />
	
	<!-- Does the user want to cluster results -->
	<html:hidden name="proteinSetComparisonForm" property="clusteringToken" />
	<html:hidden name="proteinSetComparisonForm" property="newToken" />
	
	<!-- Sorting criteria for the results -->
	<html:hidden name="proteinSetComparisonForm" property="sortByString"  styleId="sortBy" />
	<html:hidden name="proteinSetComparisonForm" property="sortOrderString"  styleId="sortOrder" />
	
	
	<html:hidden name="proteinSetComparisonForm" property="numPerPage" styleId="numPerPage" />
	<html:hidden name="proteinSetComparisonForm" property="pageNum" styleId="pageNum" />
	<html:hidden name="proteinSetComparisonForm" property="rowIndex" styleId="rowIndex" />
	
<center>
<br>

<!--  ====================================================================================== -->
<!-- FILTERING OPTIONS -->
<!--  ====================================================================================== -->
<div style="background-color:#F2F2F2;width:80%; margin:0 0 0 0; padding:1 0 1 0; color:black; border: 1px solid gray; font-size:8pt;" align="left">
<span style="margin-left:5;" 
	  class="foldable fold-close" id="comparison_filters_fold">&nbsp;&nbsp;&nbsp;&nbsp; </span>
<b>Filtering Options</b>
</div>
<div style="background-color:#F0F8FF; padding: 5 0 5 0; border: 1px solid gray; border-top:0;width:80%; display:none;" id="comparison_filters_fold_target">
<table align="center">
	<tr>
		<td valign="middle" style="padding: 0 0 10 5;">Filter: </td>
		<td style="padding-bottom:10px;"  align="left" colspan="3">
		<table>
		<tr>
		<td valign="top"><b>AND</b></td>
		<td style="padding-right:10px">
			<table cellpadding="0" cellspacing="0">
			<tr>
				<logic:iterate name="proteinSetComparisonForm" property="andList" id="andDataset" indexId="dsIndex">
					
					<bean:define name="andDataset" property="datasetIndex" id="datasetIndex"/>
					<logic:equal name="andDataset" property="selected" value="true">
						<td style="background-color:rgb(<bean:write name='andDataset' property='red'/>,<bean:write name='andDataset' property='green'/>,<bean:write name='andDataset' property='blue'/>); border:1px solid #AAAAAA;"
							id="AND_<bean:write name='datasetIndex'/>_td"
						>
					</logic:equal>
					<logic:notEqual name="andDataset" property="selected" value="true">
						<td style="background-color:#FFFFFF; border:1px solid #AAAAAA;" id="AND_<bean:write name='datasetIndex'/>_td" >
					</logic:notEqual>
					<span style="cursor:pointer;" onclick="javascript:toggleAndSelect(<bean:write name='datasetIndex'/>, <bean:write name='andDataset' property='red'/>,<bean:write name='andDataset' property='green'/>,<bean:write name='andDataset' property='blue'/>);">&nbsp;&nbsp;</span>
					<html:hidden name="andDataset" property="datasetId" indexed="true" />
					<html:hidden name="andDataset" property="datasetIndex" indexed="true" 
								styleId='<%= "AND_index_"+datasetIndex%>'/>
					<html:hidden name="andDataset" property="sourceString" indexed="true" />
					<html:hidden name="andDataset" property="datasetComments" indexed="true" />
					<html:hidden name="andDataset" property="datasetName" indexed="true" />
					<html:hidden name="andDataset" property="selected" indexed="true" 
				             styleId='<%= "AND_"+datasetIndex+"_select"%>' />
				</td>
				</logic:iterate>
			</tr>
			</table>
		</td>
		<td valign="top"><b>OR</b></td>
		<td style="padding-right:10px">
			<table  cellpadding="0" cellspacing="0">
			<tr>
				<logic:iterate name="proteinSetComparisonForm" property="orList" id="orDataset" indexId="dsIndex">
					
					<bean:define name="orDataset" property="datasetIndex" id="datasetIndex"/>
					<logic:equal name="orDataset" property="selected" value="true">
						<td style="background-color:rgb(<bean:write name='orDataset' property='red'/>,<bean:write name='orDataset' property='green'/>,<bean:write name='orDataset' property='blue'/>); border:1px solid #AAAAAA;"
							id="OR_<bean:write name='datasetIndex'/>_td"
						>
					</logic:equal>
					<logic:notEqual name="orDataset" property="selected" value="true">
						<td style="background-color:#FFFFFF; border:1px solid #AAAAAA;" id="OR_<bean:write name='datasetIndex'/>_td">
					</logic:notEqual>
					<span style="cursor:pointer;" onclick="javascript:toggleOrSelect(<bean:write name='datasetIndex'/>,<bean:write name='orDataset' property='red'/>,<bean:write name='orDataset' property='green'/>,<bean:write name='orDataset' property='blue'/>);">&nbsp;&nbsp;</span>
					<html:hidden name="orDataset" property="datasetId" indexed="true" />
					<html:hidden name="orDataset" property="datasetIndex" indexed="true"
								 styleId='<%= "OR_index_"+datasetIndex%>'/>
					<html:hidden name="orDataset" property="sourceString" indexed="true" />
					<html:hidden name="orDataset" property="datasetComments" indexed="true" />
					<html:hidden name="orDataset" property="datasetName" indexed="true" />
					<html:hidden name="orDataset" property="selected" indexed="true" 
								styleId='<%= "OR_"+datasetIndex+"_select"%>' />
				</td>

				</logic:iterate>
			</tr>
			</table>
		</td>
		<td valign="top"><b>NOT</b></td>
		<td style="padding-right:10px">
			<table  cellpadding="0" cellspacing="0">
			<tr>
				<logic:iterate name="proteinSetComparisonForm" property="notList" id="notDataset" indexId="dsIndex">
					
					<bean:define name="notDataset" property="datasetIndex" id="datasetIndex"/>
					<logic:equal name="notDataset" property="selected" value="true">
						<td style="background-color:rgb(<bean:write name='notDataset' property='red'/>,<bean:write name='notDataset' property='green'/>,<bean:write name='notDataset' property='blue'/>); border:1px solid #AAAAAA;"
							id="NOT_<bean:write name='datasetIndex'/>_td"
						>
					</logic:equal>
					<logic:notEqual name="notDataset" property="selected" value="true">
						<td style="background-color:#FFFFFF; border:1px solid #AAAAAA;" id="NOT_<bean:write name='datasetIndex'/>_td">
					</logic:notEqual>
					<span style="cursor:pointer;" onclick="javascript:toggleNotSelect(<bean:write name='datasetIndex'/>,<bean:write name='notDataset' property='red'/>,<bean:write name='notDataset' property='green'/>,<bean:write name='notDataset' property='blue'/>);">&nbsp;&nbsp;</span>
					<html:hidden name="notDataset" property="datasetId" indexed="true" />
					<html:hidden name="notDataset" property="datasetIndex" indexed="true" 
								 styleId='<%= "NOT_index_"+datasetIndex%>'/>
					<html:hidden name="notDataset" property="sourceString" indexed="true" />
					<html:hidden name="notDataset" property="datasetComments" indexed="true" />
					<html:hidden name="notDataset" property="datasetName" indexed="true" />
					<html:hidden name="notDataset" property="selected" indexed="true" 
								styleId='<%= "NOT_"+datasetIndex+"_select"%>' />
				</td>
					
				</logic:iterate>
			</tr>
			</table>
		</td>
		<td valign="top"><b>XOR</b></td>
		<td>
			<table  cellpadding="0" cellspacing="0">
			<tr>
				<logic:iterate name="proteinSetComparisonForm" property="xorList" id="xorDataset" indexId="dsIndex">
					
					<bean:define name="xorDataset" property="datasetIndex" id="datasetIndex"/>
					<logic:equal name="xorDataset" property="selected" value="true">
						<td style="background-color:rgb(<bean:write name='xorDataset' property='red'/>,<bean:write name='xorDataset' property='green'/>,<bean:write name='xorDataset' property='blue'/>); border:1px solid #AAAAAA;"
							id="XOR_<bean:write name='datasetIndex'/>_td"
						>
					</logic:equal>
					<logic:notEqual name="xorDataset" property="selected" value="true">
						<td style="background-color:#FFFFFF; border:1px solid #AAAAAA;" id="XOR_<bean:write name='datasetIndex'/>_td">
					</logic:notEqual>
					<span style="cursor:pointer;" onclick="javascript:toggleXorSelect(<bean:write name='datasetIndex'/>,<bean:write name='xorDataset' property='red'/>,<bean:write name='xorDataset' property='green'/>,<bean:write name='xorDataset' property='blue'/>);">&nbsp;&nbsp;</span>
					<html:hidden name="xorDataset" property="datasetId" indexed="true" />
					<html:hidden name="xorDataset" property="datasetIndex" indexed="true" 
								 styleId='<%= "XOR_index_"+datasetIndex%>'/>
					<html:hidden name="xorDataset" property="sourceString" indexed="true" />
					<html:hidden name="xorDataset" property="datasetComments" indexed="true" />
					<html:hidden name="xorDataset" property="datasetName" indexed="true" />
					<html:hidden name="xorDataset" property="selected" indexed="true" 
								styleId='<%= "XOR_"+datasetIndex+"_select"%>' />
					</td>
					
				</logic:iterate>
			</tr>
			</table>
		</td>
		</tr>
		</table>
		</td>
	</tr>
	
	<tr>
		<td valign="top" style="padding-bottom: 10px;">Include Proteins:</td>
		<td valign="top" colspan="3" style="padding-bottom: 10px;">
		
			<table cellpadding="0" cellspacing="0">
			<tr>
				<td>
					<html:radio name="proteinSetComparisonForm" property="parsimoniousParam" value="0">
					<span class="tooltip" title="All proteins from each dataset are included in the analysis"><b>All</b></span>
					</html:radio>
				</td>
				<td>
					<html:radio name="proteinSetComparisonForm" property="parsimoniousParam" value="1">
					<span class="tooltip" title="Proteins not parsimonious in a dataset but parsimonious in at least one other dataset are included, in addition to all parsimonious proteins in the dataset.">
					<b>Parsimonious in &gt;= 1 Dataset</b>
					</span>
					</html:radio>
				</td>
				<td>
					<html:radio name="proteinSetComparisonForm" property="parsimoniousParam" value="2">
					<span class="tooltip" title="Only parsimonious proteins from each dataset are included in the analysis">
					<b>Parsimonious ONLY</b>
					</span>
					</html:radio>
				</td>
			</tr>
			
			<logic:equal name="proteinSetComparisonForm" property="hasProteinProphetDatasets" value="false">
			<tr>
				<td></td>
				<td>
					<html:radio name="proteinSetComparisonForm" property="parsimoniousParam" value="3">
					<span class="tooltip" title="All proteins that are non-subset in at least one of the datasets are included in the analysis">
					<b>Non-subset in &gt;= 1 Dataset</b>
					</span>
					</html:radio>
				</td>
				<td>
					<html:radio name="proteinSetComparisonForm" property="parsimoniousParam" value="4">
					<span class="tooltip" title="Only non-subset proteins from each dataset are included in the analysis">
					<b>Non-subset ONLY</b>
					</span>
					</html:radio>
				</td>
			</tr>
			</logic:equal>
			
			<logic:equal name="proteinSetComparisonForm" property="hasProteinProphetDatasets" value="true">
			<tr>
				<td colspan="3">
					<span style="font-size:8pt;">
					NOTE: For ProteinProphet datasets "parsimonious" = NOT "subsumed"
					</span>
				</td>
			</tr>
			</logic:equal>
			</table>
			
		</td>
	</tr>
	
	<tr><td colspan="4" style="border: 1px solid rgb(170, 170, 170); background-color: white;padding:1"><span></span></td></tr>
	<tr><td colspan="4" style="padding:4"><span></span></td></tr>
	
	<!-- ################## MOLECULAR WT. AND pI FILTERS	  ########################################### -->
	<tr>
		<td style="padding: 0 0 5 5;"><b>Mol. Wt:</b> </td>
		<td style="padding: 0 5 5 0" align="left">
			Min. <html:text name="proteinSetComparisonForm" property="minMolecularWt" size="8"></html:text> 
		    Max. <html:text name="proteinSetComparisonForm" property="maxMolecularWt" size="8"></html:text>
		</td>
		<td style="padding:0 0 5 5;"><b>pI:</b></td>
		<td style="padding:0 0 5 0;" align="left">
			Min. <html:text name="proteinSetComparisonForm" property="minPi" size="8"></html:text> 
			Max. <html:text name="proteinSetComparisonForm" property="maxPi" size="8"></html:text>
		</td>
	</tr>
	
	<!-- ################## MIN / MAX PEPTIDES FILTERS	  ########################################### -->
	<tr>
		<td style="padding: 0 0 0 5;"><b># Peptides*:</b> </td>
		<td style="padding: 0 5 0 0" align="left">
			Min. <html:text name="proteinSetComparisonForm" property="minPeptides" size="8"></html:text> 
		    Max. <html:text name="proteinSetComparisonForm" property="maxPeptides" size="8"></html:text>
		</td>
		<td style="padding:0 0 0 5;"><b># Uniq. Peptides*:</b></td>
		<td style="padding:0 0 0 0;" align="left">
			Min. <html:text name="proteinSetComparisonForm" property="minUniquePeptides" size="8"></html:text> 
			Max. <html:text name="proteinSetComparisonForm" property="maxUniquePeptides" size="8"></html:text>
			<!--
			<html:checkbox name="proteinSetComparisonForm"  property="peptideUniqueSequence">Unique Sequence</html:checkbox>
			-->
		</td>
	</tr>
	<tr><td colspan="4" style="padding-bottom:10px;"><span style="font-size:8pt;">* Peptide = sequence + modifications + charge</span></td></tr>
	
	
	<!-- ################## MIN / MAX SPECTRUM COUNT FILTERS	  ########################################### -->
	<tr>
		<td style="padding: 0 0 10 5;"><b># Spectra:</b> </td>
		<td style="padding: 0 5 10 0" align="left">
			Min. <html:text name="proteinSetComparisonForm" property="minSpectrumCount" size="8"></html:text> 
		    Max. <html:text name="proteinSetComparisonForm" property="maxSpectrumCount" size="8"></html:text>
		</td>
		<td style="padding:0 0 10 5;"></td>
		<td style="padding:0 0 10 0;" align="left"></td>
	</tr>
	
	
	<!-- ################## PEPTIDE PROBABILITY FILTERS	  ########################################### -->
	<logic:equal name="proteinSetComparisonForm" property="hasProteinProphetDatasets" value="true">
	<tr>
		<td><b>Min. Peptide Probability: </b></td>
		<td style="padding-left: 5px;">
			<html:text name="proteinSetComparisonForm" property="minPeptideProbability" size="8"></html:text>
			<br/>
  			<span style="font-size:8pt;color:red;">NSP Adjusted Probability is used</span>
		</td>
		<td colspan="2">Apply to: 
			<html:checkbox name="proteinSetComparisonForm" property="applyProbToPept"># Peptides</html:checkbox>
			<html:checkbox name="proteinSetComparisonForm" property="applyProbToUniqPept"># Uniq. Peptides</html:checkbox>
		</td>
	</tr>
	</logic:equal>
	
	
	<!-- ################## PROTEIN PROPHET OPTIONS	  ########################### -->
	<html:hidden name="proteinSetComparisonForm" property="hasProteinProphetDatasets"/>
	<logic:equal name="proteinSetComparisonForm" property="hasProteinProphetDatasets" value="true">
	<tr>
		<td valign="top" style="padding-bottom: 10px;"><b>ProteinProphet Error: </b></td>
		<td valign="top" style="padding-bottom: 10px; padding-left: 5px;">
			<html:text name="proteinSetComparisonForm" property="errorRate"></html:text>
			<br>
			<span style="font-size:8pt;">
				The error rate closest to the one entered above<br>will be used to determine the probability cutoff
			</span>
		</td>
		<td colspan="2" style="padding-bottom: 10px;">
			Cutoff on Protein Group Probability<html:checkbox property="useProteinGroupProbability" />
			<br>
			<span style="font-size:8pt;">
				If checked, probability cutoff will be applied to protein group probability.
				<br>Otherwise cutoff will be applied to individual protein probability.
			</span>
		</td>
	</tr>
	</logic:equal>
	
	
	
		<!-- ################## SEARCH BOX	  ########################################### -->
	<logic:present name="goSupported">
	<tr>
		<td valign="top">GO Terms: <br/><span class="clickable underline" style="color:red; font-weight:bold;" 
		onclick="javascript:openGOTermSearcher();return false;">Search</span></td>
		<td valign="top"><html:text name="proteinSetComparisonForm" property="goTerms" size="40"></html:text><br>
			<span style="font-size:8pt;">Enter a comma-separated list of GO terms (e.g. GO:0006950)</span>
		</td>
		<td valign="top" colspan="2">
			<html:checkbox name="proteinSetComparisonForm" property="matchAllGoTerms" title="Return proteins that match all terms">Match All </html:checkbox>
			<html:checkbox name="proteinSetComparisonForm" property="exactGoAnnotation" title="Return proteins directly annotated with the GO terms">Exact </html:checkbox>
			&nbsp;
			<nobr>
			Exclude: 
			<html:checkbox name="proteinSetComparisonForm" property="excludeIea" title="Inferred from Electronic Annotation">IEA</html:checkbox>
			<html:checkbox name="proteinSetComparisonForm" property="excludeNd" title="No Biological Data available">ND</html:checkbox>
			<html:checkbox name="proteinSetComparisonForm" property="excludeCompAnalCodes" title="Computational Analysis Evidence Codes">ISS, ISO, ISA, ISM, IGC, RCA</html:checkbox>
			</nobr>
		</td>
	</tr>
	</logic:present>
	
	<tr>
		<td style="padding-left:5px;" valign="top">Fasta ID(s):</td>
		
		<logic:present name="commonNameSupported">
			<td style="padding:0 5 5 0;"> 
		</logic:present>
		<logic:notPresent name="commonNameSupported">
			<td style="padding:0 5 5 0;" colspan="3"> 
		</logic:notPresent>
		
			<html:text name="proteinSetComparisonForm" property="accessionLike" size="40"></html:text><br>
 			<span style="font-size:8pt;">Enter a comma-separated list of FASTA identifiers.</span>
 		</td>
 		
 		<logic:present name="commonNameSupported">
 		<td style="padding-left:5px;" valign="top">
 			Common Name(s):
 		</td>
 		<td style="padding-left:5px;" valign="top">
 			<html:text name="proteinSetComparisonForm" property="commonNameLike" size="40"></html:text><br>
 			<span style="font-size:8pt;">Enter a comma-separated list of names.</span>
 		</td>
 		</logic:present>
 	</tr>
 	<tr>
 		<td style="padding-left:5px;" valign="top">Description Include: </td>
 		<td style="padding:0 5 5 0;"> 
			<html:text name="proteinSetComparisonForm" property="descriptionLike" size="40"></html:text>
 		</td>
 		<td style="padding-left:5px;" valign="top"> Exclude:</td>
 		<td> 
			<html:text name="proteinSetComparisonForm" property="descriptionNotLike" size="40"></html:text>
			<span style="font-size:8pt;"><nobr>Search All:<html:checkbox name="proteinSetComparisonForm" property="searchAllDescriptions"></html:checkbox></nobr></span>
 		</td>
 	</tr>
 	<tr>
  		<td></td>
  		<td colspan="3" ">
  			<div style="font-size:8pt;" align="left">Enter a comma-separated list of terms.
  			Descriptions will be included from the fasta file(s) associated with the data-sets <br>
  			being compared, as well as species specific databases (e.g. SGD) 
  			for any associated target species.
  			<br>Check "Search All" to include descriptions from Swiss-Prot and NCBI-NR. 
  			<br/><font color="red">NOTE: Description searches can be time consuming, especially when "Search All" is checked.</font></div>
  		</td>
  		</tr>
 	
 	<tr><td colspan="4" style="border: 1px solid rgb(170, 170, 170); background-color: white;padding:1"><span></span></td></tr>
	<tr><td colspan="4" style="padding:4"><span></span></td></tr>
	
	<tr>
	<td valign="top" colspan="2">
			<html:checkbox name="proteinSetComparisonForm" property="groupIndistinguishableProteins">Group Indistinguishable Proteins</html:checkbox>
		</td>
	<td valign="top" align="left" colspan="2">
			<html:checkbox name="proteinSetComparisonForm" property="keepProteinGroups">Keep Protein Groups</html:checkbox><br>
			<span style="font-size:8pt;">Display ALL protein group members even if some of them do not pass the filtering criteria.</span>
 	</td>
 	</tr>	
 		
	</table>
	</div> <!-- END OF FILTERING OPTIONS -->
	
	
	<!--  ====================================================================================== -->
	<!-- CLUSTRING OPTIONS -->
	<!--  ====================================================================================== -->
	<div style="background-color:#F2F2F2;width:80%; margin:5 0 0 0; padding:1 0 1 0; color:black; border: 1px solid gray; font-size:8pt;" align="left">
	<span style="margin-left:5;" 
	  class="foldable fold-close" id="comparison_culster_opts_fold">&nbsp;&nbsp;&nbsp;&nbsp; </span>
	<b>Clustering Options</b>
	</div>
	<div style="background-color:#F0F8FF; padding: 5 0 5 0; border: 1px solid gray; border-top:0;width:80%; display:none;" id="comparison_culster_opts_fold_target">
	Gradient:
	<html:select name="proteinSetComparisonForm" property="heatMapGradientString">
		<html:option value="<%=ClusteringConstants.GRADIENT.BY.getDisplayName() %>"></html:option>
		<html:option value="<%=ClusteringConstants.GRADIENT.GR.getDisplayName() %>"></html:option>
	</html:select>
	&nbsp;
	
	<html:checkbox name="proteinSetComparisonForm" property="scaleRows"><nobr>
	<span class="tooltip" title="If checked the spectrum count values for each protein(row) are standardized so that the mean is 0 and standard deviation is 1.">Standardize Rows</span>
	</nobr></html:checkbox>
	&nbsp;
	
	<html:checkbox name="proteinSetComparisonForm" property="clusterColumns"><nobr>Cluster Samples</nobr></html:checkbox>
	&nbsp;
	
	<!-- 
	<html:checkbox name="proteinSetComparisonForm" property="useLogScale">Log Scale</html:checkbox>
	&nbsp;
	
	Base:
	<html:select name="proteinSetComparisonForm" property="logBase">
		<html:option value="10"></html:option>
		<html:option value="2"></html:option>
	</html:select>
	&nbsp;
	
	<yrcwww:member group="administrators">
	Replace missing with: 
	<html:text name="proteinSetComparisonForm" property="replaceMissingWithValue" size="3"></html:text>
	&nbsp; 
	</yrcwww:member>
	-->
	<br/>
	<span class="small_font"><b>Note: </b>Normalized spectrum counts are used</span>
	</div> <!-- END OF CLUSTERING OPTIONS -->	
	
	<!--  ====================================================================================== -->
	<!-- GENE ONTOLOGY OPTIONS -->
	<!--  ====================================================================================== -->
	<div style="display:none;">
	<logic:present name="goSupported">
	<div style="background-color:#F2F2F2;width:80%; margin:5 0 0 0; padding:1 0 1 0; color:black; border: 1px solid gray; font-size:8pt;" align="left">
	<span style="margin-left:5;" 
	  class="foldable fold-close" id="comparison_go_opts_fold">&nbsp;&nbsp;&nbsp;&nbsp; </span>
	<b>Gene Ontology Analysis Options</b>
	</div>
	<div style="background-color:#F0F8FF; padding: 5 0 5 0; border: 1px solid gray; border-top:0;width:80%; display:none;" id="comparison_go_opts_fold_target">
	
	<html:hidden name="proteinSetComparisonForm" property="goAspect" />
	
	<table cellpadding="5">
    	<tr>
    	<td valign="top" style="padding:5x;"><b>GO Slim Analysis: </b></td>
    	<td style="padding:5x;" valign="top">
    		GO Aspect: 
    		<html:select name="proteinSetComparisonForm" property="goAspect" styleId="goAspectField1">
			<html:option
				value="<%=String.valueOf(GOUtils.BIOLOGICAL_PROCESS) %>">Biological Process</html:option>
			<html:option
				value="<%=String.valueOf(GOUtils.CELLULAR_COMPONENT) %>">Cellular Component</html:option>
			<html:option
				value="<%=String.valueOf(GOUtils.MOLECULAR_FUNCTION) %>">Molecular Function</html:option>
			</html:select>
    	</td>
    	
    	<td style="padding:5x;" valign="top">
    		GO Slim:
			<html:select name="proteinSetComparisonForm" property="goSlimTermId">
			<html:options collection="goslims" property="id" labelProperty="name"/>
			</html:select>
			<div align="center" style="width:100%; font-size:8pt;">
			<a href="http://www.geneontology.org/GO.slims.shtml" target="go_window">More information on GO Slims</a>
			</div>
    	</td>
    	</tr>
    	
    	<tr>
    	<td valign="top" style="padding:5x;"><b>GO Enrichment: </b></td>
    	<td style="padding:5x;">
    		GO Aspect:
    		<html:select name="proteinSetComparisonForm" property="goAspect" styleId="goAspectField2">
			<html:option
				value="<%=String.valueOf(GOUtils.BIOLOGICAL_PROCESS) %>">Biological Process</html:option>
			<html:option
				value="<%=String.valueOf(GOUtils.CELLULAR_COMPONENT) %>">Cellular Component</html:option>
			<html:option
				value="<%=String.valueOf(GOUtils.MOLECULAR_FUNCTION) %>">Molecular Function</html:option>
		</html:select>
    	</td>
    	
    	<td style="padding:5x;">
    		P-Value: <html:text name="proteinSetComparisonForm" property="goEnrichmentPVal"></html:text>
    	</td>
		
		<td style="padding:5x;">
			<logic:present name="speciesList">
				Species: <html:select name="proteinSetComparisonForm" property="speciesId">
    			<html:option value="0">None</html:option>
    			<html:options collection="speciesList" property="id" labelProperty="name"/>
    			</html:select>
			</logic:present>
    	</td>
    	</tr>
    	</table>
	</div> <!-- END OF GENE ONTOLOGY OPTIONS -->	
	</logic:present>
	</div>
	
	<!--  ====================================================================================== -->
	<!-- DISPLAY OPTIONS -->
	<!--  ====================================================================================== -->
	<div style="background-color:#F2F2F2;width:80%; margin:5 0 0 0; padding:1 0 1 0; color:black; border: 1px solid gray; font-size:8pt;" align="left">
	<span style="margin-left:5;" 
	  class="foldable fold-close" id="comparison_display_opts_fold">&nbsp;&nbsp;&nbsp;&nbsp; </span>
	<b>Display Options</b>
	</div>
	<div style="background-color:#F0F8FF; padding: 5 0 5 0; border: 1px solid gray; border-top:0;width:80%; display:none;" id="comparison_display_opts_fold_target">
	
	<table align="center" width="80%">
	<tr>
		<td valign="top" align="center">
		<span class="clickable underline" id="columnChooser" 
			      onclick="toggleColumnChooser();">Choose Columns</span>
		</td>
		<td valign="top" align="center">
			<span class="clickable underline" id="orderChanger" 
 			      onclick="toggleOrderChanger();">Order Datasets</span> 
		</td>
 	</tr>
	
	<tr>
	<td colspan="2" align="center">
	
	<!-- DATASET ORDER CHANGER -->
	<div id="orderChangerTgt" class="small_font" align="left" 
		 style="padding: 5 0 5 0; border: 1px solid gray; width:70%; display:none; margin-bottom:5px;">
		 
		 <table id="datasetOrderTbl" cellpadding="3" cellspacing="2" align="center">
		 <thead>
		 	<tr>
		 		<th></th>
		 		<th class="sort-int clickable">ID</th>
		 		<th class="sort-alpha clickable">Name</th>
		 		<th class="sort-alpha clickable">Comments</th>
		 		<th></th>
		 	</tr>
		 </thead>
		 <tbody>
		 	<logic:iterate name="proteinSetComparisonForm" property="andList" id="andDataset" indexId="row">
		 	<tr id="<bean:write name='andDataset' property='datasetIndex' />">
		 		<!-- <td class="small_font"><%=row %></td> -->
		 		<td><span style="display:block; float:left; width:10px; height:10px; background: rgb(<%=DatasetColor.get(row).R %>,<%=DatasetColor.get(row).G %>,<%=DatasetColor.get(row).B %> );;"></span></td>
		 		<td class="small_font"><b><bean:write name='andDataset' property='datasetId' /></b></td>
		 		<td class="small_font"><bean:write name='andDataset' property='datasetName' /></td>
		 		<td class="small_font"><bean:write name='andDataset' property='datasetComments' /></td>
		 		<td class="draggable"><span class="dragHandle"></span></td>
		 	</tr>
		 	</logic:iterate>
		 </tbody>
		 </table>
		
		<div class="small_font" style="padding:10px;">Drag the blue-bordered white boxes to reorder the datasets, or click on the column headers to sort. Click "Update" to display results with the new order</div>
		
		<div align="center" style="padding:3px;"><input type="button" value="Reset"  id="resetDatasetOrder"/></div>
		
	</div>
	
	
	<!-- DISPLAY COLUMN CHOOSER -->
	<div id="columnChooserTgt" class="small_font" align="left" 
		 style="padding: 5 0 5 0; border: 1px solid gray; width:50%; display:none">
	
		<html:checkbox name="proteinSetComparisonForm" property="showPresent" styleClass="colChooser"
					   styleId="showPresent"  title="<%=String.valueOf(DisplayColumns.present) %>">Present / Not-present</html:checkbox>
		<br/>
		<html:checkbox name="proteinSetComparisonForm" property="showFastaId" styleClass="colChooser"
		   			   styleId="showFastaId" title="<%=String.valueOf(DisplayColumns.fasta) %>">Fasta ID</html:checkbox>
		<br/>
		<html:checkbox name="proteinSetComparisonForm" property="showCommonName" styleClass="colChooser"
					   styleId="showCommonName" title="<%=String.valueOf(DisplayColumns.commonName) %>">Common Name</html:checkbox>
		<br/>
		<html:checkbox name="proteinSetComparisonForm" property="showDescription" styleClass="colChooser"
					   styleId="showDescription" title="<%=String.valueOf(DisplayColumns.description) %>">Description</html:checkbox>
		<br/>
		<html:checkbox name="proteinSetComparisonForm" property="showMolWt" styleClass="colChooser"
					   styleId="showMolWt" title="<%=String.valueOf(DisplayColumns.molWt) %>">Molecular Wt.</html:checkbox>
		<br/>
		<html:checkbox name="proteinSetComparisonForm" property="showPi" styleClass="colChooser"
		 			   styleId="showPi" title="<%=String.valueOf(DisplayColumns.pi) %>">pI</html:checkbox>
		<br/>
		<html:checkbox name="proteinSetComparisonForm" property="showTotalSeq" styleClass="colChooser"
					   styleId="showTotalSeq" title="<%=String.valueOf(DisplayColumns.totalSeq) %>">Total # Sequences for a protein</html:checkbox>
		<br/>
		<html:checkbox name="proteinSetComparisonForm" property="showNumSeq" styleClass="colChooser"
					   styleId="showNumSeq" title="<%=String.valueOf(DisplayColumns.numSeq) %>"># Sequences (S) for a protein in a dataset</html:checkbox>
		<br/>
		<html:checkbox name="proteinSetComparisonForm" property="showNumIons" styleClass="colChooser"
					   styleId="showNumIons" title="<%=String.valueOf(DisplayColumns.numIons) %>"># Ions (I) for a protein in a dataset</html:checkbox>
		<br/>
		<html:checkbox name="proteinSetComparisonForm" property="showNumUniqIons" styleClass="colChooser"
					   styleId="showNumUniqIons" title="<%=String.valueOf(DisplayColumns.numUniqueIons) %>"># Unique Ions (U.I) for a protein in a dataset</html:checkbox>
		<br/>
		<html:checkbox name="proteinSetComparisonForm" property="showSpectrumCount" styleClass="colChooser"
					   styleId="showSpectrumCount" title="<%=String.valueOf(DisplayColumns.numSpectrumCount) %>">Spectrum Count (SC) for a protein in a dataset</html:checkbox>
		<br/>
		<html:checkbox name="proteinSetComparisonForm" property="showNsaf" styleClass="colChooser"
					   styleId="showSpectrumCount" title="<%=String.valueOf(DisplayColumns.nsaf) %>">NSAF* (N) for a protein in a dataset</html:checkbox>
		<br/>
		<span class="small_font">*NSAF is available only for proteins inferred via MSDaPl.</span>
		<br/><br/>
		<input type="button" value="Save Settings"  onclick="saveDisplayColumnsCookie();"/>
		</div>
	</td>
	</tr>
	</table>
	</div> <!-- END OF DISPLAY OPTIONS -->
	
	
	<div style="background-color:#F0F8FF; padding: 5 0 5 0; border: 1px solid gray; margin-top:10px; width:80%" align="center">
		<table align="center" width="95%">
		<tr>
		<!-- UPDATE RESULTS -->
		<td align="left">
			<b>Action:</b>
			<html:select name="proteinSetComparisonForm" property="comparisonActionId" styleId="actionOptions" onchange="javascript:showActionOptions();">
				<html:options collection="comparisonCommands" property="id" labelProperty="displayName"/>
			</html:select>
			&nbsp; &nbsp;
 			<html:submit value="Update" onclick="javascript:updateResults();return false;" styleClass="plain_button" style="margin-top:0px;"></html:submit>
 		</td>
 		
 		<td style="border: 1px solid rgb(170, 170, 170); background-color: white;padding:1"><span></span></td>
	
 		<!-- DOWNLOAD RESULTS -->
 		<td align="right">
 			<html:checkbox name="proteinSetComparisonForm" property="collapseProteinGroups">Collapse Protein Groups</html:checkbox>
 			&nbsp;
 			Include: 
			<html:checkbox name="proteinSetComparisonForm" property="includeDescriptions">Description</html:checkbox>
			&nbsp;
			<html:checkbox name="proteinSetComparisonForm" property="includePeptides">Peptides</html:checkbox>
			&nbsp; &nbsp;
			<html:submit value="Download" onclick="javascript:downloadResults(); return false;" styleClass="plain_button" style="margin-top:0px;"></html:submit>
		</td>
		</tr>
		</table>
	</div>

</center>
</html:form>