/**
 * DatasetGoInformation.java
 * @author Vagisha Sharma
 * Jun 16, 2009
 * @version 1.0
 */
package org.yeastrc.www.compare.dataset;


/**
 * 
 */
public class DatasetGoInformation {

    private boolean present;
    private final Dataset dataset;
    
    public DatasetGoInformation(Dataset dataset) {
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
}
