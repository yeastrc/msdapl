package org.yeastrc.ms.domain.search.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.yeastrc.ms.domain.search.MsModification;
import org.yeastrc.ms.domain.search.MsResultResidueMod;
import org.yeastrc.ms.domain.search.MsResultTerminalMod;
import org.yeastrc.ms.domain.search.MsSearchResultPeptide;
import org.yeastrc.ms.domain.search.MsTerminalModification.Terminal;
import org.yeastrc.ms.service.ModifiedSequenceBuilder;
import org.yeastrc.ms.service.ModifiedSequenceBuilderException;
import org.yeastrc.ms.util.AminoAcidUtilsFactory;
import org.yeastrc.ms.util.BaseAminoAcidUtils;

public class SearchResultPeptideBean  implements MsSearchResultPeptide {

    
    private char[] sequence;
    private String modifiedSequence;
    private String modifiedSequence_massDiffOnly;
    private char preResidue = MsModification.EMPTY_CHAR;
    private char postResidue = MsModification.EMPTY_CHAR;
    
    private List<MsResultResidueMod> dynaResidueMods;
    private List<MsResultTerminalMod> dynaTerminalMods;
    
    
    public SearchResultPeptideBean() {
        dynaResidueMods = new ArrayList<MsResultResidueMod>();
        dynaTerminalMods = new ArrayList<MsResultTerminalMod>();
    }

    @Override
    public String getPeptideSequence() {
        return String.valueOf(sequence);
    }

    @Override
    public int getSequenceLength() {
        if (sequence != null)
            return sequence.length;
        return 0;
    }
    
    /**
     * @param preResidue the preResidue to set
     */
    public void setPreResidue(char preResidue) {
        this.preResidue = preResidue;
    }
    
    @Override
    public char getPreResidue() {
        return preResidue;
    }
    
    /**
     * @param postResidue the postResidue to set
     */
    public void setPostResidue(char postResidue) {
        this.postResidue = postResidue;
    }
    
    @Override
    public char getPostResidue() {
        return postResidue;
    }
    

    public void setPeptideSequence(String sequence) {
        this.sequence = sequence.toCharArray();
    }
    
    //-----------------------------------------------------------------------------------------
    // DYNAMIC MODIFICATIONS
    //--------------------------------------------------------------------------------------O---
    public List<MsResultResidueMod> getResultDynamicResidueModifications() {
        return (List<MsResultResidueMod>) dynaResidueMods;
    }
    
    public void setDynamicResidueModifications(List<MsResultResidueMod> dynaMods) {
        this.dynaResidueMods = dynaMods;
        this.modifiedSequence = null;
        this.modifiedSequence_massDiffOnly = null;
    }
    
    //-----------------------------------------------------------------------------------------
    // TERMINAL DYNAMIC MODIFICATIONS
    //-----------------------------------------------------------------------------------------
    public List<MsResultTerminalMod> getResultDynamicTerminalModifications() {
        return (List<MsResultTerminalMod>) dynaTerminalMods;
    }
    
    public void setDynamicTerminalModifications(List<MsResultTerminalMod> termDynaMods) {
        this.dynaTerminalMods = termDynaMods;
        this.modifiedSequence = null;
        this.modifiedSequence_massDiffOnly = null;
    }
    
    /**
     * Returns the modified peptide in a program specific format. Terminal modifications are included
     * @return
     */
    public String getModifiedPeptidePS() {
    	
    	return getModifiedPeptidePS(true);
    }
    
    /**
     * Returns the modified peptide in a program specific format
     * @return
     */
    public String getModifiedPeptidePS(boolean includeTermMods) {
        
        
        if (dynaResidueMods.size() == 0 && dynaTerminalMods.size() == 0) {
//            modifiedSequence = preResidue+"."+String.valueOf(sequence)+"."+postResidue;
            return String.valueOf(sequence);
        }
        else {
            String origseq = String.valueOf(sequence);
            int lastIdx = 0;
            StringBuilder seq = new StringBuilder();
            
            if(includeTermMods) {
	            // Add any N-term modification
	            for(MsResultTerminalMod termMod: dynaTerminalMods) {
	            	if(termMod.getModifiedTerminal() == Terminal.NTERM) {
	            		char modSymbol = termMod.getModificationSymbol();
	                    if(modSymbol == MsModification.EMPTY_CHAR) {
	                        seq.append("["+Math.round(termMod.getModificationMass().doubleValue() + BaseAminoAcidUtils.NTERM_MASS)+"]");
	                    }
	                    else {
	                        seq.append(modSymbol);
	                    }
	            	}
	            }
            }
            
            sortDynaResidueModifications();
            for (MsResultResidueMod mod: dynaResidueMods) {
                seq.append(origseq.subSequence(lastIdx, mod.getModifiedPosition()+1)); // get sequence up to an including the modified position.
                char modSymbol = mod.getModificationSymbol();
                if(modSymbol == MsModification.EMPTY_CHAR) {
                    seq.append("["+Math.round(mod.getModificationMass().doubleValue() +
                            AminoAcidUtilsFactory.getAminoAcidUtils().monoMass(origseq.charAt(mod.getModifiedPosition())))+"]");
                }
                else {
                    seq.append(modSymbol);
                }
                
                lastIdx = mod.getModifiedPosition()+1;
            }
            if (lastIdx < origseq.length())
                seq.append(origseq.subSequence(lastIdx, origseq.length()));
            
            if(includeTermMods) {
	            // Add any C-term modification
	            for(MsResultTerminalMod termMod: dynaTerminalMods) {
	            	if(termMod.getModifiedTerminal() == Terminal.CTERM) {
	            		char modSymbol = termMod.getModificationSymbol();
	                    if(modSymbol == MsModification.EMPTY_CHAR) {
	                        seq.append("["+Math.round(termMod.getModificationMass().doubleValue() + BaseAminoAcidUtils.CTERM_MASS)+"]");
	                    }
	                    else {
	                        seq.append(modSymbol);
	                    }
	            	}
	            }
            }
            
            return seq.toString();
        }
    }
    
