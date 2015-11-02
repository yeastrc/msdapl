
<%@ taglib uri="/WEB-INF/yrc-www.tld" prefix="yrcwww" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>


 
<%@ include file="/includes/header.jsp" %>

<%@ include file="/includes/errors.jsp" %>


<script src="<yrcwww:link path='/js/dragtable.js'/>"></script>

<script type="text/javascript" src="<yrcwww:link path='/js/jquery.ui-1.6rc2/ui/ui.core.js'/>"></script>
<script type="text/javascript" src="<yrcwww:link path='/js/jquery.ui-1.6rc2/ui/ui.tabs.js'/>"></script>
<script type="text/javascript" src="<yrcwww:link path='/js/jquery.ui-1.6rc2/ui/ui.dialog.js'/>"></script>
<script type="text/javascript" src="<yrcwww:link path='/js/jquery.ui-1.6rc2/ui/ui.draggable.js'/>"></script>
<script type="text/javascript" src="<yrcwww:link path='/js/jquery.ui-1.6rc2/ui/ui.resizable.js'/>"></script>

<script type="text/javascript" src="<yrcwww:link path='/js/jquery.history.js'/>"></script>
<script type="text/javascript" src="<yrcwww:link path='/js/jquery.cookie.js'/>"></script>



<script src="<yrcwww:link path='/js/jquery.form.js'/>"></script>


<link rel="stylesheet" href="<yrcwww:link path='/css/proteinfer.css'/>" type="text/css" >

<yrcwww:notauthenticated>
 <logic:forward name="authenticate" />
</yrcwww:notauthenticated>

<logic:notPresent name="proteinInferFilterForm">
	<logic:forward  name="viewProteinInferenceResult" />
</logic:notPresent>

<script>

$(document).ready(function() {
   $(".sortable_table").each(function() {
   		var $table = $(this);
   		makeSortableTable($table);
   });
});

// ---------------------------------------------------------------------------------------
// GENE ONTOLOGY ENRICHMENT
// ---------------------------------------------------------------------------------------
function doGoEnrichmentAnalysis() {

	// validate form
	if(!validateForm())
    	return false;
	
	$("form#filterForm input[name='doDownload']").val("false");
    $("form#filterForm input[name='doGoEnrichment']").val("true");
    
	$("form#filterForm").submit();
	
}

// ---------------------------------------------------------------------------------------
// FORM VALIDATION
// ---------------------------------------------------------------------------------------
function validateForm() {

	// fieldValue is a Form Plugin method that can be invoked to find the 
    // current value of a field 
    
    // validate pvalue cutoff for enrichment calculation
	var value = $('form#filterForm input[name=goEnrichmentPVal]').fieldValue();
    var valid = validateFloat(value, "P-Value", 0.0, 1.0);
    if(!valid)	return false;
	
	
    var value = $("form#filterForm input[name='minPeptides']").fieldValue();
    var valid = validateInt(value, "Min. Peptides", 1);
    if(!valid)	return false;
    var minPept = parseInt(value);
    $('form#filterForm input[name=minPeptides]').val(minPept);
    
    value = $('form#filterForm input[name=minUniquePeptides]').fieldValue();
    valid = validateInt(value, "Min. Unique Peptides", 0, minPept);
    if(!valid)	return false;
    $('form#filterForm input[name=minUniquePeptides]').val(parseInt(value));
    
    value = $('form#filterForm input[name=minCoverage]').fieldValue();
    valid = validateFloat(value, "Min. Coverage", 0.0, 100.0);
    if(!valid)	return false;
    
    value = $('form#filterForm input[name=minSpectrumMatches]').fieldValue();
    valid = validateInt(value, "Min. Spectrum Matches", 1);
    if(!valid)	return false;
    $('form#filterForm input[name=minSpectrumMatches]').val(parseInt(value));
    
    value = $('form#filterForm input[name=minMolecularWt]').fieldValue();
    valid = validateFloat(value, "Min. Molecular Wt.", 0);
    if(!valid)	return false;
    $('form#filterForm input[name=minMolecularWt]').val(parseInt(value));
    
    
    return true;
}
function validateInt(value, fieldName, min, max) {
	var intVal = parseInt(value);
	var valid = true;
	if(isNaN(intVal))						valid = false;
	if(valid && intVal < min)				valid = false;
	if(max && (valid && intVal > max))		valid = false;
	
	if(!valid) {
		if(max) alert("Value for "+fieldName+" should be between "+min+" and "+max);
		else	alert("Value for "+fieldName+" should be >= "+min);
	}
	return valid;
}
function validateFloat(value, fieldName, min, max) {
	var floatVal = parseFloat(value);
	var valid = true;
	if(isNaN(floatVal))						valid = false;
	if(valid && floatVal < min)			valid = false;
	if(max && (valid && floatVal > max))	valid = false;
	if(!valid) {
		if(max) alert("Value for "+fieldName+" should be between "+min+" and "+max);
		else	alert("Value for "+fieldName+" should be >= "+min);
	}
	return valid;
}

