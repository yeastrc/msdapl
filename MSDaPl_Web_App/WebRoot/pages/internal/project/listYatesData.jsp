<%@ taglib uri="/WEB-INF/yrc-www.tld" prefix="yrcwww" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>

<logic:notEmpty name="yatesdata" scope="request">

	<!-- WE HAVE YEAST TWO-HYBRID DATA FOR THIS PROJECT -->
	<p><yrcwww:contentbox title="DTASelect Results" centered="true" width="750" scheme="ms">

	 <CENTER>
	 <TABLE CELLPADDING="no" CELLSPACING="0" width="90%" class="table_basic">

	  <thead>
	  <tr>
	   <TH>&nbsp;</TH>
	   <TH valign="top"><B><U>Run Date</U></B></TH>
	   <TH valign="top"><B><U>Bait<br>Protein</U></B></TH>
	   <TH valign="top"><B><U>Bait<br>Desc</U></B></TH>
	   <TH valign="top"><B><U>Comments</U></B></TH>
	  </tr>
	  </thead>
	
	 <tbody>
	 <logic:iterate id="run" name="yatesdata">

	  <yrcwww:colorrow scheme="ms">
	   <TD valign="top" width="20%">
	   	<html:link action="viewYatesRun.do" paramId="id" paramName="run" paramProperty="id">View Run</html:link>
	  </TD>
	   <TD valign="top" width="20%"><bean:write name="run" property="runDate"/></TD>
	   <TD valign="top" width="20%"><NOBR>
	   
	   <logic:empty name="run" property="baitProtein">
	    None Entered
	   </logic:empty>
	   <logic:notEmpty name="run" property="baitProtein">
	   
	   	<nobr>
	   	 <yrcwww:proteinLink name="run" property="baitProtein" />
		</nobr>
	   
	   </logic:notEmpty>
	   
	   
	   
	   </NOBR></TD>
	   <TD valign="top" width="20%"><bean:write name="run" property="baitDesc"/></TD>

	   <TD valign="top" width="20%" class="left_align">
	   <logic:empty name="run" property="comments">
	    No Comments
	   </logic:empty>
	   <logic:notEmpty name="run" property="comments">
	    <bean:write name="run" property="comments"/>
	   </logic:notEmpty>
	   </TD>

	  </yrcwww:colorrow>
	  
     </logic:iterate>
	 </tbody>
	 </TABLE>
	 </CENTER>
	 
	</yrcwww:contentbox>

</logic:notEmpty>