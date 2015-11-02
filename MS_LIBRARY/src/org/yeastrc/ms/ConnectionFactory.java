/**
 * ConnectionFactory.java
 * @author Vagisha Sharma
 * Apr 24, 2009
 * @version 1.0
 */
package org.yeastrc.ms;

import java.io.IOException;
import java.io.Reader;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

import javax.sql.DataSource;

import org.apache.commons.dbcp.BasicDataSource;
import org.apache.log4j.Logger;

import com.ibatis.common.resources.Resources;

/**
 * 
 */
public class ConnectionFactory {

    private static String msDataDbName;
    private static DataSource msDataDataSource = null;
    
    private static String nrseqDbName;
    private static DataSource nrseqDataSource = null;
    
    
    private static final Logger log = Logger.getLogger(ConnectionFactory.class.getName());
    
    static {
        Properties props = new Properties();
        Reader reader = null;
        try {
            reader = Resources.getResourceAsReader("msDataDB.properties");
            props.load(reader);
            
            String msDataUser = props.getProperty("db.user");
            String msDataPassword = props.getProperty("db.password");
            String mainDbUrl = props.getProperty("db.url");
            msDataDbName = props.getProperty("db.name");
            msDataDataSource = setupDataSource(mainDbUrl, msDataUser, msDataPassword);
            
            
            String nrseqUser = props.getProperty("db.nrseq.user");
            String nrseqPassword = props.getProperty("db.nrseq.password");
            String nrseqUrl = props.getProperty("db.nrseq.url");
            nrseqDbName = props.getProperty("db.nrseq.name");
            nrseqDataSource = setupDataSource(nrseqUrl, nrseqUser, nrseqPassword);
            
        }
        catch (IOException e) {
            log.error("Error reading properties file msDataDB.properties", e);
        }
        finally {
        	if(reader != null) try {reader.close();} catch(IOException e){}
        }
        
    }
    private ConnectionFactory() {}
    
    public static Connection getNrseqConnection() throws SQLException {
    	return nrseqDataSource.getConnection();
    }
  
    public static Connection getMsDataConnection() throws SQLException {
    	return msDataDataSource.getConnection();
    }
    
    private static DataSource setupDataSource(String dbUrl, String user, String password) {
        
        BasicDataSource bds = new BasicDataSource();;
        bds.setDriverClassName("com.mysql.jdbc.Driver");
        bds.setUsername(user);
        bds.setPassword(password);
        bds.setUrl(dbUrl);
        bds.setMaxActive(30);
        bds.setMaxIdle(10);
        bds.setMaxWait(10000);
        bds.setDefaultAutoCommit(true);
        bds.setValidationQuery("SELECT 1");
        return bds;
    }
    
    public static String masterDbName() {
        return msDataDbName;
    }
    
    public static String nrseqDbName() {
        return nrseqDbName;
    }
    
    public static DataSource getNrseqDataSource() {
    	return nrseqDataSource;
    }
    
    public static DataSource getDataSource(String host, String dbName, String user, String password) {
    	String dbUrl = "jdbc:mysql://"+host+"/"+dbName;
        DataSource ds = setupDataSource(dbUrl, user, password);
        return ds;
    }
    
    public static Connection getConnection(String host, String dbName, String user, String password) throws SQLException {
        String dbUrl = "jdbc:mysql://"+host+"/"+dbName;
        DataSource ds = setupDataSource(dbUrl, user, password);
        return ds.getConnection();
    }
}
