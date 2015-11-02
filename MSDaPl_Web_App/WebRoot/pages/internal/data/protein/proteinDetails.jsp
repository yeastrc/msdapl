<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/yrc-www.tld" prefix="yrcwww" %>

<yrcwww:notauthenticated>
 <logic:forward name="authenticate" />
</yrcwww:notauthenticated>

<logic:empty name="protein">
  <logic:forward name="standardHome" />
</logic:empty>

<%@ include file="/includes/header.jsp" %>

<script>
// ---------------------------------------------------------------------------------------
// SUBMIT/SHOW/HIDE PHILIUS RESULTS
// --------------------------------------------------------------------------------------- 
// Submit Philius job OR show / hide results
function philiusAnnotations(nrseqProteinId) {

	var button = $("#philiusbutton_"+nrseqProteinId);
	
	if(button.text() == "[Get Annotations]") {
		// submit a Philius job, get a job token and display a status message.
		//alert("Submitting Philius job...");
		button.text("[Processing...]");
		
		
		var token = 0;
		var statusText = "Your request has been submitted to the Philius Server.  ";
		statusText += "Processing typically takes about <b>10 seconds</b>.  ";
		statusText += "To get your results click ";
		statusText += "<span id=\"philius_refresher\" style=\"text-decoration: underline; cursor: pointer;\" ";
		
		$.post("<yrcwww:link path='submitPhiliusJob.do'/>",
   					{'nrseqId': nrseqProteinId},
   					function(data) {
   						token = data;
   						if(token.substr(0,6) == "FAILED") {
   							statusText = "Philius Job Submission failed!<br>"+token;
   							button.text("[Failed...]");
   							// show the status text with a link for fetching the results with the returned token.
							// OR the failure message.
							$("#philius_status_"+nrseqProteinId).html(statusText);
							$("#philius_status_"+nrseqProteinId).show();
   						}
   						// If the result was not already available in the database a request is submitted to the Philius server
   						else if(token.substr(0,9) == "SUMBITTED") {
   							//alert("Returned token is: "+token);
   							token = token.substr(10);
   							statusText += " onclick=philiusAnnotations("+nrseqProteinId+") >";
   							statusText += "<b>REFRESH</b></span>.";
   							button.attr('name', token);
   							// show the status text with a link for fetching the results with the returned token.
							// OR the failure message.
							$("#philius_status_"+nrseqProteinId).html(statusText);
							$("#philius_status_"+nrseqProteinId).show();
						}
						else {
							$("#protsequence_"+nrseqProteinId).hide();
							$("#philiusannot_"+nrseqProteinId).html(data);
							$("#philiusannot_"+nrseqProteinId).show();
							button.text("[Hide Annotations]");
						}
   					}
    		);
	}
	else if (button.text() == "[Processing...]") {
		// hide the philius status text
		$("#philius_status_"+nrseqProteinId).hide();
		// request the results 
		var token = button.attr('name');
		$.post("<yrcwww:link path='getPhiliusResults.do'/>",
   					{'nrseqProteinId': nrseqProteinId,
   					 'philiusToken': token},
   					function(data) {
   						// if results are still not available
   						if(data == "WAIT") {
   							// show philius status again
   							$("#philius_status_"+nrseqProteinId).show();
   						}
   						// if the request failed
   						else if (data.substr(0,6) == "FAILED") {
   							var statusText = "Philius Job Submission failed!<br>"+data;
   							$("#philius_status_"+nrseqProteinId).html(statusText);
   							$("#philius_status_"+nrseqProteinId).show();
   							button.text("[Failed...]");
   						}
   						// Philius did not find any segments
   						else if (data == "NONE") {
   							var statusText = "Philius did not predict any segments";
   							$("#philius_status_"+nrseqProteinId).html(statusText);
   							$("#philius_status_"+nrseqProteinId).show();
   							button.text("[Hide Annotations]");
   						}
   						// request succeeded; display the results
   						else {
   							// show the Philius annotations and hide the protein sequence.
							$("#protsequence_"+nrseqProteinId).hide();
							$("#philiusannot_"+nrseqProteinId).html(data);
							$("#philiusannot_"+nrseqProteinId).show();
							button.text("[Hide Annotations]");
   						}
   					}
   				);
	}
	else if (button.text() == "[Hide Annotations]") {
		// If the Philius status is visible hide it too
		$("#philius_status_"+nrseqProteinId).hide();
		$("#philiusannot_"+nrseqProteinId).hide();
		$("#protsequence_"+nrseqProteinId).show();
		button.text("[Show Annotations]");
	}
	else if (button.text() == "[Show Annotations]") {
		$("#philiusannot_"+nrseqProteinId).show();
		$("#protsequence_"+nrseqProteinId).hide();
		button.text("[Hide Annotations]");
	}
	else if (button.text() == "[Failed...]") {
		button.text("[Get Annotations]");
		$("#philius_status_"+nrseqProteinId).hide();
	}
}


</script>


<yrcwww:contentbox title="View Protein Information" centered="true" width="80" widthRel="true">

<center>
<div align="center" style="padding:5px;font-size: 10pt;width: 90%; color: black;">

