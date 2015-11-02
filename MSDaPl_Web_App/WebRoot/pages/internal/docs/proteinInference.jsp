<%@ taglib uri="/WEB-INF/yrc-www.tld" prefix="yrcwww" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>

<head>
 <title>MSDaPl Docs: Protein Inference</title>
 <link REL="stylesheet" TYPE="text/css" HREF="<yrcwww:link path='/css/global.css' />">
</head>

<yrcwww:contentbox centered="true" width="90" widthRel="true" title="Protein inference">

	<!-- PROTEIN INFERENCE PROCESS -->
	<div style="border: 1px dotted; margin:10x; padding:10px;">
	This document is for the protein inference program implemented in MSDaPl.
	It is available for use with <a href="http://noble.gs.washington.edu/proj/percolator/" target="_blank">Percolator</a> 
	results generated with the MacCoss Lab's pipeline.
	The parsimonious protein inference in this program is based on the IDPicker algorithm published in:<br>
 	<i>Proteomic Parsimony through Bipartite Graph Analysis Improves Accuracy and Transparency.</i>
 	&nbsp; &nbsp;
	<br>Tabb <i>et. al.</i> <i>J Proteome Res.</i> 2007 Sep;6(9):3549-57
	
	<br/>
	<br/>
	<div style="font-weight:bold; background-color:#FFEF7F; border: 1px gray dotted;">
	Parsimonious Protein Inference<br/></div>
	<li><b>Step 1:</b></li>
	A bipartitie graph is created with edges between peptides and their matching proteins.<br/>
	<div align="center" style="margin: 5px;">
	<img src="<yrcwww:link path='images/docs/protinfer1.png'/>" border="1"/>
	</div> <br/> <br/>
	
	<li><b>Step 2:</b></li>
	Peptides that match the same set of proteins are merged into a single node in the graph.
	For example, peptides 3, 7, and 9 match protein A and no other protein.<br/>
	<div align="center" style="margin: 5px;">
	<img src="<yrcwww:link path='images/docs/protinfer2.png'/>" border="1"/>
	</div> <br/> <br/>
	
	<li><b>Step 3:</b></li>
	Proteins that match the same set of peptide are merged into a single node in the graph. These proteins comprise
	an <span style="color:red;"><b>indistinguishable protein group</b></span>. <br/>
	<div align="center" style="margin: 5px;">
	<img src="<yrcwww:link path='images/docs/protinfer3.png'/>" border="1"/>
	</div> <br/> <br/>
	
	<li><b>Step 4:</b></li>
	The graph is then resolved into its connected components, or proteins that share peptides. 
	Each connected component is referred to as a <b>protein cluster</b>.<br/>
	<div align="center" style="margin: 5px;">
	<img src="<yrcwww:link path='images/docs/protinfer4.png'/>" border="1"/>
	</div> <br/> <br/>
	
	<li><b>Step 5:</b></li>
	The smallest set of proteins sufficient to explain the peptides in each cluster are marked as parsimonious.<br/>
	<div align="center" style="margin: 5px;">
	<img src="<yrcwww:link path='images/docs/protinfer5.png'/>" border="1"/>
	</div> <br/> <br/>
	
	<br/>
	<br/>
	
	
	<!-- Parsimonious vs Non-subset proteins -->
	<a name="PI_PARSIM_SUBSET"></a>
	<div style="font-weight:bold; background-color:#FFEF7F; border: 1px gray dotted;">
	Parsimonious proteins vs. Non-subset proteins<br/></div>
	<br/>
	As of version 0.2 the protein inference program implemented in MSDaPl calculates both parsimonious and non-subset proteins. 
	The set of parsimonious proteins is the minimum number of proteins required to explain the observed peptides. 
	This set is always smaller (or same size) as the set of non-subset proteins (as calculated by DTASelect, for example). 
	<br/><br/>
	
	In general, the number of parsimonious proteins is a good conservative estimate of the number of proteins in your sample. 
	However, when looking for specific proteins or when compiling a list of proteins for further analysis it may be 
	better to go with the less conservative list of non-subset proteins. 
	<br/><br/>
	
	
	Please see the description and figures in the "Parsimonious Protein Inference" section above for information on 
	the process of parsimonious protein inference.   The figure below describes a subset protein -- a protein whose 
	observed peptides are a subset of the observed peptides of another protein. 
	<div align="center" style="margin: 5px;">
	<img src="<yrcwww:link path='images/docs/protinfer_parsim_subset4.png'/>" border="1"/>
	</div>
	<br/><br/>
	
	<b>Example 1. </b> Parsimonious protein set is the same as the non-subset protein set. 
	<div align="center" style="margin: 5px;">
	<img src="<yrcwww:link path='images/docs/protinfer_parsim_subset5.png'/>" border="1"/>
	</div>
	<br/><br/>
	
	<b>Example 2. </b> Parsimonious protein set is the smaller than the non-subset protein set. 
	<div align="center" style="margin: 5px;">
	<img src="<yrcwww:link path='images/docs/protinfer_parsim_subset6.png'/>" border="1"/>
	</div>
	<br/><br/>
	
	
	It is important to note that a parsimonious set of proteins may not be unique, as can be seen in the figure above. 
	Proteins <b>A</b> and <b>B</b> also form a parsimonious set since they explain all the observed peptides.  However, in this case, 
	the protein that explains more observed peptides (Protein <b>C</b> explains 3 peptides, 1 more that protein <b>B</b>) is picked 
	to be in the parsimonious set (<b>A, C</b>). 
	<br/><br/>
	
	There can be instances where the number of peptides a protein explains is not sufficient to resolve ties,
	as can be seen in the example below. Any two of the three proteins (<b>A, B, C</b>) can be picked to form a parsimonious set. 
	In such instances the parsimonious protein inference process makes an arbitraty choice and picks one of 
	the three possible sets.  
	<div align="center" style="margin: 5px;">
	<img src="<yrcwww:link path='images/docs/protinfer_parsim_subset7.png'/>" border="1"/>
	</div>
	<br/><br/>
	
	
	
	<br/><br/>
	The protein inference view, by default displays all the inferred proteins.  In order to display only the non-parsimonious proteins, 
	check the "Exclude Non-Parsimonious" checkbox. 
	<br/>
	<img src="<yrcwww:link path='images/docs/protinfer_parsim_subset1.png'/>" border="1"/>
	<br/>
	<br/>
	To display only the non-subset proteins check the "Exclude Subset" checkbox.
	<br/>
	<img src="<yrcwww:link path='images/docs/protinfer_parsim_subset2.png'/>" border="1"/>
	<br/>
	<br/>
	
	In order to display all proteins that were marked as non-parsimonious but were not subset proteins, check both the 
	"Exclude Parsimonious" and "Exclude Subset" checkboxes.  This will list all the proteins that would be included in a 
	non-subset protein list but not in a parsimonious list.
	<br/>
	<img src="<yrcwww:link path='images/docs/protinfer_parsim_subset3.png'/>" border="1"/>
	<br/>
	<br/>
	
	<br/><br/>
	<!-- OPTIONS -->
	<a name="PI_OPTIONS"></a>
	<div style="font-weight:bold; background-color:#FFEF7F; border: 1px gray dotted;">
	Program Options<br/></div>
	Protein inference implemented in MSDaPl takes <a href="http://noble.gs.washington.edu/proj/percolator/" target="_blank">Percolator</a>
	results as input.<br><br/>
	Results can be filtered on <i>q-value</i> and <i>Posterior Error Probability (PEP)</i> calculated by Percolator.
	As of version 1.16, Percolator calculates q-values and PEP at the peptide-level in addition to scores at the 
	PSM (Peptide Spectrum Match) level. Filters can be applied at both the peptide and PSM-level scores 
	when inferring proteins from Percolator results where peptide-level scores are available .
	<br/><br/>
	<img src="<yrcwww:link path='images/docs/protinfer_opts4.png'/>" border="1"/>
	 
	
	
	<br/><br/>
	Proteins (indistinguishable protein groups) can be filtered on the number of peptides and number of unique peptides identified.
	<br/>
	The number of peptides can be calculated as one of the following:<br/>
	<table>
	<tr>
	<td valign="top">
		<li>unique peptide sequences</li>
		<li>unique modified peptide sequence</li>
		<li>Unique combination of peptide sequence + charge</li>
		<li>Unique ions (sequence + charge + modifications)</li>
	</td>
	<td valign="top">
		<img src="<yrcwww:link path='images/docs/protinfer_opts1.png'/>" border="1" align="middle"/>
	</td>
	</tr>
	</table>
	<br/><br/>
	If the "Remove Ambiguous Spectra" option is checked <br/><br/>
	<img src="<yrcwww:link path='images/docs/protinfer_opts2.png'/>" border="1"/>
	<br/>
	<br/>
	any spectra that have 2 or more Percolator results that pass the q-value threshold are removed from the analysis.
	<br/><br/>
	
	<a name="PI_OPTIONS_3"></a>
	Protein matches for peptides are re-calculated if the "Refresh Protein Matches" option is checked.  
	Otherwise, protein matches reported in Sequest's SQT files are used. 
	The protein inference process will take longer to run if this option is checked.  
	<br/>
	<b>NOTE: </b>Sequest may not report all protein matches for a peptide if the number of matches exceeds a hard-coded limit in Sequest. 
	At the time of writing this documentation the limit was 21 proteins.
	<br/><br/>
	<img src="<yrcwww:link path='images/docs/protinfer_opts3.png'/>" border="1"/>
	<br/><br/>
	
	Isoleucine / Leucine substitutions are allowed while calculating protein matches if the second option above is checked. 
	
	<br/>
	<br/>
	<!--  
	<div style="font-weight:bold; background-color:#FFEF7F; border: 1px gray dotted;">
	Results View<br/></div>
	Coming Soon...
	
	<br/>
	<br/>
	<div style="font-weight:bold; background-color:#FFEF7F; border: 1px gray dotted;">
	Normalized Spectrum Abundance Factor (NSAF)<br/></div>
	Coming Soon...
	-->
</yrcwww:contentbox>