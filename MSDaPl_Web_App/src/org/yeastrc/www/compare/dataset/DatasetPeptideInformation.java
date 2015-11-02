/**
 * DatasetPeptideInformation.java
 * @author Vagisha Sharma
 * Apr 15, 2009
 * @version 1.0
 */
package org.yeastrc.www.compare.dataset;


/**
 * 
 */
public class DatasetPeptideInformation {

    private boolean present;
    private boolean unique;
    private int spectrumCount;
    private final Dataset dataset;
    
    public DatasetPeptideInformation(Dataset dataset) {
        this.dataset = dataset;
    }
    
    public Dataset getDataset() {
        return dataset;
    }
    
    public int getDatasetId() {
        return dataset.getDatasetId();
    }
    
    public DatasetSource getDatasetSource() {
        return dataset.getSource();
    }
    
    public boolean isPresent() {
        return present;
    }
    public void setPresent(boolean present) {
        this.present = present;
    }
    
    public boolean isUnique() {
        return unique;
    }
    public void setUnique(boolean unique) {
        this.unique = unique;
    }

    public int getSpectrumCount() {
        return spectrumCount;
    }

    public void setSpectrumCount(int spectrumCount) {
        this.spectrumCount = spectrumCount;
    }
}
