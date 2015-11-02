<%@ taglib uri="/WEB-INF/yrc-www.tld" prefix="yrcwww" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>

<%@ include file="/includes/adminHeader.jsp" %>

<script language="javascript">

// keep track of the form field we're finding a project for
var currentProjectField;

// pop open the Project Finder window
function projectListerPopUp(field) {
	currentProjectField = field;
	var doc = "/yrc/projectSelectionAction.do";

	var winHeight = 500
	var winWidth = 600;

	window.open(doc, "PROJECT_SELECTION_WINDOW", "width=" + winWidth + ",height=" + winHeight + ",status=no,resizable=yes,scrollbars=yes");
}

function projectSearcherPopUp(field) {
	currentProjectField = field;
	var doc = "/yrc/pages/admin/upload/uploadProjectSearch.jsp";

	var winHeight = 500
	var winWidth = 600;

	window.open(doc, "PROJECT_SELECTION_WINDOW", "width=" + winWidth + ",height=" + winHeight + ",status=no,resizable=yes,scrollbars=yes");
}

</script>


<%@ include file="/includes/errors.jsp" %>

<logic:equal name="queued" value="true" scope="request">
 <center>
 <hr width="50%">
  <B><font color="red">The MS/MS data upload request has been successfully added to the job queue.<BR>
  You will be notified by email when it is completed.</font></B>
 <hr width="50%">
 </center>
</logic:equal>

<yrcwww:contentbox title="Upload Yates MS Data" centered="true" width="1000" scheme="upload">
<center>

<p align="center">To upload Yates Lab MS data, please fill out the simple form below.</p>

<html:form action="uploadYates" method="POST">
 <html:hidden property="group" value="Yates"/>	
	<center>
		<table border="0" width="50%">
			<tr>
				<td valign="top" width="50%">
					<span style="font-size:12pt;font-weight:bold;">Project Number:</span><br>
					<span style="font-size:8pt;color:red;">IMPORTANT: This <span style="text-decoration:underline;">must</span> be the project<br>
							       to which this data belongs.</span>
				</td>
				<td valign="top" width="50%">
					<html:text property="projectID" style="font-size:12pt;font-weight:bold;" size="10" maxlength="10" /><br>
						<a href="javascript:projectSearcherPopUp(document.uploadYatesForm.projectID)" style="text-decoration:none;"><span style="font-size:8pt;color:red;text-decoration:none;">[SEARCH PROJECTS]</span></a>
						<!--<br><a href="javascript:projectListerPopUp(document.uploadYatesForm.projectID)" style="text-decoration:none;"><span style="font-size:8pt;color:red;text-decoration:none;">[PULLDOWN LIST]</span></a>-->
				</td>
			</tr>
		</table>
	</center>


<div name="uploadBox1">
<yrcwww:contentbox scheme="upload" width="700">
  <table border="0">

   <tr>
    <td valign="top">Server directory:<br>
    <font style="font-size:8pt;">e.g.: /wfs/bfd/3/scott/davis/ti6b</font></td>
    <td valign="top"><html:text property="directoryName1" size="40" maxlength="255"/></td>
   </tr>

   <tr>
    <td>Run date:</td>
    <td>
     <html:select property="year1">
      <html:option value="0">Year</html:option>
      <html:option value="1998">1998</html:option>
      <html:option value="1999">1999</html:option>
      <html:option value="2000">2000</html:option>
      <html:option value="2001">2001</html:option>
      <html:option value="2002">2002</html:option>
      <html:option value="2003">2003</html:option>
      <html:option value="2004">2004</html:option>
      <html:option value="2005">2005</html:option>
      <html:option value="2006">2006</html:option>
      <html:option value="2007">2007</html:option>
      <html:option value="2008">2008</html:option>
      <html:option value="2009">2009</html:option>
      <html:option value="20010">2010</html:option>
     </html:select>
     
     <b> - </b>
      
     <html:select property="month1">
      <html:option value="0">Month</html:option>
      <html:option value="01">01</html:option>
      <html:option value="02">02</html:option>
      <html:option value="03">03</html:option>
      <html:option value="04">04</html:option>
      <html:option value="05">05</html:option>
      <html:option value="06">06</html:option>
      <html:option value="07">07</html:option>
      <html:option value="08">08</html:option>
      <html:option value="09">09</html:option>
      <html:option value="10">10</html:option>
      <html:option value="11">11</html:option>
      <html:option value="12">12</html:option>
     </html:select>
     
     <b> - </b>
     
     <html:select property="day1">
      <html:option value="0">Day</html:option>
      <html:option value="01">01</html:option>
      <html:option value="02">02</html:option>
      <html:option value="03">03</html:option>
      <html:option value="04">04</html:option>
      <html:option value="05">05</html:option>
      <html:option value="06">06</html:option>
      <html:option value="07">07</html:option>
      <html:option value="08">08</html:option>
      <html:option value="09">09</html:option>
      <html:option value="10">10</html:option>
      <html:option value="11">11</html:option>
      <html:option value="12">12</html:option>
      <html:option value="13">13</html:option>
      <html:option value="14">14</html:option>
      <html:option value="15">15</html:option>
      <html:option value="16">16</html:option>
      <html:option value="17">17</html:option>
      <html:option value="18">18</html:option>
      <html:option value="19">19</html:option>
      <html:option value="20">20</html:option>
      <html:option value="21">21</html:option>
      <html:option value="22">22</html:option>
      <html:option value="23">23</html:option>
      <html:option value="24">24</html:option>
      <html:option value="25">25</html:option>
      <html:option value="26">26</html:option>
      <html:option value="27">27</html:option>
      <html:option value="28">28</html:option>
      <html:option value="29">29</html:option>
      <html:option value="30">30</html:option>
      <html:option value="31">31</html:option>
     </html:select>
     
    </td>
   </tr>

   <tr>
    <td valign="top"><br>Bait Protein:<br>
     <font style="font-size:8pt;">(Name of purified protein,<br> leave blank if none)</font></td>
    <td valign="top"><br><html:text property="baitDesc1" size="10" maxlength="20"/></td>
   </tr>

   <tr>
    <td valign="top"><br>Target Species:<br>
     <font style="font-size:8pt;">(Species of sample)</font></td>
    <td valign="top"><br>
     <html:select property="targetSpecies1">
      <html:option value="9913">B. taurus (cow)</html:option>
      <html:option value="6239">C. elegans</html:option>
      <html:option value="7227">D. melanogaster (fruit fly)</html:option>
      <html:option value="9031">G. gallus (chicken)</html:option>
      <html:option value="9606">H. sapiens</html:option>
      <html:option value="10090">M. musculus (mouse)</html:option>
      <html:option value="10116">R. norvegicus (rat)</html:option>
      <html:option value="4932">S. cerevisiae (budding yeast)</html:option>
      <html:option value="4896">S. pombe (fission yeast)</html:option>
      <html:option value="0">Not Specified/Not Applicable</html:option>
     </html:select>
    </td>
   </tr>
   
   <tr>
    <td valign="top">If target species is not listed<br>
    enter the <a target="ncbi_window" href="http://www.ncbi.nlm.nih.gov/Taxonomy/taxonomyhome.html/">NCBI Taxonomy</a> ID number:<br><br></td>
    <td valign="top"><html:text property="otherSpecies1" size="5" maxlength="7"/><br><br></td>
   </tr>

   <tr>
    <td valign="top">Comments:</td>
    <td><html:textarea property="comments1" cols="40" rows="5"/></td>
   </tr>

  </table>
</yrcwww:contentbox>
</div>


