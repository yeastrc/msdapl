<%@ taglib uri="/WEB-INF/yrc-www.tld" prefix="yrcwww" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>

<yrcwww:notauthenticated>
 <logic:forward name="authenticate" />
</yrcwww:notauthenticated>

<logic:notPresent name="editProjectForm">
	<logic:forward name="editProject" />
</logic:notPresent>
 
<%@ include file="/includes/header.jsp" %>

<%@ include file="/includes/errors.jsp" %>



<script type="text/javascript">

<bean:size name="editProjectForm" property="researcherList" id="researchers_size"/>
var lastResearcherIndex = <bean:write name="researchers_size" />
var researcherList = "<option value='0'>None</option>";
<logic:iterate name="researchers" id="researcher">
	var id = <bean:write name="researcher" property="ID"/>;
	var name = "<bean:write name='researcher' property='listing' />"
	researcherList += "<option value='"+id+"'>"+name+"</option>";
</logic:iterate>

function removeResearcher(rowIdx) {
	//alert("removing researcher at index "+rowIdx);
	$("#researcherRow_"+rowIdx).hide();
	$("#researcherRow_"+rowIdx+" select").val(0);
}

function confirmRemoveResearcher(rowIdx) {
	if(confirm("Are you sure you want to remove this researcher from the project?")) {
		removeResearcher(rowIdx);
	}
}

function addResearcher() {
	
	//alert("last researcher: "+lastResearcherIndex);
	var newRow = "<tr id='researcherRow_"+lastResearcherIndex+"'>";
	newRow += "<td width='25%' valign='top'>Researcher: </td>";
	newRow += "<td width='25%' valign='top'>";
	newRow += "<select name='researcher["+lastResearcherIndex+"].ID'>";
	newRow += researcherList;
	newRow += "</select>";
	newRow += " <a href='javascript:confirmRemoveResearcher("+lastResearcherIndex+")' style='color:red; font-size:8pt;'>[Remove]</a>";
	newRow += "</td>";
	newRow +="</tr>";
	if(lastResearcherIndex == 0) {
		$("#piRow").after(newRow);
	}
	else {
		$("#researcherRow_"+(lastResearcherIndex-1)).after(newRow);
	}
	lastResearcherIndex++;
}

</script>


<yrcwww:contentbox title="Edit Project Details" centered="true" width="750">

 <CENTER>
  <html:form action="saveProject" method="post" styleId="form1">
  <html:hidden name="editProjectForm" property="ID"/>
  
  
  <TABLE CELLPADDING="no" CELLSPACING="0" width="90%" align="center">

   <tr>
   		<td width="25%" valign="top"><b>Affiliation*</b></td>
   		<td WIDTH="75%" VALIGN="top">
   			<html:select property="affiliationName" styleId="affiliation">
	            <html:options collection="affiliationTypes" property="name" labelProperty="longName"/>
            </html:select>
   		</td>
   </tr>
   
   <TR id="piRow">
    <TD WIDTH="25%" VALIGN="top">PI:</TD>
    <TD WIDTH="75%" VALIGN="top">
    
    	<html:select property="PI">
    		<html:option value="0">None</html:option>
			<html:options collection="researchers" property="ID" labelProperty="listing"/>
    	</html:select>
    
    </TD>
   </TR>
   
   <logic:iterate id="researcher" property="researcherList" name="editProjectForm" indexId="cnt">
   <!-- index is 0-based -->
   <tr id="researcherRow_<%=cnt%>" >
   	<TD WIDTH="25%" VALIGN="top">Researcher:</TD>
   	<td WIDTH="25%" VALIGN="top">
   		<html:select name="researcher" property="ID" indexed="true">
   			<html:option value="0">None</html:option>
			<html:options collection="researchers" property="ID" labelProperty="listing"/>
   		</html:select>
   		<a href="javascript:confirmRemoveResearcher('<%=(cnt)%>')" style="color:red; font-size:8pt;">[Remove]</a>
   	</td>
   </tr>
   </logic:iterate>

	<tr>
   		<td colspan="2" align="center"><a href="javascript:addResearcher()">Add Researcher</a></td>
   	</tr>
	<tr><td colspan="2"><hr width="100%"></td></tr>

   

   <TR>
    <TD WIDTH="25%" VALIGN="top">Project Title: </TD>
    <TD WIDTH="75%" VALIGN="top"><html:text property="title" size="60" maxlength="80"/></TD>
   </TR>
   
   <TR>
    <TD WIDTH="25%" VALIGN="top">Abstract:</TD>
    <TD WIDTH="75%" VALIGN="top"><html:textarea property="abstract" rows="7" cols="50"/></TD>
   </TR>

   <TR>
    <TD WIDTH="25%" VALIGN="top">Progress:</TD>
    <TD WIDTH="75%" VALIGN="top"><html:textarea property="progress" rows="7" cols="50"/></TD>
   </TR>

   <TR>
    <TD WIDTH="25%" VALIGN="top">Publications: </TD>
    <TD WIDTH="75%" VALIGN="top"><html:textarea property="publications" rows="5" cols="50"/></TD>
   </TR>

   <TR>
    <TD WIDTH="25%" VALIGN="top">Comments:</TD>
    <TD WIDTH="75%" VALIGN="top"><html:textarea property="comments" rows="5" cols="50"/></TD>
   </TR>

	<tr><td colspan="2"><hr width="100%"></td></tr>

	<!-- ===================================================================================== -->
	<!--  List grants here -->
	<%@ include file="grantListForm.jsp" %>
	<!-- ===================================================================================== -->
	
	<tr><td colspan="2"><hr width="100%"></td></tr>

  </TABLE>
  


 <P><NOBR>
 <html:submit value="Save Changes" styleClass="plain_button" />
 <input type="button" class="plain_button" onclick="javascript:onCancel(<bean:write name="editProjectForm" property="ID"/>);" value="Cancel"/>
 </NOBR>
 
  </html:form>

<script type="text/javascript">
	function onCancel(projectID) {
		document.location = "<yrcwww:link path='viewProject.do?ID=' />"+projectID;
	}
</script>

 </CENTER>

</yrcwww:contentbox>

<%@ include file="/includes/footer.jsp" %>