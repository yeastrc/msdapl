/**
 * MsResidueModification.java
 * @author Vagisha Sharma
 * Aug 18, 2008
 * @version 1.0
 */
package org.yeastrc.ms.domain.search;

/**
 * 
 */
public interface MsResidueModificationIn extends MsModification {

    public abstract char getModifiedResidue();
    
    public abstract void setModifiedResidue(char modResidue);
}
