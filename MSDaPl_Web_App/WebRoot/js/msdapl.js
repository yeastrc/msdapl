// ---------------------------------------------------------------------------------------
// MAKE A TABLE STRIPED
// ---------------------------------------------------------------------------------------
function makeStripedTable(table) {
	var $table = $(table);
	$('tbody > tr:odd', $table).addClass("tr_odd");
   	$('tbody > tr:even', $table).addClass("tr_even");
}

// ---------------------------------------------------------------------------------------
// MAKE A TABLE SORTABLE
// ---------------------------------------------------------------------------------------
function makeSortableTable(table) {
  	
	var $table = $(table);
	$('th', $table).each(function(column) {
  		
  		if ($(this).is('.sort-alpha') || $(this).is('.sort-int') 
  			|| $(this).is('.sort-float') ) {
  		
  			var $header = $(this);
      		$(this).click(function() {

				// remove row striping
				if($table.is('.stripe_table')) {
					$("tbody > tr:odd", $table).removeClass("tr_odd");
					$("tbody > tr:even", $table).removeClass("tr_even");
				}
				
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
				
				if ($header.is('.sort-float')) {
        					$.each(rows, function(index, row) {
								var key = parseFloat($(row).children('td').eq(column).text());
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
        
        		
        		// add row striping back
        		if($table.is('.stripe_table')) {
					$('tbody > tr:odd', $table).addClass("tr_odd");
   					$('tbody > tr:even', $table).addClass("tr_even");
        		}
      		});
	}
  });
}

// ---------------------------------------------------------------------------------------
// FOLDABLE
// ---------------------------------------------------------------------------------------
function fold(foldable) {
	//alert("foldable clicked");
	if(!foldClose(foldable))
	foldOpen(foldable);
}
function foldClose(foldable) {
	var target_id = foldable.attr('id')+"_target";
   	//alert("target is: "+target_id);
	
	if(foldable.is('.fold-open')) {
		foldable.removeClass('fold-open');
		foldable.addClass('fold-close');
		$("#"+target_id).hide();
		
		return true;
 	}
 	return false;
}
function foldOpen(foldable) {
	var target_id = foldable.attr('id')+"_target";
   	//alert("target is: "+target_id);
	
	if(foldable.is('.fold-close')) {
		foldable.removeClass('fold-close');
		foldable.addClass('fold-open');
		$("#"+target_id).show();
		return true;
	}
	return false;
}

//---------------------------------------------------------------------------------------
//HIGHLIGHT MODIFICATIONS IN PEPTIDE SEQUENCES
//---------------------------------------------------------------------------------------
function highlightModifications (highightable) {
	
	var options = {};
	options.colors = new Array();
	
    highlightable.highlightPeptide(options); 
}

function openInformationPopup(url) {
	window.open(
		url,
		'MSDaPl_info',
		'height=500,width=700,left=10,top=10,resizable=yes,scrollbars=yes,toolbar=no,menubar=no,location=no,directories=no,status=yes');
}