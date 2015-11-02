package org.yeastrc.ms.dao.search.sequest;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.yeastrc.ms.dao.search.MsSearchDAOImplTest.MsSearchTest;
import org.yeastrc.ms.dao.search.sqtfile.SQTBaseDAOTestCase;
import org.yeastrc.ms.domain.search.Param;
import org.yeastrc.ms.domain.search.sequest.SequestSearch;
import org.yeastrc.ms.domain.search.sequest.SequestSearchIn;


public class SequestSearchDAOImplTest extends SQTBaseDAOTestCase {

   
    protected void setUp() throws Exception {
        super.setUp();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void testOperationsOnSequestSearch () {
        
        int experimentId = 67;
        // no saved search exists right now
        assertNull(sequestSearchDao.loadSearch(1));
        
        // save a search (don't add any extra information)
        SequestSearchIn search_1 = makeSequestSearch(false, false, false);
        assertEquals(0, search_1.getSearchDatabases().size());
        int searchId_1 = sequestSearchDao.saveSearch(search_1, experimentId, 987); // proteinDatabaseId = 987
        
        // load using our specialized SequestSearchDAO
        SequestSearch search_1_db = sequestSearchDao.loadSearch(searchId_1);
        assertNotNull(search_1_db);
        assertEquals(searchId_1, search_1_db.getId());
        assertEquals(0, search_1_db.getDynamicResidueMods().size());
        assertEquals(0, search_1_db.getDynamicTerminalMods().size());
        assertEquals(0, search_1_db.getStaticResidueMods().size());
        assertEquals(0, search_1_db.getStaticTerminalMods().size());
        assertEquals(0, search_1_db.getSearchDatabases().size());
        assertEquals(0, search_1_db.getEnzymeList().size());
        checkSequestSearch(search_1, search_1_db);
        
        // save another search (add extra information)
        SequestSearchIn search_2 = makeSequestSearch(true, true, true);
        int searchId_2 = sequestSearchDao.saveSearch(search_2, experimentId, 789); // proteinDatabaseId = 789
        
        // load the search and check values
        SequestSearch search_2_db = sequestSearchDao.loadSearch(searchId_2);
        assertNotNull(search_2_db);
        assertEquals(searchId_2, search_2_db.getId());
        assertTrue(search_2_db.getDynamicResidueMods().size() > 0);
        assertTrue(search_2_db.getDynamicTerminalMods().size() > 0);
        assertTrue(search_2_db.getStaticResidueMods().size() > 0);
        assertTrue(search_2_db.getStaticTerminalMods().size() > 0);
        assertTrue(search_2_db.getSearchDatabases().size() > 0);
        assertTrue(search_2_db.getEnzymeList().size() == 0);
        checkSequestSearch(search_2, search_2_db);
        
        // delete the searches
        sequestSearchDao.deleteSearch(searchId_1);
        assertNull(sequestSearchDao.loadSearch(searchId_1));
        
        sequestSearchDao.deleteSearch(searchId_2);
        assertNull(sequestSearchDao.loadSearch(searchId_2));
        
    }
    
    protected void checkSequestSearch(SequestSearchIn input, SequestSearch output) {
        super.checkSearch(input, output);
        
        List<Param> inputParams = input.getSequestParams();
        List<Param> outputParams = output.getSequestParams();
        
        assertEquals(inputParams.size(), outputParams.size());
        
        Collections.sort(inputParams, new Comparator<Param>() {
            public int compare(Param o1, Param o2) {
                return o1.getParamName().compareTo(o2.getParamName());
            }});
        Collections.sort(outputParams, new Comparator<Param>() {
            public int compare(Param o1, Param o2) {
                return o1.getParamName().compareTo(o2.getParamName());
            }});
        
        for (int i = 0; i < inputParams.size(); i++) {
            Param ip = inputParams.get(i);
            Param op = outputParams.get(i);
            assertEquals(ip.getParamName(), op.getParamName());
            assertEquals(op.getParamValue(), op.getParamValue());
        }
    }
    
    public static final class SequestSearchTest extends MsSearchTest implements SequestSearchIn {

        private List<Param> params;
        
        public void setSequestParams(List<Param> params) {
            this.params = params;
        }

		@Override
		public List<Param> getSequestParams() {
			return params;
		}
    }
}
