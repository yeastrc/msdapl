<%@ taglib uri="/WEB-INF/yrc-www.tld" prefix="yrcwww" %>

<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>

<%@ include file="/includes/header.jsp" %>
<%@ include file="/includes/errors.jsp" %>


<yrcwww:contentbox title="Add New Payment Method">
<center>
<html:form action="saveNewPaymentMethod.do" method="POST">

<table border="0" cellpadding="7">

	<tbody>
	<tr>
		<td><b>Project ID:</b></td>
		<td>
			<b><bean:write name="paymentMethodForm" property="projectId"/></b>
			<html:hidden property="projectId"/>
		</td>
		<td></td>
		<td></td>
	</tr>
	<tr>
		<logic:equal name="paymentMethodForm" property="uwbudgetAllowed" value="true">
			<td>UW Budget Number:</td>
	    	<td>
	    		<html:text  property="uwBudgetNumber"/> <span style="font-size:10px;">format: 00-0000</span>
	    	</td>
		</logic:equal>
    	
    	<logic:equal name="paymentMethodForm" property="ponumberAllowed" value="true">
	    	<td>PO Number:</td>
	    	<td>
	    		<html:text  property="poNumber"/>
	    		<!--
	    		<br/>
	    		<span style="font-weight:bold;">
	    			<html:link href="pages/admin/costcenter/paymentInformation.jsp">Payment Information</html:link>
	    		</span>
	    		-->
	    	</td>
    	</logic:equal>
   </tr>
   <tr>
	   <td>
		   <logic:equal name="paymentMethodForm" property="uwbudgetAllowed" value="true">
			   Budget
		   </logic:equal>
		   <logic:equal name="paymentMethodForm" property="ponumberAllowed" value="true">
			   PO
		   </logic:equal>
		   Name
	   </td>
	   <td><html:text  property="paymentMethodName" size="40" /></td>
   </tr>
   <!-- 
   <tr>
   		<td>Federal Funding:</td>
   		<td colspan="4">
   			<html:checkbox property="federalFunding"></html:checkbox>
   			<br/>
   			<span style="color:red; font-size:10px;">
   				Please check this box if the chosen payment method is federally funded.
   			</span>
   		</td>
   </tr>
   
   
   
   <tr>
   		<td colspan="5" style="color:red;font-size:10pt;">
   			Please provide contact information of the person responsible for accounting and billing.
   		</td>
   </tr>
   <tr>
   		<td>First Name:</td>
   		<td colspan="4">
   			<html:text  property="contactFirstName" size="40" />
   		</td>
   	</tr>
   	<tr>
   		<td>Last Name:</td>
   		<td colspan="4">
   			<html:text  property="contactLastName" size="40" />
   		</td>
   	</tr>
   	<tr>
   		<td>Email:</td>
   		<td colspan="4">
   			<html:text  property="contactEmail" size="40" />
   		</td>
   	</tr>
   	<tr>
   		<td>Phone:</td>
   		<td colspan="4">
   			<html:text  property="contactPhone"/>
   		</td>
   	</tr>
   	<tr>
   		<td>Organization:</td>
   		<td colspan="4">
   			<html:text  property="organization" size="40"/>
   		</td>
   	</tr>
   	<tr>
   		<td>Address Line 1:</td>
   		<td colspan="4">
   			<html:text  property="addressLine1" size="80"/>
   		</td>
   	</tr>
   	<tr>
   		<td>Address Line 2:</td>
   		<td colspan="4">
   			<html:text  property="addressLine2" size="80"/>
   		</td>
   	</tr>
   	<tr>
   		<td>City:</td>
   		<td colspan="4">
   			<html:text  property="city"/>
   		</td>
   	</tr>
   	<tr>
   		<td>State:</td>
   		<td colspan="4">
   			<html:select property="state">
	     		<html:option value="No">If in US, choose state:</html:option>
	     		<html:options collection="states" property="code" labelProperty="name"/>
	    	</html:select>
   		</td>
   	</tr>
   	<tr>
   		<td>Zip/Postal Code:</td>
   		<td colspan="4">
   			<html:text property="zip" size="20" maxlength="255"/>
   		</td>
   	</tr>
   	<tr>
   		<td>Country:</td>
   		<td colspan="4">
   			<html:select property="country">
	     		<html:options collection="countries" property="code" labelProperty="name"/>
	    	</html:select>
   		</td>
   	</tr>
  -->
  
   <tr>
   		<td colspan="6" align="center">
   			<html:submit>Save</html:submit>
   			<input type="button" onclick="history.back();" value="Cancel"/>
   		</td>
   		
   </tr>

   </tbody>
   
</table>

</html:form>

</center>

</yrcwww:contentbox>

<%@ include file="/includes/footer.jsp"%>