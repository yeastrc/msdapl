/**
 * Cms2FileReader.java
 * @author Vagisha Sharma
 * Feb 12, 2009
 * @version 1.0
 */
package org.yeastrc.ms.parser.ms2File;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.yeastrc.ms.domain.run.DataConversionType;
import org.yeastrc.ms.domain.run.RunFileFormat;
import org.yeastrc.ms.domain.run.ms2file.MS2RunIn;
import org.yeastrc.ms.domain.run.ms2file.MS2ScanIn;
import org.yeastrc.ms.domain.run.ms2file.impl.MS2Header;
import org.yeastrc.ms.domain.run.ms2file.impl.Scan;
import org.yeastrc.ms.domain.run.ms2file.impl.ScanCharge;
import org.yeastrc.ms.domain.run.ms2file.impl.Scan.PEAK_TYPE;
import org.yeastrc.ms.parser.DataProviderException;
import org.yeastrc.ms.parser.MS2RunDataProvider;

import ed.mslib.MS2Scan;
import ed.mslib.ReadMS2Comp;

/**
 * 
 */
public class Cms2FileReader implements MS2RunDataProvider {

    private String sha1Sum;
    private String fileName;
    private ed.mslib.ReadMS2Comp reader = null;
    private DataConversionType dataConversionType;
    
    private static final Pattern nameValPattern = Pattern.compile("([\\S]+)\\s*(.*)");
    
    private static final Logger log = Logger.getLogger(Cms2FileReader.class);
    
    @Override
    public void open(String filePath, String sha1Sum)
            throws DataProviderException {
        this.sha1Sum = sha1Sum;
        try {
            reader = new ReadMS2Comp(new File(filePath));
        }
        catch (FileNotFoundException e) {
            throw new DataProviderException("File not found: "+filePath, e);
        }
        catch (IOException e) {
            throw new DataProviderException("Error reading file", e);
        } 
        fileName = new File(filePath).getName();
    }
    
    @Override
    public MS2RunIn getRunHeader() throws DataProviderException {
       
       
        if(reader.getversion() > 3) {
            DataProviderException e = new DataProviderException("Unsupported version of CMS2 file found. Version: "+reader.getversion());
            log.warn(e.getMessage());
            throw e;
        }
        
        MS2Header header = new MS2Header(RunFileFormat.CMS2);
       
       String h = reader.getheader();
       String[] lines = h.split("\\n");
       for(String line: lines) {
           line = line.trim();
           if(line.length() == 0)
               continue;
           String[] nameAndVal = parseHeader(line);
           if (nameAndVal.length == 2) {
               header.addHeaderItem(nameAndVal[0], nameAndVal[1]);
           }
           else if (nameAndVal.length == 1) {
               DataProviderException e = new DataProviderException("Missing value in 'H' line.Setting value to null. -- "+line);
               log.warn(e.getMessage());
               header.addHeaderItem(nameAndVal[0], null);
           }
           else {
               // ignore if both label and value for this header item are missing
               DataProviderException e = new DataProviderException("Invalid 'H' line; Ignoring. -- "+line);
               log.warn(e.getMessage());
           }
       }
       header.setFileName(fileName);
       header.setSha1Sum(sha1Sum);
       this.dataConversionType = header.getDataConversionType();
       return header;
    }
    
    private String[] parseHeader(String header) {
        return parseNameValue(header, nameValPattern);
    }
    
    @Override
    public void close() {
        reader.closeReader();
    }

    @Override
    public String getFileName() {
        return this.fileName;
    }

