
<%@ taglib uri="/WEB-INF/yrc-www.tld" prefix="yrcwww" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>

<bean:define name="experiment" property="id" id="experimentId" />
<yrcwww:table name="experiment" tableId='<%="search_files_"+experimentId %>' tableClass="table_basic search_files stripe_table" center="true" />
