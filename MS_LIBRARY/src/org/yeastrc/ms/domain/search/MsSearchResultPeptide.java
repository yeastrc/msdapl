/**
 * MsSearchResultPeptide.java
 * @author Vagisha Sharma
 * Jul 11, 2008
 * @version 1.0
 */
package org.yeastrc.ms.domain.search;

import java.util.List;

import org.yeastrc.ms.service.ModifiedSequenceBuilderException;


/**
 * 
 */
public interface MsSearchResultPeptide {

    /**
     * Returns a list of dynamic residue modifications, along with the index (0-based) at which 
     * they are present, in the peptide sequence for this result.
     */
    public abstract List<MsResultResidueMod> getResultDynamicResidueModifications();
    
    public abstract void setDynamicResidueModifications(List<MsResultResidueMod> dynaMods);
    
    /**
     * Returns a list of dynamic terminal modifications.
     * @return
     */
    public abstract List<MsResultTerminalMod> getResultDynamicTerminalModifications();

    public abstract void setDynamicTerminalModifications(List<MsResultTerminalMod> termDynaMods);
    
    
    public abstract String getPeptideSequence();
    
    public abstract void setPeptideSequence(String sequence);
    
    
    /**
     * Returns the modified peptide sequence: e.g. PEP[177]TIDE
     * The number in the square brackets is the modification mass plus the mass of the amino acid.
     * @return
     * @throws ModifiedSequenceBuilderException 
     */
    public abstract String getModifiedPeptide() throws ModifiedSequenceBuilderException;
    
    /**
     * Returns the modified peptide sequence.
     * If massDiffOnly is true, only the modification mass is displayed within the square brackets:
     *  e.g. PEP[+80]TIDE
     * Otherwise, the mass of the residue is added to the modification mass
     *  e.g. PEP[177]TIDE
     * @return 
     * @throws ModifiedSequenceBuilderException 
     */
    public abstract String getModifiedPeptide(boolean massDiffOnly) throws ModifiedSequenceBuilderException;
    
    /**
     * Returns the modified peptide along with the pre and post residues. e.g. K.PEP[177]TIDE.L
     * The number in the square brackets is the modification mass plus the mass of the amino acid.
     * @return
     * @throws ModifiedSequenceBuilderException 
     */
    public abstract String getFullModifiedPeptide() throws ModifiedSequenceBuilderException;
    
    /**
     * Returns the modified peptide along with the pre and post residues. 
     * If massDiffOnly is true, only the modification mass is displayed within the square brackets:
     *  e.g. K.PEP[+80]TIDE.L
     * Otherwise, the mass of the residue is added to the modification mass
     *  e.g. K.PEP[177]TIDE.L
     * @return
     * @throws ModifiedSequenceBuilderException 
     */
    public abstract String getFullModifiedPeptide(boolean massDiffOnly) throws ModifiedSequenceBuilderException;
    
    
    /**
     * Returns the modified peptide in a program specific format. 
     * @return
     */
    public abstract String getModifiedPeptidePS();
    
    public abstract String getModifiedPeptidePS(boolean includeTermMods);
    
    /**
     * Returns the modified peptide along with the pre and post residues
     * @return
     */
    public abstract String getFullModifiedPeptidePS();
    
    public abstract String getFullModifiedPeptidePS(boolean includeTermMods);
    
    
    public abstract char getPreResidue();
    
    public abstract void setPreResidue(char preResidue);
    
    
    public abstract char getPostResidue();
    
    public abstract void setPostResidue(char postResidue);
    
    
    public abstract int getSequenceLength();
    
    public abstract boolean hasDynamicModification();
}
