package org.yeastrc.ms.util;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import junit.framework.TestCase;

import org.yeastrc.ms2.utils.Compresser;

public class PeakUtilsTest extends TestCase {

    protected void setUp() throws Exception {
        super.setUp();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void testEncodingAndDecoding() throws IOException, ClassNotFoundException {
        
        String peaks = "123.01:98.7;234.01:87.6;345.01:76.5";
        byte[] peakBytes = PeakUtils.encodePeakString(peaks);
        assertEquals(peaks, PeakUtils.decodePeakString(peakBytes));
        
        peaks = "";
        peakBytes = PeakUtils.encodePeakString(peaks);
        assertEquals(peaks, PeakUtils.decodePeakString(peakBytes));
        
        peaks = null;
        peakBytes = PeakUtils.encodePeakString(peaks);
        assertEquals(peaks, PeakUtils.decodePeakString(peakBytes));
    }
    
    public void testCompressGZIP1() {
        String peaks = "123.01:98.7;234.01:87.6;345.01:76.5";
        System.out.println("uncompressed "+ peaks.getBytes().length);
        byte[] compressed = null;
        try {
            compressed = PeakUtils.compressPeakStringGZIP(peaks);
            System.out.println("compressed "+compressed.length);
            
            byte[] c2 = Compresser.getInstance().compressString(peaks);
            System.out.println("Compresser "+c2.length);
        }
        catch (IOException e) {
            e.printStackTrace();
            fail("Could not compress peaks");
        }
        String peaksOut = null;;
        try {
            peaksOut = PeakUtils.decompressPeaksGZIP(compressed);
        }
        catch (IOException e) {
            e.printStackTrace();
            fail("Could not decompress peaks");
        }
        assertEquals(peaks, peaksOut);
    }
    
    public void testCompressGZIP2() {
        String peaks = readPeaks("test_resources/peakDataTest_dir/peaks.1.txt");
        System.out.println("uncompressed "+ peaks.getBytes().length);
        byte[] compressed = null;
        try {
            compressed = PeakUtils.compressPeakStringGZIP(peaks);
            System.out.println("compressed "+compressed.length);
            
            byte[] c2 = Compresser.getInstance().compressString(peaks);
            System.out.println("Compresser "+c2.length);
        }
        catch (IOException e) {
            e.printStackTrace();
            fail("Could not compress peaks");
        }
        String peaksOut = null;;
        try {
            peaksOut = PeakUtils.decompressPeaksGZIP(compressed);
        }
        catch (IOException e) {
            e.printStackTrace();
            fail("Could not decompress peaks");
        }
        assertEquals(peaks, peaksOut);
    }
    
    private String readPeaks(String filePath) {
        StringBuilder buf = new StringBuilder();
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(filePath));
            String line = reader.readLine();
            while(line != null) {
                buf.append(line);
                line = reader.readLine();
            }
        }
        catch (FileNotFoundException e) {
            fail("File does not exist: "+filePath);
        }
        catch (IOException e) {
            fail("Error reading file: "+filePath);
        }
        finally {
            try {
                if (reader != null) reader.close();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
        return buf.toString();
    }
}
