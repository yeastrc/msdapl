<%@ taglib uri="/WEB-INF/yrc-www.tld" prefix="yrcwww" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>


<bean:define name="pept_comparison" id="pept_comparison" type="org.yeastrc.www.compare.PeptideComparisonDataset"></bean:define>


<!-- RESULTS TABLE -->
<div > 
<yrcwww:table name="pept_comparison" 
			  tableClass="table_basic_small sortable_table" 
			  tableId='<%="peptides_table_"+pept_comparison.getNrseqProteinId()%>'
			  center="true" />
</div>