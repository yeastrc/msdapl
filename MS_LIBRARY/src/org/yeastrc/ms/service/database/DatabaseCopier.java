/**
 * DatabaseCopier.java
 * @author Vagisha Sharma
 * Sep 29, 2009
 * @version 1.0
 */
package org.yeastrc.ms.service.database;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.log4j.Logger;
import org.yeastrc.ms.ConnectionFactory;

/**
 * 
 */
public class DatabaseCopier {

    private static final Logger log = Logger.getLogger(DatabaseCopier.class.getName());
    
    private final String dbhost;
    private final String dbUser;
    private final String dbPasswd;
    
    public DatabaseCopier(String dbHost, String dbUser, String dbPasswd) {
    	this.dbhost = dbHost;
        this.dbUser = dbUser;
        this.dbPasswd = dbPasswd;
    }
    

	public void copyDatabase(String originalDatabase, String copyDatabase, boolean dropCopyDbIfExists) 
    throws DatabaseCopyException {

        log.info("Original database: "+originalDatabase+"; Copy database: "+copyDatabase);

        if(originalDatabase.equals(copyDatabase)) {
            throw new DatabaseCopyException("original and copy databases have the same name: "+originalDatabase);
        }

        try {
            createCopyDatabase(originalDatabase, copyDatabase, dropCopyDbIfExists);
        }
        catch (SQLException e) {
            throw new DatabaseCopyException("Could not create the copy database: "+copyDatabase+". "+e.getMessage(), e);
        }
        copyTables(originalDatabase, copyDatabase);
        copyTriggers(originalDatabase, copyDatabase);
    }
    
    public void copyDatabase(String originalDatabase, String copyDatabase) 
        throws DatabaseCopyException {
        
        copyDatabase(originalDatabase, copyDatabase, false);
    }
    
    //-----------------------------------------------------------------------------------------
    // DATABASE
    //-----------------------------------------------------------------------------------------
    private void createCopyDatabase(String originalDatabase, String copyDatabase,
            boolean dropCopyDbIfExists) throws SQLException {

        Connection conn = null;
        Statement stmt = null;
        try {
            conn = ConnectionFactory.getConnection(this.dbhost, originalDatabase, this.dbUser, this.dbPasswd);
            stmt = conn.createStatement();
            if(dropCopyDbIfExists)
                stmt.execute("DROP DATABASE IF EXISTS "+copyDatabase);
            stmt.execute("CREATE DATABASE "+copyDatabase);
        }
        finally {
            if(stmt != null) try { stmt.close(); stmt = null;}
            catch (SQLException e) {}

            if(conn != null) try { conn.close(); conn = null;}
            catch (SQLException e) {}
        }
    }


    //-----------------------------------------------------------------------------------------
    // TABLES
    //-----------------------------------------------------------------------------------------
    public void copyTables(String originalDatabase, String copyDatabase) throws DatabaseCopyException {
        List<String> tableNames = getTableNames(originalDatabase);
        createTables(originalDatabase, copyDatabase, tableNames);
        matchTables(originalDatabase, copyDatabase);
    }
    
    private final List<String> getTableNames(String databaseName) throws DatabaseCopyException {
        
        List<String> tableNames;
        Connection conn = null;
        try {
            conn = ConnectionFactory.getConnection(this.dbhost, databaseName, this.dbUser, this.dbPasswd);
            tableNames = getTableNames(conn);
        }
        catch (SQLException e) {
            throw new DatabaseCopyException("Exception getting table names for: "+databaseName, e);
        }
        finally {
            if(conn != null) try { conn.close(); conn = null;}
            catch (SQLException e) {}
        }
        return tableNames;
    }
    
    private List<String> getTableNames(Connection conn) throws SQLException {
        
        Statement stmt = null;
        ResultSet rs = null;
        List<String> tableNames = new ArrayList<String>();
        try {
            String sql = "SHOW TABLES";
            
            stmt = conn.createStatement();
            rs = stmt.executeQuery(sql);
            
            while(rs.next()) {
                tableNames.add(rs.getString(1));
            }
        }
        finally {
            if(rs != null) try { rs.close(); rs = null;}
            catch (SQLException e) {}
            
            if(stmt != null) try { stmt.close(); stmt = null;}
            catch (SQLException e) {}
        }
        return tableNames;
    }

