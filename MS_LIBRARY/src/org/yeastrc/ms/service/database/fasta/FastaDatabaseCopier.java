/**
 * FastaDatabaseCopier.java
 * @author Vagisha Sharma
 * Sep 29, 2009
 * @version 1.0
 */
package org.yeastrc.ms.service.database.fasta;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import javax.sql.DataSource;

import org.yeastrc.ms.ConnectionFactory;
import org.yeastrc.ms.service.database.DatabaseCopier;
import org.yeastrc.ms.service.database.DatabaseCopyException;
import org.yeastrc.nrseq.dao.NrSeqLookupUtil;
import org.yeastrc.nrseq.domain.NrDatabase;
import org.yeastrc.nrseq.domain.NrDbProtein;

/**
 * 
 */
public class FastaDatabaseCopier {

    
    private final String nrseqDbName;
    private final String nrseqTmpDbName;
    private DataSource nrseqTmpDs;
    private final DataSource nrseqDs;
    
    private final String dbhost;
    private final String dbUser;
    private final String dbPasswd;
    
	
    private FastaDatabaseCopier (String dbHost, String dbUser, String dbPasswd) throws SQLException {
    	
    	this.dbhost = dbHost;
        this.dbUser = dbUser;
        this.dbPasswd = dbPasswd;
        
        nrseqDbName = ConnectionFactory.nrseqDbName();
        nrseqTmpDbName = nrseqDbName+"_temp";
        nrseqDs = ConnectionFactory.getDataSource(dbHost, nrseqDbName, dbUser, dbPasswd);
    }
    
    
    public void copyDatabase(int databaseId) throws DatabaseCopyException {
        
        // make a copy of the YRC_NRSEQ database
        DatabaseCopier copier = new DatabaseCopier(this.dbhost, this.dbUser, this.dbPasswd);
        
        copier.copyDatabase(nrseqDbName, nrseqTmpDbName, true);
        
        nrseqTmpDs = ConnectionFactory.getDataSource(this.dbhost, nrseqTmpDbName, this.dbUser, this.dbPasswd);
        
        // if the database was copied successfully, copy the entries for the given databaseIDs
        // these are my tables
        // tblDatabase 
        // tblProtein      
        // tblProteinDatabase 
        // tblProteinSequence 
        // tblProteins 
        
        // make an entry in the tblDatabase table
        try {
            copyMainFastaDatabaseEntry(databaseId);
        }
        catch (SQLException e) {
            throw new DatabaseCopyException("Exception copying main database entry", e);
        }
        
        // get a list of ids from tblProteinDatabase matching the given databaseId
        List<Integer> dbProteinIds = NrSeqLookupUtil.getDbProteinIdsForDatabase(databaseId);
        System.out.println("# proteins: "+dbProteinIds.size());
        
        // Iterate over the ids and 
        for(int dbProteinId: dbProteinIds) {
            copyProtein(dbProteinId);
        }
                
    }

    private void copyProtein(int dbProteinId) throws DatabaseCopyException {
        
        // 1. copy the entry from tblProteinDatabase
        // 2. copy the corresponding entry from tblProtein (if it has not already been copied)
        // 3. copy the sequence (if it has not already been copied)
        NrDbProtein protein = NrSeqLookupUtil.getDbProtein(dbProteinId);
        if(protein == null) {
            throw new DatabaseCopyException("No protein found in tblProteinDatabase with ID: "+dbProteinId);
        }
        copyProtein(protein);
        
    }

