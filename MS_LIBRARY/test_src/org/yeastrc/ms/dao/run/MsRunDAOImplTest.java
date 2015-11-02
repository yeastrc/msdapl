package org.yeastrc.ms.dao.run;

import java.util.ArrayList;
import java.util.List;

import org.yeastrc.ms.dao.BaseDAOTestCase;
import org.yeastrc.ms.domain.general.MsEnzyme;
import org.yeastrc.ms.domain.general.MsEnzymeIn;
import org.yeastrc.ms.domain.run.MsRun;
import org.yeastrc.ms.domain.run.MsRunIn;
import org.yeastrc.ms.domain.run.MsRunLocation;
import org.yeastrc.ms.domain.run.RunFileFormat;

public class MsRunDAOImplTest extends BaseDAOTestCase {

    
    protected void setUp() throws Exception {
        super.setUp();
        resetDatabase();
        addEnzymes();
    }
    
    protected void tearDown() throws Exception {
        super.tearDown();
    }
    
    public void testSaveLoadAndDelete() {
        MsRunIn run = createDefaultRun();
        int runId = runDao.saveRun(run, "remoteDirectory");
        MsRun runDb = runDao.loadRun(runId);
        checkRun(run, runDb);
        runDao.delete(runId);
        assertNull(runDao.loadRun(runId));
    }
    
    public void testSaveAndLoadRunFileFormats() {
        MsRunIn run = createRunForFormat(RunFileFormat.MS2);
        int id1 = runDao.saveRun(run, "remoteDirectory");
        MsRun runDb = runDao.loadRun(id1);
        assertEquals(RunFileFormat.MS2, runDb.getRunFileFormat());
        
        run = createRunForFormat(RunFileFormat.UNKNOWN);
        int id2 = runDao.saveRun(run, "remoteDirectory");
        runDb = runDao.loadRun(id2);
        assertEquals(RunFileFormat.UNKNOWN, runDb.getRunFileFormat());
        
        run = createRunForFormat(null);
        int id3 = runDao.saveRun(run, "remoteDirectory");
        runDb = runDao.loadRun(id3);
        assertEquals(RunFileFormat.UNKNOWN, runDb.getRunFileFormat());
        
        runDao.delete(id1);
        runDao.delete(id2);
        runDao.delete(id3);
        assertNull(runDao.loadRun(id1));
        assertNull(runDao.loadRun(id2));
        assertNull(runDao.loadRun(id3));
    }
    
    public void testLoadRunsForFileNameAndSha1Sum() {
        MsRunIn run = createDefaultRun();
        int id1 = runDao.saveRun(run, "remoteDirectory");
        run = createDefaultRun();
        int id2 = runDao.saveRun(run, "remoteDirectory");
        
//        List<Integer> runs = runDao.loadRunIdForFileNameAndSha1Sum(run.getFileName(), run.getSha1Sum());
//        assertEquals(2, runs.size());
        try {
            runDao.loadRunIdForFileNameAndSha1Sum(run.getFileName(), run.getSha1Sum());
            fail("Multiple entries with same filename and sha1sum -- should not happen in the real application");
        }
        catch(Exception e) {
            assertEquals(e.getMessage(), "Failed to execute select statement: MsRun.selectRunIdsForFileNameAndSha1Sum");
        }
        
        runDao.delete(id1);
        runDao.delete(id2);
        assertNull(runDao.loadRun(id1));
        assertNull(runDao.loadRun(id2));
    }
    
    public void testSaveAndLoadRunWithNoEnzymes() {
        // create a run and save it
        int runId = runDao.saveRun(createDefaultRun(), "remoteDirectory");
        
        // read back the run
        MsRun dbRun = runDao.loadRun(runId);
        assertEquals(0, dbRun.getEnzymeList().size());
        runDao.delete(runId);
        assertNull(runDao.loadRun(runId));
    }
    
