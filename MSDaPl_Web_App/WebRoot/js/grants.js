function getGrantsTable() {
	return document.getElementById("fundingSources");
}

function addGrant(grantID, grantTitle, piID, PI, sourceType, sourceName, grantNumber, grantAmount) {
	
	var table = getGrantsTable();
	var numRows = table.rows.length;
	
	// if table has 0 rows, add a header row
	if (numRows == 0) {
		addHeaderRow(table);
		numRows += 1;
	}
	
	var sourceIdx = numRows -1; // first row is the header
	
	var row = table.insertRow(numRows);
	var grantIDCell = row.insertCell(0);
	var grantTitleCell = row.insertCell(1);
	var grantPICell = row.insertCell(2);
	var sourceTypeCell = row.insertCell(3);
	var sourceNameCell = row.insertCell(4);
	var grantNumberCell = row.insertCell(5);
	var grantAmtCell = row.insertCell(6);
	var removeCell = row.insertCell(7);
	var editCell = row.insertCell(8);
	
	grantIDCell.innerHTML = '<input type="hidden" name="grant['+sourceIdx+']" value="'+grantID+'" />';
	sourceTypeCell.innerHTML = sourceType;
	sourceTypeCell.style.fontSize = "8pt";
	sourceNameCell.innerHTML = sourceName;
	sourceNameCell.style.fontSize = "8pt";
	grantTitleCell.innerHTML = grantTitle;
	grantTitleCell.style.fontSize = "8pt";
	grantPICell.innerHTML = '<a href="'+<yrcwww:link path='viewResearcher.do?'/>+'id='+piID+'">'+PI+'</a>';
	grantPICell.style.fontSize = "8pt";
	grantNumberCell.style.fontSize = "8pt";
	grantNumberCell.innerHTML = grantNumber;
	grantAmtCell.style.fontSize = "8pt";
	grantAmtCell.innerHTML = grantAmount;
	removeCell.innerHTML = '<a href="javascript:confirmRemoveGrant('+numRows+');" style="color:red; font-size:8pt;">[Remove]</a>';
	editCell.innerHTML = '<a href="javascript:editGrant('+grantID+');" style="font-size:8pt;">Edit</a>';
}

function addHeaderRow(table) {
	var row = table.insertRow(0);
	row.insertCell(0);
	var titleCell = row.insertCell(1);
	var piCell = row.insertCell(2);
	var sourceTypeCell = row.insertCell(3);
	var sourceNameCell = row.insertCell(4);
	var grantNumberCell = row.insertCell(5);
	var grantAmtCell = row.insertCell(6);
	
	sourceTypeCell.innerHTML = '<b>Source Type</b>';
	sourceTypeCell.style.fontSize = "8pt";
	sourceNameCell.innerHTML = '<b>Source Name</b>';
	sourceNameCell.style.fontSize = "8pt";
	titleCell.innerHTML = '<b>Grant Title</b>';
	titleCell.style.fontSize = "8pt";
	piCell.innerHTML = '<b>PI</b>';
	piCell.style.fontSize = "8pt";
	grantNumberCell.style.fontSize = "8pt";
	grantNumberCell.innerHTML = '<b>Grant #</b>';
	grantAmtCell.style.fontSize = "8pt";
	grantAmtCell.innerHTML = '<b>Annual Funds</b>';
}

function removeGrant(rowIdx) {
	var table = getGrantsTable();
	var row = table.getElementsByTagName("tr")[rowIdx];
	var idCell = row.getElementsByTagName("td")[0];
	// set the value to 0 so that we know it is not valid
	idCell.childNodes[0].value='0';
	row.style.display = 'none';
}

function confirmRemoveGrant(rowIdx) {
   	if(confirm("Are you sure you want to remove this grant?")) {
   		removeGrant(rowIdx);
   	}
	}
	
function addFundingSource() {
	// get the selected PI ID from the drop-down list
	var piList = document.getElementsByName("PI")[0];
	PI = 0;
	for (var i = 0; i < piList.length; i+=1) {
		if (piList[i].selected) {
			PI = piList[i].value;
			break;
		}
	}
	
	var winHeight = 500
	var winWidth = 700;
	var doc = "<yrcwww:link path='viewGrants.do'/>?PI="+PI;
	window.open(doc, "GRANT_WINDOW", "width=" + winWidth + ",height=" + winHeight + ",status=no,resizable=yes,scrollbars=yes");
}


function editGrant(grantID) {
	var winHeight = 500
	var winWidth = 600;
	var doc = "<yrcwww:link path='editGrant.do?grantID='/>"+grantID;
	window.open(doc, "GRANT_WINDOW", "width=" + winWidth + ",height=" + winHeight + ",status=no,resizable=yes,scrollbars=yes");
}

function updateGrant(grantID, grantTitle, piID, PI, sourceType, sourceName, grantNumber, grantAmount) {
	// find a row with the given grantID
	var rows = getGrantsTable().rows;
	for (var i = 1; i < rows.length; i+=1) { // start from row 1; first row is the header
		var cells = rows[i].getElementsByTagName("td");
		var idCell = cells[0];
		if (idCell.childNodes[0].value == grantID) {
			cells[1].innerHTML = grantTitle;
			cells[2].innerHTML = '<a href="'+<yrcwww:link path='viewResearcher.do'/>+'?id='+piID+'">'+PI+'</a>';
			cells[3].innerHTML = sourceType;
			cells[4].innerHTML = sourceName;
			cells[5].innerHTML = grantNumber;
			cells[6].innerHTML = grantAmount;
		}
	}
}