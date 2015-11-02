/**
 * Ms2FileChargeDependentAnalysisDAO.java
 * @author Vagisha Sharma
 * Jun 18, 2008
 * @version 1.0
 */
package org.yeastrc.ms.dao.run.ms2file;

import java.util.List;

import org.yeastrc.ms.domain.run.ms2file.MS2ChargeDependentAnalysis;
import org.yeastrc.ms.domain.run.ms2file.MS2NameValuePair;

/**
 * 
 */
public interface MS2ChargeDependentAnalysisDAO {

    public abstract void save(MS2NameValuePair analysis, int scanChargeId);
    
    public abstract void saveAll(List<MS2ChargeDependentAnalysis> analysisList);

    public abstract List<MS2NameValuePair> loadAnalysisForScanCharge(int scanChargeId);
    
    public abstract void deleteByScanChargeId(int scanChargeId);
}
