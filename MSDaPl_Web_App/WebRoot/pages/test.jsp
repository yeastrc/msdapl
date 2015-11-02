<%@ page import="org.yeastrc.project.*"%>
<%@ page import="org.yeastrc.www.user.*"%>
<%@ page import="java.util.*"%>
<%@ page import="org.yeastrc.db.*"%>
<%@ page import="java.sql.*"%>
<%@ page import="javax.sql.*"%>
<%@ page import="java.io.*" %>

<%

		try {
			Connection conn = DBConnectionManager.getConnection("yrc");
						
			Statement stmt = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);

			String sqlStr = "SELECT * FROM tblUsers WHERE researcherID = 254";
			ResultSet rs = stmt.executeQuery(sqlStr);
			
			rs.next();
			String firstName = rs.getString("username");
			out.println(firstName);
			
			rs.close();
			stmt.close();
			conn.close();
			
		} catch (Exception e) {
			//out.println(e + ":" + e.getMessage());
			//out.println(e.getStackTrace());
			
			PrintWriter pw = new PrintWriter(out);
			out.println("<B>FOUND ERRORS:</B><BR><BR>");
			e.printStackTrace(pw);
		}
%>