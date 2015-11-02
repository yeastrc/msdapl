/**
 * PepXmlProteinMatch.java
 * @author Vagisha Sharma
 * Jul 28, 2009
 * @version 1.0
 */
package org.yeastrc.ms.domain.search.pepxml;

/**
 * 
 */
public interface PepXmlProteinMatch {

    public abstract int getProteinMatchId();
    
    public abstract char getNtermResidue();
    
    public abstract char getCtermResidue();
    
    public abstract int getNumEnzymaticTermini();
}
