package org.yeastrc.ms.dao.search.sequest;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.yeastrc.ms.dao.search.MsSearchResultDAOImplTest.MsSearchResultTest;
import org.yeastrc.ms.dao.search.sqtfile.SQTBaseDAOTestCase;
import org.yeastrc.ms.domain.search.MsSearchResultProteinIn;
import org.yeastrc.ms.domain.search.impl.SearchResultPeptideBean;
import org.yeastrc.ms.domain.search.sequest.SequestResultData;
import org.yeastrc.ms.domain.search.sequest.SequestSearchResult;
import org.yeastrc.ms.domain.search.sequest.SequestSearchResultIn;

public class SequestSearchResultDAOImplTest extends SQTBaseDAOTestCase {


    protected void setUp() throws Exception {
        super.setUp();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void testOperationsOnSqtSearchResult() {
        
        super.resetDatabase();
        // try to get the result for a result id that does not exist in the table
        SequestSearchResult res = sequestResDao.load(1);
        assertNull(res);
        
        // insert one result in to the table
        SequestSearchResultTest result = makeSequestResult(3, "PEPTIDE", new BigDecimal("1024.5"), false); // charge = 3;
        
        // we have not yet set any of the SQT file specific values. Saving the 
        // result at this point should fail
        try {
            sequestResDao.save(97, result, 45, 32); // searchId = 97; runSearchId = 45; scanId = 32
            fail("Was able to save SQTSearchResult with null values!");
        }
        catch (RuntimeException e){
//            resultDao.deleteResultsForSearch(1);
        }
        
        result.setXCorrRank(1);
        result.setXCorr(new BigDecimal("0.50"));
        result.setSpRank(1);
        result.setSp(new BigDecimal("123.5"));
        result.setDeltaCN(new BigDecimal("0.001"));
        result.setMatchingIons(200);
        
        int resultId = sequestResDao.save(97, result, 45, 32); // searchId = 97; searchResultId = 45; scanId = 32
        
        // make sure everything got saved
        assertNotNull(resultDao.load(resultId));
        SequestSearchResult sqtResult_db = sequestResDao.load(resultId);
        assertEquals(sqtResult_db.getRunSearchId(), 45);
        assertEquals(sqtResult_db.getScanId(), 32);
        assertEquals(resultId, sqtResult_db.getId());
        checkSearchResult(result, sqtResult_db, true);
        
        // delete the result
        sequestResDao.delete(resultId);
        assertNull(resultDao.load(resultId));
        assertEquals(0, resultDao.loadResultIdsForRunSearch(1).size());
        assertNull(sequestResDao.load(resultId));
    }
    
    public final void testLoadTopResultsWProteinsForRunSearchN() {
        super.resetDatabase();
        // insert some result in to the table
        SequestSearchResultTest result1 = makeSequestResult(3, "PEPTIDEA", new BigDecimal("1024.5"), true); // charge = 3;
        result1.setXCorrRank(1);
        result1.setXCorr(new BigDecimal("0.50"));
        result1.setSpRank(1);
        result1.setSp(new BigDecimal("123.5"));
        result1.setDeltaCN(new BigDecimal("0.001"));
        result1.setMatchingIons(200);
        
        SequestSearchResultTest result2 = makeSequestResult(3, "PEPTIDEB", new BigDecimal("512.5"),true); // charge = 3;
        result2.setXCorrRank(2);
        result2.setXCorr(new BigDecimal("0.10"));
        result2.setSpRank(3);
        result2.setSp(new BigDecimal("123.5"));
        result2.setDeltaCN(new BigDecimal("0.001"));
        result2.setMatchingIons(200);
        
        SequestSearchResultTest result3 = makeSequestResult(3, "PEPTIDEC", new BigDecimal("256.5"),true); // charge = 3;
        result3.setXCorrRank(1);
        result3.setXCorr(new BigDecimal("0.10"));
        result3.setSpRank(1);
        result3.setSp(new BigDecimal("123.5"));
        result3.setDeltaCN(new BigDecimal("0.001"));
        result3.setMatchingIons(200);
        
        int resultId1 = sequestResDao.save(97, result1, 45, 32); // searchId = 97; runSearchId = 45; scanId = 32
        int resultId2 = sequestResDao.save(97, result2, 45, 32);
        int resultId3 = sequestResDao.save(97, result3, 45, 32);
        
        // We put 3 results in but the following queries should give us 2 results only
        // because result2 has XCorrRank > 1
//        List<Integer> resultIds= sequestResDao.loadTopResultIdsForRunSearch(45);
//        assertEquals(2, resultIds.size());
//        List<SequestSearchResult> resultList = sequestResDao.loadTopResultsWProteinsForRunSearchN(45);
//        assertEquals(2, resultList.size());
//        
//        SequestSearchResult res = resultList.get(0);
//        assertEquals(3, res.getProteinMatchList().size());
//        checkSearchResult(result1, res, true);
//        res = resultList.get(1);
//        assertEquals(3, res.getProteinMatchList().size());
//        checkSearchResult(result3, res, true);
        
        // delete the results;
        sequestResDao.delete(resultId1);
        sequestResDao.delete(resultId2);
        sequestResDao.delete(resultId3);
        
        assertEquals(0, sequestResDao.loadTopResultIdsForRunSearch(45).size());
    }
    
    public final void testLoadTopResultsForRunSearchN() {
        super.resetDatabase();
        // insert some results in to the table.
        SequestSearchResultTest result1 = makeSequestResult(3, "PEPTIDEA", new BigDecimal("1024.5"),true); // charge = 3;
        result1.setXCorrRank(1);
        result1.setXCorr(new BigDecimal("0.50"));
        result1.setSpRank(1);
        result1.setSp(new BigDecimal("123.5"));
        result1.setDeltaCN(new BigDecimal("0.001"));
        result1.setMatchingIons(200);
        
        SequestSearchResultTest result2 = makeSequestResult(3, "PEPTIDEB", new BigDecimal("512.5"), true); // charge = 3;
        result2.setXCorrRank(2);
        result2.setXCorr(new BigDecimal("0.10"));
        result2.setSpRank(3);
        result2.setSp(new BigDecimal("123.5"));
        result2.setDeltaCN(new BigDecimal("0.001"));
        result2.setMatchingIons(200);
        
        SequestSearchResultTest result3 = makeSequestResult(3, "PEPTIDEC", new BigDecimal("256.5"), true); // charge = 3;
        result3.setXCorrRank(1);
        result3.setXCorr(new BigDecimal("0.10"));
        result3.setSpRank(1);
        result3.setSp(new BigDecimal("123.5"));
        result3.setDeltaCN(new BigDecimal("0.001"));
        result3.setMatchingIons(200);
        
        int resultId1 = sequestResDao.save(97, result1, 45, 32); // searchId = 97; runSearchId = 45; scanId = 32
        int resultId2 = sequestResDao.save(97, result2, 45, 32);
        int resultId3 = sequestResDao.save(97, result3, 45, 32);
        
        // We put 3 results in but the following queries should give us 2 results only
        // because result2 has XCorrRank > 1 and result1 and result3 are tied for XCorrRank = 1
        List<Integer> resultIds= sequestResDao.loadTopResultIdsForRunSearch(45);
        assertEquals(2, resultIds.size());
        List<SequestSearchResult> resultList = sequestResDao.loadTopResultsForRunSearchN(45, false);
        assertEquals(2, resultList.size());
        
        SequestSearchResult res = resultList.get(0);
        assertEquals(0, res.getProteinMatchList().size());
        checkSearchResult(result1, res, false);
//        SequestSearchResult res = resultList.get(0);
//        assertEquals(0, res.getProteinMatchList().size());
//        checkSearchResult(result3, res, false);
        
        // delete the results;
        sequestResDao.delete(resultId1);
        sequestResDao.delete(resultId2);
        sequestResDao.delete(resultId3);
        
        assertEquals(0, sequestResDao.loadTopResultIdsForRunSearch(45).size());
    }
    
    private void checkSearchResult(SequestSearchResultIn input, SequestSearchResult output, boolean checkProteins) {
        super.checkSearchResult(input, output, checkProteins);
        
        SequestResultData iData = input.getSequestResultData();
        SequestResultData oData = output.getSequestResultData();
        assertEquals(iData.getDeltaCN().doubleValue(), oData.getDeltaCN().doubleValue());
        assertEquals(iData.getSp().doubleValue(), oData.getSp().doubleValue());
        assertEquals(iData.getxCorr().doubleValue(), oData.getxCorr().doubleValue());
        assertEquals(iData.getSpRank(), oData.getSpRank());
        assertEquals(iData.getxCorrRank(), oData.getxCorrRank());
        assertEquals(iData.getEvalue(), oData.getEvalue());
        assertNull(iData.getCalculatedMass());
        assertNull(oData.getCalculatedMass());
        assertEquals(iData.getMatchingIons(), oData.getMatchingIons());
        assertEquals(iData.getPredictedIons(), oData.getPredictedIons());
    }
    
    private SequestSearchResultTest makeSequestResult(int charge,String peptide, BigDecimal mass, boolean addProteins) {
        SequestSearchResultTest result = new SequestSearchResultTest();
        result.setCharge(charge);
        result.setObservedMass(mass);
        SearchResultPeptideBean resultPeptide = new SearchResultPeptideBean();
        resultPeptide.setPeptideSequence(peptide);
        result.setResultPeptide(resultPeptide);
        
        if (addProteins) {
            List<MsSearchResultProteinIn> proteins = new ArrayList<MsSearchResultProteinIn>();
            proteins.add(super.makeResultProtein("accession_1", null));
            proteins.add(super.makeResultProtein("accession_2", null));
            proteins.add(super.makeResultProtein("accession_3", null));
            result.setProteinMatchList(proteins);
        }

        return result;
    }
    
    public static final class SequestSearchResultTest extends MsSearchResultTest implements SequestSearchResultIn {

        private BigDecimal deltaCN;
        private BigDecimal deltaCNStar;
        private int spRank;
        private BigDecimal xCorr;
        private int xCorrRank;
        private BigDecimal sp;
        private BigDecimal calculatedMass;
        private Double evalue;
        private int matchingIons;
        private int predictedIons;

        public void setDeltaCN(BigDecimal deltaCN) {
            this.deltaCN = deltaCN;
        }
        public void setSpRank(int spRank) {
            this.spRank = spRank;
        }
        public void setXCorr(BigDecimal corr) {
            xCorr = corr;
        }
        public void setXCorrRank(int corrRank) {
            xCorrRank = corrRank;
        }
        public void setSp(BigDecimal sp) {
            this.sp = sp;
        }
        public void setEvalue(double evalue) {
            this.evalue = evalue;
        }
        public void setCalculatedMass(BigDecimal mass) {
            this.calculatedMass = mass;
        }
        public void setMatchingIons(int matchingIons) {
            this.matchingIons = matchingIons;
        }
        public void setPredictedIons(int predictedIons) {
            this.predictedIons = predictedIons;
        }
        @Override
        public SequestResultData getSequestResultData() {
            return new SequestResultData() {
                public BigDecimal getCalculatedMass() {
                    return calculatedMass;
                }
                public BigDecimal getDeltaCN() {
                    return deltaCN;
                }
                public Double getEvalue() {
                    return evalue;
                }
                public int getMatchingIons() {
                    return matchingIons;
                }
                public int getPredictedIons() {
                    return predictedIons;
                }
                public BigDecimal getSp() {
                    return sp;
                }
                public int getSpRank() {
                    return spRank;
                }
                public BigDecimal getxCorr() {
                    return xCorr;
                }
                public int getxCorrRank() {
                    return xCorrRank;
                }
				@Override
				public BigDecimal getDeltaCNstar() {
					return deltaCNStar;
				}
				@Override
				public void setCalculatedMass(BigDecimal mass) {
					throw new UnsupportedOperationException();
				}
				@Override
				public void setDeltaCN(BigDecimal deltaCn) {
					throw new UnsupportedOperationException();
				}
				@Override
				public void setDeltaCNstar(BigDecimal dcnStar) {
					throw new UnsupportedOperationException();
				}
				@Override
				public void setEvalue(Double evalue) {
					throw new UnsupportedOperationException();
				}
				@Override
				public void setMatchingIons(int matchingIons) {
					throw new UnsupportedOperationException();
				}
				@Override
				public void setPredictedIons(int predictedIons) {
					throw new UnsupportedOperationException();
				}
				@Override
				public void setSp(BigDecimal sp) {
					throw new UnsupportedOperationException();
				}
				@Override
				public void setSpRank(int rank) {
					throw new UnsupportedOperationException();
				}
				@Override
				public void setxCorr(BigDecimal xcorr) {
					throw new UnsupportedOperationException();
				}
				@Override
				public void setxCorrRank(int rank) {
					throw new UnsupportedOperationException();
				}};
        }
    }
}
