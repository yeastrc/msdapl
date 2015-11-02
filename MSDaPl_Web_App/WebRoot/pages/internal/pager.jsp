<%@ taglib uri="/WEB-INF/yrc-www.tld" prefix="yrcwww" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>

<!-- NOTE: a pageResults(pageNum) javascript function should be defined in the jsp that includes this one. -->
<div id="resultPager"  style="margin-top: 10px; margin-left: 10px;" align="left">
	
	<bean:define name="pageable" property="currentPage" id="currentPage" />
	<bean:define name="pageable" property="lastPage" id="lastPage" />
	<bean:define name="pageable" property="pageCount" id="pagecount" />
	<bean:define name="pageable" property="displayPageNumbers" id="displayPageNumbers" />
	
	<logic:greaterThan name="pagecount" value="0">
	<%int nextPage = Integer.valueOf(currentPage.toString()) + 1; int prevPage = Integer.valueOf(currentPage.toString()) - 1; %>
	
	<logic:notEqual name="currentPage" value="1">
		<span style="text-decoration: underline; cursor: pointer;" onclick="pageResults(1)">
			First</span> &nbsp;
		<span style="cursor: pointer;" onclick="pageResults(<%=prevPage %>)">
			&lt;&lt;
		</span>
	</logic:notEqual>
	
	<logic:iterate name="displayPageNumbers" id="pg">
		<logic:notEqual name="pg" value="<%=currentPage.toString()%>">
			<span style="text-decoration: underline; cursor: pointer;font-size:8pt;" onclick="pageResults(<bean:write name="pg"/>)">
				<bean:write name="pg"/>
			</span>&nbsp;
		</logic:notEqual>
		<logic:equal name="pg" value="<%=currentPage.toString()%>">
			<bean:write name="pg"/> &nbsp;
		</logic:equal>
	</logic:iterate>
	
	<logic:notEqual name="currentPage" value="<%=lastPage.toString()%>" >
		<span style="cursor: pointer;" onclick="pageResults(<%=nextPage %>)">
			&gt;&gt;
		</span>  &nbsp;
		<span style="text-decoration: underline; cursor: pointer;" onclick="pageResults(<bean:write name='lastPage' />)">
			Last</span>
	</logic:notEqual>
	&nbsp; &nbsp; <span style="font-size:8pt;">Page <bean:write name="currentPage"/> of <bean:write name="pagecount" /></span>
	
	</logic:greaterThan>
	
	<input type="text" size="3" value='<bean:write name="pageable" property="numPerPage"/>' 
			style="margin-left:20px;" id="pager_result_count" />
		<span style="font-size:8pt;">Results / Page</span>
</div>