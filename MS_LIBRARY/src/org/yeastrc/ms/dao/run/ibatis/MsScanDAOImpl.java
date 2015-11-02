/**
 * MsScanDAOImpl.java
 * @author Vagisha Sharma
 * Jun 17, 2008
 * @version 1.0
 */
package org.yeastrc.ms.dao.run.ibatis;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.yeastrc.db.DBConnectionManager;
import org.yeastrc.ms.ConnectionFactory;
import org.yeastrc.ms.dao.DAOFactory;
import org.yeastrc.ms.dao.ibatis.BaseSqlMapDAO;
import org.yeastrc.ms.dao.run.MsScanDAO;
import org.yeastrc.ms.domain.run.DataConversionType;
import org.yeastrc.ms.domain.run.MsScan;
import org.yeastrc.ms.domain.run.MsScanIn;
import org.yeastrc.ms.domain.run.Peak;
import org.yeastrc.ms.domain.run.PeakStorageType;
import org.yeastrc.ms.util.PeakStringBuilder;
import org.yeastrc.ms.util.StringUtils;

import com.ibatis.sqlmap.client.SqlMapClient;
import com.ibatis.sqlmap.client.extensions.ParameterSetter;
import com.ibatis.sqlmap.client.extensions.ResultGetter;
import com.ibatis.sqlmap.client.extensions.TypeHandlerCallback;

/**
 * 
 */
public class MsScanDAOImpl extends BaseSqlMapDAO implements MsScanDAO {

    private PeakStorageType peakStorageType;
    
    public MsScanDAOImpl(SqlMapClient sqlMap, PeakStorageType peakStorageType) {
        super(sqlMap);
        this.peakStorageType = peakStorageType;
    }

    public int save(MsScanIn scan, int runId, int precursorScanId) {
        MsScanSqlMapParam scanDb = new MsScanSqlMapParam(runId, precursorScanId, scan);
        int scanId = saveAndReturnId("MsScan.insert", scanDb);
        
        // save the peak data
        MsScanDataSqlMapParam param;
        String statementName = "insertPeakData";
        try {
            param = new MsScanDataSqlMapParam(scanId, scan, peakStorageType);
        }
        catch (IOException e) {
            throw new RuntimeException("Failed to execute statement: "+statementName, e);
        }
        
        save("MsScan."+statementName, param);
        return scanId;
    }

    public int save(MsScanIn scan, int runId) {
        return save(scan, runId, 0); // a value of 0 for precursorScanId should insert NULL in the database.
    }
    
    public MsScan load(int scanId) {
        return (MsScan) queryForObject("MsScan.select", scanId);
    }

    @Override
    public MsScan loadScanLite(int scanId) {
        return (MsScan) queryForObject("MsScan.selectScanNoData", scanId);
    }
    
    @Override
    public int loadScanNumber(int scanId) {
        Integer scanNum = (Integer)queryForObject("MsScan.selectScanNumber", scanId);
        if(scanNum != null)
            return scanNum;
        else
            return 0;
    }
    
    @Override
    public List<Integer> loadScanIdsForRun(int runId) {
        return queryForList("MsScan.selectScanIdsForRun", runId);
    }
    
    @Override
    public List<Integer> loadScanIdsForRunAndLevel(int runId, int level) {
        Map<String, Integer> map = new HashMap<String, Integer>(4);
        map.put("runId", runId);
        map.put("level", level);
        return queryForList("MsScan.selectScanIdsForRunAndLevel", map);
    }
    
    @Override
    public List<Integer> loadMS2ScanIdsForMS1Scan(int ms1ScanId) {
        return queryForList("MsScan.selectMs2ScansForMs1Scan", ms1ScanId);
    }

    @Override
    public int numScans(int runId) {
        return (Integer)queryForObject("MsScan.selectScanCountForRun", runId);
    }
    
    
    @Override
    public int numScans(int runId, int level) {
        Map<String, Integer> map = new HashMap<String, Integer>(4);
        map.put("runId", runId);
        map.put("level", level);
        return (Integer)queryForObject("MsScan.selectScanCountForRunLevel", map);
    }
    
    
    @Override
    public int numScansForExperimentIdScanLevelNotOnePreMZNotNULL(int experimentId) {
        return (Integer)queryForObject("MsScan.selectScanCountForExperimentIdScanLevelNotOnePreMZNotNULL", experimentId);
    }
    
    
    @Override
    public int numScansForExperimentIdScanLevelNotOne(int experimentId) {
        return (Integer)queryForObject("MsScan.selectScanCountForExperimentIdScanLevelNotOne", experimentId);
    }
    
    
    @Override
    public BigDecimal getMaxPreMZForExperimentIdScanLevelNotOnePreMZNotNULL(int experimentId) {
    	BigDecimal maxPreMZ = (BigDecimal) queryForObject("MsScan.getMaxPreMZForExperimentIdScanLevelNotOnePreMZNotNULL", experimentId);
        return maxPreMZ;
    }
    
