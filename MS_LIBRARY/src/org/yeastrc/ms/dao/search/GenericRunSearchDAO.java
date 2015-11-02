/**
 * MsRunSearchDAO.java
 * @author Vagisha Sharma
 * Aug 18, 2008
 * @version 1.0
 */
package org.yeastrc.ms.dao.search;

import java.util.List;

import org.yeastrc.ms.domain.search.MsRunSearch;
import org.yeastrc.ms.domain.search.Program;

/**
 * 
 */
public interface GenericRunSearchDAO <O extends MsRunSearch> {

    public abstract O loadRunSearch(int runSearchId);
    
    /**
     * Returns the database ids of individual run searches in a search group.
     * @param searchId
     * @return
     */
    public abstract List<Integer> loadRunSearchIdsForSearch(int searchId);
    
    /**
     * Returns the database ids of all searches on a run with the given 
     * database id. 
     * @param runId
     * @return
     */
    public abstract List<Integer> loadRunSearchIdsForRun(int runId);
    
    /**
     * Returns the database id of a search on the given run, and belonging 
     * to the given search group.
     * @param runId
     * @param searchId
     * @return
     */
    public abstract int loadIdForRunAndSearch(int runId, int searchId);
    
    
    /**
     * Returns the database id of a search on the given filename and belonging 
     * to the given search group.
     * @param searchId
     * @param filename
     * @return
     */
    public abstract int loadIdForSearchAndFileName(int searchId, String filename);
    
    
    /**
     * Returns the base name of the original file for this run search
     * @param runSearchId
     * @return
     */
    public abstract String loadFilenameForRunSearch(int runSearchId);
    
    
    /**
     * Returns the program used for the search
     * @param runSearchId
     * @return
     */
    public abstract Program loadSearchProgramForRunSearch(int runSearchId);
    
    
    /**
     * Saves the search (for a single run) in the database.
     * @param search
     * @return database id of the search
     */
    public abstract int saveRunSearch(O search);

    /**
     * Deletes the search (for a single run).
     * @param runSearchId
     */
    public abstract void deleteRunSearch(int runSearchId);
    
    /**
     * Returns the number of results for this run search
     * @param runSearchId
     * @return
     */
    public abstract int numResults(int runSearchId);
}
