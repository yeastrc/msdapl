<%@ taglib uri="/WEB-INF/yrc-www.tld" prefix="yrcwww" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>

<%@ include file="/includes/header.jsp" %>


<!-- Available documents table -->
<table class="table_basic" width="60%" align="center">
<thead>
	<tr><th></th>
	<th>Topics</th></tr>
</thead>
<tbody>
	<tr>
	<td>1</td>
	<td>
		<a href="#UPLOAD">Uploading data</a>
	</td>
	</tr>
	<tr>
	<td>2</td>
	<td>
		<a href="#PROTINFER">Protein inference</a>
	</td>
	</tr>
	<tr>
	<td>3</td>
	<td>
		<a href="#COMPARISON">Protein inference comparison</a>
	</td>
	</tr>
	<tr>
	<td>4</td>
	<td>
		<a href="#PROT_NAMES">Protein common names and descriptions</a>
	</td>
	</tr>
</tbody>
</table>

<br/>
<br/>
<div align="center" width="80%">
<a name="UPLOAD"></a>
<%@ include file="uploadingData.jsp" %>

<br/>
<br/>
<a name="PROTINFER"></a>
<%@ include file="proteinInference.jsp" %>

<br/>
<br/>
<a name="COMPARISON"></a>
<%@ include file="comparison.jsp" %>

<br/>
<br/>

<a name="PROT_NAMES"></a>
<%@ include file="proteinNaming.jsp" %>

<br/>
<br/>
</div>

<%@ include file="/includes/footer.jsp" %>