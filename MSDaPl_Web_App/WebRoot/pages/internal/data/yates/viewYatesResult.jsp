<%@ taglib uri="/WEB-INF/yrc-www.tld" prefix="yrcwww" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>

<yrcwww:notauthenticated>
 <logic:forward name="authenticate" />
</yrcwww:notauthenticated>

<logic:empty name="result">
  <logic:forward name="viewYatesResult" />
</logic:empty>

<%@ include file="/includes/header.jsp" %>

<%@ include file="/includes/errors.jsp" %>


<yrcwww:contentbox title="DTASelect Result Peptides" centered="true" width="850" scheme="ms">

 <CENTER>
  <B>Run Information:</B><BR><BR>
 <TABLE CELLPADDING="no" CELLSPACING="0"> 

  <yrcwww:colorrow scheme="ms">
   <TD valign="top" width="25%">Bait Protein:</TD>
   <TD valign="top" width="75%">
    <logic:empty name="run" property="baitProtein">
     N/A
    </logic:empty>
    <logic:notEmpty name="run" property="baitProtein">
      <html:link action="viewProtein.do" paramId="id" paramName="run" paramProperty="baitProtein.id">
     <bean:write name="run" property="baitProtein.listing"/></html:link>
    </logic:notEmpty>
   </TD>
  </yrcwww:colorrow>

  <logic:notEmpty name="run" property="baitDesc">
  <yrcwww:colorrow scheme="ms">
   <TD valign="top" width="25%">Bait Desc.:</TD>
   <TD valign="top" width="75%"><bean:write name="run" property="baitDesc"/></TD>
  </yrcwww:colorrow>
  </logic:notEmpty>
  
  <yrcwww:colorrow scheme="ms">
   <TD valign="top" width="25%">Hit Protein:</TD>

   <TD>
				<nobr>
				 <html:link action="viewProtein.do" paramId="id" paramName="result" paramProperty="hitProtein.id">				 
				  <bean:write name="result" property="hitProtein.listing"/></html:link>
				</nobr>
   </TD>

  </yrcwww:colorrow>

  <yrcwww:colorrow scheme="ms">
   <TD valign="top" width="25%">Protein Desc:</TD>     
   <TD valign="top" width="75%"><bean:write name="result" property="hitProtein.description"/></TD> 
  </yrcwww:colorrow>

  <yrcwww:colorrow scheme="ms">
   <TD valign="top" width="25%">Sequence Coverage:</TD>
   <TD valign="top" width="75%"><bean:write name="result" property="sequenceCoverage"/>%</TD>
  </yrcwww:colorrow>

  <yrcwww:colorrow scheme="ms">
   <TD valign="top" width="25%">Organism:</TD>
   <TD valign="top" width="75%"><i><bean:write name="result" property="hitProtein.species.name"/></i></TD>
  </yrcwww:colorrow>
  <yrcwww:colorrow scheme="ms">
   <TD valign="top" width="25%">MS Run:</TD>
   <TD valign="top" width="75%">
     <html:link action="viewYatesRun.do" paramId="id" paramName="run" paramProperty="id">
     View Run</html:link></TD>
  </yrcwww:colorrow>
  <yrcwww:colorrow scheme="ms">
   <TD valign="top" width="25%">Project:</TD>
   <TD valign="top" width="75%">
     <html:link action="viewProject.do" paramId="ID" paramName="run" paramProperty="projectID">
     <bean:write name="result" property="project.title"/></html:link></TD>
  </yrcwww:colorrow>
 </TABLE>

