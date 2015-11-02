/**
 * PeakUtils.java
 * @author Vagisha Sharma
 * Jul 12, 2008
 * @version 1.0
 */
package org.yeastrc.ms.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.yeastrc.ms2.utils.Compresser;
import org.yeastrc.ms2.utils.Decompresser;

/**
 * 
 */
public class PeakUtils {

    private PeakUtils() {}
    
    public static byte[] compressPeakStringGZIP(String peakString) throws IOException {
        
        return Compresser.getInstance().compressString(peakString);
    }
    
    public static String decompressPeaksGZIP(byte[] peaks) throws IOException {
        
        ByteArrayOutputStream out = null;
        InputStream gis = null;
        
        try {
            gis = Decompresser.getInstance().decompressString(peaks);
            out = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int len = 0;
            while ((len = gis.read(buffer)) > 0) {
                out.write(buffer, 0, len);
            }
        }
        finally {
            if (gis != null) try {gis.close();}
            catch (IOException e) {}
            if (out != null) try {out.close();}
            catch (IOException e) {}
        }
        return out.toString();
    }
    
    public static byte[] encodePeakString(String peakString) throws IOException {
        ByteArrayOutputStream baos = null;
        ObjectOutputStream oos = null;
        baos = new ByteArrayOutputStream();
        try {
            oos = new ObjectOutputStream(baos);
            oos.writeObject(peakString);
            oos.flush();
            return baos.toByteArray();
        }
        
        finally {
            if (oos != null) {
                try {oos.close();}
                catch (IOException e) {e.printStackTrace();}
            }
        }
    }
    
    public static String decodePeakString(byte[] peakData) throws IOException, ClassNotFoundException {
        ByteArrayInputStream bais = new ByteArrayInputStream(peakData);
        ObjectInputStream ois = null;
        try {
            ois = new ObjectInputStream(bais);
            return ((String) ois.readObject());
        }
        
        finally {
            if (ois != null) {
                try {ois.close();} 
                catch (IOException e) {e.printStackTrace();}
            }
        }
    }
}
