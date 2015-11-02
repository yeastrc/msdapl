
<%@page import="org.yeastrc.www.compare.dataset.DatasetColor"%>
<%@ taglib uri="/WEB-INF/yrc-www.tld" prefix="yrcwww" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>




<!-- RESULTS TABLE -->
<div > 
<table class="table_basic_small">
<logic:iterate name="groupProteins" id="group" indexId="index">
<tr>
<% String color = DatasetColor.get(index).R+", "+DatasetColor.get(index).G+","+DatasetColor.get(index).B;%>
<td width="2" style="background-color: rgb(<%=color%>)">
	&nbsp;&nbsp;
</td>
<td>
	<bean:write name="group"/>
</td>
</tr>
</logic:iterate>
</table>
</div>