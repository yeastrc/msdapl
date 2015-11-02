/**
 * GenericScanDAO.java
 * @author Vagisha Sharma
 * Sep 7, 2008
 * @version 1.0
 */
package org.yeastrc.ms.dao.run;

import java.math.BigDecimal;
import java.util.List;

import org.yeastrc.ms.domain.run.MsScanIn;
import org.yeastrc.ms.domain.run.MsScan;

/**
 * 
 */
public interface GenericScanDAO <I extends MsScanIn, O extends MsScan> {

    /**
     * Saves the given scan in the database.
     * @param scan
     * @return
     */
    public abstract int save(I scan, int runId, int precursorScanId);
    
    /**
     * Saves the given scan in the database.
     * This method should be used when there is no precursorScanId for the scan
     * e.g. a MS1 scan
     * @param scan
     * @return
     */
    public abstract int save(I scan, int runId);

    public abstract <T extends MsScanIn> List<Integer> save(List<T> scans, int runId);
    
    /**
     * Returns a scan with the given scan ID from the database.
     * @param scanId
     * @return
     */
    public abstract O load(int scanId);
    
    /**
     * Returns a scan with the givenID.  Peak data for the scan is not loaded.
     * @param scanId
     * @return
     */
    public abstract O loadScanLite(int scanId);
    
    /**
     * Returns the scan number for the given scanID
     * @param scanId
     * @return
     */
    public abstract int loadScanNumber(int scanId);
    
    
    /**
     * Returns a list of scan ID's for the given run
     * @param runId
     * @return
     */
    public abstract List<Integer> loadScanIdsForRun(int runId);
    
    /**
     * Returns a list of scan ID's for the given run and level (MS level of 1,2,...)
     * @param runId
     * @return
     */
    public List<Integer> loadScanIdsForRunAndLevel(int runId, int level);

    
    
    /**
     * Returns the number of scans for the given runId;
     * @param runId
     * @return
     */
    public abstract int numScans(int runId);
    
    /**
     * Returns the number of scans of the given level for the given runId;
     * @param runId
     * @return
     */
    public abstract int numScans(int runId, int level);
    
    /**
     * Returns the number of scans of the given experimentId;
     * searching msScan records where the level is not one and the preMZ is not NULL
     * @param experimentId
     * @return
     */
    public abstract int numScansForExperimentIdScanLevelNotOnePreMZNotNULL(int experimentId);

    /**
     * Returns the number of scans of the given experimentId;
     * searching msScan records where the level is not one
     * @param experimentId
     * @return
     */
    public abstract int numScansForExperimentIdScanLevelNotOne(int experimentId);

    
    
    /**
     * Returns the max value of preMZ in msScans for the given experimentId
     * searching msScan records where the level is not one and the preMZ is not NULL
     * @param experimentId
     * @return
     */
    public abstract BigDecimal getMaxPreMZForExperimentIdScanLevelNotOnePreMZNotNULL(int experimentId);
    
    /**
     * Returns the min value of preMZ in msScans for the given experimentId
     * searching msScan records where the level is not one and the preMZ is not NULL
     * @param experimentId
     * @return
     */
    public abstract BigDecimal getMinPreMZForExperimentIdScanLevelNotOnePreMZNotNULL(int experimentId);
    
    /**
     * Returns the list of values of preMZ in msScans for the given experimentId
     * searching msScan records where the level is not one and the preMZ is not NULL
     * @param experimentId
     * @return
     */
    public abstract List<BigDecimal> getPreMZForExperimentIdScanLevelNotOnePreMZNotNULL(int experimentId);

    
    /**
     * Returns the array of values of preMZ in msScans for the given experimentId
     * searching msScan records where the level is not one and the preMZ is not NULL
     * @param experimentId
     * @return
     */    
    public double[] getPreMZArrayForExperimentIdScanLevelNotOnePreMZNotNULL(int experimentId);
    
    
    
    /**
     * Returns the array of values of peakCount in msScans for the given experimentId
     * searching msScan records where the level is not one and the preMZ is not NULL
     * @param experimentId
     * @return
     */    
    public int[] getPeakCountArrayForExperimentIdScanLevelNotOne(int experimentId);
    
    
    
    public abstract void delete(int scanId);
    
    /**
     * Returns the database id for the scan with the given scan number and runId
     * @param scanNum
     * @param runId
     * @return
     */
    public abstract int loadScanIdForScanNumRun(int scanNum, int runId);
    
    /**
     * Returns a list of MS/MS scanIds that have the given MS1 as their parent.
     * @param ms1ScanId
     * @return
     */
    public abstract List<Integer> loadMS2ScanIdsForMS1Scan(int ms1ScanId);
}
