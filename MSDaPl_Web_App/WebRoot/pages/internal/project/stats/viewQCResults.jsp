<%@ taglib uri="/WEB-INF/yrc-www.tld" prefix="yrcwww" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>

<yrcwww:notauthenticated>
 <logic:forward name="authenticate" />
</yrcwww:notauthenticated>


<%@ include file="/includes/header.jsp" %>

<%@ include file="/includes/errors.jsp" %>

<script>

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


function updatePsmRetTimeResults() {

	var qval = $("#psmrt_qval_input").val();
	var analysisId = <bean:write name="analysisId"/>
	$.blockUI();
	$("#psmrtplot").load("<yrcwww:link path='viewPsmVsRetTimeResults.do'/>", 	//url
						{'analysisId': analysisId, 		// data
						 'scoreCutoff': qval
						 },
						 function(responseText, status, xhr) {						// callback
						 	$.unblockUI();
							
	 });
}

function updateSpectraRetTimeResults() {

	var qval = $("#spectrart_qval_input").val();
	var analysisId = <bean:write name="analysisId"/>
	
	$.blockUI();
	$("#spectrartplot").load("<yrcwww:link path='viewSpectraVsRetTimeResults.do'/>", 	//url
						{'analysisId': analysisId, 		// data
						 'scoreCutoff': qval
						 },
						 function(responseText, status, xhr) {						// callback
						 	$.unblockUI();
							
	 });
}

function getPsmDeltaMassResults(usePpmMassDiff) {
	
	qval = 0.01;
	var analysisId = <bean:write name="analysisId"/>
	$.blockUI();
	$("#deltaMassPlot").load("<yrcwww:link path='viewPsmDeltaMassResults.do'/>", 	//url
						{'analysisId': analysisId, 		// data
						 'qvalue': qval,
						 'usePpmMassDiff': usePpmMassDiff
						 },
						 function(responseText, status, xhr) {						// callback
						 	$.unblockUI();
						 	//$("#rt_psm_fold").click();
						 	//$("#rt_spectra_fold").click();
							
	 });
}

function updatePsmDeltaMassResults() {

	var qval = $("#psm_delta_mass_qval_input").val();
	var massType = $("input[name='massType']:checked").val();
	var usePpm = false;
	if(massType=='ppm')
		usePpm = true;
	
	var analysisId = <bean:write name="analysisId"/>
	$.blockUI();
	$("#deltaMassPlot").load("<yrcwww:link path='viewPsmDeltaMassResults.do'/>", 	//url
						{'analysisId': analysisId, 		// data
						 'qvalue': qval,
						 'usePpmMassDiff': usePpm
						 },
						 function(responseText, status, xhr) {						// callback
						 	$.unblockUI();
						 	//$("#rt_psm_fold").click();
						 	//$("#rt_spectra_fold").click();
							
	 });
}

</script>
<yrcwww:contentbox title="Statistics" centered="true" width="95" widthRel="true">
<center>

	<div style="padding:10 7 10 7; margin-bottom:5; border: 1px dashed gray;background-color: #F0F0F0;">
		<a href="#psm_vs_rt">Retention Time vs Peptide Spectrum Matches</a>
		<br/><br/>
		<a href="#spectra_vs_rt">Retention Time vs MS/MS Spectra</a>
		<br/>
		
		<logic:present name="is_percolator">
			<br/>
			Delta precursor mass historgram:
			&nbsp;
			<span class="underline clickable" onclick="getPsmDeltaMassResults(false);return false;">[Da]</span>
			&nbsp;
			<span class="underline clickable" onclick="getPsmDeltaMassResults(true);return false;">[ppm]</span>
			<br/>
		</logic:present>
		
		<logic:present name="peptideTerminalAAResult">
			<br/>
			<a href="#peptide_termini_aa_result">Amino Acid Frequency at Peptide Termini</a>
			<br/>
		</logic:present>
	</div>
	
	
	<!-- #PSM vs RT plot -->
	<a name="psm_vs_rt"></a>
	<div id="psmrtplot">
	<%@ include file="psmVsRetTimePlots.jsp" %>
	</div>
	
	<!-- #Spectra vs RT plot -->
	<a name="spectra_vs_rt"></a>
	<div id="spectrartplot">
	<%@ include file="spectraVsRetTimePlots.jsp" %>
	</div>
	
	<!-- PSM Delta mass histogram -->
	<a name="psm_delta_mass"></a>
	<div id="deltaMassPlot"></div>
	
	<!-- Amino Acid Frequency at Peptide Termini -->
	<logic:present name="peptideTerminalAAResult">
		<a name="peptide_termini_aa_result"></a>
		<%@ include file="peptideTerminiAAResult.jsp" %>
	</logic:present>
	
</center>	
</yrcwww:contentbox>

<%@ include file="/includes/footer.jsp" %>