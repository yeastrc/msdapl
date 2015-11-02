/**
 * ProteinferDataset.java
 * @author Vagisha Sharma
 * Nov 13, 2009
 * @version 1.0
 */
package org.yeastrc.www.compare.dataset;

import org.yeastrc.ms.domain.protinfer.ProteinFilterCriteria;

/**
 * 
 */
public class ProteinferDataset extends FilterableDataset {

    private ProteinFilterCriteria filterCriteria;
    
    public ProteinferDataset(Dataset dataset) {
        super(dataset);
        this.filterCriteria = new ProteinFilterCriteria();
    }
    
    @Override
    public ProteinFilterCriteria getProteinFilterCrteria() {
        return filterCriteria;
    }

    public void setProteinFilterCriteria(ProteinFilterCriteria filterCriteria) {
        this.filterCriteria = filterCriteria;
    }

}