<div align="center">
<table align="center" 
      style="background-color:#F8F8FF; border: 1px solid #CBCBCB; cellspacing="0" cellpadding="2">
<tr>
	<td valign="top" align="left" width="20%"><b>Accession(s):</b></td>
	<td valign="top" align="left">
		<logic:iterate name="protein" property="proteinListing.fastaReferences" id="reference">
			<bean:write name="reference" property="accession"/>
			<br/>
		</logic:iterate>
	</td>
</tr>
<logic:notEmpty name="protein" property="proteinListing.commonReferences">
<tr>
	<td valign="top" align="left"><b>Common Name(s):</b></td>
	<td valign="top" align="left">
		<logic:iterate name="protein" property="proteinListing.commonReferences" id="reference">
			<bean:define name="reference" property="commonReference.name" id="commonName" type="java.lang.String"/>
			<bean:define name="reference" property="accession" id="accession" type="java.lang.String"/>
			<bean:write name="commonName"/>
			<logic:notEqual name="commonName" value="<%=accession.toString() %>">
			 / <bean:write name="accession" />
			</logic:notEqual>
			<logic:equal name="reference" property="hasExternalLink" value="true">
				<a href="<bean:write name="reference" property="url"/>" style="font-size: 8pt;" target="ExternalLink">
				[<bean:write name="reference" property="databaseName"/>]</a>
			</logic:equal>
			<br/>
		</logic:iterate>
	</td>
</tr>
</logic:notEmpty>

<tr>
	<td valign="top" align="left"><b>Organism:</b></td>
	<td valign="top" align="left">
		<a target="ncbi_window" href="http://www.ncbi.nlm.nih.gov/Taxonomy/Browser/wwwtax.cgi?mode=Info&id=<bean:write name="protein" property="proteinListing.speciesId"/>">
    	<i><bean:write name="protein" property="proteinListing.speciesName" /></i></a>
	</td>
</tr>
<tr>
	<td valign="top" align="left"><b>Molecular Wt.:</b></td>
	<td valign="top" align="left">
    	<bean:write name="protein" property="molecularWeight" />
	</td>
</tr>
<tr>
	<td valign="top" align="left"><b>pI:</b></td>
	<td valign="top" align="left">
    	<bean:write name="protein" property="pi" />
	</td>
</tr>

<logic:present name="philiusAnnotation">
<tr>
	<td valign="top" align="left"><b>Philius Annotation:</b>
	<br/>
    <span class="small_font"><a href="http://www.ncbi.nlm.nih.gov/sites/entrez?cmd=Search&db=pubmed&term=18989393">Reynolds <I>et al.</I></a></span><br>
	</td>
	<td valign="top" align="left">
    	<bean:write name="philiusAnnotation" />
	</td>
</tr>
</logic:present>

<logic:present name="proteinAbundance">
<tr>
	<td valign="top" align="left"><b>Abundance:</b><br/>(copies / cell)
	<br/>
    <span class="small_font">Ghaemmaghami, <em>et al., </em></span><br>
    <span class="small_font"><em>Nature</em> <strong>425</strong>, 737-741 (2003)</span>
	</td>
	<td valign="top" align="left">
    	<bean:write name="proteinAbundance" />
	</td>
</tr>
</logic:present>

<tr>
	<td valign="top" align="left"><b>Description(s):</b></td>
	<td valign="top" align="left" style="color: #888888; font-size: 9pt;">
		<logic:iterate name="protein" property="proteinListing.descriptionReferences" id="reference">
			<li>
				<logic:equal name="reference" property="hasExternalLink" value="true">
					<a href="<bean:write name="reference" property="url"/>" style="font-size: 8pt;" target="External Link"><b>[<bean:write name="reference" property="databaseName"/>]</b></a>
				</logic:equal>
				<logic:equal name="reference" property="hasExternalLink" value="false">
					<span style="color:#000080;"><b>[<bean:write name="reference" property="databaseName"/>]</b></span>
				</logic:equal>
				 &nbsp; &nbsp; <bean:write name="reference" property="descriptionEscaped"/>
				<br/>
				
			</li>
		</logic:iterate>
	</td>
</tr>

<!-- GENE ONTOLOGY -->
<tr>
   <td valign="top" align="left"><b>GO Cellular Component:</b></td>
   <td valign="top" align="left">
    <logic:empty name="components">
     NONE FOUND
    </logic:empty>
    <logic:notEmpty name="components">
     <logic:iterate name="components" id="gonode">
     
     <logic:equal name="gonode" property="isNot" value="true"><span style="font-size:8pt;font-weight:bold;">[NOT]</span></logic:equal>
	 <a target="go_window"
	    href="http://www.godatabase.org/cgi-bin/amigo/go.cgi?action=query&view=query&search_constraint=terms&query=<bean:write name="gonode" property="node.accession"/>">
      <bean:write name="gonode" property="node.name"/></a>
      <logic:equal name="gonode" property="node.obsolete" value="true">
		<span class="go_obsolete"  title="Obsolete GO Term">O</span>
	  </logic:equal>
      <yrcwww:goEvidence name="gonode" />	
      <br>
     </logic:iterate>    
    </logic:notEmpty>
    </td>