<div id="uploadBox2" style="display:none;">
<yrcwww:contentbox scheme="upload" width="700">
  <table border="0">

   <tr>
    <td valign="top">Server directory:</td>
    <td valign="top"><html:text property="directoryName2" size="40" maxlength="255"/></td>
   </tr>

   <tr>
    <td>Run date:</td>
    <td>
     <html:select property="year2">
      <html:option value="0">Year</html:option>
      <html:option value="1998">1998</html:option>
      <html:option value="1999">1999</html:option>
      <html:option value="2000">2000</html:option>
      <html:option value="2001">2001</html:option>
      <html:option value="2002">2002</html:option>
      <html:option value="2003">2003</html:option>
      <html:option value="2004">2004</html:option>
      <html:option value="2005">2005</html:option>
      <html:option value="2006">2006</html:option>
      <html:option value="2007">2007</html:option>
      <html:option value="2008">2008</html:option>
      <html:option value="2009">2009</html:option>
      <html:option value="20010">2010</html:option>
     </html:select>
     
     <b> - </b>
      
     <html:select property="month2">
      <html:option value="0">Month</html:option>
      <html:option value="01">01</html:option>
      <html:option value="02">02</html:option>
      <html:option value="03">03</html:option>
      <html:option value="04">04</html:option>
      <html:option value="05">05</html:option>
      <html:option value="06">06</html:option>
      <html:option value="07">07</html:option>
      <html:option value="08">08</html:option>
      <html:option value="09">09</html:option>
      <html:option value="10">10</html:option>
      <html:option value="11">11</html:option>
      <html:option value="12">12</html:option>
     </html:select>
     
     <b> - </b>
     
     <html:select property="day2">
      <html:option value="0">Day</html:option>
      <html:option value="01">01</html:option>
      <html:option value="02">02</html:option>
      <html:option value="03">03</html:option>
      <html:option value="04">04</html:option>
      <html:option value="05">05</html:option>
      <html:option value="06">06</html:option>
      <html:option value="07">07</html:option>
      <html:option value="08">08</html:option>
      <html:option value="09">09</html:option>
      <html:option value="10">10</html:option>
      <html:option value="11">11</html:option>
      <html:option value="12">12</html:option>
      <html:option value="13">13</html:option>
      <html:option value="14">14</html:option>
      <html:option value="15">15</html:option>
      <html:option value="16">16</html:option>
      <html:option value="17">17</html:option>
      <html:option value="18">18</html:option>
      <html:option value="19">19</html:option>
      <html:option value="20">20</html:option>
      <html:option value="21">21</html:option>
      <html:option value="22">22</html:option>
      <html:option value="23">23</html:option>
      <html:option value="24">24</html:option>
      <html:option value="25">25</html:option>
      <html:option value="26">26</html:option>
      <html:option value="27">27</html:option>
      <html:option value="28">28</html:option>
      <html:option value="29">29</html:option>
      <html:option value="30">30</html:option>
      <html:option value="31">31</html:option>
     </html:select>
     
    </td>
   </tr>

   <tr>
    <td valign="top"><br>Bait Protein:<br>
     <font style="font-size:8pt;">(Name of purified protein,<br> leave blank if none)</font></td>
    <td valign="top"><br><html:text property="baitDesc2" size="10" maxlength="20"/></td>
   </tr>

   <tr>
    <td valign="top"><br>Target Species:<br>
     <font style="font-size:8pt;">(Species of sample)</font></td>
    <td valign="top"><br>
     <html:select property="targetSpecies2">
      <html:option value="9913">B. taurus (cow)</html:option>
      <html:option value="6239">C. elegans</html:option>
      <html:option value="7227">D. melanogaster (fruit fly)</html:option>
      <html:option value="9031">G. gallus (chicken)</html:option>
      <html:option value="9606">H. sapiens</html:option>
      <html:option value="10090">M. musculus (mouse)</html:option>
      <html:option value="10116">R. norvegicus (rat)</html:option>
      <html:option value="4932">S. cerevisiae (budding yeast)</html:option>
      <html:option value="4896">S. pombe (fission yeast)</html:option>
      <html:option value="0">Not Specified/Not Applicable</html:option>
     </html:select>
    </td>
   </tr>
   
   <tr>
    <td valign="top">If target species is not listed<br>
    enter the <a target="ncbi_window" href="http://www.ncbi.nlm.nih.gov/Taxonomy/taxonomyhome.html/">NCBI Taxonomy</a> ID number:<br><br></td>
    <td valign="top"><html:text property="otherSpecies2" size="5" maxlength="7"/><br><br></td>
   </tr>

   <tr>
    <td valign="top">Comments:</td>
    <td><html:textarea property="comments2" cols="40" rows="5"/></td>
   </tr>

  </table>
</yrcwww:contentbox>
</div>


<div id="uploadBox3" style="display:none;">
<yrcwww:contentbox scheme="upload" width="700">
  <table border="0">

   <tr>
    <td valign="top">Server directory:</td>
    <td valign="top"><html:text property="directoryName3" size="40" maxlength="255"/></td>
   </tr>

   <tr>
    <td>Run date:</td>
    <td>
     <html:select property="year3">
      <html:option value="0">Year</html:option>
      <html:option value="1998">1998</html:option>
      <html:option value="1999">1999</html:option>
      <html:option value="2000">2000</html:option>
      <html:option value="2001">2001</html:option>
      <html:option value="2002">2002</html:option>
      <html:option value="2003">2003</html:option>
      <html:option value="2004">2004</html:option>
      <html:option value="2005">2005</html:option>
      <html:option value="2006">2006</html:option>
      <html:option value="2007">2007</html:option>
      <html:option value="2008">2008</html:option>
      <html:option value="2009">2009</html:option>
      <html:option value="20010">2010</html:option>
     </html:select>
     
     <b> - </b>
      
     <html:select property="month3">
      <html:option value="0">Month</html:option>
      <html:option value="01">01</html:option>
      <html:option value="02">02</html:option>
      <html:option value="03">03</html:option>
      <html:option value="04">04</html:option>
      <html:option value="05">05</html:option>
      <html:option value="06">06</html:option>
      <html:option value="07">07</html:option>
      <html:option value="08">08</html:option>
      <html:option value="09">09</html:option>
      <html:option value="10">10</html:option>
      <html:option value="11">11</html:option>
      <html:option value="12">12</html:option>
     </html:select>
     
     <b> - </b>
     
     <html:select property="day3">
      <html:option value="0">Day</html:option>
      <html:option value="01">01</html:option>
      <html:option value="02">02</html:option>
      <html:option value="03">03</html:option>
      <html:option value="04">04</html:option>
      <html:option value="05">05</html:option>
      <html:option value="06">06</html:option>
      <html:option value="07">07</html:option>
      <html:option value="08">08</html:option>
      <html:option value="09">09</html:option>
      <html:option value="10">10</html:option>
      <html:option value="11">11</html:option>
      <html:option value="12">12</html:option>
      <html:option value="13">13</html:option>
      <html:option value="14">14</html:option>
      <html:option value="15">15</html:option>
      <html:option value="16">16</html:option>
      <html:option value="17">17</html:option>
      <html:option value="18">18</html:option>
      <html:option value="19">19</html:option>
      <html:option value="20">20</html:option>
      <html:option value="21">21</html:option>
      <html:option value="22">22</html:option>
      <html:option value="23">23</html:option>
      <html:option value="24">24</html:option>
      <html:option value="25">25</html:option>
      <html:option value="26">26</html:option>
      <html:option value="27">27</html:option>
      <html:option value="28">28</html:option>
      <html:option value="29">29</html:option>
      <html:option value="30">30</html:option>
      <html:option value="31">31</html:option>
     </html:select>
     
    </td>
   </tr>

   <tr>
    <td valign="top"><br>Bait Protein:<br>
     <font style="font-size:8pt;">(Name of purified protein,<br> leave blank if none)</font></td>
    <td valign="top"><br><html:text property="baitDesc3" size="10" maxlength="20"/></td>
   </tr>

   <tr>
    <td valign="top"><br>Target Species:<br>
     <font style="font-size:8pt;">(Species of sample)</font></td>
    <td valign="top"><br>
     <html:select property="targetSpecies3">
      <html:option value="9913">B. taurus (cow)</html:option>
      <html:option value="6239">C. elegans</html:option>
      <html:option value="7227">D. melanogaster (fruit fly)</html:option>
      <html:option value="9031">G. gallus (chicken)</html:option>
      <html:option value="9606">H. sapiens</html:option>
      <html:option value="10090">M. musculus (mouse)</html:option>
      <html:option value="10116">R. norvegicus (rat)</html:option>
      <html:option value="4932">S. cerevisiae (budding yeast)</html:option>
      <html:option value="4896">S. pombe (fission yeast)</html:option>
      <html:option value="0">Not Specified/Not Applicable</html:option>
     </html:select>
    </td>
   </tr>
   
   <tr>
    <td valign="top">If target species is not listed<br>
    enter the <a target="ncbi_window" href="http://www.ncbi.nlm.nih.gov/Taxonomy/taxonomyhome.html/">NCBI Taxonomy</a> ID number:<br><br></td>
    <td valign="top"><html:text property="otherSpecies3" size="5" maxlength="7"/><br><br></td>
   </tr>

   <tr>
    <td valign="top">Comments:</td>
    <td><html:textarea property="comments3" cols="40" rows="5"/></td>
   </tr>

  </table>
