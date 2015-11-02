<tr><td colspan="2" align="center" style="font-size: 10pt; font-weight: bold;">Funding Sources&nbsp;&nbsp;&nbsp;</td></tr>
<tr><td colspan="2" align="center">
	
	<logic:present name="editProjectForm">
		<bean:define name="editProjectForm" id="form" />
	</logic:present>
	
	<bean:size name="form" property="grantList" id="numGrants" />
	
	<table id="fundingSources" style="border: 1px solid #999999; width:80%; margin-top: 10px;" cellpadding="5px">
	<logic:greaterThan name="numGrants" value="0">
		<tr>
			<td></td>
			<td style="font-size:8pt;"><b>Grant Title</b></td>
			<td style="font-size:8pt;"><b>PI</b></td>
			<td style="font-size:8pt;"><b>Source Type</b></td>
			<td style="font-size:8pt;"><b>Source Name</b></td>
			<td style="font-size:8pt;"><b>Grant #</b></td>
			<td style="font-size:8pt;"><b>Annual Funds</b></td>
			<td></td>
			<td></td>
		</tr>
	</logic:greaterThan>
	<% int rowIdx = 1; int sourceIdx = rowIdx-1;%>
	<logic:iterate name="form" property="grantList" id="grant">
		<tr name="grantrow">
			<td>
				<html:hidden name="grant" property="ID" indexed="true" />
			</td>
			<td style="font-size:8pt;">
				<html:hidden name="grant" property="title" indexed="true" />
				<span id="gc_title"><bean:write name="grant" property="title" /></span>
			</td>
			<td style="font-size:8pt;">
				<html:hidden name="grant" property="grantPI.ID" indexed="true" />
				<html:hidden name="grant" property="grantPI.lastName" indexed="true" />
				<html:link action="viewResearcher.do" paramId="id" paramName="grant" paramProperty="grantPI.ID">
					<bean:write name="grant" property="grantPI.lastName" />
				</html:link>
			</td>
			<td style="font-size:8pt;">
				<html:hidden name="grant" property="fundingSource.sourceType.displayName" indexed="true" />
				<span id="gc_ftype"><bean:write name="grant" property="fundingSource.sourceType.displayName" /></span>
			</td>
			<td style="font-size:8pt;">
				<html:hidden name="grant" property="fundingSource.sourceName.displayName" indexed="true" />
				<span id="gc_fname"><bean:write name="grant" property="fundingSource.sourceName.displayName" /></span>
			</td>
			<td style="font-size:8pt;">
				<html:hidden name="grant" property="grantNumber" indexed="true" />
				<span id="gc_num"><bean:write name="grant" property="grantNumber" /></span>
			</td>
			<td style="font-size:8pt;">
				<html:hidden name="grant" property="grantAmount" indexed="true" />
				<span id="gc_amt"><bean:write name="grant" property="grantAmount" /></span>
			</td>
			<td><a href="javascript:editGrant(<bean:write name="grant" property="ID" />);" style="font-size:8pt;">Edit</a></td>
			<td><a href="javascript:confirmRemoveGrant('<%=rowIdx%>')" style="color:red; font-size:8pt;">[Remove]</a></td>
		</tr>
		<% rowIdx++; sourceIdx++;%>
	</logic:iterate>
	</table>
</td></tr>
<tr>
	<td colspan="2" align="center">
		<input type="button" value="Add" onclick="javascript:addFundingSource();" class="button"/>
</tr>

<script>

// value of this variable will be used by pop-up to determine what action to take when
// Cancel is clicked in the New / Edit grant form window.
// If EDIT_CLICKED == true, the popup is simply closed
// otherwise, the grant list view is displayed again in the pop-up window.
var EDIT_CLICKED = false;

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
	
	if (!hasGrant(grantID)) {
		var row = table.insertRow(numRows);
		row.setAttribute('name', grantRowName);
		var grantIDCell = row.insertCell(0);
		var grantTitleCell = row.insertCell(1);
		var grantPICell = row.insertCell(2);
		var sourceTypeCell = row.insertCell(3);
		var sourceNameCell = row.insertCell(4);
		var grantNumberCell = row.insertCell(5);
		var grantAmtCell = row.insertCell(6);
		var editCell = row.insertCell(7);
		var removeCell = row.insertCell(8);
		
		// grant ID
		grantIDCell.innerHTML = '<input type="hidden" name="grant['+sourceIdx+'].ID" value="'+grantID+'" />';
		// grant title
		grantTitleCell.innerHTML = '<input type="hidden" name="grant['+sourceIdx+'].title" value="'+grantTitle+'" />'+
									'<span id="gc_title">'+grantTitle+'</span>';
		grantTitleCell.style.fontSize = "8pt";
		// grant PI
		var html = '<input type="hidden" name="grant['+sourceIdx+'].grantPI.ID" value="'+piID+'"/> ';
		html += '<input type="hidden" name="grant['+sourceIdx+'].grantPI.lastName" value="'+PI+'"/> ';
		html += '<a href="<yrcwww:link path='viewResearcher.do?id='/>'+piID+'">'+PI+'</a>';
		grantPICell.innerHTML = html;
		grantPICell.style.fontSize = "8pt";
		// funding source type	
		sourceTypeCell.innerHTML = '<input type="hidden" name="grant['+sourceIdx+'].fundingSource.sourceType.displayName" value="'+sourceType+'"/> '+
									'<span id="gc_ftype">'+sourceType+'</span>';
		sourceTypeCell.style.fontSize = "8pt";
		// funding source name
		sourceNameCell.innerHTML = '<input type="hidden" name="grant['+sourceIdx+'].fundingSource.sourceType.displayName" value="'+sourceName+'"/> '+
									'<span id="gc_fname">'+sourceName+'</span>';
		sourceNameCell.style.fontSize = "8pt";
		// grant number
		grantNumberCell.style.fontSize = "8pt";
		grantNumberCell.innerHTML = '<input type="hidden" name="grant['+sourceIdx+'].grantNumber" value="'+grantNumber+'" />'+
									'<span id="gc_num">'+grantNumber+'</span>';;
		// grant amount
		grantAmtCell.style.fontSize = "8pt";
		grantAmtCell.innerHTML = '<input type="hidden" name="grant['+sourceIdx+'].grantAmount" value="'+grantAmount+'" />'+
									'<span id="gc_amt">'+grantAmount+'</span>';
		
		// edit link
		editCell.innerHTML = '<a href="javascript:editGrant('+grantID+');" style="font-size:8pt;">Edit</a>';
		// remove link
		removeCell.innerHTML = '<a href="javascript:confirmRemoveGrant('+numRows+');" style="color:red; font-size:8pt;">[Remove]</a>';
	}
	else {
		//alert("Grant already added: "+grantTitle);
	}
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
	for (var i = 0; i < idCell.childNodes.length; i++) {
		if (idCell.childNodes[i].value)
			idCell.childNodes[i].value = '0';
	}
	row.style.display = 'none';
}