<br><br>

 <TABLE CELLPADDING="no" CELLSPACING="0" width="90%"> 

  <yrcwww:colorrow scheme="ms">
   <TD valign="top" width="100%" colspan="2" align="center"><font style="font-size:12pt;"><b>Sequence Coverage:</b></font><br>[<a href="<yrcwww:link path='pages/internal/data/yates/aboutSequenceCoverageMap.jsp'/>">What do the colors mean?</a>]<br><br></TD>
  </yrcwww:colorrow>
  
  <yrcwww:colorrow scheme="ms">
   <TD valign="top" width="15%">Hit Protein<br>Sequence:<BR>
    <font style="font-size:8pt;">
     [<a target="blast_window"
         href="http://www.ncbi.nlm.nih.gov/blast/Blast.cgi?CMD=Web&LAYOUT=TwoWindows&AUTO_FORMAT=Semiauto&ALIGNMENTS=50&ALIGNMENT_VIEW=Pairwise&CDD_SEARCH=on&CLIENT=web&COMPOSITION_BASED_STATISTICS=on&DATABASE=nr&DESCRIPTIONS=100&ENTREZ_QUERY=(none)&EXPECT=1000&FILTER=L&FORMAT_OBJECT=Alignment&FORMAT_TYPE=HTML&I_THRESH=0.005&MATRIX_NAME=BLOSUM62&NCBI_GI=on&PAGE=Proteins&PROGRAM=blastp&SERVICE=plain&SET_DEFAULTS.x=41&SET_DEFAULTS.y=5&SHOW_OVERVIEW=on&END_OF_HTTPGET=Yes&SHOW_LINKOUT=yes&QUERY=<bean:write name="result" property="hitProtein.peptide.sequenceString"/>">NCBI BLAST</a>]

     <BR>
     [<a target="prot_param_window"
         href="http://us.expasy.org/cgi-bin/protparam?sequence=<bean:write name="result" property="hitProtein.peptide.sequenceString"/>">ProtParam</a>]
    </font>
   </TD>
   <TD valign="top" width="85%"><pre><bean:write name="result" property="formatedSequenceCoverageMap" filter="false"/></pre></TD>
  </yrcwww:colorrow>
 </TABLE>
 
 
 
 <P><TABLE CELLPADDING="no" CELLSPACING="0" WIDTH="95%">

	<yrcwww:colorrow scheme="ms">
		<TD colspan="10" align="center"><B>Run Result Peptides:</B><BR><BR></TD>
	</yrcwww:colorrow>

	
	<yrcwww:colorrow scheme="ms">
		<TD>&nbsp;</TD>
		<!--<TD><B><U>Unique</U></B></TD>-->
		<TD valign="bottom"><B><U>XCorr</U></B></TD>
		<TD valign="bottom"><B><U>Delta CN</U></B></TD>
		<TD valign="bottom"><B><U>M+H+</U></B></TD>
		<TD valign="bottom"><B><U>Total<BR>Intensity</U></B></TD>
		<TD valign="bottom"><B><U>Sp<BR>Rank</U></B></TD>
		<TD valign="bottom"><B><U>Ion<BR>Prop.</U></B></TD>
		<TD valign="bottom"><B><U>Red.</U></B></TD>
		<TD valign="bottom"><B><U>Sequence</U></B></TD>
	</yrcwww:colorrow>

	<logic:iterate id="peptide" name="result" property="peptides">
		<yrcwww:colorrow scheme="ms">
			<TD>
			 <logic:equal name="peptide" property="isSpectraAvailable" value="true">
			 	<html:link action="viewSpectra.do" paramId="id" paramName="peptide" paramProperty="id">
 			  	Spectra</html:link>
			 </logic:equal>
			 <logic:notEqual name="peptide" property="isSpectraAvailable" value="true">
			 	<logic:present name="hasYatesCycles">
			 		<html:link action="viewSpectra.do" paramId="id" paramName="peptide" paramProperty="id">
 			  		Spectra</html:link>
			 	</logic:present>
			 </logic:notEqual>
 			</TD>
			<!--<TD><bean:write name="peptide" property="unique"/></TD>-->
			<TD><bean:write name="peptide" property="XCorr"/></TD>
			<TD><bean:write name="peptide" property="deltaCN"/></TD>
			<TD><bean:write name="peptide" property="MH"/></TD>
			<TD><bean:write name="peptide" property="totalIntensity"/></TD>
			<TD><bean:write name="peptide" property="spRank"/></TD>
			<TD><bean:write name="peptide" property="ionProportion"/></TD>
			<TD><bean:write name="peptide" property="redundancy"/></TD>
			<TD><bean:write name="peptide" property="HTMLSequence" filter="false" /></TD>
		</yrcwww:colorrow>
	</logic:iterate>

 </TABLE>

 </CENTER>

</yrcwww:contentbox>

<%@ include file="/includes/footer.jsp" %>