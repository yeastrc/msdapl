package org.yeastrc.ms.util;

import junit.framework.TestCase;

public class PeakStringBuilderTest extends TestCase {

    protected void setUp() throws Exception {
        super.setUp();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void testGetPeaksAsString() {
        PeakStringBuilder builder = new PeakStringBuilder();
        assertEquals("", builder.getPeaksAsString());
        
        builder.addPeak("100.000", "200.1230");
        assertEquals("100 200.123", builder.getPeaksAsString());
        builder.addPeak("123.4", "987.600001000");
        assertEquals("100 200.123\n123.4 987.600001", builder.getPeaksAsString());
    }
    
    public void testTrimTrailingZeros() {
        String number = "";
        assertEquals(number, PeakStringBuilder.trimTrailingZeros(number));
        
        number = "0";
        assertEquals(number, PeakStringBuilder.trimTrailingZeros(number));
        
        number = "0.";
        assertEquals("0", PeakStringBuilder.trimTrailingZeros(number));
        
        number = ".0";
        assertEquals("", PeakStringBuilder.trimTrailingZeros(number));
        
        number = "0.0";
        assertEquals("0", PeakStringBuilder.trimTrailingZeros(number));
        
        
        number = "1";
        assertEquals(number, PeakStringBuilder.trimTrailingZeros(number));
        
        number = ".1";
        assertEquals(number, PeakStringBuilder.trimTrailingZeros(number));
        
        number = "1.";
        assertEquals(number, PeakStringBuilder.trimTrailingZeros(number));
        
        number = "1.0";
        assertEquals("1", PeakStringBuilder.trimTrailingZeros(number));
        
        number = "1.0101000";
        assertEquals("1.0101", PeakStringBuilder.trimTrailingZeros(number));
        
        number = "1.000000";
        assertEquals("1", PeakStringBuilder.trimTrailingZeros(number));
        
        number = "10";
        assertEquals("10", PeakStringBuilder.trimTrailingZeros(number));
    }

    public void testParseDouble() {
        String number = "0.";
        Double.parseDouble(number);
        number = ".0";
        Double.parseDouble(number);
        number = ".";
        try {Double.parseDouble(number);fail("Invalid number");}
        catch(NumberFormatException e){}
        number = "";
        try {Double.parseDouble(number);fail("Invalid number");}
        catch(NumberFormatException e){}
        number = "0";
        Double.parseDouble(number);
    }
}
