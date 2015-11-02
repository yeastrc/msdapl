package org.yeastrc.ms.service.sqtfile;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.yeastrc.ms.dao.BaseDAOTestCase;
import org.yeastrc.ms.dao.DAOFactory;
import org.yeastrc.ms.dao.search.sequest.SequestSearchDAO;
import org.yeastrc.ms.dao.search.sequest.SequestSearchResultDAO;
import org.yeastrc.ms.dao.search.sqtfile.SQTRunSearchDAO;
import org.yeastrc.ms.dao.search.sqtfile.SQTSearchScanDAO;
import org.yeastrc.ms.domain.general.MsEnzyme;
import org.yeastrc.ms.domain.general.MsEnzyme.Sense;
import org.yeastrc.ms.domain.search.MsResidueModification;
import org.yeastrc.ms.domain.search.MsResultResidueMod;
import org.yeastrc.ms.domain.search.MsResultTerminalMod;
import org.yeastrc.ms.domain.search.MsSearchDatabase;
import org.yeastrc.ms.domain.search.MsTerminalModification;
import org.yeastrc.ms.domain.search.Param;
import org.yeastrc.ms.domain.search.Program;
import org.yeastrc.ms.domain.search.SearchFileFormat;
import org.yeastrc.ms.domain.search.ValidationStatus;
import org.yeastrc.ms.domain.search.MsTerminalModification.Terminal;
import org.yeastrc.ms.domain.search.sequest.SequestSearch;
import org.yeastrc.ms.domain.search.sequest.SequestSearchResult;
import org.yeastrc.ms.domain.search.sqtfile.SQTHeaderItem;
import org.yeastrc.ms.domain.search.sqtfile.SQTRunSearch;
import org.yeastrc.ms.domain.search.sqtfile.SQTSearchScan;
import org.yeastrc.ms.service.MsDataUploader;

public class SequestSQTDataUploadServiceTest extends BaseDAOTestCase {