    public void testSaveAndLoadRunWithEnzymeInfo() {
        
        // load some enzymes from the database
        MsEnzyme enzyme1 = enzymeDao.loadEnzyme(1);
        MsEnzyme enzyme2 = enzymeDao.loadEnzyme(2);
        MsEnzyme enzyme3 = enzymeDao.loadEnzyme(3);
        
        assertNotNull(enzyme1);
        assertNotNull(enzyme2);
        assertNotNull(enzyme3);
        
        // create a run with enzyme information
        List <MsEnzymeIn> enzymeList1 = new ArrayList<MsEnzymeIn>(2);
        enzymeList1.add(enzyme1);
        enzymeList1.add(enzyme2);
        MsRunIn run1 = createRunWEnzymeInfo(enzymeList1);
        
        // save the run
        int runId_1 = runDao.saveRun(run1, "remoteDirectory");
        
        // now read back the run and make sure it has the enzyme information
        MsRun runFromDb_1 = runDao.loadRun(runId_1);
        List<MsEnzyme> enzymes = runFromDb_1.getEnzymeList();
        assertNotNull(enzymes);
        assertEquals(2, enzymes.size());
        
        // save another run for this experiment
        List <MsEnzymeIn> enzymeList2 = new ArrayList<MsEnzymeIn>(1);
        enzymeList2.add(enzyme3);
        MsRunIn run2 = createRunWEnzymeInfo(enzymeList2);
        
        // save the run
        int runId_2 = runDao.saveRun(run2, "remoteDirectory");
        
        // now read back the run and make sure it has the enzyme information
        MsRun runFromDb_2 = runDao.loadRun(runId_2);
        enzymes = runFromDb_2.getEnzymeList();
        assertNotNull(enzymes);
        assertEquals(1, enzymes.size());
        checkEnzyme(enzyme3, enzymes.get(0));
        
        runDao.delete(runId_1);
        runDao.delete(runId_2);
        assertNull(runDao.loadRun(runId_1));
        assertNull(runDao.loadRun(runId_2));
    }
    

    public void testSaveAndDeleteRunsWithEnzymeInfoAndScans() {
        
        // load some enzymes from the database
        MsEnzyme enzyme1 = enzymeDao.loadEnzyme(1);
        MsEnzyme enzyme2 = enzymeDao.loadEnzyme(2);
        MsEnzyme enzyme3 = enzymeDao.loadEnzyme(3);
        
        assertNotNull(enzyme1);
        assertNotNull(enzyme2);
        assertNotNull(enzyme3);
        
        
        // create a run with enzyme information and save it
        List <MsEnzymeIn> enzymeList1 = new ArrayList<MsEnzymeIn>(2);
        enzymeList1.add(enzyme1);
        enzymeList1.add(enzyme2);
        MsRunIn run1 = createRunWEnzymeInfo(enzymeList1);
        int runId_1 = runDao.saveRun(run1, "remoteDirectory");
        
        // now read back the run and make sure it has the enzyme information
        MsRun runFromDb_1 = runDao.loadRun(runId_1);
        List<MsEnzyme> enzymes = runFromDb_1.getEnzymeList();
        assertNotNull(enzymes);
        assertEquals(2, enzymes.size());
        
        
        // save another run 
        List <MsEnzymeIn> enzymeList2 = new ArrayList<MsEnzymeIn>(1);
        enzymeList2.add(enzyme3);
        MsRunIn run2 = createRunWEnzymeInfo(enzymeList2);
        int runId_2 = runDao.saveRun(run2, "remoteDirectory");
        
        // now read back the run and make sure it has the enzyme information
        MsRun runFromDb_2 = runDao.loadRun(runId_2);
        enzymes = runFromDb_1.getEnzymeList();
        assertNotNull(enzymes);
        assertEquals(2, enzymes.size());
        
        
        // save some scans for the runs
        saveScansForRun(runId_1, 10);
        saveScansForRun(runId_2, 5);
        
        // make sure the run and associated enzyme information got saved (RUN 1)
        assertEquals(2, enzymeDao.loadEnzymesForRun(runId_1).size());
        assertEquals(10, scanDao.loadScanIdsForRun(runId_1).size());
        
        // make sure the run and associated enzyme information got saved (RUN 2)
        assertEquals(1, enzymeDao.loadEnzymesForRun(runId_2).size());
        assertEquals(5, scanDao.loadScanIdsForRun(runId_2).size());
        
        // now delete the first run
        runDao.delete(runId_1);
        
        // make sure the run is deleted ...
        assertNull(runDao.loadRun(runId_1));
        // ... and the associated enzyme information is deleted ...
        assertEquals(0, enzymeDao.loadEnzymesForRun(runId_1).size());
        // ... and all scans for the run are deleted.
        assertEquals(0, scanDao.loadScanIdsForRun(runId_1).size());
        
        // make sure nothing was delete for Run 2
        assertEquals(1, enzymeDao.loadEnzymesForRun(runId_2).size());
        assertEquals(5, scanDao.loadScanIdsForRun(runId_2).size());
        
        // now delete the second run
        runDao.delete(runId_2);
        
        // make sure the run is deleted ...
        assertNull(runDao.loadRun(runId_2));
        // ... and the associated enzyme information is deleted ...
        assertEquals(0, enzymeDao.loadEnzymesForRun(runId_2).size());
        // ... and all scans for the run are deleted.
        assertEquals(0, scanDao.loadScanIdsForRun(runId_2).size());
        
    }

    
    public void testRunLocation() {
        MsRunIn run1 = createDefaultRun();
        String server = "my.host";
        String remoteDir = "/my/server/directory";
        
        int runId = runDao.saveRun(run1, remoteDir);
        List<MsRunLocation> locDbList = runDao.loadLocationsForRun(runId);
        assertEquals(1, locDbList.size());
        
        MsRunLocation locDb = locDbList.get(0);
        assertEquals(remoteDir, locDb.getServerDirectory());
        assertEquals(runId, locDb.getRunId());
        
        int matchingLocs = runDao.loadMatchingRunLocations(runId, remoteDir);
        assertEquals(1, matchingLocs);
        
        
        // save another location for the run
        runDao.saveRunLocation("/my/server/directory/2", runId);
        locDbList = runDao.loadLocationsForRun(runId);
        assertEquals(2, locDbList.size());
        assertEquals(locDbList.get(0).getRunId(), locDbList.get(1).getRunId());
        assertNotSame(locDbList.get(0).getServerDirectory(), locDbList.get(1).getServerDirectory());
        
        matchingLocs = runDao.loadMatchingRunLocations(runId, "/my/server/directory/2");
        assertEquals(1, matchingLocs);
        
        // try to find a matching location that does not exist
        assertEquals(0, runDao.loadMatchingRunLocations(runId, "directory"));
        
        runDao.delete(runId);
        assertEquals(0, runDao.loadLocationsForRun(runId).size());
        assertNull(runDao.loadRun(runId));
    }
    
    
    public static class MsRunTest implements MsRunIn {

        
        private String sha1Sum;
        private RunFileFormat runFileFormat;
        private String instrumentVendor;
        private String instrumentSN;
        private String instrumentModel;
        private String fileName;
        private List<MsEnzymeIn> enzymeList = new ArrayList<MsEnzymeIn>();
        private String dataType;
        private String creationDate;
        private String conversionSWVersion;
        private String conversionSWOptions;
        private String conversionSW;
        private String comment;
        private String aquisitionMethod;

