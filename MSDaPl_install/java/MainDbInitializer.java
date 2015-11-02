import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

/**
 * MainDbInitializer.java
 * @author Vagisha Sharma
 * Jun 28, 2011
 */

/**
 * This class will
 * 1. create an "administrators" group in tblYRCGroups
 * 2. Create an admin user
 * 3. Create a group for the lab; "MyLab" is used if name of group is not found in the properties file.
 * 4. Create a test user and add to the lab group
 * 5. admin user is also added to the lab group
 * 
 */
public class MainDbInitializer {

	private String DB_HOST;
	private String DB_NAME;
	private String DB_USER;
	private String DB_PASSWD;
	
	private String LAB_GROUP;
	
	private String ADMIN_USER_LOGIN;
	private String ADMIN_USER_PASSWORD;
	private String ADMIN_USER_FIRST_NAME;
	private String ADMIN_USER_LAST_NAME;
	private String ADMIN_USER_EMAIL;
	
	private String LAB_USER_LOGIN;
	private String LAB_USER_PASSWORD;
	private String LAB_USER_FIRST_NAME;
	private String LAB_USER_LAST_NAME;
	private String LAB_USER_EMAIL;
	
	
	private MainDbInitializer() {}
	
	public void initialize(String labGroupName) throws Exception {
		
		// make sure we have this class; Required for getting connection to the database.
		Class.forName("com.mysql.jdbc.Driver");
		
		// read the database host, user, password etc.
		readProperties();
		
		// create an "administrators" entry in tblYRCGroups
		int adminGroupId = createGroup("administrators", "Group for Site Administrators");
		
		// Create a group for the lab
		int labGroupId = createGroup(LAB_GROUP, "Group for "+LAB_GROUP);
		
		// create an admin user
		int adminUserId = createAdminUser();
		System.out.println("Created admin user");
		
		// Create a normal user
		int labUserId = 0;
		if(LAB_USER_LOGIN != null) {
			labUserId = createLabUser();
			System.out.println("Created lab user");
		}
		
		// Add admin user to the "administrators" and lab groups
		makeMember(adminUserId, adminGroupId);
		makeMember(adminUserId, labGroupId);
		
		// add normal user to the lab group
		if(labUserId != 0)
			makeMember(labUserId, labGroupId);
		
	}
	
	private void makeMember(int userId, int groupId) throws Exception {
		
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		
		try {
			
			conn = getDbConnection();
			String sql = "SELECT * FROM tblYRCGroupMembers WHERE ";
			sql += "researcherID="+userId+" AND groupId="+groupId;
			
			stmt = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
			
			rs = stmt.executeQuery(sql);
			
			if(rs.next()) {
				throw new Exception("An entry already exists for user: "+userId+" and group: "+groupId);
			}
			else {
				rs.moveToInsertRow();
				rs.updateInt("researcherID", userId);
				rs.updateInt("groupID", groupId);
				rs.insertRow();
			}
			
		}
		finally {
			if(conn != null) try {conn.close();} catch(SQLException e){}
			if(stmt != null) try {stmt.close();} catch(SQLException e){}
			if(rs != null) try {rs.close();} catch(SQLException e){}
		}
	}

	private int createUser(String firstName, String lastName, String email,
			String login, String password) throws Exception {
		
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		
		int researcherId = 0;
		
		try {
			
			conn = getDbConnection();
			String sql = "SELECT * FROM tblResearchers WHERE ";
			sql += "researcherFirstName=\""+firstName+"\" AND researcherLastName=\""+lastName+"\"";
			
			stmt = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
			
			rs = stmt.executeQuery(sql);
			
			if(rs.next()) {
				System.out.println("Admin user: "+firstName+" "+lastName+" already exists");
				return rs.getInt("researcherID");
			}
			else {
				rs.moveToInsertRow();
				rs.updateString("researcherFirstName", firstName);
				rs.updateString("researcherLastName", lastName);
				rs.updateString("researcherEmail", email);
				rs.insertRow();
				
				// Get the ID generated for this item from the database
				rs.last();
				researcherId = rs.getInt("researcherID");
				
				if(researcherId != 0) {
					addTblUsersEntry(researcherId, login, password);
				}
			}
			
		}
		finally {
			if(conn != null) try {conn.close();} catch(SQLException e){}
			if(stmt != null) try {stmt.close();} catch(SQLException e){}
			if(rs != null) try {rs.close();} catch(SQLException e){}
		}
		
		return researcherId;
	}
	
