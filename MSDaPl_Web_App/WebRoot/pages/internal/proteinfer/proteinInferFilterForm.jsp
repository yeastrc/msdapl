
<%@page import="org.yeastrc.ms.domain.protinfer.ProteinUserValidation"%>
<%@page import="org.yeastrc.bio.go.GOUtils"%>
<%@page import="org.yeastrc.www.proteinfer.idpicker.DisplayColumns"%>
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

function saveDisplayColumnsCookie() {
	//alert("saving cookie");
	var cookieVal = "";
	$(".colChooser").each(function() {
		if($(this).is(":checked")) {}
		else {cookieVal += "_"+$(this).next("span").attr('id')};
	});
	
	if(cookieVal.length > 0) {
		cookieVal = cookieVal.substring(1);
		//alert(cookieVal);
		var COOKIE_NAME = 'noDispCols_protinfer';
		var options = { path: '/', expires: 100 };
    	$.cookie(COOKIE_NAME, cookieVal, options);
    }
	
	return false;
}

</script>

  <html:form action="/proteinInferGateway" method="post" styleId="filterForm" >
  
  <html:hidden name="proteinInferFilterForm" property="pinferId" />
  <html:hidden name="proteinInferFilterForm" property="doDownload" />
  <html:hidden name="proteinInferFilterForm" property="doGoSlimAnalysis" />
  <html:hidden name="proteinInferFilterForm" property="getGoSlimTree" />
  <html:hidden name="proteinInferFilterForm" property="doGoEnrichAnalysis" />
  <html:hidden name="proteinInferFilterForm" property="goAspect" />
  <html:hidden name="proteinInferFilterForm" property="goSlimTermId" />
  <html:hidden name="proteinInferFilterForm" property="goEnrichmentPVal" />
  <html:hidden name="proteinInferFilterForm" property="exactAnnotations" value="true"/>
  <html:hidden name="proteinInferFilterForm" property="applyMultiTestCorrection" value="true"/>
  <html:hidden name="proteinInferFilterForm" property="speciesId" />
  
  
  <TABLE CELLPADDING="5px" CELLSPACING="5px" align="center" style="border: 1px solid gray;" width="100%" style="max-width:1000;">
  
  <!-- Filtering options -->
  <tr>
  
  <td><table>
  <tr>
  <td>Peptides: </td>
  <td>
  	<nobr>
  	Min <html:text name="proteinInferFilterForm" property="minPeptides" size="3"></html:text>
  	Max <html:text name="proteinInferFilterForm" property="maxPeptides" size="3"></html:text>
  	</nobr>
  </td>
  </tr>
  <tr>
  <td>Unique Peptides: </td>
  <td>
  	<nobr>
  	Min <html:text name="proteinInferFilterForm" property="minUniquePeptides" size="3"></html:text>
  	Max <html:text name="proteinInferFilterForm" property="maxUniquePeptides" size="3"></html:text>
  	</nobr>
  </td>
  </tr>
  <tr>
  <td>Protein Mol. Wt.: </td>
  <td>
  	<nobr>
  	Min <html:text name="proteinInferFilterForm" property="minMolecularWt" size="3"></html:text>
  	Max <html:text name="proteinInferFilterForm" property="maxMolecularWt" size="3"></html:text>
  	</nobr>
  </td>
  </tr>
  </table></td>
  
  <td><table>
  <tr>
  <td>Coverage(%):</td>
  <td>
  	<nobr>
  	Min <html:text name="proteinInferFilterForm" property="minCoverage" size="3"></html:text>
  	Max <html:text name="proteinInferFilterForm" property="maxCoverage" size="3"></html:text>
  	</nobr>
  </td>
  </tr>
  <tr>
  <td>Spectrum Matches: </td>
  <td>
  	<nobr>
  	Min <html:text name="proteinInferFilterForm" property="minSpectrumMatches" size="3"></html:text>
  	Max <html:text name="proteinInferFilterForm" property="maxSpectrumMatches" size="3"></html:text>
  	</nobr>
  </td>
  </tr>
  <tr>
  <td>Protein pI: </td>
  <td>
  	<nobr>
  	Min <html:text name="proteinInferFilterForm" property="minPi" size="3"></html:text>
  	Max <html:text name="proteinInferFilterForm" property="maxPi" size="3"></html:text>
  	</nobr>
  </td>
  </tr>
  </table></td>
  
  <td valign="top"><table>
  
  <logic:notPresent name="goView">
  <tr>
  	<td colspan="3">Group Indistinguishable Proteins: </td>
  	<td>
  		<nobr>
  		<html:radio name="proteinInferFilterForm" property="joinGroupProteins" value="true">Yes</html:radio>
  		</nobr>
  		<nobr>
  		<html:radio name="proteinInferFilterForm" property="joinGroupProteins" value="false">No</html:radio>
  		</nobr>
  	</td>
  </tr>
  </logic:notPresent>
  
  <tr>
  	<td colspan="2" rowspan="2">Exlcude: <br/>
  	<a href="" onclick="openInformationPopup('pages/internal/docs/proteinInference.jsp#PI_PARSIM_SUBSET'); return false;">
   		<img src="<yrcwww:link path="images/new_21.gif"/>" align="bottom" border="2" style="background:yellow; border-color:orange;"/></a>
   				
  	</td>
  	<td>
  		<nobr>
  		<html:checkbox name="proteinInferFilterForm" property="excludeParsimoniousProteins">Parsimonious</html:checkbox>
  		</nobr>
  	</td>
  	<td>
  		<nobr>
  		<html:checkbox name="proteinInferFilterForm" property="excludeNonParsimoniousProteins">Non-Parsimonious</html:checkbox>
  		</nobr>
  	</td>
  </tr>
  <tr>
  	<td>
  		<nobr>
  		<html:checkbox name="proteinInferFilterForm" property="excludeNonSubsetProteins" >Non-Subset</html:checkbox>
  		</nobr>
  	</td>
  	<td>
  		<nobr>
  		<html:checkbox name="proteinInferFilterForm" property="excludeSubsetProteins" >Subset</html:checkbox>
  		</nobr>
  	</td>
  </tr>
  <tr>
  	<td colspan="4">Exclude Indistinguishable Groups: <html:checkbox name="proteinInferFilterForm" property="excludeIndistinProteinGroups" value="true"/></td>
  </tr>
  </table></td></tr>
  
  <tr>
  	<td colspan="2">
  		Validation Status: 
  		<nobr><html:multibox name="proteinInferFilterForm" property="validationStatus" value="All" /> All</nobr>
  		<nobr><html:multibox name="proteinInferFilterForm" property="validationStatus" 
  					   value="<%=String.valueOf(ProteinUserValidation.UNVALIDATED.getStatusChar()) %>"/> Unvalidated</nobr>
  		<nobr><html:multibox name="proteinInferFilterForm" property="validationStatus"  
  		               value="<%=String.valueOf(ProteinUserValidation.ACCEPTED.getStatusChar()) %>"/> Accepted</nobr>
  		<nobr><html:multibox name="proteinInferFilterForm" property="validationStatus"  
  		               value="<%=String.valueOf(ProteinUserValidation.REJECTED.getStatusChar()) %>"/> Rejected</nobr>
  		<nobr><html:multibox name="proteinInferFilterForm" property="validationStatus"  
  		               value="<%=String.valueOf(ProteinUserValidation.NOT_SURE.getStatusChar()) %>" /> Not Sure</nobr>
  	</td>
  	
  </tr>
  
  
  
  <tr>
  	<td colspan="3">
  	<table align="left">
  		
  	<tr>
  	<td>Peptide:</td>
  	<td><html:text name="proteinInferFilterForm" property="peptide" size="40"></html:text>
  		<nobr><span style="font-size:8pt;">Exact Match:<html:checkbox name="proteinInferFilterForm" property="exactPeptideMatch"></html:checkbox></span></nobr>
  	</td>
  	<td> Charge:</td>
  	<td>
  		<nobr><html:multibox name="proteinInferFilterForm" property="chargeStates" value="All"/> All</nobr> &nbsp;
  		<nobr><html:multibox name="proteinInferFilterForm" property="chargeStates" value="1"/> +1</nobr> &nbsp;
  		<nobr><html:multibox name="proteinInferFilterForm" property="chargeStates" value="2"/> +2</nobr> &nbsp;
  		<nobr><html:multibox name="proteinInferFilterForm" property="chargeStates" value="3"/> +3</nobr> &nbsp;
  		<nobr><html:multibox name="proteinInferFilterForm" property="chargeStates" value="4"/> +4</nobr> &nbsp;
  		<nobr><html:multibox name="proteinInferFilterForm" property="chargeStates" value=">4"/> &gt; +4</nobr>
  		<br>
  		<span style="font-size:8pt;">Get proteins with peptides identified in at least one of the selected charge states</span> 
  	</td>
  </tr>
  
  		<logic:present name="goSupported">
  		<tr>
  			<td valign="top">GO Terms: <br/><span class="clickable underline" style="color:red; font-weight:bold;" 
  			onclick="javascript:openGOTermSearcher();return false;">Search</span></td>
  			<td valign="top"><html:text name="proteinInferFilterForm" property="goTerms" size="40"></html:text><br>
  				<span style="font-size:8pt;">Enter a comma-separated list of GO terms (e.g. GO:0006950)</span>
  			</td>
  			<td valign="top" colspan="2">
  				<html:checkbox name="proteinInferFilterForm" property="matchAllGoTerms" title="Return proteins that match all terms">Match All </html:checkbox>
  				<html:checkbox name="proteinInferFilterForm" property="exactGoAnnotation" title="Return proteins directly annotated with the GO terms">
  				<span class="tooltip" title="If checked, only direct GO annotations for a protein are used. Otherwise, annotations to descendant terms are interpreted as annotations to the parent term.">Exact</span> </html:checkbox>
  				&nbsp;
  				<nobr>
  				Exclude: 
  				<html:checkbox name="proteinInferFilterForm" property="excludeIea"><span class="tooltip" title="Inferred from Electronic Annotation">IEA</span></html:checkbox>
  				<html:checkbox name="proteinInferFilterForm" property="excludeNd"><span class="tooltip" title="No Biological Data available">ND</span></html:checkbox>
  				<html:checkbox name="proteinInferFilterForm" property="excludeCompAnalCodes" title="Computational Analysis Evidence Codes">
  				<span class="tooltip" title="Inferred from Sequence or Structural Similarity">ISS</span>,
  				<span class="tooltip" title="Inferred from Sequence Orthology">ISO</span>, 
  				<span class="tooltip" title="Inferred from Sequence Alignment">ISA</span>,
  				<span class="tooltip" title="Inferred from Sequence Model">ISM</span>,
  				<span class="tooltip" title="Inferred from Genomic Context">IGC</span>,
  				<span class="tooltip" title="Inferred from Reviewed Computational Analysis">RCA</span>
  				</html:checkbox>
  				</nobr>
  			</td>
  		</tr>
  		</logic:present>
  		
  		<tr>
  			<td valign="top">Fasta IDs: </td>
  			<td valign="top"><html:text name="proteinInferFilterForm" property="accessionLike" size="40"></html:text><br>
  				<span style="font-size:8pt;">Enter a comma-separated list of identifiers</span>
  			</td>
  			
  			<logic:present name="commonNameSupported">
  			<td valign="top">Common Names: </td>
  			<td valign="top"><html:text name="proteinInferFilterForm" property="commonNameLike" size="40"></html:text><br>
  				<span style="font-size:8pt;">Enter a comma-separated list of common names</span>
  			</td>
  			</logic:present>
  			
  		</tr>
  		<tr>
  			<td valign="top">Description Include: </td>
  			<td valign="top"><html:text name="proteinInferFilterForm" property="descriptionLike" size="40"></html:text>
  			</td>
  			<td valign="top">Exclude: </td>
  			<td valign="top">
  				<html:text name="proteinInferFilterForm" property="descriptionNotLike" size="40"></html:text>
  				<nobr><span style="font-size:8pt;">Search All:<html:checkbox name="proteinInferFilterForm" property="searchAllDescriptions"></html:checkbox></span></nobr>
  			</td>
  		</tr>
  		<tr>
  		<td></td>
  		<td colspan="3" ">
  			<div style="font-size:8pt;" align="left">Enter a comma-separated list of terms.
  			Descriptions will be included from the fasta file(s) associated with the experiment(s) <br>for
  			this protein inference as well as species specific databases (e.g. SGD) 
  			if a target species is associated with the experiment(s).
  			<br>Check "Search All" to include descriptions from Swiss-Prot and NCBI-NR. 
  			<br/><font color="red">NOTE: Description searches can be time consuming, especially when "Search All" is checked.</font></div>
  		</td>
  		</tr>
  	</table>
  	</td>
  </tr>
  
  <tr>
    	<td colspan="3" align="center">
    		<button class="plain_button" style="margin-top:2px;" 
    		        onclick="javascript:updateResults();return false;">Update</button>
    		<!--<html:submit styleClass="plain_button" style="margin-top:2px;">Update</html:submit>-->
    	</td>
    	 
  </tr>
  
 </TABLE>
 

	<!-- DISPLAY COLUMN CHOOSER -->
	<div style="background-color:#F2F2F2;width:100%; margin:5 0 0 0; padding:1 0 1 0; color:black; border: 1px solid gray; font-size:8pt;" align="left">
	<span style="margin-left:5;" 
	  class="foldable fold-close" id="protinfer_display_opts_fold">&nbsp;&nbsp;&nbsp;&nbsp; </span>
	<b>Display Options</b>
	</div>
	
	<div id="protinfer_display_opts_fold_target" class="small_font" align="center" 
		 style="padding: 5 0 5 0; border: 1px solid gray; width:100%; display:none;">
	
		<div style="width:30%;" align="left">
		<logic:iterate name="proteinInferFilterForm" property="displayColumnList" id="displayColumn" >
			<logic:equal name="displayColumn" property="disabled" value="false">
				<html:checkbox name="displayColumn" property="selected" indexed="true" styleClass="colChooser">
					<bean:write name="displayColumn" property="columnName"/>
				</html:checkbox>
			</logic:equal>
			
			<logic:equal name="displayColumn" property="disabled" value="true">
				<html:checkbox name="displayColumn" property="selected" indexed="true" disabled="true" styleClass="colChooser" >
					<bean:write name="displayColumn" property="columnName"/>
				</html:checkbox>
			</logic:equal>
			<span id="<bean:write name="displayColumn" property="columnCode"/>"></span>
			<html:hidden name="displayColumn" property="columnName" indexed="true"/>
			<html:hidden name="displayColumn" property="columnCode" indexed="true"/>
			<html:hidden name="displayColumn" property="disabled" indexed="true"/>
			<br/>
		</logic:iterate>
		<br/><br/>
		<input type="button" value="Save Settings"  onclick="saveDisplayColumnsCookie();"/>
		</div>
	</div> <!-- END OF DISPLAY OPTIONS -->


 <!-- Download Options -->
 <div align="center" style="margin:10 0 5 0;">
  	<a href="" onclick="javascript:downloadResults();return false;" ><b>Download Results</b></a> &nbsp; 
  	<html:checkbox name="proteinInferFilterForm" property="printPeptides" >Include Peptides</html:checkbox>
  	<html:checkbox name="proteinInferFilterForm" property="printDescriptions" >Include Descriptions</html:checkbox>
  	<html:checkbox name="proteinInferFilterForm" property="collapseGroups" >Collapse Protein Groups</html:checkbox>
  	<html:checkbox name="proteinInferFilterForm" property="downloadGOAnnotations" >GO Annotations</html:checkbox>
 </div>
 
 
</html:form>
