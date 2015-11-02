package org.yeastrc.ms.util;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

import junit.framework.TestCase;

public class Sha1SumCalculatorTest extends TestCase {

    public void testSha1SumForInputStream() {
        String file = "resources/PARC_p75_01_itms.ms2";
        FileInputStream inStr = null;
        try {
            inStr = new FileInputStream(file);
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
            fail("Could not open file");
        }
        try {
            String sha1Sum = Sha1SumCalculator.instance().sha1SumFor(inStr);
            assertEquals(40, sha1Sum.length());
            assertEquals("3fc8b86cacacf5eba8039ffdccdf87532c377fd6", sha1Sum);
            System.out.println(sha1Sum.length());
        }
        catch (Exception e) {
            e.printStackTrace();
            fail("Error calculating SHA-1 sum");
        }
    }

    public void testIntegerToHexString() {
        byte[] bytes = new byte[20];
        for (int i = 0; i < bytes.length; i++)
            bytes[i] = (byte)i;
//        bytes[5] = -128;
//        System.out.println(Integer.toHexString(-128));
        StringBuilder buf = new StringBuilder();
        for (int i = 0; i < bytes.length; i++) {
            String hex = Integer.toHexString(0xFF & bytes[i]);
            if (hex.length() == 1)
                hex = "0"+hex;
            buf.append(hex);
        }
        System.out.println(buf.toString());
        System.out.println(buf.length());
//        System.out.println(Byte.MIN_VALUE+" "+Byte.MAX_VALUE);
//        byte b = -1;
//        System.out.println(Integer.toBinaryString(b));
//        System.out.println(Integer.toHexString(b));
//        System.out.println(Integer.toHexString(0xFF & b));
//        System.out.println(Integer.toBinaryString(3));
//        System.out.println(Integer.toBinaryString(-3));
//        System.out.println(Integer.toBinaryString(0xFF));
//        b = -1;
//        System.out.println(Integer.toBinaryString(b));
//        System.out.println(Integer.toHexString(b));
//        System.out.println(Integer.toBinaryString(0xff));
//        System.out.println(new Integer(0xff));
//        System.out.println(Integer.toHexString(255));
//        System.out.println(Integer.toBinaryString(255));
        
    }
}
