/**
 * SequestResultPlus.java
 * @author Vagisha Sharma
 * Apr 7, 2009
 * @version 1.0
 */
package org.yeastrc.experiment;

import java.math.BigDecimal;
import java.util.List;

import org.yeastrc.ms.domain.run.MsScan;
import org.yeastrc.ms.domain.run.ms2file.MS2Scan;
import org.yeastrc.ms.domain.search.MsSearchResultPeptide;
import org.yeastrc.ms.domain.search.MsSearchResultProtein;
import org.yeastrc.ms.domain.search.ValidationStatus;
import org.yeastrc.ms.domain.search.sequest.SequestResultData;
import org.yeastrc.ms.domain.search.sequest.SequestSearchResult;

/**
 * 
 */
public class SequestResultPlus implements SequestSearchResult {

    private final SequestSearchResult result;
    private final int scanNumber;
    private final BigDecimal retentionTime;
    private double area = -1.0;
    private String filename;
    
    public SequestResultPlus(SequestSearchResult result, MsScan scan) {
        this.result = result;
        this.scanNumber = scan.getStartScanNum();
        this.retentionTime = scan.getRetentionTime();
        if(scan instanceof MS2Scan) {
            MS2Scan scan2 = (MS2Scan) scan;
            area = scan2.getBullsEyeArea();
        }
    }
    
    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }
    
    @Override
    public SequestResultData getSequestResultData() {
        return result.getSequestResultData();
    }
    @Override
    public int getId() {
        return result.getId();
    }
    @Override
    public List<MsSearchResultProtein> getProteinMatchList() {
        return result.getProteinMatchList();
    }
    
    public String getOtherProteinsShortHtml() {
        if(result.getProteinMatchList() == null)
            return null;
        else {
            StringBuilder buf = new StringBuilder();
            int i = 0;
            for(MsSearchResultProtein protein: result.getProteinMatchList()) {
                if(i == 0) {
                    i++;
                    continue;
                }
                buf.append("<br>"+makeShort(protein.getAccession()));
            }
            if(buf.length() > 0)
                buf.delete(0, "<br>".length());
            return buf.toString();
        }
    }
    
    public String getOneProteinShort() {
        if(result.getProteinMatchList() == null)
            return null;
        else {
            return makeShort(result.getProteinMatchList().get(0).getAccession());
        }
    }
    
    private String makeShort(String string) {
    	if(string.length() > 23)
    		return string.substring(0, 20)+"...";
    	else
    		return string;
    }
    
    public int getProteinCount() {
        return result.getProteinMatchList().size();
    }
    
    @Override
    public int getRunSearchId() {
        return result.getRunSearchId();
    }
    @Override
    public int getScanId() {
        return result.getScanId();
    }
    @Override
    public int getCharge() {
        return result.getCharge();
    }
    @Override
    public BigDecimal getObservedMass() {
        return result.getObservedMass();
    }
    @Override
    public MsSearchResultPeptide getResultPeptide() {
        return result.getResultPeptide();
    }
    @Override
    public ValidationStatus getValidationStatus() {
        return result.getValidationStatus();
    }

    public int getScanNumber() {
        return scanNumber;
    }

    public BigDecimal getRetentionTime() {
        return retentionTime;
    }

    public double getArea() {
        return area;
    }
    
    @Override
    public void setCharge(int charge) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setObservedMass(BigDecimal mass) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setResultPeptide(MsSearchResultPeptide resultPeptide) {
        throw new UnsupportedOperationException();
    }
    
}
