package org.yeastrc.ms.parser.ms2File;

import junit.framework.TestCase;

import org.yeastrc.ms.domain.run.ms2file.impl.ScanCharge;
import org.yeastrc.ms.parser.DataProviderException;

public class Ms2FileReaderTest extends TestCase {

    
    protected void setUp() throws Exception {
        super.setUp();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public final void testParseHeader() {
        Ms2FileReader reader = new Ms2FileReader();
        String header = "H\t FilteringProgram\tParc";
        
        String[] parsed = reader.parseHeader(header);
        assertEquals(2, parsed.length);
        assertEquals("FilteringProgram", parsed[0]);
        assertEquals("Parc", parsed[1]);
        
        header = "H\t FilteringProgram  Parc1, Parc2 !@#$";
        parsed = reader.parseHeader(header);
        assertEquals(2, parsed.length);
        assertEquals("FilteringProgram", parsed[0]);
        assertEquals("Parc1, Parc2 !@#$", parsed[1]);
        
        header = "H\t FilteringProgram      ";
        parsed = reader.parseHeader(header);
        assertEquals(1, parsed.length);
        assertEquals("FilteringProgram", parsed[0]);
        
        header = "H\t FilteringProgram";
        parsed = reader.parseHeader(header);
        assertEquals(1, parsed.length);
        assertEquals("FilteringProgram", parsed[0]);
        
        header = "H       Precursor/Fragment Ion Isotopes AVG/MONO";
        parsed = reader.parseHeader(header);
        assertEquals(2, parsed.length);
        assertEquals("Precursor/Fragment", parsed[0]);
        assertEquals("Ion Isotopes AVG/MONO", parsed[1]);
    }
    
    public void testParseScanChargeValid() {
        
        Ms2FileReader reader = new Ms2FileReader();
        String line = "Z       1       1394.58";
        try {
            ScanCharge scanCharge = reader.parseScanCharge(line);
            assertEquals(1, scanCharge.getCharge());
            assertEquals(1394.58, scanCharge.getMass().doubleValue());
        }
        catch (DataProviderException e) {
            fail("Valid 'Z' line");
            e.printStackTrace();
        }
        
        line = "Z       1       1394.";
        try {
            ScanCharge scanCharge = reader.parseScanCharge(line);
            assertEquals(1, scanCharge.getCharge());
            assertEquals(1394.0, scanCharge.getMass().doubleValue());
        }
        catch (DataProviderException e) {
            fail("Valid 'Z' line");
            e.printStackTrace();
        }
        
        line = "Z       1       1394   ";
        try {
            ScanCharge scanCharge = reader.parseScanCharge(line);
            assertEquals(1, scanCharge.getCharge());
            assertEquals(1394.0, scanCharge.getMass().doubleValue());
        }
        catch (DataProviderException e) {
            fail("Valid 'Z' line");
            e.printStackTrace();
        }
    }
    
    public void testParseScanChargeInValid() {
        Ms2FileReader reader = new Ms2FileReader();
        String line = "S       1       1394.58";
        try {
            reader.parseScanCharge(line);
            fail("Invalid 'Z' line");
        }
        catch (DataProviderException e) {}
        
        line = "  Z       1.0       1394.";
        try {
            reader.parseScanCharge(line);
            fail("Invalid 'Z' line");
        }
        catch (DataProviderException e) {}
        
        line = "Z       1.0       1394.";
        try {
            reader.parseScanCharge(line);
            fail("Invalid 'Z' line");
        }
        catch (DataProviderException e) {}
        
        line = "Z       1   ";
        try {
            reader.parseScanCharge(line);
            fail("Invalid 'Z' line");
        }
        catch (DataProviderException e) {}
        
        line = "Z       1$%      1394";
        try {
            reader.parseScanCharge(line);
            fail("Invalid 'Z' line");
        }
        catch (DataProviderException e) {}
        
        line = "Z       1       1394abcd";
        try {
            reader.parseScanCharge(line);
            fail("Invalid 'Z' line");
        }
        catch (DataProviderException e) {}
        
        line = "Z       1      1394 abcde";
        try {
            reader.parseScanCharge(line);
            fail("Invalid 'Z' line");
        }
        catch (DataProviderException e) {}
    }
}
