<%@ taglib uri="/WEB-INF/yrc-www.tld" prefix="yrcwww" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>


<%@ include file="/includes/header.jsp" %>

<%@ include file="/includes/errors.jsp" %>

<script type="text/javascript" src="<yrcwww:link path='js/wz_jsgraphics.js'/>"></script>
<script type="text/javascript" src="<yrcwww:link path='js/jquery.ui-1.6rc2/ui/ui.core.js'/>"></script>
<script type="text/javascript" src="<yrcwww:link path='js/jquery.ui-1.6rc2/ui/ui.slider.js'/>"></script>

<logic:present name="alignedProteins">

<center>
<div style="font-weight:bold;">Protein Inference ID: <bean:write name="pinferId" /></div>
<logic:present name="clusterId">
<div style="font-weight:bold;">Protein Cluster ID: <bean:write name="clusterId" /></div>
</logic:present>
<logic:present name="groupId">
<div style="font-weight:bold;">Protein Group ID: <bean:write name="groupId" /></div>
</logic:present>


<script type="text/javascript">

$(document).ready(function() {
	var align_graphic = new jsGraphics("alignment_graphic");
	//var align_text    = new jsGraphics("alignment_text");
	
    myDrawFunction(align_graphic);
    
    $("#content-slider").slider({
    	animate: true,
    	handle: ".content-slider-handle",
    	change: handleSliderChange,
    	slide: handleSliderSlide
  	});
});

function handleSliderChange(e, ui)
{
  var maxScroll = $("#content-scroll").attr("scrollWidth") -
                  $("#content-scroll").width();
  $("#content-scroll").animate({scrollLeft: ui.value *
     (maxScroll / 100) }, 1000);
}

function handleSliderSlide(e, ui)
{
  var maxScroll = $("#content-scroll").attr("scrollWidth") -
                  $("#content-scroll").width();
  $("#content-scroll").attr({scrollLeft: ui.value * (maxScroll / 100) });
}


