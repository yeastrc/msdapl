/**
 * ProteinProphetDataset.java
 * @author Vagisha Sharma
 * Nov 13, 2009
 * @version 1.0
 */
package org.yeastrc.www.compare.dataset;

import org.yeastrc.ms.domain.protinfer.proteinProphet.ProteinProphetFilterCriteria;
import org.yeastrc.ms.domain.protinfer.proteinProphet.ProteinProphetROC;

/**
 * 
 */
public class ProteinProphetDataset extends FilterableDataset {

    private ProteinProphetFilterCriteria filterCriteria;
    private ProteinProphetROC roc;
    
    public ProteinProphetDataset() {}
    
    public ProteinProphetDataset(Dataset dataset) {
        super(dataset);
        this.filterCriteria = new ProteinProphetFilterCriteria();
    }

    @Override
    public ProteinProphetFilterCriteria getProteinFilterCrteria() {
        return filterCriteria;
    }

    public void setProteinFilterCriteria(ProteinProphetFilterCriteria filterCriteria) {
        this.filterCriteria = filterCriteria;
    }
    
    public ProteinProphetROC getRoc() {
        return roc;
    }
    
    public void setRoc(ProteinProphetROC roc) {
        this.roc = roc;
    }
    
    public double getProbabilityForDefaultError() {
        return roc.getMinProbabilityForError(0.01);
    }
    
    public void setProbabilityForError(double error) {
        if(roc != null) {
            filterCriteria.setMinGroupProbability(roc.getMinProbabilityForError(error));
        }
    }
    
    public void setProbabilityForDefaultError() {
        setProbabilityForError(0.01);
    }
}
