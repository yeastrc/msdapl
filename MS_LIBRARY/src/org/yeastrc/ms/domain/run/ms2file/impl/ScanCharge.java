/**
 * ScanCharge.java
 * @author Vagisha Sharma
 * Jun 16, 2008
 * @version 1.0
 */
package org.yeastrc.ms.domain.run.ms2file.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.yeastrc.ms.domain.run.ms2file.MS2NameValuePair;
import org.yeastrc.ms.domain.run.ms2file.MS2ScanCharge;
import org.yeastrc.ms.util.PeakStringBuilder;

/**
 * Represents a "Z" line (and any following "D" lines) from a MS2 file.  Describes the charge for a scan.
 * A scan can have multiple predicted charges
 */
public class ScanCharge implements MS2ScanCharge {

    private int charge;
    private BigDecimal mass;
    private List<MS2NameValuePair> analysisItems;

    public ScanCharge() {
        analysisItems = new ArrayList<MS2NameValuePair>();
    }

    /**
     * @return the charge
     */
    public int getCharge() {
        return charge;
    }
    /**
     * @param charge the charge to set
     */
    public void setCharge(int charge) {
        this.charge = charge;
    }
    /**
     * @return the mass
     */
    public BigDecimal getMass() {
        return mass;
    }

    public void setMass(BigDecimal mass) {
        this.mass = mass;
    }

    public void addAnalysisItem(String label, String value) {
        if (label == null)   return;
        analysisItems.add(new NameValuePair(label, value));
    }

    public String toString() {
        StringBuilder buf = new StringBuilder();
        buf.append("Z\t");
        buf.append(charge);
        buf.append("\t");
        String massS = mass.toString();
        massS = PeakStringBuilder.trimTrailingZerosKeepDecimalPoint(massS);
        buf.append(massS);
        buf.append("\n");
        for (MS2NameValuePair item: analysisItems) {
            buf.append("D\t");
            buf.append(item.getName());
            if (item.getValue() != null) {
                buf.append("\t");
                buf.append(item.getValue());
            }
            buf.append("\n");
        }
        buf.deleteCharAt(buf.length() - 1); // remove last new-line character
        return buf.toString();
    }

    @Override
    public List<MS2NameValuePair> getChargeDependentAnalysisList() {
        return analysisItems;
    }
    
    public void setChargeDependentAnalysisList(List<MS2NameValuePair> analysisItems) {
        this.analysisItems = analysisItems;
    }
}
