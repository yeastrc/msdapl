/**
 * FastaDatabaseSuffixCreator.java
 * @author Vagisha Sharma
 * Oct 1, 2009
 * @version 1.0
 */
package org.yeastrc.ms.service.database.fasta;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.yeastrc.ms.ConnectionFactory;
import org.yeastrc.ms.service.database.DatabaseCopyException;
import org.yeastrc.ms.util.AminoAcidUtilsFactory;
import org.yeastrc.ms.util.TimeUtils;
import org.yeastrc.nrseq.dao.NrSeqLookupUtil;
import org.yeastrc.nrseq.domain.NrDbProteinFull;

/**
 * NOTE: This class is no longer used.
 */
public class FastaDatabaseSuffixCreator {

    public static final int SUFFIX_LENGTH = 4;
    //private String nrseqDbName;
    private DataSource nrseqDs;
    
    private String dbTableName;
    private static String suffixTableName = "suffix_"+SUFFIX_LENGTH;
    
    private Map<String, Integer> suffixIdMap;
    
    private List<Suffix> suffixCache;
    
    private static final int BUF_SIZE = 1000;
    
    private static final Logger log = Logger.getLogger(FastaDatabaseSuffixCreator.class.getName());
    
    public void createSuffixTable(int databaseId) throws SQLException {
        
        log.info("\n\nCreating suffix table for databaseID: "+databaseId);
        
        // set up our datasource
        nrseqDs = ConnectionFactory.getNrseqDataSource();
        
        suffixCache = new ArrayList<Suffix>();
        
        // check if the main suffix table exists. If not, create it.
        if(checkTableExists(suffixTableName)) {
            log.info("Main suffix table exists");
        }
        else {
            log.info("Creating main suffix table: "+suffixTableName);
            createMainSuffixTable();
            this.addMainSuffixTableIndex();
            buildSuffixTable();
        }
        
        // create the suffix table for the given database
        dbTableName = getDbSuffixTableName(databaseId);
        // first check if a table for this database already exists
        if(checkTableExists(dbTableName)) {
            log.info("Table "+dbTableName+" already exists");
            return;
        }
        
        // create the table
        createDbSuffixTable(dbTableName);
        // do the actual work
        buildSuffixIdMap(); // build a map for fast lookup of suffixIds.
        saveSuffixes(dbTableName, databaseId); // save the suffixes in the table
        // add an index on the table
        addTableIndex(dbTableName);
    }

    Map<String, Integer> getSuffixIdMap() throws SQLException {
        if(this.suffixIdMap == null) {
            buildSuffixIdMap();
        }
        return this.suffixIdMap;
    }
    
    private void buildSuffixIdMap() throws SQLException {
       
      log.info("Building suffixID map");
      
      Connection conn = null;
      Statement stmt = null;
      ResultSet rs = null;
      try {
          conn = this.nrseqDs.getConnection();
          stmt = conn.createStatement();
          String sql = "SELECT * FROM "+suffixTableName;
          
          rs = stmt.executeQuery(sql);
          
          this.suffixIdMap = new HashMap<String, Integer>(320000);
          while(rs.next()) {
              suffixIdMap.put(rs.getString("suffix"), rs.getInt("id"));
          }
      }
      finally {
          
          if(conn != null)    try {conn.close();} catch(SQLException e){}
          if(stmt != null)    try {stmt.close();} catch(SQLException e){}
          if(rs != null)      try {rs.close();} catch(SQLException e){}
      }
      log.info("Finished bulding suffixID map");
    }

    public static String getDbSuffixTableName(int databaseId) {
        return "suffix_db_"+databaseId;
    }
    
    public static String getMainSuffixTableName() {
        return suffixTableName;
    }
   