function confirmRemoveGrant(rowIdx) {
   	if(confirm("Are you sure you want to remove this grant from the project?")) {
   		removeGrant(rowIdx);
   	}
}
	
function addFundingSource() {

	EDIT_CLICKED = false;
	
	var uniqIDs = new Array();
	// get the selected PI ID from the drop-down list
	var piList = document.getElementsByName("PI")[0];
	for (var i = 0; i < piList.length; i+=1) {
		if (piList[i].selected) {
			uniqIDs[piList[i].value] = 1;
			break;
		}
	}
	// If some grants have already been added, get the PI's of those grants
	var piIds = document.getElementsByName("PIID");
	for (var i = 0; i < piIds.length; i += 1) {
		uniqIDs[piIds[i].value] = 1;
	}
	
	var PIs = '';
	for (var id in uniqIDs)
		PIs += ','+id;
	
	if (PIs.length > 0)
		PIs = PIs.substring(1); // remove the first comma
		
	// get all the grantIDs already in the table
	var selectedGrants = getSelectedGrants();
	
	var winHeight = 500
	var winWidth = 700;
	var doc = "<yrcwww:link path='viewGrants.do?PIs='/>"+PIs+"&selectedGrants="+selectedGrants;
	//alert(doc);
	window.open(doc, "GRANT_WINDOW", "width=" + winWidth + ",height=" + winHeight + ",status=no,resizable=yes,scrollbars=yes");
}

var grantRowName = "grantrow";

function getSelectedGrants() {
	var grantRows = document.getElementsByName(grantRowName);
	var selectedGrants = "";
	for (var i = 0; i < grantRows.length; i+= 1) {
		var grantID = getGrantIdInRow(grantRows[i]);
		if (i > 0)
			selectedGrants += ",";
		selectedGrants += grantID;
	}
	return selectedGrants;
}

function hasGrant(grantID) {
	// get all the grantIDs already in the table
	var grantRows = document.getElementsByName("grantrow");
	for (var i = 0; i < grantRows.length; i+= 1) {
		var myID = getGrantIdInRow(grantRows[i]);
		if (myID == grantID)
			return true;
	}
	return false;
}

function editGrant(grantID) {
	EDIT_CLICKED = true;
	var winHeight = 500;
	var winWidth = 700;
	var doc = "<yrcwww:link path='editGrant.do?grantID='/>"+grantID;
	window.open(doc, "GRANT_WINDOW", "width=" + winWidth + ",height=" + winHeight + ",status=no,resizable=yes,scrollbars=yes");
}

function updateGrant(grantID, grantTitle, piID, PI, sourceType, sourceName, grantNumber, grantAmount) {
	// find a row with the given grantID
	var rows = getGrantsTable().rows;
	for (var i = 1; i < rows.length; i+=1) { // start from row 1; first row is the header
		var cells = rows[i].getElementsByTagName("td");
		
		if (getGrantIdInRow(rows[i]) == grantID) {
			// grant title
			updateValueInCell(cells[1], grantTitle);
			// grantPI
			updatePIColumn(cells[2], piID, PI);
			// funding source type
			updateValueInCell(cells[3], sourceType);
			// funding source name
			updateValueInCell(cells[4], sourceName);
			// grant number
			updateValueInCell(cells[5], grantNumber);
			// grant amount
			updateValueInCell(cells[6], grantAmount);
			
			break;
		}
	}
}
function getGrantIdInRow(row) {
	var cell = row.getElementsByTagName("td")[0];
	for (var i = 0; i < cell.childNodes.length; i++)
		if (cell.childNodes[i].value != null)
			return cell.childNodes[i].value;
}
function updateValueInCell(cell, val) {
	for (var i = 0; i < cell.childNodes.length; i++) {
		
		var child = cell.childNodes[i];
		if (child.value) {
			child.value = val;
		}
		if (child.id) {
			if (child.id.substring(0,2) == "gc") {
				child.innerHTML = val;
			}
		}
	}
}
function updatePIColumn(cell, piID, piLastName) {
	for (var i = 0; i < cell.childNodes.length; i++) {
		
		if (cell.childNodes[i].value != null) {
			cell.childNodes[i].value = piID;
		}
		if (cell.childNodes[i].href) {
			cell.childNodes[i].href = "<yrcwww:link path='viewResearcher.do?id='/>"+piID;
			cell.childNodes[i].firstChild.data = piLastName;
		}
	}
}
</script>