    private void copyProtein(NrDbProtein protein) throws DatabaseCopyException {
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            String sql = "INSERT INTO tblProteinDatabase (id, proteinID, databaseID, accessionString, description) VALUES (?,?,?,?,?)";
            conn = nrseqTmpDs.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, protein.getId());
            stmt.setInt(2, protein.getProteinId());
            stmt.setInt(3, protein.getDatabaseId());
            stmt.setString(4, protein.getAccessionString());
            stmt.setString(5, protein.getDescription());
            stmt.executeUpdate();
        }
        catch (SQLException e) {
            throw new DatabaseCopyException("Error copying to tblProteinDatabase dbProteinID: "+protein.getId(), e);
        }
        finally {
            if(conn != null)    try {conn.close();} catch(SQLException e){}
            if(stmt != null)    try {stmt.close();} catch(SQLException e){}
        }
        
        // copy the corresponding entry from tblProtein
        copyNrProtein(protein.getProteinId());
    }
    
    private void copyNrProtein(int proteinId) throws DatabaseCopyException {
        
        // load the original protein
        NrProtein nrProt;
        try {
            nrProt = loadNrProtein(proteinId);
        }
        catch (SQLException e1) {
            throw new DatabaseCopyException("No entry found in tblProtein for ID: "+proteinId+" in original database");
        }
        
        // if it has not already been copied copy it now
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        try {
            conn = this.nrseqTmpDs.getConnection();
            String sql = "SELECT * FROM tblProtein WHERE id="+proteinId;
            
            stmt = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            rs = stmt.executeQuery(sql);
            if(!rs.next()) {
                rs.moveToInsertRow();
                rs.updateInt("id", proteinId);
                rs.updateInt("sequenceID", nrProt.getSequenceId());
                rs.updateInt("speciesID", nrProt.getSpeciesId());
                rs.insertRow();
            }
        }
        catch (SQLException e) {
            throw new DatabaseCopyException("Error copying nrProteinId: "+proteinId+" to tblProtein", e);
        }
        finally {
            
            if(conn != null)    try {conn.close();} catch(SQLException e){}
            if(stmt != null)    try {stmt.close();} catch(SQLException e){}
            if(rs != null)    try {rs.close();} catch(SQLException e){}
        }
        
        saveSequence(nrProt);
    }

    private void saveSequence(NrProtein nrProt) throws DatabaseCopyException {
        
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        try {
            conn = this.nrseqTmpDs.getConnection();
            String sql = "SELECT * FROM tblProteinSequence WHERE id="+nrProt.getSequenceId();
            
            stmt = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            rs = stmt.executeQuery(sql);
            if(!rs.next()) {
                String seq = NrSeqLookupUtil.getProteinSequence(nrProt.getId());
                rs.moveToInsertRow();
                rs.updateInt("id", nrProt.getSequenceId());
                rs.updateString("sequence", seq);
                rs.insertRow();
            }
        }
        catch (SQLException e) {
            throw new DatabaseCopyException("Error copying sequenceID: "+nrProt.getSequenceId()+" to tblProteinSequence", e);
        }
        finally {
            
            if(conn != null)    try {conn.close();} catch(SQLException e){}
            if(stmt != null)    try {stmt.close();} catch(SQLException e){}
            if(rs != null)    try {rs.close();} catch(SQLException e){}
        }
    }

    private NrProtein loadNrProtein(int proteinId) throws SQLException {
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        try {
            conn = this.nrseqDs.getConnection();
            String sql = "SELECT * FROM tblProtein WHERE id="+proteinId;
            
            stmt = conn.createStatement();
            rs = stmt.executeQuery(sql);
            if(rs.next()) {
                NrProtein prot = new NrProtein();
                prot.setId(rs.getInt("id"));
                prot.setSequenceId(rs.getInt("SequenceID"));
                prot.setSpeciesId(rs.getInt("speciesID"));
                return prot;
            }
            else
                return null;
        }
        finally {
            
            if(conn != null)    try {conn.close();} catch(SQLException e){}
            if(stmt != null)    try {stmt.close();} catch(SQLException e){}
            if(rs != null)    try {rs.close();} catch(SQLException e){}
        }
    }


    private void copyMainFastaDatabaseEntry(int databaseId) throws SQLException  {
        NrDatabase origEntry = NrSeqLookupUtil.getDatabase(databaseId);
        
        Connection conn = null;
        Statement stmt = null;
        try {
            conn = this.nrseqTmpDs.getConnection();
            String sql = "INSERT into tblDatabase "+
            "VALUES("+origEntry.getId()+", '"
            +origEntry.getName()+"', '"+origEntry.getDescription()+"')";
            
            stmt = conn.createStatement();
            stmt.executeUpdate(sql);
        }
        finally {
            
            if(conn != null)    try {conn.close();} catch(SQLException e){}
            if(stmt != null)    try {stmt.close();} catch(SQLException e){}
        }
    }
    
    public static void main(String[] args) throws DatabaseCopyException, SQLException, IOException {
        
        FastaDatabaseCopier copier = new FastaDatabaseCopier("localhost", "root", "");
        copier.copyDatabase(123);
        
    }
    
    private static class NrProtein {
        private int id;
        private int sequenceId;
        private int speciesId;
        public int getId() {
            return id;
        }
        public void setId(int id) {
            this.id = id;
        }
        public int getSequenceId() {
            return sequenceId;
        }
        public void setSequenceId(int sequenceId) {
            this.sequenceId = sequenceId;
        }
        public int getSpeciesId() {
            return speciesId;
        }
        public void setSpeciesId(int speciesId) {
            this.speciesId = speciesId;
        }
    }
}