</yrcwww:contentbox>
</div>

<div id="uploadBox4" style="display:none;">
<yrcwww:contentbox scheme="upload" width="700">
  <table border="0">

   <tr>
    <td valign="top">Server directory:</td>
    <td valign="top"><html:text property="directoryName4" size="40" maxlength="255"/></td>
   </tr>

   <tr>
    <td>Run date:</td>
    <td>
     <html:select property="year4">
      <html:option value="0">Year</html:option>
      <html:option value="1998">1998</html:option>
      <html:option value="1999">1999</html:option>
      <html:option value="2000">2000</html:option>
      <html:option value="2001">2001</html:option>
      <html:option value="2002">2002</html:option>
      <html:option value="2003">2003</html:option>
      <html:option value="2004">2004</html:option>
      <html:option value="2005">2005</html:option>
      <html:option value="2006">2006</html:option>
      <html:option value="2007">2007</html:option>
      <html:option value="2008">2008</html:option>
      <html:option value="2009">2009</html:option>
      <html:option value="20010">2010</html:option>
     </html:select>
     
     <b> - </b>
      
     <html:select property="month4">
      <html:option value="0">Month</html:option>
      <html:option value="01">01</html:option>
      <html:option value="02">02</html:option>
      <html:option value="03">03</html:option>
      <html:option value="04">04</html:option>
      <html:option value="05">05</html:option>
      <html:option value="06">06</html:option>
      <html:option value="07">07</html:option>
      <html:option value="08">08</html:option>
      <html:option value="09">09</html:option>
      <html:option value="10">10</html:option>
      <html:option value="11">11</html:option>
      <html:option value="12">12</html:option>
     </html:select>
     
     <b> - </b>
     
     <html:select property="day4">
      <html:option value="0">Day</html:option>
      <html:option value="01">01</html:option>
      <html:option value="02">02</html:option>
      <html:option value="03">03</html:option>
      <html:option value="04">04</html:option>
      <html:option value="05">05</html:option>
      <html:option value="06">06</html:option>
      <html:option value="07">07</html:option>
      <html:option value="08">08</html:option>
      <html:option value="09">09</html:option>
      <html:option value="10">10</html:option>
      <html:option value="11">11</html:option>
      <html:option value="12">12</html:option>
      <html:option value="13">13</html:option>
      <html:option value="14">14</html:option>
      <html:option value="15">15</html:option>
      <html:option value="16">16</html:option>
      <html:option value="17">17</html:option>
      <html:option value="18">18</html:option>
      <html:option value="19">19</html:option>
      <html:option value="20">20</html:option>
      <html:option value="21">21</html:option>
      <html:option value="22">22</html:option>
      <html:option value="23">23</html:option>
      <html:option value="24">24</html:option>
      <html:option value="25">25</html:option>
      <html:option value="26">26</html:option>
      <html:option value="27">27</html:option>
      <html:option value="28">28</html:option>
      <html:option value="29">29</html:option>
      <html:option value="30">30</html:option>
      <html:option value="31">31</html:option>
     </html:select>
     
    </td>
   </tr>

   <tr>
    <td valign="top"><br>Bait Protein:<br>
     <font style="font-size:8pt;">(Name of purified protein,<br> leave blank if none)</font></td>
    <td valign="top"><br><html:text property="baitDesc4" size="10" maxlength="20"/></td>
   </tr>

   <tr>
    <td valign="top"><br>Target Species:<br>
     <font style="font-size:8pt;">(Species of sample)</font></td>
    <td valign="top"><br>
     <html:select property="targetSpecies4">
      <html:option value="9913">B. taurus (cow)</html:option>
      <html:option value="6239">C. elegans</html:option>
      <html:option value="7227">D. melanogaster (fruit fly)</html:option>
      <html:option value="9031">G. gallus (chicken)</html:option>
      <html:option value="9606">H. sapiens</html:option>
      <html:option value="10090">M. musculus (mouse)</html:option>
      <html:option value="10116">R. norvegicus (rat)</html:option>
      <html:option value="4932">S. cerevisiae (budding yeast)</html:option>
      <html:option value="4896">S. pombe (fission yeast)</html:option>
      <html:option value="0">Not Specified/Not Applicable</html:option>
     </html:select>
    </td>
   </tr>
   
   <tr>
    <td valign="top">If target species is not listed<br>
    enter the <a target="ncbi_window" href="http://www.ncbi.nlm.nih.gov/Taxonomy/taxonomyhome.html/">NCBI Taxonomy</a> ID number:<br><br></td>
    <td valign="top"><html:text property="otherSpecies4" size="5" maxlength="7"/><br><br></td>
   </tr>

   <tr>
    <td valign="top">Comments:</td>
    <td><html:textarea property="comments4" cols="40" rows="5"/></td>
   </tr>

  </table>
</yrcwww:contentbox>
</div>


