package org.yeastrc.ms.dao.run.ms2file.ibatis;

import java.util.ArrayList;
import java.util.List;

import org.yeastrc.ms.dao.ibatis.BaseSqlMapDAO;
import org.yeastrc.ms.dao.run.MsRunDAO;
import org.yeastrc.ms.dao.run.ms2file.MS2HeaderDAO;
import org.yeastrc.ms.dao.run.ms2file.MS2RunDAO;
import org.yeastrc.ms.domain.run.MsRun;
import org.yeastrc.ms.domain.run.MsRunLocation;
import org.yeastrc.ms.domain.run.RunFileFormat;
import org.yeastrc.ms.domain.run.ms2file.MS2NameValuePair;
import org.yeastrc.ms.domain.run.ms2file.MS2Run;
import org.yeastrc.ms.domain.run.ms2file.MS2RunIn;

import com.ibatis.sqlmap.client.SqlMapClient;

public class MS2RunDAOImpl extends BaseSqlMapDAO implements MS2RunDAO {

    private MsRunDAO msRunDao;
    private MS2HeaderDAO ms2HeaderDao;
    
    public MS2RunDAOImpl(SqlMapClient sqlMap, MsRunDAO msRunDao, MS2HeaderDAO ms2headerDao) {
        super(sqlMap);
        this.msRunDao = msRunDao;
        this.ms2HeaderDao = ms2headerDao;
    }

    public RunFileFormat getRunFileFormat(int runId) throws Exception {
        return msRunDao.getRunFileFormat(runId);
    }

    /**
     * Saves the run along with MS2 file specific information
     */
    public int saveRun(MS2RunIn run, String serverDirectory) {

        // save the run and location
        int runId = msRunDao.saveRun(run, serverDirectory);
        try {
            for (MS2NameValuePair header: run.getHeaderList()) {
                ms2HeaderDao.save(header, runId);
            }
        }
        catch(RuntimeException e) {
            delete(runId);// this will delete anything that got saved with this runId;
            throw e;
        }
        return runId;
    }

    @Override
    public void saveRunLocation(String serverDirectory, int runId) {
        msRunDao.saveRunLocation(serverDirectory, runId);
    }
    
    public MS2Run loadRun(int runId) {
        // MsRun.select has a discriminator and will instantiate the
        // appropriate type of run object
        MsRun run = (MsRun)queryForObject("MsRun.select", runId);
        if(run instanceof MS2Run)
        	return (MS2Run)run;
        else
        	return null;
    }

    @Override
    public List<MS2Run> loadRuns(List<Integer> runIdList) {
        if (runIdList.size() == 0)
            return new ArrayList<MS2Run>(0);
        StringBuilder buf = new StringBuilder();
        for (Integer i: runIdList) {
            buf.append(","+i);
        }
        buf.deleteCharAt(0);
        return queryForList("MsRun.selectRuns", buf.toString());
    }
    
    @Override
    public List<MsRunLocation> loadLocationsForRun(int runId) {
        return msRunDao.loadLocationsForRun(runId);
    }

    @Override
    public int loadMatchingRunLocations(int runId, String serverDirectory) {
        return msRunDao.loadMatchingRunLocations(runId, serverDirectory);
    }
    
    @Override
    public List<Integer> loadRunIdsForFileName(String fileName) {
        return msRunDao.loadRunIdsForFileName(fileName);
    }
    
    public int loadRunIdForFileNameAndSha1Sum(String fileName, String sha1Sum) {
        return msRunDao.loadRunIdForFileNameAndSha1Sum(fileName, sha1Sum);
    }

    @Override
    public int loadRunIdForSearchAndFileName(int searchId, String runFileName) {
        return msRunDao.loadRunIdForSearchAndFileName(searchId, runFileName);
    }
    
    @Override
    public boolean isGeneratedByBullseye(int runId) {
        MS2Run run = loadRun(runId);
        if(run == null) // we did not find a MS2 run with this runId
        	return false;
        List<MS2NameValuePair> headers = run.getHeaderList();
        // get the value of the file header with name "FileGenerator"
        for(MS2NameValuePair header: headers) {
            if(header.getName().equalsIgnoreCase("FileGenerator")) {
                String headerValue = header.getValue();
                if(headerValue != null && headerValue.toLowerCase().startsWith("bullseye")) 
                    return true;
            }
            
        }
        return false;
    }
    
    public void delete(int runId) {
        msRunDao.delete(runId);
    }

    @Override
    public String loadFilenameForRun(int runId) {
        return msRunDao.loadFilenameForRun(runId);
    }

    @Override
    public Integer loadRunIdForExperimentAndFileName(int experimentId,
            String runFileName) {
        return msRunDao.loadRunIdForExperimentAndFileName(experimentId, runFileName);
    }

    @Override
    public double getMaxRetentionTimeForRun(int runId) {
        return msRunDao.getMaxRetentionTimeForRun(runId);
    }

    @Override
    public double getMaxRetentionTimeForRuns(List<Integer> runIds) {
        return msRunDao.getMaxRetentionTimeForRuns(runIds);
    }

    @Override
    public double getMinRetentionTimeForRun(int runId) {
        return msRunDao.getMinRetentionTimeForRun(runId);
    }

    @Override
    public double getMinRetentionTimeForRuns(List<Integer> runIds) {
        return msRunDao.getMinRetentionTimeForRuns(runIds);
    }
}
