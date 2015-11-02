/**
 * Ms2FileScan.java
 * @author Vagisha Sharma
 * Jun 16, 2008
 * @version 1.0
 */
package org.yeastrc.ms.domain.run.ms2file.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.yeastrc.ms.domain.run.DataConversionType;
import org.yeastrc.ms.domain.run.Peak;
import org.yeastrc.ms.domain.run.ms2file.MS2NameValuePair;
import org.yeastrc.ms.domain.run.ms2file.MS2ScanCharge;
import org.yeastrc.ms.domain.run.ms2file.MS2ScanIn;
import org.yeastrc.ms.util.PeakStringBuilder;

/**
 * 
 */
public class Scan implements MS2ScanIn {

    public static enum PEAK_TYPE{
        STRING, NUMBER;
    }
    
    public static final String PRECURSOR_SCAN = "PrecursorScan"; // precursor scan number
    public static final String ACTIVATION_TYPE = "ActivationType";
    public static final String RET_TIME = "RetTime";
    public static final String RT_TIME = "RTime";

    private int startScan = -1;
    private int endScan = -1;
    private int precursorScanNum = -1;

    private BigDecimal precursorMz;
    private BigDecimal retentionTime;
    private String activationType;

    private DataConversionType dataConversionType = DataConversionType.UNKNOWN;
    
    private List<String[]> peakList;
    
    private List<Peak> peakNList;
    
    private List<MS2ScanCharge> chargeStates;

    private List<MS2NameValuePair> analysisItems;

    private PEAK_TYPE peakType;

    public Scan(PEAK_TYPE peakType) {
        chargeStates = new ArrayList<MS2ScanCharge>();
        analysisItems = new ArrayList<MS2NameValuePair>();
        this.peakType = peakType;
        
        if(peakType == PEAK_TYPE.STRING) {
            peakList = new ArrayList<String[]>();
        }
        else {
            peakNList = new ArrayList<Peak>();
        }
    }

//    public boolean isValid() {
//        return peakList.size() > 0 && chargeStates.size() > 0;
//    }

    @Override
    public List<MS2NameValuePair> getChargeIndependentAnalysisList() {
        return this.analysisItems;
    }

    public void addAnalysisItem(String label, String value) {
        if (label == null)   return;
        analysisItems.add(new NameValuePair(label, value));
        if (label.equalsIgnoreCase(RET_TIME))
            setRetentionTime(value);
        else if (label.equalsIgnoreCase(RT_TIME))
            setRetentionTime(value);
        else if (label.equalsIgnoreCase(PRECURSOR_SCAN))
            setPrecursorScanNum(value);
        else if (label.equalsIgnoreCase(ACTIVATION_TYPE))
            setFragmentationType(value);
    }

    public BigDecimal getRetentionTime() {
        return retentionTime;
    }
    private void setRetentionTime(String rt) {
        if (rt == null) return;
        try {
            this.retentionTime = new BigDecimal(rt);
        }
        catch(NumberFormatException e) {
            this.retentionTime = null;
        }
    }

    public int getPrecursorScanNum() {
        return precursorScanNum;
    }
    private void setPrecursorScanNum(String num) {
        if (num == null)    return;
        try {
            this.precursorScanNum = Integer.parseInt(num);
        }
        catch(NumberFormatException e) {
            this.precursorScanNum = -1;
        }
    }

    public List<MS2ScanCharge> getScanChargeList() {
        return this.chargeStates;
    }
    public void addChargeState(ScanCharge chargeState) {
        chargeStates.add(chargeState);
    }

    public int getStartScanNum() {
        return startScan;
    }
    public void setStartScan(int startScan) {
        this.startScan = startScan;
    }

    public int getEndScanNum() {
        return this.endScan;
    }
    public void setEndScan(int endScan) {
        this.endScan = endScan;
    }

    public BigDecimal getPrecursorMz() {
        return precursorMz;
    }
    public void setPrecursorMz(String precursorMz) {
        this.precursorMz = new BigDecimal(precursorMz);
    }