<div id="uploadBox5" style="display:none;">
<yrcwww:contentbox scheme="upload" width="700">
  <table border="0">

   <tr>
    <td valign="top">Server directory:</td>
    <td valign="top"><html:text property="directoryName5" size="40" maxlength="255"/></td>
   </tr>

   <tr>
    <td>Run date:</td>
    <td>
     <html:select property="year5">
      <html:option value="0">Year</html:option>
      <html:option value="1998">1998</html:option>
      <html:option value="1999">1999</html:option>
      <html:option value="2000">2000</html:option>
      <html:option value="2001">2001</html:option>
      <html:option value="2002">2002</html:option>
      <html:option value="2003">2003</html:option>
      <html:option value="2004">2004</html:option>
      <html:option value="2005">2005</html:option>
      <html:option value="2006">2006</html:option>
      <html:option value="2007">2007</html:option>
      <html:option value="2008">2008</html:option>
      <html:option value="2009">2009</html:option>
      <html:option value="20010">2010</html:option>
     </html:select>
     
     <b> - </b>
      
     <html:select property="month5">
      <html:option value="0">Month</html:option>
      <html:option value="01">01</html:option>
      <html:option value="02">02</html:option>
      <html:option value="03">03</html:option>
      <html:option value="04">04</html:option>
      <html:option value="05">05</html:option>
      <html:option value="06">06</html:option>
      <html:option value="07">07</html:option>
      <html:option value="08">08</html:option>
      <html:option value="09">09</html:option>
      <html:option value="10">10</html:option>
      <html:option value="11">11</html:option>
      <html:option value="12">12</html:option>
     </html:select>
     
     <b> - </b>
     
     <html:select property="day5">
      <html:option value="0">Day</html:option>
      <html:option value="01">01</html:option>
      <html:option value="02">02</html:option>
      <html:option value="03">03</html:option>
      <html:option value="04">04</html:option>
      <html:option value="05">05</html:option>
      <html:option value="06">06</html:option>
      <html:option value="07">07</html:option>
      <html:option value="08">08</html:option>
      <html:option value="09">09</html:option>
      <html:option value="10">10</html:option>
      <html:option value="11">11</html:option>
      <html:option value="12">12</html:option>
      <html:option value="13">13</html:option>
      <html:option value="14">14</html:option>
      <html:option value="15">15</html:option>
      <html:option value="16">16</html:option>
      <html:option value="17">17</html:option>
      <html:option value="18">18</html:option>
      <html:option value="19">19</html:option>
      <html:option value="20">20</html:option>
      <html:option value="21">21</html:option>
      <html:option value="22">22</html:option>
      <html:option value="23">23</html:option>
      <html:option value="24">24</html:option>
      <html:option value="25">25</html:option>
      <html:option value="26">26</html:option>
      <html:option value="27">27</html:option>
      <html:option value="28">28</html:option>
      <html:option value="29">29</html:option>
      <html:option value="30">30</html:option>
      <html:option value="31">31</html:option>
     </html:select>
     
    </td>
   </tr>

   <tr>
    <td valign="top"><br>Bait Protein:<br>
     <font style="font-size:8pt;">(Name of purified protein,<br> leave blank if none)</font></td>
    <td valign="top"><br><html:text property="baitDesc5" size="10" maxlength="20"/></td>
   </tr>

   <tr>
    <td valign="top"><br>Target Species:<br>
     <font style="font-size:8pt;">(Species of sample)</font></td>
    <td valign="top"><br>
     <html:select property="targetSpecies5">
      <html:option value="9913">B. taurus (cow)</html:option>
      <html:option value="6239">C. elegans</html:option>
      <html:option value="7227">D. melanogaster (fruit fly)</html:option>
      <html:option value="9031">G. gallus (chicken)</html:option>
      <html:option value="9606">H. sapiens</html:option>
      <html:option value="10090">M. musculus (mouse)</html:option>
      <html:option value="10116">R. norvegicus (rat)</html:option>
      <html:option value="4932">S. cerevisiae (budding yeast)</html:option>
      <html:option value="4896">S. pombe (fission yeast)</html:option>
      <html:option value="0">Not Specified/Not Applicable</html:option>
     </html:select>
    </td>
   </tr>
   
   <tr>
    <td valign="top">If target species is not listed<br>
    enter the <a target="ncbi_window" href="http://www.ncbi.nlm.nih.gov/Taxonomy/taxonomyhome.html/">NCBI Taxonomy</a> ID number:<br><br></td>
    <td valign="top"><html:text property="otherSpecies5" size="5" maxlength="7"/><br><br></td>
   </tr>

   <tr>
    <td valign="top">Comments:</td>
    <td><html:textarea property="comments5" cols="40" rows="5"/></td>
   </tr>

  </table>
</yrcwww:contentbox>
</div>

<div id="uploadBox6" style="display:none;">
<yrcwww:contentbox scheme="upload" width="700">
  <table border="0">

   <tr>
    <td valign="top">Server directory:</td>
    <td valign="top"><html:text property="directoryName6" size="40" maxlength="255"/></td>
   </tr>

   <tr>
    <td>Run date:</td>
    <td>
     <html:select property="year6">
      <html:option value="0">Year</html:option>
      <html:option value="1998">1998</html:option>
      <html:option value="1999">1999</html:option>
      <html:option value="2000">2000</html:option>
      <html:option value="2001">2001</html:option>
      <html:option value="2002">2002</html:option>
      <html:option value="2003">2003</html:option>
      <html:option value="2004">2004</html:option>
      <html:option value="2005">2005</html:option>
      <html:option value="2006">2006</html:option>
      <html:option value="2007">2007</html:option>
      <html:option value="2008">2008</html:option>
      <html:option value="2009">2009</html:option>
      <html:option value="20010">2010</html:option>
     </html:select>
     
     <b> - </b>
      
     <html:select property="month6">
      <html:option value="0">Month</html:option>
      <html:option value="01">01</html:option>
      <html:option value="02">02</html:option>
      <html:option value="03">03</html:option>
      <html:option value="04">04</html:option>
      <html:option value="05">05</html:option>
      <html:option value="06">06</html:option>
      <html:option value="07">07</html:option>
      <html:option value="08">08</html:option>
      <html:option value="09">09</html:option>
      <html:option value="10">10</html:option>
      <html:option value="11">11</html:option>
      <html:option value="12">12</html:option>
     </html:select>
     
     <b> - </b>
     
     <html:select property="day6">
      <html:option value="0">Day</html:option>
      <html:option value="01">01</html:option>
      <html:option value="02">02</html:option>
      <html:option value="03">03</html:option>
      <html:option value="04">04</html:option>
      <html:option value="05">05</html:option>
      <html:option value="06">06</html:option>
      <html:option value="07">07</html:option>
      <html:option value="08">08</html:option>
      <html:option value="09">09</html:option>
      <html:option value="10">10</html:option>
      <html:option value="11">11</html:option>
      <html:option value="12">12</html:option>
      <html:option value="13">13</html:option>
      <html:option value="14">14</html:option>
      <html:option value="15">15</html:option>
      <html:option value="16">16</html:option>
      <html:option value="17">17</html:option>
      <html:option value="18">18</html:option>
      <html:option value="19">19</html:option>
      <html:option value="20">20</html:option>
      <html:option value="21">21</html:option>
      <html:option value="22">22</html:option>
      <html:option value="23">23</html:option>
      <html:option value="24">24</html:option>
      <html:option value="25">25</html:option>
      <html:option value="26">26</html:option>
      <html:option value="27">27</html:option>
      <html:option value="28">28</html:option>
      <html:option value="29">29</html:option>
      <html:option value="30">30</html:option>
      <html:option value="31">31</html:option>
     </html:select>
     
    </td>
   </tr>

   <tr>
    <td valign="top"><br>Bait Protein:<br>
     <font style="font-size:8pt;">(Name of purified protein,<br> leave blank if none)</font></td>
    <td valign="top"><br><html:text property="baitDesc6" size="10" maxlength="20"/></td>
   </tr>

   <tr>
    <td valign="top"><br>Target Species:<br>
     <font style="font-size:8pt;">(Species of sample)</font></td>
    <td valign="top"><br>
     <html:select property="targetSpecies6">
      <html:option value="9913">B. taurus (cow)</html:option>
      <html:option value="6239">C. elegans</html:option>
      <html:option value="7227">D. melanogaster (fruit fly)</html:option>
      <html:option value="9031">G. gallus (chicken)</html:option>
      <html:option value="9606">H. sapiens</html:option>
      <html:option value="10090">M. musculus (mouse)</html:option>
      <html:option value="10116">R. norvegicus (rat)</html:option>
      <html:option value="4932">S. cerevisiae (budding yeast)</html:option>
      <html:option value="4896">S. pombe (fission yeast)</html:option>
      <html:option value="0">Not Specified/Not Applicable</html:option>
     </html:select>
    </td>
   </tr>
   
   <tr>
    <td valign="top">If target species is not listed<br>
    enter the <a target="ncbi_window" href="http://www.ncbi.nlm.nih.gov/Taxonomy/taxonomyhome.html/">NCBI Taxonomy</a> ID number:<br><br></td>
    <td valign="top"><html:text property="otherSpecies6" size="5" maxlength="7"/><br><br></td>
   </tr>

   <tr>
    <td valign="top">Comments:</td>
    <td><html:textarea property="comments6" cols="40" rows="5"/></td>
   </tr>

  </table>
