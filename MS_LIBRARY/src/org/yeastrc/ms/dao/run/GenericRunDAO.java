/**
 * GenericRunDAO.java
 * @author Vagisha Sharma
 * Sep 7, 2008
 * @version 1.0
 */
package org.yeastrc.ms.dao.run;

import java.util.List;

import org.yeastrc.ms.domain.run.MsRunIn;
import org.yeastrc.ms.domain.run.MsRun;
import org.yeastrc.ms.domain.run.MsRunLocation;
import org.yeastrc.ms.domain.run.RunFileFormat;

/**
 * 
 */
public interface GenericRunDAO <I extends MsRunIn, O extends MsRun> {

    /**
     * Saves the given run in the database and returns the database id for the run.
     * Any enzyme information is saved.
     * The location of the run is saved as well.
     * @param run
     * @param runLocation
     * @return
     */
    public abstract int saveRun(I run, String serverDirectory);
    
    
    /**
     * Saves the original location of the run corresponding to the runId.
     * @param runLocation
     */
    public abstract void saveRunLocation(String serverDirectory, int runId);
    
    
    /**
     * Returns a run from the database with the given runId
     * @param runId
     * @return
     */
    public abstract O loadRun(int runId);
    
    /**
     * Returns the list of runs for the given runIds
     * The returned runs have any associated enzyme related information as well.
     * @param runIdList
     * @return
     */
    public abstract List<O> loadRuns(List<Integer> runIdList);
    
    /**
     * Returns a list of run IDs for runs in the database with the given file name
     * and sha1sum. 
     * @param fileName
     * @param sha1Sum
     * @return
     */
    public abstract int loadRunIdForFileNameAndSha1Sum(String fileName, String sha1Sum);
    
    
    /**
     * Returns a list of runIDs for runs in the database with the given filename.
     * @param fileName
     * @return
     */
    public abstract List<Integer> loadRunIdsForFileName(String fileName);
    
    
    /**
     * Returns the runID for a run with the given file name that is associated with
     * the given experimentId.
     * @param experimentId
     * @param runFileName
     * @return
     */
    public abstract Integer loadRunIdForExperimentAndFileName(int experimentId, String runFileName);
    
    
    /**
     * Returns the runID for a run with the given file name that was searched in a
     * search group represented by the given searchId.
     * @param searchId
     * @param runFileName
     * @return
     */
    public abstract int loadRunIdForSearchAndFileName(int searchId, String runFileName);
    
    /**
     * Returns a list of locations for the given run
     * @param runId
     * @return
     */
    public abstract List<MsRunLocation> loadLocationsForRun(int runId);
    
    /**
     * Returns the number of locations with the given runId and serverDirectory.
     * @param runId
     * @param serverDirectory
     * @return
     */
    public abstract int loadMatchingRunLocations(int runId, String serverDirectory);
    
    /**
     * Deletes the run with the given id. Enzyme information and scans are also deleted
     * Any location entries for this run are also deleted.
     * @param runId
     */
    public abstract void delete(int runId);
    
    
    /**
     * Returns the original file format for the run.
     * @param runId
     * @return
     * @throws Exception if a run with the given id is not found in the database
     */
    public abstract RunFileFormat getRunFileFormat(int runId) throws Exception;
    
    /**
     * Returns the filename for the run
     * @param runId
     * @return
     */
    public abstract String loadFilenameForRun(int runId);
    
    /**
     * Returns the minimum retention time for a scan in the given run
     * @param runId
     * @return
     */
    public double getMinRetentionTimeForRun(int runId);
    
    /**
     * Returns the maximum retention time for a scan in the given run
     * @param runId
     * @return
     */
    public double getMaxRetentionTimeForRun(int runId);
    
    /**
     * Returns the minimum retentionTime for a scan in the given runs
     * @param runIds
     * @return
     */
    public double getMinRetentionTimeForRuns(List<Integer> runIds);
    
    /**
     * Returns the maximum retentionTime for a scan in the given runs.
     * @param runIds
     * @return
     */
    public double getMaxRetentionTimeForRuns(List<Integer> runIds);
    
}