    public List<String[]> getPeaksString() {
        if(peakList != null)
            return peakList;
        else {
            peakList = new ArrayList<String[]>(peakNList.size());
            for(Peak peak: peakNList) {
                peakList.add(new String[]{String.valueOf(peak.getMz()), String.valueOf(peak.getIntensity())});
            }
            return peakList;
        }
    }
    
    public List<Peak> getPeaks() {
        if(peakNList != null)
            return peakNList;
        else {
            peakNList = new ArrayList<Peak>(peakList.size());
            for(String[] peakArr: peakList) {
                double mz = Double.parseDouble(peakArr[0]);
                float intensity = Float.parseFloat(peakArr[1]);
                peakNList.add(new Peak(mz, intensity));
            }
            return peakNList;
        }
    }
    
    public void addPeak(String mz, String intensity) {
        if(this.peakType == PEAK_TYPE.NUMBER)
            throw new IllegalArgumentException("Scan does not accept peak data as String.");
        peakList.add(new String[]{mz, intensity});
    }
    
    public void addPeak(double mz, float intensity) {
        if(this.peakType == PEAK_TYPE.STRING)
            throw new IllegalArgumentException("Scan does not accept peak data as numbers.");
        peakNList.add(new Peak(mz, intensity));
    }

    public String getFragmentationType() {
        return activationType;
    }

    /**
     * The database (msScan table) currently supports a 3 character value for 
     * fragmentationType. We should get a SQL exception if actType is more than 3 characters long.
     */
    private void setFragmentationType(String actType) {
        this.activationType = actType;
    }

    
    // In MS2 files there is a file header if the data is centroided.  We will need to set it 
    // for each scan.
    public void setDataConversionType(DataConversionType convType) {
        this.dataConversionType = convType;
    }
    
    @Override
    public DataConversionType getDataConversionType() {
        return this.dataConversionType;
    }
    
    public int getPeakCount() {
        if(peakList != null)
            return peakList.size();
        else if(peakNList != null)
            return peakNList.size();
        return 0;
    }
    
    public int getMsLevel() {
        return 2;
    }

    public String toString() {
        StringBuilder buf = new StringBuilder();
        buf.append("S\t");
        buf.append(startScan);
//        buf.append(String.format("%06d", startScan));
        buf.append("\t");
        buf.append(endScan);
//        buf.append(String.format("%06d", endScan));
        if (precursorMz != null) {
            buf.append("\t");
            String premz = precursorMz.toString();
            premz = PeakStringBuilder.trimTrailingZerosKeepDecimalPoint(premz);
            buf.append(premz);
        }
        buf.append("\n");
        // charge independent analysis
        for (MS2NameValuePair item: analysisItems) {
            buf.append("I\t");
            buf.append(item.getName());
            if (item.getValue() != null) {
                buf.append("\t");
                buf.append(item.getValue());
            }
            buf.append("\n");
        }
        // charge states along with their charge dependent analysis
        Collections.sort(chargeStates, new Comparator<MS2ScanCharge>() {
            @Override
            public int compare(MS2ScanCharge o1, MS2ScanCharge o2) {
                return Integer.valueOf(o1.getCharge()).compareTo(o2.getCharge());
            }});
        
        for (MS2ScanCharge charge: chargeStates) {
            buf.append(charge.toString());
            buf.append("\n");
        }
        
        // peak data
        List<String[]> peaksStr = getPeaksString();
        for (String[] peak: peaksStr){
            String mass = PeakStringBuilder.trimTrailingZerosKeepDecimalPoint(peak[0]);
            String inten = PeakStringBuilder.trimTrailingZerosKeepDecimalPoint(peak[1]);
            buf.append(mass);
            buf.append("\t");
            buf.append(inten);
            buf.append("\n");
        }
        buf.deleteCharAt(buf.length() - 1); // remove last new-line character
        return buf.toString();
    }
}