    @Override
    public BigDecimal getMinPreMZForExperimentIdScanLevelNotOnePreMZNotNULL(int experimentId) {
    	BigDecimal minPreMZ = (BigDecimal) queryForObject("MsScan.getMinPreMZForExperimentIdScanLevelNotOnePreMZNotNULL", experimentId);
        return minPreMZ;
    }
    
    
    // Not used, iBatis returned exception: Error: executeQueryForObject returned too many results.
//    @Override
//    public List<BigDecimal> getPreMZForExperimentId(int experimentId) {
//    	List<BigDecimal> preMZList = (List<BigDecimal>) queryForObject("MsScan.getPreMZListForExperimentId", experimentId);
//        return preMZList;
//    }
    
    

    // query string
    private static final String getPreMZForExperimentIdScanLevelNotOnePreMZNotNULLSqlStr = "SELECT msScan.preMZ FROM msScan "
    		+ " INNER JOIN msRun ON msScan.runID = msRun.id INNER JOIN msExperimentRun ON msRun.id = msExperimentRun.runID  "
    		+ " WHERE msExperimentRun.experimentID = ? AND msScan.level != 1 AND msScan.preMZ IS NOT NULL";
    
    @Override
    public List<BigDecimal> getPreMZForExperimentIdScanLevelNotOnePreMZNotNULL(int experimentId) {
    	
    	
    	final String sql = getPreMZForExperimentIdScanLevelNotOnePreMZNotNULLSqlStr;
    	
    	

        // Get our connection to the database.
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;    
        
        List<BigDecimal> outputList = new ArrayList<BigDecimal>();


        try {
            conn = DAOFactory.instance().getConnection();

            stmt = conn.prepareStatement(sql);

            stmt.setInt( 1, experimentId );
            
            rs = stmt.executeQuery();

            while(rs.next()) {
            	
            	BigDecimal preMZ = rs.getBigDecimal("preMZ");

            	outputList.add(preMZ);
            }
        }
        catch (SQLException e) {
            log.error("getPreMZForExperimentIdScanLevelNotOnePreMZNotNULL: Failed to execute sql: " + sql, e);
            throw new RuntimeException("getPreMZForExperimentIdScanLevelNotOnePreMZNotNULL: Failed to execute sql: " + sql, e);
        }
        finally {

            // Always make sure result sets and statements are closed,
            // and the connection is returned to the pool
            if (rs != null) {
                try { rs.close(); } catch (SQLException e) { ; }
                rs = null;
            }
            if (stmt != null) {
                try { stmt.close(); } catch (SQLException e) { ; }
                stmt = null;
            }
            if (conn != null) {
                try { conn.close(); } catch (SQLException e) { ; }
                conn = null;
            }
        }
        
        return outputList;
    }
    
    
    
    //  Add msScan.level != 1  to only retrieve MS2 records

    // query string
    private static final String getPreMZArrayForExperimentIdScanLevelNotOnePreMZNotNULLSqlStr = "SELECT msScan.preMZ FROM msScan "
    		+ " INNER JOIN msRun ON msScan.runID = msRun.id INNER JOIN msExperimentRun ON msRun.id = msExperimentRun.runID  "
    		+ " WHERE msExperimentRun.experimentID = ? AND msScan.level != 1 AND msScan.preMZ IS NOT NULL";
    
