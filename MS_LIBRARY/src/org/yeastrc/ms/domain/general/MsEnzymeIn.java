/**
 * MsEnzyme.java
 * @author Vagisha Sharma
 * Jul 11, 2008
 * @version 1.0
 */
package org.yeastrc.ms.domain.general;

import org.yeastrc.ms.domain.general.MsEnzyme.Sense;

public interface MsEnzymeIn {

    /**
     * Name of the enzyme
     * @return the name
     */
    public abstract String getName();

    /**
     * Sense repesents the terminal (C term or N term) where the enzyme cleaves.
     * @return the sense
     */
    public abstract Sense getSense();

    /**
     * Amino acid residue(s) where the enzyme cleaves.
     * Example: KR for enyme Trypsin
     * @return 
     */
    public abstract String getCut();

    /**
     * Amino acid(s), which when present next to the cleavage site inhibit enzyme action.
     * @return the nocut
     */
    public abstract String getNocut();

    /**
     * @return the description, if any
     */
    public abstract String getDescription();

}