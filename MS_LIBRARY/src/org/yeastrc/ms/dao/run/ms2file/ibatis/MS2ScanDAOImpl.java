/**
 * MS2ScanDAOImpl.java
 * @author Vagisha Sharma
 * Jul 5, 2008
 * @version 1.0
 */
package org.yeastrc.ms.dao.run.ms2file.ibatis;

import java.math.BigDecimal;
import java.util.List;

import org.yeastrc.ms.dao.ibatis.BaseSqlMapDAO;
import org.yeastrc.ms.dao.run.MsScanDAO;
import org.yeastrc.ms.dao.run.ms2file.MS2ChargeIndependentAnalysisDAO;
import org.yeastrc.ms.dao.run.ms2file.MS2ScanChargeDAO;
import org.yeastrc.ms.dao.run.ms2file.MS2ScanDAO;
import org.yeastrc.ms.domain.run.MsScanIn;
import org.yeastrc.ms.domain.run.ms2file.MS2NameValuePair;
import org.yeastrc.ms.domain.run.ms2file.MS2Scan;
import org.yeastrc.ms.domain.run.ms2file.MS2ScanCharge;
import org.yeastrc.ms.domain.run.ms2file.MS2ScanIn;

import com.ibatis.sqlmap.client.SqlMapClient;

/**
 * 
 */
public class MS2ScanDAOImpl extends BaseSqlMapDAO implements MS2ScanDAO {

    private MsScanDAO msScanDao;
    private MS2ChargeIndependentAnalysisDAO iAnalDao;
    private MS2ScanChargeDAO chargeDao;
    
    public MS2ScanDAOImpl(SqlMapClient sqlMap, MsScanDAO msScanDAO,
            MS2ChargeIndependentAnalysisDAO iAnalDao, MS2ScanChargeDAO chargeDao) {
        super(sqlMap);
        this.msScanDao = msScanDAO;
        this.iAnalDao = iAnalDao;
        this.chargeDao = chargeDao;
    }

    /**
     * This will return a MS2FileScan object. NO check is made to determine if 
     * the run this scan belongs to is of type MS2
     */
    public MS2Scan load(int scanId) {
        return (MS2Scan) queryForObject("MS2Scan.select", scanId);
    }
    
    @Override
    public MS2Scan loadScanLite(int scanId) {
        return (MS2Scan) queryForObject("MS2Scan.selectScanNoData", scanId);
    }
    
    @Override
    public int loadScanNumber(int scanId) {
        return msScanDao.loadScanNumber(scanId);
    }
    
    @Override
    public boolean isGeneratedByBullseye(int scanId) {
        // get the value of the MS2 / CMS2 file header with name "FileGenerator"
        String headerValue = (String) queryForObject("MS2Scan.selectFileGeneratorHeader", scanId);
        if(headerValue != null && headerValue.toLowerCase().startsWith("bullseye")) 
            return true;
        else
            return false;
    }
    
    /**
     * Returns a list of scan ids for the given run.
     */
    public List<Integer> loadScanIdsForRun(int runId) {
        
        // TODO: should we check if the run for the given id is a MS2 run?
        return msScanDao.loadScanIdsForRun(runId);
    }
    

	@Override
	public List<Integer> loadScanIdsForRunAndLevel(int runId, int level) {

		return msScanDao.loadScanIdsForRunAndLevel( runId, level );
	}

	@Override
    public List<Integer> loadMS2ScanIdsForMS1Scan(int ms1ScanId) {
        return msScanDao.loadMS2ScanIdsForMS1Scan(ms1ScanId);
    }
    
    @Override
    public int numScans(int runId) {
        return msScanDao.numScans(runId);
    }
    
    @Override
    public int numScans(int runId, int level) {
        return msScanDao.numScans(runId, level);
    }
    

	@Override
	public int numScansForExperimentIdScanLevelNotOnePreMZNotNULL(int experimentId) {
		return msScanDao.numScansForExperimentIdScanLevelNotOnePreMZNotNULL(experimentId);
	}

    
    public int loadScanIdForScanNumRun(int scanNum, int runId) {
        return msScanDao.loadScanIdForScanNumRun(scanNum, runId);
    }
    

	@Override
	public BigDecimal getMaxPreMZForExperimentIdScanLevelNotOnePreMZNotNULL(int experimentId) {
		return msScanDao.getMaxPreMZForExperimentIdScanLevelNotOnePreMZNotNULL(experimentId);
	}

	@Override
	public BigDecimal getMinPreMZForExperimentIdScanLevelNotOnePreMZNotNULL(int experimentId) {
		return msScanDao.getMinPreMZForExperimentIdScanLevelNotOnePreMZNotNULL(experimentId);
	}

	@Override
	public List<BigDecimal> getPreMZForExperimentIdScanLevelNotOnePreMZNotNULL(int experimentId) {
		return msScanDao.getPreMZForExperimentIdScanLevelNotOnePreMZNotNULL(experimentId);
	}


	@Override
	public double[] getPreMZArrayForExperimentIdScanLevelNotOnePreMZNotNULL(int experimentId) {
		return msScanDao.getPreMZArrayForExperimentIdScanLevelNotOnePreMZNotNULL(experimentId);
	}

	

	@Override
	public int[] getPeakCountArrayForExperimentIdScanLevelNotOne(int experimentId) {
		return msScanDao.getPeakCountArrayForExperimentIdScanLevelNotOne(experimentId);
	}

    
    /**
     * Saves the scan along with any MS2 file format specific data.
     */
    public int save(MS2ScanIn scan, int runId, int precursorScanId) {
        
        // save the parent scan first
        int scanId = msScanDao.save(scan, runId, precursorScanId);
        
        // save the charge independent analysis
        for (MS2NameValuePair iAnalysis: scan.getChargeIndependentAnalysisList()) {
            iAnalDao.save(iAnalysis, scanId);
        }
        
        // save the charge state
        for (MS2ScanCharge charge: scan.getScanChargeList()) {
            chargeDao.save(charge, scanId);
        }
        
        return scanId;
    }
    
    @Override
    public <T extends MsScanIn> List<Integer> save(List<T> scans, int runId) {
        return msScanDao.save(scans, runId);
    }
    
    public int save(MS2ScanIn scan, int runId) {
        return save(scan, runId, 0);
    }
    
    /**
     * Deletes the scan along with any MS2 file format specific information
     */
    public void delete(int scanId) {
        msScanDao.delete(scanId);
    }

	@Override
	public int numScansForExperimentIdScanLevelNotOne(int experimentId) {
		// TODO Auto-generated method stub
		return 0;
	}
}
