<%@page import="org.yeastrc.ms.domain.protinfer.ProteinUserValidation"%>
<%@page import="org.yeastrc.bio.go.GOUtils"%>
<%@ taglib uri="/WEB-INF/yrc-www.tld" prefix="yrcwww" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>

<script type="text/javascript">
	$(document).ready(function() {
		$("input[name='validationStatus'][value='All']").click(function() {
			$("input[name='validationStatus'][value!='All']").each(function() {
				this.checked = false;
			});
		});
		$("input[name='validationStatus'][value!='All']").click(function() {
			$("input[name='validationStatus'][value='All']").each(function() {
				this.checked = false;
			});
		});
		
		$("input[name='chargeStates'][value='All']").click(function() {
			$("input[name='chargeStates'][value!='All']").each(function() {
				this.checked = false;
			});
		});
		$("input[name='chargeStates'][value!='All']").click(function() {
			$("input[name='chargeStates'][value='All']").each(function() {
				this.checked = false;
			});
		});
	});
	
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
	var current = $("form#filterForm input[name='goTerms']").val();
	// If this terms in not already in the list add it.
	if(current.indexOf(goTerm) == -1) {
		var terms = current;
		if(current)
			terms = terms+","
		terms = terms+goTerm;
		$("form#filterForm input[name='goTerms']").val(terms);
	}
	else if(warn) {
		alert(goTerm+" has already been added");
	}
	$(".go_filter_add[id='"+goTerm+"']").hide();
	$(".go_filter_remove[id='"+goTerm+"']").show();
}

