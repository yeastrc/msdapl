/**
 * FilterableDataset.java
 * @author Vagisha Sharma
 * Nov 15, 2009
 * @version 1.0
 */
package org.yeastrc.www.compare.dataset;

import org.yeastrc.ms.domain.protinfer.ProteinFilterCriteria;

/**
 * 
 */
public abstract class FilterableDataset extends Dataset {

    public FilterableDataset() {}
    
    public FilterableDataset(Dataset dataset) {
        super(dataset.getDatasetId(), dataset.getSource());
        super.setDatasetComments(dataset.getDatasetComments());
        super.setDatasetName(dataset.getDatasetName());
        super.setSpectrumCount(dataset.getSpectrumCount());
        super.setMinProteinSpectrumCount(dataset.getMinProteinSpectrumCount());
        super.setMaxProteinSpectrumCount(dataset.getMaxProteinSpectrumCount());
        super.setSpectrumCountNormalizationFactor(dataset.getSpectrumCountNormalizationFactor());
    }
    
    public abstract ProteinFilterCriteria getProteinFilterCrteria();
}
