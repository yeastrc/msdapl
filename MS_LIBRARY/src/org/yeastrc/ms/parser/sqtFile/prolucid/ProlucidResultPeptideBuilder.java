/**
 * ProlucidResultPeptideBuilder.java
 * @author Vagisha Sharma
 * Aug 30, 2008
 * @version 1.0
 */
package org.yeastrc.ms.parser.sqtFile.prolucid;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.yeastrc.ms.domain.search.MsResidueModificationIn;
import org.yeastrc.ms.domain.search.MsResultResidueMod;
import org.yeastrc.ms.domain.search.MsResultTerminalMod;
import org.yeastrc.ms.domain.search.MsSearchResultPeptide;
import org.yeastrc.ms.domain.search.MsTerminalModificationIn;
import org.yeastrc.ms.domain.search.MsTerminalModification.Terminal;
import org.yeastrc.ms.domain.search.impl.SearchResultPeptideBean;
import org.yeastrc.ms.domain.search.impl.ResultResidueModBean;
import org.yeastrc.ms.domain.search.impl.ResultTerminalModBean;
import org.yeastrc.ms.parser.sqtFile.PeptideResultBuilder;
import org.yeastrc.ms.parser.sqtFile.SQTParseException;

/**
 * 
 */
public final class ProlucidResultPeptideBuilder implements PeptideResultBuilder {

    private static final ProlucidResultPeptideBuilder instance = new ProlucidResultPeptideBuilder();
    
    static final Pattern singleMod = Pattern.compile("\\(([+|-]?\\d+\\.?\\d*)\\)");
    static final Pattern multipleMods = Pattern.compile("("+singleMod.toString()+")+");
    static final Pattern nTermModPattern = Pattern.compile("^"+multipleMods);
    static final Pattern cTermModPattern = Pattern.compile(multipleMods+"$");
    static final Pattern nonResiduePattern = Pattern.compile("[^A-Z]");
    

    private ProlucidResultPeptideBuilder() {}

    public static ProlucidResultPeptideBuilder instance() {
        return instance;
    }

    public MsSearchResultPeptide build(String resultSequence, 
            List<? extends MsResidueModificationIn> dynaResidueMods,
            List<? extends MsTerminalModificationIn> dynaTerminalMods) 
    throws SQTParseException {
        if (resultSequence == null || resultSequence.length() == 0)
            throw new SQTParseException("sequence cannot be null or empty");

        if (dynaResidueMods == null)
            dynaResidueMods = new ArrayList<MsResidueModificationIn>(0);
        if (dynaTerminalMods == null)
            dynaTerminalMods = new ArrayList<MsTerminalModificationIn>(0);

        if (resultSequence.length() < 5)
            throw new SQTParseException("sequence appears to be invalid: "+resultSequence);
//        resultSequence = resultSequence.toUpperCase();
        final char preResidue = getPreResidue(resultSequence);
        final char postResidue = getPostResidue(resultSequence);
        String dotless = removeDots(resultSequence);
        // get the terminal mods first
        final List<MsResultTerminalMod> termMods = getResultTerminalMods(dotless, dynaTerminalMods, dynaResidueMods);
        // now the residue mods
        final List<MsResultResidueMod> residueMods = getResultResidueMods(dotless, dynaResidueMods, dynaTerminalMods);
        
        final String justPeptide = getOnlyPeptideSequence(dotless);
        SearchResultPeptideBean resultPeptide = new SearchResultPeptideBean();
        resultPeptide.setPeptideSequence(justPeptide);
        resultPeptide.setPreResidue(preResidue);
        resultPeptide.setPostResidue(postResidue);
        resultPeptide.setDynamicResidueModifications(residueMods);
        resultPeptide.setDynamicTerminalModifications(termMods);
        return resultPeptide;
    }

    char getPreResidue(String sequence) throws SQTParseException {
        if (sequence.charAt(1) == '.')
            return sequence.charAt(0);
        throw new SQTParseException("Invalid peptide sequence; cannot get PRE residue: "+sequence);
    }

    char getPostResidue(String sequence) throws SQTParseException {
        if (sequence.charAt(sequence.length() - 2) == '.')
            return sequence.charAt(sequence.length() -1);
        throw new SQTParseException("Invalid peptide sequence; cannot get POST residue: "+sequence);
    }

