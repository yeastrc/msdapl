package org.yeastrc.ms.service;


import java.util.Date;
import java.util.List;

import org.yeastrc.ms.dao.BaseDAOTestCase;
import org.yeastrc.ms.parser.DataProviderException;
import org.yeastrc.ms.service.UploadException.ERROR_CODE;

public class MsDataUploaderTest extends BaseDAOTestCase {

    private MsDataUploader uploader = null;
    
    protected void setUp() throws Exception {
        super.setUp();
        uploader = new MsDataUploader();
        resetDatabase();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }
   
    public void testUploadNoResultsForScan() {
        String dir = "test_resources/invalidSQT_noResultsForScan_dir";
//        try {
            uploader.setSpectrumDataDirectory(dir);
            uploader.setSearchDirectory(dir);
            uploader.setSearchDate(new Date());
            uploader.setRemoteServer("remoteServer");
            uploader.uploadData();
            // This does not cause exception anymore.
            assertEquals(0, uploader.getUploadExceptionList().size());
            // assertEquals(ERROR_CODE.INVALID_SQT_SCAN, uploader.getUploadExceptionList().get(0).getErrorCode());
//        }
//        catch(UploadException e) {
//            fail("Invalid scan+charge in 1.sqt (it does not have any M or L lines; but we don't care");
//        }
    }
    
    public void testUploadNoProteinsForResult() {
        String dir = "test_resources/invalidSQT_noProteinsForResult_dir";
//        try {
        uploader.setSpectrumDataDirectory(dir);
        uploader.setSearchDirectory(dir);
        uploader.setSearchDate(new Date());
        uploader.setRemoteServer("remoteServer");
        uploader.uploadData();
            // will not fail since the exception will not be propagated all the way up
//        }
//        catch(UploadException e) {
//            fail("ms2 ddata is valid and no unsupported sqt files found");
//        }
        assertEquals(1, uploader.getUploadExceptionList().size());
        assertEquals(ERROR_CODE.INVALID_SQT_SCAN, uploader.getUploadExceptionList().get(0).getErrorCode());
//        assertTrue(uploader.getUploadWarnings().contains("Invalid 'M' line.  No locus matches found"));
    }
    
    public void testUploadDataToDbInvalidDirectory() {
        String dir = "dummy/directory";
//        try {
        uploader.setSpectrumDataDirectory(dir);
        uploader.setSearchDirectory(dir);
        uploader.setSearchDate(new Date());
        uploader.setRemoteServer("remoteServer");
        uploader.uploadData();
            //fail("Directory "+dir+" does not exist");
//        }
//        catch(UploadException e) {
            assertEquals(1, uploader.getUploadExceptionList().size());
            UploadException e = uploader.getUploadExceptionList().get(0);
            assertEquals(ERROR_CODE.PREUPLOAD_CHECK_FALIED, e.getErrorCode());
            String warning = "ERROR: Pre-upload check failed"+
	                         "\n\tError getting SpectrumDataUploadService: dataDirectory does not exist: dummy/directory";
            assertEquals(warning, uploader.getUploadWarnings().trim());
//        }
    }

    public void testUploadExperimentToDbEmptyDirectory() {
        String dir = "test_resources/empty_dir";
        int expId = 0;
//        try {
        uploader.setSpectrumDataDirectory(dir);
        uploader.setSearchDirectory(dir);
        uploader.setSearchDate(new Date());
        uploader.setRemoteServer("remoteServer");
        uploader.uploadData();
            expId = uploader.getUploadedExperimentId();
            //fail("Upload directory is empty");
//        }
//        catch (UploadException e) {
            assertEquals(1, uploader.getUploadExceptionList().size());
            UploadException e = uploader.getUploadExceptionList().get(0);
            assertEquals(ERROR_CODE.PREUPLOAD_CHECK_FALIED, e.getErrorCode());
//        }
        assertEquals(0, expId);
    }

    public void testUploadExperimentToDbMissingMS2Files() {
        
        String dir = "test_resources/missingMS2_dir";
        int expId = 0;
//        try {
        uploader.setSpectrumDataDirectory(dir);
        uploader.setSearchDirectory(dir);
        uploader.setSearchDate(new Date());
        uploader.setRemoteServer("remoteServer");
        uploader.uploadData();
            expId = uploader.getUploadedExperimentId();
//            fail("Upload directory has missing ms2 files");
//        }
//        catch (UploadException e) {
            assertEquals(1, uploader.getUploadExceptionList().size());
            UploadException e = uploader.getUploadExceptionList().get(0);
            assertEquals(ERROR_CODE.PREUPLOAD_CHECK_FALIED, e.getErrorCode());
            String warning = "ERROR: Pre-upload check failed"+  
            	             "\n\tNo corresponding spectrum data file found for: two";
            assertEquals(warning, uploader.getUploadWarnings().trim());
            
//        }
        assertEquals(0, expId);
    }
    
