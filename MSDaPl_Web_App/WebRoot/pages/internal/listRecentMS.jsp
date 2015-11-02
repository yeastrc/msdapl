<logic:notEmpty name="yatesdata" scope="request">

	<!-- WE HAVE YEAST TWO-HYBRID DATA FOR THIS PROJECT -->
	<p><yrcwww:contentbox title="Recent Mass Spectrometry Data" centered="true" width="650">

	 <CENTER>
	 <TABLE CELLPADDING="no" CELLSPACING="0">

	  <yrcwww:colorrow scheme="register">
	   <TD>&nbsp</TD>
	   <TD valign="top"><B><U>UPLOADED</U></B></TD>
	   <TD valign="top"><B><U>BAIT<br>DESC</U></B></TD>
	   <TD valign="top"><B><U>COMMENTS</U></B></TD>
	  </yrcwww:colorrow>

	 <logic:iterate id="run" name="yatesdata">

	  <yrcwww:colorrow scheme="register">
	   <TD valign="top" width="20%"><html:link href="/yrc/viewYatesRun.do" paramId="id" paramName="run" paramProperty="id">View Run</html:link></TD>
	   <TD valign="top" width="30%"><bean:write name="run" property="uploadDate"/></TD>
	   <TD valign="top" width="20%"><bean:write name="run" property="baitDesc"/></TD>
	   <TD valign="top" width="30%">
	   <logic:empty name="run" property="comments">
	    No Comments
	   </logic:empty>
	   <logic:notEmpty name="run" property="comments">
	    <bean:write name="run" property="comments"/>
	   </logic:notEmpty>
	   </TD>

	  </yrcwww:colorrow>

     </logic:iterate>
	  
	 </TABLE>
	 </CENTER>
	 
	</yrcwww:contentbox>

</logic:notEmpty>