</script>

<yrcwww:contentbox title="GO Enrichment" centered="true" width="90" widthRel="true">

<div style="padding:0 7 0 7; margin-bottom:5; border: 1px dashed gray;background-color: #F0F8FF;">
	<table align="center">
		<tr>
			<td><b>Protein Inference ID: </b></td>
			<td>
				<html:link action="viewProteinInferenceResult.do" paramId="pinferId" paramName="pinferId"><bean:write name="pinferId" /></html:link>
			</td>
		</tr>
		<tr>
			<td><b>Species: </b></td>
			<td>
				<a target="ncbi_window" href="http://www.ncbi.nlm.nih.gov/Taxonomy/Browser/wwwtax.cgi?mode=Info&id=<bean:write name="species" property="id"/>">
					<bean:write name="species" property="name" />
				</a>
			</td>
		</tr>
		<tr>
			<td><b># Proteins (input): </b></td><td><bean:write name="enrichment" property="numInputProteins" /></td>
		</tr>
		<tr>
			<td><b># Proteins (for given species): </b></td><td><bean:write name="enrichment" property="numSpeciesProteins" /></td>
		</tr>
	</table>
</div>

<%@include file="proteinInferFilterForm.jsp" %>

<!-- BIOLOGICAL PROCESS -->
<logic:present name="bioProcessTerms">
<yrcwww:contentbox title="Biological Process" centered="true" width="90" widthRel="true">
	<div align="center">
	<b># Enriched Terms (Biological Process):<bean:write name="bioProcessTerms" property="enrichedTermCount" /></b>
	</div>
	<yrcwww:table name="bioProcessTerms" tableId='bioProc_terms' tableClass="table_basic sortable_table stripe_table" center="true" />
</yrcwww:contentbox>
<br>
</logic:present>

<!-- CELLULAR COMPONENT -->
<logic:present name="cellComponentTerms">
<yrcwww:contentbox title="Cellular Component" centered="true" width="90" widthRel="true">
	<div align="center">
	<b># Enriched Terms (Cellular Component):<bean:write name="cellComponentTerms" property="enrichedTermCount" /></b>
	</div>
	<yrcwww:table name="cellComponentTerms" tableId='cellComp_terms' tableClass="table_basic sortable_table stripe_table" center="true" />
</yrcwww:contentbox>
<br>
</logic:present>

<!-- MOLECULAR FUNCTION -->
<logic:present name="molFunctionTerms" >
<yrcwww:contentbox title="Molecular Function" centered="true" width="90" widthRel="true">
	<div align="center">
	<b># Enriched Terms (Molecular Function):<bean:write name="molFunctionTerms" property="enrichedTermCount" /></b>
	</div>
	<yrcwww:table name="molFunctionTerms" tableId='molFunc_terms' tableClass="table_basic sortable_table stripe_table" center="true" />
</yrcwww:contentbox>
<br>
</logic:present>

</yrcwww:contentbox>

<%@ include file="/includes/footer.jsp" %>