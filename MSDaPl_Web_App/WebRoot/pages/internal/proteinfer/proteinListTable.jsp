
<%@page import="org.yeastrc.ms.domain.protinfer.SORT_BY"%>
<%@page import="org.yeastrc.ms.domain.protinfer.SORT_ORDER"%>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>

<div id="resultPager1"  style="margin-top: 10px; margin-left: 10px;">

	<%int currPage = (Integer)(request.getAttribute("currentPage")); %>
	
	<logic:notEqual name="onFirst" value="true">
		<span style="text-decoration: underline; cursor: pointer;" onclick="pageResults(1)">
			First</span> &nbsp;
		<span style="cursor: pointer;" onclick="pageResults(<%=currPage - 1%>)">
			&lt;&lt;
		</span>
	</logic:notEqual>
	
	<logic:iterate name="pages" id="pg">
		<logic:notEqual name="pg" value="<%=String.valueOf(currPage) %>">
			<span style="text-decoration: underline; cursor: pointer;" onclick="pageResults(<bean:write name="pg"/>)">
				<bean:write name="pg"/>
			</span> &nbsp;
		</logic:notEqual>
		<logic:equal name="pg" value="<%=String.valueOf(currPage) %>">
			<bean:write name="pg"/> &nbsp;
		</logic:equal>
	</logic:iterate>
	
	<logic:notEqual name="onLast" value="true">
		<span style="cursor: pointer;" onclick="pageResults(<%=currPage + 1%>)">
			&gt;&gt;
		</span>  &nbsp;
		<span style="text-decoration: underline; cursor: pointer;" onclick="pageResults(<bean:write name="pageCount" />)">
			Last</span>
	</logic:notEqual>
	&nbsp; &nbsp; Page <bean:write name="currentPage" /> of <bean:write name="pageCount" />
</div>

<div style="margin:top: 3px;">
<span class="underline clickable" style="font-size:8pt;color:red;" id="full_names" onclick="toggleFullNames()">[Full Names]</span> &nbsp; &nbsp;
<span class="underline clickable" style="font-size:8pt;color:red;" id="full_descriptions" onclick="toggleFullDescriptions()">[Full Descriptions]</span>
</div>
<%
 SORT_BY sortBy = (SORT_BY)request.getAttribute("sortBy");
 SORT_ORDER sortOrder = (SORT_ORDER)request.getAttribute("sortOrder");
 String sortedClass = "";
 if(sortOrder == SORT_ORDER.ASC)	sortedClass = "sorted-asc";
 else sortedClass = "sorted-desc";
 %>

<%
int columnSpan = 12;
if(request.getAttribute("yeastAbundances") != null)
	columnSpan++;
if(request.getAttribute("philiusResults") != null)
	columnSpan+=2;
 %>
<bean:define id="myColspan" value="<%=String.valueOf(columnSpan) %>"/>