    private void createTables(String originalDatabase, 
            String copyDatabase, List<String> tableNames) throws DatabaseCopyException {
        
        Connection conn  = null;
        Statement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = ConnectionFactory.getConnection(this.dbhost, copyDatabase, this.dbUser, this.dbPasswd);
            stmt = conn.createStatement();
            
            for(String tableName: tableNames) {
//                String sql = "CREATE TABLE "+tableName+" LIKE "+ConnectionFactory.masterDbName()+"."+tableName;
                String sql = "SHOW CREATE TABLE "+originalDatabase+"."+tableName;
//                log.info(sql);
                rs = stmt.executeQuery(sql);
                String createSql = null;
                if(rs.next()) {
                    createSql = rs.getString(2);
                    log.info(createSql);
                    stmt.execute(createSql);
                }
                else {
                    throw new DatabaseCopyException("Cannot get CREATE statement for table: "+tableName);
                }
                rs.close();
            }
        }
        catch (SQLException e) {
            throw new DatabaseCopyException("Exception creating tables", e);
        }
        finally {
            if(rs != null) try { rs.close(); rs = null;}
            catch (SQLException e) {}
            
            if(stmt != null) try { stmt.close(); stmt = null;}
            catch (SQLException e) {}
            
            if(conn != null) try { conn.close(); conn = null;}
            catch (SQLException e) {}
        }
    }
    
    public void matchTables(String originalDatabase, String copyDatabase) throws DatabaseCopyException {
        
        List<String> tableNames;
        tableNames = getTableNames(originalDatabase);
        List<String> newTableNames;
        newTableNames = getTableNames(copyDatabase);
        
        if(newTableNames.size() != tableNames.size()) {
            throw new DatabaseCopyException("Number of tables created in the temp database do not match");
        }
        Collections.sort(tableNames);
        Collections.sort(newTableNames);
        for(int i = 0; i < tableNames.size(); i++) {
            if(!(tableNames.get(i).equals(newTableNames.get(i)))) {
                throw new DatabaseCopyException("Table names do not match: mainTable: "
                        +tableNames.get(i)+"; tempTable: "+newTableNames.get(i));
            }
        }
    }

    //-----------------------------------------------------------------------------------------
    // TRIGGERS
    //-----------------------------------------------------------------------------------------
    public void copyTriggers(String originalDatabase, String copyDatabase) throws DatabaseCopyException {
        List<String> triggerNames = getTriggerNames(originalDatabase);
        createTriggers(originalDatabase, copyDatabase, triggerNames);
        matchTriggers(originalDatabase, copyDatabase);
    }
    
    private List<String> getTriggerNames(String databaseName) throws DatabaseCopyException {
        List<String> tableNames;
        Connection conn = null;
        try {
            conn = ConnectionFactory.getConnection(this.dbhost, databaseName, this.dbUser, this.dbPasswd);
            tableNames = getTriggerNames(conn);
        }
        catch (SQLException e) {
            throw new DatabaseCopyException("Exception getting trigger names for: "+databaseName, e);
        }
        finally {
            if(conn != null) try { conn.close(); conn = null;}
            catch (SQLException e) {}
        }
        return tableNames;
    }
    
    private List<String> getTriggerNames(Connection conn) throws SQLException {
        
        Statement stmt = null;
        ResultSet rs = null;
        List<String> tableNames = new ArrayList<String>();
        try {
            String sql = "SHOW TRIGGERS";
            
            stmt = conn.createStatement();
            rs = stmt.executeQuery(sql);
            
            while(rs.next()) {
                tableNames.add(rs.getString(1));
            }
        }
        finally {
            if(rs != null) try { rs.close(); rs = null;}
            catch (SQLException e) {}
            
            if(stmt != null) try { stmt.close(); stmt = null;}
            catch (SQLException e) {}
        }
        return tableNames;
    }
    
    private void createTriggers(String originalDatabase, String copyDatabase, List<String> triggerNames) 
            throws DatabaseCopyException {
        
        Connection conn  = null;
        Statement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = ConnectionFactory.getConnection(this.dbhost, copyDatabase, this.dbUser, this.dbPasswd);
            stmt = conn.createStatement();
            
            for(String triggerName: triggerNames) {
                String sql = "SHOW CREATE TRIGGER "+originalDatabase+"."+triggerName;
//                log.info(sql);
                rs = stmt.executeQuery(sql);
                String createSql = null;
                if(rs.next()) {
                    createSql = rs.getString("SQL Original Statement");
                    log.info(createSql);
                    stmt.execute(createSql);
                }
                else {
                    throw new DatabaseCopyException("Cannot get CREATE statement for trigger: "+triggerName);
                }
                rs.close();
            }
        }
        catch (SQLException e) {
            throw new DatabaseCopyException("Exception creating triggers", e);
        }
        finally {
            if(rs != null) try { rs.close(); rs = null;}
            catch (SQLException e) {}
            
            if(stmt != null) try { stmt.close(); stmt = null;}
            catch (SQLException e) {}
            
            if(conn != null) try { conn.close(); conn = null;}
            catch (SQLException e) {}
        }
    }

    private void matchTriggers(String originalDatabase, String copyDatabase) throws DatabaseCopyException {
        
        List<String> triggerNames = getTriggerNames(originalDatabase);
        List<String> newTriggerNames = getTriggerNames(copyDatabase);
        
        if(newTriggerNames.size() != triggerNames.size()) {
            throw new DatabaseCopyException("Number of TRIGGERS created in the temp database do not match");
        }
        Collections.sort(triggerNames);
        Collections.sort(newTriggerNames);
        for(int i = 0; i < triggerNames.size(); i++) {
            if(!(triggerNames.get(i).equals(newTriggerNames.get(i)))) {
                throw new DatabaseCopyException("TRIGGER names do not match: mainTable: "
                        +triggerNames.get(i)+"; tempTable: "+newTriggerNames.get(i));
            }
        }
    }
    
    public static void main(String[] args) {
        // create a copy of the YRC_NRSEQ database
        String originalDb = "yrc_nrseq";
        String testDb = "yrc_nrseq_test";
        DatabaseCopier copier = new DatabaseCopier("localhost", "root", "");
        
        try {
            copier.copyDatabase(originalDb, testDb);
        }
        catch (DatabaseCopyException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