    @Override
    public MS2ScanIn getNextScan() throws DataProviderException {
        ed.mslib.MS2Scan ms2Scan = null;
        try {
            ms2Scan = reader.getNextScan();
        }
        catch (IOException e) {
            throw new DataProviderException("Error reading file", e);
        }
        
        if(ms2Scan == null)
            return null;
        
        
        Scan scan = new Scan(PEAK_TYPE.NUMBER);
        scan.setStartScan(ms2Scan.getscan());
        scan.setEndScan(ms2Scan.getendscan());
        scan.setPrecursorMz(String.valueOf(ms2Scan.getprecursor()));
        
        // set the scan charge states
        setScanChargeStates(ms2Scan, scan);
        
        // set the charge independent analyses headers
        setChargeIndependentAnalysis(ms2Scan, scan);
        
        // set the peaks
        setPeaks(ms2Scan, scan);
        
        
        if (!(scan.getPeakCount() > 0) || !(scan.getScanChargeList().size() > 0)) {
            DataProviderException e = new DataProviderException( 
                    "Invalid CMS2 scan -- no valid peaks and/or charge states found for scan: "+scan.getStartScanNum());
            log.warn(e.getMessage()); // warn the user but keep going.
        }
        
        scan.setDataConversionType(this.dataConversionType);
        return scan;
    }

    private void setPeaks(MS2Scan ms2Scan, Scan scan) {
        
        int peakCount = ms2Scan.getmzintlist().size();
        for(int i = 0; i < peakCount; i++) {
            ed.mslib.MzInt mzInt = ms2Scan.getmzint(i);
            scan.addPeak(mzInt.getmz(), mzInt.getint()); 
        }
    }

    private void setChargeIndependentAnalysis(ed.mslib.MS2Scan ms2Scan, Scan scan) {
        int numILines = ms2Scan.numberOfILines();
        for(int i = 0; i < numILines; i++) {
            String iField = ms2Scan.getIfield(i).trim();
            String[] nameAndVal = parseNameValue(iField, nameValPattern);
            if (nameAndVal.length == 2) {
                scan.addAnalysisItem(nameAndVal[0], nameAndVal[1]);
            }
            else if (nameAndVal.length == 1) {
                scan.addAnalysisItem(nameAndVal[0], null);
                DataProviderException e = new DataProviderException("Missing value in 'I' line. Setting value to null --"+iField);
                log.debug(e.getMessage());
            }
            else {
                // ignore if both label and value for this analysis item are missing
                DataProviderException e = new DataProviderException("Invalid 'I' line. Expected 2 fields. Ignoring -- "+iField);
                log.debug(e.getMessage());
            }
        }
    }

    
    private void setScanChargeStates(ed.mslib.MS2Scan ms2Scan, Scan scan) {
        int numChgStates = ms2Scan.getchargeslist().size();
        ScanCharge sc = null;
        for(int i = 0; i < numChgStates; i++) {
            sc = new ScanCharge();
            sc.setCharge(ms2Scan.getcharge(i));
            sc.setMass(new BigDecimal(String.valueOf(ms2Scan.getmass(i))));
            scan.addChargeState(sc);
        }
        
        // According to the MS2Format D lines should be associated with Z lines -- S(I)[Z(D)]^k[m/z intensity]^n
        // However the .cms2 format lists all D lines after Z lines.  
        if(sc != null) {
            
              // NOTE: It appears .cms2 files do hot have any 'D' lines. 
//            int numDLines = ms2Scan.numberOfDLines();
//            for(int i = 0; i < numDLines; i++) {
//                String dField = ms2Scan.getDField(i).trim();
//                String[] nameAndVal = parseNameValue(dField, nameValPattern);
//                if (nameAndVal.length == 2) {
//                    scan.addAnalysisItem(nameAndVal[0], nameAndVal[1]);
//                }
//                else if (nameAndVal.length == 1) {
//                    scan.addAnalysisItem(nameAndVal[0], null);
//                    DataProviderException e = new DataProviderException("Missing value in 'D' line. Setting value to null -- "+dField);
//                    log.warn(e.getMessage());
//                }
//                else {
//                    // ignore if both label and value for this analysis item are missing
//                    DataProviderException e = new DataProviderException("Invalid 'D' line. Expected 2 fields. Ignoring -- "+dField);
//                    log.warn(e.getMessage());
//                }
//            }
        }
    }
    
    
    protected final String[] parseNameValue(String line, Pattern pattern) {
        Matcher match = pattern.matcher(line);
        if (match.matches()) {
            String val = match.group(2);
            if (val != null && val.length() == 0)
                return new String[]{match.group(1)};
            else
                return new String[]{match.group(1), val};
        }
        else
            return new String[0];
    }

}
