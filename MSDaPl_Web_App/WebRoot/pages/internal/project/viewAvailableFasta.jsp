<%@ taglib uri="/WEB-INF/yrc-www.tld" prefix="yrcwww" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>

<yrcwww:notauthenticated>
 <logic:forward name="authenticate" />
</yrcwww:notauthenticated>

<logic:notPresent name="databaseList">
  <logic:forward name="availableFasta"  />
</logic:notPresent>
 
<%@ include file="/includes/header.jsp" %>

<%@ include file="/includes/errors.jsp" %>

<script>
// ---------------------------------------------------------------------------------------
// MAKE TABLE SORTABLE
// ---------------------------------------------------------------------------------------
$(document).ready(function() {
   $(".sortable_table").each(function() {
   		var $table = $(this);
   		makeSortableTable($table);
   });
});
</script>

<yrcwww:contentbox title="Available Fasta Files" centered="true" width="90" widthRel="true">
<center>
<table width="98%" class="table_basic sortable_table stripe_table">	

<thead>
	<tr>
	<th class="sort-int sortable">ID</th>
	<th class="sort-alpha sortable">FASTA File</th>
	<th class="sort-alpha sortable">Description</th>
	</tr>
</thead>

<tbody>
	<logic:iterate name="databaseList" id="db">
	<tr>
		<td><bean:write name="db" property="id"/></td>
		<td><bean:write name="db" property="name"/></td>
		<td><bean:write name="db" property="description"/></td>
	</tr>
	</logic:iterate>
</tbody>

</table>
</center>
</yrcwww:contentbox>


<%@ include file="/includes/footer.jsp" %>