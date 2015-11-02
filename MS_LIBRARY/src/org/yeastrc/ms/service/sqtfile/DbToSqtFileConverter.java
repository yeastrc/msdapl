/**
 * DbToSqtFileconverter.java
 * @author Vagisha Sharma
 * Jul 28, 2008
 * @version 1.0
 */
package org.yeastrc.ms.service.sqtfile;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.yeastrc.ms.dao.DAOFactory;
import org.yeastrc.ms.dao.run.MsScanDAO;
import org.yeastrc.ms.dao.search.MsSearchDAO;
import org.yeastrc.ms.dao.search.MsSearchModificationDAO;
import org.yeastrc.ms.dao.search.MsSearchResultProteinDAO;
import org.yeastrc.ms.dao.search.sequest.SequestSearchResultDAO;
import org.yeastrc.ms.dao.search.sqtfile.SQTRunSearchDAO;
import org.yeastrc.ms.dao.search.sqtfile.SQTSearchScanDAO;
import org.yeastrc.ms.domain.run.MsScan;
import org.yeastrc.ms.domain.search.MsResidueModification;
import org.yeastrc.ms.domain.search.MsResidueModificationIn;
import org.yeastrc.ms.domain.search.MsResultResidueMod;
import org.yeastrc.ms.domain.search.MsSearch;
import org.yeastrc.ms.domain.search.MsSearchDatabase;
import org.yeastrc.ms.domain.search.MsSearchResultPeptide;
import org.yeastrc.ms.domain.search.MsSearchResultProtein;
import org.yeastrc.ms.domain.search.MsTerminalModification;
import org.yeastrc.ms.domain.search.SearchFileFormat;
import org.yeastrc.ms.domain.search.sequest.SequestResultData;
import org.yeastrc.ms.domain.search.sequest.SequestSearchResult;
import org.yeastrc.ms.domain.search.sequest.impl.SequestResult;
import org.yeastrc.ms.domain.search.sqtfile.SQTHeaderItem;
import org.yeastrc.ms.domain.search.sqtfile.SQTRunSearch;
import org.yeastrc.ms.domain.search.sqtfile.SQTSearchScan;
import org.yeastrc.ms.parser.sqtFile.SQTHeader;
import org.yeastrc.ms.parser.sqtFile.SQTParseException;
import org.yeastrc.ms.parser.sqtFile.SearchScan;
import org.yeastrc.nrseq.dao.NrSeqLookupUtil;

/**
 * 
 */
public class DbToSqtFileConverter {

    private BufferedWriter outFile = null;

    public void convertToSqt(int runSearchId, String outputFile) throws IOException {

        try {
            outFile = new BufferedWriter(new FileWriter(outputFile));

            SQTRunSearchDAO searchDao = DAOFactory.instance().getSqtRunSearchDAO();
            SQTRunSearch runSearch = searchDao.loadRunSearch(runSearchId);
            if (runSearch == null) {
                System.err.println("No run search found with id: "+runSearchId);
                return;
            }
            
            int searchDatabaseId = getSearchDatabaseId(runSearch.getSearchId());
            
            printSqtHeader(runSearch);
            outFile.write("\n");
            SearchFileFormat origFileType = runSearch.getSearchFileFormat();
            if (origFileType == SearchFileFormat.SQT_SEQ) {
                printSequestSQTData(runSearch, searchDatabaseId, outFile);
            }
            else if (origFileType == SearchFileFormat.SQT_PLUCID) {
                // TODO
            }
            
            outFile.flush();
        }
        finally {
            if (outFile != null)
                outFile.close();
        }
    }

    private int getSearchDatabaseId(int searchId) {
        MsSearchDAO searchDao = DAOFactory.instance().getMsSearchDAO();
        MsSearch search = searchDao.loadSearch(searchId);
        List<MsSearchDatabase> db = search.getSearchDatabases();
        if (db.size() == 0)
            return 0;
        return NrSeqLookupUtil.getDatabaseId(db.get(0).getDatabaseFileName());
    }
    
