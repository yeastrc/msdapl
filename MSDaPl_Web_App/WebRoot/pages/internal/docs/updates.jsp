<%@ taglib uri="/WEB-INF/yrc-www.tld" prefix="yrcwww" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>

<%@ include file="/includes/header.jsp" %>


<!-- Updates table -->
<table class="table_basic" width="50%" align="center">
<thead>
	<tr>
		<th>MSDaPl Updates</th>
	</tr>
</thead>
<tbody>
	<tr>
	<td class="center_align">
		<b>03/18/10</b> &nbsp; &nbsp; <a href="#03/18/10">[Details]</a>
	</td>
	</tr>
</tbody>
</table>
<!-- END Updates table -->
<br/>
<br/>

<a name="03/18/10"></a>
<yrcwww:contentbox centered="true" width="70" widthRel="true" title="Updates 03/18/10">
	
	<div style="border: 1px dotted; margin:10x; padding:10px;">
	<ul>
	<li>Support for uploading and viewing results from the Trans-Proteomic Pipeline (TPP) is now available</li>
	<li>Added a page for <a href="<yrcwww:link path='/pages/internal/docs/documentation.jsp'/>">documentation</a>. 
	This is still being updated.</li>
	<li>The project page now lists experiments in reverse order of upload date -- most recent on top</li>
	<ul>
		<li>Details are displayed only for the last 5 experiments.</li>
		<li>If a project has more than 5 experiments, 
		a table (All Available Experiments) is provided above the first experiment on the page. 
		This can be used to navigate to older experiments which get loaded on demand.
		You can also click on the "+" link for an experiment to load experiment details.</li>
	</ul>
	<li>Displaying common names and descriptions in the protein inference and comparison pages has been updated.
	Click <a href="<yrcwww:link path='/pages/internal/docs/documentation.jsp#PROT_NAMES'/>">here</a> for details.</li>
	<li>A couple of new filtering options have been added to the protein comparison page. 
	    Some details on the available options  
		<a href="<yrcwww:link path='/pages/internal/docs/documentation.jsp#COMPARISON'/>">here</a>.</li>
	</ul>
	
	
	</div>
</yrcwww:contentbox>

<br/>
<br/>

<%@ include file="/includes/footer.jsp" %>