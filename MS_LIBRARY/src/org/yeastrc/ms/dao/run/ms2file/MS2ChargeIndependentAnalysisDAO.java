package org.yeastrc.ms.dao.run.ms2file;

import java.util.List;

import org.yeastrc.ms.domain.run.ms2file.MS2ChargeIndependentAnalysis;
import org.yeastrc.ms.domain.run.ms2file.MS2NameValuePair;

public interface MS2ChargeIndependentAnalysisDAO {

    public abstract List<MS2NameValuePair> loadAnalysisForScan(int scanId);

    public abstract void save(MS2NameValuePair analysis, int scanId);
    
    public abstract void saveAll(List<MS2ChargeIndependentAnalysis> analysisList);
    
    public abstract void deleteByScanId(int scanId);

}