        public void setSha1Sum(String sha1Sum) {
            this.sha1Sum = sha1Sum;
        }

        public void setRunFileFormat(RunFileFormat runFileFormat) {
            this.runFileFormat = runFileFormat;
        }

        public void setInstrumentVendor(String instrumentVendor) {
            this.instrumentVendor = instrumentVendor;
        }

        public void setInstrumentSN(String instrumentSN) {
            this.instrumentSN = instrumentSN;
        }

        public void setInstrumentModel(String instrumentModel) {
            this.instrumentModel = instrumentModel;
        }

        public void setFileName(String fileName) {
            this.fileName = fileName;
        }

        public void setEnzymeList(List<MsEnzymeIn> enzymeList) {
            this.enzymeList = enzymeList;
        }

        public void setDataType(String dataType) {
            this.dataType = dataType;
        }

        public void setCreationDate(String creationDate) {
            this.creationDate = creationDate;
        }

        public void setConversionSWVersion(String conversionSWVersion) {
            this.conversionSWVersion = conversionSWVersion;
        }

        public void setConversionSWOptions(String conversionSWOptions) {
            this.conversionSWOptions = conversionSWOptions;
        }

        public void setConversionSW(String conversionSW) {
            this.conversionSW = conversionSW;
        }

        public void setComment(String comment) {
            this.comment = comment;
        }

        public void setAcquisitionMethod(String squisitionMethod) {
            this.aquisitionMethod = squisitionMethod;
        }

        public String getAcquisitionMethod() {
            return this.aquisitionMethod;
        }

        public String getComment() {
            return this.comment;
        }

        public String getConversionSW() {
            return this.conversionSW;
        }

        public String getConversionSWOptions() {
            return this.conversionSWOptions;
        }

        public String getConversionSWVersion() {
            return this.conversionSWVersion;
        }

        public String getCreationDate() {
            return this.creationDate;
        }

        public String getDataType() {
            return this.dataType;
        }

        public List<MsEnzymeIn> getEnzymeList() {
            return this.enzymeList;
        }

        public String getFileName() {
            return this.fileName;
        }

        public String getInstrumentModel() {
            return this.instrumentModel;
        }

        public String getInstrumentSN() {
            return this.instrumentSN;
        }

        public String getInstrumentVendor() {
            return this.instrumentVendor;
        }

        public RunFileFormat getRunFileFormat() {
            return this.runFileFormat;
        }

        public String getSha1Sum() {
            return this.sha1Sum;
        }

    }
}
