/**
 * MsScan.java
 * @author Vagisha Sharma
 * Jun 16, 2008
 * @version 1.0
 */
package org.yeastrc.ms.domain.run.impl;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

import org.yeastrc.ms.domain.run.DataConversionType;
import org.yeastrc.ms.domain.run.MsScan;
import org.yeastrc.ms.domain.run.Peak;
import org.yeastrc.ms.domain.run.PeakStorageType;
import org.yeastrc.ms.util.PeakConverter;

/**
 * 
 */
public class ScanDb implements MsScan {

    private int runId;  // database id of the run this scan belongs to
    
    private int id;     // database id of this scan
    
    private int startScanNum = -1;
    private int endScanNum = -1;
    
    private int msLevel = 0; // 1 for MS1, 2 for MS2 and so on
    private BigDecimal retentionTime;
    private String fragmentationType; 
    
    private BigDecimal precursorMz;  // 0 if this is a MS1 scan
    private int precursorScanId = 0; // id (database) of the precursor scan.  0 if this is a MS1 scan
    private int precursorScanNum = -1; // scan number of the precursor scan
    
    private DataConversionType dataConversionType;
    
    private int peakCount;
    
//    private String peakString;
    
    private byte[] peakData;
    private PeakStorageType storageType;
    
    public ScanDb() {
        peakData = new byte[0];
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
    
    public int getRunId() {
        return runId;
    }

    public void setRunId(int runId) {
        this.runId = runId;
    }

    public int getStartScanNum() {
        return startScanNum;
    }

    public void setStartScanNum(int startScanNum) {
        this.startScanNum = startScanNum;
    }

    public int getEndScanNum() {
        return endScanNum;
    }

    public void setEndScanNum(int endScanNum) {
        this.endScanNum = endScanNum;
    }

    public int getPrecursorScanNum() {
        return precursorScanNum;
    }

    public void setPrecursorScanNum(int precursorScanNum) {
        this.precursorScanNum = precursorScanNum;
    }
    
    public int getPrecursorScanId() {
        return precursorScanId;
    }

    public void setPrecursorScanId(int precursorScanId) {
        this.precursorScanId = precursorScanId;
    }
    
    public int getMsLevel() {
        return msLevel;
    }

    public void setMsLevel(int msLevel) {
        this.msLevel = msLevel;
    }

    public String getFragmentationType() {
        return fragmentationType;
    }

    public void setFragmentationType(String fragmentationType) {
        this.fragmentationType = fragmentationType;
    }
    
    public BigDecimal getRetentionTime() {
        return retentionTime;
    }
    
    public void setRetentionTime(BigDecimal retentionTime) {
        this.retentionTime = retentionTime;
    }
    
    public BigDecimal getPrecursorMz() {
        return precursorMz;
    }

    public void setPrecursorMz(BigDecimal precursorMz) {
        this.precursorMz = precursorMz;
    }

    public DataConversionType getDataConversionType() {
        return this.dataConversionType;
    }
    
    public void setDataConversionType(DataConversionType convType) {
        this.dataConversionType = convType;
    }
    
    public int getPeakCount() {
        return this.peakCount;
    }
    
    public void setPeakCount(int peakCount) {
        this.peakCount = peakCount;
    }
    
//    public void setPeakData(String peaks) {
//        this.peakString = peaks;
//    }
    
    public void setPeakData(byte[] peakData) {
        this.peakData = peakData;
    }

    @Override
    public List<Peak> getPeaks() throws IOException {
        return PeakConverter.instance().convert(peakData, this.storageType);
    }

    @Override
    public List<String[]> getPeaksString() throws IOException {
        return PeakConverter.instance().convertToStringPeaks(peakData, this.storageType);
    }

    @Override
    public PeakStorageType getPeakStorageType() {
        return this.storageType;
    }

    public void setPeakStorageType(PeakStorageType type) {
        this.storageType = type;
    }
    
}
