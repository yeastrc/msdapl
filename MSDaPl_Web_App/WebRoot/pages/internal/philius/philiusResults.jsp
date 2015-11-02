
<%@page import="javax.media.jai.operator.EncodeDescriptor"%>
<%@page import="java.awt.image.RenderedImage"%>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/yrc-www.tld" prefix="yrcwww" %>

<div id="dhtmltooltip" 
     style="position: absolute;width: 250px;border: 2px solid black;padding: 2px;background-color: lightyellow;visibility: hidden;z-index: 100;"></div>
<script type="text/javascript">

/***********************************************
* Cool DHTML tooltip script- © Dynamic Drive DHTML code library (www.dynamicdrive.com)
* This notice MUST stay intact for legal use
* Visit Dynamic Drive at http://www.dynamicdrive.com/ for full source code
***********************************************/

var offsetxpoint=-60 //Customize x offset of tooltip
var offsetypoint=20 //Customize y offset of tooltip
var ie=document.all
var ns6=document.getElementById && !document.all
var enabletip=false
if (ie||ns6)
	var tipobj=document.all? document.all["dhtmltooltip"] : document.getElementById? document.getElementById("dhtmltooltip") : ""

function ietruebody(){
	return (document.compatMode && document.compatMode!="BackCompat")? document.documentElement : document.body
}

function ddrivetip(thetext, thecolor, thewidth){
	if (ns6||ie){
		//if (typeof thewidth!="undefined") tipobj.style.width=thewidth+"px"
		//if (typeof thecolor!="undefined" && thecolor!="") tipobj.style.backgroundColor=thecolor
		tipobj.innerHTML=thetext
		//tipobj.style.backgroundColor='lightyellow'
		enabletip=true
		return false
	}
}

function positiontip(e){
	if (enabletip){
		var curX=(ns6)?e.pageX : event.clientX+ietruebody().scrollLeft;
		var curY=(ns6)?e.pageY : event.clientY+ietruebody().scrollTop;
		//Find out how close the mouse is to the corner of the window
		var rightedge=ie&&!window.opera? ietruebody().clientWidth-event.clientX-offsetxpoint : window.innerWidth-e.clientX-offsetxpoint-20
		var bottomedge=ie&&!window.opera? ietruebody().clientHeight-event.clientY-offsetypoint : window.innerHeight-e.clientY-offsetypoint-20

		var leftedge=(offsetxpoint<0)? offsetxpoint*(-1) : -1000

		//if the horizontal distance isn't enough to accomodate the width of the context menu
		if (rightedge<tipobj.offsetWidth)
		//move the horizontal position of the menu to the left by it's width
			tipobj.style.left=ie? ietruebody().scrollLeft+event.clientX-tipobj.offsetWidth+"px" : window.pageXOffset+e.clientX-tipobj.offsetWidth+"px"
			else if (curX<leftedge)
			tipobj.style.left="5px"
		else
		//position the horizontal position of the menu where the mouse is positioned
			tipobj.style.left=curX+offsetxpoint+"px"

		//same concept with the vertical position
		if (bottomedge<tipobj.offsetHeight)
			tipobj.style.top=ie? ietruebody().scrollTop+event.clientY-tipobj.offsetHeight-offsetypoint+"px" : window.pageYOffset+e.clientY-tipobj.offsetHeight-offsetypoint+"px"
		else
			tipobj.style.top=curY+offsetypoint+"px"
		tipobj.style.visibility="visible"
	}
}

function hideddrivetip(){
	if (ns6||ie){
		enabletip=false
		tipobj.style.visibility="hidden"
		tipobj.style.left="-1000px"
		//tipobj.style.backgroundColor=''
		//tipobj.style.width=''
	}
}

document.onmousemove=positiontip
</script>

<script type="text/javascript">
function viewImageMap() {
	$("#philius_image").show();
	$("#philius_image_show_link").hide();
}
function hideImageMap() {
	$("#philius_image").hide();
	$("#philius_image_show_link").show();
}

function viewPredictedSegments() {
	$("#philius_segments").show();
	$("#philius_segments_show_link").hide();
}
function hidePredictedSegments() {
	$("#philius_segments").hide();
	$("#philius_segments_show_link").show();
}
</script>


