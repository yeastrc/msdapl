/**
 * MsScanChargeDAO.java
 * @author Vagisha Sharma
 * Jun 17, 2008
 * @version 1.0
 */
package org.yeastrc.ms.dao.run.ms2file.ibatis;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.yeastrc.ms.dao.ibatis.BaseSqlMapDAO;
import org.yeastrc.ms.dao.run.ms2file.MS2ChargeDependentAnalysisDAO;
import org.yeastrc.ms.dao.run.ms2file.MS2ScanChargeDAO;
import org.yeastrc.ms.domain.run.ms2file.MS2NameValuePair;
import org.yeastrc.ms.domain.run.ms2file.MS2ScanCharge;

import com.ibatis.sqlmap.client.SqlMapClient;

public class MS2ScanChargeDAOImpl extends BaseSqlMapDAO implements MS2ScanChargeDAO {

    private MS2ChargeDependentAnalysisDAO dAnalysisDao;
    
    public MS2ScanChargeDAOImpl(SqlMapClient sqlMap, MS2ChargeDependentAnalysisDAO dAnalysisDao) {
        super(sqlMap);
        this.dAnalysisDao = dAnalysisDao;
    }

    public List<Integer> loadScanChargeIdsForScan(int scanId) {
        return queryForList("MS2ScanCharge.selectIdsForScan", scanId);
    }
    
    public List<MS2ScanCharge> loadScanChargesForScan(int scanId) {
        return queryForList("MS2ScanCharge.selectForScan", scanId);
    }
    
    public List<MS2ScanCharge> loadScanChargesForScanAndCharge(int scanId, int charge) {
        Map<String, Integer> map = new HashMap<String, Integer>(2);
        map.put("scanId", scanId);
        map.put("charge", charge);
        return queryForList("MS2ScanCharge.selectForScanAndCharge", map);
    }
    
    public int save(MS2ScanCharge scanCharge, int scanId) {
        
        int id = saveScanChargeOnly(scanCharge, scanId);
        
        // save any charge dependent anaysis with the scan charge object
        for (MS2NameValuePair dAnalysis: scanCharge.getChargeDependentAnalysisList()) {
            dAnalysisDao.save(dAnalysis, id);
        }
        return id;
    }

    public int saveScanChargeOnly(MS2ScanCharge scanCharge, int scanId) {
        MS2ScanChargeWrap scanChargeDb = new MS2ScanChargeWrap(scanCharge, scanId);
        return saveAndReturnId("MS2ScanCharge.insert", scanChargeDb);
    }

    public void deleteByScanId(int scanId) {
        // delete the scan charge entries for the scanId
        delete("MS2ScanCharge.deleteByScanId", scanId);
    }
}