    /**
     * Returns the modified peptide sequence: e.g. PEP[177]TIDE
     * The number in the square brackets is the modification mass plus the mass of the amino acid.
     * @return
     * @throws ModifiedSequenceBuilderException 
     */
    @Override
    public String getModifiedPeptide() throws ModifiedSequenceBuilderException {
        
        if (modifiedSequence != null)
            return modifiedSequence;
        
        if (dynaResidueMods.size() == 0) {
            modifiedSequence = String.valueOf(sequence);
        }
        else {
            
            String origseq = String.valueOf(sequence);
            modifiedSequence = ModifiedSequenceBuilder.build(origseq, dynaResidueMods, dynaTerminalMods);
        }
        
        return modifiedSequence;
    }
    
    /**
     * Returns the modified peptide sequence.
     * If massDiffOnly is true, only the modification mass is displayed within the square brackets:
     *  e.g. PEP[+80]TIDE
     * Otherwise, the mass of the residue is added to the modification mass
     *  e.g. PEP[177]TIDE
     * @return 
     * @throws ModifiedSequenceBuilderException 
     */
    public String getModifiedPeptide(boolean massDiffOnly) throws ModifiedSequenceBuilderException {
    	
    	if(!massDiffOnly) {
    		return getModifiedPeptide();
    	}
    	
    	if (modifiedSequence_massDiffOnly != null)
            return modifiedSequence_massDiffOnly;
        
        if (dynaResidueMods.size() == 0 && dynaTerminalMods.size() == 0) {
            modifiedSequence_massDiffOnly = String.valueOf(sequence);
        }
        else {
            
            String origseq = String.valueOf(sequence);
            modifiedSequence_massDiffOnly = ModifiedSequenceBuilder.buildWithDiffMass(origseq, dynaResidueMods, dynaTerminalMods);
        }
        
        return modifiedSequence_massDiffOnly;
    }
    
    private void sortDynaResidueModifications() {
        Collections.sort(dynaResidueMods, new Comparator<MsResultResidueMod>(){
            public int compare(MsResultResidueMod o1, MsResultResidueMod o2) {
                return Integer.valueOf(o1.getModifiedPosition()).compareTo(Integer.valueOf(o2.getModifiedPosition()));
            }});
    }

    /**
     * Returns the modified peptide along with the pre and post residues. e.g. K.PEP[177]TIDE.L
     * The number in the square brackets is the modification mass plus the mass of the amino acid.
     * @return
     * @throws ModifiedSequenceBuilderException 
     */
    @Override
    public String getFullModifiedPeptide() throws ModifiedSequenceBuilderException {
        String pept = getModifiedPeptide();
        return preResidue+"."+pept+"."+postResidue;
    }

    /**
     * Returns the modified peptide along with the pre and post residues. 
     * If massDiffOnly is true, only the modification mass is displayed within the square brackets:
     *  e.g. K.PEP[+80]TIDE.L
     * Otherwise, the mass of the residue is added to the modification mass
     *  e.g. K.PEP[177]TIDE.L
     * @return
     * @throws ModifiedSequenceBuilderException 
     */
    @Override
    public String getFullModifiedPeptide(boolean massDiffOnly) throws ModifiedSequenceBuilderException {
    	String pept = getModifiedPeptide(massDiffOnly);
        return preResidue+"."+pept+"."+postResidue;
    }
    
    @Override
    public String getFullModifiedPeptidePS() {
        return getFullModifiedPeptidePS(true);
    }
    
    @Override
    public String getFullModifiedPeptidePS(boolean includeTermMods) {
        String pept = getModifiedPeptidePS(includeTermMods);
        return preResidue+"."+pept+"."+postResidue;
    }

	@Override
	public boolean hasDynamicModification() {
		return dynaResidueMods.size() > 0;
	}
}
