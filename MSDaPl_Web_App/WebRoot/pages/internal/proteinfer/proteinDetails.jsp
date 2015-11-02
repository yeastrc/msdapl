
<%@page import="org.yeastrc.ms.domain.search.Program"%>
<%@page import="org.yeastrc.ms.domain.protinfer.ProteinInferenceProgram"%>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/yrc-www.tld" prefix="yrcwww" %>


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
					<a href="<bean:write name="reference" property="url"/>" style="font-size: 8pt;" target="External Link">
						<b>[<bean:write name="reference" property="databaseName"/>]</b>
					</a>
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
<a  style="color:red;"   href="<yrcwww:link path='listExperimentsWithProtein.do?id'/>=<bean:write name='protein' property='protein.nrseqProteinId'/>">[List experiments with this protein]</a>
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
         href="http://www.ncbi.nlm.nih.gov/blast/Blast.cgi?CMD=Web&LAYOUT=TwoWindows&AUTO_FORMAT=Semiauto&ALIGNMENTS=50&ALIGNMENT_VIEW=Pairwise&CDD_SEARCH=on&CLIENT=web&COMPOSITION_BASED_STATISTICS=on&DATABASE=nr&DESCRIPTIONS=100&ENTREZ_QUERY=(none)&EXPECT=1000&FILTER=L&FORMAT_OBJECT=Alignment&FORMAT_TYPE=HTML&I_THRESH=0.005&MATRIX_NAME=BLOSUM62&NCBI_GI=on&PAGE=Proteins&PROGRAM=blastp&SERVICE=plain&SET_DEFAULTS.x=41&SET_DEFAULTS.y=5&SHOW_OVERVIEW=on&END_OF_HTTPGET=Yes&SHOW_LINKOUT=yes&QUERY=<bean:write name="proteinSequence"/>">NCBI BLAST</a>]

	<BR><br>
	<b>YRC Philius</b><br>
	<span><a href="http://www.ncbi.nlm.nih.gov/sites/entrez?cmd=Search&db=pubmed&term=18989393">Reynolds <I>et al.</I></a></span>
	<br/>
	<span style="text-decoration: underline; cursor: pointer;"
      onclick="philiusAnnotations(<bean:write name="protein" property="protein.id" />,<bean:write name="protein" property="protein.nrseqProteinId" />)"
      id="philiusbutton_<bean:write name="protein" property="protein.id"/>">[Get Annotations]</span>
    </font>
	</td>
	<td align="left" valign="top">
	<div id="protsequence_<bean:write name="protein" property="protein.id"/>">
	<!-- Protein sequwnce -->
	<pre><bean:write name="proteinSequenceHtml" filter="false"/></pre>
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


<table align="center" cellpadding="2" style="border: 1px solid gray; border-spacing: 2px">
<tr class="pinfer_A">
	<td style="border: 1px #CCCCCC dotted;" align="center">Coverage(%)</td>
	<td style="border: 1px #CCCCCC dotted;" align="center"># Sequences</td>
	<td style="border: 1px #CCCCCC dotted;" align="center"># Uniq.Sequence</td>
	<td style="border: 1px #CCCCCC dotted;" align="center"># Spectra </td>
	<logic:equal name="isIdPicker" value="true">
		<td style="border: 1px #CCCCCC dotted;" align="center">NSAF** </td>
	</logic:equal>
	<td style="border: 1px #CCCCCC dotted;" align="center">Parsimonious</td>
	<td style="border: 1px #CCCCCC dotted;" align="center">Subset</td>
	<logic:present name="superProteins" >
		<td style="border: 1px #CCCCCC dotted;" align="center">Subset Of </td>
	</logic:present>
	<logic:present name="subsetProteins" >
		<td style="border: 1px #CCCCCC dotted;" align="center">Subset Proteins </td>
	</logic:present>
	<td style="border: 1px #CCCCCC dotted;" align="center">Other Proteins in Group</td>
	<logic:equal name="isIdPicker" value="true">
		<td style="border: 1px #CCCCCC dotted;" align="center">Protein Cluster</td>
	</logic:equal>
