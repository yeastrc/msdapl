/**
 * ProteinProphetProteinPeptide.java
 * @author Vagisha Sharma
 * Jul 29, 2009
 * @version 1.0
 */
package org.yeastrc.ms.domain.protinfer.proteinProphet;

import org.yeastrc.ms.domain.protinfer.GenericProteinferPeptide;
import org.yeastrc.ms.domain.protinfer.ProteinferSpectrumMatch;

/**
 * 
 */
public class ProteinProphetProteinPeptide extends 
    GenericProteinferPeptide<ProteinferSpectrumMatch, ProteinProphetProteinPeptideIon> {

    private int numEnzymaticTermini;
    

    public ProteinProphetProteinPeptide() {
        super();
    }
    
    @Override
    protected ProteinProphetProteinPeptide newPeptide() {
        return new ProteinProphetProteinPeptide();
    }
    
    public int getNumEnzymaticTermini() {
        return numEnzymaticTermini;
    }

    public void setNumEnzymaticTermini(int numEnzymaticTermini) {
        this.numEnzymaticTermini = numEnzymaticTermini;
    }
}
