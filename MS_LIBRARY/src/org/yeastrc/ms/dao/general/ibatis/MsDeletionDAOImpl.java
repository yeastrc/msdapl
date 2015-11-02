    package org.yeastrc.ms.dao.general.ibatis;

import org.yeastrc.ms.dao.general.MsDeletionDAO;
import org.yeastrc.ms.dao.ibatis.BaseSqlMapDAO;

import com.ibatis.sqlmap.client.SqlMapClient;

public class MsDeletionDAOImpl extends BaseSqlMapDAO implements MsDeletionDAO {

    public MsDeletionDAOImpl(SqlMapClient sqlMap) {
        super(sqlMap);
    }

    @Override
    public void deleteExperiment(int experimentId) {
        
        // delete charge independent analysis
        delete("Deletion.deleteChargeIAnalysis", experimentId);
        // delete charge dependent analysis
        delete("Deletion.deleteChargeDAnalysis", experimentId);
        // delete scan charges
        delete("Deletion.deleteScanCharge", experimentId);
        // delete scan data
        delete("Deletion.deleteScanData", experimentId);
        // delete scans
        delete("Deletion.deleteScan", experimentId);
        // delete run enzymes
        delete("Deletion.deleteRunEnzyme", experimentId);
        // delete MS2 file headers
        delete("Deletion.deleteMS2Header", experimentId);
        // delete the runs
        delete("Deletion.deleteRun", experimentId);
        // delete the experiment
        delete("Deletion.deleteExperiment", experimentId);
    }

    @Override
    public void deleteSearch(int searchId) {
        
        // delete search protein matches
        delete("Deletion.deleteProteinMatch", searchId);
        // delete SQT search result scores
        delete("Deletion.deleteSQTResult", searchId);
        // delete search results
        delete("Deletion.deleteMsSearchResult", searchId);
        // delete scan data (SQTSpectrumData)
        delete("Deletion.deleteSQTSpectrum", searchId);
        // delete SQT headers
        delete("Deletion.deleteSQTHeader", searchId);
        // delete search enzymes
        delete("Deletion.deleteSearchEnzyme", searchId);
        // delete search database(s)
        delete("Deletion.deleteSearchDatabase", searchId);
        // delete static mods for search
        delete("Deletion.deleteSearchStaticMod", searchId);
        // delete dynamic modifications for results
        delete("Deletion.deleteResultMod", searchId);
        // delete dynamic modifications for search
        delete("Deletion.deleteSearchDynaMod", searchId);
        // finally, delete the search
        delete("Deletion.deleteSearch", searchId);
    }
}
