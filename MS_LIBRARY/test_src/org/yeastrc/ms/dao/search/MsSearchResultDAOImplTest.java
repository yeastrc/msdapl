package org.yeastrc.ms.dao.search;


import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.yeastrc.ms.dao.BaseDAOTestCase;
import org.yeastrc.ms.domain.search.MsResidueModificationIn;
import org.yeastrc.ms.domain.search.MsSearchResult;
import org.yeastrc.ms.domain.search.MsSearchResultIn;
import org.yeastrc.ms.domain.search.MsSearchResultPeptide;
import org.yeastrc.ms.domain.search.MsSearchResultProteinIn;
import org.yeastrc.ms.domain.search.ValidationStatus;
import org.yeastrc.ms.domain.search.impl.MsResidueModificationWrap;

public class MsSearchResultDAOImplTest extends BaseDAOTestCase {

    
    private int runSearchId_1 = 25;
    private int runSearchId_2 = 98;
    
    private int searchId_1 = 99;
//    private int searchId_2 = 100;
    
    protected void setUp() throws Exception {
        super.setUp();
        
        // modifications for searchId_1
        MsResidueModificationIn mod1 = makeStaticResidueMod('C', "50.0");
        modDao.saveStaticResidueMod(new MsResidueModificationWrap(mod1, searchId_1));
        MsResidueModificationIn mod2 = makeStaticResidueMod('S', "80.0");
        modDao.saveStaticResidueMod(new MsResidueModificationWrap(mod2, searchId_1));
        
        MsResidueModificationIn dmod1 = makeDynamicResidueMod('A', "10.0", '*');
        modDao.saveDynamicResidueMod(new MsResidueModificationWrap(dmod1, searchId_1));
        MsResidueModificationIn dmod2 = makeDynamicResidueMod('B', "20.0", '#');
        modDao.saveDynamicResidueMod(new MsResidueModificationWrap(dmod2, searchId_1));
        
        // modifications for searchId_2
//        MsResidueModification mod3 = makeStaticMod('M', "16.0");
//        modDao.saveStaticResidueMod(mod3, searchId_2);
//        MsResidueModification mod4 = makeStaticMod('S', "80.0");
//        modDao.saveStaticResidueMod(mod4, searchId_2);
//        
//        MsResidueModification dmod3 = makeDynamicMod('X', "100.0", '*');
//        modDao.saveDynamicResidueMod(dmod3, searchId_2);
//        MsResidueModification dmod4 = makeDynamicMod('Y', "90.0", '\u0000');
//        modDao.saveDynamicResidueMod(dmod4, searchId_2);
//        MsResidueModification dmod5 = makeDynamicMod('A', "10.0", '#');
//        modDao.saveDynamicResidueMod(dmod5, searchId_2);
        
    }
    
    protected void tearDown() throws Exception {
        super.tearDown();
        // delete modifications for searchId_1
        modDao.deleteDynamicResidueModsForSearch(runSearchId_1);
        modDao.deleteStaticResidueModsForSearch(runSearchId_1);
        
        // delete modifications for searchId_2
        modDao.deleteDynamicResidueModsForSearch(runSearchId_2);
        modDao.deleteStaticResidueModsForSearch(runSearchId_2);
    }