</tr>
<tr>
	<td style="border: 1px #CCCCCC dotted;" align="center"><bean:write name="protein" property="protein.coverage" /></td>
	<td style="border: 1px #CCCCCC dotted;" align="center"><bean:write name="protein" property="protein.peptideCount" /></td>
	<td style="border: 1px #CCCCCC dotted;" align="center"><bean:write name="protein" property="protein.uniquePeptideCount" /></td>
	<td style="border: 1px #CCCCCC dotted;" align="center"><bean:write name="protein" property="protein.spectrumCount" /></td>
	<logic:equal name="isIdPicker" value="true">
		<td style="border: 1px #CCCCCC dotted;" align="center"><bean:write name="protein" property="protein.nsafFormatted" /></td>
	</logic:equal>
	<td style="border: 1px #CCCCCC dotted;" align="center">
		<logic:equal name="protein" property="protein.isParsimonious" value="true">Yes</logic:equal>
		<logic:equal name="protein" property="protein.isParsimonious" value="false">No</logic:equal>
	</td>
	<td style="border: 1px #CCCCCC dotted;" align="center">
		<logic:equal name="protein" property="protein.isSubset" value="true">Yes</logic:equal>
		<logic:equal name="protein" property="protein.isSubset" value="false">No</logic:equal>
	</td>
	<logic:present name="superProteins" >
		<td style="border: 1px #CCCCCC dotted;" align="center">
			<logic:iterate name="superProteins" id="prot">
			<li>
			<span onclick="showProteinDetails(<bean:write name="prot" property="protein.id" />)" 
						style="text-decoration: underline; cursor: pointer">
			<logic:iterate name="prot" property="proteinListing.fastaReferences" id="reference" indexId="index">
				<logic:greaterThan name="index" value="0">,</logic:greaterThan>
				<bean:write name="reference" property="shortAccession"/>
			</logic:iterate>
			</span>
			</li>
			</logic:iterate>
		</td>
	</logic:present>
	<logic:present name="subsetProteins">
		<td style="border: 1px #CCCCCC dotted;" align="center">
			<logic:iterate name="subsetProteins" id="prot">
			<li>
			<span onclick="showProteinDetails(<bean:write name="prot" property="protein.id" />)" 
						style="text-decoration: underline; cursor: pointer">
			<logic:iterate name="prot" property="proteinListing.fastaReferences" id="reference" indexId="index">
				<logic:greaterThan name="index" value="0">,</logic:greaterThan>
				<bean:write name="reference" property="shortAccession"/>
			</logic:iterate>
			</span>
			</li>
			</logic:iterate>
		</td>
	</logic:present>
	<td style="border: 1px #CCCCCC dotted; text-align: left;" align="center">
	<logic:empty name="groupProteins">--</logic:empty>
	<logic:notEmpty name="groupProteins">
	 <ul>
	<logic:iterate name="groupProteins" id="prot">
		<li>
		<span onclick="showProteinDetails(<bean:write name="prot" property="protein.id" />)" 
						style="text-decoration: underline; cursor: pointer">
			<logic:iterate name="prot" property="proteinListing.fastaReferences" id="reference" indexId="index">
				<logic:greaterThan name="index" value="0">,</logic:greaterThan>
				<bean:write name="reference" property="shortAccession"/>
			</logic:iterate>
		</span>
		</li>
	</logic:iterate>
		</ul>
	</logic:notEmpty>
	</td>
	<logic:equal name="isIdPicker" value="true">
		<td style="border: 1px #CCCCCC dotted;" align="center">
			<span style="cursor:pointer;text-decoration:underline" 
			  onclick="showProteinCluster(<bean:write name="protein" property="protein.clusterLabel"/>)">
			<bean:write name="protein" property="protein.clusterLabel"/>
			</span>
		</td>
	</logic:equal>
	
</tr>
</table>
</div>