</yrcwww:contentbox>
</div>

<div id="uploadBox7" style="display:none;">
<yrcwww:contentbox scheme="upload" width="700">
  <table border="0">

   <tr>
    <td valign="top">Server directory:</td>
    <td valign="top"><html:text property="directoryName7" size="40" maxlength="255"/></td>
   </tr>

   <tr>
    <td>Run date:</td>
    <td>
     <html:select property="year7">
      <html:option value="0">Year</html:option>
      <html:option value="1998">1998</html:option>
      <html:option value="1999">1999</html:option>
      <html:option value="2000">2000</html:option>
      <html:option value="2001">2001</html:option>
      <html:option value="2002">2002</html:option>
      <html:option value="2003">2003</html:option>
      <html:option value="2004">2004</html:option>
      <html:option value="2005">2005</html:option>
      <html:option value="2006">2006</html:option>
      <html:option value="2007">2007</html:option>
      <html:option value="2008">2008</html:option>
      <html:option value="2009">2009</html:option>
      <html:option value="20010">2010</html:option>
     </html:select>
     
     <b> - </b>
      
     <html:select property="month7">
      <html:option value="0">Month</html:option>
      <html:option value="01">01</html:option>
      <html:option value="02">02</html:option>
      <html:option value="03">03</html:option>
      <html:option value="04">04</html:option>
      <html:option value="05">05</html:option>
      <html:option value="06">06</html:option>
      <html:option value="07">07</html:option>
      <html:option value="08">08</html:option>
      <html:option value="09">09</html:option>
      <html:option value="10">10</html:option>
      <html:option value="11">11</html:option>
      <html:option value="12">12</html:option>
     </html:select>
     
     <b> - </b>
     
     <html:select property="day7">
      <html:option value="0">Day</html:option>
      <html:option value="01">01</html:option>
      <html:option value="02">02</html:option>
      <html:option value="03">03</html:option>
      <html:option value="04">04</html:option>
      <html:option value="05">05</html:option>
      <html:option value="06">06</html:option>
      <html:option value="07">07</html:option>
      <html:option value="08">08</html:option>
      <html:option value="09">09</html:option>
      <html:option value="10">10</html:option>
      <html:option value="11">11</html:option>
      <html:option value="12">12</html:option>
      <html:option value="13">13</html:option>
      <html:option value="14">14</html:option>
      <html:option value="15">15</html:option>
      <html:option value="16">16</html:option>
      <html:option value="17">17</html:option>
      <html:option value="18">18</html:option>
      <html:option value="19">19</html:option>
      <html:option value="20">20</html:option>
      <html:option value="21">21</html:option>
      <html:option value="22">22</html:option>
      <html:option value="23">23</html:option>
      <html:option value="24">24</html:option>
      <html:option value="25">25</html:option>
      <html:option value="26">26</html:option>
      <html:option value="27">27</html:option>
      <html:option value="28">28</html:option>
      <html:option value="29">29</html:option>
      <html:option value="30">30</html:option>
      <html:option value="31">31</html:option>
     </html:select>
     
    </td>
   </tr>

   <tr>
    <td valign="top"><br>Bait Protein:<br>
     <font style="font-size:8pt;">(Name of purified protein,<br> leave blank if none)</font></td>
    <td valign="top"><br><html:text property="baitDesc7" size="10" maxlength="20"/></td>
   </tr>

   <tr>
    <td valign="top"><br>Target Species:<br>
     <font style="font-size:8pt;">(Species of sample)</font></td>
    <td valign="top"><br>
     <html:select property="targetSpecies7">
      <html:option value="9913">B. taurus (cow)</html:option>
      <html:option value="6239">C. elegans</html:option>
      <html:option value="7227">D. melanogaster (fruit fly)</html:option>
      <html:option value="9031">G. gallus (chicken)</html:option>
      <html:option value="9606">H. sapiens</html:option>
      <html:option value="10090">M. musculus (mouse)</html:option>
      <html:option value="10116">R. norvegicus (rat)</html:option>
      <html:option value="4932">S. cerevisiae (budding yeast)</html:option>
      <html:option value="4896">S. pombe (fission yeast)</html:option>
      <html:option value="0">Not Specified/Not Applicable</html:option>
     </html:select>
    </td>
   </tr>
   
   <tr>
    <td valign="top">If target species is not listed<br>
    enter the <a target="ncbi_window" href="http://www.ncbi.nlm.nih.gov/Taxonomy/taxonomyhome.html/">NCBI Taxonomy</a> ID number:<br><br></td>
    <td valign="top"><html:text property="otherSpecies7" size="5" maxlength="7"/><br><br></td>
   </tr>

   <tr>
    <td valign="top">Comments:</td>
    <td><html:textarea property="comments7" cols="40" rows="5"/></td>
   </tr>

  </table>
</yrcwww:contentbox>
</div>

<div id="uploadBox8" style="display:none;">
<yrcwww:contentbox scheme="upload" width="700">
  <table border="0">

   <tr>
    <td valign="top">Server directory:</td>
    <td valign="top"><html:text property="directoryName8" size="40" maxlength="255"/></td>
   </tr>

   <tr>
    <td>Run date:</td>
    <td>
     <html:select property="year8">
      <html:option value="0">Year</html:option>
      <html:option value="1998">1998</html:option>
      <html:option value="1999">1999</html:option>
      <html:option value="2000">2000</html:option>
      <html:option value="2001">2001</html:option>
      <html:option value="2002">2002</html:option>
      <html:option value="2003">2003</html:option>
      <html:option value="2004">2004</html:option>
      <html:option value="2005">2005</html:option>
      <html:option value="2006">2006</html:option>
      <html:option value="2007">2007</html:option>
      <html:option value="2008">2008</html:option>
      <html:option value="2009">2009</html:option>
      <html:option value="20010">2010</html:option>
     </html:select>
     
     <b> - </b>
      
     <html:select property="month8">
      <html:option value="0">Month</html:option>
      <html:option value="01">01</html:option>
      <html:option value="02">02</html:option>
      <html:option value="03">03</html:option>
      <html:option value="04">04</html:option>
      <html:option value="05">05</html:option>
      <html:option value="06">06</html:option>
      <html:option value="07">07</html:option>
      <html:option value="08">08</html:option>
      <html:option value="09">09</html:option>
      <html:option value="10">10</html:option>
      <html:option value="11">11</html:option>
      <html:option value="12">12</html:option>
     </html:select>
     
     <b> - </b>
     
     <html:select property="day8">
      <html:option value="0">Day</html:option>
      <html:option value="01">01</html:option>
      <html:option value="02">02</html:option>
      <html:option value="03">03</html:option>
      <html:option value="04">04</html:option>
      <html:option value="05">05</html:option>
      <html:option value="06">06</html:option>
      <html:option value="07">07</html:option>
      <html:option value="08">08</html:option>
      <html:option value="09">09</html:option>
      <html:option value="10">10</html:option>
      <html:option value="11">11</html:option>
      <html:option value="12">12</html:option>
      <html:option value="13">13</html:option>
      <html:option value="14">14</html:option>
      <html:option value="15">15</html:option>
      <html:option value="16">16</html:option>
      <html:option value="17">17</html:option>
      <html:option value="18">18</html:option>
      <html:option value="19">19</html:option>
      <html:option value="20">20</html:option>
      <html:option value="21">21</html:option>
      <html:option value="22">22</html:option>
      <html:option value="23">23</html:option>
      <html:option value="24">24</html:option>
      <html:option value="25">25</html:option>
      <html:option value="26">26</html:option>
      <html:option value="27">27</html:option>
      <html:option value="28">28</html:option>
      <html:option value="29">29</html:option>
      <html:option value="30">30</html:option>
      <html:option value="31">31</html:option>
     </html:select>
     
    </td>
   </tr>

   <tr>
    <td valign="top"><br>Bait Protein:<br>
     <font style="font-size:8pt;">(Name of purified protein,<br> leave blank if none)</font></td>
    <td valign="top"><br><html:text property="baitDesc8" size="10" maxlength="20"/></td>
   </tr>

   <tr>
    <td valign="top"><br>Target Species:<br>
     <font style="font-size:8pt;">(Species of sample)</font></td>
    <td valign="top"><br>
     <html:select property="targetSpecies8">
      <html:option value="9913">B. taurus (cow)</html:option>
      <html:option value="6239">C. elegans</html:option>
      <html:option value="7227">D. melanogaster (fruit fly)</html:option>
      <html:option value="9031">G. gallus (chicken)</html:option>
      <html:option value="9606">H. sapiens</html:option>
      <html:option value="10090">M. musculus (mouse)</html:option>
      <html:option value="10116">R. norvegicus (rat)</html:option>
      <html:option value="4932">S. cerevisiae (budding yeast)</html:option>
      <html:option value="4896">S. pombe (fission yeast)</html:option>
      <html:option value="0">Not Specified/Not Applicable</html:option>
     </html:select>
    </td>
   </tr>
   
   <tr>
    <td valign="top">If target species is not listed<br>
    enter the <a target="ncbi_window" href="http://www.ncbi.nlm.nih.gov/Taxonomy/taxonomyhome.html/">NCBI Taxonomy</a> ID number:<br><br></td>
    <td valign="top"><html:text property="otherSpecies8" size="5" maxlength="7"/><br><br></td>
   </tr>

   <tr>
    <td valign="top">Comments:</td>
    <td><html:textarea property="comments8" cols="40" rows="5"/></td>
   </tr>

  </table>
