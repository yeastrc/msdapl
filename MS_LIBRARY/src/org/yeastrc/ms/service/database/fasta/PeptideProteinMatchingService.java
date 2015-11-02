/**
 * PeptideProteinMatchingService.java
 * @author Vagisha Sharma
 * Sep 11, 2009
 * @version 1.0
 */
package org.yeastrc.ms.service.database.fasta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.yeastrc.ms.domain.general.EnzymeRule;
import org.yeastrc.ms.domain.general.MsEnzyme;
import org.yeastrc.nrseq.dao.NrSeqLookupUtil;
import org.yeastrc.nrseq.domain.NrDbProtein;

/**
 * 
 */
public class PeptideProteinMatchingService {

    
    private final boolean createSuffixInMemory;
    private final int databaseId;
    private int numEnzymaticTermini = 0;
    private List<EnzymeRule> enzymeRules;
    private boolean doItoLSubstitution = false;
    // If asterisks are not removed from the protein sequences, they are treated as the start or end of a protein.
    private boolean removeAsterisks = false;
    
    // There is a new parameter in Sequest -- clip_nterm_methionine
    // From Jimmy's email (July 14, 2011)
    // In the latest version, there is a new parameter "clip_nterm_methionine" which will analyze a protein 
    // with and w/o the n-term methionine. ÊSo two forms of the protein are analyzed independently presuming 
    // two different n-termini. ÊWas a feature requested by the Villen lab.
    private boolean clipNtermMethionine = false;
    
    private Map<String, List<Integer>> suffixMap;
    
    private Map<String, Integer> suffixIdMap;
    
    private static final Logger log = Logger.getLogger(PeptideProteinMatchingService.class.getName());
    
    public PeptideProteinMatchingService(int databaseId) throws PeptideProteinMatchingServiceException {
        
        this.databaseId = databaseId;
        this.enzymeRules = new ArrayList<EnzymeRule>();
        
        createSuffixInMemory =true;
        
        if(createSuffixInMemory) {
            buildInMemorySuffixes(databaseId);
        }
    }
    
    // Constructor used only for testing
    PeptideProteinMatchingService() {
    	createSuffixInMemory = false;
    	databaseId = 0;
	}
    
    public void clearMaps() {
    	if(suffixMap != null) {
    		suffixMap.clear();
    		suffixMap = null;
    	}
    	if(suffixIdMap != null) {
    		suffixIdMap.clear();
    		suffixIdMap = null;
    	}
    }
    
    public int getFastaDatabaseId() {
    	return this.databaseId;
    }
    
    public String getCriteria() {
    	StringBuilder buf = new StringBuilder();
    	buf.append("Fasta Database ID: "+getFastaDatabaseId());
    	buf.append("; numEnzymaticTermini: "+getNumEnzymaticTermini());
    	buf.append("; removeAsteriks: "+removeAsterisks);
    	buf.append("; I&L Substitution: "+this.doItoLSubstitution);
    	return buf.toString();
    }
    
    // --------------------------------------------------------------------------------
    // SUFFIX MAP IN MEMORY
    // --------------------------------------------------------------------------------
    private void buildInMemorySuffixes(int databaseId) {
        
        FastaInMemorySuffixCreator creator = new FastaInMemorySuffixCreator();
        suffixMap = creator.buildInMemorySuffixes(databaseId);
    }
    
    // --------------------------------------------------------------------------------
    // --------------------------------------------------------------------------------
    public void setNumEnzymaticTermini(int net) {
        this.numEnzymaticTermini = net;
    }
    
    public void setDoItoLSubstitution(boolean doItoLSubstitution) {
		this.doItoLSubstitution = doItoLSubstitution;
	}
    
    public void setRemoveAsterisks(boolean removeAsterisks) {
    	this.removeAsterisks = removeAsterisks;
    }
    
    public void setClipNtermMet(boolean clipNtermMethionine) {
    	this.clipNtermMethionine = clipNtermMethionine;
    }

