<%@page import="org.yeastrc.www.compare.dataset.DatasetColor"%>
<%@ taglib uri="/WEB-INF/yrc-www.tld" prefix="yrcwww" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>

<yrcwww:notauthenticated>
 <logic:forward name="authenticate" />
</yrcwww:notauthenticated>
 
<%@ include file="/includes/header.jsp" %>

<%@ include file="/includes/errors.jsp" %>


<script src="<yrcwww:link path='js/jquery.form.js'/>"></script>

<script src="<yrcwww:link path='js/comparison.js'/>"></script>
<script>

// submit the form as soon as the document loads
$(window).load(function() {
	updateResults(); // submit the form
});

</script>

<!-- Put the form in a hidden div -->
<div style="display:none;">

<%@include file="comparisonFilterForm.jsp" %>

</div>

<div align="center" style="margin-top:25; margin-bottom:25;">

<b>Clustering may take a few minutes to complete.
This page will refresh automatically when the results are available.</b>
<br/><br/>
<img alt="Processing clustering results..." src="<yrcwww:link path="images/ajax-loader.gif"/>"
 style="margin-top:25; margin-bottom:25;">

</div>

<%@ include file="/includes/footer.jsp" %>