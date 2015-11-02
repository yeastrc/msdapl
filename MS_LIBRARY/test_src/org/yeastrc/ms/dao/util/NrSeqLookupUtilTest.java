package org.yeastrc.ms.dao.util;

import junit.framework.TestCase;

import org.yeastrc.nrseq.dao.NrSeqLookupException;
import org.yeastrc.nrseq.dao.NrSeqLookupUtil;
import org.yeastrc.nrseq.domain.NrDbProtein;

public class NrSeqLookupUtilTest extends TestCase {

    protected void setUp() throws Exception {
        super.setUp();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void testGetProteinId() {
        String databaseName = "database";
        String accession = "accession_string_1";
        
        assertEquals(1, NrSeqLookupUtil.getDbProteinId(databaseName, accession));
        NrDbProtein dbProt = NrSeqLookupUtil.getDbProtein(1);
        assertEquals(25, dbProt.getProteinId());
        assertEquals(1, dbProt.getDatabaseId());
        assertEquals("accession_string_1", dbProt.getAccessionString());
        assertNull(dbProt.getDescription());
        
        databaseName = "dummy";
        int id = NrSeqLookupUtil.getDbProteinId(databaseName, accession);
        assertEquals(0, id);
        
        
        databaseName = "database2";
        id = NrSeqLookupUtil.getDbProteinId(databaseName, accession);
        assertEquals(0, id);
        
        accession = "accession_string_4";
        assertEquals(2, NrSeqLookupUtil.getDbProteinId(databaseName, accession));
        dbProt = NrSeqLookupUtil.getDbProtein(2);
        assertEquals(28, dbProt.getProteinId());
        assertEquals(2, dbProt.getDatabaseId());
        assertEquals("accession_string_4", dbProt.getAccessionString());
        assertNull(dbProt.getDescription());
    }
    
    public void testGetProtein() {
        String databaseName = "database";
        String accession = "accession_string_1";
        
        NrDbProtein dbProt = NrSeqLookupUtil.getDbProtein(databaseName, accession);
        assertEquals(25, dbProt.getProteinId());
        assertEquals(1, dbProt.getDatabaseId());
        assertEquals("accession_string_1", dbProt.getAccessionString());
        assertNull(dbProt.getDescription());
        
        databaseName = "dummy";
        dbProt = NrSeqLookupUtil.getDbProtein(databaseName, accession);
        assertNull(dbProt);
        
        
        databaseName = "database2"; 
        int databaseId = NrSeqLookupUtil.getDatabaseId(databaseName);
        assertEquals(2, databaseId);
        accession = "accession_string_4";
        dbProt = NrSeqLookupUtil.getDbProtein(databaseName, accession);
        assertEquals(28, dbProt.getProteinId());
        assertEquals(2, dbProt.getDatabaseId());
        assertEquals("accession_string_4", dbProt.getAccessionString());
        assertNull(dbProt.getDescription());
    }
    
    
    public void testGetDatabaseId() {
        String database = "database";
        assertEquals(1, NrSeqLookupUtil.getDatabaseId(database));
        
        database = "database2";
        assertEquals(2, NrSeqLookupUtil.getDatabaseId(database));
        
        database = "database_does_not_exist";
        int id = NrSeqLookupUtil.getDatabaseId(database);
        assertEquals(0, id);
        
        database = null;
        id = NrSeqLookupUtil.getDatabaseId(database);
        assertEquals(0, id);
    }
    
    public void testGetProteinAccession() {
        int searchDatabaseId = 1;
        int proteinId = 25;
        
        assertEquals("accession_string_1", NrSeqLookupUtil.getProteinAccession(searchDatabaseId, proteinId));
        
        searchDatabaseId = 2;
        try {NrSeqLookupUtil.getProteinAccession(searchDatabaseId, proteinId); fail("No match should be found");}
        catch(NrSeqLookupException e){}
        
        proteinId = 28;
        assertEquals("accession_string_4", NrSeqLookupUtil.getProteinAccession(searchDatabaseId, proteinId));
    }
}
