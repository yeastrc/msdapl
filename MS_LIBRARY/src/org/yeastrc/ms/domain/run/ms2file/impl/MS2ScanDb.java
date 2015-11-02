/**
 * MS2ScanBean.java
 * @author Vagisha Sharma
 * Jul 2, 2008
 * @version 1.0
 */
package org.yeastrc.ms.domain.run.ms2file.impl;

import java.util.ArrayList;
import java.util.List;

import org.yeastrc.ms.domain.run.impl.ScanDb;
import org.yeastrc.ms.domain.run.ms2file.MS2NameValuePair;
import org.yeastrc.ms.domain.run.ms2file.MS2Scan;
import org.yeastrc.ms.domain.run.ms2file.MS2ScanCharge;

/**
 * 
 */
public class MS2ScanDb extends ScanDb implements MS2Scan {

   
    private List<MS2ScanCharge> scanChargeList;
    private List<MS2NameValuePair> chargeIndependentAnalysisList;
    
    public MS2ScanDb() {
        scanChargeList = new ArrayList<MS2ScanCharge>();
        chargeIndependentAnalysisList = new ArrayList<MS2NameValuePair>();
    }
    
    public List<MS2ScanCharge> getScanChargeList() {
        return scanChargeList;
    }

    public void setScanChargeList(List<MS2ScanCharge> scanChargeList) {
        this.scanChargeList = scanChargeList;
    }

    public List<MS2NameValuePair> getChargeIndependentAnalysisList() {
        return chargeIndependentAnalysisList;
    }

    public void setChargeIndependentAnalysisList(List<MS2NameValuePair> chargeIndependentAnalysisList) {
        this.chargeIndependentAnalysisList = chargeIndependentAnalysisList;
    }

    @Override
    public double getBullsEyeArea() {
        List <MS2NameValuePair> analyses = getChargeIndependentAnalysisList();
        for(MS2NameValuePair pair: analyses) {
            // Extract the area of the precursor ion as calculated by Bullseye.
            // Example EZ line: I    EZ    3    4353.2741    0.2220    15650459.0
            // EZ  <charge> <m+h> <retention time> <area>
            if(pair.getName().equalsIgnoreCase("EZ")) {
                return Double.parseDouble(pair.getValue().split("\\s+")[3]);
            }
        }
        return -1.0;
    }

}
