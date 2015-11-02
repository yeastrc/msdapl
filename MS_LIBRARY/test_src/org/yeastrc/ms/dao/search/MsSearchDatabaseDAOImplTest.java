/**
 * MsSequenceDatabaseDAOImplTest.java
 * @author Vagisha Sharma
 * Jul 4, 2008
 * @version 1.0
 */
package org.yeastrc.ms.dao.search;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.yeastrc.ms.dao.BaseDAOTestCase;
import org.yeastrc.ms.domain.search.MsSearchDatabase;
import org.yeastrc.ms.domain.search.MsSearchDatabaseIn;
import org.yeastrc.ms.domain.search.impl.MsSearchDatabaseWrap;

/**
 * 
 */
public class MsSearchDatabaseDAOImplTest extends BaseDAOTestCase {

    protected void setUp() throws Exception {
        super.setUp();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void testSaveLoadDelete() {
        
        int searchId_1 = 1;
        int searchId_2 = 2;
        
        // try to select databases for the search ids; make sure no databases are found
        List<MsSearchDatabase> dbs_1 = seqDbDao.loadSearchDatabases(searchId_1);
        assertEquals(0, dbs_1.size());
        
        List<MsSearchDatabase> dbs_2 = seqDbDao.loadSearchDatabases(searchId_2);
        assertEquals(0, dbs_2.size());
        
        // create a sequence database and assign it to both the search ids
        MsSearchDatabaseIn db1 = makeSequenceDatabase("serverAddress_1", "serverPath_1");
        // assign this database to searchId_1; this will return the id from the msSequenceDatabaseDetails table
        int db1_id = seqDbDao.saveSearchDatabase(new MsSearchDatabaseWrap(db1, 293), searchId_1); // proteinDatabaseId = 293
        
        // we assign the same database to searchId_2.  Since no new entry should be created in 
        // msSequenceDatabaseDetails table, the returned id should be the same as the one above
        assertEquals(db1_id, seqDbDao.saveSearchDatabase(new MsSearchDatabaseWrap(db1, 293), searchId_2)); // proteinDatabaseId = 293
        
        // create another dababase with some null values
        MsSearchDatabaseIn db2 = makeSequenceDatabase("serverAddress_1", null);
        // assign the database to searchId_1; we should get a different id since a new entry will be
        // created in msSequenceDatabaseDetails
        int db2_id = seqDbDao.saveSearchDatabase(new MsSearchDatabaseWrap(db2, 293), searchId_1);
        assertNotSame(db1_id, db2_id);
        
        // load the databases associated with searchId_1 and check the returned objects
        List<MsSearchDatabase> searchId_1_dbs = seqDbDao.loadSearchDatabases(searchId_1);
        assertEquals(2, searchId_1_dbs.size());
        
        // sort the results by db id
        Collections.sort(searchId_1_dbs, new Comparator<MsSearchDatabase> () {
            public int compare(MsSearchDatabase o1, MsSearchDatabase o2) {
                return new Integer(o1.getId()).compareTo(new Integer(o2.getId()));
            }});
        checkDatabase(db1, searchId_1_dbs.get(0));
        checkDatabase(db2, searchId_1_dbs.get(1));
        
        // delete sequence database associations for searchId_1;
        seqDbDao.deleteSearchDatabases(searchId_1);
        searchId_1_dbs = seqDbDao.loadSearchDatabases(searchId_1);
        assertEquals(0, searchId_1_dbs.size());
        
        // we should still have one sequence db associated with searchId_2
        List<MsSearchDatabase> searchId_2_dbs = seqDbDao.loadSearchDatabases(searchId_2);
        assertEquals(1, searchId_2_dbs.size());
        
        // delete sequence database associations for searchId_2
        seqDbDao.deleteSearchDatabases(searchId_2);
        searchId_2_dbs = seqDbDao.loadSearchDatabases(searchId_2);
        assertEquals(0, searchId_2_dbs.size());
        
    }
    
    protected void checkDatabase(MsSearchDatabaseIn input, MsSearchDatabase output) {
        assertEquals(input.getServerAddress(), output.getServerAddress());
        assertEquals(input.getServerPath(), output.getServerPath());
    }
}
