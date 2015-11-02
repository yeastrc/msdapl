/**
 * BaseDAOTestCase.java
 * @author Vagisha Sharma
 * Jul 4, 2008
 * @version 1.0
 */
package org.yeastrc.ms.dao;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import junit.framework.TestCase;

import org.yeastrc.ms.dao.general.MsEnzymeDAO;
import org.yeastrc.ms.dao.general.MsExperimentDAO;
import org.yeastrc.ms.dao.run.MsRunDAO;
import org.yeastrc.ms.dao.run.MsScanDAO;
import org.yeastrc.ms.dao.run.MsRunDAOImplTest.MsRunTest;
import org.yeastrc.ms.dao.run.MsScanDAOImplTest.MsScanTest;
import org.yeastrc.ms.dao.search.MsRunSearchDAO;
import org.yeastrc.ms.dao.search.MsSearchDAO;
import org.yeastrc.ms.dao.search.MsSearchDatabaseDAO;
import org.yeastrc.ms.dao.search.MsSearchModificationDAO;
import org.yeastrc.ms.dao.search.MsSearchResultDAO;
import org.yeastrc.ms.dao.search.MsSearchResultProteinDAO;
import org.yeastrc.ms.dao.search.MsRunSearchDAOImplTest.MsRunSearchTest;
import org.yeastrc.ms.dao.search.MsSearchDAOImplTest.MsSearchTest;
import org.yeastrc.ms.dao.search.MsSearchResultDAOImplTest.MsSearchResultTest;
import org.yeastrc.ms.domain.general.MsEnzyme;
import org.yeastrc.ms.domain.general.MsEnzymeIn;
import org.yeastrc.ms.domain.general.MsEnzyme.Sense;
import org.yeastrc.ms.domain.general.impl.Enzyme;
import org.yeastrc.ms.domain.run.DataConversionType;
import org.yeastrc.ms.domain.run.MsRun;
import org.yeastrc.ms.domain.run.MsRunIn;
import org.yeastrc.ms.domain.run.MsScan;
import org.yeastrc.ms.domain.run.MsScanIn;
import org.yeastrc.ms.domain.run.RunFileFormat;
import org.yeastrc.ms.domain.search.MsResidueModification;
import org.yeastrc.ms.domain.search.MsResidueModificationIn;
import org.yeastrc.ms.domain.search.MsResultResidueMod;
import org.yeastrc.ms.domain.search.MsRunSearch;
import org.yeastrc.ms.domain.search.MsRunSearchIn;
import org.yeastrc.ms.domain.search.MsSearch;
import org.yeastrc.ms.domain.search.MsSearchDatabaseIn;
import org.yeastrc.ms.domain.search.MsSearchIn;
import org.yeastrc.ms.domain.search.MsSearchResult;
import org.yeastrc.ms.domain.search.MsSearchResultIn;
import org.yeastrc.ms.domain.search.MsSearchResultPeptide;
import org.yeastrc.ms.domain.search.MsSearchResultProtein;
import org.yeastrc.ms.domain.search.MsSearchResultProteinIn;
import org.yeastrc.ms.domain.search.MsTerminalModificationIn;
import org.yeastrc.ms.domain.search.Program;
import org.yeastrc.ms.domain.search.SearchFileFormat;
import org.yeastrc.ms.domain.search.ValidationStatus;
import org.yeastrc.ms.domain.search.MsTerminalModification.Terminal;
import org.yeastrc.ms.domain.search.impl.ResidueModification;
import org.yeastrc.ms.domain.search.impl.ResultResidueModBean;
import org.yeastrc.ms.domain.search.impl.SearchDatabase;
import org.yeastrc.ms.domain.search.impl.SearchResultPeptideBean;
import org.yeastrc.ms.domain.search.impl.TerminalModification;

/**
 * 
 */
public class BaseDAOTestCase extends TestCase {

    protected MsExperimentDAO exptDao = DAOFactory.instance().getMsExperimentDAO();
    protected MsScanDAO scanDao = DAOFactory.instance().getMsScanDAO();
    protected MsRunDAO runDao = DAOFactory.instance().getMsRunDAO();

