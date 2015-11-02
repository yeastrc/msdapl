<%@page import="org.yeastrc.ms.domain.search.SORT_ORDER"%>

<script>
// ---------------------------------------------------------------------------------------
// SETUP THE TABLE
// ---------------------------------------------------------------------------------------
$(document).ready(function() {
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
						if ($(this).is('.sorted-asc')) {
		          			sortOrder = "<%=SORT_ORDER.DESC.name()%>";
		        		}
		        		else if ($(this).is('.sorted-desc')) {
		          			sortOrder = "<%=SORT_ORDER.ASC.name()%>";
		        		}
	        			sortResults(sortBy, sortOrder);
      			});
      		}
      	});
   });
});

// ---------------------------------------------------------------------------------------
// PAGE RESULTS
// ---------------------------------------------------------------------------------------
function pageResults(pageNum) {
  	$("input#pageNum").val(pageNum);
  	$("input[name='doDownload']").val("false");
  	//alert("setting to "+pageNum+" value set to: "+$("input#pageNum").val());
  	$("form").submit();
}
// ---------------------------------------------------------------------------------------
// SORT RESULTS
// ---------------------------------------------------------------------------------------
function sortResults(sortBy, sortOrder) {
  	// alert(sortBy+" "+sortOrder);
  	$("input[name='doDownload']").val("false");
  	$("input#pageNum").val(1); // reset the page number to 1
  	$("input#sortBy").val(sortBy);
  	$("input#sortOrder").val(sortOrder);
  	//alert($("input#pageNum").val()+"   "+$("input#sortBy").val()+"   "+$("input#sortOrder").val());
  	$("form").submit();
}
// ---------------------------------------------------------------------------------------
// UPDATE RESULTS
// ---------------------------------------------------------------------------------------
function updateResults() {
	$("input[name='doDownload']").val("false");
	$("input#pageNum").val(1); // reset the page number to 1
	$("input#numPerPage").val($("input#pager_result_count").val());
	//alert("setting result count to: "+$("input#numPerPage").val());
	$("form").submit();
}

// ---------------------------------------------------------------------------------------
// DOWNLOAD RESULTS
// ---------------------------------------------------------------------------------------
function downloadResults() {
	// alert("Downloading results "+$("input[name='doDownload']").val());
	$("input[name='doDownload']").val("true");
	$("form").submit();
}

// ---------------------------------------------------------------------------------------
// TOGGLE PROTEINS
// ---------------------------------------------------------------------------------------
function toggleProteins(resultId) {
	
	if($("#proteins_for_"+resultId).is(':visible')) {
		$("#proteins_for_"+resultId).hide();
	}
	else {
		$("#proteins_for_"+resultId).show();
	}
}

</script>