    public void testUploadInvalidMS2_S() {
        String dir = "test_resources/invalid_ms2_S_dir";
//        try {
        uploader.setSpectrumDataDirectory(dir);
        uploader.setRemoteServer("remoteServer");
        uploader.uploadData();
//            fail("2.ms2 is invalid");
//        }
//        catch (UploadException e1) {
            assertEquals(1, uploader.getUploadExceptionList().size());
            UploadException e1 = uploader.getUploadExceptionList().get(0);
            assertEquals(ERROR_CODE.INVALID_MS2_SCAN, e1.getErrorCode());
            String msg = "Invalid 'S' line. Expected 4 fields.\n\tLINE NUMBER: 43";
            System.out.println(e1.getMessage());
//            assertTrue(e1.getMessage().contains(msg));
//        }
        assertNull(runDao.loadRun(1));
        assertNull(searchDao.loadSearch(1));
    }
    
    public void testUploadInvalidMS2_peak() {
        String dir = "test_resources/invalid_ms2_peak_dir";
//        try {
        uploader.setSpectrumDataDirectory(dir);
        uploader.setSearchDirectory(dir);
        uploader.setSearchDate(new Date());
        uploader.setRemoteServer("remoteServer");
        uploader.uploadData();
            // fail("1.ms2 is invalid");
//        }
//        catch (UploadException e1) {
            // We are no longer throwing exception for scan with missing peak data.
//            fail("1.ms2 is valid");
//            assertEquals(ERROR_CODE.INVALID_MS2_SCAN, e1.getErrorCode());
//            String msg = "Invalid MS2 scan -- no valid peaks and/or charge states found for scan: 11"+
//            "\n\tLINE NUMBER: 61\n\tLINE: S\t000012\t000012\t1394.58000";
//            System.out.println(e1.getMessage());
//            assertTrue(e1.getMessage().contains(msg));
//        }
        assertEquals(1, runDao.loadRunIdsForFileName("1").size());
        assertEquals(1, runDao.loadRunIdsForFileName("2").size());
        assertNotNull(searchDao.loadSearch(1));
    }
    
    public void testUploadInvalidMS2_Z() {
        String dir = "test_resources/invalid_ms2_Z_dir";
        uploader.setSpectrumDataDirectory(dir);
        uploader.setSearchDirectory(dir);
        uploader.setSearchDate(new Date());
        uploader.setRemoteServer("remoteServer");
        uploader.uploadData();
        //fail("1.ms2 is invalid");
        assertEquals(1, uploader.getUploadExceptionList().size());
        UploadException e1 = uploader.getUploadExceptionList().get(0);
        
        assertEquals(ERROR_CODE.INVALID_MS2_SCAN, e1.getErrorCode());
        String msg = "Invalid 'Z' line.\n\tLINE NUMBER: 60\n\tLINE: Z\t1\t1394.58 invalid Z line";
        System.out.println(e1.getMessage());
        assertTrue(e1.getMessage().contains(msg));
        
        assertEquals(0, runDao.loadRunIdsForFileName("1").size());
        assertEquals(0, runDao.loadRunIdsForFileName("2").size()); // If 2.ms2 is uploaded first this will be 1
        assertNull(searchDao.loadSearch(1));
    }
    
    public void testUploadInvalidSQTFiles() {
        
        
      String dir = "test_resources/invalid_sqt_dir";
      uploader.setSpectrumDataDirectory(dir);
      uploader.setSearchDirectory(dir);
      uploader.setSearchDate(new Date());
      uploader.setRemoteServer("remoteServer");
      uploader.uploadData();
      List<UploadException> exceptionList = uploader.getUploadExceptionList();
      assertEquals(1, exceptionList.size());
      assertEquals(ERROR_CODE.PREUPLOAD_CHECK_FALIED, exceptionList.get(0).getErrorCode());
      String warnings = "ERROR: Pre-upload check failed"+
                          "\n\tError getting SearchDataUploadService: We do not currently have support for the SQT format: UNKNOWN";
      
      assertEquals(warnings, uploader.getUploadWarnings().trim());
      // Error happened at the pre-upload check. No files should been uploaded. 
      List<Integer> runIds = runDao.loadRunIdsForFileName("percolator.ms2");
      assertEquals(0, runIds.size());
      runIds = runDao.loadRunIdsForFileName("pepprobe.ms2");
      assertEquals(0, runIds.size());
      runIds = runDao.loadRunIdsForFileName("prolucid.ms2");
      assertEquals(0, runIds.size());
    }
    