    protected void setUp() throws Exception {
        super.setUp();
        resetDatabase();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void testUploadSequestData() {
        String dir = "test_resources/validSequestData_dir";
        MsDataUploader uploader = new MsDataUploader();
        java.util.Date experimentDate = new java.util.Date();
        
        int experimentId = 0;
        uploader.setSpectrumDataDirectory(dir);
        uploader.setSearchDirectory(dir);
        uploader.setSearchDate(experimentDate);
        uploader.setRemoteServer("remoteServer");
        uploader.setRemoteSearchDataDirectory("remoteDirectory");
        uploader.uploadData();
        experimentId = uploader.getUploadedExperimentId();
        assertNotSame(0, experimentId);
        assertEquals(0, uploader.getUploadExceptionList().size());
        
        // make sure all the data got uploaded
        int runId1 = getRunId("1");
        int runId2 = getRunId("2");
        
        checkSearch(uploader.getUploadedSearchId(), experimentDate);
        
//        checkFirstRunSearch(searchId, runId1);
        checkSecondRunSearch(uploader.getUploadedSearchId(), runId2);
        
    }
    
    private void checkSearch(int searchId, java.util.Date date) {
        SequestSearchDAO searchDao = DAOFactory.instance().getSequestSearchDAO();
        SequestSearch search = searchDao.loadSearch(searchId);
        assertNotNull(search);
        assertNull(searchDao.loadSearch(24));
        
        assertEquals("remoteDirectory", search.getServerDirectory());
        assertEquals(Program.SEQUEST, search.getSearchProgram());
        assertEquals("3.0", search.getSearchProgramVersion());
//        System.out.println(date.toString());
//        System.out.println(search.getSearchDate().toString());
//        assertEquals(date.getTime(), search.getSearchDate().getTime()); TODO
        assertNotNull(search.getUploadDate());
        
        // check search databases
        List<MsSearchDatabase> dbs = search.getSearchDatabases();
        assertEquals(1, dbs.size());
        assertEquals("/net/maccoss/vol2/software/pipeline/dbase/mouse-ipi-250309-contam.fasta", dbs.get(0).getServerPath());
        assertEquals("remoteServer", dbs.get(0).getServerAddress());
        
        // check search enzymes
        List<MsEnzyme> enzymes = search.getEnzymeList();
        assertEquals(1, enzymes.size());
        assertEquals("Elastase/Tryp/Chymo", enzymes.get(0).getName());
        assertEquals(Sense.CTERM, enzymes.get(0).getSense());
        assertEquals("ALIVKRWFY", enzymes.get(0).getCut());
        assertEquals("P", enzymes.get(0).getNocut());
        
        // check the static residue modifications
        List<MsResidueModification> staticResMods = search.getStaticResidueMods();
        assertEquals(1, staticResMods.size());
        assertEquals(160.1390, staticResMods.get(0).getModificationMass().doubleValue());
        assertEquals('C', staticResMods.get(0).getModifiedResidue());
        assertEquals(0, staticResMods.get(0).getModificationSymbol());
        
        // check the static terminal modifications
        List<MsTerminalModification> staticTermMods = search.getStaticTerminalMods();
        assertEquals(2, staticTermMods.size());
        Collections.sort(staticTermMods, new Comparator<MsTerminalModification>(){
            @Override
            public int compare(MsTerminalModification o1,
                    MsTerminalModification o2) {
                return Integer.valueOf(o1.getId()).compareTo(Integer.valueOf(o2.getId()));
            }});
        assertEquals(Terminal.CTERM, staticTermMods.get(0).getModifiedTerminal());
        assertEquals(123.4567, staticTermMods.get(0).getModificationMass().doubleValue());
        assertEquals(0, staticTermMods.get(0).getModificationSymbol());
//        assertEquals(searchId, staticTermMods.get(0).getSearchId());
        
        assertEquals(Terminal.NTERM, staticTermMods.get(1).getModifiedTerminal());
        assertEquals(987.6543, staticTermMods.get(1).getModificationMass().doubleValue());
        assertEquals(0, staticTermMods.get(1).getModificationSymbol());
//        assertEquals(searchId, staticTermMods.get(1).getSearchId());
        
        // check the dynamic residue modifications
        List<MsResidueModification> dynaResMods = search.getDynamicResidueMods();
        assertEquals(6, dynaResMods.size());
        Collections.sort(dynaResMods, new Comparator<MsResidueModification>(){
            @Override
            public int compare(MsResidueModification o1,
                    MsResidueModification o2) {
                return Integer.valueOf(o1.getId()).compareTo(Integer.valueOf(o2.getId()));
            }});
        String modChars = "STYGVD";
        String s = "";
        for (int i = 0; i < dynaResMods.size(); i++)
            s += dynaResMods.get(i).getModifiedResidue();
        assertEquals(modChars, s);
        for (int i = 0; i < 3; i++) {
            assertEquals('*', dynaResMods.get(i).getModificationSymbol());
            assertEquals(79.9876, dynaResMods.get(i).getModificationMass().doubleValue());
        }
        for (int i = 3; i < 6; i++) {
            assertEquals('#', dynaResMods.get(i).getModificationSymbol());
            assertEquals(-99.9, dynaResMods.get(i).getModificationMass().doubleValue());
        }
        
        // check the dynamic terminal modifications
        assertEquals(0, search.getDynamicTerminalMods().size());
        
        // check sequest params
        List<Param> params = search.getSequestParams();
        String[] paramArr = new String[] {
                "database_name = /net/maccoss/vol2/software/pipeline/dbase/mouse-ipi-250309-contam.fasta",
                "peptide_mass_tolerance = 3.000",
                "create_output_files = 1",
                "ion_series = 0 1 1 0.0 1.0 0.0 0.0 0.0 0.0 0.0 1.0 0.0",
                "fragment_ion_tolerance = 0.0",
                "num_output_lines = 5",
                "num_description_lines = 3",
                "show_fragment_ions = 0",
                "print_duplicate_references = 1",
                "enzyme_number = 13",
                "xcorr_mode = 0",
                "print_expect_score = 1",
                "diff_search_options = +79.9876 STY -99.9 GVD 0.0 X",
                "max_num_differential_AA_per_mod = 0",
                "nucleotide_reading_frame = 0",
                "mass_type_parent = 0",
                "remove_precursor_peak = 1",
                "mass_type_fragment = 1",
                "ion_cutoff_percentage = 0.1",
                "match_peak_count = 0",
                "match_peak_allowed_error = 1",
                "match_peak_tolerance = 1.0",
                "max_num_internal_cleavage_sites = 1",
                "partial_sequence = ",
                "protein_mass_filter = 0 0",
                "sequence_header_filter =",
                "add_C_terminus = 123.4567",
                "add_N_terminus = 987.6543",
                "add_G_Glycine = 0.0000",
                "add_A_Alanine = 0.0000",
                "add_S_Serine = 0.0000",
                "add_P_Proline = 0.0000",
                "add_V_Valine = 0.0000",
                "add_T_Threonine = 0.0000",
                "add_C_Cysteine = 160.1390",
                "add_L_Leucine = 0.0000",
                "add_I_Isoleucine = 0.0000",
                "add_X_LorI = 0.0000",
                "add_N_Asparagine = 0.0000",
                "add_O_Ornithine = 0.0000",
                "add_B_avg_NandD = 0.0000",
                "add_D_Aspartic_Acid = 0.0000",
                "add_Q_Glutamine = 0.0000",
                "add_K_Lysine = 0.0000",
                "add_Z_avg_QandE = 0.0000",
                "add_E_Glutamic_Acid = 0.0000",
                "add_M_Methionine = 0.0000",
                "add_H_Histidine = 0.0000",
                "add_F_Phenyalanine = 0.0000",
                "add_R_Arginine = 0.0000",
                "add_Y_Tyrosine = 0.0000",
                "add_W_Tryptophan = 0.0000"
        };
        assertEquals(paramArr.length, params.size());
        for (int i = 0; i < params.size(); i++) {
            Param p = params.get(i);
            checkParam(p, paramArr[i]);
        }
    }
    
    private void checkParam(Param param, String origStr) {
       String[] tokens = origStr.trim().split("=");
       assertEquals(param.getParamName(), tokens[0].trim());
       if (tokens.length == 2)
           assertEquals(param.getParamValue(), tokens[1].trim());
       else
           assertEquals("", param.getParamValue());
    }
    
    // 2.sqt
    private void checkSecondRunSearch(int searchId, int runId) {
        SQTRunSearchDAO runSearchDao = DAOFactory.instance().getSqtRunSearchDAO();
        int runSearchId = runSearchDao.loadIdForRunAndSearch(runId, searchId);
        
        assertTrue(runSearchId != 0);
        SQTRunSearch runSearch = runSearchDao.loadRunSearch(runSearchId);
        assertNotNull(runSearch);
        assertEquals(runId, runSearch.getRunId());
        assertEquals(searchId, runSearch.getSearchId());
        assertEquals(runSearchId, runSearch.getId());
        assertEquals(SearchFileFormat.SQT_SEQ, runSearch.getSearchFileFormat());
        assertEquals(Program.SEQUEST, runSearch.getSearchProgram());
        // TODO check search date and search duration
        List<SQTHeaderItem> headerList = runSearch.getHeaders();
        assertEquals(23, headerList.size());
        
        // check headers. 
        String headerSec = 
                "H\tSQTGenerator\tSEQUEST\n"+ 
                "H\tSQTGeneratorVersion\t3.0\n"+ 
                "H\tComment\tSEQUEST was written by J Eng and JR Yates, III\n"+ 
                "H\tComment\tSEQUEST ref. J. Am. Soc. Mass Spectrom., 1994, v. 4, p. 976\n"+ 
                "H\tComment\tSEQUEST ref. Eng,J.K.; McCormack A.L.; Yates J.R.\n"+ 
                "H\tComment\tSEQUEST is licensed to Finnigan Corp.\n"+ 
                "H\tComment\tParalellization Program is run_ms2\n"+ 
                "H\tComment\trun_ms2 was written by Rovshan Sadygov\n"+ 
                "H\tStartTime\t01/29/2008, 03:34 AM\n"+ 
                "H\tEndTime\t01/29/2008, 06:21 AM\n"+ 
                "H\tDatabase\t/net/maccoss/vol2/software/pipeline/dbase/mouse-contam.fasta\n"+ 
                "H\tDBSeqLength\t96816536\n"+ 
                "H\tDBLocusCount\t271842\n"+ 
                "H\tPrecursorMasses\tAVG\n"+ 
                "H\tFragmentMasses\tMONO\n"+ 
                "H\tAlg-PreMassTol\t3.000\n"+ 
                "H\tAlg-FragMassTol\t0.0\n"+ 
                "H\tAlg-XCorrMode\t0\n"+ 
                "H\tStaticMod\tC=160.139\n"+ 
                "H\tDiffMod\tSTY*=+80.000 \n"+ 
                "H\tAlg-MaxDiffMod\t3H\tAlg-DisplayTop\t5\n"+ 
                "H\tAlg-IonSeries\t0 1 1 0.0 1.0 0.0 0.0 0.0 0.0 0.0 1.0 0.0\n"+ 
                "H\tEnzymeSpec\tNo_Enzyme";
        String[] tokens = headerSec.split("\\n");
        assertEquals(tokens.length, headerList.size());
        for (int i = 0; i < tokens.length; i++) {
            SQTHeaderItem header = headerList.get(i);
            assertEquals(tokens[i].trim(), "H\t"+header.getName()+"\t"+header.getValue());
        }
        
        // check search results
        SequestSearchResultDAO seqResDao = DAOFactory.instance().getSequestResultDAO();
        List<Integer> resultIds = seqResDao.loadResultIdsForRunSearch(runSearchId);
        assertEquals(8, resultIds.size());
        
        
        // spectrum data
        SQTSearchScanDAO searchScanDao = DAOFactory.instance().getSqtSpectrumDAO();
        // S       00023   00023   1       22      shamu048        866.46000       1892.2  56.4    4716510
        int scanId = this.scanDao.loadScanIdForScanNumRun(23, runId);
        assertEquals(23, this.scanDao.load(scanId).getStartScanNum());
        SQTSearchScan scan = searchScanDao.load(runSearchId, scanId, 1, new BigDecimal("866.46000"));
        assertNotNull(scan);
        assertEquals(scanId, scan.getScanId());
        assertEquals(1, scan.getCharge());
        assertEquals(runSearchId, scan.getRunSearchId());
        assertEquals(22, scan.getProcessTime());
        assertEquals("shamu048", scan.getServerName());
//        assertEquals(866.46, scan.getObservedMass().doubleValue());
        assertEquals(1892.2, scan.getTotalIntensity().doubleValue());
        assertEquals(56.4, scan.getLowestSp().doubleValue());
        assertEquals(4716510, scan.getSequenceMatches());
        // check results for this scan + charge combination
        resultIds = seqResDao.loadResultIdsForSearchScanCharge(runSearchId, scanId, 1);
        assertEquals(4, resultIds.size()); // number of results for this scan + charge combination
        Collections.sort(resultIds);
        // M         1       4      866.96470      0.00000  1.1529   3.137   9     14          L.S*D#MSASRT*Y*.T   U
        SequestSearchResult res = seqResDao.load(resultIds.get(0));
        assertEquals(1, res.getCharge());
        assertEquals(scanId, res.getScanId());
        assertEquals(866.46, res.getObservedMass().doubleValue());
        assertEquals(runSearchId, res.getRunSearchId());
        assertEquals(1, res.getSequestResultData().getxCorrRank());
        assertEquals(4, res.getSequestResultData().getSpRank());
        assertEquals(866.9647, res.getSequestResultData().getCalculatedMass().doubleValue());
        assertEquals(0.0, res.getSequestResultData().getDeltaCN().doubleValue());
        assertEquals(1.1529, res.getSequestResultData().getxCorr().doubleValue());
        assertEquals(3.137, res.getSequestResultData().getEvalue().doubleValue());
        assertNull(res.getSequestResultData().getSp());
        assertEquals(9, res.getSequestResultData().getMatchingIons());
        assertEquals(14, res.getSequestResultData().getPredictedIons());
        assertEquals(ValidationStatus.UNVALIDATED, res.getValidationStatus());
        assertEquals('L', res.getResultPeptide().getPreResidue());
        assertEquals('T', res.getResultPeptide().getPostResidue());
        assertEquals("SDMSASRTY", res.getResultPeptide().getPeptideSequence());
        List<MsResultResidueMod> resMods = res.getResultPeptide().getResultDynamicResidueModifications();
        List<MsResultTerminalMod> termMods = res.getResultPeptide().getResultDynamicTerminalModifications();
        assertEquals(4, resMods.size());
        assertEquals(0, termMods.size());
        Collections.sort(resMods, new Comparator<MsResultResidueMod>(){
            public int compare(MsResultResidueMod o1,
                    MsResultResidueMod o2) {
                return Integer.valueOf(o1.getModifiedPosition()).compareTo(Integer.valueOf(o2.getModifiedPosition()));
            }});
        assertEquals(0, resMods.get(0).getModifiedPosition());
        assertEquals('S', resMods.get(0).getModifiedResidue());
        assertEquals('*', resMods.get(0).getModificationSymbol());
        assertEquals(79.9876, resMods.get(0).getModificationMass().doubleValue());
        assertEquals(1, resMods.get(1).getModifiedPosition());
        assertEquals('D', resMods.get(1).getModifiedResidue());
        assertEquals('#', resMods.get(1).getModificationSymbol());
        assertEquals(-99.9, resMods.get(1).getModificationMass().doubleValue());
        assertEquals(7, resMods.get(2).getModifiedPosition());
        assertEquals('T', resMods.get(2).getModifiedResidue());
        assertEquals('*', resMods.get(2).getModificationSymbol());
        assertEquals(79.9876, resMods.get(2).getModificationMass().doubleValue());
        assertEquals(8, resMods.get(3).getModifiedPosition());
        assertEquals('Y', resMods.get(3).getModifiedResidue());
        assertEquals('*', resMods.get(3).getModificationSymbol());
        assertEquals(79.9876, resMods.get(3).getModificationMass().doubleValue());
        
        //M         2     200      865.91874      0.0311   1.117    2.953   8     16        T.SG#TSSAS*LR.K      V
        res = seqResDao.load(resultIds.get(1));
        assertEquals(1, res.getCharge());
        assertEquals(scanId, res.getScanId());
        assertEquals(866.46, res.getObservedMass().doubleValue());
        assertEquals(runSearchId, res.getRunSearchId());
        assertEquals(2, res.getSequestResultData().getxCorrRank());
        assertEquals(200, res.getSequestResultData().getSpRank());
        assertEquals(865.91874, res.getSequestResultData().getCalculatedMass().doubleValue());
        assertEquals(0.0311, res.getSequestResultData().getDeltaCN().doubleValue());
        assertEquals(1.117, res.getSequestResultData().getxCorr().doubleValue());
        assertEquals(2.953, res.getSequestResultData().getEvalue().doubleValue());
        assertEquals(8, res.getSequestResultData().getMatchingIons());
        assertEquals(16, res.getSequestResultData().getPredictedIons());
        assertEquals(ValidationStatus.VALID, res.getValidationStatus());
        assertEquals('T', res.getResultPeptide().getPreResidue());
        assertEquals('K', res.getResultPeptide().getPostResidue());
        assertEquals("SGTSSASLR", res.getResultPeptide().getPeptideSequence());
        resMods = res.getResultPeptide().getResultDynamicResidueModifications();
        termMods = res.getResultPeptide().getResultDynamicTerminalModifications();
        assertEquals(2, resMods.size());
        assertEquals(0, termMods.size());
        Collections.sort(resMods, new Comparator<MsResultResidueMod>(){
            public int compare(MsResultResidueMod o1,
                    MsResultResidueMod o2) {
                return Integer.valueOf(o1.getModifiedPosition()).compareTo(Integer.valueOf(o2.getModifiedPosition()));
            }});
        assertEquals(1, resMods.get(0).getModifiedPosition());
        assertEquals('G', resMods.get(0).getModifiedResidue());
        assertEquals('#', resMods.get(0).getModificationSymbol());
        assertEquals(-99.9, resMods.get(0).getModificationMass().doubleValue());
        assertEquals(6, resMods.get(1).getModifiedPosition());
        assertEquals('S', resMods.get(1).getModifiedResidue());
        assertEquals('*', resMods.get(1).getModificationSymbol());
        assertEquals(79.9876, resMods.get(1).getModificationMass().doubleValue());
        
        
        
        // S       00020   00020   1       22      shamu049        807.67000       2681.7  95.3    5138490
        scanId = this.scanDao.loadScanIdForScanNumRun(20, runId);
        assertEquals(20, this.scanDao.load(scanId).getStartScanNum());
        assertEquals(2, seqResDao.loadResultIdsForSearchScanCharge(runSearchId, scanId, 1).size()); // number of results for this scan + charge combination
        scan = searchScanDao.load(runSearchId, scanId, 1, new BigDecimal("807.67000"));
        assertNotNull(scan);
        assertEquals(scanId, scan.getScanId());
        assertEquals(1, scan.getCharge());
        assertEquals(runSearchId, scan.getRunSearchId());
        assertEquals(22, scan.getProcessTime());
        assertEquals("shamu049", scan.getServerName());
//        assertEquals(807.67, scan.getObservedMass().doubleValue());
        assertEquals(2681.7, scan.getTotalIntensity().doubleValue());
        assertEquals(95.3, scan.getLowestSp().doubleValue());
        assertEquals(5138490, scan.getSequenceMatches());
        
        // NOTE (03/29/09)  NO LONGER ACCEPTING SCANS WITH NO RESULTS
        // THIS LINE HAS BEEN REMOVED FROM THE SQT
        // S       00010   00010   1       23      shamu050        717.62000       4000.6  111.6   5928764
        scanId = this.scanDao.loadScanIdForScanNumRun(10, runId);
        assertEquals(10, this.scanDao.load(scanId).getStartScanNum());
        assertEquals(0, seqResDao.loadResultIdsForSearchScanCharge(runSearchId, scanId, 1).size()); // number of results for this scan + charge combination
        scan = searchScanDao.load(runSearchId, scanId, 1, new BigDecimal("717.62000"));
        assertNull(scan);
//        assertNotNull(scan);
//        assertEquals(scanId, scan.getScanId());
//        assertEquals(1, scan.getCharge());
//        assertEquals(runSearchId, scan.getRunSearchId());
//        assertEquals(23, scan.getProcessTime());
//        assertEquals("shamu050", scan.getServerName());
////        assertEquals(717.62, scan.getObservedMass().doubleValue());
//        assertEquals(4000.6, scan.getTotalIntensity().doubleValue());
//        assertEquals(111.6, scan.getLowestSp().doubleValue());
//        assertEquals(5928764, scan.getSequenceMatches());

        // S       00026   00026   1       23      shamu048        817.33000       2044.4  69.6    5697304
        scanId = this.scanDao.loadScanIdForScanNumRun(26, runId);
        assertEquals(26, this.scanDao.load(scanId).getStartScanNum());
        assertEquals(2, seqResDao.loadResultIdsForSearchScanCharge(runSearchId, scanId, 1).size()); // number of results for this scan + charge combination
        scan = searchScanDao.load(runSearchId, scanId, 1, new BigDecimal("817.33000"));
        assertNotNull(scan);
        assertEquals(scanId, scan.getScanId());
        assertEquals(1, scan.getCharge());
        assertEquals(runSearchId, scan.getRunSearchId());
        assertEquals(23, scan.getProcessTime());
        assertEquals("shamu048", scan.getServerName());
//        assertEquals(817.33, scan.getObservedMass().doubleValue());
        assertEquals(2044.4, scan.getTotalIntensity().doubleValue());
        assertEquals(69.6, scan.getLowestSp().doubleValue());
        assertEquals(5697304, scan.getSequenceMatches());

    }
    
    private int getRunId(String runFileName) {
        List<Integer> runIds = runDao.loadRunIdsForFileName(runFileName);
        assertEquals(1, runIds.size());
        int runId = runIds.get(0);
        assertNotSame(0, runId);
        return runId;
    }
}
