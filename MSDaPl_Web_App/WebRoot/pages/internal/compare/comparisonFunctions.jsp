
<%@page import="org.yeastrc.www.compare.dataset.DatasetColor"%>
<%@page import="org.yeastrc.ms.domain.search.SORT_ORDER"%>

<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>


<script src="<yrcwww:link path='js/comparison.js'/>"></script>
<script>

$(document).ready(function() {
	
	// make the table sortable
   	makeSortable();
    
   $("#compare_results_pager1").attr('width', "95%").attr('align', 'center');
   $("#compare_results_pager2").attr('width', "95%").attr('align', 'center');
   
    $("#compare_results").each(function() {
   		var $table = $(this);
   		$table.attr('width', "95%");
   		$table.attr('align', 'center');
   		$('.prot_descr', $table).css("font-size", "8pt");
   		
   		<%for(int i = 0; i < comparison.getDatasetCount(); i++) {
   			String color = DatasetColor.get(i).R+", "+DatasetColor.get(i).G+","+DatasetColor.get(i).B;
   		%>
   			$("td.prot-found[id="+<%=String.valueOf(i)%>+"]", $table).css('background-color', "rgb(<%=color%>)");
   		<%}%>
   		
   		$('td.prot-parsim', $table).css('color', '#FFFFFF').css('font-weight', 'bold');
   	});
   	
 });
 
 // ---------------------------------------------------------------------------------------
// MAKE TABLE SORTABLE
// ---------------------------------------------------------------------------------------
function makeSortable() {

   $(".sortable_table").each(function() {
   		var $table = $(this);
   		$table.attr('width', "100%");
   		$table.attr('align', 'center');
   		//$('tbody > tr:odd', $table).addClass("tr_odd");
   		//$('tbody > tr:even', $table).addClass("tr_even");
   		
   		$('th', $table).each(function() {
   		
   				if($(this).is('.sortable')) {
      					
      				$(this).click(function() {
						var sortBy = $(this).attr('id');
						// sorting direction
						var sortOrder = "<%=SORT_ORDER.ASC.name()%>";
						// is the column already sorted?
						if ($(this).is('.sorted-asc')) {
		          			sortOrder = "<%=SORT_ORDER.DESC.name()%>";
		        		}
		        		else if ($(this).is('.sorted-desc')) {
		          			sortOrder = "<%=SORT_ORDER.ASC.name()%>";
		        		}
		        		// do we have a default sorting order?
		        		else if ($(this).is('.def-sorted-desc')) { 
		          			sortOrder = "<%=SORT_ORDER.DESC.name()%>";
		        		}
		        		else if ($(this).is('.def-sorted-asc')) {
		          			sortOrder = "<%=SORT_ORDER.ASC.name()%>";
		        		}
	        			sortResults(sortBy, sortOrder);
      			});
      		}
      	});
   });
}
 
 
// ---------------------------------------------------------------------------------------
// SETUP THE PEPTIDES TABLE
// ---------------------------------------------------------------------------------------
function  setupPeptidesTable(table){
		var $table = $(table);
   		$table.attr('width', "60%");
   		$table.attr('align', 'center');
   		$table.css("margin", "5 5 5 5");
   		
   		<%for(int i = 0; i < comparison.getDatasetCount(); i++) {
   			String color = DatasetColor.get(i).R+", "+DatasetColor.get(i).G+","+DatasetColor.get(i).B;
   		%>
   			$("td.pept-found[id="+<%=String.valueOf(i)%>+"]", $table).css('background-color', "rgb(<%=color%>)").css('color', "rgb(<%=color%>)");
   		<%}%>
   		
   		$('td.pept-unique', $table).css('color', '#FFFFFF').css('font-weight', 'bold');
   		makeSortableTable($table);
   		
   		
}

