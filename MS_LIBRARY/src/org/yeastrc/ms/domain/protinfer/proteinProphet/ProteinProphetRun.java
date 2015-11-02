/**
 * ProteinProphetRun.java
 * @author Vagisha Sharma
 * Jul 28, 2009
 * @version 1.0
 */
package org.yeastrc.ms.domain.protinfer.proteinProphet;

import java.util.ArrayList;
import java.util.List;

import org.yeastrc.ms.domain.protinfer.GenericProteinferRun;
import org.yeastrc.ms.domain.protinfer.ProteinferInput;

/**
 * 
 */
public class ProteinProphetRun extends GenericProteinferRun<ProteinferInput> {

    private String filename;
    private List<ProteinProphetParam> params;
    private ProteinProphetROC roc;
    
    public ProteinProphetRun() {
        params = new ArrayList<ProteinProphetParam>();
    }

    public List<ProteinProphetParam> getParams() {
        return params;
    }

    public void setParams(List<ProteinProphetParam> params) {
        this.params = params;
    }

    public ProteinProphetROC getRoc() {
        return roc;
    }

    public void setRoc(ProteinProphetROC roc) {
        this.roc = roc;
    }
    
    public void setRocPoints(List<ProteinProphetROCPoint> points) {
        this.roc = new ProteinProphetROC();
        roc.setProteinferId(this.getId());
        roc.setRocPoints(points);
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }
    
}
