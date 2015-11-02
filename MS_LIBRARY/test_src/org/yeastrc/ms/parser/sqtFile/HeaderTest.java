package org.yeastrc.ms.parser.sqtFile;

import java.sql.Date;
import java.text.ParseException;
import java.util.Calendar;
import java.util.GregorianCalendar;

import org.yeastrc.ms.domain.search.sqtfile.SQTSearchScanIn;
import org.yeastrc.ms.parser.DataProviderException;

import junit.framework.TestCase;

public class HeaderTest extends TestCase {

    protected void setUp() throws Exception {
        super.setUp();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

//    public void testMultipleDatabases() {
//        SQTHeader header = new SQTHeader();
//        String filePath = "/scratch/yates/NCBI-RefSeq_human_na_01-19-2007_reversed.fasta";
//        assertFalse(header.multipleDatabases(filePath));
//
//        filePath = "     /scratch/yates/NCBI-RefSeq_human_na_01-19-2007_reversed.fasta       ";
//        assertFalse(header.multipleDatabases(filePath));
//
//        filePath = "/scratch/yates/NCBI-RefSeq_human_na_01-19-2007_reversed.fasta,/scratch/yates/NCBI-RefSeq_human_na_01-19-2007_reversed.fasta";
//        assertTrue(header.multipleDatabases(filePath));
//
//        filePath = "/scratch/yates/NCBI-RefSeq_human_na_01-19-2007_reversed.fasta, ";
//        assertTrue(header.multipleDatabases(filePath));
//
//        filePath = ", /scratch/yates/NCBI-RefSeq_human_na_01-19-2007_reversed.fasta";
//        assertTrue(header.multipleDatabases(filePath));
//
//        filePath = "/path/1  /path/2 ";
//        assertTrue(header.multipleDatabases(filePath));
//    }

    public void testGetTime() {

        SQTHeader header = new SQTHeader();
        // Example of a valid time string: 01/29/2008, 03:34 AM
        try {
            header.getTime(" 01/29/2008, 03:34 AM ");
        }
        catch (ParseException e) {
            e.printStackTrace();
            fail("Valid time string");
        }
        
        try {
            header.getTime(" 01/29/2008");
            fail("Invalid time string");
        }
        catch (ParseException e) {}
    }
    
    
    public void testGetStartDate() {
        SQTHeader header = new SQTHeader();
        try {
            header.addHeaderItem("StartTime", "01/29/2008, 03:34 AM");
        }
        catch (SQTParseException e) {
            fail("Header is valid");
            e.printStackTrace();
        }
        Date date = header.getSearchDate();
        Calendar myCal = GregorianCalendar.getInstance();
        myCal.setTime(date);
        assertEquals(0, myCal.get(Calendar.MONTH));
        assertEquals(29, myCal.get(Calendar.DAY_OF_MONTH));
        assertEquals(2008, myCal.get(Calendar.YEAR));
        assertEquals(3, myCal.get(Calendar.HOUR));
        assertEquals(34, myCal.get(Calendar.MINUTE));
        assertEquals(0, myCal.get(Calendar.SECOND));
        assertEquals(0, myCal.get(Calendar.MILLISECOND));
        
        Calendar cal = GregorianCalendar.getInstance();
        cal.set(Calendar.DAY_OF_MONTH, 29);
        cal.set(Calendar.MONTH, 0); // months go from 0 to 11
        cal.set(Calendar.YEAR, 2008);
        cal.set(Calendar.HOUR, 3);
        cal.set(Calendar.MINUTE, 34);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        cal.set(Calendar.AM_PM, Calendar.AM);
        
        assertEquals(date.getTime(), cal.getTimeInMillis());
    }
    
    public void testGetStartDateInvalidDate() {
        SQTHeader header = new SQTHeader();
        try {
            header.addHeaderItem("StartTime", "01/29/2008");
            fail("Valid header and start time");
        }
        catch (SQTParseException e) {
            assertTrue(e.getMessage().startsWith("Error parsing start time: 01/29/2008"));
        }
    }
    
    public void testGetSearchDurationNoEndTime() {
        SQTHeader header = new SQTHeader();
        try {
            header.addHeaderItem("StartTime", "01/29/2008, 03:34 AM");
        }
        catch (SQTParseException e) {
            fail("Valid start time in header");
        }
        
        assertEquals(0, header.getSearchDuration());
    }
    
    public void testGetSearchDurationWithEndTime() {
        SQTHeader header = new SQTHeader();
        try {
            header.addHeaderItem("StartTime", "01/29/2008, 03:34 AM");
        }
        catch (SQTParseException e) {
            fail("Valid start time in header");
        }
        try {
            header.addHeaderItem("EndTime", "01/29/2008, 03:44 AM");
        }
        catch (SQTParseException e) {
            fail("Valid end time in header");
        }
        assertEquals(10, header.getSearchDuration());
    }
    
    public void testGetSearchDurationInvalidEndDate() {
        SQTHeader header = new SQTHeader();
        try {
            header.addHeaderItem("StartTime", "01/29/2008, 03:34 AM");
        }
        catch (SQTParseException e) {
            fail("Valid start time in header");
        }
        try {
            header.addHeaderItem("EndTime", "01/29/2008, 03:44");
            fail("Valid end time in header");
        }
        catch (SQTParseException e) {
            assertTrue(e.getMessage().startsWith("Error parsing end time: 01/29/2008, 03:44"));
        }
        
        assertEquals(0, header.getSearchDuration());
        
    }
    
    public void testParseHeader() {
        SQTFileReader reader = new SQTFileReader(){
            protected SQTSearchScanIn nextSearchScan()
                    throws DataProviderException {
                throw new UnsupportedOperationException();
            }};
        String header = "H       Precursor/Fragment Ion Isotopes AVG/MONO";
        String[] parsed = reader.parseHeader(header);
        assertEquals(2, parsed.length);
        assertEquals("Precursor/Fragment", parsed[0]);
        assertEquals("Ion Isotopes AVG/MONO", parsed[1]);
    }
    
    public void testParsePercolatorVersion() {
        String versionStr = "v 1.07, Build Date Aug 27 2008 10:06:10";
        SQTHeader header = new SQTHeader();
        String version = header.parsePercolatorVersion(versionStr);
        assertEquals("1.07", version);
    }

}
