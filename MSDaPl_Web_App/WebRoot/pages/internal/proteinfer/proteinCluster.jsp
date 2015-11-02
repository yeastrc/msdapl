
<%@page import="org.yeastrc.www.proteinfer.idpicker.WIdPickerCluster"%>	
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>

<!-- PROTEINS TABLE -->
<br>
<div style="padding: 2px; cursor: pointer;" class="pinfer_header_small protgrplist">
 <b>Proteins in  Cluster <bean:write name="clusterLabel" /></b>
</div>
<br>

<table cellpadding="2" cellspacing="2" align="center" width="90%"  id="prot_grp_table_<bean:write name="clusterLabel" />">
 <thead>
 <tr>
 <th><b><font size="2pt">Protein<br>Group ID</font></b></th>
 <th width="1%"><span class="tooltip" title="Subset / Not Subset">S</span></th> <!-- column header for subset protein -->
 <th><b><font size="2pt">Accession(s)</font></b></th>
 <th><b><font size="2pt"># Peptides<br>(Unique)</font></b></th>
 <th><b><font size="2pt"># Spectra</font></b></th>
 </tr>
 </thead>
 
 <tbody>
 <logic:iterate name="cluster" property="proteinGroups" id="protGrp">
  <tr id="protGrp_<bean:write name="protGrp" property="proteinGroupLabel" />">
     <td valign="middle" style="text-align: center;">
     <span onclick="highlightProteinAndPeptides('<bean:write name="protGrp" property="proteinGroupLabel" />', '<bean:write name="protGrp" property="nonUniqMatchingPeptideGroupLabelsString" />', '<bean:write name="protGrp" property="uniqMatchingPeptideGroupLabelsString" />')"
     style="cursor:pointer;text-decoration:underline"><bean:write name="protGrp" property="proteinGroupLabel" />
     </span>
     </td>
     <logic:equal name="protGrp" property="isSubset" value="true">
		<td style="background-color:#FFA07A;"></td>
	 </logic:equal>
	 <logic:equal name="protGrp" property="isSubset" value="false">
		<td></td>
	 </logic:equal>
     <td>
        <logic:iterate name="protGrp" property="proteins" id="prot" >
            <logic:equal name="prot" property="protein.isParsimonious" value="true"><b></logic:equal>
            <div onclick="showProteinDetails(<bean:write name="prot" property="protein.id" />)"
                 style="text-decoration: underline; cursor: pointer">
                 <logic:equal name="prot" property="protein.isParsimonious" value="false"><font color="#888888"></logic:equal>
                 <logic:iterate name="prot" property="proteinListing.fastaReferences" id="reference" indexId="index">
                 	<logic:greaterThan name="index" value="0">, </logic:greaterThan>
                 	<bean:write name="reference" property="shortAccession" />
                 </logic:iterate>
                 
                 <logic:equal name="prot" property="protein.isParsimonious" value="false"></font></logic:equal>
            </div>
            <logic:equal name="prot" property="protein.isParsimonious" value="true"></b></logic:equal>
        </logic:iterate>
     </td>
     <td style="text-align: center;" ><bean:write name="protGrp" property="matchingPeptideCount" />(<bean:write name="protGrp" property="uniqMatchingPeptideCount" />)</td>
     <td style="text-align: center;"><bean:write name="protGrp" property="spectrumCount" /></td>
 </tr>
 </logic:iterate>
 </tbody>
        
</table>
<br>



<!-- PROTEINS-PEPTIDE ASSOCIATION TABLE -->
<bean:size name="cluster" property="proteinGroups" id="protGroupsSize"/>
<logic:greaterEqual name="protGroupsSize" value="2">
<br><div style="padding: 2px" class="pinfer_header_small" ><b>Protein - Peptide Association</b></div><br>
<table id="assoctable_<bean:write name="clusterLabel" />"
       cellpadding="4" cellspacing="2" align="center" class="draggable">
    
    <thead> 
    <tr>
      <th><b><font size="2pt">Group ID <br>(Peptide / Protein)</font></b></th>
      <logic:iterate name="cluster" property="proteinGroups" id="protGrp" >
      
      		<%String style="font-weight:bold;"; %>
      		<!-- Parsimonious or Not parsimonious -->
      		<logic:equal name="protGrp" property="isParsimonious" value="true">
      			<% style += " color:red;"; %>
      		</logic:equal>
            
            <!-- Subset or non-subset -->
            <logic:equal name="protGrp" property="isSubset" value="true">
            	<% style += " background-color:#FFA07A;"; %>
      		</logic:equal>
            
            <th style="<%=style %>"><bean:write name="protGrp" property="proteinGroupLabel" /></th>
            
       </logic:iterate>
    </tr>
    </thead>

	<%
	    WIdPickerCluster cluster = (WIdPickerCluster)request.getAttribute("cluster");
	%>
	<tbody>
	<logic:iterate name="cluster" property="peptideGroups" id="peptGrp" type="org.yeastrc.www.proteinfer.idpicker.WIdPickerPeptideGroup">
    <tr>
       	<th><b><font size="2pt"><bean:write name="peptGrp" property="peptideGroupLabel" /></font></b></th>
		<logic:iterate name="cluster" property="proteinGroups" id="protGrp" type="org.yeastrc.www.proteinfer.idpicker.WIdPickerProteinGroup">
	    	<td id="peptEvFor_<bean:write name="protGrp" property="proteinGroupLabel" />_<bean:write name="peptGrp" property="peptideGroupLabel" />" style="text-align: center;">
	         	<%if(cluster.proteinAndPeptideGroupsMatch(protGrp.getProteinGroupLabel(), peptGrp.getPeptideGroupLabel())) { %>
	          	 x
	          	<%} else {%>&nbsp;<%} %>
	        </td>
		</logic:iterate>
    </tr>
    </logic:iterate>
    </tbody>
</table>
</logic:greaterEqual>
<br>

<!-- PEPTIDES TABLE -->

<div style="padding: 2px; cursor: pointer;" class="peptgrplist pinfer_header_small" ><b>Peptides in Cluster <bean:write name="clusterLabel" /></b></div><br>
<table cellpadding="4" cellspacing="2" align="center" width="90%" id="pept_grp_table_<bean:write name="clusterLabel" />">

        <thead>
        <tr>
        <th><b><font size="2pt">Peptide<br>Group ID</font></b></th>
        <th><b><font size="2pt">Sequence(s)</font></b></th>
        <th><b><font size="2pt"># Spectra</font></b></th>
        </tr>
        </thead>
        
        <tbody>
        <logic:iterate name="cluster" property="peptideGroups" id="peptGrp">
        	<logic:iterate name="peptGrp" property="peptides" id="pept">
        	<tr class="peptGrp_<bean:write name="pept" property="peptideGroupLabel" />">
        		<td style="text-align: center;"><bean:write name="pept" property="peptideGroupLabel" /></td>
        		<td><bean:write name="pept" property="sequence" /></td>
        		<td style="text-align: center;"><bean:write name="pept" property="spectrumCount" /></td>
        	</tr>
       		</logic:iterate>
        </logic:iterate>
        </tbody>

</table>

<!--
<div align="center" style="font-weight:bold;"><a href="viewAlignedClusterProteins.do?pinferId=<bean:write name='pinferId' />&clusterLabel=<bean:write name='clusterLabel' />" >View Alignment</></div>
-->


