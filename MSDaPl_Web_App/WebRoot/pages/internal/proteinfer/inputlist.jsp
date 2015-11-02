
<%@page import="org.yeastrc.ms.domain.protinfer.ProteinferInput.InputType"%>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/yrc-www.tld" prefix="yrcwww" %>


<bean:define name="index" id="index" type="java.lang.Integer"/>

<logic:equal name="inputType" value="<%=InputType.SEARCH.name()%>">

<logic:iterate name="inputList" id="inputSummary">

	<div id="<bean:write name="inputSummary" property="inputGroupId"/>">
    	<div style="background-color: #2E8B57; color: white; font-weight: bold;">
    	
    		<span style="margin-left:10;" class="foldable fold-open"
    		 id="foldable_search_<bean:write name="inputSummary" property="inputGroupId"/>_target"></span>
    		 <span>
    		 Search ID: <bean:write name="inputSummary" property="inputGroupId"/>
    		 </span>
    	</div>
    	
    	
    	<div id="foldable_search_<bean:write name="inputSummary" property="inputGroupId"/>_target">
    	
    	<div style="color: black;">
    		Search Program: 
    		<bean:write name="inputSummary" property="programName" />&nbsp;
  			<bean:write name="inputSummary" property="programVersion" />
  			<br>
  			Search Database:
  			<bean:write name="inputSummary" property="searchDatabase" /> 
    	</div>
    	<br>
    	
    
		<table width="100%">
 		<logic:iterate name="inputSummary" property="inputFiles" id="inputFile" >
		<yrcwww:colorrow scheme="pinfer" repeat="true">
			<td WIDTH="20%" VALIGN="top"> 
				<input type="checkbox" checked="checked" 
					   id="toggle_search_<bean:write name="inputSummary" property="inputGroupId"/>_file"
					   value="true"
					   name="inputFile[<%=index %>].isSelected" />
			</td>
			<td>
				<input type="hidden" value="<bean:write name="inputFile" property="inputId"/>" 
				       name="inputFile[<%=index %>].inputId"/>
				<input type="hidden" value="<bean:write name="inputFile" property="runName"/>" 
				       name="inputFile[<%=index %>].runName"/>
				<bean:write  name="inputFile" property="runName" />
			</td>
			<%index++; %>
		</yrcwww:colorrow>
		</logic:iterate>
 		</table>
		<div class="clickable toggle_selection" style="font-size: 7pt; color: #006400;" 
		     id="toggle_search_<bean:write name="inputSummary" property="inputGroupId"/>">Deselect All</div>
		</div>
		</div>
		<br>
</logic:iterate>
</logic:equal>




<logic:equal name="inputType" value="<%=InputType.ANALYSIS.name()%>">

<logic:iterate name="inputList" id="inputSummary">

	<div id="<bean:write name="inputSummary" property="inputGroupId"/>">
	
    	<div style="background-color: #939CB0; color: white; font-weight: bold;">
    	
    		<span style="margin-left:10;" class="foldable fold-open"
    		 id="foldable_analysis_<bean:write name="inputSummary" property="inputGroupId"/>">
    		 &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</span>
    		 <span>
    		 	Analysis ID: <bean:write name="inputSummary" property="inputGroupId"/>
    		 </span>
    	</div>
    	
    	
    	<div id="foldable_analysis_<bean:write name="inputSummary" property="inputGroupId"/>_target">
    	
    	<div style="color: black;">
    		Analysis Program: 
    		<bean:write name="inputSummary" property="programName" />&nbsp;
  			<bean:write name="inputSummary" property="programVersion" />
  			<br>
  			Search Database:
  			<bean:write name="inputSummary" property="searchDatabase" /> 
    	</div>
    	<br>
    	
    
		<table width="100%">
 		<logic:iterate name="inputSummary" property="inputFiles" id="inputFile" >
		<tr class="project_A">
			<td WIDTH="20%" VALIGN="top"> 
				<input type="checkbox" checked="checked" 
					   id="toggle_analysis_<bean:write name="inputSummary" property="inputGroupId"/>_file"
					   value="true"
					   name="inputFile[<%=index %>].isSelected" />
			</td>
			<td>
				<input type="hidden" value="<bean:write name="inputFile" property="inputId"/>" 
				       name="inputFile[<%=index %>].inputId"/>
				<input type="hidden" value="<bean:write name="inputFile" property="runName"/>" 
				       name="inputFile[<%=index %>].runName"/>
				<bean:write  name="inputFile" property="runName" />
			</td>
			<%index++; %>
		</tr>
		</logic:iterate>
 		</table>
		<div class="clickable toggle_selection" style="font-size: 7pt; color: #000000;" 
		     id="toggle_analysis_<bean:write name="inputSummary" property="inputGroupId"/>">Deselect All</div>
		</div>
		</div>
		<br>
</logic:iterate>
</logic:equal>