    @Override
    public double[] getPreMZArrayForExperimentIdScanLevelNotOnePreMZNotNULL(int experimentId) {
    	
    	
    	final String sql = getPreMZArrayForExperimentIdScanLevelNotOnePreMZNotNULLSqlStr;
    	
    	
        // Get our connection to the database.
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;    
        
        int numScans = numScansForExperimentIdScanLevelNotOnePreMZNotNULL( experimentId );
        
        double[] preMZArray  = new double[ numScans ];


        try {
            conn = DAOFactory.instance().getConnection();

            stmt = conn.prepareStatement(sql);

            stmt.setInt( 1, experimentId );
            
            rs = stmt.executeQuery();
            
            int rowIndex = 0;

            while(rs.next()) {
            	
            	if ( rowIndex >= preMZArray.length ) {
            		
            		String msg = "getPreMZArrayForExperimentId: Unexpected error: rowIndex >= preMZArray.length:"
            				+ "experimentId: " + experimentId + ", sql: " + sql;
            		log.error( msg );
            		throw new RuntimeException(msg);
            	}
            	
            	double preMZ = rs.getDouble("preMZ");

            	preMZArray[ rowIndex ] = preMZ;
            	
            	rowIndex++;
            }
            
        	if ( rowIndex != preMZArray.length ) {
        		
        		String msg = "getPreMZArrayForExperimentId: Unexpected error: after loading all rows, rowIndex != preMZArray.length:"
        				+ "experimentId: " + experimentId + ", sql: " + sql;
        		log.error( msg );
        		throw new RuntimeException(msg);
        	}
        }
        catch (SQLException e) {
            log.error("getPreMZArrayForExperimentIdScanLevelNotOnePreMZNotNULL: Failed to execute sql: " + sql, e);
            throw new RuntimeException("Failed to execute sql: " + sql, e);
        }
        finally {

            // Always make sure result sets and statements are closed,
            // and the connection is returned to the pool
            if (rs != null) {
                try { rs.close(); } catch (SQLException e) { ; }
                rs = null;
            }
            if (stmt != null) {
                try { stmt.close(); } catch (SQLException e) { ; }
                stmt = null;
            }
            if (conn != null) {
                try { conn.close(); } catch (SQLException e) { ; }
                conn = null;
            }
        }
        
        return preMZArray;
    }
    
    

    // query string
    private static final String getPeakCountArrayForExperimentIdScanLevelNotOneSqlStr = "SELECT msScan.peakCount FROM msScan "
    		+ " INNER JOIN msRun ON msScan.runID = msRun.id INNER JOIN msExperimentRun ON msRun.id = msExperimentRun.runID  "
    		+ " WHERE msExperimentRun.experimentID = ? AND msScan.level != 1 ";
    

	@Override
	public int[] getPeakCountArrayForExperimentIdScanLevelNotOne( int experimentId ) {

    	
    	final String sql = getPeakCountArrayForExperimentIdScanLevelNotOneSqlStr;
    	
    	
        // Get our connection to the database.
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;    
        
        int numScans = numScansForExperimentIdScanLevelNotOne( experimentId );
        
        int[] peakCountArray  = new int[ numScans ];


        try {
            conn = DAOFactory.instance().getConnection();

            stmt = conn.prepareStatement(sql);

            stmt.setInt( 1, experimentId );
            
            rs = stmt.executeQuery();
            
            int rowIndex = 0;

            while(rs.next()) {
            	
            	if ( rowIndex >= peakCountArray.length ) {
            		
            		String msg = "getPeakCountArrayForExperimentIdScanLevelNotOne: Unexpected error: rowIndex >= peakCountArray.length:"
            				+ "experimentId: " + experimentId + ", sql: " + sql;
            		log.error( msg );
            		throw new RuntimeException(msg);
            	}
            	
            	int peakCount = rs.getInt("peakCount");

            	peakCountArray[ rowIndex ] = peakCount;
            	
            	rowIndex++;
            }
            
        	if ( rowIndex != peakCountArray.length ) {
        		
        		String msg = "getPeakCountArrayForExperimentIdScanLevelNotOne: Unexpected error: after loading all rows, rowIndex != peakCountArray.length:"
        				+ "experimentId: " + experimentId + ", sql: " + sql;
        		log.error( msg );
        		throw new RuntimeException(msg);
        	}
        }
        catch (SQLException e) {
            log.error("getPeakCountArrayForExperimentIdScanLevelNotOne: Failed to execute sql: " + sql, e);
            throw new RuntimeException("Failed to execute sql: " + sql, e);
        }
        finally {

            // Always make sure result sets and statements are closed,
            // and the connection is returned to the pool
            if (rs != null) {
                try { rs.close(); } catch (SQLException e) { ; }
                rs = null;
            }
            if (stmt != null) {
                try { stmt.close(); } catch (SQLException e) { ; }
                stmt = null;
            }
            if (conn != null) {
                try { conn.close(); } catch (SQLException e) { ; }
                conn = null;
            }
        }
        
        return peakCountArray;
	}





    
    