<table cellpadding="0" cellspacing="0" align="center" width="99%"  id="protlisttable" class="table_pinfer" style="margin-top:10;">

	<logic:notEmpty name="proteinGroups">
		<thead>
		<tr>
		
		<% String colSortedClass = "";
			 if(sortBy == SORT_BY.GROUP_ID) colSortedClass = sortedClass;
		%>
		<logic:equal name="displayColumns" property="showGroupId" value="true">
		<th class="sortable def_sort_asc <%=colSortedClass %>" width="1%" id="<%=SORT_BY.GROUP_ID.name()%>">
			<b><font size="2pt">Grp</font></b>
		</th>
		</logic:equal>
		
		
		<logic:equal name="displayColumns" property="showValidation" value="true">
		<!-- Make protein annotation sortable only if indistinguishable proteins are NOT grouped together -->
		<logic:equal name="groupProteins" value="true">
			<th width="1%"><b><span class="tooltip" title="User Validation">V</span></b></th>
		</logic:equal>
		<logic:equal name="groupProteins" value="false">
			<% colSortedClass = "";
			 if(sortBy == SORT_BY.VALIDATION_STATUS) colSortedClass = sortedClass;
			%>
			<th width="1%" class="sortable def_sort_asc <%=colSortedClass %>" id="<%=SORT_BY.VALIDATION_STATUS.name()%>" ><b><font size="2pt">
				<span class="tooltip" title="User Validation">V</span></font></b></th>
		</logic:equal>
		</logic:equal>
		
		
		<logic:equal name="displayColumns" property="showFastaId" value="true">
		<th><span class="tooltip" title="Subset Protein">S</span></th> <!-- column header for subset protein -->
		<!-- Make FastaID sortable only if indistinguishable proteins are NOT grouped together -->
		<logic:equal name="groupProteins" value="true">
			<th width="5%">Fasta ID</th>
		</logic:equal>
		<logic:equal name="groupProteins" value="false">
			<% colSortedClass = "";
			 if(sortBy == SORT_BY.ACCESSION) colSortedClass = sortedClass;
			%>
			<th width="5%" class="sortable def_sort_asc <%=colSortedClass %>" id="<%=SORT_BY.ACCESSION.name()%>"><b><font size="2pt">Fasta ID</font></b></th>
		</logic:equal>
		</logic:equal>
		
		
		<logic:equal name="displayColumns" property="showCommonName" value="true">
		<th width="5%"><b><font size="2pt">Common<br>Name</font></b></th>
		</logic:equal>
		
		
		<logic:equal name="displayColumns" property="showDescription" value="true">
		<th><b><font size="2pt">Description</font></b></th>
		</logic:equal>
		
		
		<logic:equal name="displayColumns" property="showMolWt" value="true">
		<% colSortedClass = "";
		 if(sortBy == SORT_BY.MOL_WT) colSortedClass = sortedClass;
		%>
		<th class="sortable def_sort_desc <%=colSortedClass %>" width="5%" id="<%=SORT_BY.MOL_WT.name()%>">
			<b><font size="2pt">Mol.Wt.</font></b>
		</th>
		</logic:equal>
		
		
		<logic:equal name="displayColumns" property="showPi" value="true">
		<% colSortedClass = "";
		 if(sortBy == SORT_BY.PI) colSortedClass = sortedClass;
		%>
		<th class="sortable def_sort_desc <%=colSortedClass %>" width="3%" id="<%=SORT_BY.PI.name()%>">
			<b><font size="2pt">pI</font></b>
		</th>
		</logic:equal>
		
		
		<logic:equal name="displayColumns" property="showPhiliusAnnotations" value="true">
			<th width="1%" title="Trans Membrane" class="tooltip">
				<b><font size="2pt">TM</font></b>
			</th>
			<th width="1%" title="Signal Peptide" class="tooltip">
				<b><font size="2pt">SP</font></b>
			</th>
		</logic:equal>
		
		
		<logic:equal name="displayColumns" property="showCoverage" value="true">
		<% colSortedClass = "";
		 if(sortBy == SORT_BY.COVERAGE) colSortedClass = sortedClass;
		%>
		<th class="sortable def_sort_desc <%=colSortedClass %>" width="3%" id="<%=SORT_BY.COVERAGE.name()%>">
			<b><font size="2pt">Cov.<br/>(%)</font></b>
		</th>
		</logic:equal>
		
		
		
		<logic:equal name="displayColumns" property="showNsaf" value="true">
		<% colSortedClass = "";
		 if(sortBy == SORT_BY.NSAF) colSortedClass = sortedClass;
		%>
		<th class="sortable def_sort_desc <%=colSortedClass %>" width="5%" id="<%=SORT_BY.NSAF.name()%>">
			<b><font size="2pt">NSAF**</font></b>
		</th>
		</logic:equal>
		
		
		<logic:equal name="displayColumns" property="showYeastCopiesPerCell" value="true">
			<th width="5%">
				<b><font size="2pt">Copies /<br>cell***</font></b>
			</th>
		</logic:equal>
		
		
		<logic:equal name="displayColumns" property="showNumPept" value="true">
		<% colSortedClass = "";
			 if(sortBy == SORT_BY.NUM_PEPT) colSortedClass = sortedClass;
		%>
		<th class="sortable def_sort_desc <%=colSortedClass %>" width="1%" id="<%=SORT_BY.NUM_PEPT.name()%>">
			<b><font size="2pt"># Pept.</font></b>
		</th>
		</logic:equal>
		
		
		<logic:equal name="displayColumns" property="showNumUniqPept" value="true">
		<% colSortedClass = "";
			 if(sortBy == SORT_BY.NUM_UNIQ_PEPT) colSortedClass = sortedClass;
		%>
		<th class="sortable def_sort_desc <%=colSortedClass %>" width="1%" id="<%=SORT_BY.NUM_UNIQ_PEPT.name()%>">
			<b><font size="2pt"># Uniq. Pept.</font></b></th>
		</logic:equal>
		
		
		
		<logic:equal name="displayColumns" property="showNumSpectra" value="true">
		<% colSortedClass = "";
			 if(sortBy == SORT_BY.NUM_SPECTRA) colSortedClass = sortedClass;
		%>
		<th class="sortable def_sort_desc <%=colSortedClass %>" width="1%" id="<%=SORT_BY.NUM_SPECTRA.name()%>">
			<b><font size="2pt"># Spectra</font></b></th>
		</logic:equal>
		
		
		<logic:equal name="displayColumns" property="showClusterId" value="true">
		<% colSortedClass = "";
			 if(sortBy == SORT_BY.CLUSTER_ID) colSortedClass = sortedClass;
		%>
		<th class="sortable def_sort_asc <%=colSortedClass %>" width="1%" id="<%=SORT_BY.CLUSTER_ID.name()%>">
			<b><font size="2pt">Clust.</font></b></th>
		</logic:equal>
		
			
		</tr>
		</thead>
	</logic:notEmpty>
	<tbody>
	
	
	<logic:iterate name="proteinGroups" id="proteinGroup">
	
	
		<!--  WE ARE NOT DISPLAYING THE PROTEIN GROUP MEMBERS TOGETHER -->
		<logic:equal name="groupProteins" value="false">
			<bean:define value="1" id="rowspan" />
			
		<logic:iterate name="proteinGroup" property="proteins" id="protein">
		
		<tr class="protgrp_row sorting_row">
		
			<logic:equal name="displayColumns" property="showGroupId" value="true">
				<td rowspan="<bean:write name="rowspan" />" valign="middle">
					<bean:write name="proteinGroup" property="proteinGroupLabel" />
				</td>
			</logic:equal>
		
		
		
			<logic:equal name="displayColumns" property="showValidation" value="true">
			<td>
			<span id="<bean:write name="protein" property="protein.id" />"
				title="<bean:write name="protein" property="accessionsCommaSeparated" />" 
				class="editprotannot"
				style="text-decoration: underline; cursor: pointer" >
				
			<!-- Are there user entered comments for this protein -->
			<logic:present name="protein" property="protein.userAnnotation">
				<span 
					id="annot_comment_<bean:write name="protein" property="protein.id" />" 
					style="display: none;"><bean:write name="protein" property="protein.userAnnotation" /></span>
			</logic:present>
			<logic:notPresent name="protein" property="protein.userAnnotation">
				<span id="annot_comment_<bean:write name="protein" property="protein.id" />" style="display: none;"></span>
			</logic:notPresent>
			
			<!-- User entered validation -->
				<span 
				class="prot_annot_<bean:write name="protein" property="protein.userValidation.statusChar" />"
				id="annot_validation_style_<bean:write name="protein" property="protein.id" />"></span>
				<span 
				class="sort_key"
				style="display: none;"
				id="annot_validation_text_<bean:write name="protein" property="protein.id" />"><bean:write name="protein" property="protein.userValidation.statusChar" /></span>
					
			</span>
			</td>
			</logic:equal>
			
			
			
			<!-- Protein accession -->
			<logic:equal name="displayColumns" property="showFastaId" value="true">
			
			<logic:equal name="protein" property="protein.isSubset" value="true">
				<td style="background-color:#FFA07A;"></td>
			</logic:equal>
			<logic:equal name="protein" property="protein.isSubset" value="false">
				<td></td>
			</logic:equal>
			
			<td> 
			<logic:equal name="protein" property="protein.isParsimonious" value="false"><font color="#888888"></logic:equal>
			<logic:equal name="protein" property="protein.isParsimonious" value="true"><b></logic:equal>
			<logic:equal name="protein" property="protein.isParsimonious" value="false"><font color="#888888"></logic:equal>
			<span onclick="showProteinDetails(<bean:write name="protein" property="protein.id" />)" 
					style="display:none;"
					class="full_name clickable underline">
			<logic:iterate name="protein" property="proteinListing.fastaReferences" id="reference">
				<bean:write name="reference" property="accession"/>
				<br/>
			</logic:iterate>
			</span>
			
			<span onclick="showProteinDetails(<bean:write name="protein" property="protein.id" />)" 
					class="short_name clickable underline">
			<logic:iterate name="protein" property="proteinListing.fastaReferences" id="reference">
				<bean:write name="reference" property="shortAccession"/>
				<br/>
			</logic:iterate>
			</span>
			<logic:equal name="protein" property="protein.isParsimonious" value="false"></font></logic:equal>
			<logic:equal name="protein" property="protein.isParsimonious" value="true"></b></logic:equal>
			
			</td>
			</logic:equal>
			
			
			
			<!-- Protein common name -->
			<logic:equal name="displayColumns" property="showCommonName" value="true">
			<td> 
			<logic:equal name="protein" property="protein.isParsimonious" value="true"><b></logic:equal>
			<logic:equal name="protein" property="protein.isParsimonious" value="false"><font color="#888888"></logic:equal>
			<span onclick="showProteinDetails(<bean:write name="protein" property="protein.id" />)" 
					style="text-decoration: underline; cursor: pointer">
			<logic:iterate name="protein" property="proteinListing.commonReferences" id="reference">
				<bean:write name="reference" property="commonReference.name"/>
				<br/>
			</logic:iterate>
			</span>
			<logic:equal name="protein" property="protein.isParsimonious" value="false"></font></logic:equal>
			<logic:equal name="protein" property="protein.isParsimonious" value="true"></b></logic:equal>
			</td>
			</logic:equal>
			
			
			
			<!-- Protein description -->
			<logic:equal name="displayColumns" property="showDescription" value="true">
			<bean:size name="protein" property="proteinListing.descriptionReferences" id="refCount"/>
			
			<td style="font-size: 8pt;">
			<span class="full_description" style="display:none;" id="full_desc_<bean:write name="protein" property="protein.id" />">
			<logic:iterate name="protein" property="proteinListing.descriptionReferences" id="reference">
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
			</logic:iterate>
			<logic:greaterThan value="1" name="refCount">
				<span class="clickable" onclick="hideAllDescriptionsForProtein(<bean:write name="protein" property="protein.id" />)"><b>[-]</b></span>
			</logic:greaterThan>
			</span>
		
			<span class="short_description" id="short_desc_<bean:write name="protein" property="protein.id" />">
			<logic:present name="protein" property="oneDescriptionReference">
				<bean:write name="protein" property="oneDescriptionReference.shortDescriptionEscaped"/>
				<br/>
			</logic:present>
		
			<logic:greaterThan value="1" name="refCount">
				<span class="clickable" onclick="showAllDescriptionsForProtein(<bean:write name="protein" property="protein.id" />)"><b>[+]</b></span>
			</logic:greaterThan>
			</span>
			</td>
			</logic:equal>
			
			
			<!-- Moleculat Wt. -->
			<logic:equal name="displayColumns" property="showMolWt" value="true">
				<td><bean:write name="protein" property="molecularWeight"/></td>
			</logic:equal>
			
			<!-- pI -->
			<logic:equal name="displayColumns" property="showPi" value="true">
				<td><bean:write name="protein" property="pi"/></td>
			</logic:equal>
			
			<!-- Philius Annotations -->
			<logic:equal name="displayColumns" property="showPhiliusAnnotations" value="true">
				<logic:equal name="protein" property="transMembrane" value="false">
					<td>&nbsp;</td>
				</logic:equal>
				<logic:equal name="protein" property="transMembrane" value="true">
					<td style="vertical-align:middle; text-align:center;"><span style="width:10px;height:10px;background-color:yellow;border:1px solid gray;display:block;">&nbsp;</span></td>
				</logic:equal>
				
				<logic:equal name="protein" property="signalPeptide" value="false">
					<td>&nbsp;</td>
				</logic:equal>
				<logic:equal name="protein" property="signalPeptide" value="true">
					<td style="vertical-align:middle; text-align:center;"><span style="width:10px;height:10px;background-color:red;border:1px solid gray;display:block;">&nbsp;</span></td>
				</logic:equal>
			</logic:equal>
			
			
			<!-- Coverage -->
			<logic:equal name="displayColumns" property="showCoverage" value="true">
				<td><bean:write name="protein" property="protein.coverage"/></td>
			</logic:equal>
			
			<!-- NSAF -->
			<logic:equal name="displayColumns" property="showNsaf" value="true">
				<td><bean:write name="protein" property="protein.nsafFormatted"/></td>
			</logic:equal>
		
			<!-- YEAST abundance (copies / cell) -->
			<logic:equal name="displayColumns" property="showYeastCopiesPerCell" value="true">
				<td><bean:write name="protein" property="yeastProteinAbundanceString" filter="false"/></td>
			</logic:equal>
		
		
			<!-- # Peptides -->
			<logic:equal name="displayColumns" property="showNumPept" value="true">
			<td rowspan="<bean:write name="rowspan" />" valign="middle">
				<bean:write name="proteinGroup" property="matchingPeptideCount"/>
			</td>
			</logic:equal>
			
			
			<!-- # Unique Peptides -->
			<logic:equal name="displayColumns" property="showNumUniqPept" value="true">
			<td rowspan="<bean:write name="rowspan" />" valign="middle">
				<bean:write name="proteinGroup" property="uniqMatchingPeptideCount"/>
			</td>
			</logic:equal>
			
			
			<!-- # Spectra -->
			<logic:equal name="displayColumns" property="showNumSpectra" value="true">
			<td rowspan="<bean:write name="rowspan" />" valign="middle">
			<bean:write name="proteinGroup" property="spectrumCount"/>
			</td>
			</logic:equal>
			
			
			<!-- Cluster ID -->
			<logic:equal name="displayColumns" property="showClusterId" value="true">
			<td rowspan="<bean:write name="rowspan" />" valign="middle">
				<span id="protgrpslink" style="cursor:pointer;text-decoration:underline" 
				  onclick="showProteinCluster(<bean:write name="protein" property="protein.clusterLabel"/>)">
				<bean:write name="protein" property="protein.clusterLabel"/>
				</span>
			</td>
			</logic:equal>
			
		</tr>
		
		<!-- Display the View Peptides link for each protein in the group -->
		<tr class="pept_row linked_row pinfer_filler">
		<td valign="top" colspan="2" class="pinfer_filler"><nobr>
			<span class="showpeptForProt" 
				  style="text-decoration: underline; cursor: pointer;font-size: 7pt; color: #000000;" 
				  id="<bean:write name="protein" property="protein.id" />"
				  title="<bean:write name="proteinGroup" property="proteinGroupLabel" />"
				  >Show Peptides</span></nobr></td>
		<td colspan='<bean:write name="myColspan"/>' class="pinfer_filler">
			<!--  peptides table will go here: proteinPeptides.jsp -->
			<div id="peptforprot_<bean:write name="protein" property="protein.id" />_<bean:write name="proteinGroup" property="proteinGroupLabel" />"></div>
		</td>
		</tr>
			
		</logic:iterate>
		</logic:equal>
		
		
		
		
		
		<!-- WE ARE DISPLAYING PROTEIN GROUP MEMBERS TOGETHER -->
		<logic:equal name="groupProteins" value="true">
			<bean:define name="proteinGroup" property="proteinCount" id="rowspan" />
		
		<%boolean first = true;%>
		<logic:iterate name="proteinGroup" property="proteins" id="protein">
		
		<%if(first) { %>
		<tr class="protgrp_row sorting_row">
		
		<!-- Group ID -->
		<logic:equal name="displayColumns" property="showGroupId" value="true">
		<td rowspan="<bean:write name="rowspan" />" valign="middle">
			<bean:write name="proteinGroup" property="proteinGroupLabel" />
		</td>
		</logic:equal>
		
		<%} else {%>
			<tr class="protgrp_row linked_row">
		<%} %>
		
		<!-- Validation Status -->
		<logic:equal name="displayColumns" property="showValidation" value="true">
		<td>
		<span id="<bean:write name="protein" property="protein.id" />"
				title="<bean:write name="protein" property="accessionsCommaSeparated" />" 
				class="editprotannot"
				style="text-decoration: underline; cursor: pointer" >
				
			<!-- Are there user entered comments for this protein -->
			<logic:present name="protein" property="protein.userAnnotation">
				<span id="annot_comment_<bean:write 
				      name="protein" property="protein.id" />" 
				      style="display: none;"><bean:write name="protein" property="protein.userAnnotation" />
				</span>
			</logic:present>
			<logic:notPresent name="protein" property="protein.userAnnotation">
				<span id="annot_comment_<bean:write name="protein" property="protein.id" />" style="display: none;"></span>
			</logic:notPresent>
			
			<!-- User entered validation -->
			<span 
			class="prot_annot_<bean:write name="protein" property="protein.userValidation.statusChar" />"
			id="annot_validation_style_<bean:write name="protein" property="protein.id" />"></span>
			<span 
			class="sort_key"
			style="display: none;"
			id="annot_validation_text_<bean:write name="protein" property="protein.id" />"><bean:write name="protein" property="protein.userValidation.statusChar" /></span>
					
		</span>
		</td>
		</logic:equal>
		
			
		<!-- Protein accession -->
		<logic:equal name="displayColumns" property="showFastaId" value="true">
		<logic:equal name="protein" property="protein.isSubset" value="true">
			<td style="background-color:#FFA07A;"></td>
		</logic:equal>
		<logic:equal name="protein" property="protein.isSubset" value="false">
			<td></td>
		</logic:equal>
			
		<td>
			<logic:equal name="protein" property="protein.isParsimonious" value="true"><b></logic:equal>
			<logic:equal name="protein" property="protein.isParsimonious" value="false"><font color="#888888"></logic:equal>
			<span onclick="showProteinDetails(<bean:write name="protein" property="protein.id" />)" 
					style="display:none;"
					class="full_name clickable underline">
			<logic:iterate name="protein" property="proteinListing.fastaReferences" id="reference">
				<bean:write name="reference" property="accession"/>
				<br/>
			</logic:iterate>
			</span>
			
			<span onclick="showProteinDetails(<bean:write name="protein" property="protein.id" />)" 
					class="short_name clickable underline">
			<logic:iterate name="protein" property="proteinListing.fastaReferences" id="reference">
				<bean:write name="reference" property="shortAccession"/>
				<br/>
			</logic:iterate>
			</span>
			<logic:equal name="protein" property="protein.isParsimonious" value="false"></font></logic:equal>
			<logic:equal name="protein" property="protein.isParsimonious" value="true"></b></logic:equal>
			
		</td>
		</logic:equal>
		
		
		<!-- Protein common name -->
		<logic:equal name="displayColumns" property="showCommonName" value="true">
			<td> 
			<logic:equal name="protein" property="protein.isParsimonious" value="true"><b></logic:equal>
			<logic:equal name="protein" property="protein.isParsimonious" value="false"><font color="#888888"></logic:equal>
			<span onclick="showProteinDetails(<bean:write name="protein" property="protein.id" />)" 
					style="text-decoration: underline; cursor: pointer">
			
			<logic:iterate name="protein" property="proteinListing.commonReferences" id="reference">
				<bean:write name="reference" property="commonReference.name"/>
				<br/>
			</logic:iterate>
			
			</span>
			<logic:equal name="protein" property="protein.isParsimonious" value="false"></font></logic:equal>
			<logic:equal name="protein" property="protein.isParsimonious" value="true"></b></logic:equal>
			</td>
		</logic:equal>
		
		
		<!-- Protein Description -->
		<logic:equal name="displayColumns" property="showDescription" value="true">
		<bean:size name="protein" property="proteinListing.descriptionReferences" id="refCount"/>
			
			<td style="font-size: 8pt;">
			<span class="full_description" style="display:none;" id="full_desc_<bean:write name="protein" property="protein.id" />">
			<logic:iterate name="protein" property="proteinListing.descriptionReferences" id="reference">
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
			</logic:iterate>
			<logic:greaterThan value="1" name="refCount">
				<span class="clickable" onclick="hideAllDescriptionsForProtein(<bean:write name="protein" property="protein.id" />)"><b>[-]</b></span>
			</logic:greaterThan>
			</span>
		
			<span class="short_description" id="short_desc_<bean:write name="protein" property="protein.id" />">
			<logic:present name="protein" property="oneDescriptionReference">
				<bean:write name="protein" property="oneDescriptionReference.shortDescriptionEscaped"/>
				<br/>
			</logic:present>
		
			<logic:greaterThan value="1" name="refCount">
				<span class="clickable" onclick="showAllDescriptionsForProtein(<bean:write name="protein" property="protein.id" />)"><b>[+]</b></span>
			</logic:greaterThan>
			</span>
			</td>
		</logic:equal>
		
		
		<!-- Moleculat Wt. -->
		<logic:equal name="displayColumns" property="showMolWt" value="true">
			<td><bean:write name="protein" property="molecularWeight"/></td>
		</logic:equal>
		
		<!-- pI -->
		<logic:equal name="displayColumns" property="showPi" value="true">
			<td><bean:write name="protein" property="pi"/></td>
		</logic:equal>
		
		
		<!-- Philius Annotations -->
		<logic:equal name="displayColumns" property="showPhiliusAnnotations" value="true">
				<logic:equal name="protein" property="transMembrane" value="false">
					<td>&nbsp;</td>
				</logic:equal>
				<logic:equal name="protein" property="transMembrane" value="true">
					<td style="vertical-align:middle; text-align:center;"><span style="width:10px;height:10px;background-color:yellow;border:1px solid gray;display:block;">&nbsp;</span></td>
				</logic:equal>
				
				<logic:equal name="protein" property="signalPeptide" value="false">
					<td>&nbsp;</td>
				</logic:equal>
				<logic:equal name="protein" property="signalPeptide" value="true">
					<td style="vertical-align:middle; text-align:center;"><span style="width:10px;height:10px;background-color:red;border:1px solid gray;display:block;">&nbsp;</span></td>
				</logic:equal>
		</logic:equal>
		
		<!-- Coverage -->
		<logic:equal name="displayColumns" property="showCoverage" value="true">
			<td><bean:write name="protein" property="protein.coverage"/></td>
		</logic:equal>
		
		
		<!-- NSAF -->
		<logic:equal name="displayColumns" property="showNsaf" value="true">
			<td><bean:write name="protein" property="protein.nsafFormatted"/></td>
		</logic:equal>
		
		
		<!-- YEAST Abundance (# copies / cell) -->
		<logic:equal name="displayColumns" property="showYeastCopiesPerCell" value="true">
			<td><bean:write name="protein" property="yeastProteinAbundanceString" filter="false" /></td>
		</logic:equal>
			
		
		<%if(first) { first = false;%>
		
		<!-- # Peptides -->
		<logic:equal name="displayColumns" property="showNumPept" value="true">
		<td rowspan="<bean:write name="rowspan" />" valign="middle">
			<bean:write name="proteinGroup" property="matchingPeptideCount"/>
		</td>
		</logic:equal>
		
		<!-- # Unique Peptides -->
		<logic:equal name="displayColumns" property="showNumUniqPept" value="true">
		<td rowspan="<bean:write name="rowspan" />" valign="middle">
			<bean:write name="proteinGroup" property="uniqMatchingPeptideCount"/>
		</td>
		</logic:equal>
		
		<!-- # Spectra -->
		<logic:equal name="displayColumns" property="showNumSpectra" value="true">
		<td rowspan="<bean:write name="rowspan" />" valign="middle">
			<bean:write name="proteinGroup" property="spectrumCount"/>
		</td>
		</logic:equal>
		
		
		<!-- Cluster ID -->
		<logic:equal name="displayColumns" property="showClusterId" value="true">
		<td rowspan="<bean:write name="rowspan" />" valign="middle">
			<span id="protgrpslink" style="cursor:pointer;text-decoration:underline" 
				  onclick="showProteinCluster(<bean:write name="protein" property="protein.clusterLabel"/>)">
				<bean:write name="protein" property="protein.clusterLabel"/>
			</span>
		</td>
		</logic:equal>
		
		<%} %>
		
		</tr>
		</logic:iterate>
		
		
		<!-- List the peptides and the best match for each peptide -->
		<tr class="pept_row linked_row">
			<td valign="top" colspan="2" class="pinfer_filler"><nobr>
				<span class="showpeptForProtGrp" 
					  style="text-decoration: underline; cursor: pointer;font-size: 7pt; color: #000000;" 
					  id="<bean:write name="proteinGroup" property="proteinGroupLabel" />"
					  >Show Peptides</span></nobr></td>
			<td colspan="<bean:write name="myColspan"/>" class="pinfer_filler">
				<!--  peptides table will go here: proteinPeptides.jsp -->
				<div id="peptforprot_<bean:write name="proteinGroup" property="proteinGroupLabel" />"></div>
			</td>
		</tr>
		</logic:equal>
	</logic:iterate>
	</tbody>
</table>

<div id="resultPager2"  style="margin-top: 10px; margin-left: 10px;">

	<logic:notEqual name="onFirst" value="true">
		<span style="text-decoration: underline; cursor: pointer;" onclick="pageResults(1)">
			First</span> &nbsp;
		<span style="cursor: pointer;" onclick="pageResults(<%=currPage - 1%>)">
			&lt;&lt;
		</span>
	</logic:notEqual>
	
	<logic:iterate name="pages" id="pg">
		<logic:notEqual name="pg" value="<%=String.valueOf(currPage) %>">
			<span style="text-decoration: underline; cursor: pointer;" onclick="pageResults(<bean:write name="pg"/>)">
				<bean:write name="pg"/>
			</span> &nbsp;
		</logic:notEqual>
		<logic:equal name="pg" value="<%=String.valueOf(currPage) %>">
			<bean:write name="pg"/> &nbsp;
		</logic:equal>
	</logic:iterate>
	
	<logic:notEqual name="onLast" value="true">
		<span style="cursor: pointer;" onclick="pageResults(<%=currPage + 1%>)">
			&gt;&gt;
		</span>  &nbsp;
		<span style="text-decoration: underline; cursor: pointer;" onclick="pageResults(<bean:write name="pageCount" />)">
			Last</span>
	</logic:notEqual>
	&nbsp; &nbsp; Page <bean:write name="currentPage" /> of <bean:write name="pageCount" />
</div>