</yrcwww:contentbox>
</div>

<div id="uploadBox9" style="display:none;">
<yrcwww:contentbox scheme="upload" width="700">
  <table border="0">

   <tr>
    <td valign="top">Server directory:</td>
    <td valign="top"><html:text property="directoryName9" size="40" maxlength="255"/></td>
   </tr>

   <tr>
    <td>Run date:</td>
    <td>
     <html:select property="year9">
      <html:option value="0">Year</html:option>
      <html:option value="1998">1998</html:option>
      <html:option value="1999">1999</html:option>
      <html:option value="2000">2000</html:option>
      <html:option value="2001">2001</html:option>
      <html:option value="2002">2002</html:option>
      <html:option value="2003">2003</html:option>
      <html:option value="2004">2004</html:option>
      <html:option value="2005">2005</html:option>
      <html:option value="2006">2006</html:option>
      <html:option value="2007">2007</html:option>
      <html:option value="2008">2008</html:option>
      <html:option value="2009">2009</html:option>
      <html:option value="20010">2010</html:option>
     </html:select>
     
     <b> - </b>
      
     <html:select property="month9">
      <html:option value="0">Month</html:option>
      <html:option value="01">01</html:option>
      <html:option value="02">02</html:option>
      <html:option value="03">03</html:option>
      <html:option value="04">04</html:option>
      <html:option value="05">05</html:option>
      <html:option value="06">06</html:option>
      <html:option value="07">07</html:option>
      <html:option value="08">08</html:option>
      <html:option value="09">09</html:option>
      <html:option value="10">10</html:option>
      <html:option value="11">11</html:option>
      <html:option value="12">12</html:option>
     </html:select>
     
     <b> - </b>
     
     <html:select property="day9">
      <html:option value="0">Day</html:option>
      <html:option value="01">01</html:option>
      <html:option value="02">02</html:option>
      <html:option value="03">03</html:option>
      <html:option value="04">04</html:option>
      <html:option value="05">05</html:option>
      <html:option value="06">06</html:option>
      <html:option value="07">07</html:option>
      <html:option value="08">08</html:option>
      <html:option value="09">09</html:option>
      <html:option value="10">10</html:option>
      <html:option value="11">11</html:option>
      <html:option value="12">12</html:option>
      <html:option value="13">13</html:option>
      <html:option value="14">14</html:option>
      <html:option value="15">15</html:option>
      <html:option value="16">16</html:option>
      <html:option value="17">17</html:option>
      <html:option value="18">18</html:option>
      <html:option value="19">19</html:option>
      <html:option value="20">20</html:option>
      <html:option value="21">21</html:option>
      <html:option value="22">22</html:option>
      <html:option value="23">23</html:option>
      <html:option value="24">24</html:option>
      <html:option value="25">25</html:option>
      <html:option value="26">26</html:option>
      <html:option value="27">27</html:option>
      <html:option value="28">28</html:option>
      <html:option value="29">29</html:option>
      <html:option value="30">30</html:option>
      <html:option value="31">31</html:option>
     </html:select>
     
    </td>
   </tr>

   <tr>
    <td valign="top"><br>Bait Protein:<br>
     <font style="font-size:8pt;">(Name of purified protein,<br> leave blank if none)</font></td>
    <td valign="top"><br><html:text property="baitDesc9" size="10" maxlength="20"/></td>
   </tr>

   <tr>
    <td valign="top"><br>Target Species:<br>
     <font style="font-size:8pt;">(Species of sample, Optional)</font></td>
    <td valign="top"><br>
     <html:select property="targetSpecies9">
      <html:option value="9913">B. taurus (cow)</html:option>
      <html:option value="6239">C. elegans</html:option>
      <html:option value="7227">D. melanogaster (fruit fly)</html:option>
      <html:option value="9031">G. gallus (chicken)</html:option>
      <html:option value="9606">H. sapiens</html:option>
      <html:option value="10090">M. musculus (mouse)</html:option>
      <html:option value="10116">R. norvegicus (rat)</html:option>
      <html:option value="4932">S. cerevisiae (budding yeast)</html:option>
      <html:option value="4896">S. pombe (fission yeast)</html:option>
      <html:option value="0">Not Specified/Not Applicable</html:option>
     </html:select>
    </td>
   </tr>
   
   <tr>
    <td valign="top">If target species is not listed<br>
    enter the <a target="ncbi_window" href="http://www.ncbi.nlm.nih.gov/Taxonomy/taxonomyhome.html/">NCBI Taxonomy</a> ID number:<br><br></td>
    <td valign="top"><html:text property="otherSpecies9" size="5" maxlength="7"/><br><br></td>
   </tr>

   <tr>
    <td valign="top">Comments:</td>
    <td><html:textarea property="comments9" cols="40" rows="5"/></td>
   </tr>

  </table>
</yrcwww:contentbox>
</div>

