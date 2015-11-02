/**
 * ModifiedSequenceBuilder.java
 * @author Vagisha Sharma
 * Jul 30, 2009
 * @version 1.0
 */
package org.yeastrc.ms.service;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.yeastrc.ms.domain.search.MsResidueModification;
import org.yeastrc.ms.domain.search.MsResultResidueMod;
import org.yeastrc.ms.domain.search.MsResultTerminalMod;
import org.yeastrc.ms.domain.search.MsTerminalModification.Terminal;
import org.yeastrc.ms.util.AminoAcidUtilsFactory;
import org.yeastrc.ms.util.BaseAminoAcidUtils;

/**
 * 
 */
public class ModifiedSequenceBuilder {

    
    private ModifiedSequenceBuilder() {}
    
    public static String build(String sequence, List<MsResultResidueMod> dynamicResidueMods,
            List<MsResultTerminalMod> dynamicTerminalMods) throws ModifiedSequenceBuilderException {
    	
    	// no static mods; diffMassOnly = false
    	return build(sequence, dynamicResidueMods, dynamicTerminalMods, null, false, 0);
    }
    
    public static String buildWithDiffMass(String sequence, List<MsResultResidueMod> dynamicResidueMods,
            List<MsResultTerminalMod> dynamicTerminalMods) throws ModifiedSequenceBuilderException {
    	
    	// no static mods; diffMassOnly = true
    	return build(sequence, dynamicResidueMods, dynamicTerminalMods, null, true, 0);
    }
    
    
    public static String build(String sequence, List<MsResultResidueMod> dynamicResidueMods,
            List<MsResultTerminalMod> dynamicTerminalMods, 
            List<MsResidueModification> staticResidueMods, 
            boolean diffMassOnly,
            int precision) throws ModifiedSequenceBuilderException {
        
        if((dynamicResidueMods == null || dynamicResidueMods.size() == 0) &&
           (dynamicTerminalMods == null || dynamicTerminalMods.size() == 0) &&
           (staticResidueMods == null || staticResidueMods.size() == 0))
            return sequence;
        
        DecimalFormat format = null;
        if(precision > 0)
        	format = makeDecimalFormatter(precision);
        
        
        // map of dynamic residue modifications
        Map<Integer, List<MsResultResidueMod>> dynaResModMap = new HashMap<Integer, List<MsResultResidueMod>>();
        for(MsResultResidueMod mod: dynamicResidueMods) {
            List<MsResultResidueMod> mods = dynaResModMap.get(mod.getModifiedPosition());
            if(mods == null) {
                mods = new ArrayList<MsResultResidueMod>();
                dynaResModMap.put(mod.getModifiedPosition(), mods);
            }
            mods.add(mod);
        }
        
        // map of static residue modifications
        Map<Character, List<MsResidueModification>> staticResModMap = new HashMap<Character, List<MsResidueModification>>();
        if(staticResidueMods != null) {
        	for(MsResidueModification mod: staticResidueMods) {
        		List<MsResidueModification> mods = staticResModMap.get(Character.valueOf(mod.getModificationSymbol()));
        		if(mods == null) {
        			mods = new ArrayList<MsResidueModification>();
        			staticResModMap.put(Character.valueOf(mod.getModifiedResidue()), mods);
        		}
        		mods.add(mod);
        	}
        }
        
        // dynamic terminal modifications
        List<MsResultTerminalMod> ntermMods = new ArrayList<MsResultTerminalMod>();
        List<MsResultTerminalMod> ctermMods = new ArrayList<MsResultTerminalMod>();
        for(MsResultTerminalMod mod: dynamicTerminalMods) {
            if(mod.getModifiedTerminal() == Terminal.NTERM) {
                ntermMods.add(mod);
            }
            else if (mod.getModifiedTerminal() == Terminal.CTERM) {
                ctermMods.add(mod);
            }
        }
        
        // build the modified sequence
        StringBuilder buf = new StringBuilder();
        
        // add any dynamic N-terminal modifications
        if(ntermMods.size() > 0) {
        	double ntermmod = 0;
            for(MsResultTerminalMod mod: ntermMods)
            	ntermmod += mod.getModificationMass().doubleValue();
            if(ntermmod != 0) {
            	if(!diffMassOnly)
            		ntermmod += BaseAminoAcidUtils.NTERM_MASS;
            	buf.append("n[");
            	if(ntermmod > 0 && diffMassOnly) 
            		buf.append("+"); // positive mass diff
            	buf.append(getNumber(format, ntermmod)).append("]");
            }
        }
        
        for(int i = 0; i < sequence.length(); i++) {
           
        	// add the amino acid char
            buf.append(sequence.charAt(i));
            
            double mass = 0;

            // add any dynamic residue modifications for this amino acid in the sequence
            List<MsResultResidueMod> mods = dynaResModMap.get(i);
            if(mods != null) {
                for(MsResultResidueMod mod: mods) {
                    if(mod.getModifiedResidue() != sequence.charAt(i)) {
                        throw new ModifiedSequenceBuilderException("Amino acid at index: "+i+" of sequence: "+sequence+
                                " does not match modified residue: "+mod.getModifiedResidue());
                    }
                    mass += mod.getModificationMass().doubleValue();
                }
            }
            
            // add any static residue modifications for this amino acid
            List<MsResidueModification> staticMods = staticResModMap.get(Character.valueOf(sequence.charAt(i)));
            if(staticMods != null) {
                for(MsResidueModification mod: staticMods) {
                    if(mod.getModifiedResidue() != sequence.charAt(i)) {
                        throw new ModifiedSequenceBuilderException("Amino acid at index: "+i+" of sequence: "+sequence+
                                " does not match static modified residue: "+mod.getModifiedResidue());
                    }
                    mass += mod.getModificationMass().doubleValue();
                }
            }
            
            // If this position is modified add a string representing the mass of the residue at this position.
            if(mass != 0) {
            	if(!diffMassOnly) {
            		double charMass = AminoAcidUtilsFactory.getAminoAcidUtils().monoMass(sequence.charAt(i));
            		mass += charMass;
            	}
                String modStr = ""+getNumber(format, mass); // (AminoAcidUtils.monoMass(sequence.charAt(i)) + mass);
                if(diffMassOnly) {
                	if(mass > 0) 
                		modStr = "+"+modStr; // positive mass diff
                }
                buf.append("["+modStr+"]");
            }
        }
        
        // add any dynamic C-term modification
        if(ctermMods.size() > 0) {
        	double ctermmod = 0;
            for(MsResultTerminalMod mod: ctermMods)
            	ctermmod += mod.getModificationMass().doubleValue();
            if(ctermmod != 0) {
            	if(!diffMassOnly)
            		ctermmod += BaseAminoAcidUtils.CTERM_MASS;
            	buf.append("c[");
            	if(ctermmod > 0 && diffMassOnly) 
            		buf.append("+"); // positive mass diff
            	buf.append(getNumber(format, ctermmod)).append("]");
            }
        }
        
        return buf.toString();
    }
    
    private static String getNumber(DecimalFormat fmt, double number) {
    	if(fmt == null)
    		return String.valueOf(Math.round(number));
    	else 
    		return fmt.format(number);
    }
    
    private static DecimalFormat makeDecimalFormatter(int precision) {
    	String fmtString = "0.";
    	for(int i = 0; i < precision; i++)
    		fmtString += "0";
    	DecimalFormat format = new DecimalFormat(fmtString);
    	return format;
    }
}
