package org.yeastrc.ms.dao.run.ms2file;

import java.util.List;

import org.yeastrc.ms.domain.run.ms2file.MS2ScanCharge;

public interface MS2ScanChargeDAO {

    
    public abstract List<Integer> loadScanChargeIdsForScan(int scanId);
    
    public abstract List<MS2ScanCharge> loadScanChargesForScan(int scanId);
    
    public abstract List<MS2ScanCharge> loadScanChargesForScanAndCharge(int scanId, int charge);
    
    
    /**
     * Saves the given MS2FileScanCharge along with associated charge dependent analyses
     * @param scanCharge
     * @return database id of the saved MS2FileScanCharge
     */
    public abstract int save(MS2ScanCharge scanCharge, int scanId);

    public abstract int saveScanChargeOnly(MS2ScanCharge scanCharge, int scanId);
    
    /**
     * Deletes all entries associated with the given scanId. Related charge dependent
     * analyses are deleted as well. 
     * @param scanId
     */
    public abstract void deleteByScanId(int scanId);
    
}