// ---------------------------------------------------------------------------------------
// SUBMIT FORM FOR GO SLIM ANALYSIS
// ---------------------------------------------------------------------------------------
function submitFormForGoSlimAnalysis() {

	// hide the main table
	$("#result_table").hide();
	
	var goAspect = $("#goAspectField1").val();
	$("form[name='proteinSetComparisonForm'] input[name='goAspect']").val(goAspect);
	
	$.blockUI();
	$.post( "<yrcwww:link path='updateProteinSetComparison.do'/>", 	//url, 
			$("form[name='proteinSetComparisonForm']").serialize(), // data to submit
    		 function(result, status) {
    		 	// load the result
    		 	$('#goslim_result').html(result);
    		 	$.unblockUI();
    		 	// stripe the table
    		 	if($("#go_slim_table").length > 0) {
	    		 	makeStripedTable($("#go_slim_table")[0]);
	    		 	makeSortableTable($("#go_slim_table")[0]);
    		 	}
    		 	hideGoEnrichmentDetails();
    });
}

// ---------------------------------------------------------------------------------------
// SUBMIT FORM FOR GO SLIM ANALYSIS (TREE)
// ---------------------------------------------------------------------------------------
function viewGoSlimGraph() {
	alert("tree!!");
	submitFormForGoSlimAnalysisTree();
}
function submitFormForGoSlimAnalysisTree() {

	// hide the main table
	$("#result_table").hide();
	
	var goAspect = $("#goAspectField1").val();
	$("form[name='proteinSetComparisonForm'] input[name='goAspect']").val(goAspect);
	$("form[name='proteinSetComparisonForm'] input[name='comparisonActionId']").val(3);
	
	$.blockUI();
	$.post( "<yrcwww:link path='updateProteinSetComparison.do'/>", 	//url, 
			$("form[name='proteinSetComparisonForm']").serialize(), // data to submit
    		 function(result, status) {
    		 	// load the result
    		 	$.unblockUI();
    		 	winWidth="500";
    		 	winHeight="500";
    		 	var windowRef = window.open("http://google.com", "GO_TREE_WINDOW", "width=" + winWidth + ",height=" + winHeight + ",status=no,resizable=yes,scrollbars=yes");
    		 	windowRef.location.reload("http://news.google.com");
    });
}


// ---------------------------------------------------------------------------------------
// SUBMIT FORM FOR GO ENRICHMENT ANALYSIS
// ---------------------------------------------------------------------------------------
function submitFormForGoEnrichmentAnalysis() {
	
	// hide the main table
	$("#result_table").hide();
	
	var goAspect = $("#goAspectField2").val();
	$("form[name='proteinSetComparisonForm'] input[name='goAspect']").val(goAspect);
	
	$.blockUI();
	$.post( "<yrcwww:link path='updateProteinSetComparison.do'/>", 	//url, 
			$("form[name='proteinSetComparisonForm']").serialize(), // data to submit
    		 function(result, status) {
    		 	// load the result
    		 	$('#goenrichment_result').html(result);
    		 	$.unblockUI();
    		 	// stripe the table
    		 	if($("#go_enrichment_table").length > 0) {
    		 		makeStripedTable($("#go_enrichment_table")[0]);
    		 		makeSortableTable($("#go_enrichment_table")[0]);
    		 	}
    		 	hideGoSlimDetails();
    });

}

function openGOTermSearcher() {
	var url = "<yrcwww:link path='goTermSearch.do'/>";
	// we want the result to open in a new window
	window.open(url, 'gotermsearcher', 'scrollbars=yes,menubar=no,height=500,width=650,resizable=yes,toolbar=no,status=no');
}

// terms is an array of goTerms
function addToGoSearchTerms(terms) {
	for(var i = 0; i < terms.length; i++) {
		addToGoTermFilters(terms[i], false);
	}
}
	
function addToGoTermFilters(goTerm, warn) {
	var current = $("form[name='proteinSetComparisonForm'] input[name='goTerms']").val();
	// If this terms in not already in the list add it.
	if(current.indexOf(goTerm) == -1) {
		var terms = current;
		if(current)
			terms = terms+","
		terms = terms+goTerm;
		$("form[name='proteinSetComparisonForm'] input[name='goTerms']").val(terms);
	}
	else if(warn) {
		alert(goTerm+" has already been added");
	}
	$(".go_filter_add[id='"+goTerm+"']").hide();
	$(".go_filter_remove[id='"+goTerm+"']").show();
}
</script>