	public int getNumEnzymaticTermini() {
        return this.numEnzymaticTermini;
    }
   
    public void setEnzymeRules(List<EnzymeRule> enzymeRules) {
        this.enzymeRules = enzymeRules;
    }
    
    public void setEnzymes(List<MsEnzyme> enzymes) {
        enzymeRules = new ArrayList<EnzymeRule>(enzymes.size());
        for(MsEnzyme enzyme: enzymes)
            enzymeRules.add(new EnzymeRule(enzyme));
    }

    public List<PeptideProteinMatch> getMatchingProteins(String peptide) throws PeptideProteinMatchingServiceException {
        
    	// find the matching database protein ids for the given peptide and fasta databases
        List<Integer> dbProtIds = getMatchingDbProteinIds(peptide);
        
        // find the best protein peptide match based on the given enzyme and num enzymatic termini criteria
        List<PeptideProteinMatch> matchingProteins = new ArrayList<PeptideProteinMatch>(dbProtIds.size());
        log.debug("Number of matching proteins for peptide : "+peptide+" before applying enzyme rules: "+dbProtIds.size());
        
        for(int dbProtId: dbProtIds) {
            NrDbProtein dbProt = NrSeqLookupUtil.getDbProtein(dbProtId);
            PeptideProteinMatch match = getPeptideProteinMatch(dbProt, peptide);
            if(match != null) {
                matchingProteins.add(match);
            }
        }
        
        log.debug("Number of matches after applying emzyme rules: "+matchingProteins.size());
        if(matchingProteins.size() == 0) {
        	log.error("No matches found for peptide "+peptide+" after applying enzyme rules");
        	throw new PeptideProteinMatchingServiceException("No matches found for peptide "+peptide+" after applying enzyme rules. "+getCriteria());
        }
        
        return matchingProteins;
    }
    
    private PeptideProteinMatch getPeptideProteinMatch(NrDbProtein dbProt, String peptide) {
        
        String sequence = NrSeqLookupUtil.getProteinSequence(dbProt.getProteinId());
        
        return getPeptideProteinMatch(dbProt, peptide, sequence);
    }
    
    PeptideProteinMatch getPeptideProteinMatch(NrDbProtein dbProt, String peptide, String sequence) {
        
    	if(removeAsterisks) {
        	// Remove any '*' characters from the sequence
        	sequence = FastaInMemorySuffixCreator.removeAsterisks(sequence);
        	peptide = FastaInMemorySuffixCreator.removeAsterisks(peptide);
        }
    	
    	if(doItoLSubstitution) {
    		peptide = FastaInMemorySuffixCreator.doIAndLSubstitution(peptide);
    		sequence = FastaInMemorySuffixCreator.doIAndLSubstitution(sequence);
    	}
    	
        int idx = sequence.indexOf(peptide);
        
        while(idx != -1) {
            
            char nterm = idx == 0 ? '-' : sequence.charAt(idx - 1);
            
            char cterm = idx + peptide.length() == sequence.length() ? '-' : sequence.charAt(idx + peptide.length());
            
            if(enzymeRules.size() == 0) {
                return makeMatch(peptide, nterm, cterm, 0, dbProt);
            }
            
            char ntermForCalcEnzTerm = nterm;
            // Update to Sequest (May, 2011): Sequest no longer ignores '*' characters in the middle of protein sequences
            // '*' characters are now treated as the start  and end of a protein sequence by Sequest.
            // With this change, the protein substring that matches a peptide "PEPTR" can be "*PEPTR*",
            // where the nterm and cterm characters are '*'.
            // We will treat this (*.PEPTR.*) as a fully tryptic 
            // peptide, or num enzymatic termini = 2;
            if(!removeAsterisks) {
            	ntermForCalcEnzTerm = nterm == '*' ? '-' : nterm;
            }
            
            char ctermForCalcEnzTerm = cterm;
            if(!removeAsterisks) {
            	ctermForCalcEnzTerm = cterm == '*' ? '-' : cterm;
            }
            
            
            // look at each enzyme rule and return the first match
            for(EnzymeRule rule: enzymeRules) {
                int net = rule.getNumEnzymaticTermini(peptide, ntermForCalcEnzTerm, ctermForCalcEnzTerm);
                if(net >= numEnzymaticTermini) {
                    return makeMatch(peptide, nterm, cterm, net, dbProt);
                }
            }
            
            // If we are here it means the match starting at the index "idx" did not satisfy the enzyme rules
            // If the match index is 1, try again after clipping the nterm Methionine, if that option is 
            // set to true
            if(this.clipNtermMethionine && sequence.startsWith("M") && idx == 1) {
            	

            	// look at each enzyme rule again and return the first match
            	for(EnzymeRule rule: enzymeRules) {
            		// set nterm (pre-residue) to '-' so that it is treated as the beginning of the 
            		// protein sequence.
            		int net = rule.getNumEnzymaticTermini(peptide, '-', ctermForCalcEnzTerm);
            		if(net >= numEnzymaticTermini) {
            			return makeMatch(peptide, nterm, cterm, net, dbProt);
            		}
            	}
            }
            
            idx = sequence.indexOf(peptide, idx+1);
        }
        
        return null;
    }
    
