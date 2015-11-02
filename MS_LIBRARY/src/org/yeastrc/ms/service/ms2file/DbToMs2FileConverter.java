package org.yeastrc.ms.service.ms2file;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

import org.yeastrc.ms.dao.DAOFactory;
import org.yeastrc.ms.dao.run.ms2file.MS2RunDAO;
import org.yeastrc.ms.dao.run.ms2file.MS2ScanDAO;
import org.yeastrc.ms.domain.run.ms2file.MS2NameValuePair;
import org.yeastrc.ms.domain.run.ms2file.MS2Run;
import org.yeastrc.ms.domain.run.ms2file.MS2Scan;
import org.yeastrc.ms.domain.run.ms2file.MS2ScanCharge;
import org.yeastrc.ms.domain.run.ms2file.impl.MS2Header;
import org.yeastrc.ms.domain.run.ms2file.impl.Scan;
import org.yeastrc.ms.domain.run.ms2file.impl.ScanCharge;
import org.yeastrc.ms.domain.run.ms2file.impl.Scan.PEAK_TYPE;

public class DbToMs2FileConverter {

    private BufferedWriter outFile = null;
    
    public void convertToMs2(int dbRunId, String outputFile) throws IOException {
        
        try {
            outFile = new BufferedWriter(new FileWriter(outputFile));

            MS2RunDAO runDao = DAOFactory.instance().getMS2FileRunDAO();
            MS2Run run = runDao.loadRun(dbRunId);
            if (run == null) {
                System.err.println("No run found with id: "+dbRunId);
                return;
            }
            printMs2Header(run);
            outFile.write("\n");

            MS2ScanDAO scanDao = DAOFactory.instance().getMS2FileScanDAO();
            List<Integer> scanIds = scanDao.loadScanIdsForRun(dbRunId);
            Collections.sort(scanIds);

            for (Integer scanId: scanIds) {
                MS2Scan scan = scanDao.load(scanId);
                printMs2Scan(scan);
                outFile.write("\n");
            }

            outFile.flush();
        }
        finally {
            if (outFile != null)
                outFile.close();
        }
        
    }
    
    private void printMs2Scan(MS2Scan scan) throws IOException {
       Scan ms2scan = new Scan(PEAK_TYPE.STRING);
       ms2scan.setStartScan(scan.getStartScanNum());
       ms2scan.setEndScan(scan.getEndScanNum());
       ms2scan.setPrecursorMz(scan.getPrecursorMz().toString());
       
       // add predicted charge states for the scan
       for (MS2ScanCharge scanCharge: scan.getScanChargeList()) {
           ScanCharge sc = new ScanCharge();
           sc.setCharge(scanCharge.getCharge());
           sc.setMass(scanCharge.getMass());
           for (MS2NameValuePair dAnalysis: scanCharge.getChargeDependentAnalysisList()) {
               sc.addAnalysisItem(dAnalysis.getName(), dAnalysis.getValue());
           }
           ms2scan.addChargeState(sc);
       }
       
       // add charge independent analysis
       for (MS2NameValuePair item: scan.getChargeIndependentAnalysisList()) {
            ms2scan.addAnalysisItem(item.getName(), item.getValue());
       }
       
       // finally, the peak data!
       List<String[]> peaks = scan.getPeaksString();
       for(String[] peak: peaks) {
           ms2scan.addPeak(String.valueOf(peak[0]), String.valueOf(peak[1]));
       }
       
       outFile.write(ms2scan.toString());
    }

    private void printMs2Header(MS2Run run) throws IOException {
        MS2Header ms2Header = new MS2Header(run.getRunFileFormat());
        List<MS2NameValuePair> headerList = run.getHeaderList();
        
        for (MS2NameValuePair header: headerList) {
            ms2Header.addHeaderItem(header.getName(), header.getValue());
        }
        outFile.write(ms2Header.toString());
    }
    
    public static void main (String[] args) {
        DbToMs2FileConverter converter = new DbToMs2FileConverter();
        try {
            long start = System.currentTimeMillis();
            converter.convertToMs2(565, "db2ms2_string.ms2");
            long end = System.currentTimeMillis();
            long timeElapsed = (end - start)/1000;
            System.out.println("Seconds to convert to MS2: "+timeElapsed);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
}