    private void saveSuffixes(String tableName, int databaseId) throws SQLException {
        
        // get all the ids from tblProteinDatabase for the given databaseID
        List<Integer> dbProteinIds = NrSeqLookupUtil.getDbProteinIdsForDatabase(databaseId);
        log.info("# proteins: "+dbProteinIds.size()+" for database: "+databaseId);
        
        // some proteins in a fasta file have the same sequence.  We will not create suffixes twice
//        Set<Integer> seenSequenceIds = new HashSet<Integer>(dbProteinIds.size());
        
        long s = System.currentTimeMillis();
        
        int idx = 0;
        for(int dbProteinId: dbProteinIds) {
            NrDbProteinFull protein = NrSeqLookupUtil.getDbProteinFull(dbProteinId);
            
//            if(seenSequenceIds.contains(protein.getSequenceId()))
//                continue;
//            else
//                seenSequenceIds.add(protein.getSequenceId());
            
            String sequence = NrSeqLookupUtil.getProteinSequenceForNrSeqDbProtId(dbProteinId);
            
            createSuffixes(sequence, protein.getSequenceId(), dbProteinId);
            
            if(this.suffixCache.size() >= BUF_SIZE)
                flushDbSuffixCache(suffixCache);
            
            if(idx%1000 == 0) {
                log.info("Saved suffixes for # proteins "+idx);
            }
            idx++;
        }
        
        if(this.suffixCache.size() > 0)
            flushDbSuffixCache(suffixCache);
        
        long e = System.currentTimeMillis();
        log.info("Total time to create suffix table for databaseID: "+databaseId+" was "
                +TimeUtils.timeElapsedSeconds(s, e)+"\n\n");
    }

    
    private void createSuffixes(String sequence, int sequenceId, int dbProteinId) throws SQLException {
        
        Set<String> uniqSuffixes = new HashSet<String>();
        
        // Remove any '*' characters from the sequence
        String oldSequence = sequence;
        sequence = sequence.replaceAll("\\*", "");
        if(!oldSequence.equals(sequence)) {
            log.warn("Sequence contains *'s : "+oldSequence);
        }
        
        // get the unique suffixes in this sequence
        for(int i = 0; i < sequence.length(); i++) {
            int end = Math.min(i+SUFFIX_LENGTH, sequence.length());
            String subseq = sequence.substring(i, end);
            uniqSuffixes.add(subseq);
            if(i+SUFFIX_LENGTH >= sequence.length())
                break;
        }
        
        // add the suffixes to the cache after looking up the suffixId from the suffixIdMap.
        for(String s: uniqSuffixes) {
            int suffixId = getSuffixId(s);
            // suffixes that contain characters that are not amino acid codes will not 
            // have an id in the suffixIdMap. We will ignore these suffixes. 
            if(suffixId == 0) {
                log.warn("No id found for: "+s+"; sequenceID: "+sequenceId);
                continue;
            }
            Suffix suffix = new Suffix();
            suffix.dbProteinId = dbProteinId;
            suffix.sequenceId = sequenceId;
            suffix.suffixId = suffixId;
            suffix.suffix = s;
            this.suffixCache.add(suffix);
        }
    }
    
    
    private int getSuffixId(String suffix) throws SQLException {
        
        Integer id = suffixIdMap.get(suffix);
        if(id == null) {
            return 0;
        }
        else {
            return id;
        }
    }
    
    private void flushDbSuffixCache(List<Suffix> cache) throws SQLException {
        
//        log.  info("Flushing...");
        
        Connection conn = null;
        Statement stmt = null;
        StringBuilder sql = new StringBuilder("INSERT INTO "+dbTableName+" VALUES ");
        for(Suffix suffix: cache) {
            sql.append("(");
            sql.append(suffix.sequenceId+", ");
            sql.append(suffix.dbProteinId+", ");
            sql.append(suffix.suffixId);
            sql.append("),");
        }
        sql.deleteCharAt(sql.length() - 1);
        
        try {
            conn = this.nrseqDs.getConnection();
            stmt = conn.createStatement();
            stmt.executeUpdate(sql.toString());
        }
        finally {
            
            if(conn != null)    try {conn.close();} catch(SQLException e){}
            if(stmt != null)    try {stmt.close();} catch(SQLException e){}
        }
        cache.clear();
    }

    
    private boolean checkTableExists(String tableName) throws SQLException {
        
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        try {
            conn = this.nrseqDs.getConnection();
            String sql = "SHOW TABLES LIKE '"+tableName+"'";
            
            stmt = conn.createStatement();
            rs = stmt.executeQuery(sql);
            if(rs.next())
                return true;
            else
                return false;
        }
        finally {
            
            if(conn != null)    try {conn.close();} catch(SQLException e){}
            if(stmt != null)    try {stmt.close();} catch(SQLException e){}
            if(rs != null)    try {rs.close();} catch(SQLException e){}
        }
    }
    
    private void createMainSuffixTable() throws SQLException {
        
        Connection conn = null;
        Statement stmt = null;
        try {
            conn = this.nrseqDs.getConnection();
            String sql = "CREATE TABLE "+suffixTableName+" (id INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY, suffix VARCHAR("+SUFFIX_LENGTH+") NOT NULL)";
            
            stmt = conn.createStatement();
            stmt.executeUpdate(sql);
        }
        finally {
            
            if(conn != null)    try {conn.close();} catch(SQLException e){}
            if(stmt != null)    try {stmt.close();} catch(SQLException e){}
        }
    }
    
