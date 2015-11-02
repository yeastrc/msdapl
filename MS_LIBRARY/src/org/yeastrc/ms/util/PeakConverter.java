/**
 * PeakConverterString.java
 * @author Vagisha Sharma
 * Jul 28, 2008
 * @version 1.0
 */
package org.yeastrc.ms.util;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.yeastrc.ms.domain.run.Peak;
import org.yeastrc.ms.domain.run.PeakStorageType;

/**
 * 
 */
public class PeakConverter {

    private static PeakConverter instance;
    private PeakConverter(){}
    
    public static PeakConverter instance() {
        if(instance == null)
            instance = new PeakConverter();
        return instance;
    }
    
    private List<String[]> convertToStringPeaks(String peakString) {

        List<String[]> peakList = new ArrayList<String[]>();

        if (peakString == null || peakString.length() == 0)
            return peakList;

        String[] peaksStr = peakString.split("\\n");
        for (String peak: peaksStr) {
            String [] peakVals = splitPeakVals(peak);
            peakList.add(peakVals);
        }
        return peakList;
    }

    private String[] splitPeakVals(String peak) {
        int i = peak.indexOf(" ");
        String[] vals = new String[2];
        vals[0] = peak.substring(0, i);
        if (vals[0].lastIndexOf('.') == -1) vals[0] = vals[0]+".0";
        vals[1] = peak.substring(i+1, peak.length());
        if (vals[1].lastIndexOf('.') == -1) vals[1] = vals[1]+".0";
        return vals;
    }
    
    public List<String[]> convertToStringPeaks(byte[] peakData, PeakStorageType storageType) throws IOException {
        if(storageType == PeakStorageType.STRING) {
            return convertToStringPeaks(new String(peakData));
        }
        else if(storageType == PeakStorageType.DOUBLE_FLOAT){
            return convertToStringPeaks(peakData);
        }
        else return null;
    }

    private List<String[]> convertToStringPeaks(byte[] peakData) throws IOException {
        ByteArrayInputStream bis = null;
        DataInputStream dis = null;
        List<String[]> peaks = new ArrayList<String[]>();
        try {
            bis = new ByteArrayInputStream(peakData);
            dis = new DataInputStream(bis);
            while(true) {
                try {
                    String mz = String.valueOf(dis.readDouble());
                    String intensity = String.valueOf(dis.readFloat());
                    peaks.add(new String[]{mz, intensity});
                }
                catch (EOFException e) {
                    break;
                }
            }
        }
        finally {
            if(dis != null) dis.close();
            if(bis != null) bis.close();
        }
        return peaks;
    }
    
    private List<Peak> convert(String peakString) {
        
        List<Peak> peakList = new ArrayList<Peak>();
        
        if (peakString == null || peakString.length() == 0)
            return peakList;
        
        String[] peaksStr = peakString.split("\\n");
        for (String peakStr: peaksStr) {
            String [] peakVals = splitPeakVals(peakStr);
            Peak peak = new Peak(Double.parseDouble(peakVals[0]), Float.parseFloat(peakVals[1]));
            peakList.add(peak);
        }
        return peakList;
    }
    
    
    
    public List<Peak> convert(byte[] peakData, PeakStorageType storageType) throws IOException {
        
        if(storageType == PeakStorageType.STRING) {
            return convert(new String(peakData));
        }
        else if(storageType == PeakStorageType.DOUBLE_FLOAT){
            return convert(peakData);
        }
        else
            return null;
    }

    private List<Peak> convert(byte[] peakData) throws IOException {
        ByteArrayInputStream bis = null;
        DataInputStream dis = null;
        List<Peak> peaks = new ArrayList<Peak>();
        try {
            bis = new ByteArrayInputStream(peakData);
            dis = new DataInputStream(bis);
            while(true) {
                try {
                    double mz = dis.readDouble();
                    float intensity = dis.readFloat();
                    peaks.add(new Peak(mz, intensity));
                }
                catch (EOFException e) {
                    break;
                }
            }
        }
        finally {
            if(dis != null) dis.close();
            if(bis != null) bis.close();
        }
        return peaks;
    }
}
