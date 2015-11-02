/**
 * ResultResidueModBean.java
 * @author Vagisha Sharma
 * Sep 6, 2008
 * @version 1.0
 */
package org.yeastrc.ms.domain.search.impl;

import java.math.BigDecimal;

import org.yeastrc.ms.domain.search.MsResultResidueMod;

/**
 * 
 */
public class ResultResidueModBean extends ResidueModification implements MsResultResidueMod {

    private int modPosition = -1;
    
    public ResultResidueModBean() {}
    
    public ResultResidueModBean(char modifiedResidue, char modificationSymbol, BigDecimal modificationMass, int modifiedPosition) {
    	super(modifiedResidue, modificationSymbol, modificationMass);
    	this.modPosition = modifiedPosition;
    }
    /**
     * @return the modPosition
     */
    public int getModifiedPosition() {
        return modPosition;
    }
    /**
     * @param modPosition the modPosition to set
     */
    public void setModifiedPosition(int modPosition) {
        this.modPosition = modPosition;
    }
}
