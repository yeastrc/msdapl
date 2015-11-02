package org.yeastrc.ms.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import junit.framework.TestCase;

public class DTASelectFileNameParseTest extends TestCase {

    protected void setUp() throws Exception {
        super.setUp();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

    
    public void testParseFileName() {
        Pattern pattern = Pattern.compile("(\\S+)\\.(\\d+)\\.(\\d+)\\.(\\d{1})");
        Matcher match = pattern.matcher("NE063005ph8s02.17247.17247.2");
        assertTrue(match.matches());
        assertEquals("NE063005ph8s02", match.group(1));
        assertEquals("17247", match.group(2));
        assertEquals("17247", match.group(3));
        assertEquals("2", match.group(4));
        
        match = pattern.matcher("NE063005ph8s02.17247.17247.21");
        assertFalse(match.matches());
        
        match = pattern.matcher("NE0630.05ph8s02.17247.17247.2");
        assertTrue(match.matches());
        assertEquals("NE0630.05ph8s02", match.group(1));
        assertEquals("17247", match.group(2));
        assertEquals("17247", match.group(3));
        assertEquals("2", match.group(4));
    }
}
