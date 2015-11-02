<%@ taglib uri="/WEB-INF/yrc-www.tld" prefix="yrcwww" %>

<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>

<%@ include file="/includes/header.jsp" %>
<%@ include file="/includes/errors.jsp" %>



<script>
function backToProject() {

	document.location='<yrcwww:link path="/viewProject.do"/>?ID='+<bean:write name="paymentMethodForm" property="projectId"/>
}
</script>

<yrcwww:contentbox title="Edit Payment Method">
<center>

<logic:equal name="paymentMethodForm" property="editable" value="false">
	<div style="font-size:8pt; font-weight:bold; color:red; margin:10px 0 10px 0;">
	
		This payment method is already in use. You may only change the status field ("Current"). 
		If you select "No", this payment method will no longer appear in the drop-down
		menu when scheduling instrument time. 
	</div>
</logic:equal>


<html:form action="savePaymentMethod.do" method="POST">

<table border="0" cellpadding="7">

	<tbody>
	<tr>
		<td><b>Project ID:</b></td>
		<td>
			<b><bean:write name="paymentMethodForm" property="projectId"/></b>
			<html:hidden property="projectId"/>
			<html:hidden property="paymentMethodId"/>
		</td>
		<td></td>
		<td></td>
	</tr>
	<tr>
    	
    	<logic:equal name="paymentMethodForm" property="editable" value="true">
    		<logic:equal name="paymentMethodForm" property="uwbudgetAllowed" value="true">
				<td>UW Budget Number:</td>
		    	<td>
		    		<html:text  property="uwBudgetNumber" /> <span style="font-size:10px;">format: 00-0000</span>
		    	</td>
			</logic:equal>
    	
	    	<logic:equal name="paymentMethodForm" property="ponumberAllowed" value="true">
		    	<td>PO Number:</td>
		    	<td>
		    		<html:text  property="poNumber"/>
		    		<br/>
	    			<span style="font-weight:bold;">
	    				<html:link href="pages/admin/costcenter/paymentInformation.jsp">Payment Information</html:link>
	    			</span>
	    		</td>
	    	</logic:equal>
    	</logic:equal>
    	
    	<logic:equal name="paymentMethodForm" property="editable" value="false">
    		<logic:notEmpty name="paymentMethodForm" property="uwBudgetNumber">
    			<td>UW Budget Number:</td>
	    		<td>
	    		<html:text  property="uwBudgetNumber" readonly="true"/>
	    	</td>
    		</logic:notEmpty>
    		<logic:notEmpty name="paymentMethodForm" property="poNumber">
    			<td>PO Number:</td>
	    		<td>
	    			<html:text  property="poNumber" readonly="true"/>
	    		</td>
    		</logic:notEmpty>
    	</logic:equal>
    	
   </tr>
   
   <logic:equal name="paymentMethodForm" property="editable" value="true">
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
-->
   </logic:equal>
   
   <logic:equal name="paymentMethodForm" property="editable" value="false">
   		<tr>
	   		<td>Federal Funding:</td>
	   		<td colspan="4">
	   			<html:checkbox property="federalFunding" disabled="true"></html:checkbox>
	   			<br/>
	   		</td>
	   </tr>
   </logic:equal>
   
   <tr>
   		<td>Current:</td>
   		<td>
  			<html:radio property="current" value="true">YES</html:radio>
   			<html:radio property="current" value="false">NO</html:radio>
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
   			<logic:equal name="paymentMethodForm" property="editable" value="true">
   				<html:text  property="contactFirstName" size="40" />
   			</logic:equal>
   			<logic:equal name="paymentMethodForm" property="editable" value="false">
   				<html:text  property="contactFirstName" size="40" readonly="true"/>
   			</logic:equal>
   		</td>
   	</tr>
   	<tr>
   		<td>Last Name:</td>
   		<td colspan="4">
   			<logic:equal name="paymentMethodForm" property="editable" value="true">
   				<html:text  property="contactLastName" size="40" />
   			</logic:equal>
   			<logic:equal name="paymentMethodForm" property="editable" value="false">
   				<html:text  property="contactLastName" size="40" readonly="true"/>
   			</logic:equal>
   		</td>
   	</tr>
   	<tr>
   		<td>Email:</td>
   		<td colspan="4">
   			<logic:equal name="paymentMethodForm" property="editable" value="true">
   				<html:text  property="contactEmail" size="40" />
   			</logic:equal>
   			<logic:equal name="paymentMethodForm" property="editable" value="false">
   				<html:text  property="contactEmail" size="40" readonly="true" />
   			</logic:equal>
   		</td>
   	</tr>
   	<tr>
   		<td>Phone:</td>
   		<td colspan="4">
   			<logic:equal name="paymentMethodForm" property="editable" value="true">
   				<html:text  property="contactPhone"/>
   			</logic:equal>
   			<logic:equal name="paymentMethodForm" property="editable" value="false">
   				<html:text  property="contactPhone" readonly="true"/>
   			</logic:equal>
   		</td>
   	</tr>
   	<tr>
   		<td>Organization:</td>
   		<td colspan="4">
   			<logic:equal name="paymentMethodForm" property="editable" value="true">
   				<html:text  property="organization" size="40"/>
   			</logic:equal>
   			<logic:equal name="paymentMethodForm" property="editable" value="false">
   				<html:text  property="organization" size="40" readonly="true"/>
   			</logic:equal>
   		</td>
   	</tr>
   	<tr>
   		<td>Address Line 1:</td>
   		<td colspan="4">
   			<logic:equal name="paymentMethodForm" property="editable" value="true">
   				<html:text  property="addressLine1" size="80"/>
   			</logic:equal>
   			<logic:equal name="paymentMethodForm" property="editable" value="false">
   				<html:text  property="addressLine1" size="80" readonly="true"/>
   			</logic:equal>
   		</td>
   	</tr>
   	<tr>
   		<td>Address Line 2:</td>
   		<td colspan="4">
   			<logic:equal name="paymentMethodForm" property="editable" value="true">
   				<html:text  property="addressLine2" size="80"/>
   			</logic:equal>
   			<logic:equal name="paymentMethodForm" property="editable" value="false">
   				<html:text  property="addressLine2" size="80" readonly="true"/>
   			</logic:equal>
   		</td>
   	</tr>
   	<tr>
   		<td>City:</td>
   		<td colspan="4">
   			<logic:equal name="paymentMethodForm" property="editable" value="true">
   				<html:text  property="city"/>
   			</logic:equal>
   			<logic:equal name="paymentMethodForm" property="editable" value="false">
   				<html:text  property="city" readonly="true" />
   			</logic:equal>
   		</td>
   	</tr>
   	<tr>
   		<td>State:</td>
   		<td colspan="4">
   			<logic:equal name="paymentMethodForm" property="editable" value="true">
   				<html:select property="state">
		     		<html:option value="No">If in US, choose state:</html:option>
		     			<html:options collection="states" property="code" labelProperty="name"/>
	    		</html:select>
	    	</logic:equal>
	    	<logic:equal name="paymentMethodForm" property="editable" value="false">
	    		<bean:write name="paymentMethodForm" property="state"/>
	    		<html:hidden property="state" />
	    	</logic:equal>
   		</td>
   	</tr>
   	<tr>
   		<td>Zip/Postal Code:</td>
   		<td colspan="4">
   			<logic:equal name="paymentMethodForm" property="editable" value="true">
   				<html:text property="zip" size="20" maxlength="255"/>
   			</logic:equal>
   			<logic:equal name="paymentMethodForm" property="editable" value="false">
   				<html:text property="zip" size="20" maxlength="255" readonly="true"/>
   			</logic:equal>
   		</td>
   	</tr>
   	<tr>
   		<td>Country:</td>
   		<td colspan="4">
   			<logic:equal name="paymentMethodForm" property="editable" value="true">
   				<html:select property="country">
	     			<html:options collection="countries" property="code" labelProperty="name"/>
	    		</html:select>
	    	</logic:equal>
	    	<logic:equal name="paymentMethodForm" property="editable" value="false">
	    		<bean:write name="paymentMethodForm" property="country"/>
	    		<html:hidden property="country" />
	    	</logic:equal>
   		</td>
   	</tr>
   	
   	
   <tr>
   		<td colspan="6" align="center">
   			<html:submit>Save</html:submit>
   			<input type="button" onclick="backToProject(); return false;" value="Cancel"/>
   		</td>
   		
   </tr>
   </tbody>
   
</table>

</html:form>

</center>


</yrcwww:contentbox>

<%@ include file="/includes/footer.jsp"%>