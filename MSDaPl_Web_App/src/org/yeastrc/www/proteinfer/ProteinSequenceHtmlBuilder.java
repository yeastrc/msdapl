/**
 * ProteinSequenceHtmlBuilder.java
 * @author Vagisha Sharma
 * Feb 3, 2010
 * @version 1.0
 */
package org.yeastrc.www.proteinfer;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.yeastrc.ms.domain.search.MsResultResidueMod;
import org.yeastrc.ms.domain.search.MsSearchResultPeptide;

/**
 * 
 */
public class ProteinSequenceHtmlBuilder {

    private static ProteinSequenceHtmlBuilder instance = null;
    
    private ProteinSequenceHtmlBuilder() {}
    
    public static ProteinSequenceHtmlBuilder getInstance() {
        if(instance == null) {
            instance = new ProteinSequenceHtmlBuilder();
        }
        return instance;
    }
    
    public String build(String proteinSequence, List<MsSearchResultPeptide> peptides) throws ProteinSequenceHtmlBuilderException {
        
        char[] reschars = proteinSequence.toCharArray();
        int[] covered = new int[reschars.length];
        int[] modificationMass = new int[reschars.length];
        
        String altProteinSequence = proteinSequence;
        // remove '*' characters from the protein sequence
        altProteinSequence = altProteinSequence.replaceAll("\\*", "");
        Map<Integer, Integer> indexMap = null;
        
        indexMap = makeIndexMap(proteinSequence, altProteinSequence);
        
        // Replace all 'L' with '1'
        altProteinSequence = altProteinSequence.replaceAll("L", "1");
        // Replace all "I" with "1"
        altProteinSequence = altProteinSequence.replaceAll("I", "1");
        
        for(MsSearchResultPeptide peptide: peptides) {
        	
        	// find matching indices for this peptide
        	markCoveredResidues(altProteinSequence, peptide, 
        			covered, modificationMass,
					indexMap);
        	
        }

        // Build the HTML sequence
        return buildHtmlSequence(reschars, covered, modificationMass);
    }

	private String buildHtmlSequence(char[] reschars, int[] covered,
			int[] modificationMass) {
		
		// String array should be set up appropriately with red font tags for sub peptide overlaps, format it into a displayable peptide sequence
        String retStr = "      1          11         21         31         41         51         \n";
        retStr +=       "      |          |          |          |          |          |          \n";
        retStr +=       "    1 ";

        
        //  Add in font tags for labelling covered sequences in the parent sequence
        int counter = 0;
        for(int i = 0; i < reschars.length; i++) {

        	// this residue is covered AND 
        	// the previous residue is not covered OR this is the first residue in the protein 
        	if(covered[i] > 0) {
        		if(i == 0 || covered[i-1] == 0) {
        			retStr += "<span class=\"covered_peptide\">";
        		}
        	}
        	
        	// Add modifications if any at this residue
        	if(modificationMass[i] != 0) {
        		retStr += "<span class=\"peptide\">";
        	}
        	
        	// add the residue
        	retStr += reschars[i];
        	
        	if(modificationMass[i] != 0) {
        		if(modificationMass[i] > 0)
        			retStr+= "[+"+modificationMass[i]+"]";
        		else
        			retStr+= "["+modificationMass[i]+"]";
        		retStr += "</span>";
        	}
        	
        	// this residue is covered AND 
        	// the next residue is not covered OR this is the last residue in the protein 
        	if(covered[i] > 0) {
        		if(i == reschars.length - 1 || covered[i+1] == 0) {
        			retStr += "</span>";
        		}
        	}
        	
        	counter++;
        	if (counter % 60 == 0) {
                if (counter < 1000) retStr += " ";
                if (counter< 100) retStr += " ";

                retStr += "<font style=\"color:black;\">" + String.valueOf(counter) + "</font>";
                retStr += "\n ";

                if (counter < 100) retStr += " ";
                if (counter < 1000) retStr += " ";
                retStr += "<font style=\"color:black;\">" + String.valueOf(counter + 1) + "</font> ";

            } else if (counter % 10 == 0) {
                retStr += " ";
            }
        }

        return retStr;
	}

