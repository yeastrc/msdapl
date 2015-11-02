/**
 * FileUtils.java
 * @author Vagisha Sharma
 * Aug 3, 2009
 * @version 1.0
 */
package org.yeastrc.ms.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * 
 */
public class FileUtils {

    private FileUtils() {}

    public static void copyFile (File src, File dest) throws IOException  {

        InputStream in = null;
        OutputStream out = null;
        try {
            in = new FileInputStream(src);
            out = new FileOutputStream(dest);

            // Transfer bytes from in to out
            byte[] buf = new byte[4096];
            int len;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
        }
        finally {
            if(in != null) try {
                in.close();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
            if(out != null) try {
                out.close();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    
    public static void deleteFile(File file) {
        if(file.isDirectory()) {
            File[] files = file.listFiles();
            for(File f: files)
                deleteFile(f);
        }
        file.delete();
    }
    
    public static String removeExtension(String filename) {
        if(filename == null)
            return null;
        int idx = filename.lastIndexOf('.');
        if (idx != -1)
            filename = filename.substring(0, idx);
        return filename;
    }
}