function myDrawFunction(jg)
{
	var totalLength = <bean:write name="alignedProteins" property="anchorProtein.alignedLength"/>
	var labelOffset = 70;
	var canvasWidth = 800 - labelOffset;
	var num_markers = 10;
	var increment = 10;
	var first = totalLength / num_markers;
	var low;
	var high;
	if(totalLength <= 100) {
		low = 10; high = 10;
	}
	else if(totalLength <= 1000) {
		low = Math.floor(totalLength / (5.0 * num_markers))*5;
		high = Math.ceil(totalLength / (5.0 * num_markers))*5;
	}
	else if(totalLength <= 10000) {
		low = Math.floor(totalLength / (50.0 * num_markers))*50;
		high = Math.ceil(totalLength / (50.0 * num_markers))*50;
	}
	else {
		low = Math.floor(totalLength / (500.0 * num_markers))*500;
		high = Math.ceil(totalLength / (500.0 * num_markers))*500;
	}
	increment = first - low < high - first ? low : high;
	
	//-------------------------------------------------------------
	// horizontal scale line
	jg.setColor("#000000");
	jg.setStroke(2);
	jg.fillRect(labelOffset, 20, canvasWidth, 2);
	//jg.drawLine(labelOffset,20,800,20);
	
	// first marker
	jg.setStroke(1);
	jg.drawLine(labelOffset,15,labelOffset,20);
	jg.drawString("<span style='font-size:6pt;'>1</span>", labelOffset-2,2);
	var marker = 0;
	while(true) {
		marker += increment;
		if(marker > totalLength) {
			jg.drawLine(800,15, 800,20);
			jg.drawString("<span style='font-size:6pt;'>"+totalLength+"</span>", 800-5,2);
			break;
		}
		var x = marker * (canvasWidth / totalLength) + labelOffset;
		jg.drawLine(x,15,x,20);
		jg.drawString("<span style='font-size:6pt;'>"+marker+"</span>", x-2,2);
	}
	
	//-------------------------------------------------------------
	// now draw the proteins
	// first the anchor protein
	var y = 40;
	var h = 10;
	var nogap_col = "#BBBBBB";
	var cov_col   = "#6495ED";
	var mism_col = "DC143C";
	
	jg.drawStringRect("<span style='font-size:8pt; color:#2F4F4F'><bean:write name='alignedProteins' property='anchorProtein.accession' /></span>", 0, y, 60);
	var x;
	var x2;
	jg.setColor(nogap_col);
	<logic:iterate name="alignedProteins" property="anchorProtein.ungappedBlocks" id="blk">
		x = <bean:write name="blk" property="start" /> * (canvasWidth / totalLength) + labelOffset;
		x2 = <bean:write name="blk" property="end" /> * (canvasWidth / totalLength) + labelOffset;
		jg.fillRect(x,y,(x2 - x),15);
	</logic:iterate>
	jg.setColor(cov_col);
	<logic:iterate name="alignedProteins" property="anchorProtein.coveredBlocks" id="blk">
		x = <bean:write name="blk" property="start" /> * (canvasWidth / totalLength) + labelOffset;
		x2 = <bean:write name="blk" property="end" /> * (canvasWidth / totalLength) + labelOffset;
		jg.fillRect(x,y,(x2 - x),15);
	</logic:iterate>
	jg.setColor(mism_col);
	<logic:iterate name="alignedProteins" property="anchorProtein.mismatchedBlocks" id="blk">
		x = <bean:write name="blk" property="start" /> * (canvasWidth / totalLength) + labelOffset;
		x2 = <bean:write name="blk" property="end" /> * (canvasWidth / totalLength) + labelOffset;
		jg.fillRect(x,y,(x2 - x),15);
	</logic:iterate>
	
	<logic:iterate name="alignedProteins" property="alignedProteins" id="protein">
		y = y + 20;
		jg.drawStringRect("<span style='font-size:8pt; color:#2F4F4F'><bean:write name='protein' property='accession' /></span>", 0, y, 60);
		
		jg.setColor(nogap_col);
		<logic:iterate name="protein" property="ungappedBlocks" id="blk">
			x = <bean:write name="blk" property="start" /> * (canvasWidth / totalLength) + labelOffset;
			x2 = <bean:write name="blk" property="end" /> * (canvasWidth / totalLength) + labelOffset;
			jg.fillRect(x,y,(x2 - x),15);
		</logic:iterate>
		
		jg.setColor(cov_col);
		<logic:iterate name="protein" property="coveredBlocks" id="blk">
			x = <bean:write name="blk" property="start" /> * (canvasWidth / totalLength) + labelOffset;
			x2 = <bean:write name="blk" property="end" /> * (canvasWidth / totalLength) + labelOffset;
			jg.fillRect(x,y,(x2 - x),15);
		</logic:iterate>
		
		jg.setColor(mism_col);
		<logic:iterate name="protein" property="mismatchedBlocks" id="blk">
			x = <bean:write name="blk" property="start" /> * (canvasWidth / totalLength) + labelOffset;
			x2 = <bean:write name="blk" property="end" /> * (canvasWidth / totalLength) + labelOffset;
			jg.fillRect(x,y,(x2 - x),15);
		</logic:iterate>
	
	</logic:iterate>
		
	
  //jg.setColor("#000000"); 
  //jg.drawStringRect("<b>Protein1</b>", 0,10,60);
  //jg.drawRect(70,10,200,20);
  //jg.setColor("#0000ff"); 
  //jg.fillRect(270,10,100,21);
  //jg.setColor("#000000"); 
  //jg.drawRect(370,10,350,20);
  
  //jg.drawStringRect("<b>Protein2</b>", 0,40,60);
  //jg.drawRect(100,40,200,20);
  //jg.setColor("#0000ff"); 
  //jg.fillRect(270,40,100,21);
  //jg.setColor("#000000"); 
  //jg.drawRect(370,40,500,20);
  
  
  jg.paint();
}

</script>


<div id="main" style="width:800px; margin:0 auto;" >

<div id="content-scroll" style="width: 850px; height: 300px; margin-top: 10px; overflow: hidden; border: solid 1px black;">
<div id="content-holder" style="position:relative;height:300px;width:1800px; background-color: #EFEFEF;">

<div id="alignment_graphic" style="position:relative;height:300px;width:1800px; background-color: #EFEFEF; float: left;">


</div>

</div>
</div>


<div id="content-slider" style="width: 850px;height: 6px; margin-top: 10px; margin-bottom:20px; background: #AAAAAA; position: relative;">
	<div class="content-slider-handle" style="width: 8px; height: 14px; position: absolute; top: -4px; background: #478AFF;border: solid 1px black;"></div>
</div>

</div>

</center>
</logic:present>

<%@ include file="/includes/footer.jsp" %>