<div id="uploadBox10" style="display:none;">
<yrcwww:contentbox scheme="upload" width="700">
  <table border="0">

   <tr>
    <td valign="top">Server directory:</td>
    <td valign="top"><html:text property="directoryName10" size="40" maxlength="255"/></td>
   </tr>

   <tr>
    <td>Run date:</td>
    <td>
     <html:select property="year10">
      <html:option value="0">Year</html:option>
      <html:option value="1998">1998</html:option>
      <html:option value="1999">1999</html:option>
      <html:option value="2000">2000</html:option>
      <html:option value="2001">2001</html:option>
      <html:option value="2002">2002</html:option>
      <html:option value="2003">2003</html:option>
      <html:option value="2004">2004</html:option>
      <html:option value="2005">2005</html:option>
      <html:option value="2006">2006</html:option>
      <html:option value="2007">2007</html:option>
      <html:option value="2008">2008</html:option>
      <html:option value="2009">2009</html:option>
      <html:option value="20010">2010</html:option>
     </html:select>
     
     <b> - </b>
      
     <html:select property="month10">
      <html:option value="0">Month</html:option>
      <html:option value="01">01</html:option>
      <html:option value="02">02</html:option>
      <html:option value="03">03</html:option>
      <html:option value="04">04</html:option>
      <html:option value="05">05</html:option>
      <html:option value="06">06</html:option>
      <html:option value="07">07</html:option>
      <html:option value="08">08</html:option>
      <html:option value="09">09</html:option>
      <html:option value="10">10</html:option>
      <html:option value="11">11</html:option>
      <html:option value="12">12</html:option>
     </html:select>
     
     <b> - </b>
     
     <html:select property="day10">
      <html:option value="0">Day</html:option>
      <html:option value="01">01</html:option>
      <html:option value="02">02</html:option>
      <html:option value="03">03</html:option>
      <html:option value="04">04</html:option>
      <html:option value="05">05</html:option>
      <html:option value="06">06</html:option>
      <html:option value="07">07</html:option>
      <html:option value="08">08</html:option>
      <html:option value="09">09</html:option>
      <html:option value="10">10</html:option>
      <html:option value="11">11</html:option>
      <html:option value="12">12</html:option>
      <html:option value="13">13</html:option>
      <html:option value="14">14</html:option>
      <html:option value="15">15</html:option>
      <html:option value="16">16</html:option>
      <html:option value="17">17</html:option>
      <html:option value="18">18</html:option>
      <html:option value="19">19</html:option>
      <html:option value="20">20</html:option>
      <html:option value="21">21</html:option>
      <html:option value="22">22</html:option>
      <html:option value="23">23</html:option>
      <html:option value="24">24</html:option>
      <html:option value="25">25</html:option>
      <html:option value="26">26</html:option>
      <html:option value="27">27</html:option>
      <html:option value="28">28</html:option>
      <html:option value="29">29</html:option>
      <html:option value="30">30</html:option>
      <html:option value="31">31</html:option>
     </html:select>
     
    </td>
   </tr>

   <tr>
    <td valign="top"><br>Bait Protein:<br>
     <font style="font-size:8pt;">(Name of purified protein,<br> leave blank if none)</font></td>
    <td valign="top"><br><html:text property="baitDesc10" size="10" maxlength="20"/></td>
   </tr>

   <tr>
    <td valign="top"><br>Target Species:<br>
     <font style="font-size:8pt;">(Species of sample)</font></td>
    <td valign="top"><br>
     <html:select property="targetSpecies10">
      <html:option value="9913">B. taurus (cow)</html:option>
      <html:option value="6239">C. elegans</html:option>
      <html:option value="7227">D. melanogaster (fruit fly)</html:option>
      <html:option value="9031">G. gallus (chicken)</html:option>
      <html:option value="9606">H. sapiens</html:option>
      <html:option value="10090">M. musculus (mouse)</html:option>
      <html:option value="10116">R. norvegicus (rat)</html:option>
      <html:option value="4932">S. cerevisiae (budding yeast)</html:option>
      <html:option value="4896">S. pombe (fission yeast)</html:option>
      <html:option value="0">Not Specified/Not Applicable</html:option>
     </html:select>
    </td>
   </tr>
   
   <tr>
    <td valign="top">If target species is not listed<br>
    enter the <a target="ncbi_window" href="http://www.ncbi.nlm.nih.gov/Taxonomy/taxonomyhome.html/">NCBI Taxonomy</a> ID number:<br><br></td>
    <td valign="top"><html:text property="otherSpecies10" size="5" maxlength="7"/><br><br></td>
   </tr>

   <tr>
    <td valign="top">Comments:</td>
    <td><html:textarea property="comments10" cols="40" rows="5"/></td>
   </tr>

  </table>
</yrcwww:contentbox>
</div>



	<input id="addUploadButton" type="button" style="background-color:#FFBF59;color:#000000;font-size:16pt;font-weight:bold;margin-top:20px;" value="+ Add Another Run" onClick="addRun()">


	<html:submit style="background-color:#FFBF59;color:#000000;font-size:16pt;font-weight:bold;margin-top:24px;" value="Upload Data"/>