    private void buildSuffixTable() throws SQLException {
        
        char[] aaChars = AminoAcidUtilsFactory.getProteinAminoAcidUtils().getAminoAcidChars();
        
        int[] posIndex = new int[SUFFIX_LENGTH];
        for(int i = 0; i < posIndex.length; i++) {
            posIndex[i] = 0;
        }
        int[] posIncr = new int[SUFFIX_LENGTH];
        int numPossibilities = 1;
        for(int i = 0; i < posIncr.length; i++) {
            posIncr[i] = numPossibilities;
            numPossibilities *= aaChars.length;
        }
        
        List<String> suffixes = new ArrayList<String>();
        
        for(int i = 1; i <= numPossibilities; i++) {
            StringBuilder buf = new StringBuilder();
            
            for(int j = 0; j < SUFFIX_LENGTH; j++) {
                buf.append(aaChars[posIndex[j]]);
            }
            
            for(int x = 0; x < SUFFIX_LENGTH; x++) {
                int y = i % posIncr[x];
                if(y == 0) {
                    posIndex[x] = (posIndex[x]+ 1)%aaChars.length;
                };
            }
            suffixes.add(buf.toString());
            if(suffixes.size() >= BUF_SIZE) {
               flushSuffixCache(suffixes);
            }
        }

        if(suffixes.size() >= 1) {
            flushSuffixCache(suffixes);
        }
    }
    
    private void flushSuffixCache(List<String> cache) throws SQLException {
        
//      log.info("Flushing...");
      
      Connection conn = null;
      Statement stmt = null;
      StringBuilder sql = new StringBuilder("INSERT INTO "+suffixTableName+" (suffix) VALUES ");
      for(String suffix: cache) {
          sql.append("('");
          sql.append(suffix);
          sql.append("'),");
      }
      sql.deleteCharAt(sql.length() - 1);
      
      try {
          conn = this.nrseqDs.getConnection();
          stmt = conn.createStatement();
          stmt.executeUpdate(sql.toString());
      }
      finally {
          
          if(conn != null)    try {conn.close();} catch(SQLException e){}
          if(stmt != null)    try {stmt.close();} catch(SQLException e){}
      }
      cache.clear();
  }
    
    private void addMainSuffixTableIndex() throws SQLException {
        
        Connection conn = null;
        Statement stmt = null;
        try {
            conn = this.nrseqDs.getConnection();
            String sql = "ALTER TABLE "+suffixTableName+" ADD INDEX (suffix)";
            
            stmt = conn.createStatement();
            stmt.executeUpdate(sql);
        }
        finally {
            
            if(conn != null)    try {conn.close();} catch(SQLException e){}
            if(stmt != null)    try {stmt.close();} catch(SQLException e){}
        }
    }


    private void createDbSuffixTable(String tableName) throws SQLException {
        
        // TODO for partitioning if required
        // alter table suffix_db_124 partition by key(suffixID) partitions 10;
        Connection conn = null;
        Statement stmt = null;
        try {
            conn = this.nrseqDs.getConnection();
            String sql = "CREATE TABLE "+tableName+" (sequenceID INT UNSIGNED NOT NULL, dbProteinID INT UNSIGNED NOT NULL, suffixID INT UNSIGNED NOT NULL)";
            
            stmt = conn.createStatement();
            stmt.executeUpdate(sql);
        }
        finally {
            
            if(conn != null)    try {conn.close();} catch(SQLException e){}
            if(stmt != null)    try {stmt.close();} catch(SQLException e){}
        }
    }
    
    private void addTableIndex(String tableName) throws SQLException {
        
        long s = System.currentTimeMillis();
        Connection conn = null;
        Statement stmt = null;
        try {
            conn = this.nrseqDs.getConnection();
            String sql = "ALTER TABLE "+tableName+" ADD INDEX (suffixID)";
            
            stmt = conn.createStatement();
            stmt.executeUpdate(sql);
        }
        finally {
            
            if(conn != null)    try {conn.close();} catch(SQLException e){}
            if(stmt != null)    try {stmt.close();} catch(SQLException e){}
        }
        long e = System.currentTimeMillis();
        log.info("Time to add index on suffixID: "+TimeUtils.timeElapsedSeconds(s, e)+" seconds");
    }
    
    
    public static void main(String[] args) throws DatabaseCopyException, SQLException, IOException {
     
        FastaDatabaseSuffixCreator creator = new FastaDatabaseSuffixCreator();
        creator.createSuffixTable(123);

    }
 
 
    private class Suffix {
        
        int dbProteinId;
        int sequenceId;
        int suffixId;
        String suffix;
        
        public int getSuffixId() {
            return suffixId;
        }
        public void setSuffixId(int suffixId) {
            this.suffixId = suffixId;
        }
        //        private String suffix;
        public int getDbProteinId() {
            return dbProteinId;
        }
        public void setDbProteinId(int dbProteinId) {
            this.dbProteinId = dbProteinId;
        }
        public int getSequenceId() {
            return sequenceId;
        }
        public void setSequenceId(int sequenceId) {
            this.sequenceId = sequenceId;
        }
        public String getSuffix() {
            return suffix;
        }
        public void setSuffix(String suffix) {
            this.suffix = suffix;
        }
    }
}