<br><br>



	
<table align="center" width="95%" id="protdetailstbl_<bean:write name="protein" property="protein.id"/>" class="table_pinfer">
	<thead>
    <tr class="main">
    <th class="main" style="font-size:10pt;"><b>Uniq</b></th>
    <th class="main" style="font-size:10pt;"><b>Peptide</b></th>
    <th width="10%" align="left" class="main" style="font-size:10pt;"><b>Charge</b></th>
    <th width="10%" align="left" class="main" style="font-size:10pt;"><b># Spectra</b></th>
    <logic:equal name="protInferProgram" value="<%= ProteinInferenceProgram.PROTINFER_SEQ.name()%>">
     	<th class="main" style="font-size:10pt;">Best FDR</th>
     </logic:equal>
     <logic:equal name="protInferProgram" value="<%= ProteinInferenceProgram.PROTINFER_PLCID.name()%>">
     	<th class="main" style="font-size:10pt;">Best FDR</th>
     </logic:equal>
     <logic:equal name="inputGenerator" value="<%=Program.SEQUEST.name() %>">
     	<th class="main" style="font-size:10pt;">DeltaCN</th>
     	<th class="main" style="font-size:10pt;">XCorr</th>
     </logic:equal>
     
     <logic:equal name="inputGenerator" value="<%=Program.PROLUCID.name() %>">
     	<th class="main" style="font-size:10pt;">DeltaCN</th>
     	<th class="main" style="font-size:10pt;">Primary Score</th>
     </logic:equal>
     <logic:equal name="inputGenerator" value="<%=Program.PERCOLATOR.name() %>">
     	<th class="main" style="font-size:10pt;">qValue</th>
     	<th class="main" style="font-size:10pt;">PEP</th>
     </logic:equal>
     <logic:equal name="protInferProgram" value="<%= ProteinInferenceProgram.PROTEIN_PROPHET.name()%>">
     	<th class="sort-float" align="left">nsp</th>
     	<th class="sort-float" align="left">Wt.</th>
     	<th class="sort-float" align="left">Init.Prob.</th>
     	<th class="sort-float" align="left">nsp adj. Prob.</th>
     </logic:equal>
     <logic:equal name="inputGenerator" value="<%=Program.PEPTIDE_PROPHET.name() %>">
     	<!-- <th class="sort-float" align="left">Probability</th> -->
     	<th class="sort-float" align="left">NET</th>
     	<th class="sort-float" align="left">NMC</th>
     	<th class="sort-float" align="left">Mass Diff.</th>
     	<th class="sort-float" align="left">FVal</th>
     </logic:equal>
     <th class="main" style="font-size:10pt;">Spectrum</th>
    </tr>
    </thead>
    
   	<tbody>
       <logic:iterate name="ionList" id="ion">
            <tr class="main">
            <td>
     			<logic:equal name="ion" property="isUniqueToProteinGroup" value="true">*</logic:equal>
     			<logic:equal name="ion" property="isUniqueToProteinGroup" value="false"></logic:equal>
     		</td>
     		<td class="left_align"><span class="peptide"><bean:write name="ion" property="ionSequence" /></span></td>
     		<td><bean:write name="ion" property="charge" /></td>
     		<td>
     			<bean:write name="ion" property="spectrumCount" />
     			<span class="showAllIonHits" style="text-decoration: underline; cursor: pointer;font-size: 7pt; color: #000000;" 
				  id="showhitsforion_<bean:write name="ion" property="ion.id" />"
				  onclick="toggleHitsForIon(<bean:write name="ion" property="ion.id" />)"
				  >[Show]</span>
     		</td>
     		
     		<logic:equal name="protInferProgram" value="<%= ProteinInferenceProgram.PROTINFER_SEQ.name()%>">
     			<bean:define name="ion" property="ion.bestSpectrumMatch" id="psm_idp" type="org.yeastrc.ms.domain.protinfer.idpicker.IdPickerSpectrumMatch"/>
     			<td><bean:write name="psm_idp" property="fdrRounded" /></td>
     		</logic:equal>
     		
     		<logic:equal name="protInferProgram" value="<%= ProteinInferenceProgram.PROTINFER_PLCID.name()%>">
     			<bean:define name="ion" property="ion.bestSpectrumMatch" id="psm_idp" type="org.yeastrc.ms.domain.protinfer.idpicker.IdPickerSpectrumMatch"/>
     			<td><bean:write name="psm_idp" property="fdrRounded" /></td>
     		</logic:equal>
     		
     		<logic:equal name="inputGenerator" value="<%=Program.SEQUEST.name() %>">
     			<bean:define name="ion" property="bestSpectrumMatch" id="psm_seq" type="org.yeastrc.ms.domain.search.sequest.SequestSearchResult"/>
     			<td><bean:write name="psm_seq" property="sequestResultData.deltaCN" /></td>
     			<td><bean:write name="psm_seq" property="sequestResultData.xCorr" /></td>
     			
     			<td>
     			<span class="clickable underline" title="Java Spectrum Viewer"
				onclick="viewSpectrum(<bean:write name="ion" property="scanId" />, <bean:write name="ion" property="bestSpectrumMatch.id" />, 'java')" >
				J
				</span>
				&nbsp;
	     		<span class="clickable underline" title="JavaScript Spectrum Viewer"
				onclick="viewSpectrum(<bean:write name="ion" property="scanId" />, <bean:write name="ion" property="bestSpectrumMatch.id" />)" >
				JS
				</span>
				</td>
     			
     		</logic:equal>
     		
     		<logic:equal name="inputGenerator" value="<%=Program.PROLUCID.name() %>">
     		 	<bean:define name="ion" property="bestSpectrumMatch" id="psm_plc" type="org.yeastrc.ms.domain.search.prolucid.ProlucidSearchResult"/>
     		 	<td><bean:write name="psm_plc" property="prolucidResultData.primaryScore" /></td>
				<td><bean:write name="psm_plc" property="prolucidResultData.deltaCN" /></td>
				
				<td>
     			<span class="clickable underline" title="Java Spectrum Viewer"
				onclick="viewSpectrum(<bean:write name="ion" property="scanId" />, <bean:write name="ion" property="bestSpectrumMatch.id" />, 'java')" >
				J
				</span>
				&nbsp;
	     		<span class="clickable underline" title="JavaScript Spectrum Viewer"
				onclick="viewSpectrum(<bean:write name="ion" property="scanId" />, <bean:write name="ion" property="bestSpectrumMatch.id" />)" >
				JS
				</span>
				</td>
				
     		</logic:equal>
     		 
     		<logic:equal name="inputGenerator" value="<%=Program.PERCOLATOR.name() %>">
     		 	<bean:define name="ion" property="bestSpectrumMatch" id="psm_perc" type="org.yeastrc.ms.domain.analysis.percolator.PercolatorResult"/>
     		 	<td><bean:write name="psm_perc" property="qvalueRounded" /></td>
     			<td><bean:write name="psm_perc" property="posteriorErrorProbabilityRounded" /></td>
     			<td>
     			<span class="clickable underline" title="Java Spectrum Viewer"
				onclick="viewSpectrum(<bean:write name="ion" property="scanId" />, <bean:write name="psm_perc" property="id" />, 'java')" >
				J
				</span>
				&nbsp;
	     		<span class="clickable underline" title="JavaScript Spectrum Viewer"
				onclick="viewSpectrum(<bean:write name="ion" property="scanId" />, <bean:write name="psm_perc" property="id" />)" >
				JS
				</span>
				</td>
     		</logic:equal>
     		 
     		 <logic:equal name="protInferProgram" value="<%= ProteinInferenceProgram.PROTEIN_PROPHET.name()%>">
     			<bean:define name="ion" property="ion" id="prophetIon" type="org.yeastrc.ms.domain.protinfer.proteinProphet.ProteinProphetProteinPeptideIon"/>
     		 	<td><bean:write name="prophetIon" property="numSiblingPeptides" /></td>
     		 	<td><bean:write name="prophetIon" property="weight" /></td>
	     		<td><bean:write name="prophetIon" property="initialProbability" /></td>
	     		<td><bean:write name="prophetIon" property="nspAdjProbability" /></td>
     		</logic:equal>
     		 <logic:equal name="inputGenerator" value="<%=Program.PEPTIDE_PROPHET.name() %>">
     		 	<bean:define name="ion" property="bestSpectrumMatch" id="psm_peptProphet" type="org.yeastrc.ms.domain.analysis.peptideProphet.PeptideProphetResult"/>
     		 	<!--  <td><bean:write name="psm_peptProphet" property="probabilityRounded" /></td> -->
	     		<td><bean:write name="psm_peptProphet" property="numEnzymaticTermini" /></td>
	     		<td><bean:write name="psm_peptProphet" property="numMissedCleavages" /></td>
	     		<td><bean:write name="psm_peptProphet" property="massDifferenceRounded" /></td>
	     		<td><bean:write name="psm_peptProphet" property="fValRounded" /></td>
	     		
	     		<td>
     			<span class="clickable underline" title="Java Spectrum Viewer"
				onclick="viewSpectrum(<bean:write name="ion" property="scanId" />, <bean:write name="psm_peptProphet" property="id" />, 'java')" >
				J
				</span>
				&nbsp;
	     		<span class="clickable underline" title="JavaScript Spectrum Viewer"
				onclick="viewSpectrum(<bean:write name="ion" property="scanId" />, <bean:write name="psm_peptProphet" property="id" />)" >
				JS
				</span>
				</td>
				
     		</logic:equal>
     		
            </tr>
            
           	<tr>
           		<td colspan="13" align="center">
				  <!--  peptide ion hits table will go here: psmListForIon.jsp -->
				<div align="center" id="hitsforion_<bean:write name="ion" property="ion.id" />"></div>
				</td>
			</tr>
        </logic:iterate>
        </tbody>
        </table>
</center>