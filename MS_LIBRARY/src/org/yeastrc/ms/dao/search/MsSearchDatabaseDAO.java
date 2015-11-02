package org.yeastrc.ms.dao.search;

import java.util.List;

import org.yeastrc.ms.domain.search.MsSearchDatabase;

public interface MsSearchDatabaseDAO {

    public abstract List<MsSearchDatabase> loadSearchDatabases(int searchId);
    

    public abstract void deleteSearchDatabases(int searchId);

    /**
     * Saves and entry for the given database, if one does not already exist.
     * @param database
     * @return
     */
    public abstract int saveDatabase(MsSearchDatabase database);
    
    /**
     * Links the search (represented by the searchId) with the given sequence database. 
     * If the given sequence database does not already exist in the 
     * msSequenceDatabaseDetails table, it is saved first.
     * @param database
     * @param searchId
     * @return id (from msSequenceDatabaseDetail) of the database that was linked to the searchId
     */
    public abstract int saveSearchDatabase(MsSearchDatabase database, int searchId);

    /**
     * Links the search (represented by the searchId) with the given sequence database id. 
     */
    public abstract void linkDatabaseAndSearch(int databaseId, int searchId);
    
    /**
     * Returns the stored nrseq database ID for a fasta database with the given filepath.
     * @param serverPath
     * @return
     */
    public abstract int getSequenceDatabaseId(String serverPath);
    
    /**
     * Returns the database Ids of all entries matching the given database
     * @param database
     * @return
     */
    public abstract List<Integer> loadMatchingDatabaseIds(MsSearchDatabase database);
    
    /**
     * Returns a list of search databases that contain the given query string in the
     * file name.
     * @param matchString
     * @return
     */
    public abstract List<MsSearchDatabase> findSearchDatabases(String matchString);
    
}