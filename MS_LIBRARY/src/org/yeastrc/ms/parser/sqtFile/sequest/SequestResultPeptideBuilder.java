/**
 * MsSearchResultPeptideBuilder.java
 * @author Vagisha Sharma
 * Jul 13, 2008
 * @version 1.0
 */
package org.yeastrc.ms.parser.sqtFile.sequest;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.yeastrc.ms.domain.search.MsResidueModificationIn;
import org.yeastrc.ms.domain.search.MsResultResidueMod;
import org.yeastrc.ms.domain.search.MsResultTerminalMod;
import org.yeastrc.ms.domain.search.MsSearchResultPeptide;
import org.yeastrc.ms.domain.search.MsTerminalModificationIn;
import org.yeastrc.ms.domain.search.MsTerminalModification.Terminal;
import org.yeastrc.ms.domain.search.impl.ResultResidueModBean;
import org.yeastrc.ms.domain.search.impl.ResultTerminalModBean;
import org.yeastrc.ms.domain.search.impl.SearchResultPeptideBean;
import org.yeastrc.ms.parser.sqtFile.PeptideResultBuilder;
import org.yeastrc.ms.parser.sqtFile.SQTParseException;

/**
 * 
 */
public final class SequestResultPeptideBuilder implements PeptideResultBuilder {

    private static final SequestResultPeptideBuilder instance = new SequestResultPeptideBuilder();

    private SequestResultPeptideBuilder() {}

    public static SequestResultPeptideBuilder instance() {
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
        
        if(dynaTerminalMods == null)
        	dynaTerminalMods = new ArrayList<MsTerminalModificationIn>(0);
        
        if (resultSequence.length() < 5)
            throw new SQTParseException("sequence appears to be invalid: "+resultSequence);
//        resultSequence = resultSequence.toUpperCase();
        final char preResidue = getPreResidue(resultSequence);
        final char postResidue = getPostResidue(resultSequence);
        String dotless = removeDots(resultSequence);
        final List<MsResultResidueMod> resultMods = getResultMods(dotless, dynaResidueMods);
        final List<MsResultTerminalMod> terminalMods = getTerminalMods(resultSequence, dynaTerminalMods);
        
        final String justPeptide = getOnlyPeptideSequence(dotless);
        
        SearchResultPeptideBean resultPeptide = new SearchResultPeptideBean();
        resultPeptide.setPeptideSequence(justPeptide);
        resultPeptide.setPreResidue(preResidue);
        resultPeptide.setPostResidue(postResidue);
        resultPeptide.setDynamicResidueModifications(resultMods);
        resultPeptide.setDynamicTerminalModifications(terminalMods);
        
        return resultPeptide;
    }

	char getPreResidue(String sequence) throws SQTParseException {
        if (hasNtermSeparator(sequence))
            return sequence.charAt(0);
        throw new SQTParseException("Invalid peptide sequence; cannot get PRE residue: "+sequence);
    }
    
    private static boolean hasNtermSeparator(String sequence) {
    	return (sequence.charAt(1) == '.' ||
                sequence.charAt(1) == MsTerminalModificationIn.NTERM_MOD_CHAR_SEQUEST);
    }
    
    char getPostResidue(String sequence) throws SQTParseException {
        if (hasCtermSeparater(sequence))
            return sequence.charAt(sequence.length() -1);
        throw new SQTParseException("Invalid peptide sequence; cannot get POST residue: "+sequence);
    }
    
    private static boolean hasCtermSeparater(String sequence) {
    	return (sequence.charAt(sequence.length() - 2) == '.' ||
            	sequence.charAt(sequence.length() - 2) == MsTerminalModificationIn.CTERM_MOD_CHAR_SEQUEST);
    }
    
    List<MsResultTerminalMod> getTerminalMods(String resultSequence,
			List<? extends MsTerminalModificationIn> dynaTerminalMods) throws SQTParseException {
		
    	MsTerminalModificationIn ntermMod = null;
    	MsTerminalModificationIn ctermMod = null;
    	
    	for(MsTerminalModificationIn mod: dynaTerminalMods) {
    		if(mod.getModifiedTerminal() == Terminal.NTERM) {
    			ntermMod = mod;
    		}
    		else if(mod.getModifiedTerminal() == Terminal.CTERM) {
    			ctermMod = mod;
    		}
    	}
    	
    	List<MsResultTerminalMod> resultTermMods = new ArrayList<MsResultTerminalMod>();
    	if(resultSequence.charAt(1) == MsTerminalModificationIn.NTERM_MOD_CHAR_SEQUEST) {
    		
    		// make sure we have a variable N-terminal modification in the search settings
    		if(ntermMod == null) {
    			throw new SQTParseException("No variable N-terminus modification found for peptide "+resultSequence);
    		}
    		ResultTerminalModBean rmod = new ResultTerminalModBean();
    		rmod.setModificationMass(ntermMod.getModificationMass());
            rmod.setModificationSymbol(ntermMod.getModificationSymbol());
            rmod.setModifiedTerminal(ntermMod.getModifiedTerminal());
            resultTermMods.add(rmod);
    	}
    	if(resultSequence.charAt(resultSequence.length() - 2) == MsTerminalModificationIn.CTERM_MOD_CHAR_SEQUEST) {
    		
    		// make sure we have a variable C-terminal modification in the search settings
    		if(ctermMod == null) {
    			throw new SQTParseException("No variable C-terminus modification found for peptide "+resultSequence);
    		}
    		ResultTerminalModBean rmod = new ResultTerminalModBean();
    		rmod.setModificationMass(ctermMod.getModificationMass());
            rmod.setModificationSymbol(ctermMod.getModificationSymbol());
            rmod.setModifiedTerminal(ctermMod.getModifiedTerminal());
            resultTermMods.add(rmod);
    	}
    	
    	return resultTermMods;
	}
    
    
    
