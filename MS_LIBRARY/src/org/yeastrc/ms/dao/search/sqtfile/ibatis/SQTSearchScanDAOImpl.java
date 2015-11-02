/**
 * SQTSpectrumDataDAO.java
 * @author Vagisha Sharma
 * Jul 5, 2008
 * @version 1.0
 */
package org.yeastrc.ms.dao.search.sqtfile.ibatis;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.yeastrc.ms.dao.ibatis.BaseSqlMapDAO;
import org.yeastrc.ms.dao.search.sqtfile.SQTSearchScanDAO;
import org.yeastrc.ms.domain.search.sqtfile.SQTSearchScan;
import org.yeastrc.ms.domain.search.sqtfile.impl.SQTSearchScanBean;

import com.ibatis.sqlmap.client.SqlMapClient;

/**
 * 
 */
public class SQTSearchScanDAOImpl extends BaseSqlMapDAO implements SQTSearchScanDAO {

    public SQTSearchScanDAOImpl(SqlMapClient sqlMap) {
        super(sqlMap);
    }
    
    public SQTSearchScanBean load(int runSearchId, int scanId, int charge, BigDecimal observedMass) {
        Map<String, Object> map = new HashMap<String, Object>(8);
        map.put("runSearchId", runSearchId);
        map.put("scanId", scanId);
        map.put("charge", charge);
        map.put("observedMass", observedMass);
        return (SQTSearchScanBean) queryForObject("SqtSpectrum.select", map);
    }
    
    public void save(SQTSearchScan scanData) {
        save("SqtSpectrum.insert", scanData);
    }
    
    public void deleteForRunSearch(int runSearchId) {
        delete("SqtSpectrum.deleteForRunSearch", runSearchId);
    }

    @Override
    public void saveAll(List<SQTSearchScan> scanDataList) {
        if (scanDataList.size() == 0)
            return;
//        INSERT INTO SQTSpectrumData 
//        (runSearchID, 
//        scanID, 
//        charge, 
//        observedMass,
//        processTime, 
//        serverName,
//        totalIntensity,
//        observedMass,
//        lowestSp,
//        sequenceMatches) 
        StringBuilder values = new StringBuilder();
        for ( SQTSearchScan scan: scanDataList) {
            values.append(",(");
            values.append(scan.getRunSearchId() == 0 ? "NULL" : scan.getRunSearchId());
            values.append(",");
            values.append(scan.getScanId() == 0 ? "NULL" : scan.getScanId());
            values.append(",");
            values.append(scan.getCharge() == 0 ? "NULL" : scan.getCharge());
            values.append(",");
            values.append(scan.getObservedMass());
            values.append(",");
            values.append(scan.getProcessTime());
            values.append(",");
            values.append("\""+scan.getServerName()+"\"");
            values.append(",");
            values.append(scan.getTotalIntensity());
            values.append(",");
            values.append(scan.getLowestSp());
            values.append(",");
            values.append(scan.getSequenceMatches());
            values.append(")");
        }
        values.deleteCharAt(0);
        
        save("SqtSpectrum.insertAll", values.toString());
        
    }
    
    @Override
    public void disableKeys() throws SQLException {
        Connection conn = null;
        Statement stmt = null;
        try {
            conn = super.getConnection();
            String sql = "ALTER TABLE SQTSpectrumData DISABLE KEYS";
            stmt = conn.createStatement();
            stmt.executeUpdate(sql);
        }
        finally {
            try {if(conn != null) conn.close();}
            catch(SQLException e){}
            try {if(stmt != null) stmt.close();}
            catch(SQLException e){}
        }
    }

    @Override
    public void enableKeys() throws SQLException {
        Connection conn = null;
        Statement stmt = null;
        try {
            conn = super.getConnection();
            String sql = "ALTER TABLE SQTSpectrumData ENABLE KEYS";
            stmt = conn.createStatement();
            stmt.executeUpdate(sql);
        }
        finally {
            try {if(conn != null) conn.close();}
            catch(SQLException e){}
            try {if(stmt != null) stmt.close();}
            catch(SQLException e){}
        }
    }
}
