/**
 * Ms2FileChargeDependentAnalysisDAOImpl.java
 * @author Vagisha Sharma
 * Jun 18, 2008
 * @version 1.0
 */
package org.yeastrc.ms.dao.run.ms2file.ibatis;

import java.util.List;

import org.yeastrc.ms.dao.ibatis.BaseSqlMapDAO;
import org.yeastrc.ms.dao.run.ms2file.MS2ChargeDependentAnalysisDAO;
import org.yeastrc.ms.domain.run.ms2file.MS2ChargeDependentAnalysis;
import org.yeastrc.ms.domain.run.ms2file.MS2NameValuePair;
import org.yeastrc.ms.domain.run.ms2file.impl.MS2ChargeDependentAnalysisDb;

import com.ibatis.sqlmap.client.SqlMapClient;

/**
 * 
 */
public class MS2ChargeDependentAnalysisDAOImpl extends BaseSqlMapDAO
        implements MS2ChargeDependentAnalysisDAO {

    public MS2ChargeDependentAnalysisDAOImpl(SqlMapClient sqlMap) {
        super(sqlMap);
    }

    public List<MS2NameValuePair> loadAnalysisForScanCharge(int scanChargeId) {
        return queryForList("MS2ChgDAnalysis.selectAnalysisForCharge", scanChargeId);
    }

    public void save(MS2NameValuePair analysis, int scanChargeId) {
        MS2ChargeDependentAnalysisDb analysisDb = new MS2ChargeDependentAnalysisDb(analysis, scanChargeId);
        save("MS2ChgDAnalysis.insert", analysisDb);
    }

    @Override
    public void saveAll(List<MS2ChargeDependentAnalysis> analysisList) {
        if (analysisList.size() == 0)
            return;
        
        // TODO Change this to use the <iterate> tag in the ibatis sqlmap
        StringBuilder values = new StringBuilder();
        for (MS2ChargeDependentAnalysis analysis: analysisList) {
            values.append("(");
            values.append(analysis.getScanChargeId());
            values.append(",");
            String name = analysis.getName();
            if (name != null)   values.append("\"");
            values.append(name);
            if (name != null)   values.append("\"");
            values.append(",");
            String val = analysis.getValue();
            if (val != null)   values.append("\"");
            values.append(val);
            if (val != null)   values.append("\"");
            values.append("),");
        }
        values.deleteCharAt(values.length() - 1);
        
        save("MS2ChgDAnalysis.insertAll", values.toString());
    }
    
    public void deleteByScanChargeId(int scanChargeId) {
        delete("MS2ChgDAnalysis.deleteByScanChargeId", scanChargeId);
    }
}
