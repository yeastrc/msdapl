/**
 * Ms2FileChargeIndependentAnalysisDAOImpl.java
 * @author Vagisha Sharma
 * Jun 18, 2008
 * @version 1.0
 */
package org.yeastrc.ms.dao.run.ms2file.ibatis;

import java.util.List;

import org.yeastrc.ms.dao.ibatis.BaseSqlMapDAO;
import org.yeastrc.ms.dao.run.ms2file.MS2ChargeIndependentAnalysisDAO;
import org.yeastrc.ms.domain.run.ms2file.MS2ChargeIndependentAnalysis;
import org.yeastrc.ms.domain.run.ms2file.MS2NameValuePair;
import org.yeastrc.ms.domain.run.ms2file.impl.MS2ChargeIndependentAnalysisDb;

import com.ibatis.sqlmap.client.SqlMapClient;

/**
 * 
 */
public class MS2ChargeIndependentAnalysisDAOImpl extends BaseSqlMapDAO
        implements MS2ChargeIndependentAnalysisDAO {

    public MS2ChargeIndependentAnalysisDAOImpl(SqlMapClient sqlMap) {
        super(sqlMap);
    }

    public List<MS2NameValuePair> loadAnalysisForScan(int scanId) {
        return queryForList("MS2ChgIAnalysis.selectAnalysisForScan", scanId);
    }

    public void save(MS2NameValuePair analysis, int scanId) {
        MS2ChargeIndependentAnalysisDb analysisDb = new MS2ChargeIndependentAnalysisDb(analysis, scanId);
        save("MS2ChgIAnalysis.insert", analysisDb);
    }

    @Override
    public void saveAll(List<MS2ChargeIndependentAnalysis> analysisList) {
        if (analysisList.size() == 0)
            return;
        StringBuilder values = new StringBuilder();
        for (MS2ChargeIndependentAnalysis analysis: analysisList) {
            values.append("(");
            values.append(analysis.getScanId());
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
        
        save("MS2ChgIAnalysis.insertAll", values.toString());
    }
    
    public void deleteByScanId(int scanId) {
        delete("MS2ChgIAnalysis.deleteByScanId", scanId);
    }
}
