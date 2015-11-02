/**
 * MsResidueModificationDbImpl.java
 * @author Vagisha Sharma
 * Aug 18, 2008
 * @version 1.0
 */
package org.yeastrc.ms.domain.search.impl;

import java.math.BigDecimal;

import org.yeastrc.ms.domain.search.MsResidueModification;
import org.yeastrc.ms.domain.search.MsResidueModificationIn;

/**
 * 
 */
public class MsResidueModificationWrap implements MsResidueModification {

    private int searchId;
    private MsResidueModificationIn mod;

    public MsResidueModificationWrap(MsResidueModificationIn mod, int searchId) {
        this.mod = mod;
        this.searchId = searchId;
    }
    
    /**
     * @return the id
     */
    public int getId() {
        throw new UnsupportedOperationException();
    }

    /**
     * @return the searchId
     */
    public int getSearchId() {
        return searchId;
    }

    @Override
    public char getModifiedResidue() {
        return mod.getModifiedResidue();
    }

    @Override
    public BigDecimal getModificationMass() {
        return mod.getModificationMass();
    }

    @Override
    public char getModificationSymbol() {
        return mod.getModificationSymbol();
    }

    @Override
    public void setModifiedResidue(char modResidue) {
        this.mod.setModifiedResidue(modResidue);
    }

    @Override
    public void setModificationMass(BigDecimal modMass) {
        this.mod.setModificationMass(modMass);
    }

    @Override
    public void setModificationSymbol(char modSymbol) {
        this.mod.setModificationSymbol(modSymbol);
    }
   
}