    public void testUploadSequestData() throws DataProviderException {
        
        
        String dir = "test_resources/validSequestData_dir";
        
//        String dbName = "/net/maccoss/vol2/software/pipeline/dbase/mouse-contam.fasta";
//        Set<String> uniqueAccessions = new HashSet<String>();
//        
//        // parse the sequest params file:
//        SequestParamsParser parser = new SequestParamsParser("remote.server");
//        parser.parseParamsFile(dir+File.separator+"sequest.params");
//        
//        int dbId = NrSeqLookupUtil.getDatabaseId(parser.getSearchDatabase().getServerPath());
//        assertEquals(3, dbId);
//        
//        int proteinId = 1;
//        StringBuilder buf = new StringBuilder();
//        buf.append("INSERT INTO tblProteinDatabase (proteinID, databaseID, accessionString, isCurrent) VALUES ");
//        
//        
//        List<MsResidueModification> dynaResidueMods = new ArrayList<MsResidueModification>();
//        dynaResidueMods.add(new ResidueModification('G', BigDecimal.ZERO, '#'));
//        dynaResidueMods.add(new ResidueModification('V', BigDecimal.ZERO, '#'));
//        dynaResidueMods.add(new ResidueModification('D', BigDecimal.ZERO, '#'));
//        dynaResidueMods.add(new ResidueModification('S', BigDecimal.ZERO, '*'));
//        dynaResidueMods.add(new ResidueModification('T', BigDecimal.ZERO, '*'));
//        dynaResidueMods.add(new ResidueModification('Y', BigDecimal.ZERO, '*'));
//        
//        List<MsResidueModification> modsFromParams = parser.getDynamicResidueMods();
//        assertEquals(dynaResidueMods.size(), modsFromParams.size());
//        
//        Comparator<MsResidueModification> comp = new Comparator<MsResidueModification>() {
//
//            public int compare(MsResidueModification o1,
//                    MsResidueModification o2) {
//                return Character.valueOf(o1.getModificationSymbol()).compareTo(Character.valueOf(o2.getModificationSymbol()));
//            }};
//        Collections.sort(dynaResidueMods, comp);
//        Collections.sort(modsFromParams, comp);
//        for (int i = 0; i < dynaResidueMods.size(); i++) {
//            MsResidueModification m1 = dynaResidueMods.get(i);
//            MsResidueModification m2 = modsFromParams.get(i);
//            assertEquals(m1.getModifiedResidue(), m2.getModifiedResidue());
//            assertEquals(m2.getModificationMass(), m2.getModificationMass());
//            assertEquals(m1.getModificationSymbol(), m2.getModificationSymbol());
//        }
//        
//        // read the first file.
//        SequestSQTFileReader reader = new SequestSQTFileReader();
//        reader.open(dir+File.separator+"1.sqt");
//        reader.setDynamicResidueMods(modsFromParams);
//        reader.getSearchHeader();
//        while (reader.hasNextSearchScan()) {
//            SequestSearchScan scan = reader.getNextSearchScan();
//            for (SequestSearchResult result : scan.getScanResults()) {
//                for (MsSearchResultProtein pr : result.getProteinMatchList()) {
//                    uniqueAccessions.add(pr.getAccession());
//                    
//                }
//            }
//        }
//        reader.close();
//        
//        for (String acc: uniqueAccessions) {
//            buf.append("("+proteinId+", "+dbId+", \""+acc+"\", 'T'),");
//            proteinId++;
//        }
//        
//        // read the second file
//        reader = new SequestSQTFileReader();
//        reader.open(dir+File.separator+"2.sqt");
//        reader.setDynamicResidueMods(modsFromParams);
//        reader.getSearchHeader();
//        while (reader.hasNextSearchScan()) {
//            SequestSearchScan scan = reader.getNextSearchScan();
//            for (SequestSearchResult result : scan.getScanResults()) {
//                for (MsSearchResultProtein pr : result.getProteinMatchList()) {
//                    proteinId++;
//                    buf.append("("+proteinId+", "+dbId+", \""+pr.getAccession()+"\", 'T'),");
//                }
//            }
//        }
//        reader.close();
//        
//        buf.deleteCharAt(buf.length() -1); // delete last comma
//        System.out.println(buf.toString());
        
        
        MsDataUploader uploader = new MsDataUploader();
        uploader.setSpectrumDataDirectory(dir);
        uploader.setSearchDirectory(dir);
        uploader.setSearchDate(new Date());
        uploader.setRemoteServer("remoteServer");
        uploader.uploadData();
        assertEquals(0, uploader.getUploadExceptionList().size());
        
        // make sure all the data got uploaded
    }
    
    
//    public void testUploadValidData() {
//        String dir = "test_resources/validData_dir";
//        int expId = 0;
//        try {
//            expId = uploader.uploadExperimentToDb("remoteServer", "remoteDirectory", dir, new Date(), false);
//        }
//        catch (UploadException e1) {
//            fail("Data is valid. Error message: "+e1.getMessage());
//        }
//        assertEquals(1, expId);
//        
//        // read from database and make sure files are identical (MS2 files)
//        String outputTest = "test_resources/validData_dir/fromDb.ms2";
//        // remove the output if it already exists
//        new File(outputTest).delete();
//        DbToMs2FileConverter ms2Converter = new DbToMs2FileConverter();
//        // compare the first ms2 file uploaded
//        try {
//            ms2Converter.convertToMs2(1, outputTest);
//        }
//        catch (IOException e) {
//            e.printStackTrace();
//        }
//        assertTrue(filesIdentical(outputTest, dir+File.separator+"771_5489.ms2"));
//        // remove the output file
//        assertTrue(new File(outputTest).delete());
//        // compare the second ms2 file uploaded
//        try {
//            ms2Converter.convertToMs2(2, outputTest);
//        }
//        catch (IOException e) {
//            e.printStackTrace();
//        }
//        assertTrue(filesIdentical(outputTest, dir+File.separator+"PARC_p75_01_itms.ms2"));
//        // remove the output file
//        assertTrue(new File(outputTest).delete());
//        
//        // read from database and make sure files are identical (SQT files)
//        outputTest = "test_resources/validData_dir/fromDb.sqt";
//        // remove the output if it already exists
//        new File(outputTest).delete();
//        DbToSqtFileConverter sqtConverter = new DbToSqtFileConverter();
//        // compare the first sqt file uploaded
//        try {
//            sqtConverter.convertToSqt(1, outputTest);
//        }
//        catch (IOException e) {
//            e.printStackTrace();
//        }
//        assertTrue(filesIdentical(outputTest, dir+File.separator+"771_5489.sqt"));
//        // remove the output file
//        assertTrue(new File(outputTest).delete());
//        // compare the second sqt file uploaded
//        try {
//            sqtConverter.convertToSqt(2, outputTest);
//        }
//        catch (IOException e) {
//            e.printStackTrace();
//        }
//        assertTrue(filesIdentical(outputTest, dir+File.separator+"PARC_p75_01_itms.sqt"));
//        // remove the output file
//        assertTrue(new File(outputTest).delete());
//        
//        
//        // upload the same experiment.  This time the MS2 file should not be uploaded
//        // we should have a new experiment id and there should be two entries in the msExperimentRun
//        // table for the runs uploaded before.
//        List<Integer> runIds = expDao.loadRunIdsForExperiment(expId);
//        int expId2 = 0;
//        try {
//            expId2 = uploader.uploadExperimentToDb("remoteServer", "remoteDirectory", dir, new Date(), false);
//        }
//        catch (UploadException e) {
//            fail("Data is valid. Error message: "+e.getMessage());
//        }
//        assertEquals(2, expId2);
//        
//        for (Integer runId: runIds) {
//            List<Integer> expIds = expDao.loadExperimentIdsForRun(runId);
//            assertEquals(2, expIds.size());
//            Collections.sort(expIds);
//            assertEquals(expId, expIds.get(0).intValue());
//            assertEquals(expId2, expIds.get(1).intValue());
//        }
//    }


    
//    public void testUploadExperimentInvalidSQTHeader() {
//        String dir = "test_resources/invalidSQTHeader_dir";
//        try {
//            uploader.uploadExperimentToDb("remoteServer", "remoteDirectory", dir, new Date());
//        }
//        catch (UploadException e) {
//            fail("Valid ms2 file in directory");
//        }
//        
//        List<UploadException> exceptions = uploader.getUploadExceptionList();
//        assertEquals(1, exceptions.size());
//        
//        assertEquals(1, exceptions.size());
//        List<Integer> runIds = runDao.loadRunIdsForFileName("1437_9274.ms2");
//        assertEquals(1, runIds.size());
//        assertEquals(0, runSearchDao.loadRunSearchIdsForRun(runIds.get(0)).size());
//    }
    
