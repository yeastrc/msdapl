<logic:notEmpty name="project" property="paymentMethods">
    <table cellspacing="0" cellpadding="0" border="1">
    <tbody>
    <logic:iterate name="project" property="paymentMethods" id="paymentMethod">
            <tr>
            <logic:notEmpty name="paymentMethod" property="uwbudgetNumber">
                    <td style="padding:3px;">UW Budget Number</td>
                    <td style="padding:3px;font-weight:bold"><bean:write name="paymentMethod" property="uwbudgetNumber"/></td>
            </logic:notEmpty>
            <logic:notEmpty name="paymentMethod" property="ponumber">
                    <td style="padding:3px;">PO Number</td>
                    <td style="padding:3px;font-weight:bold"><bean:write name="paymentMethod" property="ponumber"/></td>
            </logic:notEmpty>
            <td style="padding:3px">
				<bean:write name="paymentMethod" property="name50Chars" />
			</td>
            <td style="padding:3px">
                    <logic:equal name="paymentMethod" property="current" value="true">
                            <span style="color:green">current</span>
                    </logic:equal>
                    <logic:equal name="paymentMethod" property="current" value="false">
                            &nbsp;
                    </logic:equal>
            </td>
            <td style="padding:3px">
                    <a href='<yrcwww:link path="/viewPaymentMethod.do"/>?projectId=<bean:write name="project" property="ID"/>&paymentMethodId=<bean:write name="paymentMethod" property="id"/>'>[View]</a>
            </td>
            </tr>
    </logic:iterate>
    </tbody>
    </table>
    <div style="margin:10px 0px 10px 0px; text-align:left;font-weight:bold;">
            <html:link action="newPaymentMethod.do" paramId="projectId" paramName="project" paramProperty="ID">
            [Add New Payment Method]
            </html:link>
            <html:link action="viewScheduler.do" paramId="projectId" paramName="project" paramProperty="ID">
           		[Schedule Instrument Time]
          	</html:link>
    </div>
    
    <div style="margin:5px 0px 15px 0px">
                <html:link action="viewScheduledTimeDetails.do" paramId="projectId" paramName="project" paramProperty="ID">
                <b>[View]</b>
                </html:link>
                instrument time scheduled for the project
        </div>
        
</logic:notEmpty>

<logic:empty name="project" property="paymentMethods">
        <div style="color:red;margin:10px 0px 10px 0px;">
                There are no payment methods associated with this project.
                <br/>
                In order to schedule instrument time you must have at least one payment method.
                <br/>  
                Click <html:link action="newPaymentMethod.do" paramId="projectId" paramName="project" paramProperty="ID">here</html:link>
                to add a payment method for this project.
        </div>
</logic:empty>