    /**
     * NOTE: peptide should not contain any pre and post-residues or dots
     * @param peptide
     * @param dynaTermMods
     * @param dynaResMods
     * @return
     * @throws SQTParseException
     */
    List<MsResultTerminalMod> getResultTerminalMods(String peptide, 
            List<? extends MsTerminalModificationIn> dynaTermMods,
            List<? extends MsResidueModificationIn> dynaResMods) throws SQTParseException {

        if (dynaResMods == null)
            dynaResMods = new ArrayList<MsResidueModificationIn>(0);
        if (dynaTermMods == null)
            dynaTermMods = new ArrayList<MsTerminalModificationIn>(0);
        
        
        // create a map of the dynamic residue modifications for the search for easy access.
        Map<String, MsResidueModificationIn> resModMap = new HashMap<String, MsResidueModificationIn>(dynaResMods.size());
        for (MsResidueModificationIn mod: dynaResMods)
            resModMap.put(mod.getModifiedResidue()+""+mod.getModificationMass(), mod);
        
        // create a map of the dynamic terminal modifications for the search
        Map<String, MsTerminalModificationIn> termModMap = new HashMap<String, MsTerminalModificationIn>(dynaTermMods.size());
        for (MsTerminalModificationIn mod: dynaTermMods) 
            termModMap.put(mod.getModifiedTerminal().toChar()+""+mod.getModificationMass(), mod);
        
        
        List<MsResultTerminalMod> resultMods = new ArrayList<MsResultTerminalMod>();
        
        // get any n-term mods
        Matcher m = nTermModPattern.matcher(peptide);
        if (m.find()) {
            String match = m.group();
            if (m.end() >= peptide.length())
                throw new SQTParseException("Invalid n-term modification in peptide: "+peptide);
            char firstNonModChar = peptide.charAt(m.end());
            
            Matcher nm = singleMod.matcher(match);
            while (nm.find()) {
                String modMass = nm.group(1);
                // is this a valid N-terminal modification?
                MsTerminalModificationIn mod = termModMap.get(Terminal.NTERM.toChar()+modMass);
                if (mod != null) {
                    
                    // if this is also a valid residue modification throw an exception
                    if (resModMap.get(firstNonModChar+""+modMass) != null)
                        throw new SQTParseException("Conflicting modification at n-terminus: "+peptide
                                +"\n\tFound n-term modification with mass: "+modMass
                                +" and modification for residue: "+firstNonModChar+" and mass: "+modMass);
                    
                    // add this to result mods
                    ResultTerminalModBean rmod = new ResultTerminalModBean();
                    rmod.setModificationMass(mod.getModificationMass());
                    rmod.setModificationSymbol(mod.getModificationSymbol());
                    rmod.setModifiedTerminal(mod.getModifiedTerminal());
                    resultMods.add(rmod);
                }
            }
        }
        
        // get any c-term mods
        m = cTermModPattern.matcher(peptide);
        if (m.find()) {
            String match = m.group();
            if (m.start() <= 0)
                throw new SQTParseException("Invalid c-term modification in peptide: "+peptide);
            char modChar = peptide.charAt(m.start() - 1);
            
            Matcher cm = singleMod.matcher(match);
            while (cm.find()) {
                String modMass = cm.group(1);
                // is this a valid C-terminal modification?
                MsTerminalModificationIn mod = termModMap.get(Terminal.CTERM.toChar()+modMass);
                if (mod != null) {
                    
                    // if this is also a valid residue modification throw an exception
                    if (resModMap.get(modChar+""+modMass) != null)
                        throw new SQTParseException("Conflicting modification at c-terminus: "+peptide
                                +"\n\tFound c-term modification with mass: "+modMass
                                +" and modification for residue: "+modChar+" and mass: "+modMass);
                    
                    // add this to result mods
                    ResultTerminalModBean rmod = new ResultTerminalModBean();
                    rmod.setModificationMass(mod.getModificationMass());
                    rmod.setModificationSymbol(mod.getModificationSymbol());
                    rmod.setModifiedTerminal(mod.getModifiedTerminal());
                    resultMods.add(rmod);
                }
            }
        }
        
        return resultMods;
    }
    