    private PeptideProteinMatch makeMatch(String peptide, char nterm, char cterm, int net, NrDbProtein dbProtein) {
    	
    	PeptideProteinMatch match = new PeptideProteinMatch();
        match.setPeptide(peptide);
        match.setPreResidue(nterm);
        match.setPostResidue(cterm);
        match.setProtein(dbProtein);
        match.setNumEnzymaticTermini(net);
        return match;
    }
    
    private List<Integer> getMatchingDbProteinIds(String peptide) throws PeptideProteinMatchingServiceException {
        
        if(this.createSuffixInMemory) {
            return getMatchingDbProteinIdsForPeptideFromMemory(peptide);
        }
        else {
            return NrSeqLookupUtil.getDbProteinIdsMatchingPeptide(peptide, databaseId);
        }
    }
    
    
    private List<String> getSuffixList(String peptideSeq) {
        
    	Set<String> suffixList = new HashSet<String>();
    	for(int i = 0; i < peptideSeq.length(); i++) {
    		int end = Math.min(i+FastaInMemorySuffixCreator.SUFFIX_LENGTH, peptideSeq.length());
    		suffixList.add(peptideSeq.substring(i, end));

    		if(i+FastaInMemorySuffixCreator.SUFFIX_LENGTH >= peptideSeq.length())
    			break;
    	}
    	return new ArrayList<String>(suffixList);
    }
    