    public void testOperationsOnMsSearchResult() {
        assertNull(resultDao.load(runSearchId_1));
        
        // insert a search result with NO extra information
        MsSearchResultIn result1 = makeSearchResult(searchId_1, 3, "PEPTIDE1", new BigDecimal("1024.5"), false);
        int resultId_1 = resultDao.save(searchId_1, result1,  runSearchId_1, 123);// scanId = 123
        
        // read it back
        MsSearchResult resultdb1 = resultDao.load(resultId_1);
        assertNotNull(resultdb1);
        assertEquals(0, resultdb1.getProteinMatchList().size());
        assertEquals(0, resultdb1.getResultPeptide().getResultDynamicResidueModifications().size());
        checkSearchResult(result1, resultdb1);
       
        
        // save another result this time save protein matches
        MsSearchResultTest result2 = (MsSearchResultTest)makeSearchResult(searchId_1, 3, "PEPTIDE2", new BigDecimal("512.5"), false);
        addProteinMatches(result2);
        int resultId_2 = resultDao.save(searchId_1, result2, runSearchId_1, 123); // scanId = 123
        
        // read it back
        MsSearchResult resultdb2 = resultDao.load(resultId_2);
        assertNotNull(resultdb2);
        assertEquals(2, resultdb2.getProteinMatchList().size());
        assertEquals(0, resultdb2.getResultPeptide().getResultDynamicResidueModifications().size());
        checkSearchResult(result2, resultdb2);
        
        
        // save another result this time save dynamic mods
        // this time use runSearchId_2
        MsSearchResultIn result3 = makeSearchResult(searchId_1, 3, "PEPTIDE3", new BigDecimal("256.5"), true);
        int resultId_3 = resultDao.save(searchId_1, result3,  runSearchId_2, 321);
        
        // read it back
        MsSearchResult resultdb3 = resultDao.load(resultId_3);
        assertNotNull(resultdb3);
        assertEquals(0, resultdb3.getProteinMatchList().size());
        assertEquals(2, resultdb3.getResultPeptide().getResultDynamicResidueModifications().size());
        
        
        // delete ALL results for runSearchId_1
        List<Integer> resultIdList = resultDao.loadResultIdsForRunSearch(runSearchId_1);
        assertEquals(2, resultIdList.size());
        Collections.sort(resultIdList);
        assertEquals(resultId_1, resultIdList.get(0).intValue());
        assertEquals(resultId_2, resultIdList.get(1).intValue());
        resultDao.delete(resultId_1);
        resultDao.delete(resultId_2);
        
        
        // make sure everything was deleted
        assertEquals(0, resultDao.loadResultIdsForRunSearch(runSearchId_1).size());
        assertNull(resultDao.load(resultId_1));
        assertNull(resultDao.load(resultId_2));
        assertEquals(0, modDao.loadDynamicResidueModsForResult(resultId_1).size());
        assertEquals(0, matchDao.loadResultProteins(resultId_1).size());
        assertEquals(0, modDao.loadDynamicResidueModsForResult(resultId_2).size());
        assertEquals(0, matchDao.loadResultProteins(resultId_2).size());
        
        // these are for searchId_2 so should still exist
        assertNotNull(resultDao.load(resultId_3)); 
        assertEquals(2, modDao.loadDynamicResidueModsForResult(resultId_3).size());
        assertEquals(0,  matchDao.loadResultProteins(resultId_3).size());
        
        // delete ALL results for searchId_2
        resultIdList = resultDao.loadResultIdsForRunSearch(runSearchId_2);
        assertEquals(1, resultIdList.size());
        assertEquals(resultId_3, resultIdList.get(0).intValue());
        resultDao.delete(resultId_3);
        
        // make sure everything was deleted
        assertEquals(0, resultDao.loadResultIdsForRunSearch(runSearchId_2).size());
        assertNull(resultDao.load(resultId_3));
        assertEquals(0, modDao.loadDynamicResidueModsForResult(resultId_3).size());
        assertEquals(0, matchDao.loadResultProteins(resultId_3).size());
    }
    
    protected void addProteinMatches(MsSearchResultTest result) {

        List<MsSearchResultProteinIn> matchProteins = new ArrayList<MsSearchResultProteinIn>(2);
      
        matchProteins.add(makeResultProtein("accession_string_1", null));

        matchProteins.add(makeResultProtein("accession_string_2", null));
        
        result.setProteinMatchList(matchProteins);
    }
    
    public static class MsSearchResultTest implements MsSearchResultIn {

        private ValidationStatus validationStatus;
        private MsSearchResultPeptide resultPeptide;
        private List<MsSearchResultProteinIn> proteinMatchList = new ArrayList<MsSearchResultProteinIn>();
        private int charge;
        private int scanNumber;
        private BigDecimal observedMass;

        
        public int getCharge() {
            return charge;
        }

        public List<MsSearchResultProteinIn> getProteinMatchList() {
            return proteinMatchList;
        }

        public MsSearchResultPeptide getResultPeptide() {
            return resultPeptide;
        }

        public ValidationStatus getValidationStatus() {
            return validationStatus;
        }

        public void setValidationStatus(ValidationStatus validationStatus) {
            this.validationStatus = validationStatus;
        }

        public void setResultPeptide(MsSearchResultPeptide resultPeptide) {
            this.resultPeptide = resultPeptide;
        }

        public void setProteinMatchList(List<MsSearchResultProteinIn> proteinMatchList) {
            this.proteinMatchList = proteinMatchList;
        }

        public void setCharge(int charge) {
            this.charge = charge;
        }

        @Override
        public int getScanNumber() {
            return scanNumber;
        }
        
        public void setScanNumber(int scanNum) {
            this.scanNumber = scanNum;
        }

        @Override
        public BigDecimal getObservedMass() {
            return observedMass;
        }
        public void setObservedMass(BigDecimal mass) {
            this.observedMass = mass;
        }

		@Override
		public void addMatchingProteinMatch(MsSearchResultProteinIn match) {
			throw new UnsupportedOperationException();
		}
    }
}