function removeFromGoTermFilters(goTerm, warn) {
	var current = $("form#filterForm input[name='goTerms']").val();
	// If this terms is in the list remove it
	var idx = current.indexOf(goTerm);
	if(idx != -1) {
		// get everything before the goTerm
		var term = current.substring(0,idx);
		if(term.charAt(term.length - 1) == ',') {
			term = term.substring(0,term.length-1);
		}
		// get everything after the goTerm
		term = term+current.substring(idx+goTerm.length);
		//alert(term);
		if(term.charAt(term.length - 1) == ',') {
			term = term.substring(0,term.length-1);
		}
		
		$("form#filterForm input[name='goTerms']").val(term);
	}
	else if(warn) {
		alert(goTerm+" was not found in the filter list");
	}
	$(".go_filter_add[id='"+goTerm+"']").show();
	$(".go_filter_remove[id='"+goTerm+"']").hide();
}
</script>

  <html:form action="/proteinProphetGateway" method="post" styleId="filterForm" >
  
  <html:hidden name="proteinProphetFilterForm" property="pinferId" />
  <html:hidden name="proteinProphetFilterForm" property="doDownload" />
  <html:hidden name="proteinProphetFilterForm" property="doGoSlimAnalysis" />
  <html:hidden name="proteinProphetFilterForm" property="getGoSlimTree" />
  <html:hidden name="proteinProphetFilterForm" property="doGoEnrichAnalysis" />
  <html:hidden name="proteinProphetFilterForm" property="goAspect" />
  <html:hidden name="proteinProphetFilterForm" property="goSlimTermId" />
  <html:hidden name="proteinProphetFilterForm" property="goEnrichmentPVal" />
  <html:hidden name="proteinProphetFilterForm" property="speciesId" />
  
  <TABLE CELLPADDING="5px" CELLSPACING="5px" align="center" style="border: 1px solid gray;" width="100%" style="max-width:1000;">
  
  <!-- Filtering options -->
  <tr>
  
  <td><table>
  <tr>
  <td>Peptides*: </td>
  <td>
  	<nobr>
  	Min <html:text name="proteinProphetFilterForm" property="minPeptides" size="3"></html:text>
  	Max <html:text name="proteinProphetFilterForm" property="maxPeptides" size="3"></html:text>
  	</nobr>
  </td>
  </tr>
  <tr>
  <td>Unique Peptides*: </td>
  <td>
  	<nobr>
  	Min <html:text name="proteinProphetFilterForm" property="minUniquePeptides" size="3"></html:text>
  	Max <html:text name="proteinProphetFilterForm" property="maxUniquePeptides" size="3"></html:text>
  	</nobr>
  </td>
  </tr>
  <tr>
  <td>Protein Mol. Wt.: </td>
  <td>
  	<nobr>
  	Min <html:text name="proteinProphetFilterForm" property="minMolecularWt" size="3"></html:text>
  	Max <html:text name="proteinProphetFilterForm" property="maxMolecularWt" size="3"></html:text>
  	</nobr>
  </td>
  </tr>
  <tr>
  	<td valign="bottom">ProteinProphet<br>Group Probability:</td>
  	<td valign="bottom">
  		<nobr>
  		Min <html:text name="proteinProphetFilterForm" property="minGroupProbability" size="3"></html:text>
  		Max <html:text name="proteinProphetFilterForm" property="maxGroupProbability" size="3"></html:text>
  		</nobr>
  	</td>
  </tr>
  <tr>
  		<td valign="bottom">Peptide<br>Probability:</td>
  		<td valign="bottom">
  		<nobr>Min: 
  		<html:text name="proteinProphetFilterForm" property="minPeptideProbability" size="3"></html:text>
  		</nobr>
  		<br/>
  		<span style="font-size:8pt;color:red;">NSP Adjusted Probability is used</span>
  	</td>
  </tr>
  </table></td>
  
  <td valign="top"><table>
  <tr>
  <td>Coverage(%):</td>
  <td>
  	<nobr>
  	Min <html:text name="proteinProphetFilterForm" property="minCoverage" size="3"></html:text>
  	Max <html:text name="proteinProphetFilterForm" property="maxCoverage" size="3"></html:text>
  	</nobr>
  </td>
  </tr>
  <tr>
  <td>Spectrum Matches: </td>
  <td>
  	<nobr>
  	Min <html:text name="proteinProphetFilterForm" property="minSpectrumMatches" size="3"></html:text>
  	Max <html:text name="proteinProphetFilterForm" property="maxSpectrumMatches" size="3"></html:text>
  	</nobr>
  </td>
  </tr>
   <tr>
  <td>Protein pI: </td>
  <td>
  	<nobr>
  	Min <html:text name="proteinProphetFilterForm" property="minPi" size="3"></html:text>
  	Max <html:text name="proteinProphetFilterForm" property="maxPi" size="3"></html:text>
  	</nobr>
  </td>
  </tr>
  <tr>
  	<td valign="bottom">ProteinProphet<br>Protein Probability:</td>
  	<td valign="bottom">
  		<nobr>
  		Min <html:text name="proteinProphetFilterForm" property="minProteinProbability" size="3"></html:text>
  		Max <html:text name="proteinProphetFilterForm" property="maxProteinProbability" size="3"></html:text>
  		</nobr>
  	</td>
  </tr>
  </table></td>
  
  <td valign="top" align="left">
  <table>
  <tr>
  	<td colspan="2" align="left">Display ProteinProphet Groups: </td>
  	<td align="left">
  		<html:radio name="proteinProphetFilterForm" property="joinProphetGroupProteins" value="true">Yes</html:radio>
  	</td>
  	<td align="left">
  		<html:radio name="proteinProphetFilterForm" property="joinProphetGroupProteins" value="false">No</html:radio>
  	</td>
  </tr>
  <tr>
  	<td colspan="4" align="left">
  		<html:checkbox name="proteinProphetFilterForm" property="excludeSubsumed" >Exclude Subsumed</html:checkbox>
  	</td>
  </tr>
  <tr>
  	<td colspan="4" align="left">
  		<html:checkbox name="proteinProphetFilterForm" property="excludeIndistinProteinGroups" /> Exclude Indistinguishable Groups
  	</td>
  </tr>
  </table></td>
  </tr>
  
  <tr>
  	<td colspan="2">
  		Validation Status: 
  		<html:multibox name="proteinProphetFilterForm" property="validationStatus" value="All"/> All
  		<html:multibox name="proteinProphetFilterForm" property="validationStatus" 
  					   value="<%=String.valueOf(ProteinUserValidation.UNVALIDATED.getStatusChar()) %>"/> Unvalidated 
  		<html:multibox name="proteinProphetFilterForm" property="validationStatus"  
  		               value="<%=String.valueOf(ProteinUserValidation.ACCEPTED.getStatusChar()) %>"/> Accepted
  		<html:multibox name="proteinProphetFilterForm" property="validationStatus"  
  		               value="<%=String.valueOf(ProteinUserValidation.REJECTED.getStatusChar()) %>"/> Rejected
  		<html:multibox name="proteinProphetFilterForm" property="validationStatus"  
  		               value="<%=String.valueOf(ProteinUserValidation.NOT_SURE.getStatusChar()) %>"/> Not Sure
  	</td>
  	
  </tr>
  
  <tr>
  	<td colspan="3">
  	<table>
  	<tr>
  	<td valign="top">Peptide: </td>
	<td valign="top">
		<html:text name="proteinProphetFilterForm" property="peptide" size="40"></html:text>
		<nobr><span style="font-size:8pt;">Exact Match:<html:checkbox name="proteinProphetFilterForm" property="exactPeptideMatch"></html:checkbox></span></nobr>
	</td>
  	
  	<td> Charge:</td>		
  	<td>
  		<nobr><html:multibox name="proteinProphetFilterForm" property="chargeStates" value="All"/> All</nobr> &nbsp;
  		<nobr><html:multibox name="proteinProphetFilterForm" property="chargeStates" value="1"/> +1</nobr> &nbsp;
  		<nobr><html:multibox name="proteinProphetFilterForm" property="chargeStates" value="2"/> +2</nobr> &nbsp;
  		<nobr><html:multibox name="proteinProphetFilterForm" property="chargeStates" value="3"/> +3</nobr> &nbsp;
  		<nobr><html:multibox name="proteinProphetFilterForm" property="chargeStates" value="4"/> +4</nobr> &nbsp;
  		<nobr><html:multibox name="proteinProphetFilterForm" property="chargeStates" value=">4"/> &gt; +4</nobr>
  		<br>
  		<span style="font-size:8pt;">Get proteins with peptides identified in at least one of the selected charge states</span>    
  	</td>
  </tr>
  
  <logic:present name="goSupported">
  		<tr>
  			<td valign="top">GO Terms: <br/><span class="clickable underline" style="color:red; font-weight:bold;" 
  			onclick="javascript:openGOTermSearcher();return false;">Search</span></td>
  			<td valign="top"><html:text name="proteinProphetFilterForm" property="goTerms" size="40"></html:text><br>
  				<span style="font-size:8pt;">Enter a comma-separated list of GO terms (e.g. GO:0006950)</span>
  			</td>
  			<td valign="top" colspan="2">
  				<html:checkbox name="proteinProphetFilterForm" property="matchAllGoTerms" title="Return proteins that match all terms">Match All </html:checkbox>
  				<html:checkbox name="proteinProphetFilterForm" property="exactGoAnnotation" title="Return proteins directly annotated with the GO terms">Exact </html:checkbox>
  				&nbsp;
  				<nobr>
  				Exclude: 
  				<html:checkbox name="proteinProphetFilterForm" property="excludeIea" title="Inferred from Electronic Annotation">IEA</html:checkbox>
  				<html:checkbox name="proteinProphetFilterForm" property="excludeNd" title="No Biological Data available">ND</html:checkbox>
  				<html:checkbox name="proteinProphetFilterForm" property="excludeCompAnalCodes" title="Computational Analysis Evidence Codes">ISS, ISO, ISA, ISM, IGC, RCA</html:checkbox>
  				</nobr>
  			</td>
  		</tr>
  </logic:present>
  		
  		
  		<tr>
  		
  			<td valign="top">Fasta ID(s): </td>
  			<td valign="top"><html:text name="proteinProphetFilterForm" property="accessionLike" size="40"></html:text><br>
  				<span style="font-size:8pt;">Enter a comma-separated list of FASTA accessions</span>
  			</td>
  			
  			<logic:present name="commonNameSupported">
  			<td valign="top">Common Names: </td>
  			<td valign="top"><html:text name="proteinProphetFilterForm" property="commonNameLike" size="40"></html:text><br>
  				<span style="font-size:8pt;">Enter a comma-separated list of common names</span>
  			</td>
  			</logic:present>
  		</tr>
  		
  		<tr>
  			<td valign="top">Description Include: </td>
  			<td valign="bottom"><html:text name="proteinProphetFilterForm" property="descriptionLike" size="40"></html:text>
  			</td>
  			<td valign="top">Exclude: </td>
  			<td valign="bottom">
  				<html:text name="proteinProphetFilterForm" property="descriptionNotLike" size="40"></html:text>
  				<nobr><span style="font-size:8pt;">Search All:<html:checkbox name="proteinProphetFilterForm" property="searchAllDescriptions"></html:checkbox></span></nobr>
  			</td>
  		</tr>
  		
  		<tr>
  		<td></td>
  		<td colspan="3" valign="top" align="left" style="font-size:8pt;padding:0px;margin:0px;">
  			Enter a comma-separated list of terms.
  			Descriptions will be included from the fasta file(s) associated with the experiment(s) <br>for
  			this protein inference as well as species specific databases (e.g. SGD) 
  			if a target species is associated with the experiment(s).
  			<br>Check "Search All" to include descriptions from Swiss-Prot and NCBI-NR. 
  			<br/><font color="red">NOTE: Description searches can be time consuming, especially when "Search All" is checked.</font>
  		</td>
  		</tr>
  </table>
  </td>
  
  <tr>
    	<td colspan="3" align="center">
    		<button class="plain_button" style="margin-top:2px;" 
    		        onclick="javascript:updateResults();return false;">Update</button>
    		<!--<html:submit styleClass="plain_button" style="margin-top:2px;">Update</html:submit>-->
    		
    		<!-- Download Results -->
    		&nbsp;
    		<a href="" onclick="javascript:downloadResults();return false;" ><b>Download Results</b></a> &nbsp; 
    	</td>
    	 
  </tr>
  
  
 </TABLE>


</html:form>
