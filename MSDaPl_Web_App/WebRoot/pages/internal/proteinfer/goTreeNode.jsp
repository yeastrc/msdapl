<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/yrc-www.tld" prefix="yrcwww" %>

<logic:present name="goTree">
	<yrcwww:gotree treeName="goTree" />
</logic:present>