package org.yeastrc.ms.dao.general;

import java.util.Arrays;
import java.util.List;

import org.yeastrc.ms.dao.BaseDAOTestCase;
import org.yeastrc.ms.dao.general.MsEnzymeDAO.EnzymeProperties;
import org.yeastrc.ms.domain.general.MsEnzyme;
import org.yeastrc.ms.domain.general.MsEnzymeIn;
import org.yeastrc.ms.domain.general.MsEnzyme.Sense;
import org.yeastrc.ms.domain.general.impl.Enzyme;

public class MsEnzymeDAOImplTest extends BaseDAOTestCase {

    
    protected void setUp() throws Exception {
        super.setUp();
        resetDatabase();
        addEnzymes();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void testLoadEnzymeString() {
        List<MsEnzyme> enzymes = enzymeDao.loadEnzymes("trypsin");
        assertNotNull(enzymes);
        assertEquals(1, enzymes.size());
        
        enzymes = enzymeDao.loadEnzymes("xyz");
        assertEquals(0, enzymes.size());
    }

    public void testLoadEnzymeStringIntStringString() {
        // load an enzyme we know exists in the database
        List<MsEnzyme> enzymes = enzymeDao.loadEnzymes("trypsin", Sense.CTERM, "KR", "P");
        assertEquals(1, enzymes.size());
        MsEnzyme enzyme = enzymes.get(0);
        assertNotNull(enzyme);
        assertEquals("trypsin".toUpperCase(), enzyme.getName().toUpperCase());
        assertEquals(Sense.CTERM, enzyme.getSense());
        assertEquals("KR", enzyme.getCut());
        assertEquals("P", enzyme.getNocut());
        
        // this enzyme does not exist in the database.
        enzymes = enzymeDao.loadEnzymes("trypsin", Sense.UNKNOWN, "KR", "P");
        assertEquals(0, enzymes.size());
    }
    
    public void testSaveEnzymeCheckAllProps() {
        List<MsEnzyme> enzymes = enzymeDao.loadEnzymes("trypsin", Sense.CTERM, "KR", "P");
        assertEquals(1, enzymes.size());
        MsEnzyme oEnzyme = enzymes.get(0);
        int enzyme_db_id = oEnzyme.getId();
        
        Enzyme enz = new Enzyme();
        enz.setName("trypsin");
        enz.setCut("KR");
        enz.setNocut("P");
        enz.setSense(Sense.CTERM);
        int id = enzymeDao.saveEnzyme(enz);
        // this is the same as an enzyme we already have; id returned should be the same
        assertEquals(id, enzyme_db_id);
        
        // change one of the properties and save again.
        enz.setNocut(null);
        id = enzymeDao.saveEnzyme(enz);
        assertNotSame(enzyme_db_id, id);
       
    }
    
    public void testSaveEnzymeForRunCheckName() {
        // load an enzyme we know exists in the database
        List<MsEnzyme> enzymes = enzymeDao.loadEnzymes("trypsin", Sense.CTERM, "KR", "P");
        assertEquals(1, enzymes.size());
        MsEnzyme oEnzyme = enzymes.get(0);
        int enzyme_db_id = oEnzyme.getId();
        
        int runId1 = 20; 
        int runId2 = 30;
        
        // try to link a runid with with this enzyme
        // the database id returned by the save method should be the same as for the enzyme above
        int enzymeId_1 = enzymeDao.saveEnzymeforRun(oEnzyme, runId1);
        assertEquals(enzymeId_1, enzyme_db_id);
        
        // we know this enzyme does not exist in the database
        enzymes = enzymeDao.loadEnzymes("Dummy", Sense.UNKNOWN, "ABC", null);
        assertEquals(0, enzymes.size());
        
        // create and save the enzyme
        MsEnzymeIn iEnzyme = makeDigestionEnzyme("Dummy", Sense.UNKNOWN, "ABC", null);
        // create a link between the enzyme and the runID
        // this should also save a new entry in the msDigestionEnzyme table
        int enzymeId_2 = enzymeDao.saveEnzymeforRun(iEnzyme, runId1);
        // make sure a new entry was created for the enzyme
        assertNotSame(enzymeId_1, enzymeId_2);

        // make sure we now have two enzyme entries for this run;
        enzymes = enzymeDao.loadEnzymesForRun(runId1);
        assertEquals(2, enzymes.size());


        // try to create another link for this enzyme to another runId. 
        // This time specify the parameters that will be used to look for 
        // a matching enzyme in the database;
        iEnzyme = makeDigestionEnzyme("Dummy", null, null, null);
        EnzymeProperties[] properties = new EnzymeProperties[]{EnzymeProperties.NAME};
        int enzymeId_3 = enzymeDao.saveEnzymeforRun(iEnzyme, runId2, Arrays.asList(properties));
        // this should not have saved a new enzyme so the returned id should the the same as before
        assertEquals(enzymeId_3, enzymeId_2);
        
        
        // clean up 
        // remove entries from the msRunEnzyme table
        enzymeDao.deleteEnzymesForRun(runId1);
        assertEquals(0, enzymeDao.loadEnzymesForRun(runId1).size());
        enzymeDao.deleteEnzymesForRun(runId2);
        assertEquals(0, enzymeDao.loadEnzymesForRun(runId2).size());
        
        // delete the "Dummy" enzymes
        enzymeDao.deleteEnzymeById(enzymeId_2); 
        enzymeDao.deleteEnzymeById(enzymeId_3); 
    }
    
    public void testSaveEnzymeForSearchCheckName() {
        // load an enzyme we know exists in the database
        List<MsEnzyme> enzymes = enzymeDao.loadEnzymes("TRYPSIN", Sense.CTERM, "KR", "P");
        assertEquals(1, enzymes.size());
        MsEnzyme oEnzyme = enzymes.get(0);
        int enzyme_db_id = oEnzyme.getId();
        
        int searchId1 = 42; 
        int searchId2 = 24;
        
        // try to link a searchid with with this enzyme
        // the database id returned by the save method should be the same as for the enzyme above
        int enzymeId_1 = enzymeDao.saveEnzymeforSearch(oEnzyme, searchId1);
        assertEquals(enzymeId_1, enzyme_db_id);
        
        // we know this enzyme does not exist in the database
        enzymes = enzymeDao.loadEnzymes("Dummy", Sense.UNKNOWN, "ABC", null);
        assertEquals(0, enzymes.size());
        
        // create and save the enzyme
        MsEnzymeIn iEnzyme = makeDigestionEnzyme("Dummy", Sense.UNKNOWN, "ABC", null);
        // create a link between the enzyme and the serachID
        // this should also save a new entry in the msDigestionEnzyme table
        int enzymeId_2 = enzymeDao.saveEnzymeforSearch(iEnzyme, searchId1);
        // make sure a new entry was created for the enzyme
        assertNotSame(enzymeId_1, enzymeId_2);

        // make sure we now have two enzyme entries for this search;
        enzymes = enzymeDao.loadEnzymesForSearch(searchId1);
        assertEquals(2, enzymes.size());


        // try to create another link for this enzyme to another searchId. 
        // This time specify the parameters that will be used to look for 
        // a matching enzyme in the database;
        iEnzyme = makeDigestionEnzyme("Dummy", null, null, null);
        EnzymeProperties[] properties = new EnzymeProperties[]{EnzymeProperties.NAME};
        int enzymeId_3 = enzymeDao.saveEnzymeforSearch(iEnzyme, searchId2, Arrays.asList(properties));
        // this should not have saved a new enzyme so the returned id should the the same as before
        assertEquals(enzymeId_3, enzymeId_2);
        
        
        // clean up 
        // remove entries from the msRunEnzyme table
        enzymeDao.deleteEnzymesForSearch(searchId1);
        assertEquals(0, enzymeDao.loadEnzymesForSearch(searchId1).size());
        enzymeDao.deleteEnzymesForSearch(searchId2);
        assertEquals(0, enzymeDao.loadEnzymesForSearch(searchId2).size());
        
        // delete the "Dummy" enzymes
        enzymeDao.deleteEnzymeById(enzymeId_2); 
        enzymeDao.deleteEnzymeById(enzymeId_3); 
        
    }
    
    public void testSenseValue() {
        
        MsEnzymeIn enzyme = super.makeDigestionEnzyme("Dummy", Sense.UNKNOWN, null, null);
        assertEquals(Sense.UNKNOWN, enzyme.getSense());
        int enzymeId_1 = enzymeDao.saveEnzyme(enzyme);
        MsEnzyme enzyme_db = enzymeDao.loadEnzyme(enzymeId_1);
        assertEquals(Sense.UNKNOWN, enzyme_db.getSense());
        
        enzyme = super.makeDigestionEnzyme("Dummy", Sense.CTERM, null, null);
        int enzymeId_2 = enzymeDao.saveEnzyme(enzyme);
        enzyme_db = enzymeDao.loadEnzyme(enzymeId_2);
        assertEquals(Sense.CTERM, enzyme_db.getSense());
        
        // delete the "Dummy" enzymes
        enzymeDao.deleteEnzymeById(enzymeId_1); 
        enzymeDao.deleteEnzymeById(enzymeId_2); 
        
    }
}