	private void markCoveredResidues(String proteinSequence, MsSearchResultPeptide peptide, 
			int[] covered, int[] modificationMass, 
			Map<Integer, Integer> indexMap) throws ProteinSequenceHtmlBuilderException {
		
		String altPeptide = peptide.getPeptideSequence();
		altPeptide = altPeptide.replaceAll("L", "1");
		altPeptide = altPeptide.replaceAll("I", "1");
		
		int lastMatchIndex = 0;
		
		while(lastMatchIndex <= proteinSequence.length() - altPeptide.length()) {
			
			int pepIndex = proteinSequence.indexOf(altPeptide, lastMatchIndex);
			
			if(lastMatchIndex == 0 && pepIndex == -1) 
				throw new ProteinSequenceHtmlBuilderException("Peptide "+peptide+" not found in protein!");
			
			if(pepIndex == -1)
				break;
			
			lastMatchIndex = pepIndex + altPeptide.length();
			
			// If there were '*' characters in the original sequence, get the actual index 
			// in the original sequence (with all the '*' characters).
			Integer origSIndex = null;
			Integer origEIndex = null;
			origSIndex = indexMap.get(pepIndex);
			if(origSIndex == null)
				throw new ProteinSequenceHtmlBuilderException("Error mapping indices in protein sequence for peptide: "+peptide);
			origEIndex = indexMap.get(pepIndex + altPeptide.length() - 1);
			if(origEIndex == null)
				throw new ProteinSequenceHtmlBuilderException("Error mapping indices in protein sequence for peptide: "+peptide);
			
			

			if (origSIndex > covered.length - 1)  { //shouldn't happen
				throw new ProteinSequenceHtmlBuilderException("Matching index out of bounds for peptide: "+peptide);
			}

			for(int c = origSIndex; c <= origEIndex; c++) {
				covered[c] = covered[c]+1;
			}
			
			List<MsResultResidueMod> dynaMods = peptide.getResultDynamicResidueModifications();
			for(MsResultResidueMod mod: dynaMods) {
				BigDecimal modMass = mod.getModificationMass();
				int modIdx = mod.getModifiedPosition();
				
				int modIdxInProtein = modIdx+origSIndex;
				if(proteinSequence.charAt(modIdxInProtein) != mod.getModifiedResidue()) {
					throw new ProteinSequenceHtmlBuilderException("Residue in protein ("+
							mod.getModifiedResidue()+") at index "+modIdxInProtein+
							" not the same as modified residue.");
				}
				
				if(modificationMass[modIdxInProtein] == 0) {
					modificationMass[modIdxInProtein] = (int)Math.round(modMass.doubleValue());
				}
				
			}
		}
	}

    
    public String build(String sequence, Set<String> peptideSequences) throws ProteinSequenceHtmlBuilderException {
        
        char[] reschars = sequence.toCharArray();
        String[] residues = new String[reschars.length];        // the array of strings, which are the residues of the matched protein
        for (int i = 0; i < reschars.length; i++) { residues[i] = String.valueOf(reschars[i]); }
        reschars = null;

        // structure of these maps is: Integer=>Integer (Residue index (0..residues.length))=>(number of peptides marking that residue thusly)
        Map<Integer, Integer> starResidues = new HashMap<Integer, Integer>();       // residues marked with a *
        Map<Integer, Integer> atResidues = new HashMap<Integer, Integer>();         // residues marked with a @
        Map<Integer, Integer> hashResidues = new HashMap<Integer, Integer>();       // residues marked with a #
        // TODO add highlighting of modified residues in protein sequences.
        
        
        String altSequence = sequence;
        // remove '*' characters from the protein sequence
        altSequence = altSequence.replaceAll("\\*", "");
        Map<Integer, Integer> indexMap = null;
        boolean hasAsterisk = false;
        if(sequence.length() > altSequence.length()) {
        	indexMap = makeIndexMap(sequence, altSequence);
        	hasAsterisk = true;
        }
        
        
        // Replace all 'L' with '1'
        altSequence = altSequence.replaceAll("L", "1");
        // Replace all "I" with "1"
        altSequence = altSequence.replaceAll("I", "1");
        
        
        //  Add in font tags for labelling covered sequences in the parent sequence
        for( String peptideSequence : peptideSequences ) {

            if (peptideSequence == null || peptideSequence.length() == 0) continue;   
            
            String altPeptide = peptideSequence;
        	altPeptide = altPeptide.replaceAll("L", "1");
        	altPeptide = altPeptide.replaceAll("I", "1");
        	
        	int lastMatchIndex = 0;
        	
        	while(lastMatchIndex <= sequence.length() - peptideSequence.length()) {
        		
        		int pepIndex = altSequence.indexOf(altPeptide, lastMatchIndex);
        		
        		if(lastMatchIndex == 0 && pepIndex == -1) 
        			throw new ProteinSequenceHtmlBuilderException("Peptide "+peptideSequence+" not found in protein!");
        		
        		if(pepIndex == -1)
        			break;
        		
        		lastMatchIndex = pepIndex + altPeptide.length();
        		
        		// If there were '*' characters in the original sequence, get the actual index 
        		// in the original sequence (with all the '*' characters).
        		Integer origSIndex = null;
        		Integer origEIndex = null;
        		if(hasAsterisk) {
        			origSIndex = indexMap.get(pepIndex);
        			if(origSIndex == null)
        				throw new ProteinSequenceHtmlBuilderException("Error mapping indices in protein sequence for peptide: "+peptideSequence);
        			origEIndex = indexMap.get(pepIndex + peptideSequence.length() - 1);
        			if(origEIndex == null)
        				throw new ProteinSequenceHtmlBuilderException("Error mapping indices in protein sequence for peptide: "+peptideSequence);
        		}
        		else {
        			origSIndex = pepIndex;
        			origEIndex = origSIndex + peptideSequence.length() - 1;
        		}
        		

        		if (origSIndex > residues.length - 1)  { //shouldn't happen
        			throw new ProteinSequenceHtmlBuilderException("Matching index out of bounds for peptide: "+peptideSequence);
        		}

        		// Place a red font start at beginning of this sub sequence in main sequence
        		residues[origSIndex] = "<span class=\"covered_peptide\">" + residues[origSIndex];

        		// this means that the sub-peptide extends beyond the main peptide's sequence... shouldn't happen but check for it
        		if (origEIndex > residues.length) {
        			throw new ProteinSequenceHtmlBuilderException("Peptide ("+peptideSequence+") match extends beyond length of protein");
        			// just stop the red font at the end of the main sequence string
        			//residues[residues.length - 1] = residues[residues.length - 1] + "</span>";
        		} else {

        			// add the font end tag after the last residue in the sub sequence
        			residues[origEIndex] = residues[origEIndex] + "</span>";
        		}
        	}
        }
        

        // String array should be set up appropriately with red font tags for sub peptide overlaps, format it into a displayable peptide sequence
        String retStr = "      1          11         21         31         41         51         \n";
        retStr +=       "      |          |          |          |          |          |          \n";
        retStr +=       "    1 ";

        int counter = 0;

        // retStr += "RESIDUE 0: [" + residues[0] + "]";

        for (int i = 0; i < residues.length; i++ ) {
            retStr += residues[i];

            counter++;
            if (counter % 60 == 0) {
                if (counter < 1000) retStr += " ";
                if (counter< 100) retStr += " ";

                retStr += "<font style=\"color:black;\">" + String.valueOf(counter) + "</font>";
                retStr += "\n ";

                if (counter < 100) retStr += " ";
                if (counter < 1000) retStr += " ";
                retStr += "<font style=\"color:black;\">" + String.valueOf(counter + 1) + "</font> ";

            } else if (counter % 10 == 0) {
                retStr += " ";
            }

        }

        return retStr;
    }
    
