import org.yeastrc.project.*;
import org.yeastrc.www.user.*;
import java.util.*;
import org.yeastrc.db.*;
import java.sql.*;
import javax.sql.*;


public class test {

	public static void main(String[] args) {

		try {
			Connection conn = DBConnectionManager.getConnection("yrc");
			Statement stmt = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);

			String sqlStr = "SELECT * FROM tblUsers WHERE researcherID = 254";
			ResultSet rs = stmt.executeQuery(sqlStr);
			
			rs.next();
			String firstName = rs.getString("researcherFirstName");
			System.out.println(firstName);
			
		} catch (Exception e) {
			e.printStackTrace( System.err );
		}
	}

}