</tr>
<tr>
   <td valign="top" align="left"><b>GO Biological Process:</b></td>
   <td valign="top" align="left">
    
    <logic:empty name="processes">
     NONE FOUND
    </logic:empty>
    <logic:notEmpty name="processes">
     <logic:iterate name="processes" id="gonode">
     
     <logic:equal name="gonode" property="isNot" value="true"><span style="font-size:8pt;font-weight:bold;">[NOT]</span></logic:equal>
	 <a target="go_window"
	    href="http://www.godatabase.org/cgi-bin/amigo/go.cgi?action=query&view=query&search_constraint=terms&query=<bean:write name="gonode" property="node.accession"/>">
      <bean:write name="gonode" property="node.name"/></a>
      <logic:equal name="gonode" property="node.obsolete" value="true">
		<span class="go_obsolete"  title="Obsolete GO Term">O</span>
	  </logic:equal>
      <yrcwww:goEvidence name="gonode" />
      <br>
     </logic:iterate>    
    </logic:notEmpty>
    
   </td>
</tr>

<tr>
   <td valign="top" align="left"><b>GO Molecular Function:</b></td>
   <td valign="top" align="left">
    <logic:empty name="functions">
     NONE FOUND
    </logic:empty>
    <logic:notEmpty name="functions">
     <logic:iterate name="functions" id="gonode">
     
     <logic:equal name="gonode" property="isNot" value="true"><span style="font-size:8pt;font-weight:bold;">[NOT]</span></logic:equal>
	 <a target="go_window"
	    href="http://www.godatabase.org/cgi-bin/amigo/go.cgi?action=query&view=query&search_constraint=terms&query=<bean:write name="gonode" property="node.accession"/>">
      <bean:write name="gonode" property="node.name"/></a>
      <logic:equal name="gonode" property="node.obsolete" value="true">
		<span class="go_obsolete"  title="Obsolete GO Term">O</span>
	  </logic:equal>
      <yrcwww:goEvidence name="gonode" />
      <br>
     </logic:iterate>    
    </logic:notEmpty>
    
   </td>
</tr>

<tr>
<td colspan="2" style="font-size: 8pt; color: red;" align="center">
<a  style="color:red;"   href="<yrcwww:link path='viewProtein.do?id'/>=<bean:write name='protein' property='protein.id'/>">[List experiments with this protein]</a>
</td>
</tr>
</table>

</div>

<br>

<!-- PROTEIN SEQUENCE -->
<div align="center">
<table  align="center" width="60%" id="protseqtbl_<bean:write name='protein' property='protein.id'/>" style="border:1px solid gray;">
	<tr>
	<td valign="top">
	<font style="font-size:9pt;">
     [<a target="blast_window"
         href="http://www.ncbi.nlm.nih.gov/blast/Blast.cgi?CMD=Web&LAYOUT=TwoWindows&AUTO_FORMAT=Semiauto&ALIGNMENTS=50&ALIGNMENT_VIEW=Pairwise&CDD_SEARCH=on&CLIENT=web&COMPOSITION_BASED_STATISTICS=on&DATABASE=nr&DESCRIPTIONS=100&ENTREZ_QUERY=(none)&EXPECT=1000&FILTER=L&FORMAT_OBJECT=Alignment&FORMAT_TYPE=HTML&I_THRESH=0.005&MATRIX_NAME=BLOSUM62&NCBI_GI=on&PAGE=Proteins&PROGRAM=blastp&SERVICE=plain&SET_DEFAULTS.x=41&SET_DEFAULTS.y=5&SHOW_OVERVIEW=on&END_OF_HTTPGET=Yes&SHOW_LINKOUT=yes&QUERY=<bean:write name="protein" property="sequence"/>">NCBI BLAST</a>]

	<BR><br>
	<b>YRC Philius</b><br>
	<span><a href="http://www.ncbi.nlm.nih.gov/sites/entrez?cmd=Search&db=pubmed&term=18989393">Reynolds <I>et al.</I></a></span>
	<br/>
	<span style="text-decoration: underline; cursor: pointer;"
      onclick="philiusAnnotations(<bean:write name="protein" property="id" />)"
      id="philiusbutton_<bean:write name="protein" property="protein.id"/>">[Get Annotations]</span>
    </font>
	</td>
	<td align="left" valign="top">
	<div id="protsequence_<bean:write name="protein" property="protein.id"/>">
	<!-- Protein sequwnce -->
	<pre><bean:write name="protein"  property="htmlSequence" filter="false"/></pre>
	</div>
	<!-- Place holder for Philius Annotations -->
	<div id="philiusannot_<bean:write name="protein" property="protein.id"/>"></div>
	</td>
	</tr>
	<tr>
		<td colspan="2" align="center">
		<div id="philius_status_<bean:write name="protein" property="protein.id"/>" 
			 style="display:none;font-size:10pt;color:red;"></div>
		</td>
	</tr>
</table>
</div>
<br><br>
</div>
</center>
</yrcwww:contentbox>

<%@ include file="/includes/footer.jsp" %>