<style type="text/css">
	SPAN.philius_sp {
		background-color: #B90000;
		color: #FFFFFF;
		padding-bottom:4px;
	}
	SPAN.philius_nc {
		background-color: #009B00;
		color: #FFFFFF;
		padding-bottom:4px;
	}
	SPAN.philius_c {
		background-color: #0000B9;
		color: #FFFFFF;
		padding-bottom:4px;
	}
	SPAN.philius_tm {
		background-color: #FFFF00;
		color: #000000;
		padding-bottom:4px;
	}
	DIV.philius_box {
		background-color:#F8F8FF;
		border: 1px solid #E5E5E5; 
		margin-top:10px;
		padding:5px;
	}
</style>

<!--  PREDICTION -->
<table align="center" style="border: 1px solid #E5E5E5; border-spacing: 2px;background-color:#F8F8FF;">
<tr><td><b>Philius Prediction:</b></td><td><bean:write name="philiusAnnotation" property="result.annotation" /></td></tr>
<tr><td><b>Confidence:</b></td><td><bean:write name="philiusAnnotation" property="result.typeScore" /></td></tr>
</table>
<br/>
<!-- SEQUENCE -->
<pre><bean:write name="sequenceHtml" filter="false"/></pre>
<br/>

<bean:size id="segmentCount" name="philiusAnnotation" property="result.segments" />

<!-- LEGEND -->
<logic:greaterThan name="segmentCount" value="0">
<b>Legend: </b>
<span class="philius_tm">Transmembrane Helix</span>,
<span class = "philius_nc">Non-Cytoplasmic</span>,
<span class="philius_c">Cytoplasmic</span>,
<span class="philius_sp">Signal Peptide</span>
<br/>
</logic:greaterThan>

<!--  IMAGE MAP -->
<logic:greaterThan name="segmentCount" value="0">
<logic:notEmpty name="philiusmap" scope="request">
<bean:write name="philiusmap" scope="request" filter="false" />

<div id="philius_image_show_link" 
	  class="clickable underline philius_box" 
	  onclick="viewImageMap()"
	  style="font-weight:bold;"
	  align="center">[View Philius Prediction Image]</div>

<div style="display:none;" id="philius_image" align="center" class="philius_box">
	<!-- The 'philiusToken' is added as a hack to prevent caching of this image -->
	<img SRC='<yrcwww:link path="viewPhiliusGraphic.do?" /><bean:write name="philiusToken"/>' usemap="#philiusMap" border="0" style="margin-bottom:10px;"/>
	<br/>
	<img src='<yrcwww:link path="images/philius/philiusLegend.jpg"/>' alt="Philius confidence legend" style="margin-bottom:10px;"/>
	<br/>
	<span style="font-size:9px;font-weight:bold;" class="clickable underline" onclick="hideImageMap()">[Hide Image]</span>
</div>
</logic:notEmpty>
</logic:greaterThan>

<!-- SEGMENT DETAILS -->
<logic:greaterThan name="segmentCount" value="0">
	<div id="philius_segments_show_link" 
	  class="clickable underline philius_box" 
	  onclick="viewPredictedSegments()"
	  style="font-weight:bold;"
	  align="center">[View Predicted Segments]</div>
<div style="display:none;" id="philius_segments" align="center" class="philius_box">
<table style="border:1px dotted #CCCCCC;">
<thead><tr><th>Type</th><th>Start</th><th>End</th><th>Confidence</th></tr></tr></thead>
<tbody>
<logic:iterate name="philiusAnnotation" property="result.segments" id="segment">
	<tr>
	<td style="border:1px dotted #CCCCCC;"><bean:write name="segment" property="type.longName"/></td>
	<td style="border:1px dotted #CCCCCC;"><bean:write name="segment" property="start"/></td>
	<td style="border:1px dotted #CCCCCC;"><bean:write name="segment" property="end"/></td>
	<td style="border:1px dotted #CCCCCC;"><bean:write name="segment" property="confidence"/></td>
	</tr>
</logic:iterate>
</tbody>
</table>
<br/>
<span style="font-size:9px;font-weight:bold;" class="clickable underline" onclick="hidePredictedSegments()">[Hide Segments]</span>
</div>  

</logic:greaterThan>