    protected MsSearchDAO searchDao = DAOFactory.instance().getMsSearchDAO();
    protected MsRunSearchDAO runSearchDao = DAOFactory.instance().getMsRunSearchDAO();
    protected MsSearchResultDAO resultDao = DAOFactory.instance().getMsSearchResultDAO();
    protected MsSearchDatabaseDAO seqDbDao = DAOFactory.instance().getMsSequenceDatabaseDAO();
    protected MsSearchModificationDAO modDao = DAOFactory.instance().getMsSearchModDAO();
    protected MsSearchResultProteinDAO matchDao = DAOFactory.instance().getMsProteinMatchDAO();
    protected MsEnzymeDAO enzymeDao = DAOFactory.instance().getEnzymeDAO();

    protected void setUp() throws Exception {
        super.setUp();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public static void resetDatabase() {
        System.out.println("Resetting database");
        String script = "src/resetDatabase.sh";
        try {
            Process proc = Runtime.getRuntime().exec("sh "+script);
            BufferedReader reader = new BufferedReader(new InputStreamReader(proc.getErrorStream()));
            String line = reader.readLine();
            while(line != null) {
                System.out.println(line);
                line = reader.readLine();
            }
            reader.close();
            proc.waitFor();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    
    public static void addEnzymes() {
        System.out.println("Adding enzymes");
        String script = "src/addEnzymes.sh";
        try {
            Process proc = Runtime.getRuntime().exec("sh "+script);
            BufferedReader reader = new BufferedReader(new InputStreamReader(proc.getErrorStream()));
            String line = reader.readLine();
            while(line != null) {
                System.out.println(line);
                line = reader.readLine();
            }
            reader.close();
            proc.waitFor();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    
    //-----------------------------------------------------------------------------------------------------
    // SEARCH DATABASE
    //-----------------------------------------------------------------------------------------------------
    protected MsSearchDatabaseIn makeSequenceDatabase(final String serverAddress, final String serverPath) {
        SearchDatabase db = new SearchDatabase();
        db.setServerAddress(serverAddress);
        db.setServerPath(serverPath);
        return db;
    }

    //-----------------------------------------------------------------------------------------------------
    // SEARCH RESULT
    //-----------------------------------------------------------------------------------------------------
    protected MsSearchResultIn makeSearchResult(int searchId, int charge,String peptide, BigDecimal observedMass,
            boolean addDynaResMod) {

        //!!------------ RESET the dynamic mod lookup table --------------------------------
//        DynamicModLookupUtil.instance().reset();
        //!!------------ RESET the dynamic mod lookup table --------------------------------
        
        MsSearchResultTest result = makeSearchResult(charge, peptide, observedMass);
        SearchResultPeptideBean resultPeptide = new SearchResultPeptideBean();
        resultPeptide.setPeptideSequence(peptide);
        result.setResultPeptide(resultPeptide);
        
        // add dynamic modifications
        if (addDynaResMod)     addResultDynamicModifications(resultPeptide, searchId);

        return result;
    }

    protected MsSearchResultTest makeSearchResult(int charge, String peptide, BigDecimal observedMass) {
        MsSearchResultTest result = new MsSearchResultTest();
        result.setCharge(charge);
        result.setObservedMass(observedMass);
        return result;
    }

    protected void addResultDynamicModifications(SearchResultPeptideBean peptide, int searchId) {

        List<MsResidueModification> dynaMods = modDao.loadDynamicResidueModsForSearch(searchId);

        List<MsResultResidueMod> resultDynaMods = new ArrayList<MsResultResidueMod>(dynaMods.size());
        int pos = 1;
        for (MsResidueModification mod: dynaMods) {
            MsResultResidueMod resMod = makeResultDynamicResidueMod(mod.getModifiedResidue(), 
                    mod.getModificationMass().toString(), 
                    mod.getModificationSymbol(), 
                    pos++);
            resultDynaMods.add(resMod);
        }
        peptide.setDynamicResidueModifications(resultDynaMods);
    }

    protected void checkSearchResult(MsSearchResultIn input, MsSearchResult output) {
        checkSearchResult(input, output, true);
    }
    
    protected void checkSearchResult(MsSearchResultIn input, MsSearchResult output, boolean checkProteins) {
        assertEquals(input.getCharge(), output.getCharge());
        assertEquals(input.getObservedMass().doubleValue(), output.getObservedMass().doubleValue());
        if (input.getValidationStatus() == null)
            assertEquals(ValidationStatus.UNKNOWN, output.getValidationStatus());
        else {
            assertEquals(input.getValidationStatus(), output.getValidationStatus());
        }
        assertNull(input.getValidationStatus());
        // make sure the scan number in input matches scan number of scan with scanId in output
//        assertEquals(input.getScanNumber(), scanDao.load(output.getScanId()).getStartScanNum());
        if(checkProteins) {
            assertEquals(input.getProteinMatchList().size(), output.getProteinMatchList().size());
            List<MsSearchResultProteinIn> proteinsIn = input.getProteinMatchList();
            List<MsSearchResultProtein> proteinsOut = output.getProteinMatchList();
            Collections.sort(proteinsIn, new Comparator<MsSearchResultProteinIn>() {
                public int compare(MsSearchResultProteinIn o1,
                        MsSearchResultProteinIn o2) {
                    return o1.getAccession().compareTo(o2.getAccession());
                }});
            
            Collections.sort(proteinsOut, new Comparator<MsSearchResultProtein>() {
                public int compare(MsSearchResultProtein o1,
                        MsSearchResultProtein o2) {
                    return o1.getAccession().compareTo(o2.getAccession());
                }});
            
            for(int i = 0; i < proteinsIn.size(); i++) {
                assertEquals(proteinsIn.get(i).getAccession(), proteinsOut.get(i).getAccession());
            }
        }
        checkResultPeptide(input.getResultPeptide(), output.getResultPeptide());
    }
    
    protected void checkResultPeptide(MsSearchResultPeptide input, MsSearchResultPeptide output) {
        assertEquals(input.getPeptideSequence(), output.getPeptideSequence());
        assertEquals(input.getPreResidue(), output.getPreResidue());
        assertEquals(input.getPostResidue(), output.getPostResidue());
        assertEquals(input.getSequenceLength(), output.getSequenceLength());
        assertEquals(input.getResultDynamicResidueModifications().size(), output.getResultDynamicResidueModifications().size());
        assertEquals(input.getResultDynamicTerminalModifications().size(), output.getResultDynamicTerminalModifications().size());
    }
    
    //-----------------------------------------------------------------------------------------------------
    // SEARCH RESULT PROTEIN
    //-----------------------------------------------------------------------------------------------------
    protected MsSearchResultProteinIn makeResultProtein(final String acc, final String desc) {
        MsSearchResultProteinIn match = new MsSearchResultProteinIn() {
            public String getAccession() {
                return acc;
            }

            public String getDescription() {
                return desc;
            }

			@Override
			public void setAccession(String accession) {
				throw new UnsupportedOperationException();
			}};
        return match;
    }

    //-----------------------------------------------------------------------------------------------------
    // MODIFICATIONS
    //-----------------------------------------------------------------------------------------------------
    protected MsResidueModificationIn makeStaticResidueMod(final char modChar, final String modMass) {
        ResidueModification mod = new ResidueModification();
        mod.setModifiedResidue(modChar);
        if (modMass != null)
            mod.setModificationMass(new BigDecimal(modMass));
        return mod;
    }

    protected MsResidueModificationIn makeDynamicResidueMod(final char modChar, final String modMass, final char modSymbol) {
        ResidueModification mod = new ResidueModification();
        mod.setModifiedResidue(modChar);
        mod.setModificationSymbol(modSymbol);
        if (modMass != null)
            mod.setModificationMass(new BigDecimal(modMass));
        return mod;
    }
    
    protected MsTerminalModificationIn makeStaticTerminalMod(final Terminal term, final String modMass) {
        TerminalModification mod = new TerminalModification();
        if (modMass != null)
            mod.setModificationMass(new BigDecimal(modMass));
        mod.setModifiedTerminal(term);
        return mod;
    }

    protected MsTerminalModificationIn makeDynamicTerminalMod(final Terminal term, final String modMass, final char modSymbol) {
        TerminalModification mod = new TerminalModification();
        if (modMass != null)
            mod.setModificationMass(new BigDecimal(modMass));
        mod.setModifiedTerminal(term);
        mod.setModificationSymbol(modSymbol);
        return mod;
    }
    
    protected MsResultResidueMod makeResultDynamicResidueMod(final char modChar, final String modMass,
            final char modSymbol, final int modPos) {
        ResultResidueModBean mod = new ResultResidueModBean();
        if (modMass != null)
            mod.setModificationMass(new BigDecimal(modMass));
        mod.setModificationSymbol(modSymbol);
        mod.setModifiedPosition(modPos);
        mod.setModifiedResidue(modChar);
        return mod;
    }
    
    //-----------------------------------------------------------------------------------------------------
    // SEARCH
    //-----------------------------------------------------------------------------------------------------
    protected MsSearchIn makeSearch(boolean addSeqDb, boolean addStaticMods, boolean addDynaMods, boolean addEnzymes) {

        MsSearchTest search = new MsSearchTest();
        search.setSearchProgram(Program.SEQUEST);
        search.setAnalysisProgramVersion("1.0");
        search.setSearchDate(new Date(getTime("01/29/2008, 03:34 AM", true)));

        if (addSeqDb) {
            MsSearchDatabaseIn db1 = makeSequenceDatabase("serverAddress", "path1");
            MsSearchDatabaseIn db2 = makeSequenceDatabase("serverAddress", "path2");
            search.setSearchDatabases(Arrays.asList(new MsSearchDatabaseIn[]{db1, db2}));
        }

        if (addStaticMods) {
            MsResidueModificationIn mod1 = makeStaticResidueMod('C', "50.0");
            MsResidueModificationIn mod2 = makeStaticResidueMod('S', "80.0");
            search.setStaticResidueMods(Arrays.asList(new MsResidueModificationIn[]{mod1, mod2}));
        }

        if (addDynaMods) {
            MsResidueModificationIn dmod1 = makeDynamicResidueMod('A', "10.0", '*');
            MsResidueModificationIn dmod2 = makeDynamicResidueMod('B', "20.0", '#');
            MsResidueModificationIn dmod3 = makeDynamicResidueMod('C', "30.0", '@');
            search.setDynamicResidueMods(Arrays.asList(new MsResidueModificationIn[]{dmod1, dmod2, dmod3}));
        }

        if (addEnzymes) {
            MsEnzymeIn enzyme1 = makeDigestionEnzyme("TestEnzyme", Sense.UNKNOWN, null, null);
            MsEnzymeIn enzyme2 = makeDigestionEnzyme("Trypsin", null, null, null);
            search.setEnzymeList(Arrays.asList(new MsEnzymeIn[]{enzyme1, enzyme2}));
        }
        return search;
    }
    
    protected void checkSearch(MsSearchIn input, MsSearch output) {
        assertEquals(input.getSearchDatabases().size(), output.getSearchDatabases().size());
        assertEquals(input.getStaticResidueMods().size(), output.getStaticResidueMods().size());
        assertEquals(input.getDynamicResidueMods().size(), output.getDynamicResidueMods().size());
        assertEquals(input.getStaticTerminalMods().size(), output.getStaticTerminalMods().size());
        assertEquals(input.getDynamicTerminalMods().size(), output.getDynamicTerminalMods().size());
        assertEquals(input.getEnzymeList().size(), output.getEnzymeList().size());
        assertEquals(input.getSearchDate().toString(), output.getSearchDate().toString());
        assertEquals(input.getSearchProgram(), output.getSearchProgram());
        assertEquals(input.getSearchProgramVersion(), output.getSearchProgramVersion());
    }
    
    //-----------------------------------------------------------------------------------------------------
    // RUN SEARCH
    //-----------------------------------------------------------------------------------------------------
    protected MsRunSearchIn makeRunSearch(SearchFileFormat format) {

        MsRunSearchTest runSearch = new MsRunSearchTest();
        runSearch.setFileFormat(format);
        long startTime = getTime("01/29/2008, 03:34 AM", false);
        long endTime = getTime("01/29/2008, 06:21 AM", false);
        runSearch.setSearchDate(new Date(getTime("01/29/2008, 03:34 AM", true)));
        runSearch.setSearchDuration(searchTimeMinutes(startTime, endTime));

        return runSearch;
    }

    protected int  searchTimeMinutes(long startTime, long endTime) {
        assertTrue(endTime > startTime);
        return (int)((endTime - startTime)/(1000*60));
    }

    /**
     * date/time string should look like: 01/29/2008, 03:34 AM
     * @param string
     * @param justDate
     * @return
     */
    protected long getTime(String string, boolean justDate) {
        // example: 01/29/2008, 03:34 AM
        Calendar cal = GregorianCalendar.getInstance();
        string = string.replaceAll("\\s", "");
        String[] tok = string.split(",");
        String date = tok[0];
        String time = tok[1];

        String[] dateTok = date.split("\\/");
        cal.set(Calendar.MONTH, Integer.valueOf(dateTok[0]));
        cal.set(Calendar.DATE, Integer.valueOf(dateTok[1]));
        cal.set(Calendar.YEAR, Integer.valueOf(dateTok[2]));

        if (justDate) {
            cal.set(Calendar.MINUTE, 0);
            cal.set(Calendar.SECOND, 0);
            cal.set(Calendar.MILLISECOND, 0);
        }
        else {
            String ampm = time.substring(time.length() - 2, time.length());
            String justTime = time.substring(0, time.length() -2);

            String[] justTimeTok = justTime.split(":");
            cal.set(Calendar.AM_PM, (ampm.equalsIgnoreCase("AM") ?  Calendar.AM : Calendar.PM));
            cal.set(Calendar.HOUR_OF_DAY, Integer.parseInt(justTimeTok[0]));
            cal.set(Calendar.MINUTE, Integer.parseInt(justTimeTok[1]));
            cal.set(Calendar.SECOND, 0);
            cal.set(Calendar.MILLISECOND, 0);
        }
        return cal.getTimeInMillis();
    }
    
    protected void checkRunSearch(MsRunSearchIn input, MsRunSearch output) {
        assertEquals(input.getSearchDate().toString(), output.getSearchDate().toString());
        assertEquals(input.getSearchDuration(), output.getSearchDuration());
        assertEquals(input.getSearchFileFormat(), output.getSearchFileFormat());
    }

    //---------------------------------------------------------------------------------
    // ENZYME
    //---------------------------------------------------------------------------------
    protected MsEnzymeIn makeDigestionEnzyme(String name, Sense sense,String cut, String nocut) {
        Enzyme enzyme = new Enzyme();
        enzyme.setName(name);
        enzyme.setSense(sense);
        enzyme.setCut(cut);
        enzyme.setNocut(nocut);
        return enzyme;
    }

    protected void checkEnzyme(MsEnzymeIn inputEnzyme, MsEnzyme outputEnzyme) {
        assertEquals(inputEnzyme.getName(), outputEnzyme.getName());
        assertEquals(inputEnzyme.getSense(), outputEnzyme.getSense());
        assertEquals(inputEnzyme.getCut(), outputEnzyme.getCut());
        assertEquals(inputEnzyme.getNocut(), outputEnzyme.getNocut());
        assertEquals(inputEnzyme.getDescription(), outputEnzyme.getDescription());
    }
    
    //---------------------------------------------------------------------------------
    // SCAN
    //---------------------------------------------------------------------------------
    protected MsScanIn makeMsScan(int scanNum, int precursorScanNum, DataConversionType convType) {
        MsScanTest scan = new MsScanTest();
        scan.setStartScanNum(scanNum);
        scan.setEndScanNum(scanNum);
        scan.setFragmentationType("ETD");
        scan.setMsLevel(2);
        scan.setPrecursorMz(new BigDecimal("123.45"));
        scan.setPrecursorScanNum(precursorScanNum);
        scan.setRetentionTime(new BigDecimal("98.7"));
        scan.setDataConversionType(convType);
        return scan;
    }

    protected MsScanIn makeMsScanWithPeakData(int scanNum, int precursorScanNum, DataConversionType convType) {
        MsScanTest scan = (MsScanTest) makeMsScan(scanNum, precursorScanNum, convType);
        List<String[]> peaks = new ArrayList<String[]>(10);
        Random r = new Random();
        for (int i = 0; i < 10; i++) {
            String[] peak = new String[2];
            peak[0] = Double.toString(r.nextDouble());
            peak[1] = Float.toString(r.nextFloat());
            peaks.add(peak);
        }
        scan.setPeaks(peaks);
        assertEquals(10, scan.getPeakCount());
        return scan;
    }
    
    protected void saveScansForRun(int runId, int scanCount) {
        Random random = new Random();
        for (int i = 0; i < scanCount; i++) {
            int scanNum = random.nextInt(100);
            MsScanIn scan = makeMsScanWithPeakData(scanNum, 26, DataConversionType.CENTROID);
            scanDao.save(scan, runId);
        }
    }
    
    protected void checkScan (MsScanIn input, MsScan output) {
        assertEquals(input.getStartScanNum(), output.getStartScanNum());
        assertEquals(input.getFragmentationType(), output.getFragmentationType());
        assertEquals(input.getMsLevel(), output.getMsLevel());
        assertEquals(input.getPrecursorMz().doubleValue(), output.getPrecursorMz().doubleValue());
        assertEquals(input.getPrecursorScanNum(), output.getPrecursorScanNum());
        assertEquals(input.getRetentionTime().doubleValue(), output.getRetentionTime().doubleValue());
        assertEquals(input.getEndScanNum(), output.getEndScanNum());
        assertEquals(input.getDataConversionType(), output.getDataConversionType());
        assertEquals(input.getPeakCount(), output.getPeakCount());
        List<String[]> iPeaks = input.getPeaksString();
        List<String[]> oPeaks = null;
        try {
            oPeaks = output.getPeaksString();
        }
        catch (IOException e) {
            fail("Error reading peaks from output scan "+e.getMessage());
        }
        Iterator<String[]> ipiter = iPeaks.iterator();
        Iterator<String[]> opiter = oPeaks.iterator();
        while(ipiter.hasNext()) {
            String[] ipeak = ipiter.next();
            String[] opeak = opiter.next();
            assertEquals(ipeak[0], opeak[0]);
            assertEquals(ipeak[1], opeak[1]);
        }
    }
    
    //---------------------------------------------------------------------------------
    // RUN
    //---------------------------------------------------------------------------------
    protected MsRunIn createRunWEnzymeInfo(List<MsEnzymeIn> enzymes) {
        MsRunTest run = createDefaultRun();
        run.setEnzymeList(enzymes);
        return run;
    }

    protected MsRunTest createDefaultRun() {
        return createRunForFormat(RunFileFormat.MS2);
    }
    
    protected MsRunTest createRunForFormat(RunFileFormat format) {
        MsRunTest run = new MsRunTest();
        run.setRunFileFormat(format);
        run.setFileName("my_file1.ms2");
        run.setAcquisitionMethod("Data dependent");
        run.setComment("Dummy run");
        run.setConversionSW("ms2Convert");
        run.setConversionSWVersion("1.0");
        run.setConversionSWOptions("options string");
        run.setDataType("profile");
        run.setInstrumentModel("ETD");
        run.setInstrumentVendor("Thermo");
        run.setSha1Sum("sha1sum");
        return run;
    }

    protected void checkRun(MsRunIn inputRun, MsRun outputRun) {
        assertEquals(inputRun.getAcquisitionMethod(), outputRun.getAcquisitionMethod());
        assertEquals(inputRun.getComment(), outputRun.getComment());
        assertEquals(inputRun.getConversionSW(), outputRun.getConversionSW());
        assertEquals(inputRun.getConversionSWOptions(), outputRun.getConversionSWOptions());
        assertEquals(inputRun.getConversionSWVersion(), outputRun.getConversionSWVersion());
        assertEquals(inputRun.getCreationDate(), outputRun.getCreationDate());
        assertEquals(inputRun.getFileName(), outputRun.getFileName());
        assertEquals(inputRun.getInstrumentModel(), outputRun.getInstrumentModel());
        assertEquals(inputRun.getInstrumentSN(), outputRun.getInstrumentSN());
        assertEquals(inputRun.getInstrumentVendor(), outputRun.getInstrumentVendor());
        assertEquals(inputRun.getFileName(), outputRun.getFileName());
        assertEquals(inputRun.getSha1Sum(), outputRun.getSha1Sum());
        assertEquals(inputRun.getRunFileFormat(), outputRun.getRunFileFormat());
        assertEquals(inputRun.getEnzymeList().size(), outputRun.getEnzymeList().size());
    }
}