    /**
     * NOTE: peptide should not contain any pre and post-residues or dots 
     * @param peptide
     * @param dynaResMods
     * @param dynaTermMods
     * @return
     * @throws SQTParseException
     */
    List<MsResultResidueMod> getResultResidueMods(String peptide, 
            List<? extends MsResidueModificationIn> dynaResMods,
            List<? extends MsTerminalModificationIn> dynaTermMods) throws SQTParseException {

        if (dynaResMods == null)
            dynaResMods = new ArrayList<MsResidueModificationIn>(0);
        if (dynaTermMods == null)
            dynaTermMods = new ArrayList<MsTerminalModificationIn>(0);
        
        // create a map of the dynamic residue modifications for the search for easy access.
        Map<String, MsResidueModificationIn> modMap = new HashMap<String, MsResidueModificationIn>(dynaResMods.size());
        for (MsResidueModificationIn mod: dynaResMods)
            modMap.put(mod.getModifiedResidue()+""+mod.getModificationMass(), mod);

        // create a map of the dynamic terminal modifications for the search
        Map<String, MsTerminalModificationIn> termModMap = new HashMap<String, MsTerminalModificationIn>(dynaTermMods.size());
        for (MsTerminalModificationIn mod: dynaTermMods) 
            termModMap.put(mod.getModifiedTerminal().toChar()+""+mod.getModificationMass(), mod);
        
        
        List<MsResultResidueMod> resultMods = new ArrayList<MsResultResidueMod>();
        int modCharIndex = -1;
        int matchedPatternsLength = 0;
        Matcher m = multipleMods.matcher(peptide);
        char modChar = 0;
        
        while(m.find()) {
            
            matchedPatternsLength += m.group().length();
            modCharIndex = m.end() - matchedPatternsLength - 1;
            
            // is this match right at the beginning? It could be a N terminal modification
            if (m.start() == 0) {
                
                modChar = peptide.charAt(m.end());
                modCharIndex = 0;
                
                String match = m.group();
                Matcher sm = singleMod.matcher(match);
                while (sm.find()) {
                    String modMass = sm.group(1);
                    // if this a valid C-terminal modification ignore it
                    if (termModMap.get(Terminal.NTERM.toChar()+modMass) != null)
                        continue;
                    
                    // this is a dynamic residue modification for sure. Make sure it is a valid one
                    MsResidueModificationIn mod = modMap.get(modChar+""+modMass);
                    if (mod != null) {
                        ResultResidueModBean resultMod = new ResultResidueModBean();
                        resultMod.setModificationMass(mod.getModificationMass());
                        resultMod.setModifiedResidue(mod.getModifiedResidue());
                        resultMod.setModifiedPosition(modCharIndex);
                        resultMods.add(resultMod);
                    }
                    else {
                        throw new SQTParseException("No matching modification found for modified char: "+modChar+"; mass: "+modMass+" in sequence: "+peptide);
                    }   
                }
            }
            
            else {
                modChar = peptide.charAt(m.start() -1); // the character just before the modification
                String match = m.group();
                Matcher sm = singleMod.matcher(match);
                while (sm.find()) {
                    String modMass = sm.group(1);
                    
                    // is this match at the end of the sequence? 
                    if (m.end() == peptide.length()) {
                        // if this a valid N-terminal modification ignore it
                        if (termModMap.get(Terminal.CTERM.toChar()+modMass) != null)
                            continue;
                    }
                    
                    // this is a dynamic residue modification for sure. Make sure it is a valid one
                    MsResidueModificationIn mod = modMap.get(modChar+""+modMass);
                    if (mod != null) {
                        ResultResidueModBean resultMod = new ResultResidueModBean();
                        resultMod.setModificationMass(mod.getModificationMass());
                        resultMod.setModifiedResidue(mod.getModifiedResidue());
                        resultMod.setModifiedPosition(modCharIndex);
                        resultMods.add(resultMod);
                    }
                    else {
                        throw new SQTParseException("No matching modification found for modified char: "+modChar+"; mass: "+modMass+" in sequence: "+peptide);
                    }   
                }
                
            }
        }
        
        return resultMods;
    }

    static String removeDots(String sequence) throws SQTParseException {
        if (sequence.charAt(1) != '.' || sequence.charAt(sequence.length() - 2) != '.')
            throw new SQTParseException("Sequence does not have .(dots) in the expected position: "+sequence);
        return sequence.substring(2, sequence.length() - 2);
    }

    static String getOnlyPeptideSequence(String sequence) throws SQTParseException {
        String origSequence = sequence;
        // remove any modifiation strings
        Matcher m = singleMod.matcher(sequence);
        sequence = m.replaceAll("");
        
        // make sure no other non-residue characters are in the sequence
        m = nonResiduePattern.matcher(sequence);
        if (m.find())
            throw new SQTParseException("Peptide sequence has invalid character(s): "+origSequence);
        
        return sequence;
    }

    public static String getOnlyPeptide(String peptideAndExtras) throws SQTParseException {
        String dotless = removeDots(peptideAndExtras);
        return getOnlyPeptideSequence(dotless);
    }
}