    private void printSequestSQTData(SQTRunSearch runSearch, int searchDatabaseId, BufferedWriter outFile) throws IOException {
        
        List<MsResidueModification> dynaResidueModsDb = getDynaResidueModsForSearch(runSearch.getSearchId());
        
        SQTSearchScanDAO scanDao = DAOFactory.instance().getSqtSpectrumDAO();
        
        SequestSearchResultDAO resultDao = DAOFactory.instance().getSequestResultDAO();
        List<Integer> resultIds = resultDao.loadResultIdsForRunSearch(runSearch.getId());
        int currCharge = -1;
        int currScanId = -1;
        SearchScan currScan = null;
        for (Integer resultId: resultIds) {
            SequestSearchResult result = resultDao.load(resultId);
            if (result.getScanId() != currScanId || result.getCharge() != currCharge) {
                if (currScan != null) {
                    outFile.write(currScan.toString());
                    outFile.write("\n");
                }
                currScanId = result.getScanId();
                currCharge = result.getCharge();
                SQTSearchScan scanDb = scanDao.load(runSearch.getId(), currScanId, currCharge, result.getObservedMass());
                currScan = makeScanResult(scanDb);
            }
            List<MsResidueModificationIn> dynaResidueMods = new ArrayList<MsResidueModificationIn>();
            for (MsResidueModification modDb: dynaResidueModsDb) {
                dynaResidueMods.add(modDb);
            }
            SequestResult peptResult = new SequestResult();
            peptResult.setResultPeptide(result.getResultPeptide());
            SequestResultData data = result.getSequestResultData();
            peptResult.setCharge(result.getCharge());
            peptResult.setObservedMass(result.getObservedMass());
            peptResult.setDeltaCN(data.getDeltaCN());
            peptResult.setCalculatedMass(data.getCalculatedMass());
            peptResult.setMatchingIons(data.getMatchingIons());
            peptResult.setPredictedIons(data.getPredictedIons());
            peptResult.setOriginalPeptideSequence(reconstructSequestPeptideSequence(runSearch.getSearchId(), result));
            peptResult.setScanNumber(currScan.getScanNumber());
            peptResult.setSp(data.getSp());
            peptResult.setSpRank(data.getSpRank());
            peptResult.setValidationStatus(result.getValidationStatus().getStatusChar());
            peptResult.setxCorr(data.getxCorr());
            peptResult.setxCorrRank(data.getxCorrRank());
            peptResult.setEvalue(data.getEvalue());
            
            
            List<MsSearchResultProtein> proteins = getProteinsForResultId(resultId);
            for (MsSearchResultProtein pr: proteins) {
                peptResult.addMatchingLocus(pr.getAccession(), null);
            }
            //currScan.addPeptideResult(peptResult);
        }
        // print the last one
        if (currScan != null) {
            outFile.write(currScan.toString());
            outFile.write("\n");
        }
    }

    private List<MsSearchResultProtein> getProteinsForResultId(Integer resultId) {
        MsSearchResultProteinDAO proteinDao = DAOFactory.instance().getMsProteinMatchDAO();
        return proteinDao.loadResultProteins(resultId);
    }

    private String reconstructSequestPeptideSequence(int searchId, SequestSearchResult resultDb) {
        // dynamic modifications for the search
        MsSearchResultPeptide peptideSeq = resultDb.getResultPeptide();
        List<MsResultResidueMod> resultMods = peptideSeq.getResultDynamicResidueModifications();
        Collections.sort(resultMods, new Comparator<MsResultResidueMod>() {
            public int compare(MsResultResidueMod o1,
                    MsResultResidueMod o2) {
                return new Integer(o1.getModifiedPosition()).compareTo(new Integer(o2.getModifiedPosition()));
            }});
        
        String justSeq = peptideSeq.getPeptideSequence();
        StringBuilder fullSeq = new StringBuilder();
        fullSeq.append(peptideSeq.getPreResidue()+".");
        int lastIdx = 0;
        for (MsResultResidueMod mod: resultMods) {
            int pos = mod.getModifiedPosition();
            fullSeq.append(justSeq.substring(lastIdx, pos+1));
            fullSeq.append(mod.getModificationSymbol());
            lastIdx = pos+1;
        }
        if (lastIdx < justSeq.length()) {
            fullSeq.append(justSeq.substring(lastIdx, justSeq.length()));
        }
        fullSeq.append("."+peptideSeq.getPostResidue());
        return fullSeq.toString();
    }
    
    private List<MsResidueModification> getDynaResidueModsForSearch(int dbSearchId) {
        MsSearchModificationDAO modDao = DAOFactory.instance().getMsSearchModDAO();
        List<MsResidueModification> dynaMods = modDao.loadDynamicResidueModsForSearch(dbSearchId);
        return dynaMods;
    }
    
    private List<MsTerminalModification> getDynaTermModsForSearch(int dbSearchId) {
        MsSearchModificationDAO modDao = DAOFactory.instance().getMsSearchModDAO();
        List<MsTerminalModification> dynaMods = modDao.loadDynamicTerminalModsForSearch(dbSearchId);
        return dynaMods;
    }

    private SearchScan makeScanResult(SQTSearchScan resultScan) {
        SearchScan scanResult = new SearchScan();
        MsScan msScan = getScanForId(resultScan.getScanId());
        scanResult.setStartScan(msScan.getStartScanNum());
        scanResult.setEndScan(msScan.getStartScanNum());
        scanResult.setCharge(resultScan.getCharge());
        scanResult.setLowestSp(resultScan.getLowestSp());
        scanResult.setObservedMass(msScan.getPrecursorMz());
        scanResult.setProcessingTime(resultScan.getProcessTime());
        scanResult.setSequenceMatches(resultScan.getSequenceMatches());
        scanResult.setServer(resultScan.getServerName());
        scanResult.setTotalIntensity(resultScan.getTotalIntensity());
        return scanResult;
    }

    private MsScan getScanForId(int scanId) {
        MsScanDAO scanDao = DAOFactory.instance().getMsScanDAO();
        return scanDao.load(scanId);
    }
    
    private void printSqtHeader(SQTRunSearch search) throws IOException {
        SQTHeader sqtHeader = new SQTHeader();
        List<SQTHeaderItem> headerList = search.getHeaders();

        for (SQTHeaderItem header: headerList) {
            try {
                sqtHeader.addHeaderItem(header.getName(), header.getValue());
            }
            catch (SQTParseException e) {
                e.printStackTrace();
            }
        }
        outFile.write(sqtHeader.toString());
    }
}
