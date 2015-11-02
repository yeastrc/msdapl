<%@ taglib uri="/WEB-INF/yrc-www.tld" prefix="yrcwww" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>

<yrcwww:notauthenticated>
 <logic:forward name="authenticate" />
</yrcwww:notauthenticated>

<%@ include file="/includes/header.jsp" %>

<yrcwww:contentbox title="Sequence Coverage Description" centered="true" width="850" scheme="ms">

	<p align="center"><a href="javascript:history.back()">Go Back</a></p>

	<p style="font-size:12pt;color:black;text-decoration:underline;">Example sequence coverage map:</p>
<p><pre>      1          11         21         31         41         51         
      |          |          |          |          |          |          
    1 MPYTWKFLGI SKQLSLENGI AK<span class="covered_peptide">LNQLLNLE <span class="covered_peptide">VDLDIQ<span class="covered_peptide">TIR</span></span><span class="covered_peptide">V <span class="covered_peptide">PSDPDGGTA</span>A DEYIR</span></span>YEMR<span class="covered_peptide">L  <font style="color:black;">60</font>
   <font style="color:black;">61</font> DISNLDEGTY SK</span>FIFLGNSK <span class="covered_peptide"><span class="covered_peptide"><span class="covered_peptide"><span class="single_star_residue">M</span>EV<span class="covered_peptide">P<span class="single_star_residue">M</span>FLCYC GTDNR</span></span></span></span><span class="covered_peptide"><span class="covered_peptide">NEVVL QWLK</span></span><span class="covered_peptide"><span class="covered_peptide"><span class="covered_peptide">AEYGVI <span class="single_star_residue">M</span>WPIK</span></span>FEQK</span>T <font style="color:black;">120</font>
  <font style="color:black;">121</font> MIK<span class="covered_peptide"><span class="covered_peptide">LADASIV HVT<span class="covered_peptide">K</span></span><span class="covered_peptide">ENI<span class="covered_peptide">EQI TWF</span>SSK</span></span><span class="covered_peptide">LYFE PETQDK</span>NLRQ F<span class="covered_peptide">SIEIPR<span class="covered_peptide"><span class="covered_peptide"><span class="covered_peptide"><span class="covered_peptide"><span class="covered_peptide">ESC EGL</span>ALGY<span class="covered_peptide">GNT <font style="color:black;">180</font>
  <font style="color:black;">181</font> <span class="multiple_star_residue">M</span>HPY</span></span><span class="covered_peptide">NDAIVP Y<span class="covered_peptide">IYNETG<span class="single_star_residue">M</span>AV ER</span></span></span></span></span></span><span class="covered_peptide"><span class="covered_peptide"><span class="covered_peptide">L<span class="covered_peptide">PL<span class="covered_peptide">TSVIL AGH</span>TK</span></span></span></span>IMRES IVTSTRSLR<span class="covered_peptide">N R<span class="covered_peptide"><span class="covered_peptide"><span class="covered_peptide">VLAVVLQSI <font style="color:black;">240</font>
  <font style="color:black;">241</font> Q</span>FT</span>SE</span></span></pre>
</p>
	
	<p><span style="font-size:12pt;color:black;text-decoration:underline;">What the colors mean:</span><br>
	<span style="font-size:10pt;color:black;">
		Residues in the protein sequence that are covered by peptides identified during MS analysis are presented in <span style="color:red;">red</span>.  Residues not covered by peptides are presented in black.<br><br>
		Experiments may be run to identify sites of protein modification, such as phosphorylation.  In these experiments, modified residues may be marked with '#', '@' or '*' symbols in the DTASelect files.  Which modification is represented
		by each of these symbols may vary between experiments.  See the experimental description to determine which modification the symbol is representing.<br><br>
		The sequence coverage map detects and color codes these sites of modification.  The color also changes if the modified residue was identified in a single peptide, or if it was identified in multiple peptides.  The color coding works as follows:<br><br>
		<br>
		
			<center><table border="1">
				<tr>
					<td>&nbsp;</td>
					<td>Single peptide</td>
					<td>Multiple peptides</td>
				</tr>
				<tr>
					<td><b>*</b></td>
					<td><span class="single_star_residue">example</span></td>
					<td><span class="multiple_star_residue">example</span></td>
				</tr>
				<tr>
					<td><b>@</b></td>
					<td><span class="single_at_residue">example</span></td>
					<td><span class="multiple_at_residue">example</span></td>
				</tr>
				<tr>
					<td><b>#</b></td>
					<td><span class="single_hash_residue">example</span></td>
					<td><span class="multiple_hash_residue">example</span></td>
				</tr>
			</table></center>
	</span>
	</p>
	
	
	<p align="center"><a href="javascript:history.back()">Go Back</a></p>


</yrcwww:contentbox>

<%@ include file="/includes/footer.jsp" %>