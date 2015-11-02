package org.yeastrc.ms.dao.general;

import java.util.List;

import org.yeastrc.ms.domain.general.MsEnzyme;
import org.yeastrc.ms.domain.general.MsEnzymeIn;
import org.yeastrc.ms.domain.general.MsEnzyme.Sense;

public interface MsEnzymeDAO {

    
    public enum EnzymeProperties {NAME, SENSE, CUT, NOTCUT};
    
    
    public MsEnzyme loadEnzyme(int enzymeId);
    
    public List<MsEnzyme> loadEnzymes(String name);
    
    public List<MsEnzyme> loadEnzymes(String name, Sense sense, String cut, String nocut);
    
    public List<MsEnzyme> loadEnzymesForRun(int runId);
    
    public List<MsEnzyme> loadEnzymesForSearch(int searchId);
    
    public abstract void deleteEnzymeById(int enzymeId);
    
    /**
     * Saves the enzyme if it does not already exist in the database. 
     * All enzyme properties (name, sense, cut, nocut) are used to to look for a matching enzyme
     * before saving
     * @param enzyme
     * @return
     */
    public int saveEnzyme(MsEnzymeIn enzyme);
    
    /**
     * Saves the enzyme if it does not already exist in the database.
     * @param enzyme
     * @param properties -- list of properties used to look for a matching enzyme before saving
     * @return
     */
    public int saveEnzyme(MsEnzymeIn enzyme, List<EnzymeProperties> properties);
    
    
    /**
     * Saves an entry in msRunEnzyme linking the enzyme with the runId.
     * If enzyme does not exist in the database (msDigestionEnzyme) it is saved
     * All enzyme properties are used to look for a matching enzyme in the database
     * If multiple matching enzymes are found the run is linked to the first enzyme
     * @param enzyme
     * @param runId
     * @return
     */
    public int saveEnzymeforRun(MsEnzymeIn enzyme, int runId);
    
    /**
     * Saves an entry in msRunEnzyme linking the enzyme with the runId.
     * If enzyme does not exist in the database (msDigestionEnzyme) it is saved
     * The given enzyme properties are used to look for a matching enzyme in the database
     * If multiple matching enzymes are found the run is linked to the first enzyme
     * @param enzyme
     * @param runId
     * @param properties
     * @return
     */
    public int saveEnzymeforRun(MsEnzymeIn enzyme, int runId, List<EnzymeProperties> properties);
    
    
    public void saveEnzymeForRun(int enzymeId, int runId);
    
    /**
     * Deletes the enzyme to run association from the msRunEnzyme table. The actual enzyme is NOT deleted.
     * @param runId
     */
    public void deleteEnzymesForRun(int runId);
    
    /**
     * Deletes the enzyme to run association from the msRunEnzyme table for all the given enzymes.
     * The actual enzymes are NOT deleted.
     * @param runIds
     */
    public void deleteEnzymesForRuns(List<Integer> runIds);
    
    
    
    /**
     * Saves an entry in msSearchEnzyme linking the enzyme with the searchId.
     * If enzyme does not exist in the database (msDigestionEnzyme) it is saved
     * All enzyme properties are used to look for a matching enzyme in the database
     * If multiple matching enzymes are found the search is linked to the first enzyme
     * @param enzyme
     * @param searchId
     * @return
     */
    public int saveEnzymeforSearch(MsEnzymeIn enzyme, int searchId);
    
    /**
     * Saves an entry in msSearchEnzyme linking the enzyme with the searchId.
     * If enzyme does not exist in the database (msDigestionEnzyme) it is saved
     * The given enzyme properties are used to look for a matching enzyme in the database
     * If multiple matching enzymes are found the search is linked to the first enzyme
     * @param enzyme
     * @param searchId
     * @param properties
     * @return
     */
    public int saveEnzymeforSearch(MsEnzymeIn enzyme, int searchId, List<EnzymeProperties> properties);
    
    public void saveEnzymeForSearch(int enzymeId, int searchId);
    
    /**
     * Deletes the enzyme to search association from the msSearchEnzyme table. The actual enzyme is NOT deleted.
     * @param searchId
     */
    public void deleteEnzymesForSearch(int searchId);
    
    /**
     * Deletes the enzyme to search association from the msSearchEnzyme table for all the given enzymes.
     * The actual enzymes are NOT deleted.
     * @param searchIds
     */
    public void deleteEnzymesForSearches(List<Integer> searchIds);
}