	private int createAdminUser() throws Exception {
		
		return createUser(ADMIN_USER_FIRST_NAME, ADMIN_USER_LAST_NAME, ADMIN_USER_EMAIL,
				ADMIN_USER_LOGIN, ADMIN_USER_PASSWORD);
		
	}
	
	private int createLabUser() throws Exception {
		
		return createUser(LAB_USER_FIRST_NAME, LAB_USER_LAST_NAME, LAB_USER_EMAIL,
				LAB_USER_LOGIN, LAB_USER_PASSWORD);
		
	}

	private void addTblUsersEntry(int researcherId, String login, String password) throws Exception {
		
		
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		
		try {
			
			conn = getDbConnection();
			String sql = "SELECT * FROM tblUsers WHERE researcherID="+researcherId;
			
			stmt = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
			
			rs = stmt.executeQuery(sql);
			
			if(rs.next()) {
				throw new Exception("An entry already exists in tbUsers for researcherID: "+researcherId);
			}
			else {
				rs.moveToInsertRow();
				rs.updateInt("researcherID", researcherId);
				rs.updateString("username", login);
				MessageDigest md5 = MessageDigest.getInstance("MD5");
				md5.reset();
				md5.update(password.getBytes());
				byte[] digest = md5.digest();
				rs.updateString("password", toHex(digest));
				rs.insertRow();
			}
			
		}
		finally {
			if(conn != null) try {conn.close();} catch(SQLException e){}
			if(stmt != null) try {stmt.close();} catch(SQLException e){}
			if(rs != null) try {rs.close();} catch(SQLException e){}
		}
		
	}
	
	/**
	 * Turns array of bytes into string representing each byte as
	 * a two digit unsigned hex number.
	 * 
	 * @param hash Array of bytes to convert to hex-string
	 * @return  Generated hex string
	 */
	private static String toHex(byte byte_arr[]){
		
//		StringBuffer hexString = new StringBuffer();
//		for (int i=0;i<byte_arr.length;i++) {
//			String hex = Integer.toHexString(0xFF & byte_arr[i]);
//			if(hex.length()==1)
//				hexString.append('0');
//
//			hexString.append(hex);
//		}
//		System.out.println(hexString.toString());
//		return hexString.toString();
		
		StringBuffer buf = new StringBuffer(byte_arr.length * 2);
		for (int i=0; i<byte_arr.length; i++){
			int intVal = byte_arr[i] & 0xff;
			if (intVal < 0x10){
				// append a zero before a one digit hex 
				// number to make it two digits.
				buf.append("0");
			}
			buf.append(Integer.toHexString(intVal));
		}
		//System.out.println(buf.toString());
		return buf.toString();
	}

	private int createGroup(String groupName, String groupDesc) throws SQLException {
		
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		try {
			
			conn = getDbConnection();
			String sql = "SELECT * FROM tblYRCGroups WHERE groupName=\""+groupName+"\"";
			
			stmt = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
			rs = stmt.executeQuery(sql);
			
			if(rs.next()) {
				System.out.println("Group: "+groupName+" already exists");
				return rs.getInt("groupID");
			}
			else {
				rs.moveToInsertRow();
				rs.updateString("groupName", groupName);
				rs.updateString("groupDesc", groupDesc);
				rs.insertRow();
				
				// Get the ID generated for this item from the database
				rs.last();
				return rs.getInt("groupID");
			}
			
		}
		finally {
			if(conn != null) try {conn.close();} catch(SQLException e){}
			if(stmt != null) try {stmt.close();} catch(SQLException e){}
			if(rs != null) try {rs.close();} catch(SQLException e){}
		}
	}

