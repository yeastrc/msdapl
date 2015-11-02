<%@ taglib uri="/WEB-INF/yrc-www.tld" prefix="yrcwww" %>

<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>

<%@ include file="/includes/header.jsp" %>
<%@ include file="/includes/errors.jsp" %>


<logic:empty name="paymentMethod">
  <logic:forward name="viewPaymentMethod" />
</logic:empty>

<script type="text/javascript">
function editPaymentMethod(paymentMethodId, projectId) {
	document.location.href="<yrcwww:link path='/editPaymentMethod.do'/>?paymentMethodId="+paymentMethodId+"&projectId="+projectId;
}
function deletePaymentMethod(paymentMethodId, projectId) {
	if(confirm("Are you sure you want to delete this payment method?")) {
		document.location.href="<yrcwww:link path='/deletePaymentMethod.do'/>?paymentMethodId="+paymentMethodId+"&projectId="+projectId;
	}
}
function backToProject(projectId) {
	document.location.href="<yrcwww:link path='/viewProject.do'/>?ID="+projectId;
}
</script>
   			
<yrcwww:contentbox title="View Payment Method">
<center>

<table border="0" cellpadding="7">

	<tbody>
	<tr>
		<td><b>Project ID:</b></td>
		<td>
			<b><html:link action="viewProject.do" paramId="ID" paramName="projectId"><bean:write name="projectId"/></html:link></b>
		</td>
		<td></td>
		<td></td>
	</tr>
	<logic:notEmpty name="paymentMethod" property="uwbudgetNumber">
		<tr>
	    	<td>UW Budget Number:</td>
	    	<td>
	    		<bean:write name="paymentMethod"  property="uwbudgetNumber"/>
	    	</td>
	   </tr>
   </logic:notEmpty>
   
   <logic:notEmpty name="paymentMethod" property="ponumber">
		<tr>
	    	<td>PO Number:</td>
	    	<td>
	    		<bean:write name="paymentMethod"  property="ponumber"/>
	    	</td>
	   </tr>
   </logic:notEmpty>
   <!--  
   <tr>
	   	<td>Federal Funding:</td>
	   	<td>
	   		<bean:write name="paymentMethod"  property="federalFunding"/>
	   	</td>
	</tr>
	-->
    <tr>
   		<td>Current:</td>
   		<td>
   			<bean:write name="paymentMethod"  property="current"/>
   		</td>
   </tr>
   
   <tr>
   		<td colspan="2" style="font-weight:bold; text-align:left;">
   			Contact details of the person responsible for accounting and billing.
   		</td>
   </tr>
   <tr>
   		<td>First Name:</td>
   		<td>
   			<bean:write name="paymentMethod"  property="contactFirstName" />
   		</td>
   	</tr>
   	<tr>
   		<td>Last Name:</td>
   		<td >
   			<bean:write name="paymentMethod"  property="contactLastName" />
   		</td>
   	</tr>
   	<tr>
   		<td>Email:</td>
   		<td >
   			<bean:write name="paymentMethod"  property="contactEmail" />
   		</td>
   	</tr>
   	<tr>
   		<td>Phone:</td>
   		<td >
   			<bean:write name="paymentMethod"  property="contactPhone"/>
   		</td>
   	</tr>
   	<tr>
   		<td>Organization:</td>
   		<td >
   			<bean:write name="paymentMethod"  property="organization" />
   		</td>
   	</tr>
   	<tr>
   		<td>Address Line 1:</td>
   		<td >
   			<bean:write name="paymentMethod"  property="addressLine1" />
   		</td>
   	</tr>
   	<tr>
   		<td>Address Line 2:</td>
   		<td >
   			<bean:write name="paymentMethod"  property="addressLine2" />
   		</td>
   	</tr>
   	<tr>
   		<td>City:</td>
   		<td >
   			<bean:write name="paymentMethod"  property="city"/>
   		</td>
   	</tr>
   	<tr>
   		<td>State:</td>
   		<td >
   			<bean:write name="paymentMethod" property="state" />
   		</td>
   	</tr>
   	<tr>
   		<td>Zip/Postal Code:</td>
   		<td >
   			<bean:write name="paymentMethod" property="zip" />
   		</td>
   	</tr>
   	<tr>
   		<td>Country:</td>
   		<td >
   			<bean:write name="paymentMethod" property="country" />
   		</td>
   	</tr>
   	
   	<tr>
   		<td colspan="2" style="text-align:center;">

   			<input onclick='editPaymentMethod(<bean:write name="paymentMethod" property="id"/>, <bean:write name="projectId"/>)' type="button" value="Edit"/>
   			<input onclick='deletePaymentMethod(<bean:write name="paymentMethod" property="id"/>, <bean:write name="projectId"/>)' type="button" value="Delete"/>
   			<input onclick='backToProject(<bean:write name="projectId"/>)' type="button" value="Back to Project"/>
   			
   		</td>
   	</tr>
   	
   </tbody>
   
</table>

</center>

</yrcwww:contentbox>

<%@ include file="/includes/footer.jsp"%>