    public int loadScanIdForScanNumRun(int scanNum, int runId) {
        Map<String, Integer> map = new HashMap<String, Integer>(2);
        map.put("scanNum", scanNum);
        map.put("runId", runId);
        Integer id = (Integer)queryForObject("MsScan.selectScanIdForScanNumRun", map);
        if (id != null) return id;
        return 0;
    }
    
    public void delete(int scanId) {
        delete("MsScan.delete", scanId);
        delete("MsScan.deletePeakData", scanId);
    }
    
    // ONLY USED FOR MZXML SCANS FOR NOW
    @Override
    public <T extends MsScanIn> List<Integer> save(List<T> scans, int runId) {
        
        List<Integer> keys = insertAllScans(scans, runId);
        insertAllScanData(scans, keys);
        return keys;
        
    }
    
    private <T extends MsScanIn> void insertAllScanData(List<T> scans, List<Integer> keys) {
        
        String sql = "INSERT INTO msScanData (scanID,type,data) VALUES (?,?,COMPRESS(?))";
        
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = ConnectionFactory.getMsDataConnection();
            conn.setAutoCommit(false);
            stmt = conn.prepareStatement(sql);
            
            for(int i = 0; i < scans.size(); i++) {
                MsScanIn scan = scans.get(i);
                int scanId = keys.get(i);
                MsScanDataSqlMapParam param = new MsScanDataSqlMapParam(scanId, scan, peakStorageType);
                prepareScanDataInsertStatement(stmt, param, scanId);
                stmt.addBatch();
            }
            int[] counts = stmt.executeBatch();
            int numInserted = 0;
            conn.commit();
            for(int cnt: counts)    numInserted += cnt;
            if(numInserted != scans.size())
                throw new RuntimeException("Number of scan data rows inserted ("+numInserted+") does not equal number input ("+scans.size()+")");
            
        }
        catch (SQLException e) {
            log.error("Failed to execute sql: "+sql, e);
            throw new RuntimeException("Failed to execute sql: "+sql, e);
        }
        catch (IOException e) {
            log.error("Error convering peak data", e);
            throw new RuntimeException("Failed to save peak data", e);
        }
        finally {
            if(stmt != null) try { stmt.close(); } catch (SQLException e){}
            if(conn != null) try { conn.close(); } catch (SQLException e){}
        }
    }

    private <T extends MsScanIn> List<Integer> insertAllScans(List<T> scans, int runId) {
        String sql = "INSERT INTO msScan (runID, startScanNumber, endScanNumber, ";
        sql +=       "level, preMZ, preScanID, prescanNumber, ";
        sql +=       "retentionTime, fragmentationType, isCentroid, peakCount) ";
        sql +=       "VALUES (?,?,?,?,?,?,?,?,?,?,?)";
        
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            conn = ConnectionFactory.getMsDataConnection();
            stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            conn.setAutoCommit(false);
            
            for(MsScanIn scan: scans) {
                if(runId == 0)  stmt.setNull(1, Types.INTEGER);
                else            stmt.setInt(1, runId);
                
                if(scan.getStartScanNum() == -1)    stmt.setNull(2, Types.INTEGER);
                else                                stmt.setInt(2, scan.getStartScanNum());
                
                if(scan.getEndScanNum() == -1)      stmt.setNull(3, Types.INTEGER);
                else                                stmt.setInt(3, scan.getEndScanNum());
                
                if(scan.getMsLevel() == 0)          stmt.setNull(4, Types.INTEGER);
                else                                stmt.setInt(4, scan.getMsLevel());
                
                stmt.setBigDecimal(5, scan.getPrecursorMz());
                
                int precursorScanId = this.loadScanIdForScanNumRun(scan.getPrecursorScanNum(), runId);
                if(precursorScanId > 0)
                    stmt.setInt(6, precursorScanId);
                else 
                    stmt.setNull(6, Types.INTEGER); // precursorScanId
                
                if(scan.getPrecursorScanNum() == -1)    stmt.setNull(7, Types.INTEGER);
                else                                    stmt.setInt(7, scan.getPrecursorScanNum());
                
                stmt.setBigDecimal(8, scan.getRetentionTime());
                stmt.setString(9, scan.getFragmentationType());
                
                String dataConvType = getDataConversionTypeString(scan.getDataConversionType());
                stmt.setString(10, dataConvType);
                
                if(scan.getPeakCount() == -1)       stmt.setNull(11, Types.INTEGER);
                else                                stmt.setInt(11, scan.getPeakCount());
                
                stmt.addBatch();
            }
            
            int[] counts = stmt.executeBatch();
            conn.commit();
            
            int numInserted = 0;
            for(int cnt: counts)    numInserted += cnt;
            
            if(numInserted != scans.size())
                throw new RuntimeException("Number of scans inserted ("+numInserted+") does not equal number input ("+scans.size()+")");
                
            
            // check that we inserted everything and get the generated ids
            rs = stmt.getGeneratedKeys();
            List<Integer> generatedKeys = new ArrayList<Integer>(scans.size());
            while(rs.next())
                generatedKeys.add(rs.getInt(1));
            
            if(generatedKeys.size() != numInserted)
                throw new RuntimeException("Failed to get auto_increment key for all scans inserted. Number of keys returned: "
                        +generatedKeys.size());
            
            return generatedKeys;
        }
        catch (SQLException e) {
            log.error("Failed to execute sql: "+sql, e);
            throw new RuntimeException("Failed to execute sql: "+sql, e);
        }
        finally {
            if(rs != null) try { rs.close(); } catch (SQLException e){}
            if(stmt != null) try { stmt.close(); } catch (SQLException e){}
            if(conn != null) try { conn.close(); } catch (SQLException e){}
        }
    }
    
    private String getDataConversionTypeString(DataConversionType type) {
        if (DataConversionType.CENTROID == type)                 return "T";
        else if (DataConversionType.NON_CENTROID == type)        return "F";
        else                                                     return null;
    }
    
    private void prepareScanDataInsertStatement(PreparedStatement stmt, MsScanDataSqlMapParam scan, int scanId) throws SQLException, IOException {
        stmt.setInt(1, scanId);
        if(peakStorageType == null)
            stmt.setString(2, null);
        else
            stmt.setString(2, peakStorageType.getCode());
        
        byte[] peakData = scan.getPeakData();
        stmt.setBytes(3, peakData);
    }

    /**
     * Convenience class for encapsulating a MsScan along with the associated runId 
     * and precursorScanId (if any)
     */
    public static class MsScanSqlMapParam implements MsScan {

        private int runId;
        private int precursorScanId;
        private MsScanIn scan;

        public MsScanSqlMapParam(int runId, int precursorScanId, MsScanIn scan) {
            this.runId = runId;
            this.precursorScanId = precursorScanId;
            this.scan = scan;
        }

        public int getRunId() {
            return runId;
        }
        
        public int getPrecursorScanId() {
            return precursorScanId;
        }

        public int getEndScanNum() {
            return scan.getEndScanNum();
        }

        public String getFragmentationType() {
            return scan.getFragmentationType();
        }

        public int getMsLevel() {
            return scan.getMsLevel();
        }

        public BigDecimal getPrecursorMz() {
            return scan.getPrecursorMz();
        }

        public int getPrecursorScanNum() {
            return scan.getPrecursorScanNum();
        }

        public BigDecimal getRetentionTime() {
            return scan.getRetentionTime();
        }

        public int getStartScanNum() {
            return scan.getStartScanNum();
        }

        public List<String[]> getPeaksString() {
            throw new UnsupportedOperationException();
        }
        
        public List<Peak> getPeaks() {
            throw new UnsupportedOperationException();
        }
        
        public PeakStorageType getPeakStorageType() {
            throw new UnsupportedOperationException();
        }

        @Override
        public int getPeakCount() {
            return scan.getPeakCount();
        }

        @Override
        public DataConversionType getDataConversionType() {
            return scan.getDataConversionType();
        }

        @Override
        public int getId() {
            throw new UnsupportedOperationException("getId() not supported by MsScanSqlMapParam");
        }
        
    }
    

    //---------------------------------------------------------------------------------------
    /** 
     * Type handler for converting between 'F', 'T' from database to DataConversionType
     */
    public static class DataConversionTypeHandler implements TypeHandlerCallback {

        private static final String TRUE = "T";
        private static final String FALSE = "F";
        
        public Object getResult(ResultGetter getter) throws SQLException {
            return trueFalseToDataConversionType(getter.getString());
        }

        public void setParameter(ParameterSetter setter, Object parameter)
                throws SQLException {
            String type = dataConversionTypeToTrueFalse((DataConversionType)parameter);
            if (type == null)
                setter.setNull(java.sql.Types.CHAR);
            else
                setter.setString(type);
        }

        public Object valueOf(String s) {
            return trueFalseToDataConversionType(s);
        }
        
        private String dataConversionTypeToTrueFalse(DataConversionType type) {
            if (DataConversionType.CENTROID == type)                 return TRUE;
            else if (DataConversionType.NON_CENTROID == type)        return FALSE;
            else                                                     return null;
        }
        
        private DataConversionType trueFalseToDataConversionType(String val) {
            if (val == null)    
                return DataConversionType.UNKNOWN;
            if (TRUE.equalsIgnoreCase(val))
                return DataConversionType.CENTROID;
            else if (FALSE.equalsIgnoreCase(val))
                return DataConversionType.NON_CENTROID;
            else
                throw new IllegalArgumentException("Cannot convert "+val+" to DataConversionType");
        }
    }
    
    //---------------------------------------------------------------------------------------
    /** 
     * Type handler for converting between PeakStorageType to CHAR stored in the database
     */
    public static class PeakStorageTypeHandler implements TypeHandlerCallback {

        
        public Object getResult(ResultGetter getter) throws SQLException {
            return stringToPeakStorageType(getter.getString());
        }

        public void setParameter(ParameterSetter setter, Object parameter)
                throws SQLException {
            if (parameter == null || !(parameter instanceof PeakStorageType))
                setter.setNull(java.sql.Types.CHAR);
            else 
                setter.setString(((PeakStorageType)parameter).getCode());
        }

        public Object valueOf(String s) {
            return stringToPeakStorageType(s);
        }

        private PeakStorageType stringToPeakStorageType(String val) {
            PeakStorageType type = PeakStorageType.instance(val);
            if(type == null)
                throw new IllegalArgumentException("Cannot convert "+val+" to PeakStorageType");
            return type;
        }
    }
    
    /**
     * Convenience class for encapsulating data for a row in the msScanData table.
     */
    public static class MsScanDataSqlMapParam {
        private int scanId;
        private byte[] peakData;
        private PeakStorageType peakStorageType;
        
        public MsScanDataSqlMapParam(int scanId, MsScanIn scan, PeakStorageType storageType) throws IOException {
            this.scanId = scanId;
            
            if(storageType == PeakStorageType.DOUBLE_FLOAT)
                this.peakData = getBytesForPeakDataAsDoubleFloat(scan);
            else if(storageType == PeakStorageType.STRING)
                this.peakData = getBytesForPeakDataAsString(scan);
            
            this.peakStorageType = storageType;
        }
        
        public int getScanId() {
            return scanId;
        }
        
        public PeakStorageType getPeakStorageType() {
            return peakStorageType;
        }
        
        public byte[] getPeakData() {
            return peakData;
        }
        
        private byte[] getBytesForPeakDataAsString(MsScanIn scan) {
            List<String[]> peaksStr = scan.getPeaksString();
            PeakStringBuilder builder = new PeakStringBuilder();
            for(String[] peak: peaksStr) {
                builder.addPeak(peak[0], peak[1]);
            }
            return builder.getPeaksAsString().getBytes();
        }
        
        private byte[] getBytesForPeakDataAsDoubleFloat(MsScanIn scan) throws IOException {
            
            ByteArrayOutputStream bos = null;
            DataOutputStream dos = null;
            
            List<Peak> peaks = scan.getPeaks();
            try {
                bos = new ByteArrayOutputStream();
                dos = new DataOutputStream(bos);
                for(Peak peak: peaks) {
                    dos.writeDouble(peak.getMz());
                    dos.writeFloat(peak.getIntensity());
                }
                dos.flush();
            }
            finally {
                if(dos != null) try {dos.close();} catch(IOException e){}
                if(bos != null) try {bos.close();} catch(IOException e){}
            }
            byte [] data = bos.toByteArray();
            return data;
        }
    }
}
