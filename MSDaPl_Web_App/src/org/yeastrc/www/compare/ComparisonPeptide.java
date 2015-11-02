/**
 * ComparisonPeptide.java
 * @author Vagisha Sharma
 * Apr 15, 2009
 * @version 1.0
 */
package org.yeastrc.www.compare;

import java.util.ArrayList;
import java.util.List;

import org.yeastrc.www.compare.dataset.Dataset;
import org.yeastrc.www.compare.dataset.DatasetPeptideInformation;

/**
 * 
 */
public class ComparisonPeptide {

    private int nrseqProteinId;
    private String sequence;
    private int charge;
    
    List<DatasetPeptideInformation> datasetInfo;
    
    public ComparisonPeptide(int nrseqId) {
        this.nrseqProteinId = nrseqId;
        datasetInfo = new ArrayList<DatasetPeptideInformation>();
    }
    
    public int getNrseqId() {
        return nrseqProteinId;
    }

    public List<DatasetPeptideInformation> getDatasetInfo() {
        return datasetInfo;
    }
    
    public void setDatasetInformation(List<DatasetPeptideInformation> infoList) {
        this.datasetInfo = infoList;
    }
    
    public void addDatasetInformation(DatasetPeptideInformation info) {
        datasetInfo.add(info);
    }
    
    public DatasetPeptideInformation getDatasetPeptideInformation(Dataset dataset) {
        
        for(DatasetPeptideInformation dsInfo: datasetInfo) {
            if(dataset.equals(dsInfo.getDataset())) {
                return dsInfo;
            }
        }
        return null;
    }
    
    public boolean isInDataset(Dataset dataset) {
        DatasetPeptideInformation dpi = getDatasetPeptideInformation(dataset);
        if(dpi != null)
            return dpi.isPresent();
        return false;
    }

    public String getSequence() {
        return sequence;
    }
    
    public void setSequence(String sequence) {
        this.sequence = sequence;
    }
    
    public int getCharge() {
    	return charge;
    }
    
    public void setCharge(int charge) {
    	this.charge = charge;
    }
}