	private Connection getDbConnection() throws SQLException {
		String dbUrl = "jdbc:mysql://"+DB_HOST+"/"+DB_NAME+"?autoReconnect=true";
		return DriverManager.getConnection(dbUrl, DB_USER, DB_PASSWD);
	}

	private void readProperties() throws Exception {
		
		String file = "config.properties";
		
		Properties props = new Properties();
		InputStream is = null;
		try {
			
			is = this.getClass().getResourceAsStream(file);
			props.load(is);

			DB_HOST = props.getProperty("db_host");
			DB_NAME = props.getProperty("db_main_name");
			DB_USER = props.getProperty("db_user");
			DB_PASSWD = props.getProperty("db_passwd", "");
			
			LAB_GROUP = props.getProperty("lab_group_name");
			
			ADMIN_USER_FIRST_NAME = props.getProperty("admin_user_firstname");
			ADMIN_USER_LAST_NAME = props.getProperty("admin_user_lastname");
			ADMIN_USER_EMAIL = props.getProperty("admin_user_email");
			ADMIN_USER_LOGIN = props.getProperty("admin_user_login");
			ADMIN_USER_PASSWORD=props.getProperty("admin_user_password");
			
			LAB_USER_FIRST_NAME = props.getProperty("lab_user_firstname");
			LAB_USER_LAST_NAME = props.getProperty("lab_user_lastname");
			LAB_USER_EMAIL = props.getProperty("lab_user_email");
			LAB_USER_LOGIN = props.getProperty("lab_user_login");
			LAB_USER_PASSWORD=props.getProperty("lab_user_password");

		}
		catch (IOException e) {
			System.out.println("Error reading properties file "+file);
			throw e;
		}
		finally {
			if(is != null) try {is.close();} catch(IOException e){}
		}
		
		if(DB_HOST == null) {
			System.out.println("MySQL host name not found.  Using localhost");
			DB_HOST = "localhost";
		}
		if(DB_NAME == null) {
			System.out.println("Database name not found. Using mainDB");
			DB_NAME = "mainDB";
		}
		if(DB_USER == null) {
			throw new Exception("MySQL user name not found in the properties file: "+file);
		}
		if(LAB_GROUP == null || LAB_GROUP.trim().length() == 0) {
			System.out.println("Lab group name not found. Using MyLab");
			LAB_GROUP = "MyLab";
		}
		
		if(ADMIN_USER_FIRST_NAME == null || ADMIN_USER_FIRST_NAME.trim().length() == 0) {
			throw new Exception("admin_user_firstname cannot be empty in the properties file: "+file);
		}
		if(ADMIN_USER_LAST_NAME == null || ADMIN_USER_LAST_NAME.trim().length() == 0) {
			throw new Exception("admin_user_lastname cannot be empty in the properties file: "+file);
		}
		if(ADMIN_USER_LOGIN == null || ADMIN_USER_LOGIN.trim().length() == 0) {
			throw new Exception("admin_user_login cannot be empty in the properties file: "+file);
		}
		if(ADMIN_USER_PASSWORD == null || ADMIN_USER_PASSWORD.trim().length() == 0) {
			throw new Exception("lab_user_password cannot be empty in the properties file: "+file);
		}
		if(ADMIN_USER_EMAIL == null || ADMIN_USER_EMAIL.trim().length() == 0) {
			throw new Exception("admin_user_email cannot be empty in the properties file: "+file);
		}
		
	}

	public static void main(String[] args) {
		
		String labGroupName = "";
		
		if(args.length > 0) {
			labGroupName = args[0].trim();
		}
		MainDbInitializer initializer = new MainDbInitializer();
		try {
			initializer.initialize(labGroupName);
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
		
	}
}