    List<MsResultResidueMod> getResultMods(String peptide, List<? extends MsResidueModificationIn> dynaMods) throws SQTParseException {
        
    	// Total rewrite of this method to add support for embedded dynamic mod mass with mass value surrounded 
    	// by "()" or "[]", ie "(123)" or "[123]"
    	
    	
        // create a map of the dynamic modifications keyed on symbol for the search for easy access.
        Map<String, MsResidueModificationIn> modMapKeyedOnSymbol = new HashMap<String, MsResidueModificationIn>(dynaMods.size());
        for (MsResidueModificationIn mod: dynaMods) {
            modMapKeyedOnSymbol.put(mod.getModifiedResidue() + "" + mod.getModificationSymbol(), mod);
        }
        
        // create a map of the dynamic modifications keyed on mass for the search for easy access.
        Map<String, MsResidueModificationIn> modMapKeyedOnMassTrailingZerosRemoved = new HashMap<String, MsResidueModificationIn>(dynaMods.size());
        for (MsResidueModificationIn mod: dynaMods) {
        	
//        	char modResidue = mod.getModifiedResidue();
        	
        	BigDecimal massBD = mod.getModificationMass();
        	
//        	String massStr = massBD.toString();
        	String massPlainStr = massBD.toPlainString();
        	
        	String massForMap = massPlainStr;
        	
        	if ( massPlainStr.contains( "." ) ) {
        		
        		//  remove trailing zeros to right of "." since they are not passed by Prolucid
        	
        		int lastNonZeroCharIndex = massPlainStr.length() - 1;

        		while ( lastNonZeroCharIndex > 0 && massPlainStr.charAt( lastNonZeroCharIndex ) == '0' ) {

        			lastNonZeroCharIndex--;
        		}
        		
        		if ( lastNonZeroCharIndex > 0 && massPlainStr.charAt( lastNonZeroCharIndex ) == '.' ) {

        			lastNonZeroCharIndex--;
        		}

        		massForMap = massPlainStr.substring(0, lastNonZeroCharIndex + 1 );
        	}        	
        	
        	StringBuilder mapKeySB = new StringBuilder();
        	mapKeySB.append( mod.getModifiedResidue() );
        	mapKeySB.append( massForMap );
        	String mapKey = mapKeySB.toString();
        	
        	modMapKeyedOnMassTrailingZerosRemoved.put( mapKey, mod);
        }
        

        
        List<MsResultResidueMod> resultMods = new ArrayList<MsResultResidueMod>();
        char modifiedChar = 0;
        int modCharIndex = -1;
        
        char modificationSymbol = ' ';
        
        for (int peptideIndex = 0; peptideIndex < peptide.length(); peptideIndex++) {
        	
            char peptideCharAtIndex = peptide.charAt(peptideIndex);
            
            // if this is a valid residue skip over it
            if (isResidue(peptideCharAtIndex))   {
                modifiedChar = peptideCharAtIndex;
                modCharIndex++;
                continue;
            }
            
            MsResidueModificationIn matchingMod = null;
            
            //  wanted to support "[]" for dynamic mod mass delimiter but "[]" appears to be already used
            
            if ( peptideCharAtIndex == ')' ) { //  || peptideCharAtIndex == ']'
            	
        		throw new SQTParseException("Found closing dynamic modification character '" 
        				+ peptideCharAtIndex + "' without preceding matching start dynamic modification character.  "
        				+ "Processing modified residue '" + modifiedChar
        				+ "' at position " + modCharIndex
        				+ "; sequence: " + peptide);
            }
            
            if ( peptideCharAtIndex == '(' ) { //  || peptideCharAtIndex == '[' 
            	
            	
            	//  dynamic mod with embedded mass
            	
            	char openingModChar = peptideCharAtIndex;
            	
//            	char closingModChar = ']';
//            	
//                if ( openingModChar == '(' ) {
//                	
//                	closingModChar = ')';
//                }
                
                char closingModChar = ')';
                
                
                
        		StringBuilder massSB = new StringBuilder(20);					// the string we're building that contains a mass (e.g. 24.12)

                peptideIndex++;  // advance to first character of mass
                
                while ( ( peptideCharAtIndex = peptide.charAt(peptideIndex) ) != closingModChar ) {
                	
                	massSB.append( peptideCharAtIndex );
                	
                    peptideIndex++;  // advance to next character
                    
                    if ( peptideIndex >=  peptide.length() ) {
                    	
                		throw new SQTParseException("Failed to find closing dynamic modification character '" 
                				+ closingModChar + "' before end of sequence.  "
                				+ "Processing modified residue '" + modifiedChar
                				+ "' at position " + modCharIndex
                				+ "; sequence: " + peptide);
                    }
                }
                
                String massStr = massSB.toString();
                
                BigDecimal massBD = null;
                
                try {
                	
                    massBD = new BigDecimal( massStr );
                	
                } catch ( Exception e) {
                	
            		throw new SQTParseException("Failed to parse dynamic modification mass '" 
            				+ massStr + "'.  "
            				+ "Processing modified residue '" + modifiedChar
            				+ "' at position " + modCharIndex
            				+ "; sequence: " + peptide);
                }
                
                //  Look up mass
                for (MsResidueModificationIn mod: dynaMods) {
                    
                	if ( mod.getModificationMass().equals( massBD ) && mod.getModifiedResidue() == modifiedChar ) {
                		
                		matchingMod = mod;
                		
                		break;
                	}
                }
                
                if ( matchingMod == null ) {
                	
                	String searchMapKey = modifiedChar + massBD.toPlainString();

                	matchingMod = modMapKeyedOnMassTrailingZerosRemoved.get( searchMapKey );
                }
                
                if ( matchingMod == null ) {
                	
            		throw new SQTParseException("No matching modification found: " 
            				+ modifiedChar + openingModChar + massStr + closingModChar + "; sequence: " + peptide);
                }
                
            	
            	modificationSymbol = matchingMod.getModificationSymbol();

            	
            } else {
            	
            	modificationSymbol = peptideCharAtIndex;
            	matchingMod = modMapKeyedOnSymbol.get(modifiedChar + "" + peptideCharAtIndex);
            	if (matchingMod == null)
            		throw new SQTParseException("No matching modification found: " + modifiedChar + peptideCharAtIndex + "; sequence: " + peptide);
            }
            
            // found a match!!
            ResultResidueModBean resultMod = new ResultResidueModBean();
            resultMod.setModificationMass(matchingMod.getModificationMass());
            resultMod.setModifiedResidue(modifiedChar);
            resultMod.setModificationSymbol(modificationSymbol);
            resultMod.setModifiedPosition(modCharIndex);
            resultMods.add(resultMod);
        }
        
        return resultMods;
    }
    
    
    //   WAS 
//    List<MsResultResidueMod> getResultMods(String peptide, List<? extends MsResidueModificationIn> dynaMods) throws SQTParseException {
//        
//        // create a map of the dynamic modifications for the search for easy access.
//        Map<String, MsResidueModificationIn> modMap = new HashMap<String, MsResidueModificationIn>(dynaMods.size());
//        for (MsResidueModificationIn mod: dynaMods)
//            modMap.put(mod.getModifiedResidue()+""+mod.getModificationSymbol(), mod);
//        
//        List<MsResultResidueMod> resultMods = new ArrayList<MsResultResidueMod>();
//        char modifiedChar = 0;
//        int modCharIndex = -1;
//        for (int i = 0; i < peptide.length(); i++) {
//            char x = peptide.charAt(i);
//            // if this is a valid residue skip over it
//            if (isResidue(x))   {
//                modifiedChar = x;
//                modCharIndex++;
//                continue;
//            }
//            MsResidueModificationIn matchingMod = modMap.get(modifiedChar+""+x);
//            if (matchingMod == null)
//                throw new SQTParseException("No matching modification found: "+modifiedChar+x+"; sequence: "+peptide);
//            
//            // found a match!!
//            ResultResidueModBean resultMod = new ResultResidueModBean();
//            resultMod.setModificationMass(matchingMod.getModificationMass());
//            resultMod.setModifiedResidue(modifiedChar);
//            resultMod.setModificationSymbol(x);
//            resultMod.setModifiedPosition(modCharIndex);
//            resultMods.add(resultMod);
//        }
//        
//        return resultMods;
//    }
    
    
    
    
    
    

    static String removeDots(String sequence) throws SQTParseException {
        if (!hasNtermSeparator(sequence) || !hasCtermSeparater(sequence))
            throw new SQTParseException("Sequence does not have .(dots) or terminam modification characters (']', '[') in the expected position: "+sequence);
        return sequence.substring(2, sequence.length() - 2);
    }

    static String getOnlyPeptideSequence(String sequence) throws SQTParseException {
        char[] residueChars = new char[sequence.length()];
        int j = 0;
        for (int i = 0; i < sequence.length(); i++) {
            char x = sequence.charAt(i);
            if (isResidue(x))
                residueChars[j++] = x;
        }
        sequence = String.valueOf(residueChars).trim();
        if (sequence.length() == 0)
            throw new SQTParseException("No residues found: "+sequence);
        return sequence;
    }
    
    static private boolean isResidue(char residue) {
        return residue >= 'A' && residue <= 'Z';
    }
    
    public static String getOnlyPeptide(String peptideAndExtras) throws SQTParseException {
        String dotless = removeDots(peptideAndExtras);
        return getOnlyPeptideSequence(dotless);
    }
}
