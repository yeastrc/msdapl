package org.yeastrc.ms.dao.search;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import org.yeastrc.ms.dao.BaseDAOTestCase;
import org.yeastrc.ms.domain.general.MsEnzymeIn;
import org.yeastrc.ms.domain.search.MsResidueModificationIn;
import org.yeastrc.ms.domain.search.MsSearch;
import org.yeastrc.ms.domain.search.MsSearchDatabaseIn;
import org.yeastrc.ms.domain.search.MsSearchIn;
import org.yeastrc.ms.domain.search.MsTerminalModificationIn;
import org.yeastrc.ms.domain.search.Program;

public class MsSearchDAOImplTest extends BaseDAOTestCase {

    protected void setUp() throws Exception {
        super.setUp();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void testOperationsOnMsSearch() {
        
        int experimentId = 45;
        
        // create and save a search with no seq. db information or modifications or enzymes
        MsSearchIn search_1 = makeSearch(false, false, false, false);
        assertEquals(0, search_1.getSearchDatabases().size());
        assertEquals(0, search_1.getStaticResidueMods().size());
        assertEquals(0, search_1.getDynamicResidueMods().size());
        assertEquals(0, search_1.getEnzymeList().size());
        
        int searchId_1 = searchDao.saveSearch(search_1, experimentId, 256); // proteinDatabaseId = 256
        assertEquals(0, runSearchDao.loadRunSearchIdsForSearch(searchId_1).size());
        checkSearch(search_1, searchDao.loadSearch(searchId_1));
        
        
        // create and save a search with seq. db information and modifications AND enzymes
        MsSearchIn search_2 = makeSearch(true, true, true, true);
        assertTrue(search_2.getSearchDatabases().size() > 0);
        assertTrue(search_2.getStaticResidueMods().size() > 0);
        assertTrue(search_2.getDynamicResidueMods().size() > 0);
        assertTrue(search_2.getEnzymeList().size() > 0);
        
        int searchId_2 = searchDao.saveSearch(search_2, experimentId, 256); // proteinDatabaseId = 256
        assertEquals(2, seqDbDao.loadSearchDatabases(searchId_2).size());
        assertEquals(2, modDao.loadStaticResidueModsForSearch(searchId_2).size());
        assertEquals(3, modDao.loadDynamicResidueModsForSearch(searchId_2).size());
        assertEquals(2, enzymeDao.loadEnzymesForSearch(searchId_2).size());
        checkSearch(search_2, searchDao.loadSearch(searchId_2));
        
        
        searchDao.updateSearchProgram(searchId_2, Program.PROLUCID);
        MsSearch fromDb = searchDao.loadSearch(searchId_2);
        assertEquals(Program.PROLUCID, fromDb.getSearchProgram());
        
        // delete the searches
        searchDao.deleteSearch(searchId_1);
        searchDao.deleteSearch(searchId_2);
        
        testSearchDeleted(searchId_1);
        testSearchDeleted(searchId_2);
        
    }

    private void testSearchDeleted(int searchId) {
        assertNull(searchDao.loadSearch(searchId));
        assertEquals(0, seqDbDao.loadSearchDatabases(searchId).size());
        assertEquals(0, modDao.loadStaticResidueModsForSearch(searchId).size());
        assertEquals(0, modDao.loadDynamicResidueModsForSearch(searchId).size());
        assertEquals(0, modDao.loadStaticTerminalModsForSearch(searchId).size());
        assertEquals(0, modDao.loadDynamicTerminalModsForSearch(searchId).size());
        assertEquals(0, runSearchDao.loadRunSearchIdsForSearch(searchId).size());
    }
    
    public static class MsSearchTest implements MsSearchIn {

        private List<MsResidueModificationIn> dynamicResidueModifications = new ArrayList<MsResidueModificationIn>();
        private List<MsResidueModificationIn> staticResidueModifications = new ArrayList<MsResidueModificationIn>();
        private List<MsTerminalModificationIn> dynamicTerminalModifications = new ArrayList<MsTerminalModificationIn>();
        private List<MsTerminalModificationIn> staticTerminalModifications = new ArrayList<MsTerminalModificationIn>();
        
        private List<MsSearchDatabaseIn> searchDatabases = new ArrayList<MsSearchDatabaseIn>();
        private List<MsEnzymeIn> enzymes = new ArrayList<MsEnzymeIn>();
        private String searchEngineVersion;
        private Program searchProgram;
        private Date searchDate;


        public List<MsSearchDatabaseIn> getSearchDatabases() {
            return searchDatabases;
        }

        public void setSearchDatabases(List<MsSearchDatabaseIn> searchDatabases) {
            this.searchDatabases = searchDatabases;
        }
        
        public Date getSearchDate() {
            return searchDate;
        }

        public void setSearchDate(Date searchDate) {
            this.searchDate = searchDate;
        }
        
        public List<MsResidueModificationIn> getStaticResidueMods() {
            return staticResidueModifications;
        }

        public void setStaticResidueMods(
                List<MsResidueModificationIn> staticModifications) {
            this.staticResidueModifications = staticModifications;
        }
        
        public List<MsResidueModificationIn> getDynamicResidueMods() {
            return dynamicResidueModifications;
        }

        public void setDynamicResidueMods(
                List<MsResidueModificationIn> dynaResMods) {
            this.dynamicResidueModifications = dynaResMods;
        }

        public List<MsEnzymeIn> getEnzymeList() {
            return enzymes;
        }
        
        public void setEnzymeList(List<MsEnzymeIn> enzymeList) {
            this.enzymes = enzymeList;
        }

        @Override
        public List<MsTerminalModificationIn> getDynamicTerminalMods() {
            return dynamicTerminalModifications;
        }

        public void setDynamicTerminalMods(List<MsTerminalModificationIn> mods) {
            this.dynamicTerminalModifications = mods;
        }
        
        @Override
        public List<MsTerminalModificationIn> getStaticTerminalMods() {
            return staticTerminalModifications;
        }

        public void setStaticTerminalMods(List<MsTerminalModificationIn> mods) {
            this.staticTerminalModifications = mods;
        }
        
        public void setSearchProgram(Program searchProgram) {
            this.searchProgram = searchProgram;
        }
        
        @Override
        public Program getSearchProgram() {
            return searchProgram;
        }

        public void setAnalysisProgramVersion(String searchEngineVersion) {
            this.searchEngineVersion = searchEngineVersion;
        }
        
        @Override
        public String getSearchProgramVersion() {
            return searchEngineVersion;
        }

        @Override
        public String getServerDirectory() {
            return "remote/directory";
        }
    }
}
