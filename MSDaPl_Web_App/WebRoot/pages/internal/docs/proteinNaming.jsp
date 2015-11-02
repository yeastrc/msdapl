<%@ taglib uri="/WEB-INF/yrc-www.tld" prefix="yrcwww" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>

<head>
 <title>MSDaPl Docs: Protein Common Names & Descriptions</title>
 <link REL="stylesheet" TYPE="text/css" HREF="<yrcwww:link path='/css/global.css' />">
</head>

<yrcwww:contentbox centered="true" width="90" widthRel="true" title="Protein Common Names & Descriptions">
	
	<div style="border: 1px dotted; margin:10x; padding:10px;">
		This document applies to the names and descriptions displayed in the protein inference and 
		comparision pages.<br/>
		Common names are displayed only for proteins from the following supported species:
		<ul>
			<li><i>Saccharomyces cerevisiae</i></li>
			<li><i>Schizosaccharomyces pombe</i></li>
			<li><i>Caenorhabditis elegans</i></li>
			<li><i>Drosophila melanogaster</i></li>
			<li><i>Homo sapiens</i></li>
		</ul>
		An attempt is made to display the most relevant description for a protein.
		    For supported species this description comes from the species specific databases
		<ul>
			<li><a href="http://www.yeastgenome.org/" target="_blank">SGD</a> for <i>S. cerevisiae</i></li>
			<li><a href="http://old.genedb.org/genedb/pombe/" target="_blank">Sanger Pombe</a> for <i>S. pombe</i></li>
			<li><a href="http://www.wormbase.org/" target="_blank">WormBase</a> for <i>C. elegans</i></li>
			<li><a href="http://www.genenames.org/" target="_blank">HGNC (HUGO)</a> for <i>H. sapiens</i></li>
		</ul>
		If a description is not found in a species specific database, other databases are queried in the following order:
		<ul>
			<li>Swiss-Prot</li>
			<li>NCBI-NR</li>
		</ul>
		<b>NOTE: </b> An exception is made for <i>D. melanogaster</i>. Since <a href="http://flybase.org/">FlyBase</a> descriptions may
		not provide the information most researchers are interested in, descriptions for <i>Drosophila</i> proteins
		are taken either from Swiss-Prot or NCBI-NR. If no description was found in these two databases,
		 FlyBase descriptions are displayed.
		<br/><br/>
		In the protein inference and comparison pages, descriptions from the fasta file used for the peptide search are also
		shown in addition to the best description determined above.  If this description is identical to the
		best description it is ignored.  When multiple descriptions are available for a protein, only one is shown by default.
		The other available descriptions can be be made visible clicking on the [+] link or the [Full Descriptions] link.
		
		<div align="center" style="margin:10px;"><img src = "<yrcwww:link path='/images/docs/names_desc1.png'/>" border="1"/></div>
		<div align="center" style="margin:10px;"><img src = "<yrcwww:link path='/images/docs/names_desc2.png'/>" border="1"/></div>
		
		
	</div>
	
</yrcwww:contentbox>