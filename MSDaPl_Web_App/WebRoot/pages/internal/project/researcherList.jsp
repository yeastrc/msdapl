	<logic:notEmpty name="project" property="PI">
		<bean:define id="pi" name="project" property="PI" scope="request"/>

		<yrcwww:colorrow>
			<TD valign="top" width="25%">PI:</TD>
			<TD valign="top" width="75%">
			<html:link action="viewResearcher.do" paramId="id" paramName="pi" paramProperty="ID">
			    <bean:write name="pi" property="firstName"/> <bean:write name="pi" property="lastName"/>, <bean:write name="pi" property="degree"/></html:link>
			</TD>
		</yrcwww:colorrow>
	</logic:notEmpty>

	<logic:notEmpty name="project" property="researcherB">
		<bean:define id="researcherB" name="project" property="researcherB" scope="request"/>

		<yrcwww:colorrow>
			<TD valign="top" width="25%">Researcher B:</TD>
			<TD valign="top" width="75%">
			<html:link action="viewResearcher.do" paramId="id" paramName="researcherB" paramProperty="ID">
				<bean:write name="researcherB" property="firstName"/> <bean:write name="researcherB" property="lastName"/>, <bean:write name="researcherB" property="degree"/></html:link>
			</TD>
		</yrcwww:colorrow>
	</logic:notEmpty>

	<logic:notEmpty name="project" property="researcherC">
		<bean:define id="researcherC" name="project" property="researcherC" scope="request"/>

		<yrcwww:colorrow>
			<TD valign="top" width="25%">Researcher C:</TD>
			<TD valign="top" width="75%">
			<html:link action="viewResearcher.do" paramId="id" paramName="researcherC" paramProperty="ID">
				<bean:write name="researcherC" property="firstName"/> <bean:write name="researcherC" property="lastName"/>, <bean:write name="researcherC" property="degree"/></html:link>
			</TD>
		</yrcwww:colorrow>
	</logic:notEmpty>

	<logic:notEmpty name="project" property="researcherD">
		<bean:define id="researcherD" name="project" property="researcherD" scope="request"/>

		<yrcwww:colorrow>
			<TD valign="top" width="25%">Researcher D:</TD>
			<TD valign="top" width="75%">
			<html:link action="viewResearcher.do" paramId="id" paramName="researcherD" paramProperty="ID">
				<bean:write name="researcherD" property="firstName"/> <bean:write name="researcherD" property="lastName"/>, <bean:write name="researcherD" property="degree"/></html:link>
			</TD>
		</yrcwww:colorrow>
	</logic:notEmpty>
	
	<logic:notEmpty name="project" property="PI">

		<yrcwww:colorrow>
			<TD valign="top" width="25%">Organization:</TD>
			<TD valign="top" width="75%"><bean:write name="pi" property="organization"/>
		</yrcwww:colorrow>

	</logic:notEmpty>