<script language="javascript">

	var upload2=document.all? document.all["uploadBox2"] : document.getElementById? document.getElementById("uploadBox2") : ""
	var upload3=document.all? document.all["uploadBox3"] : document.getElementById? document.getElementById("uploadBox3") : ""
	var upload4=document.all? document.all["uploadBox4"] : document.getElementById? document.getElementById("uploadBox4") : ""
	var upload5=document.all? document.all["uploadBox5"] : document.getElementById? document.getElementById("uploadBox5") : ""
	var upload6=document.all? document.all["uploadBox6"] : document.getElementById? document.getElementById("uploadBox6") : ""
	var upload7=document.all? document.all["uploadBox7"] : document.getElementById? document.getElementById("uploadBox7") : ""
	var upload8=document.all? document.all["uploadBox8"] : document.getElementById? document.getElementById("uploadBox8") : ""
	var upload9=document.all? document.all["uploadBox9"] : document.getElementById? document.getElementById("uploadBox9") : ""
	var upload10=document.all? document.all["uploadBox10"] : document.getElementById? document.getElementById("uploadBox10") : ""

	var addUploadButton=document.all? document.all["addUploadButton"] : document.getElementById? document.getElementById("addUploadButton") : ""


	// make sure divs are being shown if we have values for them
	if (document.uploadYatesForm.directoryName2.value != null && document.uploadYatesForm.directoryName2.value != "") { upload2.style.display = "inline"; }
	if (document.uploadYatesForm.directoryName3.value != null && document.uploadYatesForm.directoryName3.value != "") { upload3.style.display = "inline"; }
	if (document.uploadYatesForm.directoryName4.value != null && document.uploadYatesForm.directoryName4.value != "") { upload4.style.display = "inline"; }
	if (document.uploadYatesForm.directoryName5.value != null && document.uploadYatesForm.directoryName5.value != "") { upload5.style.display = "inline"; }
	if (document.uploadYatesForm.directoryName6.value != null && document.uploadYatesForm.directoryName6.value != "") { upload6.style.display = "inline"; }
	if (document.uploadYatesForm.directoryName7.value != null && document.uploadYatesForm.directoryName7.value != "") { upload7.style.display = "inline"; }
	if (document.uploadYatesForm.directoryName8.value != null && document.uploadYatesForm.directoryName8.value != "") { upload8.style.display = "inline"; }
	if (document.uploadYatesForm.directoryName9.value != null && document.uploadYatesForm.directoryName9.value != "") { upload9.style.display = "inline"; }
	if (document.uploadYatesForm.directoryName10.value != null && document.uploadYatesForm.directoryName10.value != "") {
		upload10.style.display = "inline";
		addUploadButton.style.display = "none";
	}

	var lastDisplay = 1;

	function addRun() {
	
	
		if (lastDisplay == 1) {
			upload2.style.display = "inline";

			document.uploadYatesForm.directoryName2.value = document.uploadYatesForm.directoryName1.value;
			document.uploadYatesForm.year2.selectedIndex = document.uploadYatesForm.year1.selectedIndex;
			document.uploadYatesForm.month2.selectedIndex = document.uploadYatesForm.month1.selectedIndex;
			document.uploadYatesForm.day2.selectedIndex = document.uploadYatesForm.day1.selectedIndex;
			document.uploadYatesForm.baitDesc2.value = document.uploadYatesForm.baitDesc1.value;
			document.uploadYatesForm.targetSpecies2.selectedIndex = document.uploadYatesForm.targetSpecies1.selectedIndex;
			document.uploadYatesForm.otherSpecies2.value = document.uploadYatesForm.otherSpecies1.value;
			document.uploadYatesForm.comments2.value = document.uploadYatesForm.comments1.value;

		} else if (lastDisplay == 2) {
			upload3.style.display = "inline";

			document.uploadYatesForm.directoryName3.value = document.uploadYatesForm.directoryName1.value;
			document.uploadYatesForm.year3.selectedIndex = document.uploadYatesForm.year1.selectedIndex;
			document.uploadYatesForm.month3.selectedIndex = document.uploadYatesForm.month1.selectedIndex;
			document.uploadYatesForm.day3.selectedIndex = document.uploadYatesForm.day1.selectedIndex;
			document.uploadYatesForm.baitDesc3.value = document.uploadYatesForm.baitDesc1.value;
			document.uploadYatesForm.targetSpecies3.selectedIndex = document.uploadYatesForm.targetSpecies1.selectedIndex;
			document.uploadYatesForm.otherSpecies3.value = document.uploadYatesForm.otherSpecies1.value;
			document.uploadYatesForm.comments3.value = document.uploadYatesForm.comments1.value;

		} else if (lastDisplay == 3) {
			upload4.style.display = "inline";

			document.uploadYatesForm.directoryName4.value = document.uploadYatesForm.directoryName1.value;
			document.uploadYatesForm.year4.selectedIndex = document.uploadYatesForm.year1.selectedIndex;
			document.uploadYatesForm.month4.selectedIndex = document.uploadYatesForm.month1.selectedIndex;
			document.uploadYatesForm.day4.selectedIndex = document.uploadYatesForm.day1.selectedIndex;
			document.uploadYatesForm.baitDesc4.value = document.uploadYatesForm.baitDesc1.value;
			document.uploadYatesForm.targetSpecies4.selectedIndex = document.uploadYatesForm.targetSpecies1.selectedIndex;
			document.uploadYatesForm.otherSpecies4.value = document.uploadYatesForm.otherSpecies1.value;
			document.uploadYatesForm.comments4.value = document.uploadYatesForm.comments1.value;

		} else if (lastDisplay == 4) {
			upload5.style.display = "inline";

			document.uploadYatesForm.directoryName5.value = document.uploadYatesForm.directoryName1.value;
			document.uploadYatesForm.year5.selectedIndex = document.uploadYatesForm.year1.selectedIndex;
			document.uploadYatesForm.month5.selectedIndex = document.uploadYatesForm.month1.selectedIndex;
			document.uploadYatesForm.day5.selectedIndex = document.uploadYatesForm.day1.selectedIndex;
			document.uploadYatesForm.baitDesc5.value = document.uploadYatesForm.baitDesc1.value;
			document.uploadYatesForm.targetSpecies5.selectedIndex = document.uploadYatesForm.targetSpecies1.selectedIndex;
			document.uploadYatesForm.otherSpecies5.value = document.uploadYatesForm.otherSpecies1.value;
			document.uploadYatesForm.comments5.value = document.uploadYatesForm.comments1.value;

		} else if (lastDisplay == 5) {
			upload6.style.display = "inline";

			document.uploadYatesForm.directoryName6.value = document.uploadYatesForm.directoryName1.value;
			document.uploadYatesForm.year6.selectedIndex = document.uploadYatesForm.year1.selectedIndex;
			document.uploadYatesForm.month6.selectedIndex = document.uploadYatesForm.month1.selectedIndex;
			document.uploadYatesForm.day6.selectedIndex = document.uploadYatesForm.day1.selectedIndex;
			document.uploadYatesForm.baitDesc6.value = document.uploadYatesForm.baitDesc1.value;
			document.uploadYatesForm.targetSpecies6.selectedIndex = document.uploadYatesForm.targetSpecies1.selectedIndex;
			document.uploadYatesForm.otherSpecies6.value = document.uploadYatesForm.otherSpecies1.value;
			document.uploadYatesForm.comments6.value = document.uploadYatesForm.comments1.value;

		} else if (lastDisplay == 6) {
			upload7.style.display = "inline";

			document.uploadYatesForm.directoryName7.value = document.uploadYatesForm.directoryName1.value;
			document.uploadYatesForm.year7.selectedIndex = document.uploadYatesForm.year1.selectedIndex;
			document.uploadYatesForm.month7.selectedIndex = document.uploadYatesForm.month1.selectedIndex;
			document.uploadYatesForm.day7.selectedIndex = document.uploadYatesForm.day1.selectedIndex;
			document.uploadYatesForm.baitDesc7.value = document.uploadYatesForm.baitDesc1.value;
			document.uploadYatesForm.targetSpecies7.selectedIndex = document.uploadYatesForm.targetSpecies1.selectedIndex;
			document.uploadYatesForm.otherSpecies7.value = document.uploadYatesForm.otherSpecies1.value;
			document.uploadYatesForm.comments7.value = document.uploadYatesForm.comments1.value;

		} else if (lastDisplay == 7) {
			upload8.style.display = "inline";

			document.uploadYatesForm.directoryName8.value = document.uploadYatesForm.directoryName1.value;
			document.uploadYatesForm.year8.selectedIndex = document.uploadYatesForm.year1.selectedIndex;
			document.uploadYatesForm.month8.selectedIndex = document.uploadYatesForm.month1.selectedIndex;
			document.uploadYatesForm.day8.selectedIndex = document.uploadYatesForm.day1.selectedIndex;
			document.uploadYatesForm.baitDesc8.value = document.uploadYatesForm.baitDesc1.value;
			document.uploadYatesForm.targetSpecies8.selectedIndex = document.uploadYatesForm.targetSpecies1.selectedIndex;
			document.uploadYatesForm.otherSpecies8.value = document.uploadYatesForm.otherSpecies1.value;
			document.uploadYatesForm.comments8.value = document.uploadYatesForm.comments1.value;

		} else if (lastDisplay == 8) {
			upload9.style.display = "inline";

			document.uploadYatesForm.directoryName9.value = document.uploadYatesForm.directoryName1.value;
			document.uploadYatesForm.year9.selectedIndex = document.uploadYatesForm.year1.selectedIndex;
			document.uploadYatesForm.month9.selectedIndex = document.uploadYatesForm.month1.selectedIndex;
			document.uploadYatesForm.day9.selectedIndex = document.uploadYatesForm.day1.selectedIndex;
			document.uploadYatesForm.baitDesc9.value = document.uploadYatesForm.baitDesc1.value;
			document.uploadYatesForm.targetSpecies9.selectedIndex = document.uploadYatesForm.targetSpecies1.selectedIndex;
			document.uploadYatesForm.otherSpecies9.value = document.uploadYatesForm.otherSpecies1.value;
			document.uploadYatesForm.comments9.value = document.uploadYatesForm.comments1.value;

		} else if (lastDisplay == 9) {
			upload10.style.display = "inline";

			document.uploadYatesForm.directoryName10.value = document.uploadYatesForm.directoryName1.value;
			document.uploadYatesForm.year10.selectedIndex = document.uploadYatesForm.year1.selectedIndex;
			document.uploadYatesForm.month10.selectedIndex = document.uploadYatesForm.month1.selectedIndex;
			document.uploadYatesForm.day10.selectedIndex = document.uploadYatesForm.day1.selectedIndex;
			document.uploadYatesForm.baitDesc10.value = document.uploadYatesForm.baitDesc1.value;
			document.uploadYatesForm.targetSpecies10.selectedIndex = document.uploadYatesForm.targetSpecies1.selectedIndex;
			document.uploadYatesForm.otherSpecies10.value = document.uploadYatesForm.otherSpecies1.value;
			document.uploadYatesForm.comments10.value = document.uploadYatesForm.comments1.value;

			addUploadButton.style.display = "none";
		}		
		
		lastDisplay++;
		return;	
	}


</script>


</html:form>

</center>
</yrcwww:contentbox>

<%@ include file="/includes/footer.jsp" %>