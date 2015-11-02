<%@ taglib uri="/WEB-INF/yrc-www.tld" prefix="yrcwww" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>

<head>
 <title>MSDaPl Docs: Protein Inference Comparison</title>
 <link REL="stylesheet" TYPE="text/css" HREF="<yrcwww:link path='/css/global.css' />">
</head>

<yrcwww:contentbox centered="true" width="90" widthRel="true" title="Protein inference comparison">
	
	<div style="border: 1px dotted; margin:10x; padding:10px;">
	MSDaPl supports comparing results from two or more protein inference runs.  These can be results from the protein inference
	program implemented in MSDaPl and/or ProteinProphet results. 
	<br/>
	<br/>
	
	<!-- OPTIONS -->
	<div style="font-weight:bold; background-color:#FFEF7F; border: 1px gray dotted;">
	Options<br/></div>
	
	<li>The default behavior is to include all parsimonious proteins from a dataset as well as any non-parsimonious proteins
	    that were inferred as parsimonious in one or more of the other datasets being compared. <br/>
	(<b>NOTE: </b> For ProteinProphet parsimonious = NOT subsumed.)  
	You can change this behavior by selecting one of the other two available options:</li>
	<div align="center" style="margin:10px;"><img src="<yrcwww:link path='images/docs/comparison1.png'/>" border="1"/></div>
	
	<br/>
	The "All" options will include all proteins from each dataset being compared.<br/>
	The "Parsimonious ONLY" option will inlcude only parsimonious proteins from each dataset.  
	This means that if a protein was parsimonious in dataset1 and non-parsimonious in dataset2, it will be listed as missing 
	in dataset2 in the comparison analysis.
	<br/><br/>
	
	<li>Proteins can be filtered on the accession strings in the fasta file(s) used for peptide search.
	Support for filtering on common names has also been added.</li>
	<br/><br/>
	
	<li>Filtering criteria can either be applied to individual proteins or to protein groups
	(only when "Group Indistinguishable Proteins" is checked).</li>
	
	<div align="center" style="margin:10px;"><img src="<yrcwww:link path='images/docs/comparison3.png'/>" border="1"/></div>
	
	With "Keep Protein Groups" checked a protein group is filtered out of the final list only if ALL members
	of the group fail to pass the filtering criteria.
	<br/><br/>
	
	<!-- COMPARISON WITH INDISTINGUISHABLE PROTEIN GROUPS -->
	<br/>
	<div style="font-weight:bold; background-color:#FFEF7F; border: 1px gray dotted;">
	Comparison with indistinguishable protein groups<br/></div>
	When comparing protein runs you can choose to <b><span style="color:red">group indistinguishable proteins</span></b>. This is the default option.
	If this option is NOT selected information about shared peptides among proteins is ignored when displaying the results. 
	With this option checked, proteins with identical set of peptides (indistinguishable proteins) are displayed together.
	The figure below explains the process of building a list of indistinguishable protein groups from 2 datasets being compared.
	<br/>
	<div align="center" style="margin:10px;"><img src="<yrcwww:link path='images/docs/comparison2.png'/>" border="1"/></div>
	<br/><br/>
	
	The results from the comparison in the figure above will be displayed as: 
	<br/>
	<div align="center" style="margin:10px;"><img src="<yrcwww:link path='images/docs/comparison4.png'/>" border="1"/></div>
	<br/><br/>
	
	
	<!-- SPECTRUM COUNTS -->
	<br/>
	<div style="font-weight:bold; background-color:#FFEF7F; border: 1px gray dotted;">
	Spectrum Counts<br/></div>
	Two numbers are displayed in the spectrum counts columns for a protein in each dataset.
	The first is the number of filtered (after any cutoffs applied during the protein inference process) spectra 
	for a protein.  The second number, in parentheses, is the normalized spectrum count. 
	Normalization is done using the total (filtered) spectrum counts for the datasets being compared. 
	
	
	<!-- FREQUENTLY ASKED QUESTIONS -->
	<br/><br/>
	<a name="comparison_FAQ"/>
	<div style="font-weight:bold; background-color:#FFEF7F; border: 1px gray dotted;">
	Frequently Asked Questions<br/></div>
	<ul>
	<li><b>Q.</b> Why is the number of proteins and protein groups displayed in the comparison view
	          more than the numbers in the protein inference view, or the numbers 
	          displayed on the main project page?
	          <br/>
	          <b>A.</b> (part 1) The number of proteins in the comparison view depends on the options 
	          used for comparison. 
	          <div align="center" style="margin:10px;"><img src="<yrcwww:link path='images/docs/comparison1.png'/>" border="1"/></div>
	          With the default option the number of proteins included from a dataset may be more than the number of parsimonious proteins
	          in the dataset. The default option is to select all proteins from a dataset that were either parsimonious in that 
	          dataset or one of the other datasets in the comparison analysis.  Choose the "Parsinonious ONLY" option to limit 
	          the analysis to only parsimonious proteins in each dataset.
	          <br/><br/>
	          <b>A.</b> (part 2) The number of protein groups reported is in the context of the comparison analysis.  The comparison process
	          pools all the individual filtered proteins from each dataset and creates a bi-partite graph connecting proteins with peptides.
	          The proteins are then grouped again into indistinguishable proteins. These groups may not be identical to those in the 
	          original datasets due to possibly different peptide identifications.
	          <br/>
	          In the figure above, there were 3 protein groups in Dataset1 before comparison but 4 after comparison since one of the groups 
	          (proteins B,C,D) was split up. This happened because Dataset 2  had a unique peptide for protein D.
	
	</li>
	
	</ul>
	
	</div>
	
</yrcwww:contentbox>