    private List<Integer> getMatchingDbProteinIdsForPeptideFromMemory(String peptide) throws PeptideProteinMatchingServiceException {
        
    	
    	peptide = FastaInMemorySuffixCreator.format(peptide); // removes asterisks and replaces 'L' and 'I' with '1'
    	
        int SUFFIX_LENGTH = FastaInMemorySuffixCreator.SUFFIX_LENGTH;
        
        if(peptide.length() < SUFFIX_LENGTH) {
            log.info("LOOKING FOR MATCH FOR SMALL PEPTIDE: "+peptide);
            Set<Integer> allMatches = new HashSet<Integer>();
            for(String suffix: suffixMap.keySet()) {
                if(suffix.startsWith(peptide)) {
                    allMatches.addAll(suffixMap.get(suffix));
                }
            }
            
            if(allMatches.size() > 10)
                log.debug("!!!# matches found: "+allMatches.size());
            List<Integer> matchList = new ArrayList<Integer>(allMatches.size());
            matchList.addAll(allMatches);
            return matchList;
        }
        
        Set<Integer> allMatches = new HashSet<Integer>();
        
        List<String> suffixList = getSuffixList(peptide);
        //log.info("SUFFIX LIST: "+suffixList);

        int numSuffixesInSeq = suffixList.size();

        Map<Integer, Integer> matches = new HashMap<Integer, Integer>();

        int idx = 0;
        for(String suffix: suffixList) {
        	List<Integer> matchingProteins = suffixMap.get(suffix);
        	if(matchingProteins == null || matchingProteins.isEmpty()) {
        		log.error("No protein matches found for suffix: "+suffix+" for peptide: "+peptide);
        		throw new PeptideProteinMatchingServiceException("No protein matches found for suffix: "+suffix+" for peptide: "+peptide);
        	}
        	if(idx == 0) {
        		for(Integer proteinId: matchingProteins)
        			matches.put(proteinId, 1);
        	}
        	else {
        		for(Integer proteinId: matchingProteins) {
        			Integer num = matches.get(proteinId);
        			if(num != null) {
        				matches.put(proteinId, ++num);
        			}
        		}
        	}
        	idx++;
        }
        
        // keep only those matches that had all the suffixes in our peptide.
        for(int proteinId: matches.keySet()) {
        	int cnt = matches.get(proteinId);
        	if(cnt >= numSuffixesInSeq)
        		allMatches.add(proteinId);
        }
        
        //log.info(allMatches);
        
        if(allMatches.size() > 10)
            log.debug("!!!# matches found: "+allMatches.size()+" for peptide: "+peptide);
        
        if(allMatches.size() == 0) {
        	log.error("No protein matches found for suffix set for peptide: "+peptide);
        	throw new PeptideProteinMatchingServiceException("No protein matches found for suffix set for peptide: "+peptide+". "+getCriteria());
        }
        
        return new ArrayList<Integer>(allMatches);
    }
    
    private static List<String> getAlternateSequences(String peptide) {
    	
    	List<String> list = new ArrayList<String>();
    	if(peptide.contains("I") || peptide.contains("L")) {
    		
    		for(int i = 0; i < peptide.length(); i++) {
    			char c = peptide.charAt(i);

    			if(list.size() == 0) {
					list.add(new String());
				}
    			
    			List<String> altList = new ArrayList<String>();
    			if(c == 'I') {
    				for(String seq: list)
    					altList.add(seq+"L");
    			}
    			else if (c == 'L') {
    				for(String seq: list)
    					altList.add(seq+"I");
    			}
    			
    			for(int j = 0; j < list.size(); j++) {
					String seq = list.get(j);
					seq += c;
					list.set(j, seq);
				}
    			
    			list.addAll(altList);
    			
    		}
    	}
    	else {
    		list.add(peptide);
    	}
    	return list;
    }
    
    public static void main(String[] args) {
    	
    	// 8192 for DLLLIRGGDLRHIGGGGNILLGGLNLLGGGLGQG
    	// SUFFIX SET SIZE: 247808

    	// 32768 for VILFMIILSGNLSIIILIRISSQLHHPMYFFLSHLAFAD
    	 // INFO [Thread-1] [16 Apr 2010 22:47:24] - SUFFIX SET SIZE: 1177600

    	//  Number of alternate peptides: 1048576 for MPLIYINIILEFTISLLGILVYRSHLISSLLCLEGIILSL

    	// 512 for LLGMGDIEGLIDKVNELKLDDNEALIE
    	
    	String sequence = "ABCDIFG";
    	List<String> alternames = getAlternateSequences(sequence);
    	System.out.println(alternames);
    	
    	sequence = "ABCDLFG";
    	alternames = getAlternateSequences(sequence);
    	System.out.println(alternames);
    	
    	sequence = "ABCD";
    	alternames = getAlternateSequences(sequence);
    	System.out.println(alternames);
    	
    	sequence = "IABCL";
    	alternames = getAlternateSequences(sequence);
    	System.out.println(alternames);
    	
    	sequence = "ABCIFGHLPQRLS";
    	alternames = getAlternateSequences(sequence);
    	System.out.println(alternames);
    	
    	sequence = "ILL";
    	alternames = getAlternateSequences(sequence);
    	System.out.println(alternames);
    }
}
