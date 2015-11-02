<%@ taglib uri="/WEB-INF/yrc-www.tld" prefix="yrcwww" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>

<html>

<head>
 <yrcwww:title />
 <link REL="stylesheet" TYPE="text/css" HREF="<yrcwww:link path='/css/global.css' />">
</head>

<body>

<%@ include file="/includes/errors.jsp" %>

<script src="<yrcwww:link path='js/jquery-1.4.2.js'/>"></script>
<script src="<yrcwww:link path='js/jquery.blockUI.js'/>"></script>
<script>

var fontsize = 2;
$(document).ready(function() {
	$('.plotLink').click(function() { 
	
		var url = $(this).attr('href');
		$("#img").html('<img src="'+url+'"/>');
		
		var proteinName = $(this).attr('id');
		$("#proteinName").html('<b>'+proteinName+'</b>');
		
        $.blockUI({ 
            message: $("#imgdiv"),
            css: {cursor:'default',
            	  top:  ($(window).height() - 350) /2 + 'px', 
                  left: ($(window).width() - 500) /2 + 'px',
                  width: '500px',
                  }
        }); 
 		return false;
    });
     
    $('#close').click(function() { 
            $.unblockUI(); 
            return false; 
    });
});

function setFont(size) {
	$("td.rowname").each(function() {
		//alert("changing font size");
		$(this).css("fontSize",size); 
	});
}

function increaseFont() {
	//alert("Increasing font");
	if(fontsize < 10) 
		fontsize++;
	setFont(fontsize);
}

function decreaseFont() {
	//alert("decreasing font");
	if(fontsize > 2) 
		fontsize--;
	setFont(fontsize);
}

function updatePage(rowIndex) {
	window.opener.goToHeatMapIndex(rowIndex);
}

function toggleNames() {
	var label = $("#allNamesTrigger").text();
	if(label == "Show ALL Indistinguishable Protein Names") {
		$(".allNames").show();
		$(".oneName").hide();
		$("#allNamesTrigger").text("Show ONE Indistinguishable Protein Names");
	}
	else if(label == "Show ONE Indistinguishable Protein Names") {
		$(".oneName").show();
		$(".allNames").hide();
		$("#allNamesTrigger").text("Show ALL Indistinguishable Protein Names");
	}
}

</script>

<!-- Div to modal dialog with plot -->
<div id="imgdiv" style="display:none">
<div style="width:100%;" align="right"><img src="<yrcwww:link path='images/proteinfer/dialog-titlebar-close.png'/>"  id="close"/></div>
<div id="proteinName"></div>
<div id="img" style="margin:10px;"></div>
<div>Note: Normalized spectrum counts are used for clustering</div>
</div>

<!-- RESULTS TABLE -->
<div style="margin:10 5 10 5;"> 

<div align="center">Molecular Weight</div>
<table width="60%" align="center" style="border: 1px dashed gray;">
<tr>
<td width="2%" class="small_font" style="background-color:rgb(255,255,0);">&nbsp;&nbsp;</td><td class="small_font">0 to 12000</td>
<td width="2%" style="background-color:rgb(255,180,0);">&nbsp;&nbsp;</td><td class="small_font">12000 to 18000</td>
<td width="2%" style="background-color:rgb(255,0,0);">&nbsp;&nbsp;</td><td class="small_font">18000 to 22000</td>
</tr>
<tr>
<td width="2%" style="background-color:rgb(255,0,180);">&nbsp;&nbsp;</td><td class="small_font">22000 to 25000</td>
<td width="2%" style="background-color:rgb(255,0,255);">&nbsp;&nbsp;</td><td class="small_font">25000 to 35000</td>
<td width="2%" style="background-color:rgb(180,0,255);">&nbsp;&nbsp;</td><td class="small_font">35000 to 40000</td>
</tr>
<tr>
<td width="2%" style="background-color:rgb(0,0,255);">&nbsp;&nbsp;</td><td class="small_font">40000 to 50000</td>
<td width="2%" style="background-color:rgb(0,180,255);">&nbsp;&nbsp;</td><td class="small_font">50000 to 60000</td>
<td width="2%" style="background-color:rgb(0,255,255);">&nbsp;&nbsp;</td><td class="small_font">60000 to 70000</td>
</tr>
<tr>
<td width="2%" style="background-color:rgb(0,255,180);">&nbsp;&nbsp;</td><td class="small_font">70000 to 80000</td>
<td width="2%" style="background-color:rgb(0,255,0);">&nbsp;&nbsp;</td><td class="small_font">80000 to 90000</td>
<td width="2%" style="background-color:rgb(0,0,0);">&nbsp;&nbsp;</td><td class="small_font">90000 to 100000</td>
</tr>
</table>


<logic:present name="heatmap">

	<table width="90%" cellspacing="0" cellpadding="10" align="center">
	<tr>
	
	<logic:present name="hasGroups">
	<td align="left" style="color: #3D4960;">
	<span class="clickable" style="color:red; font-weight:bold;" 
	id="allNamesTrigger" onclick="javascript:toggleNames();return false;">Show ALL Indistinguishable Protein Names</span>
	</td>
	</logic:present>
	
	<td align="right" style="color: #3D4960;">
	<b>Font: &nbsp;<span class="clickable" 
				style="background: white; border: 1px solid #CBCBCB; padding:3 3 3 3;" onclick="increaseFont();">+</span> 
				&nbsp;
				<span class="clickable" 
				style="background:white; border: 1px solid #CBCBCB; padding:3 4 3 4;" onclick="decreaseFont()">-</span></b>
	</td>
	</tr>
	</table>
	
	<center>
	<div style="border: 1px solid; width:90%; padding: 3 3 3 3; color: #3D4960;">
	<span>Click on the heatmap to navigate to the relevant page in the comparison view.</span><br/><br/>
	<table width="100%" cellspacing="0" cellpadding="0" align="center" >
	<tr>
		<th width="2%" class="header">Protein</th>
		<th width="1%" class="header"></th>
		<th width="2%" class="header"></th>
		<logic:iterate name="heatmap" property="datasetLabels" id="datasetLabel">
			<th class="header"><bean:write name="datasetLabel"/></th>
		</logic:iterate>
	</tr>
	
	<logic:iterate name="heatmap" property="rows" id="row">
	<tr>
		<td class="rowname" style="font-size:2">
			<span class="oneName"><bean:write name="row" property="rowName" /></span>
			<logic:present name="hasGroups">
			<div class="allNames" style="display:none;">
				<logic:iterate name="row" property="rowAllNames" id="name">
					<bean:write name="name"/> <br/>
				</logic:iterate>
			</div>
			</logic:present>
		</td>
		<td class="rowname" style="font-size:2">
			<a href='<bean:write name='row' property='plotUrl'/>' class="plotLink" 
			   id='<bean:write name="row" property="rowName" />'>Plot</a>
		</td>
		<logic:iterate name="row" property="cells" id="cell">
			<td style="font-size:0pt; background-color:<bean:write name='cell' property='hexColor' />;" 
				class="clickable"
			    onclick="updatePage(<bean:write name='row' property='indexInList' />)">
			    &nbsp;
			</td>
		</logic:iterate>
	</tr>
	</logic:iterate>
	
	</table>
	</div>
	</center>
</logic:present>
</div>

</body>
</html>