    public void testUploadExperimntNoScanIdFound() {
        
        
        String dir = "test_resources/noScanIdFound_dir";
        uploader.setSpectrumDataDirectory(dir);
        uploader.setSearchDirectory(dir);
        uploader.setSearchDate(new Date());
        uploader.setRemoteServer("remoteServer");
        uploader.uploadData();
        
        List<UploadException> exceptions = uploader.getUploadExceptionList();
        assertEquals(1, exceptions.size());
//        System.out.println(uploader.getUploadWarnings());
        assertEquals(ERROR_CODE.NO_SCANID_FOR_SCAN, exceptions.get(0).getErrorCode());
        
        assertEquals(1, runDao.loadRunIdsForFileName("771_5489").size());
        assertNull(searchDao.loadSearch(1));
    }

        
//    public void testDeleteExperiment() {
//        String exp1Dir = "test_resources/deleteExperiment_dir/one"; //has ONE ms2, sqt pair
//        String exp2Dir = "test_resources/deleteExperiment_dir/two"; //has TWO ms2, sqt pair (one of them is the same as above)
//        
//        int expID1 = 0;
//        int expID2 = 0;
//        try {
//            expID1 = uploader.uploadExperimentToDb("remoteServer", "remoteDirectory", exp1Dir, new Date(), false);
//            expID2 = uploader.uploadExperimentToDb("remoteServer", "remoteDirectory", exp2Dir, new Date(), false);
//        }
//        catch (UploadException e) {
//            fail("Valid files in both directories. Error is: "+e.getMessage());
//        }
//        
//        assertEquals(1, expID1);
//        assertEquals(2, expID2);
//        
//        // make sure everything got uploaded
//        assertEquals(1, searchDao.loadRunSearchIdsForSearch(expID1).size());
//        List<MsRunDb> runs1 = runDao.loadExperimentRuns(expID1);
//        assertEquals(1, runs1.size());
//        
//        assertEquals(2, searchDao.loadRunSearchIdsForSearch(expID2).size());
//        List<MsRunDb> runs2 = runDao.loadExperimentRuns(expID2);
//        assertEquals(2, runs2.size());
//        
//        Set<Integer> distinctRunIds = new HashSet<Integer>();
//        for (MsRunDb run: runs1)
//            distinctRunIds.add(run.getId());
//        for (MsRunDb run: runs2)
//            distinctRunIds.add(run.getId());
//        assertEquals(2, distinctRunIds.size());
//        
//        // delete the second experiment
////        uploader.deleteExperiment(expID2);
////        
////        // make sure run common to both experiments is still there
////        List<MsRunDb> runs = runDao.loadExperimentRuns(expID1);
////        assertEquals(1, runs.size());
////        assertEquals("771_5489.ms2", runs.get(0).getFileName());
////        
////        // make sure runs for deleted experiment are gone
////        assertEquals(0, runDao.loadExperimentRuns(expID2).size());
////        assertEquals(0, searchDao.loadSearchIdsForExperiment(expID2).size());
//    }
//    
//    public void testCheckNonSqtFilesFirst() {
//        String dir = "test_resources/invalid_sqt_dir";
//        try {
//            uploader.uploadExperimentToDb("remoteServer", "remoteDirectory", dir, new Date(), true);
//            fail("We care checking for non-sequest SQT's first. We have those in the directory");
//        }
//        catch (UploadException e) {
//            assertEquals(ERROR_CODE.UNSUPPORTED_SQT, e.getErrorCode());
//        }
//        // make sure nothing got uploaded
//        assertEquals(0, expDao.selectAllExperimentIds().size());
//    }
//    
//    private boolean filesIdentical(String output, String input) {
//        BufferedReader orig = null;
//        BufferedReader fromDb = null;
//        try {
//           orig = new BufferedReader(new FileReader(input)); 
//           fromDb = new BufferedReader(new FileReader(output));
//           String origL = orig.readLine();
//           String fromDbL = fromDb.readLine();
//           while(origL != null) {
//               origL = origL.trim().replaceAll("\\s+", " ");
//               fromDbL = fromDbL.trim().replaceAll("\\s+", " ");
//               assertEquals(origL, fromDbL);
//               origL = orig.readLine();
//               fromDbL = fromDb.readLine();
//           }
//        }
//        catch (IOException e) {
//            e.printStackTrace();
//            return false;
//            
//        }
//        finally {
//            try {
//                orig.close();
//                fromDb.close();
//            }
//            catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//        return true;
//    }
}