    private Map<Integer, Integer> makeIndexMap(String origSeq,  String trimSeq) throws ProteinSequenceHtmlBuilderException {
    	
    	Map<Integer, Integer> map = new HashMap<Integer, Integer>();
    	
    	int j = 0;
    	for(int i = 0; i < trimSeq.length(); i++,j++) {
    		
    		while(origSeq.charAt(j) == '*') {
    			j++;
    			if(j >= origSeq.length()) {
    				throw new ProteinSequenceHtmlBuilderException("Error building index map for protein sequence");
    			}
    		}
    		map.put(i, j);
    	}

    	return map;
	}
    
	public static void main(String[] args) throws ProteinSequenceHtmlBuilderException {
    	String protein = "WNLASSRVPFLFRVL*SA*R*SRHQKSSAVPMGRLRAVR*IVSVLCSSAKPSSPAACRRGSCRSLTATTPCPLFQPTS*KILLPGCLMENRVHTTPNCVLSPRKRKGLGHHLTLPLSKMALQKLS*RSPKRT*TGSLSLNLPFVVTVLLMCLSQKKLDKNQNTKQEGTGRGA*NMTPCWSRALSKQLLCLRSRVLQIKRFQLRKSLVQTLEETKTTC*NTMSVIWCGPKCRVTLGGLAWFLQIHSFTAIPNLKVRKRVHASITYSSLVTPQKELGYLRRAS*LLKEKDSLKNYARKVPSRHPRKLRKLSY*NLFQGN*GPSGKWALFKQKKLQACQWRSGKPSSPFYTWGTSFISTLK*PRRLALLQSLSEKWQSPQESVKKLLKTPSLREKRAFP*REGGGPNCVALQRPWRVTPM**RVLLKRRQRLTPEEE*RLLLGGRRPQPLRHEAGREMQHPSFWSSVKNTGMRWWLSTQMLQVRRLKSCSGHSGVC*VRSRERATTPSLPWWPLSRLKKTLVT*MGKKETTQRGHRTLQKMLRLRTHPGKDSGQTSTVFGXXXXXXXXXXEQALTRPWRQPPRSRARQQRKICLMHANH*RSEIGLPRQHLQLLGLAKVHLLLHP*LRMRSRTARETSPRSPHMKVQMKHKLKCLSHPKSLSEELLPKRSTCASCVRSQAASCSVKDPAAELSTSPALGFPGGQKGGSPAASVPQGFTHVSCVKRARQMLSAVW*LSVENFTMRLV*KNTL*LYLRAEVSAAPFTAV*AATLPTLQTQGHQKVK*CGVSAAPLPITAGMLVWQQDVQ*SPPTASSALPTSLLGRGSDTTPTST*AGALCAPKGGAFCAASPAQRPSTLTA*TSRCLMAAGSATTAGLGRSCTSRISFG*NLGTTDGGRQKFAIPKMFPQIFRK*STRLENSLCFSLGLKIITGRIRRECSRTWRGTGAAATRGSEGSEESSKTHCKKLKLVFVKLSFRGKPEKHRRASASPHHTSTSR*ISLTGKSRSTQRIFQKSLSATASPQMRILVALIRSV*TGC*CLSATRRCVPQASSARTSASPSASTQRPRSSRQMAKGGAWSPRGISERENLLTSTSGS*SMRRSAWRESSTHMRTTSPTSTCSL*TXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXPQRPFLQRKRAXXXXXXXXXXXXXXXXXXSQRTSASAAAMAGSWCCVTASSAPRPTTCPAWALASGPSGSGNVLGIIVTCVANLQLHFATSAPIHSVRSTRTGQPSAAPRTGRSYCCEHDLGAASVRSAKTEKPPPEPGKPKGKRRRRRGWRRVTEGK";
    	//protein = protein.replaceAll("\\*", "");
    	Set<String> peptides = new HashSet<String>();
    	peptides.add("VKCGVSAAPLPITAGMLVWQQ");
    	
    	ProteinSequenceHtmlBuilder builder = ProteinSequenceHtmlBuilder.getInstance();
    	String formatted = builder.build(protein, peptides);
    	System.out.println(formatted);
    }
}
