/**
 * Ms2FileHeader.java
 * @author Vagisha Sharma
 * Jun 16, 2008
 * @version 1.0
 */
package org.yeastrc.ms.domain.run.ms2file.impl;

import java.util.ArrayList;
import java.util.List;

import org.yeastrc.ms.domain.general.MsEnzymeIn;
import org.yeastrc.ms.domain.run.DataConversionType;
import org.yeastrc.ms.domain.run.RunFileFormat;
import org.yeastrc.ms.domain.run.ms2file.MS2NameValuePair;
import org.yeastrc.ms.domain.run.ms2file.MS2RunIn;

/**
 * 
 */
public class MS2Header implements MS2RunIn {

    private List<MS2NameValuePair> headerList;
    private String fileName;
    private String sha1Sum;
    private String creationDate;
    private String extractor;
    private String extractorVersion;
    private String extractorOptions;
    private String instrumentModel;
    private String instrumentSN;
    private String acquisionMethod;
    private DataConversionType dataConversionType = DataConversionType.UNKNOWN;
    private StringBuilder comment;
    private RunFileFormat format;
    
    public MS2Header(RunFileFormat format) {
        headerList = new ArrayList<MS2NameValuePair>();
        comment = new StringBuilder();
        this.format = format;
    }
    
    public void addHeaderItem(String name, String value) {
        
        if (name == null)
//            throw new NullPointerException("name for Header cannot be null.");
            return;
        
        headerList.add(new NameValuePair(name, value));
        
        // if there is no value for this header ignore it; It will still get added to the 
        // headerItems list. 
        if (value == null || value.trim().length() == 0)
            return;
        
        if (isCreationDate(name))
            creationDate = value;
        if (isExtractor(name))
            extractor = value;
        if (isExtractorVersion(name)) 
            extractorVersion = value;
        if (isExtractorOptions(name))
            extractorOptions = value;
        if (isInstrumentModel(name))
            instrumentModel = value;
        if (isInstrumentSN(name))
            instrumentSN = value;
        if (isAcquisitionMethod(name))
            acquisionMethod = value;
        if (isDataType(name)) 
            setDataConversionType(value);
        if (isComment(name)) {
            comment.append(value+";");
        }
    }

    public int headerCount() {
        return headerList.size();
    }
    
    public boolean isValid() {
        if (creationDate        == null ||
            extractor           == null ||
            extractorVersion    == null ||
            extractorOptions    == null ||
            sha1Sum             == null ||
            fileName            == null)
            return false;
        return true;
    }
    
    public void setSha1Sum(String sha1Sum) {
        this.sha1Sum = sha1Sum;
    }
    
    public void setFileName(String fileName) {
        int idx = fileName.lastIndexOf("."+format.name().toLowerCase());
        if(idx == -1)
            idx = fileName.lastIndexOf("."+format.name());
        if(idx == -1)
            this.fileName = fileName;
        else
            this.fileName = fileName.substring(0, idx);
    }
    
    public String getSha1Sum() {
        return this.sha1Sum;
    }
    
    public String getFileName() {
        return fileName;
    }
    
    public String getCreationDate() {
        return creationDate;
    }
    
    public String getInstrumentModel() {
        return instrumentModel;
    }
    
    public String getInstrumentSN() {
        return instrumentSN;
    }
    
    public String getConversionSW() {
        return extractor;
    }
    
    public String getConversionSWVersion() {
        return extractorVersion;
    }
    
    public String getConversionSWOptions() {
        return extractorOptions;
    }
    
    public String getAcquisitionMethod() {
        return acquisionMethod;
    }
    
    private void setDataConversionType(String value) {
        if (value.toUpperCase().contains(DataConversionType.CENTROID.name()))
            this.dataConversionType = DataConversionType.CENTROID;
        else
            this.dataConversionType = DataConversionType.NON_CENTROID;
    }
    
    public DataConversionType getDataConversionType() {
        return this.dataConversionType;
    }
    
    public String getComment() {
        if (comment == null || comment.length() == 0)    return null;
        return comment.deleteCharAt(comment.length() -1).toString(); // delete last semi-colon
    }
    
    public List<MS2NameValuePair> getHeaderList() {
        return headerList;
    }

    // MS2 files don't have any enzyme information
    public List<MsEnzymeIn> getEnzymeList() {
        return new ArrayList<MsEnzymeIn>(0);
    }

    public String getInstrumentVendor() {
        return null;
    }

    public RunFileFormat getRunFileFormat() {
        return this.format;
    }

    public String toString() {
        
        StringBuilder buf = new StringBuilder();
        for (MS2NameValuePair headerItem: headerList) {
            if (headerItem == null)
                continue;
            buf.append("H\t");
            buf.append(headerItem.getName());
            if (headerItem.getValue() != null) {
                buf.append("\t");
                buf.append(headerItem.getValue());
            }
            buf.append("\n");
        }
        buf.deleteCharAt(buf.length() - 1); // remove the last new line character.
        return buf.toString();
    }
    
    private boolean isCreationDate(String value) {
        return value.equalsIgnoreCase("CreationDate");
    }
    
    private boolean isExtractor(String value) {
        return value.equalsIgnoreCase("Extractor");
    }
    
    private boolean isExtractorVersion(String value) {
        return value.equalsIgnoreCase("ExtractorVersion");
    }
    
    private boolean isExtractorOptions(String value) {
        return value.equalsIgnoreCase("ExtractorOptions");
    }
    
    private boolean isInstrumentModel(String value) {
        return value.equalsIgnoreCase("InstrumentType");
    }
    
    private boolean isInstrumentSN(String value) {
        return value.equalsIgnoreCase("InstrumentSN");
    }
    
    private boolean isAcquisitionMethod(String value) {
        return value.equalsIgnoreCase("AcquisitionMethod");
    }
    
    private boolean isDataType(String value) {
        return value.equalsIgnoreCase("DataType");
    }
    
    private boolean isComment(String value) {
        return value.startsWith("Comment");
    }
    
}
